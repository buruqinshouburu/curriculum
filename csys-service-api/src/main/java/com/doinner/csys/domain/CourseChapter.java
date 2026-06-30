package com.doinner.csys.domain;

import com.doinner.common.core.domain.db.AbstractDoinnerLogicalDelBaseEntity;
import com.doinner.common.core.annotation.Excel;

/**
 * 课程章节对象 t_csys_course_chapter
 *
 * @author doinner
 * @date 2023-03-14
 */
public class CourseChapter extends AbstractDoinnerLogicalDelBaseEntity {

    private static final long serialVersionUID = 2311010500458579020L;

    /**
     * 课程id(course_id)
     */
    @Excel(name = "课程id(course_id)")
    private Long courseId;

    /**
     * 名称(name)
     */
    @Excel(name = "名称(name)")
    private String name;

    /**
     * 内容(content)
     */
    @Excel(name = "内容(content)")
    private String content;

    /**
     * 学时(hours)
     */
    @Excel(name = "学时(hours)")
    private Double hours;

    /**
     * 类型(type)
     */
    @Excel(name = "类型(type)")
    private Integer type;


    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

    public Long getCourseId() {
        return courseId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public void setHours(Double hours) {
        this.hours = hours;
    }

    public Double getHours() {
        return hours;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getType() {
        return type;
    }


}
