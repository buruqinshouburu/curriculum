package com.doinner.csys.domain;

import com.doinner.common.core.domain.db.AbstractDoinnerLogicalDelBaseEntity;
import com.doinner.common.core.annotation.Excel;

/**
 * 课程教学计划审核历史对象 t_csys_course_review
 *
 * @author doinner
 * @date 2023-03-27
 */
public class CourseReview extends AbstractDoinnerLogicalDelBaseEntity {

    private static final long serialVersionUID = -7615869558565359935L;


    /**
     * 记录(review)
     */
    @Excel(name = "记录(review)")
    private String review;

    /**
     * 课程(course_id)
     */
    @Excel(name = "课程(course_id)")
    private Long courseId;

    /**
     * 创建部门(dept_by)
     */
    @Excel(name = "创建部门(dept_by)")
    private Long deptBy;

    public void setReview(String review) {
        this.review = review;
    }

    public String getReview() {
        return review;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

    public Long getCourseId() {
        return courseId;
    }

    public void setDeptBy(Long deptBy) {
        this.deptBy = deptBy;
    }

    public Long getDeptBy() {
        return deptBy;
    }


}
