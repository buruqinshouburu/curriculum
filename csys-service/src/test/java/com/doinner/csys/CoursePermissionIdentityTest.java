package com.doinner.csys;

import com.doinner.common.core.context.SecurityContextHolder;
import com.doinner.csys.domain.CourseTeamMember;
import com.doinner.csys.permission.CoursePermissionException;
import com.doinner.csys.permission.CoursePermissionService;
import com.doinner.system.domain.view.LoginUser;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.nio.file.Files;
import java.nio.file.Paths;

import static com.doinner.csys.permission.CourseResource.PLAN;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/** Exercises the real permission service with supplied rows; no SQL or application startup. */
class CoursePermissionIdentityTest {
    private JdbcTemplate jdbc;
    private CoursePermissionService permissions;
    private Map<String, Object> course;
    private Map<String, Object> plan;

    @BeforeEach
    void setup() {
        SecurityContextHolder.remove();
        jdbc = mock(JdbcTemplate.class);
        permissions = new CoursePermissionService(jdbc);
        course = new HashMap<>();
        course.put("id", 42L);
        course.put("sysflag", 0);
        course.put("creator", "creator.account");
        course.put("author_ids", "owner.first, 00123,teacher.b@example.com, owner.last ,12345");
        course.put("authors", "First,Leading Zero,Email,Last,Numeric");
        plan = new HashMap<>();
        plan.put("id", 101L);
        plan.put("source_course_id", 42L);
        plan.put("sysflag", 0);
        plan.put("status", 0);
        for (String lock : Arrays.asList("", " FOR UPDATE")) {
            when(jdbc.queryForList("SELECT * FROM t_csys_course WHERE id=?" + lock, 42L))
                    .thenReturn(Collections.singletonList(course));
            when(jdbc.queryForList("SELECT * FROM t_csys_teaching_plan WHERE id=?" + lock, 101L))
                    .thenReturn(Collections.singletonList(plan));
        }
        CourseTeamMember team = new CourseTeamMember();
        team.setCourseId(42L);
        team.setUserId("team.account");
        team.setUserName("Team Display");
        when(jdbc.query(anyString(), org.mockito.ArgumentMatchers.<RowMapper<CourseTeamMember>>any(), eq(42L)))
                .thenReturn(Collections.singletonList(team));
    }

    @AfterEach
    void cleanup() {
        SecurityContextHolder.remove();
    }

    @Test
    void listPermissionsUseBatchQueriesAndKeepPracticeDeleteSeparate() {
        login(901, "team.account");
        course.put("type", "2");
        when(jdbc.queryForList(startsWith("SELECT * FROM t_csys_course WHERE sysflag=0 AND id IN"), eq(42L)))
                .thenReturn(Collections.singletonList(course));
        clearInvocations(jdbc);
        com.doinner.csys.domain.vo.CoursePermissionVo result = permissions
                .getListPermissions(Arrays.asList(42L, 42L)).get(42L);
        assertTrue(result.isCanOperate());
        assertFalse(result.isCanDelete());
        assertFalse(result.isCanConfigureAuthors());
        assertTrue(result.isCanConfigureTeamMembers());
        assertEquals(2, mockingDetails(jdbc).getInvocations().size());
    }

    @Test
    void twentySourceRowsStillUseOnlyTwoPermissionQueries() {
        login(901, "creator.account");
        java.util.List<Long> ids = new java.util.ArrayList<>();
        java.util.List<Map<String, Object>> rows = new java.util.ArrayList<>();
        for (long id = 1; id <= 20; id++) {
            ids.add(id);
            Map<String, Object> row = new HashMap<>(course);
            row.put("id", id);
            rows.add(row);
        }
        when(jdbc.queryForList("SELECT * FROM t_csys_course WHERE sysflag=0 AND id IN ("
                + String.join(",", Collections.nCopies(20, "?")) + ")", ids.toArray())).thenReturn(rows);
        clearInvocations(jdbc);
        Map<Long, com.doinner.csys.domain.vo.CoursePermissionVo> result = permissions.getListPermissions(ids);
        assertEquals(20, result.size());
        assertTrue(result.values().stream().allMatch(com.doinner.csys.domain.vo.CoursePermissionVo::isCanOperate));
        assertEquals(2, mockingDetails(jdbc).getInvocations().size());
    }

    @Test
    void listPermissionsNeverGrantInvokedCourseRightsToItsSource() {
        login(901, "stranger");
        Map<String, Object> invoked = new HashMap<>(course);
        invoked.put("id", 84L);
        invoked.put("source_id", 42L);
        when(jdbc.queryForList(startsWith("SELECT * FROM t_csys_course WHERE sysflag=0 AND id IN"), eq(84L)))
                .thenReturn(Collections.singletonList(invoked));
        when(jdbc.queryForList(startsWith("SELECT * FROM t_csys_course WHERE sysflag=0 AND id IN"), eq(42L)))
                .thenReturn(Collections.singletonList(course));
        com.doinner.csys.domain.vo.CoursePermissionVo result = permissions
                .getListPermissions(Collections.singletonList(84L)).get(84L);
        assertEquals(42L, result.getPermissionCourseId());
        assertFalse(result.isCanOperate());
        course.put("source_id", 84L);
        assertFalse(permissions.getListPermissions(Collections.singletonList(84L)).get(84L).isCanOperate());
        assertFalse(permissions.getListPermissions(Collections.singletonList(999L)).get(999L).isCanOperate());
    }

    @ParameterizedTest
    @CsvSource({
            "901,creator.account,true",
            "901,owner.first,true",
            "901,00123,true",
            "901,teacher.b@example.com,true",
            "901,owner.last,true",
            "901,12345,true",
            "901,team.account,true",
            "12345,stranger,false",
            "901,123,false",
            "901,owner,false",
            "901,OWNER.LAST,false",
            "901,First,false",
            "901,Team Display,false",
            "1,admin,true"
    })
    void capabilitiesAndPlanWritesUseAnyExactAccount(long userId, String account, boolean allowed) {
        login(userId, account);
        assertEquals(allowed, permissions.getPermission(42L).isCanOperate());
        if (allowed) {
            assertDoesNotThrow(() -> permissions.check(PLAN, 101L, null, true));
        } else {
            assertStatus(403, () -> permissions.check(PLAN, 101L, null, true));
        }
    }

    @Test
    void aLeadingZeroAccountIsNotItsNumericValue() {
        course.put("author_ids", "00123");
        login(901, "123");
        assertFalse(permissions.getPermission(42L).isCanOperate());
        assertStatus(403, () -> permissions.check(PLAN, 101L, null, true));
        login(901, "00123");
        assertDoesNotThrow(() -> permissions.check(PLAN, 101L, null, true));
    }

    @Test
    void loginUserIdentityIsUsedWhenSeparateContextFieldsAreMissing() {
        LoginUser user = new LoginUser();
        user.setUserid(901L);
        user.setUsername("owner.last");
        SecurityContextHolder.set("login_user", user);
        assertTrue(permissions.getPermission(42L).isCanOperate());
        assertDoesNotThrow(() -> permissions.check(PLAN, 101L, null, true));
    }

    @Test
    void anAccountWithoutAnAuthenticatedUserIdRemainsDenied() {
        SecurityContextHolder.setUserName("owner.last");
        clearInvocations(jdbc);
        assertStatus(401, () -> permissions.getPermission(42L));
        assertStatus(401, () -> permissions.check(PLAN, 101L, null, true));
        verifyNoInteractions(jdbc);
    }

    @Test
    void everyOwnerStillHonorsThePlanStatusLock() {
        login(901, "owner.last");
        for (int status : Arrays.asList(1, 2)) {
            plan.put("status", status);
            assertStatus(409, () -> permissions.check(PLAN, 101L, null, true));
        }
    }

    @ParameterizedTest
    @CsvSource({
            "901,creator.account,true",
            "901,owner.first,true",
            "901,team.account,false",
            "901,stranger,false",
            "1,admin,true"
    })
    void practiceDeletionRequiresCreatorOwnerOrSystemAdmin(long id, String account, boolean allowed) {
        login(id, account);
        for (String type : Arrays.asList("2", "4")) {
            course.put("type", type);
            if (allowed) assertDoesNotThrow(() -> permissions.checkSourceDelete(Collections.singletonList(42L)));
            else assertStatus(403, () -> permissions.checkSourceDelete(Collections.singletonList(42L)));
        }
    }

    @Test
    void ordinaryCourseDeletionStillAllowsTeamMember() {
        course.put("type", "1");
        login(901, "team.account");
        assertDoesNotThrow(() -> permissions.checkSourceDelete(Collections.singletonList(42L)));
    }

    @ParameterizedTest
    @CsvSource({"admin", "cur"})
    void administratorRolesCanManageCoursesAndPracticeDeletion(String role) {
        login(901, "administrator");
        LoginUser user = new LoginUser();
        user.setUserid(901L);
        user.setUsername("administrator");
        user.setRoles(Collections.singleton(role));
        SecurityContextHolder.set("login_user", user);
        assertTrue(permissions.getPermission(42L).isCanOperate());
        assertDoesNotThrow(() -> permissions.check(PLAN, 101L, null, true));
        for (String type : Arrays.asList("1", "2", "4")) {
            course.put("type", type);
            assertDoesNotThrow(() -> permissions.checkSourceDelete(Collections.singletonList(42L)));
        }
    }

    @Test
    void bothSourceDeletionEndpointsEnforcePracticeDeletePermission() throws Exception {
        for (String file : Arrays.asList("CurriculumServiceImpl.java", "CourseServiceImpl.java")) {
            String source = Files.readString(Paths.get("csys-service/src/main/java/com/doinner/csys/service/impl", file));
            assertTrue(source.matches("(?s).*@CoursePermission\\(value = \"#p0\", sourceCoursesOnly = true, sourceDelete = true\\)\\s+public (?:int deleteCourseByIds|void removeStorageCourse)\\(.*"), file);
        }
    }

    @Test
    void invokedCourseIdentityCannotAuthorizeSourcePlanWrites() {
        Map<String, Object> invoked = new HashMap<>();
        invoked.put("id", 200L);
        invoked.put("source_id", 42L);
        invoked.put("sysflag", 0);
        invoked.put("creator", "invoked.creator");
        invoked.put("author_ids", "invoked.owner");
        when(jdbc.queryForList("SELECT * FROM t_csys_course WHERE id=?", 200L))
                .thenReturn(Collections.singletonList(invoked));
        plan.put("source_course_id", 200L);
        login(901, "invoked.owner");
        assertStatus(403, () -> permissions.check(PLAN, 101L, null, true));
        login(901, "owner.last");
        assertDoesNotThrow(() -> permissions.check(PLAN, 101L, null, true));
    }

    private static void login(long id, String account) {
        SecurityContextHolder.remove();
        SecurityContextHolder.setUserId(String.valueOf(id));
        SecurityContextHolder.setUserName(account);
    }

    private static void assertStatus(int expected, Runnable operation) {
        assertEquals(expected, assertThrows(CoursePermissionException.class, operation::run).getStatus());
    }
}
