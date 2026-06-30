package com.doinner.csys.domain.vo;

/**
 * @author wzg
 * @date 2023/3/24 10:01
 */
public class CurriculumVo {
    private String collectName;
    private Long collectId;
    private Integer courseCount;

    public String getCollectName() {
        return collectName;
    }

    public void setCollectName(String collectName) {
        this.collectName = collectName;
    }

    public Long getCollectId() {
        return collectId;
    }

    public void setCollectId(Long collectId) {
        this.collectId = collectId;
    }

    public Integer getCourseCount() {
        return courseCount;
    }

    public void setCourseCount(Integer courseCount) {
        this.courseCount = courseCount;
    }
}
