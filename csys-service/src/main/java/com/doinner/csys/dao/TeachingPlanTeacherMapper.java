package com.doinner.csys.dao;

import com.doinner.csys.domain.TeachingPlanTeacher;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 教学计划教员团队 Mapper
 */
public interface TeachingPlanTeacherMapper {

    /**
     * 单条新增，新增后回填自增主键id
     */
    int insert(TeachingPlanTeacher record);

    /**
     * 批量新增
     */
    int insertBatch(@Param("list") List<TeachingPlanTeacher> list);

    /**
     * 根据主键更新
     */
    int updateById(TeachingPlanTeacher record);

    /**
     * 根据主键查询
     */
    TeachingPlanTeacher selectById(@Param("id") Long id);

    /**
     * 根据教学计划ID查询列表
     */
    List<TeachingPlanTeacher> selectByPlanId(@Param("planId") Long planId);

    /**
     * 根据教学计划ID逻辑删除
     */
    int deleteByPlanId(@Param("planId") Long planId);

    /**
     * 根据主键逻辑删除
     */
    int deleteById(@Param("id") Long id);
}