package com.doinner.csys;

import com.doinner.common.core.context.SecurityContextHolder;
import com.doinner.csys.dao.TrainingSchemeCourseScheduleMapper;
import com.doinner.csys.domain.Course;
import com.doinner.csys.domain.CourseSchedule;
import com.doinner.csys.domain.TrainingSchemeCourseSchedule;
import com.doinner.csys.domain.vo.TrainingSchemeCourseScheduleVo;
import com.doinner.csys.domain.vo.TrainingSchemeVo;
import com.doinner.csys.service.impl.TrainingServiceImpl;
import org.apache.ibatis.builder.xml.XMLMapperBuilder;
import org.apache.ibatis.datasource.unpooled.UnpooledDataSource;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

class TrainingScheduleContractTest {
    @AfterEach
    void clearContext() {
        SecurityContextHolder.remove();
    }

    @Test
    void mapperReturnsOrderedCurrentPlanDetailsWithoutDroppingSameTermRecordsOrJoiningSourceDetails() throws Exception {
        try (SqlSession session = scheduleSession()) {
            TrainingSchemeCourseSchedule query = new TrainingSchemeCourseSchedule();
            query.setSchemeId(7601L);
            List<TrainingSchemeCourseScheduleVo> rows = session.getMapper(TrainingSchemeCourseScheduleMapper.class)
                    .selectTrainingSchemeCourseScheduleList(query);
            assertEquals(2, rows.size(), "One result per saved schedule ID, not per course/term or detail join");
            assertEquals(1, rows.get(0).getTerm());
            assertEquals(1, rows.get(1).getTerm());
            assertNotEquals(rows.get(0).getId(), rows.get(1).getId());
            assertEquals(16.0, rows.get(0).getTeachHours());
            assertEquals(8.0, rows.get(1).getTeachHours());
            for (TrainingSchemeCourseScheduleVo row : rows) {
                List<CourseSchedule> details = details(row);
                assertEquals(2, details.size());
                assertEquals(201L, details.get(0).getId(), "Current course details retain stable ID ordering");
                assertEquals(70031L, details.get(0).getCourseId());
                assertEquals("1", details.get(0).getUnit());
                assertEquals(1.0, details.get(0).getTimeWeek());
                assertEquals(202L, details.get(1).getId());
                assertEquals("2", details.get(1).getUnit());
                assertEquals(24.0, details.get(1).getTimeWeek());
                assertEquals("3", details.get(1).getSpringAutumn());
            }
        }
    }

    @Test
    void savedScheduleEditsRemainIndependentOfCurrentCourseDetailsOnSingleRead() throws Exception {
        try (SqlSession session = scheduleSession()) {
            TrainingSchemeCourseScheduleMapper mapper = session.getMapper(TrainingSchemeCourseScheduleMapper.class);
            TrainingSchemeCourseSchedule edit = new TrainingSchemeCourseSchedule();
            edit.setId(101L);
            edit.setTeachHours(32.0);
            edit.setPracticeHours(8.0);
            assertEquals(1, mapper.updateTrainingSchemeCourseSchedule(edit));
            TrainingSchemeCourseSchedule row = mapper.selectTrainingSchemeCourseScheduleById(101L);
            assertEquals(70031L, row.getCourseId());
            assertEquals(40.0, row.getTeachHours() + row.getPracticeHours());
            assertTrue(row instanceof TrainingSchemeCourseScheduleVo);
            List<CourseSchedule> details = details((TrainingSchemeCourseScheduleVo) row);
            assertEquals(2, details.size());
            assertEquals(16.0, details.get(0).getTeachHours(), "Editing saved hours must not rewrite course details");
            assertEquals(8.0, details.get(1).getTeachHours());
        }
    }

    @Test
    void secondYearAutumnGeneratesTermThreeWithSavedLectureAndPracticeHours() {
        List<TrainingSchemeCourseSchedule> rows = generate(schedule("2", "1", 16.0, 8.0));
        assertEquals(1, rows.size());
        assertEquals(3, rows.get(0).getTerm());
        assertEquals(16.0, rows.get(0).getTeachHours());
        assertEquals(8.0, rows.get(0).getPracticeHours());
        assertEquals(999.0, rows.get(0).getHours(), "hours is whole-course metadata, not term duration");
    }

    @Test
    void bothSeasonsGenerateTwoFullRecordsWithoutSplittingHours() {
        List<TrainingSchemeCourseSchedule> rows = generate(schedule("2", "5", 16.0, 8.0));
        assertEquals(2, rows.size());
        assertEquals(3, rows.get(0).getTerm());
        assertEquals(4, rows.get(1).getTerm());
        for (TrainingSchemeCourseSchedule row : rows) {
            assertEquals(16.0, row.getTeachHours());
            assertEquals(8.0, row.getPracticeHours());
        }
    }

    @Test
    void autumnAndWinterRemainSeparateRecordsEvenWhenTheirTermIsTheSame() {
        List<TrainingSchemeCourseSchedule> rows = generate(
                schedule("2", "1", 16.0, 0.0), schedule("2", "3", 8.0, 0.0));
        assertEquals(2, rows.size());
        assertEquals(3, rows.get(0).getTerm());
        assertEquals(3, rows.get(1).getTerm());
        assertEquals(24.0, rows.stream().mapToDouble(TrainingSchemeCourseSchedule::getTeachHours).sum());
    }

    @Test
    void practicalDurationIsNotStoredAsTermHoursByGeneration() {
        CourseSchedule detail = schedule("2", "5", null, null);
        detail.setTimeWeek(2.0);
        detail.setUnit("1");
        List<TrainingSchemeCourseSchedule> rows = generate(detail);
        assertEquals(2, rows.size());
        assertNull(rows.get(0).getTeachHours());
        assertNull(rows.get(0).getPracticeHours());
        assertNull(rows.get(1).getPracticeHours());
    }

    @SuppressWarnings("unchecked")
    private List<CourseSchedule> details(TrainingSchemeCourseScheduleVo row) {
        BeanWrapper properties = new BeanWrapperImpl(row);
        assertTrue(properties.isReadableProperty("courseScheduleList"),
                "Schedule VO must expose current plan course details instead of source-course totals");
        return (List<CourseSchedule>) properties.getPropertyValue("courseScheduleList");
    }

    private CourseSchedule schedule(String year, String season, Double lecture, Double practice) {
        CourseSchedule row = new CourseSchedule();
        row.setSemesterSchedule(year);
        row.setSpringAutumn(season);
        row.setTeachHours(lecture);
        row.setPracticeHours(practice);
        return row;
    }

    @SuppressWarnings("unchecked")
    private List<TrainingSchemeCourseSchedule> generate(CourseSchedule... details) {
        SecurityContextHolder.setUserId("77");
        SecurityContextHolder.setUserName("schedule.test");
        TrainingServiceImpl service = new TrainingServiceImpl();
        TrainingSchemeCourseScheduleMapper mapper = mock(TrainingSchemeCourseScheduleMapper.class);
        ReflectionTestUtils.setField(service, "trainingSchemeCourseScheduleMapper", mapper);
        List<TrainingSchemeCourseSchedule> result = new java.util.ArrayList<>();
        when(mapper.insertTrainingSchemeCourseSchedules(anyList())).thenAnswer(call -> {
            result.addAll((List<TrainingSchemeCourseSchedule>) call.getArgument(0));
            return result.size();
        });
        Course course = new Course();
        course.setId(70031L);
        course.setCourseModuleChildren("1");
        course.setHours(999.0);
        course.setCourseScheduleList(Arrays.asList(details));
        TrainingSchemeVo scheme = new TrainingSchemeVo();
        scheme.setId(7601L);
        service.setCourseSchedule(scheme, Collections.singletonList(course));
        return result;
    }

    private SqlSession scheduleSession() throws Exception {
        UnpooledDataSource source = new UnpooledDataSource("org.h2.Driver",
                "jdbc:h2:mem:" + UUID.randomUUID() + ";MODE=MySQL;DB_CLOSE_DELAY=-1", "sa", "");
        JdbcTemplate jdbc = new JdbcTemplate(source);
        jdbc.execute("CREATE TABLE t_csys_course (id BIGINT PRIMARY KEY, name VARCHAR(100),"
                + "before_course_id VARCHAR(100), after_course_id VARCHAR(100), course_attr VARCHAR(10),"
                + "type VARCHAR(10), time_week DOUBLE, unit VARCHAR(10), source_id BIGINT,"
                + "academic_terms_number INT, semester_schedule VARCHAR(10))");
        jdbc.execute("CREATE TABLE t_csys_training_scheme_course_schedule (id BIGINT PRIMARY KEY,"
                + "scheme_id BIGINT, course_id BIGINT, type VARCHAR(10), term INT, hours DOUBLE,"
                + "teach_hours DOUBLE, practice_hours DOUBLE, checked INT)");
        jdbc.execute("CREATE TABLE t_csys_course_ref_schedule (id BIGINT PRIMARY KEY, course_id BIGINT,"
                + "semester_schedule VARCHAR(10), spring_autumn VARCHAR(10), teach_hours DOUBLE,"
                + "practice_hours DOUBLE, time_week DOUBLE, unit VARCHAR(10))");
        jdbc.execute("INSERT INTO t_csys_course VALUES (70031,'Current',NULL,NULL,'3','1',25,'1',7003,2,'1')");
        jdbc.execute("INSERT INTO t_csys_course_ref_schedule VALUES (202,70031,'1','3',8,0,24,'2'),"
                + "(201,70031,'1','1',16,0,1,'1'),(301,7003,'1','1',999,999,999,'2')");
        jdbc.execute("INSERT INTO t_csys_training_scheme_course_schedule VALUES"
                + "(101,7601,70031,'1',1,999,16,0,1),(102,7601,70031,'1',1,999,8,0,1)");
        Configuration configuration = new Configuration(new Environment("schedule-test", new JdbcTransactionFactory(), source));
        try (InputStream xml = getClass().getResourceAsStream("/mapper/csys/TrainingSchemeCourseScheduleMapper.xml")) {
            assertNotNull(xml);
            new XMLMapperBuilder(xml, configuration, "TrainingSchemeCourseScheduleMapper.xml", configuration.getSqlFragments()).parse();
        }
        return new SqlSessionFactoryBuilder().build(configuration).openSession(true);
    }
}
