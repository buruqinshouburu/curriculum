package com.doinner.csys.io.controller;

import com.doinner.csys.constant.DomainFieldConstant;
import com.doinner.csys.domain.vo.CourseExportVo;
import com.doinner.csys.domain.vo.MatrixVo;
import com.doinner.csys.entity.csys.model.CourseChooseStatusModel;
import com.doinner.csys.io.service.ExportService;
import com.doinner.common.core.utils.poi.ExcelUtil;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.net.URLEncoder;
import java.util.List;

@RestController
@RequestMapping
public class ExportController {

    @Resource
    private ExportService exportService;

    @ApiOperation("课程导出")
    @PostMapping("/course/export")
    public void importCourse(HttpServletResponse response, @RequestBody List<Long> courseIds){
        try {
            response.setContentType("application/x-download");
            String fileName = "教学计划.xlsx";
            response.setHeader("Content-disposition","attachment;filename*=UTF-8''" + URLEncoder.encode(fileName, "utf-8"));
            List<CourseExportVo> courseExportVoList = exportService.courseExportConvert(courseIds);
            ExcelUtil<CourseExportVo> util = new ExcelUtil<>(CourseExportVo.class);
            util.exportExcel(response, courseExportVoList,"教学计划");
        }catch (Exception e){
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }

    @ApiOperation("课程详细信息导出")
    @PostMapping("/course/data/export")
    public void importCourseData(HttpServletResponse response, @RequestBody List<Long> courseIds){
        try {
            response.setContentType("application/x-download");
            String fileName = DomainFieldConstant.EXCEL_FILE_NAME;
            response.setHeader("Content-disposition","attachment;filename*=UTF-8''" + URLEncoder.encode(fileName, "utf-8"));
            exportService.courseDataExportConvert(response, courseIds);
        }catch (Exception e){
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }

    @ApiOperation("矩阵图导出")
    @PostMapping("/matrix/export/{type}")
    public void importStandardCultivationTarget(HttpServletResponse response, Long id, @PathVariable("type") Integer type){
        try {
            response.setContentType("application/x-download");

            exportService.exportMatrix(response, id, type);
        }catch (Exception e){
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }

    @ApiOperation("矩阵图")
    @GetMapping("/matrix/{type}")
    public MatrixVo importStandardCultivationTarget(Long id, @PathVariable("type") Integer type){
        return exportService.assembleMatrix(id, type);
    }

    @ApiOperation("课程被选用情况表导出")
    @PostMapping("/course/chooseStatus/export")
    public void exportCourseChooseStatus(HttpServletResponse response, @RequestBody List<Long> sourceCourseIds){
        try {
            exportService.exportCourseChooseStatus(response, sourceCourseIds);
        }catch (Exception e){
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }

    @ApiOperation("毕业要求导出")
    @PostMapping("/graduation/export")
    public void exportGraduation(HttpServletResponse response, @RequestBody List<Long> ids){
        try {
            response.setContentType("application/x-download");
            String fileName = "毕业要求.xlsx";
            response.setHeader("Content-disposition","attachment;filename*=UTF-8''" + URLEncoder.encode(fileName, "utf-8"));
            exportService.exportGraduation(response, ids);
        }catch (Exception e){
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }


}
