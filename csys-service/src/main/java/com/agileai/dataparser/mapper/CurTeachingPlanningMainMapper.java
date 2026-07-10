package com.agileai.dataparser.mapper;

import com.agileai.dataparser.domain.CurTeachingPlanningMain;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 教学计划(新)主表Mapper
 *
 * @author fjn
 * @date 2022-11-03
 */
@Component
public interface CurTeachingPlanningMainMapper {

    /**
     * 查询教学计划(新)主表
     *
     * @param curTeachingPlanningMain
     * @return 教学计划(新)主表
     */
    List<CurTeachingPlanningMain> selectCurTeachingPlanningMainList(CurTeachingPlanningMain curTeachingPlanningMain);

    /**
     * 查询教学计划(新)主表
     *
     * @param id
     * @return 教学计划(新)主表
     */
    CurTeachingPlanningMain selectCurTeachingPlanningMainById(Long id);

    List<CurTeachingPlanningMain> selectCurTeachingPlanningMainByIds(@Param("ids") List<Long> ids);

    /**
     * 新增教学计划(新)主表
     *
     * @param curTeachingPlanningMain 教学计划(新)主表
     * @return 结果
     */
    int insertCurTeachingPlanningMain(CurTeachingPlanningMain curTeachingPlanningMain);

    /**
     * 更新教学计划(新)主表
     *
     * @param curTeachingPlanningMain 教学计划(新)主表
     * @return 结果
     */
    int updateCurTeachingPlanningMain(CurTeachingPlanningMain curTeachingPlanningMain);

    /**
     * 删除教学计划(新)主表
     *
     * @param id
     * @return 结果
     */
    int deleteCurTeachingPlanningMainById(Long id);

    /**
     * 批量删除教学计划(新)主表
     *
     * @param ids
     * @return 结果
     */
    int deleteCurTeachingPlanningMainByIds(Long[] ids);

    /**
     * 审核
     *
     * @return 结果
     */
    int auditCurTeachingPlanning(CurTeachingPlanningMain curTeachingPlanningMain);

    int insertCurTeachingPlanningMainList(@Param("list") List<CurTeachingPlanningMain> curTeachingPlanningMains);

    int updateCurTeachingPlanningMainList(@Param("list") List<CurTeachingPlanningMain> curTeachingPlanningMains);
}
