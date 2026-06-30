package com.doinner.csys.domain.vo;

import java.util.List;

public class CourseTemplateVo {

    private List<Long> ids;

    //private Integer type;

    private Long collegeId;

    private Long majorId;

    private Long subMajorId;

    private Long categoryId;

    private String version;

    private Long trainingSchemeId;

    public List<Long> getIds() {
        return ids;
    }

    public void setIds(List<Long> ids) {
        this.ids = ids;
    }


    public Long getCollegeId() {
        return collegeId;
    }

    public void setCollegeId(Long collegeId) {
        this.collegeId = collegeId;
    }

    public Long getMajorId() {
        return majorId;
    }

    public void setMajorId(Long majorId) {
        this.majorId = majorId;
    }

    public Long getSubMajorId() {
        return subMajorId;
    }

    public void setSubMajorId(Long subMajorId) {
        this.subMajorId = subMajorId;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }



    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public Long getTrainingSchemeId() {
        return trainingSchemeId;
    }

    public void setTrainingSchemeId(Long trainingSchemeId) {
        this.trainingSchemeId = trainingSchemeId;
    }
}
