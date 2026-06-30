package com.doinner.csys.dao;

import java.util.List;

import com.doinner.csys.domain.CourseRefSourceDomain;
import org.apache.ibatis.annotations.Param;

/**
 * 课程关联知识领域(t_csys_source_domain)Mapper接口
 *
 * @author wzg
 * @date 2026-03-06
 */
public interface CourseRefSourceDomainMapper {
    /**
     * 查询课程关联知识领域(t_csys_source_domain)
     *
     * @param courseId 课程关联知识领域(t_csys_source_domain)主键
     * @return 课程关联知识领域(t_csys_source_domain)
     */
     List<CourseRefSourceDomain> selectCourseRefSourceDomainByCourseId(Long courseId);
     List<CourseRefSourceDomain> selectCourseRefSourceDomainByDomainId(Long domainId);

    /**
     * 查询课程关联知识领域(t_csys_source_domain)列表
     *
     * @param courseRefSourceDomain 课程关联知识领域(t_csys_source_domain)
     * @return 课程关联知识领域(t_csys_source_domain)集合
     */
     List<CourseRefSourceDomain> selectCourseRefSourceDomainList(CourseRefSourceDomain courseRefSourceDomain);

    /**
     * 新增课程关联知识领域(t_csys_source_domain)
     *
     * @param courseRefSourceDomain 课程关联知识领域(t_csys_source_domain)
     * @return 结果
     */
     int insertCourseRefSourceDomain(CourseRefSourceDomain courseRefSourceDomain);

    /**
     * 修改课程关联知识领域(t_csys_source_domain)
     *
     * @param courseRefSourceDomain 课程关联知识领域(t_csys_source_domain)
     * @return 结果
     */
     int updateCourseRefSourceDomain(CourseRefSourceDomain courseRefSourceDomain);

    /**
     * 删除课程关联知识领域(t_csys_source_domain)
     *
     * @param courseId 课程关联知识领域(t_csys_source_domain)主键
     * @return 结果
     */

     int deleteCourseRefSourceDomainByCourseId(@Param("courseId") Long courseId,
                                               @Param("courseTargetId") Long courseTargetId,
                                               @Param("collegeId") Long collegeId,
                                               @Param("categoryId") Long categoryId,
                                               @Param("majorId") Long majorId);

    /**
     * 批量删除课程关联知识领域(t_csys_source_domain)
     *
     * @param courseIds 需要删除的数据主键集合
     * @return 结果
     */
     int deleteCourseRefSourceDomainByCourseIds(Long[] courseIds);

    List<CourseRefSourceDomain> selectCourseTargetRefSourceDomainByCourseIds(@Param("courseIds") List<Long> courseIds);

    void insertCourseUnitRefSourceDomainList(@Param("courseTargetRefDomainList") List<CourseRefSourceDomain> courseTargetRefDomainList);
}
