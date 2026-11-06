package com.doinner.csys.service;

import com.doinner.common.core.domain.DataSet;
import com.doinner.common.security.utils.SecurityUtils;
import com.doinner.csys.permission.CoursePermissionException;
import com.doinner.file.api.constant.DomainFieldConstants;
import com.doinner.file.api.domain.FileInfo;
import com.doinner.file.api.service.RemoteFileInfoService;
import com.doinner.system.domain.view.LoginUser;
import com.doinner.system.domain.entity.SysUser;
import com.doinner.system.service.DoinnerUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.util.*;

@Service
public class WorkOrderService {
    private static final Logger log = LoggerFactory.getLogger(WorkOrderService.class);
    private final JdbcTemplate jdbc;
    private final RemoteFileInfoService files;
    private final DoinnerUserService users;

    public WorkOrderService(JdbcTemplate jdbc, RemoteFileInfoService files, DoinnerUserService users) {
        this.jdbc = jdbc;
        this.files = files;
        this.users = users;
    }

    public Map<String, Object> upload(MultipartFile file) {
        long user = userId();
        if (file == null || file.isEmpty() || file.getSize() > 5L * 1024 * 1024) {
            throw error(400, "附件不能为空且不能超过5MB");
        }
        String name = file.getOriginalFilename();
        if (name == null || name.length() > 255 || name.contains("/") || name.contains("\\")
                || name.chars().anyMatch(Character::isISOControl)) throw error(400, "附件名称无效");
        int dot = name.lastIndexOf('.');
        String extension = dot < 0 ? "" : name.substring(dot + 1).toLowerCase(Locale.ROOT);
        if (!Arrays.asList("png", "jpg", "jpeg", "pdf", "doc", "docx", "xls", "xlsx", "ppt", "pptx", "txt", "csv", "zip", "rar", "7z").contains(extension))
            throw error(400, "不支持此附件类型");
        String type = "application/octet-stream";
        if (Arrays.asList("png", "jpg", "jpeg").contains(extension)) try (ImageInputStream input = ImageIO.createImageInputStream(file.getInputStream())) {
            if (input == null) throw error(400, "无效的图片");
            Iterator<ImageReader> readers = ImageIO.getImageReaders(input);
            if (!readers.hasNext()) throw error(400, "仅支持PNG和JPEG图片");
            ImageReader reader = readers.next();
            try {
                reader.setInput(input, true, true);
                String format = reader.getFormatName();
                if ("png".equalsIgnoreCase(format) && extension.equals("png")) type = "image/png";
                else if (("jpeg".equalsIgnoreCase(format) || "jpg".equalsIgnoreCase(format)) && !extension.equals("png")) type = "image/jpeg";
                else throw error(400, "仅支持PNG和JPEG图片");
                if ((long) reader.getWidth(0) * reader.getHeight(0) > 40000000L) {
                    throw error(400, "图片尺寸过大");
                }
            } finally {
                reader.dispose();
            }
        } catch (IOException e) {
            throw error(400, "无效的图片");
        }
        DataSet<FileInfo> result = files.upload(file, DomainFieldConstants.COURSE_CATEGORY_ID);
        FileInfo info = result == null ? null : result.getData();
        if (info == null || info.getFileId() == null || info.getFileId().trim().isEmpty()
                || info.getFileId().length() > 255) throw error(502, "附件上传失败");
        jdbc.update("INSERT INTO t_csys_work_order_image(file_id,creator_id,file_name,content_type) VALUES(?,?,?,?)",
                info.getFileId(), user, name, type);
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("fileId", info.getFileId());
        response.put("fileName", name);
        response.put("contentType", type);
        return response;
    }

    @Transactional
    public Map<String, Object> create(String description, List<String> imageIds) {
        long user = userId();
        String text = text(description);
        List<String> ids = images(imageIds);
        checkImages(ids, user, null);
        KeyHolder key = new GeneratedKeyHolder();
        jdbc.update(connection -> {
            PreparedStatement statement = connection.prepareStatement(
                    "INSERT INTO t_csys_work_order(creator_id,description) VALUES(?,?)", new String[]{"id"});
            statement.setLong(1, user);
            statement.setString(2, text);
            return statement;
        }, key);
        long id = Objects.requireNonNull(key.getKey()).longValue();
        attach(ids, user, id);
        return detail(id);
    }

    public Map<String, Object> list(boolean admin, int pageNum, int pageSize, String description, String status) {
        long user = userId();
        if (pageNum < 1 || pageSize < 1 || pageSize > 100) throw error(400, "分页参数无效");
        if (status != null && !status.isEmpty() && !status.equals("0") && !status.equals("1")) throw error(400, "状态无效");
        if (description != null && description.length() > 2000) throw error(400, "查询条件过长");
        StringBuilder where = new StringBuilder(" WHERE 1=1");
        List<Object> args = new ArrayList<>();
        if (!admin) {
            where.append(" AND creator_id=?");
            args.add(user);
        }
        if (description != null && !description.trim().isEmpty()) {
            where.append(" AND description LIKE ? ESCAPE '!'");
            args.add("%" + description.trim().replace("!", "!!").replace("%", "!%").replace("_", "!_") + "%");
        }
        if ("0".equals(status)) where.append(" AND reply_version=0");
        if ("1".equals(status)) where.append(" AND reply_version>0");
        long total = Objects.requireNonNull(jdbc.queryForObject("SELECT COUNT(*) FROM t_csys_work_order" + where, Long.class, args.toArray()));
        args.add(pageSize);
        args.add(((long) pageNum - 1) * pageSize);
        String order = admin ? " ORDER BY CASE WHEN reply_version=0 THEN 0 ELSE 1 END, created_at DESC, id DESC"
                : " ORDER BY id DESC";
        List<Long> ids = jdbc.queryForList("SELECT id FROM t_csys_work_order" + where + order + " LIMIT ? OFFSET ?", Long.class, args.toArray());
        List<Map<String, Object>> rows = new ArrayList<>();
        Map<Long, String> names = new HashMap<>();
        for (Long id : ids) {
            Map<String, Object> row = detail(id);
            if (admin) {
                Long creator = (Long) row.get("creatorId");
                if (!names.containsKey(creator)) names.put(creator, creatorName(creator));
                row.put("creatorName", names.get(creator));
            }
            rows.add(row);
        }
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("rows", rows);
        result.put("total", total);
        return result;
    }

    public Map<String, Object> detail(long id) {
        Map<String, Object> row = require(id, false);
        long version = ((Number) row.get("reply_version")).longValue();
        long read = ((Number) row.get("read_version")).longValue();
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("id", ((Number) row.get("id")).longValue());
        result.put("description", row.get("description"));
        result.put("imageIds", jdbc.queryForList("SELECT file_id FROM t_csys_work_order_image WHERE work_order_id=? ORDER BY file_id", String.class, id));
        List<Map<String, Object>> attachments = new ArrayList<>();
        for (Map<String, Object> file : jdbc.queryForList("SELECT file_id,file_name,content_type FROM t_csys_work_order_image WHERE work_order_id=? ORDER BY file_id", id)) {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("fileId", file.get("file_id"));
            item.put("fileName", file.get("file_name"));
            item.put("contentType", file.get("content_type"));
            attachments.add(item);
        }
        result.put("attachments", attachments);
        result.put("status", version == 0 ? "0" : "1");
        result.put("reply", row.get("reply"));
        result.put("replyDescription", row.get("reply_description"));
        result.put("replyTime", row.get("reply_time"));
        result.put("replyVersion", version);
        result.put("unread", version > read);
        result.put("creatorId", ((Number) row.get("creator_id")).longValue());
        result.put("createdAt", row.get("created_at"));
        result.put("updatedAt", row.get("updated_at"));
        return result;
    }

    @Transactional
    public void update(long id, String description, List<String> imageIds) {
        long user = userId();
        String value = text(description);
        List<String> ids = images(imageIds);
        Map<String, Object> row = require(id, true);
        owner(row, user);
        checkImages(ids, user, id);
        jdbc.update("UPDATE t_csys_work_order SET description=?,updated_at=CURRENT_TIMESTAMP WHERE id=?", value, id);
        jdbc.update("UPDATE t_csys_work_order_image SET work_order_id=NULL WHERE work_order_id=?", id);
        attach(ids, user, id);
    }

    @Transactional
    public void delete(long id) {
        long user = userId();
        Map<String, Object> row = require(id, true);
        owner(row, user);
        jdbc.update("UPDATE t_csys_work_order_image SET work_order_id=NULL WHERE work_order_id=?", id);
        jdbc.update("DELETE FROM t_csys_work_order WHERE id=?", id);
    }

    @Transactional
    public void reply(long id, String reply) {
        userId();
        String value = text(reply);
        if (jdbc.update("UPDATE t_csys_work_order SET reply=?,reply_description=description,reply_time=CURRENT_TIMESTAMP,reply_version=reply_version+1,updated_at=CURRENT_TIMESTAMP WHERE id=?", value, id) == 0) {
            throw error(404, "工单不存在");
        }
    }

    public long unreadCount() {
        return Objects.requireNonNull(jdbc.queryForObject("SELECT COUNT(*) FROM t_csys_work_order WHERE creator_id=? AND reply_version>read_version", Long.class, userId()));
    }

    public void read(long id, long version) {
        long user = userId();
        Map<String, Object> row = require(id, false);
        owner(row, user);
        if (version < 1) throw error(400, "反馈版本无效");
        if (jdbc.update("UPDATE t_csys_work_order SET read_version=? WHERE id=? AND creator_id=? AND reply_version=?", version, id, user, version) == 0) {
            throw error(409, "反馈已更新，请重新查看");
        }
    }

    public Map<String, Object> image(long id, String fileId) {
        require(id, false);
        List<Map<String, Object>> rows = jdbc.queryForList("SELECT file_id,file_name,content_type FROM t_csys_work_order_image WHERE work_order_id=? AND file_id=?", id, fileId);
        if (rows.isEmpty()) throw error(404, "截图不存在");
        DataSet<byte[]> response = files.getFileById(fileId);
        if (response == null || response.getData() == null) throw error(502, "截图读取失败");
        Map<String, Object> result = new HashMap<>();
        result.put("contentType", rows.get(0).get("content_type"));
        result.put("fileName", rows.get(0).get("file_name"));
        result.put("bytes", response.getData());
        return result;
    }

    private Map<String, Object> require(long id, boolean lock) {
        userId();
        if (id <= 0) throw error(400, "工单ID无效");
        List<Map<String, Object>> rows = jdbc.queryForList("SELECT * FROM t_csys_work_order WHERE id=?" + (lock ? " FOR UPDATE" : ""), id);
        if (rows.isEmpty()) throw error(404, "工单不存在");
        Map<String, Object> row = rows.get(0);
        return row;
    }

    private void checkImages(List<String> ids, long user, Long orderId) {
        for (String id : ids) {
            List<Map<String, Object>> rows = jdbc.queryForList("SELECT creator_id,work_order_id FROM t_csys_work_order_image WHERE file_id=? FOR UPDATE", id);
            if (rows.isEmpty() || ((Number) rows.get(0).get("creator_id")).longValue() != user
                    || (rows.get(0).get("work_order_id") != null && !Objects.equals(((Number) rows.get(0).get("work_order_id")).longValue(), orderId))) {
                throw error(400, "截图不存在或不可使用");
            }
        }
    }

    private void attach(List<String> ids, long user, long orderId) {
        for (String id : ids) {
            if (jdbc.update("UPDATE t_csys_work_order_image SET work_order_id=? WHERE file_id=? AND creator_id=? AND work_order_id IS NULL", orderId, id, user) != 1) {
                throw error(409, "截图已被使用");
            }
        }
    }

    private static List<String> images(List<String> ids) {
        if (ids == null) return Collections.emptyList();
        if (ids.size() > 5 || ids.stream().anyMatch(id -> id == null || id.trim().isEmpty() || id.length() > 255)
                || new HashSet<>(ids).size() != ids.size()) throw error(400, "截图列表无效");
        List<String> sorted = new ArrayList<>(ids);
        Collections.sort(sorted);
        return sorted;
    }

    private static String text(String value) {
        if (value == null || value.trim().isEmpty() || value.length() > 2000) throw error(400, "内容不能为空且不能超过2000字");
        return value.trim();
    }

    private static void owner(Map<String, Object> row, long user) {
        if (((Number) row.get("creator_id")).longValue() != user) throw error(403, "仅提交人可操作");
    }

    private String creatorName(Long creatorId) {
        try {
            DataSet<SysUser> response = users.getUserById(creatorId);
            SysUser user = response == null ? null : response.getData();
            return user == null ? null : user.getNickName();
        } catch (RuntimeException e) {
            log.warn("Unable to resolve work order creator {}", creatorId, e);
            return null;
        }
    }

    private static long userId() {
        Long id = SecurityUtils.getUserId();
        LoginUser user = SecurityUtils.getLoginUser();
        if (id == null || id <= 0) id = user == null ? null : user.getUserid();
        if (id == null || id <= 0) throw error(401, "请先登录");
        return id;
    }

    private static CoursePermissionException error(int status, String message) {
        return new CoursePermissionException(status, message);
    }
}
