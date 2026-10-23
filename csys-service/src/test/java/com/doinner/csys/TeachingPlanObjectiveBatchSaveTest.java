package com.doinner.csys;

import com.doinner.common.core.context.SecurityContextHolder;
import com.doinner.csys.dao.*;
import com.doinner.csys.domain.*;
import com.doinner.csys.domain.vo.*;
import com.doinner.csys.permission.CoursePermission;
import com.doinner.csys.service.impl.TeachingPlanModuleServiceImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static com.doinner.csys.permission.CourseResource.PLAN;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/** Mapper-only tests: no Spring bootstrap, datasource, remote service or production data. */
public class TeachingPlanObjectiveBatchSaveTest {
    private TeachingPlanModuleServiceImpl service;
    private TeachingPlanObjectiveMapper objectives;
    private TeachingPlanObjectiveRefMapper refs;
    private TeachingPlanTargetDesignMapper designs;
    private TeachingPlanObjectiveAssessmentMapper assessments;
    private TeachingPlanMapper plans;
    private CourseMapper courses;
    private StandardGraduationMapper graduations;
    private TeachingPlanObjective existing;

    @BeforeEach
    void setup() {
        service = new TeachingPlanModuleServiceImpl();
        objectives = mock(TeachingPlanObjectiveMapper.class);
        refs = mock(TeachingPlanObjectiveRefMapper.class);
        designs = mock(TeachingPlanTargetDesignMapper.class);
        assessments = mock(TeachingPlanObjectiveAssessmentMapper.class);
        plans = mock(TeachingPlanMapper.class);
        courses = mock(CourseMapper.class);
        graduations = mock(StandardGraduationMapper.class);
        ReflectionTestUtils.setField(service, "teachingPlanObjectiveMapper", objectives);
        ReflectionTestUtils.setField(service, "teachingPlanObjectiveRefMapper", refs);
        ReflectionTestUtils.setField(service, "teachingPlanTargetDesignMapper", designs);
        ReflectionTestUtils.setField(service, "teachingPlanObjectiveAssessmentMapper", assessments);
        ReflectionTestUtils.setField(service, "teachingPlanMapper", plans);
        ReflectionTestUtils.setField(service, "courseMapper", courses);
        ReflectionTestUtils.setField(service, "standardGraduationMapper", graduations);
        TeachingPlan plan = new TeachingPlan();
        plan.setId(101L);
        plan.setSourceCourseId(1L);
        plan.setPlanType(1);
        when(plans.selectById(101L)).thenReturn(plan);
        CourseVo course = new CourseVo();
        course.setId(1L);
        course.setCourseModule("professional");
        when(courses.selectCourseById(1L)).thenReturn(course);
        existing = row(11L, "Original", "1").getObjective();
        existing.setPlanId(101L);
        existing.setSchemeId(10L);
        when(objectives.selectByPlanAndScheme(101L, 10L, false))
                .thenReturn(Collections.singletonList(existing));
        when(objectives.insert(any())).thenAnswer(call -> {
            TeachingPlanObjective objective = call.getArgument(0);
            objective.setId(100L);
            return 1;
        });
        when(graduations.selectStandardGraduationByIds(anyList())).thenAnswer(call -> {
            List<Long> ids = call.getArgument(0);
            return ids.stream().map(id -> {
                StandardGraduation graduation = new StandardGraduation();
                graduation.setId(id);
                graduation.setName("Graduation " + id);
                return graduation;
            }).collect(Collectors.toList());
        });
        SecurityContextHolder.setUserName("objective-test");
    }

    @AfterEach
    void cleanup() {
        SecurityContextHolder.remove();
    }

    @Test
    void appendSameTypePreservesIdAndExistingDesignAndAssessmentLinks() {
        TeachingPlanTargetDesign design = new TeachingPlanTargetDesign();
        design.setObjectiveId(11L);
        TeachingPlanObjectiveAssessment assessment = new TeachingPlanObjectiveAssessment();
        assessment.setObjectiveId(11L);
        TeachingPlanObjectiveSaveVo kept = row(11L, "Edited", "0.4");
        TeachingPlanObjectiveSaveVo added = row(null, "Another", "0.6");
        service.saveObjectivesBatch(batch(kept, added));
        assertEquals(11L, kept.getObjective().getId());
        verify(objectives).updateById(kept.getObjective());
        verify(objectives).insert(added.getObjective());
        verify(objectives, never()).deleteById(design.getObjectiveId());
        verify(objectives, never()).deleteById(assessment.getObjectiveId());
        verify(objectives, never()).deleteByPlanAndScheme(anyLong(), any(), anyBoolean());
        verifyNoInteractions(designs, assessments);
    }

    @Test
    void sameTypeAndContentRowsKeepIndependentWeightsAndRefs() {
        TeachingPlanObjectiveSaveVo first = row(11L, "Same content", "0.3");
        TeachingPlanObjectiveSaveVo second = row(null, "Same content", "0.7");
        first.setRefs(Collections.singletonList(ref(501L)));
        second.setRefs(Collections.singletonList(ref(502L)));
        service.saveObjectivesBatch(batch(first, second));
        assertEquals(new BigDecimal("0.3"), first.getObjective().getWeight());
        assertEquals(new BigDecimal("0.7"), second.getObjective().getWeight());
        ArgumentCaptor<TeachingPlanObjectiveRef> saved = ArgumentCaptor.forClass(TeachingPlanObjectiveRef.class);
        verify(refs, times(2)).insert(saved.capture());
        assertEquals(11L, saved.getAllValues().get(0).getObjectiveId());
        assertEquals(501L, saved.getAllValues().get(0).getGraduationId());
        assertEquals(100L, saved.getAllValues().get(1).getObjectiveId());
        assertEquals(502L, saved.getAllValues().get(1).getGraduationId());
        for (TeachingPlanObjectiveRef ref : saved.getAllValues()) {
            assertEquals(101L, ref.getPlanId());
            assertEquals(10L, ref.getSchemeId());
        }
        verify(refs).deleteByObjectiveId(11L);
        verify(refs, never()).deleteByPlanAndScheme(anyLong(), any(), anyBoolean());
    }

    @Test
    void omittedRowsOnlyAreLogicallyDeleted() {
        TeachingPlanObjective omitted = row(12L, "Omitted", "0.5").getObjective();
        omitted.setPlanId(101L);
        omitted.setSchemeId(10L);
        when(objectives.selectByPlanAndScheme(101L, 10L, false))
                .thenReturn(Arrays.asList(existing, omitted));
        service.saveObjectivesBatch(batch(row(11L, "Kept", "1")));
        verify(objectives).deleteById(12L);
        verify(refs).deleteByObjectiveId(12L);
        verify(objectives, never()).deleteById(11L);
        verify(objectives, never()).deleteByPlanAndScheme(anyLong(), any(), anyBoolean());
    }

    @Test
    void emptyBatchDeletesOnlyItsScope() {
        service.saveObjectivesBatch(batch());
        verify(objectives).selectByPlanAndScheme(101L, 10L, false);
        verify(objectives).deleteById(11L);
        verify(refs).deleteByObjectiveId(11L);
        verify(objectives, never()).deleteByPlanAndScheme(anyLong(), any(), anyBoolean());
    }

    @Test
    void publicFoundationUsesOnlyNullSchemeAndNormalizesRefs() {
        when(courses.selectCourseById(1L)).thenAnswer(call -> {
            CourseVo course = new CourseVo();
            course.setCourseModule("\u516c\u5171\u57fa\u7840");
            return course;
        });
        existing.setSchemeId(null);
        when(objectives.selectByPlanAndScheme(101L, null, true))
                .thenReturn(Collections.singletonList(existing));
        TeachingPlanObjectiveSaveVo kept = row(11L, "Foundation", "1");
        kept.getObjective().setSchemeId(999L);
        kept.setRefs(Collections.singletonList(ref(501L)));
        TeachingPlanObjectiveBatchSaveVo batch = batch(kept);
        batch.setSchemeId(null);
        service.saveObjectivesBatch(batch);
        assertEquals(11L, kept.getObjective().getId());
        assertNull(kept.getObjective().getSchemeId());
        verify(objectives).selectByPlanAndScheme(101L, null, true);
        verify(objectives).updateById(kept.getObjective());
        ArgumentCaptor<TeachingPlanObjectiveRef> saved = ArgumentCaptor.forClass(TeachingPlanObjectiveRef.class);
        verify(refs).insert(saved.capture());
        assertNull(saved.getValue().getSchemeId());
    }

    @Test
    void rejectsForeignPlanSchemeMissingAndDeletedIdsBeforeAnyWrite() {
        for (Long foreignId : Arrays.asList(22L, 33L, 44L, 999L)) {
            assertThrows(IllegalArgumentException.class,
                    () -> service.saveObjectivesBatch(batch(
                            row(11L, "Valid first row", "0.4"), row(foreignId, "Foreign", "0.6"))));
        }
        assertNoWrites();
    }

    @Test
    void rejectsDuplicateIdsBeforeAnyWrite() {
        assertThrows(IllegalArgumentException.class,
                () -> service.saveObjectivesBatch(batch(row(11L, "First", "0.4"), row(11L, "Second", "0.6"))));
        assertNoWrites();
    }

    @Test
    void rejectsForeignRowPlanAndSchemeBeforeAnyWrite() {
        TeachingPlanObjectiveSaveVo row = row(11L, "Foreign plan", "1");
        row.getObjective().setPlanId(202L);
        assertThrows(IllegalArgumentException.class, () -> service.saveObjectivesBatch(batch(row)));
        row.getObjective().setPlanId(101L);
        row.getObjective().setSchemeId(20L);
        assertThrows(IllegalArgumentException.class, () -> service.saveObjectivesBatch(batch(row)));
        assertNoWrites();
    }

    @Test
    void rejectsForeignSchemeIdForPublicFoundation() {
        when(courses.selectCourseById(1L)).thenAnswer(call -> {
            CourseVo course = new CourseVo();
            course.setCourseModule("\u516c\u5171\u57fa\u7840");
            return course;
        });
        when(objectives.selectByPlanAndScheme(101L, null, true)).thenReturn(Collections.emptyList());
        assertThrows(IllegalArgumentException.class,
                () -> service.saveObjectivesBatch(batch(row(11L, "Historical scheme row", "1"))));
        assertNoWrites();
    }

    @Test
    void keepsExistingWeightValidation() {
        for (String invalidWeight : Arrays.asList(null, "-1", "0.9", "1.1")) {
            assertThrows(IllegalArgumentException.class,
                    () -> service.saveObjectivesBatch(batch(row(11L, "Invalid weight", invalidWeight))));
        }
        assertNoWrites();
    }

    @Test
    void validatesContentBeforeDeletingOmittedRows() {
        assertThrows(IllegalArgumentException.class,
                () -> service.saveObjectivesBatch(batch(row(null, "  ", "1"))));
        assertNoWrites();
    }

    @Test
    void rejectsNullGraduationIdInLaterRowBeforeAnyWrite() {
        TeachingPlanObjectiveSaveVo first = row(11L, "Valid first row", "0.4");
        first.setRefs(Arrays.asList(null, ref(501L)));
        TeachingPlanObjectiveSaveVo second = row(null, "Invalid later refs", "0.6");
        second.setRefs(Collections.singletonList(ref(null)));
        assertThrows(IllegalArgumentException.class,
                () -> service.saveObjectivesBatch(batch(first, second)));
        assertNoWrites();
    }

    @Test
    void rejectsMissingGraduationIdInLaterRowBeforeAnyWrite() {
        StandardGraduation valid = new StandardGraduation();
        valid.setId(501L);
        when(graduations.selectStandardGraduationByIds(anyList()))
                .thenReturn(Collections.singletonList(valid));
        TeachingPlanObjectiveSaveVo first = row(11L, "Valid first row", "0.4");
        first.setRefs(Collections.singletonList(ref(501L)));
        TeachingPlanObjectiveSaveVo second = row(null, "Missing graduation", "0.6");
        second.setRefs(Collections.singletonList(ref(999L)));
        assertThrows(IllegalArgumentException.class,
                () -> service.saveObjectivesBatch(batch(first, second)));
        assertNoWrites();
    }

    @Test
    void keepsPermissionAndRollbackAnnotations() throws Exception {
        Method method = TeachingPlanModuleServiceImpl.class.getMethod(
                "saveObjectivesBatch", TeachingPlanObjectiveBatchSaveVo.class);
        CoursePermission permission = method.getAnnotation(CoursePermission.class);
        assertNotNull(permission);
        assertEquals(PLAN, permission.resource());
        assertEquals("#p0.planId", permission.value());
        assertTrue(Arrays.asList(method.getAnnotation(Transactional.class).rollbackFor()).contains(Exception.class));
    }

    private void assertNoWrites() {
        verify(objectives, never()).insert(any());
        verify(objectives, never()).updateById(any());
        verify(objectives, never()).deleteById(anyLong());
        verify(objectives, never()).deleteByPlanAndScheme(anyLong(), any(), anyBoolean());
        verifyNoInteractions(refs, designs, assessments);
    }

    private static TeachingPlanObjectiveSaveVo row(Long id, String content, String weight) {
        TeachingPlanObjective objective = new TeachingPlanObjective();
        objective.setId(id);
        objective.setObjectiveTypeCode("knowledge");
        objective.setContent(content);
        objective.setWeight(weight == null ? null : new BigDecimal(weight));
        TeachingPlanObjectiveSaveVo row = new TeachingPlanObjectiveSaveVo();
        row.setObjective(objective);
        return row;
    }

    private static TeachingPlanObjectiveRef ref(Long graduationId) {
        TeachingPlanObjectiveRef ref = new TeachingPlanObjectiveRef();
        ref.setGraduationId(graduationId);
        ref.setQuoteCourseId(1L);
        ref.setPlanId(202L);
        ref.setSchemeId(20L);
        ref.setObjectiveId(999L);
        return ref;
    }

    private static TeachingPlanObjectiveBatchSaveVo batch(TeachingPlanObjectiveSaveVo... rows) {
        TeachingPlanObjectiveBatchSaveVo batch = new TeachingPlanObjectiveBatchSaveVo();
        batch.setPlanId(101L);
        batch.setSchemeId(10L);
        batch.setObjectives(Arrays.asList(rows));
        return batch;
    }

    /** Offline fallback when the cached Maven dependencies cannot resolve. */
    public static void main(String[] args) throws Exception {
        int passed = 0;
        for (Method method : TeachingPlanObjectiveBatchSaveTest.class.getDeclaredMethods()) {
            if (!method.isAnnotationPresent(Test.class)
                    || (args.length > 0 && !method.getName().equals(args[0]))) {
                continue;
            }
            TeachingPlanObjectiveBatchSaveTest test = new TeachingPlanObjectiveBatchSaveTest();
            test.setup();
            try {
                method.invoke(test);
                System.out.println("PASS " + method.getName());
                passed++;
            } catch (InvocationTargetException e) {
                throw new AssertionError(method.getName(), e.getCause());
            } finally {
                test.cleanup();
            }
        }
        assertTrue(passed > 0, "No tests selected");
        System.out.println("Tests passed: " + passed);
    }
}
