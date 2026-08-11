package com.doinner.csys.domain.vo;

import java.util.List;

/**
 * 实践项目第二节「涉及的知识体系或训练内容」多选保存入参（type4，整表重建）。
 * <p>
 * contentIds：绑定的教学内容（t_csys_teaching_plan_content.id），
 * 来源支撑课程的 content 行（知识体系，知识单元知识点全部内容行）或支撑训练课目第四部分的 content 行（训练内容）；
 * ref_type 由来源教学计划类型推导（课目 planType=2 -> 2训练内容，其余 -> 1知识体系）。
 * 保存时先逻辑删除该 plan 下旧绑定，再按 id 重建快照；空列表或 null 表示清空。
 */
public class TeachingPlanSupportContentSaveVo {

    /** 实践项目教学计划ID（必填，type4） */
    private Long planId;

    /** 要绑定的教学内容 id 列表 */
    private List<Long> contentIds;

    public Long getPlanId() {
        return planId;
    }

    public void setPlanId(Long planId) {
        this.planId = planId;
    }

    public List<Long> getContentIds() {
        return contentIds;
    }

    public void setContentIds(List<Long> contentIds) {
        this.contentIds = contentIds;
    }
}
