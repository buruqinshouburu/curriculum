package com.doinner.csys.domain;

import com.doinner.common.core.domain.db.AbstractDoinnerLogicalDelBaseEntity;

/**
 * 教学计划任务背景支撑毕业要求实体。
 * <p>
 * 每条记录表示一条任务背景绑定一个毕业要求，结构对标 {@link TeachingPlanObjectiveRef}。
 */
public class TeachingPlanTaskBackgroundRef extends AbstractDoinnerLogicalDelBaseEntity {

    /** 主键ID */
    private Long id;

    /** 教学计划ID */
    private Long planId;

    /** 任务背景ID */
    private Long taskBackgroundId;

    /** 培养方案调用课程毕业要求关联ID，优先关联 t_csys_scheme_course_ref_graduation.id */
    private Long schemeCourseGraduationId;

    /** 调用课程ID快照 */
    private Long quoteCourseId;

    /** 培养方案ID快照 */
    private Long schemeId;

    /** 方案内毕业标准ID，t_csys_std_graduation.id */
    private Long graduationId;

    /** 毕业标准总库ID，通常为 t_csys_std_graduation.source_id */
    private Long sourceGraduationId;

    /** 毕业标准编码快照 */
    private String graduationCode;

    /** 毕业标准名称快照 */
    private String graduationName;

    /** 绑定来源：scheme_course_ref 或 course_ref_graduation */
    private String graduationBindSource;

    /** 支撑说明 */
    private String supportDesc;

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

    public Long getTaskBackgroundId() {
        return taskBackgroundId;
    }

    public void setTaskBackgroundId(Long taskBackgroundId) {
        this.taskBackgroundId = taskBackgroundId;
    }

    public Long getSchemeCourseGraduationId() {
        return schemeCourseGraduationId;
    }

    public void setSchemeCourseGraduationId(Long schemeCourseGraduationId) {
        this.schemeCourseGraduationId = schemeCourseGraduationId;
    }

    public Long getQuoteCourseId() {
        return quoteCourseId;
    }

    public void setQuoteCourseId(Long quoteCourseId) {
        this.quoteCourseId = quoteCourseId;
    }

    public Long getSchemeId() {
        return schemeId;
    }

    public void setSchemeId(Long schemeId) {
        this.schemeId = schemeId;
    }

    public Long getGraduationId() {
        return graduationId;
    }

    public void setGraduationId(Long graduationId) {
        this.graduationId = graduationId;
    }

    public Long getSourceGraduationId() {
        return sourceGraduationId;
    }

    public void setSourceGraduationId(Long sourceGraduationId) {
        this.sourceGraduationId = sourceGraduationId;
    }

    public String getGraduationCode() {
        return graduationCode;
    }

    public void setGraduationCode(String graduationCode) {
        this.graduationCode = graduationCode;
    }

    public String getGraduationName() {
        return graduationName;
    }

    public void setGraduationName(String graduationName) {
        this.graduationName = graduationName;
    }

    public String getGraduationBindSource() {
        return graduationBindSource;
    }

    public void setGraduationBindSource(String graduationBindSource) {
        this.graduationBindSource = graduationBindSource;
    }

    public String getSupportDesc() {
        return supportDesc;
    }

    public void setSupportDesc(String supportDesc) {
        this.supportDesc = supportDesc;
    }

    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }
}
