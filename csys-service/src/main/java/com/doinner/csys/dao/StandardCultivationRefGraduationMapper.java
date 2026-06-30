package com.doinner.csys.dao;

import com.doinner.csys.domain.KnowledgeUnitRefStdCultivation;
import com.doinner.csys.domain.StandardCultivationRefGraduation;
import com.doinner.csys.domain.StandardGraduationRefCultivationTarget;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 培养标准与毕业标准关联Mapper接口
 *
 * @author doinner
 */
public interface StandardCultivationRefGraduationMapper {
    /**
     * 查询培养标准与毕业标准关联
     *
     * @param cultivationId 培养标准与毕业标准关联主键
     * @return 培养标准与毕业标准关联
     */
    StandardCultivationRefGraduation selectStandardCultivationRefGraduationByCultivationId(Long cultivationId);

    /**
     * 查询培养标准与毕业标准关联列表
     *
     * @param StandardCultivationRefGraduation 培养标准与毕业标准关联
     * @return 培养标准与毕业标准关联集合
     */
    List<StandardCultivationRefGraduation> selectStandardCultivationRefGraduationList(StandardCultivationRefGraduation StandardCultivationRefGraduation);

    /**
     * 新增培养标准与毕业标准关联
     *
     * @param StandardCultivationRefGraduation 培养标准与毕业标准关联
     * @return 结果
     */
    StandardCultivationRefGraduation insertStandardCultivationRefGraduation(StandardCultivationRefGraduation StandardCultivationRefGraduation);

    /**
     * 修改培养标准与毕业标准关联
     *
     * @param StandardCultivationRefGraduation 培养标准与毕业标准关联
     * @return 结果
     */
    int updateStandardCultivationRefGraduation(StandardCultivationRefGraduation StandardCultivationRefGraduation);

    /**
     * 删除培养标准与毕业标准关联
     *
     * @param cultivationId 培养标准与毕业标准关联主键
     * @return 结果
     */
    int deleteStandardCultivationRefGraduationByCultivationId(Long cultivationId);



    void deleteByCultivationIdsIds(List<Long> ids);

    int insetList(@Param("insetInfo") List<StandardCultivationRefGraduation> insetInfo);


    List<StandardCultivationRefGraduation> selectRefByCultivationIds(@Param("standardCultivationIds") List<Long> standardCultivationIds);

    List<StandardCultivationRefGraduation> selectRefByGrIds(@Param("grIds") List<Long> grIds);

}
