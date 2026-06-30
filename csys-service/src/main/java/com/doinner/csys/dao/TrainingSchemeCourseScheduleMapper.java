package com.doinner.csys.dao;

import com.doinner.csys.domain.TrainingSchemeCourseSchedule;
import com.doinner.csys.domain.statisticsVo.StandardCultivationTargetStatisticsVo;
import com.doinner.csys.domain.statisticsVo.TrainingSchemeCourseScheduleStatisticsVo;
import com.doinner.csys.domain.vo.CourseChooseStatusVo;
import com.doinner.csys.domain.vo.TrainingSchemeCourseScheduleVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 培养方案排课Mapper接口
 *
 * @author doinner
 */
public interface TrainingSchemeCourseScheduleMapper {
    /**
     * 查询培养方案排课
     *
     * @param id 培养方案排课主键
     * @return 培养方案排课
     */
    TrainingSchemeCourseSchedule selectTrainingSchemeCourseScheduleById(Long id);

    /**
     * 查询培养方案排课列表
     *
     * @param trainingSchemeCourseSchedule 培养方案排课
     * @return 培养方案排课集合
     */
    List<TrainingSchemeCourseScheduleVo> selectTrainingSchemeCourseScheduleList(TrainingSchemeCourseSchedule trainingSchemeCourseSchedule);

    /**
     * 新增培养方案排课
     *
     * @param trainingSchemeCourseSchedule 培养方案排课
     * @return 结果
     */
    int insertTrainingSchemeCourseSchedule(TrainingSchemeCourseSchedule trainingSchemeCourseSchedule);

    /**
     * 批量新增培养方案排课
     *
     * @param trainingSchemeCourseSchedules 培养方案排课
     * @return 结果
     */
    int insertTrainingSchemeCourseSchedules(@Param("trainingSchemeCourseSchedules") List<TrainingSchemeCourseSchedule> trainingSchemeCourseSchedules);

    /**
     * 修改培养方案排课
     *
     * @param trainingSchemeCourseSchedule 培养方案排课
     * @return 结果
     */
    int updateTrainingSchemeCourseSchedule(TrainingSchemeCourseSchedule trainingSchemeCourseSchedule);

    /**
     * 删除培养方案排课
     *
     * @param id 培养方案排课主键
     * @return 结果
     */
    int deleteTrainingSchemeCourseScheduleById(Long id);

    /**
     * 批量删除培养方案排课
     *
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    int deleteTrainingSchemeCourseScheduleByIds(Long[] ids);

    void deleteTrainingSchemeCourseScheduleByTrainingSchemeId(Long trainingSchemeId);

    void deleteTrainingSchemeCourseScheduleByTrainingSchemeIds(Long[] schemeIds);

    List<TrainingSchemeCourseScheduleStatisticsVo> selectHoursBySchemeId(@Param("schemeId") Long schemeId);

    List<TrainingSchemeCourseScheduleStatisticsVo> selectHoursBySchemeIds(@Param("schemeIds") List<Long> schemeIds);

    List<StandardCultivationTargetStatisticsVo> selectCourseTypeBySchemeId(@Param("schemeId") Long schemeId);

    List<StandardCultivationTargetStatisticsVo> selectCourseTypeBySchemeIds(@Param("schemeIds") List<Long> schemeIds);

    int deleteByCourseIds(@Param("ids") List<Long> ids);

    /**
     * 查询课程被选用情况（用于"课程被选用情况表"导出）。
     * 入参为源课程ID集合，通过 source_id 定位被选用课程(被各培养方案引用排课的课程实例)，
     * 选用单位/专业类/专业取自被选用课程自身字段。
     *
     * @param sourceCourseIds 源课程id集合
     * @return 课程被选用情况VO集合
     */
    List<CourseChooseStatusVo> selectCourseChooseStatus(@Param("sourceCourseIds") List<Long> sourceCourseIds);
}
