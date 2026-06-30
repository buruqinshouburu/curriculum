package com.doinner.csys.domain;

public class TeachingProgrammeInstanceDto {
    public Long categoryId;
    public Long majorId;
    public String version;
    public Long sourceInstanceId;

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public Long getMajorId() {
        return majorId;
    }

    public void setMajorId(Long majorId) {
        this.majorId = majorId;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public Long getSourceInstanceId() {
        return sourceInstanceId;
    }

    public void setSourceInstanceId(Long sourceInstanceId) {
        this.sourceInstanceId = sourceInstanceId;
    }
}
