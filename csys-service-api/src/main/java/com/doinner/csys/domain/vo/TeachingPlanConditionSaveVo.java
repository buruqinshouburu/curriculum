package com.doinner.csys.domain.vo;

import com.doinner.csys.domain.TeachingPlanCondition;

import java.util.List;

/**
 * 教学条件及资源大保存入参（条件保障整表重建）。
 * <p>
 * 流程：GET /condition/list 取列表（首访按字典 sys_condition_type 自动初始化）->
 * 前端编辑 conditionType / requirement / sort -> POST /condition/save 一次性提交多条。
 * <p>
 * 保存语义：先按 planId 逻辑删除该教学计划下全部旧条件，再按 conditions 批量写入；
 * conditions 传空列表或 null 表示清空全部条件。
 */
public class TeachingPlanConditionSaveVo {

    /** 教学计划 id（必填，作为整表重建的 owner） */
    private Long planId;

    /**
     * 要保存的条件保障列表。
     * 保存时先逻辑删除该 plan 下旧记录，再按本列表 insert；
     * 传空列表或 null 表示清空全部。每行 planId 以顶层 planId 为准覆盖。
     */
    private List<TeachingPlanCondition> conditions;

    public Long getPlanId() {
        return planId;
    }

    public void setPlanId(Long planId) {
        this.planId = planId;
    }

    public List<TeachingPlanCondition> getConditions() {
        return conditions;
    }

    public void setConditions(List<TeachingPlanCondition> conditions) {
        this.conditions = conditions;
    }
}
