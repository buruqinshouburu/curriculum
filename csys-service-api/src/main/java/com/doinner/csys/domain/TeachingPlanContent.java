package com.doinner.csys.domain;

import com.doinner.common.core.domain.db.AbstractDoinnerLogicalDelBaseEntity;

import java.math.BigDecimal;

/**
 * 教学计划内容与学时安排实体
 * 专题、模块、实验、项目、大作业等内容与学时安排
 */
public class TeachingPlanContent extends AbstractDoinnerLogicalDelBaseEntity {

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 教学计划ID
     */
    private Long planId;

    /**
     * 父级内容ID
     */
    private Long parentId;

    /**
     * 内容类型：1专题 2课程项目 3实验 4大作业 5训练模块 6模块内容
     */
    private Integer contentType;

    /**
     * 专题/模块/内容名称
     */
    private String title;

    /**
     * 内容说明
     */
    private String content;

    /**
     * 目的
     */
    private String purpose;

    /**
     * 学时
     */
    private BigDecimal hours;

    /**
     * 时间安排，如1天
     */
    private String timeArrange;

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

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public Integer getContentType() {
        return contentType;
    }

    public void setContentType(Integer contentType) {
        this.contentType = contentType;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getPurpose() {
        return purpose;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

    public BigDecimal getHours() {
        return hours;
    }

    public void setHours(BigDecimal hours) {
        this.hours = hours;
    }

    public String getTimeArrange() {
        return timeArrange;
    }

    public void setTimeArrange(String timeArrange) {
        this.timeArrange = timeArrange;
    }

    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }
}