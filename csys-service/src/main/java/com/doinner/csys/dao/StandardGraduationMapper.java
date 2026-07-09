package com.doinner.csys.dao;

import java.util.Collection;
import java.util.List;

import com.doinner.csys.domain.StandardCultivation;
import com.doinner.csys.domain.StandardGraduation;
import com.doinner.csys.domain.vo.StandardCultivationTargetVo;
import com.doinner.csys.domain.vo.StandardCultivationVo;
import com.doinner.csys.domain.vo.StandardGraduationVo;
import com.doinner.csys.domain.vo.TrainingSchemeVo;
import org.apache.ibatis.annotations.Param;

/**
 * 毕业标准Mapper接口
 *
 * @author doinner
 * @date 2023-03-21
 */
public interface StandardGraduationMapper {
    /**
     * 查询毕业标准
     *
     * @param id 毕业标准主键
     * @return 毕业标准
     */
    StandardGraduation selectStandardGraduationById(Long id);
    List<StandardGraduation> selectStandardGraduationByIds(@Param("ids")List<Long> ids);

    /**
     * 查询毕业标准列表
     *
     * @param standardGraduation 毕业标准
     * @return 毕业标准集合
     */
    List<StandardGraduation> selectStandardGraduationList(StandardGraduation standardGraduation);
    List<StandardGraduation> selectStandardGraduationListNoJoin(StandardGraduation standardGraduation);


    List<StandardGraduationVo> selectGraduationRefTarget(@Param("id") Long id);

    /**
     * 新增毕业标准
     *
     * @param standardGraduation 毕业标准
     * @return 结果
     */
    int insertStandardGraduation(StandardGraduation standardGraduation);

    /**
     * 修改毕业标准
     *
     * @param standardGraduation 毕业标准
     * @return 结果
     */
    int updateStandardGraduation(StandardGraduation standardGraduation);


    void deleteStandardGraduation(Long id);

    void setLeafIsNode(@Param("pId") Long pId);


    void insertStandardGraduationList(@Param("standardGraduationList") Collection<StandardGraduation> standardGraduationList);

    void updateStandardGraduationList(@Param("standardGraduationList") Collection<StandardGraduation> standardGraduationList);

    List<StandardGraduation> selectStandardGraduationAll(Long id);

    int updateCultivationTargetId(StandardGraduation standardGraduation);

    List<Long> selectStandardGraduationRefByCultivationId(@Param("id") Long id, @Param("cultivationId") Long cultivationId);

    List<StandardCultivationVo> selectGByCId(@Param("cultivationId") Long cultivationId);

    List<String> selectGUrlPName(@Param("ids") List<Long> ids);

    List<StandardGraduationVo> selectStandardGraduationVoAll(@Param("id") Long id);

    List<StandardCultivationTargetVo> selectGByTId(@Param("targetId") Long targetId);

    StandardGraduation selectStandardGraduationByRoot(@Param("id") Long id,@Param("name") String name);

    void deleteByIds(@Param("ids") List<Long> gIds);

    List<StandardGraduation> selectStandardGraduationByMajorId(@Param("trainingSchemeVo") TrainingSchemeVo trainingSchemeVo);

    void deleteStandardGraduationBySchemeId(Long schemeId);

    /**
     * 查询某培养方案下、有总库来源(source_id 非空)的毕业要求，
     * 用于同步时把总库毕业要求 id 映射为方案毕业要求 id。
     * 返回对象填充 id(方案毕业要求id) 与 sourceId(总库毕业要求id)。
     *
     * @param schemeId 培养方案id
     * @return 方案毕业要求集合
     */
    List<StandardGraduation> selectSchemeGraduationWithSourceBySchemeId(@Param("schemeId") Long schemeId);
}
