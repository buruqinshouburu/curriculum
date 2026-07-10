package com.agileai.dataparser.mapper;

import com.agileai.dataparser.domain.CurTeachingPlanningTheory;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 教学计划(新)理论表Mapper
 *
 * @author fjn
 * @date 2022-11-03
 */
@Component
public interface CurTeachingPlanningTheoryMapper {

    /**
     * 查询教学计划(新)理论表
     *
     * @param curTeachingPlanningTheory
     * @return 教学计划(新)理论表
     */
    List<CurTeachingPlanningTheory> selectCurTeachingPlanningTheoryList(CurTeachingPlanningTheory curTeachingPlanningTheory);

    /**
     * 查询教学计划(新)理论表
     *
     * @param id
     * @return 教学计划(新)理论表
     */
    CurTeachingPlanningTheory selectCurTeachingPlanningTheoryById(Long id);

    /**
     * 根据主表id查询教学计划(新)理论表
     *
     * @param mainId
     * @return 教学计划(新)理论表
     */
    CurTeachingPlanningTheory selectCurTeachingPlanningTheoryByMainId(Long mainId);

    List<CurTeachingPlanningTheory> selectCurTeachingPlanningTheoryByMainIds(@Param("mainIds") List<Long> mainIds);

    /**
     * 新增教学计划(新)理论表
     *
     * @param curTeachingPlanningTheory 教学计划(新)理论表
     * @return 结果
     */
    int insertCurTeachingPlanningTheory(CurTeachingPlanningTheory curTeachingPlanningTheory);

    /**
     * 更新教学计划(新)理论表
     *
     * @param curTeachingPlanningTheory 教学计划(新)理论表
     * @return 结果
     */
    int updateCurTeachingPlanningTheory(CurTeachingPlanningTheory curTeachingPlanningTheory);

    /**
     * 删除教学计划(新)理论表
     *
     * @param id
     * @return 结果
     */
    int deleteCurTeachingPlanningTheoryById(Long id);

    /**
     * 批量删除教学计划(新)理论表
     *
     * @param ids
     * @return 结果
     */
    int deleteCurTeachingPlanningTheoryByIds(Long[] ids);

    /**
     * 批量删除教学计划(新)理论表
     *
     * @param ids
     * @return 结果
     */
    int deleteCurTeachingPlanningTheoryByMainIds(Long[] ids);

    int insertCurTeachingPlanningTheoryList(@Param("list") List<CurTeachingPlanningTheory> curTeachingPlanningTheorys);

    int updateCurTeachingPlanningTheoryList(@Param("list") List<CurTeachingPlanningTheory> curTeachingPlanningTheorys);
    
}
