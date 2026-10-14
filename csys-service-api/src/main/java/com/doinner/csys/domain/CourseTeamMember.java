package com.doinner.csys.domain;

import java.io.Serializable;

/**
 * 课程库团队成员 t_csys_course_team_member。
 * <p>
 * 与「课程负责人」（t_csys_course.authors / author_ids）是两个独立业务身份，互不覆盖：
 * 同一帐号可同时保留两个身份；两者都为空时创建人可编辑并初始化人员名单。
 */
public class CourseTeamMember implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    /** 课程ID，始终指向总库课程 t_csys_course.id */
    private Long courseId;
    /** 统一认证帐号：第三方 email，对应 sys_user.user_name；不要求已注册。 */
    private String userId;
    /** 成员姓名快照 */
    private String userName;
    private Integer sort;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEmail() { return userId; }
    public void setEmail(String email) { this.userId = email; }

    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }
}
