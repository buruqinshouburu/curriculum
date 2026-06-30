package com.doinner.csys.domain.vo;

import java.util.List;

public class CourseKnowledgeVo {
    private Long courseId;
    private List<CourseKnowledgeUnitVo> unitList;

    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

    public List<CourseKnowledgeUnitVo> getUnitList() {
        return unitList;
    }

    public void setUnitList(List<CourseKnowledgeUnitVo> unitList) {
        this.unitList = unitList;
    }
}
