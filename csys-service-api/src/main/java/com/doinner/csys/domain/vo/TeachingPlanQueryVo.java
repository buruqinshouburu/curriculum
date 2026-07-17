package com.doinner.csys.domain.vo;

/**
 * 课程教学计划管理列表查询入参。
 *
 * 以总库课程为主表(t_csys_course, source_id is null)，left join 教学计划表
 * (t_csys_teaching_plan)，按下列条件过滤：
 * 课程名称(模糊)、开课单位、课程编号(模糊)、适用对象、课程模块、修读要求。
 */
public class TeachingPlanQueryVo {

    /** 课程名称(模糊查询) -> t_csys_course.name */
    private String courseName;

    /** 开课单位 -> t_csys_course.teach_college_id */
    private Long teachCollegeId;

    /** 课程编号(模糊查询) -> t_csys_course.code */
    private String courseCode;

    /** 适用对象 -> t_csys_course.education_level */
    private String educationLevel;

    /** 课程模块 -> t_csys_course.course_Module */
    private String courseModule;

    /** 修读要求 -> t_csys_course.course_attr */
    private String courseAttr;

    /** 课程类型 -> t_csys_course.type (1课程 2实践训练课目 3实验课程 4实践项目) */
    private String type;

    /** 课程版本 -> t_csys_course.version */
    private String version;

    /**
     * 是否已被培养方案调用: 1=只查已被调用的原课程(存在 source_id 指向该课程的调用课程实例),
     * 0/空=查全部原课程。该接口仅查询原课程(source_id 为空)。
     */
    private Integer quoted;

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public Long getTeachCollegeId() {
        return teachCollegeId;
    }

    public void setTeachCollegeId(Long teachCollegeId) {
        this.teachCollegeId = teachCollegeId;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode;
    }

    public String getEducationLevel() {
        return educationLevel;
    }

    public void setEducationLevel(String educationLevel) {
        this.educationLevel = educationLevel;
    }

    public String getCourseModule() {
        return courseModule;
    }

    public void setCourseModule(String courseModule) {
        this.courseModule = courseModule;
    }

    public String getCourseAttr() {
        return courseAttr;
    }

    public void setCourseAttr(String courseAttr) {
        this.courseAttr = courseAttr;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public Integer getQuoted() {
        return quoted;
    }

    public void setQuoted(Integer quoted) {
        this.quoted = quoted;
    }
}
