package com.agileai.dataparser.mapper;

import com.agileai.dataparser.domain.CurTeachingPlanningTheoryChapter;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 教学计划(新)理论章节表Mapper
 *
 * @author fjn
 * @date 2022-11-03
 */
@Component
public interface CurTeachingPlanningTheoryChapterMapper {

    /**
     * 查询教学计划(新)理论章节表
     *
     * @param curTeachingPlanningTheoryChapter
     * @return 教学计划(新)理论章节表
     */
    List<CurTeachingPlanningTheoryChapter> selectCurTeachingPlanningTheoryChapterList(CurTeachingPlanningTheoryChapter curTeachingPlanningTheoryChapter);

    /**
     * 查询教学计划(新)理论章节表
     *
     * @param id
     * @return 教学计划(新)理论章节表
     */
    CurTeachingPlanningTheoryChapter selectCurTeachingPlanningTheoryChapterById(Long id);

    /**
     * 根据主表id查询教学计划(新)理论章节表
     *
     * @param mainId
     * @return 教学计划(新)理论章节表
     */
    List<CurTeachingPlanningTheoryChapter> selectCurTeachingPlanningTheoryChapterByMainId(Long mainId);

    /**
     * 新增教学计划(新)理论章节表
     *
     * @param curTeachingPlanningTheoryChapter 教学计划(新)理论章节表
     * @return 结果
     */
    int insertCurTeachingPlanningTheoryChapter(CurTeachingPlanningTheoryChapter curTeachingPlanningTheoryChapter);

    /**
     * 更新教学计划(新)理论章节表
     *
     * @param curTeachingPlanningTheoryChapter 教学计划(新)理论章节表
     * @return 结果
     */
    int updateCurTeachingPlanningTheoryChapter(CurTeachingPlanningTheoryChapter curTeachingPlanningTheoryChapter);

    /**
     * 删除教学计划(新)理论章节表
     *
     * @param id
     * @return 结果
     */
    int deleteCurTeachingPlanningTheoryChapterById(Long id);

    /**
     * 批量删除教学计划(新)理论章节表
     *
     * @param ids
     * @return 结果
     */
    int deleteCurTeachingPlanningTheoryChapterByIds(Long[] ids);

    /**
     * 批量删除教学计划(新)理论章节表
     *
     * @param mainId
     * @return 结果
     */
    int deleteCurTeachingPlanningTheoryChapterByMainId(Long mainId);

    List<CurTeachingPlanningTheoryChapter> selectCurTeachingPlanByParentId(Long parentId);

    int insertCurTeachingPlanningTheoryChapterList(@Param("list") List<CurTeachingPlanningTheoryChapter> curTeachingPlanningTheoryChapters);
    
}
