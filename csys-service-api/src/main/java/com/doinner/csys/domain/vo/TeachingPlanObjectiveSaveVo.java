package com.doinner.csys.domain.vo;

import com.doinner.csys.domain.TeachingPlanObjective;
import com.doinner.csys.domain.TeachingPlanObjectiveRef;

import java.util.List;

/**
 * 教学计划目标 + 支撑毕业要求 同事务保存入参（兼容旧前端一次提交）。
 * <p>
 * 推荐流程已拆分：先 POST /objective 只存目标类型/内容，
 * 再 POST /objectiveRef/save 单独绑定毕业要求（见 {@link TeachingPlanObjectiveRefSaveVo}）。
 * 本 VO 仍可用于需要同事务一次提交的场景。
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
