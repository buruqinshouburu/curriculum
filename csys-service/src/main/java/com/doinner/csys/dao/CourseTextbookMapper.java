package com.doinner.csys.dao;

import com.doinner.csys.domain.CourseTextbook;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 课程教材Mapper接口
 *
 * @author doinner
 * @date 2023-03-15
 */
public interface CourseTextbookMapper
{
    /**
     * 查询课程教材
     *
     * @param id 课程教材主键
     * @return 课程教材
     */
    CourseTextbook selectCourseTextbookById(Long id);

    /**
     * 查询课程教材列表
     *
     * @param courseTextbook 课程教材
     * @return 课程教材集合
     */
    List<CourseTextbook> selectCourseTextbookList(CourseTextbook courseTextbook);

    /**
     * 新增课程教材
     *
     * @param courseTextbook 课程教材
     * @return 结果
     */
    int insertCourseTextbook(CourseTextbook courseTextbook);

    /**
     * 批量新增课程教材
     *
     * @param courseTextbooks 课程教材
     * @return 结果
     */
    int insertCourseTextbooks(@Param("courseTextbookList") List<? extends CourseTextbook> courseTextbooks);

    /**
     * 修改课程教材
     *
     * @param courseTextbook 课程教材
     * @return 结果
     */
    int updateCourseTextbook(CourseTextbook courseTextbook);

    /**
     * 删除课程教材
     *
     * @param id 课程教材主键
     * @return 结果
     */
    int deleteCourseTextbookById(Long id);

    /**
     * 批量删除课程教材
     *
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    int deleteCourseTextbookByIds(List<Long> ids);

    /**
     * 根据课程id删除课程教材
     *
     * @param courseId 课程主键
     * @return 结果
     */
    int deleteCourseTextbookByCourseId(Long courseId);
}
