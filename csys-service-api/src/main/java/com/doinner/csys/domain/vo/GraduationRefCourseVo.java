package com.doinner.csys.domain.vo;

/**
 * 毕业要求(叶子)绑定课程的查询结果。
 *
 * 对应 CourseRefGraduationMapper.selectCourseRefGraduationWithCourseByGraduationIds,
 * 用于构建"毕业要求与课程支撑矩阵"。
 */
public class GraduationRefCourseVo {

    /** 毕业要求(叶子)id */
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
