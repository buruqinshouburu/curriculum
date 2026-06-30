package com.doinner.csys.domain.vo;

import com.doinner.common.core.annotation.Excel;

public class PracticeExcelVo {

    /**
     * 课程名(name)
     */
    @Excel(name = "*项目名称")
    private String name;

    /**
     *  培养层次
     */
    @Excel(name = "*适用对象", readConverterExp = "1=生长军官学员,2=地方本科学员,3=军士职业技术教育学员,4=生长军官学员及地方本科学员,5=生长军官学员及军士职业技术教育学员,6=地方本科学员及军士职业技术教育学员,7=英烈子女班,8=生长军官学员及英烈子女班")
    private String educationLevel;

    /**
     * 课程属性 cur_course_attribute
     */
    @Excel(name = "*修读要求",readConverterExp = "1=必修,2=限选,3=任选")
    private String courseAttr;

    /**
     *  字典 cur_program_level
     */
    @Excel(name = "*项目层级",readConverterExp = "1=基础,2=进阶,3=综合")
    private String programLevel;

    /**
     * 学院名称
     */
    @Excel(name = "*组织实施单位")
    private String collegeName;

    private Long collegeId;

    @Excel(name = "*时间安排")
    private Double timeWeek;

    /**
     * 时间单位(用于确定 timeWeek 的具体单位)
     */
    @Excel(name = "*时间安排单位",readConverterExp = "1=周,2=学时")
    private String unit;

    /**
     *  由两个字段组成   semesterSchedule  springAutumn
     */
    //@Excel(name = "*学期安排")
    //private String semesterScheduleAndSpringAutumnName;

    // 第一、二、三、四 学年 字典 根据名称查询然后把code 存数据库 cur_semester_arrange
    @Excel(name = "*学年安排",readConverterExp = "1=第一学年,2=第二学年,3=第三学年,4=第四学年,5=第五学年,6=贯穿四年,7=多学期安排")
    private String semesterSchedule;

    // 春 秋  字典 cur_semester_arrange_season
    @Excel(name = "*学期安排",readConverterExp = "1=秋,2=春,3=暑假,4=寒假,5=春秋学期同时开设")
    private String springAutumn;

    @Excel(name = "备注")
    private String remark;

    @Excel(name = "*是否提交大作业",readConverterExp = "0=否,1=是")
    private Integer hasWork;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProgramLevel() {
        return programLevel;
    }

    public void setProgramLevel(String programLevel) {
        this.programLevel = programLevel;
    }

    public String getCollegeName() {
        return collegeName;
    }

    public void setCollegeName(String collegeName) {
        this.collegeName = collegeName;
    }

    public Long getCollegeId() {
        return collegeId;
    }

    public void setCollegeId(Long collegeId) {
        this.collegeId = collegeId;
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

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getEducationLevel() {
        return educationLevel;
    }

    public void setEducationLevel(String educationLevel) {
        this.educationLevel = educationLevel;
    }

    public String getCourseAttr() {
        return courseAttr;
    }

    public void setCourseAttr(String courseAttr) {
        this.courseAttr = courseAttr;
    }

    public Integer getHasWork() {
        return hasWork;
    }

    public void setHasWork(Integer hasWork) {
        this.hasWork = hasWork;
    }
}
