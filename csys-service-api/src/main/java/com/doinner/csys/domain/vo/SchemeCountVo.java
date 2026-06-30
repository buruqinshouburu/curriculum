package com.doinner.csys.domain.vo;

public class SchemeCountVo {
    private Long categoryId;
    private String categoryName;
    private Long schemeNum;
    private Double proportion;

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public Long getSchemeNum() {
        return schemeNum;
    }

    public void setSchemeNum(Long schemeNum) {
        this.schemeNum = schemeNum;
    }

    public Double getProportion() {
        return proportion;
    }

    public void setProportion(Double proportion) {
        this.proportion = proportion;
    }
}
