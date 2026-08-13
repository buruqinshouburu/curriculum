package com.doinner.csys.dao;

import com.doinner.csys.domain.TeachingPlanTaskBackground;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 教学计划任务背景 Mapper（实验课程第三节，对标 TeachingPlanObjectiveMapper）。
 */
public interface TeachingPlanTaskBackgroundMapper {

    /**
     * 单条新增，新增后回填自增主键id
     */
    int insert(TeachingPlanTaskBackground record);

    /**
     * 批量新增
     */
    int insertBatch(@Param("list") List<TeachingPlanTaskBackground> list);

    /**
     * 根据主键更新
     */
    int updateById(TeachingPlanTaskBackground record);

    /**
     * 根据主键查询
     */
    TeachingPlanTaskBackground selectById(@Param("id") Long id);

    /**
     * 按计划与培养方案查询任务背景。
     * onlyNullScheme=true 时只取 scheme_id IS NULL（公共基础单组）；
     * onlyNullScheme=false 且 schemeId 非空时按 scheme 过滤；
     * onlyNullScheme=false 且 schemeId 为空时不过滤 scheme（全量，用于按 scheme 分组渲染）。
     */
    List<TeachingPlanTaskBackground> selectByPlanAndScheme(@Param("planId") Long planId,
                                                            @Param("schemeId") Long schemeId,
                                                            @Param("onlyNullScheme") Boolean onlyNullScheme);

    /**
     * 根据教学计划ID逻辑删除
     */
    int deleteByPlanId(@Param("planId") Long planId);

    /** 按当前培养方案逻辑删除；公共基础课程只删除 scheme_id IS NULL。 */
    int deleteByPlanAndScheme(@Param("planId") Long planId,
                              @Param("schemeId") Long schemeId,
                              @Param("onlyNullScheme") Boolean onlyNullScheme);

    /**
     * 根据主键逻辑删除
     */
    int deleteById(@Param("id") Long id);

    /**
     * 将任务背景 scheme_id 置空（公共基础单组）。
     */
    int clearSchemeIdById(@Param("id") Long id);
}
