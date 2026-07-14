package com.doinner.csys.domain;

import com.doinner.common.core.domain.db.AbstractDoinnerLogicalDelBaseEntity;

/**
 * 教学计划条件保障实体
 * 教室、平台、实验室、场地、装备等条件保障
 */
public class TeachingPlanCondition extends AbstractDoinnerLogicalDelBaseEntity {

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 教学计划ID
     */
    private Long planId;

    /**
     * 条件类型
     */
    private String conditionType;

    /**
     * 有关要求
     */
    private String requirement;

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

    public String getConditionType() {
        return conditionType;
    }

    public void setConditionType(String conditionType) {
        this.conditionType = conditionType;
    }

    public String getRequirement() {
        return requirement;
    }

    public void setRequirement(String requirement) {
        this.requirement = requirement;
    }

    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }
}