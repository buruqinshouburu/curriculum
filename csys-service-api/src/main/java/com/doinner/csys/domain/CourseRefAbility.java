package com.doinner.csys.domain;

import com.doinner.common.core.annotation.Excel;

/**
 * 课程和能力对象 t_csys_course_ref_ability
 *
 * @author wzg
 * @date 2026-03-16
 */
public class CourseRefAbility extends BaseCourseTargetRefEntity{

    private static final long serialVersionUID=1L;

    /** 能力id */
            @Excel(name = "能力id")
    private Long abilityId;


    public void setAbilityId(Long abilityId)
            {
            this.abilityId = abilityId;
            }

    public Long getAbilityId()
            {
            return abilityId;
            }



}
