package com.doinner.csys.dao;

import com.doinner.csys.domain.TeachingPlanCondition;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 教学计划条件保障 Mapper
 */
public interface TeachingPlanConditionMapper {

    /**
     * 单条新增，新增后回填自增主键id
     */
    int insert(TeachingPlanCondition record);

    /**
     * 批量新增
     */
    int insertBatch(@Param("list") List<TeachingPlanCondition> list);

    /**
     * 根据主键更新
     */
    int updateById(TeachingPlanCondition record);

    /**
     * 根据主键查询
     */
    TeachingPlanCondition selectById(@Param("id") Long id);

    /**
     * 根据教学计划ID查询列表
     */
    List<TeachingPlanCondition> selectByPlanId(@Param("planId") Long planId);

    /**
     * 按教学计划统计全部记录数（含 sysflag=2 已删除），用于判断是否需要初始化
     */
    int countAllByPlanId(@Param("planId") Long planId);

    /**
     * 根据教学计划ID逻辑删除
     */
    int deleteByPlanId(@Param("planId") Long planId);

    /**
     * 根据主键逻辑删除
     */
    int deleteById(@Param("id") Long id);
}