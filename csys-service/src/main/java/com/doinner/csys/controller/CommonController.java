package com.doinner.csys.controller;

import com.doinner.common.core.domain.DataSet;
import com.doinner.common.core.domain.DataTable;
import com.doinner.common.core.domain.Message;
import com.doinner.csys.constant.CourseConstant;
import com.doinner.csys.domain.StandardMajor;
import com.doinner.csys.service.CommonService;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * 通用Controller
 *
 * @author doinner
 */
@RestController
@RequestMapping("/common")
public class CommonController {

    private static final Logger log = LoggerFactory.getLogger(CommonController.class);

    @Resource
    private CommonService commonService;

    @ApiOperation("分析文档")
    @PostMapping("/analysis")
    public DataSet analysis(@RequestParam("id") Long id, @RequestParam("fileId") String fileId, @RequestParam("type") Long type) {
        if(ObjectUtils.isEmpty(id) || StringUtils.isEmpty(fileId)){
            return DataSet.error("缺少参数");
        }
        try {
            String result;
            if(CourseConstant.CUR_TYPE_THEORY.equals(type)){
                result = commonService.analysisTeachPlan(fileId,id);
            }else if(CourseConstant.CUR_TYPE_PRACTICE.equals(type)){
                result = commonService.analysisTeachPlanPractice(fileId,id);
            }else{
                result = commonService.analysisTeachPlanAll(fileId,id);
            }
            if(CourseConstant.ANALYSIS_SUCCESS.equals(result)){
                return DataSet.success(result);
            }else{
                return DataSet.error(result);
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            return DataSet.error(e.getMessage());
        }
    }

    @ApiOperation("查询专业按学院查询的一级专业")
    @GetMapping("/standardMajor/major/list/{collegeId}")
    public DataSet<List<StandardMajor>> selectMajorList(@PathVariable("collegeId") Long collegeId){
        return DataSet.success(commonService.selectStandardMajorListByCollegeId(collegeId));
    }

    @ApiOperation("查询细分专业")
    @GetMapping("/standardMajor/subMajor/list/{parentId}")
    public DataSet<List<StandardMajor>> selectSubMajorList(@PathVariable("parentId") Long parentId){
        return DataSet.success(commonService.selectStandardMajorListByParentId(parentId));
    }

    @ApiOperation("新增专业")
    @PostMapping("/standardMajor/major")
    public DataSet<StandardMajor> addStandardMajor(@RequestBody StandardMajor standardMajor){
        return DataSet.success(commonService.addStandardMajor(standardMajor));
    }

    @ApiOperation("修改专业")
    @PutMapping("/standardMajor/major")
    public DataSet<StandardMajor> updateStandardMajor(@RequestBody StandardMajor standardMajor){
        return DataSet.success(commonService.updateStandardMajor(standardMajor));
    }

    @ApiOperation("删除专业")
    @DeleteMapping("/standardMajor/major/{id}")
    public Message deleteStandardMajor(@PathVariable("id") Long id){
        commonService.deleteStandardMajor(id);
        return Message.success();
    }

    @ApiOperation("查询专业树状结构")
    @GetMapping("/standardMajor/major/tree")
    public DataTable treeSubMajorList(StandardMajor standardMajor){
        return DataTable.success(commonService.treeSubMajorList(standardMajor));
    }

    @ApiOperation("查询专业类下子专业")
    @GetMapping("/standardMajor/subMajorList")
    public DataTable getSubMajorList(){
        return DataTable.success(commonService.getSubMajorList());
    }
}
