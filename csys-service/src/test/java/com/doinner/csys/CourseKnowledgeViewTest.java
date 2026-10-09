package com.doinner.csys;

import com.doinner.csys.dao.CourseMapper;
import com.doinner.csys.domain.vo.CourseKnowledgeViewVo;
import com.doinner.csys.service.impl.CourseServiceImpl;
import com.doinner.kg.domain.Dictionary;
import com.doinner.kg.service.RemoteKgService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import com.github.pagehelper.PageHelper;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CourseKnowledgeViewTest {
    private CourseServiceImpl service;
    private CourseMapper mapper;
    private RemoteKgService kg;

    @BeforeEach
    void setUp() {
        service = new CourseServiceImpl();
        mapper = mock(CourseMapper.class);
        kg = mock(RemoteKgService.class, RETURNS_DEEP_STUBS);
        ReflectionTestUtils.setField(service, "courseMapper", mapper);
        ReflectionTestUtils.setField(service, "remoteKgService", kg);
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(new MockHttpServletRequest()));
    }

    @AfterEach
    void tearDown() {
        PageHelper.clearPage();
        RequestContextHolder.resetRequestAttributes();
    }

    private CourseKnowledgeViewVo row(String module) {
        CourseKnowledgeViewVo row = new CourseKnowledgeViewVo();
        row.setCourseId("247");
        row.setCourseModelId(module);
        row.setUnitCount(2);
        row.setPointCount(5);
        return row;
    }

    @Test
    void missingDictionaryDoesNotBreakOtherRowsOrCounts() {
        CourseKnowledgeViewVo missing = row("missing");
        CourseKnowledgeViewVo valid = row("valid");
        List<CourseKnowledgeViewVo> rows = Arrays.asList(missing, valid);
        when(mapper.selectCourseKnowledgeList(247L)).thenReturn(rows);
        when(kg.getDictionary("missing").getData()).thenReturn(null);
        Dictionary dictionary = mock(Dictionary.class);
        when(dictionary.getName()).thenReturn("课程模块");
        when(kg.getDictionary("valid").getData()).thenReturn(dictionary);
        assertSame(rows, service.viewCourseKnowledgeList(247L));
        assertNull(missing.getCourseModelName());
        assertEquals("课程模块", valid.getCourseModelName());
        assertEquals(Integer.valueOf(2), missing.getUnitCount());
        assertEquals(Integer.valueOf(5), missing.getPointCount());
    }

    @Test
    void nullResponseDoesNotThrow() {
        when(mapper.selectCourseKnowledgeList(247L)).thenReturn(Collections.singletonList(row("missing")));
        when(kg.getDictionary("missing")).thenReturn(null);
        assertEquals(1, service.viewCourseKnowledgeList(247L).size());
    }

    @Test
    void blankModuleDoesNotCallDictionaryService() {
        when(mapper.selectCourseKnowledgeList(247L)).thenReturn(Arrays.asList(row(null), row(""), row("  ")));
        assertEquals(3, service.viewCourseKnowledgeList(247L).size());
        verifyNoInteractions(kg);
    }

    @Test
    void emptyCoursesReturnEmptyList() {
        when(mapper.selectCourseKnowledgeList(247L)).thenReturn(Collections.emptyList());
        assertTrue(service.viewCourseKnowledgeList(247L).isEmpty());
        verifyNoInteractions(kg);
    }
}
