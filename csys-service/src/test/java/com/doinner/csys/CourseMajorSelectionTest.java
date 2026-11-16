package com.doinner.csys;

import com.doinner.csys.dao.CourseMapper;
import com.doinner.csys.dao.StandardMajorMapper;
import com.doinner.csys.domain.Course;
import com.doinner.csys.domain.StandardMajor;
import com.doinner.csys.domain.vo.CourseVo;
import com.doinner.csys.entity.csys.model.DictContent;
import com.doinner.csys.entity.csys.model.TrainingPlanModel;
import com.doinner.csys.entity.csys.model.TrainingSchemeCourseModel;
import com.doinner.csys.permission.CoursePermissionException;
import com.doinner.csys.service.CourseMajorSelectionService;
import org.apache.ibatis.builder.xml.XMLMapperBuilder;
import org.apache.ibatis.datasource.unpooled.UnpooledDataSource;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.InputStream;
import java.io.StringReader;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

class CourseMajorSelectionTest {
    private static final String NS = "com.doinner.csys.dao.CourseMapper.";

    @Test
    void onlyNewExplicitDirectionsConflictWithSiblingInvocations() {
        CourseMajorSelectionService service = new CourseMajorSelectionService(mock(StandardMajorMapper.class));
        Course stored = invocation(1L, 101L);
        Course request = invocation(1L, 101L);
        request.setInvocationSubMajorIds(Arrays.asList(101L, 102L));
        Course sibling = invocation(2L, 102L);
        assertThrows(CoursePermissionException.class, () -> ReflectionTestUtils.invokeMethod(service,
                "validateInvocationOverlap", request, stored, Collections.singletonList(sibling)));
        sibling.setInvocationSubMajorIds(Arrays.asList(102L, 103L));
        assertThrows(CoursePermissionException.class, () -> ReflectionTestUtils.invokeMethod(service,
                "validateInvocationOverlap", request, stored, Collections.singletonList(sibling)));
        stored.setInvocationSubMajorIds(Arrays.asList(101L, 102L));
        assertDoesNotThrow(() -> ReflectionTestUtils.invokeMethod(service,
                "validateInvocationOverlap", request, stored, Collections.singletonList(sibling)));
        for (boolean changeModule : Arrays.asList(false, true)) {
            if (changeModule) stored.setCourseModule("old-module");
            else stored.setCourseModuleChildren(DictContent.DUTY_POSITION);
            assertThrows(CoursePermissionException.class, () -> ReflectionTestUtils.invokeMethod(service,
                    "validateInvocationOverlap", request, stored, Collections.singletonList(sibling)),
                    "Moving an existing direction into a sibling scope introduces overlap");
            stored.setCourseModule(request.getCourseModule());
            stored.setCourseModuleChildren(request.getCourseModuleChildren());
        }
        stored.setInvocationSubMajorIds(null);
        sibling.setInvocationSubMajorIds(Collections.singletonList(103L));
        assertDoesNotThrow(() -> ReflectionTestUtils.invokeMethod(service,
                "validateInvocationOverlap", request, stored, Collections.singletonList(sibling)));
        sibling.setInvocationSubMajorIds(null);
        sibling.setSubMajorId(null);
        assertDoesNotThrow(() -> ReflectionTestUtils.invokeMethod(service,
                "validateInvocationOverlap", request, stored, Collections.singletonList(sibling)));
        sibling.setSubMajorId(102L);
        sibling.setCourseModuleChildren(DictContent.DUTY_POSITION);
        assertDoesNotThrow(() -> ReflectionTestUtils.invokeMethod(service,
                "validateInvocationOverlap", request, stored, Collections.singletonList(sibling)));
        sibling.setCourseModuleChildren(stored.getCourseModuleChildren());
        sibling.setCourseModule("other");
        assertDoesNotThrow(() -> ReflectionTestUtils.invokeMethod(service,
                "validateInvocationOverlap", request, stored, Collections.singletonList(sibling)));
        sibling.setCourseModule(stored.getCourseModule());
        sibling.setSourceId(null);
        assertDoesNotThrow(() -> ReflectionTestUtils.invokeMethod(service,
                "validateInvocationOverlap", request, stored, Collections.singletonList(sibling)));
        sibling.setSourceId(stored.getSourceId());
        sibling.setId(stored.getId());
        assertDoesNotThrow(() -> ReflectionTestUtils.invokeMethod(service,
                "validateInvocationOverlap", request, stored, Collections.singletonList(sibling)));
        sibling.setId(2L);
        sibling.setSubMajorId(101L);
        assertDoesNotThrow(() -> ReflectionTestUtils.invokeMethod(service,
                "validateInvocationOverlap", request, stored, Collections.singletonList(sibling)), "Old scalar overlap stays editable");
        stored.setSubMajorId(null);
        assertThrows(CoursePermissionException.class, () -> ReflectionTestUtils.invokeMethod(service,
                "validateInvocationOverlap", request, stored, Collections.singletonList(sibling)), "Whole-major to explicit introduces an explicit overlap");
    }

    private Course invocation(long id, Long direction) {
        Course course = new Course();
        course.setId(id);
        course.setSourceId(99L);
        course.setMajorId(10L);
        course.setCourseModule(DictContent.MILITARY_VOCATIONAL_EDUCATION);
        course.setCourseModuleChildren(DictContent.POSITION_FOUNDATION);
        course.setSubMajorId(direction);
        return course;
    }

    @Test
    void invocationDisplayNamesAreIndependentFromSourceApplicability() {
        StandardMajorMapper mapper = mock(StandardMajorMapper.class);
        when(mapper.selectStandardMajorByIds(anyList())).thenReturn(Arrays.asList(
                major(101, 10, 1, "Source"), major(102, 10, 1, "Alpha"), major(103, 10, 1, "Beta")));
        Course course = new Course();
        course.setSourceId(90L);
        course.setCourseModule(DictContent.DISCIPLINE_CORE_COURSES_SCHEDULE);
        course.setSubMajorId(999L);
        course.setSubMajorName("Old scalar");
        course.setSubMajorIds(Collections.singletonList(101L));
        course.setInvocationSubMajorIds(Arrays.asList(103L, 102L));
        new CourseMajorSelectionService(mapper).resolveNames(Collections.singletonList(course));
        assertEquals("Beta、Alpha", course.getSubMajorName());
        assertEquals(Arrays.asList("Beta", "Alpha"), ReflectionTestUtils.getField(course, "invocationSubMajorNames"));
        assertEquals(Collections.singletonList("Source"), course.getSubMajorNames());
        assertEquals(Collections.singletonList(101L), course.getSubMajorIds());
        assertEquals(999L, course.getSubMajorId());
        verify(mapper).selectStandardMajorByIds(Arrays.asList(101L, 103L, 102L));
        course.setSourceId(null);
        new CourseMajorSelectionService(mapper).resolveNames(Collections.singletonList(course));
        assertEquals("Source", course.getSubMajorName());
    }

    @Test
    void schemeListDisplaysAllInvocationNamesAfterLegacyScalarMapping() {
        com.doinner.csys.service.impl.CurriculumServiceImpl service = new com.doinner.csys.service.impl.CurriculumServiceImpl();
        CourseMapper courses = mock(CourseMapper.class);
        StandardMajorMapper majors = mock(StandardMajorMapper.class);
        com.doinner.csys.dao.TrainingSchemeCategoryMapper categories = mock(com.doinner.csys.dao.TrainingSchemeCategoryMapper.class);
        ReflectionTestUtils.setField(service, "courseMapper", courses);
        ReflectionTestUtils.setField(service, "standardMajorMapper", majors);
        ReflectionTestUtils.setField(service, "trainingSchemeCategoryMapper", categories);
        Course course = new Course();
        course.setSourceId(90L);
        course.setSubMajorId(101L);
        course.setInvocationSubMajorIds(Arrays.asList(103L, 102L));
        when(courses.selectSchemeCourseList(any())).thenReturn(Collections.singletonList(course));
        when(majors.selectStandardMajorList(null)).thenReturn(Arrays.asList(
                major(101, 10, 1, "Old"), major(102, 10, 1, "Alpha"), major(103, 10, 1, "Beta")));
        assertEquals("Beta、Alpha", service.listSchemeCourse(new CourseVo()).get(0).getSubMajorName());
    }

    @Test
    void directionSqlIncludesMajorFallbackAndPrefersExistingDirectionalCopy() {
        UnpooledDataSource db = new UnpooledDataSource("org.h2.Driver",
                "jdbc:h2:mem:" + UUID.randomUUID() + ";MODE=MySQL;DB_CLOSE_DELAY=-1", "sa", "");
        Configuration config = new Configuration(new Environment("scope", new JdbcTransactionFactory(), db));
        config.getTypeAliasRegistry().registerAliases("com.doinner.csys.domain");
        InputStream xml = getClass().getResourceAsStream("/mapper/csys/CourseMapper.xml");
        new XMLMapperBuilder(xml, config, "CourseMapper.xml", config.getSqlFragments()).parse();
        for (String mapper : Arrays.asList("TrainingSchemeMapper", "TrainingSchemeCourseScheduleMapper", "TrainingSchemeRefCourseMapper")) {
            InputStream resource = getClass().getResourceAsStream("/mapper/csys/" + mapper + ".xml");
            new XMLMapperBuilder(resource, config, mapper + ".xml", config.getSqlFragments()).parse();
        }
        Map<String, Object> schemeQuery = new HashMap<>();
        schemeQuery.put("id", 5L);
        schemeQuery.put("subMajorId", 101L);
        String schemeSql = config.getMappedStatement("com.doinner.csys.dao.TrainingSchemeMapper.selectTrainingSchemeCoursesByIdAndsubMajorId")
                .getBoundSql(schemeQuery).getSql();
        assertTrue(schemeSql.contains("scoped_ref.scheme_id = src.scheme_id"));
        com.doinner.csys.domain.TrainingSchemeCourseSchedule scheduleQuery = new com.doinner.csys.domain.TrainingSchemeCourseSchedule();
        scheduleQuery.setSubMajorId(101L);
        String scheduleSql = config.getMappedStatement("com.doinner.csys.dao.TrainingSchemeCourseScheduleMapper.selectTrainingSchemeCourseScheduleList")
                .getBoundSql(scheduleQuery).getSql();
        assertTrue(scheduleSql.contains("scoped_ref.scheme_id = tcs.scheme_id"));
        CourseVo courseQuery = new CourseVo();
        courseQuery.setSubMajorId(101L);
        String courseSql = config.getMappedStatement(NS + "selectSchemeCourseList").getBoundSql(courseQuery).getSql();
        assertTrue(courseSql.contains("scoped_ref.scheme_id = schemeRefCourse.scheme_id"));
        String query = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>"
                + "<!DOCTYPE mapper PUBLIC \"-//mybatis.org//DTD Mapper 3.0//EN\" \"http://mybatis.org/dtd/mybatis-3-mapper.dtd\">"
                + "<mapper namespace=\"scope\"><select id=\"list\" resultType=\"long\">"
                + "select c.id from t_csys_course c join t_csys_training_scheme_ref_course r on r.course_id=c.id where r.scheme_id=#{schemeId} "
                + "<include refid=\"com.doinner.csys.dao.CourseMapper.invokedDirectionFilter\">"
                + "<property name=\"directionParameter\" value=\"directionId\"/>"
                + "<property name=\"schemeExpression\" value=\"r.scheme_id\"/></include> order by c.id</select></mapper>";
        new XMLMapperBuilder(new StringReader(query), config, "scope.xml", config.getSqlFragments()).parse();
        JdbcTemplate jdbc = new JdbcTemplate(db);
        jdbc.execute("create table t_csys_course (id bigint, source_id bigint, major_Id bigint, sub_Major_Id bigint, invocation_sub_major_ids varchar(4000), type varchar(10), course_Module varchar(50), course_Module_Children varchar(50), location varchar(50), program_Level varchar(50))");
        jdbc.execute("create table t_csys_training_scheme_ref_course (scheme_id bigint, course_id bigint)");
        jdbc.execute("create table t_csys_std_major (id bigint, parent_id bigint)");
        jdbc.execute("insert into t_csys_std_major values (101,10),(102,10),(201,20)");
        jdbc.execute("insert into t_csys_course (id,source_id,major_Id,sub_Major_Id,type) values (1,90,10,null,'2'),(2,90,10,101,'2'),(3,91,10,null,'1'),(4,90,10,102,'2')");
        jdbc.execute("insert into t_csys_training_scheme_ref_course values (5,1),(5,2),(5,3),(6,4)");
        Map<String, Object> params = new HashMap<>();
        params.put("schemeId", 5L);
        try (SqlSession session = new SqlSessionFactoryBuilder().build(config).openSession(true)) {
            params.put("directionId", 101L);
            assertEquals(Arrays.asList(2L, 3L), session.selectList("scope.list", params));
            params.put("directionId", 102L);
            assertEquals(Arrays.asList(1L, 3L), session.selectList("scope.list", params), "Other schemes must not shadow a major-wide copy");
            jdbc.execute("insert into t_csys_std_major values (103,10)");
            params.put("directionId", 103L);
            assertEquals(Arrays.asList(1L, 3L), session.selectList("scope.list", params));
            params.put("directionId", 201L);
            assertEquals(Collections.singletonList(3L), session.selectList("scope.list", params));
            jdbc.execute("update t_csys_course set invocation_sub_major_ids='102,103' where id=2");
            session.clearCache();
            params.put("directionId", 101L);
            assertEquals(Arrays.asList(1L, 3L), session.selectList("scope.list", params));
            params.put("directionId", 102L);
            assertEquals(Arrays.asList(2L, 3L), session.selectList("scope.list", params));
            params.put("directionId", 103L);
            assertEquals(Arrays.asList(2L, 3L), session.selectList("scope.list", params));
        }
    }

    @Test
    void actualMapperInsertsReadsUpdatesAndClearsArraysWithoutTruncation() throws Exception {
        UnpooledDataSource db = new UnpooledDataSource("org.h2.Driver",
                "jdbc:h2:mem:" + UUID.randomUUID() + ";MODE=MySQL;DB_CLOSE_DELAY=-1", "sa", "");
        Configuration config = new Configuration(new Environment("major", new JdbcTransactionFactory(), db));
        try (InputStream xml = getClass().getResourceAsStream("/mapper/csys/CourseMapper.xml")) {
            assertNotNull(xml);
            new XMLMapperBuilder(xml, config, "CourseMapper.xml", config.getSqlFragments()).parse();
        }
        String sql = config.getMappedStatement(NS + "selectCourseById").getBoundSql(1L).getSql();
        String columns = sql.substring(sql.toLowerCase().indexOf("select") + 6, sql.toLowerCase().indexOf("from"));
        List<String> definitions = new ArrayList<>();
        for (String column : columns.split(",")) {
            column = column.trim();
            assertTrue(column.matches("[A-Za-z_]+"));
            definitions.add(column.equals("id") ? "id BIGINT AUTO_INCREMENT PRIMARY KEY" : column + " VARCHAR(4000)");
        }
        JdbcTemplate jdbc = new JdbcTemplate(db);
        jdbc.execute("CREATE TABLE t_csys_course (" + String.join(",", definitions) + ")");
        try (SqlSession session = new SqlSessionFactoryBuilder().build(config).openSession(true)) {
            CourseMapper mapper = session.getMapper(CourseMapper.class);
            CourseVo input = new CourseVo();
            input.setName("Course");
            input.setMajorIds(Arrays.asList(10L, 20L));
            input.setSubMajorIds(Arrays.asList(101L, 202L));
            input.setInvocationSubMajorIds(Arrays.asList(102L, 103L));
            mapper.insertCourse(input);
            assertNotNull(input.getId());
            CourseVo saved = mapper.selectCourseById(input.getId());
            assertEquals(input.getMajorIds(), saved.getMajorIds());
            assertEquals(input.getSubMajorIds(), saved.getSubMajorIds());
            assertEquals(input.getInvocationSubMajorIds(), saved.getInvocationSubMajorIds());
            input.setInvocationSubMajorIds(Collections.singletonList(103L));
            mapper.updateCourse(input);
            assertEquals(Collections.singletonList(103L), mapper.selectCourseById(input.getId()).getInvocationSubMajorIds());
            input.setInvocationSubMajorIds(null);
            mapper.updateCourse(input);
            assertEquals(Collections.singletonList(103L), mapper.selectCourseById(input.getId()).getInvocationSubMajorIds());
            input.setSubMajorIds(Collections.singletonList(202L));
            mapper.updateCourse(input);
            assertEquals(Collections.singletonList(202L), mapper.selectCourseById(input.getId()).getSubMajorIds());
            input.setMajorIds(Collections.emptyList());
            input.setSubMajorIds(Collections.emptyList());
            mapper.updateCourse(input);
            saved = mapper.selectCourseById(input.getId());
            assertEquals(Collections.emptyList(), saved.getMajorIds());
            assertEquals(Collections.emptyList(), saved.getSubMajorIds());
            assertNull(saved.getMajorId());
            assertNull(saved.getSubMajorId());
            jdbc.update("UPDATE t_csys_course SET source_id='99', major_Id='10', sub_Major_Id='101' WHERE id=?", input.getId());
            input.setMajorIds(Arrays.asList(10L, 20L));
            input.setSubMajorIds(Arrays.asList(101L, 202L));
            mapper.updateCourse(input);
            saved = mapper.selectCourseById(input.getId());
            assertEquals(10L, saved.getMajorId(), "Multi-select editing must not remove the invoked tree scope");
            assertEquals(101L, saved.getSubMajorId());
            assertEquals(input.getSubMajorIds(), saved.getSubMajorIds());
            jdbc.execute("CREATE TABLE t_csys_course_ref_schedule (id bigint, course_id bigint, semester_schedule varchar(10), spring_autumn varchar(10), teach_hours double, practice_hours double, time_week double, unit varchar(10))");
            jdbc.execute("CREATE TABLE t_csys_training_scheme_ref_course (scheme_id bigint, course_id bigint)");
            jdbc.execute("CREATE TABLE t_csys_std_major (id bigint, parent_id bigint)");
            jdbc.execute("CREATE TABLE sys_dept (dept_id bigint, dept_name varchar(100))");
            jdbc.execute("INSERT INTO t_csys_std_major VALUES (101,10),(102,10),(103,10)");
            jdbc.update("INSERT INTO t_csys_training_scheme_ref_course VALUES (5,?)", input.getId());
            jdbc.update("UPDATE t_csys_course SET sysflag=0, type='1', invocation_sub_major_ids='102,103' WHERE id=?", input.getId());
            session.clearCache();
            CourseVo filter = new CourseVo();
            filter.setSchemeId(5L);
            for (Long direction : Arrays.asList(102L, 103L)) {
                filter.setSubMajorId(direction);
                List<Course> matches = mapper.selectSchemeCourseList(filter);
                assertEquals(1, matches.size());
                assertEquals(input.getId(), matches.get(0).getId());
                assertEquals(Arrays.asList(102L, 103L), matches.get(0).getInvocationSubMajorIds());
            }
            filter.setSubMajorId(101L);
            assertTrue(mapper.selectSchemeCourseList(filter).isEmpty(), "Actual scheme list must ignore legacy scalar and source applicability when explicit");
            filter.setSubMajorId(null);
            assertEquals(1, mapper.selectSchemeCourseList(filter).size());
            jdbc.update("UPDATE t_csys_course SET invocation_sub_major_ids=null, course_Module=?, course_Module_Children=? WHERE id=?",
                    DictContent.MILITARY_VOCATIONAL_EDUCATION, DictContent.POSITION_FOUNDATION, input.getId());
            CourseVo sibling = new CourseVo();
            org.springframework.beans.BeanUtils.copyProperties(invocation(200L, 102L), sibling);
            sibling.setSysflag(0);
            mapper.insertCourse(sibling);
            jdbc.update("UPDATE t_csys_course SET sysflag=0 WHERE id=?", sibling.getId());
            session.clearCache();
            assertEquals(2, mapper.selectCoursesByIds(Arrays.asList(input.getId(), sibling.getId())).size());
            com.doinner.csys.service.impl.CurriculumServiceImpl service = new com.doinner.csys.service.impl.CurriculumServiceImpl();
            for (java.lang.reflect.Field field : service.getClass().getDeclaredFields()) {
                if (field.getType().isInterface()) ReflectionTestUtils.setField(service, field.getName(), mock(field.getType()));
            }
            ReflectionTestUtils.setField(service, "courseMapper", mapper);
            ReflectionTestUtils.setField(service, "coursePermissions", mock(com.doinner.csys.permission.CoursePermissionService.class));
            StandardMajorMapper majorMapper = mock(StandardMajorMapper.class);
            when(majorMapper.selectStandardMajorByIds(anyList())).thenReturn(Arrays.asList(major(101,10,1,"A"), major(102,10,1,"B")));
            ReflectionTestUtils.setField(service, "courseMajorSelectionService", new CourseMajorSelectionService(majorMapper));
            com.doinner.csys.dao.TrainingSchemeMapper schemes = (com.doinner.csys.dao.TrainingSchemeMapper) ReflectionTestUtils.getField(service, "trainingSchemeMapper");
            com.doinner.csys.domain.vo.TrainingSchemeVo scheme = new com.doinner.csys.domain.vo.TrainingSchemeVo();
            scheme.setId(5L);
            scheme.setMajorId(10L);
            when(schemes.selectTrainingSchemeById(5L)).thenReturn(scheme);
            com.doinner.csys.dao.TrainingSchemeRefCourseMapper refs = (com.doinner.csys.dao.TrainingSchemeRefCourseMapper) ReflectionTestUtils.getField(service, "trainingSchemeRefCourseMapper");
            com.doinner.csys.domain.TrainingSchemeRefCourse currentRef = new com.doinner.csys.domain.TrainingSchemeRefCourse();
            currentRef.setSchemeId(5L);
            currentRef.setCourseId(input.getId());
            com.doinner.csys.domain.TrainingSchemeRefCourse siblingRef = new com.doinner.csys.domain.TrainingSchemeRefCourse();
            siblingRef.setSchemeId(5L);
            siblingRef.setCourseId(sibling.getId());
            when(refs.selectTrainingSchemeRefCourseList(any())).thenReturn(Collections.singletonList(currentRef));
            when(refs.selectTrainingSchemeRefCourseByTrainingSchemeVoId(5L)).thenReturn(Arrays.asList(currentRef, siblingRef));
            CourseVo edit = new CourseVo();
            edit.setId(input.getId());
            edit.setCourseModuleChildren(DictContent.POSITION_FOUNDATION);
            edit.setInvocationSubMajorIds(Arrays.asList(101L, 102L));
            edit.setName("Must not persist");
            assertThrows(CoursePermissionException.class, () -> service.updateCourse(edit));
            assertThrows(CoursePermissionException.class, () -> service.saveCourse(edit));
            com.doinner.csys.domain.vo.TrainingSchemeVo secondScheme = new com.doinner.csys.domain.vo.TrainingSchemeVo();
            secondScheme.setId(6L);
            secondScheme.setMajorId(10L);
            when(schemes.selectTrainingSchemeById(6L)).thenReturn(secondScheme);
            com.doinner.csys.domain.TrainingSchemeRefCourse secondRef = new com.doinner.csys.domain.TrainingSchemeRefCourse();
            secondRef.setSchemeId(6L);
            secondRef.setCourseId(input.getId());
            when(refs.selectTrainingSchemeRefCourseList(any())).thenReturn(Arrays.asList(currentRef, secondRef));
            when(refs.selectTrainingSchemeRefCourseByTrainingSchemeVoId(5L)).thenReturn(Collections.singletonList(currentRef));
            when(refs.selectTrainingSchemeRefCourseByTrainingSchemeVoId(6L)).thenReturn(Arrays.asList(secondRef, siblingRef));
            assertThrows(CoursePermissionException.class, () -> service.updateCourse(edit), "Every bound scheme must be checked");
            edit.setInvocationSubMajorIds(Collections.singletonList(101L));
            assertDoesNotThrow(() -> ReflectionTestUtils.invokeMethod(service, "validateInvocationSelection", edit,
                    mapper.selectCourseById(input.getId())));
            session.clearCache();
            CourseVo unchanged = mapper.selectCourseById(input.getId());
            assertNull(unchanged.getInvocationSubMajorIds());
            assertEquals(101L, unchanged.getSubMajorId());
            assertEquals("Course", unchanged.getName());
            assertEquals(2, jdbc.queryForObject("SELECT COUNT(*) FROM t_csys_course", Integer.class));
        }
        Map<String, Object> params = Collections.singletonMap("courseList", Collections.singletonList(new Course()));
        String copySql = config.getMappedStatement(NS + "insertCourseList").getBoundSql(params).getSql();
        assertTrue(copySql.contains("major_ids, sub_major_ids"), "Invocation copies must preserve both arrays");
        for (String query : Arrays.asList("selectCourseList", "selectSchemeCourseList", "selectCoursesByIds", "selectCoursesByIdsWithSort",
                "selectCourseAndRelevanceById", "selectCourseAndRelevanceByIds")) {
            Object argument = query.endsWith("ByIds") || query.endsWith("WithSort")
                    ? Collections.singletonMap("ids", Collections.singletonList(1L)) : new CourseVo();
            if (query.endsWith("ById")) argument = 1L;
            String querySql = config.getMappedStatement(NS + query).getBoundSql(argument).getSql();
            assertTrue(querySql.contains("major_ids"), query);
            assertTrue(querySql.contains("sub_major_ids"), query);
            assertTrue(querySql.contains("invocation_sub_major_ids"), query);
        }
    }

    @Test
    void validatesDirectionUnionAndAllowsNoSelectedCategory() {
        StandardMajorMapper mapper = mock(StandardMajorMapper.class);
        when(mapper.selectStandardMajorByIds(anyList())).thenReturn(Arrays.asList(
                major(10, 0, 1, "Major A"), major(20, 0, 1, "Major B"),
                major(101, 10, 1, "Direction A"), major(202, 20, 1, "Direction B")));
        CourseMajorSelectionService service = new CourseMajorSelectionService(mapper);
        Course course = new Course();
        course.setMajorIds(Arrays.asList(10L, 20L));
        course.setSubMajorIds(Arrays.asList(101L, 202L));
        assertDoesNotThrow(() -> service.validate(course));
        course.setMajorIds(Collections.singletonList(10L));
        assertThrows(CoursePermissionException.class, () -> service.validate(course));
        course.setMajorIds(Collections.emptyList());
        assertDoesNotThrow(() -> service.validate(course));
        course.setSubMajorIds(Collections.singletonList(999L));
        assertThrows(CoursePermissionException.class, () -> service.validate(course));
        course.setSubMajorIds(Collections.singletonList(-1L));
        assertThrows(CoursePermissionException.class, () -> service.validate(course));
    }

    @Test
    void validatesInvocationAgainstStoredCourseAndSchemeDirections() {
        StandardMajorMapper mapper = mock(StandardMajorMapper.class);
        when(mapper.selectStandardMajorByIds(anyList())).thenReturn(Arrays.asList(
                major(101, 10, 1, "A"), major(102, 10, 1, "B"), major(201, 20, 1, "Other")));
        CourseMajorSelectionService service = new CourseMajorSelectionService(mapper);
        Course stored = new Course();
        stored.setSourceId(90L);
        stored.setMajorId(10L);
        stored.setCourseModule(DictContent.MILITARY_VOCATIONAL_EDUCATION);
        stored.setCourseModuleChildren(DictContent.POSITION_FOUNDATION);
        com.doinner.csys.domain.vo.TrainingSchemeVo scheme = new com.doinner.csys.domain.vo.TrainingSchemeVo();
        scheme.setMajorId(10L);
        scheme.setSubMajorIds(Arrays.asList(101L, 102L));
        Course request = new Course();
        request.setSubMajorId(999L);
        request.setInvocationSubMajorIds(Arrays.asList(101L, 102L));
        assertDoesNotThrow(() -> service.validateInvocation(request, stored, Collections.singletonList(scheme)));
        stored.setInvocationSubMajorIds(Arrays.asList(101L, 102L));
        request.setCourseModule("other");
        assertDoesNotThrow(() -> service.validateInvocation(request, stored, Collections.singletonList(scheme)));
        request.setInvocationSubMajorIds(Collections.singletonList(101L));
        assertThrows(CoursePermissionException.class, () -> service.validateInvocation(request, stored, Collections.singletonList(scheme)));
        request.setCourseModule(null);
        request.setInvocationSubMajorIds(Arrays.asList(101L, 102L));
        scheme.setSubMajorIds(Collections.singletonList(101L));
        assertThrows(CoursePermissionException.class, () -> service.validateInvocation(request, stored, Collections.singletonList(scheme)));
        scheme.setSubMajorIds(Collections.emptyList());
        assertDoesNotThrow(() -> service.validateInvocation(request, stored, Collections.singletonList(scheme)));
        for (List<Long> invalid : Arrays.asList(Collections.<Long>emptyList(), Collections.singletonList(201L),
                Collections.singletonList(999L), Collections.singletonList(-1L), Collections.<Long>singletonList(null))) {
            request.setInvocationSubMajorIds(invalid);
            assertThrows(CoursePermissionException.class, () -> service.validateInvocation(request, stored, Collections.singletonList(scheme)));
        }
        request.setInvocationSubMajorIds(Collections.singletonList(101L));
        assertThrows(CoursePermissionException.class, () -> service.validateInvocation(request, stored, Collections.emptyList()));
        stored.setSourceId(null);
        assertThrows(CoursePermissionException.class, () -> service.validateInvocation(request, stored, Collections.singletonList(scheme)));
        request.setInvocationSubMajorIds(null);
        assertDoesNotThrow(() -> service.validateInvocation(request, stored, Collections.emptyList()));
    }

    @Test
    void singleSelectClearedArraysDoNotEraseScalarLabels() {
        Course course = new Course();
        course.setCourseModule(DictContent.MAJOR_COURSES_SCHEDULE);
        course.setCourseAttr("1");
        course.setMajorId(10L);
        course.setSubMajorId(101L);
        course.setMajorName("Major A");
        course.setSubMajorName("Direction A");
        course.setMajorIds(Collections.emptyList());
        course.setSubMajorIds(Collections.emptyList());
        CourseMajorSelectionService.applyNames(course, Collections.singletonMap(101L, "Direction A"));
        assertEquals("Major A", course.getMajorName());
        assertEquals("Direction A", course.getSubMajorName());
        assertEquals(Collections.singletonList("Direction A"), course.getSubMajorNames());
    }

    @Test
    void exportAppendsAllDirectionNamesInSelectionOrderWithoutRenamingCourse() {
        StandardMajorMapper mapper = mock(StandardMajorMapper.class);
        when(mapper.selectStandardMajorByIds(anyList())).thenReturn(Arrays.asList(
                major(101, 10, 1, "Direction A"), major(202, 20, 1, "Direction B")));
        Course course = new Course();
        course.setName("Course");
        course.setCourseModule(DictContent.DISCIPLINE_CORE_COURSES_SCHEDULE);
        course.setSubMajorIds(Arrays.asList(202L, 101L));
        new CourseMajorSelectionService(mapper).resolveNames(Collections.singletonList(course));
        List<TrainingSchemeCourseModel> rows = new ArrayList<>();
        ReflectionTestUtils.invokeMethod(new TrainingPlanModel(), "setCourse", rows, course, Collections.emptyList());
        assertEquals("Course（Direction B、Direction A）", rows.get(0).getName());
        assertEquals("Course", course.getName());
        rows.clear();
        course.setCourseModule(DictContent.MAJOR_COURSES_SCHEDULE);
        ReflectionTestUtils.invokeMethod(new TrainingPlanModel(), "setCourse", rows, course, Collections.emptyList());
        assertEquals("Course", rows.get(0).getName());
    }

    private StandardMajor major(long id, long parent, long category, String name) {
        StandardMajor major = new StandardMajor();
        major.setId(id);
        major.setParentId(parent);
        major.setCategoryId(category);
        major.setName(name);
        return major;
    }
}
