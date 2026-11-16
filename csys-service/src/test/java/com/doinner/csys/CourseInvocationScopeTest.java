package com.doinner.csys;

import com.doinner.csys.domain.Course;
import com.doinner.csys.domain.TrainingSchemeRefCourse;
import com.doinner.csys.domain.vo.CourseTemplateVo;
import com.doinner.csys.domain.vo.TrainingSchemeVo;
import com.doinner.csys.dao.CourseMapper;
import com.doinner.csys.dao.StandardMajorMapper;
import com.doinner.csys.domain.StandardMajor;
import com.doinner.csys.dao.TrainingSchemeMapper;
import com.doinner.csys.dao.TrainingSchemeRefCourseMapper;
import com.doinner.csys.permission.CoursePermissionService;
import com.doinner.common.core.context.SecurityContextHolder;
import com.doinner.csys.entity.csys.model.DictContent;
import com.doinner.csys.service.impl.CourseServiceImpl;
import com.doinner.csys.utils.CourseInvocationScope;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collections;
import java.util.ArrayList;
import java.util.List;
import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class CourseInvocationScopeTest {
    private final CourseServiceImpl service = new CourseServiceImpl();

    @org.junit.jupiter.api.BeforeEach
    void prepareDirectionLookup() {
        StandardMajorMapper mapper = mock(StandardMajorMapper.class);
        StandardMajor first = new StandardMajor();
        first.setId(501L);
        first.setName("Direction A");
        StandardMajor second = new StandardMajor();
        second.setId(502L);
        second.setName("Direction B");
        when(mapper.selectStandardMajorByParentId(50L)).thenReturn(java.util.Arrays.asList(first, second));
        ReflectionTestUtils.setField(service, "standardMajorMapper", mapper);
    }

    @Test
    void explicitInvocationDirectionsOverrideLegacyScalarWithoutChangingSourceApplicability() {
        Course whole = invoked(DictContent.MILITARY_VOCATIONAL_EDUCATION, DictContent.POSITION_FOUNDATION, null);
        whole.setMajorId(10L);
        Course explicit = invoked(whole.getCourseModule(), whole.getCourseModuleChildren(), 99L);
        explicit.setMajorId(10L);
        explicit.setSubMajorIds(Collections.singletonList(88L));
        ReflectionTestUtils.setField(explicit, "invocationSubMajorIds", java.util.Arrays.asList(11L, 12L));
        List<Course> rows = java.util.Arrays.asList(whole, explicit);
        assertEquals(Collections.singletonList(explicit), CourseInvocationScope.forDirection(rows, 11L, 10L));
        assertEquals(Collections.singletonList(explicit), CourseInvocationScope.forDirection(rows, 12L, 10L));
        assertEquals(Collections.singletonList(whole), CourseInvocationScope.forDirection(rows, 99L, 10L));
        assertEquals(Collections.singletonList(whole), CourseInvocationScope.forDirection(rows, 1000L, 10L));
        assertEquals(rows, CourseInvocationScope.forDirection(rows, null, 10L));
        assertTrue(duplicate(explicit, whole.getCourseModule(), whole.getCourseModuleChildren(), 11L));
        assertFalse(duplicate(explicit, whole.getCourseModule(), whole.getCourseModuleChildren(), 99L));
        assertFalse(duplicate(explicit, whole.getCourseModule(), whole.getCourseModuleChildren(), null));
        assertEquals(Collections.singletonList(88L), explicit.getSubMajorIds());
        Course manual = invoked(whole.getCourseModule(), whole.getCourseModuleChildren(), 11L);
        manual.setSourceId(null);
        manual.setMajorId(10L);
        assertEquals(java.util.Arrays.asList(explicit, manual),
                CourseInvocationScope.forDirection(java.util.Arrays.asList(whole, explicit, manual), 11L, 10L));
    }

    @Test
    void wholeMajorCopyDoesNotInheritSourceDirection() {
        Course source = invoked(DictContent.MAJOR_COURSES_SCHEDULE, null, 999L);
        source.setId(101L);
        source.setInvocationSubMajorIds(java.util.Arrays.asList(11L, 12L));
        TrainingSchemeVo scheme = new TrainingSchemeVo();
        scheme.setMajorId(10L);
        CourseTemplateVo target = new CourseTemplateVo();
        target.setMajorId(10L);
        target.setCourseModule(source.getCourseModule());
        SecurityContextHolder.setUserName("scope.test");
        try {
            Course copy = ReflectionTestUtils.invokeMethod(service, "copyCourse", scheme, source, target);
            assertNull(copy.getSubMajorId());
            assertNull(copy.getInvocationSubMajorIds());
            assertEquals(10L, copy.getMajorId());
        } finally {
            SecurityContextHolder.remove();
        }
    }

    @Test
    void majorScopeCoversFutureDirectionsAndKeepsDirectionalOverride() {
        Course common = invoked(DictContent.MAJOR_COURSES_SCHEDULE, null, null);
        common.setMajorId(10L);
        Course specific = invoked(common.getCourseModule(), null, 11L);
        specific.setMajorId(10L);
        List<Course> rows = java.util.Arrays.asList(common, specific);
        assertEquals(Collections.singletonList(specific), CourseInvocationScope.forDirection(rows, 11L, 10L));
        assertEquals(Collections.singletonList(common), CourseInvocationScope.forDirection(rows, 12L, 10L));
        assertEquals(Collections.singletonList(common), CourseInvocationScope.forDirection(rows, 999L, 10L));
        assertTrue(CourseInvocationScope.forDirection(rows, 999L, 20L).isEmpty());
    }

    private Course invoked(String module, String child, Long direction) {
        Course course = new Course();
        course.setSourceId(101L);
        course.setCourseModule(module);
        course.setCourseModuleChildren(child);
        course.setSubMajorId(direction);
        return course;
    }

    @ParameterizedTest
    @ValueSource(strings = {"1", "2", "4"})
    void legacyRequestWithoutScopeUsesSchemeMajorAndSourceModuleForDeduplication(String type) {
        for (Field field : CourseServiceImpl.class.getDeclaredFields()) {
            if (field.getType().isInterface()) ReflectionTestUtils.setField(service, field.getName(), mock(field.getType()));
        }
        ReflectionTestUtils.setField(service, "coursePermissions", mock(CoursePermissionService.class));
        CourseMapper courses = (CourseMapper) ReflectionTestUtils.getField(service, "courseMapper");
        TrainingSchemeMapper schemes = (TrainingSchemeMapper) ReflectionTestUtils.getField(service, "trainingSchemeMapper");
        TrainingSchemeRefCourseMapper refs = (TrainingSchemeRefCourseMapper) ReflectionTestUtils.getField(service, "trainingSchemeRefCourseMapper");
        TrainingSchemeVo scheme = new TrainingSchemeVo();
        scheme.setId(1L);
        scheme.setMajorId(10L);
        when(schemes.selectTrainingSchemeById(1L)).thenReturn(scheme);
        Course source = invoked(DictContent.MAJOR_COURSES_SCHEDULE, null, 99L);
        source.setSourceId(null);
        source.setId(101L);
        source.setType(type);
        source.setMajorId(99L);
        source.setLocation("module");
        List<Course> saved = new ArrayList<>();
        when(refs.selectTrainingSchemeRefCourseByTrainingSchemeVoId(1L)).thenAnswer(call -> {
            List<TrainingSchemeRefCourse> rows = new ArrayList<>();
            for (Course copy : saved) {
                TrainingSchemeRefCourse row = new TrainingSchemeRefCourse();
                row.setCourseId(copy.getId());
                rows.add(row);
            }
            return rows;
        });
        when(courses.selectCoursesByIds(anyList())).thenAnswer(call -> {
            List<Long> ids = call.getArgument(0);
            return ids.contains(101L) ? Collections.singletonList(source) : saved;
        });
        doAnswer(call -> {
            List<Course> copies = call.getArgument(0);
            copies.forEach(copy -> copy.setId(200L + saved.size()));
            saved.addAll(copies);
            return null;
        }).when(courses).insertCourseList(anyList());
        CourseTemplateVo request = new CourseTemplateVo();
        request.setTrainingSchemeId(1L);
        request.setIds(Collections.singletonList(101L));
        SecurityContextHolder.setUserName("scope.test");
        try {
            assertEquals(200, service.courseInvoke(request).getCode());
            assertEquals(10L, saved.get(0).getMajorId());
            assertNull(saved.get(0).getSubMajorId());
            assertNotEquals(200, service.courseInvoke(request).getCode());
            assertEquals(1, saved.size());
        } finally {
            SecurityContextHolder.remove();
        }
    }

    private boolean duplicate(Course course, String module, String child, Long direction) {
        CourseTemplateVo target = new CourseTemplateVo();
        target.setCourseModule(module);
        target.setCourseModuleChildren(child);
        target.setSubMajorId(direction);
        target.setMajorId(course.getMajorId());
        return Boolean.TRUE.equals(ReflectionTestUtils.invokeMethod(service, "isCourseAlreadyInvoked",
                101L, Collections.singletonList(course), target));
    }

    @ParameterizedTest
    @ValueSource(strings = {DictContent.DISCIPLINE_CORE_COURSES_SCHEDULE, DictContent.MAJOR_COURSES_SCHEDULE})
    void multiMajorCopyUsesSelectedTreeScopeWithoutChangingSource(String module) {
        Course source = new Course();
        source.setId(101L);
        source.setCourseModule(module);
        source.setCourseAttr("3");
        source.setMajorIds(java.util.Arrays.asList(10L, 20L));
        source.setSubMajorIds(java.util.Arrays.asList(101L, 202L));
        TrainingSchemeVo scheme = new TrainingSchemeVo();
        scheme.setCategoryId(99L);
        scheme.setMajorId(50L);
        CourseTemplateVo target = new CourseTemplateVo();
        target.setMajorId(50L);
        target.setSubMajorId(501L);
        SecurityContextHolder.setUserName("scope.test");
        try {
            Course copy = ReflectionTestUtils.invokeMethod(service, "copyCourse", scheme, source, target);
            assertNotNull(copy);
            assertEquals(99L, copy.getCategoryId());
            assertEquals(Collections.singletonList(50L), copy.getMajorIds());
            assertEquals(Collections.singletonList(501L), copy.getSubMajorIds());
            assertEquals(50L, copy.getMajorId());
            assertEquals(501L, copy.getSubMajorId());
            target.setSubMajorId(null);
            Course whole = ReflectionTestUtils.invokeMethod(service, "copyCourse", scheme, source, target);
            assertEquals(java.util.Arrays.asList(501L, 502L), whole.getSubMajorIds());
            assertNull(whole.getSubMajorId());
            assertNull(whole.getInvocationSubMajorIds());
            whole.setCourseAttr("1");
            java.util.Map<Long, String> names = new java.util.HashMap<>();
            names.put(501L, "Direction A");
            names.put(502L, "Direction B");
            com.doinner.csys.service.CourseMajorSelectionService.applyNames(whole, names);
            assertEquals("Direction A、Direction B", whole.getSubMajorName());
            assertEquals(java.util.Arrays.asList(10L, 20L), source.getMajorIds());
            assertEquals(java.util.Arrays.asList(101L, 202L), source.getSubMajorIds());
        } finally {
            SecurityContextHolder.remove();
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {DictContent.POSITION_FOUNDATION, DictContent.DUTY_POSITION})
    void sergeantDeduplicatesOnlySameModuleChildAndDirection(String child) {
        String module = DictContent.MILITARY_VOCATIONAL_EDUCATION;
        Course course = invoked(module, child, 11L);
        assertTrue(duplicate(course, module, child, 11L));
        assertFalse(duplicate(course, module, child, 12L));
        assertFalse(duplicate(course, module, child, null));
        assertFalse(duplicate(invoked("other", child, 11L), module, child, 11L));
        assertFalse(duplicate(invoked(module, "other", 11L), module, child, 11L));
        course.setSourceId(102L);
        assertFalse(duplicate(course, module, child, 11L));
    }

    @Test
    void missingSergeantDirectionDoesNotAllowRepeatedUnassignedCopies() {
        String module = DictContent.MILITARY_VOCATIONAL_EDUCATION;
        String child = DictContent.POSITION_FOUNDATION;
        assertTrue(duplicate(invoked(module, child, null), module, child, null));
        assertTrue(duplicate(invoked(module, child, null), module, child, 11L));
    }

    @ParameterizedTest
    @ValueSource(strings = {DictContent.POLITICAL_THEORY_NCO, DictContent.MILITARY_FOUNDATION_NCO})
    void otherSergeantChildrenStillIgnoreDirection(String child) {
        String module = DictContent.MILITARY_VOCATIONAL_EDUCATION;
        assertTrue(duplicate(invoked(module, child, 11L), module, child, 12L));
    }

    @Test
    void ordinaryModulesStillDeduplicateByModuleAndChild() {
        String module = DictContent.GENERAL_EDUCATION_COURSES_SCHEDULE;
        Course course = invoked(module, "child", 11L);
        assertTrue(duplicate(course, module, "child", 12L));
        assertFalse(duplicate(course, module, "other", 11L));
        assertFalse(duplicate(course, "other", "child", 11L));
    }

    @Test
    void professionalCourseDeduplicatesWithinItsOwnModule() {
        String module = DictContent.MAJOR_COURSES_SCHEDULE;
        Course course = invoked("legacy-module", "child", 11L);
        assertFalse(duplicate(course, module, null, 11L));
        assertFalse(duplicate(course, module, null, 12L));
        assertFalse(duplicate(course, module, null, null));
        assertTrue(duplicate(invoked(module, null, null), module, null, 11L));
        assertTrue(duplicate(invoked(module, null, null), module, null, null));
    }

    @ParameterizedTest
    @ValueSource(strings = {"2", "4"})
    void trainingAndProjectAllowDifferentDirectionsButNotWholeMajorDuplicates(String type) {
        Course row = invoked("module", "child", 11L);
        row.setType(type);
        row.setMajorId(10L);
        assertTrue(duplicate(row, "module", "child", 11L));
        assertFalse(duplicate(row, "module", "child", 12L));
        assertFalse(duplicate(row, "module", "child", null));
        row.setSubMajorId(null);
        assertTrue(duplicate(row, "module", "child", 12L));
        assertTrue(duplicate(row, "module", "child", null));
        assertTrue(duplicate(row, null, null, 12L), "Practice trees need not send source course-module fields");
        CourseTemplateVo target = new CourseTemplateVo();
        target.setMajorId(20L);
        target.setSubMajorId(12L);
        assertFalse(Boolean.TRUE.equals(ReflectionTestUtils.invokeMethod(service, "isCourseAlreadyInvoked",
                101L, Collections.singletonList(row), target)), "Another major remains independent");
        target.setMajorId(10L);
        target.setLocation("new-module");
        row.setLocation("old-module");
        assertFalse(Boolean.TRUE.equals(ReflectionTestUtils.invokeMethod(service, "isCourseAlreadyInvoked",
                101L, Collections.singletonList(row), target)), "Different training modules remain independent");
    }

    @ParameterizedTest
    @ValueSource(strings = {DictContent.POSITION_FOUNDATION, DictContent.DUTY_POSITION})
    void courseInvokeCopiesIntoAnotherDirectionAndRejectsItsRepeat(String child) {
        for (Field field : CourseServiceImpl.class.getDeclaredFields()) {
            if (field.getType().isInterface()) {
                ReflectionTestUtils.setField(service, field.getName(), mock(field.getType()));
            }
        }
        CoursePermissionService permissions = mock(CoursePermissionService.class);
        ReflectionTestUtils.setField(service, "coursePermissions", permissions);
        CourseMapper courses = (CourseMapper) ReflectionTestUtils.getField(service, "courseMapper");
        TrainingSchemeMapper schemes = (TrainingSchemeMapper) ReflectionTestUtils.getField(service, "trainingSchemeMapper");
        TrainingSchemeRefCourseMapper refs = (TrainingSchemeRefCourseMapper) ReflectionTestUtils.getField(service, "trainingSchemeRefCourseMapper");
        Course existing = invoked(DictContent.MILITARY_VOCATIONAL_EDUCATION, child, 11L);
        existing.setId(201L);
        List<Course> saved = new ArrayList<>(Collections.singletonList(existing));
        TrainingSchemeRefCourse ref = new TrainingSchemeRefCourse();
        ref.setCourseId(existing.getId());
        when(refs.selectTrainingSchemeRefCourseByTrainingSchemeVoId(1L)).thenReturn(Collections.singletonList(ref));
        Course source = new Course();
        source.setId(101L);
        source.setSubMajorId(99L);
        when(courses.selectCoursesByIds(anyList())).thenAnswer(call -> {
            List<Long> ids = call.getArgument(0);
            return ids.contains(101L) ? Collections.singletonList(source) : saved;
        });
        doAnswer(call -> {
            List<Course> copies = call.getArgument(0);
            copies.forEach(course -> course.setId(202L));
            saved.addAll(copies);
            return null;
        }).when(courses).insertCourseList(anyList());
        TrainingSchemeVo scheme = new TrainingSchemeVo();
        scheme.setId(1L);
        when(schemes.selectTrainingSchemeById(1L)).thenReturn(scheme);
        CourseTemplateVo request = new CourseTemplateVo();
        request.setIds(Collections.singletonList(101L));
        request.setTrainingSchemeId(1L);
        request.setCourseModule(existing.getCourseModule());
        request.setCourseModuleChildren(child);
        request.setSubMajorId(12L);
        SecurityContextHolder.setUserName("scope.test");
        try {
            assertEquals(200, service.courseInvoke(request).getCode());
            assertEquals(2, saved.size());
            assertEquals(12L, saved.get(1).getSubMajorId());
            assertEquals(child, saved.get(1).getCourseModuleChildren());
            assertEquals(101L, saved.get(1).getSourceId());
            assertNotEquals(200, service.courseInvoke(request).getCode());
            verify(courses, times(1)).insertCourseList(anyList());
            verify(permissions, times(2)).lockKnowledgeSources(request.getIds());
        } finally {
            SecurityContextHolder.remove();
        }
    }
}
