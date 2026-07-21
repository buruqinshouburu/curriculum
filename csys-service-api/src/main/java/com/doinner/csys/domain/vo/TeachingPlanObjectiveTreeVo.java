package com.doinner.csys.domain.vo;

import java.util.List;

/**
 * 教学计划「课程目标与支撑毕业要求」总览树节点。
 * <p>
 * 结构对齐 {@code TrainingController#viewTrainingCourseKnowLedge}：
 * 顶层为目标类型（字典 sys_plan_target_type），children 为目标内容；
 * 目标节点的 children 为支撑毕业要求（TeachingPlanObjectiveRef）。
 */
public class TeachingPlanObjectiveTreeVo {

    /** 节点id：类型层=字典dictValue；目标层=objectiveId 字符串 */
    private String id;

    /** 节点名称：类型层=字典标签；目标层=目标内容 content */
    private String name;

    /** 目标类型字典编码(sys_plan_target_type.dict_value) */
    private String objectiveTypeCode;

    /** 目标类型名称(字典标签 / 快照) */
    private String objectiveTypeName;

    /** 教学目标id（仅目标层有值） */
    private Long objectiveId;

    /** 目标内容（仅目标层） */
    private String content;

    /** 培养方案id */
    private Long schemeId;

    /** 教学计划id */
    private Long planId;

    /** 专业id（目标层） */
    private Long majorId;

    /** 排序 */
    private Integer sort;

    /**
     * 子节点：
     * - 类型层：目标列表(TeachingPlanObjectiveTreeVo)
     * - 目标层：支撑毕业要求(TeachingPlanObjectiveRef)
     */
    private List<?> children;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public Long getObjectiveId() {
        return objectiveId;
    }

    public void setObjectiveId(Long objectiveId) {
        this.objectiveId = objectiveId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Long getSchemeId() {
        return schemeId;
    }

    public void setSchemeId(Long schemeId) {
        this.schemeId = schemeId;
    }

    public Long getPlanId() {
        return planId;
    }

    public void setPlanId(Long planId) {
        this.planId = planId;
    }

    public Long getMajorId() {
        return majorId;
    }

    public void setMajorId(Long majorId) {
        this.majorId = majorId;
    }

    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }

    public List<?> getChildren() {
        return children;
    }

    public void setChildren(List<?> children) {
        this.children = children;
    }
}
