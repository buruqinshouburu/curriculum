package com.doinner.csys.dao;

import com.doinner.csys.domain.KnowledgePoint;
import com.doinner.csys.domain.vo.KnowledgePointVo;
import com.doinner.csys.domain.vo.KnowledgePointVoMaps;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 知识点Mapper接口
 *
 * @author doinner
 * @date 2023-03-14
 */
public interface KnowledgePointMapper {
    /**
     * 查询知识点
     *
     * @param id 知识点主键
     * @return 知识点
     */
    KnowledgePoint selectKnowledgePointById(Long id);

    /**
     * 查询知识点列表
     *
     * @param knowledgePoint 知识点
     * @return 知识点集合
     */
    List<KnowledgePoint> selectKnowledgePointList(KnowledgePoint knowledgePoint);

    /**
     * 新增知识点
     *
     * @param knowledgePoint 知识点
     * @return 结果
     */
    int insertKnowledgePoint(KnowledgePoint knowledgePoint);
    int insertKnowledgePoints(@Param("knowledgePointList") List<? extends KnowledgePoint> knowledgePointList);

    /**
     * 修改知识点
     *
     * @param knowledgePoint 知识点
     * @return 结果
     */
    int updateKnowledgePoint(KnowledgePoint knowledgePoint);

    /**
     * 删除知识点
     *
     * @param id 知识点主键
     * @return 结果
     */
    int deleteKnowledgePointById(Long id);

    /**
     * 批量删除知识点
     *
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    int deleteKnowledgePointByIds(List<Long> ids);


    List<KnowledgePoint> selectKnowledgePointByUnitId(@Param("unitId") Long unitId);


    List<KnowledgePointVo> checkPoint( Long courseId);

    List<KnowledgePointVoMaps> checkPoints(@Param("courseIds") List<Long> courseIds);

    List<KnowledgePointVoMaps> checkCoursePoints(@Param("courseIds") List<Long> courseIds);

    List<KnowledgePointVoMaps> checkPointsByDomainIds(@Param("domainIds") List<Long> domainIds);

    List<KnowledgePointVoMaps> checkPointsByCourseIds(@Param("courseIds") List<Long> courseIds);

    void updateKnowledgePoints(@Param("pointList") List<? extends KnowledgePoint> pointList);
}
