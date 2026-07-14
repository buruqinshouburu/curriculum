package com.doinner.csys.domain.vo;

import java.math.BigDecimal;

/**
 * 教学计划详情返回。
 *
 * 逻辑：
 * 1) 教学计划id为空时，字段全部取自总库课程 t_csys_course；
 * 2) 教学计划id存在时，基础信息取自 t_csys_teaching_plan，
 *    适用对象/开课学期/课程模块/适用专业/修读性质/学时学分等取自 t_csys_teaching_plan_context。
 */
public class TeachingPlanDetailVo {

    /** 总库课程id */
    private Long courseId;

    /** 教学计划id(可能为空) */
    private Long teachingPlanId;

    /** 课程名称 */
    private String courseName;

    /** 课程编号 */
    private String courseCode;

    /** 课程英文名称 */
    private String courseEnName;

    /** 启用时间 */
    private String enabledTerm;

    /** 适用对象 */
    private String educationLevel;

    /** 开课学期(课程分支取 open_term 文本，计划分支取 context.term) */
    private String term;

    /** 课程模块 */
    private String courseModule;

    /** 适用专业id */
    private Long majorId;

    /** 修读性质 */
    private String courseAttr;

    /** 讲授学时 */
    private BigDecimal teachHours;

    /** 实践学时 */
    private BigDecimal practiceHours;

    /** 总学时 */
    private BigDecimal hours;

    /** 学分 */
    private BigDecimal credit;

    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

    public Long getTeachingPlanId() {
        return teachingPlanId;
    }

    public void setTeachingPlanId(Long teachingPlanId) {
        this.teachingPlanId = teachingPlanId;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode;
    }

    public String getCourseEnName() {
        return courseEnName;
    }

    public void setCourseEnName(String courseEnName) {
        this.courseEnName = courseEnName;
    }

    public String getEnabledTerm() {
        return enabledTerm;
    }

    public void setEnabledTerm(String enabledTerm) {
        this.enabledTerm = enabledTerm;
    }

    public String getEducationLevel() {
        return educationLevel;
    }

    public void setEducationLevel(String educationLevel) {
        this.educationLevel = educationLevel;
    }

    public String getTerm() {
        return term;
    }

    public void setTerm(String term) {
        this.term = term;
    }

    public String getCourseModule() {
        return courseModule;
    }

    public void setCourseModule(String courseModule) {
        this.courseModule = courseModule;
    }

    public Long getMajorId() {
        return majorId;
    }

    public void setMajorId(Long majorId) {
        this.majorId = majorId;
    }

    public String getCourseAttr() {
        return courseAttr;
    }

    public void setCourseAttr(String courseAttr) {
        this.courseAttr = courseAttr;
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

    public BigDecimal getHours() {
        return hours;
    }

    public void setHours(BigDecimal hours) {
        this.hours = hours;
    }

    public BigDecimal getCredit() {
        return credit;
    }

    public void setCredit(BigDecimal credit) {
        this.credit = credit;
    }
}
