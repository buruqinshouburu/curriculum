package com.doinner.csys.service.impl;

import com.doinner.csys.dao.CourseMapper;
import com.doinner.csys.dao.CourseRefGraduationMapper;
import com.doinner.csys.dao.StandardGraduationMapper;
import com.doinner.csys.dao.TeachingPlanAssessmentMapper;
import com.doinner.csys.dao.TeachingPlanConditionMapper;
import com.doinner.csys.dao.TeachingPlanContentMapper;
import com.doinner.csys.dao.TeachingPlanContextMapper;
import com.doinner.csys.dao.TeachingPlanObjectiveMapper;
import com.doinner.csys.dao.TeachingPlanObjectiveRefMapper;
import com.doinner.csys.dao.TeachingPlanPracticeItemDetailMapper;
import com.doinner.csys.dao.TeachingPlanPracticeItemMapper;
import com.doinner.csys.dao.TeachingPlanProcessStepMapper;
import com.doinner.csys.dao.TeachingPlanRefMapper;
import com.doinner.csys.dao.TeachingPlanTargetDesignMapper;
import com.doinner.csys.dao.TeachingPlanTextbookMapper;
import com.doinner.csys.domain.Course;
import com.doinner.csys.domain.CourseRefGraduation;
import com.doinner.csys.domain.StandardGraduation;
import com.doinner.csys.domain.TeachingPlanAssessment;
import com.doinner.csys.domain.TeachingPlanCondition;
import com.doinner.csys.domain.TeachingPlanContent;
import com.doinner.csys.domain.TeachingPlanContext;
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
import com.doinner.csys.service.TeachingPlanModuleService;
import com.doinner.csys.utils.UserUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 课程教学计划各模块 Service 实现(AGENTS 任务 6-17)。
 *
 * 复用已有 DAO 层，写法与原 TeachingPlanServiceImpl 中教员/章节保持一致：
 * 写操作前统一 UserUtils.reflash 回填创建人/修改人/时间，删除均为逻辑删除。
 *
 * @author codex
 */
@Service
@Transactional(readOnly = true)
public class TeachingPlanModuleServiceImpl implements TeachingPlanModuleService {

    @Resource
    private TeachingPlanObjectiveMapper teachingPlanObjectiveMapper;

    @Resource
    private TeachingPlanObjectiveRefMapper teachingPlanObjectiveRefMapper;

    @Resource
    private TeachingPlanContentMapper teachingPlanContentMapper;

    @Resource
    private TeachingPlanTargetDesignMapper teachingPlanTargetDesignMapper;

    @Resource
    private TeachingPlanPracticeItemMapper teachingPlanPracticeItemMapper;

    @Resource
    private TeachingPlanPracticeItemDetailMapper teachingPlanPracticeItemDetailMapper;

    @Resource
    private TeachingPlanAssessmentMapper teachingPlanAssessmentMapper;

    @Resource
    private TeachingPlanTextbookMapper teachingPlanTextbookMapper;

    @Resource
    private TeachingPlanConditionMapper teachingPlanConditionMapper;

    @Resource
    private TeachingPlanProcessStepMapper teachingPlanProcessStepMapper;

    @Resource
    private TeachingPlanRefMapper teachingPlanRefMapper;

    @Resource
    private TeachingPlanContextMapper teachingPlanContextMapper;

    @Resource
    private CourseMapper courseMapper;

    @Resource
    private CourseRefGraduationMapper courseRefGraduationMapper;

    @Resource
    private StandardGraduationMapper standardGraduationMapper;

    // ============ 6. 专业查询 ============

    @Override
    public List<TeachingPlanMajorVo> listMajor(Long teachingPlanId, Long courseId) {
        // 1. 根据总库课程id查询专业id与名称
        List<TeachingPlanMajorVo> majors = teachingPlanObjectiveMapper.selectMajorByCourseId(courseId);
        if (ObjectUtils.isEmpty(majors)) {
            return new ArrayList<>();
        }
        // 2. status：教学计划id为空时无目标数据，全部记为0(未完成)
        for (TeachingPlanMajorVo major : majors) {
            major.setStatus(computeMajorStatus(teachingPlanId, major.getId()));
        }
        return majors;
    }

    /**
     * 计算专业状态：该计划+专业下至少有一条目标，且每条目标都至少绑定一条毕业要求时为1，否则为0。
     */
    private Integer computeMajorStatus(Long teachingPlanId, Long majorId) {
        if (teachingPlanId == null || majorId == null) {
            return 0;
        }
        int objectiveCount = teachingPlanObjectiveMapper.countByPlanAndMajor(teachingPlanId, majorId);
        if (objectiveCount <= 0) {
            return 0;
        }
        int unboundCount = teachingPlanObjectiveMapper.countUnboundObjectiveByPlanAndMajor(teachingPlanId, majorId);
        return unboundCount == 0 ? 1 : 0;
    }

    // ============ 7. 教学计划目标 ============

    @Override
    public List<TeachingPlanObjective> listObjective(Long planId, Long contextId) {
        return teachingPlanObjectiveMapper.selectByPlanAndContext(planId, contextId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long addObjective(TeachingPlanObjective objective) {
        UserUtils.reflash(objective);
        teachingPlanObjectiveMapper.insert(objective);
        return objective.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateObjective(TeachingPlanObjective objective) {
        UserUtils.reflash(objective);
        teachingPlanObjectiveMapper.updateById(objective);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteObjective(Long id) {
        teachingPlanObjectiveMapper.deleteById(id);
    }

    // ============ 8. 课程绑定毕业要求 ============

    @Override
    public List<StandardGraduation> listCourseGraduation(Long courseId) {
        return listGraduationsByQuoteCourseIds(resolveQuoteCourseIds(courseId));
    }

    @Override
    public List<StandardGraduation> listCourseGraduationByContext(Long courseId, Long contextId) {
        if (contextId == null) {
            return listCourseGraduation(courseId);
        }
        TeachingPlanContext ctx = teachingPlanContextMapper.selectById(contextId);
        if (ctx == null || ctx.getQuoteCourseId() == null) {
            return new ArrayList<>();
        }
        // 仅当前 tab 对应调用课的毕业要求
        return listGraduationsByQuoteCourseIds(Collections.singletonList(ctx.getQuoteCourseId()));
    }

    private List<Long> resolveQuoteCourseIds(Long sourceCourseId) {
        if (sourceCourseId == null) {
            return new ArrayList<>();
        }
        List<Course> courses = courseMapper.selectCourseBySourceId(sourceCourseId);
        if (ObjectUtils.isEmpty(courses)) {
            return new ArrayList<>();
        }
        return courses.stream().map(Course::getId).filter(Objects::nonNull).collect(Collectors.toList());
    }

    private List<StandardGraduation> listGraduationsByQuoteCourseIds(List<Long> quoteCourseIds) {
        if (ObjectUtils.isEmpty(quoteCourseIds)) {
            return new ArrayList<>();
        }
        List<CourseRefGraduation> refs =
                courseRefGraduationMapper.selectCourseTargetRefGraduationByCourseIds(quoteCourseIds);
        if (ObjectUtils.isEmpty(refs)) {
            return new ArrayList<>();
        }
        List<Long> graduationIds = refs.stream()
                .map(CourseRefGraduation::getGraduationId)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());
        if (ObjectUtils.isEmpty(graduationIds)) {
            return new ArrayList<>();
        }
        return standardGraduationMapper.selectStandardGraduationByIds(graduationIds);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long saveObjectiveWithRefs(TeachingPlanObjectiveSaveVo saveVo) {
        if (saveVo == null || saveVo.getObjective() == null) {
            throw new IllegalArgumentException("教学目标不能为空");
        }
        TeachingPlanObjective objective = saveVo.getObjective();
        UserUtils.reflash(objective);
        if (objective.getId() == null) {
            teachingPlanObjectiveMapper.insert(objective);
        } else {
            teachingPlanObjectiveMapper.updateById(objective);
            // 重建绑定：先逻辑删除旧 ref
            teachingPlanObjectiveRefMapper.deleteByObjectiveId(objective.getId());
        }
        List<TeachingPlanObjectiveRef> refs = saveVo.getRefs();
        if (ObjectUtils.isNotEmpty(refs)) {
            for (TeachingPlanObjectiveRef ref : refs) {
                ref.setId(null);
                ref.setObjectiveId(objective.getId());
                if (ref.getPlanId() == null) {
                    ref.setPlanId(objective.getPlanId());
                }
                if (ref.getContextId() == null) {
                    ref.setContextId(objective.getContextId());
                }
                UserUtils.reflash(ref);
                teachingPlanObjectiveRefMapper.insert(ref);
            }
        }
        return objective.getId();
    }

    // ============ 9. 教学计划目标支撑毕业要求 ============

    @Override
    public List<TeachingPlanObjectiveRef> listObjectiveRef(Long objectiveId) {
        return teachingPlanObjectiveRefMapper.selectByObjectiveId(objectiveId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long addObjectiveRef(TeachingPlanObjectiveRef ref) {
        UserUtils.reflash(ref);
        teachingPlanObjectiveRefMapper.insert(ref);
        return ref.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateObjectiveRef(TeachingPlanObjectiveRef ref) {
        UserUtils.reflash(ref);
        teachingPlanObjectiveRefMapper.updateById(ref);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteObjectiveRef(Long id) {
        teachingPlanObjectiveRefMapper.deleteById(id);
    }

    // ============ 10. 教学内容与学时安排 ============

    @Override
    public List<TeachingPlanContent> listContent(Long planId) {
        return teachingPlanContentMapper.selectByPlanId(planId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long addContent(TeachingPlanContent content) {
        UserUtils.reflash(content);
        teachingPlanContentMapper.insert(content);
        return content.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateContent(TeachingPlanContent content) {
        UserUtils.reflash(content);
        teachingPlanContentMapper.updateById(content);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteContent(Long id) {
        teachingPlanContentMapper.deleteById(id);
    }

    // ============ 11-13. 目标达成设计 ============

    @Override
    public List<TeachingPlanTargetDesign> listTargetDesign(Long planId, Long contextId, String designTypeCode) {
        return teachingPlanTargetDesignMapper.selectByPlanContextAndType(planId, contextId, designTypeCode);
    }

    @Override
    public List<CourseKnowledgeUnit> listKnowledgeUnitInit(Long courseId) {
        List<CourseKnowledgeUnit> units = teachingPlanTargetDesignMapper.selectKnowledgeUnitInitByCourseId(courseId);
        return units == null ? new ArrayList<>() : units;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long addTargetDesign(TeachingPlanTargetDesign design) {
        UserUtils.reflash(design);
        teachingPlanTargetDesignMapper.insert(design);
        return design.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateTargetDesign(TeachingPlanTargetDesign design) {
        UserUtils.reflash(design);
        teachingPlanTargetDesignMapper.updateById(design);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteTargetDesign(Long id) {
        teachingPlanTargetDesignMapper.deleteById(id);
    }

    // ============ 14. 实验/实践环节 ============

    @Override
    public List<TeachingPlanPracticeItem> listPracticeItem(Long planId) {
        return teachingPlanPracticeItemMapper.selectByPlanId(planId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long addPracticeItem(TeachingPlanPracticeItem item) {
        UserUtils.reflash(item);
        teachingPlanPracticeItemMapper.insert(item);
        return item.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updatePracticeItem(TeachingPlanPracticeItem item) {
        UserUtils.reflash(item);
        teachingPlanPracticeItemMapper.updateById(item);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deletePracticeItem(Long id) {
        // 逻辑删除项目，同时清理其明细
        teachingPlanPracticeItemMapper.deleteById(id);
        teachingPlanPracticeItemDetailMapper.deleteByItemId(id);
    }

    @Override
    public List<TeachingPlanPracticeItemDetail> listPracticeItemDetail(Long itemId) {
        return teachingPlanPracticeItemDetailMapper.selectByItemId(itemId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long addPracticeItemDetail(TeachingPlanPracticeItemDetail detail) {
        teachingPlanPracticeItemDetailMapper.insert(detail);
        return detail.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updatePracticeItemDetail(TeachingPlanPracticeItemDetail detail) {
        teachingPlanPracticeItemDetailMapper.updateById(detail);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deletePracticeItemDetail(Long id) {
        teachingPlanPracticeItemDetailMapper.deleteById(id);
    }

    // ============ 15. 考核评价 ============

    @Override
    public List<TeachingPlanAssessment> listAssessment(Long planId) {
        return teachingPlanAssessmentMapper.selectByPlanId(planId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long addAssessment(TeachingPlanAssessment assessment) {
        UserUtils.reflash(assessment);
        teachingPlanAssessmentMapper.insert(assessment);
        return assessment.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateAssessment(TeachingPlanAssessment assessment) {
        UserUtils.reflash(assessment);
        teachingPlanAssessmentMapper.updateById(assessment);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteAssessment(Long id) {
        teachingPlanAssessmentMapper.deleteById(id);
    }

    // ============ 16. 教材 ============

    @Override
    public List<TeachingPlanTextbook> listTextbook(Long planId) {
        return teachingPlanTextbookMapper.selectByPlanId(planId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long addTextbook(TeachingPlanTextbook textbook) {
        UserUtils.reflash(textbook);
        teachingPlanTextbookMapper.insert(textbook);
        return textbook.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateTextbook(TeachingPlanTextbook textbook) {
        UserUtils.reflash(textbook);
        teachingPlanTextbookMapper.updateById(textbook);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteTextbook(Long id) {
        teachingPlanTextbookMapper.deleteById(id);
    }

    // ============ 17. 条件保障 ============

    @Override
    public List<TeachingPlanCondition> listCondition(Long planId) {
        return teachingPlanConditionMapper.selectByPlanId(planId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long addCondition(TeachingPlanCondition condition) {
        UserUtils.reflash(condition);
        teachingPlanConditionMapper.insert(condition);
        return condition.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateCondition(TeachingPlanCondition condition) {
        UserUtils.reflash(condition);
        teachingPlanConditionMapper.updateById(condition);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteCondition(Long id) {
        teachingPlanConditionMapper.deleteById(id);
    }

    // ============ 18. 实施步骤 ============

    @Override
    public List<TeachingPlanProcessStep> listProcessStep(Long planId) {
        return teachingPlanProcessStepMapper.selectByPlanId(planId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long addProcessStep(TeachingPlanProcessStep step) {
        UserUtils.reflash(step);
        teachingPlanProcessStepMapper.insert(step);
        return step.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateProcessStep(TeachingPlanProcessStep step) {
        UserUtils.reflash(step);
        teachingPlanProcessStepMapper.updateById(step);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteProcessStep(Long id) {
        teachingPlanProcessStepMapper.deleteById(id);
    }

    // ============ 19. 通用引用 ============

    @Override
    public List<TeachingPlanRef> listRef(Long planId, Integer refType) {
        if (refType != null) {
            return teachingPlanRefMapper.selectByPlanAndType(planId, refType);
        }
        return teachingPlanRefMapper.selectByPlanId(planId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long addRef(TeachingPlanRef ref) {
        UserUtils.reflash(ref);
        teachingPlanRefMapper.insert(ref);
        return ref.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateRef(TeachingPlanRef ref) {
        UserUtils.reflash(ref);
        teachingPlanRefMapper.updateById(ref);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteRef(Long id) {
        teachingPlanRefMapper.deleteById(id);
    }
}
