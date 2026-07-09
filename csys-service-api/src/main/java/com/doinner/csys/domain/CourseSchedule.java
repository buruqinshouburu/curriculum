package com.doinner.csys.domain;

public class CourseSchedule {

    private Long id;

    private Long courseId;

    private String semesterSchedule;

    private String springAutumn;

    private Double teachHours;

    private Double practiceHours;

    private Double timeWeek;

    private String unit;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

    public String getSemesterSchedule() {
        return semesterSchedule;
    }

    public void setSemesterSchedule(String semesterSchedule) {
        this.semesterSchedule = semesterSchedule;
    }

    public String getSpringAutumn() {
        return springAutumn;
    }

    public void setSpringAutumn(String springAutumn) {
        this.springAutumn = springAutumn;
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

    public Double getTimeWeek() {
        return timeWeek;
    }

    public void setTimeWeek(Double timeWeek) {
        this.timeWeek = timeWeek;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }
}
