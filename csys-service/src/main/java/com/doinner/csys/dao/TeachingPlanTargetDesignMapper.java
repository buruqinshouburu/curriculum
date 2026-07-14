package com.doinner.csys.dao;

import com.doinner.csys.domain.TeachingPlanTargetDesign;
import com.doinner.csys.entity.csys.po.CourseKnowledgeUnit;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 教学目标达成设计 Mapper
 */
public interface TeachingPlanTargetDesignMapper {

    /**
     * 单条新增，新增后回填自增主键id
     */
    int insert(TeachingPlanTargetDesign record);

    /**
     * 批量新增
     */
    int insertBatch(@Param("list") List<TeachingPlanTargetDesign> list);

    /**
     * 根据主键更新
     */
    int updateById(TeachingPlanTargetDesign record);

    /**
     * 根据主键查询
     */
    TeachingPlanTargetDesign selectById(@Param("id") Long id);

    /**
     * 根据教学计划ID查询列表
     */
    List<TeachingPlanTargetDesign> selectByPlanId(@Param("planId") Long planId);

    /**
     * 按计划与上下文查询目标达成设计
     */
    List<TeachingPlanTargetDesign> selectByPlanAndContext(@Param("planId") Long planId,
                                                          @Param("contextId") Long contextId);

    /**
     * 按计划、上下文与设计类型查询目标达成设计。
     * 用于知识/能力/素质目标(design_type_code)分tab展示。
     */
    List<TeachingPlanTargetDesign> selectByPlanContextAndType(@Param("planId") Long planId,
                                                              @Param("contextId") Long contextId,
                                                              @Param("designTypeCode") String designTypeCode);

    /**
     * 知识目标初始化：根据总库课程id查询其调用课程关联的知识单元。
     * 链路：总库课程 -> 调用课程(source_id) -> 课程知识单元关联 -> 知识单元。
     * 表内无目标达成设计数据时，用该结果初始化展示知识单元。
     *
     * @param courseId 总库课程id
     * @return 知识单元列表(去重)
     */
    List<CourseKnowledgeUnit> selectKnowledgeUnitInitByCourseId(@Param("courseId") Long courseId);

    /**
     * 根据教学计划ID逻辑删除
     */
    int deleteByPlanId(@Param("planId") Long planId);

    /**
     * 根据主键逻辑删除
     */
    int deleteById(@Param("id") Long id);
}
