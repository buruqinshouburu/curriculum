package com.doinner.csys.service;

import com.doinner.common.core.domain.Message;
import com.doinner.csys.domain.*;
import com.doinner.csys.domain.vo.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 * 课程Service接口
 *
 * @author doinner
 */
public interface CurriculumService {
    /**
     * 查询课程
     *
     * @param id 课程主键
     * @return 课程
     */
    CourseVo selectCourseById(Long id);

    CourseVo selectCourseByKnowledgeUnitId(Long knowledgeUnitListId);

    List<Course> selectCourseById(List<Long> ids);

    List<CourseVo> selectCourseVoById(List<Long> ids);

    List<CourseVo> selectCourseAndRelevanceList(Course course);

    /**
     * 查询课程列表
     *
     * @param course 课程
     * @return 课程集合
     */
    List<Course> selectCourseList(CourseVo course);

    List<Course> listSchemeCourse(CourseVo course);

    /**
     * 新增课程
     *
     * @param course 课程
     * @return 结果
     */
    CourseVo insertCourse(CourseVo course);

    int insertCourse(List<Course> courseList);

    int deleteFile(Long id);

    /**
     * 修改课程
     *
     * @param course 课程
     * @return 结果
     */
    CourseVo updateCourse(CourseVo course);

    CourseTarget courseTargetConfiguration(CourseTarget courseTarget);

    /**
     * 批量删除课程
     *
     * @param ids 需要删除的课程主键集合
     * @return 结果
     */
    /**
     * 修改课程详细信息
     *
     * @param course 课程
     * @return 结果
     */
    CourseVo saveCourse(CourseVo course);


    /**
     * 新增理论教学计划
     *
     * @param courseTeachingTheoryPlan 理论教学计划
     * @return 结果
     */
    CourseTeachingTheoryPlan insertCourseTeachingTheoryPlan(CourseTeachingTheoryPlan courseTeachingTheoryPlan);

    /**
     * 修改理论教学计划
     *
     * @param courseTeachingTheoryPlan 理论教学计划
     * @return 结果
     */
    CourseTeachingTheoryPlan updateCourseTeachingTheoryPlan(CourseTeachingTheoryPlan courseTeachingTheoryPlan);

    /**
     * 批量删除理论教学计划
     *
     * @param ids 需要删除的理论教学计划主键集合
     * @return 结果
     */
    int deleteCourseTeachingTheoryPlanByIds(Long[] ids);


    /**
     * 新增实践教学计划
     *
     * @param courseTeachingPracticePlan 实践教学计划
     * @return 结果
     */
    CourseTeachingPracticePlan insertCourseTeachingPracticePlan(CourseTeachingPracticePlan courseTeachingPracticePlan);

    /**
     * 修改实践教学计划
     *
     * @param courseTeachingPracticePlan 实践教学计划
     * @return 结果
     */
    CourseTeachingPracticePlan updateCourseTeachingPracticePlan(CourseTeachingPracticePlan courseTeachingPracticePlan);

    /**
     * 批量删除实践教学计划
     *
     * @param ids 需要删除的实践教学计划主键集合
     * @return 结果
     */
    int deleteCourseTeachingPracticePlanByIds(Long[] ids);


    /**
     * 新增课程章节
     *
     * @param courseChapter 课程章节
     * @return 结果
     */
    CourseChapter insertCourseChapter(CourseChapter courseChapter);

    /**
     * 修改课程章节
     *
     * @param courseChapter 课程章节
     * @return 结果
     */
    CourseChapter updateCourseChapter(CourseChapter courseChapter);


    /**
     * 新增知识点
     *
     * @param knowledgePoint 知识点
     * @return 结果
     */
    KnowledgePoint insertKnowledgePoint(KnowledgePointVo knowledgePoint);

    /**
     * 修改知识点
     *
     * @param knowledgePoint 知识点
     * @return 结果
     */
    KnowledgePoint updateKnowledgePoint(KnowledgePoint knowledgePoint);


    /**
     * 新增知识单元
     *
     * @param knowledgeUnit 知识单元
     * @return 结果
     */
    KnowledgeUnit insertKnowledgeUnit(KnowledgeUnitVo knowledgeUnit);

    /**
     * 修改知识单元
     *
     * @param knowledgeUnit 知识单元
     * @return 结果
     */
    KnowledgeUnit updateKnowledgeUnit(KnowledgeUnit knowledgeUnit);

    /**
     * 删除知识单元
     *
     * @param knowledgeUnit 知识单元
     * @return 结果
     */
    int deleteUnitById(KnowledgeUnitVo knowledgeUnit);


    /**
     * 新增课程与知识单元关联
     *
     * @param courseRefKeUnit 课程与知识单元关联
     * @return 结果
     */
    CourseRefKeUnit insertCourseRefKeUnit(CourseRefKeUnit courseRefKeUnit);


    /**
     * 删除课程与知识单元关联信息
     *
     * @param courseId 课程与知识单元关联主键
     * @return 结果
     */
    int deleteCourseRefKeUnitByCourseIdAndUnitId(Long courseId, Long unitId);

    /**
     * 新增知识单元与知识点关联
     *
     * @param knowledgeUnitRefPoint 知识单元与知识点关联
     * @return 结果
     */
    KnowledgeUnitRefPoint insertKnowledgeUnitRefPoint(KnowledgeUnitRefPoint knowledgeUnitRefPoint);

    /**
     * 删除知识单元与知识点关联信息
     *
     * @param unitId 知识单元与知识点关联主键
     * @return 结果
     */
    int deleteKnowledgeUnitRefPointByUnitIdAndPointId(Long unitId, Long pointId);


    /**
     * 新增课程教材
     *
     * @param courseTextbook 课程教材
     * @return 结果
     */
    CourseTextbook insertCourseTextbook(CourseTextbook courseTextbook);

    /**
     * 修改课程教材
     *
     * @param courseTextbook 课程教材
     * @return 结果
     */
    CourseTextbook updateCourseTextbook(CourseTextbook courseTextbook);


    int deleteCourseByIds(List<Long> ids);


    List<KnowledgeUnit> selectKnowledgeUnitListByCourseId(Long courseId);


    List<KnowledgePoint> selectKnowledgePointByUnitId(Long unitId);


    List<KnowledgeViewVo> selectKnowledgeTreeByCourseId(Long courseId);


    Map courseStatistics(Long schemeId);

    List<TrainingSchemeCourseScheduleRankingVo> courseRanking();

    List courseSelectStatistics(String courseName,List<Integer> types);

    Map  countCollegeCourse();

    /**
     * 新增知识领域
     *
     * @param knowledgeDomain 知识领域
     * @return 结果
     */
    KnowledgeDomain insertKnowledgeDomain(KnowledgeDomain knowledgeDomain);

    /**
     * 修改知识领域
     *
     * @param knowledgeDomain 知识领域
     * @return 结果
     */
    KnowledgeDomain updateKnowledgeDomain(KnowledgeDomain knowledgeDomain);

    /**
     * 删除知识领域
     *
     * @param id 知识领域id
     * @return 结果
     */
    int deleteDomainById(Long id);

    /**
     * 修改文件路径
     * @param fileId
     * @param fileName
     * @param id
     */
    void updateFileById(String fileId,String fileName,Long id);

    void updateStatusByIds(List<Long> ids);

    void deleteKnowledgePointByIds(List<Long> ids);

    List<CourseReview> selectReview(CourseReview courseReview);

    CourseReview reviewById(Long id);

    CourseVo fallbackReviewById(Long id,Long courseId);

    void deleteReview(Long id);



    void exportCourseTemplate(HttpServletResponse response);

    Message importCourse(MultipartFile file, String courseModule, String courseModuleChildren, Long majorId, String type, Integer templateType, String version, Long categoryId,Long subMajorId);

    void exportCourse(HttpServletResponse response, List<Long> ids);




    void exportTrainingTemplate(HttpServletResponse response);

    Message importTraining(MultipartFile file,Long majorId,String type,Integer templateType,String version);

    void exportTraining(HttpServletResponse response, List<Long> ids);



    void exportPracticeTemplate(HttpServletResponse response);

    Message importPractice(MultipartFile file, Long majorId, String type, Integer templateType, String version, Long categoryId, Long subMajorId);

    void exportPractice(HttpServletResponse response, List<Long> ids);


    void insertCourseByTemplate(CourseTemplateVo courseTemplateVo);
    void insertCourseByTemplate_new(CourseTemplateVo courseTemplateVo);


    TreeVo courseTreeById(Long id,Integer type);


    TreeVo courseTreeByMajorId(Long majorId,Integer type,String courseType);

    Map getSchemeCourseInfo(Long schemeId);

    List<Course> checkSchemeCourse(TrainingCourseVo trainingCourseVo);
}
