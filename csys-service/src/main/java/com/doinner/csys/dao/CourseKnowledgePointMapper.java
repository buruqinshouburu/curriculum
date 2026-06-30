package com.doinner.csys.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.doinner.csys.entity.csys.po.CourseKnowledgePoint;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 课程知识点Mapper
 */
@Mapper
public interface CourseKnowledgePointMapper extends BaseMapper<CourseKnowledgePoint> {

    /**
     * 批量插入知识点
     *
     * @param list 知识点列表
     * @return 插入数量
     */
    int insertBatch(@Param("list") List<CourseKnowledgePoint> list);

    /**
     * 根据知识单元ID批量删除知识点
     *
     * @param unitIds 知识单元ID列表
     * @return 删除数量
     */
    int deleteByUnitIds(@Param("unitIds") List<Long> unitIds);

    List<CourseKnowledgePoint> selectByIds(@Param("unitIds") List<Long> unitIds);
}