package com.doinner.csys.dao;

import com.doinner.csys.domain.TeachingPlanTrainingPurposeRef;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 教学计划训练目的支撑毕业要求 Mapper（type2 第二节，对标 TeachingPlanTaskBackgroundRefMapper）。
 */
public interface TeachingPlanTrainingPurposeRefMapper {

    /**
     * 单条新增，新增后回填自增主键id
     */
    int insert(TeachingPlanTrainingPurposeRef record);

    /**
     * 批量新增
     */
    int insertBatch(@Param("list") List<TeachingPlanTrainingPurposeRef> list);

    /**
     * 根据主键更新
     */
    int updateById(TeachingPlanTrainingPurposeRef record);

    /**
     * 根据主键查询
     */
    TeachingPlanTrainingPurposeRef selectById(@Param("id") Long id);

    /**
     * 按训练目的ID查询绑定的毕业要求
     */
    List<TeachingPlanTrainingPurposeRef> selectByPurposeId(@Param("purposeId") Long purposeId);

    /**
     * 按教学计划 + 培养方案批量查询支撑毕业要求（生成器按 scheme 分组渲染用）。
     * onlyNullScheme=true 时只取 scheme_id IS NULL；
     * onlyNullScheme=false + schemeId 非空 -> scheme_id = #{schemeId}；
     * onlyNullScheme=false + schemeId 空 -> 不过滤 scheme（plan 下全量）。
     */
    List<TeachingPlanTrainingPurposeRef> selectByPlanAndScheme(@Param("planId") Long planId,
                                                               @Param("schemeId") Long schemeId,
                                                               @Param("onlyNullScheme") Boolean onlyNullScheme);

    /**
     * 按训练目的ID逻辑删除
     */
    int deleteByPurposeId(@Param("purposeId") Long purposeId);

    /**
     * 根据教学计划ID逻辑删除
     */
    int deleteByPlanId(@Param("planId") Long planId);

    /**
     * 根据主键逻辑删除
     */
    int deleteById(@Param("id") Long id);
}
