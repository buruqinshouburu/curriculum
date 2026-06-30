package com.doinner.csys.domain.vo;

import com.doinner.common.core.annotation.Excel;
import com.doinner.csys.domain.TrainingSchemeCourseSchedule;

import java.util.List;

public class TrainingSchemeCourseScheduleVo extends TrainingSchemeCourseSchedule {

    /** 课程名称 */
    private String courseName;

    private String courseAttr;
    private String c_type;
    private String timeWeek;
    /** 时间单位(用于确定 timeWeek 的具体单位) */
    private String unit;
    /**
     * 预修课程(before_course_id)
     */
    private String beforeCourseId;

    /**
     * 后续课程(after_course_id)
     */
    private String afterCourseId;


    /** 课程顺序逻辑错误 */
    private List<Long> errorCourseIds;

    private Double teachHoursAll;

    /**
     * 实践课时
     */
    private Double practiceHoursAll;

    private Integer academicTermsNumber;

    private String semesterSchedule;

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getBeforeCourseId() {
        return beforeCourseId;
    }

    public void setBeforeCourseId(String beforeCourseId) {
        this.beforeCourseId = beforeCourseId;
    }

    public String getAfterCourseId() {
        return afterCourseId;
    }

    public void setAfterCourseId(String afterCourseId) {
        this.afterCourseId = afterCourseId;
    }

    public List<Long> getErrorCourseIds() {
        return errorCourseIds;
    }

    public void setErrorCourseIds(List<Long> errorCourseIds) {
        this.errorCourseIds = errorCourseIds;
    }

    public String getCourseAttr() {
        return courseAttr;
    }

    public void setCourseAttr(String courseAttr) {
        this.courseAttr = courseAttr;
    }

    public String getC_type() {
        return c_type;
    }

    public void setC_type(String c_type) {
        this.c_type = c_type;
    }

    public String getTimeWeek() {
        return timeWeek;
    }

    public void setTimeWeek(String timeWeek) {
        this.timeWeek = timeWeek;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public Double getTeachHoursAll() {
        return teachHoursAll;
    }

    public void setTeachHoursAll(Double teachHoursAll) {
        this.teachHoursAll = teachHoursAll;
    }

    public Double getPracticeHoursAll() {
        return practiceHoursAll;
    }

    public void setPracticeHoursAll(Double practiceHoursAll) {
        this.practiceHoursAll = practiceHoursAll;
    }

    public Integer getAcademicTermsNumber() {
        return academicTermsNumber;
    }

    public void setAcademicTermsNumber(Integer academicTermsNumber) {
        this.academicTermsNumber = academicTermsNumber;
    }

    public String getSemesterSchedule() {
        return semesterSchedule;
    }

    public void setSemesterSchedule(String semesterSchedule) {
        this.semesterSchedule = semesterSchedule;
    }
}
