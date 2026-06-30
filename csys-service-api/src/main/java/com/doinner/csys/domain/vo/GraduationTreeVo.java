package com.doinner.csys.domain.vo;

import java.util.ArrayList;
import java.util.List;

public class GraduationTreeVo {

    private Long id;

    private Long parentId;

    private String name;

    private Long collegeId;

    private Long categoryId;

    private Long majorId;

    private List<GraduationTreeVo> children = new ArrayList<GraduationTreeVo>();

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

    public List<GraduationTreeVo> getChildren() {
        return children;
    }

    public void setChildren(List<GraduationTreeVo> children) {
        this.children = children;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public Long getCollegeId() {
        return collegeId;
    }

    public void setCollegeId(Long collegeId) {
        this.collegeId = collegeId;
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
}
