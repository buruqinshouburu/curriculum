package com.doinner.csys.domain;

import com.doinner.common.core.annotation.Excel;
import com.doinner.common.core.domain.db.AbstractDoinnerLogicalDelBaseEntity;

/**
 * 课程中的知识领域关联知识单元(t_csys_source_unit、t_csys_source_point)对象 t_csys_course_unit_ref_source_point
 *
 * @author wzg
 * @date 2026-03-06
 */
public class CourseUnitRefSourcePoint extends BaseCourseTargetRefEntity{
    private static final long serialVersionUID=1L;

    /** 知识单元id */
            @Excel(name = "知识单元id")
    private Long unitId;

    /** 知识点id */
            @Excel(name = "知识点id")
    private Long pointId;

    public void setUnitId(Long unitId)
            {
            this.unitId = unitId;
            }

    public Long getUnitId()
            {
            return unitId;
            }
    public void setPointId(Long pointId)
            {
            this.pointId = pointId;
            }

    public Long getPointId()
            {
            return pointId;
            }

}
