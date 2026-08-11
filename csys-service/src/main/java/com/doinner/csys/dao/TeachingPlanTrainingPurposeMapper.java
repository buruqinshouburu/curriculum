package com.doinner.csys.dao;

import com.doinner.csys.domain.TeachingPlanTrainingPurpose;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 教学计划训练目的 Mapper（type2 第二节，对标 TeachingPlanTaskBackgroundMapper）。
 */
public interface TeachingPlanTrainingPurposeMapper {

    /**
     * 单条新增，新增后回填自增主键id
     */
    int insert(TeachingPlanTrainingPurpose record);

    /**
     * 批量新增
     */
    int insertBatch(@Param("list") List<TeachingPlanTrainingPurpose> list);

    /**
     * 根据主键更新
     */
    int updateById(TeachingPlanTrainingPurpose record);

    /**
     * 根据主键查询
     */
    TeachingPlanTrainingPurpose selectById(@Param("id") Long id);

    /**
     * 按计划与培养方案查询训练目的。
     * onlyNullScheme=true 时只取 scheme_id IS NULL（通识通用单组）；
     * onlyNullScheme=false 且 schemeId 非空时按 scheme 过滤；
     * onlyNullScheme=false 且 schemeId 为空时不过滤 scheme（全量，用于按 scheme 分组渲染）。
     */
    List<TeachingPlanTrainingPurpose> selectByPlanAndScheme(@Param("planId") Long planId,
                                                            @Param("schemeId") Long schemeId,
                                                            @Param("onlyNullScheme") Boolean onlyNullScheme);

    /**
     * 根据教学计划ID逻辑删除
     */
    int deleteByPlanId(@Param("planId") Long planId);

    /**
     * 根据主键逻辑删除
     */
    int deleteById(@Param("id") Long id);

    /**
     * 将训练目的 scheme_id 置空（通识通用单组）。
     */
    int clearSchemeIdById(@Param("id") Long id);
}
