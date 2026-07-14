package com.doinner.csys.domain;

import com.doinner.common.core.domain.db.AbstractDoinnerLogicalDelBaseEntity;

/**
 * 培养方案调用课程毕业要求关联表实体
 * 保存“培养方案 + 调用课程 + 方案内毕业要求”的绑定
 */
public class SchemeCourseRefGraduation extends AbstractDoinnerLogicalDelBaseEntity {

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 培养方案ID
     */
    private Long schemeId;

    /**
     * 调用课程ID，t_csys_course.id
     */
    private Long quoteCourseId;

    /**
     * 总库课程ID快照
     */
    private Long sourceCourseId;

    /**
     * 方案内毕业标准ID，t_csys_std_graduation.id
     */
    private Long graduationId;

    /**
     * 毕业标准总库ID，通常为t_csys_std_graduation.source_id
     */
    private Long sourceGraduationId;

    /**
     * 学院ID
     */
    private Long collegeId;

    /**
     * 门类ID
     */
    private Long categoryId;

    /**
     * 专业ID
     */
    private Long majorId;

    /**
     * 专业方向ID
     */
    private Long subMajorId;

    /**
     * 支撑强度/支撑程度字典编码，可选
     */
    private String supportLevel;

    /**
     * 毕业标准编码快照
     */
    private String graduationCode;

    /**
     * 毕业标准名称快照
     */
    private String graduationName;

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

    public Long getSchemeId() {
        return schemeId;
    }

    public void setSchemeId(Long schemeId) {
        this.schemeId = schemeId;
    }

    public Long getQuoteCourseId() {
        return quoteCourseId;
    }

    public void setQuoteCourseId(Long quoteCourseId) {
        this.quoteCourseId = quoteCourseId;
    }

    public Long getSourceCourseId() {
        return sourceCourseId;
    }

    public void setSourceCourseId(Long sourceCourseId) {
        this.sourceCourseId = sourceCourseId;
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

    public Long getCollegeId() {
        return collegeId;
    }

    public void setCollegeId(Long collegeId) {
        this.collegeId = collegeId;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public Long getMajorId() {
        return majorId;
    }

    public void setMajorId(Long majorId) {
        this.majorId = majorId;
    }

    public Long getSubMajorId() {
        return subMajorId;
    }

    public void setSubMajorId(Long subMajorId) {
        this.subMajorId = subMajorId;
    }

    public String getSupportLevel() {
        return supportLevel;
    }

    public void setSupportLevel(String supportLevel) {
        this.supportLevel = supportLevel;
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

    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }
}