package com.doinner.csys.service;

import com.doinner.csys.domain.TeachingPlanSection;
import com.doinner.csys.domain.TeachingPlanTeacher;
import com.doinner.csys.domain.vo.CourseQuoteMajorVo;
import com.doinner.csys.domain.vo.TeachingPlanDetailVo;
import com.doinner.csys.domain.vo.TeachingPlanImportResultVo;
import com.doinner.csys.domain.vo.TeachingPlanListVo;
import com.doinner.csys.domain.vo.TeachingPlanQueryVo;
import com.doinner.csys.domain.vo.TeachingPlanSaveVo;
import com.doinner.csys.domain.vo.TeachingPlanTeacherBatchSaveVo;
import com.doinner.csys.domain.vo.TeachingPlanTeamCandidateVo;
import com.doinner.file.api.domain.FileInfo;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 课程教学计划 Service（主流程：列表/详情/保存/Word/教员/章节）。
 * 培养方案 tab 不再使用 context 表，见 {@link TeachingPlanModuleService#listSchemes(Long)}。
 */
public interface TeachingPlanService {

    List<TeachingPlanListVo> selectTeachingPlanPage(TeachingPlanQueryVo query);

    TeachingPlanDetailVo getDetail(Long courseId, Long teachingPlanId);

    /**
     * 保存教学计划主表。plan.id 为空时按 (sourceCourseId, planType) 查重。
     * 不再接收/写入 context 表。
     */
    Long saveTeachingPlan(TeachingPlanSaveVo saveVo);

    FileInfo generateTeachingPlanWord(Long courseId);

    FileInfo generateTeachingPlanWord(Long courseId, Long planId);

    /**
     * 从系统导出的教学计划 Word 覆盖导入。
     * planType 由前端传入，按 (courseId, planType) 定位：无 plan 则新建；有则逻辑删除子模块后重写；
     * 审核中/已通过的计划不允许覆盖导入。
     * 基本信息仅回写 plan.sourceCourseEnName。
     *
     * @param courseId 总库源课程 id
     * @param planType 教学计划类型（1课程/2实践训练课目/3实验课程/4实践项目）
     * @param file     docx 文件
     * @return 导入结果（成功计数 + 问题列表）
     */
    TeachingPlanImportResultVo importWord(Long courseId, Integer planType, MultipartFile file);

    List<TeachingPlanTeacher> listTeacher(Long planId);

    Long addTeacher(TeachingPlanTeacher teacher);

    void updateTeacher(TeachingPlanTeacher teacher);

    void deleteTeacher(Long id);

    /**
     * 教学团队候选人：当前教学计划所属课程（总库）的课程负责人 + 课程团队成员，
     * 带「是否已加入本教学计划团队」标记，供前端勾选。
     */
    List<TeachingPlanTeamCandidateVo> listTeacherCandidates(Long planId);

    /**
     * 批量保存教学团队成员（选择课程库已有人员或从第三方人员新增）。
     * 人员以统一认证帐号识别，允许未注册帐号；保存后同步并入课程库团队成员，不改变课程负责人。
     */
    List<TeachingPlanTeacher> saveTeachersBatch(TeachingPlanTeacherBatchSaveVo request);

    /** 批量移除教学团队成员：只解除当前教学计划的关联，不删除课程库团队成员。 */
    void removeTeachersBatch(Long planId, List<Long> ids);

    List<TeachingPlanSection> listSection(Long planId);

    Long addSection(TeachingPlanSection section);

    void updateSection(TeachingPlanSection section);

    void deleteSection(Long id);

    List<CourseQuoteMajorVo> listQuoteMajors(Long courseId);

    /** 逻辑删除教学计划；审核中/已通过不可删 */
    void deleteTeachingPlan(Long planId);
}
