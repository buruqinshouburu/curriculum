package com.doinner.csys.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.doinner.csys.entity.csys.po.CourseKnowledgeUnit;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 课程知识单元Mapper
 */
@Mapper
public interface CourseKnowledgeUnitMapper extends BaseMapper<CourseKnowledgeUnit> {

    /**
     * 批量插入知识单元
     *
     * @param list 知识单元列表
     * @return 插入数量
     */
    int insertBatch(@Param("list") List<CourseKnowledgeUnit> list);

    /**
     * 根据课程ID查询知识单元列表
     *
     * @param courseId 课程ID
     * @return 知识单元列表
     */
    List<CourseKnowledgeUnit> selectByCourseId(@Param("courseId") Long courseId);

    void deleteByIds(@Param("ids")List<Long> ids);

    List<CourseKnowledgeUnit> selectByIds(@Param("unitIds") List<Long> unitIds);
}