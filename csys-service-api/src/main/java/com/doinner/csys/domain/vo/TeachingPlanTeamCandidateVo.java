package com.doinner.csys.domain.vo;

import java.io.Serializable;

/**
 * 教学计划「教学团队」候选人（来源：课程库的课程负责人 + 课程团队成员）。
 * <p>
 * roleType 只描述该人在课程库中的既有身份，供前端分组展示；不因被教学计划选择而改变。
 */
public class TeachingPlanTeamCandidateVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 统一认证帐号（第三方 email，对应 sys_user.user_name）。 */
    private String userId;
    /** 姓名 */
    private String userName;
    /** 课程库身份：1课程负责人 2课程团队成员 */
    private Integer roleType;
    /** 课程库身份名称 */
    private String roleName;
    /** 是否已在当前教学计划教学团队中 */
    private boolean selected;
    /** 已选中时对应的 t_csys_teaching_plan_teacher.id，未选中为 null */
    private Long teacherRecordId;

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

    public Integer getRoleType() {
        return roleType;
    }

    public void setRoleType(Integer roleType) {
        this.roleType = roleType;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public Long getTeacherRecordId() {
        return teacherRecordId;
    }

    public void setTeacherRecordId(Long teacherRecordId) {
        this.teacherRecordId = teacherRecordId;
    }
}
