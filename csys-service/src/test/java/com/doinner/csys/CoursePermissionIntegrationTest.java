package com.doinner.csys;

import com.doinner.common.core.context.SecurityContextHolder;
import com.doinner.common.core.domain.DataSet;
import com.doinner.csys.domain.Course;
import com.doinner.csys.domain.TeachingPlanContent;
import com.doinner.csys.domain.TeachingPlan;
import com.doinner.csys.domain.vo.*;
import com.doinner.csys.permission.*;
import com.doinner.csys.controller.CurriculumController;
import com.doinner.csys.dao.*;
import com.doinner.csys.entity.csys.po.CourseKnowledgeUnit;
import com.doinner.csys.entity.csys.po.CourseKnowledgePoint;
import com.doinner.csys.service.IKnowledgeUnitService;
import com.doinner.csys.service.impl.KnowledgeUnitServiceImpl;
import com.doinner.csys.service.impl.TeachingPlanServiceImpl;
import com.doinner.csys.service.impl.CourseServiceImpl;
import com.doinner.system.domain.entity.SysUser;
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

import javax.sql.DataSource;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

import static com.doinner.csys.permission.CourseResource.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
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
        assertFalse(CoursePermissionService.hasAuthor("11,22", 1L));
        assertTrue(CoursePermissionService.hasAuthor(" 11,22 ", 11L));
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
    void creatorsConfigureValidatedNamesAndCanClearAgainWithoutEditingContent() {
        login(5, "creator");
        CourseAuthorsSaveVo input = new CourseAuthorsSaveVo(); input.setAuthorIds("22, 22,11");
        CoursePermissionVo result = permissions.saveAuthors(1L, input);
        assertEquals("22,11", result.getAuthorIds());
        assertEquals("name22,name11", result.getAuthors());
        assertEquals("22,11", jdbc.queryForObject("SELECT author_ids FROM t_csys_course WHERE id=3", String.class));
        assertFalse(result.isCanOperate());
        input.setAuthorIds(""); permissions.saveAuthors(1L, input);
        assertStatus(403, () -> writer.editCourse(1L));
        input.setAuthorIds("11"); permissions.saveAuthors(1L, input);
        input.setAuthorIds(null); assertStatus(400, () -> permissions.saveAuthors(1L, input));
        input.setAuthorIds("11,,22"); assertStatus(400, () -> permissions.saveAuthors(1L, input));
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
        Course course = new Course(); course.setId(1L); course.setAuthorIds("11,22");
        assertStatus(400, () -> permissions.preserveCourseOwners(course));
        course.setAuthorIds(null); course.setCreator("forged");
        assertStatus(400, () -> permissions.preserveCourseOwners(course));
        CourseVo newCourse = new CourseVo(); newCourse.setId(1L);
        assertStatus(400, () -> permissions.prepareCreation(newCourse));
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
                try { CourseAuthorsSaveVo input = new CourseAuthorsSaveVo(); input.setAuthorIds("22"); permissions.saveAuthors(1L, input); }
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
    private static void login(long id, String name) { SecurityContextHolder.setUserId(String.valueOf(id)); SecurityContextHolder.setUserName(name); }
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
        @Bean CoursePermissionService permissions(JdbcTemplate jdbc, DoinnerUserService users) { return new CoursePermissionService(jdbc, users); }
        @Bean CoursePermissionAspect aspect(CoursePermissionService permissions, PlatformTransactionManager manager) { return new CoursePermissionAspect(permissions, manager); }
        @Bean Writer writer(JdbcTemplate jdbc, CoursePermissionService permissions) { return new WriterImpl(jdbc, permissions); }
        @Bean CourseMapper courseMapper() { return mock(CourseMapper.class); }
        @Bean CourseKnowledgeUnitMapper unitMapper() { return mock(CourseKnowledgeUnitMapper.class); }
        @Bean CourseKnowledgePointMapper pointMapper() { return mock(CourseKnowledgePointMapper.class); }
        @Bean CourseRefKnowledgeUnitMapper refMapper() { return mock(CourseRefKnowledgeUnitMapper.class); }
        @Bean IKnowledgeUnitService knowledge() { return new KnowledgeUnitServiceImpl(); }
    }
}
