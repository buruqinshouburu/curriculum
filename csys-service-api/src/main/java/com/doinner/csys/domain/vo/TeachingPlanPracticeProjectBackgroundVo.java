package com.doinner.csys.domain.vo;

import com.doinner.csys.domain.TeachingPlanSupportContent;
import com.doinner.csys.domain.TeachingPlanSupportObjective;

import java.util.List;

/** 实践项目(type=4)「二、任务背景与目标」整页详情。 */
public class TeachingPlanPracticeProjectBackgroundVo {

    private Long planId;
    private String complexProblem;
    private String mainTask;
    private List<Long> objectiveIds;
    private List<Long> purposeIds;
    private List<Long> contentIds;
    /** 已绑定目标/目的完整信息，供页面直接显示名称。培养方案名称从候选树对应节点显示。 */
    private List<TeachingPlanSupportObjective> supportObjectives;
    /** 已绑定知识体系/训练内容完整信息，供页面直接显示名称。 */
    private List<TeachingPlanSupportContent> supportContents;

    public Long getPlanId() {
        return planId;
    }

    public void setPlanId(Long planId) {
        this.planId = planId;
    }

    public String getComplexProblem() {
        return complexProblem;
    }

    public void setComplexProblem(String complexProblem) {
        this.complexProblem = complexProblem;
    }

    public String getMainTask() {
        return mainTask;
    }

    public void setMainTask(String mainTask) {
        this.mainTask = mainTask;
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

    public List<Long> getContentIds() {
        return contentIds;
    }

    public void setContentIds(List<Long> contentIds) {
        this.contentIds = contentIds;
    }

    public List<TeachingPlanSupportObjective> getSupportObjectives() {
        return supportObjectives;
    }

    public void setSupportObjectives(List<TeachingPlanSupportObjective> supportObjectives) {
        this.supportObjectives = supportObjectives;
    }

    public List<TeachingPlanSupportContent> getSupportContents() {
        return supportContents;
    }

    public void setSupportContents(List<TeachingPlanSupportContent> supportContents) {
        this.supportContents = supportContents;
    }
}
