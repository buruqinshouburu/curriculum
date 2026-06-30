package com.doinner.csys.service;

import com.doinner.csys.domain.KnowledgeCheckLog;
import com.doinner.csys.domain.vo.KnowledgeChekTotalVo;

import java.util.List;

public interface KnowledgeCheckLogService {

    //根据课程ID查重知识点
    void knowledgePointCheck(List<Long> sourceDomainIds);

    //根据课程ID查看知识点查重list+统计
    KnowledgeChekTotalVo selectCheckPointLog(List<Long> courseIds);

    //根据课程ID查看知识点查重统计
    KnowledgeChekTotalVo selectCheckPointLogNoList(List<Long> courseIds);


    //导出培养规划知识点查重list
    List<KnowledgeCheckLog> selectCheckPointLogListBySchemeId(List<Long> sourceDomainIds);

    //修改相似记录
    void similePoint(KnowledgeCheckLog KnowledgeCheckLog);


    //根据学院ID查询课程ID
    List<Long> getCourseIdsByCollegeId(Long collegeId);

    //根据培养方案ID查询课程ID
    List<Long> getCourseIdsBySchemeId(Long schemeId);

     List<KnowledgeCheckLog> selectKnowledgeCheckLogs(List<Long> courseIds);


}
