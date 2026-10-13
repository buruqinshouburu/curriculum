package com.doinner.csys.domain.vo;

public class CourseAuthorsSaveVo {
    /** Comma-separated sys_user.user_id values; empty string explicitly clears the list. */
    private String authorIds;
    public String getAuthorIds() { return authorIds; }
    public void setAuthorIds(String authorIds) { this.authorIds = authorIds; }
}
