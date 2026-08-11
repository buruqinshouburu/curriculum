package com.doinner.csys.domain.vo;

import java.util.List;

/** 任务背景 + 支撑毕业要求整表保存入参（对标 TeachingPlanObjectiveBatchSaveVo）。 */
public class TeachingPlanTaskBackgroundBatchSaveVo {
    private Long planId;
    private Long schemeId;
    private List<TeachingPlanTaskBackgroundSaveVo> taskBackgrounds;

    public Long getPlanId() {
        return planId;
    }

    public void setPlanId(Long planId) {
        this.planId = planId;
    }

    public Long getSchemeId() {
        return schemeId;
    }

    public void setSchemeId(Long schemeId) {
        this.schemeId = schemeId;
    }

    public List<TeachingPlanTaskBackgroundSaveVo> getTaskBackgrounds() {
        return taskBackgrounds;
    }

    public void setTaskBackgrounds(List<TeachingPlanTaskBackgroundSaveVo> taskBackgrounds) {
        this.taskBackgrounds = taskBackgrounds;
    }
}
