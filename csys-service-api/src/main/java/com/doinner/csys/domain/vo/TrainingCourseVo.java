package com.doinner.csys.domain.vo;

import java.util.HashMap;
import java.util.Map;

public class TrainingCourseVo {
    private Long  schemeId;
    private String  name;
    private String code ;
    private String  type;
    private Long  categoryId;
    private Long  majorId;
    private Long  collegeId;
    private String  courseModule;
    private String  courseModuleChildren;
    private String  courseAttr;
    private Double  hours;
    private Double  credit;
    private Long  subMajorId;

    /** 排序字段: 前端属性名 -> 数据库列名(列名与 selectCoursesByIdsWithSort 别名一致) */
    private static Map<String, String> propMapping = new HashMap<String, String>();
    static {
        propMapping.put("name", "c.name");                 // 课程名称
        propMapping.put("courseModule", "c.course_Module"); // 课程模块
        propMapping.put("courseAttr", "c.course_attr");     // 修读要求
        propMapping.put("teachCollegeId", "c.teach_college_id"); // 开课单位
        propMapping.put("collegeId", "c.college_id"); // 开课单位
    }

    /** 排序字段(前端属性名) */
    private String prop;
    /** 排序字段对应数据库列名 */
    private String database_prop;
    /** 排序方式 */
    private String order;

    public Long getSchemeId() {
        return schemeId;
    }

    public void setSchemeId(Long schemeId) {
        this.schemeId = schemeId;
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

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public Long getMajorId() {
        return majorId;
    }

    public void setMajorId(Long majorId) {
        this.majorId = majorId;
    }

    public Long getCollegeId() {
        return collegeId;
    }

    public void setCollegeId(Long collegeId) {
        this.collegeId = collegeId;
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

    public Double getHours() {
        return hours;
    }

    public void setHours(Double hours) {
        this.hours = hours;
    }

    public Double getCredit() {
        return credit;
    }

    public void setCredit(Double credit) {
        this.credit = credit;
    }

    public String getProp() {
        return prop;
    }

    public void setProp(String prop) {
        this.prop = prop;
        this.database_prop = propMapping.get(prop);
    }

    public String getDatabase_prop() {
        return database_prop;
    }

    public void setDatabase_prop(String database_prop) {
        this.database_prop = database_prop;
    }

    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
    }

    public Long getSubMajorId() {
        return subMajorId;
    }

    public void setSubMajorId(Long subMajorId) {
        this.subMajorId = subMajorId;
    }

    public String getCourseModuleChildren() {
        return courseModuleChildren;
    }

    public void setCourseModuleChildren(String courseModuleChildren) {
        this.courseModuleChildren = courseModuleChildren;
    }
}
