package com.agileai.dataparser.mapper;

import com.agileai.dataparser.domain.CurTeachingPlan;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 选课详情Mapper接口
 *
 * @author agileai
 * @date 2022-04-25
 */
public interface CurTeachingPlanMapper
{
    /**
     * 查询选课详情
     *
     * @param id 选课详情ID
     * @return 选课详情
     */
    CurTeachingPlan selectCurTeachingPlanById(Long id);

    /**
     * 查询选课详情列表
     *
     * @param curTeachingPlan 选课详情
     * @return 选课详情集合
     */
    List<CurTeachingPlan> selectCurTeachingPlanList(CurTeachingPlan curTeachingPlan);

    /**
     * 新增选课详情
     *
     * @param curTeachingPlan 选课详情
     * @return 结果
     */
    int insertCurTeachingPlan(CurTeachingPlan curTeachingPlan);

    /**
     * 修改选课详情
     *
     * @param curTeachingPlan 选课详情
     * @return 结果
     */
    int updateCurTeachingPlan(CurTeachingPlan curTeachingPlan);

    /**
     * 删除选课详情
     *
     * @param id 选课详情ID
     * @return 结果
     */
    int deleteCurTeachingPlanById(Long id);

    /**
     * 批量删除选课详情
     *
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    int deleteCurTeachingPlanByIds(Long[] ids);

    List<CurTeachingPlan> selectCurTeachingPlanByPlanningIds(@Param("planningIds") List<Long> planningIds);
}
