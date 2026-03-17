package com.example.cscy.entity.scheme;

public class TrainingSchemeCourseVo {
    private String name;
    private Double hours;
    private Double theoryHours;
    private Double practiceHours;
    private Double courseTypeId;
    private String courseTypeName;
    //修复要求
    private String courseAttrName;
    //开课学期
    private String openTerm;
    //第一、二、三、四学年
    private String semesterSchedule;
    //春秋
    private String springAutumn;
    //课程模块  政治理论、军事基础、基础科学
    private String courseModeChildrenName;
    //通识课、专业课、实践训练科目
    private String courseModelName;

    private String majorName;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getHours() {
        return hours;
    }

    public void setHours(Double hours) {
        this.hours = hours;
    }

    public Double getTheoryHours() {
        return theoryHours;
    }

    public void setTheoryHours(Double theoryHours) {
        this.theoryHours = theoryHours;
    }

    public Double getPracticeHours() {
        return practiceHours;
    }

    public void setPracticeHours(Double practiceHours) {
        this.practiceHours = practiceHours;
    }

    public Double getCourseTypeId() {
        return courseTypeId;
    }

    public void setCourseTypeId(Double courseTypeId) {
        this.courseTypeId = courseTypeId;
    }

    public String getCourseTypeName() {
        return courseTypeName;
    }

    public void setCourseTypeName(String courseTypeName) {
        this.courseTypeName = courseTypeName;
    }

    public String getCourseAttrName() {
        return courseAttrName;
    }

    public void setCourseAttrName(String courseAttrName) {
        this.courseAttrName = courseAttrName;
    }

    public String getOpenTerm() {
        return openTerm;
    }

    public void setOpenTerm(String openTerm) {
        this.openTerm = openTerm;
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

    public String getCourseModelName() {
        return courseModelName;
    }

    public void setCourseModelName(String courseModelName) {
        this.courseModelName = courseModelName;
    }

    public String getCourseModeChildrenName() {
        return courseModeChildrenName;
    }

    public void setCourseModeChildrenName(String courseModeChildrenName) {
        this.courseModeChildrenName = courseModeChildrenName;
    }

    public String getMajorName() {
        return majorName;
    }

    public void setMajorName(String majorName) {
        this.majorName = majorName;
    }
}
