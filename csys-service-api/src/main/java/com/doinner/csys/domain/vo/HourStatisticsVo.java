package com.doinner.csys.domain.vo;

/**
 * @author wzg
 * @date 2023/4/3 14:17
 */
public class HourStatisticsVo {

    private Long collegeId;
    private Long majorId;
    private String majorName;
    private Long courseCount;
    private Long hourCount;

    public Long getHours() {
        return hours;
    }

    public void setHours(Long hours) {
        this.hours = hours;
    }

    private Long courseId;
    private Long hours;

    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

    public Long getCollegeId() {
        return collegeId;
    }

    public void setCollegeId(Long collegeId) {
        this.collegeId = collegeId;
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

    public Long getCourseCount() {
        return courseCount;
    }

    public void setCourseCount(Long courseCount) {
        this.courseCount = courseCount;
    }

    public Long getHourCount() {
        return hourCount;
    }

    public void setHourCount(Long hourCount) {
        this.hourCount = hourCount;
    }
}
