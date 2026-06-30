package com.doinner.csys.dao;

import java.util.List;

import com.doinner.csys.domain.SourcePoint;

/**
 * 源知识点Mapper接口
 *
 * @author wzg
 * @date 2026-02-26
 */
public interface SourcePointMapper {
    /**
     * 查询源知识点
     *
     * @param id 源知识点主键
     * @return 源知识点
     */
     SourcePoint selectSourcePointById(Long id);

    /**
     * 查询源知识点列表
     *
     * @param sourcePoint 源知识点
     * @return 源知识点集合
     */
     List<SourcePoint> selectSourcePointList(SourcePoint sourcePoint);

    /**
     * 新增源知识点
     *
     * @param sourcePoint 源知识点
     * @return 结果
     */
     int insertSourcePoint(SourcePoint sourcePoint);

    /**
     * 修改源知识点
     *
     * @param sourcePoint 源知识点
     * @return 结果
     */
     int updateSourcePoint(SourcePoint sourcePoint);

    /**
     * 删除源知识点
     *
     * @param id 源知识点主键
     * @return 结果
     */
     int deleteSourcePointById(Long id);

    /**
     * 批量删除源知识点
     *
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
     int deleteSourcePointByIds(Long[] ids);
}
