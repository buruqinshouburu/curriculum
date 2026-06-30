package com.doinner.csys.controller;

import com.doinner.common.core.domain.DataSet;
import com.doinner.common.core.domain.DataTable;
import com.doinner.csys.constant.DomainFieldConstant;
import com.doinner.csys.domain.TrainingScheme;
import com.doinner.csys.domain.statisticsVo.*;
import com.doinner.csys.io.utils.ExcelUtils;
import com.doinner.csys.service.SchemeStatisticsService;
import com.google.common.collect.Lists;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 培养规划统计页面Controller
 *
 * @author doinner
 */
@RestController
@RequestMapping("/scheme")
@Api(value = "/scheme", tags = "scheme-controller")
public class SchemeStatisticsController {

    @Resource
    private SchemeStatisticsService schemeStatisticsService;

    //-------------------------------xf --------------------------------------------

    /**
     * 方案细分图
     */
    @ApiOperation("方案细分图")
    @GetMapping("/subject")
    public DataSet<List<TrainingScheme>> schemeSub(Long majorId) {
        List<TrainingScheme> trainingSchemeList = schemeStatisticsService.schemeSub(majorId);
        return DataTable.success(trainingSchemeList);

    }

    /**
     * 学分配比图
     */
    @ApiOperation("学分配比图")
    @GetMapping("/credit/scale")
    public DataSet<List<CreditStaticticsVo>> creditScale(Long schemeId) {
        List<CreditStaticticsVo> creditStaticticsVoList = schemeStatisticsService.statisticsCredit(schemeId);
        return DataTable.success(creditStaticticsVoList);

    }

    /**
     * 导出学分配比图
     */
    @ApiOperation("导出学分配比图")
    @GetMapping("/credit/scale/export")
    public void creditScaleExport(HttpServletResponse response, Long schemeId){
        List<CreditStaticticsVo> creditStaticticsVoList = schemeStatisticsService.statisticsCredit(schemeId);
        List<StatisticsExcelVo> statisticsExcelVoList = creditStaticticsVoList.parallelStream().map(StatisticsExcelVo::new).collect(Collectors.toList());
        response.setContentType("application/x-download");
        try {
            response.setHeader("Content-disposition","attachment;filename*=UTF-8''" + URLEncoder.encode(DomainFieldConstant.CREDIT_EXCEL_NAME, "utf-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        ExcelUtils.simpleStatisticsExport(response, List.of("所属学期", "学分数", "学分占比"), statisticsExcelVoList);

    }

    /**
     * 扩展导出学分配比图
     */
    @ApiOperation("扩展导出学分配比图")
    @PostMapping("/credit/scale/multi/export")
    public void creditScaleExport(HttpServletResponse response, @RequestBody List<Long> schemeIds){
        List<StatisticsExcelMultiVo> creditStaticticsVoList = schemeStatisticsService.statisticsCreditIn(schemeIds);
//        List<StatisticsExcelVo> statisticsExcelVoList = Lists.newArrayList();
//        List<StatisticsExcelVo> statisticsExcelVoList = creditStaticticsVoList.parallelStream().map(StatisticsExcelVo::new).collect(Collectors.toList());
        response.setContentType("application/x-download");
        try {
            response.setHeader("Content-disposition","attachment;filename*=UTF-8''" + URLEncoder.encode(DomainFieldConstant.CREDIT_EXCEL_NAME, "utf-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        ExcelUtils.simpleStatisticsMultiExport(response, List.of("培养方案名称","所属学期", "学分数", "学分占比"), creditStaticticsVoList);

    }

    /**
     * 选修必修比例
     */
    @ApiOperation("选修必修比例")
    @GetMapping("/courseType/scale")
    public DataSet<List<CourseTypeVo>> statisticsCourseType(Long schemeId){
        List<CourseTypeVo> creditStaticticsVoList = schemeStatisticsService.courseType(schemeId);
        return DataTable.success(creditStaticticsVoList);
    }

    /**
     * 导出选修必修比例
     */
    @ApiOperation("导出选修必修比例")
        @GetMapping("/courseType/scale/export")
    public void statisticsCourseTypeExport(HttpServletResponse response, Long schemeId){
        List<CourseTypeVo> courseTypeVoList = schemeStatisticsService.courseType(schemeId);
        List<StatisticsExcelVo> statisticsExcelVoList = courseTypeVoList.parallelStream().map(StatisticsExcelVo::new).collect(Collectors.toList());
        response.setContentType("application/x-download");
        try {
            response.setHeader("Content-disposition","attachment;filename*=UTF-8''" + URLEncoder.encode(DomainFieldConstant.COURSE_TYPE_EXCEL_NAME, "utf-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        ExcelUtils.complexStatisticsExport(response, List.of("所属学期", "课程占比统计"), List.of("小计", "其中：必修课", "其中：选修课"), statisticsExcelVoList);
    }

    /**
     * 扩展导出选修必修比例
     */
    @ApiOperation("扩展导出选修必修比例")
    @PostMapping("/courseType/scale/multi/export")
    public void statisticsCourseTypeExport(HttpServletResponse response, @RequestBody List<Long> schemeIds){
        List<StatisticsExcelMultiVo> courseTypeVoList = schemeStatisticsService.courseTypeIn(schemeIds);
//        List<StatisticsExcelVo> statisticsExcelVoList = courseTypeVoList.parallelStream().map(StatisticsExcelVo::new).collect(Collectors.toList());
        response.setContentType("application/x-download");
        try {
            response.setHeader("Content-disposition","attachment;filename*=UTF-8''" + URLEncoder.encode(DomainFieldConstant.COURSE_TYPE_EXCEL_NAME, "utf-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        ExcelUtils.complexStatisticsMultiExport(response, List.of("培养方案名称","所属学期", "课程占比统计"), List.of("小计", "其中：必修课", "其中：选修课"), courseTypeVoList);
    }

    //-------------------------------xf --------------------------------------------
    //-------------------------------xwy --------------------------------------------


    @GetMapping("targetPropUp/{id}")
    @ApiOperation("培养目标支持度")
    public DataSet<List<StandardCultivationTargetStatisticsVo>> statisticsTargetPropUpBySchemeId(@PathVariable("id") Long schemeId) {
        return DataSet.success(schemeStatisticsService.selectTargetPropUpBySchemeId(schemeId));
    }

    @GetMapping("schemeHours/{id}")
    @ApiOperation("讲授，实践课程比例")
    public DataSet<List<TrainingSchemeCourseScheduleStatisticsVo>> statisticsSchemeHours(@PathVariable("id") Long schemeId) {
        return DataSet.success(schemeStatisticsService.selectHoursBySchemeId(schemeId));
    }

    @ApiOperation("导出讲授，实践课程比例")
    @GetMapping("/schemeHours/export")
    public void statisticsSchemeHoursExport(HttpServletResponse response, Long schemeId) {
        List<TrainingSchemeCourseScheduleStatisticsVo> voList = schemeStatisticsService.selectHoursBySchemeId(schemeId);
        List<StatisticsExcelVo> statisticsExcelVoList = new ArrayList<>();
        for (TrainingSchemeCourseScheduleStatisticsVo vo : voList) {
            StatisticsExcelVo excelVo = new StatisticsExcelVo();
            excelVo.setName(DomainFieldConstant.TERM_NUMBER_NAME_MAP.get((vo.getTerm()).intValue()));
            excelVo.setNumber1((double) vo.getsTeachHours());
            excelVo.setNumber2((double) vo.getsPracticeHours());
            statisticsExcelVoList.add(excelVo);
        }
        response.setContentType("application/x-download");
        String fileName = DomainFieldConstant.SCHEME_HOURS_EXCEL_NAME;
        try {
            response.setHeader("Content-disposition", "attachment;filename*=UTF-8''" + URLEncoder.encode(fileName, "utf-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        ExcelUtils.complexStatisticsExport(response, List.of("所属学期", "课程占比统计"), List.of("小计", "其中：讲授课", "其中：实践课"), statisticsExcelVoList);
    }

    @ApiOperation("扩展导出讲授，实践课程比例")
    @PostMapping("/schemeHours/multi/export")
    public void statisticsSchemeHoursExport(HttpServletResponse response, @RequestBody List<Long> schemeIds) {
        List<StatisticsExcelMultiVo> voList = schemeStatisticsService.selectHoursBySchemeIdIn(schemeIds);
        response.setContentType("application/x-download");
        String fileName = DomainFieldConstant.SCHEME_HOURS_EXCEL_NAME;
        try {
            response.setHeader("Content-disposition", "attachment;filename*=UTF-8''" + URLEncoder.encode(fileName, "utf-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        ExcelUtils.complexStatisticsMultiExport(response, List.of("培养方案名称","所属学期", "课程占比统计"), List.of("小计", "其中：讲授课", "其中：实践课"), voList);
    }

    @GetMapping("/schemeType/{id}")
    @ApiOperation("课程比例分配图")
    public DataSet<List<StandardCultivationTargetStatisticsVo>> statisticsSchemeTypeBySchemeId(@PathVariable("id") Long schemeId) throws Exception {
        return DataSet.success(schemeStatisticsService.selectCourseTypeBySchemeId(schemeId));
    }

    @GetMapping("/schemeType/export")
    @ApiOperation("课程比例分配图")
    public void statisticsSchemeTypeExportBySchemeId(HttpServletResponse response, Long schemeId) throws Exception {
        List<StandardCultivationTargetStatisticsVo> voList = schemeStatisticsService.selectCourseTypeBySchemeId(schemeId);
        response.setContentType("application/x-download");
        String fileName = DomainFieldConstant.SCHEME_TYPE_EXCEL_NAME;
        try {
            response.setHeader("Content-disposition", "attachment;filename*=UTF-8''" + URLEncoder.encode(fileName, "utf-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        XSSFWorkbook xssfWorkbook = ExcelUtils.getSchemeType(voList);

        try (OutputStream outputStream = response.getOutputStream()) {
            xssfWorkbook.write(outputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @PostMapping("/schemeType/multi/export")
    @ApiOperation("扩展课程比例分配图")
    public void statisticsSchemeTypeExportBySchemeId(HttpServletResponse response, @RequestBody List<Long> schemeIds) throws Exception {
        List<StandardCultivationTargetStatisticsMultiVo> voList = schemeStatisticsService.selectCourseTypeBySchemeIdIn(schemeIds);
        response.setContentType("application/x-download");
        String fileName = DomainFieldConstant.SCHEME_TYPE_EXCEL_NAME;
        try {
            response.setHeader("Content-disposition", "attachment;filename*=UTF-8''" + URLEncoder.encode(fileName, "utf-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        XSSFWorkbook xssfWorkbook = ExcelUtils.getSchemeTypeMulti(voList);

        try (OutputStream outputStream = response.getOutputStream()) {
            xssfWorkbook.write(outputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    //-------------------------------xwy --------------------------------------------
}
