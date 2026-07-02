package com.doinner.csys.dao;

import com.doinner.csys.domain.TrainingSchemeRefCourse;
import com.doinner.csys.domain.vo.TrainingCourseVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 培养方案课程关联Mapper接口
 *
 * @author doinner
 */
public interface TrainingSchemeRefCourseMapper {
    /**
     * 查询培养方案课程关联
     *
     * @param id 培养方案课程关联主键
     * @return 培养方案课程关联
     */
    TrainingSchemeRefCourse selectTrainingSchemeRefCourseById(Long id);

    /**
     * 查询培养方案课程关联列表
     *
     * @param trainingSchemeRefCourse 培养方案课程关联
     * @return 培养方案课程关联集合
     */
    List<TrainingSchemeRefCourse> selectTrainingSchemeRefCourseList(TrainingSchemeRefCourse trainingSchemeRefCourse);

    /**
     * 新增培养方案课程关联
     *
     * @param trainingSchemeRefCourse 培养方案课程关联
     * @return 结果
     */
    int insertTrainingSchemeRefCourse(TrainingSchemeRefCourse trainingSchemeRefCourse);

    /**
     * 批量新增培养方案课程关联
     *
     * @param trainingSchemeRefCourses 培养方案课程关联列表
     * @return 结果
     */
    int insertTrainingSchemeRefCourses(@Param("trainingSchemeRefCourses") List<TrainingSchemeRefCourse> trainingSchemeRefCourses);

    /**
     * 修改培养方案课程关联
     *
     * @param trainingSchemeRefCourse 培养方案课程关联
     * @return 结果
     */
    int updateTrainingSchemeRefCourse(TrainingSchemeRefCourse trainingSchemeRefCourse);

    /**
     * 删除培养方案课程关联
     *
     * @param id 培养方案课程关联主键
     * @return 结果
     */
    int deleteTrainingSchemeRefCourseById(Long id);

    /**
     * 批量删除培养方案课程关联
     *
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    int deleteTrainingSchemeRefCourseByIds(Long[] ids);

    int deleteTrainingSchemeRefCourseBySchemeId(Long schemeId);

    int deleteTrainingSchemeRefCourseBySchemeIds(Long[] schemeIds);

    List<TrainingSchemeRefCourse> selectTrainingSchemeRefCourseByTrainingSchemeVoId(Long schemeId);
    List<TrainingSchemeRefCourse> selectTrainingSchemeRefCourseByTrainingIdAndType(@Param("schemeId")Long schemeId
            ,@Param("type")String type);

    int deleteTrainingSchemeRefCourseByCourseIds(@Param("ids") List<Long> ids);

    /**
     * 按课程id集合查询培养方案课程关联，用于根据调用课程反查所属培养方案
     *
     * @param ids 课程id集合
     * @return 培养方案课程关联集合
     */
    List<TrainingSchemeRefCourse> selectByCourseIds(@Param("ids") List<Long> ids);

    List<TrainingSchemeRefCourse> selectTrainingSchemeRefCourseByTrainingSchemeVoIdAndCourseType(@Param("trainingCourseVo") TrainingCourseVo trainingCourseVo);
}
