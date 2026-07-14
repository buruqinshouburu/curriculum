package com.doinner.csys.domain;

import com.doinner.common.core.domain.db.AbstractDoinnerLogicalDelBaseEntity;

/**
 * 教学计划教材实体
 * 教材、实验教材、实验指导书
 */
public class TeachingPlanTextbook extends AbstractDoinnerLogicalDelBaseEntity {

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 教学计划ID
     */
    private Long planId;

    /**
     * 教材性质
     */
    private String materialNature;

    /**
     * 教材名称
     */
    private String name;

    /**
     * 第一作者
     */
    private String firstAuthor;

    /**
     * 版次
     */
    private String edition;

    /**
     * 出版或颁发单位
     */
    private String publisher;

    /**
     * 出版或颁发时间
     */
    private String publishTime;

    /**
     * ISBN号或统一书号/文件号
     */
    private String isbn;

    /**
     * 出版方式
     */
    private String publishMethod;

    /**
     * 排序
     */
    private Integer sort;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPlanId() {
        return planId;
    }

    public void setPlanId(Long planId) {
        this.planId = planId;
    }

    public String getMaterialNature() {
        return materialNature;
    }

    public void setMaterialNature(String materialNature) {
        this.materialNature = materialNature;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFirstAuthor() {
        return firstAuthor;
    }

    public void setFirstAuthor(String firstAuthor) {
        this.firstAuthor = firstAuthor;
    }

    public String getEdition() {
        return edition;
    }

    public void setEdition(String edition) {
        this.edition = edition;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getPublishTime() {
        return publishTime;
    }

    public void setPublishTime(String publishTime) {
        this.publishTime = publishTime;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getPublishMethod() {
        return publishMethod;
    }

    public void setPublishMethod(String publishMethod) {
        this.publishMethod = publishMethod;
    }

    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }
}