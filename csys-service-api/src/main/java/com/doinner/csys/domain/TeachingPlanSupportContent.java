package com.doinner.csys.domain;

import com.doinner.common.core.domain.db.AbstractDoinnerLogicalDelBaseEntity;

/**
 * 实践项目教学计划(type=4)第二节「涉及的知识体系或训练内容」绑定实体。
 *
 * 计划级多选绑定，不按培养方案(scheme)区分：
 * - {@link #refType}=1：从支撑课程(源课 before_course_id)的课程教学计划 content 绑知识体系(知识单元知识点，全部内容行)；
 * - {@link #refType}=2：从支撑训练课目(源课 after_course_id)的实践训练课目第四部分绑训练内容。
 *
 * 内容名称快照(itemTitle)在保存时从来源 content 实体回填，Word 渲染直接读快照。
 */
public class TeachingPlanSupportContent extends AbstractDoinnerLogicalDelBaseEntity {

    /** 主键ID */
    private Long id;

    /** 实践项目教学计划ID(type4) */
    private Long planId;

    /** 绑定类型：1知识体系(课程教学内容) 2训练内容(课目训练内容) */
    private Integer refType;

    /** 来源教学计划ID(支撑课程/训练课目的教学计划) */
    private Long refPlanId;

    /** 支撑课程/训练课目ID */
    private Long refCourseId;

    /** 绑定的教学内容ID t_csys_teaching_plan_content.id */
    private Long contentId;

    /** 内容名称快照 */
    private String itemTitle;

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

    public Long getContentId() {
        return contentId;
    }

    public void setContentId(Long contentId) {
        this.contentId = contentId;
    }

    public String getItemTitle() {
        return itemTitle;
    }

    public void setItemTitle(String itemTitle) {
        this.itemTitle = itemTitle;
    }

    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }
}
