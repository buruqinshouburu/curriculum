package com.doinner.csys.dao;

import java.util.List;
import com.doinner.csys.domain.TrainingSchemeWeek;

/**
 * 修业时间分配情况Mapper接口
 *
 * @author doinner
 * @date 2023-04-10
 */
public interface TrainingSchemeWeekMapper
{
    /**
     * 查询修业时间分配情况
     *
     * @param id 修业时间分配情况主键
     * @return 修业时间分配情况
     */
     TrainingSchemeWeek selectTrainingSchemeWeekById(Long id);

    /**
     * 查询修业时间分配情况列表
     *
     * @param trainingSchemeWeek 修业时间分配情况
     * @return 修业时间分配情况集合
     */
     List<TrainingSchemeWeek> selectTrainingSchemeWeekList(TrainingSchemeWeek trainingSchemeWeek);

    TrainingSchemeWeek selectTrainingSchemeWeekBySchemeId(Long id);

    /**
     * 新增修业时间分配情况
     *
     * @param trainingSchemeWeek 修业时间分配情况
     * @return 结果
     */
     int insertTrainingSchemeWeek(TrainingSchemeWeek trainingSchemeWeek);

    /**
     * 修改修业时间分配情况
     *
     * @param trainingSchemeWeek 修业时间分配情况
     * @return 结果
     */
     int updateTrainingSchemeWeek(TrainingSchemeWeek trainingSchemeWeek);

    /**
     * 删除修业时间分配情况
     *
     * @param id 修业时间分配情况主键
     * @return 结果
     */
     int deleteTrainingSchemeWeekById(Long id);

    /**
     * 批量删除修业时间分配情况
     *
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
     int deleteTrainingSchemeWeekByIds(Long[] ids);
}
