package com.doinner.csys.domain.vo;

import java.util.ArrayList;
import java.util.List;

public class SourceDomainTreeVo {

    private Long id;

    private String name;


    private List<SourceDomainTreeVo> children = new ArrayList<>();


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

    public List<SourceDomainTreeVo> getChildren() {
        return children;
    }

    public void setChildren(List<SourceDomainTreeVo> children) {
        this.children = children;
    }

}
