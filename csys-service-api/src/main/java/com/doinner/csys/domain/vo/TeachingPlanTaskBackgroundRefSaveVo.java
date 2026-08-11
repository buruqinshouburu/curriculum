package com.doinner.csys.domain.vo;

import com.doinner.csys.domain.TeachingPlanTaskBackgroundRef;

import java.util.List;

/**
 * 教学计划任务背景绑定毕业要求的保存入参（与任务背景新增解耦）。
 * <p>
 * 流程：先 POST /taskBackground 只填描述/技术目标/能力目标 ->
 * 再 GET /courseGraduation 取候选、GET /taskBackgroundRef/list 回显已绑 ->
 * 最后 POST /taskBackgroundRef/save 整表重建绑定。
 */
public class TeachingPlanTaskBackgroundRefSaveVo {

    /** 任务背景 id（必填） */
    private Long taskBackgroundId;

    /** 教学计划 id（可选，refs 缺省时回填） */
    private Long planId;

    /** 培养方案 id（可选，refs 缺省时回填） */
    private Long schemeId;

    /**
     * 要绑定的毕业要求列表。
     * 保存时先逻辑删除该任务背景下旧 ref，再按本列表 insert；
     * 传空列表或 null 表示清空全部绑定。
     */
    private List<TeachingPlanTaskBackgroundRef> refs;

    public Long getTaskBackgroundId() {
        return taskBackgroundId;
    }

    public void setTaskBackgroundId(Long taskBackgroundId) {
        this.taskBackgroundId = taskBackgroundId;
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

    public List<TeachingPlanTaskBackgroundRef> getRefs() {
        return refs;
    }

    public void setRefs(List<TeachingPlanTaskBackgroundRef> refs) {
        this.refs = refs;
    }
}
