package com.doinner.csys.domain.vo;

import java.util.List;

public class TrainingBoundCourseVo {

    private Long trainingSchemeId;

    private List<Long> courseIds;

    private Long majorId;

    private String version;
    /**
     * 类型 0：追加 1：覆盖
     */
    private String type;
    /**
     * 课程属性 1 课程 3 实践训练科目 4 实践项目
     */
    private String attr;

    public Long getTrainingSchemeId() {
        return trainingSchemeId;
    }

    public void setTrainingSchemeId(Long trainingSchemeId) {
        this.trainingSchemeId = trainingSchemeId;
    }

    public List<Long> getCourseIds() {
        return courseIds;
    }

    public void setCourseIds(List<Long> courseIds) {
        this.courseIds = courseIds;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getAttr() {
        return attr;
    }

    public void setAttr(String attr) {
        this.attr = attr;
    }

    public Long getMajorId() {
        return majorId;
    }

    public void setMajorId(Long majorId) {
        this.majorId = majorId;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}
