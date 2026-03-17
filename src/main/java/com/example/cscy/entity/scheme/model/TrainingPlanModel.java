package com.example.cscy.entity.scheme.model;

import java.util.List;

public class TrainingPlanModel {
    //培养方案名称
    private String trainingPlanName;
    //培养目标
    private TrainingTargetModel trainingTarget;
    //修业时间与学时学分
    private DurationAndCreditsModel durationAndCredits;
    //毕业要求内容
    private String standardGraduationContent="具有学籍的本科学员，在修业年限内完成本培养方案规定的教学训练，通过各项考核、达成以下毕业要求，依据国防科技大学《高等教育生长军官学员、军士职业技术教育学员学籍管理规定实施细则（暂行）》，颁发毕业证书；依据《国防科技大学学位工作细则（暂行）》，对符合学位授予条件的毕业学员，授予X学士学位。";
    //毕业要求
    private List<StandardGraduationModel> standardGraduations;
    //通识类课程
    private List<TrainingSchemeCourseModel> generalEducationCourses;
    //专业大类课程教学安排
    private List<TrainingSchemeCourseModel>  majorCategoryCourseArrangements;
    //专业方向课程教学安排
    private List<TrainingSchemeCourseModel> majorDirectionCourseArrangements;

    public String getTrainingPlanName() {
        return trainingPlanName;
    }

    public void setTrainingPlanName(String trainingPlanName) {
        this.trainingPlanName = trainingPlanName;
    }

    public TrainingTargetModel getTrainingTarget() {
        return trainingTarget;
    }

    public void setTrainingTarget(TrainingTargetModel trainingTarget) {
        this.trainingTarget = trainingTarget;
    }

    public List<StandardGraduationModel> getStandardGraduations() {
        return standardGraduations;
    }

    public void setStandardGraduations(List<StandardGraduationModel> standardGraduations) {
        this.standardGraduations = standardGraduations;
    }

    public List<TrainingSchemeCourseModel> getGeneralEducationCourses() {
        return generalEducationCourses;
    }

    public void setGeneralEducationCourses(List<TrainingSchemeCourseModel> generalEducationCourses) {
        this.generalEducationCourses = generalEducationCourses;
    }

    public List<TrainingSchemeCourseModel> getMajorCategoryCourseArrangements() {
        return majorCategoryCourseArrangements;
    }

    public void setMajorCategoryCourseArrangements(List<TrainingSchemeCourseModel> majorCategoryCourseArrangements) {
        this.majorCategoryCourseArrangements = majorCategoryCourseArrangements;
    }

    public List<TrainingSchemeCourseModel> getMajorDirectionCourseArrangements() {
        return majorDirectionCourseArrangements;
    }

    public void setMajorDirectionCourseArrangements(List<TrainingSchemeCourseModel> majorDirectionCourseArrangements) {
        this.majorDirectionCourseArrangements = majorDirectionCourseArrangements;
    }

    public String getStandardGraduationContent() {
        return standardGraduationContent;
    }

    public void setStandardGraduationContent(String standardGraduationContent) {
        this.standardGraduationContent = standardGraduationContent;
    }

    public DurationAndCreditsModel getDurationAndCredits() {
        return durationAndCredits;
    }

    public void setDurationAndCredits(DurationAndCreditsModel durationAndCredits) {
        this.durationAndCredits = durationAndCredits;
    }
}
