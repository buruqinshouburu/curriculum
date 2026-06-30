package com.doinner.csys.dao;

import com.doinner.csys.domain.CourseRefKeUnit;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 课程与知识单元关联Mapper接口
 *
 * @author doinner
 * @date 2023-03-14
 */
public interface CourseRefKeUnitMapper {
    /**
     * 查询课程与知识单元关联
     *
     * @param courseId 课程与知识单元关联主键
     * @return 课程与知识单元关联
     */
    List<CourseRefKeUnit> selectCourseRefKeUnitByCourseId(Long courseId);

    /**
     * 查询课程与知识单元关联列表
     *
     * @param courseRefKeUnit 课程与知识单元关联
     * @return 课程与知识单元关联集合
     */
    List<CourseRefKeUnit> selectCourseRefKeUnitList(CourseRefKeUnit courseRefKeUnit);

    /**
     * 新增课程与知识单元关联
     *
     * @param courseRefKeUnit 课程与知识单元关联
     * @return 结果
     */
    int insertCourseRefKeUnit(CourseRefKeUnit courseRefKeUnit);

    /**
     * 修改课程与知识单元关联
     *
     * @param courseRefKeUnit 课程与知识单元关联
     * @return 结果
     */
    int updateCourseRefKeUnit(CourseRefKeUnit courseRefKeUnit);

    /**
     * 删除课程与知识单元关联
     *
     * @param courseId 课程与知识单元关联主键
     * @return 结果
     */
    int deleteCourseRefKeUnitByCourseId(Long courseId);

    /**
     * 批量删除课程与知识单元关联
     *
     * @param courseIds 需要删除的数据主键集合
     * @return 结果
     */
    int deleteCourseRefKeUnitByCourseIds(Long[] courseIds);

    /**
     * 删除课程与知识单元关联
     *
     * @param courseId 课程与知识单元关联主键
     * @return 结果
     */
    int deleteCourseRefKeUnitByCourseIdAndUnitId(@Param("courseId") Long courseId, @Param("unitId") Long unitId);

    void insertCourseRefKeUnits(@Param("courseRefUnitList") List<CourseRefKeUnit> courseRefUnitList);

    void deleteCourseRefKeUnitByCourseIdAndUnitIds(@Param("courseId") Long courseId, @Param("unitIds") List<Long> deleteUnitIds);

    List<Long> totalUnitByCourseId(@Param("courseId") Long courseId);

    List<CourseRefKeUnit> selectCourseRefKeUnitByCourseIds(@Param("courseIds")List<Long> courseIds);
}
