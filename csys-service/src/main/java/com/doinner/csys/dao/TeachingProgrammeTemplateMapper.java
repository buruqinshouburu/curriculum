package com.doinner.csys.dao;

import java.util.List;

import com.doinner.csys.domain.TeachingProgrammeTemplate;

/**
 * 教学大纲模板Mapper接口
 *
 * @author wzg
 * @date 2026-02-27
 */
public interface TeachingProgrammeTemplateMapper {
    /**
     * 查询教学大纲模板
     *
     * @param id 教学大纲模板主键
     * @return 教学大纲模板
     */
     TeachingProgrammeTemplate selectTeachingProgrammeTemplateById(Long id);

    /**
     * 查询教学大纲模板列表
     *
     * @param teachingProgrammeTemplate 教学大纲模板
     * @return 教学大纲模板集合
     */
     List<TeachingProgrammeTemplate> selectTeachingProgrammeTemplateList(TeachingProgrammeTemplate teachingProgrammeTemplate);

    /**
     * 新增教学大纲模板
     *
     * @param teachingProgrammeTemplate 教学大纲模板
     * @return 结果
     */
     int insertTeachingProgrammeTemplate(TeachingProgrammeTemplate teachingProgrammeTemplate);

    /**
     * 修改教学大纲模板
     *
     * @param teachingProgrammeTemplate 教学大纲模板
     * @return 结果
     */
     int updateTeachingProgrammeTemplate(TeachingProgrammeTemplate teachingProgrammeTemplate);

    /**
     * 删除教学大纲模板
     *
     * @param id 教学大纲模板主键
     * @return 结果
     */
     int deleteTeachingProgrammeTemplateById(Long id);

     int deleteTeachingProgrammeAttributeByOutlineId(Long id);

    /**
     * 批量删除教学大纲模板
     *
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
     int deleteTeachingProgrammeTemplateByIds(Long[] ids);

    int deleteTeachingProgrammeAttributeByOutlineIds(Long[] outlineIds);
}
