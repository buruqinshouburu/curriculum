package com.doinner.csys.domain.vo;

/**
 * 课程被选用情况表导出VO
 * <p>
 * 入参为源课程ID集合。源课程(source_id 为空)与被选用课程(source_id 指向源课程)通过 source_id 区分。
 * 课程基本信息(名称/编号)来自源课程 t_csys_course，
 * 学期学时安排(讲授+实践)来自被选用课程在 t_csys_training_scheme_course_schedule 的排课记录，
 * 选用单位/选用专业类/选用专业来自被选用课程 t_csys_course 自身的 college_id/category_id/major_id 关联字段。
 *
 * @author doinner
 */
public class CourseChooseStatusVo {

    /** 源课程id(被选用课程的 source_id) */
    private Long sourceCourseId;

    /** 课程id(被选用课程id) */
    private Long courseId;

    /** 课程名称(源课程名称) */
    private String courseName;

    /** 课程编号(源课程编号) */
    private String courseCode;

    /** 培养方案id */
    private Long schemeId;

    /** 学期 1-8 */
    private Integer term;

    /** 讲授学时 */
    private Double teachHours;

    /** 实践学时 */
    private Double practiceHours;

    /** 选用单位(培养方案所属学院) */
    private String collegeName;

    /** 选用专业类(门类) */
    private String categoryName;

    /** 选用专业 */
    private String majorName;

    public Long getSourceCourseId() {
        return sourceCourseId;
    }

    public void setSourceCourseId(Long sourceCourseId) {
        this.sourceCourseId = sourceCourseId;
    }

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

    public Long getSchemeId() {
        return schemeId;
    }

    public void setSchemeId(Long schemeId) {
        this.schemeId = schemeId;
    }

    public Integer getTerm() {
        return term;
    }

    public void setTerm(Integer term) {
        this.term = term;
    }

    public Double getTeachHours() {
        return teachHours;
    }

    public void setTeachHours(Double teachHours) {
        this.teachHours = teachHours;
    }

    public Double getPracticeHours() {
        return practiceHours;
    }

    public void setPracticeHours(Double practiceHours) {
        this.practiceHours = practiceHours;
    }

    public String getCollegeName() {
        return collegeName;
    }

    public void setCollegeName(String collegeName) {
        this.collegeName = collegeName;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getMajorName() {
        return majorName;
    }

    public void setMajorName(String majorName) {
        this.majorName = majorName;
    }
}
