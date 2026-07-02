package com.doinner.csys.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.doinner.csys.domain.vo.CourseKnowledgeExportRow;
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

    /**
     * 按课程id集合查询「课程-知识单元-知识点」扁平行, 供导出使用。
     * 结果已按 课程id、单元sort、知识点sort 排序, 保证同一课程/单元的行连续(单元格合并前提)。
     * 知识点用 LEFT JOIN: 单元下无知识点时仍保留该单元一行(pointName 为空)。
     *
     * @param courseIds 课程id集合
     * @return 扁平行集合
     */
    List<CourseKnowledgeExportRow> selectExportRowsByCourseIds(@Param("courseIds") List<Long> courseIds);

    @Select("  SELECT course_unit_id\n" +
            "        FROM t_csys_course_ref_knowledge_unit sru\n" +
            "          where  sru.course_id = #{courseId}")
    List<Long> totalUnitByCourseId(Long courseId);
}