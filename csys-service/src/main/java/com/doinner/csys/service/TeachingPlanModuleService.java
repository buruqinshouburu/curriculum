package com.doinner.csys.service;

import com.doinner.csys.domain.StandardGraduation;
import com.doinner.csys.domain.TeachingPlanAssessment;
import com.doinner.csys.domain.TeachingPlanCondition;
import com.doinner.csys.domain.TeachingPlanContent;
import com.doinner.csys.domain.TeachingPlanObjective;
import com.doinner.csys.domain.TeachingPlanObjectiveRef;
import com.doinner.csys.domain.TeachingPlanPracticeItem;
import com.doinner.csys.domain.TeachingPlanPracticeItemDetail;
import com.doinner.csys.domain.TeachingPlanTargetDesign;
import com.doinner.csys.domain.TeachingPlanTextbook;
import com.doinner.csys.domain.vo.TeachingPlanMajorVo;
import com.doinner.csys.entity.csys.po.CourseKnowledgeUnit;

import java.util.List;

/**
 * 课程教学计划各模块 Service。
 *
 * 承载 AGENTS 任务 6-17 的接口逻辑，与原有 {@link TeachingPlanService} 拆开，
 * 避免改动已有主流程代码。
 *
 * @author codex
 */
public interface TeachingPlanModuleService {

    // ============ 6. 专业查询(教学计划id + 课程id) ============

    /**
     * 根据教学计划id与课程id查询专业id、name、status。
     * 具体查询逻辑留空，由业务方后续补充。
     */
    List<TeachingPlanMajorVo> listMajor(Long teachingPlanId, Long courseId);

    // ============ 7. 教学计划目标 t_csys_teaching_plan_objective ============

    /** 按计划与上下文查询目标 */
    List<TeachingPlanObjective> listObjective(Long planId, Long contextId);

    /** 新增目标，返回主键id */
    Long addObjective(TeachingPlanObjective objective);

    /** 修改目标 */
    void updateObjective(TeachingPlanObjective objective);

    /** 删除目标(逻辑删除) */
    void deleteObjective(Long id);

    // ============ 8. 课程绑定毕业要求(按课程id) ============

    /**
     * 根据总库课程id查询绑定的毕业要求。
     * 先查 source_id = 课程id 的调用课程，再取这些课程绑定的毕业要求。
     */
    List<StandardGraduation> listCourseGraduation(Long courseId);

    // ============ 9. 教学计划目标支撑毕业要求 t_csys_teaching_plan_objective_ref ============

    /** 按目标id查询绑定的毕业要求 */
    List<TeachingPlanObjectiveRef> listObjectiveRef(Long objectiveId);

    /** 新增目标毕业要求绑定，返回主键id */
    Long addObjectiveRef(TeachingPlanObjectiveRef ref);

    /** 修改目标毕业要求绑定 */
    void updateObjectiveRef(TeachingPlanObjectiveRef ref);

    /** 删除目标毕业要求绑定(逻辑删除) */
    void deleteObjectiveRef(Long id);

    // ============ 10. 教学内容与学时安排 t_csys_teaching_plan_content ============

    List<TeachingPlanContent> listContent(Long planId);

    Long addContent(TeachingPlanContent content);

    void updateContent(TeachingPlanContent content);

    void deleteContent(Long id);

    // ============ 11-13. 目标达成设计 t_csys_teaching_plan_target_design ============

    /**
     * 按计划、上下文、设计类型查询目标达成设计。
     * 表内无数据且为知识目标时，用课程知识单元初始化(见 {@link #listKnowledgeUnitInit(Long)})。
     */
    List<TeachingPlanTargetDesign> listTargetDesign(Long planId, Long contextId, String designTypeCode);

    /**
     * 知识目标初始化：根据总库课程id查询调用课程关联的知识单元。
     */
    List<CourseKnowledgeUnit> listKnowledgeUnitInit(Long courseId);

    Long addTargetDesign(TeachingPlanTargetDesign design);

    void updateTargetDesign(TeachingPlanTargetDesign design);

    void deleteTargetDesign(Long id);

    // ============ 14. 实验/实践环节 t_csys_teaching_plan_practice_item(_detail) ============

    List<TeachingPlanPracticeItem> listPracticeItem(Long planId);

    Long addPracticeItem(TeachingPlanPracticeItem item);

    void updatePracticeItem(TeachingPlanPracticeItem item);

    void deletePracticeItem(Long id);

    List<TeachingPlanPracticeItemDetail> listPracticeItemDetail(Long itemId);

    Long addPracticeItemDetail(TeachingPlanPracticeItemDetail detail);

    void updatePracticeItemDetail(TeachingPlanPracticeItemDetail detail);

    void deletePracticeItemDetail(Long id);

    // ============ 15. 考核评价 t_csys_teaching_plan_assessment ============

    List<TeachingPlanAssessment> listAssessment(Long planId);

    Long addAssessment(TeachingPlanAssessment assessment);

    void updateAssessment(TeachingPlanAssessment assessment);

    void deleteAssessment(Long id);

    // ============ 16. 教材 t_csys_teaching_plan_textbook ============

    List<TeachingPlanTextbook> listTextbook(Long planId);

    Long addTextbook(TeachingPlanTextbook textbook);

    void updateTextbook(TeachingPlanTextbook textbook);

    void deleteTextbook(Long id);

    // ============ 17. 条件保障(教室等) t_csys_teaching_plan_condition ============

    List<TeachingPlanCondition> listCondition(Long planId);

    Long addCondition(TeachingPlanCondition condition);

    void updateCondition(TeachingPlanCondition condition);

    void deleteCondition(Long id);
}
