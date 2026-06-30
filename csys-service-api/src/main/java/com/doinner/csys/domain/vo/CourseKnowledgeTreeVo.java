package com.doinner.csys.domain.vo;

import java.util.List;

public class CourseKnowledgeTreeVo {

    private Long id;

    private String name;

    private Long parentId;

    private Long courseId;

    private List<CourseKnowledgeTreeVo> children;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<CourseKnowledgeTreeVo> getChildren() {
        return children;
    }

    public void setChildren(List<CourseKnowledgeTreeVo> children) {
        this.children = children;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }
}
