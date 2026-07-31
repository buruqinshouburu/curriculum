package com.doinner.csys.dao;

import com.doinner.csys.domain.TeachingPlan;
import com.doinner.csys.domain.vo.TeachingPlanDetailVo;
import com.doinner.csys.domain.vo.TeachingPlanListVo;
import com.doinner.csys.domain.vo.TeachingPlanQueryVo;
import com.doinner.csys.domain.vo.TeachingPlanQuoteAggVo;
import com.doinner.csys.domain.vo.TeachingPlanSupportingAggVo;
import com.doinner.csys.domain.CourseSchedule;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 课程教学计划主表Mapper
 */
public interface TeachingPlanMapper {

    /**
     * 单条新增，新增后回填自增主键id
     *
     * @param teachingPlan 教学计划
     * @return 影响行数
     */
    int insert(TeachingPlan teachingPlan);

    /**
     * 根据主键更新
     *
     * @param teachingPlan 教学计划
     * @return 影响行数
     */
    int updateById(TeachingPlan teachingPlan);

    /**
     * 根据主键查询
     *
     * @param id 主键
     * @return 教学计划
     */
    TeachingPlan selectById(@Param("id") Long id);

    /**
     * 根据总库课程ID查询当前版本教学计划
     *
     * @param sourceCourseId 总库课程ID
     * @return 教学计划
     */
    TeachingPlan selectBySourceCourseId(@Param("sourceCourseId") Long sourceCourseId);

    /**
     * 根据总库课程ID + 教学计划类型查询有效记录。
     * 用于保存时判断「一门课程同一类型只能一条」：已存在则改、不存在才增。
     *
     * @param sourceCourseId 总库课程ID
     * @param planType       教学计划类型
     * @return 教学计划(可能为空)
     */
    TeachingPlan selectBySourceCourseIdAndPlanType(@Param("sourceCourseId") Long sourceCourseId,
                                                   @Param("planType") Integer planType);

    /**
     * 查询列表
     *
     * @param teachingPlan 查询条件
     * @return 教学计划列表
     */
    List<TeachingPlan> selectList(TeachingPlan teachingPlan);

    /**
     * 根据主键逻辑删除
     *
     * @param id 主键
     * @return 影响行数
     */
    int deleteById(@Param("id") Long id);

    /**
     * 课程教学计划管理列表：以总库课程为主表(source_id is null)，left join 教学计划表。
     * 支持课程名称/课程编号模糊，开课单位、适用对象、课程模块、修读要求精确过滤。
     * 分页由调用方使用 PageHelper.startPage 控制。
     * <p>
     * 列表只返回总库课程自身字段；适用对象/专业/修读性质/课程模块的被引用聚合
     * 由 {@link #selectQuoteAggByCourseIds(List)} 对当前页批量补全。
     *
     * @param query 查询条件
     * @return 列表数据
     */
    List<TeachingPlanListVo> selectTeachingPlanPage(TeachingPlanQueryVo query);

    /**
     * 按总库课程id批量聚合被引用侧字段（适用对象/专业名/修读性质/课程模块）。
     * 仅用于列表当前页补全，避免在分页主 SQL 中做相关子查询。
     *
     * @param courseIds 总库课程id集合
     * @return 按源课程聚合结果
     */
    List<TeachingPlanQuoteAggVo> selectQuoteAggByCourseIds(@Param("courseIds") List<Long> courseIds);

    /**
     * 教学计划详情-课程分支：教学计划id为空时，详情字段全部取自总库课程 t_csys_course。
     *
     * @param courseId 总库课程id
     * @return 详情
     */
    TeachingPlanDetailVo selectDetailByCourseId(@Param("courseId") Long courseId);

    /**
     * 教学计划详情-计划分支：教学计划id存在时，基础信息取自 t_csys_teaching_plan，
     * 适用对象/开课学期/课程模块/适用专业/修读性质/学时学分取自 t_csys_teaching_plan_context。
     *
     * @param teachingPlanId 教学计划id
     * @return 详情
     */
    TeachingPlanDetailVo selectDetailByPlanId(@Param("teachingPlanId") Long teachingPlanId);

    /**
     * 支撑课程被引用侧聚合（原始值）：每条支撑课程取修读性质(c2.course_attr 聚合回退自身)、
     * 学期安排(t_csys_training_scheme_course_schedule.term via c2->tcs 聚合，无被调用为 null)。
     *
     * @param courseIds 支撑课程id集合
     * @return 按 courseId 聚合结果
     */
    List<TeachingPlanSupportingAggVo> selectSupportingAgg(@Param("courseIds") List<Long> courseIds);

    /**
     * 支撑课程被调用课 c2 的时间安排对 (time_Week, unit) 去重列表。
     * Service 层按 courseId 分组；某课程无 c2 时返回无该 courseId 行，由调用方回退课程自身字段。
     *
     * @param courseIds 支撑课程id集合
     * @return (courseId, timeWeek, unit) 去重行
     */
    List<CourseSchedule> selectSupportingTimePairs(@Param("courseIds") List<Long> courseIds);

    /**
     * 批量查询课程子表 t_csys_course_ref_schedule 行(semester_Schedule + spring_Autumn)，
     * 供支撑课程「无被调用」时学期安排回退拼接。
     *
     * @param courseIds 课程id集合
     * @return 排课子表行
     */
    List<CourseSchedule> selectCourseRefScheduleBatch(@Param("courseIds") List<Long> courseIds);
}
