package com.doinner.csys.domain.vo;

import com.doinner.common.core.annotation.Excel;
import com.doinner.csys.domain.CourseSchedule;
import com.doinner.csys.domain.TrainingSchemeCourseSchedule;

import java.util.List;

public class TrainingSchemeCourseScheduleVo extends TrainingSchemeCourseSchedule {

    /** 课程名称 */
    private String courseName;

    private String courseAttr;
    private String c_type;
    /** 课程顶层原始时间小计，混合单位时不能据此换算学时。 */
    private String timeWeek;
    /** 课程首条安排的单位；各组实际单位见 courseScheduleList。 */
    private String unit;
    /** 当前调用课程 courseId 的安排，按 id 升序；不读取 sourceId 对应的源课安排。 */
    private List<CourseSchedule> courseScheduleList;
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

    public List<CourseSchedule> getCourseScheduleList() {
        return courseScheduleList;
    }

    public void setCourseScheduleList(List<CourseSchedule> courseScheduleList) {
        this.courseScheduleList = courseScheduleList;
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
