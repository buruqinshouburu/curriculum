package com.doinner.csys.domain.vo;

/**
 * 支撑课程被引用侧聚合（原始值，未译字典）。
 * Service 层据此翻译为中文：courseAttrRaw -> cur_course_attribute；termRaw(1-10 顿号串) -> 第N学年秋/春。
 */
public class TeachingPlanSupportingAggVo {

    /** 支撑课程id */
    private Long courseId;

    /** 修读性质原始值：c2.course_attr 聚合(顿号)回退 sc.course_attr；字典 cur_course_attribute 编码 */
    private String courseAttrRaw;

    /** 学期安排原始值：t_csys_training_scheme_course_schedule.term(1-10) 聚合(顿号)；无被调用为 null */
    private String termRaw;

    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

    public String getCourseAttrRaw() {
        return courseAttrRaw;
    }

    public void setCourseAttrRaw(String courseAttrRaw) {
        this.courseAttrRaw = courseAttrRaw;
    }

    public String getTermRaw() {
        return termRaw;
    }

    public void setTermRaw(String termRaw) {
        this.termRaw = termRaw;
    }
}
