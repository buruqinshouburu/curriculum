package com.doinner.csys.domain.vo;

import com.doinner.csys.domain.CourseTeamMember;

import java.util.ArrayList;
import java.util.List;

public class CoursePermissionVo {
    private Long courseId;
    private Long permissionCourseId;
    private String authors;
    private String authorIds;
    private boolean canOperate;
    private boolean canConfigureAuthors;
    /** 课程团队成员明细（调用课程返回总库成员） */
    private List<CourseTeamMember> teamMembers = new ArrayList<>();
    /** 课程团队成员统一认证帐号，英文逗号拼接（与作者 authorIds 同形状） */
    private String teamMemberIds;
    /** 课程团队成员姓名，英文逗号拼接（与作者 authors 同形状） */
    private String teamMemberNames;
    /** 当前用户是否可维护课程团队成员 */
    private boolean canConfigureTeamMembers;
    public Long getCourseId() { return courseId; }
    public void setCourseId(Long courseId) { this.courseId = courseId; }
    public Long getPermissionCourseId() { return permissionCourseId; }
    public void setPermissionCourseId(Long permissionCourseId) { this.permissionCourseId = permissionCourseId; }
    public String getAuthors() { return authors; }
    public void setAuthors(String authors) { this.authors = authors; }
    public String getAuthorIds() { return authorIds; }
    public void setAuthorIds(String authorIds) { this.authorIds = authorIds; }
    public boolean isCanOperate() { return canOperate; }
    public void setCanOperate(boolean canOperate) { this.canOperate = canOperate; }
    public boolean isCanConfigureAuthors() { return canConfigureAuthors; }
    public void setCanConfigureAuthors(boolean canConfigureAuthors) { this.canConfigureAuthors = canConfigureAuthors; }
    public List<CourseTeamMember> getTeamMembers() { return teamMembers; }
    public void setTeamMembers(List<CourseTeamMember> teamMembers) { this.teamMembers = teamMembers; }
    public String getTeamMemberIds() { return teamMemberIds; }
    public void setTeamMemberIds(String teamMemberIds) { this.teamMemberIds = teamMemberIds; }
    public String getTeamMemberNames() { return teamMemberNames; }
    public void setTeamMemberNames(String teamMemberNames) { this.teamMemberNames = teamMemberNames; }
    public boolean isCanConfigureTeamMembers() { return canConfigureTeamMembers; }
    public void setCanConfigureTeamMembers(boolean canConfigureTeamMembers) { this.canConfigureTeamMembers = canConfigureTeamMembers; }
}
