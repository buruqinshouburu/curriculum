package com.doinner.csys.domain.vo;

public class CourseAuthorsSaveVo {
    /** 第三方 email / sys_user.user_name 帐号，英文逗号分隔；空字符串清空。 */
    private String authorIds;
    /** 与 authorIds 一一对应的第三方 userName（姓名），英文逗号分隔。 */
    private String authors;
    public String getAuthorIds() { return authorIds; }
    public void setAuthorIds(String authorIds) { this.authorIds = authorIds; }
    public String getAuthors() { return authors; }
    public void setAuthors(String authors) { this.authors = authors; }
}
