package com.doinner.csys.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.doinner.csys.entity.csys.po.CourseKnowledgePoint;
import com.doinner.csys.entity.csys.po.CourseRefKnowledgeUnit;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 课程知识点Mapper
 */
@Mapper
public interface CourseRefKnowledgeUnitMapper extends BaseMapper<CourseRefKnowledgeUnit> {


    List<CourseRefKnowledgeUnit> selectByCourseId(@Param("courseId")Long courseId);

    int deleteByCourseId(@Param("courseId")Long courseId);

    List<CourseRefKnowledgeUnit> selectByCourseIds(@Param("courseIds")List<Long> ids);

    int deleteByCourseIds(@Param("courseIds")List<Long> ids);

    int insertBatch(@Param("list") List<CourseRefKnowledgeUnit> newCourseRefKnowledgeUnitList);

    @Select("  SELECT course_unit_id\n" +
            "        FROM t_csys_course_ref_knowledge_unit sru\n" +
            "          where  sru.course_id = #{courseId}")
    List<Long> totalUnitByCourseId(Long courseId);
}