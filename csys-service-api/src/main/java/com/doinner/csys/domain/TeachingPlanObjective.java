package com.doinner.csys.domain;

import com.doinner.common.core.domain.db.AbstractDoinnerLogicalDelBaseEntity;

/**
 * 教学计划目标实体
 * 按 plan_id + scheme_id + 目标类型字典编码 保存，目标内容手工录入
 */
public class TeachingPlanObjective extends AbstractDoinnerLogicalDelBaseEntity {

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 教学计划ID
     */
    private Long planId;

    /**
     * 培养方案ID，对应页面当前培养方案tab（t_csys_training_scheme.id）
     */
    private Long schemeId;

    /**
     * 专业ID
     */
    private Long majorId;

    /**
     * 目标类型字典编码：知识目标/能力目标/素质目标
     */
    private String objectiveTypeCode;

    /**
     * 目标类型名称快照
     */
    private String objectiveTypeName;

    /**
     * 目标内容，手工录入
     */
    private String content;

    /**
     * 来源方式：2手工录入
     */
    private Integer sourceMode;

    /** 课程目标权重，普通课程目标权重合计必须为 1 */
    private java.math.BigDecimal weight;

    /**
     * 排序
     */
    private Integer sort;

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

    public Long getSchemeId() {
        return schemeId;
    }

    public void setSchemeId(Long schemeId) {
        this.schemeId = schemeId;
    }

    public Long getMajorId() {
        return majorId;
    }

    public void setMajorId(Long majorId) {
        this.majorId = majorId;
    }

    public String getObjectiveTypeCode() {
        return objectiveTypeCode;
    }

    public void setObjectiveTypeCode(String objectiveTypeCode) {
        this.objectiveTypeCode = objectiveTypeCode;
    }

    public String getObjectiveTypeName() {
        return objectiveTypeName;
    }

    public void setObjectiveTypeName(String objectiveTypeName) {
        this.objectiveTypeName = objectiveTypeName;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Integer getSourceMode() {
        return sourceMode;
    }

    public void setSourceMode(Integer sourceMode) {
        this.sourceMode = sourceMode;
    }

    public java.math.BigDecimal getWeight() {
        return weight;
    }

    public void setWeight(java.math.BigDecimal weight) {
        this.weight = weight;
    }

    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }
}
