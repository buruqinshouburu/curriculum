package com.doinner.csys.domain;

import com.doinner.common.core.domain.db.AbstractDoinnerLogicalDelBaseEntity;

/**
 * 教学计划教员团队实体
 */
public class TeachingPlanTeacher extends AbstractDoinnerLogicalDelBaseEntity {

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 教学计划ID
     */
    private Long planId;

    /**
     * 统一认证帐号：第三方 email，对应 sys_user.user_name；字段名保留兼容。
     */
    private String teacherId;

    /**
     * 教员姓名
     */
    private String teacherName;

    /**
     * 职称
     */
    private String professionalTitle;

    /**
     * 职责
     */
    private String duty;

    /**
     * 主讲内容
     */
    private String lectureContent;

    /**
     * 排序
     */
    private Integer sort;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPlanId() {
        return planId;
    }

    public void setPlanId(Long planId) {
        this.planId = planId;
    }

    public String getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(String teacherId) {
        this.teacherId = teacherId;
    }

    public String getTeacherName() {
        return teacherName;
    }

    public void setTeacherName(String teacherName) {
        this.teacherName = teacherName;
    }

    /** 接受第三方人员原始字段，兼容原 teacherId / teacherName 入参。 */
    public String getEmail() { return teacherId; }
    public void setEmail(String email) { this.teacherId = email; }
    public String getUserName() { return teacherName; }
    public void setUserName(String userName) { this.teacherName = userName; }

    public String getProfessionalTitle() {
        return professionalTitle;
    }

    public void setProfessionalTitle(String professionalTitle) {
        this.professionalTitle = professionalTitle;
    }

    public String getDuty() {
        return duty;
    }

    public void setDuty(String duty) {
        this.duty = duty;
    }

    public String getLectureContent() {
        return lectureContent;
    }

    public void setLectureContent(String lectureContent) {
        this.lectureContent = lectureContent;
    }

    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }
}
