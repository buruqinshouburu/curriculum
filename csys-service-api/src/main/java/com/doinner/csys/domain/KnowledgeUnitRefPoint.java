package com.doinner.csys.domain;

import com.doinner.common.core.domain.AbstractEntity;

/**
 * 知识单元与知识点关联对象 t_csys_knowledge_unit_ref_point
 *
 * @author doinner
 * @date 2023-03-14
 */
public class KnowledgeUnitRefPoint extends AbstractEntity {

    private static final long serialVersionUID = -1033326537647380029L;

    /**
     * 知识单元id
     */
    private Long unitId;

    /**
     * 知识点id
     */
    private Long pointId;

    public void setUnitId(Long unitId) {
        this.unitId = unitId;
    }

    public Long getUnitId() {
        return unitId;
    }

    public void setPointId(Long pointId) {
        this.pointId = pointId;
    }

    public Long getPointId() {
        return pointId;
    }

}
