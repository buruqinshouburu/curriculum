package com.doinner.csys.domain;

import com.doinner.common.core.domain.db.AbstractDoinnerLogicalDelBaseEntity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 教学计划调用课程上下文实体
 * 记录总库课程被哪些培养方案调用，以及调用课程、排课、专业、对象等快照
 */
public class TeachingPlanContext extends AbstractDoinnerLogicalDelBaseEntity {

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 教学计划ID
     */
    private Long planId;

    /**
     * 总库课程ID
     */
    private Long sourceCourseId;

    /**
     * 调用课程ID，t_csys_course.id
     */
    private Long quoteCourseId;

    /**
     * 培养方案ID
     */
    private Long schemeId;

    /**
     * 培养方案排课ID
     */
    private Long scheduleId;

    /**
     * 培养方案名称快照
     */
    private String schemeName;

    /**
     * 培养方案版本快照
     */
    private String schemeVersion;

    /**
     * 适用对象/培养层次
     */
    private String educationLevel;

    /**
     * 培养对象类型
     */
    private String objectType;

    /**
     * 学历
     */
    private String education;

    /**
     * 学制类型
     */
    private String academicType;

    /**
     * 学制年限
     */
    private String durationType;

    /**
     * 授予学位类型
     */
    private String degree;

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
     * 课程模块编码
     */
    private String courseModule;

    /**
     * 课程子模块编码
     */
    private String courseModuleChildren;

    /**
     * 学期安排
     */
    private String semesterSchedule;

    /**
     * 开课学期
     */
    private Integer term;

    /**
     * 修读性质
     */
    private String courseAttr;

    /**
     * 时间安排
     */
    private String timeArrange;

    /**
     * 上下文总学时
     */
    private BigDecimal hours;

    /**
     * 上下文讲授学时
     */
    private BigDecimal teachHours;

    /**
     * 上下文实践/实验学时
     */
    private BigDecimal practiceHours;

    /**
     * 上下文学分
     */
    private BigDecimal credits;

    /**
     * 从课程调用关系同步时间
     */
    private LocalDateTime syncTime;

    /**
     * 同步状态：1有效 2调用关系已失效
     */
    private Integer syncFlag;

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

    public Long getSourceCourseId() {
        return sourceCourseId;
    }

    public void setSourceCourseId(Long sourceCourseId) {
        this.sourceCourseId = sourceCourseId;
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

    public Long getScheduleId() {
        return scheduleId;
    }

    public void setScheduleId(Long scheduleId) {
        this.scheduleId = scheduleId;
    }

    public String getSchemeName() {
        return schemeName;
    }

    public void setSchemeName(String schemeName) {
        this.schemeName = schemeName;
    }

    public String getSchemeVersion() {
        return schemeVersion;
    }

    public void setSchemeVersion(String schemeVersion) {
        this.schemeVersion = schemeVersion;
    }

    public String getEducationLevel() {
        return educationLevel;
    }

    public void setEducationLevel(String educationLevel) {
        this.educationLevel = educationLevel;
    }

    public String getObjectType() {
        return objectType;
    }

    public void setObjectType(String objectType) {
        this.objectType = objectType;
    }

    public String getEducation() {
        return education;
    }

    public void setEducation(String education) {
        this.education = education;
    }

    public String getAcademicType() {
        return academicType;
    }

    public void setAcademicType(String academicType) {
        this.academicType = academicType;
    }

    public String getDurationType() {
        return durationType;
    }

    public void setDurationType(String durationType) {
        this.durationType = durationType;
    }

    public String getDegree() {
        return degree;
    }

    public void setDegree(String degree) {
        this.degree = degree;
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

    public String getCourseModule() {
        return courseModule;
    }

    public void setCourseModule(String courseModule) {
        this.courseModule = courseModule;
    }

    public String getCourseModuleChildren() {
        return courseModuleChildren;
    }

    public void setCourseModuleChildren(String courseModuleChildren) {
        this.courseModuleChildren = courseModuleChildren;
    }

    public String getSemesterSchedule() {
        return semesterSchedule;
    }

    public void setSemesterSchedule(String semesterSchedule) {
        this.semesterSchedule = semesterSchedule;
    }

    public Integer getTerm() {
        return term;
    }

    public void setTerm(Integer term) {
        this.term = term;
    }

    public String getCourseAttr() {
        return courseAttr;
    }

    public void setCourseAttr(String courseAttr) {
        this.courseAttr = courseAttr;
    }

    public String getTimeArrange() {
        return timeArrange;
    }

    public void setTimeArrange(String timeArrange) {
        this.timeArrange = timeArrange;
    }

    public BigDecimal getHours() {
        return hours;
    }

    public void setHours(BigDecimal hours) {
        this.hours = hours;
    }

    public BigDecimal getTeachHours() {
        return teachHours;
    }

    public void setTeachHours(BigDecimal teachHours) {
        this.teachHours = teachHours;
    }

    public BigDecimal getPracticeHours() {
        return practiceHours;
    }

    public void setPracticeHours(BigDecimal practiceHours) {
        this.practiceHours = practiceHours;
    }

    public BigDecimal getCredits() {
        return credits;
    }

    public void setCredits(BigDecimal credits) {
        this.credits = credits;
    }

    public LocalDateTime getSyncTime() {
        return syncTime;
    }

    public void setSyncTime(LocalDateTime syncTime) {
        this.syncTime = syncTime;
    }

    public Integer getSyncFlag() {
        return syncFlag;
    }

    public void setSyncFlag(Integer syncFlag) {
        this.syncFlag = syncFlag;
    }

    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }
}