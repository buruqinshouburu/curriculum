package com.doinner.csys;

import com.doinner.csys.dao.CourseMapper;
import com.doinner.csys.domain.vo.CourseVo;
import com.doinner.csys.service.impl.CurriculumServiceImpl;
import com.doinner.system.service.DoinnerDeptService;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class CourseCodeUpdateTest {
    private final CurriculumServiceImpl service = new CurriculumServiceImpl();
    private final CourseMapper mapper = mock(CourseMapper.class);

    private CourseVo course() {
        CourseVo course = new CourseVo();
        course.setVersion("2026");
        course.setEducationLevel("1");
        course.setCollegeId(Long.valueOf("1000"));
        course.setCollegeName("理学院");
        course.setCode("OLD-CODE");
        return course;
    }

    private void update(CourseVo old, CourseVo next) {
        ReflectionTestUtils.setField(service, "courseMapper", mapper);
        ReflectionTestUtils.invokeMethod(service, "updateCourseCode", old, next);
    }

    @Test void missingOldEducationWithSubmittedValueGeneratesCode() {
        CourseVo old = course();
        old.setEducationLevel(null);
        CourseVo next = course();
        update(old, next);
        assertEquals("26-JG-01-0001", next.getCode());
    }

    @Test void missingOldVersionDoesNotThrow() {
        CourseVo old = course();
        old.setVersion(null);
        CourseVo next = course();
        update(old, next);
        assertEquals("26-JG-01-0001", next.getCode());
    }

    @Test void equalDepartmentIdsDoNotRegenerate() {
        CourseVo next = course();
        update(course(), next);
        assertEquals("OLD-CODE", next.getCode());
        assertEquals("1", next.getEducationLevel());
        verify(mapper, never()).selectCoursesByCodeLike(anyString());
    }

    @Test void incompleteNewFieldsPreserveExistingCode() {
        CourseVo next = course();
        next.setEducationLevel(null);
        update(course(), next);
        assertEquals("OLD-CODE", next.getCode());
        assertEquals("1", next.getEducationLevel());
        verify(mapper, never()).selectCoursesByCodeLike(anyString());
    }

    @Test void omittedCodeDoesNotRegenerateUnchangedCourse() {
        CourseVo next = course();
        next.setCode(null);
        update(course(), next);
        assertEquals("OLD-CODE", next.getCode());
        verify(mapper, never()).selectCoursesByCodeLike(anyString());
    }

    @Test void bothEducationValuesMissingPreserveCodeWithoutDefaulting() {
        CourseVo old = course();
        old.setEducationLevel(null);
        CourseVo next = course();
        next.setEducationLevel(null);
        update(old, next);
        assertEquals("OLD-CODE", next.getCode());
        assertNull(next.getEducationLevel());
        verify(mapper, never()).selectCoursesByCodeLike(anyString());
    }

    @Test void missingCodeIsGeneratedWhenReady() {
        CourseVo old = course();
        old.setCode(null);
        CourseVo next = course();
        next.setCode(null);
        update(old, next);
        assertEquals("26-JG-01-0001", next.getCode());
    }

    @Test void missingDepartmentResponsePreservesCode() {
        DoinnerDeptService departments = mock(DoinnerDeptService.class);
        ReflectionTestUtils.setField(service, "doinnerDeptService", departments);
        CourseVo next = course();
        next.setCollegeName(null);
        next.setCollegeId(2000L);
        update(course(), next);
        assertEquals("OLD-CODE", next.getCode());
    }

    @Test void invalidShortVersionPreservesCode() {
        CourseVo next = course();
        next.setVersion("2");
        update(course(), next);
        assertEquals("OLD-CODE", next.getCode());
    }
}
