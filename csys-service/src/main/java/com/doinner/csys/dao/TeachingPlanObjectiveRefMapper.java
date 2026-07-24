package com.doinner.csys.dao;

import com.doinner.csys.domain.TeachingPlanObjectiveRef;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 教学计划目标支撑毕业要求 Mapper
 */
public interface TeachingPlanObjectiveRefMapper {

    /**
     * 单条新增，新增后回填自增主键id
     */
    int insert(TeachingPlanObjectiveRef record);

    /**
     * 批量新增
     */
    int insertBatch(@Param("list") List<TeachingPlanObjectiveRef> list);

    /**
     * 根据主键更新
     */
    int updateById(TeachingPlanObjectiveRef record);

    /**
     * 根据主键查询
     */
    TeachingPlanObjectiveRef selectById(@Param("id") Long id);

    /**
     * 按目标ID查询绑定的毕业要求
     */
    List<TeachingPlanObjectiveRef> selectByObjectiveId(@Param("objectiveId") Long objectiveId);

    /**
     * 按培养方案查询绑定的毕业要求
     */
    List<TeachingPlanObjectiveRef> selectBySchemeId(@Param("schemeId") Long schemeId);

    /**
     * 按教学计划 + 培养方案批量查询支撑毕业要求（总览树用）。
     * onlyNullScheme=true 时只取 scheme_id IS NULL；
     * onlyNullScheme=false 时 schemeId 必填，按 scheme_id = #{schemeId}。
     */
    List<TeachingPlanObjectiveRef> selectByPlanAndScheme(@Param("planId") Long planId,
                                                          @Param("schemeId") Long schemeId,
                                                          @Param("onlyNullScheme") Boolean onlyNullScheme);

    /**
     * 按目标ID逻辑删除
     */
    int deleteByObjectiveId(@Param("objectiveId") Long objectiveId);

    /**
     * 根据教学计划ID逻辑删除
     */
    int deleteByPlanId(@Param("planId") Long planId);

    /**
     * 根据主键逻辑删除
     */
    int deleteById(@Param("id") Long id);
}