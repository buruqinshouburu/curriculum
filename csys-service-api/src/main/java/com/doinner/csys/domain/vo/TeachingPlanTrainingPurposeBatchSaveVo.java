package com.doinner.csys.domain.vo;

import java.util.List;

/** 训练目的 + 支撑毕业要求整表保存入参（type2 第二节，对标 TeachingPlanObjectiveBatchSaveVo）。 */
public class TeachingPlanTrainingPurposeBatchSaveVo {
    private Long planId;
    private Long schemeId;
    private List<TeachingPlanTrainingPurposeSaveVo> purposes;

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

    public List<TeachingPlanTrainingPurposeSaveVo> getPurposes() {
        return purposes;
    }

    public void setPurposes(List<TeachingPlanTrainingPurposeSaveVo> purposes) {
        this.purposes = purposes;
    }
}
