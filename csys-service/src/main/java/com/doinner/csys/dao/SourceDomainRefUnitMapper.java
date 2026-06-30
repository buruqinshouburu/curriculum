package com.doinner.csys.dao;

import java.util.List;

import com.doinner.csys.domain.SourceDomainRefUnit;

/**
 * 源知识领域关联知识单元Mapper接口
 *
 * @author wzg
 * @date 2026-02-26
 */
public interface SourceDomainRefUnitMapper {
    /**
     * 查询源知识领域关联知识单元
     *
     * @param domainId 源知识领域关联知识单元主键
     * @return 源知识领域关联知识单元
     */
     List<SourceDomainRefUnit> selectSourceDomainRefUnitByDomainId(Long domainId);

    /**
     * 查询源知识领域关联知识单元列表
     *
     * @param sourceDomainRefUnit 源知识领域关联知识单元
     * @return 源知识领域关联知识单元集合
     */
     List<SourceDomainRefUnit> selectSourceDomainRefUnitList(SourceDomainRefUnit sourceDomainRefUnit);

    /**
     * 新增源知识领域关联知识单元
     *
     * @param sourceDomainRefUnit 源知识领域关联知识单元
     * @return 结果
     */
     int insertSourceDomainRefUnit(SourceDomainRefUnit sourceDomainRefUnit);

    /**
     * 修改源知识领域关联知识单元
     *
     * @param sourceDomainRefUnit 源知识领域关联知识单元
     * @return 结果
     */
     int updateSourceDomainRefUnit(SourceDomainRefUnit sourceDomainRefUnit);

    /**
     * 删除源知识领域关联知识单元
     *
     * @param domainId 源知识领域关联知识单元主键
     * @return 结果
     */
     int deleteSourceDomainRefUnitByDomainId(Long domainId);

    /**
     * 批量删除源知识领域关联知识单元
     *
     * @param domainIds 需要删除的数据主键集合
     * @return 结果
     */
     int deleteSourceDomainRefUnitByDomainIds(Long[] domainIds);

     int deleteSourceDomainRefUnitByUnitIds(Long[] unitIds);

     List<Long> totalUnitByDomainId(Long domainId);
}
