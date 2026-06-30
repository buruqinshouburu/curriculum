package com.doinner.csys.domain;

/**
 * 源知识单元与知识点对象 t_csys_source_unit_ref_point
 *
 * @author wzg
 * @date 2026-02-26
 */
public class SourceUnitRefPoint{

    private static final long serialVersionUID=1L;

    /** 知识单元id */
    private Long unitId;

    /** 知识点id */
    private Long pointId;

    public Long getUnitId() {
        return unitId;
    }

    public void setUnitId(Long unitId) {
        this.unitId = unitId;
    }

    public Long getPointId() {
        return pointId;
    }

    public void setPointId(Long pointId) {
        this.pointId = pointId;
    }
}
