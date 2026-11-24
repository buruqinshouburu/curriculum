package com.doinner.csys;

import com.doinner.csys.dao.CourseMapper;
import com.doinner.csys.dao.CourseRefGraduationMapper;
import com.doinner.csys.dao.StandardGraduationMapper;
import com.doinner.csys.dao.TrainingSchemeRefCourseMapper;
import com.doinner.csys.domain.Course;
import com.doinner.csys.domain.CourseRefGraduation;
import com.doinner.csys.domain.StandardGraduation;
import com.doinner.csys.domain.TrainingSchemeRefCourse;
import com.doinner.csys.service.impl.CourseServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.ArgumentCaptor;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class CourseGraduationSyncTest {
    private static final String PUBLIC_BASIC = "69a7f32e2dc370362ef3ee6e";
    private CourseServiceImpl service;
    private CourseMapper courses;
    private CourseRefGraduationMapper bindings;
    private StandardGraduationMapper graduations;
    private TrainingSchemeRefCourseMapper references;

    @BeforeEach
    void setup() {
        service = new CourseServiceImpl();
        courses = mock(CourseMapper.class);
        bindings = mock(CourseRefGraduationMapper.class);
        graduations = mock(StandardGraduationMapper.class);
        references = mock(TrainingSchemeRefCourseMapper.class);
        ReflectionTestUtils.setField(service, "courseMapper", courses);
        ReflectionTestUtils.setField(service, "courseRefGraduationMapper", bindings);
        ReflectionTestUtils.setField(service, "standardGraduationMapper", graduations);
        ReflectionTestUtils.setField(service, "trainingSchemeRefCourseMapper", references);
        StandardGraduation graduation = new StandardGraduation();
        graduation.setId(900L); graduation.setSourceId(90L);
        when(graduations.selectSchemeGraduationWithSourceBySchemeId(501L))
                .thenReturn(Collections.singletonList(graduation));
    }

    @Test
    void exemptCourseDoesNotRequireGraduationTreeAndLeavesExistingBindingUntouched() {
        Course exempt = course(3L, 1L, PUBLIC_BASIC, "3");
        exempt.setUnifiedCourse(0);
        prepare(Collections.singletonList(exempt));
        List<CourseRefGraduation> existing = new ArrayList<>(Collections.singletonList(binding(3L, 800L)));
        when(bindings.selectExistingRefByCourseIds(anyList())).thenReturn(existing);
        when(bindings.selectCourseTargetRefGraduationByCourseIds(anyList()))
                .thenReturn(Collections.singletonList(binding(1L, 90L)));

        Map<String, Object> summary = service.syncGraduationFromSource(501L);

        assertEquals(0, summary.get("rowsInserted"));
        assertEquals(0, summary.get("coursesSynced"));
        assertEquals(1, existing.size());
        assertEquals(800L, existing.get(0).getGraduationId());
        verifyNoInteractions(graduations, bindings);
        verify(courses, never()).updateBindStatusById(anyLong(), anyInt());
    }

    @Test
    void mixedSyncSkipsOnlyExemptCourseAndStillAppendsNormalBindings() {
        Course exempt = course(3L, 1L, PUBLIC_BASIC, "3");
        exempt.setUnifiedCourse(0);
        Course ordinary = course(4L, 2L, PUBLIC_BASIC, "1");
        prepare(Arrays.asList(exempt, ordinary));
        when(bindings.selectCourseTargetRefGraduationByCourseIds(anyList()))
                .thenReturn(Arrays.asList(binding(1L, 90L), binding(2L, 90L)));
        when(bindings.selectExistingRefByCourseIds(anyList()))
                .thenReturn(Collections.singletonList(binding(3L, 800L)));

        Map<String, Object> summary = service.syncGraduationFromSource(501L);

        assertEquals(1, summary.get("rowsInserted"));
        assertEquals(1, summary.get("coursesSynced"));
        ArgumentCaptor<List<CourseRefGraduation>> inserted = ArgumentCaptor.forClass(List.class);
        verify(bindings).insertCourseTargetRefGraduationList(inserted.capture());
        assertEquals(1, inserted.getValue().size());
        assertEquals(4L, inserted.getValue().get(0).getCourseId());
        assertEquals(900L, inserted.getValue().get(0).getGraduationId());
        verify(courses).updateBindStatusById(eq(4L), anyInt());
        verify(courses, never()).updateBindStatusById(eq(3L), anyInt());
        verify(bindings, never()).deleteCourseRefGraduationByCourseId(anyLong());
        verify(bindings, never()).deleteCourseRefGraduationByCourseIds(any());
    }

    @ParameterizedTest
    @CsvSource({
            "69a7f32e2dc370362ef3ee6e,1",
            "69a7f32e2dc370362ef3ee6e,2",
            "other-module,3",
            "69a7f32e2dc370362ef3ee6e-extra,3"
    })
    void unifiedCourseSyncIgnoresModuleAndStudyAttribute(String module, String attribute) {
        Course ordinary = course(3L, 1L, module, attribute);
        ordinary.setCourseModuleName("公共基础");
        prepare(Collections.singletonList(ordinary));
        when(bindings.selectCourseTargetRefGraduationByCourseIds(anyList()))
                .thenReturn(Collections.singletonList(binding(1L, 90L)));

        assertEquals(1, service.syncGraduationFromSource(501L).get("rowsInserted"));

        verify(bindings).insertCourseTargetRefGraduationList(anyList());
    }

    @Test
    void ordinaryExistingBindingsRemainIdempotent() {
        prepare(Collections.singletonList(course(3L, 1L, "other-module", "3")));
        when(bindings.selectCourseTargetRefGraduationByCourseIds(anyList()))
                .thenReturn(Collections.singletonList(binding(1L, 90L)));
        when(bindings.selectExistingRefByCourseIds(anyList()))
                .thenReturn(Collections.singletonList(binding(3L, 900L)));

        Map<String, Object> summary = service.syncGraduationFromSource(501L);

        assertEquals(0, summary.get("rowsInserted"));
        assertEquals(1, summary.get("skippedExisting"));
        verify(bindings, never()).insertCourseTargetRefGraduationList(anyList());
        verify(courses, never()).updateBindStatusById(anyLong(), anyInt());
    }

    @Test
    void unmappedGraduationIsReferencedOnlyByInvokedCourse() {
        prepare(Collections.singletonList(course(4L, 2L, PUBLIC_BASIC, "1")));
        when(bindings.selectCourseTargetRefGraduationByCourseIds(anyList()))
                .thenReturn(Collections.singletonList(binding(2L, 91L)));
        assertEquals(1, service.syncGraduationFromSource(501L).get("rowsInserted"));
        ArgumentCaptor<List<CourseRefGraduation>> inserted = ArgumentCaptor.forClass(List.class);
        verify(bindings).insertCourseTargetRefGraduationList(inserted.capture());
        assertEquals(4L, inserted.getValue().get(0).getCourseId());
        assertEquals(91L, inserted.getValue().get(0).getGraduationId());
        verify(graduations).selectSchemeGraduationWithSourceBySchemeId(501L);
        org.mockito.Mockito.verifyNoMoreInteractions(graduations);
    }

    private void prepare(List<Course> invoked) {
        List<TrainingSchemeRefCourse> refs = new ArrayList<>();
        for (Course course : invoked) {
            TrainingSchemeRefCourse ref = new TrainingSchemeRefCourse();
            ref.setSchemeId(501L); ref.setCourseId(course.getId()); refs.add(ref);
        }
        when(references.selectTrainingSchemeRefCourseByTrainingSchemeVoId(501L)).thenReturn(refs);
        List<Course> sources = new ArrayList<>();
        for (Course copy : invoked) {
            Course source = new Course();
            source.setId(copy.getSourceId());
            source.setUnifiedCourse(copy.getUnifiedCourse());
            sources.add(source);
        }
        when(courses.selectCoursesByIds(anyList())).thenReturn(invoked, sources);
    }

    private static Course course(Long id, Long sourceId, String module, String attribute) {
        Course course = new Course();
        course.setUnifiedCourse(1);
        course.setId(id); course.setSourceId(sourceId); course.setCourseModule(module); course.setCourseAttr(attribute);
        return course;
    }

    private static CourseRefGraduation binding(Long course, Long graduation) {
        CourseRefGraduation binding = new CourseRefGraduation();
        binding.setCourseId(course); binding.setGraduationId(graduation);
        return binding;
    }
}
