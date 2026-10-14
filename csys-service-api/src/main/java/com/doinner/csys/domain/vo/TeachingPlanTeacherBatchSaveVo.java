package com.doinner.csys.domain.vo;

import com.doinner.csys.domain.TeachingPlanTeacher;

import java.util.List;

/**
 * 教学计划教学团队批量保存入参。
 * <p>
 * teacherId/email 传统一认证帐号，teacherName/userName 传第三方姓名；不要求已注册。
 * 保存后：写入当前教学计划教学团队，并同步并入课程库团队成员；不会改动课程负责人。
 */
public class TeachingPlanTeacherBatchSaveVo {

    private Long planId;
    private List<TeachingPlanTeacher> teachers;

    public Long getPlanId() {
        return planId;
    }

    public void setPlanId(Long planId) {
        this.planId = planId;
    }

    public List<TeachingPlanTeacher> getTeachers() {
        return teachers;
    }

    public void setTeachers(List<TeachingPlanTeacher> teachers) {
        this.teachers = teachers;
    }
}
