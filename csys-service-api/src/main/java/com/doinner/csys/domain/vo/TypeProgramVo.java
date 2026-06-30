package com.doinner.csys.domain.vo;

/**
 * @author wzg
 * @date 2023/3/24 10:10
 */
public class TypeProgramVo {

    private Long categoryId;
    private Long categoryCount;
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public Long getCategoryCount() {
        return categoryCount;
    }

    public void setCategoryCount(Long categoryCount) {
        this.categoryCount = categoryCount;
    }
}
