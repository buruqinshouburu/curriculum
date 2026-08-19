package com.doinner.csys.service;

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
import com.doinner.csys.domain.TeachingPlanContentPurpose;
import com.doinner.csys.domain.TeachingPlanSupportContent;
import com.doinner.csys.domain.TeachingPlanSupportObjective;
import com.doinner.csys.domain.TeachingPlanTaskBackground;
import com.doinner.csys.domain.TeachingPlanTaskBackgroundRef;
import com.doinner.csys.domain.TeachingPlanTextbook;
import com.doinner.csys.domain.TeachingPlanTrainingPurpose;
import com.doinner.csys.domain.TeachingPlanTrainingPurposeRef;
import com.doinner.csys.domain.vo.TeachingPlanConditionSaveVo;
import com.doinner.csys.domain.vo.TeachingPlanContentPurposeSaveVo;
import com.doinner.csys.domain.vo.TeachingPlanMajorVo;
import com.doinner.csys.domain.vo.TeachingPlanObjectiveOptionVo;
import com.doinner.csys.domain.vo.TeachingPlanObjectiveRefSaveVo;
import com.doinner.csys.domain.vo.TeachingPlanObjectiveSaveVo;
import com.doinner.csys.domain.vo.TeachingPlanObjectiveTreeVo;
import com.doinner.csys.domain.vo.TeachingPlanTaskBackgroundRefSaveVo;
import com.doinner.csys.domain.vo.TeachingPlanTrainingPurposeBatchSaveVo;
import com.doinner.csys.domain.vo.TeachingPlanTrainingPurposeRefSaveVo;
import com.doinner.csys.domain.vo.TeachingPlanOrganizationSaveVo;
import com.doinner.csys.domain.vo.TeachingPlanPracticeProjectBackgroundSaveVo;
import com.doinner.csys.domain.vo.TeachingPlanPracticeProjectBackgroundVo;
import com.doinner.csys.domain.vo.TeachingPlanSchemeVo;
import com.doinner.csys.domain.vo.TeachingPlanSupportCandidateVo;
import com.doinner.csys.domain.vo.TeachingPlanSupportCandidateGroupVo;
import com.doinner.csys.domain.vo.TeachingPlanSupportContentSaveVo;
import com.doinner.csys.domain.vo.TeachingPlanSupportObjectiveSaveVo;
import com.doinner.csys.entity.csys.po.CourseKnowledgeUnit;

import java.util.List;

/**
 * 课程教学计划各模块 Service。
 * tab 维度使用培养方案 schemeId（不再使用 context 表）。
 */
public interface TeachingPlanModuleService {

    /** 专业列表（与 quoteMajor 同源）；status 按 plan+major 目标绑定情况 */
    List<TeachingPlanMajorVo> listMajor(Long teachingPlanId, Long courseId);

    /**
     * 编辑页培养方案 tab 列表：源课被哪些培养方案引用（scheme 去重）。
     * 同一方案下源课被引用多次时 quoteCourseCount &gt; 1。
     */
    List<TeachingPlanSchemeVo> listSchemes(Long sourceCourseId);

    List<TeachingPlanObjective> listObjective(Long planId, Long schemeId);

    /**
     * 课程目标与支撑毕业要求总览树。
     * 结构对齐调用课程知识体系总览：顶层=目标类型(字典 sys_plan_target_type)，
     * children=目标内容，目标节点 children=支撑毕业要求。
     * schemeId 可选：传入则按培养方案过滤；不传则返回该 plan 下全部目标（不过滤 scheme）。
     * 不按课程模块判定是否必填（源课模块与引用后展示可能不一致）。
     *
     * @param planId            教学计划id
     * @param schemeId          培养方案id（可选）
     * @param objectiveTypeCode 可选，按目标类型字典值过滤
     */
    List<TeachingPlanObjectiveTreeVo> listObjectiveTree(Long planId, Long schemeId, String objectiveTypeCode);

    Long addObjective(TeachingPlanObjective objective);

    /** 课程目标、支撑毕业要求、权重整表保存。 */
    void saveObjectivesBatch(com.doinner.csys.domain.vo.TeachingPlanObjectiveBatchSaveVo saveVo);

    void updateObjective(TeachingPlanObjective objective);

    void deleteObjective(Long id);

    /**
     * 目标 + 支撑毕业要求同事务保存（兼容旧前端一次提交）。
     * 推荐改用：POST /objective 只存目标 → POST /objectiveRef/save 单独绑定。
     */
    Long saveObjectiveWithRefs(TeachingPlanObjectiveSaveVo saveVo);

    /**
     * 仅保存目标与毕业要求的绑定关系（与目标新增解耦）。
     * 先逻辑删除 objectiveId 下旧 ref，再按 saveVo.refs 重建；refs 空=清空绑定。
     */
    void saveObjectiveRefs(TeachingPlanObjectiveRefSaveVo saveVo);

    /**
     * 候选毕业要求：源课在指定培养方案下被调用课绑定的毕业要求；
     * 被调用课均无绑定时回退源课自身在 t_csys_course_ref_graduation 的绑定。
     * schemeId 为空时汇总源课全部调用课绑定，仍空则回退源课。
     * 公共基础课程：始终只返回源课公共毕业要求，忽略 schemeId。
     *
     * @param courseId 总库源课程 id
     * @param schemeId 培养方案 id（tab），可选
     */
    List<StandardGraduation> listCourseGraduationByScheme(Long courseId, Long schemeId);

    /**
     * 全部调用课汇总毕业要求，空则回退源课（Word 生成等兼容）。
     */
    List<StandardGraduation> listCourseGraduation(Long courseId);

    List<TeachingPlanObjectiveRef> listObjectiveRef(Long objectiveId);

    List<com.doinner.csys.domain.TeachingPlanObjectiveAssessment> listObjectiveAssessment(Long planId, Long schemeId);

    void saveObjectiveAssessmentBatch(com.doinner.csys.domain.vo.TeachingPlanObjectiveAssessmentSaveVo saveVo);

    Long addObjectiveRef(TeachingPlanObjectiveRef ref);

    void updateObjectiveRef(TeachingPlanObjectiveRef ref);

    void deleteObjectiveRef(Long id);

    // ============ 任务背景（实验课程第三节，对标课程目标） ============

    /**
     * 任务背景列表。
     * 公共基础课程：始终只取 scheme_id IS NULL 单组；
     * 非公共基础：schemeId 必填并按 schemeId 过滤。
     */
    List<TeachingPlanTaskBackground> listTaskBackground(Long planId, Long schemeId);

    Long addTaskBackground(TeachingPlanTaskBackground taskBackground);

    void updateTaskBackground(TeachingPlanTaskBackground taskBackground);

    /** 删除任务背景，同步逻辑删除其绑定的毕业要求。 */
    void deleteTaskBackground(Long id);

    /** 任务背景绑定的毕业要求列表。 */
    List<TeachingPlanTaskBackgroundRef> listTaskBackgroundRef(Long taskBackgroundId);

    /**
     * 仅重建任务背景的毕业要求绑定（与任务背景新增解耦）。
     * 先逻辑删除 taskBackgroundId 下旧绑定，再按 saveVo.graduationIds 重建；空列表=清空绑定。
     */
    void saveTaskBackgroundRefs(TeachingPlanTaskBackgroundRefSaveVo saveVo);

    // ============ 训练目的（实践训练课目 type2 第二节，对标任务背景） ============

    /**
     * 训练目的列表。
     * 通识通用（课目模块仅∈{1,2,3,9}）：始终只取 scheme_id IS NULL 单组；
     * 非通识通用：按 schemeId 过滤；schemeId 为空返回 plan 下全量（按 scheme 分组渲染用）。
     */
    List<TeachingPlanTrainingPurpose> listTrainingPurpose(Long planId, Long schemeId);

    /**
     * 训练目的 + 支撑毕业要求整表保存。
     * 先按 planId 逻辑删除旧训练目的及绑定，再按 purposes 重建。
     */
    void saveTrainingPurposesBatch(TeachingPlanTrainingPurposeBatchSaveVo saveVo);

    Long addTrainingPurpose(TeachingPlanTrainingPurpose trainingPurpose);

    void updateTrainingPurpose(TeachingPlanTrainingPurpose trainingPurpose);

    /** 删除训练目的，同步逻辑删除其绑定的毕业要求。 */
    void deleteTrainingPurpose(Long id);

    /** 训练目的绑定的毕业要求列表。 */
    List<TeachingPlanTrainingPurposeRef> listTrainingPurposeRef(Long purposeId);

    /**
     * 仅重建训练目的的毕业要求绑定（与训练目的新增解耦）。
     * 先逻辑删除 purposeId 下旧 ref，再按 saveVo.refs 重建；refs 空=清空绑定。
     */
    void saveTrainingPurposeRefs(TeachingPlanTrainingPurposeRefSaveVo saveVo);

    // ============ 训练内容支撑训练目的（type2 第四节「目的」多选） ============

    /** 某训练内容已绑定的训练目的列表（含目的文本快照）。 */
    List<TeachingPlanContentPurpose> listContentPurpose(Long contentId);

    /**
     * 整表重建训练内容的训练目的绑定（与训练内容新增解耦）。
     * 先逻辑删除 contentId 下旧绑定，再按 saveVo.purposeIds 重建；purposeIds 空=清空。
     */
    void saveContentPurposes(TeachingPlanContentPurposeSaveVo saveVo);

    // ============ 实践项目第二节支撑绑定（type4） ============

    /**
     * 候选数据：项目支撑课程(源课 before_course_id)/支撑训练课目(after_course_id) 各自教学计划的
     * 课程目标(第四部分)、训练目的(第二部分)、知识体系(课程 content 全部行)、训练内容(课目第四部分 content)。
     * 课程目标按同专业(与项目首个培养方案 major_id 一致)优先排序。
     */
    TeachingPlanSupportCandidateVo listSupportCandidates(Long courseId);

    List<TeachingPlanSupportCandidateGroupVo> listSupportCandidateGroups(Long courseId, Long projectPlanId);

    /** 实践项目第二部分整页详情：正文与两类绑定统一返回。 */
    TeachingPlanPracticeProjectBackgroundVo getPracticeProjectBackground(Long planId);

    /** 实践项目第二部分整页大保存；任一部分失败时全部回滚。 */
    void savePracticeProjectBackground(TeachingPlanPracticeProjectBackgroundSaveVo saveVo);

    /** 内部读取：实践项目计划(type4)已绑定的课程目标/训练目的列表（Word 生成使用）。 */
    List<TeachingPlanSupportObjective> listSupportObjective(Long planId);

    /**
     * 内部保存：整表重建实践项目计划(type4)的课程目标/训练目的绑定（整页保存与 Word 导入使用）。
     * 先逻辑删除该 plan 下旧绑定，再按 objectiveIds/purposeIds 重建快照；空列表或 null=清空。
     */
    void saveSupportObjectives(TeachingPlanSupportObjectiveSaveVo saveVo);

    /** 内部读取：实践项目计划(type4)已绑定的知识体系/训练内容列表（Word 生成使用）。 */
    List<TeachingPlanSupportContent> listSupportContent(Long planId);

    /**
     * 内部保存：整表重建实践项目计划(type4)的知识体系/训练内容绑定（整页保存与 Word 导入使用）。
     * 先逻辑删除该 plan 下旧绑定，再按 contentIds 重建快照；空列表或 null=清空。
     */
    void saveSupportContents(TeachingPlanSupportContentSaveVo saveVo);

    // ============ 分组判定（供 Word 生成/导入共用） ============

    /**
     * 普通课程(type1/3)源课是否属公共基础：聚合被引用课程的 course_Module 多值串
     * 拆分后每一项均为公共基础（空聚合回退源课自身 course_Module）。
     */
    boolean isPublicFoundationCourse(Long sourceCourseId);

    /**
     * 实践训练课目(type2)是否属通识通用：聚合被引用课程的 location 多值串
     * 拆分后每一项均∈{1,2,3,9}（空聚合回退源课自身 location）。
     */
    boolean isGeneralSubjectModuleCourse(Long sourceCourseId);

    List<TeachingPlanContent> listContent(Long planId);

    Long addContent(TeachingPlanContent content);

    void updateContent(TeachingPlanContent content);

    void deleteContent(Long id);

    /**
     * 知识/能力/素质目标达成设计列表。
     * planId 为空时返回空列表（用户未建计划直接进 tab 不报错）；
     * schemeId 可选。
     */
    List<TeachingPlanTargetDesign> listTargetDesign(Long planId, Long schemeId, String designTypeCode);

    List<CourseKnowledgeUnit> listKnowledgeUnitInit(Long courseId);

    Long addTargetDesign(TeachingPlanTargetDesign design);

    void updateTargetDesign(TeachingPlanTargetDesign design);

    void deleteTargetDesign(Long id);

    /**
     * 查询教学计划下某一类型的课程目标选项（达成设计弹框「支撑目标」数据源）。
     * 按 content 字符串去重，同名称只保留一条。
     * planId 为空返回空列表。
     *
     * @param planId             教学计划 id（可空）
     * @param schemeId           培养方案 id（可空=汇总全部方案下该类型）
     * @param objectiveTypeCode  目标类型：知识目标/能力目标/素质目标（或字典编码）
     */
    List<TeachingPlanObjectiveOptionVo> listObjectiveOptions(Long planId, Long schemeId, String objectiveTypeCode);

    List<TeachingPlanPracticeItem> listPracticeItem(Long planId);

    Long addPracticeItem(TeachingPlanPracticeItem item);

    void updatePracticeItem(TeachingPlanPracticeItem item);

    void deletePracticeItem(Long id);

    List<TeachingPlanPracticeItemDetail> listPracticeItemDetail(Long itemId);

    Long addPracticeItemDetail(TeachingPlanPracticeItemDetail detail);

    void updatePracticeItemDetail(TeachingPlanPracticeItemDetail detail);

    void deletePracticeItemDetail(Long id);

    List<TeachingPlanAssessment> listAssessment(Long planId);

    Long addAssessment(TeachingPlanAssessment assessment);

    void updateAssessment(TeachingPlanAssessment assessment);

    void deleteAssessment(Long id);

    List<TeachingPlanTextbook> listTextbook(Long planId);

    Long addTextbook(TeachingPlanTextbook textbook);

    void updateTextbook(TeachingPlanTextbook textbook);

    void deleteTextbook(Long id);

    List<TeachingPlanCondition> listCondition(Long planId);

    Long addCondition(TeachingPlanCondition condition);

    void updateCondition(TeachingPlanCondition condition);

    void deleteCondition(Long id);

    /**
     * 条件保障大保存（整表重建）：先按 planId 逻辑删除旧记录，再按 conditions 批量写入。
     * conditions 为空/null = 清空该 plan 全部条件；每行 planId 以入参顶层为准覆盖。
     */
    void saveConditions(TeachingPlanConditionSaveVo saveVo);

    /**
     * 实践项目(type=4)「三、组织与实施」整表大保存。
     * - teamScale/division：复用 t_csys_teaching_plan_section（sectionTitle=团队规模/分工方式），按 planId+title upsert；
     *   null 不改，空串清空。
     * - steps：t_csys_teaching_plan_process_step 整表重建（deleteByPlanId + insertBatch）；空/null=清空。
     *
     * @param saveVo 入参
     */
    void saveOrganization(TeachingPlanOrganizationSaveVo saveVo);

    List<TeachingPlanProcessStep> listProcessStep(Long planId);

    Long addProcessStep(TeachingPlanProcessStep step);

    void updateProcessStep(TeachingPlanProcessStep step);

    void deleteProcessStep(Long id);

    List<TeachingPlanRef> listRef(Long planId, Integer refType);

    Long addRef(TeachingPlanRef ref);

    void updateRef(TeachingPlanRef ref);

    void deleteRef(Long id);
}
