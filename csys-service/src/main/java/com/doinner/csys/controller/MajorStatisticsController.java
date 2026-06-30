package com.doinner.csys.controller;

import com.doinner.common.core.domain.DataSet;
import com.doinner.csys.constant.DomainFieldConstant;
import com.doinner.csys.domain.StandardMajor;
import com.doinner.csys.domain.statisticsVo.StatisticsExcelVo;
import com.doinner.csys.domain.vo.CategoryCountVo;
import com.doinner.csys.domain.vo.MajorCountVo;
import com.doinner.csys.domain.vo.SchemeCountVo;
import com.doinner.csys.io.utils.ExcelUtils;
import com.doinner.csys.service.MajorStatisticsService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import java.util.stream.Collectors;

@Api(tags = "统计页面")
@RestController
@RequestMapping("/statistics")
public class MajorStatisticsController {

    @Resource
    private MajorStatisticsService majorStatisticsService;

    @ApiOperation("专业统计")
    @GetMapping("/major/count")
    public DataSet<List<MajorCountVo>> majorCount(){
        return DataSet.success(majorStatisticsService.majorCount());
    }

    @ApiOperation("培养方案统计")
    @GetMapping("/scheme/count")
    public DataSet<List<SchemeCountVo>> schemeCount(){
        return DataSet.success(majorStatisticsService.schemeCount());
    }

    @ApiOperation("4大专业统计")
    @GetMapping("/systemType/count")
    public DataSet<List<CategoryCountVo>> systemTypeCount(){
        return DataSet.success(majorStatisticsService.systemTypeCount());
    }

    @ApiOperation("专业统计导出Excel")
    @GetMapping("/export/major/count")
    public void exportMajorCount(HttpServletResponse response){
        List<MajorCountVo> majorCountVos = majorStatisticsService.majorCount();
        List<List<String>> data = majorCountVos.stream().map(majorCountVo -> {
            return List.of(majorCountVo.getCollegeName(), String.valueOf(majorCountVo.getMajorNum()), String.valueOf(majorCountVo.getMinMajorNum()));
        }).collect(Collectors.toList());
        response.setContentType("application/x-download");
        try {
            response.setHeader("Content-disposition","attachment;filename*=UTF-8''" + URLEncoder.encode(DomainFieldConstant.MAJOR_COUNT_EXCEL_NAME, "utf-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        ExcelUtils.exportSimpleExcel(response, List.of("所属学院", "专业数","细分方向专业数"), data,true);
    }

    @ApiOperation("培养方案统计导出Excel")
    @GetMapping("/export/scheme/count")
    public void exportSchemeCount(HttpServletResponse response){
        List<SchemeCountVo> schemeCountVos = majorStatisticsService.schemeCount();
        List<StatisticsExcelVo> statisticsExcelVoList = schemeCountVos.parallelStream().map(StatisticsExcelVo::new).collect(Collectors.toList());
        response.setContentType("application/x-download");
        try {
            response.setHeader("Content-disposition","attachment;filename*=UTF-8''" + URLEncoder.encode(DomainFieldConstant.SCHEME_COUNT_EXCEL_NAME, "utf-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        ExcelUtils.simpleStatisticsExport(response, List.of("门类分类", "门类数量", "门类占比"), statisticsExcelVoList);
    }

}
