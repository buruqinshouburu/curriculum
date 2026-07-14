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
     * 按上下文查询绑定的毕业要求
     */
    List<TeachingPlanObjectiveRef> selectByContextId(@Param("contextId") Long contextId);

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