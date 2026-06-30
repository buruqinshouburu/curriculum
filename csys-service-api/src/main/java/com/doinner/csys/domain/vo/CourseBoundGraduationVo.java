package com.doinner.csys.domain.vo;

import java.util.List;

public class CourseBoundGraduationVo {
    private Long courseId;
    private List<Long> graduationIds;

    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

    public List<Long> getGraduationIds() {
        return graduationIds;
    }

    public void setGraduationIds(List<Long> graduationIds) {
        this.graduationIds = graduationIds;
    }
}
