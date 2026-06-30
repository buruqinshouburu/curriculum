package com.doinner.csys.domain.vo;

import java.util.ArrayList;
import java.util.List;

public class SourceKnowledgeVo {

    private Long id;

    private String name;

    private Long collegeId;

    private Long categoryId;

    private Long majorId;

    private List<SourceKnowledgeVo> children = new ArrayList<>();


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

    public List<SourceKnowledgeVo> getChildren() {
        return children;
    }

    public void setChildren(List<SourceKnowledgeVo> children) {
        this.children = children;
    }

    public Long getCollegeId() {
        return collegeId;
    }

    public void setCollegeId(Long collegeId) {
        this.collegeId = collegeId;
    }

    public Long getMajorId() {
        return majorId;
    }

    public void setMajorId(Long majorId) {
        this.majorId = majorId;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }
}
