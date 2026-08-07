package com.doinner.csys.domain;

import com.doinner.common.core.domain.db.AbstractDoinnerLogicalDelBaseEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.math.BigDecimal;
import java.util.List;

/**
 * 教学目标达成设计实体。
 * <p>
 * 知识/能力/素质目标达成设计：
 * <ul>
 *   <li>支撑目标：{@link #objectiveText} 直接存目标内容字符串（不再依赖 objectiveId 关联）</li>
 *   <li>知识目标可绑定多个知识点（可跨知识单元）：{@link #knowledgePoints}</li>
 *   <li>能力/素质目标仍用观测点 + 教学设计，不绑知识点</li>
 *   <li>教学环节 / 教法 / 学法为字典值（编码或名称），由前端从字典表选择后写入字符串</li>
 * </ul>
 */
public class TeachingPlanTargetDesign extends AbstractDoinnerLogicalDelBaseEntity {

    private Long id;
    private Long planId;
    /** 培养方案 tab id */
    private Long schemeId;
    /** 设计类型字典编码：知识目标 / 能力目标 / 素质目标 */
    private String designTypeCode;
    /**
     * 对应教学计划目标ID（可选，兼容旧数据）。
     * 新流程优先写 {@link #objectiveText}。
     */
    private Long objectiveId;
    /**
     * 支撑的课程目标文本（知识/能力/素质目标内容）。
     * 前端从目标选项接口取 content 后直接写入，同名合并后的字符串。
     */
    private String objectiveText;
    /** 知识单元ID（兼容：单知识点或 knowledgePoints 首项回填） */
    private Long knowledgeUnitId;
    private String knowledgeUnitName;
    /** 知识点ID（兼容：单知识点或 knowledgePoints 首项回填） */
    private Long knowledgePointId;
    private String knowledgePointName;
    /**
     * 知识目标绑定的多个知识点（可跨不同知识单元）。
     * 接口入参/出参用此列表；Service 与 {@link #knowledgePointsJson} 互转后落库。
     */
    private List<KnowledgePointItem> knowledgePoints;
    /**
     * 对应表字段 knowledge_points（JSON 原文），MyBatis 读写用；接口不返回，请用 knowledgePoints。
     */
    @JsonIgnore
    private String knowledgePointsJson;
    /** 观测点（能力/素质） */
    private String observationPoint;
    /** 关联教学内容ID数组（JSON字符串） */
    private String contentIds;
    /** 教学内容文本快照 */
    private String contentText;
    /**
     * 教学环节（字典值，如 sys 字典编码或名称）
     */
    private String teachingLink;
    /** 教法（字典值） */
    private String teachingMethod;
    /** 学法（字典值） */
    private String learningMethod;
    private BigDecimal hours;
    /** 教学设计（能力/素质） */
    private String teachingDesign;
    private Integer sort;
    /**
     * 设计类型名称（字典 sys_plan_target_type 的 label，如"知识目标"）。
     * 仅生成 Word 时由 Service 译码填充用于分流，不入库、不由前端维护。
     */
    private String designTypeName;

    /**
     * 知识目标下绑定的单个知识点项（可属于不同知识单元）。
     */
    public static class KnowledgePointItem {
        private Long knowledgeUnitId;
        private String knowledgeUnitName;
        private Long knowledgePointId;
        private String knowledgePointName;

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
    }

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

    public String getObjectiveText() {
        return objectiveText;
    }

    public void setObjectiveText(String objectiveText) {
        this.objectiveText = objectiveText;
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

    public List<KnowledgePointItem> getKnowledgePoints() {
        return knowledgePoints;
    }

    public void setKnowledgePoints(List<KnowledgePointItem> knowledgePoints) {
        this.knowledgePoints = knowledgePoints;
    }

    public String getKnowledgePointsJson() {
        return knowledgePointsJson;
    }

    public void setKnowledgePointsJson(String knowledgePointsJson) {
        this.knowledgePointsJson = knowledgePointsJson;
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

    public String getDesignTypeName() {
        return designTypeName;
    }

    public void setDesignTypeName(String designTypeName) {
        this.designTypeName = designTypeName;
    }
}
