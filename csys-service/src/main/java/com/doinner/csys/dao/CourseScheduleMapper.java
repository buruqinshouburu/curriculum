package com.doinner.csys.dao;

import com.doinner.csys.domain.CourseSchedule;

import java.util.List;

/**
 *
 */
public interface CourseScheduleMapper {

     int insert(CourseSchedule schedule);

     int delete(Long courseId);

     List<CourseSchedule> selectByCourseId(Long courseId);

}
