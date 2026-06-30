package com.doinner.csys.dao;

import com.doinner.csys.domain.CourseTarget;
import org.apache.ibatis.annotations.Param;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/**
 * 课程目标
 */
public interface CourseTargetMapper {

    void insertCourseTarget(CourseTarget courseTarget);

    void updateCourseTarget(CourseTarget courseTarget);

    List<CourseTarget> selectCourseTargetByCourseId(Long courseId);


    CourseTarget selectCourseTargetById(Long courseId);


    void deleteCourseTargetByCourseId(Long courseId);

    void deleteCourseTargetById(Long id);

    void deleteCourseTargetByIds(@Param("courseTargetIds") List<Long> courseTargetIds);

    List<CourseTarget> selectCourseTargetByCourseIds(@Param("baseCourseIds")List<Long> baseCourseIds);

    List<CourseTarget> selectTargetNotInGraduationByTargetIds(@Param("targetIds")List<Long> targetIds);

    List<CourseTarget> selectTargetNotInKnowLedgeByTargetIds(@Param("targetIds")List<Long> targetIds);

    int selectBoundGraduationByCourseId(@Param("courseIds")List<Long> courseIds);

    void deleteCourseTargetByCourseIds(@Param("courseIds")List<Long> ids);

    void insertCourseTargets(@Param("targetList") List<CourseTarget> targetList);
}
