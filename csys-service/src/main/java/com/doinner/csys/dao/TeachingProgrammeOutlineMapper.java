package com.doinner.csys.dao;

import java.util.List;

import com.doinner.csys.domain.TeachingProgrammeOutline;

/**
 * 教学大纲模板实例Mapper接口
 *
 * @author wzg
 * @date 2026-02-28
 */
public interface TeachingProgrammeOutlineMapper {
    /**
     * 查询教学大纲模板实例
     *
     * @param id 教学大纲模板实例主键
     * @return 教学大纲模板实例
     */
     TeachingProgrammeOutline selectTeachingProgrammeOutlineById(Long id);

    /**
     * 查询教学大纲模板实例列表
     *
     * @param teachingProgrammeOutline 教学大纲模板实例
     * @return 教学大纲模板实例集合
     */
     List<TeachingProgrammeOutline> selectTeachingProgrammeOutlineList(TeachingProgrammeOutline teachingProgrammeOutline);

    /**
     * 新增教学大纲模板实例
     *
     * @param teachingProgrammeOutline 教学大纲模板实例
     * @return 结果
     */
     int insertTeachingProgrammeOutline(TeachingProgrammeOutline teachingProgrammeOutline);

    /**
     * 修改教学大纲模板实例
     *
     * @param teachingProgrammeOutline 教学大纲模板实例
     * @return 结果
     */
     int updateTeachingProgrammeOutline(TeachingProgrammeOutline teachingProgrammeOutline);

    /**
     * 删除教学大纲模板实例
     *
     * @param id 教学大纲模板实例主键
     * @return 结果
     */
     int deleteTeachingProgrammeOutlineById(Long id);

    /**
     * 批量删除教学大纲模板实例
     *
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
     int deleteTeachingProgrammeOutlineByIds(Long[] ids);
}
