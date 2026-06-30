package com.doinner.csys.dao;

import java.util.List;

import com.doinner.csys.domain.CourseRefQuality;
import org.apache.ibatis.annotations.Param;

/**
 * 课程和素质Mapper接口
 *
 * @author wzg
 * @date 2026-03-16
 */
public interface CourseRefQualityMapper {
    /**
     * 查询课程和素质
     *
     * @param courseId 课程和素质主键
     * @return 课程和素质
     */
     List<CourseRefQuality> selectCourseRefQualityByCourseId(Long courseId);

     List<CourseRefQuality> selectCourseRefQualityByCourseTargetId(Long courseTargetId);

     List<CourseRefQuality> selectCourseRefQualityByQualityId(Long qualityId);

    /**
     * 查询课程和素质列表
     *
     * @param courseRefQuality 课程和素质
     * @return 课程和素质集合
     */
     List<CourseRefQuality> selectCourseRefQualityList(CourseRefQuality courseRefQuality);

    /**
     * 新增课程和素质
     *
     * @param courseRefQuality 课程和素质
     * @return 结果
     */
     int insertCourseRefQuality(CourseRefQuality courseRefQuality);

    /**
     * 修改课程和素质
     *
     * @param courseRefQuality 课程和素质
     * @return 结果
     */
     int updateCourseRefQuality(CourseRefQuality courseRefQuality);

    /**
     * 删除课程和素质
     *
     * @param courseId 课程和素质主键
     * @return 结果
     */
     int deleteCourseRefQualityByCourseId(@Param("courseId") Long courseId,
                                          @Param("courseTargetId") Long courseTargetId,
                                          @Param("collegeId") Long collegeId,
                                          @Param("categoryId") Long categoryId,
                                          @Param("majorId") Long majorId);

    /**
     * 批量删除课程和素质
     *
     * @param courseIds 需要删除的数据主键集合
     * @return 结果
     */
     int deleteCourseRefQualityByCourseIds(Long[] courseIds);

    List<CourseRefQuality> selectCourseTargetRefQualityByCourseIds(@Param("courseIds") List<Long> courseIds);

    void insertCourseTargetRefQualityList(@Param("courseTargetRefQualityList") List<CourseRefQuality> courseTargetRefQualityList);
}
