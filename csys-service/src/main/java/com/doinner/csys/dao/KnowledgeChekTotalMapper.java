package com.doinner.csys.dao;

import java.util.List;

import com.doinner.csys.domain.KnowledgeChekTotal;
import org.apache.ibatis.annotations.Param;

/**
 * 课程知识单元，知识点，统计Mapper接口
 *
 * @author doinner
 * @date 2023-03-27
 */
public interface KnowledgeChekTotalMapper {
    /**
     * 查询课程知识单元，知识点，统计
     *
     * @param id 课程知识单元，知识点，统计主键
     * @return 课程知识单元，知识点，统计
     */
    KnowledgeChekTotal selectKnowledgeChekTotalById(Long id);

    KnowledgeChekTotal selectBySourceCourseId(Long id);

    /**
     * 查询课程知识单元，知识点，统计列表
     *
     * @param knowledgeChekTotal 课程知识单元，知识点，统计
     * @return 课程知识单元，知识点，统计集合
     */
    List<KnowledgeChekTotal> selectKnowledgeChekTotalList(KnowledgeChekTotal knowledgeChekTotal);

    /**
     * 新增课程知识单元，知识点，统计
     *
     * @param knowledgeChekTotal 课程知识单元，知识点，统计
     * @return 结果
     */
    int insertKnowledgeChekTotal(KnowledgeChekTotal knowledgeChekTotal);

    /**
     * 修改课程知识单元，知识点，统计
     *
     * @param knowledgeChekTotal 课程知识单元，知识点，统计
     * @return 结果
     */
    int updateKnowledgeChekTotal(KnowledgeChekTotal knowledgeChekTotal);

    /**
     * 删除课程知识单元，知识点，统计
     *
     * @param id 课程知识单元，知识点，统计主键
     * @return 结果
     */
    int deleteKnowledgeChekTotalById(Long id);

    int deleteBySourceDomainId(@Param("sourceDomainId") Long SourceDomainId);

    /**
     * 批量删除课程知识单元，知识点，统计
     *
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    int deleteKnowledgeChekTotalByIds(Long[] ids);

    KnowledgeChekTotal selectTotalBySourceCourseIds(@Param("sourceDomainIds") List<Long> sourceDomainIds);

    void deleteByCourseId(@Param("courseIds")List<Long> courseIds);
}
