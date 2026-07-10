package com.agileai.dataparser.mapper;

import com.agileai.dataparser.domain.CurTeachingPlanningBook;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 教学计划(新)教材表Mapper
 *
 * @author fjn
 * @date 2022-11-03
 */
@Component
public interface CurTeachingPlanningBookMapper {

    /**
     * 查询教学计划(新)教材表
     *
     * @param curTeachingPlanningBook
     * @return 教学计划(新)教材表
     */
    List<CurTeachingPlanningBook> selectCurTeachingPlanningBookList(CurTeachingPlanningBook curTeachingPlanningBook);

    /**
     * 查询教学计划(新)教材表
     *
     * @param id
     * @return 教学计划(新)教材表
     */
    CurTeachingPlanningBook selectCurTeachingPlanningBookById(Long id);

    /**
     * 查询教学计划(新)教材表
     *
     * @param mainId
     * @return 教学计划(新)教材表
     */
    List<CurTeachingPlanningBook> selectCurTeachingPlanningBookByMainId(Long mainId);

    /**
     * 新增教学计划(新)教材表
     *
     * @param curTeachingPlanningBook 教学计划(新)教材表
     * @return 结果
     */
    int insertCurTeachingPlanningBook(CurTeachingPlanningBook curTeachingPlanningBook);

    /**
     * 更新教学计划(新)教材表
     *
     * @param curTeachingPlanningBook 教学计划(新)教材表
     * @return 结果
     */
    int updateCurTeachingPlanningBook(CurTeachingPlanningBook curTeachingPlanningBook);

    /**
     * 删除教学计划(新)教材表
     *
     * @param id
     * @return 结果
     */
    int deleteCurTeachingPlanningBookById(Long id);

    /**
     * 批量删除教学计划(新)教材表
     *
     * @param ids
     * @return 结果
     */
    int deleteCurTeachingPlanningBookByIds(Long[] ids);

    /**
     * 批量删除教学计划(新)教材表
     *
     * @param mainId
     * @return 结果
     */
    int deleteCurTeachingPlanningBookByMainId(Long mainId);

    int insertCurTeachingPlanningBookList(@Param("list") List<CurTeachingPlanningBook> curTeachingPlanningBooks);
}
