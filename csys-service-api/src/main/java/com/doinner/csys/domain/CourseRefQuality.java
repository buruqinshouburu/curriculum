package com.doinner.csys.domain;

import com.doinner.common.core.annotation.Excel;

/**
 * 课程和素质对象 t_csys_course_ref_quality
 *
 * @author wzg
 * @date 2026-03-16
 */
public class CourseRefQuality extends BaseCourseTargetRefEntity{

    private static final long serialVersionUID=1L;

    /** 素质id */
            @Excel(name = "素质id")
    private Long qualityId;

    public void setQualityId(Long qualityId)
            {
            this.qualityId = qualityId;
            }

    public Long getQualityId()
            {
            return qualityId;
            }



}
