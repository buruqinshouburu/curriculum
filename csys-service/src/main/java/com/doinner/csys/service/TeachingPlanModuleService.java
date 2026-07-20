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
import com.doinner.csys.domain.TeachingPlanTextbook;
import com.doinner.csys.domain.vo.TeachingPlanMajorVo;
import com.doinner.csys.domain.vo.TeachingPlanObjectiveSaveVo;
import com.doinner.csys.entity.csys.po.CourseKnowledgeUnit;

import java.util.List;

/**
 * 课程教学计划各模块 Service。
 *
 * 承载目标/内容/达成设计/实验/考核/教材/条件/步骤/引用等模块逻辑，
 * 与 {@link TeachingPlanService} 主流程拆开。
 *
 * @author codex
 */
public interface TeachingPlanModuleService {

    // ============ 6. 专业查询(教学计划id + 课程id) ============

    /**
     * 根据教学计划id与课程id查询专业id、name、status。
     * 专业列表与 /quoteMajor 同源（源课->调用课->排课->培养方案 major_id）；
     * status：计划+专业下至少有一条目标且每条都绑定毕业要求时为1，否则0。
     */
    List<TeachingPlanMajorVo> listMajor(Long teachingPlanId, Long courseId);

    // ============ 7. 教学计划目标 t_csys_teaching_plan_objective ============

    List<TeachingPlanObjective> listObjective(Long planId, Long contextId);

    Long addObjective(TeachingPlanObjective objective);

    void updateObjective(TeachingPlanObjective objective);

    void deleteObjective(Long id);

    /**
     * 目标 + 支撑毕业要求 同事务保存。
     * 有 objective.id 则更新目标并重建 ref；无则新增目标再插 ref。
     *
     * @return 目标 id
     */
    Long saveObjectiveWithRefs(TeachingPlanObjectiveSaveVo saveVo);

    // ============ 8. 课程绑定毕业要求 ============

    /**
     * 根据总库课程id查询绑定的毕业要求（全部调用课汇总，兼容旧调用）。
     */
    List<StandardGraduation> listCourseGraduation(Long courseId);

    /**
     * 按当前 tab 上下文过滤：仅返回该 context 对应调用课(quoteCourseId)已绑定的毕业要求。
     * contextId 为空时回退为 listCourseGraduation(courseId)。
     */
    List<StandardGraduation> listCourseGraduationByContext(Long courseId, Long contextId);

    // ============ 9. 教学计划目标支撑毕业要求 ============

    List<TeachingPlanObjectiveRef> listObjectiveRef(Long objectiveId);

    Long addObjectiveRef(TeachingPlanObjectiveRef ref);

    void updateObjectiveRef(TeachingPlanObjectiveRef ref);

    void deleteObjectiveRef(Long id);

    // ============ 10. 教学内容与学时安排 ============

    List<TeachingPlanContent> listContent(Long planId);

    Long addContent(TeachingPlanContent content);

    void updateContent(TeachingPlanContent content);

    void deleteContent(Long id);

    // ============ 11-13. 目标达成设计 ============

    List<TeachingPlanTargetDesign> listTargetDesign(Long planId, Long contextId, String designTypeCode);

    List<CourseKnowledgeUnit> listKnowledgeUnitInit(Long courseId);

    Long addTargetDesign(TeachingPlanTargetDesign design);

    void updateTargetDesign(TeachingPlanTargetDesign design);

    void deleteTargetDesign(Long id);

    // ============ 14. 实验/实践环节 ============

    List<TeachingPlanPracticeItem> listPracticeItem(Long planId);

    Long addPracticeItem(TeachingPlanPracticeItem item);

    void updatePracticeItem(TeachingPlanPracticeItem item);

    void deletePracticeItem(Long id);

    List<TeachingPlanPracticeItemDetail> listPracticeItemDetail(Long itemId);

    Long addPracticeItemDetail(TeachingPlanPracticeItemDetail detail);

    void updatePracticeItemDetail(TeachingPlanPracticeItemDetail detail);

    void deletePracticeItemDetail(Long id);

    // ============ 15. 考核评价 ============

    List<TeachingPlanAssessment> listAssessment(Long planId);

    Long addAssessment(TeachingPlanAssessment assessment);

    void updateAssessment(TeachingPlanAssessment assessment);

    void deleteAssessment(Long id);

    // ============ 16. 教材 ============

    List<TeachingPlanTextbook> listTextbook(Long planId);

    Long addTextbook(TeachingPlanTextbook textbook);

    void updateTextbook(TeachingPlanTextbook textbook);

    void deleteTextbook(Long id);

    // ============ 17. 条件保障 ============

    List<TeachingPlanCondition> listCondition(Long planId);

    Long addCondition(TeachingPlanCondition condition);

    void updateCondition(TeachingPlanCondition condition);

    void deleteCondition(Long id);

    // ============ 18. 实施步骤 t_csys_teaching_plan_process_step ============

    List<TeachingPlanProcessStep> listProcessStep(Long planId);

    Long addProcessStep(TeachingPlanProcessStep step);

    void updateProcessStep(TeachingPlanProcessStep step);

    void deleteProcessStep(Long id);

    // ============ 19. 通用引用 t_csys_teaching_plan_ref ============

    /**
     * @param refType 可空；空则返回该计划全部引用
     */
    List<TeachingPlanRef> listRef(Long planId, Integer refType);

    Long addRef(TeachingPlanRef ref);

    void updateRef(TeachingPlanRef ref);

    void deleteRef(Long id);
}
