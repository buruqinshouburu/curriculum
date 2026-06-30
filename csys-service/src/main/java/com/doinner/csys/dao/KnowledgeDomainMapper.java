package com.doinner.csys.dao;

import java.util.List;
import com.doinner.csys.domain.KnowledgeDomain;
import org.apache.ibatis.annotations.Param;

/**
 * 知识领域表Mapper接口
 *
 * @author doinner
 * @date 2023-03-16
 */
public interface KnowledgeDomainMapper
{
    /**
     * 查询知识领域表
     *
     * @param id 知识领域表主键
     * @return 知识领域表
     */
     KnowledgeDomain selectKnowledgeDomainById(Long id);

    /**
     * 查询知识领域表列表
     *
     * @param knowledgeDomain 知识领域表
     * @return 知识领域表集合
     */
     List<KnowledgeDomain> selectKnowledgeDomainList(KnowledgeDomain knowledgeDomain);

    /**
     * 新增知识领域表
     *
     * @param knowledgeDomain 知识领域表
     * @return 结果
     */
     int insertKnowledgeDomain(KnowledgeDomain knowledgeDomain);

    /**
     * 修改知识领域表
     *
     * @param knowledgeDomain 知识领域表
     * @return 结果
     */
     int updateKnowledgeDomain(KnowledgeDomain knowledgeDomain);

    /**
     * 删除知识领域表
     *
     * @param id 知识领域表主键
     * @return 结果
     */
     int deleteKnowledgeDomainById(Long id);

    /**
     * 批量删除知识领域表
     *
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
     int deleteKnowledgeDomainByIds(Long[] ids);

    void deleteKnowledgeDomainByCourseId(@Param("courseId") Long courseId);

    void insertKnowledgeDomains(@Param("knowledgeDomains") List<? extends KnowledgeDomain> knowledgeDomainVoList);
}
