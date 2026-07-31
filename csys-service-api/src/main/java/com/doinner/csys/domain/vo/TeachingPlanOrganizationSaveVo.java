package com.doinner.csys.domain.vo;

import com.doinner.csys.domain.TeachingPlanProcessStep;

import java.util.List;

/**
 * 实践项目(type=4)「三、组织与实施」整表大保存入参。
 * <p>
 * 前端该页面团队规模 / 分工方式 / 项目步骤同页一次提交：
 * - teamScale(团队规模文本)、division(分工方式文本)：复用 t_csys_teaching_plan_section，
 *   sectionTitle 分别为「团队规模」「分工方式」，按 planId+sectionTitle upsert。
 * - steps(项目步骤列表)：t_csys_teaching_plan_process_step 整表重建(deleteByPlanId + insertBatch)。
 * <p>
 * 传空 steps 表示清空项目步骤；teamScale/division 传 null 不改原值，传空串清空。
 */
public class TeachingPlanOrganizationSaveVo {

    /** 教学计划 id（必填） */
    private Long planId;

    /** 团队规模（sectionTitle=团队规模）；null 不改，空串清空 */
    private String teamScale;

    /** 分工方式（sectionTitle=分工方式）；null 不改，空串清空 */
    private String division;

    /**
     * 项目步骤列表(stepName+requirement+sort)。
     * 整表重建：先逻辑删除该 plan 下旧步骤，再按本列表 insert；空列表/null=清空。
     */
    private List<TeachingPlanProcessStep> steps;

    public Long getPlanId() {
        return planId;
    }

    public void setPlanId(Long planId) {
        this.planId = planId;
    }

    public String getTeamScale() {
        return teamScale;
    }

    public void setTeamScale(String teamScale) {
        this.teamScale = teamScale;
    }

    public String getDivision() {
        return division;
    }

    public void setDivision(String division) {
        this.division = division;
    }

    public List<TeachingPlanProcessStep> getSteps() {
        return steps;
    }

    public void setSteps(List<TeachingPlanProcessStep> steps) {
        this.steps = steps;
    }
}
