package com.doinner.csys.domain.vo;

import java.util.List;

/**
 * 训练内容支撑训练目的 整表重建保存入参（type2 第四节「目的」多选，对标 TrainingPurposeRefSaveVo）。
 * 保存时先逻辑删除该训练内容下旧绑定，再按 purposeIds 重建；空列表或 null 表示清空。
 */
public class TeachingPlanContentPurposeSaveVo {

    /** 训练内容 id（必填） */
    private Long contentId;

    /** 教学计划 id（可选，purposeIds 缺省时回填） */
    private Long planId;

    /** 要绑定的训练目的 id 列表 */
    private List<Long> purposeIds;

    public Long getContentId() {
        return contentId;
    }

    public void setContentId(Long contentId) {
        this.contentId = contentId;
    }

    public Long getPlanId() {
        return planId;
    }

    public void setPlanId(Long planId) {
        this.planId = planId;
    }

    public List<Long> getPurposeIds() {
        return purposeIds;
    }

    public void setPurposeIds(List<Long> purposeIds) {
        this.purposeIds = purposeIds;
    }
}
