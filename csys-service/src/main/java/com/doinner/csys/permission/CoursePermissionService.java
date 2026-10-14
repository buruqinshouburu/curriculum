package com.doinner.csys.permission;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.doinner.common.security.utils.SecurityUtils;
import com.doinner.csys.domain.Course;
import com.doinner.csys.domain.CourseTeamMember;
import com.doinner.csys.domain.vo.*;
import com.doinner.system.domain.view.LoginUser;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Array;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

/** Authors and course team members edit their courses; creators edit unassigned courses; cur can operate all courses. */
@Service
public class CoursePermissionService {

    /** 课程库团队成员表（团队成员业务身份，与执笔人相互独立） */
    private static final String TEAM_TABLE = "t_csys_course_team_member";

    private final JdbcTemplate jdbc;

    public CoursePermissionService(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
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
            if (!canOperateCourse(course, userId, teamMembers(course.getId(), lock)))
                throw new CoursePermissionException(403, "您不是该课程负责人或团队成员，无权操作课程相关数据");
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
        List<CourseTeamMember> teamMembers = excludeCourseOwners(teamMembers(course.getId()), course.getAuthorIds());
        CoursePermissionVo result = new CoursePermissionVo();
        result.setCourseId(courseId);
        result.setPermissionCourseId(course.getId());
        result.setAuthors(course.getAuthors());
        result.setAuthorIds(course.getAuthorIds());
        result.setTeamMembers(teamMembers);
        result.setTeamMemberIds(joinUserIds(teamMembers));
        result.setTeamMemberNames(joinUserNames(teamMembers));
        result.setCanOperate(!courseId.equals(course.getId()) || canOperateCourse(course, userId, teamMembers));
        result.setCanConfigureAuthors(canConfigure(course, userId));
        result.setCanConfigureTeamMembers(canConfigureTeam(course, userId, teamMembers));
        return result;
    }

    @Transactional(rollbackFor = Exception.class)
    public CoursePermissionVo saveAuthors(Long courseId, CourseAuthorsSaveVo request) {
        Long userId = userId();
        Course course = root(courseId, true);
        if (!canConfigure(course, userId))
            throw new CoursePermissionException(403, "仅课程执笔人、创建人或课程管理员可配置执笔人");
        if (request == null || request.getAuthorIds() == null)
            throw new CoursePermissionException(400, "authorIds 必填，清空名单请传空字符串");
        String[] values = normalizeAuthors(course, request);
        jdbc.update("UPDATE t_csys_course SET authors=?,author_ids=?,last_modifier=?,last_modified_time=CURRENT_TIMESTAMP WHERE id=? AND sysflag=0",
                values[0], values[1], username(), course.getId());
        // Existing invoked rows retain display compatibility. Authorization always reads the root.
        jdbc.update("UPDATE t_csys_course SET authors=?,author_ids=?,last_modifier=?,last_modified_time=CURRENT_TIMESTAMP WHERE source_id=? AND sysflag=0",
                values[0], values[1], username(), course.getId());
        return getPermission(courseId);
    }

    // 人员帐号沿用 author_ids 的口径：第三方 email = sys_user.user_name。
    public LinkedHashSet<String> teamMemberIdSet(Long courseId) {
        LinkedHashSet<String> accounts = new LinkedHashSet<>();
        for (CourseTeamMember member : teamMembers(required(courseId), true)) accounts.add(member.getUserId());
        return accounts;
    }

    public List<CourseTeamMember> listTeamMembers(Long courseId) {
        Course course = root(courseId, false);
        return excludeCourseOwners(teamMembers(course.getId()), course.getAuthorIds());
    }

    @Transactional(rollbackFor = Exception.class)
    public CoursePermissionVo saveTeamMembers(Long courseId, CourseTeamMemberSaveVo request) {
        Course course = requireTeamConfigure(courseId);
        LinkedHashMap<String, String> resolved = resolveMembers(course, requestedMembers(request));
        Set<String> owners = splitIds(course.getAuthorIds());
        // 查询隐藏负责人团队身份，全量保存可见名单时保留这些隐藏行。
        for (String account : teamMemberIdSet(course.getId())) {
            if (!resolved.containsKey(account) && !owners.contains(account)) deactivateTeamMember(course.getId(), account);
        }
        int sort = 1;
        for (Map.Entry<String, String> entry : resolved.entrySet())
            upsertTeamMemberRow(course.getId(), entry.getKey(), entry.getValue(), sort++);
        return getPermission(courseId);
    }

    @Transactional(rollbackFor = Exception.class)
    public CoursePermissionVo addTeamMembers(Long courseId, CourseTeamMemberSaveVo request) {
        Course course = requireTeamConfigure(courseId);
        List<CoursePersonVo> members = requestedMembers(request);
        if (members.isEmpty()) throw new CoursePermissionException(400, "members 必填");
        appendTeamMembers(course, resolveMembers(course, members), false);
        return getPermission(courseId);
    }

    @Transactional(rollbackFor = Exception.class)
    public CoursePermissionVo removeTeamMembers(Long courseId, Collection<String> accounts) {
        Course course = requireTeamConfigure(courseId);
        if (accounts == null || accounts.isEmpty()) throw new CoursePermissionException(400, "userIds 必填（统一认证帐号）");
        for (String account : accounts) deactivateTeamMember(course.getId(), normalizeAccount(account));
        return getPermission(courseId);
    }

    /** 新增/更换计划人员时合并团队；同步失败直接回滚调用事务。 */
    @Transactional(rollbackFor = Exception.class)
    public List<CourseTeamMember> mergeTeamMembers(Long courseId, Collection<CoursePersonVo> members) {
        Course course = root(courseId, true);
        if (!canOperateCourse(course, userId(), teamMembers(course.getId(), true)))
            throw new CoursePermissionException(403, "无课程权限，不能同步课程团队成员");
        return appendTeamMembers(course, resolveMembers(course, members), true);
    }

    private static List<CoursePersonVo> requestedMembers(CourseTeamMemberSaveVo request) {
        try {
            List<CoursePersonVo> members = request == null ? null : request.resolvedMembers();
            if (members == null) throw new CoursePermissionException(400, "members 或 teamMemberIds 必填，清空请传空数组");
            return members;
        } catch (IllegalArgumentException exception) {
            throw new CoursePermissionException(400, exception.getMessage());
        }
    }

    private Course requireTeamConfigure(Long courseId) {
        Long id = userId();
        Course course = root(courseId, true);
        if (!canConfigureTeam(course, id, teamMembers(course.getId(), true)))
            throw new CoursePermissionException(403, "仅课程负责人、团队成员或课程管理员可维护团队；空名单时允许创建人初始化");
        return course;
    }

    /** 姓名取第三方提交值；省略姓名时只复用同课程已有姓名，不依赖首次单点注册。 */
    private LinkedHashMap<String, String> resolveMembers(Course course, Collection<CoursePersonVo> members) {
        Map<String, String> known = coursePeople(course);
        LinkedHashMap<String, String> resolved = new LinkedHashMap<>();
        for (CoursePersonVo person : members) {
            if (person == null) throw new CoursePermissionException(400, "人员不能为空");
            String account = normalizeAccount(person.getEmail());
            String name = person.getUserName();
            if (name == null || name.trim().isEmpty()) name = known.get(account);
            resolved.put(account, normalizePersonName(name));
        }
        return resolved;
    }

    private Map<String, String> coursePeople(Course course) {
        Map<String, String> result = new LinkedHashMap<>();
        String[] accounts = course.getAuthorIds() == null ? new String[0] : course.getAuthorIds().split(",", -1);
        String[] names = course.getAuthors() == null ? new String[0] : course.getAuthors().split(",", -1);
        for (int i = 0; i < accounts.length; i++)
            if (!accounts[i].trim().isEmpty() && i < names.length) result.put(accounts[i].trim(), names[i].trim());
        for (CourseTeamMember member : teamMembers(course.getId(), true)) result.putIfAbsent(member.getUserId(), member.getUserName());
        return result;
    }

    private List<CourseTeamMember> appendTeamMembers(Course course, LinkedHashMap<String, String> resolved, boolean skipOwners) {
        List<CourseTeamMember> added = new ArrayList<>();
        Set<String> owners = splitIds(course.getAuthorIds());
        List<CourseTeamMember> currentMembers = teamMembers(course.getId(), true);
        Set<String> current = currentMembers.stream().map(CourseTeamMember::getUserId).collect(Collectors.toSet());
        int sort = currentMembers.stream().map(CourseTeamMember::getSort).filter(Objects::nonNull).mapToInt(Integer::intValue).max().orElse(0) + 1;
        for (Map.Entry<String, String> entry : resolved.entrySet()) {
            if (skipOwners && owners.contains(entry.getKey())) continue;
            if (current.contains(entry.getKey())) continue;
            upsertTeamMemberRow(course.getId(), entry.getKey(), entry.getValue(), sort);
            CourseTeamMember member = new CourseTeamMember();
            member.setCourseId(course.getId()); member.setUserId(entry.getKey());
            member.setUserName(entry.getValue()); member.setSort(sort++);
            added.add(member);
        }
        return added;
    }

    private static List<CourseTeamMember> excludeCourseOwners(List<CourseTeamMember> members, String authorIds) {
        Set<String> owners = splitIds(authorIds);
        return members.stream().filter(member -> !owners.contains(member.getUserId())).collect(Collectors.toList());
    }

    private void upsertTeamMemberRow(Long courseId, String account, String name, int sort) {
        List<Long> found = jdbc.queryForList("SELECT id FROM " + TEAM_TABLE + " WHERE course_id=? AND user_id=? FOR UPDATE", Long.class, courseId, account);
        if (!found.isEmpty()) {
            jdbc.update("UPDATE " + TEAM_TABLE + " SET user_name=?,sort=?,sysflag=0,last_modifier=?,last_modified_time=CURRENT_TIMESTAMP WHERE course_id=? AND user_id=?",
                    name, sort, username(), courseId, account);
        } else {
            jdbc.update("INSERT INTO " + TEAM_TABLE + "(course_id,user_id,user_name,sort,creator,last_modifier) VALUES(?,?,?,?,?,?)",
                    courseId, account, name, sort, username(), username());
        }
    }

    private void deactivateTeamMember(Long courseId, String account) {
        jdbc.update("UPDATE " + TEAM_TABLE + " SET sysflag=2,last_modifier=?,last_modified_time=CURRENT_TIMESTAMP WHERE course_id=? AND user_id=? AND sysflag=0",
                username(), courseId, account);
    }

    private List<CourseTeamMember> teamMembers(Long courseId) {
        return teamMembers(courseId, false);
    }

    private List<CourseTeamMember> teamMembers(Long courseId, boolean lock) {
        return jdbc.query("SELECT id,course_id,user_id,user_name,sort FROM " + TEAM_TABLE
                + " WHERE course_id=? AND sysflag=0 ORDER BY sort ASC, id ASC" + (lock ? " FOR UPDATE" : ""), CoursePermissionService::readTeamMember, courseId);
    }

    private static CourseTeamMember readTeamMember(ResultSet rs, int rowNum) throws SQLException {
        CourseTeamMember member = new CourseTeamMember();
        member.setId(rs.getLong("id")); member.setCourseId(rs.getLong("course_id"));
        member.setUserId(rs.getString("user_id")); member.setUserName(rs.getString("user_name")); member.setSort(rs.getInt("sort"));
        return member;
    }

    private static String joinUserIds(List<CourseTeamMember> members) {
        return members.stream().map(CourseTeamMember::getUserId).collect(Collectors.joining(","));
    }

    private static String joinUserNames(List<CourseTeamMember> members) {
        return members.stream().map(CourseTeamMember::getUserName).collect(Collectors.joining(","));
    }

    /**
     * 读接口使用：返回课程，调用课程解析到总库课程。
     * 课程不存在时返回 null（例如来源课程已删除），不抛错，便于只读回退。
     */
    public Course findRootCourse(Long courseId) {
        if (courseId == null || courseId <= 0) return null;
        try {
            return root(courseId, false);
        } catch (CoursePermissionException exception) {
            return null;
        }
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
        return hasCurRole() || hasAuthor(course.getAuthorIds(), username()) || SecurityUtils.isAdmin(id)
                || isCreator(course);
    }

    /** 负责人/团队成员维护团队；空名单时允许创建人初始化，管理员规则保留。 */
    private boolean canConfigureTeam(Course course, Long id, List<CourseTeamMember> teamMembers) {
        return hasCurRole() || SecurityUtils.isAdmin(id) || canOperateCourse(course, id, teamMembers);
    }

    /**
     * 课程内容写权限 = 课程负责人 ∪ 课程团队成员；两者均为空时维持既有兜底（创建人可操作），
     * 一旦配置了任一人员，创建人不再仅凭创建人身份维护内容或团队。
     */
    private boolean canOperateCourse(Course course, Long id, List<CourseTeamMember> teamMembers) {
        if (hasCurRole() || hasAuthor(course.getAuthorIds(), username()) || isTeamMember(teamMembers, id)) return true;
        boolean noOwners = (course.getAuthorIds() == null || course.getAuthorIds().trim().isEmpty())
                && teamMembers.isEmpty();
        return noOwners && isCreator(course);
    }

    private static boolean isTeamMember(List<CourseTeamMember> teamMembers, Long id) {
        if (id == null || id <= 0 || teamMembers == null) return false;
        String account = username();
        for (CourseTeamMember member : teamMembers) {
            if (account != null && account.equals(member.getUserId())) return true;
        }
        return false;
    }

    private static boolean isCreator(Course course) {
        String name = username();
        return name != null && !name.trim().isEmpty() && name.equals(course.getCreator());
    }

    /** Role keys come from the existing authenticated login context, never request fields. */
    private static boolean hasCurRole() {
        LoginUser user = SecurityUtils.getLoginUser();
        return user != null && user.getRoles() != null && user.getRoles().contains("cur");
    }

    public static boolean hasAuthor(String authorIds, String account) {
        return account != null && !account.trim().isEmpty() && splitIds(authorIds).contains(account);
    }

    private String[] normalizeAuthors(Course course, CourseAuthorsSaveVo request) {
        if (request.getAuthorIds().trim().isEmpty()) return new String[]{"", ""};
        String[] accounts = request.getAuthorIds().split(",", -1);
        String[] names = request.getAuthors() == null ? new String[0] : request.getAuthors().split(",", -1);
        if (request.getAuthors() != null && accounts.length != names.length)
            throw new CoursePermissionException(400, "authorIds 与 authors 数量必须一致");
        List<CoursePersonVo> people = new ArrayList<>();
        for (int i = 0; i < accounts.length; i++) people.add(new CoursePersonVo(accounts[i], i < names.length ? names[i] : null));
        LinkedHashMap<String, String> resolved = resolveMembers(course, people);
        String idsValue = String.join(",", resolved.keySet());
        String namesValue = String.join(",", resolved.values());
        if (idsValue.length() > 500 || namesValue.length() > 500) throw new CoursePermissionException(400, "执笔人名单超过字段长度限制");
        return new String[]{namesValue, idsValue};
    }

    public static String normalizeAccount(String value) {
        String account = value == null ? null : value.trim();
        if (account == null || account.isEmpty() || account.length() > 100 || !account.matches("[^\\s,，、;；]+"))
            throw new CoursePermissionException(400, "email/人员帐号必填，不允许空白或多值分隔符，最多100字符");
        return account;
    }

    public static String normalizePersonName(String value) {
        String name = value == null ? null : value.trim();
        if (name == null || name.isEmpty() || name.length() > 100 || name.contains(","))
            throw new CoursePermissionException(400, "人员姓名必填，最多100字符，不能包含英文逗号");
        return name;
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
