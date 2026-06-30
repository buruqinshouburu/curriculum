package com.doinner.csys.domain.vo;

public class CourseKnowledgeViewVo {
    private String courseModelName;
    private String courseModelId;
    private String courseName;
    private String courseId;
    private Integer unitCount;
    private Integer pointCount;

    public String getCourseModelName() {
        return courseModelName;
    }

    public void setCourseModelName(String courseModelName) {
        this.courseModelName = courseModelName;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public Integer getUnitCount() {
        return unitCount;
    }

    public void setUnitCount(Integer unitCount) {
        this.unitCount = unitCount;
    }

    public Integer getPointCount() {
        return pointCount;
    }

    public void setPointCount(Integer pointCount) {
        this.pointCount = pointCount;
    }

    public String getCourseModelId() {
        return courseModelId;
    }

    public void setCourseModelId(String courseModelId) {
        this.courseModelId = courseModelId;
    }

    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }
}
