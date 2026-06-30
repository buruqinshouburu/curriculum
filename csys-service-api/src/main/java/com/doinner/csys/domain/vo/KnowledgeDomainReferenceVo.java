package com.doinner.csys.domain.vo;

import java.util.List;

public class KnowledgeDomainReferenceVo {
    private List<Long> sourceDomainIdList;
    private Long collegeId;
    private Long majorId;
    private Long categoryId;

    public List<Long> getSourceDomainIdList() {
        return sourceDomainIdList;
    }

    public void setSourceDomainIdList(List<Long> sourceDomainIdList) {
        this.sourceDomainIdList = sourceDomainIdList;
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

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }
}
