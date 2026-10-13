package com.doinner.csys.domain.vo;

public class CoursePermissionVo {
    private Long courseId;
    private Long permissionCourseId;
    private String authors;
    private String authorIds;
    private boolean canOperate;
    private boolean canConfigureAuthors;
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
}
