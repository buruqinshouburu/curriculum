package com.doinner.csys.domain;

import com.doinner.common.core.domain.db.AbstractDoinnerLogicalDelBaseEntity;

import java.math.BigDecimal;

/**
 * 教学目标达成设计实体
 * 知识/能力/素质目标达成设计，绑定知识单元、知识点、教学环节、教法、学法、观测点
 */
public class TeachingPlanTargetDesign extends AbstractDoinnerLogicalDelBaseEntity {

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 教学计划ID
     */
    private Long planId;

    /**
     * 教学计划调用上下文ID；按tab维护时填写
     */
    private Long schemeId;

    /**
     * 设计类型字典编码：知识目标/能力目标/素质目标
     */
    private String designTypeCode;

    /**
     * 对应教学计划目标ID
     */
    private Long objectiveId;

    /**
     * 知识单元ID，t_csys_course_knowledge_unit.id
     */
    private Long knowledgeUnitId;

    /**
     * 知识单元名称快照
     */
    private String knowledgeUnitName;

    /**
     * 知识点ID，t_csys_course_knowledge_point.id
     */
    private Long knowledgePointId;

    /**
     * 知识点名称快照
     */
    private String knowledgePointName;

    /**
     * 观测点
     */
    private String observationPoint;

    /**
     * 关联教学内容ID数组（JSON字符串）
     */
    private String contentIds;

    /**
     * 教学内容文本快照
     */
    private String contentText;

    /**
     * 教学环节
     */
    private String teachingLink;

    /**
     * 教法
     */
    private String teachingMethod;

    /**
     * 学法
     */
    private String learningMethod;

    /**
     * 学时
     */
    private BigDecimal hours;

    /**
     * 教学设计
     */
    private String teachingDesign;

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

    public String getDesignTypeCode() {
        return designTypeCode;
    }

    public void setDesignTypeCode(String designTypeCode) {
        this.designTypeCode = designTypeCode;
    }

    public Long getObjectiveId() {
        return objectiveId;
    }

    public void setObjectiveId(Long objectiveId) {
        this.objectiveId = objectiveId;
    }

    public Long getKnowledgeUnitId() {
        return knowledgeUnitId;
    }

    public void setKnowledgeUnitId(Long knowledgeUnitId) {
        this.knowledgeUnitId = knowledgeUnitId;
    }

    public String getKnowledgeUnitName() {
        return knowledgeUnitName;
    }

    public void setKnowledgeUnitName(String knowledgeUnitName) {
        this.knowledgeUnitName = knowledgeUnitName;
    }

    public Long getKnowledgePointId() {
        return knowledgePointId;
    }

    public void setKnowledgePointId(Long knowledgePointId) {
        this.knowledgePointId = knowledgePointId;
    }

    public String getKnowledgePointName() {
        return knowledgePointName;
    }

    public void setKnowledgePointName(String knowledgePointName) {
        this.knowledgePointName = knowledgePointName;
    }

    public String getObservationPoint() {
        return observationPoint;
    }

    public void setObservationPoint(String observationPoint) {
        this.observationPoint = observationPoint;
    }

    public String getContentIds() {
        return contentIds;
    }

    public void setContentIds(String contentIds) {
        this.contentIds = contentIds;
    }

    public String getContentText() {
        return contentText;
    }

    public void setContentText(String contentText) {
        this.contentText = contentText;
    }

    public String getTeachingLink() {
        return teachingLink;
    }

    public void setTeachingLink(String teachingLink) {
        this.teachingLink = teachingLink;
    }

    public String getTeachingMethod() {
        return teachingMethod;
    }

    public void setTeachingMethod(String teachingMethod) {
        this.teachingMethod = teachingMethod;
    }

    public String getLearningMethod() {
        return learningMethod;
    }

    public void setLearningMethod(String learningMethod) {
        this.learningMethod = learningMethod;
    }

    public BigDecimal getHours() {
        return hours;
    }

    public void setHours(BigDecimal hours) {
        this.hours = hours;
    }

    public String getTeachingDesign() {
        return teachingDesign;
    }

    public void setTeachingDesign(String teachingDesign) {
        this.teachingDesign = teachingDesign;
    }

    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }
}