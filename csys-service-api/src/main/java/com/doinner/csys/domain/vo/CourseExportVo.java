package com.doinner.csys.domain.vo;

import com.doinner.csys.domain.Course;
import com.doinner.common.core.annotation.Excel;
import org.apache.commons.lang3.ObjectUtils;

public class CourseExportVo {

    /**
     * 课程名(name)
     */
    @Excel(name = "课程名")
    private String name;

    /**
     * 课程编号(code)
     */
    @Excel(name = "课程编号")
    private String code;

    /**
     * 课程类型(type)
     */
    @Excel(name = "课程类型")
    private String type;

    /**
     * 执笔人id(authors)
     */
    @Excel(name = "执笔人")
    private String authors;

    /**
     * 所属学院(college_id)
     */
    //@Excel(name = "所属学院")
    private String college;

    /**
     * 施教学院(teach_college_id)
     */
    @Excel(name = "施教学院")
    private String teachCollege;

    /**
     * 英文名称(en_name)
     */
    @Excel(name = "英文名称")
    private String enName;

    /**
     * 预修课程(before_course_id)
     */
    @Excel(name = "预修课程")
    private String beforeCourseNames;

    /**
     * 后续课程(after_course_id)
     */
    @Excel(name = "后续课程")
    private String afterCourseNames;

    /**
     * 学时安排(hours)
     */
    @Excel(name = "学时安排")
    private String hours;

    /**
     * 理论学时(theory_hours)
     */
    @Excel(name = "理论学时")
    private String theoryHours;

    /**
     * 实践学时(practice_hours)
     */
    @Excel(name = "实践学时")
    private String practiceHours;

    /**
     * 周学时(week_hours)
     */
    @Excel(name = "周学时")
    private String weekHours;

    /**
     * 讲授学时(teach_hours)
     */
    @Excel(name = "讲授学时")
    private String teachHours;

    /**
     * 考核学时(examine_hours)
     */
    @Excel(name = "考核学时")
    private String examineHours;

    /**
     * 其他学时(other_hours)
     */
    @Excel(name = "其他学时")
    private String otherHours;

    /**
     * 学时单位(hours_unit)
     */
    @Excel(name = "学时单位")
    private String hoursUnit;

    /**
     * 学分(credit)
     */
    @Excel(name = "学分")
    private String credit;

    /**
     * 课程性质(curr_prop)
     */
    @Excel(name = "课程性质")
    private String courseProp;

    /**
     * 课程大类(curr_type)
     */
    @Excel(name = "课程大类")
    private String courseType;

    /**
     * 课程属性(curr_attr)
     */
    @Excel(name = "课程属性")
    private String courseAttr;

    /**
     * 实施地点(location)
     */
    @Excel(name = "实施地点")
    private String location;

    /**
     * 开课学期(open_term)
     */
    @Excel(name = "开课学期")
    private String openTerm;

    /**
     * 内容简介(summary)
     */
    @Excel(name = "内容简介")
    private String summary;

//    /**
//     * 文件id(file_id)
//     */
//    @Excel(name = "文件id")
//    private String fileId;

    /**
     * 文件名称(file_name)
     */
    @Excel(name = "文件名称")
    private String fileName;

    /**
     * 审核状态： 0未审核1审核中2审核通过3审核失败
     */
    @Excel(name = "审核状态", readConverterExp = "0=未审核,1=审核中,2=触发一次执行,3=审核失败")
    private Integer status;

    public CourseExportVo() {
    }

    public String getCourseProp() {
        return courseProp;
    }

    public void setCourseProp(String courseProp) {
        this.courseProp = courseProp;
    }

    public String getCourseType() {
        return courseType;
    }

    public void setCourseType(String courseType) {
        this.courseType = courseType;
    }

    public String getCourseAttr() {
        return courseAttr;
    }

    public void setCourseAttr(String courseAttr) {
        this.courseAttr = courseAttr;
    }

    public CourseExportVo(Course course) {
        this.setName(course.getName());
        this.setCode(course.getCode());
        if(ObjectUtils.isNotEmpty(course.getCredit())) {
            if(course.getCredit() == course.getCredit().intValue()){
                this.setCredit(String.valueOf(course.getCredit().intValue()));
            }else {
                this.setCredit(course.getCredit().toString());
            }
        }
        if(ObjectUtils.isNotEmpty(course.getCourseAttr())) {
            this.setCourseAttr(course.getCourseAttr().toString());
        }
        if(ObjectUtils.isNotEmpty(course.getCourseProp())) {
            this.setCourseProp(course.getCourseProp().toString());
        }
        if(ObjectUtils.isNotEmpty(course.getCourseType())) {
            this.setCourseType(course.getCourseType().toString());
        }
        this.setAuthors(course.getAuthors());
        this.setEnName(course.getEnName());
        this.setFileName(course.getFileName());
        if(ObjectUtils.isNotEmpty(course.getHours())) {
            if(course.getHours() == course.getHours().intValue()){
                this.setHours(String.valueOf(course.getHours().intValue()));
            }else {
                this.setHours(course.getHours().toString());
            }
        }
        this.setHoursUnit(course.getHoursUnit());
        if(ObjectUtils.isNotEmpty(course.getExamineHours())) {
            if(course.getExamineHours() == course.getExamineHours().intValue()){
                this.setExamineHours(String.valueOf(course.getExamineHours().intValue()));
            }else {
                this.setExamineHours(course.getExamineHours().toString());
            }
        }
        if(ObjectUtils.isNotEmpty(course.getOtherHours())) {
            if(course.getOtherHours() == course.getOtherHours().intValue()){
                this.setOtherHours(String.valueOf(course.getOtherHours().intValue()));
            }else {
                this.setOtherHours(course.getOtherHours().toString());
            }
        }
        if(ObjectUtils.isNotEmpty(course.getTeachHours())) {
            if(course.getTeachHours() == course.getTeachHours().intValue()){
                this.setTeachHours(String.valueOf(course.getTeachHours().intValue()));
            }else {
                this.setTeachHours(course.getTeachHours().toString());
            }
        }
        if(ObjectUtils.isNotEmpty(course.getTheoryHours())) {
            if(course.getTheoryHours() == course.getTheoryHours().intValue()){
                this.setTheoryHours(String.valueOf(course.getTheoryHours().intValue()));
            }else {
                this.setTheoryHours(course.getTheoryHours().toString());
            }
        }
        if(ObjectUtils.isNotEmpty(course.getPracticeHours())) {
            if(course.getPracticeHours() == course.getPracticeHours().intValue()){
                this.setPracticeHours(String.valueOf(course.getPracticeHours().intValue()));
            }else {
                this.setPracticeHours(course.getPracticeHours().toString());
            }
        }
        if(ObjectUtils.isNotEmpty(course.getWeekHours())) {
            if(course.getWeekHours() == course.getWeekHours().intValue()){
                this.setWeekHours(String.valueOf(course.getWeekHours().intValue()));
            }else {
                this.setWeekHours(course.getWeekHours().toString());
            }
        }
        this.setBeforeCourseNames(course.getBeforeCourseId());
        this.setAfterCourseNames(course.getAfterCourseId());
        this.setLocation(course.getLocation());
        this.setOpenTerm(course.getOpenTerm());
        this.setSummary(course.getSummary());
        this.status = course.getStatus();
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getAuthors() {
        return authors;
    }

    public void setAuthors(String authors) {
        this.authors = authors;
    }

    public String getCollege() {
        return college;
    }

    public void setCollege(String college) {
        this.college = college;
    }

    public String getTeachCollege() {
        return teachCollege;
    }

    public void setTeachCollege(String teachCollege) {
        this.teachCollege = teachCollege;
    }

    public String getEnName() {
        return enName;
    }

    public void setEnName(String enName) {
        this.enName = enName;
    }

    public String getBeforeCourseNames() {
        return beforeCourseNames;
    }

    public void setBeforeCourseNames(String beforeCourseNames) {
        this.beforeCourseNames = beforeCourseNames;
    }

    public String getAfterCourseNames() {
        return afterCourseNames;
    }

    public void setAfterCourseNames(String afterCourseNames) {
        this.afterCourseNames = afterCourseNames;
    }

    public String getHours() {
        return hours;
    }

    public void setHours(String hours) {
        this.hours = hours;
    }

    public String getTheoryHours() {
        return theoryHours;
    }

    public void setTheoryHours(String theoryHours) {
        this.theoryHours = theoryHours;
    }

    public String getPracticeHours() {
        return practiceHours;
    }

    public void setPracticeHours(String practiceHours) {
        this.practiceHours = practiceHours;
    }

    public String getWeekHours() {
        return weekHours;
    }

    public void setWeekHours(String weekHours) {
        this.weekHours = weekHours;
    }

    public String getTeachHours() {
        return teachHours;
    }

    public void setTeachHours(String teachHours) {
        this.teachHours = teachHours;
    }

    public String getExamineHours() {
        return examineHours;
    }

    public void setExamineHours(String examineHours) {
        this.examineHours = examineHours;
    }

    public String getOtherHours() {
        return otherHours;
    }

    public void setOtherHours(String otherHours) {
        this.otherHours = otherHours;
    }

    public String getHoursUnit() {
        return hoursUnit;
    }

    public void setHoursUnit(String hoursUnit) {
        this.hoursUnit = hoursUnit;
    }

    public String getCredit() {
        return credit;
    }

    public void setCredit(String credit) {
        this.credit = credit;
    }


    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getOpenTerm() {
        return openTerm;
    }

    public void setOpenTerm(String openTerm) {
        this.openTerm = openTerm;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
