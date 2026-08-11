package com.doinner.csys.domain.vo;

import com.doinner.csys.domain.TeachingPlanTrainingPurpose;
import com.doinner.csys.domain.TeachingPlanTrainingPurposeRef;

import java.util.List;

/**
 * 教学计划训练目的 + 支撑毕业要求 同事务保存入参（type2 第二节）。
 * <p>
 * 推荐流程已拆分：先 POST /trainingPurpose 只存训练目的，
 * 再 POST /trainingPurposeRef/save 单独绑定毕业要求（见 {@link TeachingPlanTrainingPurposeRefSaveVo}）。
 * 本 VO 仍可用于需要同事务一次提交的场景。
 */
public class TeachingPlanTrainingPurposeSaveVo {

    /** 训练目的主表（必填） */
    private TeachingPlanTrainingPurpose purpose;

    /**
     * 支撑毕业要求列表（可空）。
     * 有 purpose.id 时：先逻辑删除该训练目的下旧 ref，再按本列表重建；
     * 无 id 时：先 insert 训练目的，再 insert 本列表。
     */
    private List<TeachingPlanTrainingPurposeRef> refs;

    public TeachingPlanTrainingPurpose getPurpose() {
        return purpose;
    }

    public void setPurpose(TeachingPlanTrainingPurpose purpose) {
        this.purpose = purpose;
    }

    public List<TeachingPlanTrainingPurposeRef> getRefs() {
        return refs;
    }

    public void setRefs(List<TeachingPlanTrainingPurposeRef> refs) {
        this.refs = refs;
    }
}
