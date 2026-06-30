package com.doinner.csys.domain;

import com.doinner.common.core.annotation.Excel;
import com.doinner.common.core.domain.db.AbstractDoinnerLogicalDelBaseEntity;

/**
 * 课程关联知识领域(t_csys_source_domain)对象 t_csys_course_ref_source_domain
 *
 * @author wzg
 * @date 2026-03-06
 */
public class CourseRefSourceDomain extends BaseCourseTargetRefEntity{

    private static final long serialVersionUID=1L;

    /** 知识领域id */
            @Excel(name = "知识领域id")
    private Long domainId;

    public void setDomainId(Long domainId)
            {
            this.domainId = domainId;
            }

    public Long getDomainId()
            {
            return domainId;
            }


}
