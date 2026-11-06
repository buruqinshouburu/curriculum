package com.doinner.csys.controller;

import com.doinner.common.core.utils.poi.ExcelUtil;
import com.doinner.csys.domain.vo.TeachingPlanTextbookExportVo;
import com.doinner.csys.service.TeachingPlanTextbookExportService;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@RestController
@RequestMapping("/teachingPlan/textbook")
public class TeachingPlanTextbookExportController {
    @Resource
    private TeachingPlanTextbookExportService textbookExportService;

    @ApiOperation("导出当前教学计划教材信息")
    @PostMapping("/export")
    public void export(@RequestParam("planId") Long planId, HttpServletResponse response) {
        if (planId <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "教学计划ID必须为正数");
        }
        List<TeachingPlanTextbookExportVo> rows = textbookExportService.listForExport(planId);
        new ExcelUtil<>(TeachingPlanTextbookExportVo.class).exportExcel(response, rows, "教学计划教材信息");
    }
}
