package com.agileai.dataparser.domain;

import com.doinner.common.core.web.domain.BaseEntity;

/** 课程计划历史记录 **/
public class CurTeachingPlanningReview extends BaseEntity {

    private Long id;    // 主键id
    private Long mainId;    // 课程计划主表id
    private String review;    // 历史记录

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getMainId() {
        return mainId;
    }

    public void setMainId(Long mainId) {
        this.mainId = mainId;
    }

    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
    }
}
