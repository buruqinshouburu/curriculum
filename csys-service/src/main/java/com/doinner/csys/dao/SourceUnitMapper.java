package com.doinner.csys.dao;

import java.util.List;

import com.doinner.csys.domain.SourceUnit;

/**
 * 源知识单元Mapper接口
 *
 * @author wzg
 * @date 2026-02-26
 */
public interface SourceUnitMapper {
    /**
     * 查询源知识单元
     *
     * @param id 源知识单元主键
     * @return 源知识单元
     */
     SourceUnit selectSourceUnitById(Long id);

    /**
     * 查询源知识单元列表
     *
     * @param sourceUnit 源知识单元
     * @return 源知识单元集合
     */
     List<SourceUnit> selectSourceUnitList(SourceUnit sourceUnit);

    /**
     * 新增源知识单元
     *
     * @param sourceUnit 源知识单元
     * @return 结果
     */
     int insertSourceUnit(SourceUnit sourceUnit);

    /**
     * 修改源知识单元
     *
     * @param sourceUnit 源知识单元
     * @return 结果
     */
     int updateSourceUnit(SourceUnit sourceUnit);

    /**
     * 删除源知识单元
     *
     * @param id 源知识单元主键
     * @return 结果
     */
     int deleteSourceUnitById(Long id);

    /**
     * 批量删除源知识单元
     *
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
     int deleteSourceUnitByIds(Long[] ids);
}
