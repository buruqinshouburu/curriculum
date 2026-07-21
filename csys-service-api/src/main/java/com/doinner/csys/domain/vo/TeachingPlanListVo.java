package com.doinner.csys.domain.vo;

import java.math.BigDecimal;

/**
 * 课程教学计划管理列表返回行。
 *
 * 主数据来自总库课程 t_csys_course，left join t_csys_teaching_plan 得到该课程当前的教学计划信息
 * (未建教学计划时计划相关字段为 null)。
 */
public class TeachingPlanListVo {

    // ============ 总库课程信息 ============

    /** 总库课程id (t_csys_course.id) */
    private Long courseId;

    /** 课程名称 */
    private String courseName;

    /** 课程编号 */
    private String courseCode;

    /** 开课单位id -> t_csys_course.college_id（与 /course/list 一致） */
    private Long collegeId;

    /** 适用对象 */
    private String educationLevel;

    /** 课程模块编码 */
    private String courseModule;

    /** 课程模块名称 */
    private String courseModuleName;

    /** 修读要求 */
    private String courseAttr;

    /** 课程类型 (1课程 2实践训练课目 3实验课程 4实践项目) -> t_csys_course.type */
    private String type;

    /** 课程版本 -> t_csys_course.version */
    private String version;

    /** 项目层级 -> t_csys_course.program_Level */
    private String programLevel;

    /** 适用专业名称 */
    private String majorName;

    /** 专业方向id -> t_csys_course.sub_Major_Id */
    private Long subMajorId;

    /** 专业方向名称 */
    private String subMajorName;

    /** 开课单位名称（由 collegeId 翻译） */
    private String collegeName;

    /** 课程模块子项编码 -> t_csys_course.course_Module_Children */
    private String courseModuleChildren;

    /** 课程模块子项名称 */
    private String courseModuleChildrenName;

    /** 适用专业id -> t_csys_course.major_Id */
    private Long majorId;

    /** 总学时 */
    private Double hours;

    /** 学分 */
    private Double credit;

    // ============ 教学计划信息(left join，可能为空) ============

    /** 教学计划id (t_csys_teaching_plan.id) */
    private Long teachingPlanId;

    /** 计划类型：1普通课程 2实验课程 3实践训练课目 4实践项目 */
    private Integer planType;

    /** 教学计划版本 */
    private String planVersion;

    /** 是否当前版本：1是 0否 */
    private Integer currentFlag;

    /** 启用时间 */
    private String enabledTerm;

    /** 状态：0草稿 1审核中 2通过 3退回 9停用 */
    private Integer status;

    /** 教学计划文件id */
    private String fileId;

    /** 教学计划文件名称 */
    private String fileName;

    /** 总库课程学分快照 */
    private BigDecimal sourceCredit;

    /** 计分规则 -> t_csys_teaching_plan.score_rule（未建教学计划时为 null） */
    private String scoreRule;

    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
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

    public Long getCollegeId() {
        return collegeId;
    }

    public void setCollegeId(Long collegeId) {
        this.collegeId = collegeId;
    }

    public String getEducationLevel() {
        return educationLevel;
    }

    public void setEducationLevel(String educationLevel) {
        this.educationLevel = educationLevel;
    }

    public String getCourseModule() {
        return courseModule;
    }

    public void setCourseModule(String courseModule) {
        this.courseModule = courseModule;
    }

    public String getCourseModuleName() {
        return courseModuleName;
    }

    public void setCourseModuleName(String courseModuleName) {
        this.courseModuleName = courseModuleName;
    }

    public String getCourseAttr() {
        return courseAttr;
    }

    public void setCourseAttr(String courseAttr) {
        this.courseAttr = courseAttr;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getProgramLevel() {
        return programLevel;
    }

    public void setProgramLevel(String programLevel) {
        this.programLevel = programLevel;
    }

    public String getMajorName() {
        return majorName;
    }

    public void setMajorName(String majorName) {
        this.majorName = majorName;
    }

    public Long getSubMajorId() {
        return subMajorId;
    }

    public void setSubMajorId(Long subMajorId) {
        this.subMajorId = subMajorId;
    }

    public String getSubMajorName() {
        return subMajorName;
    }

    public void setSubMajorName(String subMajorName) {
        this.subMajorName = subMajorName;
    }

    public String getCollegeName() {
        return collegeName;
    }

    public void setCollegeName(String collegeName) {
        this.collegeName = collegeName;
    }

    public String getCourseModuleChildren() {
        return courseModuleChildren;
    }

    public void setCourseModuleChildren(String courseModuleChildren) {
        this.courseModuleChildren = courseModuleChildren;
    }

    public String getCourseModuleChildrenName() {
        return courseModuleChildrenName;
    }

    public void setCourseModuleChildrenName(String courseModuleChildrenName) {
        this.courseModuleChildrenName = courseModuleChildrenName;
    }

    public Long getMajorId() {
        return majorId;
    }

    public void setMajorId(Long majorId) {
        this.majorId = majorId;
    }

    public Double getHours() {
        return hours;
    }

    public void setHours(Double hours) {
        this.hours = hours;
    }

    public Double getCredit() {
        return credit;
    }

    public void setCredit(Double credit) {
        this.credit = credit;
    }

    public Long getTeachingPlanId() {
        return teachingPlanId;
    }

    public void setTeachingPlanId(Long teachingPlanId) {
        this.teachingPlanId = teachingPlanId;
    }

    public Integer getPlanType() {
        return planType;
    }

    public void setPlanType(Integer planType) {
        this.planType = planType;
    }

    public String getPlanVersion() {
        return planVersion;
    }

    public void setPlanVersion(String planVersion) {
        this.planVersion = planVersion;
    }

    public Integer getCurrentFlag() {
        return currentFlag;
    }

    public void setCurrentFlag(Integer currentFlag) {
        this.currentFlag = currentFlag;
    }

    public String getEnabledTerm() {
        return enabledTerm;
    }

    public void setEnabledTerm(String enabledTerm) {
        this.enabledTerm = enabledTerm;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public BigDecimal getSourceCredit() {
        return sourceCredit;
    }

    public void setSourceCredit(BigDecimal sourceCredit) {
        this.sourceCredit = sourceCredit;
    }

    public String getScoreRule() {
        return scoreRule;
    }

    public void setScoreRule(String scoreRule) {
        this.scoreRule = scoreRule;
    }
}
