package com.doinner.csys.service;

import com.doinner.csys.domain.StandardMajor;
import com.doinner.csys.domain.TrainingSchemeWeek;
import com.doinner.csys.domain.vo.CourseAndSpecializedVo;
import com.doinner.csys.domain.vo.HourStatisticsVo;
import com.doinner.csys.domain.vo.OverQuoteCourseInfo;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author wzg
 * @date 2023/4/3 10:19
 */
public interface HomePageService {
    List<CourseAndSpecializedVo> courseAndSpecializedStatistics();

    List categoryAndSchedule();

    List<HourStatisticsVo> classHourStatistics(Long collegeId);

    Map<String, Long> countSchemeAndCourse();

    List<StandardMajor> selectMajorBySubCategories(List<Long> categoryIds);

    List<StandardMajor> selectMajorBySystemId(Long systemId);

    List<StandardMajor> selectMajorBySubCategory(Long categoryId);

    List<HourStatisticsVo> classAllHourStatistics();

    Map<String, AtomicInteger> standardTargetWordCloud(Integer limit);

    Map<String, Map<String, AtomicInteger>> standardGraduationWordCloud(Integer limit);

    TrainingSchemeWeek selectWeekBySchemeId(Long schemeId);

    void updateTrainingSchemeWeek(TrainingSchemeWeek trainingSchemeWeek);

    void standardTargetWordCloudExport(HttpServletResponse response);

    void standardGraduationWordCloudExport(HttpServletResponse response);

    Map<String,Object> selectCourseQuoteInfo(String version);

    List<OverQuoteCourseInfo> selectCourseQuoteInfoDetail(List<Long> ids);
}
