package com.doinner.csys.domain.vo;

/**
 * 课程教学计划列表：当前页课程被引用侧聚合字段。
 * <p>
 * 由 source_id 定位被培养方案引用的课程实例 c2，再关联排课/培养方案后按源课程聚合。
 * 用于替代列表 SQL 中对每一行执行的相关子查询。
 */
public class TeachingPlanQuoteAggVo {

    /** 总库课程id（= c2.source_id） */
    private Long courseId;

    /** 适用对象：被引用培养方案 education_level 去重拼接 */
    private String educationLevel;

    /** 适用专业：被引用培养方案 major_id 对应专业名去重拼接 */
    private String majorName;

    /** 修读性质：被引用课程 course_attr 去重拼接 */
    private String courseAttr;

    /** 课程模块：被引用课程 course_Module 去重拼接 */
    private String courseModule;

    /** 课目模块：被引用课程 location 去重拼接（type2 实践训练课目用，判定是否通识通用） */
    private String location;

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

    public String getEducationLevel() {
        return educationLevel;
    }

    public void setEducationLevel(String educationLevel) {
        this.educationLevel = educationLevel;
    }

    public String getMajorName() {
        return majorName;
    }

    public void setMajorName(String majorName) {
        this.majorName = majorName;
    }

    public String getCourseAttr() {
        return courseAttr;
    }

    public void setCourseAttr(String courseAttr) {
        this.courseAttr = courseAttr;
    }

    public String getCourseModule() {
        return courseModule;
    }

    public void setCourseModule(String courseModule) {
        this.courseModule = courseModule;
    }
}
