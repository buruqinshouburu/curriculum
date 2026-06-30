package com.doinner.csys.dao;

import java.util.List;

import com.doinner.csys.domain.KnowledgeCheckLog;
import com.doinner.csys.domain.vo.KnowledgeCheckLogVo;
import org.apache.ibatis.annotations.Param;

/**
 * 知识点查重意见Mapper接口
 *
 * @author doinner
 * @date 2023-03-23
 */
public interface KnowledgeCheckLogMapper {


    /**
     * 新增知识点查重意见
     */
    int insertKnowledgeCheckLog(KnowledgeCheckLog knowledgeCheckLog);

    /**
     * 修改知识点查重意见
     */
    int updateKnowledgeCheckLog(KnowledgeCheckLog knowledgeCheckLog);

    /**
     * 根据课程id删除数据
     */
    void deleteBySourceDomainId(@Param("sourceDomainId") Long sourceDomainId);


    //查询相似数据查询数据
    List<KnowledgeCheckLog> selectKnowledgeCheckLogListByCourses(@Param("ids") List<Long> ids);

    //查询相似数据查询数据
    List<KnowledgeCheckLog> selectKnowledgeCheckLogListAll();

    //统计知识点
    List<KnowledgeCheckLog> countTPoint(@Param("targetCIds") List<Long> targetCIds);

    //统计知识点
    List<KnowledgeCheckLog> countSPoint(@Param("sourceCIds") List<Long> sourceCIds);

    //查询所有数据
    List<KnowledgeCheckLog> selectBySourceCourseIdList(@Param("sourceDomainIds") List<Long> sourceDomainIds);


    int deleteByCourseId(@Param("courseIds") List<Long> courseIds);
}
