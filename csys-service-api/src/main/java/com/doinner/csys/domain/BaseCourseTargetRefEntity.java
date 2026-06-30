package com.doinner.csys.domain;

import com.doinner.common.core.domain.db.AbstractDoinnerLogicalDelBaseEntity;

public abstract class BaseCourseTargetRefEntity extends AbstractDoinnerLogicalDelBaseEntity {
    private Long courseId;

    private Long collegeId;

    private Long categoryId;

    private Long majorId;

    private Long courseTargetId;

    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

    public Long getCollegeId() {
        return collegeId;
    }

    public void setCollegeId(Long collegeId) {
        this.collegeId = collegeId;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public Long getMajorId() {
        return majorId;
    }

    public void setMajorId(Long majorId) {
        this.majorId = majorId;
    }

    public Long getCourseTargetId() {
        return courseTargetId;
    }

    public void setCourseTargetId(Long courseTargetId) {
        this.courseTargetId = courseTargetId;
    }
}
