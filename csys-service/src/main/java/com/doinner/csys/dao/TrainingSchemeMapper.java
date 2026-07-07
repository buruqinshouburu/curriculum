package com.doinner.csys.dao;

import com.doinner.csys.domain.TrainingScheme;
import com.doinner.csys.domain.statisticsVo.CourseTypeVo;
import com.doinner.csys.domain.statisticsVo.CreditStaticticsVo;
import com.doinner.csys.domain.vo.*;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 培养方案Mapper接口
 *
 * @author doinner
 */
public interface TrainingSchemeMapper {

    /**
     * 查询培养方案中关联的课程
     *
     * @param id 培养方案主键
     * @return 课程列表
     */
    List<TrainingSchemeCourseVo> selectTrainingSchemeCoursesById(Long id);
    /**
     * 查询培养方案
     *
     * @param id 培养方案主键
     * @return 培养方案
     */
    TrainingSchemeVo selectTrainingSchemeById(Long id);

    List<TrainingSchemeVo> selectTrainingSchemeByIds(@Param("ids") List<Long> ids);

    /**
     * 查询培养方案列表
     *
     * @param trainingScheme 培养方案
     * @return 培养方案集合
     */
    List<TrainingSchemeVo> selectTrainingSchemeVoList(TrainingScheme trainingScheme);

    List<TrainingSchemeVo> selectTrainingSchemeVoCategoryList(TrainingScheme trainingScheme);

    List<TrainingScheme> selectTrainingSchemeList(TrainingScheme trainingScheme);

    /**
     * 查询培养方案带门类id
     *
     * @param trainingScheme 培养方案
     * @return 培养方案集合
     */
    List<TrainingScheme> selectTrainingSchemeCategoryList(TrainingScheme trainingScheme);

    /**
     * 新增培养方案
     *
     * @param trainingScheme 培养方案
     * @return 结果
     */
    int insertTrainingScheme(TrainingScheme trainingScheme);

    /**
     * 修改培养方案
     *
     * @param trainingScheme 培养方案
     * @return 结果
     */
    int updateTrainingScheme(TrainingScheme trainingScheme);

    /**
     * 删除培养方案
     *
     * @param id 培养方案主键
     * @return 结果
     */
    int deleteTrainingSchemeById(Long id);

    /**
     * 批量删除培养方案
     *
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    int deleteTrainingSchemeByIds(Long[] ids);

    TrainingSchemeVo selectTrainingSchemeCoursesAllById(Long id);

    Long selectTargetId(Long id);

    /** 逻辑删除 */
    int deleteTrainingScheme(Long id);

    /** 逻辑删除 */
    int deleteTrainingSchemes(Long[] ids);

    List<CollegeVo> selectCollegeList(CollegeVo collegeVo);

    /** 关联表查询培养标准 */
    List<TrainingSchemeStandardCultivationVo> selectCultivation(@Param("schemeId") Long schemeId,@Param("courseId") Long courseId,@Param("unitId") Long unitId);

    /** 关联表查询培养标准及关联的知识单元 */
    List<TrainingSchemeStandardCultivationVo> selectStandardCultivationVoAll(Long id);

    /** 关联表查询培养目标及关联的知识单元 */
    List<TrainingSchemeStandardCultivationTargetVo> selectStandardCultivationTargetVoAll(Long id);

    List<CourseVo> selectCourseAndRelevanceByIds(@Param("ids") List<Long> ids);

    List<CourseVo> selectCourseByIds(@Param("ids") List<Long> ids);

    List<SchemeCountVo> schemeCount();

    Long firstLevelPowerCountNum(Long id);
    Long secondLevelPowerCountNum(Long id);
    Long thirdLevelPowerCountNum(Long id);

    List<TrainingSchemeScheduleVo> selectTrainingSchemeListByCourseId(@Param("courseId")Long courseId,@Param("types")List<Integer> types);

    List<CreditStaticticsVo> sumCreditBySchemeId(Long schemeId);

    List<CreditStaticticsVo> sumCreditBySchemeIds(@Param("schemeIds") List<Long> schemeIds);

    List<CourseTypeVo> countCourseByType(Long schemeId);

    List<CourseTypeVo> countCourseByTypes(@Param("schemeIds") List<Long> schemeIds);

    Long selectSchemeCountByCategoryIds(List<Long> categoryIds);

    List<HourStatisticsVo> classHourStatistics(Long majorId);
    HourStatisticsVo classHourStatisticsList(@Param("majorIds") List<Long> majorIds);

    Long countScheme();

    List<HourStatisticsVo> countAndSumHoursGroupByCollege();

    List<HourStatisticsVo> countAndSumHoursByCollegeId(Long collegeId);

    Long countMajorHasScheme();

    List<TrainingScheme> selectTrainingSchemeListAccurate(TrainingScheme trainingScheme);

    List<CollegeSchemeVo> selectSchemeCollegeVoList();

    void deleteTrainingSchemeIndex(@Param("id") Long id);

    void updateTrainingSchemeFileId(@Param("trainingSchemeVo") TrainingSchemeVo trainingSchemeVo);

    List<TrainingSchemeVo> selectTrainingSchemeVoByIds(@Param("trainingSchemeIds")List<Long> trainingSchemeIds, @Param("order") String order, @Param("databaseProp")String databaseProp);

    List<TrainingSchemeCourseVo> selectTrainingSchemeCoursesByIdAndsubMajorId(@Param("id") Long id, @Param("subMajorId") Long subMajorId);
}
