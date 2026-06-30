package com.doinner.csys.dao;

import java.util.List;

import com.doinner.csys.domain.SourceUnitRefPoint;
import org.apache.ibatis.annotations.Param;

/**
 * 源知识单元与知识点Mapper接口
 *
 * @author wzg
 * @date 2026-02-26
 */
public interface SourceUnitRefPointMapper {
    /**
     * 查询源知识单元与知识点
     *
     * @param unitId 源知识单元与知识点主键
     * @return 源知识单元与知识点
     */
     List<SourceUnitRefPoint> selectSourceUnitRefPointByUnitId(Long unitId);

     List<SourceUnitRefPoint> selectSourceUnitRefPointByUnitIds(@Param("unitIds") List<Long> unitIds);

    /**
     * 查询源知识单元与知识点列表
     *
     * @param sourceUnitRefPoint 源知识单元与知识点
     * @return 源知识单元与知识点集合
     */
     List<SourceUnitRefPoint> selectSourceUnitRefPointList(SourceUnitRefPoint sourceUnitRefPoint);

    /**
     * 新增源知识单元与知识点
     *
     * @param sourceUnitRefPoint 源知识单元与知识点
     * @return 结果
     */
     int insertSourceUnitRefPoint(SourceUnitRefPoint sourceUnitRefPoint);

    /**
     * 修改源知识单元与知识点
     *
     * @param sourceUnitRefPoint 源知识单元与知识点
     * @return 结果
     */
     int updateSourceUnitRefPoint(SourceUnitRefPoint sourceUnitRefPoint);

    /**
     * 删除源知识单元与知识点
     *
     * @param unitId 源知识单元与知识点主键
     * @return 结果
     */
     int deleteSourceUnitRefPointByUnitId(Long unitId);

    /**
     * 批量删除源知识单元与知识点
     *
     * @param unitIds 需要删除的数据主键集合
     * @return 结果
     */
     int deleteSourceUnitRefPointByUnitIds(Long[] unitIds);

    int deleteSourceUnitRefPointByPointIds(Long[] pointIds);
}
