package com.doinner.csys.domain;

import com.doinner.common.core.domain.AbstractEntity;

/**
 * 培养标准与毕业标准关联对象 t_csys_std_cultivation_ref_graduation
 *
 * @author doinner
 * @date 2023-03-14
 */
public class StandardCultivationRefGraduation extends AbstractEntity {

    private static final long serialVersionUID = -7644342578420161628L;

    /**
     * 培养标准id
     */
    private Long cultivationId;

    /**
     * 毕业标准id
     */
    private Long graduationId;

    public void setCultivationId(Long cultivationId) {
        this.cultivationId = cultivationId;
    }

    public Long getCultivationId() {
        return cultivationId;
    }

    public void setGraduationId(Long graduationId) {
        this.graduationId = graduationId;
    }

    public Long getGraduationId() {
        return graduationId;
    }

}
