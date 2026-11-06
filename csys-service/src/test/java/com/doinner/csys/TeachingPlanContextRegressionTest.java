package com.doinner.csys;

import com.alibaba.fastjson2.JSONArray;
import com.doinner.common.core.utils.SpringUtils;
import com.doinner.common.redis.service.RedisService;
import com.doinner.csys.dao.CourseKnowledgePointMapper;
import com.doinner.csys.dao.CourseKnowledgeUnitMapper;
import com.doinner.csys.dao.CourseMapper;
import com.doinner.csys.dao.CourseRefKnowledgeUnitMapper;
import com.doinner.csys.dao.TeachingPlanMapper;
import com.doinner.csys.domain.TeachingPlan;
import com.doinner.csys.domain.vo.CourseVo;
import com.doinner.csys.domain.vo.TeachingPlanDetailVo;
import com.doinner.csys.domain.vo.TeachingPlanQuoteAggVo;
import com.doinner.csys.entity.csys.model.DictContent;
import com.doinner.csys.entity.csys.po.CourseKnowledgeUnit;
import com.doinner.csys.entity.csys.po.CourseRefKnowledgeUnit;
import com.doinner.csys.permission.CoursePermissionService;
import com.doinner.csys.service.impl.KnowledgeUnitServiceImpl;
import com.doinner.csys.service.impl.TeachingPlanModuleServiceImpl;
import com.doinner.csys.service.impl.TeachingPlanServiceImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/** Local mapper tests; no datasource, authentication server, or production writes. */
public class TeachingPlanContextRegressionTest {
    private Object previousBeanFactory;

    @BeforeEach
    void setupDictionaries() {
        previousBeanFactory = ReflectionTestUtils.getField(SpringUtils.class, "beanFactory");
        RedisService redis = mock(RedisService.class);
        when(redis.getCacheObject(anyString())).thenReturn(new JSONArray());
        ConfigurableListableBeanFactory beans = mock(ConfigurableListableBeanFactory.class);
        when(beans.getBean(RedisService.class)).thenReturn(redis);
        ReflectionTestUtils.setField(SpringUtils.class, "beanFactory", beans);
    }

    @AfterEach
    void restoreDictionaries() {
        ReflectionTestUtils.setField(SpringUtils.class, "beanFactory", previousBeanFactory);
    }

    @Test
    void publicSourceQuotedAsProfessionalRequiresSchemeInBothDetailAndSave() {
        assertClassification(DictContent.GENERAL_EDUCATION_COURSES_SCHEDULE, "professional", false);
    }

    @Test
    void professionalSourceQuotedOnlyAsPublicUsesOneNullSchemeGroup() {
        assertClassification("professional", DictContent.GENERAL_EDUCATION_COURSES_SCHEDULE, true);
    }

    @Test
    void mixedQuotedModulesAreNotReportedAsOnePublicGroup() {
        assertClassification(DictContent.GENERAL_EDUCATION_COURSES_SCHEDULE,
                DictContent.GENERAL_EDUCATION_COURSES_SCHEDULE + "\u3001professional", false);
    }

    @Test
    void unquotedPublicSourceKeepsItsNullSchemeGroup() {
        assertClassification(DictContent.GENERAL_EDUCATION_COURSES_SCHEDULE, null, true);
    }

    @Test
    void mixedSourceModulesAreNotPublicWhenNoQuoteOverridesThem() {
        assertClassification(DictContent.GENERAL_EDUCATION_COURSES_SCHEDULE + ",professional", null, false);
    }

    @Test
    void missingPlanIdReturnsTheSavedCurrentPlanDetail() {
        TeachingPlanMapper plans = mock(TeachingPlanMapper.class);
        TeachingPlan current = new TeachingPlan();
        current.setId(101L);
        when(plans.selectBySourceCourseId(8L)).thenReturn(current);
        TeachingPlanDetailVo saved = new TeachingPlanDetailVo();
        saved.setCourseId(8L);
        saved.setTeachingPlanId(101L);
        saved.setScoreRule("saved score rule");
        when(plans.selectDetailByPlanId(101L)).thenReturn(saved);
        when(plans.selectDetailByCourseId(8L)).thenReturn(new TeachingPlanDetailVo());

        TeachingPlanDetailVo actual = detailService(plans).getDetail(8L, null);

        assertEquals(101L, actual.getTeachingPlanId());
        assertEquals("saved score rule", actual.getScoreRule());
        verify(plans).selectBySourceCourseId(8L);
        verify(plans).selectDetailByPlanId(101L);
        verify(plans, never()).selectDetailByCourseId(any());
    }

    @Test
    void missingPlanIdWithoutSavedPlanReturnsCourseDetail() {
        TeachingPlanMapper plans = mock(TeachingPlanMapper.class);
        TeachingPlanDetailVo course = new TeachingPlanDetailVo();
        course.setCourseId(8L);
        when(plans.selectDetailByCourseId(8L)).thenReturn(course);

        TeachingPlanDetailVo actual = detailService(plans).getDetail(8L, null);

        assertSame(course, actual);
        assertNull(actual.getTeachingPlanId());
        verify(plans).selectBySourceCourseId(8L);
        verify(plans).selectDetailByCourseId(8L);
        verify(plans, never()).selectDetailByPlanId(any());
    }

    @Test
    void explicitHistoricalPlanIdIsNotReplacedByTheCurrentPlan() {
        TeachingPlanMapper plans = mock(TeachingPlanMapper.class);
        TeachingPlan current = new TeachingPlan();
        current.setId(101L);
        when(plans.selectBySourceCourseId(8L)).thenReturn(current);
        TeachingPlanDetailVo historical = new TeachingPlanDetailVo();
        historical.setCourseId(8L);
        historical.setTeachingPlanId(99L);
        when(plans.selectDetailByPlanId(99L)).thenReturn(historical);

        TeachingPlanDetailVo actual = detailService(plans).getDetail(8L, 99L);

        assertSame(historical, actual);
        assertEquals(99L, actual.getTeachingPlanId());
        verify(plans).selectDetailByPlanId(99L);
        verify(plans, never()).selectBySourceCourseId(any());
        verify(plans, never()).selectDetailByCourseId(any());
        verify(plans, never()).selectDetailByPlanId(101L);
    }

    @Test
    void explicitMissingPlanIdDoesNotFallBackToAnotherPlanOrCourse() {
        TeachingPlanMapper plans = mock(TeachingPlanMapper.class);
        TeachingPlan current = new TeachingPlan();
        current.setId(101L);
        when(plans.selectBySourceCourseId(8L)).thenReturn(current);
        when(plans.selectDetailByPlanId(101L)).thenReturn(new TeachingPlanDetailVo());
        when(plans.selectDetailByCourseId(8L)).thenReturn(new TeachingPlanDetailVo());

        assertNull(detailService(plans).getDetail(8L, 999L));

        verify(plans).selectDetailByPlanId(999L);
        verify(plans, never()).selectBySourceCourseId(any());
        verify(plans, never()).selectDetailByCourseId(any());
        verify(plans, never()).selectDetailByPlanId(101L);
    }

    @SuppressWarnings("unchecked")
    private TeachingPlanServiceImpl detailService(TeachingPlanMapper plans) {
        TeachingPlanServiceImpl service = new TeachingPlanServiceImpl();
        ReflectionTestUtils.setField(service, "teachingPlanMapper", plans);
        ReflectionTestUtils.setField(service, "courseMapper", mock(CourseMapper.class));
        ReflectionTestUtils.setField(service, "teachingPlanModuleService", mock(TeachingPlanModuleServiceImpl.class));
        ((Map<String, String>) ReflectionTestUtils.getField(service, "courseModuleDictionaryIdToNameMap"))
                .put("cached", "Cached label");
        return service;
    }

    @SuppressWarnings("unchecked")
    private void assertClassification(String sourceModule, String quoteModule, boolean expected) {
        CourseMapper courses = mock(CourseMapper.class);
        TeachingPlanMapper plans = mock(TeachingPlanMapper.class);
        CourseVo source = new CourseVo();
        source.setId(8L);
        source.setType("1");
        source.setCourseModule(sourceModule);
        when(courses.selectCourseById(8L)).thenReturn(source);
        TeachingPlanQuoteAggVo aggregate = new TeachingPlanQuoteAggVo();
        aggregate.setCourseModule(quoteModule);
        when(plans.selectQuoteAggBySourceCourseId(8L)).thenReturn(aggregate);
        TeachingPlanDetailVo detail = new TeachingPlanDetailVo();
        detail.setCourseId(8L);
        detail.setType("1");
        detail.setCourseModule(quoteModule == null ? sourceModule : quoteModule);
        when(plans.selectDetailByCourseId(8L)).thenReturn(detail);

        TeachingPlanModuleServiceImpl modules = new TeachingPlanModuleServiceImpl();
        ReflectionTestUtils.setField(modules, "courseMapper", courses);
        ReflectionTestUtils.setField(modules, "teachingPlanMapper", plans);
        TeachingPlanServiceImpl service = new TeachingPlanServiceImpl();
        ReflectionTestUtils.setField(service, "courseMapper", courses);
        ReflectionTestUtils.setField(service, "teachingPlanMapper", plans);
        ReflectionTestUtils.setField(service, "teachingPlanModuleService", modules);
        ((Map<String, String>) ReflectionTestUtils.getField(service, "courseModuleDictionaryIdToNameMap"))
                .put("cached", "Cached label");

        assertEquals(expected, modules.isPublicFoundationCourse(8L), "save classification");
        assertEquals(expected, service.getDetail(8L, null).getPublicFoundation(),
                "detail must give the frontend the same scheme requirement as saves");
    }

    @Test
    void nullableKnowledgeUnitSortStillReturnsTheCourseKnowledgePoints() {
        KnowledgeUnitServiceImpl service = new KnowledgeUnitServiceImpl();
        CoursePermissionService permissions = mock(CoursePermissionService.class);
        CourseRefKnowledgeUnitMapper references = mock(CourseRefKnowledgeUnitMapper.class);
        CourseKnowledgeUnitMapper units = mock(CourseKnowledgeUnitMapper.class);
        CourseKnowledgePointMapper points = mock(CourseKnowledgePointMapper.class);
        ReflectionTestUtils.setField(service, "coursePermissions", permissions);
        ReflectionTestUtils.setField(service, "courseRefKnowledgeUnitMapper", references);
        ReflectionTestUtils.setField(service, "knowledgeUnitMapper", units);
        ReflectionTestUtils.setField(service, "knowledgePointMapper", points);
        when(permissions.sourceCourseId(8L)).thenReturn(8L);
        CourseRefKnowledgeUnit reference = new CourseRefKnowledgeUnit();
        reference.setCourseId(8L);
        reference.setCourseUnitId(1L);
        when(references.selectByCourseId(8L)).thenReturn(Collections.singletonList(reference));
        when(units.selectByIds(anyList())).thenReturn(new ArrayList<>(Arrays.asList(
                knowledgeUnit(1L, null), knowledgeUnit(2L, 2), knowledgeUnit(3L, 1))));
        when(points.selectByIds(anyList())).thenReturn(Collections.emptyList());

        assertEquals(Arrays.asList(3L, 2L, 1L), service.listByCourseId(8L).stream()
                .map(row -> row.getId()).collect(Collectors.toList()));
    }

    private CourseKnowledgeUnit knowledgeUnit(Long id, Integer sort) {
        CourseKnowledgeUnit unit = new CourseKnowledgeUnit();
        unit.setId(id);
        unit.setName("Unit " + id);
        unit.setSort(sort);
        return unit;
    }
}
