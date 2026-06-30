package com.doinner.csys.domain.vo;

import java.util.List;
import java.util.Map;

public class StandardCultivationImportVo {

    private Long id;

    private String name;

    private Integer leaf;

    private Map<String, List<StandardCultivationImportVo>> children;

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

    public Integer getLeaf() {
        return leaf;
    }

    public void setLeaf(Integer leaf) {
        this.leaf = leaf;
    }

    public Map<String, List<StandardCultivationImportVo>> getChildren() {
        return children;
    }

    public void setChildren(Map<String, List<StandardCultivationImportVo>> children) {
        this.children = children;
    }
}
