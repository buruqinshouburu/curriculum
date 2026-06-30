package com.doinner.csys.service;

import com.doinner.csys.domain.TrainingScheme;
import com.doinner.csys.domain.statisticsVo.*;

import java.util.List;

public interface SchemeStatisticsService {
    List<TrainingScheme> schemeSub(Long majorId);


    List<CourseTypeVo> courseType(Long schemeId);

    List<CreditStaticticsVo> statisticsCredit(Long schemeId);

    //统计培养目标支撑度
    List<StandardCultivationTargetStatisticsVo> selectTargetPropUpBySchemeId(Long schemeId);

    //讲授，实践课程比例
    List<TrainingSchemeCourseScheduleStatisticsVo> selectHoursBySchemeId(Long schemeId);

    //课程比例分配图
    List<StandardCultivationTargetStatisticsVo> selectCourseTypeBySchemeId(Long schemeId) throws Exception;

    List<StatisticsExcelMultiVo> statisticsCreditIn(List<Long> schemeIds);

    List<StatisticsExcelMultiVo> courseTypeIn(List<Long> schemeIds);

    List<StatisticsExcelMultiVo> selectHoursBySchemeIdIn(List<Long> schemeIds);

    List<StandardCultivationTargetStatisticsMultiVo> selectCourseTypeBySchemeIdIn(List<Long> schemeIds);
}
