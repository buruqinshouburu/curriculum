package com.doinner.csys.domain.vo;

import java.util.ArrayList;
import java.util.List;

public class StandardTreeVo {

    private Long id;

    private String name;

    private Long parentId;

    private Long collegeId;

    private Long categoryId;

    private Long majorId;

    private List<StandardTreeVo> children = new ArrayList<StandardTreeVo>();


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

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public List<StandardTreeVo> getChildren() {
        return children;
    }

    public void setChildren(List<StandardTreeVo> children) {
        this.children = children;
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
