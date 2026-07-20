package com.doinner.csys.domain.vo;

import com.doinner.csys.domain.TeachingPlan;

/**
 * 保存教学计划入参（仅主表）。
 * plan.id 为空时按 (sourceCourseId, planType) 查重：已存在则改，否则新增。
 * 培养方案 tab 不再通过 context 传递，前端用 /teachingPlan/scheme/list 取 schemeId。
 */
public class TeachingPlanSaveVo {

    /** 教学计划主表信息 */
    private TeachingPlan plan;

    public TeachingPlan getPlan() {
        return plan;
    }

    public void setPlan(TeachingPlan plan) {
        this.plan = plan;
    }
}
