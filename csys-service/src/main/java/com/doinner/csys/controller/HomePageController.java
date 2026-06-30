package com.doinner.csys.controller;

import com.doinner.common.core.domain.DataSet;
import com.doinner.common.core.domain.Message;
import com.doinner.common.core.utils.poi.ExcelUtil;
import com.doinner.csys.constant.DomainFieldConstant;

import com.doinner.csys.domain.StandardAbility;
import com.doinner.csys.domain.StandardMajor;
import com.doinner.csys.domain.TrainingSchemeWeek;
import com.doinner.csys.domain.vo.*;
import com.doinner.csys.io.utils.ExcelUtils;
import com.doinner.csys.service.CurriculumService;
import com.doinner.csys.service.HomePageService;
import com.doinner.common.core.utils.PageUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * @author wzg
 * @date 2023/4/3 10:18
 */
@RestController
@RequestMapping("/homePage")
@Api(value = "/homePage", tags = "homePage-controller")
public class HomePageController {

    @Resource
    private HomePageService homePageService;

    @Resource
    private CurriculumService courseService;

    /**
     * 学院的课程和专业统计
     */
    @GetMapping("/courseAndSpecialized")
    @ApiOperation("学院的课程和专业统计")
    public DataSet<List<CourseAndSpecializedVo>> courseStatistics() {
        return DataSet.success(homePageService.courseAndSpecializedStatistics());
    }

    /**
     * 查询细分门类对应的专业
     */
    @GetMapping("/major/category/{categoryId}")
    @ApiOperation("查询细分门类对应的专业")
    public DataSet<List<StandardMajor>> selectMajorByCategories(@PathVariable("categoryId") Long categoryId) {
        return DataSet.success(homePageService.selectMajorBySubCategory(categoryId));
    }

    /**
     * 查询大门类对应的专业
     */
    @GetMapping("/major/{systemId}")
    @ApiOperation("查询大门类对应的专业")
    public DataSet<List<StandardMajor>> categoryAndSchedule(@PathVariable("systemId") Long systemId) {
        return DataSet.success(homePageService.selectMajorBySystemId(systemId));
    }

    /**
     * 全部方案及课程统计
     */
    @GetMapping("/schemeAndCourse/count")
    @ApiOperation("全部方案及课程统计")
    public DataSet<Map<String, Long>> countSchemeAndCourse() {
        return DataSet.success(homePageService.countSchemeAndCourse());
    }


    /**
     * 单个学院总课程和课时统计
     */
    @GetMapping("/classHour/{collegeId}")
    @ApiOperation("总课程和课时统计")
    public DataSet<List<HourStatisticsVo>> classHourStatisticsByCollegeId(@PathVariable("collegeId") Long collegeId) {
        return DataSet.success(homePageService.classHourStatistics(collegeId));
    }

    /**
     * 全部学院总课程和课时统计
     */
    @GetMapping("/classHour")
    @ApiOperation("总课程和课时统计")
    public DataSet<List<HourStatisticsVo>> classHourStatistics() {
        return DataSet.success(homePageService.classAllHourStatistics());
    }

    /**
     * 导出课程排行榜
     */
    @GetMapping("/ranking")
    @ApiOperation("导出课程排行榜")
    public void courseRanking(HttpServletResponse response) {
        List<TrainingSchemeCourseScheduleRankingVo> trainingSchemeCourseScheduleRankingVoList = courseService.courseRanking();
        List<List<String>> data = trainingSchemeCourseScheduleRankingVoList.parallelStream().map(trainingSchemeCourseScheduleRankingVo -> {
            return List.of(trainingSchemeCourseScheduleRankingVo.getCourseName(), trainingSchemeCourseScheduleRankingVo.getSelectedNum().toString());
        }).collect(Collectors.toList());
        response.setContentType("application/x-download");
        try {
            response.setHeader("Content-disposition", "attachment;filename*=UTF-8''" + URLEncoder.encode(DomainFieldConstant.COURSE_SELECT_EXCEL_NAME, "utf-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        ExcelUtils.exportSimpleExcel(response, List.of("课程名称", "选用次数"), data,false);
    }

    /**
     * 培养目标词云图
     */
    @GetMapping("/wordCloud/standard/target")
    @ApiOperation("培养目标词云图")
    public DataSet<Map<String, AtomicInteger>> standardTargetWordCloud(@RequestParam(defaultValue = "10")
                                                                         Integer limit) {
        Map<String, AtomicInteger> standardTargetWordCloudMap = homePageService.standardTargetWordCloud(limit);
        return DataSet.success(standardTargetWordCloudMap);
    }

    /**
     * 毕业标准词云图
     */
    @GetMapping("/wordCloud/standard/graduation")
    @ApiOperation("毕业标准词云图")
    public DataSet<Map<String, Map<String, AtomicInteger>>> standardGraduationWordCloud(@RequestParam(defaultValue = "10")
                                                                                          Integer limit) {
        Map<String, Map<String, AtomicInteger>> standardGraduationWordCloudMap = homePageService.standardGraduationWordCloud(limit);
        return DataSet.success(standardGraduationWordCloudMap);
    }

    /**
     * 培养目标词云图
     */
    @PostMapping("/wordCloud/standard/target/export")
    @ApiOperation("导出培养目标词云图")
    public void standardTargetWordCloudExport(HttpServletResponse response) {
        response.setContentType("application/x-download");
        try {
            response.setHeader("Content-disposition", "attachment;filename*=UTF-8''" + URLEncoder.encode(DomainFieldConstant.WORD_CLOUD_STANDARD_TARGET_EXCEL_NAME, "utf-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        homePageService.standardTargetWordCloudExport(response);
    }

    /**
     * 毕业标准词云图
     */
    @PostMapping("/wordCloud/standard/graduation/export")
    @ApiOperation("导出毕业标准词云图")
    public void standardGraduationWordCloudExport(HttpServletResponse response) {
        response.setContentType("application/x-download");
        try {
            response.setHeader("Content-disposition", "attachment;filename*=UTF-8''" + URLEncoder.encode(DomainFieldConstant.WORD_CLOUD_STANDARD_GRADUATION_EXCEL_NAME, "utf-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        homePageService.standardGraduationWordCloudExport(response);
    }

    @GetMapping("/selectWeekBySchemeId/{schemeId}")
    @ApiOperation("修业时间分配情况")
    public DataSet<TrainingSchemeWeek> selectWeekBySchemeId(@PathVariable("schemeId") Long schemeId) {
        return DataSet.success(homePageService.selectWeekBySchemeId(schemeId));
    }

    @PutMapping("/selectWeekBySchemeId")
    @ApiOperation("修改修业时间分配情况")
    public Message TrainingSchemeWeek(@RequestBody TrainingSchemeWeek trainingSchemeWeek) {
        homePageService.updateTrainingSchemeWeek(trainingSchemeWeek);
        return Message.success();
    }

    @GetMapping("/selectCourseQuoteInfo")
    @ApiOperation("公共课程承载情况")
    public DataSet< Map<String,Object>> selectCourseQuoteInfo(@RequestParam("version") String version) {
        return DataSet.success(homePageService.selectCourseQuoteInfo(version));
    }

    @PostMapping("/selectCourseQuoteInfoDetail")
    @ApiOperation("公共课程承载情况详情")
    public DataSet<List<OverQuoteCourseInfo>> selectCourseQuoteInfoDetail(@RequestBody List<Long> ids) {
        return DataSet.success(homePageService.selectCourseQuoteInfoDetail(ids));
    }
    @ApiOperation("公共课程承载情况详情导出")
    @PostMapping("/courseQuoteInfo/export")
    public void courseQuoteInfo(HttpServletResponse response, @RequestBody List<Long> ids){
        try {
            response.setContentType("application/x-download");
            String fileName = "公共课程承载情况详情.xlsx";
            response.setHeader("Content-disposition","attachment;filename*=UTF-8''" + URLEncoder.encode(fileName, "utf-8"));
            List<OverQuoteCourseInfo> overQuoteCourseInfos = homePageService.selectCourseQuoteInfoDetail(ids);
            ExcelUtil<OverQuoteCourseInfo> util = new ExcelUtil<>(OverQuoteCourseInfo.class);
            util.exportExcel(response, overQuoteCourseInfos,"公共课程承载情况详情");
        }catch (Exception e){
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }
}
