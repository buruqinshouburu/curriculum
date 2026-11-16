package com.doinner.csys;

import com.doinner.csys.dao.CourseMapper;
import com.doinner.csys.dao.StandardMajorMapper;
import com.doinner.csys.dao.TrainingSchemeMapper;
import com.doinner.csys.domain.Course;
import com.doinner.csys.domain.StandardMajor;
import com.doinner.csys.domain.vo.TrainingSchemeCourseVo;
import com.doinner.csys.domain.vo.TrainingSchemeCourseScheduleVo;
import com.doinner.csys.domain.vo.TrainingSchemeVo;
import com.doinner.csys.entity.csys.model.DictContent;
import com.doinner.csys.entity.csys.model.TrainingPlanModel;
import com.doinner.csys.entity.csys.model.TrainingSchemeCourseModel;
import com.doinner.csys.entity.csys.TrainingPlanGenerator;
import com.doinner.csys.entity.csys.FiveYearTrainingPlanGenerator;
import com.doinner.csys.entity.csys.NcoTrainingPlanGenerator;
import com.doinner.csys.service.impl.TrainingServiceImpl;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.ByteArrayInputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

class TrainingWholeMajorExportTest {
    @Test
    void explicitNcoDirectionsKeepManualScheduleNarrowAndTotalsSingle() {
        Course course = course(1, "1", 999L);
        course.setCourseModule(DictContent.MILITARY_VOCATIONAL_EDUCATION);
        course.setCourseModuleChildren(DictContent.POSITION_FOUNDATION);
        course.setInvocationSubMajorIds(Arrays.asList(501L, 502L));
        course.setHours(32.0);
        course.setCredit(2.0);
        TrainingSchemeCourseScheduleVo manual = new TrainingSchemeCourseScheduleVo();
        manual.setCourseId(1L);
        manual.setSubMajorId(501L);
        manual.setTerm(2);
        manual.setTeachHours(12.0);
        TrainingPlanModel model = new TrainingPlanModel(Collections.emptyList(), Collections.emptyList(),
                Collections.emptyList(), Collections.singletonList(course), Collections.singletonList(manual), scheme(501L, 502L, 999L));
        assertEquals(Arrays.asList(501L, 502L), model.getNcoCourses().stream()
                .map(TrainingSchemeCourseModel::getSubmajorId).collect(Collectors.toList()));
        assertEquals(12.0, model.getNcoCourses().get(0).getTermHoursMap().get(2));
        assertTrue(model.getNcoCourses().get(1).getTermHoursMap().isEmpty());
        model.getNcoCourses().forEach(row -> { row.setCourseModeChildrenName("Position"); row.setChildrenModelSort(1); });
        model.setDurationAndCredits();
        assertEquals(32.0, model.getDurationAndCredits().getTotalHour());
        assertEquals(2.0, model.getDurationAndCredits().getTotalCredit());
        assertEquals(999L, course.getSubMajorId());
    }

    @Test
    void xlsxExplicitDirectionsIgnoreScalarAndDoNotDuplicateRows() throws Exception {
        TrainingServiceImpl service = new TrainingServiceImpl();
        TrainingSchemeMapper schemes = mock(TrainingSchemeMapper.class);
        CourseMapper courses = mock(CourseMapper.class);
        StandardMajorMapper majors = mock(StandardMajorMapper.class);
        ReflectionTestUtils.setField(service, "trainingSchemeMapper", schemes);
        ReflectionTestUtils.setField(service, "courseMapper", courses);
        ReflectionTestUtils.setField(service, "standardMajorMapper", majors);
        when(schemes.selectTrainingSchemeById(9L)).thenReturn(scheme(501L, 502L, 999L));
        TrainingSchemeCourseVo bound = new TrainingSchemeCourseVo();
        bound.setId(1L);
        when(schemes.selectTrainingSchemeCoursesById(9L)).thenReturn(Collections.singletonList(bound));
        Course explicit = course(1, "1", 999L);
        explicit.setCourseModule(DictContent.MILITARY_VOCATIONAL_EDUCATION);
        explicit.setCourseModuleChildren(DictContent.DUTY_POSITION);
        explicit.setInvocationSubMajorIds(Arrays.asList(501L, 502L));
        when(courses.selectCoursesByIds(anyList())).thenReturn(Collections.singletonList(explicit));
        for (Long id : Arrays.asList(501L, 502L, 999L)) {
            StandardMajor direction = new StandardMajor();
            direction.setName("Direction" + id);
            when(majors.selectStandardMajorById(id)).thenReturn(direction);
            MockHttpServletResponse response = new MockHttpServletResponse();
            service.exportTrainingCourse(response, 9L, id);
            try (XSSFWorkbook workbook = new XSSFWorkbook(new ByteArrayInputStream(response.getContentAsByteArray()))) {
                int matches = 0;
                for (org.apache.poi.ss.usermodel.Row row : workbook.getSheetAt(0)) {
                    for (org.apache.poi.ss.usermodel.Cell cell : row) if ("course-1".equals(cell.toString())) matches++;
                }
                assertEquals(id.equals(999L) ? 0 : 1, matches);
            }
        }
    }

    private Course course(long id, String type, Long direction) {
        Course course = new Course();
        course.setId(id);
        course.setSourceId(100L);
        course.setTemplateType(2);
        course.setType(type);
        course.setCourseModule(DictContent.MAJOR_COURSES_SCHEDULE);
        course.setCourseModuleChildren("child");
        course.setMajorId(50L);
        course.setSubMajorId(direction);
        course.setName("course-" + id);
        course.setCourseAttr("1");
        return course;
    }

    private TrainingSchemeVo scheme(Long... directions) {
        TrainingSchemeVo scheme = new TrainingSchemeVo();
        scheme.setMajorId(50L);
        scheme.setSubMajorIds(Arrays.asList(directions));
        return scheme;
    }

    private TrainingPlanModel model(List<Course> courses, TrainingSchemeVo scheme) {
        TrainingSchemeCourseScheduleVo schedule = new TrainingSchemeCourseScheduleVo();
        schedule.setCourseId(1L);
        schedule.setTerm(2);
        schedule.setTeachHours(12.0);
        return new TrainingPlanModel(Collections.emptyList(), Collections.emptyList(),
                Collections.emptyList(), courses, Collections.singletonList(schedule), scheme);
    }

    private List<TrainingSchemeCourseModel> rows(TrainingPlanModel model, String type) {
        if ("2".equals(type)) return model.getTrainingSubjectCourses();
        if ("4".equals(type)) return model.getPracticalProjectCourse();
        return model.getMajorCourses();
    }

    private String tableText(XWPFTable table) {
        return table.getRows().stream().flatMap(row -> row.getTableCells().stream())
                .map(cell -> cell.getText()).collect(Collectors.joining("\n"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"1", "2", "3", "4"})
    void wordExpandsWholeMajorAndUsesExactOverrideForOnlyItsDirection(String type) {
        Course whole = course(1, type, null);
        Course override = course(2, type, 501L);
        Course otherMajor = course(3, type, null);
        otherMajor.setMajorId(60L);
        List<Course> courses = Arrays.asList(whole, override, otherMajor);
        List<TrainingSchemeCourseModel> rows = rows(model(courses, scheme(501L, 502L)), type);
        assertEquals(Arrays.asList("course-2", "course-1"),
                rows.stream().map(TrainingSchemeCourseModel::getName).collect(Collectors.toList()));
        assertEquals(Arrays.asList(501L, 502L),
                rows.stream().map(TrainingSchemeCourseModel::getSubmajorId).collect(Collectors.toList()));
        assertEquals(12.0, rows.get(1).getTermHoursMap().get(2));
        assertNull(whole.getSubMajorId(), "Export must not mutate the bound invocation");
        List<TrainingSchemeCourseModel> withNewDirection = rows(model(courses, scheme(501L, 502L, 503L)), type);
        assertEquals(3, withNewDirection.size());
        assertEquals("course-1", withNewDirection.get(2).getName());
        assertEquals(503L, withNewDirection.get(2).getSubmajorId());
    }

    @Test
    void academicBasicDirectionLabelsAreNotExpandedOrLost() {
        Course basic = course(1, "1", null);
        basic.setCourseModule(DictContent.DISCIPLINE_CORE_COURSES_SCHEDULE);
        basic.setSubMajorNames(Arrays.asList("Alpha", "Beta"));
        List<TrainingSchemeCourseModel> rows = model(Collections.singletonList(basic), scheme(501L, 502L)).getDisciplineCourses();
        assertEquals(1, rows.size());
        assertEquals("course-1\uff08Alpha\u3001Beta\uff09", rows.get(0).getName());
    }

    @ParameterizedTest
    @ValueSource(strings = {"1", "2", "3", "4"})
    void schemeTotalsCountWholeMajorOnceRegardlessOfDirectionCount(String type) {
        Course whole = course(1, type, null);
        whole.setHours(32.0);
        whole.setCredit(2.0);
        whole.setTimeWeek(1.0);
        for (TrainingSchemeVo scheme : Arrays.asList(scheme(501L, 502L), scheme(501L, 502L, 503L))) {
            TrainingPlanModel model = model(Collections.singletonList(whole), scheme);
            model.setDurationAndCredits();
            assertEquals(scheme.getSubMajorIds().size(), rows(model, type).size());
            assertEquals(32.0, model.getDurationAndCredits().getTotalHour());
            assertEquals(2.0, model.getDurationAndCredits().getTotalCredit());
            assertEquals(1.0, model.getDurationAndCredits().getProjectTime());
            model.setDurationAndCredits();
            assertEquals(32.0, model.getDurationAndCredits().getTotalHour());
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {DictContent.POSITION_FOUNDATION, DictContent.DUTY_POSITION})
    void ncoSchemeTotalsCountWholeMajorOnce(String child) {
        Course whole = course(1, "1", null);
        whole.setCourseModule(DictContent.MILITARY_VOCATIONAL_EDUCATION);
        whole.setCourseModuleChildren(child);
        whole.setHours(32.0);
        whole.setCredit(2.0);
        for (TrainingSchemeVo scheme : Arrays.asList(scheme(501L, 502L), scheme(501L, 502L, 503L))) {
            TrainingPlanModel model = model(Collections.singletonList(whole), scheme);
            model.getNcoCourses().forEach(row -> {
                row.setCourseModeChildrenName("Position");
                row.setChildrenModelSort(1);
            });
            model.setDurationAndCredits();
            assertEquals(scheme.getSubMajorIds().size(), model.getNcoCourses().size());
            assertEquals(32.0, model.getDurationAndCredits().getTotalHour());
            assertEquals(2.0, model.getDurationAndCredits().getTotalCredit());
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {"1", "2", "3", "4"})
    void schemeTotalsKeepDistinctInvocationIdsEvenWithSameSource(String type) {
        Course whole = course(1, type, null);
        whole.setHours(32.0);
        whole.setCredit(2.0);
        Course override = course(2, type, 501L);
        override.setHours(16.0);
        override.setCredit(1.0);
        override.setCourseAttr("2");
        for (TrainingSchemeVo scheme : Arrays.asList(scheme(501L, 502L), scheme(501L, 502L, 503L))) {
            TrainingPlanModel model = model(Arrays.asList(whole, override), scheme);
            model.setDurationAndCredits();
            assertEquals(48.0, model.getDurationAndCredits().getTotalHour());
            assertEquals(3.0, model.getDurationAndCredits().getTotalCredit());
            assertEquals(32.0, model.getDurationAndCredits().getRequiredHour());
            assertEquals(16.0, model.getDurationAndCredits().getOptionalHour());
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {"2", "4"})
    void practiceOverridesKeepDistinctTrainingModulesAndProjectLevels(String type) {
        Course whole = course(1, type, null);
        Course override = course(2, type, 501L);
        Course anotherGroup = course(3, type, null);
        if ("2".equals(type)) {
            whole.setLocation("A");
            override.setLocation("A");
            anotherGroup.setLocation("B");
        } else {
            whole.setProgramLevel("A");
            override.setProgramLevel("A");
            anotherGroup.setProgramLevel("B");
        }
        List<TrainingSchemeCourseModel> rows = rows(model(Arrays.asList(whole, override, anotherGroup),
                scheme(501L, 502L)), type);
        assertEquals(Arrays.asList("course-2", "course-3", "course-1", "course-3"),
                rows.stream().map(TrainingSchemeCourseModel::getName).collect(Collectors.toList()));
    }

    @Test
    void trainingWordTableLabelsEachExpandedDirection() throws Exception {
        List<TrainingSchemeCourseModel> rows = model(Arrays.asList(course(1, "2", null), course(2, "2", 501L)),
                scheme(501L, 502L)).getTrainingSubjectCourses();
        rows.get(0).setSubMajorName("Alpha");
        rows.get(1).setSubMajorName("Beta");
        try (XWPFDocument document = new XWPFDocument()) {
            XWPFTable table = ReflectionTestUtils.invokeMethod(new TrainingPlanGenerator(), "practicalProjectCourseTable",
                    document, "Training", rows);
            assertNotNull(table);
            assertTrue(tableText(table).contains("Alpha"));
            assertTrue(tableText(table).contains("Beta"));
            assertEquals(3, table.getNumberOfRows());
        }
    }

    @Test
    void fourAndFiveYearMajorTablesAndPracticeTableHaveNoDuplicateDirectionRows() throws Exception {
        for (TrainingPlanGenerator generator : Arrays.asList(new TrainingPlanGenerator(), new FiveYearTrainingPlanGenerator())) {
            for (String type : Arrays.asList("1", "4")) {
                List<TrainingSchemeCourseModel> rows = rows(model(Arrays.asList(course(1, type, null), course(2, type, 501L)),
                        scheme(501L, 502L)), type);
                rows.get(0).setSubMajorName("Alpha");
                rows.get(1).setSubMajorName("Beta");
                try (XWPFDocument document = new XWPFDocument()) {
                    XWPFTable table = ReflectionTestUtils.invokeMethod(generator,
                            "4".equals(type) ? "trainingSubjectCoursesTable" : "majorCoursesTable", document, "Courses", rows);
                    assertNotNull(table);
                    assertTrue(tableText(table).contains("Alpha"));
                    assertTrue(tableText(table).contains("Beta"));
                    assertEquals(1, tableText(table).split("course-1", -1).length - 1);
                    assertEquals(1, tableText(table).split("course-2", -1).length - 1);
                }
            }
        }
    }

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void ncoSeparatesDirectionTablesAndIncludesPracticeProjects(boolean explicit) throws Exception {
        Course whole = course(1, "1", null);
        Course override = course(2, "1", 501L);
        if (explicit) {
            override.setSubMajorId(999L);
            override.setInvocationSubMajorIds(Arrays.asList(501L, 502L));
        }
        for (Course course : Arrays.asList(whole, override)) {
            course.setCourseModule(DictContent.MILITARY_VOCATIONAL_EDUCATION);
            course.setCourseModuleChildren(DictContent.POSITION_FOUNDATION);
        }
        TrainingPlanModel model = model(Arrays.asList(whole, override, course(3, "4", null)), scheme(501L, 502L));
        for (List<TrainingSchemeCourseModel> rows : Arrays.asList(model.getNcoCourses(), model.getPracticalProjectCourse())) {
            assertEquals(2, rows.size());
            rows.get(0).setSubMajorName("Alpha");
            rows.get(1).setSubMajorName("Beta");
        }
        try (XWPFDocument document = new XWPFDocument()) {
            ReflectionTestUtils.invokeMethod(new NcoTrainingPlanGenerator(), "generateCourseArrangements", model, document);
            assertEquals(3, document.getTables().size());
            assertTrue(tableText(document.getTables().get(0)).contains("course-2"));
            assertFalse(tableText(document.getTables().get(0)).contains("course-1"));
            assertTrue(tableText(document.getTables().get(1)).contains(explicit ? "course-2" : "course-1"));
            assertTrue(tableText(document.getTables().get(2)).contains("course-3"));
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {"1", "2", "3", "4"})
    void xlsxUsesValueEqualityAndDoesNotExportOverriddenWholeMajor(String type) throws Exception {
        TrainingServiceImpl service = new TrainingServiceImpl();
        TrainingSchemeMapper schemes = mock(TrainingSchemeMapper.class);
        CourseMapper courses = mock(CourseMapper.class);
        StandardMajorMapper majors = mock(StandardMajorMapper.class);
        ReflectionTestUtils.setField(service, "trainingSchemeMapper", schemes);
        ReflectionTestUtils.setField(service, "courseMapper", courses);
        ReflectionTestUtils.setField(service, "standardMajorMapper", majors);
        when(schemes.selectTrainingSchemeById(9L)).thenReturn(scheme(501L, 502L));
        TrainingSchemeCourseVo bound = new TrainingSchemeCourseVo();
        bound.setId(1L);
        when(schemes.selectTrainingSchemeCoursesById(9L)).thenReturn(Collections.singletonList(bound));
        StandardMajor direction = new StandardMajor();
        direction.setName("Alpha");
        when(majors.selectStandardMajorById(501L)).thenReturn(direction);
        when(majors.selectStandardMajorById(503L)).thenReturn(direction);
        when(courses.selectCoursesByIds(anyList())).thenReturn(Arrays.asList(
                course(1, type, null), course(2, type, Long.valueOf("501")), course(3, type, 502L)));
        MockHttpServletResponse response = new MockHttpServletResponse();
        service.exportTrainingCourse(response, 9L, Long.valueOf("501"));
        try (XSSFWorkbook workbook = new XSSFWorkbook(new ByteArrayInputStream(response.getContentAsByteArray()))) {
            StringBuilder text = new StringBuilder();
            workbook.getSheetAt(0).forEach(row -> row.forEach(cell -> text.append(cell.toString()).append('\n')));
            assertTrue(text.toString().contains("course-2"));
            assertFalse(text.toString().contains("course-1"));
            assertFalse(text.toString().contains("course-3"));
        }
        MockHttpServletResponse newDirection = new MockHttpServletResponse();
        service.exportTrainingCourse(newDirection, 9L, 503L);
        try (XSSFWorkbook workbook = new XSSFWorkbook(new ByteArrayInputStream(newDirection.getContentAsByteArray()))) {
            StringBuilder text = new StringBuilder();
            workbook.getSheetAt(0).forEach(row -> row.forEach(cell -> text.append(cell.toString()).append('\n')));
            assertTrue(text.toString().contains("course-1"));
            assertFalse(text.toString().contains("course-2"));
            assertFalse(text.toString().contains("course-3"));
        }
    }
}
