package com.agileai.dataparser.mapper;

import com.agileai.dataparser.domain.CurTeachingPlanningPractice;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 教学计划(新)实践表Mapper
 *
 * @author fjn
 * @date 2022-11-03
 */
@Component
public interface CurTeachingPlanningPracticeMapper {

    /**
     * 查询教学计划(新)实践表
     *
     * @param curTeachingPlanningPractice
     * @return 教学计划(新)实践表
     */
    List<CurTeachingPlanningPractice> selectCurTeachingPlanningPracticeList(CurTeachingPlanningPractice curTeachingPlanningPractice);

    /**
     * 查询教学计划(新)实践表
     *
     * @param id
     * @return 教学计划(新)实践表
     */
    CurTeachingPlanningPractice selectCurTeachingPlanningPracticeById(Long id);

    /**
     * 根据主表id查询教学计划(新)实践表
     *
     * @param mainId
     * @return 教学计划(新)实践表
     */
    CurTeachingPlanningPractice selectCurTeachingPlanningPracticeByMainId(Long mainId);

    List<CurTeachingPlanningPractice> selectCurTeachingPlanningPracticeByMainIds(@Param("mainIds") List<Long> mainIds);

    /**
     * 新增教学计划(新)实践表
     *
     * @param curTeachingPlanningPractice 教学计划(新)实践表
     * @return 结果
     */
    int insertCurTeachingPlanningPractice(CurTeachingPlanningPractice curTeachingPlanningPractice);

    /**
     * 更新教学计划(新)实践表
     *
     * @param curTeachingPlanningPractice 教学计划(新)实践表
     * @return 结果
     */
    int updateCurTeachingPlanningPractice(CurTeachingPlanningPractice curTeachingPlanningPractice);

    /**
     * 删除教学计划(新)实践表
     *
     * @param id
     * @return 结果
     */
    int deleteCurTeachingPlanningPracticeById(Long id);

    /**
     * 批量删除教学计划(新)实践表
     *
     * @param ids
     * @return 结果
     */
    int deleteCurTeachingPlanningPracticeByIds(Long[] ids);

    /**
     * 批量删除教学计划(新)实践表
     *
     * @param ids
     * @return 结果
     */
    int deleteCurTeachingPlanningPracticeByMainIds(Long[] ids);

    int insertCurTeachingPlanningPracticeList(@Param("list") List<CurTeachingPlanningPractice> curTeachingPlanningPractices);

    int updateCurTeachingPlanningPracticeList(@Param("list") List<CurTeachingPlanningPractice> curTeachingPlanningPractices);

}
