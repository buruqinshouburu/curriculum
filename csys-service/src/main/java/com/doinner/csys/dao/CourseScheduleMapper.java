package com.doinner.csys.dao;

import com.doinner.csys.domain.CourseSchedule;

import java.util.List;

/**
 *
 */
public interface CourseScheduleMapper {

     int insert(CourseSchedule schedule);

     int delete(Long courseId);

     /**
      * 仅删除某课程历史 6/7 学年安排的关联行(保留 1-5 等有效行)。
      */
     int deleteLegacyByCourseId(Long courseId);

     List<CourseSchedule> selectByCourseId(Long courseId);

}
