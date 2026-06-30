package com.doinner.csys.dao;

import com.doinner.csys.domain.KnowledgeUnitRefPoint;
import org.apache.ibatis.annotations.Param;

import java.util.Collection;
import java.util.List;

/**
 * 知识单元与知识点关联Mapper接口
 *
 * @author doinner
 * @date 2023-03-14
 */
public interface KnowledgeUnitRefPointMapper {
    /**
     * 查询知识单元与知识点关联
     *
     * @param unitId 知识单元与知识点关联主键
     * @return 知识单元与知识点关联
     */
    KnowledgeUnitRefPoint selectKnowledgeUnitRefPointByUnitId(Long unitId);

    /**
     * 查询知识单元与知识点关联列表
     *
     * @param knowledgeUnitRefPoint 知识单元与知识点关联
     * @return 知识单元与知识点关联集合
     */
    List<KnowledgeUnitRefPoint> selectKnowledgeUnitRefPointList(KnowledgeUnitRefPoint knowledgeUnitRefPoint);

    /**
     * 新增知识单元与知识点关联
     *
     * @param knowledgeUnitRefPoint 知识单元与知识点关联
     * @return 结果
     */
    int insertKnowledgeUnitRefPoint(KnowledgeUnitRefPoint knowledgeUnitRefPoint);

    /**
     * 修改知识单元与知识点关联
     *
     * @param knowledgeUnitRefPoint 知识单元与知识点关联
     * @return 结果
     */
    int updateKnowledgeUnitRefPoint(KnowledgeUnitRefPoint knowledgeUnitRefPoint);

    /**
     * 删除知识单元与知识点关联
     *
     * @param unitId 知识单元与知识点关联主键
     * @return 结果
     */
    int deleteKnowledgeUnitRefPointByUnitId(Long unitId);

    /**
     * 批量删除知识单元与知识点关联
     *
     * @param unitIds 需要删除的数据主键集合
     * @return 结果
     */
    int deleteKnowledgeUnitRefPointByUnitIds(Long[] unitIds);

    /**
     * 删除知识单元与知识点关联信息
     *
     * @param unitId 知识单元与知识点关联主键
     * @return 结果
     */
    int deleteKnowledgeUnitRefPointByUnitIdAndPointId(Long unitId,Long pointId);

    List<KnowledgeUnitRefPoint> selectKnowledgeUnitRefPointByUnitIds(@Param("unitIds") List<Long> updateUnitIds);

    void deleteKnowledgeUnitRefPointByPointIds(@Param("pointIds") Collection<Long> pointIds);

    void insertKnowledgeUnitRefPoints(@Param("knowledgeUnitRefPointList") List<KnowledgeUnitRefPoint> knowledgeUnitRefPointList);
}
