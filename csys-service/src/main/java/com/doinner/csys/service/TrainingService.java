package com.doinner.csys.service;

import com.doinner.common.core.domain.DataSet;
import com.doinner.common.core.domain.Message;
import com.doinner.csys.domain.*;
import com.doinner.csys.domain.vo.*;

import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

public interface TrainingService {
    List<TrainingSchemeCourseVo> selectTrainingSchemeCoursesById(Long id);
    TrainingSchemeVo selectTrainingSchemeById(Long id);
    List<TrainingSchemeVo> selectTrainingSchemeList(TrainingScheme trainingScheme);
    List<TrainingScheme> selectTrainingSchemeCategoryList(TrainingScheme trainingScheme);
    List<TrainingScheme> listTrainingSchemesLook(TrainingScheme trainingScheme);
    List<TrainingSchemeVo> selectTrainingSchemeVoCategoryList(TrainingScheme trainingScheme);
    DataSet insertTrainingScheme(TrainingSchemeVo trainingSchemeVo);
    TrainingScheme updateTrainingScheme(TrainingSchemeVo trainingSchemeVo);
    TrainingScheme updateTrainingScheme(TrainingScheme trainingScheme);
    TrainingScheme createTrainingScheme(Long id);
    int deleteTrainingSchemeById(Long id);
    Message deleteTrainingSchemeByIds( List<Long> ids);
    TrainingSchemeVo viewTrainingScheme(Long id, Long type);
    TrainingSchemeCategory insertTrainingSchemeCategory(TrainingSchemeCategory trainingSchemeCategory);
    int deleteTrainingSchemeCategoryById(Long id);
    int deleteTrainingSchemeCategoryByIds(Long[] ids);
    TrainingSchemeCategory updateTrainingSchemeCategory(TrainingSchemeCategory trainingSchemeCategory);
    List<TrainingSchemeCategory> selectTrainingSchemeCategoryList(TrainingSchemeCategory trainingSchemeCategory);
    TrainingSchemeCourseSchedule insertTrainingSchemeCourseSchedule(TrainingSchemeCourseSchedule trainingSchemeCourseSchedule);
    int insertTrainingSchemeCourseSchedules(List<TrainingSchemeCourseSchedule> trainingSchemeCourseSchedules);
    int deleteTrainingSchemeCourseScheduleById(Long id);
    TrainingSchemeCourseSchedule updateTrainingSchemeCourseSchedule(TrainingSchemeCourseSchedule trainingSchemeCourseSchedule);
    TrainingSchemeCourseSchedule selectTrainingSchemeCourseScheduleById(Long id);
    List<TrainingSchemeCourseScheduleVo> selectTrainingSchemeCourseScheduleList(TrainingSchemeCourseSchedule trainingSchemeCourseSchedule);
    List<CollegeVo> selectCollegeList(CollegeVo collegeVo);
    byte[] getWordByte(CourseVo courseVo, String courseType);
    byte[] getWordBytes( List<CourseVo> courseVos);
    Map<String, Object> replacePlaceholder(List<CourseVo> courseVos);
    void process(OutputStream outputStream, List<CourseVo> courseVos);
    int planDispose(TowerToTower towerToTower);
    int editCultivation(Long schemeId);
    Long selectTargetId(Long id);
    void insertTrainingSchemeRefCourses(Long trainingSchemeId, List<TrainingSchemeRefCourse> trainingSchemeRefCourseList);
    void insertTrainingSchemeCourseSchedules(Long trainingSchemeId, List<TrainingSchemeCourseSchedule> trainingSchemeCourseScheduleList);
    List<KnowledgeUnitRefStdCultivation> selectKnowledgeUnitRefStdCultivationsByKnowledgeUnitIds(List<Long> knowledgeUnitIds);
    List<TrainingSchemeStandardCultivationVo> selectCultivation(Long schemeId, Long courseId, Long unitId);
    List<TrainingSchemeStandardCultivationVo> selectStandardCultivationVoAll(Long cultivationId);
    List<TrainingSchemeStandardCultivationTargetVo> selectStandardCultivationTargetVoAll(Long cultivationTargetId);
    List<CourseVo> selectCourseVosByIds(List<Long> ids);
    StandardMajor selectStandardMajorById(Long id);

    List<CollegeSchemeVo> selectSchemeCollegeVoList();

    void deleteTrainingSchemeIndex(Long id);

    TrainingSchemeVo createTrainingPlanWord(Long schemeId);

    void exportTrainingCourse(HttpServletResponse response, Long schemeId, Long subMajorId);

    Message BoundTrainingCourse(TrainingBoundCourseVo TrainingBoundCourseVo);

    List<TrainingSchemeCourseVo> viewTrainingCourseKnowLedge(Long schemeId);

    void setCourseSchedule(TrainingSchemeVo trainingSchemeVo, List<Course> courses);

    List<TrainingSchemeCourseVo> viewTrainingCourseGraduation(Long schemeId, String type);

    void updateSchedules(Long schemeId);

    void updateCourseSchedule(Long courseId, Long schemeId);
}
