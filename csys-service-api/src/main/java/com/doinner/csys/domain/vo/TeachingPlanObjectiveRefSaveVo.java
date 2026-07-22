package com.doinner.csys.domain.vo;

import com.doinner.csys.domain.TeachingPlanObjectiveRef;

import java.util.List;

/**
 * 教学计划目标绑定毕业要求的保存入参（与目标新增解耦）。
 * <p>
 * 流程：先 POST /objective 只填目标类型与内容 →
 * 再 GET /courseGraduation 取候选、GET /objectiveRef/list 回显已绑 →
 * 最后 POST /objectiveRef/save 整表重建绑定。
 */
public class TeachingPlanObjectiveRefSaveVo {

    /** 目标 id（必填） */
    private Long objectiveId;

    /** 教学计划 id（可选，refs 缺省时回填） */
    private Long planId;

    /** 培养方案 id（可选，refs 缺省时回填） */
    private Long schemeId;

    /**
     * 要绑定的毕业要求列表。
     * 保存时先逻辑删除该 objective 下旧 ref，再按本列表 insert；
     * 传空列表或 null 表示清空全部绑定。
     */
    private List<TeachingPlanObjectiveRef> refs;

    public Long getObjectiveId() {
        return objectiveId;
    }

    public void setObjectiveId(Long objectiveId) {
        this.objectiveId = objectiveId;
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

    public List<TeachingPlanObjectiveRef> getRefs() {
        return refs;
    }

    public void setRefs(List<TeachingPlanObjectiveRef> refs) {
        this.refs = refs;
    }
}
