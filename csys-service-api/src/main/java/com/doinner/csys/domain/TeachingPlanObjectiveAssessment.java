package com.doinner.csys.domain;

import com.doinner.common.core.domain.db.AbstractDoinnerLogicalDelBaseEntity;

import java.math.BigDecimal;

/** 课程目标与考核评价关联。 */
public class TeachingPlanObjectiveAssessment extends AbstractDoinnerLogicalDelBaseEntity {
    private Long id;
    private Long planId;
    private Long schemeId;
    private Long objectiveId;
    /** 关联考核评价记录，兼容前端只传 assessmentItem 时可为空。 */
    private Long assessmentId;
    /** 考核项目名称快照，用于动态列匹配和历史数据展示。 */
    private String assessmentItem;
    /** 当前课程目标在该考核项目中的权重，合计按目标维度通常为 1。 */
    private BigDecimal weight;
    /** 该课程目标对应的考核评价项内容。 */
    private String assessmentItemContent;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getPlanId() { return planId; }
    public void setPlanId(Long planId) { this.planId = planId; }
    public Long getSchemeId() { return schemeId; }
    public void setSchemeId(Long schemeId) { this.schemeId = schemeId; }
    public Long getObjectiveId() { return objectiveId; }
    public void setObjectiveId(Long objectiveId) { this.objectiveId = objectiveId; }
    public Long getAssessmentId() { return assessmentId; }
    public void setAssessmentId(Long assessmentId) { this.assessmentId = assessmentId; }
    public String getAssessmentItem() { return assessmentItem; }
    public void setAssessmentItem(String assessmentItem) { this.assessmentItem = assessmentItem; }
    public BigDecimal getWeight() { return weight; }
    public void setWeight(BigDecimal weight) { this.weight = weight; }
    public String getAssessmentItemContent() { return assessmentItemContent; }
    public void setAssessmentItemContent(String assessmentItemContent) { this.assessmentItemContent = assessmentItemContent; }
}
