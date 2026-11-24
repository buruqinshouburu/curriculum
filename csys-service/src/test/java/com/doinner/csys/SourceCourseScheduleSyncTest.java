package com.doinner.csys;

import com.doinner.common.core.context.SecurityContextHolder;
import com.doinner.csys.dao.*;
import com.doinner.csys.domain.*;
import com.doinner.csys.domain.vo.CourseVo;
import com.doinner.csys.domain.vo.TrainingSchemeVo;
import com.doinner.csys.domain.vo.TrainingSchemeCourseScheduleVo;
import com.doinner.csys.permission.CoursePermissionService;
import com.doinner.csys.service.CurriculumService;
import com.doinner.csys.service.CourseMajorSelectionService;
import com.doinner.csys.service.impl.CurriculumServiceImpl;
import com.doinner.csys.service.impl.CourseServiceImpl;
import com.doinner.csys.service.impl.TrainingServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.ibatis.builder.xml.XMLMapperBuilder;
import org.apache.ibatis.datasource.unpooled.UnpooledDataSource;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.transaction.SpringManagedTransactionFactory;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.annotation.AnnotationTransactionAttributeSource;
import org.springframework.transaction.interceptor.TransactionInterceptor;

import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class SourceCourseScheduleSyncTest {
    private CurriculumService service;
    private CourseMapper courses;
    private JdbcTemplate jdbc;
    private CourseVo stored;
    private CourseMajorSelectionService majorSelections;
    private CourseScheduleMapper detailMapper;
    private TrainingServiceImpl training;

    @BeforeEach
    void setup() throws Exception {
        SecurityContextHolder.setUserId("77");
        SecurityContextHolder.setUserName("schedule.sync.test");
        UnpooledDataSource dataSource = new UnpooledDataSource("org.h2.Driver",
                "jdbc:h2:mem:" + UUID.randomUUID() + ";MODE=MySQL;DB_CLOSE_DELAY=-1", "sa", "");
        jdbc = new JdbcTemplate(dataSource);
        jdbc.execute("CREATE TABLE t_csys_course (id BIGINT PRIMARY KEY, name VARCHAR(100),"
                + "before_course_id VARCHAR(100), after_course_id VARCHAR(100), course_attr VARCHAR(10),"
                + "type VARCHAR(10), time_week DOUBLE, unit VARCHAR(10), source_id BIGINT,"
                + "academic_terms_number INT, semester_schedule VARCHAR(10))");
        jdbc.execute("ALTER TABLE t_csys_course ADD course_module VARCHAR(50)");
        jdbc.execute("ALTER TABLE t_csys_course ADD course_module_children VARCHAR(50)");
        jdbc.execute("CREATE TABLE t_csys_course_ref_schedule (id BIGINT AUTO_INCREMENT PRIMARY KEY,"
                + "course_id BIGINT, semester_schedule VARCHAR(10), spring_autumn VARCHAR(10),"
                + "teach_hours DOUBLE, practice_hours DOUBLE, time_week DOUBLE, unit VARCHAR(10))");
        jdbc.execute("CREATE TABLE t_csys_training_scheme_course_schedule (id BIGINT AUTO_INCREMENT PRIMARY KEY,"
                + "scheme_id BIGINT, course_id BIGINT, type VARCHAR(10), term INT, hours DOUBLE,"
                + "theory_hours DOUBLE, teach_hours DOUBLE, practice_hours DOUBLE, checked INT,"
                + "course_attr VARCHAR(10), credits DOUBLE)");
        jdbc.execute("INSERT INTO t_csys_course_ref_schedule (course_id,semester_schedule,spring_autumn,teach_hours)"
                + " VALUES (1,'1','1',10),(11,'5','2',999),(12,'4','1',888),(13,'3','1',777),(99,'1','1',123)");
        jdbc.execute("INSERT INTO t_csys_training_scheme_course_schedule (scheme_id,course_id,term,teach_hours)"
                + " VALUES (101,11,10,999),(102,11,8,888),(101,12,6,777),(103,13,5,666),(101,99,1,123)");
        Configuration config = new Configuration(new Environment("sync", new SpringManagedTransactionFactory(), dataSource));
        for (String mapper : Arrays.asList("CourseMapper.xml", "CourseSheduleMapper.xml", "TrainingSchemeCourseScheduleMapper.xml")) {
            try (InputStream xml = getClass().getResourceAsStream("/mapper/csys/" + mapper)) {
                assertNotNull(xml);
                new XMLMapperBuilder(xml, config, mapper, config.getSqlFragments()).parse();
            }
        }
        SqlSessionTemplate session = new SqlSessionTemplate(new SqlSessionFactoryBuilder().build(config));
        CurriculumServiceImpl target = new CurriculumServiceImpl();
        for (Field field : CurriculumServiceImpl.class.getDeclaredFields()) {
            if (field.getType().isInterface())
                ReflectionTestUtils.setField(target, field.getName(), mock(field.getType()));
        }
        ReflectionTestUtils.setField(target, "coursePermissions", mock(CoursePermissionService.class));
        majorSelections = mock(CourseMajorSelectionService.class);
        ReflectionTestUtils.setField(target, "courseMajorSelectionService", majorSelections);
        detailMapper = session.getMapper(CourseScheduleMapper.class);
        ReflectionTestUtils.setField(target, "courseScheduleMapper", detailMapper);
        TrainingSchemeCourseScheduleMapper scheduleMapper = session.getMapper(TrainingSchemeCourseScheduleMapper.class);
        ReflectionTestUtils.setField(target, "trainingSchemeCourseScheduleMapper", scheduleMapper);
        training = new TrainingServiceImpl();
        ReflectionTestUtils.setField(training, "trainingSchemeCourseScheduleMapper", scheduleMapper);
        ReflectionTestUtils.setField(target, "trainingService", training);
        courses = (CourseMapper) ReflectionTestUtils.getField(target, "courseMapper");
        stored = request("1");
        when(courses.selectCourseById(1L)).thenReturn(stored);
        when(courses.selectCourseBySourceId(1L)).thenReturn(Arrays.asList(copy(11L,"1"), copy(12L,"1"), copy(13L,"2")));
        TrainingSchemeRefCourseMapper refs = (TrainingSchemeRefCourseMapper) ReflectionTestUtils.getField(target, "courseRefCourseMapper");
        when(refs.selectTrainingSchemeRefCourseList(any())).thenAnswer(call -> {
            Long id = ((TrainingSchemeRefCourse) call.getArgument(0)).getCourseId();
            return id == 11L ? Arrays.asList(ref(101L,id), ref(102L,id), ref(101L,id))
                    : Collections.singletonList(ref(101L,id));
        });
        ProxyFactory proxy = new ProxyFactory(target);
        proxy.addAdvice(new TransactionInterceptor(new DataSourceTransactionManager(dataSource),
                new AnnotationTransactionAttributeSource()));
        service = (CurriculumService) proxy.getProxy();
    }

    @AfterEach
    void cleanup() { SecurityContextHolder.remove(); }

    @Test
    void updateValidatesMajorSelectionOnceBeforePersistence() {
        CourseVo request = request("1");
        IllegalArgumentException rejected = new IllegalArgumentException("invalid direction");
        doThrow(rejected).when(majorSelections).validate(request, stored);
        assertSame(rejected, assertThrows(IllegalArgumentException.class, () -> service.updateCourse(request)));
        verify(majorSelections).validate(request, stored);
        verify(courses).selectCourseById(1L);
        verifyNoMoreInteractions(courses);
        assertEquals(10.0, lecture(1));
    }

    @Test
    void insertValidatesMajorSelectionOnceBeforePersistence() {
        CourseVo request = request("1");
        request.setId(null);
        IllegalArgumentException rejected = new IllegalArgumentException("invalid direction");
        doThrow(rejected).when(majorSelections).validate(request);
        assertSame(rejected, assertThrows(IllegalArgumentException.class, () -> service.insertCourse(request)));
        verify(majorSelections).validate(request);
        verifyNoInteractions(courses);
    }

    @Test
    void overwritesCustomizedCopiesAndEverySchemeUsingSourceYearsSeasonsAndHours() {
        CourseVo request = request(null); // Stored type must also activate sync.
        request.setCourseScheduleList(Arrays.asList(schedule("2","5",16,8), schedule("2","3",4,0)));
        service.updateCourse(request);
        for (long id : new long[]{11,12}) {
            assertEquals(Arrays.asList("2", "2"), jdbc.queryForList(
                    "SELECT semester_schedule FROM t_csys_course_ref_schedule WHERE course_id=? ORDER BY id", String.class, id));
            assertEquals(Arrays.asList(16.0,4.0), jdbc.queryForList(
                    "SELECT teach_hours FROM t_csys_course_ref_schedule WHERE course_id=? ORDER BY id", Double.class,id));
        }
        for (long[] pair : new long[][]{{101,11},{102,11},{101,12}}) {
            assertEquals(Arrays.asList(3,4,3), jdbc.queryForList(
                    "SELECT term FROM t_csys_training_scheme_course_schedule WHERE scheme_id=? AND course_id=? ORDER BY id",
                    Integer.class,pair[0],pair[1]));
            assertEquals(Arrays.asList(24.0,24.0,4.0), jdbc.queryForList(
                    "SELECT teach_hours+practice_hours FROM t_csys_training_scheme_course_schedule WHERE scheme_id=? AND course_id=? ORDER BY id",
                    Double.class,pair[0],pair[1]));
            assertEquals(Collections.singletonList(28.0), jdbc.queryForList(
                    "SELECT DISTINCT hours FROM t_csys_training_scheme_course_schedule WHERE scheme_id=? AND course_id=?",
                    Double.class,pair[0],pair[1]));
        }
        assertEquals(777.0, lecture(13));
        assertEquals(123.0, lecture(99));
        assertEquals(1L, request.getCourseScheduleList().get(0).getCourseId(), "Copies must not mutate source response rows");
        verify(courses).updateCourse(argThat(c -> Long.valueOf(11).equals(c.getId()) && Double.valueOf(28).equals(c.getHours())));
    }

    @ParameterizedTest
    @ValueSource(strings = {"1", "3"})
    void hoursOverwritePreservesEachInvokedChildModule(String sourceType) {
        Course first = copy(11L, "1");
        first.setCourseModuleChildren("module-a");
        Course second = copy(12L, "3");
        second.setCourseModuleChildren("module-b");
        when(courses.selectCourseBySourceId(1L)).thenReturn(Arrays.asList(first, second));
        CourseVo request = request(sourceType);
        request.setCourseModuleChildren("source-module");
        request.setCourseScheduleList(Collections.singletonList(schedule("2", "1", 16, 8)));

        service.updateCourse(request);

        verify(courses).updateCourse(argThat(c -> Long.valueOf(11).equals(c.getId())
                && "module-a".equals(c.getCourseModuleChildren()) && Double.valueOf(24).equals(c.getHours())));
        verify(courses).updateCourse(argThat(c -> Long.valueOf(12).equals(c.getId())
                && "module-b".equals(c.getCourseModuleChildren()) && Double.valueOf(24).equals(c.getHours())));
        assertEquals("module-a", jdbc.queryForObject(
                "SELECT type FROM t_csys_training_scheme_course_schedule WHERE scheme_id=101 AND course_id=11", String.class));
        assertEquals("module-b", jdbc.queryForObject(
                "SELECT type FROM t_csys_training_scheme_course_schedule WHERE scheme_id=101 AND course_id=12", String.class));
    }

    @Test
    void nullSchedulesDoNotOverwriteInvokedDetailsOrCustomizedSchemeSchedules() {
        CourseVo request = request("1");
        request.setCourseScheduleList(null);
        assertInvokedSchedulesUnchanged(request);
    }

    @Test
    void omittedJsonSchedulesDoNotOverwriteInvokedDetailsOrCustomizedSchemeSchedules() throws Exception {
        CourseVo request = new ObjectMapper().readValue("{\"id\":1,\"type\":\"1\"}", CourseVo.class);
        assertInvokedSchedulesUnchanged(request);
    }

    private void assertInvokedSchedulesUnchanged(CourseVo request) {
        List<Map<String, Object>> details = jdbc.queryForList(
                "SELECT * FROM t_csys_course_ref_schedule ORDER BY id");
        List<Map<String, Object>> schedules = jdbc.queryForList(
                "SELECT * FROM t_csys_training_scheme_course_schedule ORDER BY id");
        service.updateCourse(request);
        assertEquals(details, jdbc.queryForList(
                "SELECT * FROM t_csys_course_ref_schedule ORDER BY id"));
        assertEquals(schedules, jdbc.queryForList(
                "SELECT * FROM t_csys_training_scheme_course_schedule ORDER BY id"));
        verify(courses, never()).selectCourseBySourceId(anyLong());
        verify(courses, never()).updateCourse(argThat(c -> !Long.valueOf(1).equals(c.getId())));
    }

    @Test
    void missingSchemeSchedulesAreRebuiltAndRepeatedSavesDoNotDuplicateRows() {
        jdbc.execute("DELETE FROM t_csys_training_scheme_course_schedule WHERE course_id IN (11,12)");
        CourseVo request = request("1");
        request.setCourseScheduleList(Collections.singletonList(schedule("5","2",16,8)));
        service.updateCourse(request);
        service.updateCourse(request);
        assertEquals(3, count("t_csys_training_scheme_course_schedule", "course_id IN (11,12) AND term=10"));
        assertEquals(3, count("t_csys_course_ref_schedule", "course_id IN (1,11,12)"));
    }

    @Test
    void emptySchedulesClearInvokedDetailsAndCustomizedSchemeSchedules() throws Exception {
        CourseVo request = new ObjectMapper().readValue(
                "{\"id\":1,\"type\":\"1\",\"courseScheduleList\":[]}", CourseVo.class);
        service.updateCourse(request);
        assertEquals(0, count("t_csys_course_ref_schedule", "course_id IN (1,11,12)"));
        assertEquals(0, count("t_csys_training_scheme_course_schedule", "course_id IN (11,12)"));
        assertEquals(123.0, lecture(99));
    }

    @ParameterizedTest
    @ValueSource(strings = {"2","4"})
    void nonCourseSourcesDoNotOverwriteInvokedSchedules(String type) {
        stored.setType(type);
        CourseVo request = request(type);
        request.setCourseScheduleList(Collections.emptyList());
        service.updateCourse(request);
        assertEquals(999.0, lecture(11));
        verify(courses, never()).selectCourseBySourceId(anyLong());
    }

    @Test
    void databaseFailureRollsBackSourceCopiesAndAllPreviouslyRebuiltSchemes() {
        jdbc.execute("ALTER TABLE t_csys_training_scheme_course_schedule ADD CONSTRAINT reject_new_second_scheme CHECK (scheme_id <> 102 OR term=8)");
        CourseVo request = request("1");
        request.setCourseScheduleList(Collections.singletonList(schedule("2","1",16,8)));
        assertThrows(RuntimeException.class, () -> service.updateCourse(request));
        assertEquals(10.0, lecture(1));
        assertEquals(999.0, lecture(11));
        assertEquals(888.0, lecture(12));
        assertEquals(5, count("t_csys_training_scheme_course_schedule", "1=1"));
        assertEquals(999.0, jdbc.queryForObject("SELECT teach_hours FROM t_csys_training_scheme_course_schedule WHERE scheme_id=101 AND course_id=11", Double.class));
    }

    private double lecture(long id) {
        return jdbc.queryForObject("SELECT teach_hours FROM t_csys_course_ref_schedule WHERE course_id=?",Double.class,id);
    }

    @Test
    void fourYearPracticalSourceOverridesAllConfiguredCopiesWithoutChangingOtherColumns() {
        practicalSource();
        List<Map<String, Object>> expected = jdbc.queryForList(
                "SELECT * FROM t_csys_training_scheme_course_schedule ORDER BY id");
        for (Map<String, Object> row : expected) {
            if (Arrays.asList(11L, 12L).contains(row.get("COURSE_ID"))) row.put("TERM", 8);
        }
        CourseVo request = request(null); // Resolve omitted type from the stored course.
        request.setSemesterSchedule("1"); // Header must not override detail data.
        request.setCourseScheduleList(Collections.singletonList(schedule("6", null, null, null)));

        service.updateCourse(request);
        service.updateCourse(request);

        assertEquals(expected, jdbc.queryForList(
                "SELECT * FROM t_csys_training_scheme_course_schedule ORDER BY id"));
    }

    @Test
    void fourYearPracticalSourceSynchronizesInvokedDetailsWithoutChangingInvokedHours() {
        practicalSource();
        jdbc.execute("INSERT INTO t_csys_course_ref_schedule (course_id,semester_schedule,spring_autumn,teach_hours,practice_hours,time_week,unit)"
                + " VALUES (11,'2','1',91,7,3,'1'),(12,'3','5',81,6,4,'2')");
        List<Map<String, Object>> expected = jdbc.queryForList("SELECT course_id,semester_schedule,spring_autumn,teach_hours,practice_hours,time_week,unit"
                + " FROM t_csys_course_ref_schedule WHERE course_id IN (11,12) ORDER BY course_id,id");
        for (Map<String, Object> row : expected) {
            row.put("SEMESTER_SCHEDULE", "6");
            row.put("SPRING_AUTUMN", "2");
        }
        CourseVo request = request("2");
        request.setCourseScheduleList(Collections.singletonList(schedule("6", "1", 16, 8)));

        service.updateCourse(request);

        assertEquals(expected, jdbc.queryForList("SELECT course_id,semester_schedule,spring_autumn,teach_hours,practice_hours,time_week,unit"
                + " FROM t_csys_course_ref_schedule WHERE course_id IN (11,12) ORDER BY course_id,id"));
    }

    @Test
    void regeneratedFourYearPracticalSourceStaysInTermEightWithOneRowPerScheme() {
        practicalSource();
        jdbc.execute("INSERT INTO t_csys_course_ref_schedule (course_id,semester_schedule,spring_autumn,teach_hours,practice_hours)"
                + " VALUES (11,'2','5',91,7),(12,'3','1',81,6)");
        CourseVo request = request("2");
        request.setCourseScheduleList(Collections.singletonList(schedule("6", "1", 16, 8)));

        service.updateCourse(request);

        jdbc.execute("DELETE FROM t_csys_training_scheme_course_schedule WHERE course_id IN (11,12)");
        for (long[] pair : new long[][]{{101,11},{101,12},{102,11}}) {
            Course invoked = copy(pair[1], "2");
            invoked.setCourseScheduleList(detailMapper.selectByCourseId(pair[1]));
            TrainingSchemeVo scheme = new TrainingSchemeVo();
            scheme.setId(pair[0]);
            training.setCourseSchedule(scheme, Collections.singletonList(invoked));
        }

        assertEquals(1, count("t_csys_training_scheme_course_schedule", "scheme_id=101 AND course_id=11 AND term=8"));
        assertEquals(1, count("t_csys_training_scheme_course_schedule", "scheme_id=101 AND course_id=12 AND term=8"));
        assertEquals(1, count("t_csys_training_scheme_course_schedule", "scheme_id=102 AND course_id=11 AND term=8"));
        assertEquals(3, count("t_csys_training_scheme_course_schedule", "course_id IN (11,12)"));
        assertEquals(1090.0, jdbc.queryForObject("SELECT teach_hours FROM t_csys_training_scheme_course_schedule WHERE scheme_id=101 AND course_id=11", Double.class));
        assertEquals(969.0, jdbc.queryForObject("SELECT teach_hours FROM t_csys_training_scheme_course_schedule WHERE scheme_id=101 AND course_id=12", Double.class));
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void fourYearPracticalManualRowsCollapsePerSchemeWithoutLosingAllocatedHours(boolean sourceSave) {
        practicalSource();
        jdbc.execute("INSERT INTO t_csys_course (id,type,time_week,unit) VALUES (11,'2',3,'1')");
        jdbc.execute("UPDATE t_csys_course_ref_schedule SET time_week=3,unit='1' WHERE course_id=11");
        jdbc.execute("UPDATE t_csys_training_scheme_course_schedule SET hours=44,credits=2,course_attr='3',theory_hours=10,checked=0"
                + " WHERE scheme_id=101 AND course_id=11");
        jdbc.execute("INSERT INTO t_csys_training_scheme_course_schedule (scheme_id,course_id,term,teach_hours,practice_hours,checked)"
                + " VALUES (101,11,2,16,8,1),(102,11,3,4,NULL,0)");
        CourseVo request = request("2");
        CourseSchedule detail = schedule("6", "1", null, null);
        detail.setTimeWeek(3.0);
        detail.setUnit("1");
        request.setCourseScheduleList(Collections.singletonList(detail));
        if (!sourceSave) {
            CourseVo invoked = request("2");
            invoked.setId(11L);
            invoked.setSourceId(1L);
            when(courses.selectCourseById(11L)).thenReturn(invoked);
            request.setId(11L);
        }
        service.updateCourse(request);
        service.updateCourse(request);
        assertEquals(1, count("t_csys_training_scheme_course_schedule", "scheme_id=101 AND course_id=11 AND term=8"));
        assertEquals(1, count("t_csys_training_scheme_course_schedule", "scheme_id=102 AND course_id=11 AND term=8"));
        assertEquals(1015.0, jdbc.queryForObject("SELECT teach_hours FROM t_csys_training_scheme_course_schedule WHERE scheme_id=101 AND course_id=11", Double.class));
        assertEquals(8.0, jdbc.queryForObject("SELECT practice_hours FROM t_csys_training_scheme_course_schedule WHERE scheme_id=101 AND course_id=11", Double.class));
        assertEquals(892.0, jdbc.queryForObject("SELECT teach_hours FROM t_csys_training_scheme_course_schedule WHERE scheme_id=102 AND course_id=11", Double.class));
        assertEquals(1, jdbc.queryForObject("SELECT checked FROM t_csys_training_scheme_course_schedule WHERE scheme_id=101 AND course_id=11", Integer.class));
        assertEquals(44.0, jdbc.queryForObject("SELECT hours FROM t_csys_training_scheme_course_schedule WHERE scheme_id=101 AND course_id=11", Double.class));
        assertEquals(2.0, jdbc.queryForObject("SELECT credits FROM t_csys_training_scheme_course_schedule WHERE scheme_id=101 AND course_id=11", Double.class));
        assertEquals(10.0, jdbc.queryForObject("SELECT theory_hours FROM t_csys_training_scheme_course_schedule WHERE scheme_id=101 AND course_id=11", Double.class));
        TrainingSchemeCourseSchedule query = new TrainingSchemeCourseSchedule();
        query.setCourseId(11L);
        query.setSchemeId(101L);
        TrainingSchemeCourseScheduleMapper mapper = (TrainingSchemeCourseScheduleMapper)
                ReflectionTestUtils.getField(training, "trainingSchemeCourseScheduleMapper");
        List<TrainingSchemeCourseScheduleVo> response = mapper.selectTrainingSchemeCourseScheduleList(query);
        assertEquals(1, response.size());
        assertEquals("3.0", response.get(0).getTimeWeek());
        assertEquals("1", response.get(0).getUnit());
        assertEquals(1, response.get(0).getCourseScheduleList().size());
        assertEquals(3.0, response.get(0).getCourseScheduleList().get(0).getTimeWeek());
    }

    @Test
    void fourYearPracticalInvocationOnlyOverridesItsOwnConfiguredSchedules() {
        practicalSource();
        CourseVo invoked = request("2");
        invoked.setId(11L);
        invoked.setSourceId(1L);
        when(courses.selectCourseById(11L)).thenReturn(invoked);
        CourseVo request = request(null);
        request.setId(11L);
        request.setCourseScheduleList(Collections.singletonList(schedule("6", "5", null, null)));

        service.updateCourse(request);

        assertEquals(2, count("t_csys_training_scheme_course_schedule", "course_id=11 AND term=8"));
        assertEquals(1, count("t_csys_training_scheme_course_schedule", "course_id=12 AND term=6"));
        verify(courses, never()).selectCourseBySourceId(anyLong());
    }

    @Test
    void fourYearHeaderWithoutFourYearDetailsDoesNotOverrideManualTerms() {
        practicalSource();
        CourseVo request = request("2");
        request.setSemesterSchedule("6");
        request.setCourseScheduleList(Collections.singletonList(schedule("2", "1", null, null)));
        assertPracticalSchedulesUnchanged(request);
    }

    @Test
    void omittedNullAndEmptyPracticalDetailsDoNotUseStoredOrRequestedFourYearHeaders() {
        practicalSource();
        stored.setSemesterSchedule("6");
        stored.setCourseScheduleList(Collections.singletonList(schedule("6", "1", null, null)));
        CourseVo request = request("2");
        request.setSemesterSchedule("6");
        assertPracticalSchedulesUnchanged(request);
        request.setCourseScheduleList(null);
        assertPracticalSchedulesUnchanged(request);
        request.setCourseScheduleList(Collections.emptyList());
        assertPracticalSchedulesUnchanged(request);
    }

    @ParameterizedTest
    @ValueSource(strings = {"1", "2", "3", "4"})
    void fourYearInvocationOverridesItsManualTermForEveryType(String type) {
        CourseVo invoked = request(type);
        invoked.setId(11L);
        invoked.setSourceId(1L);
        when(courses.selectCourseById(11L)).thenReturn(invoked);
        CourseVo request = request(type);
        request.setId(11L);
        request.setCourseScheduleList(Collections.singletonList(schedule("6", "1", null, null)));
        service.updateCourse(request);
        assertEquals(2, count("t_csys_training_scheme_course_schedule", "course_id=11 AND term=8"));
        assertEquals(1, count("t_csys_training_scheme_course_schedule", "course_id=12 AND term=6"));
        verify(courses, never()).selectCourseBySourceId(anyLong());
    }

    @Test
    void fourYearPracticalUpdateDoesNotCreateUnconfiguredExecutionSchedules() {
        practicalSource();
        jdbc.execute("DELETE FROM t_csys_training_scheme_course_schedule WHERE course_id IN (11,12)");
        CourseVo request = request("2");
        request.setCourseScheduleList(Collections.singletonList(schedule("6", "1", null, null)));
        service.updateCourse(request);
        assertEquals(0, count("t_csys_training_scheme_course_schedule", "course_id IN (11,12)"));
    }

    @Test
    void fourYearPracticalScheduleFailureRollsBackAllTermsAndSourceDetails() {
        practicalSource();
        jdbc.execute("ALTER TABLE t_csys_training_scheme_course_schedule ADD CONSTRAINT reject_practical"
                + " CHECK (course_id <> 12 OR term=6)");
        List<Map<String, Object>> before = jdbc.queryForList(
                "SELECT * FROM t_csys_training_scheme_course_schedule ORDER BY id");
        List<Map<String, Object>> detailsBefore = jdbc.queryForList(
                "SELECT * FROM t_csys_course_ref_schedule ORDER BY id");
        CourseVo request = request("2");
        request.setCourseScheduleList(Collections.singletonList(schedule("6", "1", null, null)));
        assertThrows(RuntimeException.class, () -> service.updateCourse(request));
        assertEquals(before, jdbc.queryForList(
                "SELECT * FROM t_csys_training_scheme_course_schedule ORDER BY id"));
        assertEquals(10.0, lecture(1));
        assertEquals(detailsBefore, jdbc.queryForList("SELECT * FROM t_csys_course_ref_schedule ORDER BY id"));
    }

    private void practicalSource() {
        stored.setType("2");
        when(courses.selectCourseBySourceId(1L)).thenReturn(
                Arrays.asList(copy(11L, "2"), copy(12L, "2"), copy(13L, "4")));
    }

    @Test
    void fourYearProjectSourceUpdatesProjectCopiesButNotTrainingCopies() {
        stored.setType("4");
        when(courses.selectCourseBySourceId(1L)).thenReturn(
                Arrays.asList(copy(11L, "4"), copy(12L, "4"), copy(13L, "2")));
        CourseVo request = request("4");
        request.setCourseScheduleList(Collections.singletonList(schedule("6", "5", null, null)));
        service.updateCourse(request);
        assertEquals(3, count("t_csys_training_scheme_course_schedule", "course_id IN (11,12) AND term=8"));
        assertEquals(0, count("t_csys_training_scheme_course_schedule", "course_id=13 AND term=8"));
        assertEquals(2, count("t_csys_course_ref_schedule", "course_id IN (11,12) AND semester_schedule='6'"));
    }

    @Test
    void legacyRefreshRetainsExplicitFourYearChoice() {
        CourseServiceImpl target = new CourseServiceImpl();
        ReflectionTestUtils.setField(target, "courseMapper", courses);
        CourseScheduleMapper details = mock(CourseScheduleMapper.class);
        ReflectionTestUtils.setField(target, "courseScheduleMapper", details);
        Course course = copy(11L, "1");
        course.setCourseScheduleList(Collections.singletonList(schedule("6", "5", 16, 8)));
        when(courses.selectLegacyScheduleCourseIds()).thenReturn(Collections.singletonList(11L));
        when(courses.selectCoursesByIds(anyList())).thenReturn(Collections.singletonList(course));
        assertEquals(1, target.refreshLegacySchedule().get("skippedAlready"));
        verifyNoInteractions(details);
    }

    @Test
    void schemeStatisticsSeparateWeeksAndHoursAndExcludeUnknownUnits() {
        CurriculumServiceImpl target = new CurriculumServiceImpl();
        CourseMapper mapper = mock(CourseMapper.class);
        TrainingSchemeRefCourseMapper refs = mock(TrainingSchemeRefCourseMapper.class);
        ReflectionTestUtils.setField(target, "courseMapper", mapper);
        ReflectionTestUtils.setField(target, "trainingSchemeRefCourseMapper", refs);
        when(refs.selectTrainingSchemeRefCourseByTrainingSchemeVoId(101L))
                .thenReturn(Collections.singletonList(ref(101L, 11L)));
        List<Course> courses = new ArrayList<>();
        for (String type : Arrays.asList("2", "4")) {
            for (String unit : Arrays.asList("1", "2", "unknown", null)) {
                Course course = copy(11L, type);
                course.setTimeWeek("1".equals(unit) ? 2.5 : "2".equals(unit) ? 16.0 : 999.0);
                course.setUnit(unit);
                course.setCredit(1.0);
                course.setCourseScheduleList("1".equals(unit) ? null : Collections.emptyList());
                courses.add(course);
            }
            Course missingTime = copy(11L, type);
            missingTime.setUnit("1");
            courses.add(missingTime);
        }
        when(mapper.selectCoursesByIds(anyList())).thenReturn(courses);
        Map<String, Object> result = target.getSchemeCourseInfo(101L);
        for (String prefix : Arrays.asList("training", "practical")) {
            assertEquals(2.5, result.get(prefix + "Weeks"));
            assertEquals(16.0, result.get(prefix + "Hours"));
            assertEquals(4.0, result.get(prefix + "Credits"));
            assertEquals(5L, result.get(prefix + "Num"));
        }
    }

    private void assertPracticalSchedulesUnchanged(CourseVo request) {
        List<Map<String, Object>> before = jdbc.queryForList(
                "SELECT * FROM t_csys_training_scheme_course_schedule ORDER BY id");
        service.updateCourse(request);
        assertEquals(before, jdbc.queryForList(
                "SELECT * FROM t_csys_training_scheme_course_schedule ORDER BY id"));
        verify(courses, never()).selectCourseBySourceId(anyLong());
    }

    @ParameterizedTest
    @ValueSource(strings = {"2", "4"})
    void schemeStatisticsCountMixedUnitDetailsOnceAndIgnoreHeaderSubtotal(String type) {
        CurriculumServiceImpl target = new CurriculumServiceImpl();
        CourseMapper mapper = mock(CourseMapper.class);
        TrainingSchemeRefCourseMapper refs = mock(TrainingSchemeRefCourseMapper.class);
        ReflectionTestUtils.setField(target, "courseMapper", mapper);
        ReflectionTestUtils.setField(target, "trainingSchemeRefCourseMapper", refs);
        when(refs.selectTrainingSchemeRefCourseByTrainingSchemeVoId(101L))
                .thenReturn(Collections.singletonList(ref(101L, 11L)));
        Course course = copy(11L, type);
        course.setTimeWeek(1037.5);
        course.setUnit("1");
        course.setCredit(3.5);
        List<CourseSchedule> details = new ArrayList<>();
        for (String unit : Arrays.asList("1", "2", "unknown", null)) {
            CourseSchedule detail = schedule("6", "5", null, null);
            detail.setTimeWeek("1".equals(unit) ? 2.5 : "2".equals(unit) ? 16.0 : 999.0);
            detail.setUnit(unit);
            details.add(detail);
        }
        CourseSchedule noTime = schedule("6", "5", null, null);
        noTime.setUnit("1");
        details.add(noTime);
        course.setCourseScheduleList(details);
        when(mapper.selectCoursesByIds(anyList())).thenReturn(Collections.singletonList(course));

        String prefix = "2".equals(type) ? "training" : "practical";
        Map<String, Object> result = target.getSchemeCourseInfo(101L);
        assertEquals(2.5, result.get(prefix + "Weeks"));
        assertEquals(16.0, result.get(prefix + "Hours"));
        assertEquals(3.5, result.get(prefix + "Credits"));
        assertEquals(1L, result.get(prefix + "Num"));

        course.setCourseScheduleList(Collections.singletonList(noTime));
        result = target.getSchemeCourseInfo(101L);
        assertEquals(0.0, result.get(prefix + "Weeks"), "Present details never fall back to the header subtotal");
        assertEquals(0.0, result.get(prefix + "Hours"));
    }

    private int count(String table, String condition) {
        return jdbc.queryForObject("SELECT COUNT(*) FROM " + table + " WHERE " + condition,Integer.class);
    }

    private CourseVo request(String type) {
        CourseVo course = new CourseVo();
        course.setId(1L);
        course.setType(type);
        course.setCourseAttr("1");
        return course;
    }

    private Course copy(long id, String type) {
        Course course = new Course();
        course.setId(id);
        course.setSourceId(1L);
        course.setType(type);
        course.setHours(999.0);
        course.setCourseModuleChildren("1");
        return course;
    }

    private TrainingSchemeRefCourse ref(long scheme, long course) {
        TrainingSchemeRefCourse ref = new TrainingSchemeRefCourse();
        ref.setSchemeId(scheme);
        ref.setCourseId(course);
        return ref;
    }

    private CourseSchedule schedule(String year, String season, double lecture, double practice) {
        return schedule(year, season, Double.valueOf(lecture), Double.valueOf(practice));
    }

    private CourseSchedule schedule(String year, String season, Double lecture, Double practice) {
        CourseSchedule schedule = new CourseSchedule();
        schedule.setSemesterSchedule(year);
        schedule.setSpringAutumn(season);
        schedule.setTeachHours(lecture);
        schedule.setPracticeHours(practice);
        return schedule;
    }
}
