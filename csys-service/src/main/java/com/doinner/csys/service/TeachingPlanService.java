package com.doinner.csys.service;

import com.doinner.csys.domain.TeachingPlanSection;
import com.doinner.csys.domain.TeachingPlanTeacher;
import com.doinner.csys.domain.vo.TeachingPlanDetailVo;
import com.doinner.csys.domain.vo.TeachingPlanListVo;
import com.doinner.csys.domain.vo.TeachingPlanQueryVo;
import com.doinner.csys.domain.vo.TeachingPlanSaveVo;

import java.util.List;

/**
 * 课程教学计划 Service。
 *
 * @author codex
 */
public interface TeachingPlanService {

    /**
     * 课程教学计划管理列表查询(以总库课程为主表，left join 教学计划表)。
     * 分页由控制层通过 PageUtils.startPage 开启，返回列表由 DataTable 包装。
     *
     * @param query 查询条件
     * @return 列表数据
     */
    List<TeachingPlanListVo> selectTeachingPlanPage(TeachingPlanQueryVo query);

    /**
     * 教学计划详情。
     * 教学计划id为空时字段取自总库课程；教学计划id存在时取自教学计划表 + 调用课程上下文。
     *
     * @param courseId       总库课程id
     * @param teachingPlanId 教学计划id(可为空)
     * @return 详情
     */
    TeachingPlanDetailVo getDetail(Long courseId, Long teachingPlanId);

    /**
     * 保存教学计划(含调用课程上下文)。没有教学计划id则新增，有则修改。
     *
     * @param saveVo 保存入参
     * @return 教学计划id
     */
    Long saveTeachingPlan(TeachingPlanSaveVo saveVo);

    // ============ 教员团队 t_csys_teaching_plan_teacher ============

    /** 按教学计划id查询教员团队列表 */
    List<TeachingPlanTeacher> listTeacher(Long planId);

    /** 新增教员，返回主键id */
    Long addTeacher(TeachingPlanTeacher teacher);

    /** 修改教员 */
    void updateTeacher(TeachingPlanTeacher teacher);

    /** 删除教员(逻辑删除) */
    void deleteTeacher(Long id);

    // ============ 课程章节 t_csys_teaching_plan_section ============

    /** 按教学计划id查询章节列表 */
    List<TeachingPlanSection> listSection(Long planId);

    /** 新增章节，返回主键id */
    Long addSection(TeachingPlanSection section);

    /** 修改章节 */
    void updateSection(TeachingPlanSection section);

    /** 删除章节(逻辑删除) */
    void deleteSection(Long id);
}
