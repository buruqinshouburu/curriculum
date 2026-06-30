package com.doinner.csys.domain;

import com.doinner.common.core.domain.AbstractEntity;

/**
 * 毕业标准与培养目标关联对象 t_csys_std_graduation_ref_cultivation_target
 *
 * @author doinner
 * @date 2023-03-14
 */
public class StandardGraduationRefCultivationTarget extends AbstractEntity {

    private static final long serialVersionUID = -7530999703267130802L;

    /**
     * 毕业标准id
     */
    private Long graduationId;

    /**
     * 培养目标id
     */
    private Long cultivationTargetId;

    public Long getGraduationId() {
        return graduationId;
    }

    public void setGraduationId(Long graduationId) {
        this.graduationId = graduationId;
    }

    public void setCultivationTargetId(Long cultivationTargetId) {
        this.cultivationTargetId = cultivationTargetId;
    }

    public Long getCultivationTargetId() {
        return cultivationTargetId;
    }

}
