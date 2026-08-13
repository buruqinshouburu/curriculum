package com.doinner.csys.dao;

import com.doinner.csys.domain.TeachingPlanTaskBackgroundRef;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 教学计划任务背景支撑毕业要求 Mapper（对标 TeachingPlanObjectiveRefMapper）。
 */
public interface TeachingPlanTaskBackgroundRefMapper {

    /**
     * 单条新增，新增后回填自增主键id
     */
    int insert(TeachingPlanTaskBackgroundRef record);

    /**
     * 按任务背景ID查询绑定的毕业要求
     */
    List<TeachingPlanTaskBackgroundRef> selectByTaskBackgroundId(@Param("taskBackgroundId") Long taskBackgroundId);

    /**
     * 按教学计划 + 培养方案批量查询支撑毕业要求（生成器按 scheme 分组渲染用）。
     * onlyNullScheme=true 时只取 scheme_id IS NULL；
     * onlyNullScheme=false + schemeId 非空 -> scheme_id = #{schemeId}；
     * onlyNullScheme=false + schemeId 空 -> 不过滤 scheme（plan 下全量）。
     */
    List<TeachingPlanTaskBackgroundRef> selectByPlanAndScheme(@Param("planId") Long planId,
                                                              @Param("schemeId") Long schemeId,
                                                              @Param("onlyNullScheme") Boolean onlyNullScheme);

    /**
     * 按任务背景ID逻辑删除
     */
    int deleteByTaskBackgroundId(@Param("taskBackgroundId") Long taskBackgroundId);

    /**
     * 根据教学计划ID逻辑删除
     */
    int deleteByPlanId(@Param("planId") Long planId);

    /** 按当前培养方案逻辑删除任务背景绑定；公共基础课程只删除 scheme_id IS NULL。 */
    int deleteByPlanAndScheme(@Param("planId") Long planId,
                              @Param("schemeId") Long schemeId,
                              @Param("onlyNullScheme") Boolean onlyNullScheme);
}
