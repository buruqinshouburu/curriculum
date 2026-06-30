package com.doinner.csys.dao;

import com.doinner.csys.domain.TrainingSchemeCategory;
import com.doinner.csys.domain.vo.CategoryCountVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 培养方案门类Mapper接口
 *
 * @author doinner
 */
public interface TrainingSchemeCategoryMapper {
    /**
     * 查询培养方案门类
     *
     * @param id 培养方案门类主键
     * @return 培养方案门类
     */
    TrainingSchemeCategory selectTrainingSchemeCategoryById(Long id);

    /**
     * 查询培养方案门类列表
     *
     * @param trainingSchemeCategory 培养方案门类
     * @return 培养方案门类集合
     */
    List<TrainingSchemeCategory> selectTrainingSchemeCategoryList(TrainingSchemeCategory trainingSchemeCategory);

    /**
     * 新增培养方案门类
     *
     * @param trainingSchemeCategory 培养方案门类
     * @return 结果
     */
    int insertTrainingSchemeCategory(TrainingSchemeCategory trainingSchemeCategory);

    /**
     * 修改培养方案门类
     *
     * @param trainingSchemeCategory 培养方案门类
     * @return 结果
     */
    int updateTrainingSchemeCategory(TrainingSchemeCategory trainingSchemeCategory);

    /**
     * 删除培养方案门类
     *
     * @param id 培养方案门类主键
     * @return 结果
     */
    int deleteTrainingSchemeCategoryById(Long id);

    /**
     * 批量删除培养方案门类
     *
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    int deleteTrainingSchemeCategoryByIds(Long[] ids);

    /** 逻辑删除 */
    int deleteTrainingSchemeCategory(Long id);

    /** 逻辑删除 */
    int deleteTrainingSchemeCategoryList(Long[] ids);

    List<CategoryCountVo> majorCount();

    List<Long> selectTrainingSchemeCategoryIdsBySystemId(Long systemId);

    List<TrainingSchemeCategory> selectAllTrainingSchemeCategoryById(Long id);

    int deleteTrainingSchemeCategoryByIdList(@Param("categoriesIds") List<Long> categoriesIds);
}
