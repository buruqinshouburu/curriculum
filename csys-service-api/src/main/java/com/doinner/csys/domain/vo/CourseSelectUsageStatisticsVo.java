package com.doinner.csys.domain.vo;

import java.util.List;

/**
 * 总库课程被培养方案选用次数统计
 */
public class CourseSelectUsageStatisticsVo {

    private Long courseId;
    private String courseName;
    private String courseModule;
    private Long selectedNum;
    private List<CourseSelectUsageDetailVo> details;

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

    public String getCourseModule() {
        return courseModule;
    }

    public void setCourseModule(String courseModule) {
        this.courseModule = courseModule;
    }

    public Long getSelectedNum() {
        return selectedNum;
    }

    public void setSelectedNum(Long selectedNum) {
        this.selectedNum = selectedNum;
    }

    public List<CourseSelectUsageDetailVo> getDetails() {
        return details;
    }

    public void setDetails(List<CourseSelectUsageDetailVo> details) {
        this.details = details;
    }
}
