package com.doinner.csys.domain.vo;

import java.util.List;

/**
 * 实践项目(type=4)「二、任务背景与目标」整页保存入参。
 * 四块页面数据由同一个接口在同一事务内保存；三个 id 列表为空或 null 时清空对应绑定。
 */
public class TeachingPlanPracticeProjectBackgroundSaveVo {

    /** 实践项目教学计划 id（必填） */
    private Long planId;

    /** 拟解决的复杂问题（富文本字符串） */
    private String complexProblem;

    /** 主要任务（富文本字符串） */
    private String mainTask;

    /** 支撑的课程目标 id */
    private List<Long> objectiveIds;

    /** 支撑训练课目的训练目的 id */
    private List<Long> purposeIds;

    /** 涉及的知识体系或训练内容 id */
    private List<Long> contentIds;

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
}
