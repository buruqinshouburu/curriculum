package com.doinner.csys.domain.vo;

import java.util.List;

public class KnowledgeTreeVo {

    private Long id;

    private String name;

    private Long parentId;

    private String collegeName;

    private List<KnowledgeTreeVo> children;

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

    public List<KnowledgeTreeVo> getChildren() {
        return children;
    }

    public void setChildren(List<KnowledgeTreeVo> children) {
        this.children = children;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public String getCollegeName() {
        return collegeName;
    }

    public void setCollegeName(String collegeName) {
        this.collegeName = collegeName;
    }
}
