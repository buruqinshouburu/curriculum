package com.doinner.csys.domain;

import com.doinner.common.core.domain.AbstractEntity;

/**
 * 课程与知识单元关联对象 t_csys_course_ref_ke_unit
 *
 * @author doinner
 * @date 2023-03-14
 */
public class CourseRefKeUnit extends AbstractEntity {

    private static final long serialVersionUID = 1875864979589194039L;

    /**
     * 课程id
     */
    private Long courseId;

    /**
     * 知识单元id
     */
    private Long unitId;

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

    public Long getCourseId() {
        return courseId;
    }

    public void setUnitId(Long unitId) {
        this.unitId = unitId;
    }

    public Long getUnitId() {
        return unitId;
    }


}
