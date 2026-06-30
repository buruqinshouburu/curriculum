package com.doinner.csys.domain;

import com.doinner.common.core.annotation.Excel;
import com.doinner.common.core.domain.db.AbstractDoinnerLogicalDelBaseEntity;

/**
 * 课程中的知识领域关联知识单元(t_csys_source_domain、t_csys_source_unit)对象 t_csys_course_domain_ref_source_unit
 *
 * @author wzg
 * @date 2026-03-06
 */
public class CourseDomainRefSourceUnit extends BaseCourseTargetRefEntity{

    private static final long serialVersionUID=1L;

    /** 知识领域id */
            @Excel(name = "知识领域id")
    private Long domainId;

    /** 知识单元id */
            @Excel(name = "知识单元id")
    private Long unitId;


    public void setDomainId(Long domainId)
            {
            this.domainId = domainId;
            }

    public Long getDomainId()
            {
            return domainId;
            }
    public void setUnitId(Long unitId)
            {
            this.unitId = unitId;
            }

    public Long getUnitId()
            {
            return unitId;
            }

}
