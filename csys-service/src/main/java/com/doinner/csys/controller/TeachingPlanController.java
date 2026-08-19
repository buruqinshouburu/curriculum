package com.doinner.csys.controller;

import com.doinner.common.core.domain.DataTable;
import com.doinner.common.core.domain.DataSet;
import com.doinner.common.core.domain.Message;
import com.doinner.common.core.utils.PageUtils;
import com.doinner.csys.domain.StandardGraduation;
import com.doinner.csys.domain.TeachingPlanAssessment;
import com.doinner.csys.domain.TeachingPlanCondition;
import com.doinner.csys.domain.TeachingPlanContent;
import com.doinner.csys.domain.TeachingPlanObjective;
import com.doinner.csys.domain.TeachingPlanObjectiveAssessment;
import com.doinner.csys.domain.TeachingPlanObjectiveRef;
import com.doinner.csys.domain.TeachingPlanPracticeItem;
import com.doinner.csys.domain.TeachingPlanPracticeItemDetail;
import com.doinner.csys.domain.TeachingPlanProcessStep;
import com.doinner.csys.domain.TeachingPlanRef;
import com.doinner.csys.domain.TeachingPlanTargetDesign;
import com.doinner.csys.domain.TeachingPlanTaskBackground;
import com.doinner.csys.domain.TeachingPlanTaskBackgroundRef;
import com.doinner.csys.domain.TeachingPlanTextbook;
import com.doinner.csys.domain.TeachingPlanTrainingPurpose;
import com.doinner.csys.domain.TeachingPlanTrainingPurposeRef;
import com.doinner.csys.domain.TeachingPlanContentPurpose;
import com.doinner.csys.domain.TeachingPlanSection;
import com.doinner.csys.domain.TeachingPlanTeacher;
import com.doinner.csys.domain.vo.TeachingPlanConditionSaveVo;
import com.doinner.csys.domain.vo.TeachingPlanSupportCandidateGroupVo;
import com.doinner.csys.domain.vo.TeachingPlanPracticeProjectBackgroundSaveVo;
import com.doinner.csys.domain.vo.TeachingPlanPracticeProjectBackgroundVo;
import com.doinner.csys.domain.vo.TeachingPlanDetailVo;
import com.doinner.csys.domain.vo.TeachingPlanImportResultVo;
import com.doinner.csys.domain.vo.TeachingPlanListVo;
import com.doinner.csys.domain.vo.TeachingPlanMajorVo;
import com.doinner.csys.domain.vo.TeachingPlanObjectiveOptionVo;
import com.doinner.csys.domain.vo.TeachingPlanObjectiveRefSaveVo;
import com.doinner.csys.domain.vo.TeachingPlanObjectiveSaveVo;
import com.doinner.csys.domain.vo.TeachingPlanObjectiveBatchSaveVo;
import com.doinner.csys.domain.vo.TeachingPlanObjectiveAssessmentSaveVo;
import com.doinner.csys.domain.vo.TeachingPlanObjectiveTreeVo;
import com.doinner.csys.domain.vo.TeachingPlanOrganizationSaveVo;
import com.doinner.csys.domain.vo.TeachingPlanSchemeVo;
import com.doinner.csys.domain.vo.TeachingPlanTaskBackgroundRefSaveVo;
import com.doinner.csys.domain.vo.TeachingPlanTrainingPurposeBatchSaveVo;
import com.doinner.csys.domain.vo.TeachingPlanTrainingPurposeRefSaveVo;
import com.doinner.csys.domain.vo.TeachingPlanContentPurposeSaveVo;
import com.doinner.csys.domain.vo.TeachingPlanQueryVo;
import com.doinner.csys.domain.vo.TeachingPlanSaveVo;
import com.doinner.csys.domain.vo.CourseQuoteMajorVo;
import com.doinner.csys.entity.csys.po.CourseKnowledgeUnit;
import com.doinner.csys.service.TeachingPlanModuleService;
import com.doinner.csys.service.TeachingPlanService;
import com.doinner.file.api.domain.FileInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.util.List;

/**
 * 课程教学计划 Controller。
 *
 * @author codex
 */
@RestController
@RequestMapping("/teachingPlan")
@Api(value = "/teachingPlan", tags = "teaching-plan-controller")
public class TeachingPlanController {

    @Resource
    private TeachingPlanService teachingPlanService;

    @Resource
    private TeachingPlanModuleService teachingPlanModuleService;

    /**
     * 课程教学计划管理列表(分页)。
     * 以总库课程为主表，left join 教学计划表，支持课程名称/编号模糊，
     * 开课单位、适用对象、课程模块、修读要求过滤，返回前端分页 DataTable。
     */
    @ApiOperation("课程教学计划管理列表(分页)")
    @GetMapping("/list")
    public DataTable<TeachingPlanListVo> list(TeachingPlanQueryVo query) {
        PageUtils.startPage();
        return DataTable.success(teachingPlanService.selectTeachingPlanPage(query));
    }

    /**
     * 按课程id查询引用该课程的专业类(去重)。
     * 逻辑参照课程被选用情况(/chooseStatus/{sourceCourseId})：通过 source_id 定位被选用课程，
     * 经排课表关联培养方案，按培养方案 major_id 去重，返回引用该课程的专业类列表。
     * 每行含学科门类(categoryName)、专业类(majorName)、专业类ID(majorId)。
     */
    @ApiOperation("查询引用课程的专业类")
    @GetMapping("/quoteMajor/{courseId}")
    public DataSet<List<CourseQuoteMajorVo>> quoteMajor(@PathVariable("courseId") Long courseId) {
        return DataSet.success(teachingPlanService.listQuoteMajors(courseId));
    }

    /**
     * 查看教学计划详情。
     * 传入课程courseId和教学计划id(teachingPlanId 可为空)。
     * 教学计划id为空时字段取自总库课程；有则取自教学计划表 + 调用课程上下文。
     */
    @ApiOperation("查看教学计划详情")
    @GetMapping("/detail")
    public DataSet<TeachingPlanDetailVo> detail(@RequestParam("courseId") Long courseId,
                                                @RequestParam(value = "teachingPlanId", required = false) Long teachingPlanId) {
        return DataSet.success(teachingPlanService.getDetail(courseId, teachingPlanId));
    }

    /**
     * 保存教学计划(含调用课程上下文)。
     * 无 plan.id 时按 (sourceCourseId, planType) 查重：已存在则修改，不存在才新增。
     */
    @ApiOperation("保存教学计划")
    @PostMapping("/save")
    public DataSet<Long> save(@RequestBody TeachingPlanSaveVo saveVo) {
        return DataSet.success(teachingPlanService.saveTeachingPlan(saveVo));
    }

    /**
     * 课程教学计划文件生成。
     * courseId 必填；planId 可选（指定某条教学计划，多类型时用）。
     * 根据 t_csys_course.type 决定模板：1课程/2实践训练课目/3实验课程/4实践项目。
     */
    @ApiOperation("课程教学计划文件生成")
    @GetMapping("/createWord/{courseId}")
    public DataSet<FileInfo> createWord(@PathVariable("courseId") Long courseId,
                                        @RequestParam(value = "planId", required = false) Long planId) {
        return DataSet.success(teachingPlanService.generateTeachingPlanWord(courseId, planId));
    }

    /**
     * 从系统导出的教学计划 Word 覆盖导入。
     * form-data: courseId + planType + file(.docx)。
     * planType 由前端传入（1课程/2实践训练课目/3实验课程/4实践项目），与 Word 识别类型不一致时以前端为准并记 WARN。
     * 按 (courseId, planType) 定位计划：无则新建；有则清空子模块后重写；审核中/已通过不可覆盖导入。
     * 基本信息仅回写 plan.sourceCourseEnName；匹配失败项跳过并记入 issues。
     */
    @ApiOperation("教学计划 Word 导入（覆盖，planType 前端传入）")
    @PostMapping("/importWord")
    public DataSet<TeachingPlanImportResultVo> importWord(@RequestParam("courseId") Long courseId,
                                                          @RequestParam("planType") Integer planType,
                                                          @RequestParam("file") MultipartFile file) {
        return DataSet.success(teachingPlanService.importWord(courseId, planType, file));
    }

    /**
     * 逻辑删除教学计划（审核中/已通过不可删）。
     */
    @ApiOperation("删除教学计划")
    @DeleteMapping("/{planId}")
    public Message deletePlan(@PathVariable("planId") Long planId) {
        teachingPlanService.deleteTeachingPlan(planId);
        return Message.success();
    }

    // ============ 培养方案 tab（scheme） ============

    @ApiOperation("教学计划培养方案tab列表(源课被引用的培养方案)")
    @GetMapping("/scheme/list")
    public DataSet<List<TeachingPlanSchemeVo>> schemeList(@RequestParam("courseId") Long courseId) {
        return DataSet.success(teachingPlanModuleService.listSchemes(courseId));
    }

    // ============ 教员团队 t_csys_teaching_plan_teacher ============

    @ApiOperation("教员团队列表")
    @GetMapping("/teacher/list")
    public DataSet<List<TeachingPlanTeacher>> teacherList(@RequestParam("planId") Long planId) {
        return DataSet.success(teachingPlanService.listTeacher(planId));
    }

    @ApiOperation("新增教员")
    @PostMapping("/teacher")
    public DataSet<Long> addTeacher(@RequestBody TeachingPlanTeacher teacher) {
        return DataSet.success(teachingPlanService.addTeacher(teacher));
    }

    @ApiOperation("修改教员")
    @PutMapping("/teacher")
    public Message updateTeacher(@RequestBody TeachingPlanTeacher teacher) {
        teachingPlanService.updateTeacher(teacher);
        return Message.success();
    }

    @ApiOperation("删除教员")
    @DeleteMapping("/teacher/{id}")
    public Message deleteTeacher(@PathVariable("id") Long id) {
        teachingPlanService.deleteTeacher(id);
        return Message.success();
    }

    // ============ 课程章节 t_csys_teaching_plan_section ============

    @ApiOperation("课程概述章节列表")
    @GetMapping("/section/list")
    public DataSet<List<TeachingPlanSection>> sectionList(@RequestParam("planId") Long planId) {
        return DataSet.success(teachingPlanService.listSection(planId));
    }

    @ApiOperation("新增课程概述章节")
    @PostMapping("/section")
    public DataSet<Long> addSection(@RequestBody TeachingPlanSection section) {
        return DataSet.success(teachingPlanService.addSection(section));
    }

    @ApiOperation("修改课程概述章节")
    @PutMapping("/section")
    public Message updateSection(@RequestBody TeachingPlanSection section) {
        teachingPlanService.updateSection(section);
        return Message.success();
    }

    @ApiOperation("删除课程概述章节")
    @DeleteMapping("/section/{id}")
    public Message deleteSection(@PathVariable("id") Long id) {
        teachingPlanService.deleteSection(id);
        return Message.success();
    }

    // ============ 6. 专业查询(教学计划id + 课程id) ============

    @ApiOperation("根据教学计划id与课程id查询专业id/name/status")
    @GetMapping("/major/list")
    public DataSet<List<TeachingPlanMajorVo>> majorList(@RequestParam(value = "teachingPlanId", required = false) Long teachingPlanId,
                                                        @RequestParam("courseId") Long courseId) {
        return DataSet.success(teachingPlanModuleService.listMajor(teachingPlanId, courseId));
    }

    // ============ 7. 教学计划目标 t_csys_teaching_plan_objective ============

    @ApiOperation("教学计划目标列表")
    @GetMapping("/objective/list")
    public DataSet<List<TeachingPlanObjective>> objectiveList(@RequestParam("planId") Long planId,
                                                              @RequestParam(value = "schemeId",required = false) Long schemeId) {
        return DataSet.success(teachingPlanModuleService.listObjective(planId, schemeId));
    }

    /**
     * 课程目标与支撑毕业要求总览树。
     * 结构对齐 TrainingController 调用课程知识体系总览：顶层目标类型(字典 sys_plan_target_type)，
     * children 为目标内容；目标节点 children 为支撑毕业要求。
     * 可选 objectiveTypeCode 按目标类型过滤。
     * schemeId 可选：有培养方案 tab 时传入按方案过滤；无 tab（如公共基础/不按方案拆分）可不传，返回 plan 下全部。
     * 不按课程模块判定 schemeId 是否必填。
     */
    @ApiOperation("课程目标与支撑毕业要求总览")
    @GetMapping("/objective/tree")
    public DataSet<List<TeachingPlanObjectiveTreeVo>> objectiveTree(@RequestParam("planId") Long planId,
                                                                    @RequestParam(value = "schemeId", required = false) Long schemeId,
                                                                    @RequestParam(value = "objectiveTypeCode", required = false) String objectiveTypeCode) {
        return DataSet.success(teachingPlanModuleService.listObjectiveTree(planId, schemeId, objectiveTypeCode));
    }

    /** 课程目标、支撑毕业要求、权重大保存；按当前 planId + schemeId 重建。 */
    @ApiOperation("课程目标与支撑毕业要求大保存")
    @PostMapping("/objective/batchSave")
    public Message saveObjectivesBatch(@RequestBody TeachingPlanObjectiveBatchSaveVo saveVo) {
        teachingPlanModuleService.saveObjectivesBatch(saveVo);
        return Message.success();
    }

    @ApiOperation("新增教学计划目标（仅类型与内容）")
    @PostMapping("/objective")
    public DataSet<Long> addObjective(@RequestBody TeachingPlanObjective objective) {
        return DataSet.success(teachingPlanModuleService.addObjective(objective));
    }

    @ApiOperation("修改教学计划目标")
    @PutMapping("/objective")
    public Message updateObjective(@RequestBody TeachingPlanObjective objective) {
        teachingPlanModuleService.updateObjective(objective);
        return Message.success();
    }

    /** 删除目标时同步逻辑删除其支撑毕业要求绑定 */
    @ApiOperation("删除教学计划目标")
    @DeleteMapping("/objective/{id}")
    public Message deleteObjective(@PathVariable("id") Long id) {
        teachingPlanModuleService.deleteObjective(id);
        return Message.success();
    }

    /**
     * 兼容：目标 + 支撑毕业要求同事务一次提交。
     * 推荐改用 POST /objective → POST /objectiveRef/save 拆分流程。
     */
    @ApiOperation("保存教学目标及支撑毕业要求（兼容旧一次提交）")
    @PostMapping("/objective/saveWithRefs")
    public DataSet<Long> saveObjectiveWithRefs(@RequestBody TeachingPlanObjectiveSaveVo saveVo) {
        return DataSet.success(teachingPlanModuleService.saveObjectiveWithRefs(saveVo));
    }

    // ============ 8. 候选毕业要求（绑定弹框数据源） ============

    /**
     * 候选毕业要求：源课在 schemeId 下被调用课（t_csys_training_scheme_ref_course）绑定的毕业要求；
     * 被调用课均无绑定时回退源课自身 t_csys_course_ref_graduation。
     * courseId 为总库源课程 id；schemeId 为培养方案 tab（可选）。
     * 公共基础课程：始终只返回源课公共毕业要求，忽略 schemeId。
     */
    @ApiOperation("候选毕业要求（被调用课绑定，空则回退源课；公共基础仅源课）")
    @GetMapping("/courseGraduation/{courseId}")
    public DataSet<List<StandardGraduation>> courseGraduation(@PathVariable("courseId") Long courseId,
                                                              @RequestParam(value = "schemeId", required = false) Long schemeId) {
        return DataSet.success(teachingPlanModuleService.listCourseGraduationByScheme(courseId, schemeId));
    }

    // ============ 9. 教学计划目标支撑毕业要求 t_csys_teaching_plan_objective_ref ============

    /** 回显某目标已绑定的毕业要求 */
    @ApiOperation("教学计划目标已绑定毕业要求列表（回显）")
    @GetMapping("/objectiveRef/list")
    public DataSet<List<TeachingPlanObjectiveRef>> objectiveRefList(@RequestParam("objectiveId") Long objectiveId) {
        return DataSet.success(teachingPlanModuleService.listObjectiveRef(objectiveId));
    }

    /**
     * 整表重建目标的毕业要求绑定（与目标新增解耦）。
     * refs 空列表或 null = 清空该目标全部绑定。
     */
    @ApiOperation("保存目标与毕业要求绑定（整表重建）")
    @PostMapping("/objectiveRef/save")
    public Message saveObjectiveRefs(@RequestBody TeachingPlanObjectiveRefSaveVo saveVo) {
        teachingPlanModuleService.saveObjectiveRefs(saveVo);
        return Message.success();
    }

    @ApiOperation("新增教学计划目标支撑毕业要求（单条）")
    @PostMapping("/objectiveRef")
    public DataSet<Long> addObjectiveRef(@RequestBody TeachingPlanObjectiveRef ref) {
        return DataSet.success(teachingPlanModuleService.addObjectiveRef(ref));
    }

    @ApiOperation("修改教学计划目标支撑毕业要求")
    @PutMapping("/objectiveRef")
    public Message updateObjectiveRef(@RequestBody TeachingPlanObjectiveRef ref) {
        teachingPlanModuleService.updateObjectiveRef(ref);
        return Message.success();
    }

    @ApiOperation("删除教学计划目标支撑毕业要求")
    @DeleteMapping("/objectiveRef/{id}")
    public Message deleteObjectiveRef(@PathVariable("id") Long id) {
        teachingPlanModuleService.deleteObjectiveRef(id);
        return Message.success();
    }

    // ============ 任务背景（实验课程第三节，对标课程目标） ============

    /**
     * 任务背景列表。
     * 公共基础课程可不传 schemeId；其他实验课程必须传当前培养方案 schemeId。
     */
    @ApiOperation("任务背景列表（实验课程第三节）")
    @GetMapping("/taskBackground/list")
    public DataSet<List<TeachingPlanTaskBackground>> taskBackgroundList(
            @RequestParam("planId") Long planId,
            @RequestParam(value = "schemeId", required = false) Long schemeId) {
        return DataSet.success(teachingPlanModuleService.listTaskBackground(planId, schemeId));
    }

    /** 仅新增任务背景列表数据，绑定请走 POST /taskBackgroundRef/save。 */
    @ApiOperation("新增任务背景（任务背景描述/目标类型/目标内容）")
    @PostMapping("/taskBackground")
    public DataSet<Long> addTaskBackground(@RequestBody TeachingPlanTaskBackground taskBackground) {
        return DataSet.success(teachingPlanModuleService.addTaskBackground(taskBackground));
    }

    @ApiOperation("修改任务背景")
    @PutMapping("/taskBackground")
    public Message updateTaskBackground(@RequestBody TeachingPlanTaskBackground taskBackground) {
        teachingPlanModuleService.updateTaskBackground(taskBackground);
        return Message.success();
    }

    /** 删除任务背景时同步逻辑删除其支撑毕业要求绑定 */
    @ApiOperation("删除任务背景")
    @DeleteMapping("/taskBackground/{id}")
    public Message deleteTaskBackground(@PathVariable("id") Long id) {
        teachingPlanModuleService.deleteTaskBackground(id);
        return Message.success();
    }

    // ============ 任务背景支撑毕业要求 t_csys_teaching_plan_task_background_ref ============

    /** 回显某任务背景已绑定的毕业要求 */
    @ApiOperation("任务背景已绑定毕业要求列表（回显）")
    @GetMapping("/taskBackgroundRef/list")
    public DataSet<List<TeachingPlanTaskBackgroundRef>> taskBackgroundRefList(
            @RequestParam("taskBackgroundId") Long taskBackgroundId) {
        return DataSet.success(teachingPlanModuleService.listTaskBackgroundRef(taskBackgroundId));
    }

    /**
     * 整表重建任务背景的毕业要求绑定（与任务背景新增解耦）。
     * graduationIds 空列表或 null = 清空该任务背景全部绑定。
     */
    @ApiOperation("批量保存任务背景与毕业要求绑定")
    @PostMapping("/taskBackgroundRef/save")
    public Message saveTaskBackgroundRefs(@RequestBody TeachingPlanTaskBackgroundRefSaveVo saveVo) {
        teachingPlanModuleService.saveTaskBackgroundRefs(saveVo);
        return Message.success();
    }

    // ============ 训练目的（实践训练课目 type2 第二节，对标任务背景） ============

    /**
     * 训练目的列表。
     * schemeId 可选：通识通用恒为 null 单组；有培养方案 tab 时传 schemeId 过滤；
     * 不传则返回 plan 下全量（供按 scheme 分组渲染）。
     */
    @ApiOperation("训练目的列表（实践训练课目第二节）")
    @GetMapping("/trainingPurpose/list")
    public DataSet<List<TeachingPlanTrainingPurpose>> trainingPurposeList(
            @RequestParam("planId") Long planId,
            @RequestParam(value = "schemeId", required = false) Long schemeId) {
        return DataSet.success(teachingPlanModuleService.listTrainingPurpose(planId, schemeId));
    }

    /**
     * 训练目的 + 支撑毕业要求大保存；按 planId 删除旧训练目的及绑定后重建。
     */
    @ApiOperation("训练目的与支撑毕业要求大保存")
    @PostMapping("/trainingPurpose/batchSave")
    public Message saveTrainingPurposesBatch(@RequestBody TeachingPlanTrainingPurposeBatchSaveVo saveVo) {
        teachingPlanModuleService.saveTrainingPurposesBatch(saveVo);
        return Message.success();
    }

    /** 仅新增训练目的（单个值），绑定请走 POST /trainingPurposeRef/save。 */
    @ApiOperation("新增训练目的（仅目的文本）")
    @PostMapping("/trainingPurpose")
    public DataSet<Long> addTrainingPurpose(@RequestBody TeachingPlanTrainingPurpose trainingPurpose) {
        return DataSet.success(teachingPlanModuleService.addTrainingPurpose(trainingPurpose));
    }

    @ApiOperation("修改训练目的")
    @PutMapping("/trainingPurpose")
    public Message updateTrainingPurpose(@RequestBody TeachingPlanTrainingPurpose trainingPurpose) {
        teachingPlanModuleService.updateTrainingPurpose(trainingPurpose);
        return Message.success();
    }

    /** 删除训练目的时同步逻辑删除其支撑毕业要求绑定 */
    @ApiOperation("删除训练目的")
    @DeleteMapping("/trainingPurpose/{id}")
    public Message deleteTrainingPurpose(@PathVariable("id") Long id) {
        teachingPlanModuleService.deleteTrainingPurpose(id);
        return Message.success();
    }

    // ============ 训练目的支撑毕业要求 t_csys_teaching_plan_training_purpose_ref ============

    /** 回显某训练目的已绑定的毕业要求 */
    @ApiOperation("训练目的已绑定毕业要求列表（回显）")
    @GetMapping("/trainingPurposeRef/list")
    public DataSet<List<TeachingPlanTrainingPurposeRef>> trainingPurposeRefList(
            @RequestParam("purposeId") Long purposeId) {
        return DataSet.success(teachingPlanModuleService.listTrainingPurposeRef(purposeId));
    }

    /**
     * 整表重建训练目的的毕业要求绑定（与训练目的新增解耦）。
     * refs 空列表或 null = 清空该训练目的全部绑定。
     */
    @ApiOperation("保存训练目的与毕业要求绑定（整表重建）")
    @PostMapping("/trainingPurposeRef/save")
    public Message saveTrainingPurposeRefs(@RequestBody TeachingPlanTrainingPurposeRefSaveVo saveVo) {
        teachingPlanModuleService.saveTrainingPurposeRefs(saveVo);
        return Message.success();
    }

    // ============ 训练内容支撑训练目的（type2 第四节「目的」多选） ============

    /** 回显某训练内容已绑定的训练目的（含目的文本快照） */
    @ApiOperation("训练内容已绑定训练目的列表（回显）")
    @GetMapping("/contentPurpose/list")
    public DataSet<List<TeachingPlanContentPurpose>> contentPurposeList(
            @RequestParam("contentId") Long contentId) {
        return DataSet.success(teachingPlanModuleService.listContentPurpose(contentId));
    }

    /**
     * 整表重建训练内容的训练目的绑定（与训练内容新增解耦）。
     * purposeIds 空列表或 null = 清空该训练内容全部绑定。
     */
    @ApiOperation("保存训练内容与训练目的绑定（整表重建）")
    @PostMapping("/contentPurpose/save")
    public Message saveContentPurposes(@RequestBody TeachingPlanContentPurposeSaveVo saveVo) {
        teachingPlanModuleService.saveContentPurposes(saveVo);
        return Message.success();
    }

    // ============ 实践项目第二节支撑绑定（type4） ============

    @ApiOperation("实践项目支撑绑定候选树（按培养方案分组，同方案优先）")
    @GetMapping("/support/candidateTree")
    public DataSet<List<TeachingPlanSupportCandidateGroupVo>> supportCandidateTree(
            @RequestParam("courseId") Long courseId,
            @RequestParam(value = "projectPlanId", required = false) Long projectPlanId) {
        return DataSet.success(teachingPlanModuleService.listSupportCandidateGroups(courseId, projectPlanId));
    }

    @ApiOperation("实践项目任务背景与目标整页详情")
    @GetMapping("/practiceProject/background/detail")
    public DataSet<TeachingPlanPracticeProjectBackgroundVo> practiceProjectBackgroundDetail(
            @RequestParam("planId") Long planId) {
        return DataSet.success(teachingPlanModuleService.getPracticeProjectBackground(planId));
    }

    /** 正文、目标/目的绑定、知识体系/训练内容绑定在同一事务内整页保存。 */
    @ApiOperation("实践项目任务背景与目标整页保存")
    @PostMapping("/practiceProject/background/save")
    public Message savePracticeProjectBackground(
            @RequestBody TeachingPlanPracticeProjectBackgroundSaveVo saveVo) {
        teachingPlanModuleService.savePracticeProjectBackground(saveVo);
        return Message.success();
    }

    /** 课程目标与考核评价关联列表。 */
    @ApiOperation("课程目标与考核评价关联列表")
    @GetMapping("/objectiveAssessment/list")
    public DataSet<List<TeachingPlanObjectiveAssessment>> objectiveAssessmentList(
            @RequestParam("planId") Long planId,
            @RequestParam(value = "schemeId", required = false) Long schemeId) {
        return DataSet.success(teachingPlanModuleService.listObjectiveAssessment(planId, schemeId));
    }

    /** 课程目标与考核评价关联批量保存。 */
    @ApiOperation("课程目标与考核评价关联批量保存")
    @PostMapping("/objectiveAssessment/batchSave")
    public Message saveObjectiveAssessmentBatch(@RequestBody TeachingPlanObjectiveAssessmentSaveVo saveVo) {
        teachingPlanModuleService.saveObjectiveAssessmentBatch(saveVo);
        return Message.success();
    }

    // ============ 10. 教学内容与学时安排 t_csys_teaching_plan_content ============
    @ApiOperation("教学内容与学时安排列表")
    @GetMapping("/content/list")
    public DataSet<List<TeachingPlanContent>> contentList(@RequestParam("planId") Long planId) {
        return DataSet.success(teachingPlanModuleService.listContent(planId));
    }

    @ApiOperation("新增教学内容")
    @PostMapping("/content")
    public DataSet<Long> addContent(@RequestBody TeachingPlanContent content) {
        return DataSet.success(teachingPlanModuleService.addContent(content));
    }

    @ApiOperation("修改教学内容")
    @PutMapping("/content")
    public Message updateContent(@RequestBody TeachingPlanContent content) {
        teachingPlanModuleService.updateContent(content);
        return Message.success();
    }

    @ApiOperation("删除教学内容")
    @DeleteMapping("/content/{id}")
    public Message deleteContent(@PathVariable("id") Long id) {
        teachingPlanModuleService.deleteContent(id);
        return Message.success();
    }

    // ============ 11-13. 目标达成设计 t_csys_teaching_plan_target_design ============

    /**
     * 知识/能力/素质目标达成设计列表。
     * planId / schemeId 均可空：用户未建计划直接进 tab 时返回空列表，不报错。
     * designTypeCode 区分知识/能力/素质。
     * 教学环节/教法/学法为字典值字符串；支撑目标见 objectiveText；
     * 知识目标多知识点见 knowledgePoints。
     */
    @ApiOperation("知识/能力/素质目标达成设计列表(按designTypeCode区分)")
    @GetMapping("/targetDesign/list")
    public DataSet<List<TeachingPlanTargetDesign>> targetDesignList(
            @RequestParam(value = "planId", required = false) Long planId,
            @RequestParam(value = "schemeId", required = false) Long schemeId,
            @RequestParam(value = "designTypeCode", required = false) String designTypeCode) {
        return DataSet.success(teachingPlanModuleService.listTargetDesign(planId, schemeId, designTypeCode));
    }

    /**
     * 达成设计「支撑的知识/能力/素质目标」下拉数据源。
     * 按 content 去重；objectiveTypeCode 每次只查一种类型。
     * planId 为空返回空列表。
     */
    @ApiOperation("教学计划目标选项（按类型，内容去重）")
    @GetMapping("/objective/options")
    public DataSet<List<TeachingPlanObjectiveOptionVo>> objectiveOptions(
            @RequestParam(value = "planId", required = false) Long planId,
            @RequestParam(value = "schemeId", required = false) Long schemeId,
            @RequestParam("objectiveTypeCode") String objectiveTypeCode) {
        return DataSet.success(teachingPlanModuleService.listObjectiveOptions(planId, schemeId, objectiveTypeCode));
    }

    @ApiOperation("知识目标初始化：根据课程id查询知识单元")
    @GetMapping("/targetDesign/knowledgeUnitInit/{courseId}")
    public DataSet<List<CourseKnowledgeUnit>> knowledgeUnitInit(@PathVariable("courseId") Long courseId) {
        return DataSet.success(teachingPlanModuleService.listKnowledgeUnitInit(courseId));
    }

    /**
     * 新增目标达成设计。
     * 知识目标：knowledgePoints 可传多项（可跨知识单元）；objectiveText 存支撑目标字符串。
     * 能力/素质：objectiveText + observationPoint + teachingDesign。
     * teachingLink / teachingMethod / learningMethod 为字典值。
     */
    @ApiOperation("新增目标达成设计")
    @PostMapping("/targetDesign")
    public DataSet<Long> addTargetDesign(@RequestBody TeachingPlanTargetDesign design) {
        return DataSet.success(teachingPlanModuleService.addTargetDesign(design));
    }

    @ApiOperation("修改目标达成设计")
    @PutMapping("/targetDesign")
    public Message updateTargetDesign(@RequestBody TeachingPlanTargetDesign design) {
        teachingPlanModuleService.updateTargetDesign(design);
        return Message.success();
    }

    @ApiOperation("删除目标达成设计")
    @DeleteMapping("/targetDesign/{id}")
    public Message deleteTargetDesign(@PathVariable("id") Long id) {
        teachingPlanModuleService.deleteTargetDesign(id);
        return Message.success();
    }

    // ============ 14. 实验/实践环节 t_csys_teaching_plan_practice_item(_detail) ============

    @ApiOperation("实验/实践项目列表")
    @GetMapping("/practiceItem/list")
    public DataSet<List<TeachingPlanPracticeItem>> practiceItemList(@RequestParam("planId") Long planId) {
        return DataSet.success(teachingPlanModuleService.listPracticeItem(planId));
    }

    @ApiOperation("新增实验/实践项目")
    @PostMapping("/practiceItem")
    public DataSet<Long> addPracticeItem(@RequestBody TeachingPlanPracticeItem item) {
        return DataSet.success(teachingPlanModuleService.addPracticeItem(item));
    }

    @ApiOperation("修改实验/实践项目")
    @PutMapping("/practiceItem")
    public Message updatePracticeItem(@RequestBody TeachingPlanPracticeItem item) {
        teachingPlanModuleService.updatePracticeItem(item);
        return Message.success();
    }

    @ApiOperation("删除实验/实践项目(级联删除明细)")
    @DeleteMapping("/practiceItem/{id}")
    public Message deletePracticeItem(@PathVariable("id") Long id) {
        teachingPlanModuleService.deletePracticeItem(id);
        return Message.success();
    }

    @ApiOperation("实验/实践项目明细列表")
    @GetMapping("/practiceItemDetail/list")
    public DataSet<List<TeachingPlanPracticeItemDetail>> practiceItemDetailList(@RequestParam("itemId") Long itemId) {
        return DataSet.success(teachingPlanModuleService.listPracticeItemDetail(itemId));
    }

    @ApiOperation("新增实验/实践项目明细")
    @PostMapping("/practiceItemDetail")
    public DataSet<Long> addPracticeItemDetail(@RequestBody TeachingPlanPracticeItemDetail detail) {
        return DataSet.success(teachingPlanModuleService.addPracticeItemDetail(detail));
    }

    @ApiOperation("修改实验/实践项目明细")
    @PutMapping("/practiceItemDetail")
    public Message updatePracticeItemDetail(@RequestBody TeachingPlanPracticeItemDetail detail) {
        teachingPlanModuleService.updatePracticeItemDetail(detail);
        return Message.success();
    }

    @ApiOperation("删除实验/实践项目明细")
    @DeleteMapping("/practiceItemDetail/{id}")
    public Message deletePracticeItemDetail(@PathVariable("id") Long id) {
        teachingPlanModuleService.deletePracticeItemDetail(id);
        return Message.success();
    }

    // ============ 15. 考核评价 t_csys_teaching_plan_assessment ============

    @ApiOperation("考核评价列表")
    @GetMapping("/assessment/list")
    public DataSet<List<TeachingPlanAssessment>> assessmentList(@RequestParam("planId") Long planId) {
        return DataSet.success(teachingPlanModuleService.listAssessment(planId));
    }

    @ApiOperation("新增考核评价")
    @PostMapping("/assessment")
    public DataSet<Long> addAssessment(@RequestBody TeachingPlanAssessment assessment) {
        return DataSet.success(teachingPlanModuleService.addAssessment(assessment));
    }

    @ApiOperation("修改考核评价")
    @PutMapping("/assessment")
    public Message updateAssessment(@RequestBody TeachingPlanAssessment assessment) {
        teachingPlanModuleService.updateAssessment(assessment);
        return Message.success();
    }

    @ApiOperation("删除考核评价")
    @DeleteMapping("/assessment/{id}")
    public Message deleteAssessment(@PathVariable("id") Long id) {
        teachingPlanModuleService.deleteAssessment(id);
        return Message.success();
    }

    // ============ 16. 教材 t_csys_teaching_plan_textbook ============

    @ApiOperation("教材列表")
    @GetMapping("/textbook/list")
    public DataSet<List<TeachingPlanTextbook>> textbookList(@RequestParam("planId") Long planId) {
        return DataSet.success(teachingPlanModuleService.listTextbook(planId));
    }

    @ApiOperation("新增教材")
    @PostMapping("/textbook")
    public DataSet<Long> addTextbook(@RequestBody TeachingPlanTextbook textbook) {
        return DataSet.success(teachingPlanModuleService.addTextbook(textbook));
    }

    @ApiOperation("修改教材")
    @PutMapping("/textbook")
    public Message updateTextbook(@RequestBody TeachingPlanTextbook textbook) {
        teachingPlanModuleService.updateTextbook(textbook);
        return Message.success();
    }

    @ApiOperation("删除教材")
    @DeleteMapping("/textbook/{id}")
    public Message deleteTextbook(@PathVariable("id") Long id) {
        teachingPlanModuleService.deleteTextbook(id);
        return Message.success();
    }

    // ============ 17. 条件保障(教室等) t_csys_teaching_plan_condition ============

    @ApiOperation("条件保障列表")
    @GetMapping("/condition/list")
    public DataSet<List<TeachingPlanCondition>> conditionList(@RequestParam("planId") Long planId) {
        return DataSet.success(teachingPlanModuleService.listCondition(planId));
    }

    @ApiOperation("新增条件保障")
    @PostMapping("/condition")
    public DataSet<Long> addCondition(@RequestBody TeachingPlanCondition condition) {
        return DataSet.success(teachingPlanModuleService.addCondition(condition));
    }

    @ApiOperation("修改条件保障")
    @PutMapping("/condition")
    public Message updateCondition(@RequestBody TeachingPlanCondition condition) {
        teachingPlanModuleService.updateCondition(condition);
        return Message.success();
    }

    @ApiOperation("删除条件保障")
    @DeleteMapping("/condition/{id}")
    public Message deleteCondition(@PathVariable("id") Long id) {
        teachingPlanModuleService.deleteCondition(id);
        return Message.success();
    }

    /**
     * 教学条件及资源大保存（整表重建，一次多条）。
     * 先按 planId 逻辑删除该教学计划下全部旧条件，再按 conditions 批量写入；
     * conditions 传空列表或 null = 清空全部。每行 planId 以入参顶层 planId 为准。
     * 用于替代逐条 POST /condition 的新增，前端表格一次提交多条条件类型/有关要求。
     */
    @ApiOperation("条件保障大保存（整表重建，一次多条）")
    @PostMapping("/condition/save")
    public Message saveConditions(@RequestBody TeachingPlanConditionSaveVo saveVo) {
        teachingPlanModuleService.saveConditions(saveVo);
        return Message.success();
    }

    /**
     * 实践项目(type=4)「三、组织与实施」整表大保存。
     * 前端该页面团队规模/分工方式/项目步骤同页一次提交：
     * - teamScale/division：复用 t_csys_teaching_plan_section(sectionTitle=团队规模/分工方式) upsert；null 不改，空串清空。
     * - steps：t_csys_teaching_plan_process_step 整表重建；空/null=清空项目步骤。
     */
    @ApiOperation("组织与实施大保存(团队规模/分工方式/项目步骤)")
    @PostMapping("/organization/save")
    public Message saveOrganization(@RequestBody TeachingPlanOrganizationSaveVo saveVo) {
        teachingPlanModuleService.saveOrganization(saveVo);
        return Message.success();
    }

    // ============ 18. 实施步骤 t_csys_teaching_plan_process_step ============

    @ApiOperation("实施步骤列表")
    @GetMapping("/processStep/list")
    public DataSet<List<TeachingPlanProcessStep>> processStepList(@RequestParam("planId") Long planId) {
        return DataSet.success(teachingPlanModuleService.listProcessStep(planId));
    }

    @ApiOperation("新增实施步骤")
    @PostMapping("/processStep")
    public DataSet<Long> addProcessStep(@RequestBody TeachingPlanProcessStep step) {
        return DataSet.success(teachingPlanModuleService.addProcessStep(step));
    }

    @ApiOperation("修改实施步骤")
    @PutMapping("/processStep")
    public Message updateProcessStep(@RequestBody TeachingPlanProcessStep step) {
        teachingPlanModuleService.updateProcessStep(step);
        return Message.success();
    }

    @ApiOperation("删除实施步骤")
    @DeleteMapping("/processStep/{id}")
    public Message deleteProcessStep(@PathVariable("id") Long id) {
        teachingPlanModuleService.deleteProcessStep(id);
        return Message.success();
    }

    // ============ 19. 通用引用 t_csys_teaching_plan_ref ============

    @ApiOperation("通用引用列表(可选refType过滤)")
    @GetMapping("/ref/list")
    public DataSet<List<TeachingPlanRef>> refList(@RequestParam("planId") Long planId,
                                                  @RequestParam(value = "refType", required = false) Integer refType) {
        return DataSet.success(teachingPlanModuleService.listRef(planId, refType));
    }

    @ApiOperation("新增通用引用")
    @PostMapping("/ref")
    public DataSet<Long> addRef(@RequestBody TeachingPlanRef ref) {
        return DataSet.success(teachingPlanModuleService.addRef(ref));
    }

    @ApiOperation("修改通用引用")
    @PutMapping("/ref")
    public Message updateRef(@RequestBody TeachingPlanRef ref) {
        teachingPlanModuleService.updateRef(ref);
        return Message.success();
    }

    @ApiOperation("删除通用引用")
    @DeleteMapping("/ref/{id}")
    public Message deleteRef(@PathVariable("id") Long id) {
        teachingPlanModuleService.deleteRef(id);
        return Message.success();
    }
}
