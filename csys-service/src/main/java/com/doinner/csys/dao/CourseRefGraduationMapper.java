package com.doinner.csys.dao;

import java.util.List;

import com.doinner.csys.domain.CourseRefGraduation;
import com.doinner.csys.domain.CourseRefSourceDomain;
import com.doinner.csys.domain.vo.GraduationRefCourseVo;
import org.apache.ibatis.annotations.Param;

/**
 * 课程和毕业标准Mapper接口
 *
 * @author wzg
 * @date 2026-03-06
 */
public interface CourseRefGraduationMapper {
    /**
     * 查询课程和毕业标准
     *
     * @param courseId 课程和毕业标准主键
     * @return 课程和毕业标准
     */
     List<CourseRefGraduation> selectCourseRefGraduationByCourseId(Long courseId);

     List<CourseRefGraduation> selectCourseRefGraduationByCourseTargetId(Long courseId);

    /**
     * 查询课程和毕业标准列表
     *
     * @param courseRefGraduation 课程和毕业标准
     * @return 课程和毕业标准集合
     */
     List<CourseRefGraduation> selectCourseRefGraduationList(CourseRefGraduation courseRefGraduation);

    /**
     * 新增课程和毕业标准
     *
     * @param courseRefGraduation 课程和毕业标准
     * @return 结果
     */
     int insertCourseRefGraduation(CourseRefGraduation courseRefGraduation);

    /**
     * 修改课程和毕业标准
     *
     * @param courseRefGraduation 课程和毕业标准
     * @return 结果
     */
     int updateCourseRefGraduation(CourseRefGraduation courseRefGraduation);

    /**
     * 删除课程和毕业标准
     *
     * @param courseId 课程和毕业标准主键
     * @return 结果
     */
     int deleteCourseRefGraduationByCourseId(@Param("courseId") Long courseId,
                                             @Param("courseTargetId") Long courseTargetId,
                                                 @Param("collegeId") Long collegeId,
                                                 @Param("categoryId") Long categoryId,
                                                 @Param("majorId") Long majorId);

    /**
     * 批量删除课程和毕业标准
     *
     * @param courseIds 需要删除的数据主键集合
     * @return 结果
     */
     int deleteCourseRefGraduationByCourseIds(Long[] courseIds);

    List<CourseRefGraduation> selectRefBygraduationIds(@Param("ids") List<Long> gIds);

    void deleteCourseRefGraduationByGraduationIds(@Param("ids") List<Long> gIds);

    List<CourseRefGraduation> selectCourseTargetRefGraduationByCourseIds(@Param("courseIds") List<Long> courseIds);

    void insertCourseTargetRefGraduationList(@Param("courseTargetRefGraduationList")List<CourseRefGraduation> courseTargetRefGraduationList);

    int deleteCourseRefGraduationByCourseId(@Param("courseId") Long courseId);

    /**
     * 按课程id集合查询已有的课程-毕业要求绑定(course_id + graduation_id)，
     * 用于同步时跳过已存在的绑定关系(只追加不覆盖)。
     *
     * @param courseIds 课程id集合
     * @return 绑定关系集合
     */
    List<CourseRefGraduation> selectExistingRefByCourseIds(@Param("courseIds") List<Long> courseIds);

    /**
     * 按毕业要求(叶子)id集合查询绑定的课程(含课程名称/编号)，用于毕业要求与课程支撑矩阵。
     * 仅返回未删除(sysflag=0)的课程。
     *
     * @param graduationIds 毕业要求id集合
     * @return 绑定关系集合(含课程信息)
     */
    List<GraduationRefCourseVo> selectCourseRefGraduationWithCourseByGraduationIds(@Param("ids") List<Long> graduationIds);
}
