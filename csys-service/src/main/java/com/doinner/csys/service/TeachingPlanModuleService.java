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
import com.doinner.csys.domain.vo.TeachingPlanSchemeVo;
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

    Long addObjective(TeachingPlanObjective objective);

    void updateObjective(TeachingPlanObjective objective);

    void deleteObjective(Long id);

    /** 目标 + 支撑毕业要求同事务保存；objective.schemeId 必填 */
    Long saveObjectiveWithRefs(TeachingPlanObjectiveSaveVo saveVo);

    /** 全部调用课汇总（兼容） */
    List<StandardGraduation> listCourseGraduation(Long courseId);

    /**
     * 按培养方案汇总毕业要求：该 scheme 下引用本源课的**全部**调用课所绑定的毕业要求去重。
     * 同一专业课在方案中被引用多次时，全部计入。
     * schemeId 为空时回退 listCourseGraduation(courseId)。
     */
    List<StandardGraduation> listCourseGraduationByScheme(Long courseId, Long schemeId);

    List<TeachingPlanObjectiveRef> listObjectiveRef(Long objectiveId);

    Long addObjectiveRef(TeachingPlanObjectiveRef ref);

    void updateObjectiveRef(TeachingPlanObjectiveRef ref);

    void deleteObjectiveRef(Long id);

    List<TeachingPlanContent> listContent(Long planId);

    Long addContent(TeachingPlanContent content);

    void updateContent(TeachingPlanContent content);

    void deleteContent(Long id);

    List<TeachingPlanTargetDesign> listTargetDesign(Long planId, Long schemeId, String designTypeCode);

    List<CourseKnowledgeUnit> listKnowledgeUnitInit(Long courseId);

    Long addTargetDesign(TeachingPlanTargetDesign design);

    void updateTargetDesign(TeachingPlanTargetDesign design);

    void deleteTargetDesign(Long id);

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

    List<TeachingPlanProcessStep> listProcessStep(Long planId);

    Long addProcessStep(TeachingPlanProcessStep step);

    void updateProcessStep(TeachingPlanProcessStep step);

    void deleteProcessStep(Long id);

    List<TeachingPlanRef> listRef(Long planId, Integer refType);

    Long addRef(TeachingPlanRef ref);

    void updateRef(TeachingPlanRef ref);

    void deleteRef(Long id);
}
