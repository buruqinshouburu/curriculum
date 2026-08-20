package com.doinner.csys.domain.vo;

import com.doinner.csys.domain.TeachingPlanAssessment;

import java.util.List;

/**
 * 教学计划考核与评价整页大保存入参。
 * <p>
 * assessments 按 planId 整表重建：先逻辑删除旧记录，再批量写入本次提交的记录；
 * 空列表或 null 表示清空该教学计划下全部考核评价。
 * <p>
 * scoreRule 保存至 t_csys_teaching_plan.score_rule，属于本页的顶层字段，
 * 不再从某一条考核评价记录中透传。null 或空串均可清空计分规则。
 */
public class TeachingPlanAssessmentSaveVo {

    /** 教学计划 id（必填，作为整表重建 owner） */
    private Long planId;

    /** 项目计分规则；本次整页保存的最终值，null/空串均表示清空。 */
    private String scoreRule;

    /** 考核与评价列表；每行 planId 以顶层 planId 为准覆盖，id 不参与更新。 */
    private List<TeachingPlanAssessment> assessments;

    public Long getPlanId() {
        return planId;
    }

    public void setPlanId(Long planId) {
        this.planId = planId;
    }

    public String getScoreRule() {
        return scoreRule;
    }

    public void setScoreRule(String scoreRule) {
        this.scoreRule = scoreRule;
    }

    public List<TeachingPlanAssessment> getAssessments() {
        return assessments;
    }

    public void setAssessments(List<TeachingPlanAssessment> assessments) {
        this.assessments = assessments;
    }
}
