package com.doinner.csys.domain.vo;

/**
 * @author wzg
 * @date 2023/4/3 15:11
 */
public class CourseAndSpecializedVo {

    private Long collectId;
    private Long majorCount;
    private String deptName;
    private Long courseCount;

    public Long getCollectId() {
        return collectId;
    }

    public void setCollectId(Long collectId) {
        this.collectId = collectId;
    }

    public Long getMajorCount() {
        return majorCount;
    }

    public void setMajorCount(Long majorCount) {
        this.majorCount = majorCount;
    }

    public String getDeptName() {
        return deptName;
    }

    public void setDeptName(String deptName) {
        this.deptName = deptName;
    }

    public Long getCourseCount() {
        return courseCount;
    }

    public void setCourseCount(Long courseCount) {
        this.courseCount = courseCount;
    }
}
