package com.doinner.csys.domain;

import com.doinner.common.core.domain.db.AbstractDoinnerLogicalDelBaseEntity;

import java.math.BigDecimal;

/**
 * 教学计划考核评价实体
 * 考核项目、成果评价、权重、评价标准
 */
public class TeachingPlanAssessment extends AbstractDoinnerLogicalDelBaseEntity {

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 教学计划ID
     */
    private Long planId;

    /**
     * 关联实验/实践项目ID
     */
    private Long itemId;

    /**
     * 考核类别：1终结性 2形成性 3实验项目 4训练课目 5成果评价
     */
    private Integer assessmentCategory;

    /**
     * 考核项目或成果形式
     */
    private String assessmentItem;

    /**
     * 考核方式
     */
    private String method;

    /**
     * 评定机制
     */
    private String mechanism;

    /**
     * 成绩评定：百分制/五级制/两级制
     */
    private String scoreSystem;

    /**
     * 成果类型：0无 1个人成果 2团队成果
     */
    private Integer outcomeType;

    /**
     * 评价的知识和能力
     */
    private String assessedContent;

    /**
     * 权重
     */
    private BigDecimal weight;

    /**
     * 评价标准/评价准则
     */
    private String standard;

    /**
     * 排序
     */
    private Integer sort;

    /**
     * 计分规则（非本表字段，仅新增/修改时透传）。
     * 有值时回写主表 t_csys_teaching_plan.score_rule；null 表示不更新主表计分规则。
     * 与 scoreSystem（成绩评定制）不同。
     */
    private String scoreRule;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPlanId() {
        return planId;
    }

    public void setPlanId(Long planId) {
        this.planId = planId;
    }

    public Long getItemId() {
        return itemId;
    }

    public void setItemId(Long itemId) {
        this.itemId = itemId;
    }

    public Integer getAssessmentCategory() {
        return assessmentCategory;
    }

    public void setAssessmentCategory(Integer assessmentCategory) {
        this.assessmentCategory = assessmentCategory;
    }

    public String getAssessmentItem() {
        return assessmentItem;
    }

    public void setAssessmentItem(String assessmentItem) {
        this.assessmentItem = assessmentItem;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getMechanism() {
        return mechanism;
    }

    public void setMechanism(String mechanism) {
        this.mechanism = mechanism;
    }

    public String getScoreSystem() {
        return scoreSystem;
    }

    public void setScoreSystem(String scoreSystem) {
        this.scoreSystem = scoreSystem;
    }

    public Integer getOutcomeType() {
        return outcomeType;
    }

    public void setOutcomeType(Integer outcomeType) {
        this.outcomeType = outcomeType;
    }

    public String getAssessedContent() {
        return assessedContent;
    }

    public void setAssessedContent(String assessedContent) {
        this.assessedContent = assessedContent;
    }

    public BigDecimal getWeight() {
        return weight;
    }

    public void setWeight(BigDecimal weight) {
        this.weight = weight;
    }

    public String getStandard() {
        return standard;
    }

    public void setStandard(String standard) {
        this.standard = standard;
    }

    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }

    public String getScoreRule() {
        return scoreRule;
    }

    public void setScoreRule(String scoreRule) {
        this.scoreRule = scoreRule;
    }
}