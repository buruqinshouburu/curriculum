package com.doinner.csys.dao;

import java.util.List;

import com.doinner.csys.domain.TeachingProgram;

/**
 *  todo 教学计划Mapper接口
 *
 * @author wzg
 * @date 2026-02-26
 */
public interface TeachingProgramMapper {
    /**
     * 查询教学计划
     *
     * @param id 教学计划主键
     * @return 教学计划
     */
     TeachingProgram selectTeachingProgramById(Long id);

    /**
     * 查询教学计划列表
     *
     * @param teachingProgram 教学计划
     * @return 教学计划集合
     */
     List<TeachingProgram> selectTeachingProgramList(TeachingProgram teachingProgram);

    /**
     * 新增教学计划
     *
     * @param teachingProgram 教学计划
     * @return 结果
     */
     int insertTeachingProgram(TeachingProgram teachingProgram);

    /**
     * 修改教学计划
     *
     * @param teachingProgram 教学计划
     * @return 结果
     */
     int updateTeachingProgram(TeachingProgram teachingProgram);

    /**
     * 删除教学计划
     *
     * @param id 教学计划主键
     * @return 结果
     */
     int deleteTeachingProgramById(Long id);

    /**
     * 批量删除教学计划
     *
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
     int deleteTeachingProgramByIds(Long[] ids);
}
