package com.doinner.csys;

import com.doinner.common.core.context.SecurityContextHolder;
import com.doinner.common.core.domain.DataSet;
import com.doinner.csys.dao.CourseMapper;
import com.doinner.csys.dao.CourseScheduleMapper;
import com.doinner.csys.domain.Course;
import com.doinner.csys.domain.vo.CourseVo;
import com.doinner.csys.service.impl.CurriculumServiceImpl;
import com.doinner.system.domain.entity.SysDept;
import com.doinner.system.service.DoinnerDeptService;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class PracticeImportCodeTest {
    @AfterEach
    void clearContext() {
        SecurityContextHolder.remove();
    }

    @Test
    void trainingImportAssignsSequentialCodesForTheSamePrefix() throws Exception {
        checkImport(true);
    }

    @Test
    void practiceImportAssignsSequentialCodesForTheSamePrefix() throws Exception {
        checkImport(false);
    }

    @Test
    void missingDepartmentDoesNotSilentlyCreateAnUnnumberedRecord() throws Exception {
        Fixture fixture = new Fixture();
        MockMultipartFile file = workbook(true, "不存在的单位", 1);
        assertThrows(RuntimeException.class, () -> fixture.service.importTraining(file, null, "2", 1, "2026"));
        verify(fixture.courses, never()).insertCourse(any());
    }

    private void checkImport(boolean training) throws Exception {
        Fixture fixture = new Fixture();
        MockMultipartFile file = workbook(training, "理学院", 2);
        if (training) {
            fixture.service.importTraining(file, null, "2", 1, "2026");
        } else {
            fixture.service.importPractice(file, null, "4", 1, "2026", null, null);
        }
        assertEquals(2, fixture.saved.size());
        assertEquals("26-JG-01-0001", fixture.saved.get(0));
        assertEquals("26-JG-01-0002", fixture.saved.get(1));
    }

    private MockMultipartFile workbook(boolean training, String department, int rows) throws Exception {
        try (XSSFWorkbook workbook = new XSSFWorkbook(); ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            org.apache.poi.ss.usermodel.Sheet sheet = workbook.createSheet();
            org.apache.poi.ss.usermodel.Row header = sheet.createRow(0);
            header.createCell(0).setCellValue(training ? "*课目名称" : "*项目名称");
            header.createCell(1).setCellValue("*组织实施单位");
            header.createCell(2).setCellValue("*适用对象");
            for (int i = 0; i < rows; i++) {
                org.apache.poi.ss.usermodel.Row row = sheet.createRow(i + 1);
                row.createCell(0).setCellValue("测试" + (i + 1));
                row.createCell(1).setCellValue(department);
                row.createCell(2).setCellValue("生长军官学员");
            }
            workbook.write(output);
            return new MockMultipartFile("file", "test.xlsx",
                    "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", output.toByteArray());
        }
    }

    private static class Fixture {
        final CurriculumServiceImpl service = new CurriculumServiceImpl();
        final CourseMapper courses = mock(CourseMapper.class);
        final List<String> saved = new ArrayList<>();

        Fixture() {
            SecurityContextHolder.setUserName("test");
            DoinnerDeptService departments = mock(DoinnerDeptService.class);
            SysDept dept = new SysDept();
            dept.setDeptId(10L);
            dept.setDeptName("理学院");
            when(departments.list(any())).thenReturn(DataSet.success(Collections.singletonList(dept)));
            when(courses.selectCoursesByCodeLike(anyString())).thenAnswer(call -> {
                List<Course> existing = new ArrayList<>();
                for (String code : saved) {
                    if (code != null && code.startsWith(call.getArgument(0))) {
                        Course item = new Course();
                        item.setCode(code);
                        existing.add(item);
                    }
                }
                return existing;
            });
            when(courses.insertCourse(any(CourseVo.class))).thenAnswer(call -> {
                CourseVo course = call.getArgument(0);
                saved.add(course.getCode());
                course.setId((long) saved.size());
                return 1;
            });
            ReflectionTestUtils.setField(service, "courseMapper", courses);
            ReflectionTestUtils.setField(service, "courseScheduleMapper", mock(CourseScheduleMapper.class));
            ReflectionTestUtils.setField(service, "doinnerDeptService", departments);
        }
    }
}
