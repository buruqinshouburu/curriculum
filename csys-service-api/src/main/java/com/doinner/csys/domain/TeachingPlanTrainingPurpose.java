package com.doinner.csys.domain;

import com.doinner.common.core.domain.db.AbstractDoinnerLogicalDelBaseEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.List;

/**
 * 教学计划训练目的实体（实践训练课目 type2 第二节「训练目的与支撑毕业要求」）。
 * <p>
 * 按 plan_id + scheme_id 保存：可多条训练目的，每条为单个值(训练目的文本)，
 * 并可经 {@link TeachingPlanTrainingPurposeRef} 绑定多条毕业要求。
 * 通识通用（课目模块仅∈{1,2,3,9}）时 scheme_id 恒为 null，不按培养方案区分。
 * <p>
 * {@link #refs} 为非持久化辅助字段，仅用于 Word 导入解析时承载该条训练目的绑定的毕业要求，
 * 不落库、不序列化。
 */
public class TeachingPlanTrainingPurpose extends AbstractDoinnerLogicalDelBaseEntity {

    /** 主键ID */
    private Long id;

    /** 教学计划ID */
    private Long planId;

    /** 培养方案ID，对应页面当前培养方案tab（通识通用为 null） */
    private Long schemeId;

    /** 训练目的 */
    private String purpose;

    /** 排序 */
    private Integer sort;

    /**
     * 非持久化：Word 导入解析时承载该条训练目的绑定的毕业要求列表，由回写逻辑落库到 ref 表。
     * 不参与序列化、不映射数据库列。
     */
    @JsonIgnore
    private transient List<TeachingPlanTrainingPurposeRef> refs;

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

    public String getPurpose() {
        return purpose;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }

    @JsonIgnore
    public List<TeachingPlanTrainingPurposeRef> getRefs() {
        return refs;
    }

    public void setRefs(List<TeachingPlanTrainingPurposeRef> refs) {
        this.refs = refs;
    }
}
