package com.doinner.csys.domain.vo;

import com.doinner.csys.domain.TeachingPlanTrainingPurposeRef;

import java.util.List;

/**
 * 教学计划训练目的绑定毕业要求的保存入参（type2 第二节，与训练目的新增解耦）。
 * <p>
 * 流程：先 POST /trainingPurpose 只填训练目的 ->
 * 再 GET /courseGraduation 取候选、GET /trainingPurposeRef/list 回显已绑 ->
 * 最后 POST /trainingPurposeRef/save 整表重建绑定。
 */
public class TeachingPlanTrainingPurposeRefSaveVo {

    /** 训练目的 id（必填） */
    private Long purposeId;

    /** 教学计划 id（可选，refs 缺省时回填） */
    private Long planId;

    /** 培养方案 id（可选，refs 缺省时回填） */
    private Long schemeId;

    /**
     * 要绑定的毕业要求列表。
     * 保存时先逻辑删除该训练目的下旧 ref，再按本列表 insert；
     * 传空列表或 null 表示清空全部绑定。
     */
    private List<TeachingPlanTrainingPurposeRef> refs;

    public Long getPurposeId() {
        return purposeId;
    }

    public void setPurposeId(Long purposeId) {
        this.purposeId = purposeId;
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

    public List<TeachingPlanTrainingPurposeRef> getRefs() {
        return refs;
    }

    public void setRefs(List<TeachingPlanTrainingPurposeRef> refs) {
        this.refs = refs;
    }
}
