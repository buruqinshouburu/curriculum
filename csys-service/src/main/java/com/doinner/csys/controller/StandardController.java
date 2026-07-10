package com.doinner.csys.controller;

import com.doinner.common.core.constant.ExceptionConstants;
import com.doinner.common.core.domain.DataSet;
import com.doinner.common.core.domain.DataTable;
import com.doinner.common.core.domain.Message;
import com.doinner.common.core.utils.PageUtils;
import com.doinner.common.core.web.controller.BaseController;
import com.doinner.csys.constant.StandardConstant;
import com.doinner.csys.domain.*;
import com.doinner.csys.domain.vo.*;
import com.doinner.csys.service.StandardService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/standard")
@Api(value = "/standard", tags = "standard-controller")
@Validated
public class StandardController extends BaseController {

    private static final Logger log = LoggerFactory.getLogger(StandardController.class);

    @Resource
    private StandardService stdService;

    /**
     * 查询能力素质集合
     */
    @GetMapping("/ability/list")
    @ApiOperation("查询能力素质集合")
    public DataTable<StandardAbility> selectStdAbilityList(StandardAbility standardAbility) {
        PageUtils.startPage();
        List<StandardAbility> list = stdService.selectStandardAbilityList(standardAbility);
        return PageDataTable.success(list);
    }

    /**
     * 查询能力素质集合
     */
    @GetMapping("/ability/course/tree")
    @ApiOperation("查询能力素质树")
    public DataSet<List<StandardTreeVo>> selectStdAbilityTree(StandardAbility standardAbility) {
        return DataSet.success(stdService.selectStdAbilityTree(standardAbility));
    }

    /**
     * 查询能力素质
     */
    @GetMapping("/ability/{id}")
    @ApiOperation("查询能力素质")
    public DataSet<StandardAbility> selectStdAbilityByName(@PathVariable("id") Long id) {
        StandardAbility standardAbility = stdService.selectStandardAbilityById(id);
        return DataSet.success(standardAbility);
    }

    /**
     * 新增能力素质
     */
    @PostMapping("/ability")
    @ApiOperation("新增能力素质")
    public DataSet<StandardAbility> insertStdAbility(@RequestBody StandardAbility standardAbility) {
        if (standardAbility.getType() == null) {
            standardAbility.setType(StandardConstant.ABILITY_TYPE_QUALITY);
        }
        return DataSet.success(stdService.insertStandardAbility(standardAbility));
    }

    /**
     * 新增能力体系
     */
    @PostMapping("/ability/system")
    @ApiOperation("新增能力体系")
    public DataSet<StandardAbility> insertAbilitySystem(@RequestBody StandardAbility standardAbility) {
        if (standardAbility.getType() == null) {
            standardAbility.setType(StandardConstant.ABILITY_TYPE_SYSTEM);
        }
        return DataSet.success(stdService.insertAbilitySystem(standardAbility));
    }


    /**
     * 修改能力素质
     */
    @PutMapping("/ability")
    @ApiOperation("修改能力素质")
    public DataSet<StandardAbility> updadteStdAbility(@RequestBody StandardAbility standardAbility) {
        return DataSet.success(stdService.updateStandardAbility(standardAbility));
    }

    @PostMapping("/ability/tree")
    @ApiOperation("修改新增能力素质")
    public DataSet<StandardAbility> updateStdAbilityTree(@RequestBody StandardAbility standardAbility) {
        return DataSet.success(stdService.updateStdAbilityTree(standardAbility));
    }

    @PostMapping("/ability/byTemplate/tree")
    @ApiOperation("根据模板新增能力素质")
    public Message insertStandardTemplateAbilityVo(@RequestBody StandardTemplateAbilityVo standardAbility) {
        stdService.insertStandardTemplateAbilityVo(standardAbility);
        return Message.success();
    }

    /**
     * 删除能力素质
     */
    @DeleteMapping("/ability/{id}")
    @ApiOperation("删除能力素质")
    public Message deleteStdAbilityById(@NotNull @PathVariable("id") Long id) {
        stdService.deleteStandardAbilityById(id);
        return Message.success();
    }


    /**
     * 导出能力图谱
     * @param response
     * @param ids
     */
    @PostMapping("/ability/export")
    public void exportAbility(HttpServletResponse response, @RequestBody(required = false) List<Long> ids) {
        stdService.exportAbility(response,ids);
    }

    /**
     * 导入能力图谱
     * @param file
     * @return
     */
    @PostMapping("/ability/import")
    public Message importAbility(MultipartFile file,Long collegeId,Long categoryId,Long majorId,Long subMajorId,String version) {
        return stdService.importAbility(file,collegeId,categoryId,majorId,subMajorId,version);
    }

    /**
     * @param
     * @return
     */
    @RequestMapping("/ability/template")
    public void exportAbilityTemplate(HttpServletResponse response) {
        stdService.exportAbilityTemplate(response);
    }

    /**
     * 导出素质图谱
     * @param response
     * @param ids
     */
    @PostMapping("/quality/export")
    public void exportQuality(HttpServletResponse response, @RequestBody(required = false) List<Long> ids) {
        stdService.exportQuality(response,ids);
    }

    /**
     * 导入素质图谱
     * @param file
     * @return
     */
    @PostMapping("/quality/import")
    public Message importQuality(MultipartFile file,Long collegeId,Long categoryId,Long majorId,Long subMajorId,String version) {
        return stdService.importQuality(file,collegeId,categoryId,majorId,subMajorId,version);
    }

    /**
     * @param
     * @return
     */
    @RequestMapping("/quality/template")
    public void exportQualityTemplate(HttpServletResponse response) {
        stdService.exportQualityTemplate(response);
    }

//    /**
//     * 删除能力素质集合
//     */
    @DeleteMapping("/ability/batch")
    @ApiOperation("删除能力素质集合")
    public Message deleteStdAbilityByIds(@RequestBody Long[] ids) {
        stdService.deleteStandardAbilityByIds(ids);
        return Message.success();
    }

    /**
     * 查询能力的所有子分支
     */
    @GetMapping("/ability/all/{id}")
    @ApiOperation("查询能力的所有子分支")
    public DataSet<List<StandardAbility>> selectAllStdAbilityById(@PathVariable("id") Long id) {
        return DataSet.success(stdService.selectAllStdAbilityById(id));
    }


    @GetMapping("/ability/graduation/{id}")
    @ApiOperation("查询能力的所有子分支 并且把毕业要求也做成树结构")
    public DataSet<List<StandardAbility>> selectAllStdAbilityAndGraduationById(@PathVariable("id") Long id) {
        return DataSet.success(stdService.selectAllStdAbilityAndGraduationById(id));
    }

    /**
     * 能力体系中，保存能力等级
     */
    @PutMapping("/saveAbilityLevel")
    @ApiOperation("保存能力等级")
    public DataSet<StandardAbilityLevel> saveStdAbilityLevel(@RequestBody StandardAbilityLevel level) {
        return DataSet.success(stdService.saveStdAbilityLevel(level));
    }

    /*
     * -------------------------------------------
     *          能力素质---代码段结束
     * -------------------------------------------
     */

    /*
     * -------------------------------------------
     * 培养目标---代码段开始
     * -------------------------------------------
     */


    @GetMapping("/cultivationTarget/list")
    @ApiOperation("查询培养目标集合")
    public DataTable<StandardCultivationTarget> selectStdCultivationTargetListBy(StandardCultivationTarget standardCultivationTarget) {
        PageUtils.startPage();
        List<StandardCultivationTarget> list = stdService.selectStandardCultivationTargetList(standardCultivationTarget);
        return PageDataTable.success(list);
    }


    @GetMapping("/cultivationTarget/{id}")
    @ApiOperation("查询单个培养目标")
    public DataSet<StandardCultivationTarget> selectStdCultivationTargetById(@PathVariable("id") Long id) {
        return DataSet.success(stdService.selectStandardCultivationTargetById(id));
    }

    @GetMapping("/cultivationTarget/all/{id}")
    @ApiOperation("查询培养目标子数据")
    public DataSet<List<StandardCultivationTarget>> selectStdCultivationTargetAll(@PathVariable("id") Long id) {
        return DataSet.success(stdService.selectStdCultivationTargetAll(id));
    }

    @GetMapping("/cultivationTarget/allByTrainingId/{trainingSchemeId}")
    @ApiOperation("根据培养方案ID查询培养目标子数据")
    public DataSet<List<StandardCultivationTarget>> selectStdCultivationTargetAllByTrainingId(@PathVariable("trainingSchemeId") Long trainingSchemeId) {
        return DataSet.success(stdService.selectStdCultivationTargetAllByTrainingId(trainingSchemeId));
    }


    /**
     * 新增培养目标
     */
    @PostMapping("/cultivationTarget")
    @ApiOperation("新增培养目标")
    public DataSet<StandardCultivationTarget> insertStdCultivationTarget(@RequestBody StandardCultivationTarget standardCultivationTarget) {
        return DataSet.success(stdService.insertStandardCultivationTarget(standardCultivationTarget));
    }

    /**
     * 修改培养目标
     */
    @PutMapping("/cultivationTarget")
    @ApiOperation("修改培养目标")
    public DataSet<StandardCultivationTarget> updadteStdCultivationTarget(@RequestBody StandardCultivationTarget standardCultivationTarget) {
        StandardCultivationTarget result = stdService.updateStandardCultivationTarget(standardCultivationTarget);
        if(ObjectUtils.isEmpty(result)){
            return DataSet.error("已配置毕业要求支撑关系无法删除");
        }
        return DataSet.success(result);
    }

    /**
     * 删除培养目标
     */
    @DeleteMapping("/cultivationTarget/{id}")
    @ApiOperation("删除培养目标")
    public Message deleteStdCultivationTargetById(@PathVariable("id") Long id) {
        return   stdService.deleteStandardCultivationTargetById(id);

    }

    /**
     * 批量删除培养目标
     */
    @DeleteMapping("/cultivationTarget/deleteBatch")
    @ApiOperation("批量删除培养目标")
    public Message deleteBatchStdCultivationTargetById(@RequestBody List<Long> ids) {
        return   stdService.deleteBatchStdCultivationTargetById(ids);

    }


    @GetMapping("/cultivationTargetByMajorId")
    @ApiOperation("根据专业查询培养目标")
    public DataSet<List<StandardCultivationTarget>> selectCultivationTargetByMajorId(Long majorId,String version) {
        List<StandardCultivationTarget> list = stdService.selectCultivationTargetByMajorId(majorId,version);
        return DataSet.success(list);
    }
    /**
     * -------------------------------------------
     *          培养目标---代码段结束
     * -------------------------------------------
     */
    /**
     * -------------------------------------------
     * 毕业标准---代码段开始
     * -------------------------------------------
     */

    @GetMapping("/graduation/list")
    @ApiOperation("查询毕业标准集合")
    public DataTable<StandardGraduation> selectStdGraduationListBy(StandardGraduation standardGraduation) {
        PageUtils.startPage();
        List<StandardGraduation> list = stdService.selectStandardGraduationList(standardGraduation);
        return PageDataTable.success(list);
    }

    @GetMapping("/graduation/scheme/list")
    @ApiOperation("根据培养方案id查询毕业标准集合")
    public DataSet<List<StandardGraduation>> selectStdGraduationSchemeListBy(StandardGraduation standardGraduation) {
        List<StandardGraduation> list = stdService.selectStdGraduationSchemeListBy(standardGraduation);
        return DataSet.success(list);
    }

    @GetMapping("/graduation/tree")
    @ApiOperation("查询毕业标准集合树结构")
    public DataSet<List<GraduationTreeVo>> selectStdGraduationTreeBy(StandardGraduation standardGraduation) {
        return DataSet.success(stdService.selectStdGraduationTreeBy(standardGraduation));
    }


    @GetMapping("/graduation/{id}")
    @ApiOperation("查询单个毕业标准")
    public DataSet<StandardGraduation> selectStdGraduationById(@PathVariable("id") Long id) {
        return DataSet.success(stdService.selectStandardGraduationById(id));
    }

    @GetMapping("/graduation/all/{id}")
    @ApiOperation("查询毕业标准子数据")
    public DataSet<List<StandardGraduation>> selectStdGraduationAll(@PathVariable("id") Long id) {
        return DataSet.success(stdService.selectStandardGraduationAll(id));
    }

    @GetMapping("/graduation/refAll/{id}")
    @ApiOperation("查询毕业标准和培养目标子数据")
    public DataSet selectStdGraduationRefAll(@PathVariable("id") Long id,Long isRe) {
        if(0==isRe){
            return DataSet.success(stdService.selectStandardGraduationRefAll(id));
        }
        return DataSet.success(stdService.reSelectStandardGraduationRefAll(id));
    }

    /**
     * 新增毕业标准
     */
    @PostMapping("/graduation")
    @ApiOperation("新增毕业标准")
    public DataSet<StandardGraduation> insertStdGraduation(@RequestBody StandardGraduation standardGraduation) {
        return DataSet.success(stdService.insertStandardGraduation(standardGraduation));
    }

    /**
     * 修改能力素质
     */
    @PutMapping("/graduation")
    @ApiOperation("修改毕业标准")
    public DataSet<StandardGraduation> updateStdGraduation(@RequestBody StandardGraduation standardGraduation) {
        StandardGraduation graduation = stdService.updateStandardGraduation(standardGraduation);
        if(ObjectUtils.isEmpty(graduation)){
            return DataSet.error("已配置课程支持关系，无法修改");
        }
        return DataSet.success(graduation);
    }

    /**
     * 删除毕业标准
     */
    @DeleteMapping("/graduation/{id}")
    @ApiOperation("删除毕业标准")
    public Message deleteStdGraduationById(@NotNull(message = ExceptionConstants.ID_MUST_NOT_NULL) @PathVariable("id") Long id) {
        return    stdService.deleteStandardGraduationById(id);

    }

    /**
     * 批量删除毕业标准
     */
    @DeleteMapping("/graduation/deleteBatch")
    @ApiOperation("批量删除毕业标准")
    public Message deleteBatchStdGraduationById(@RequestBody List<Long> ids) {
        return    stdService.deleteBatchStdGraduationById(ids);

    }

    @GetMapping("/graduation/ref/{id}")
    @ApiOperation("毕业标准配置预览")
    public DataSet<StandardGraduationRefTargetListVo> selectStandardCultivationTargetByGraduationId(@PathVariable("id") Long id) {
        return DataSet.success(stdService.selectStandardCultivationTargetByGraduationId(id));
    }

    @GetMapping("/graduation/refNode/{id}")
    @ApiOperation("根据毕业标准节点ID查询被选中培养目标")
    public DataTable<Long> selectTargetListByGraduationId(@PathVariable("id") Long id) {
        return PageDataTable.success(stdService.selectTargetListByGraduationId(id));
    }

    @PostMapping("/graduation/ref")
    @ApiOperation("毕业标准配置")
    public Message insetGraduationRefTarget(@RequestBody TowerToTower towerToTower) {
        stdService.insetGraduationRefTarget(towerToTower);
        return Message.success();
    }

    @PostMapping("/graduation/byTemplate/tree")
    @ApiOperation("根据模板新增毕业标准")
    public Message insertStandardTemplateGraduationVo(@RequestBody StandardTemplateGraduationVo graduationVo) {
        stdService.insertStandardTemplateGraduationVo(graduationVo);
        return Message.success();
    }


    @PostMapping("/graduation/issue")
    @ApiOperation("毕业要求下发知识能力素质")
    public Message graduationIssueKnowledgeAbilityQuality(@RequestBody GraduationIssueVo graduationVo) {
        Message message=stdService.graduationIssueKnowledgeAbilityQuality(graduationVo);
        return message;
    }


    @GetMapping("/graduation/overview/tree")
    @ApiOperation("总览树")
    public DataSet<TreeVo> graduationOverviewTree(Long majorId,String version,Integer type) {
       return DataSet.success(stdService.graduationOverviewTree(majorId,version,type));
    }


    @PostMapping("/graduation/binding/scheme")
    @ApiOperation("毕业要求绑定培养方案")
    public Message graduationBindingScheme(@RequestBody GraduationBindingSchemeVo graduationBindingSchemeVo) {
        stdService.graduationBindingScheme(graduationBindingSchemeVo);
        return Message.success();
    }

    @GetMapping("/graduation/courseSupport/{schemeId}")
    @ApiOperation("根据培养方案id查询毕业要求与课程支撑矩阵")
    public DataSet<GraduationCourseSupportVo> selectGraduationCourseSupport(@PathVariable("schemeId") Long schemeId) {
        return DataSet.success(stdService.selectGraduationCourseSupport(schemeId));
    }

    @GetMapping("/graduation/courseSupport/export")
    @ApiOperation("根据培养方案id导出毕业要求与课程支撑矩阵")
    public void exportGraduationCourseSupport(HttpServletResponse response, Long schemeId) {
        stdService.exportGraduationCourseSupport(response, schemeId);
    }






    /**
     * -------------------------------------------
     *          毕业标准---代码段结束
     * -------------------------------------------
     */
    /**
     * -------------------------------------------
     *          培养标准---代码段开始
     * -------------------------------------------
     */

    /**
     * 查询培养标准集合
     */
    @GetMapping("/cultivation/list")
    @ApiOperation("查询培养标准集合")
    public DataTable<StandardCultivation> selectStdCultivationList(StandardCultivation standardCultivation) {
        PageUtils.startPage();
        List<StandardCultivation> list = stdService.selectStandardCultivationList(standardCultivation);
        return PageDataTable.success(list);
    }

    /**
     * 查询培养标准
     */
    @GetMapping("/cultivation/{id}")
    @ApiOperation("查询培养标准")
    public DataSet<StandardCultivation> selectStdCultivationById(@PathVariable("id") Long id) {
        StandardCultivation standardCultivation = stdService.selectStandardCultivationById(id);
        return DataSet.success(standardCultivation);
    }

    /**
     * 查询培养标准
     */
    @GetMapping("/cultivation/all/{id}")
    @ApiOperation("查询培养标准子数据")
    public DataTable<StandardCultivation> selectStdCultivationAll(@PathVariable("id") Long id) {
        List<StandardCultivation> standardCultivationList = stdService.selectStandardCultivationAll(id);
        return PageDataTable.success(standardCultivationList);
    }
    /**
     * 查询培养标准
     */
    @GetMapping("/cultivation/refAll/{id}")
    @ApiOperation("查询培养标准和毕业标准子数据")
    public DataTable selectStdCultivationRefAll(@PathVariable("id") Long id,Long isRe) {
        if(0==isRe){
            return PageDataTable.success(stdService.selectStdCultivationRefAll(id));
        }else {
            return PageDataTable.success(stdService.reSelectStdCultivationRefAll(id));
        }


    }

    /**
     * 新增培养标准
     */
    @PostMapping("/cultivation")
    @ApiOperation("新增培养标准")
    public DataSet<StandardCultivation> insertStdCultivation(@RequestBody StandardCultivation standardCultivation) {
        return DataSet.success(stdService.insertStandardCultivation(standardCultivation));
    }

    /**
     * 修改培养标准
     */
    @PutMapping("/cultivation")
    @ApiOperation("修改培养标准")
    public DataSet<StandardCultivation> updadteStdCultivation(@RequestBody StandardCultivation standardCultivation) {
        return DataSet.success( stdService.updateStandardCultivation(standardCultivation));
    }

    /**
     * 删除培养标准
     */
    @DeleteMapping("/cultivation/{id}")
    @ApiOperation("删除培养标准")
    public Message deleteStdCultivationById( @PathVariable("id") Long id) {
        return  stdService.deleteStandardCultivationById(id);
    }

    @GetMapping("/cultivation/ref/{id}")
    @ApiOperation("培养标准置预览")
    public DataSet<StandardCultivationRefGraduationListVo> selectStandardCultivationByCultivationId(@PathVariable("id") Long id) {
        return DataSet.success(stdService.selectStandardCultivationByCultivationId(id));
    }

    @GetMapping("/cultivation/refNode/{id}")
    @ApiOperation("根据培养标准节点ID查询被选中毕业标准")
    public DataTable<Long> selectGraduationListByCultivationId(@PathVariable("id") Long id) {
        return PageDataTable.success(stdService.selectGraduationListByCultivationId(id));
    }

    @PostMapping("/cultivation/ref")
    @ApiOperation("培养标准配置")
    public Message insetCultivationRefGraduation(@RequestBody TowerToTower towerToTower) {
        stdService.insetCultivationRefGraduation(towerToTower);
        return Message.success();
    }
    /*
     * ----------------------------------------------------
     *          培养标准---代码段结束
     * ----------------------------------------------------
     */

    @GetMapping("/checkIssueAbility")
    @ApiOperation("查看是否可以新增")
    public DataSet checkIssueAbility(StandardAbility standardAbility) {
        boolean flag=stdService.checkIssueAbility(standardAbility);
        return DataSet.success(flag);
    }

    /**
     * type:1 能力 2 素质 3 知识
     * @param majorId
     * @param version
     * @param type
     * @return
     */
    @GetMapping("/overview/tree")
    @ApiOperation("知识、能力。素质专业总览图")
    public DataSet getOverviewTree(Long majorId,String version,String type) {
        return DataSet.success(stdService.getOverviewTree(majorId,version,type));
    }

    @GetMapping("/checkAbilityList")
    @ApiOperation("能力、素质查看 type:2=能力 3=素质")
    public DataSet<List> checkAbilityList(Long schemeId, String type) {
        return PageDataTable.success(stdService.checkAbilityList(schemeId,type));
    }




}

