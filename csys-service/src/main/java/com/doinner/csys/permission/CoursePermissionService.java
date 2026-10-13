package com.doinner.csys.permission;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.doinner.common.core.domain.DataSet;
import com.doinner.common.security.utils.SecurityUtils;
import com.doinner.csys.domain.Course;
import com.doinner.csys.domain.vo.*;
import com.doinner.system.domain.entity.SysUser;
import com.doinner.system.domain.view.LoginUser;
import com.doinner.system.service.DoinnerUserService;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Array;
import java.util.*;
import java.util.stream.Collectors;

/** Course permissions use only persisted author_ids; creators/admins only manage the list. */
@Service
public class CoursePermissionService {
    private final JdbcTemplate jdbc;
    private final DoinnerUserService users;

    public CoursePermissionService(JdbcTemplate jdbc, DoinnerUserService users) {
        this.jdbc = jdbc;
        this.users = users;
    }

    public void check(CourseResource resource, Object ids, Object request, boolean lock) {
        Long userId = userId();
        List<Long> resources = ids(ids);
        SortedSet<Long> roots = new TreeSet<>();
        for (Long id : resources) {
            for (Long course : courseIds(resource, id)) roots.add(root(course, false).getId());
        }
        for (Long id : roots) {
            Course course = readCourse(id, lock);
            if (!hasAuthor(course.getAuthorIds(), userId))
                throw new CoursePermissionException(403, "您不是该课程执笔人，无权操作课程相关数据");
        }
        if (lock) for (Long id : resources) row(resource, id, true);
        // Owner fields are immutable even when the user happens to edit both courses.
        if (request != null) validateRequest(resource, request);
        if (resource != CourseResource.COURSE) {
            for (Long id : resources) {
                Long planId = planId(resource, id);
                if (planId != null) checkPlanStatus(planId);
            }
        }
    }

    public void requireCourseWrite(Long courseId) {
        check(CourseResource.COURSE, courseId, null, false);
    }

    /** Use inside the write transaction, immediately before final file binding. */
    public void requireCourseWriteLocked(Long courseId) {
        check(CourseResource.COURSE, courseId, null, true);
    }

    /** Filter by the persisted row, never by a client-provided sourceId. */
    public List<Long> sourceCourseIds(Object values) {
        List<Long> sourceIds = new ArrayList<>();
        for (Long id : ids(values)) {
            if (readCourse(id, false).getSourceId() == null) sourceIds.add(id);
        }
        return sourceIds;
    }

    public CoursePermissionVo getPermission(Long courseId) {
        Long userId = userId();
        Course course = root(courseId, false);
        CoursePermissionVo result = new CoursePermissionVo();
        result.setCourseId(courseId);
        result.setPermissionCourseId(course.getId());
        result.setAuthors(course.getAuthors());
        result.setAuthorIds(course.getAuthorIds());
        result.setCanOperate(!courseId.equals(course.getId()) || hasAuthor(course.getAuthorIds(), userId));
        result.setCanConfigureAuthors(canConfigure(course, userId));
        return result;
    }

    @Transactional(rollbackFor = Exception.class)
    public CoursePermissionVo saveAuthors(Long courseId, CourseAuthorsSaveVo request) {
        Long userId = userId();
        Course course = root(courseId, true);
        if (!canConfigure(course, userId))
            throw new CoursePermissionException(403, "仅课程执笔人、创建人或系统管理员可配置执笔人");
        if (request == null || request.getAuthorIds() == null)
            throw new CoursePermissionException(400, "authorIds 必填，清空名单请传空字符串");
        String[] values = normalizeAuthors(request.getAuthorIds());
        jdbc.update("UPDATE t_csys_course SET authors=?,author_ids=?,last_modifier=?,last_modified_time=CURRENT_TIMESTAMP WHERE id=? AND sysflag=0",
                values[0], values[1], username(), course.getId());
        // Existing invoked rows retain display compatibility. Authorization always reads the root.
        jdbc.update("UPDATE t_csys_course SET authors=?,author_ids=?,last_modifier=?,last_modified_time=CURRENT_TIMESTAMP WHERE source_id=? AND sysflag=0",
                values[0], values[1], username(), course.getId());
        return getPermission(courseId);
    }

    public void preserveCourseOwners(Course course) {
        Course stored = readCourse(course.getId(), true);
        if (stored.getSourceId() == null && course.getAuthorIds() != null && !sameIds(course.getAuthorIds(), stored.getAuthorIds()))
            throw new CoursePermissionException(400, "请通过执笔人配置接口修改执笔人");
        if (stored.getSourceId() == null && course.getAuthors() != null && !Objects.equals(course.getAuthors(), stored.getAuthors()))
            throw new CoursePermissionException(400, "请通过执笔人配置接口修改执笔人");
        if (course.getSourceId() != null && !Objects.equals(course.getSourceId(), stored.getSourceId()))
            throw new CoursePermissionException(400, "不能修改课程来源");
        if (course.getCreator() != null && !Objects.equals(course.getCreator(), stored.getCreator()))
            throw new CoursePermissionException(400, "不能修改课程创建人");
        if (stored.getSourceId() == null) {
            course.setAuthors(stored.getAuthors());
            course.setAuthorIds(stored.getAuthorIds());
        }
        course.setCreator(stored.getCreator());
        course.setCreateTime(stored.getCreateTime());
        course.setSourceId(stored.getSourceId());
        course.setSysflag(stored.getSysflag());
    }

    public void restoreCourseOwners(Course course) {
        Course stored = readCourse(course.getId(), true);
        course.setAuthors(stored.getAuthors());
        course.setAuthorIds(stored.getAuthorIds());
        course.setCreator(stored.getCreator());
        course.setCreateTime(stored.getCreateTime());
        course.setSourceId(stored.getSourceId());
        course.setSysflag(stored.getSysflag());
    }

    public void requireReviewCourse(Long reviewId, Long courseId) {
        if (!Objects.equals(required(row(CourseResource.REVIEW, reviewId, false).get("course_id")), courseId))
            throw new CoursePermissionException(400, "历史版本不属于当前课程");
    }

    public void requireSourceCourse(Long courseId) {
        if (readCourse(courseId, false).getSourceId() != null)
            throw new CoursePermissionException(400, "该操作仅支持总库课程");
    }

    public Long sourceCourseId(Long courseId) { return root(courseId, false).getId(); }

    public List<Long> knowledgeCourseFamily(Long sourceCourseId) {
        requireSourceCourse(sourceCourseId);
        SortedSet<Long> all = new TreeSet<>();
        List<Long> pending = Collections.singletonList(sourceCourseId);
        while (!pending.isEmpty()) {
            List<Long> next = new ArrayList<>();
            for (Long id : pending) {
                if (!all.add(id)) continue;
                next.addAll(jdbc.queryForList("SELECT id FROM t_csys_course WHERE source_id=? AND sysflag=0", Long.class, id));
            }
            pending = next;
        }
        return new ArrayList<>(all);
    }

    /** Copying only changes target-course relationships; no source content-edit privilege required. */
    public void lockKnowledgeSources(List<Long> courseIds) {
        SortedSet<Long> roots = new TreeSet<>();
        for (Long id : ids(courseIds)) roots.add(root(id, false).getId());
        roots.forEach(id -> readCourse(id, true));
    }

    public void synchronizeKnowledgeReferences(Long sourceCourseId) {
        List<Long> units = jdbc.queryForList("SELECT course_unit_id FROM t_csys_course_ref_knowledge_unit WHERE course_id=?", Long.class, sourceCourseId);
        for (Long courseId : knowledgeCourseFamily(sourceCourseId)) {
            if (sourceCourseId.equals(courseId)) continue;
            jdbc.update("DELETE FROM t_csys_course_ref_knowledge_unit WHERE course_id=?", courseId);
            for (Long unit : units) jdbc.update("INSERT INTO t_csys_course_ref_knowledge_unit(course_id,course_unit_id) VALUES(?,?)", courseId, unit);
        }
    }

    public void validateCourseContent(CourseVo request) {
        if (request == null) throw new CoursePermissionException(400, "课程不能为空");
        Long id = request.getId();
        if (id != null && readCourse(id, false).getSourceId() != null) return;
        if (request.getCourseTeachingTheoryPlanVo() != null)
            childCourse(CourseResource.THEORY_PLAN, request.getCourseTeachingTheoryPlanVo(), id);
        if (request.getCourseTeachingPracticePlanVo() != null)
            childCourse(CourseResource.PRACTICE_PLAN, request.getCourseTeachingPracticePlanVo(), id);
        if (request.getCourseChapterVoList() != null)
            request.getCourseChapterVoList().forEach(item -> childCourse(CourseResource.COURSE_CHAPTER, item, id));
        if (request.getCourseTextbookVoList() != null)
            request.getCourseTextbookVoList().forEach(item -> childCourse(CourseResource.COURSE_TEXTBOOK, item, id));
        if (request.getCourseTargetList() != null)
            request.getCourseTargetList().forEach(item -> childCourse(CourseResource.COURSE_TARGET, item, id));
    }

    private void childCourse(CourseResource type, Object item, Long courseId) {
        if (item == null) throw new CoursePermissionException(400, "课程子记录不能为空");
        BeanWrapper bean = new BeanWrapperImpl(item);
        String property = type == CourseResource.COURSE_TARGET ? "courseTargetId" : "id";
        Long id = number(bean.getPropertyValue(property));
        if (id != null) {
            Map<String, Object> stored = row(type, id, false);
            if (courseId == null || !courseId.equals(number(stored.get("course_id"))))
                throw new CoursePermissionException(400, "课程子记录不属于当前课程");
        }
        if (bean.isWritableProperty("courseId")) bean.setPropertyValue("courseId", courseId);
    }

    public void validateRequest(CourseResource type, Object request) {
        BeanWrapper bean = new BeanWrapperImpl(request);
        String key = type == CourseResource.COURSE_TARGET ? "courseTargetId" : "id";
        if (!bean.isReadableProperty(key)) return;
        Long id = number(bean.getPropertyValue(key));
        if (id == null) return;
        Map<String, Object> stored = row(type, id, false);
        String[][] fields = {{"planId", "plan_id"}, {"courseId", "course_id"},
                {"sourceCourseId", "source_course_id"}, {"itemId", "item_id"},
                {"creator", "creator"}};
        for (String[] field : fields) {
            if (!stored.containsKey(field[1]) || !bean.isReadableProperty(field[0])) continue;
            Object supplied = bean.getPropertyValue(field[0]);
            Object old = stored.get(field[1]);
            if (supplied != null && !Objects.equals(String.valueOf(supplied), old == null ? null : String.valueOf(old)))
                throw new CoursePermissionException(400, "不能修改记录归属或创建人：" + field[0]);
            if (supplied == null && old != null && bean.isWritableProperty(field[0]))
                bean.setPropertyValue(field[0], field[0].equals("creator") ? old.toString() : number(old));
        }
        if (type == CourseResource.OBJECTIVE_REF && bean.isReadableProperty("objectiveId")) {
            Long old = number(stored.get("objective_id"));
            Long supplied = number(bean.getPropertyValue("objectiveId"));
            if (supplied != null && !Objects.equals(supplied, old))
                throw new CoursePermissionException(400, "不能修改关联记录所属目标");
            bean.setPropertyValue("objectiveId", old);
        }
        if (type == CourseResource.PLAN) {
            for (String[] field : new String[][]{{"status", "status"}, {"currentFlag", "current_flag"}, {"rootPlanId", "root_plan_id"}}) {
                if (bean.isReadableProperty(field[0]) && bean.getPropertyValue(field[0]) != null
                        && !Objects.equals(number(bean.getPropertyValue(field[0])), number(stored.get(field[1]))))
                    throw new CoursePermissionException(400, "普通内容保存不能改变教学计划流程字段：" + field[0]);
            }
        }
        if (bean.isWritableProperty("sysflag")) bean.setPropertyValue("sysflag", stored.get("sysflag"));
    }

    public void validateKnowledge(CourseKnowledgeVo request) {
        if (request == null || request.getUnitList() == null)
            throw new CoursePermissionException(400, "unitList 必填，清空知识体系请传空数组");
        for (CourseKnowledgeUnitVo unit : request.getUnitList()) {
            if (unit == null || unit.getName() == null || unit.getName().trim().isEmpty())
                throw new CoursePermissionException(400, "知识单元名称不能为空");
            if (unit.getId() != null && jdbc.queryForObject("SELECT COUNT(*) FROM t_csys_course_ref_knowledge_unit WHERE course_id=? AND course_unit_id=?", Integer.class,
                    request.getCourseId(), unit.getId()) == 0)
                throw new CoursePermissionException(400, "知识单元不属于当前课程");
            if (unit.getKnowledgePoints() == null) unit.setKnowledgePoints(Collections.emptyList());
            for (CourseKnowledgePointVo point : unit.getKnowledgePoints()) {
                if (point == null || point.getName() == null || point.getName().trim().isEmpty())
                    throw new CoursePermissionException(400, "知识点名称不能为空");
                if (point.getId() != null && (unit.getId() == null || jdbc.queryForObject(
                        "SELECT COUNT(*) FROM t_csys_course_knowledge_point WHERE id=? AND unit_id=?", Integer.class, point.getId(), unit.getId()) == 0))
                    throw new CoursePermissionException(400, "知识点不属于当前知识单元");
            }
        }
    }

    /** Only reclaim unreferenced units; plan JSON bindings keep their original IDs alive. */
    public List<Long> unreferencedKnowledgeUnits(List<Long> unitIds) {
        if (unitIds.isEmpty()) return Collections.emptyList();
        Set<Long> boundUnits = new HashSet<>();
        Set<Long> boundPoints = new HashSet<>();
        List<String> jsonBindings = jdbc.queryForList("SELECT knowledge_points FROM t_csys_teaching_plan_target_design WHERE sysflag=0 AND knowledge_points IS NOT NULL AND knowledge_points<>'' AND knowledge_points<>'[]'", String.class);
        for (String json : jsonBindings) {
            try {
                JSONArray values = JSON.parseArray(json);
                for (int i = 0; i < values.size(); i++) {
                    JSONObject value = values.getJSONObject(i);
                    if (value == null) continue;
                    if (value.getLong("knowledgeUnitId") != null) boundUnits.add(value.getLong("knowledgeUnitId"));
                    if (value.getLong("knowledgePointId") != null) boundPoints.add(value.getLong("knowledgePointId"));
                }
            } catch (RuntimeException exception) {
                // Malformed historical bindings cannot be proven unreferenced.
                return Collections.emptyList();
            }
        }
        List<Long> result = new ArrayList<>();
        for (Long id : new TreeSet<>(unitIds)) {
            int refs = jdbc.queryForObject("SELECT COUNT(*) FROM t_csys_course_ref_knowledge_unit WHERE course_unit_id=?", Integer.class, id);
            if (refs > 0) continue;
            int plans = jdbc.queryForObject("SELECT COUNT(*) FROM t_csys_teaching_plan_target_design WHERE sysflag=0 AND (knowledge_unit_id=? OR knowledge_point_id IN (SELECT id FROM t_csys_course_knowledge_point WHERE unit_id=?))", Integer.class, id, id);
            List<Long> points = jdbc.queryForList("SELECT id FROM t_csys_course_knowledge_point WHERE unit_id=?", Long.class, id);
            if (plans == 0 && !boundUnits.contains(id) && Collections.disjoint(points, boundPoints)) result.add(id);
        }
        return result;
    }

    public void requireSamePlan(CourseResource resource, Long resourceId, Long expectedPlanId) {
        if (resourceId != null && !Objects.equals(planId(resource, resourceId), expectedPlanId))
            throw new CoursePermissionException(400, "关联记录不属于当前教学计划");
    }

    public void validatePlanScope(CourseResource resource, Object idValue, Object input) {
        if (input == null || input instanceof Number || input instanceof Collection<?> || input.getClass().isArray()) return;
        List<Long> resourceIds = ids(idValue);
        Long plan = resource == CourseResource.PLAN ? resourceIds.get(0) : planId(resource, resourceIds.get(0));
        if (plan == null) return;
        BeanWrapper bean = new BeanWrapperImpl(input);
        if (bean.isReadableProperty("objective") && bean.getPropertyValue("objective") != null) {
            validatePlanScope(resource, idValue, bean.getPropertyValue("objective"));
            return;
        }
        if (bean.isReadableProperty("planId") && bean.getPropertyValue("planId") != null
                && !plan.equals(number(bean.getPropertyValue("planId"))))
            throw new CoursePermissionException(400, "请求planId与记录所属教学计划不一致");
        if (bean.isReadableProperty("schemeId") && bean.getPropertyValue("schemeId") != null) {
            Long source = number(row(CourseResource.PLAN, plan, false).get("source_course_id"));
            Long scheme = number(bean.getPropertyValue("schemeId"));
            int count = jdbc.queryForObject("SELECT COUNT(*) FROM t_csys_training_scheme_ref_course r JOIN t_csys_course c ON c.id=r.course_id WHERE r.scheme_id=? AND c.sysflag=0 AND (c.id=? OR c.source_id=?)", Integer.class, scheme, source, source);
            if (count == 0) throw new CoursePermissionException(400, "培养方案未引用当前课程");
        }
        if ((resource == CourseResource.TARGET_DESIGN || resource == CourseResource.PRACTICE_DETAIL)
                && bean.isReadableProperty("objectiveId"))
            requireSamePlan(CourseResource.OBJECTIVE, number(bean.getPropertyValue("objectiveId")), plan);
    }

    public void requireUnitDomain(Long unitId, Long domainId) {
        Map<String, Object> domain = row(CourseResource.KNOWLEDGE_DOMAIN, domainId, false);
        Long courseId = required(domain.get("course_id"));
        if (!courseIds(CourseResource.KNOWLEDGE_UNIT, unitId).contains(courseId))
            throw new CoursePermissionException(400, "知识单元不属于当前知识领域的课程");
        String units = string(domain.get("unit_ids"));
        if (!splitIds(units).contains(String.valueOf(unitId)))
            throw new CoursePermissionException(400, "知识单元不属于当前知识领域");
    }

    public void requireSystemMaintenance() {
        if (!SecurityUtils.isAdmin(userId())) throw new CoursePermissionException(403, "仅系统管理员可执行全库维护");
    }

    public Long planId(CourseResource type, Long id) {
        if (type == CourseResource.PLAN) { row(type, id, false); return id; }
        Map<String, Object> stored = row(type, id, false);
        if (type == CourseResource.PRACTICE_DETAIL)
            return planId(CourseResource.PRACTICE_ITEM, number(stored.get("item_id")));
        return number(stored.get("plan_id"));
    }

    private void checkPlanStatus(Long planId) {
        Map<String, Object> plan = row(CourseResource.PLAN, planId, true);
        Long status = number(plan.get("status"));
        if (Objects.equals(status, 1L) || Objects.equals(status, 2L))
            throw new CoursePermissionException(409, "教学计划审核中或已通过，不能修改或删除内容");
    }

    private List<Long> courseIds(CourseResource type, Long id) {
        Map<String, Object> stored = row(type, id, false);
        if (type == CourseResource.COURSE) return Collections.singletonList(id);
        if (type == CourseResource.PLAN) return Collections.singletonList(required(stored.get("source_course_id")));
        Long plan = planId(type, id);
        if (plan != null) return courseIds(CourseResource.PLAN, plan);
        if (stored.containsKey("course_id")) return Collections.singletonList(required(stored.get("course_id")));
        if (type == CourseResource.KNOWLEDGE_UNIT) {
            List<Long> courses = jdbc.queryForList("SELECT DISTINCT course_id FROM t_csys_course_ref_ke_unit WHERE unit_id=?", Long.class, id);
            if (courses.isEmpty()) throw new CoursePermissionException(400, "知识单元没有课程归属");
            return courses;
        }
        if (type == CourseResource.KNOWLEDGE_POINT) {
            List<Long> units = jdbc.queryForList("SELECT DISTINCT unit_id FROM t_csys_knowledge_unit_ref_point WHERE point_id=?", Long.class, id);
            SortedSet<Long> courses = new TreeSet<>();
            for (Long unit : units) courses.addAll(courseIds(CourseResource.KNOWLEDGE_UNIT, unit));
            if (courses.isEmpty()) throw new CoursePermissionException(400, "知识点没有课程归属");
            return new ArrayList<>(courses);
        }
        throw new CoursePermissionException(400, "记录没有有效课程归属");
    }

    private Course root(Long id, boolean lock) {
        Set<Long> seen = new HashSet<>();
        Course course = readCourse(required(id), false);
        while (course.getSourceId() != null) {
            if (!seen.add(course.getId())) throw new CoursePermissionException(400, "课程来源存在循环");
            course = readCourse(course.getSourceId(), false);
        }
        return lock ? readCourse(course.getId(), true) : course;
    }

    private Course readCourse(Long id, boolean lock) {
        Map<String, Object> value = row(CourseResource.COURSE, required(id), lock);
        Course result = new Course();
        result.setId(id);
        result.setSourceId(number(value.get("source_id")));
        result.setAuthors(string(value.get("authors")));
        result.setAuthorIds(string(value.get("author_ids")));
        result.setCreator(string(value.get("creator")));
        result.setSysflag(number(value.get("sysflag")).intValue());
        Object created = value.get("create_time");
        if (created instanceof java.sql.Timestamp) result.setCreateTime(((java.sql.Timestamp) created).toLocalDateTime());
        else if (created instanceof java.time.LocalDateTime) result.setCreateTime((java.time.LocalDateTime) created);
        return result;
    }

    private Map<String, Object> row(CourseResource type, Long id, boolean lock) {
        List<Map<String, Object>> rows = jdbc.queryForList("SELECT * FROM " + type.table + " WHERE " + type.key + "=?" + (lock ? " FOR UPDATE" : ""), required(id));
        if (rows.size() != 1) throw new CoursePermissionException(404, "课程或关联记录不存在");
        Map<String, Object> row = rows.get(0);
        if (row.containsKey("sysflag") && !Objects.equals(number(row.get("sysflag")), 0L))
            throw new CoursePermissionException(404, "课程或关联记录已删除");
        return row;
    }

    private boolean canConfigure(Course course, Long id) {
        return hasAuthor(course.getAuthorIds(), id) || SecurityUtils.isAdmin(id)
                || (username() != null && username().equals(course.getCreator()));
    }

    public static boolean hasAuthor(String authorIds, Long userId) {
        if (userId == null || userId <= 0 || authorIds == null) return false;
        for (String id : authorIds.split(",", -1)) {
            if (id.trim().equals(userId.toString())) return true;
        }
        return false;
    }

    private String[] normalizeAuthors(String value) {
        LinkedHashSet<Long> ids = new LinkedHashSet<>();
        if (!value.trim().isEmpty()) {
            for (String item : value.split(",", -1)) {
                if (!item.trim().matches("[1-9][0-9]*")) throw new CoursePermissionException(400, "authorIds 必须为英文逗号分隔的有效用户ID");
                ids.add(required(item.trim()));
            }
        }
        List<String> names = new ArrayList<>();
        for (Long id : ids) {
            DataSet<SysUser> response;
            try { response = users.getUserById(id); }
            catch (RuntimeException exception) { throw new CoursePermissionException(503, "用户服务不可用，无法校验执笔人"); }
            SysUser user = response == null ? null : response.getData();
            if (user == null || !id.equals(user.getUserId()) || !"0".equals(user.getStatus()) || !"0".equals(user.getDelFlag()))
                throw new CoursePermissionException(400, "执笔人不存在、已停用或已删除：" + id);
            String name = user.getNickName() == null || user.getNickName().trim().isEmpty() ? user.getUserName() : user.getNickName();
            if (name == null || name.trim().isEmpty()) throw new CoursePermissionException(400, "执笔人缺少有效姓名：" + id);
            names.add(name);
        }
        String idsValue = ids.stream().map(String::valueOf).collect(Collectors.joining(","));
        String namesValue = String.join(",", names);
        if (idsValue.length() > 500 || namesValue.length() > 500) throw new CoursePermissionException(400, "执笔人名单超过字段长度限制");
        return new String[]{namesValue, idsValue};
    }

    private static Set<String> splitIds(String value) {
        if (value == null || value.trim().isEmpty()) return Collections.emptySet();
        return Arrays.stream(value.split(",", -1)).map(String::trim).collect(Collectors.toSet());
    }
    private static boolean sameIds(String left, String right) { return splitIds(left).equals(splitIds(right)); }
    private static String string(Object value) { return value == null ? null : value.toString(); }
    private static Long number(Object value) {
        if (value == null) return null;
        try { return Long.valueOf(value.toString()); }
        catch (NumberFormatException exception) { throw new CoursePermissionException(400, "无效ID"); }
    }
    private static Long required(Object value) {
        Long id = number(value);
        if (id == null || id <= 0) throw new CoursePermissionException(400, "课程或关联记录ID不能为空");
        return id;
    }
    private static List<Long> ids(Object value) {
        List<Long> ids = new ArrayList<>();
        if (value instanceof Collection<?>) for (Object id : (Collection<?>) value) ids.add(required(id));
        else if (value != null && value.getClass().isArray()) for (int i = 0; i < Array.getLength(value); i++) ids.add(required(Array.get(value, i)));
        else ids.add(required(value));
        if (ids.isEmpty()) throw new CoursePermissionException(400, "课程或关联记录ID集合不能为空");
        return new ArrayList<>(new TreeSet<>(ids));
    }
    private static Long userId() {
        Long id = SecurityUtils.getUserId();
        LoginUser user = SecurityUtils.getLoginUser();
        if (id == null || id <= 0) id = user == null ? null : user.getUserid();
        if (id == null || id <= 0) throw new CoursePermissionException(401, "请先登录");
        return id;
    }
    private static String username() {
        String name = SecurityUtils.getUsername();
        LoginUser user = SecurityUtils.getLoginUser();
        return name == null || name.trim().isEmpty() ? (user == null ? null : user.getUsername()) : name;
    }
}
