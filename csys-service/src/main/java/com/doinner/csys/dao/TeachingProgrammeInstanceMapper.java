package com.doinner.csys.dao;

import java.util.List;

import com.doinner.csys.domain.TeachingProgrammeInstance;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.mongodb.repository.Update;

/**
 * 教学大纲实例Mapper接口
 *
 * @author wzg
 * @date 2026-02-27
 */
public interface TeachingProgrammeInstanceMapper {
    /**
     * 查询教学大纲实例
     *
     * @param id 教学大纲实例主键
     * @return 教学大纲实例
     */
     TeachingProgrammeInstance selectTeachingProgrammeInstanceById(Long id);

    /**
     * 查询教学大纲实例列表
     *
     * @param teachingProgrammeInstance 教学大纲实例
     * @return 教学大纲实例集合
     */
     List<TeachingProgrammeInstance> selectTeachingProgrammeInstanceList(TeachingProgrammeInstance teachingProgrammeInstance);

    /**
     * 新增教学大纲实例
     *
     * @param teachingProgrammeInstance 教学大纲实例
     * @return 结果
     */
     int insertTeachingProgrammeInstance(TeachingProgrammeInstance teachingProgrammeInstance);

    /**
     * 修改教学大纲实例
     *
     * @param teachingProgrammeInstance 教学大纲实例
     * @return 结果
     */
     int updateTeachingProgrammeInstance(TeachingProgrammeInstance teachingProgrammeInstance);

    /**
     * 删除教学大纲实例
     *
     * @param id 教学大纲实例主键
     * @return 结果
     */
     int deleteTeachingProgrammeInstanceById(Long id);

    /**
     * 批量删除教学大纲实例
     *
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
     int deleteTeachingProgrammeInstanceByIds(Long[] ids);

     int updateInstanceFileId(@Param("downloadUrl") String downloadUrl,
                              @Param("previewUrl") String previewUrl,
                              @Param("fileId") String fileId,
                              @Param("instanceId")Long instanceId);
}
