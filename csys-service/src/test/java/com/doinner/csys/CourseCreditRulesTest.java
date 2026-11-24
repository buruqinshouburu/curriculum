package com.doinner.csys;

import com.doinner.common.core.context.SecurityContextHolder;
import com.doinner.common.core.domain.DataSet;
import com.doinner.common.core.utils.poi.ExcelUtil;
import com.doinner.csys.dao.CourseMapper;
import com.doinner.csys.dao.CourseScheduleMapper;
import com.doinner.csys.domain.Course;
import com.doinner.csys.domain.CourseSchedule;
import com.doinner.csys.domain.vo.CourseExcelVo;
import com.doinner.csys.domain.vo.CourseVo;
import com.doinner.csys.domain.vo.PracticeExcelVo;
import com.doinner.csys.domain.vo.TrainingExcelVo;
import com.doinner.csys.permission.CoursePermissionException;
import com.doinner.csys.permission.CoursePermissionService;
import com.doinner.csys.service.impl.CurriculumServiceImpl;
import com.doinner.system.service.DoinnerDeptService;
import com.doinner.system.service.DoinnerDictDataService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/** Real service calculations and Excel conversion, with persistence/remote boundaries mocked. */
class CourseCreditRulesTest {
    private CurriculumServiceImpl service;
    private CourseMapper courses;
    private CourseScheduleMapper schedules;
    private CoursePermissionService permissions;

    @BeforeEach
    void setup() {
        SecurityContextHolder.setUserId("77");
        SecurityContextHolder.setUserName("credit.writer");
        service = new CurriculumServiceImpl();
        ReflectionTestUtils.setField(service, "courseImportAuthorVerifier",
                mock(com.doinner.csys.service.CourseImportAuthorVerifier.class));
        for (Field field : CurriculumServiceImpl.class.getDeclaredFields()) {
            if (field.getType().isInterface())
                ReflectionTestUtils.setField(service, field.getName(), mock(field.getType()));
        }
        permissions = mock(CoursePermissionService.class);
        ReflectionTestUtils.setField(service, "courseMajorSelectionService",
                mock(com.doinner.csys.service.CourseMajorSelectionService.class));
        ReflectionTestUtils.setField(service, "coursePermissions", permissions);
        courses = (CourseMapper) ReflectionTestUtils.getField(service, "courseMapper");
        schedules = (CourseScheduleMapper) ReflectionTestUtils.getField(service, "courseScheduleMapper");
        AtomicLong keys = new AtomicLong(100);
        when(courses.insertCourse(any(Course.class))).thenAnswer(call -> {
            Course course = call.getArgument(0);
            course.setId(keys.incrementAndGet());
            return 1;
        });
        DoinnerDeptService departments = (DoinnerDeptService) ReflectionTestUtils.getField(service, "doinnerDeptService");
        DoinnerDictDataService dictionaries = (DoinnerDictDataService) ReflectionTestUtils.getField(service, "doinnerDictDataService");
        when(departments.list(any())).thenReturn(DataSet.success(Collections.emptyList()));
        when(dictionaries.dictType(anyString())).thenReturn(DataSet.success(Collections.emptyList()));
    }

    @AfterEach
    void cleanup() { SecurityContextHolder.remove(); }

    @ParameterizedTest
    @ValueSource(strings = {"1", "3"})
    void courseTotalsComeFromAllLectureAndPracticeSchedules(String type) {
        CourseVo course = course(type);
        course.setCourseScheduleList(Arrays.asList(hours(12, 4), hours(16, 8)));

        service.insertCourse(course);

        assertAll(
                () -> assertEquals(28.0, course.getTeachHours()),
                () -> assertEquals(12.0, course.getPracticeHours()),
                () -> assertEquals(40.0, course.getHours()),
                () -> assertEquals(2.5, course.getCredit()));
        verify(schedules, times(2)).insert(any(CourseSchedule.class));
    }

    @ParameterizedTest
    @CsvSource({"54,3", "56,3.5", "58,4"})
    void legacyTopLevelHoursUseTheSameRounding(double total, double expected) {
        CourseVo course = course("1");
        course.setTeachHours(total - 8);
        course.setPracticeHours(8.0);

        service.insertCourse(course);

        assertEquals(total, course.getHours());
        assertEquals(expected, course.getCredit());
    }

    @ParameterizedTest
    @ValueSource(strings = {"1", "3"})
    void optionalCourseUsesOnlyFirstScheduleAndKeepsEverySchedule(String type) {
        CourseVo course = course(type);
        course.setCourseAttr("3");
        List<CourseSchedule> rows = Arrays.asList(hours(40, 16), hours(160, 16));
        course.setCourseScheduleList(rows);

        service.insertCourse(course);

        assertAll(
                () -> assertEquals(40.0, course.getTeachHours()),
                () -> assertEquals(16.0, course.getPracticeHours()),
                () -> assertEquals(56.0, course.getHours()),
                () -> assertEquals(3.5, course.getCredit()),
                () -> assertSame(rows, course.getCourseScheduleList()),
                () -> assertEquals(160.0, rows.get(1).getTeachHours()));
        verify(schedules).insert(rows.get(0));
        verify(schedules).insert(rows.get(1));
    }

    @ParameterizedTest
    @CsvSource({"2,3.4,3", "2,3.5,3.5", "2,3.6,4", "4,3.4,3", "4,3.5,3.5", "4,3.6,4"})
    void fractionalWeeksUseTheApprovedRounding(String type, double weeks, double expected) {
        CourseVo course = course(type);
        course.setTimeWeek(weeks);
        course.setUnit("1");

        service.insertCourse(course);

        assertEquals(weeks, course.getTimeWeek());
        assertEquals(expected, course.getCredit());
    }

    @ParameterizedTest
    @ValueSource(strings = {"2", "4"})
    void sumsRawWeeksBeforeRoundingEvenWhenOptional(String type) {
        CourseVo course = course(type);
        course.setCourseAttr("3");
        course.setCourseScheduleList(Arrays.asList(time(1.2, "1"), time(1.3, "1")));

        service.insertCourse(course);

        assertEquals(2.5, course.getTimeWeek());
        assertEquals(2.5, course.getCredit());
        verify(schedules, times(2)).insert(any(CourseSchedule.class));
    }

    @ParameterizedTest
    @ValueSource(strings = {"2", "4"})
    void mixedUnitsConvertEachRowBeforeRoundingAndKeepRawTimeSubtotal(String type) {
        CourseVo course = course(type);
        course.setUnit("2");
        course.setTimeWeek(17.5);
        course.setCourseScheduleList(Arrays.asList(time(16, "2"), time(1.5, "1")));

        service.insertCourse(course);

        assertEquals(17.5, course.getTimeWeek());
        assertEquals(2.5, course.getCredit());
        assertEquals("2", course.getUnit());
        assertEquals("1", course.getCourseScheduleList().get(1).getUnit());
    }

    @ParameterizedTest
    @ValueSource(strings = {"teachHours", "practiceHours"})
    void topLevelHourFieldsRejectFractions(String field) {
        CourseVo course = course("1");
        ReflectionTestUtils.setField(course, field, 3.5);

        RuntimeException failure = assertThrows(RuntimeException.class, () -> service.insertCourse(course));

        assertTrue(failure.getMessage().contains("非负整数"));
        verify(courses, never()).insertCourse(any(Course.class));
    }

    @ParameterizedTest
    @ValueSource(doubles = {-1, 0.5, Double.NaN, Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY})
    void validatesEveryOptionalScheduleIncludingRowsExcludedFromTotals(double invalid) {
        CourseVo course = course("1");
        course.setCourseAttr("3");
        course.setCourseScheduleList(Arrays.asList(hours(40, 16), hours(16, invalid)));

        RuntimeException failure = assertThrows(RuntimeException.class, () -> service.insertCourse(course));

        assertTrue(failure.getMessage().contains("2"));
        assertTrue(failure.getMessage().contains("非负整数"));
        verify(courses, never()).insertCourse(any(Course.class));
    }

    @ParameterizedTest
    @ValueSource(strings = {"2", "4"})
    void timeInHoursMustBeIntegerForBothTopLevelAndScheduleInputs(String type) {
        CourseVo course = course(type);
        course.setTimeWeek(1.5);
        course.setUnit("2");
        assertThrows(RuntimeException.class, () -> service.insertCourse(course));
        course.setTimeWeek(null);
        course.setCourseScheduleList(Collections.singletonList(time(1.5, "2")));
        assertThrows(RuntimeException.class, () -> service.insertCourse(course));
        verify(courses, never()).insertCourse(any(Course.class));
    }

    @Test
    void invalidTimeUnitDoesNotSilentlyBecomeWeeks() {
        CourseVo course = course("2");
        course.setTimeWeek(16.0);
        course.setUnit("unexpected");
        assertThrows(RuntimeException.class, () -> service.insertCourse(course));
        verify(courses, never()).insertCourse(any(Course.class));
    }

    @Test
    void sourceUpdateNormalizesUsingStoredTypeAndOptionalAttributeWhenOmitted() {
        CourseVo stored = course("1");
        stored.setId(1L);
        stored.setCourseAttr("3");
        when(courses.selectCourseById(1L)).thenReturn(stored);
        CourseVo request = course(null);
        request.setId(1L);
        request.setCourseScheduleList(Arrays.asList(hours(40, 16), hours(160, 16)));

        service.updateCourse(request);

        assertEquals(56.0, request.getHours());
        assertEquals(3.5, request.getCredit());
        verify(permissions).preserveCourseOwners(request);
        verify(permissions).validateCourseContent(request);
        verify(courses).updateCourse(request);
    }

    @Test
    void libraryNormalizationDoesNotAddChangesToExistingInvocationPropagation() {
        CourseVo stored = course("1");
        stored.setId(1L);
        when(courses.selectCourseById(1L)).thenReturn(stored);
        CourseVo request = course("1");
        request.setId(1L);
        request.setTeachHours(40.0);
        request.setPracticeHours(16.0);
        double[] propagatedCredit = new double[1];
        Double[] propagatedHours = new Double[1];
        doAnswer(call -> {
            CourseVo propagated = call.getArgument(0);
            propagatedCredit[0] = propagated.getCredit();
            propagatedHours[0] = propagated.getHours();
            return null;
        }).when(courses).updateInvokeCourse(any(CourseVo.class));

        service.updateCourse(request);

        assertEquals(3.5, request.getCredit());
        assertEquals(56.0, request.getHours());
        assertEquals(99.0, propagatedCredit[0]);
        assertNull(propagatedHours[0]);
    }

    @Test
    void fullSaveNormalizesWhileSparseSaveLeavesCreditAndHoursUntouched() {
        CourseVo request = course("2");
        request.setId(1L);
        request.setCourseScheduleList(Arrays.asList(time(1.2, "1"), time(1.3, "1")));
        service.saveCourse(request);
        assertEquals(2.5, request.getCredit());
        CourseVo metadata = course("1");
        metadata.setId(2L);
        service.saveCourse(metadata);
        assertEquals(99.0, metadata.getCredit());
        assertNull(metadata.getHours());
        assertNull(metadata.getTeachHours());
    }

    @Test
    void ownershipAndSourceChecksRunBeforeCreditValidationOrPersistence() {
        CourseVo request = course("1");
        request.setId(1L);
        request.setSourceId(42L);
        request.setTeachHours(1.5);
        CoursePermissionException forbidden = new CoursePermissionException(400, "source trust check");
        doThrow(forbidden).when(permissions).preserveCourseOwners(request);
        assertSame(forbidden, assertThrows(CoursePermissionException.class, () -> service.updateCourse(request)));
        assertSame(forbidden, assertThrows(CoursePermissionException.class, () -> service.saveCourse(request)));
        verifyNoInteractions(courses, schedules);
    }

    @Test
    void invokedCreateUpdateAndSaveKeepTheirCreditWithoutApplyingLibraryValidation() {
        CourseVo created = course("1");
        created.setSourceId(42L);
        created.setCourseScheduleList(Collections.singletonList(hours(3.5, 0)));
        service.insertCourse(created);
        assertEquals(99.0, created.getCredit());
        CourseVo stored = course("1");
        stored.setId(1L);
        stored.setSourceId(42L);
        when(courses.selectCourseById(1L)).thenReturn(stored);
        CourseVo request = course("1");
        request.setId(1L);
        request.setCourseScheduleList(Collections.singletonList(hours(3.5, 0)));
        doAnswer(call -> { request.setSourceId(42L); return null; })
                .when(permissions).preserveCourseOwners(request);
        service.updateCourse(request);
        service.saveCourse(request);
        assertEquals(99.0, request.getCredit());
        assertNull(request.getHours());
    }

    @ParameterizedTest
    @CsvSource({"54,3", "56,3.5", "58,4"})
    void realCourseExcelImportUsesValidatedHoursAndCreditRounding(double total, double expected) {
        CourseExcelVo input = new CourseExcelVo();
        input.setName("Imported Course");
        input.setTeachHours(total - 8);
        input.setPracticeHours(8.0);
        assertEquals(200, service.importCourse(excel(CourseExcelVo.class, input), null, null, null,
                "1", 1, "2026", null, null).getCode());
        Course saved = inserted();
        assertEquals(total, saved.getHours());
        assertEquals(expected, saved.getCredit());
        ArgumentCaptor<CourseSchedule> row = ArgumentCaptor.forClass(CourseSchedule.class);
        verify(schedules).insert(row.capture());
        assertEquals(total - 8, row.getValue().getTeachHours());
    }

    @ParameterizedTest
    @CsvSource({"2,2,56,3.5", "4,2,58,4", "2,1,3.4,3", "4,1,3.5,3.5"})
    void realTrainingAndPracticeExcelImportsRespectUnits(String type, String unit, double time, double expected) {
        if ("2".equals(type)) {
            TrainingExcelVo input = new TrainingExcelVo();
            input.setName("Imported Training"); input.setUnit(unit); input.setTimeWeek(time);
            assertEquals(200, service.importTraining(excel(TrainingExcelVo.class, input), null, type, 1, "2026").getCode());
        } else {
            PracticeExcelVo input = new PracticeExcelVo();
            input.setName("Imported Practice"); input.setUnit(unit); input.setTimeWeek(time);
            assertEquals(200, service.importPractice(excel(PracticeExcelVo.class, input), null, type, 1,
                    "2026", null, null).getCode());
        }
        assertEquals(expected, inserted().getCredit());
        ArgumentCaptor<CourseSchedule> row = ArgumentCaptor.forClass(CourseSchedule.class);
        verify(schedules).insert(row.capture());
        assertEquals(unit, row.getValue().getUnit());
        assertEquals(time, row.getValue().getTimeWeek());
    }

    @ParameterizedTest
    @ValueSource(strings = {"1", "2", "4"})
    void everyNumericExcelImportRejectsFractionalHoursBeforeInserting(String type) {
        assertThrows(RuntimeException.class, () -> {
            if ("1".equals(type)) {
                CourseExcelVo input = new CourseExcelVo();
                input.setName("Invalid Course"); input.setTeachHours(1.5);
                service.importCourse(excel(CourseExcelVo.class, input), null, null, null,
                        type, 1, "2026", null, null);
            } else if ("2".equals(type)) {
                TrainingExcelVo input = new TrainingExcelVo();
                input.setName("Invalid Training"); input.setTimeWeek(1.5); input.setUnit("2");
                service.importTraining(excel(TrainingExcelVo.class, input), null, type, 1, "2026");
            } else {
                PracticeExcelVo input = new PracticeExcelVo();
                input.setName("Invalid Practice"); input.setTimeWeek(1.5); input.setUnit("2");
                service.importPractice(excel(PracticeExcelVo.class, input), null, type, 1, "2026", null, null);
            }
        });
        verify(courses, never()).insertCourse(any(Course.class));
        verifyNoInteractions(schedules);
    }

    @Test
    void metadataOnlyBatchImportDoesNotInventCreditsOrSchedules() {
        Course input = new Course();
        input.setName("Metadata"); input.setCode("unique"); input.setType("1");
        service.insertCourse(Collections.singletonList(input));
        assertNull(input.getCredit());
        assertNull(input.getHours());
        verify(courses).insertCourses(Collections.singletonList(input));
        verifyNoInteractions(schedules);
    }

    private Course inserted() {
        ArgumentCaptor<Course> saved = ArgumentCaptor.forClass(Course.class);
        verify(courses).insertCourse(saved.capture());
        return saved.getValue();
    }

    private static CourseVo course(String type) {
        CourseVo course = new CourseVo();
        course.setName("Course"); course.setType(type); course.setCredit(99.0);
        return course;
    }

    private static CourseSchedule hours(double lecture, double practice) {
        CourseSchedule row = new CourseSchedule();
        row.setTeachHours(lecture); row.setPracticeHours(practice);
        return row;
    }

    private static CourseSchedule time(double value, String unit) {
        CourseSchedule row = new CourseSchedule();
        row.setTimeWeek(value); row.setUnit(unit);
        return row;
    }

    private static <T> MockMultipartFile excel(Class<T> type, T row) {
        MockHttpServletResponse response = new MockHttpServletResponse();
        new ExcelUtil<>(type).exportExcel(response, Collections.singletonList(row), "credits");
        return new MockMultipartFile("file", "credits.xlsx", "application/octet-stream", response.getContentAsByteArray());
    }
}
