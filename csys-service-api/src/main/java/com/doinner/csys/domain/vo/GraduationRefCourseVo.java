package com.doinner.csys.domain.vo;

/**
 * 毕业要求-课程 绑定关系(含课程信息)，用于支撑矩阵导出/展示。
 *
 * @author doinner
 */
public class GraduationRefCourseVo {

    /** 毕业要求id(叶子节点) */
    private Long graduationId;

    /** 课程id */
    private Long courseId;

    /** 课程名称 */
    private String courseName;

    /** 课程编号 */
    private String courseCode;

    public Long getGraduationId() {
        return graduationId;
    }

    public void setGraduationId(Long graduationId) {
        this.graduationId = graduationId;
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
}
