package com.doinner.csys.service;

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
     * 逻辑参照课程被选用情况(/chooseStatus/{sourceCourseId})：通过 source_id 定位被选用课程，
     * 再经排课表关联培养方案，从培养方案维度取 major_id(专业类)/category_id(门类) 并去重。
     *
     * @param courseId 源课程id
     * @return 引用该课程的专业类集合(学科门类/专业类/专业类ID)
     */
    List<CourseQuoteMajorVo> listQuoteMajors(Long courseId);
}
