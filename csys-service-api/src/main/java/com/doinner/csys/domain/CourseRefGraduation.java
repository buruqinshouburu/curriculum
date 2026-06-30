package com.doinner.csys.domain;

import com.doinner.common.core.annotation.Excel;
import com.doinner.common.core.domain.db.AbstractDoinnerLogicalDelBaseEntity;

/**
 * 课程和毕业标准对象 t_csys_course_ref_graduation
 *
 * @author wzg
 * @date 2026-03-06
 */
public class CourseRefGraduation extends BaseCourseTargetRefEntity{
    private static final long serialVersionUID=1L;



    /** 毕业标准id */
            @Excel(name = "毕业标准id")
    private Long graduationId;


    public void setGraduationId(Long graduationId)
            {
            this.graduationId = graduationId;
            }

    public Long getGraduationId()
            {
            return graduationId;
            }


}
