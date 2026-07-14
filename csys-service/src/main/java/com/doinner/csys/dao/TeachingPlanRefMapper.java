package com.doinner.csys.dao;

import com.doinner.csys.domain.TeachingPlanRef;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 教学计划通用引用 Mapper
 */
public interface TeachingPlanRefMapper {

    /**
     * 单条新增，新增后回填自增主键id
     */
    int insert(TeachingPlanRef record);

    /**
     * 批量新增
     */
    int insertBatch(@Param("list") List<TeachingPlanRef> list);

    /**
     * 根据主键更新
     */
    int updateById(TeachingPlanRef record);

    /**
     * 根据主键查询
     */
    TeachingPlanRef selectById(@Param("id") Long id);

    /**
     * 根据教学计划ID查询列表
     */
    List<TeachingPlanRef> selectByPlanId(@Param("planId") Long planId);

    /**
     * 按计划与引用类型查询
     */
    List<TeachingPlanRef> selectByPlanAndType(@Param("planId") Long planId,
                                              @Param("refType") Integer refType);

    /**
     * 根据教学计划ID逻辑删除
     */
    int deleteByPlanId(@Param("planId") Long planId);

    /**
     * 根据主键逻辑删除
     */
    int deleteById(@Param("id") Long id);
}