package com.doinner.csys.domain.vo;

/**
 * 教学计划详情-支撑课程或实践训练科目（type=4 实践项目）。
 *
 * type=4 课程：beforeCourse（预修课程）语义为「支撑课程」，afterCourse（后续课程）语义为「支撑训练课目」。
 * 每条支撑课程带回 学期安排(term) / 时间安排(timeArrangement) / 修读性质(courseAttr) 三字段，
 * 取值规则与主课程详情一致：优先被调用课程 c2（c2.source_id=支撑课id）聚合，多值顿号分隔；
 * 无被调用时回退支撑课自身字段。
 *
 * - 修读性质：c2.course_attr 聚合回退自身 course_attr，字典 cur_course_attribute 译中。
 * - 学期安排：优先培养方案排课 t_csys_training_scheme_course_schedule.term(1-10) 译为「第N学年秋/春」；
 *   无被调用时回退课程子表 t_csys_course_ref_schedule 的 semester_Schedule + spring_Autumn 拼成「第N学年秋/春」。
 * - 时间安排：time_Week + unit(字典 sys_course_unit 译中)，c2 优先回退自身，形如「16周」。
 */
public class TeachingPlanSupportingCourseVo {

    /** 支撑课程/训练课目 id（t_csys_course.id） */
    private Long courseId;

    /** 支撑课程/训练课目名称 */
    private String courseName;

    /** 引用类型：1支撑课程（beforeCourse） 2支撑训练课目（afterCourse） */
    private Integer refType;

    /** 学期安排，形如「第一学年秋、第二学年春」 */
    private String term;

    /** 时间安排，形如「16周」 */
    private String timeArrangement;

    /** 修读性质，字典 cur_course_attribute 译中，多值顿号分隔 */
    private String courseAttr;

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

    public Integer getRefType() {
        return refType;
    }

    public void setRefType(Integer refType) {
        this.refType = refType;
    }

    public String getTerm() {
        return term;
    }

    public void setTerm(String term) {
        this.term = term;
    }

    public String getTimeArrangement() {
        return timeArrangement;
    }

    public void setTimeArrangement(String timeArrangement) {
        this.timeArrangement = timeArrangement;
    }

    public String getCourseAttr() {
        return courseAttr;
    }

    public void setCourseAttr(String courseAttr) {
        this.courseAttr = courseAttr;
    }
}
