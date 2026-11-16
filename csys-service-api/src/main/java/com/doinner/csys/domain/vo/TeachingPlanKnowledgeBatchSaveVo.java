package com.doinner.csys.domain.vo;

import com.doinner.csys.domain.TeachingPlanTargetDesign;
import java.util.List;

/** Partial knowledge-design upsert; omitted rows are never deleted. */
public class TeachingPlanKnowledgeBatchSaveVo {
    private Long planId;
    private List<TeachingPlanTargetDesign> rows;

    public Long getPlanId() { return planId; }
    public void setPlanId(Long planId) { this.planId = planId; }
    public List<TeachingPlanTargetDesign> getRows() { return rows; }
    public void setRows(List<TeachingPlanTargetDesign> rows) { this.rows = rows; }
}
