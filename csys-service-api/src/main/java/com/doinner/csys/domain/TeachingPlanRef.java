package com.doinner.csys.domain;

import com.doinner.common.core.domain.db.AbstractDoinnerLogicalDelBaseEntity;

import java.math.BigDecimal;

/**
 * 教学计划通用引用实体
 * 支撑课程、实践训练课目、涉及知识单元/知识点等通用引用
 */
public class TeachingPlanRef extends AbstractDoinnerLogicalDelBaseEntity {

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 教学计划ID
     */
    private Long planId;

    /**
     * 调用上下文ID，可为空
     */
    private Long contextId;

    /**
     * 引用类型：1支撑总库课程 2支撑调用课程/训练课目 3知识单元 4知识点 5教学目标
     */
    private Integer refType;

    /**
     * 引用对象ID
     */
    private Long refId;

    /**
     * 引用对象名称快照
     */
    private String refName;

    /**
     * 引用课程/课目学时快照
     */
    private BigDecimal refHours;

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

    public Long getContextId() {
        return contextId;
    }

    public void setContextId(Long contextId) {
        this.contextId = contextId;
    }

    public Integer getRefType() {
        return refType;
    }

    public void setRefType(Integer refType) {
        this.refType = refType;
    }

    public Long getRefId() {
        return refId;
    }

    public void setRefId(Long refId) {
        this.refId = refId;
    }

    public String getRefName() {
        return refName;
    }

    public void setRefName(String refName) {
        this.refName = refName;
    }

    public BigDecimal getRefHours() {
        return refHours;
    }

    public void setRefHours(BigDecimal refHours) {
        this.refHours = refHours;
    }

    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }
}