package com.doinner.csys.dao;

import java.util.List;

import com.doinner.csys.domain.KnowledgeNoCheckLog;
import org.apache.ibatis.annotations.Param;

/**
 * 没有重复知识点的ID组合Mapper接口
 *
 * @author doinner
 * @date 2023-03-27
 */
public interface KnowledgeNoCheckLogMapper {
    /**
     * 查询没有重复知识点的ID组合
     *
     * @param id 没有重复知识点的ID组合主键
     * @return 没有重复知识点的ID组合
     */
    KnowledgeNoCheckLog selectKnowledgeNoCheckLogById(Long id);

    /**
     * 查询没有重复知识点的ID组合列表
     *
     * @param knowledgeNoCheckLog 没有重复知识点的ID组合
     * @return 没有重复知识点的ID组合集合
     */
    List<KnowledgeNoCheckLog> selectKnowledgeNoCheckLogList(KnowledgeNoCheckLog knowledgeNoCheckLog);

    /**
     * 新增没有重复知识点的ID组合
     *
     * @param knowledgeNoCheckLog 没有重复知识点的ID组合
     * @return 结果
     */
    int insertKnowledgeNoCheckLog(KnowledgeNoCheckLog knowledgeNoCheckLog);

    /**
     * 修改没有重复知识点的ID组合
     *
     * @param knowledgeNoCheckLog 没有重复知识点的ID组合
     * @return 结果
     */
    int updateKnowledgeNoCheckLog(KnowledgeNoCheckLog knowledgeNoCheckLog);

    /**
     * 删除没有重复知识点的ID组合
     *
     * @param id 没有重复知识点的ID组合主键
     * @return 结果
     */
    int deleteKnowledgeNoCheckLogById(Long id);

    int deleteBySourceDomainId(@Param("sourceDomainId") Long sourceDomainId);

    /**
     * 批量删除没有重复知识点的ID组合
     *
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    int deleteKnowledgeNoCheckLogByIds(Long[] ids);


    List<KnowledgeNoCheckLog>selectBySourceCourseIdList(@Param("courseIds") List<Long> courseIds);

    void deleteByCourseId(@Param("courseIds")List<Long> courseIds);
}
