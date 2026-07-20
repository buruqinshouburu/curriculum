package com.doinner.csys.service;

import com.doinner.csys.domain.TeachingPlanContext;
import com.doinner.csys.domain.TeachingPlanSection;
import com.doinner.csys.domain.TeachingPlanTeacher;
import com.doinner.csys.domain.vo.TeachingPlanDetailVo;
import com.doinner.csys.domain.vo.TeachingPlanListVo;
import com.doinner.csys.domain.vo.TeachingPlanQueryVo;
import com.doinner.csys.domain.vo.TeachingPlanSaveVo;
import com.doinner.csys.domain.vo.CourseQuoteMajorVo;
import com.doinner.file.api.domain.FileInfo;

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
     * 保存教学计划(含调用课程上下文)。
     * plan.id 为空时按 (sourceCourseId, planType) 查重：已存在则修改，不存在才新增；
     * 保证一门课程同一类型只有一条教学计划。
     *
     * @param saveVo 保存入参
     * @return 教学计划id
     */
    Long saveTeachingPlan(TeachingPlanSaveVo saveVo);

    /**
     * 按总库课程id生成课程教学计划 Word 文档。
     * 根据 t_csys_course.type 决定生成哪一套模板（课程/实验课程/实践训练课目/实践项目），
     * 生成后上传文件服务并回写课程表的 plan_file_id/plan_file_name/plan_download_url/plan_preview_url，
     * 返回文件信息（含 fileId/downloadUrl/previewUrl）。
     *
     * @param courseId 总库课程id
     * @return 文件信息
     */
    FileInfo generateTeachingPlanWord(Long courseId);

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

    /**
     * 按源课程id查询引用该课程的专业类(去重)。
     */
    List<CourseQuoteMajorVo> listQuoteMajors(Long courseId);

    /**
     * 逻辑删除教学计划主表（sysflag=2）。
     * 审核中/已通过的计划不允许删除。
     */
    void deleteTeachingPlan(Long planId);

    // ============ 调用课程上下文 t_csys_teaching_plan_context ============

    /** 按教学计划id查询上下文列表（页面 tab） */
    List<TeachingPlanContext> listContext(Long planId);

    /** 新增上下文，返回主键id */
    Long addContext(TeachingPlanContext context);

    /** 修改上下文 */
    void updateContext(TeachingPlanContext context);

    /** 逻辑删除上下文 */
    void deleteContext(Long id);

    /**
     * 按总库课程调用关系同步上下文：
     * 从 c2+排课+培养方案实时查询，逻辑删除原 plan 下 context 后批量写入新快照。
     *
     * @return 同步后的 context 列表
     */
    List<TeachingPlanContext> syncContexts(Long planId);

    /**
     * 按 planId（优先）或 courseId 生成 Word；planId 指定时按该计划导出。
     */
    FileInfo generateTeachingPlanWord(Long courseId, Long planId);
}
