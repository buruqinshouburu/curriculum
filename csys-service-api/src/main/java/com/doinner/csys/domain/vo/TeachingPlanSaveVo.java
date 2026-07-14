package com.doinner.csys.domain.vo;

import com.doinner.csys.domain.TeachingPlan;
import com.doinner.csys.domain.TeachingPlanContext;

/**
 * 保存教学计划入参：同时承载教学计划主表(t_csys_teaching_plan)与
 * 调用课程上下文(t_csys_teaching_plan_context)。
 *
 * 保存逻辑：plan.id 为空则新增，非空则修改；context 随 plan 一并保存。
 */
public class TeachingPlanSaveVo {

    /** 教学计划主表信息 */
    private TeachingPlan plan;

    /** 调用课程上下文信息(可为空) */
    private TeachingPlanContext context;

    public TeachingPlan getPlan() {
        return plan;
    }

    public void setPlan(TeachingPlan plan) {
        this.plan = plan;
    }

    public TeachingPlanContext getContext() {
        return context;
    }

    public void setContext(TeachingPlanContext context) {
        this.context = context;
    }
}
