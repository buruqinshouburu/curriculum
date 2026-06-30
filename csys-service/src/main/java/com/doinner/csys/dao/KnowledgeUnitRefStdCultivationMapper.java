package com.doinner.csys.dao;

import com.doinner.csys.domain.KnowledgeUnitRefStdCultivation;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 知识单元与培养标准关联Mapper接口
 *
 * @author doinner
 * @date 2023-03-14
 */
public interface KnowledgeUnitRefStdCultivationMapper {
    /**
     * 查询知识单元与培养标准关联
     *
     * @param unitId 知识单元与培养标准关联主键
     * @return 知识单元与培养标准关联
     */
    KnowledgeUnitRefStdCultivation selectKnowledgeUnitRefStdCultivationByUnitId(Long unitId);

    /**
     * 查询知识单元与培养标准关联列表
     *
     * @param knowledgeUnitRefStdCultivation 知识单元与培养标准关联
     * @return 知识单元与培养标准关联集合
     */
    List<KnowledgeUnitRefStdCultivation> selectKnowledgeUnitRefStdCultivationList(KnowledgeUnitRefStdCultivation knowledgeUnitRefStdCultivation);


    List<KnowledgeUnitRefStdCultivation> selectRefByCuIds(@Param("cuIds") List<Long> cuIds);

    /**
     * 新增知识单元与培养标准关联
     *
     * @param knowledgeUnitRefStdCultivation 知识单元与培养标准关联
     * @return 结果
     */
    int insertKnowledgeUnitRefStdCultivation(KnowledgeUnitRefStdCultivation knowledgeUnitRefStdCultivation);

    int insertKnowledgeUnitRefStdCultivations(@Param("knowledgeUnitRefStdCultivations") List<KnowledgeUnitRefStdCultivation> knowledgeUnitRefStdCultivations);

    /**
     * 修改知识单元与培养标准关联
     *
     * @param knowledgeUnitRefStdCultivation 知识单元与培养标准关联
     * @return 结果
     */
    int updateKnowledgeUnitRefStdCultivation(KnowledgeUnitRefStdCultivation knowledgeUnitRefStdCultivation);

    /**
     * 删除知识单元与培养标准关联
     *
     * @param unitId 知识单元与培养标准关联主键
     * @return 结果
     */
    int deleteKnowledgeUnitRefStdCultivationByUnitId(Long unitId);

    /**
     * 批量删除知识单元与培养标准关联
     *
     * @param unitIds 需要删除的数据主键集合
     * @return 结果
     */
    int deleteKnowledgeUnitRefStdCultivationByUnitIds(Long[] unitIds);

    int deleteKnowledgeUnitRefStdCultivationBySchemeId(Long schemeId);

    int deleteKnowledgeUnitRefStdCultivationBySchemeIds(Long[] schemeIds);

    int deleteKnowledgeUnitRefStdCultivationByCourseIdsAndUnitIds(@Param("courseIds") List<Long> courseIds, @Param("unitIds") List<Long> unitIds);

    int deleteKnowledgeUnitRefStdCultivationBySchemeIdAndCourseIdsAndUnitIds(@Param("schemeId")Long schemeId, @Param("courseIds") List<Long> courseIds, @Param("unitIds") List<Long> unitIds);

    List<KnowledgeUnitRefStdCultivation> selectKnowledgeUnitRefStdCultivationsByKnowledgeUnitIds(@Param("knowledgeUnitIds") List<Long> knowledgeUnitIds);
}
