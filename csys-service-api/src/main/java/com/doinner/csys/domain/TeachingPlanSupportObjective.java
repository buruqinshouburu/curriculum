package com.doinner.csys.domain;

import com.doinner.common.core.domain.db.AbstractDoinnerLogicalDelBaseEntity;

/**
 * 实践项目教学计划(type=4)第二节「支撑的课程目标或训练目的」绑定实体。
 *
 * 计划级多选绑定，不按培养方案(scheme)区分：
 * - {@link #refType}=1：从支撑课程(源课 before_course_id)的课程教学计划第四部分绑课程目标；
 * - {@link #refType}=2：从支撑训练课目(源课 after_course_id)的实践训练课目第二部分绑训练目的。
 *
 * 内容快照(itemName/itemTypeName)在保存时从来源目标/目的实体回填，Word 渲染直接读快照，
 * 与 objective_ref / training_purpose_ref 的毕业要求快照模式一致。
 */
public class TeachingPlanSupportObjective extends AbstractDoinnerLogicalDelBaseEntity {

    /** 主键ID */
    private Long id;

    /** 实践项目教学计划ID(type4) */
    private Long planId;

    /** 绑定类型：1课程目标 2训练目的 */
    private Integer refType;

    /** 来源教学计划ID(支撑课程/训练课目的教学计划) */
    private Long refPlanId;

    /** 支撑课程/训练课目ID */
    private Long refCourseId;

    /** 绑定的课程目标ID(ref_type=1) */
    private Long objectiveId;

    /** 绑定的训练目的ID(ref_type=2) */
    private Long purposeId;

    /** 内容快照(课程目标内容/训练目的文本) */
    private String itemName;

    /** 课程目标类型名称快照(知识/能力/素质目标)；训练目的为空 */
    private String itemTypeName;

    /** 来源目标所属专业ID快照(同专业优先排序用) */
    private Long majorId;

    /** 排序 */
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

    public Integer getRefType() {
        return refType;
    }

    public void setRefType(Integer refType) {
        this.refType = refType;
    }

    public Long getRefPlanId() {
        return refPlanId;
    }

    public void setRefPlanId(Long refPlanId) {
        this.refPlanId = refPlanId;
    }

    public Long getRefCourseId() {
        return refCourseId;
    }

    public void setRefCourseId(Long refCourseId) {
        this.refCourseId = refCourseId;
    }

    public Long getObjectiveId() {
        return objectiveId;
    }

    public void setObjectiveId(Long objectiveId) {
        this.objectiveId = objectiveId;
    }

    public Long getPurposeId() {
        return purposeId;
    }

    public void setPurposeId(Long purposeId) {
        this.purposeId = purposeId;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getItemTypeName() {
        return itemTypeName;
    }

    public void setItemTypeName(String itemTypeName) {
        this.itemTypeName = itemTypeName;
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
}
