package com.doinner.csys.domain.vo;

import java.util.List;

/**
 * 实践项目第二节「支撑的课程目标或训练目的」多选保存入参（type4，整表重建）。
 * <p>
 * objectiveIds：绑定的课程目标（t_csys_teaching_plan_objective.id，来自支撑课程课程教学计划第四部分）；
 * purposeIds：绑定的训练目的（t_csys_teaching_plan_training_purpose.id，来自支撑训练课目第二部分）。
 * 保存时先逻辑删除该 plan 下旧绑定，再按 id 重建快照；空列表或 null 表示清空。
 */
public class TeachingPlanSupportObjectiveSaveVo {

    /** 实践项目教学计划ID（必填，type4） */
    private Long planId;

    /** 要绑定的课程目标 id 列表 */
    private List<Long> objectiveIds;

    /** 要绑定的训练目的 id 列表 */
    private List<Long> purposeIds;

    public Long getPlanId() {
        return planId;
    }

    public void setPlanId(Long planId) {
        this.planId = planId;
    }

    public List<Long> getObjectiveIds() {
        return objectiveIds;
    }

    public void setObjectiveIds(List<Long> objectiveIds) {
        this.objectiveIds = objectiveIds;
    }

    public List<Long> getPurposeIds() {
        return purposeIds;
    }

    public void setPurposeIds(List<Long> purposeIds) {
        this.purposeIds = purposeIds;
    }
}
