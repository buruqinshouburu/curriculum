package com.doinner.csys.dao;

import com.doinner.csys.domain.KnowledgeUnit;
import org.apache.ibatis.annotations.Param;

import java.util.Collection;
import java.util.List;

/**
 * 知识单元Mapper接口
 *
 * @author doinner
 * @date 2023-03-14
 */
public interface KnowledgeUnitMapper {
    /**
     * 查询知识单元
     *
     * @param id 知识单元主键
     * @return 知识单元
     */
    KnowledgeUnit selectKnowledgeUnitById(Long id);

    /**
     * 查询知识单元列表
     *
     * @param knowledgeUnit 知识单元
     * @return 知识单元集合
     */
    List<KnowledgeUnit> selectKnowledgeUnitList(KnowledgeUnit knowledgeUnit);

    /**
     * 新增知识单元
     *
     * @param knowledgeUnit 知识单元
     * @return 结果
     */
    int insertKnowledgeUnit(KnowledgeUnit knowledgeUnit);

    /**
     * 新增知识单元
     *
     * @param knowledgeUnitList 知识单元
     * @return 结果
     */
    int insertKnowledgeUnits(@Param("knowledgeUnitList") Collection<? extends KnowledgeUnit> knowledgeUnitList);

    /**
     * 修改知识单元
     *
     * @param knowledgeUnit 知识单元
     * @return 结果
     */
    int updateKnowledgeUnit(KnowledgeUnit knowledgeUnit);
    int updateKnowledgeUnits(@Param("knowledgeUnitList") Collection<? extends KnowledgeUnit> knowledgeUnitList);

    /**
     * 删除知识单元
     *
     * @param id 知识单元主键
     * @return 结果
     */
    int deleteKnowledgeUnitById(Long id);

    /**
     * 批量删除知识单元
     *
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    int deleteKnowledgeUnitByIds(List<Long> ids);


    List<KnowledgeUnit> selectKnowledgeUnitListByCourseId(@Param("courseId") Long courseId);
}
