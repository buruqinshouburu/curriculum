package com.doinner.csys.domain.vo;

/** 第三方人员：email 为统一认证帐号，userName 为姓名。 */
public class CoursePersonVo {
    private String email;
    private String userName;
    private String unitName;
    public CoursePersonVo() {}
    public CoursePersonVo(String email, String userName) { this.email = email; this.userName = userName; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }
    public String getUnitName() { return unitName; }
    public void setUnitName(String unitName) { this.unitName = unitName; }
}
