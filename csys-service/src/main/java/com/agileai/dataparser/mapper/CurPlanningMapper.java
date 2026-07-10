package com.agileai.dataparser.mapper;

import com.agileai.dataparser.domain.CurPlanning;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 培养规划Mapper接口
 *
 * @author agileai
 * @date 2022-04-27
 */
public interface CurPlanningMapper
{
    /**
     * 查询培养规划
     *
     * @param id 培养规划ID
     * @return 培养规划
     */
    CurPlanning selectCurPlanningById(Long id);

    /**
     * 查询培养规划列表
     *
     * @param curPlanning 培养规划
     * @return 培养规划集合
     */
    List<CurPlanning> selectCurPlanningList(CurPlanning curPlanning);

    /**
     * 新增培养规划
     *
     * @param curPlanning 培养规划
     * @return 结果
     */
    int insertCurPlanning(CurPlanning curPlanning);

    /**
     * 修改培养规划
     *
     * @param curPlanning 培养规划
     * @return 结果
     */
    int updateCurPlanning(CurPlanning curPlanning);

    /**
     * 删除培养规划
     *
     * @param id 培养规划ID
     * @return 结果
     */
    int deleteCurPlanningById(Long id);

    /**
     * 批量删除培养规划
     *
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    int deleteCurPlanningByIds(Long[] ids);

    void resetTargetId(@Param("infoIds") List<String> infoIds);

    List<CurPlanning> selectCurPlanningByTargetIds(@Param("infoIds") List<String> infoIds);
}
