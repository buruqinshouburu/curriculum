package com.doinner.csys.domain.vo;

import java.util.List;

/** 课程目标、支撑毕业要求、权重整表保存入参。 */
public class TeachingPlanObjectiveBatchSaveVo {
    private Long planId;
    private Long schemeId;
    private List<TeachingPlanObjectiveSaveVo> objectives;

    public Long getPlanId() { return planId; }
    public void setPlanId(Long planId) { this.planId = planId; }
    public Long getSchemeId() { return schemeId; }
    public void setSchemeId(Long schemeId) { this.schemeId = schemeId; }
    public List<TeachingPlanObjectiveSaveVo> getObjectives() { return objectives; }
    public void setObjectives(List<TeachingPlanObjectiveSaveVo> objectives) { this.objectives = objectives; }
}
