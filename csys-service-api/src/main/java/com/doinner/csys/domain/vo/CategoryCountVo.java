package com.doinner.csys.domain.vo;

import java.util.List;

public class CategoryCountVo {
    private Long systemId;
    private String systemName;
    private Long categoryCount;
    private Long schemeCount;
    private List<CategoryVo> categoryList;

    public Long getSystemId() {
        return systemId;
    }

    public void setSystemId(Long systemId) {
        this.systemId = systemId;
    }

    public String getSystemName() {
        return systemName;
    }

    public void setSystemName(String systemName) {
        this.systemName = systemName;
    }

    public Long getCategoryCount() {
        return categoryCount;
    }

    public void setCategoryCount(Long categoryCount) {
        this.categoryCount = categoryCount;
    }

    public Long getSchemeCount() {
        return schemeCount;
    }

    public void setSchemeCount(Long schemeCount) {
        this.schemeCount = schemeCount;
    }

    public List<CategoryVo> getCategoryList() {
        return categoryList;
    }

    public void setCategoryList(List<CategoryVo> categoryList) {
        this.categoryList = categoryList;
    }
}
