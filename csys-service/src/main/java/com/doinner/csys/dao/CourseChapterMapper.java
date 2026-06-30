package com.doinner.csys.dao;

import com.doinner.csys.domain.CourseChapter;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 课程章节Mapper接口
 *
 * @author doinner
 * @date 2023-03-14
 */
public interface CourseChapterMapper {
    /**
     * 查询课程章节
     *
     * @param id 课程章节主键
     * @return 课程章节
     */
     CourseChapter selectCourseChapterById(Long id);

    /**
     * 查询课程章节列表
     *
     * @param courseChapter 课程章节
     * @return 课程章节集合
     */
     List<CourseChapter> selectCourseChapterList(CourseChapter courseChapter);

    /**
     * 新增课程章节
     *
     * @param courseChapter 课程章节
     * @return 结果
     */
     int insertCourseChapter(CourseChapter courseChapter);

    /**
     * 批量新增课程章节
     *
     * @param courseChapterList 课程章节
     * @return 结果
     */
     int insertCourseChapters(@Param("courseChapterList") List<? extends CourseChapter> courseChapterList);

    /**
     * 修改课程章节
     *
     * @param courseChapter 课程章节
     * @return 结果
     */
     int updateCourseChapter(CourseChapter courseChapter);

    /**
     * 删除课程章节
     *
     * @param id 课程章节主键
     * @return 结果
     */
     int deleteCourseChapterById(Long id);

    /**
     * 批量删除课程章节
     *
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
     int deleteCourseChapterByIds(List<Long> ids);

    /**
     * 根据课程id删除课程章节
     *
     * @param courseId 课程主键
     * @return 结果
     */
    int deleteCourseChapterByCourseId(Long courseId);

}
