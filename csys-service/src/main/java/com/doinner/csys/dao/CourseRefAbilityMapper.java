package com.doinner.csys.dao;

import java.util.List;

import com.doinner.csys.domain.CourseRefAbility;
import org.apache.ibatis.annotations.Param;

/**
 * 课程和能力Mapper接口
 *
 * @author wzg
 * @date 2026-03-16
 */
public interface CourseRefAbilityMapper {
    /**
     * 查询课程和能力
     *
     * @param courseId 课程和能力主键
     * @return 课程和能力
     */
     List<CourseRefAbility> selectCourseRefAbilityByCourseId(Long courseId);

     List<CourseRefAbility> selectCourseRefAbilityByCourseTargetId(Long courseTargetId);

     List<CourseRefAbility> selectCourseRefAbilityByAbilityId(Long abilityId);

    /**
     * 查询课程和能力列表
     *
     * @param courseRefAbility 课程和能力
     * @return 课程和能力集合
     */
     List<CourseRefAbility> selectCourseRefAbilityList(CourseRefAbility courseRefAbility);

    /**
     * 新增课程和能力
     *
     * @param courseRefAbility 课程和能力
     * @return 结果
     */
     int insertCourseRefAbility(CourseRefAbility courseRefAbility);

    /**
     * 修改课程和能力
     *
     * @param courseRefAbility 课程和能力
     * @return 结果
     */
     int updateCourseRefAbility(CourseRefAbility courseRefAbility);

    /**
     * 删除课程和能力
     *
     * @param courseId 课程和能力主键
     * @return 结果
     */
     int deleteCourseRefAbilityByCourseId(@Param("courseId") Long courseId,
                                          @Param("courseTargetId") Long courseTargetId,
                                          @Param("collegeId") Long collegeId,
                                          @Param("categoryId") Long categoryId,
                                          @Param("majorId") Long majorId);

    /**
     * 批量删除课程和能力
     *
     * @param courseIds 需要删除的数据主键集合
     * @return 结果
     */
     int deleteCourseRefAbilityByCourseIds(Long[] courseIds);

    List<CourseRefAbility> selectCourseTargetRefAbilityByCourseIds(@Param("courseIds") List<Long> courseIds);

    void insertCourseTargetRefAbilityList(@Param("courseTargetRefAbilityList") List<CourseRefAbility>courseTargetRefAbilityList);
}
