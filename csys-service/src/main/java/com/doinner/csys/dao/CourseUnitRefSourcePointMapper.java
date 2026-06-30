package com.doinner.csys.dao;

import java.util.List;

import com.doinner.csys.domain.CourseUnitRefSourcePoint;
import org.apache.ibatis.annotations.Param;

/**
 * 课程中的知识领域关联知识单元(t_csys_source_unit、t_csys_source_point)Mapper接口
 *
 * @author wzg
 * @date 2026-03-06
 */
public interface CourseUnitRefSourcePointMapper {
    /**
     * 查询课程中的知识领域关联知识单元(t_csys_source_unit、t_csys_source_point)
     *
     * @param unitId 课程中的知识领域关联知识单元(t_csys_source_unit、t_csys_source_point)主键
     * @return 课程中的知识领域关联知识单元(t_csys_source_unit、t_csys_source_point)
     */
     List<CourseUnitRefSourcePoint> selectCourseUnitRefSourcePointByUnitId(Long unitId);

    /**
     * 查询课程中的知识领域关联知识单元(t_csys_source_unit、t_csys_source_point)列表
     *
     * @param courseUnitRefSourcePoint 课程中的知识领域关联知识单元(t_csys_source_unit、t_csys_source_point)
     * @return 课程中的知识领域关联知识单元(t_csys_source_unit、t_csys_source_point)集合
     */
     List<CourseUnitRefSourcePoint> selectCourseUnitRefSourcePointList(CourseUnitRefSourcePoint courseUnitRefSourcePoint);

    /**
     * 新增课程中的知识领域关联知识单元(t_csys_source_unit、t_csys_source_point)
     *
     * @param courseUnitRefSourcePoint 课程中的知识领域关联知识单元(t_csys_source_unit、t_csys_source_point)
     * @return 结果
     */
     int insertCourseUnitRefSourcePoint(CourseUnitRefSourcePoint courseUnitRefSourcePoint);

    /**
     * 修改课程中的知识领域关联知识单元(t_csys_source_unit、t_csys_source_point)
     *
     * @param courseUnitRefSourcePoint 课程中的知识领域关联知识单元(t_csys_source_unit、t_csys_source_point)
     * @return 结果
     */
     int updateCourseUnitRefSourcePoint(CourseUnitRefSourcePoint courseUnitRefSourcePoint);

    /**
     * 删除课程中的知识领域关联知识单元(t_csys_source_unit、t_csys_source_point)
     *
     * @param unitId 课程中的知识领域关联知识单元(t_csys_source_unit、t_csys_source_point)主键
     * @return 结果
     */
     int deleteCourseUnitRefSourcePointByUnitId(Long unitId);

     int deleteCourseUnitRefSourcePointByCourseId(@Param("courseId") Long courseId,
                                                  @Param("courseTargetId") Long courseTargetId,
                                                  @Param("collegeId") Long collegeId,
                                                  @Param("categoryId") Long categoryId,
                                                  @Param("majorId") Long majorId);

    /**
     * 批量删除课程中的知识领域关联知识单元(t_csys_source_unit、t_csys_source_point)
     *
     * @param unitIds 需要删除的数据主键集合
     * @return 结果
     */
     int deleteCourseUnitRefSourcePointByUnitIds(Long[] unitIds);


    int deleteByCourseIds(Long[] courseIds);


    List<CourseUnitRefSourcePoint> selectCourseUnitRefSourcePointByPointIds(Long[] pointIds);

    List<CourseUnitRefSourcePoint> selectCourseTargetRefSourcePointByCourseIds(@Param("courseIds") List<Long> courseIds);

    void insertCourseUnitRefSourcePointList(@Param("courseTargetRefPointList") List<CourseUnitRefSourcePoint> courseTargetRefPointList);
}
