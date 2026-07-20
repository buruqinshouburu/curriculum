package com.doinner.csys.domain.vo;

import com.doinner.csys.domain.TeachingPlanObjective;
import com.doinner.csys.domain.TeachingPlanObjectiveRef;

import java.util.List;

/**
 * 教学计划目标 + 支撑毕业要求 同事务保存入参。
 * <p>
 * 对应设计：新增目标内容与绑定毕业要求在同一弹框完成；
 * 一条目标可绑定当前 tab 下多个毕业要求。
 */
public class TeachingPlanObjectiveSaveVo {

    /** 目标主表（必填） */
    private TeachingPlanObjective objective;

    /**
     * 支撑毕业要求列表（可空）。
     * 有 objective.id 时：先逻辑删除该目标下旧 ref，再按本列表重建；
     * 无 id 时：先 insert 目标，再 insert 本列表。
     */
    private List<TeachingPlanObjectiveRef> refs;

    public TeachingPlanObjective getObjective() {
        return objective;
    }

    public void setObjective(TeachingPlanObjective objective) {
        this.objective = objective;
    }

    public List<TeachingPlanObjectiveRef> getRefs() {
        return refs;
    }

    public void setRefs(List<TeachingPlanObjectiveRef> refs) {
        this.refs = refs;
    }
}
