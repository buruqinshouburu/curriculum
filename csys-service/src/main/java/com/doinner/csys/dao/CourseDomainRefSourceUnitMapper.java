package com.doinner.csys.dao;

import java.util.List;

import com.doinner.csys.domain.CourseDomainRefSourceUnit;
import com.doinner.csys.domain.CourseUnitRefSourcePoint;
import org.apache.ibatis.annotations.Param;

/**
 * 课程中的知识领域关联知识单元(t_csys_source_domain、t_csys_source_unit)Mapper接口
 *
 * @author wzg
 * @date 2026-03-06
 */
public interface CourseDomainRefSourceUnitMapper {
    /**
     * 查询课程中的知识领域关联知识单元(t_csys_source_domain、t_csys_source_unit)
     *
     * @param domainId 课程中的知识领域关联知识单元(t_csys_source_domain、t_csys_source_unit)主键
     * @return 课程中的知识领域关联知识单元(t_csys_source_domain、t_csys_source_unit)
     */
     List<CourseDomainRefSourceUnit> selectCourseDomainRefSourceUnitByDomainId(Long domainId);

    /**
     * 查询课程中的知识领域关联知识单元(t_csys_source_domain、t_csys_source_unit)列表
     *
     * @param courseDomainRefSourceUnit 课程中的知识领域关联知识单元(t_csys_source_domain、t_csys_source_unit)
     * @return 课程中的知识领域关联知识单元(t_csys_source_domain、t_csys_source_unit)集合
     */
     List<CourseDomainRefSourceUnit> selectCourseDomainRefSourceUnitList(CourseDomainRefSourceUnit courseDomainRefSourceUnit);

    /**
     * 新增课程中的知识领域关联知识单元(t_csys_source_domain、t_csys_source_unit)
     *
     * @param courseDomainRefSourceUnit 课程中的知识领域关联知识单元(t_csys_source_domain、t_csys_source_unit)
     * @return 结果
     */
     int insertCourseDomainRefSourceUnit(CourseDomainRefSourceUnit courseDomainRefSourceUnit);

    /**
     * 修改课程中的知识领域关联知识单元(t_csys_source_domain、t_csys_source_unit)
     *
     * @param courseDomainRefSourceUnit 课程中的知识领域关联知识单元(t_csys_source_domain、t_csys_source_unit)
     * @return 结果
     */
     int updateCourseDomainRefSourceUnit(CourseDomainRefSourceUnit courseDomainRefSourceUnit);

    /**
     * 删除课程中的知识领域关联知识单元(t_csys_source_domain、t_csys_source_unit)
     *
     * @param domainId 课程中的知识领域关联知识单元(t_csys_source_domain、t_csys_source_unit)主键
     * @return 结果
     */
     int deleteCourseDomainRefSourceUnitByDomainId(Long domainId);

     int deleteCourseDomainRefSourceUnitByCourseId(@Param("courseId") Long courseId,
                                                   @Param("courseTargetId") Long courseTargetId,
                                                   @Param("collegeId") Long collegeId,
                                                   @Param("categoryId") Long categoryId,
                                                   @Param("majorId") Long majorId);

    /**
     * 批量删除课程中的知识领域关联知识单元(t_csys_source_domain、t_csys_source_unit)
     *
     * @param domainIds 需要删除的数据主键集合
     * @return 结果
     */
     int deleteCourseDomainRefSourceUnitByDomainIds(Long[] domainIds);

    int deleteByCourseIds(Long[] courseIds);

    List<CourseDomainRefSourceUnit> selectCourseDomainRefSourceUnitByUnitIds(Long[] unitIds);

    List<CourseDomainRefSourceUnit> selectCourseTargetRefSourceUnitByCourseIds(@Param("courseIds") List<Long> courseIds);

    void insertCourseUnitRefSourceUnitList(@Param("courseTargetRefUnitList")List<CourseDomainRefSourceUnit> courseTargetRefUnitList);

}
