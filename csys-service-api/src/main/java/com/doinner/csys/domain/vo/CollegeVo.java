package com.doinner.csys.domain.vo;


public class CollegeVo {

    /** 学院id */
    private Long deptId;

    /** 学院父级id */
    private Long parentId;

    /** 学院名称 */
    private String deptName;

    /** 总课程数 */
    private Integer countCourse;

    /** 已审核课程数 */
    private Integer countReview;

    /** 排序 */
    private Integer orderNum;

    public Long getDeptId() {
        return deptId;
    }

    public void setDeptId(Long deptId) {
        this.deptId = deptId;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public String getDeptName() {
        return deptName;
    }

    public void setDeptName(String deptName) {
        this.deptName = deptName;
    }

    public Integer getCountCourse() {
        return countCourse;
    }

    public void setCountCourse(Integer countCourse) {
        this.countCourse = countCourse;
    }

    public Integer getCountReview() {
        return countReview;
    }

    public void setCountReview(Integer countReview) {
        this.countReview = countReview;
    }

    public Integer getOrderNum() {
        return orderNum;
    }

    public void setOrderNum(Integer orderNum) {
        this.orderNum = orderNum;
    }
}
