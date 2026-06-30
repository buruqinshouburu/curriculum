package com.doinner.csys.dao;

import java.util.List;

import com.doinner.csys.domain.TeachingProgrammeInstanceExRefMajor;

/**
 * 文档抽取和专业关联Mapper接口
 *
 * @author wzg
 * @date 2026-03-11
 */
public interface TeachingProgrammeInstanceExRefMajorMapper {
    /**
     * 查询文档抽取和专业关联
     *
     * @param extractId 文档抽取和专业关联主键
     * @return 文档抽取和专业关联
     */
     List<TeachingProgrammeInstanceExRefMajor> selectTeachingProgrammeInstanceExRefMajorByExtractId(Long extractId);

    /**
     * 查询文档抽取和专业关联列表
     *
     * @param teachingProgrammeInstanceExRefMajor 文档抽取和专业关联
     * @return 文档抽取和专业关联集合
     */
     List<TeachingProgrammeInstanceExRefMajor> selectTeachingProgrammeInstanceExRefMajorList(TeachingProgrammeInstanceExRefMajor teachingProgrammeInstanceExRefMajor);

    /**
     * 新增文档抽取和专业关联
     *
     * @param teachingProgrammeInstanceExRefMajor 文档抽取和专业关联
     * @return 结果
     */
     int insertTeachingProgrammeInstanceExRefMajor(TeachingProgrammeInstanceExRefMajor teachingProgrammeInstanceExRefMajor);

    /**
     * 修改文档抽取和专业关联
     *
     * @param teachingProgrammeInstanceExRefMajor 文档抽取和专业关联
     * @return 结果
     */
     int updateTeachingProgrammeInstanceExRefMajor(TeachingProgrammeInstanceExRefMajor teachingProgrammeInstanceExRefMajor);

    /**
     * 删除文档抽取和专业关联
     *
     * @param extractId 文档抽取和专业关联主键
     * @return 结果
     */
     int deleteTeachingProgrammeInstanceExRefMajorByExtractId(Long extractId);

    /**
     * 批量删除文档抽取和专业关联
     *
     * @param extractIds 需要删除的数据主键集合
     * @return 结果
     */
     int deleteTeachingProgrammeInstanceExRefMajorByExtractIds(Long[] extractIds);
}
