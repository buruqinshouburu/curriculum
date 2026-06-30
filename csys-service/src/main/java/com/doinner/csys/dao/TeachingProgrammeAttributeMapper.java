package com.doinner.csys.dao;

import java.util.List;

import com.doinner.csys.domain.TeachingProgrammeAttribute;

/**
 * 教学大纲属性Mapper接口
 *
 * @author wzg
 * @date 2026-02-27
 */
public interface TeachingProgrammeAttributeMapper {
    /**
     * 查询教学大纲属性
     *
     * @param id 教学大纲属性主键
     * @return 教学大纲属性
     */
     TeachingProgrammeAttribute selectTeachingProgrammeAttributeById(Long id);

    /**
     * 查询教学大纲属性列表
     *
     * @param teachingProgrammeAttribute 教学大纲属性
     * @return 教学大纲属性集合
     */
     List<TeachingProgrammeAttribute> selectTeachingProgrammeAttributeList(TeachingProgrammeAttribute teachingProgrammeAttribute);

    /**
     * 新增教学大纲属性
     *
     * @param teachingProgrammeAttribute 教学大纲属性
     * @return 结果
     */
     int insertTeachingProgrammeAttribute(TeachingProgrammeAttribute teachingProgrammeAttribute);

    /**
     * 修改教学大纲属性
     *
     * @param teachingProgrammeAttribute 教学大纲属性
     * @return 结果
     */
     int updateTeachingProgrammeAttribute(TeachingProgrammeAttribute teachingProgrammeAttribute);

    /**
     * 删除教学大纲属性
     *
     * @param id 教学大纲属性主键
     * @return 结果
     */
     int deleteTeachingProgrammeAttributeById(Long id);

     int deleteTeachingProgrammeAttributeByInstanceId(Long id);

    /**
     * 批量删除教学大纲属性
     *
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
     int deleteTeachingProgrammeAttributeByIds(Long[] ids);

}
