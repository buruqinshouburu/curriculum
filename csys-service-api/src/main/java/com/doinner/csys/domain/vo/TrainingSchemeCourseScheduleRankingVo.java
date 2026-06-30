package com.doinner.csys.domain.vo;


import java.util.List;

/**
 * @author wzg
 * @date 2023/3/23 15:43
 */
public class TrainingSchemeCourseScheduleRankingVo {

    private Long courseId;
    private Long selectedNum;
    private String courseName;
    private List<TrainingSchemeCourseScheduleVo> trainingSchemeCourseScheduleList;
    private List<TrainingSchemeScheduleVo> trainingSchemeList;

    public List<TrainingSchemeScheduleVo> getTrainingSchemeList() {
        return trainingSchemeList;
    }

    public void setTrainingSchemeList(List<TrainingSchemeScheduleVo> trainingSchemeList) {
        this.trainingSchemeList = trainingSchemeList;
    }

    public List<TrainingSchemeCourseScheduleVo> getTrainingSchemeCourseScheduleList() {
        return trainingSchemeCourseScheduleList;
    }

    public void setTrainingSchemeCourseScheduleList(List<TrainingSchemeCourseScheduleVo> trainingSchemeCourseScheduleList) {
        this.trainingSchemeCourseScheduleList = trainingSchemeCourseScheduleList;
    }

    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

    public Long getSelectedNum() {
        return selectedNum;
    }

    public void setSelectedNum(Long selectedNum) {
        this.selectedNum = selectedNum;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }
}
