package com.doinner.csys.dao;

import com.doinner.csys.domain.CourseTeachingPracticePlan;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 实践教学计划Mapper接口
 *
 * @author doinner
 * @date 2023-03-14
 */
public interface CourseTeachingPracticePlanMapper {
    /**
     * 查询实践教学计划
     *
     * @param id 实践教学计划主键
     * @return 实践教学计划
     */
    CourseTeachingPracticePlan selectCourseTeachingPracticePlanById(Long id);

    /**
     * 根据课程id查询实践教学计划
     *
     * @param courseId 实践教学计划主键
     * @return 实践教学计划
     */
    CourseTeachingPracticePlan selectCourseTeachingPracticePlanByCourseId(Long courseId);

    /**
     * 查询实践教学计划列表
     *
     * @param courseTeachingPracticePlan 实践教学计划
     * @return 实践教学计划集合
     */
    List<CourseTeachingPracticePlan> selectCourseTeachingPracticePlanList(CourseTeachingPracticePlan courseTeachingPracticePlan);

    /**
     * 新增实践教学计划
     *
     * @param courseTeachingPracticePlan 实践教学计划
     * @return 结果
     */
    int insertCourseTeachingPracticePlan(CourseTeachingPracticePlan courseTeachingPracticePlan);

    /**
     * 修改实践教学计划
     *
     * @param courseTeachingPracticePlan 实践教学计划
     * @return 结果
     */
    int updateCourseTeachingPracticePlan(CourseTeachingPracticePlan courseTeachingPracticePlan);

    /**
     * 删除实践教学计划
     *
     * @param id 实践教学计划主键
     * @return 结果
     */
    int deleteCourseTeachingPracticePlanById(Long id);

    /**
     * 批量删除实践教学计划
     *
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    int deleteCourseTeachingPracticePlanByIds(Long[] ids);

    int insertCourseTeachingPracticePlans(@Param("courseTeachingPracticePlanList") List<CourseTeachingPracticePlan> courseTeachingPracticePlanList);
}
