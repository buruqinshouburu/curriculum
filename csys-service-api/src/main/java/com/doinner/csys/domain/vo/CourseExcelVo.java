package com.doinner.csys.domain.vo;

import com.doinner.common.core.annotation.Excel;

public class CourseExcelVo {

    /**
     * 课程名(name)
     */
    @Excel(name = "*课程名称")
    private String name;

    /**
     *  培养层次
     */
    @Excel(name = "*适用对象", readConverterExp = "1=生长军官学员,2=地方本科学员,3=军士职业技术教育学员,4=生长军官学员及地方本科学员,5=生长军官学员及军士职业技术教育学员,6=地方本科学员及军士职业技术教育学员,7=英烈子女班,8=生长军官学员及英烈子女班")
    private String educationLevel;

    /**
     * 学院名称
     */
    @Excel(name = "*开课单位")
    private String collegeName;

    private Long collegeId;

    /**
     *  cur_course_type
     */
    //@Excel(name = "*课程类型", readConverterExp = "1=课程,3=课程(含实践环节)")
    //private String type;

    /**
    * 课程属性 cur_course_attribute
    */
    @Excel(name = "*修读要求",readConverterExp = "1=必修,2=限选,3=任选")
    private String courseAttr;

    /**
     * 课程模块 kg多级字典 有两个字段组成用 - 拼接的 需要转
     */
    @Excel(name = "*课程模块")
    private String courseModuleAndChildrenName;

    // 课程模块的两个字段，kg字段 存id
    private String courseModule;
    private String courseModuleChildren;

    @Excel(name = "*开课年份")
    private String openYear;

    /**
     *  字典 cur_open_semester
     */
//    @Excel(name = "*开课学期",readConverterExp = "2=春一,4=春二,6=春三,8=春四,1=秋一,3=秋二,5=秋三,7=秋四")
//    private String openTerm;


    /**
     * 字典 cur_exam_format
     */
    @Excel(name = "*考核方式",readConverterExp = "1=考查,2=考试,3=其他")
    private String exaMethod;

    @Excel(name = "*讲授学时")
    private Double teachHours;

    @Excel(name = "*实践学时")
    private Double practiceHours;

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

    @Excel(name = "课程负责人")
    private String authors;

    @Excel(name = "最高承载班次")
    private Integer maxQuoteCount;



    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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



    public String getCourseModuleAndChildrenName() {
        return courseModuleAndChildrenName;
    }

    public void setCourseModuleAndChildrenName(String courseModuleAndChildrenName) {
        this.courseModuleAndChildrenName = courseModuleAndChildrenName;
    }

    public String getCourseModule() {
        return courseModule;
    }

    public void setCourseModule(String courseModule) {
        this.courseModule = courseModule;
    }

    public String getCourseModuleChildren() {
        return courseModuleChildren;
    }

    public void setCourseModuleChildren(String courseModuleChildren) {
        this.courseModuleChildren = courseModuleChildren;
    }

    public String getOpenYear() {
        return openYear;
    }

    public void setOpenYear(String openYear) {
        this.openYear = openYear;
    }

    public String getExaMethod() {
        return exaMethod;
    }

    public void setExaMethod(String exaMethod) {
        this.exaMethod = exaMethod;
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

    public String getAuthors() {
        return authors;
    }

    public void setAuthors(String authors) {
        this.authors = authors;
    }

    public String getCourseAttr() {
        return courseAttr;
    }

    public void setCourseAttr(String courseAttr) {
        this.courseAttr = courseAttr;
    }

    public Integer getMaxQuoteCount() {
        return maxQuoteCount;
    }

    public void setMaxQuoteCount(Integer maxQuoteCount) {
        this.maxQuoteCount = maxQuoteCount;
    }

    public String getEducationLevel() {
        return educationLevel;
    }

    public void setEducationLevel(String educationLevel) {
        this.educationLevel = educationLevel;
    }
}
