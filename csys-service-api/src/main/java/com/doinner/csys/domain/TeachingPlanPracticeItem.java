package com.doinner.csys.domain;

import com.doinner.common.core.domain.db.AbstractDoinnerLogicalDelBaseEntity;

import java.math.BigDecimal;

/**
 * 教学计划实验/实践项目实体
 * 实验项目、实践项目、设计实验、验证实验
 */
public class TeachingPlanPracticeItem extends AbstractDoinnerLogicalDelBaseEntity {

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 教学计划ID
     */
    private Long planId;

    /**
     * 项目类型：1实验 2实践项目 3设计实验 4验证实验
     */
    private Integer itemType;

    /**
     * 项目名称
     */
    private String name;

    /**
     * 学时
     */
    private BigDecimal hours;

    /**
     * 分组情况
     */
    private String groupInfo;

    /**
     * 实验性质
     */
    private String experimentNature;

    /**
     * 修读性质
     */
    private String studyNature;

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

    public Integer getItemType() {
        return itemType;
    }

    public void setItemType(Integer itemType) {
        this.itemType = itemType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getHours() {
        return hours;
    }

    public void setHours(BigDecimal hours) {
        this.hours = hours;
    }

    public String getGroupInfo() {
        return groupInfo;
    }

    public void setGroupInfo(String groupInfo) {
        this.groupInfo = groupInfo;
    }

    public String getExperimentNature() {
        return experimentNature;
    }

    public void setExperimentNature(String experimentNature) {
        this.experimentNature = experimentNature;
    }

    public String getStudyNature() {
        return studyNature;
    }

    public void setStudyNature(String studyNature) {
        this.studyNature = studyNature;
    }

    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }
}