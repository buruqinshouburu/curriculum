package com.example.cscy.entity.scheme;

import java.util.List;

public class TrainingSchemeVo {
    private String name;
    //培养方案名称
    private String planName;
    //学时信息
    private TotalHours totalHours;
    //毕业标准
    private List<StandardGraduation> standardGraduations;
    //课程安排
    private List<TrainingSchemeCourseVo> courses;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPlanName() {
        return planName;
    }

    public void setPlanName(String planName) {
        this.planName = planName;
    }



    public List<StandardGraduation> getStandardGraduations() {
        return standardGraduations;
    }

    public void setStandardGraduations(List<StandardGraduation> standardGraduations) {
        this.standardGraduations = standardGraduations;
    }

    public List<TrainingSchemeCourseVo> getCourses() {
        return courses;
    }

    public void setCourses(List<TrainingSchemeCourseVo> courses) {
        this.courses = courses;
    }

    public TotalHours getTotalHours() {
        return totalHours;
    }

    public void setTotalHours(TotalHours totalHours) {
        this.totalHours = totalHours;
    }
}
