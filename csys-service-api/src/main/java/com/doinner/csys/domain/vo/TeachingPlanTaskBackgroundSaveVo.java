package com.doinner.csys.domain.vo;

import com.doinner.csys.domain.TeachingPlanTaskBackground;
import com.doinner.csys.domain.TeachingPlanTaskBackgroundRef;

import java.util.List;

/**
 * 教学计划任务背景 + 支撑毕业要求 同事务保存入参。
 * <p>
 * 推荐流程已拆分：先 POST /taskBackground 只存任务背景描述/技术目标/能力目标，
 * 再 POST /taskBackgroundRef/save 单独绑定毕业要求（见 {@link TeachingPlanTaskBackgroundRefSaveVo}）。
 * 本 VO 仍可用于需要同事务一次提交的场景。
 */
public class TeachingPlanTaskBackgroundSaveVo {

    /** 任务背景主表（必填） */
    private TeachingPlanTaskBackground taskBackground;

    /**
     * 支撑毕业要求列表（可空）。
     * 有 taskBackground.id 时：先逻辑删除该任务背景下旧 ref，再按本列表重建；
     * 无 id 时：先 insert 任务背景，再 insert 本列表。
     */
    private List<TeachingPlanTaskBackgroundRef> refs;

    public TeachingPlanTaskBackground getTaskBackground() {
        return taskBackground;
    }

    public void setTaskBackground(TeachingPlanTaskBackground taskBackground) {
        this.taskBackground = taskBackground;
    }

    public List<TeachingPlanTaskBackgroundRef> getRefs() {
        return refs;
    }

    public void setRefs(List<TeachingPlanTaskBackgroundRef> refs) {
        this.refs = refs;
    }
}
