package com.doinner.csys.dao;

import java.util.List;

import com.doinner.csys.domain.TeachingProgrammeInstanceExtract;

/**
 * 教育大纲从文档抽取内容Mapper接口
 *
 * @author wzg
 * @date 2026-03-11
 */
public interface TeachingProgrammeInstanceExtractMapper {
    /**
     * 查询教育大纲从文档抽取内容
     *
     * @param id 教育大纲从文档抽取内容主键
     * @return 教育大纲从文档抽取内容
     */
     TeachingProgrammeInstanceExtract selectTeachingProgrammeInstanceExtractById(Long id);
     List<TeachingProgrammeInstanceExtract> selectTeachingProgrammeInstanceExtractByInstanceId(Long instanceId);

    /**
     * 查询教育大纲从文档抽取内容列表
     *
     * @param teachingProgrammeInstanceExtract 教育大纲从文档抽取内容
     * @return 教育大纲从文档抽取内容集合
     */
     List<TeachingProgrammeInstanceExtract> selectTeachingProgrammeInstanceExtractList(TeachingProgrammeInstanceExtract teachingProgrammeInstanceExtract);

    /**
     * 新增教育大纲从文档抽取内容
     *
     * @param teachingProgrammeInstanceExtract 教育大纲从文档抽取内容
     * @return 结果
     */
     int insertTeachingProgrammeInstanceExtract(TeachingProgrammeInstanceExtract teachingProgrammeInstanceExtract);

    /**
     * 修改教育大纲从文档抽取内容
     *
     * @param teachingProgrammeInstanceExtract 教育大纲从文档抽取内容
     * @return 结果
     */
     int updateTeachingProgrammeInstanceExtract(TeachingProgrammeInstanceExtract teachingProgrammeInstanceExtract);

    /**
     * 删除教育大纲从文档抽取内容
     *
     * @param id 教育大纲从文档抽取内容主键
     * @return 结果
     */
     int deleteTeachingProgrammeInstanceExtractById(Long id);

    /**
     * 批量删除教育大纲从文档抽取内容
     *
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
     int deleteTeachingProgrammeInstanceExtractByIds(Long[] ids);
}
