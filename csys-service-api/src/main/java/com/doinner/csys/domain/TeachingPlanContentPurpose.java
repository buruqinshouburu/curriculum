package com.doinner.csys.domain;

import com.doinner.common.core.domain.db.AbstractDoinnerLogicalDelBaseEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * 教学计划训练内容支撑训练目的绑定实体（type2 实践训练课目第四节「目的」多选）。
 * <p>
 * 每条记录表示一条训练内容(content) 绑定一条训练目的(purpose)，用于 Word 第四节「目的」列渲染。
 * 结构对标 {@link TeachingPlanTrainingPurposeRef}，但更轻量：只存引用，不存毕业要求快照。
 */
public class TeachingPlanContentPurpose extends AbstractDoinnerLogicalDelBaseEntity {

    /** 主键ID */
    private Long id;

    /** 教学计划ID */
    private Long planId;

    /** 训练内容ID t_csys_teaching_plan_content.id */
    private Long contentId;

    /** 训练目的ID t_csys_teaching_plan_training_purpose.id */
    private Long purposeId;

    /** 排序 */
    private Integer sort;

    /**
     * 非持久化：训练目的文本快照，Word 渲染/列表回显用。
     * 不映射数据库列、不参与持久化。
     */
    @JsonIgnore
    private transient String purposeText;

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

    public Long getContentId() {
        return contentId;
    }

    public void setContentId(Long contentId) {
        this.contentId = contentId;
    }

    public Long getPurposeId() {
        return purposeId;
    }

    public void setPurposeId(Long purposeId) {
        this.purposeId = purposeId;
    }

    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }

    @JsonIgnore
    public String getPurposeText() {
        return purposeText;
    }

    public void setPurposeText(String purposeText) {
        this.purposeText = purposeText;
    }
}
