package com.doinner.csys.service;

import com.doinner.common.core.domain.Message;
import com.doinner.csys.domain.*;
import com.doinner.csys.domain.vo.*;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public interface StandardService {

    /*
     * -------------------------------------------
     * 能力素质---代码段开始
     * -------------------------------------------
     */

    /**
     * 查询能力素质列表
     *
     * @param standardAbility 能力素质
     * @return 能力素质集合
     */
    List<StandardAbility> selectStandardAbilityList(StandardAbility standardAbility);
    List<StandardTreeVo> selectStdAbilityTree(StandardAbility standardAbility);


    /**
     * 查询能力素质
     *
     * @param id 能力素质
     * @return 能力素质
     */
    StandardAbility selectStandardAbilityById(Long id);

    /**
     * 新增能力素质
     *
     * @param standardAbility 能力体系
     * @return 结果
     */
    StandardAbility insertStandardAbility(StandardAbility standardAbility);

    StandardAbility insertAbilitySystem(StandardAbility standardAbility);

    @Transactional(rollbackFor = {Exception.class})
    int insertStandardAbilityTree(List<StandardAbility> standardAbilityList);

    /**
     * 修改能力素质
     *
     * @param standardAbility 能力素质
     * @return 结果
     */
    StandardAbility updateStandardAbility(StandardAbility standardAbility);

    StandardAbility updateStdAbilityTree(StandardAbility standardAbility);

    void insertStandardTemplateAbilityVo(StandardTemplateAbilityVo standardTemplateAbilityVo);

    /**
     * 删除能力素质
     *
     * @param id 能力素质主键
     * @return 结果
     */
    void deleteStandardAbilityById(Long id);

    void deleteStandardAbilityByIds(Long[] ids);


    /**
     * 根据id查询该能力的所有子能力
     */
    List<StandardAbility> selectAllStdAbilityById(Long id);

    List<StandardAbility> selectAllStdAbilityAndGraduationById(Long id);

    StandardAbilityLevel saveStdAbilityLevel(StandardAbilityLevel level);



    void exportAbility(HttpServletResponse response,List<Long> ids);

    Message importAbility(MultipartFile file,Long collegeId,Long categoryId,Long majorId,Long subMajorId,String version);

    void exportAbilityTemplate(HttpServletResponse response);


    void exportQuality(HttpServletResponse response,List<Long> ids);

    Message importQuality(MultipartFile file,Long collegeId,Long categoryId,Long majorId,Long subMajorId,String version);

    void exportQualityTemplate(HttpServletResponse response);

    /*
     * -------------------------------------------
     *          能力素质---代码段结束
     * -------------------------------------------
     */

    /*
     * -------------------------------------------
     *          培养目标---代码段开始
     * -------------------------------------------
     */


    /**
     * 查询培养目标列表
     */
    List<StandardCultivationTarget> selectStandardCultivationTargetList(StandardCultivationTarget standardCultivationTarget);

    /**
     * 查询培养目标子数据
     */
    List<StandardCultivationTarget> selectStdCultivationTargetAll(Long id);

    /**
     * 根据ID查询培养目标
     */
    StandardCultivationTarget selectStandardCultivationTargetById(Long id);

    /**
     * 新增培养目标
     */
    StandardCultivationTarget insertStandardCultivationTarget(StandardCultivationTarget standardCultivationTarget);


    @Transactional(rollbackFor = {Exception.class})
    int insertStandardCultivationTargetTree(List<StandardCultivationTarget> standardCultivationTargetList);

    /**
     * 修改培养目标
     */
    StandardCultivationTarget updateStandardCultivationTarget(StandardCultivationTarget standardCultivationTarget);

    /**
     * 删除培养目标
     */
    Message deleteStandardCultivationTargetById(Long id);

    /*
     * -------------------------------------------
     *          培养目标---代码段结束
     * -------------------------------------------
     */

    /*
     * -------------------------------------------
     *          毕业标准---代码段开始
     * -------------------------------------------
     */


    /**
     * 查询毕业标准列表
     */
    List<StandardGraduation> selectStandardGraduationList(StandardGraduation standardGraduation);
    List<StandardGraduation> selectStdGraduationSchemeListBy(StandardGraduation standardGraduation);

    List<GraduationTreeVo> selectStdGraduationTreeBy(StandardGraduation standardGraduation);

    /**
     * 查询培养标准子数据
     */
    List<StandardGraduation> selectStandardGraduationAll(Long id);

    List<StandardGraduationVo> selectStandardGraduationRefAll(Long id);

    List<StandardCultivationTargetVo>  reSelectStandardGraduationRefAll(Long id);

    /**
     * 根据ID查询毕业标准
     */
    StandardGraduation selectStandardGraduationById(Long id);


    /**
     * 新增毕业标准
     */
    StandardGraduation insertStandardGraduation(StandardGraduation standardGraduation);

    /**
     * 修改毕业标准
     */
    StandardGraduation updateStandardGraduation(StandardGraduation standardGraduation);

    /**
     * 删除毕业标准
     */
    Message deleteStandardGraduationById(Long id);

    void deleteStandardGraduationBySchemeId(Long schemeId);

    /**
     * 配置毕业标准和培养目标关系
     */
    void insetGraduationRefTarget(TowerToTower towerToTower);

    void insertStandardTemplateGraduationVo(StandardTemplateGraduationVo graduationVo);

    Message graduationIssueKnowledgeAbilityQuality(GraduationIssueVo graduationVo);

    TreeVo graduationOverviewTree(Long majorId,String version,Integer type);

    void graduationBindingScheme(GraduationBindingSchemeVo graduationBindingSchemeVo);


    /**
     * 查看毕业标准配置
     */
    StandardGraduationRefTargetListVo selectStandardCultivationTargetByGraduationId(Long graduationId);

    /**
     * 根据毕业标准ID查看被选中的培养目标
     */
    List<Long> selectTargetListByGraduationId(Long graduationId);


    StandardCultivation selectStandardCultivationTree(Long id);


    @Transactional(rollbackFor = {Exception.class})
    int insertStandardGraduationTree(List<StandardGraduation> standardGraduationTree);




    /*
     * -------------------------------------------
     *          毕业标准---代码段结束
     * -------------------------------------------
     */


    /*
     * -------------------------------------------
     * 培养标准---代码段开始
     * -------------------------------------------
     */

    StandardGraduation selectStandardGraduationTree(Long id);

    /**
     * 查询培养标准
     *
     * @param id 培养标准主键
     * @return 培养标准
     */
    StandardCultivation selectStandardCultivationById(Long id);


    List<StandardCultivation> selectStandardCultivationAll(Long id);

    List<StandardCultivationVo>selectStdCultivationRefAll(Long id);

    List<StandardGraduationVo>reSelectStdCultivationRefAll(Long id);
    /**
     * 查询培养标准列表
     *
     * @param standardCultivation 培养标准
     * @return 培养标准集合
     */
    List<StandardCultivation> selectStandardCultivationList(StandardCultivation standardCultivation);

    /**
     * 新增培养标准
     *
     * @param standardCultivation 培养标准
     * @return 结果
     */
    StandardCultivation insertStandardCultivation(StandardCultivation standardCultivation);

    @Transactional(rollbackFor = {Exception.class})
    int insertStandardCultivationTree(List<StandardCultivation> standardCultivationTree);

    /**
     * 修改培养标准
     *
     * @param standardCultivation 培养标准
     * @return 结果
     */
    StandardCultivation updateStandardCultivation(StandardCultivation standardCultivation);

    /**
     * 删除培养标准
     *
     * @param id 培养标准主键
     * @return 结果
     */
    Message deleteStandardCultivationById(Long id);


    StandardCultivationRefGraduationListVo selectStandardCultivationByCultivationId(Long id);

    List<Long> selectGraduationListByCultivationId(Long id);

    void insetCultivationRefGraduation(TowerToTower towerToTower);

    /*
     * ----------------------------------------------------
     *          培养标准---代码段结束
     * ----------------------------------------------------
     */


    /*
     * ----------------------------------------------------
     *          毕业标准与培养标准关联---代码段开始
     * ----------------------------------------------------
     */



    List<StandardCultivationRefGraduation> selectStandardCultivationRefGraduationByStandardCultivationId(List<Long> standardCultivationIds);

    List<StandardGraduationRefCultivationTarget> selectStandardGraduationRefCultivationTargetByStandardGraduationId(List<Long> standardGraduationIds);


    StandardCultivationTarget selectStdCultivationTargetTree(Long id);


    List<StandardCultivationTarget> selectCultivationTargetByMajorId(Long majorId,String version);

    ArrayList<StandardCultivationTarget> findCultivationTargetByGraduations(List<? extends StandardGraduation> graduationList);

    StandardGraduation getGraduationTree(List<StandardGraduation> standardGraduationList);

    Message deleteBatchStdCultivationTargetById(List<Long> ids);

    Message deleteBatchStdGraduationById(List<Long> ids);

    boolean checkIssueAbility(StandardAbility standardAbility);

    OverviewTreeVo getOverviewTree(Long majorId, String version, String type);

    List<StandardCultivationTarget> selectStdCultivationTargetAllByTrainingId(Long trainingSchemeId);

    List checkAbilityList(Long schemeId, String type);
}
