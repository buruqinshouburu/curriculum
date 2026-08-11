package com.doinner.csys.dao;

import com.doinner.csys.domain.TeachingPlanSupportObjective;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 实践项目教学计划支撑的课程目标/训练目的绑定 Mapper（type4 第二节）。
 */
public interface TeachingPlanSupportObjectiveMapper {

    /**
     * 单条新增，新增后回填自增主键id
     */
    int insert(TeachingPlanSupportObjective record);

    /**
     * 批量新增
     */
    int insertBatch(@Param("list") List<TeachingPlanSupportObjective> list);

    /**
     * 按教学计划ID查询全部绑定，按 sort 升序
     */
    List<TeachingPlanSupportObjective> selectByPlanId(@Param("planId") Long planId);

    /**
     * 根据教学计划ID逻辑删除
     */
    int deleteByPlanId(@Param("planId") Long planId);

    /**
     * 根据主键逻辑删除
     */
    int deleteById(@Param("id") Long id);
}
