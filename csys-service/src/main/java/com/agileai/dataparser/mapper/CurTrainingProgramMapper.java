package com.agileai.dataparser.mapper;

import com.agileai.dataparser.domain.CurTrainingProgram;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 培养方案Mapper接口
 *
 * @author agileai
 * @date 2022-05-13
 */
public interface CurTrainingProgramMapper
{
    /**
     * 查询培养方案
     *
     * @param id 培养方案ID
     * @return 培养方案
     */
    CurTrainingProgram selectCurTrainingProgramById(Long id);

    /**
     * 查询培养方案列表
     *
     * @param curTrainingProgram 培养方案
     * @return 培养方案集合
     */
    List<CurTrainingProgram> selectCurTrainingProgramList(CurTrainingProgram curTrainingProgram);

    /**
     * 新增培养方案
     *
     * @param curTrainingProgram 培养方案
     * @return 结果
     */
    int insertCurTrainingProgram(CurTrainingProgram curTrainingProgram);

    /**
     * 修改培养方案
     *
     * @param curTrainingProgram 培养方案
     * @return 结果
     */
    int updateCurTrainingProgram(CurTrainingProgram curTrainingProgram);

    /**
     * 删除培养方案
     *
     * @param id 培养方案ID
     * @return 结果
     */
    int deleteCurTrainingProgramById(Long id);

    /**
     * 批量删除培养方案
     *
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    int deleteCurTrainingProgramByIds(Long[] ids);

    List<CurTrainingProgram> selectCurTrainingProgramByPlanningIds(@Param("teachingPlanIds") List<Long> teachingPlanIds);

    List<CurTrainingProgram> selectCurTrainingProgramByParentIds(@Param("parentIds") List<Long> parentIds);

    void resetId();

    List<CurTrainingProgram> selectCurTrainingProgramTree(CurTrainingProgram curTrainingProgram);
}
