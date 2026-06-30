package com.doinner.csys.domain;

import com.doinner.common.core.annotation.Excel;

/**
 * 源知识领域关联知识单元对象 t_csys_source_domain_ref_unit
 *
 * @author wzg
 * @date 2026-02-26
 */
public class SourceDomainRefUnit{
    private static final long serialVersionUID=1L;

    /** 领域id */
            @Excel(name = "领域id")
    private Long domainId;

    /** 知识单元id */
            @Excel(name = "知识单元id")
    private Long unitId;

    public Long getDomainId() {
        return domainId;
    }

    public void setDomainId(Long domainId) {
        this.domainId = domainId;
    }

    public Long getUnitId() {
        return unitId;
    }

    public void setUnitId(Long unitId) {
        this.unitId = unitId;
    }
}
