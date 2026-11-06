package com.doinner.csys;

import com.doinner.csys.dao.CourseMapper;
import com.doinner.csys.dao.CourseInvokeDeleteLogMapper;
import com.doinner.csys.dao.CourseRefKnowledgeUnitMapper;
import com.doinner.csys.dao.TrainingSchemeCourseScheduleMapper;
import com.doinner.csys.dao.TrainingSchemeMapper;
import com.doinner.csys.dao.TrainingSchemeRefCourseMapper;
import com.doinner.csys.domain.Course;
import com.doinner.csys.domain.TrainingSchemeRefCourse;
import com.doinner.csys.domain.vo.TrainingSchemeVo;
import com.doinner.csys.permission.CoursePermissionService;
import com.doinner.csys.service.impl.CourseServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

class CourseDeletionDiagnosisTest {
    private CourseServiceImpl service;
    private CourseMapper courses;
    private TrainingSchemeRefCourseMapper references;
    private TrainingSchemeMapper schemes;
    private CourseRefKnowledgeUnitMapper knowledge;
    private CourseInvokeDeleteLogMapper deletionLogs;
    private TrainingSchemeCourseScheduleMapper schedules;

    @BeforeEach
    void setup() {
        service = new CourseServiceImpl();
        courses = mock(CourseMapper.class);
        references = mock(TrainingSchemeRefCourseMapper.class);
        schemes = mock(TrainingSchemeMapper.class);
        knowledge = mock(CourseRefKnowledgeUnitMapper.class);
        deletionLogs = mock(CourseInvokeDeleteLogMapper.class);
        schedules = mock(TrainingSchemeCourseScheduleMapper.class);
        ReflectionTestUtils.setField(service, "courseMapper", courses);
        ReflectionTestUtils.setField(service, "trainingSchemeRefCourseMapper", references);
        ReflectionTestUtils.setField(service, "trainingSchemeMapper", schemes);
        ReflectionTestUtils.setField(service, "courseRefKnowledgeUnitMapper", knowledge);
        ReflectionTestUtils.setField(service, "courseInvokeDeleteLogMapper", deletionLogs);
        ReflectionTestUtils.setField(service, "trainingSchemeCourseScheduleMapper", schedules);
        ReflectionTestUtils.setField(service, "coursePermissions", mock(CoursePermissionService.class));
        when(courses.selectCoursesByIds(anyList())).thenReturn(Collections.singletonList(course(1L, "Source Course")));
    }

    @Test
    void invokedSourceBlocksWholeBatchAndNamesEveryReferencingProgram() {
        when(courses.selectCoursesByIds(Arrays.asList(2L, 1L)))
                .thenReturn(Arrays.asList(course(2L, "Other Course"), course(1L, "Source Course")));
        when(courses.selectCourseBySourceId(1L))
                .thenReturn(Arrays.asList(course(3L, "Invoked A"), course(5L, "Invoked B")));
        when(references.selectByCourseIds(Arrays.asList(3L, 5L)))
                .thenReturn(Arrays.asList(reference(3L, 501L), reference(5L, 502L), reference(5L, 501L)));
        when(schemes.selectTrainingSchemeByIds(Arrays.asList(501L, 502L)))
                .thenReturn(Arrays.asList(scheme(501L, "Program A"), scheme(502L, "Program B")));

        RuntimeException failure = assertThrows(RuntimeException.class,
                () -> service.removeStorageCourse(Arrays.asList(2L, 1L)));

        assertAll(
                () -> assertTrue(failure.getMessage().contains("Source Course")),
                () -> assertTrue(failure.getMessage().contains("ID：1）")),
                () -> assertTrue(failure.getMessage().contains("Program A")),
                () -> assertTrue(failure.getMessage().contains("ID：501")),
                () -> assertTrue(failure.getMessage().contains("Program B")),
                () -> assertTrue(failure.getMessage().contains("ID：502")),
                () -> assertTrue(failure.getMessage().contains("调用课程ID：3、5")),
                () -> assertTrue(failure.getMessage().contains("请先移除调用课程"))
        );
        verify(courses, never()).deleteCourseByIds(anyList());
        verifyNoInteractions(knowledge);
    }

    @Test
    void invokedCourseWithoutProgramReferenceStillBlocksAndIdentifiesTheCopy() {
        when(courses.selectCourseBySourceId(1L))
                .thenReturn(Collections.singletonList(course(3L, "Unbound Copy")));

        RuntimeException failure = assertThrows(RuntimeException.class,
                () -> service.removeStorageCourse(Collections.singletonList(1L)));

        assertTrue(failure.getMessage().contains("未关联培养方案"));
        assertTrue(failure.getMessage().contains("调用课程ID：3"));
        verifyNoInteractions(schemes, knowledge);
        verify(courses, never()).deleteCourseByIds(anyList());
    }

    @Test
    void missingProgramRowKeepsItsActualReferenceIdAndFallsBackToAvailableName() {
        when(courses.selectCourseBySourceId(1L))
                .thenReturn(Collections.singletonList(course(3L, "Invoked Course")));
        when(references.selectByCourseIds(Collections.singletonList(3L)))
                .thenReturn(Arrays.asList(reference(3L, 501L), reference(3L, 502L)));
        TrainingSchemeVo fallback = new TrainingSchemeVo();
        fallback.setId(501L);
        fallback.setName("Fallback Program");
        when(schemes.selectTrainingSchemeByIds(Arrays.asList(501L, 502L)))
                .thenReturn(Collections.singletonList(fallback));

        RuntimeException failure = assertThrows(RuntimeException.class,
                () -> service.removeStorageCourse(Collections.singletonList(1L)));

        assertTrue(failure.getMessage().contains("Fallback Program"));
        assertTrue(failure.getMessage().contains("ID：501"));
        assertTrue(failure.getMessage().contains("ID：502"));
        verifyNoInteractions(knowledge);
        verify(courses, never()).deleteCourseByIds(anyList());
    }

    @Test
    void unreferencedSourceCourseCanStillBeDeleted() {
        service.removeStorageCourse(Collections.singletonList(1L));

        verify(courses).deleteCourseByIds(Collections.singletonList(1L));
        verify(knowledge).deleteByCourseIds(Collections.singletonList(1L));
        verifyNoInteractions(references, schemes);
    }

    @Test
    void historicalInvocationCanBeDeletedAndKeepsItsSourceAndVersionInTheAuditLog() {
        Course invoked = course(3L, "Historical Copy");
        invoked.setSourceId(1L);
        invoked.setTemplateType(2);
        invoked.setVersion("2025");
        when(courses.selectCoursesByIds(Collections.singletonList(3L)))
                .thenReturn(Collections.singletonList(invoked));
        when(references.selectByCourseIds(Collections.singletonList(3L)))
                .thenReturn(Collections.singletonList(reference(3L, 501L)));

        service.removeInvokeCourse(Collections.singletonList(3L));

        verify(deletionLogs).insertBatch(argThat(logs -> logs.size() == 1
                && Long.valueOf(3L).equals(logs.get(0).getCourseId())
                && Long.valueOf(1L).equals(logs.get(0).getSourceId())
                && Long.valueOf(501L).equals(logs.get(0).getSchemeId())
                && "2025".equals(logs.get(0).getVersion())));
        verify(knowledge).deleteByCourseIds(Collections.singletonList(3L));
        verify(schedules).deleteByCourseIds(Collections.singletonList(3L));
        verify(references).deleteTrainingSchemeRefCourseByCourseIds(Collections.singletonList(3L));
        verify(courses).removeCourseByIds(Collections.singletonList(3L));
        verify(courses).selectCoursesByIds(Collections.singletonList(3L));
        verifyNoMoreInteractions(courses);
        verifyNoInteractions(schemes);
    }

    @Test
    void historicalInvocationStillProtectsItsSourceCourseFromDeletion() {
        Course invoked = course(3L, "Historical Copy");
        invoked.setSourceId(1L);
        invoked.setVersion("2025");
        when(courses.selectCourseBySourceId(1L)).thenReturn(Collections.singletonList(invoked));
        when(references.selectByCourseIds(Collections.singletonList(3L)))
                .thenReturn(Collections.singletonList(reference(3L, 501L)));
        when(schemes.selectTrainingSchemeByIds(Collections.singletonList(501L)))
                .thenReturn(Collections.singletonList(scheme(501L, "2025 Program")));

        RuntimeException failure = assertThrows(RuntimeException.class,
                () -> service.removeStorageCourse(Collections.singletonList(1L)));

        assertTrue(failure.getMessage().contains("2025 Program"));
        assertTrue(failure.getMessage().contains("调用课程ID：3"));
        verify(courses, never()).deleteCourseByIds(anyList());
        verifyNoInteractions(knowledge, schedules, deletionLogs);
    }

    @Test
    void auditFailureStopsInvokedCourseDeletionBeforeAnyReferencesAreRemoved() {
        Course invoked = course(3L, "Historical Copy");
        invoked.setSourceId(1L);
        invoked.setVersion("2025");
        when(courses.selectCoursesByIds(Collections.singletonList(3L)))
                .thenReturn(Collections.singletonList(invoked));
        RuntimeException failure = new RuntimeException("audit unavailable");
        doThrow(failure).when(deletionLogs).insertBatch(anyList());

        assertSame(failure, assertThrows(RuntimeException.class,
                () -> service.removeInvokeCourse(Collections.singletonList(3L))));

        verify(references, never()).deleteTrainingSchemeRefCourseByCourseIds(anyList());
        verify(courses, never()).removeCourseByIds(anyList());
        verifyNoInteractions(knowledge, schedules);
    }

    private static Course course(Long id, String name) {
        Course course = new Course();
        course.setId(id);
        course.setName(name);
        return course;
    }

    private static TrainingSchemeRefCourse reference(Long courseId, Long schemeId) {
        TrainingSchemeRefCourse reference = new TrainingSchemeRefCourse();
        reference.setCourseId(courseId);
        reference.setSchemeId(schemeId);
        return reference;
    }

    private static TrainingSchemeVo scheme(Long id, String name) {
        TrainingSchemeVo scheme = new TrainingSchemeVo();
        scheme.setId(id);
        scheme.setName("Planning Name");
        scheme.setProgramName(name);
        return scheme;
    }
}
