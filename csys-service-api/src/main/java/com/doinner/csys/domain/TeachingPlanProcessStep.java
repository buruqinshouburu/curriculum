package com.doinner.csys.domain;

import com.doinner.common.core.domain.db.AbstractDoinnerLogicalDelBaseEntity;

/**
 * 教学计划组织实施步骤实体。
 * <p>
 * 字段为历史通用命名，实践训练课目第五部分中的明确语义为：
 * {@code stageName}=“实施步骤”列的数据（字典编码），
 * {@code stepName}=“阶段划分”列的数据，
 * {@code requirement}=“有关要求”列的数据。
 * “实施步骤 / 阶段划分 / 有关要求”三个固定表头本身不入库。
 */
public class TeachingPlanProcessStep extends AbstractDoinnerLogicalDelBaseEntity {

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 教学计划ID
     */
    private Long planId;

    /**
     * 关联实践项目ID
     */
    private Long itemId;

    /**
     * 步骤分组。
     * 实践训练课目：实施步骤类别编码（sys_plan_implementation_step，例如1=战斗准备）；
     * 实践项目：可作为项目阶段名称。
     */
    private String stageName;

    /**
     * 步骤明细。
     * 实践训练课目：阶段划分（例如“战备等级转进”）；
     * 实践项目：项目步骤。
     */
    private String stepName;

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

    public Long getItemId() {
        return itemId;
    }

    public void setItemId(Long itemId) {
        this.itemId = itemId;
    }

    public String getStageName() {
        return stageName;
    }

    public void setStageName(String stageName) {
        this.stageName = stageName;
    }

    public String getStepName() {
        return stepName;
    }

    public void setStepName(String stepName) {
        this.stepName = stepName;
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
