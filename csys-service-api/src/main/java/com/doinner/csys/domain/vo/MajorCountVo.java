package com.doinner.csys.domain.vo;

public class MajorCountVo {
    private Long collegeId;
    private String collegeName;
    private Long majorNum;
    private Long minMajorNum;

    public Long getCollegeId() {
        return collegeId;
    }

    public void setCollegeId(Long collegeId) {
        this.collegeId = collegeId;
    }

    public String getCollegeName() {
        return collegeName;
    }

    public void setCollegeName(String collegeName) {
        this.collegeName = collegeName;
    }

    public Long getMajorNum() {
        return majorNum;
    }

    public void setMajorNum(Long majorNum) {
        this.majorNum = majorNum;
    }

    public Long getMinMajorNum() {
        return minMajorNum;
    }

    public void setMinMajorNum(Long minMajorNum) {
        this.minMajorNum = minMajorNum;
    }
}
