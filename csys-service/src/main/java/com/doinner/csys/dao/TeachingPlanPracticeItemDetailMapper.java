package com.doinner.csys.dao;

import com.doinner.csys.domain.TeachingPlanPracticeItemDetail;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 实验/实践项目明细 Mapper
 */
public interface TeachingPlanPracticeItemDetailMapper {

    /**
     * 单条新增，新增后回填自增主键id
     */
    int insert(TeachingPlanPracticeItemDetail record);

    /**
     * 批量新增
     */
    int insertBatch(@Param("list") List<TeachingPlanPracticeItemDetail> list);

    /**
     * 根据主键更新
     */
    int updateById(TeachingPlanPracticeItemDetail record);

    /**
     * 根据主键查询
     */
    TeachingPlanPracticeItemDetail selectById(@Param("id") Long id);

    /**
     * 按项目ID查询明细
     */
    List<TeachingPlanPracticeItemDetail> selectByItemId(@Param("itemId") Long itemId);

    /**
     * 按项目ID集合查询明细
     */
    List<TeachingPlanPracticeItemDetail> selectByItemIds(@Param("itemIds") List<Long> itemIds);

    /**
     * 按项目ID删除
     */
    int deleteByItemId(@Param("itemId") Long itemId);

    /**
     * 根据主键删除
     */
    int deleteById(@Param("id") Long id);
}