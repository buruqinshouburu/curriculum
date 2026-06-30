package com.doinner.csys.dao;

import com.doinner.csys.domain.CourseReview;
import org.apache.ibatis.annotations.Param;

import java.util.List;


/**
 * 课程教学计划审核历史Mapper接口
 *
 * @author doinner
 * @date 2023-03-27
 */
public interface CourseReviewMapper
{
    /**
     * 查询课程教学计划审核历史
     *
     * @param id 课程教学计划审核历史主键
     * @return 课程教学计划审核历史
     */
    CourseReview selectCourseReviewById(Long id);

    /**
     * 查询课程教学计划审核历史列表
     *
     * @param courseReview 课程教学计划审核历史
     * @return 课程教学计划审核历史集合
     */
    List<CourseReview> selectCourseReviewList(CourseReview courseReview);

    /**
     * 新增课程教学计划审核历史
     *
     * @param courseReview 课程教学计划审核历史
     * @return 结果
     */
    int insertCourseReview(CourseReview courseReview);

    /**
     * 修改课程教学计划审核历史
     *
     * @param courseReview 课程教学计划审核历史
     * @return 结果
     */
    int updateCourseReview(CourseReview courseReview);

    /**
     * 删除课程教学计划审核历史
     *
     * @param id 课程教学计划审核历史主键
     * @return 结果
     */
    int deleteCourseReviewById(Long id);

    /**
     * 批量删除课程教学计划审核历史
     *
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    int deleteCourseReviewByIds(Long[] ids);


    int insertReview(@Param("reviewList") List<CourseReview> reviewList);
}
