package com.doinner.csys.domain.vo;

import java.math.BigDecimal;

/**
 * 教学计划详情返回。
 *
 * 逻辑（实时取数，保证课程相关字段与总库一致）：
 * 1) 课程名称/编号/英文名/类型/学时/学分等课程字段一律取自 t_csys_course；
 *    教学计划表上的 source_* 快照字段已废弃，不再作为详情来源。
 * 2) teachingPlanId / scoreRule / sourceCourseEnName 来自 t_csys_teaching_plan（教学计划自身字段）；
 *    启用时间 enabledTerm 取自 t_csys_course.version（课程版本/启用年份）。
 * 3) 适用对象/适用专业/修读性质/课程模块：被引用课程 c2 + 被引用培养方案 ts 聚合（多值顿号分隔），
 *    无引用时回退总库课程自身字段。
 *    Service 层会把适用对象（sys_education_level）与课程模块（KG 字典 id）译为中文名称后再返回。
 * 4) 开课学期 term：取培养方案关联的课程执行方案 t_csys_training_scheme_course_schedule.term
 *    （字典值 1-10，对应第一学年（秋）~第五学年（春）），跨全部引用培养方案去重后升序拼接；
 *    Service 层会翻译为中文标签；无排课记录时回退 course.open_term。
 */
public class TeachingPlanDetailVo {

    /** 总库课程id */
    private Long courseId;

    /** 教学计划id(可能为空) */
    private Long teachingPlanId;

    /** 课程名称（实时取自 t_csys_course.name） */
    private String courseName;

    /** 课程编号（实时取自 t_csys_course.code） */
    private String courseCode;

    /** 课程英文名称（实时取自 t_csys_course.en_name） */
    private String courseEnName;

    /**
     * 教学计划表上的源课英文名快照（t_csys_teaching_plan.source_course_en_name）。
     * 仅 teachingPlanId 存在时有值；无计划时为 null。
     */
    private String sourceCourseEnName;

    /** 课程类型(与教学计划类型同一取值)：1课程 2实践训练课目 3实验课程 4实践项目 */
    private String type;

    /** 启用时间（实时取自 t_csys_course.version，如 2026） */
    private String enabledTerm;

    /** 适用对象（被引用培养方案 education_level 聚合后译为 sys_education_level 中文 label，多值顿号分隔） */
    private String educationLevel;

    /**
     * 开课学期：t_csys_training_scheme_course_schedule.term 跨培养方案去重拼接后的中文标签，
     * 如「第一学年（秋）、第二学年（春）」；字典原值 1-10。
     */
    private String term;

    /** 课程模块（被引用课程聚合后译为 KG 字典名称，多值顿号分隔） */
    private String courseModule;

    /** 适用专业id（总库课程 major_Id 兜底单值） */
    private Long majorId;

    /** 适用专业名称(被引用培养方案major_id聚合,多值拼接) */
    private String majorName;

    /** 修读性质（被引用课程聚合，多值顿号分隔） */
    private String courseAttr;

    /** 讲授学时（实时取自 t_csys_course.teach_hours） */
    private BigDecimal teachHours;

    /** 实践学时（实时取自 t_csys_course.practice_hours） */
    private BigDecimal practiceHours;

    /** 总学时（实时取自 t_csys_course.hours） */
    private BigDecimal hours;

    /** 学分（实时取自 t_csys_course.credit） */
    private BigDecimal credit;

    /** 计分规则 -> t_csys_teaching_plan.score_rule（无教学计划时为 null） */
    private String scoreRule;

    /**
     * 是否公共基础课程（源课 course_Module == 公共基础字典 id）。
     * true 时：第四节目标/毕业要求为 plan 级单组，不按培养方案拆分；
     * 前端可据此隐藏培养方案 tab、objective/tree 可不传 schemeId。
     */
    private Boolean publicFoundation;

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

    public String getSourceCourseEnName() {
        return sourceCourseEnName;
    }

    public void setSourceCourseEnName(String sourceCourseEnName) {
        this.sourceCourseEnName = sourceCourseEnName;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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

    public String getMajorName() {
        return majorName;
    }

    public void setMajorName(String majorName) {
        this.majorName = majorName;
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

    public String getScoreRule() {
        return scoreRule;
    }

    public void setScoreRule(String scoreRule) {
        this.scoreRule = scoreRule;
    }

    public Boolean getPublicFoundation() {
        return publicFoundation;
    }

    public void setPublicFoundation(Boolean publicFoundation) {
        this.publicFoundation = publicFoundation;
    }
}
