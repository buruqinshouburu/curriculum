package com.doinner.csys.domain.vo;

import com.doinner.csys.domain.TeachingPlanObjectiveAssessment;

import java.util.List;

/** 课程目标-考核评价关联批量保存入参。 */
public class TeachingPlanObjectiveAssessmentSaveVo {
    private Long planId;
    private Long schemeId;
    private List<TeachingPlanObjectiveAssessment> items;

    public Long getPlanId() { return planId; }
    public void setPlanId(Long planId) { this.planId = planId; }
    public Long getSchemeId() { return schemeId; }
    public void setSchemeId(Long schemeId) { this.schemeId = schemeId; }
    public List<TeachingPlanObjectiveAssessment> getItems() { return items; }
    public void setItems(List<TeachingPlanObjectiveAssessment> items) { this.items = items; }
}
