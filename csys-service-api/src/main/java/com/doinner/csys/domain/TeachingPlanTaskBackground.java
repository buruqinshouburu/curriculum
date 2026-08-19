package com.doinner.csys.domain;

import com.doinner.common.core.domain.db.AbstractDoinnerLogicalDelBaseEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.List;

/**
 * 教学计划任务背景实体（实验课程第三节「任务背景与目标」）。
 * <p>
 * 按 plan_id + scheme_id 保存，对标 {@link TeachingPlanObjective}：
 * 可多条任务背景，每条含任务背景描述/技术目标/能力目标，并可经
 * {@link TeachingPlanTaskBackgroundRef} 绑定多条毕业要求。
 * <p>
 * {@link #refs} 为非持久化辅助字段，仅用于 Word 导入解析时承载该条任务背景绑定的毕业要求，
 * 不落库、不序列化。
 */
public class TeachingPlanTaskBackground extends AbstractDoinnerLogicalDelBaseEntity {

    /** 主键ID */
    private Long id;

    /** 教学计划ID */
    private Long planId;

    /** 培养方案ID，对应页面当前培养方案tab（公共基础为 null） */
    private Long schemeId;

    /** 专业ID */
    private Long majorId;

    /** 任务背景描述 */
    private String backgroundDesc;

    /** 技术目标 */
    private String technicalGoal;

    /** 能力目标 */
    private String abilityGoal;

    /** 排序 */
    private Integer sort;

    /**
     * 非持久化：Word 导入解析时承载该条任务背景绑定的毕业要求列表，由回写逻辑落库到 ref 表。
     * 不参与序列化、不映射数据库列。
     */
    @JsonIgnore
    private transient List<TeachingPlanTaskBackgroundRef> refs;

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

    public Long getSchemeId() {
        return schemeId;
    }

    public void setSchemeId(Long schemeId) {
        this.schemeId = schemeId;
    }

    public Long getMajorId() {
        return majorId;
    }

    public void setMajorId(Long majorId) {
        this.majorId = majorId;
    }

    public String getBackgroundDesc() {
        return backgroundDesc;
    }

    public void setBackgroundDesc(String backgroundDesc) {
        this.backgroundDesc = backgroundDesc;
    }

    public String getTechnicalGoal() {
        return technicalGoal;
    }

    public void setTechnicalGoal(String technicalGoal) {
        this.technicalGoal = technicalGoal;
    }

    public String getAbilityGoal() {
        return abilityGoal;
    }

    public void setAbilityGoal(String abilityGoal) {
        this.abilityGoal = abilityGoal;
    }

    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }

    @JsonIgnore
    public List<TeachingPlanTaskBackgroundRef> getRefs() {
        return refs;
    }

    public void setRefs(List<TeachingPlanTaskBackgroundRef> refs) {
        this.refs = refs;
    }
}
