package com.doinner.csys.dao;

import com.doinner.csys.domain.TeachingPlanSection;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 教学计划文本章节 Mapper
 */
public interface TeachingPlanSectionMapper {

    /**
     * 单条新增，新增后回填自增主键id
     */
    int insert(TeachingPlanSection record);

    /**
     * 批量新增
     */
    int insertBatch(@Param("list") List<TeachingPlanSection> list);

    /**
     * 根据主键更新
     */
    int updateById(TeachingPlanSection record);

    /**
     * 根据主键查询
     */
    TeachingPlanSection selectById(@Param("id") Long id);

    /**
     * 根据教学计划ID查询列表
     */
    List<TeachingPlanSection> selectByPlanId(@Param("planId") Long planId);

    /**
     * 根据教学计划ID逻辑删除
     */
    int deleteByPlanId(@Param("planId") Long planId);

    /**
     * 根据主键逻辑删除
     */
    int deleteById(@Param("id") Long id);
}