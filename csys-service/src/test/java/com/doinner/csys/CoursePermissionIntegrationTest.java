package com.doinner.csys;

import com.doinner.common.core.context.SecurityContextHolder;
import com.doinner.common.core.domain.DataSet;
import com.doinner.csys.domain.Course;
import com.doinner.csys.domain.TeachingPlanContent;
import com.doinner.csys.domain.TeachingPlan;
import com.doinner.csys.domain.TeachingPlanTeacher;
import com.doinner.csys.domain.vo.*;
import com.doinner.csys.permission.*;
import com.doinner.csys.controller.CurriculumController;
import com.doinner.csys.dao.*;
import com.doinner.csys.entity.csys.po.CourseKnowledgeUnit;
import com.doinner.csys.entity.csys.po.CourseKnowledgePoint;
import com.doinner.csys.service.IKnowledgeUnitService;
import com.doinner.csys.service.TeachingPlanService;
import com.doinner.csys.service.impl.KnowledgeUnitServiceImpl;
import com.doinner.csys.service.impl.TeachingPlanServiceImpl;
import com.doinner.csys.service.impl.CourseServiceImpl;
import com.doinner.csys.service.impl.CurriculumServiceImpl;
import com.doinner.common.core.utils.poi.ExcelUtil;
import com.doinner.system.service.DoinnerDeptService;
import com.doinner.system.service.DoinnerDictDataService;
import com.doinner.system.domain.entity.SysUser;
import com.doinner.system.domain.view.LoginUser;
import com.doinner.system.service.DoinnerUserService;
import org.junit.jupiter.api.*;
import org.springframework.context.annotation.*;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.aop.aspectj.annotation.AspectJProxyFactory;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.mock.web.MockHttpServletResponse;

import javax.sql.DataSource;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

import static com.doinner.csys.permission.CourseResource.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/** Real Spring proxies, SQL and transactions; no application bootstrap or production data. */
class CoursePermissionIntegrationTest {
    private AnnotationConfigApplicationContext context;
    private JdbcTemplate jdbc;
    private CoursePermissionService permissions;
    private Writer writer;

    @BeforeEach
    void setup() {
        context = new AnnotationConfigApplicationContext(Config.class);
        jdbc = context.getBean(JdbcTemplate.class);
        permissions = context.getBean(CoursePermissionService.class);
        writer = context.getBean(Writer.class);
        jdbc.execute("CREATE TABLE t_csys_course(id BIGINT PRIMARY KEY,name VARCHAR(100),source_id BIGINT,authors VARCHAR(500),author_ids VARCHAR(500),creator VARCHAR(64),create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,last_modifier VARCHAR(64),last_modified_time TIMESTAMP,sysflag INT DEFAULT 0)");
        jdbc.update("INSERT INTO t_csys_course(id,name,authors,author_ids,creator) VALUES(1,'source','writer','11','creator'),(2,'other','other','22','otherCreator'),(4,'empty',NULL,NULL,'creator')");
        jdbc.update("INSERT INTO t_csys_course(id,name,source_id,author_ids,creator) VALUES(3,'invoked',1,'22','copier')");
        jdbc.execute("CREATE TABLE t_csys_teaching_plan(id BIGINT PRIMARY KEY,source_course_id BIGINT,status INT DEFAULT 0,current_flag INT DEFAULT 1,root_plan_id BIGINT,creator VARCHAR(64),sysflag INT DEFAULT 0)");
        jdbc.update("INSERT INTO t_csys_teaching_plan(id,source_course_id,status) VALUES(101,1,0),(202,2,0),(303,1,2)");
        jdbc.execute("CREATE TABLE t_csys_teaching_plan_content(id BIGINT PRIMARY KEY,plan_id BIGINT,content VARCHAR(100),creator VARCHAR(64),sysflag INT DEFAULT 0)");
        jdbc.update("INSERT INTO t_csys_teaching_plan_content VALUES(1001,101,'original','writer',0),(2002,202,'other','other',0),(3003,303,'approved','writer',0)");
        jdbc.execute("CREATE TABLE t_csys_course_ref_knowledge_unit(course_id BIGINT,course_unit_id BIGINT)");
        jdbc.execute("CREATE TABLE t_csys_course_knowledge_unit(id BIGINT PRIMARY KEY,name VARCHAR(100))");
        jdbc.execute("CREATE TABLE t_csys_course_knowledge_point(id BIGINT PRIMARY KEY,unit_id BIGINT,name VARCHAR(100))");
        jdbc.update("INSERT INTO t_csys_course_knowledge_unit VALUES(10,'shared'),(20,'new')");
        jdbc.update("INSERT INTO t_csys_course_knowledge_point VALUES(100,10,'shared point'),(200,20,'new point')");
        jdbc.update("INSERT INTO t_csys_course_ref_knowledge_unit VALUES(1,10),(3,10)");
        jdbc.execute("CREATE TABLE t_csys_teaching_plan_target_design(id BIGINT PRIMARY KEY,knowledge_unit_id BIGINT,knowledge_point_id BIGINT,knowledge_points VARCHAR(2000),sysflag INT DEFAULT 0)");
        jdbc.execute("CREATE TABLE t_csys_course_team_member(id BIGINT AUTO_INCREMENT PRIMARY KEY,course_id BIGINT,user_id VARCHAR(100),user_name VARCHAR(255),sort INT,creator VARCHAR(64),last_modifier VARCHAR(64),last_modified_time TIMESTAMP,create_time TIMESTAMP,sysflag INT DEFAULT 0,UNIQUE(course_id,user_id))");
        jdbc.execute("CREATE TABLE t_csys_teaching_plan_teacher(id BIGINT PRIMARY KEY,plan_id BIGINT,teacher_id VARCHAR(100),teacher_name VARCHAR(100),professional_title VARCHAR(100),duty VARCHAR(100),lecture_content VARCHAR(2000),sort INT,creator VARCHAR(64),sysflag INT DEFAULT 0)");
        AtomicLong keys = new AtomicLong(1000);
        CourseKnowledgeUnitMapper units = context.getBean(CourseKnowledgeUnitMapper.class);
        when(units.selectByCourseId(anyLong())).thenAnswer(call -> jdbc.query("SELECT u.id,u.name FROM t_csys_course_knowledge_unit u JOIN t_csys_course_ref_knowledge_unit r ON r.course_unit_id=u.id WHERE r.course_id=?", (row, index) -> {
            CourseKnowledgeUnit unit = new CourseKnowledgeUnit(); unit.setId(row.getLong(1)); unit.setName(row.getString(2)); unit.setSort(1); return unit;
        }, (Long) call.getArgument(0)));
        when(units.insert(any(CourseKnowledgeUnit.class))).thenAnswer(call -> {
            CourseKnowledgeUnit unit = call.getArgument(0); unit.setId(keys.incrementAndGet());
            return jdbc.update("INSERT INTO t_csys_course_knowledge_unit VALUES(?,?)", unit.getId(), unit.getName());
        });
        when(units.deleteBatchIds(anyList())).thenAnswer(call -> {
            int count = 0; for (Long id : (List<Long>) call.getArgument(0)) count += jdbc.update("DELETE FROM t_csys_course_knowledge_unit WHERE id=?", id); return count;
        });
        CourseKnowledgePointMapper points = context.getBean(CourseKnowledgePointMapper.class);
        doAnswer(call -> { for (Long id : (List<Long>) call.getArgument(0)) jdbc.update("DELETE FROM t_csys_course_knowledge_point WHERE unit_id=?", id); return null; }).when(points).deleteByUnitIds(anyList());
        doAnswer(call -> {
            for (CourseKnowledgePoint point : (List<CourseKnowledgePoint>) call.getArgument(0)) jdbc.update("INSERT INTO t_csys_course_knowledge_point VALUES(?,?,?)", keys.incrementAndGet(), point.getUnitId(), point.getName());
            return null;
        }).when(points).insertBatch(anyList());
        CourseRefKnowledgeUnitMapper refs = context.getBean(CourseRefKnowledgeUnitMapper.class);
        when(refs.deleteByCourseIds(anyList())).thenAnswer(call -> { int count = 0; for (Long id : (List<Long>) call.getArgument(0)) count += jdbc.update("DELETE FROM t_csys_course_ref_knowledge_unit WHERE course_id=?", id); return count; });
        when(refs.insert(any(com.doinner.csys.entity.csys.po.CourseRefKnowledgeUnit.class))).thenAnswer(call -> {
            com.doinner.csys.entity.csys.po.CourseRefKnowledgeUnit ref = call.getArgument(0);
            return jdbc.update("INSERT INTO t_csys_course_ref_knowledge_unit VALUES(?,?)", ref.getCourseId(), ref.getCourseUnitId());
        });
        DoinnerUserService users = context.getBean(DoinnerUserService.class);
        when(users.getUserById(anyLong())).thenAnswer(call -> {
            SysUser user = new SysUser();
            Long id = call.getArgument(0);
            user.setUserId(id); user.setUserName("user" + id); user.setNickName("name" + id);
            user.setStatus("0"); user.setDelFlag("0");
            return DataSet.success(user);
        });
        login(11, "writer");
    }

    @AfterEach
    void cleanup() { SecurityContextHolder.remove(); context.close(); }

    @Test
    void exactIdsAndNoCreatorOrAdminContentBypass() {
        assertFalse(CoursePermissionService.hasAuthor("11,22", "1"));
        assertTrue(CoursePermissionService.hasAuthor(" 11,22 ", "11"));
        login(1, "admin");
        assertStatus(403, () -> writer.editCourse(1L));
        login(5, "creator");
        assertStatus(403, () -> writer.editCourse(1L));
        assertTrue(permissions.getPermission(1L).isCanConfigureAuthors());
        assertFalse(permissions.getPermission(1L).isCanOperate());
        login(11, "writer");
        writer.editCourse(1L);
        assertEquals("modified", name(1));
    }

    @Test
    void curRoleCanEditAllCoursesKnowledgePlansAndConfigureAuthors() throws Exception {
        loginRoles(77, "courseAdministrator", "ordinary", "cur");
        assertTrue(permissions.getPermission(1L).isCanOperate());
        assertTrue(permissions.getPermission(4L).isCanOperate());
        assertTrue(permissions.getPermission(1L).isCanConfigureAuthors());
        permissions.requireCourseWrite(1L);
        writer.editCourse(1L);
        writer.editCourse(2L);
        writer.editCourse(4L);
        assertEquals("modified", name(2));
        TeachingPlanContent content = new TeachingPlanContent(); content.setId(2002L); content.setContent("cur changed");
        writer.updateContent(content);
        assertEquals("cur changed", jdbc.queryForObject("SELECT content FROM t_csys_teaching_plan_content WHERE id=2002", String.class));
        CourseKnowledgeVo knowledge = new CourseKnowledgeVo(); knowledge.setCourseId(1L); knowledge.setUnitList(Collections.emptyList());
        context.getBean(IKnowledgeUnitService.class).saveBatch(knowledge);
        assertEquals(0, jdbc.queryForObject("SELECT COUNT(*) FROM t_csys_course_ref_knowledge_unit", Integer.class));
        CurriculumController controller = new CurriculumController();
        ReflectionTestUtils.setField(controller, "coursePermissions", permissions);
        MockMvc mvc = MockMvcBuilders.standaloneSetup(controller).setControllerAdvice(new CoursePermissionExceptionHandler()).build();
        mvc.perform(get("/curriculum/course/1/permission"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.data.canOperate").value(true))
                .andExpect(jsonPath("$.data.canConfigureAuthors").value(true));
        mvc.perform(put("/curriculum/course/1/authors").contentType("application/json").content("{\"authorIds\":\"\"}"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.data.canOperate").value(true));
        assertEquals("", jdbc.queryForObject("SELECT author_ids FROM t_csys_course WHERE id=1", String.class));
        writer.deleteCourses(Arrays.asList(2L, 4L));
        assertEquals(2, jdbc.queryForObject("SELECT sysflag FROM t_csys_course WHERE id=2", Integer.class));
        // Removing the role restores normal author checks even for the same account.
        loginRoles(77, "courseAdministrator", "ordinary");
        assertFalse(permissions.getPermission(1L).isCanOperate());
        assertStatus(403, () -> writer.editCourse(1L));
    }

    @Test
    void onlyExactAuthenticatedCurRoleGrantsCourseAccess() {
        login(77, "cur");
        assertStatus(403, () -> writer.editCourse(1L));
        loginRoles(77, "ordinary", "curator", "CUR");
        assertStatus(403, () -> writer.editCourse(1L));
        LoginUser loginUser = com.doinner.common.security.utils.SecurityUtils.getLoginUser();
        loginUser.setPermissions(Collections.singleton("cur"));
        assertStatus(403, () -> writer.editCourse(1L));
        loginUser.setRoles(null);
        assertStatus(403, () -> writer.editCourse(1L));
        SecurityContextHolder.remove();
        LoginUser unauthenticated = new LoginUser(); unauthenticated.setRoles(Collections.singleton("cur"));
        SecurityContextHolder.set("login_user", unauthenticated);
        assertStatus(401, () -> writer.editCourse(1L));
    }

    @Test
    void curRoleKeepsPlanStatusOwnershipAndTransactionRules() {
        loginRoles(77, "courseAdministrator", "cur");
        TeachingPlanContent content = new TeachingPlanContent(); content.setId(3003L);
        assertStatus(409, () -> writer.updateContent(content));
        content.setId(2002L); content.setPlanId(101L);
        assertStatus(400, () -> writer.updateContent(content));
        assertEquals("other", jdbc.queryForObject("SELECT content FROM t_csys_teaching_plan_content WHERE id=2002", String.class));
        assertStatus(400, () -> writer.replaceKnowledge(3L));
        assertStatus(404, () -> writer.editCourse(999L));
        assertThrows(IllegalStateException.class, () -> writer.writeThenFail(1L));
        assertEquals("source", name(1));
    }

    @Test
    void emptyAuthorCourseIsEditableOnlyByItsCreatorOrCur() throws Exception {
        CurriculumServiceImpl service = curriculum(permissions);
        CourseMapper courses = (CourseMapper) ReflectionTestUtils.getField(service, "courseMapper");
        CourseVo input = new CourseVo(); input.setId(4L); input.setName("edited empty course");
        when(courses.selectCourseById(4L)).thenReturn(input);
        login(5, "creator");
        for (String authors : Arrays.asList(null, "", "  ")) {
            jdbc.update("UPDATE t_csys_course SET author_ids=? WHERE id=4", authors);
            assertTrue(permissions.getPermission(4L).isCanOperate());
            writer.editCourse(4L);
            service.updateCourse(input);
            service.saveCourse(input);
        }
        verify(courses, times(6)).updateCourse(input);
        jdbc.update("INSERT INTO t_csys_teaching_plan(id,source_course_id,status) VALUES(404,4,0)");
        jdbc.update("INSERT INTO t_csys_teaching_plan_content VALUES(4004,404,'new','creator',0)");
        TeachingPlanContent content = new TeachingPlanContent(); content.setId(4004L); content.setContent("creator edited");
        writer.updateContent(content);
        assertEquals("creator edited", jdbc.queryForObject("SELECT content FROM t_csys_teaching_plan_content WHERE id=4004", String.class));
        CourseKnowledgeVo knowledge = new CourseKnowledgeVo(); knowledge.setCourseId(4L); knowledge.setUnitList(Collections.emptyList());
        context.getBean(IKnowledgeUnitService.class).saveBatch(knowledge);
        CurriculumController controller = new CurriculumController();
        ReflectionTestUtils.setField(controller, "coursePermissions", permissions);
        MockMvc mvc = MockMvcBuilders.standaloneSetup(controller).setControllerAdvice(new CoursePermissionExceptionHandler()).build();
        mvc.perform(get("/curriculum/course/4/permission"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.data.canOperate").value(true));
        login(77, "stranger");
        assertFalse(permissions.getPermission(4L).isCanOperate());
        assertStatus(403, () -> service.updateCourse(input));
        assertStatus(403, () -> writer.updateContent(content));
        assertStatus(403, () -> context.getBean(IKnowledgeUnitService.class).saveBatch(knowledge));
        login(1, "admin");
        assertStatus(403, () -> writer.editCourse(4L));
        loginRoles(77, "courseAdministrator", "cur");
        writer.editCourse(4L);
    }

    @Test
    void assigningAuthorsRemovesCreatorFallbackAndClearingRestoresIt() {
        login(5, "creator");
        assertTrue(permissions.getPermission(4L).isCanOperate());
        CourseAuthorsSaveVo input = new CourseAuthorsSaveVo(); setAuthorAccounts(input, "22");
        assertFalse(permissions.saveAuthors(4L, input).isCanOperate());
        assertStatus(403, () -> writer.editCourse(4L));
        login(22, "writer22"); writer.editCourse(4L);
        login(5, "creator"); setAuthorAccounts(input, "");
        assertTrue(permissions.saveAuthors(4L, input).isCanOperate());
        writer.editCourse(4L);
        login(22, "writer22");
        assertStatus(403, () -> writer.editCourse(4L));
    }

    @Test
    void missingCreatorOrInvalidAuthorIdsDoNotGrantCreatorFallback() {
        login(5, "creator");
        jdbc.update("UPDATE t_csys_course SET creator=NULL WHERE id=4");
        assertStatus(403, () -> writer.editCourse(4L));
        jdbc.update("UPDATE t_csys_course SET creator='' WHERE id=4");
        login(5, "");
        assertStatus(403, () -> writer.editCourse(4L));
        login(5, "creator");
        jdbc.update("UPDATE t_csys_course SET creator='creator',author_ids='invalid' WHERE id=4");
        assertStatus(403, () -> writer.editCourse(4L));
        jdbc.update("UPDATE t_csys_course SET author_ids=NULL WHERE id=4");
        login(77, "stranger");
        CourseVo forged = new CourseVo(); forged.setId(4L); forged.setCreator("creator");
        assertStatus(403, () -> curriculum(permissions).updateCourse(forged));
    }

    @Test
    void invokedCourseOperationsDoNotRequireSourceAuthors() {
        assertTrue(permissions.getPermission(3L).isCanOperate());
        writer.editCourse(3L);
        login(22, "copier");
        assertTrue(permissions.getPermission(3L).isCanOperate());
        assertFalse(permissions.getPermission(3L).isCanConfigureAuthors());
        writer.editCourse(3L);
        assertStatus(403, () -> writer.editCourse(1L));
        assertStatus(403, () -> writer.deleteCourses(Arrays.asList(3L, 1L)));
        assertEquals(0, jdbc.queryForObject("SELECT sysflag FROM t_csys_course WHERE id=3", Integer.class));
        writer.deleteCourses(Collections.singletonList(3L));
        assertEquals(2, jdbc.queryForObject("SELECT sysflag FROM t_csys_course WHERE id=3", Integer.class));
    }

    @Test
    void schemeBindingAndActualInvokedDeletionAreOutsideAuthorControl() throws Exception {
        login(77, "schemeEditor");
        CourseServiceImpl target = new CourseServiceImpl();
        for (java.lang.reflect.Field field : CourseServiceImpl.class.getDeclaredFields()) {
            if (field.getType().isInterface()) ReflectionTestUtils.setField(target, field.getName(), mock(field.getType()));
        }
        CourseMapper courses = (CourseMapper) ReflectionTestUtils.getField(target, "courseMapper");
        ReflectionTestUtils.setField(target, "coursePermissions", permissions);
        Course invoked = new Course(); invoked.setId(3L); invoked.setSourceId(1L);
        when(courses.selectCoursesByIds(Collections.singletonList(3L))).thenReturn(Collections.singletonList(invoked));
        AspectJProxyFactory factory = new AspectJProxyFactory(target);
        factory.setProxyTargetClass(true); factory.addAspect(context.getBean(CoursePermissionAspect.class));
        CourseServiceImpl service = factory.getProxy();
        CourseBoundGraduationVo binding = new CourseBoundGraduationVo(); binding.setCourseId(3L); binding.setGraduationIds(Collections.emptyList());
        assertEquals(200, service.boundGraduation(binding).getCode());
        service.removeInvokeCourse(Collections.singletonList(3L));
        verify(courses).removeCourseByIds(Collections.singletonList(3L));
        assertStatus(403, () -> service.removeStorageCourse(Collections.singletonList(1L)));
    }

    @Test
    void creatorsConfigureValidatedNamesAndEditOnlyWhenAuthorsAreEmpty() {
        login(5, "creator");
        CourseAuthorsSaveVo input = new CourseAuthorsSaveVo(); setAuthorAccounts(input, "22, 22,11");
        CoursePermissionVo result = permissions.saveAuthors(1L, input);
        assertEquals("22,11", result.getAuthorIds());
        assertEquals("name22,name11", result.getAuthors());
        assertEquals("22,11", jdbc.queryForObject("SELECT author_ids FROM t_csys_course WHERE id=3", String.class));
        assertFalse(result.isCanOperate());
        setAuthorAccounts(input, ""); assertTrue(permissions.saveAuthors(1L, input).isCanOperate());
        writer.editCourse(1L);
        login(11, "writer"); assertStatus(403, () -> writer.editCourse(1L));
        login(5, "creator");
        setAuthorAccounts(input, "11"); permissions.saveAuthors(1L, input);
        setAuthorAccounts(input, null); assertStatus(400, () -> permissions.saveAuthors(1L, input));
        setAuthorAccounts(input, "11,,22"); assertStatus(400, () -> permissions.saveAuthors(1L, input));
        login(22, "stranger"); assertStatus(403, () -> permissions.saveAuthors(1L, input));
    }

    @Test
    void suppliedPlanCannotHideForeignChildAndOwnerCannotMove() {
        TeachingPlanContent content = new TeachingPlanContent();
        content.setId(2002L); content.setPlanId(101L); content.setContent("attack");
        assertStatus(403, () -> writer.updateContent(content));
        jdbc.update("UPDATE t_csys_course SET author_ids='11' WHERE id=2");
        assertStatus(400, () -> writer.updateContent(content));
        assertEquals("other", jdbc.queryForObject("SELECT content FROM t_csys_teaching_plan_content WHERE id=2002", String.class));
        content.setId(1001L); content.setPlanId(null); writer.updateContent(content);
        assertEquals(101L, content.getPlanId());
    }

    @Test
    void approvedPlanIsReadOnlyEvenForAuthor() {
        TeachingPlanContent content = new TeachingPlanContent(); content.setId(3003L);
        assertStatus(409, () -> writer.updateContent(content));
    }

    @Test
    void wholeBatchIsCheckedBeforeWritingAndFailuresRollBack() {
        assertStatus(403, () -> writer.deleteCourses(Arrays.asList(1L, 2L)));
        assertEquals(0, jdbc.queryForObject("SELECT sysflag FROM t_csys_course WHERE id=1", Integer.class));
        assertStatus(404, () -> writer.deleteCourses(Arrays.asList(1L, 999L)));
        assertThrows(IllegalStateException.class, () -> writer.writeThenFail(1L));
        assertEquals("source", name(1));
    }

    @Test
    void ordinaryContentCannotReassignAuthorsOrCreator() {
        Course course = new Course(); course.setId(1L); setAuthorAccounts(course, "11,22");
        assertStatus(400, () -> permissions.preserveCourseOwners(course));
        setAuthorAccounts(course, null); course.setCreator("forged");
        assertStatus(400, () -> permissions.preserveCourseOwners(course));
    }

    @Test
    void actualCourseCreationNeedsNeitherAuthorsNorOptionalPlansAndAllowsSameName() throws Exception {
        login(77, "creator77");
        CoursePermissionService permissionSpy = mock(CoursePermissionService.class);
        CurriculumServiceImpl service = curriculum(permissionSpy);
        CourseMapper courses = (CourseMapper) ReflectionTestUtils.getField(service, "courseMapper");
        AtomicLong keys = new AtomicLong(100);
        when(courses.insertCourse(any(Course.class))).thenAnswer(call -> {
            Course course = call.getArgument(0); course.setId(keys.incrementAndGet()); return 1;
        });
        when(courses.selectByNameCount(any(CourseVo.class))).thenReturn(1L);
        for (String type : Arrays.asList("1", "2", "3", "4")) {
            CourseVo input = new CourseVo(); input.setName("source"); input.setType(type);
            input.setCreator("clientCreator");
            assertSame(input, service.insertCourse(input));
            assertNotNull(input.getId());
            assertEquals("creator77", input.getCreator());
            assertNull(input.getAuthorIds());
            assertEquals(!"2".equals(type), input.getCourseTeachingTheoryPlanVo() != null);
            assertEquals(!"1".equals(type), input.getCourseTeachingPracticePlanVo() != null);
        }
        CourseVo input = new CourseVo(); input.setName("source"); input.setType("1");
        input.setAuthors("existing display"); setAuthorAccounts(input, "77");
        service.insertCourse(input);
        assertEquals("existing display", input.getAuthors());
        verifyNoInteractions(permissionSpy);
        verify(courses, never()).selectByNameCount(any(CourseVo.class));
        verify(context.getBean(DoinnerUserService.class), never()).getUserById(anyLong());
        input.setId(1L);
        assertStatus(400, () -> service.insertCourse(input));
        verify(courses, never()).updateCourse(any(Course.class));
    }

    @Test
    void actualSourceCourseUpdatesAllowMissingOptionalPlansButStillCheckAuthorsAndOwnership() throws Exception {
        CurriculumServiceImpl service = curriculum(permissions);
        CourseMapper courses = (CourseMapper) ReflectionTestUtils.getField(service, "courseMapper");
        CourseVo input = new CourseVo(); input.setId(1L); input.setName("other");
        when(courses.selectCourseById(1L)).thenReturn(input);
        when(courses.selectByNameCount(any(CourseVo.class))).thenReturn(1L);
        service.updateCourse(input);
        service.saveCourse(input);
        verify(courses, times(2)).updateCourse(input);
        verify(courses, never()).selectByNameCount(any(CourseVo.class));
        login(77, "stranger");
        assertStatus(403, () -> service.updateCourse(input));
        assertStatus(403, () -> service.saveCourse(input));
        login(11, "writer");
        jdbc.execute("CREATE TABLE t_csys_course_teaching_theory_plan(id BIGINT PRIMARY KEY,course_id BIGINT,sysflag INT DEFAULT 0)");
        jdbc.update("INSERT INTO t_csys_course_teaching_theory_plan VALUES(100,2,0)");
        CourseTeachingTheoryPlanVo theory = new CourseTeachingTheoryPlanVo(); theory.setId(100L);
        input.setCourseTeachingTheoryPlanVo(theory);
        assertStatus(400, () -> service.updateCourse(input));
        input.setCourseTeachingTheoryPlanVo(null);
        input.setCourseChapterVoList(Collections.singletonList(null));
        assertStatus(400, () -> service.updateCourse(input));
    }

    @Test
    void actualExcelAndBatchImportsAllowSameNamesWithoutPermissionChecks() throws Exception {
        login(77, "importer");
        CoursePermissionService permissionSpy = mock(CoursePermissionService.class);
        CurriculumServiceImpl service = curriculum(permissionSpy);
        CourseMapper courses = (CourseMapper) ReflectionTestUtils.getField(service, "courseMapper");
        DoinnerDeptService departments = (DoinnerDeptService) ReflectionTestUtils.getField(service, "doinnerDeptService");
        DoinnerDictDataService dictionaries = (DoinnerDictDataService) ReflectionTestUtils.getField(service, "doinnerDictDataService");
        when(departments.list(any())).thenReturn(DataSet.success(Collections.emptyList()));
        when(dictionaries.dictType(anyString())).thenReturn(DataSet.success(Collections.emptyList()));
        when(courses.selectByNameCount(any(CourseVo.class))).thenReturn(1L);
        CourseExcelVo course = new CourseExcelVo(); course.setName("source");
        assertEquals(200, service.importCourse(excel(CourseExcelVo.class, course), null, null, null, "1", 1, "2026", null, null).getCode());
        TrainingExcelVo training = new TrainingExcelVo(); training.setName("source");
        assertEquals(200, service.importTraining(excel(TrainingExcelVo.class, training), null, "2", 1, "2026").getCode());
        PracticeExcelVo practice = new PracticeExcelVo(); practice.setName("source");
        assertEquals(200, service.importPractice(excel(PracticeExcelVo.class, practice), null, "4", 1, "2026", null, null).getCode());
        Course imported = new Course(); imported.setName("source"); imported.setCode("unique-code"); imported.setType("1");
        assertEquals(1, service.insertCourse(Collections.singletonList(imported)));
        verify(courses, times(6)).insertCourse(any(Course.class));
        verify(courses).insertCourses(Collections.singletonList(imported));
        verify(courses, never()).selectByNameCount(any(CourseVo.class));
        verifyNoInteractions(permissionSpy);
        when(courses.selectCodeByCodes(Collections.singletonList("unique-code"))).thenReturn(Collections.singletonList("unique-code"));
        assertThrows(com.doinner.common.core.exception.DataFormatException.class,
                () -> service.insertCourse(Collections.singletonList(imported)));
    }

    private CurriculumServiceImpl curriculum(CoursePermissionService permissionService) {
        CurriculumServiceImpl target = new CurriculumServiceImpl();
        for (java.lang.reflect.Field field : CurriculumServiceImpl.class.getDeclaredFields()) {
            if (field.getType().isInterface()) ReflectionTestUtils.setField(target, field.getName(), mock(field.getType()));
        }
        ReflectionTestUtils.setField(target, "coursePermissions", permissionService);
        AspectJProxyFactory factory = new AspectJProxyFactory(target);
        factory.setProxyTargetClass(true); factory.addAspect(context.getBean(CoursePermissionAspect.class));
        return factory.getProxy();
    }

    private <T> MockMultipartFile excel(Class<T> type, T row) {
        MockHttpServletResponse response = new MockHttpServletResponse();
        new ExcelUtil<>(type).exportExcel(response, Arrays.asList(row, row), "courses");
        return new MockMultipartFile("file", "courses.xlsx", "application/octet-stream", response.getContentAsByteArray());
    }

    @Test
    void knowledgeIsSourceWritableAndInvokedReferencesFollowRebuild() {
        assertStatus(400, () -> writer.replaceKnowledge(3L));
        writer.replaceKnowledge(1L);
        assertEquals(Collections.singletonList(20L), jdbc.queryForList("SELECT course_unit_id FROM t_csys_course_ref_knowledge_unit WHERE course_id=3", Long.class));
        assertEquals(Collections.singletonList(10L), permissions.unreferencedKnowledgeUnits(Collections.singletonList(10L)));
        jdbc.update("INSERT INTO t_csys_teaching_plan_target_design VALUES(1,NULL,NULL,'[{\"knowledgePointId\":100}]',0)");
        assertTrue(permissions.unreferencedKnowledgeUnits(Collections.singletonList(10L)).isEmpty());
        jdbc.update("INSERT INTO t_csys_course_ref_knowledge_unit VALUES(2,10)");
        assertTrue(permissions.unreferencedKnowledgeUnits(Collections.singletonList(10L)).isEmpty());
    }

    @Test
    void incomingKnowledgeIdsMustBelongToSourceBeforeRebuild() {
        CourseKnowledgeVo input = new CourseKnowledgeVo(); input.setCourseId(1L);
        CourseKnowledgeUnitVo unit = new CourseKnowledgeUnitVo(); unit.setId(20L); unit.setName("foreign");
        input.setUnitList(Collections.singletonList(unit));
        assertStatus(400, () -> permissions.validateKnowledge(input));
        unit.setId(10L);
        CourseKnowledgePointVo point = new CourseKnowledgePointVo(); point.setId(200L); point.setName("foreign point");
        unit.setKnowledgePoints(Collections.singletonList(point));
        assertStatus(400, () -> permissions.validateKnowledge(input));
    }

    @Test
    void actualKnowledgeSaveRebuildsBothSourceAndInvokedLinks() {
        IKnowledgeUnitService knowledge = context.getBean(IKnowledgeUnitService.class);
        CourseKnowledgeVo input = new CourseKnowledgeVo(); input.setCourseId(3L); input.setUnitList(Collections.emptyList());
        assertStatus(400, () -> knowledge.saveBatch(input));
        input.setCourseId(1L);
        CourseKnowledgeUnitVo unit = new CourseKnowledgeUnitVo(); unit.setId(10L); unit.setName("changed shared unit");
        CourseKnowledgePointVo point = new CourseKnowledgePointVo(); point.setId(100L); point.setName("changed shared point");
        unit.setKnowledgePoints(Collections.singletonList(point)); input.setUnitList(Collections.singletonList(unit));
        knowledge.saveBatch(input);
        Long sourceUnit = jdbc.queryForObject("SELECT course_unit_id FROM t_csys_course_ref_knowledge_unit WHERE course_id=1", Long.class);
        assertNotEquals(10L, sourceUnit);
        assertEquals(sourceUnit, jdbc.queryForObject("SELECT course_unit_id FROM t_csys_course_ref_knowledge_unit WHERE course_id=3", Long.class));
        assertEquals("changed shared unit", jdbc.queryForObject("SELECT name FROM t_csys_course_knowledge_unit WHERE id=?", String.class, sourceUnit));
        assertEquals("changed shared point", jdbc.queryForObject("SELECT name FROM t_csys_course_knowledge_point WHERE unit_id=?", String.class, sourceUnit));
        input.setUnitList(Collections.emptyList()); knowledge.saveBatch(input);
        assertEquals(0, jdbc.queryForObject("SELECT COUNT(*) FROM t_csys_course_ref_knowledge_unit", Integer.class));
    }

    @Test
    void revokedAuthorCannotWriteAgainAndRevokeWaitsForActiveWrite() throws Exception {
        CountDownLatch locked = new CountDownLatch(1), release = new CountDownLatch(1), revoking = new CountDownLatch(1);
        ExecutorService pool = Executors.newFixedThreadPool(2);
        try {
            Future<?> edit = pool.submit(() -> { login(11, "writer"); try { writer.holdWrite(1L, locked, release); } finally { SecurityContextHolder.remove(); } });
            assertTrue(locked.await(5, TimeUnit.SECONDS));
            Future<?> revoke = pool.submit(() -> {
                login(5, "creator"); revoking.countDown();
                try { CourseAuthorsSaveVo input = new CourseAuthorsSaveVo(); setAuthorAccounts(input, "22"); permissions.saveAuthors(1L, input); }
                finally { SecurityContextHolder.remove(); }
            });
            assertTrue(revoking.await(5, TimeUnit.SECONDS));
            assertThrows(TimeoutException.class, () -> revoke.get(150, TimeUnit.MILLISECONDS));
            release.countDown(); edit.get(5, TimeUnit.SECONDS); revoke.get(5, TimeUnit.SECONDS);
            assertStatus(403, () -> writer.editCourse(1L));
        } finally { release.countDown(); pool.shutdownNow(); }
    }

    @Test
    void missingLoginAndEmptyBatchFailClosed() {
        SecurityContextHolder.remove(); assertStatus(401, () -> writer.editCourse(1L));
        login(11, "writer"); assertStatus(400, () -> writer.deleteCourses(Collections.emptyList()));
    }

    @Test
    void frontendEndpointsExposeAbilitiesAndUseRealHttpErrors() throws Exception {
        CurriculumController controller = new CurriculumController();
        ReflectionTestUtils.setField(controller, "coursePermissions", permissions);
        MockMvc mvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new CoursePermissionExceptionHandler()).build();
        mvc.perform(get("/curriculum/course/3/permission"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.data.permissionCourseId").value(1))
                .andExpect(jsonPath("$.data.canOperate").value(true));
        login(77, "stranger");
        mvc.perform(put("/curriculum/course/1/authors").contentType("application/json").content("{\"authorIds\":\"77\"}"))
                .andExpect(status().isForbidden()).andExpect(jsonPath("$.code").value(403));
        SecurityContextHolder.remove();
        mvc.perform(get("/curriculum/course/1/permission"))
                .andExpect(status().isUnauthorized()).andExpect(jsonPath("$.code").value(401));
    }

    @Test
    void actualWordWriterRejectsForeignPlanAndApprovedImportBeforeParsing() {
        CourseMapper courses = context.getBean(CourseMapper.class);
        CourseVo source = new CourseVo(); source.setId(1L); source.setName("source");
        when(courses.selectCourseById(1L)).thenReturn(source);
        TeachingPlanMapper plans = mock(TeachingPlanMapper.class);
        TeachingPlan foreign = new TeachingPlan(); foreign.setId(202L); foreign.setSourceCourseId(2L);
        when(plans.selectById(202L)).thenReturn(foreign);
        TeachingPlan approved = new TeachingPlan(); approved.setId(303L); approved.setSourceCourseId(1L); approved.setStatus(2);
        when(plans.selectBySourceCourseIdAndPlanType(1L, 1)).thenReturn(approved);
        TeachingPlanServiceImpl target = new TeachingPlanServiceImpl();
        ReflectionTestUtils.setField(target, "courseMapper", courses);
        ReflectionTestUtils.setField(target, "teachingPlanMapper", plans);
        ReflectionTestUtils.setField(target, "coursePermissions", permissions);
        AspectJProxyFactory factory = new AspectJProxyFactory(target);
        factory.setProxyTargetClass(true); factory.addAspect(context.getBean(CoursePermissionAspect.class));
        TeachingPlanServiceImpl service = factory.getProxy();
        assertStatus(400, () -> service.generateTeachingPlanWord(1L, 202L));
        MockMultipartFile file = new MockMultipartFile("file", "plan.docx", "application/octet-stream", new byte[]{1});
        assertStatus(409, () -> service.importWord(1L, 1, file));
        verify(plans).selectById(202L);
        verify(plans).selectBySourceCourseIdAndPlanType(1L, 1);
        verifyNoMoreInteractions(plans);
    }

    private String name(long id) { return jdbc.queryForObject("SELECT name FROM t_csys_course WHERE id=?", String.class, id); }

    // ============ 课程团队成员（需求一） ============

    @Test
    void teamMemberSharesCourseWritePermissionAndCanMaintainTheTeam() {
        jdbc.update("INSERT INTO t_csys_course_team_member(course_id,user_id,user_name,sort,sysflag) VALUES(1,33,'member33',1,0)");
        login(33, "member33");
        writer.editCourse(1L);
        assertEquals("modified", name(1));

        login(44, "outsider");
        assertStatus(403, () -> writer.editCourse(1L));

        // 团队成员可以维护本课程团队成员
        login(33, "member33");
        CourseTeamMemberSaveVo add = new CourseTeamMemberSaveVo();
        setTeamAccounts(add, Collections.singletonList(44L));
        permissions.addTeamMembers(1L, add);

        // 新增后权限即时生效，移除后即时回收
        login(44, "outsider");
        writer.editCourse(1L);
        assertEquals("modified", name(1));
        permissions.removeTeamMembers(1L, Collections.singletonList("44"));
        assertStatus(403, () -> writer.editCourse(1L));
        assertEquals(1, jdbc.queryForObject("SELECT COUNT(*) FROM t_csys_course_team_member WHERE course_id=1 AND sysflag=0", Integer.class));
    }

    @Test
    void ownersAndTeamIdentitiesStayIndependentAcrossOwnerChanges() {
        login(11, "writer");
        CourseTeamMemberSaveVo save = new CourseTeamMemberSaveVo();
        setTeamAccounts(save, Arrays.asList("11", "22", "22"));
        permissions.saveTeamMembers(1L, save);
        assertEquals(2, jdbc.queryForObject("SELECT COUNT(*) FROM t_csys_course_team_member WHERE course_id=1 AND sysflag=0", Integer.class));
        assertEquals("22", permissions.getPermission(1L).getTeamMemberIds());
        setTeamAccounts(save, Collections.singletonList("22"));
        permissions.saveTeamMembers(1L, save);
        assertEquals(1, jdbc.queryForObject("SELECT COUNT(*) FROM t_csys_course_team_member WHERE course_id=1 AND user_id='11' AND sysflag=0", Integer.class));
        CourseAuthorsSaveVo owners = new CourseAuthorsSaveVo(); setAuthorAccounts(owners, "22");
        permissions.saveAuthors(1L, owners);
        assertTrue(permissions.getPermission(1L).isCanOperate());
        permissions.removeTeamMembers(1L, Collections.singletonList("11"));
        assertFalse(permissions.getPermission(1L).isCanOperate());
        login(22, "owner22");
        permissions.removeTeamMembers(1L, Collections.singletonList("22"));
        assertTrue(permissions.getPermission(1L).isCanOperate());
    }

    @Test
    void creatorOnlyInitializesAnEmptyTeamAndCannotGrantSelfAccessAfterAssignment() {
        login(5, "creator");
        assertTrue(permissions.getPermission(4L).isCanOperate());
        CourseTeamMemberSaveVo save = new CourseTeamMemberSaveVo();
        setTeamAccounts(save, Collections.singletonList("66"));
        permissions.saveTeamMembers(4L, save);
        assertFalse(permissions.getPermission(4L).isCanOperate());
        assertFalse(permissions.getPermission(4L).isCanConfigureTeamMembers());
        assertStatus(403, () -> permissions.addTeamMembers(4L, save));
        assertStatus(403, () -> writer.editCourse(4L));
        login(66, "member66");
        writer.editCourse(4L);
        setTeamAccounts(save, Collections.emptyList());
        permissions.saveTeamMembers(4L, save);
        login(5, "creator");
        assertTrue(permissions.getPermission(4L).isCanOperate());
        assertFalse(permissions.getPermission(1L).isCanConfigureTeamMembers());
        assertStatus(403, () -> permissions.addTeamMembers(1L, save));
    }

    @Test
    void teamMemberEndpointRejectsInvalidIdsAndUnauthorizedUsers() throws Exception {
        CurriculumController controller = new CurriculumController();
        ReflectionTestUtils.setField(controller, "coursePermissions", permissions);
        MockMvc mvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new CoursePermissionExceptionHandler()).build();

        login(77, "stranger");
        mvc.perform(put("/curriculum/course/1/teamMembers").contentType("application/json")
                        .content("{\"userIds\":[88]}"))
                .andExpect(status().isForbidden());
        mvc.perform(get("/curriculum/course/1/teamMembers"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.data.length()").value(0));

        login(11, "writer");
        mvc.perform(put("/curriculum/course/1/teamMembers").contentType("application/json")
                        .content("{\"userIds\":[55,56],\"teamMemberNames\":\"name55,name56\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.teamMemberIds").value("55,56"))
                .andExpect(jsonPath("$.data.canConfigureTeamMembers").value(true));
        mvc.perform(delete("/curriculum/course/1/teamMembers").param("userIds", "55"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.teamMemberIds").value("56"));
        mvc.perform(put("/curriculum/course/1/teamMembers").contentType("application/json")
                        .content("{\"userIds\":[0]}"))
                .andExpect(status().isBadRequest());

        // 兼容课程负责人 authorIds 的入参形状：英文逗号拼接字符串 / teamMemberIds 别名
        mvc.perform(put("/curriculum/course/1/teamMembers").contentType("application/json")
                        .content("{\"userIds\":\"55,56\",\"teamMemberNames\":\"name55,name56\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.teamMemberIds").value("55,56"))
                .andExpect(jsonPath("$.data.teamMemberNames").value("name55,name56"));
        mvc.perform(post("/curriculum/course/1/teamMembers").contentType("application/json")
                        .content("{\"teamMemberIds\":\"57\",\"teamMemberNames\":\"name57\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.teamMemberIds").value("55,56,57"));
        mvc.perform(put("/curriculum/course/1/teamMembers").contentType("application/json")
                        .content("{\"userIds\":\"\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.teamMemberIds").value(""));
    }

    // ============ 教学计划教学团队（需求二） ============

    private TeachingPlanService teachingTeamService(TeachingPlanMapper plans, TeachingPlanTeacherMapper teachers) {
        TeachingPlanServiceImpl target = new TeachingPlanServiceImpl();
        ReflectionTestUtils.setField(target, "teachingPlanMapper", plans);
        ReflectionTestUtils.setField(target, "teachingPlanTeacherMapper", teachers);
        ReflectionTestUtils.setField(target, "coursePermissions", permissions);
        AspectJProxyFactory factory = new AspectJProxyFactory(target);
        factory.setProxyTargetClass(true);
        factory.addAspect(context.getBean(CoursePermissionAspect.class));
        return factory.getProxy();
    }

    private TeachingPlanMapper plan101() {
        TeachingPlan plan = new TeachingPlan();
        plan.setId(101L);
        plan.setSourceCourseId(1L);
        TeachingPlanMapper plans = mock(TeachingPlanMapper.class);
        when(plans.selectById(101L)).thenReturn(plan);
        return plans;
    }

    private TeachingPlanTeacherMapper planTeachers(AtomicLong keys) {
        TeachingPlanTeacherMapper teachers = mock(TeachingPlanTeacherMapper.class);
        when(teachers.selectByPlanId(anyLong())).thenAnswer(call -> jdbc.query(
                "SELECT id,plan_id,teacher_id,teacher_name,duty,sort,professional_title,lecture_content FROM t_csys_teaching_plan_teacher WHERE plan_id=? AND sysflag=0 ORDER BY sort,id",
                (row, index) -> {
                    TeachingPlanTeacher teacher = new TeachingPlanTeacher();
                    teacher.setId(row.getLong(1));
                    teacher.setPlanId(row.getLong(2));
                    teacher.setTeacherId(row.getString(3));
                    teacher.setTeacherName(row.getString(4));
                    teacher.setDuty(row.getString(5));
                    teacher.setSort(row.getInt(6));
                    teacher.setProfessionalTitle(row.getString(7));
                    teacher.setLectureContent(row.getString(8));
                    return teacher;
                }, (Long) call.getArgument(0)));
        when(teachers.selectById(anyLong())).thenAnswer(call -> jdbc.query(
                "SELECT id,plan_id,teacher_id,teacher_name,duty,sort,professional_title,lecture_content FROM t_csys_teaching_plan_teacher WHERE id=? AND sysflag=0",
                (row, index) -> {
                    TeachingPlanTeacher teacher = new TeachingPlanTeacher();
                    teacher.setId(row.getLong(1));
                    teacher.setPlanId(row.getLong(2));
                    teacher.setTeacherId(row.getString(3));
                    teacher.setTeacherName(row.getString(4));
                    teacher.setDuty(row.getString(5));
                    teacher.setSort(row.getInt(6));
                    teacher.setProfessionalTitle(row.getString(7));
                    teacher.setLectureContent(row.getString(8));
                    return teacher;
                }, (Long) call.getArgument(0)).stream().findFirst().orElse(null));
        when(teachers.insert(any(TeachingPlanTeacher.class))).thenAnswer(call -> {
            TeachingPlanTeacher teacher = call.getArgument(0);
            teacher.setId(keys.incrementAndGet());
            return jdbc.update("INSERT INTO t_csys_teaching_plan_teacher(id,plan_id,teacher_id,teacher_name,duty,sort,professional_title,lecture_content,sysflag) VALUES(?,?,?,?,?,?,?,?,0)",
                    teacher.getId(), teacher.getPlanId(), teacher.getTeacherId(), teacher.getTeacherName(), teacher.getDuty(), teacher.getSort(), teacher.getProfessionalTitle(), teacher.getLectureContent());
        });
        when(teachers.updateById(any(TeachingPlanTeacher.class))).thenAnswer(call -> {
            TeachingPlanTeacher teacher = call.getArgument(0);
            return jdbc.update("UPDATE t_csys_teaching_plan_teacher SET teacher_id=?,teacher_name=?,duty=COALESCE(?,duty),sort=COALESCE(?,sort),professional_title=COALESCE(?,professional_title),lecture_content=COALESCE(?,lecture_content) WHERE id=?",
                    teacher.getTeacherId(), teacher.getTeacherName(), teacher.getDuty(), teacher.getSort(), teacher.getProfessionalTitle(), teacher.getLectureContent(), teacher.getId());
        });
        doAnswer(call -> jdbc.update("UPDATE t_csys_teaching_plan_teacher SET sysflag=2 WHERE id=?", (Long) call.getArgument(0))).when(teachers).deleteById(anyLong());
        return teachers;
    }

    @Test
    void teachingTeamBatchSaveSyncsCourseTeamWithoutTouchingOwners() {
        AtomicLong keys = new AtomicLong(5000);
        TeachingPlanTeacherMapper teachers = planTeachers(keys);
        TeachingPlanService service = teachingTeamService(plan101(), teachers);

        TeachingPlanTeacher input = new TeachingPlanTeacher();
        input.setTeacherId("55");
        input.setTeacherName("name55");
        input.setDuty("主讲");
        TeachingPlanTeacherBatchSaveVo request = new TeachingPlanTeacherBatchSaveVo();
        request.setPlanId(101L);
        request.setTeachers(Collections.singletonList(input));

        // 无课程权限的用户不能通过教学团队新增获取课程权限
        login(99, "outsider");
        assertStatus(403, () -> service.saveTeachersBatch(request));

        login(11, "writer");
        List<TeachingPlanTeacher> saved = service.saveTeachersBatch(request);
        assertEquals(1, saved.size());
        assertEquals("name55", saved.get(0).getTeacherName());
        assertEquals(1, jdbc.queryForObject("SELECT COUNT(*) FROM t_csys_teaching_plan_teacher WHERE plan_id=101 AND sysflag=0", Integer.class));
        // 同步并入课程库团队成员；不改变课程负责人
        assertEquals(1, jdbc.queryForObject("SELECT COUNT(*) FROM t_csys_course_team_member WHERE course_id=1 AND user_id=55 AND sysflag=0", Integer.class));
        assertEquals("writer", jdbc.queryForObject("SELECT authors FROM t_csys_course WHERE id=1", String.class));

        // 同步成员立即获得课程内容权限
        login(55, "member55");
        writer.editCourse(1L);
        assertEquals("modified", name(1));

        // 重复保存同一人：教学计划与课程库都不重复
        login(11, "writer");
        service.saveTeachersBatch(request);
        assertEquals(1, jdbc.queryForObject("SELECT COUNT(*) FROM t_csys_teaching_plan_teacher WHERE plan_id=101 AND sysflag=0", Integer.class));
        assertEquals(1, jdbc.queryForObject("SELECT COUNT(*) FROM t_csys_course_team_member WHERE course_id=1 AND user_id=55 AND sysflag=0", Integer.class));

        // 移除教学计划成员只解除计划关联，课程库团队成员保留
        Long recordId = jdbc.queryForObject("SELECT id FROM t_csys_teaching_plan_teacher WHERE plan_id=101 AND sysflag=0", Long.class);
        service.removeTeachersBatch(101L, Collections.singletonList(recordId));
        assertEquals(0, jdbc.queryForObject("SELECT COUNT(*) FROM t_csys_teaching_plan_teacher WHERE plan_id=101 AND sysflag=0", Integer.class));
        assertEquals(1, jdbc.queryForObject("SELECT COUNT(*) FROM t_csys_course_team_member WHERE course_id=1 AND user_id=55 AND sysflag=0", Integer.class));
    }

    @Test
    void teachingTeamCandidatesCoverOwnersAndTeamMembers() {
        AtomicLong keys = new AtomicLong(6000);
        TeachingPlanTeacherMapper teachers = planTeachers(keys);
        TeachingPlanService service = teachingTeamService(plan101(), teachers);

        login(11, "writer");
        CourseTeamMemberSaveVo save = new CourseTeamMemberSaveVo();
        setTeamAccounts(save, Arrays.asList(11L, 66L));
        permissions.saveTeamMembers(1L, save);

        // 候选人：课程负责人(roleType=1) + 课程团队成员(roleType=2)，同一人只出现一次
        List<TeachingPlanTeamCandidateVo> candidates = service.listTeacherCandidates(101L);
        assertEquals(2, candidates.size());
        assertEquals(1, candidates.get(0).getRoleType().intValue());
        assertEquals("11", candidates.get(0).getUserId());
        assertFalse(candidates.get(0).isSelected());
        assertEquals(2, candidates.get(1).getRoleType().intValue());
        assertEquals("66", candidates.get(1).getUserId());

        // 计划未维护教学团队时的只读回退：仅课程负责人，不写库（Word 生成口径不变）
        List<TeachingPlanTeacher> fallback = service.listTeacher(101L);
        assertTrue(fallback.isEmpty());
        assertEquals(0, jdbc.queryForObject("SELECT COUNT(*) FROM t_csys_teaching_plan_teacher", Integer.class));

        // 选择已有成员后回显为已选；课程负责人不在团队成员表里
        TeachingPlanTeacher input = new TeachingPlanTeacher();
        input.setTeacherId("66");
        input.setTeacherName("name66");
        TeachingPlanTeacherBatchSaveVo request = new TeachingPlanTeacherBatchSaveVo();
        request.setPlanId(101L);
        request.setTeachers(Collections.singletonList(input));
        service.saveTeachersBatch(request);
        List<TeachingPlanTeamCandidateVo> afterSave = service.listTeacherCandidates(101L);
        assertTrue(afterSave.stream().filter(item -> "66".equals(item.getUserId())).findFirst().orElseThrow().isSelected());
        assertEquals(2, jdbc.queryForObject("SELECT COUNT(*) FROM t_csys_course_team_member WHERE course_id=1 AND sysflag=0", Integer.class));
    }

    @Test
    void unregisteredThirdPartyAccountSyncsWithoutSystemUserLookup() {
        AtomicLong keys = new AtomicLong(7000);
        TeachingPlanTeacherMapper teachers = planTeachers(keys);
        TeachingPlanService service = teachingTeamService(plan101(), teachers);

        login(11, "writer");
        TeachingPlanTeacher input = new TeachingPlanTeacher();
        input.setTeacherId("T9001");
        input.setTeacherName("外聘教师");
        input.setProfessionalTitle("高级工程师");
        input.setDuty("主讲");
        TeachingPlanTeacherBatchSaveVo request = new TeachingPlanTeacherBatchSaveVo();
        request.setPlanId(101L);
        request.setTeachers(Collections.singletonList(input));

        List<TeachingPlanTeacher> saved = service.saveTeachersBatch(request);
        assertEquals("外聘教师", saved.get(0).getTeacherName());
        assertEquals("T9001", saved.get(0).getTeacherId());
        assertEquals(1, jdbc.queryForObject("SELECT COUNT(*) FROM t_csys_teaching_plan_teacher WHERE plan_id=101 AND sysflag=0", Integer.class));
        // 第三方外库ID无法落到系统用户，因此不同步课程库团队成员
        assertEquals(1, jdbc.queryForObject("SELECT COUNT(*) FROM t_csys_course_team_member WHERE course_id=1 AND sysflag=0", Integer.class));

        // 非系统用户教员必须提供姓名
        TeachingPlanTeacher noName = new TeachingPlanTeacher();
        noName.setTeacherId("T9002");
        request.setTeachers(Collections.singletonList(noName));
        assertStatus(400, () -> service.saveTeachersBatch(request));
    }
    @Test
    void accountIdentityIgnoresNumericIdsDisplayNamesAndSubstringMatches() {
        jdbc.update("UPDATE t_csys_course SET author_ids='owner.account',authors='张老师' WHERE id=1");
        login(901, "owner.account");
        writer.editCourse(1L);
        login(11, "same numeric id");
        assertStatus(403, () -> writer.editCourse(1L));
        login(901, "owner");
        assertStatus(403, () -> writer.editCourse(1L));
        login(902, "张老师");
        assertStatus(403, () -> writer.editCourse(1L));
        login(903, "OWNER.ACCOUNT");
        assertStatus(403, () -> writer.editCourse(1L));
        assertTrue(CoursePermissionService.hasAuthor("owner.account,12345", "12345"));
        assertFalse(CoursePermissionService.hasAuthor("owner.account,12345", "123"));
    }

    @Test
    void thirdPartyAccountAndNameRoundTripWithoutRegistration() throws Exception {
        login(11, "writer");
        CurriculumController controller = new CurriculumController();
        ReflectionTestUtils.setField(controller, "coursePermissions", permissions);
        MockMvc mvc = MockMvcBuilders.standaloneSetup(controller).setControllerAdvice(new CoursePermissionExceptionHandler()).build();
        mvc.perform(post("/curriculum/course/1/teamMembers").contentType("application/json")
                .content("{\"members\":[{\"email\":\"new.account\",\"userName\":\"新教员\",\"unitName\":\"单位\"},{\"email\":\"new.account\",\"userName\":\"新教员\"}]}"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.data.teamMemberIds").value("new.account"))
                .andExpect(jsonPath("$.data.teamMembers[0].email").value("new.account"))
                .andExpect(jsonPath("$.data.teamMemberNames").value("新教员"));
        verifyNoInteractions(context.getBean(DoinnerUserService.class));
        login(777, "new.account");
        writer.editCourse(1L);
        mvc.perform(delete("/curriculum/course/1/teamMembers").param("userIds", "new.account"))
                .andExpect(status().isOk());
        assertStatus(403, () -> writer.editCourse(1L));
        login(5, "creator");
        CourseAuthorsSaveVo owners = new CourseAuthorsSaveVo();
        owners.setAuthorIds("owner.a, owner.a,owner.b"); owners.setAuthors("教员甲,教员甲,教员乙");
        CoursePermissionVo result = permissions.saveAuthors(1L, owners);
        assertEquals("owner.a,owner.b", result.getAuthorIds());
        assertEquals("教员甲,教员乙", result.getAuthors());
        login(888, "owner.b"); writer.editCourse(1L);
    }

    private TeachingPlanTeacher accountTeacher(String account, String name) {
        TeachingPlanTeacher teacher = new TeachingPlanTeacher(); teacher.setPlanId(101L);
        teacher.setEmail(account); teacher.setUserName(name); teacher.setProfessionalTitle("讲师");
        teacher.setDuty("主讲"); teacher.setLectureContent("章节一"); return teacher;
    }

    @Test
    void accountSelectionDeduplicatesAllSourcesAndChangingPersonSyncsOnlyTheNewAccount() throws Exception {
        TeachingPlanTeacherMapper teachers = planTeachers(new AtomicLong(8000));
        TeachingPlanService service = teachingTeamService(plan101(), teachers);
        TeachingPlanTeacher teacher = new com.fasterxml.jackson.databind.ObjectMapper()
                .configure(com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false).readValue(
                "{\"email\":\"teacher.a\",\"userName\":\"教员甲\",\"unitName\":\"单位\",\"planId\":101}", TeachingPlanTeacher.class);
        // 使用 Spring 的忽略未知字段设置，第三方 unitName 不参与计划存储。
        Long id = service.addTeacher(teacher);
        assertEquals("teacher.a", teachers.selectById(id).getTeacherId());
        assertStatus(400, () -> service.addTeacher(teacher));
        assertStatus(409, () -> service.addTeacher(accountTeacher("teacher.a", "教员甲")));
        TeachingPlanTeacherBatchSaveVo batch = new TeachingPlanTeacherBatchSaveVo(); batch.setPlanId(101L);
        batch.setTeachers(Arrays.asList(accountTeacher("teacher.a", "教员甲"), accountTeacher("teacher.a", "教员甲")));
        service.saveTeachersBatch(batch);
        assertEquals(1, teachers.selectByPlanId(101L).size());
        TeachingPlanTeacher next = accountTeacher("teacher.b", "教员乙"); next.setId(id);
        batch.setTeachers(Collections.singletonList(next)); service.saveTeachersBatch(batch);
        assertEquals("teacher.b", teachers.selectById(id).getTeacherId());
        assertEquals(new HashSet<>(Arrays.asList("teacher.a", "teacher.b")), permissions.teamMemberIdSet(1L));
        Long other = service.addTeacher(accountTeacher("teacher.c", "教员丙"));
        TeachingPlanTeacher collision = accountTeacher("teacher.b", "教员乙"); collision.setId(other);
        assertStatus(409, () -> service.updateTeacher(collision));
        assertEquals("teacher.c", teachers.selectById(other).getTeacherId());
    }

    @Test
    void editingRemovedCourseMemberDoesNotRestoreMembershipThroughEitherEndpoint() {
        TeachingPlanTeacherMapper teachers = planTeachers(new AtomicLong(9000));
        TeachingPlanService service = teachingTeamService(plan101(), teachers);
        Long id = service.addTeacher(accountTeacher("teacher.a", "教员甲"));
        permissions.removeTeamMembers(1L, Collections.singletonList("teacher.a"));
        TeachingPlanTeacher edit = new TeachingPlanTeacher(); edit.setId(id); edit.setDuty("助教");
        service.updateTeacher(edit);
        assertTrue(permissions.teamMemberIdSet(1L).isEmpty());
        TeachingPlanTeacherBatchSaveVo batch = new TeachingPlanTeacherBatchSaveVo(); batch.setPlanId(101L);
        edit = accountTeacher("teacher.a", "教员甲"); edit.setId(id); edit.setLectureContent("章节二");
        batch.setTeachers(Collections.singletonList(edit)); service.saveTeachersBatch(batch);
        assertTrue(permissions.teamMemberIdSet(1L).isEmpty());
        assertEquals("章节二", teachers.selectById(id).getLectureContent());
        TeachingPlanTeacher replacement = accountTeacher("teacher.b", "教员乙"); replacement.setId(id);
        service.updateTeacher(replacement);
        assertEquals(Collections.singleton("teacher.b"), permissions.teamMemberIdSet(1L));
        service.deleteTeacher(id);
        assertEquals(Collections.singleton("teacher.b"), permissions.teamMemberIdSet(1L));
    }

    @Test
    void synchronizationFailureRollsBackSingleBatchAndPersonChanges() {
        TeachingPlanTeacherMapper teachers = planTeachers(new AtomicLong(10000));
        TeachingPlanService service = teachingTeamService(plan101(), teachers);
        jdbc.execute("ALTER TABLE t_csys_course_team_member ADD CONSTRAINT reject_test_account CHECK(user_id <> 'reject.account')");
        assertThrows(org.springframework.dao.DataAccessException.class, () -> service.addTeacher(accountTeacher("reject.account", "错误教员")));
        assertTrue(teachers.selectByPlanId(101L).isEmpty());
        TeachingPlanTeacherBatchSaveVo batch = new TeachingPlanTeacherBatchSaveVo(); batch.setPlanId(101L);
        batch.setTeachers(Arrays.asList(accountTeacher("good.account", "正常教员"), accountTeacher("reject.account", "错误教员")));
        assertThrows(org.springframework.dao.DataAccessException.class, () -> service.saveTeachersBatch(batch));
        assertTrue(teachers.selectByPlanId(101L).isEmpty());
        assertTrue(permissions.teamMemberIdSet(1L).isEmpty());
        Long id = service.addTeacher(accountTeacher("good.account", "正常教员"));
        TeachingPlanTeacher changed = accountTeacher("reject.account", "错误教员"); changed.setId(id);
        assertThrows(org.springframework.dao.DataAccessException.class, () -> service.updateTeacher(changed));
        assertEquals("good.account", teachers.selectById(id).getTeacherId());
    }

    @Test
    void newPlanSnapshotsOwnersAndSupportsEmptyOwnerAndExplicitTeamRemoval() {
        TeachingPlanTeacherMapper teachers = planTeachers(new AtomicLong(11000));
        TeachingPlanMapper plans = plan101();
        AtomicLong planKeys = new AtomicLong(400);
        when(plans.insert(any(TeachingPlan.class))).thenAnswer(call -> {
            TeachingPlan plan = call.getArgument(0); plan.setId(planKeys.incrementAndGet());
            return jdbc.update("INSERT INTO t_csys_teaching_plan(id,source_course_id,status,sysflag) VALUES(?,?,0,0)", plan.getId(), plan.getSourceCourseId());
        });
        when(plans.selectById(anyLong())).thenAnswer(call -> jdbc.query("SELECT id,source_course_id FROM t_csys_teaching_plan WHERE id=?", (row, index) -> {
            TeachingPlan plan = new TeachingPlan(); plan.setId(row.getLong(1)); plan.setSourceCourseId(row.getLong(2)); return plan;
        }, (Long) call.getArgument(0)).stream().findFirst().orElse(null));
        TeachingPlanService service = teachingTeamService(plans, teachers);
        TeachingPlanSaveVo save = new TeachingPlanSaveVo(); TeachingPlan plan = new TeachingPlan();
        plan.setSourceCourseId(1L); plan.setPlanType(1); save.setPlan(plan);
        Long id = service.saveTeachingPlan(save);
        List<TeachingPlanTeacher> stored = service.listTeacher(id);
        assertEquals(1, stored.size()); assertEquals("11", stored.get(0).getTeacherId()); assertNotNull(stored.get(0).getId());
        assertTrue(service.listTeacherCandidates(id).get(0).isSelected());
        CourseAuthorsSaveVo owners = new CourseAuthorsSaveVo(); setAuthorAccounts(owners, "22"); permissions.saveAuthors(1L, owners);
        assertEquals("11", service.listTeacher(id).get(0).getTeacherId());
        login(22, "new owner"); service.deleteTeacher(stored.get(0).getId());
        assertTrue(service.listTeacher(id).isEmpty());
        login(5, "creator"); plan = new TeachingPlan(); plan.setSourceCourseId(4L); plan.setPlanType(1); save.setPlan(plan);
        assertTrue(service.listTeacher(service.saveTeachingPlan(save)).isEmpty());
    }

    @Test
    void materialsAndCourseChildrenRejectUnauthorizedAndReassignedRecords() {
        CurriculumServiceImpl service = curriculum(permissions);
        CourseMapper courses = (CourseMapper) ReflectionTestUtils.getField(service, "courseMapper");
        jdbc.execute("CREATE TABLE t_csys_course_chapter(id BIGINT PRIMARY KEY,course_id BIGINT,sysflag INT DEFAULT 0)");
        jdbc.execute("CREATE TABLE t_csys_course_textbook(id BIGINT PRIMARY KEY,course_id BIGINT,sysflag INT DEFAULT 0)");
        jdbc.execute("CREATE TABLE t_csys_course_teaching_theory_plan(id BIGINT PRIMARY KEY,course_id BIGINT,sysflag INT DEFAULT 0)");
        jdbc.update("INSERT INTO t_csys_course_chapter VALUES(51,1,0)");
        jdbc.update("INSERT INTO t_csys_course_textbook VALUES(61,1,0)");
        jdbc.update("INSERT INTO t_csys_course_teaching_theory_plan VALUES(71,1,0)");
        com.doinner.csys.domain.CourseChapter chapter = new com.doinner.csys.domain.CourseChapter(); chapter.setId(51L);
        com.doinner.csys.domain.CourseTextbook book = new com.doinner.csys.domain.CourseTextbook(); book.setId(61L);
        login(991, "outsider");
        assertStatus(403, () -> service.deleteFile(1L));
        assertStatus(403, () -> service.updateFileById("file", "材料", 1L));
        assertStatus(403, () -> service.updateCourseChapter(chapter));
        assertStatus(403, () -> service.updateCourseTextbook(book));
        assertStatus(403, () -> service.deleteCourseTeachingTheoryPlanByIds(new Long[]{71L}));
        verify(courses, never()).deleteFile(anyLong()); verify(courses, never()).updateFileById(anyString(), anyString(), anyLong());
        login(11, "writer"); service.deleteFile(1L); service.updateFileById("file", "材料", 1L);
        chapter.setCourseId(2L); assertStatus(400, () -> service.updateCourseChapter(chapter));
        CourseTeamMemberSaveVo team = new CourseTeamMemberSaveVo(); team.setMembers(Collections.singletonList(new CoursePersonVo("team.account", "团队教员")));
        permissions.addTeamMembers(1L, team); login(992, "team.account");
        chapter.setCourseId(1L); service.updateCourseChapter(chapter); service.updateCourseTextbook(book);
        service.deleteCourseTeachingTheoryPlanByIds(new Long[]{71L});
    }

    @Test
    void simultaneousAccountAddsProduceOnePlanMemberAndOneCourseMember() throws Exception {
        TeachingPlanTeacherMapper teachers = planTeachers(new AtomicLong(12000));
        TeachingPlanService service = teachingTeamService(plan101(), teachers);
        java.util.concurrent.ExecutorService pool = java.util.concurrent.Executors.newFixedThreadPool(2);
        try {
            java.util.concurrent.Callable<Void> add = () -> {
                login(11, "writer");
                try {
                    TeachingPlanTeacherBatchSaveVo batch = new TeachingPlanTeacherBatchSaveVo(); batch.setPlanId(101L);
                    batch.setTeachers(Collections.singletonList(accountTeacher("shared.account", "同一教员")));
                    service.saveTeachersBatch(batch); return null;
                } finally { SecurityContextHolder.remove(); }
            };
            java.util.concurrent.Future<Void> first = pool.submit(add), second = pool.submit(add);
            first.get(10, java.util.concurrent.TimeUnit.SECONDS); second.get(10, java.util.concurrent.TimeUnit.SECONDS);
            assertEquals(1, teachers.selectByPlanId(101L).size());
            assertEquals(Collections.singleton("shared.account"), permissions.teamMemberIdSet(1L));
        } finally { pool.shutdownNow(); }
    }

    @Test
    void sameAccountKeepsSeparatePlanDetailsAndForeignBatchRecordCannotBeEdited() {
        jdbc.update("INSERT INTO t_csys_teaching_plan(id,source_course_id,status,sysflag) VALUES(102,1,0,0)");
        TeachingPlanMapper plans = plan101();
        TeachingPlan plan = new TeachingPlan(); plan.setId(102L); plan.setSourceCourseId(1L);
        when(plans.selectById(102L)).thenReturn(plan);
        TeachingPlanTeacherMapper teachers = planTeachers(new AtomicLong(13000));
        TeachingPlanService service = teachingTeamService(plans, teachers);
        Long first = service.addTeacher(accountTeacher("same.account", "同一教员"));
        TeachingPlanTeacher other = accountTeacher("same.account", "同一教员"); other.setPlanId(102L);
        other.setDuty("助教"); other.setLectureContent("另一个计划的内容");
        service.addTeacher(other);
        assertEquals("主讲", teachers.selectById(first).getDuty());
        assertEquals("另一个计划的内容", teachers.selectById(other.getId()).getLectureContent());
        assertEquals(Collections.singleton("same.account"), permissions.teamMemberIdSet(1L));
        TeachingPlanTeacher forged = accountTeacher("changed.account", "篡改"); forged.setId(first);
        TeachingPlanTeacherBatchSaveVo batch = new TeachingPlanTeacherBatchSaveVo(); batch.setPlanId(102L);
        batch.setTeachers(Collections.singletonList(forged));
        assertStatus(400, () -> service.saveTeachersBatch(batch));
        assertEquals("same.account", teachers.selectById(first).getTeacherId());
    }

    private static void setAuthorAccounts(CourseAuthorsSaveVo input, String accounts) {
        input.setAuthorIds(accounts);
        input.setAuthors(accounts == null || accounts.isEmpty() ? "" : Arrays.stream(accounts.split(",", -1)).map(x -> "name" + x.trim()).collect(java.util.stream.Collectors.joining(",")));
    }
    private static void setAuthorAccounts(Course input, String accounts) { input.setAuthorIds(accounts); }
    private static void setTeamAccounts(CourseTeamMemberSaveVo input, Object accounts) {
        input.setUserIds(accounts);
        List<String> ids = input.resolvedUserIds();
        input.setTeamMemberNames(ids == null ? null : ids.stream().map(x -> "name" + x).collect(java.util.stream.Collectors.joining(",")));
    }

    private static void login(long id, String name) {
        if (name != null && !name.isEmpty() && (id == 11 || id == 22 || id == 33 || id == 44 || id == 55 || id == 66)) name = String.valueOf(id);
        SecurityContextHolder.remove();
        SecurityContextHolder.setUserId(String.valueOf(id)); SecurityContextHolder.setUserName(name);
    }
    private static void loginRoles(long id, String name, String... roles) {
        if (name != null && !name.isEmpty() && (id == 11 || id == 22 || id == 33 || id == 44 || id == 55 || id == 66)) name = String.valueOf(id);

        login(id, name);
        LoginUser user = new LoginUser(); user.setUserid(id); user.setUsername(name);
        user.setRoles(new HashSet<>(Arrays.asList(roles)));
        SecurityContextHolder.set("login_user", user);
    }
    private static void assertStatus(int status, Runnable call) { assertEquals(status, assertThrows(CoursePermissionException.class, call::run).getStatus()); }

    public interface Writer {
        void editCourse(Long id);
        void deleteCourses(List<Long> ids);
        void writeThenFail(Long id);
        void updateContent(TeachingPlanContent content);
        void replaceKnowledge(Long id);
        void holdWrite(Long id, CountDownLatch locked, CountDownLatch release);
    }
    public static class WriterImpl implements Writer {
        private final JdbcTemplate jdbc;
        private final CoursePermissionService permissions;
        WriterImpl(JdbcTemplate jdbc, CoursePermissionService permissions) { this.jdbc = jdbc; this.permissions = permissions; }
        @CoursePermission(value = "#p0", sourceCoursesOnly = true)
        public void editCourse(Long id) { jdbc.update("UPDATE t_csys_course SET name='modified' WHERE id=?", id); }
        @CoursePermission(value = "#p0", sourceCoursesOnly = true)
        public void deleteCourses(List<Long> ids) { ids.forEach(id -> jdbc.update("UPDATE t_csys_course SET sysflag=2 WHERE id=?", id)); }
        @CoursePermission(value = "#p0")
        public void writeThenFail(Long id) { editCourse(id); throw new IllegalStateException("rollback"); }
        @CoursePermission(resource = CONTENT, value = "#p0.id", request = "#p0")
        public void updateContent(TeachingPlanContent content) { jdbc.update("UPDATE t_csys_teaching_plan_content SET plan_id=?,content=? WHERE id=?", content.getPlanId(), content.getContent(), content.getId()); }
        @CoursePermission(value = "#p0")
        public void replaceKnowledge(Long id) {
            permissions.requireSourceCourse(id);
            jdbc.update("DELETE FROM t_csys_course_ref_knowledge_unit WHERE course_id=?", id);
            jdbc.update("INSERT INTO t_csys_course_ref_knowledge_unit VALUES(?,20)", id);
            permissions.synchronizeKnowledgeReferences(id);
        }
        @CoursePermission(value = "#p0")
        public void holdWrite(Long id, CountDownLatch locked, CountDownLatch release) {
            locked.countDown();
            try { if (!release.await(5, TimeUnit.SECONDS)) throw new IllegalStateException("timeout"); }
            catch (InterruptedException e) { Thread.currentThread().interrupt(); throw new IllegalStateException(e); }
            editCourse(id);
        }
    }

    @TestConfiguration
    @EnableAspectJAutoProxy
    @EnableTransactionManagement
    static class Config {
        @Bean DataSource datasource() { return new DriverManagerDataSource("jdbc:h2:mem:" + UUID.randomUUID() + ";MODE=MySQL;DB_CLOSE_DELAY=-1;LOCK_TIMEOUT=5000", "sa", ""); }
        @Bean JdbcTemplate jdbc(DataSource datasource) { return new JdbcTemplate(datasource); }
        @Bean PlatformTransactionManager manager(DataSource datasource) { return new DataSourceTransactionManager(datasource); }
        @Bean DoinnerUserService users() { return mock(DoinnerUserService.class); }
        @Bean CoursePermissionService permissions(JdbcTemplate jdbc, DoinnerUserService users) { return new CoursePermissionService(jdbc); }
        @Bean CoursePermissionAspect aspect(CoursePermissionService permissions, PlatformTransactionManager manager) { return new CoursePermissionAspect(permissions, manager); }
        @Bean Writer writer(JdbcTemplate jdbc, CoursePermissionService permissions) { return new WriterImpl(jdbc, permissions); }
        @Bean CourseMapper courseMapper() { return mock(CourseMapper.class); }
        @Bean CourseKnowledgeUnitMapper unitMapper() { return mock(CourseKnowledgeUnitMapper.class); }
        @Bean CourseKnowledgePointMapper pointMapper() { return mock(CourseKnowledgePointMapper.class); }
        @Bean CourseRefKnowledgeUnitMapper refMapper() { return mock(CourseRefKnowledgeUnitMapper.class); }
        @Bean IKnowledgeUnitService knowledge() { return new KnowledgeUnitServiceImpl(); }
    }
}
