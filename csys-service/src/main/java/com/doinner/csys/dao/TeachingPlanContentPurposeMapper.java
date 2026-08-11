package com.doinner.csys.dao;

import com.doinner.csys.domain.TeachingPlanContentPurpose;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 教学计划训练内容支撑训练目的 Mapper（type2 第四节「目的」多选）。
 */
public interface TeachingPlanContentPurposeMapper {

    /**
     * 单条新增，新增后回填自增主键id
     */
    int insert(TeachingPlanContentPurpose record);

    /**
     * 批量新增
     */
    int insertBatch(@Param("list") List<TeachingPlanContentPurpose> list);

    /**
     * 根据主键查询
     */
    TeachingPlanContentPurpose selectById(@Param("id") Long id);

    /**
     * 按训练内容ID查询绑定的训练目的
     */
    List<TeachingPlanContentPurpose> selectByContentId(@Param("contentId") Long contentId);

    /**
     * 按教学计划ID查询全部绑定（覆盖导入清空/级联删除用）
     */
    List<TeachingPlanContentPurpose> selectByPlanId(@Param("planId") Long planId);

    /**
     * 按训练内容ID逻辑删除
     */
    int deleteByContentId(@Param("contentId") Long contentId);

    /**
     * 根据教学计划ID逻辑删除
     */
    int deleteByPlanId(@Param("planId") Long planId);

    /**
     * 根据主键逻辑删除
     */
    int deleteById(@Param("id") Long id);
}
