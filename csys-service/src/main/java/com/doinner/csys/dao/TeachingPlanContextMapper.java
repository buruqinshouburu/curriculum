package com.doinner.csys.dao;

import com.doinner.csys.domain.TeachingPlanContext;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 教学计划调用课程上下文 Mapper
 */
public interface TeachingPlanContextMapper {

    /**
     * 单条新增，新增后回填自增主键id
     */
    int insert(TeachingPlanContext record);

    /**
     * 批量新增
     */
    int insertBatch(@Param("list") List<TeachingPlanContext> list);

    /**
     * 根据主键更新
     */
    int updateById(TeachingPlanContext record);

    /**
     * 根据主键查询
     */
    TeachingPlanContext selectById(@Param("id") Long id);

    /**
     * 根据教学计划ID查询上下文列表（页面tab）
     */
    List<TeachingPlanContext> selectByPlanId(@Param("planId") Long planId);

    /**
     * 根据教学计划ID逻辑删除
     */
    int deleteByPlanId(@Param("planId") Long planId);

    /**
     * 根据主键逻辑删除
     */
    int deleteById(@Param("id") Long id);

    /**
     * 从总库课程的调用关系实时查询上下文候选（用于 sync）。
     * 链路：源课 -> c2(source_id) -> 排课 tcs -> 培养方案 ts。
     */
    List<TeachingPlanContext> selectQuoteContextsBySourceCourseId(@Param("sourceCourseId") Long sourceCourseId);
}