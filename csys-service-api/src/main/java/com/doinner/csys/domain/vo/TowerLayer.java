package com.doinner.csys.domain.vo;

import java.util.List;

/** 塔层信息 */
public class TowerLayer {

    /** 课程id */
    private Long courseId;

    /** 塔左端叶子节点id */
    private Long sourceId;

    private List<Long> targetIds;

    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

    public Long getSourceId() {
        return sourceId;
    }

    public void setSourceId(Long sourceId) {
        this.sourceId = sourceId;
    }

    public List<Long> getTargetIds() {
        return targetIds;
    }

    public void setTargetIds(List<Long> targetIds) {
        this.targetIds = targetIds;
    }
}
