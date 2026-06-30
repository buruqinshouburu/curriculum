package com.doinner.csys.dao;

import com.doinner.csys.domain.CourseTeachingTheoryPlan;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 理论教学计划Mapper接口
 *
 * @author doinner
 * @date 2023-03-14
 */
public interface CourseTeachingTheoryPlanMapper {
    /**
     * 查询理论教学计划
     *
     * @param id 理论教学计划主键
     * @return 理论教学计划
     */
    CourseTeachingTheoryPlan selectCourseTeachingTheoryPlanById(Long id);

    /**
     * 根据课程id查询理论教学计划
     *
     * @param courseId 理论教学计划主键
     * @return 理论教学计划
     */
    CourseTeachingTheoryPlan selectCourseTeachingTheoryPlanByCourseId(Long courseId);

    /**
     * 查询理论教学计划列表
     *
     * @param courseTeachingTheoryPlan 理论教学计划
     * @return 理论教学计划集合
     */
    List<CourseTeachingTheoryPlan> selectCourseTeachingTheoryPlanList(CourseTeachingTheoryPlan courseTeachingTheoryPlan);

    /**
     * 新增理论教学计划
     *
     * @param courseTeachingTheoryPlan 理论教学计划
     * @return 结果
     */
    int insertCourseTeachingTheoryPlan(CourseTeachingTheoryPlan courseTeachingTheoryPlan);

    /**
     * 修改理论教学计划
     *
     * @param courseTeachingTheoryPlan 理论教学计划
     * @return 结果
     */
    int updateCourseTeachingTheoryPlan(CourseTeachingTheoryPlan courseTeachingTheoryPlan);

    /**
     * 删除理论教学计划
     *
     * @param id 理论教学计划主键
     * @return 结果
     */
    int deleteCourseTeachingTheoryPlanById(Long id);

    /**
     * 批量删除理论教学计划
     *
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    int deleteCourseTeachingTheoryPlanByIds(Long[] ids);

    void insertCourseTeachingTheoryPlans(@Param("courseTeachingTheoryPlanList")List<CourseTeachingTheoryPlan> courseTeachingTheoryPlanList);
}
