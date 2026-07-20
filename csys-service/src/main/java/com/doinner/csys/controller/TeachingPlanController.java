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
import com.doinner.csys.domain.TeachingPlanObjectiveRef;
import com.doinner.csys.domain.TeachingPlanPracticeItem;
import com.doinner.csys.domain.TeachingPlanPracticeItemDetail;
import com.doinner.csys.domain.TeachingPlanProcessStep;
import com.doinner.csys.domain.TeachingPlanRef;
import com.doinner.csys.domain.TeachingPlanTargetDesign;
import com.doinner.csys.domain.TeachingPlanTextbook;
import com.doinner.csys.domain.TeachingPlanSection;
import com.doinner.csys.domain.TeachingPlanTeacher;
import com.doinner.csys.domain.vo.TeachingPlanDetailVo;
import com.doinner.csys.domain.vo.TeachingPlanListVo;
import com.doinner.csys.domain.vo.TeachingPlanMajorVo;
import com.doinner.csys.domain.vo.TeachingPlanObjectiveSaveVo;
import com.doinner.csys.domain.vo.TeachingPlanSchemeVo;
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
                                                              @RequestParam("schemeId") Long schemeId) {
        return DataSet.success(teachingPlanModuleService.listObjective(planId, schemeId));
    }

    @ApiOperation("新增教学计划目标")
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

    @ApiOperation("删除教学计划目标")
    @DeleteMapping("/objective/{id}")
    public Message deleteObjective(@PathVariable("id") Long id) {
        teachingPlanModuleService.deleteObjective(id);
        return Message.success();
    }

    /**
     * 目标 + 支撑毕业要求同事务保存（弹框一次提交）。
     */
    @ApiOperation("保存教学目标及支撑毕业要求")
    @PostMapping("/objective/saveWithRefs")
    public DataSet<Long> saveObjectiveWithRefs(@RequestBody TeachingPlanObjectiveSaveVo saveVo) {
        return DataSet.success(teachingPlanModuleService.saveObjectiveWithRefs(saveVo));
    }

    // ============ 8. 课程绑定毕业要求(按课程id / 可选context) ============

    @ApiOperation("根据课程id查询课程绑定的毕业要求(可按培养方案schemeId过滤)")
    @GetMapping("/courseGraduation/{courseId}")
    public DataSet<List<StandardGraduation>> courseGraduation(@PathVariable("courseId") Long courseId,
                                                              @RequestParam(value = "schemeId", required = false) Long schemeId) {
        return DataSet.success(teachingPlanModuleService.listCourseGraduationByScheme(courseId, schemeId));
    }

    // ============ 9. 教学计划目标支撑毕业要求 t_csys_teaching_plan_objective_ref ============

    @ApiOperation("教学计划目标支撑毕业要求列表")
    @GetMapping("/objectiveRef/list")
    public DataSet<List<TeachingPlanObjectiveRef>> objectiveRefList(@RequestParam("objectiveId") Long objectiveId) {
        return DataSet.success(teachingPlanModuleService.listObjectiveRef(objectiveId));
    }

    @ApiOperation("新增教学计划目标支撑毕业要求")
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

    @ApiOperation("知识/能力/素质目标达成设计列表(按designTypeCode区分)")
    @GetMapping("/targetDesign/list")
    public DataSet<List<TeachingPlanTargetDesign>> targetDesignList(@RequestParam("planId") Long planId,
                                                                    @RequestParam("schemeId") Long schemeId,
                                                                    @RequestParam("designTypeCode") String designTypeCode) {
        return DataSet.success(teachingPlanModuleService.listTargetDesign(planId, schemeId, designTypeCode));
    }

    @ApiOperation("知识目标初始化：根据课程id查询知识单元")
    @GetMapping("/targetDesign/knowledgeUnitInit/{courseId}")
    public DataSet<List<CourseKnowledgeUnit>> knowledgeUnitInit(@PathVariable("courseId") Long courseId) {
        return DataSet.success(teachingPlanModuleService.listKnowledgeUnitInit(courseId));
    }

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
