package com.doinner.csys.domain;

import com.doinner.common.core.domain.db.AbstractIdEntity;
import com.doinner.common.core.annotation.Excel;

/**
 * 培养方案课程关联对象 t_csys_training_scheme_ref_course
 *
 * @author doinner
 * @date 2023-03-14
 */
public class TrainingSchemeRefCourse extends AbstractIdEntity {

    private static final long serialVersionUID = -5891900452859531924L;


    /**
     * 培养方案id
     */
    @Excel(name = "培养方案id")
    private Long schemeId;

    /**
     * 课程id
     */
    @Excel(name = "课程id")
    private Long courseId;
    /**
     *课程类型:1 必修 2：选修 3：任选
     */
    @Excel(name = "课程类型:1 必修 2：选修 3：任选")
    private Integer courseTypeId;
    /**
     * 课程种类：1：课程 2：训练科目 3：实践项目
     */
    @Excel(name = "课程种类：1：课程 2：训练科目 3：实践项目")
    private Integer courseClass;

    public void setSchemeId(Long schemeId) {
        this.schemeId = schemeId;
    }

    public Long getSchemeId() {
        return schemeId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

    public Long getCourseId() {
        return courseId;
    }

    public void setCourseTypeId(Integer courseTypeId) {
        this.courseTypeId = courseTypeId;
    }

    public Integer getCourseTypeId() {
        return courseTypeId;
    }

    public Integer getCourseClass() {
        return courseClass;
    }

    public void setCourseClass(Integer courseClass) {
        this.courseClass = courseClass;
    }
}
