package com.doinner.csys.service.impl;

import com.doinner.csys.dao.CourseMapper;
import com.doinner.csys.dao.TrainingSchemeCourseScheduleMapper;
import com.doinner.csys.dao.TrainingSchemeRefCourseMapper;
import com.doinner.csys.dao.CourseRefGraduationMapper;
import com.doinner.csys.dao.StandardGraduationMapper;
import com.doinner.csys.dao.TeachingPlanAssessmentMapper;
import com.doinner.csys.dao.TeachingPlanConditionMapper;
import com.doinner.csys.dao.TeachingPlanContentMapper;
import com.doinner.csys.dao.TeachingPlanMapper;
import com.doinner.csys.dao.TeachingPlanObjectiveMapper;
import com.doinner.csys.dao.TeachingPlanObjectiveRefMapper;
import com.doinner.csys.dao.TeachingPlanObjectiveAssessmentMapper;
import com.doinner.csys.dao.TeachingPlanPracticeItemDetailMapper;
import com.doinner.csys.dao.TeachingPlanPracticeItemMapper;
import com.doinner.csys.dao.TeachingPlanProcessStepMapper;
import com.doinner.csys.dao.TeachingPlanRefMapper;
import com.doinner.csys.dao.TeachingPlanSectionMapper;
import com.doinner.csys.dao.TeachingPlanTargetDesignMapper;
import com.doinner.csys.dao.TeachingPlanTextbookMapper;
import com.doinner.csys.domain.Course;
import com.doinner.csys.domain.CourseRefGraduation;
import com.doinner.csys.domain.StandardGraduation;
import com.doinner.csys.utils.TreeBuilderUtils;
import com.doinner.csys.domain.TeachingPlan;
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
import com.doinner.csys.domain.TeachingPlanSection;
import com.doinner.csys.domain.TeachingPlanTargetDesign;
import com.doinner.csys.domain.TeachingPlanTextbook;
import com.doinner.csys.domain.vo.TeachingPlanConditionSaveVo;
import com.doinner.csys.domain.vo.TeachingPlanMajorVo;
import com.doinner.csys.domain.vo.TeachingPlanObjectiveOptionVo;
import com.doinner.csys.domain.vo.TeachingPlanObjectiveBatchSaveVo;
import com.doinner.csys.domain.vo.TeachingPlanObjectiveRefSaveVo;
import com.doinner.csys.domain.vo.TeachingPlanObjectiveSaveVo;
import com.doinner.csys.domain.vo.TeachingPlanObjectiveAssessmentSaveVo;
import com.doinner.csys.domain.vo.TeachingPlanObjectiveTreeVo;
import com.doinner.csys.domain.vo.TeachingPlanOrganizationSaveVo;
import com.doinner.csys.domain.vo.TeachingPlanSchemeVo;
import com.doinner.csys.domain.vo.CourseVo;
import com.doinner.csys.entity.csys.model.DictContent;
import com.doinner.csys.entity.csys.po.CourseKnowledgeUnit;
import com.doinner.csys.service.TeachingPlanModuleService;
import com.doinner.csys.utils.CurDictUtils;
import com.doinner.csys.utils.UserUtils;
import com.doinner.system.domain.entity.SysDictData;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.HashMap;

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

    /** 教学目标类型字典 type（知识/能力/素质等） */
    private static final String DICT_PLAN_TARGET_TYPE = "sys_plan_target_type";

    /** 条件保障类型字典 type */
    private static final String DICT_CONDITION_TYPE = "sys_condition_type";

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final TypeReference<List<TeachingPlanTargetDesign.KnowledgePointItem>> KP_LIST_TYPE =
            new TypeReference<List<TeachingPlanTargetDesign.KnowledgePointItem>>() {};

    @Resource
    private TeachingPlanObjectiveMapper teachingPlanObjectiveMapper;

    @Resource
    private TeachingPlanObjectiveRefMapper teachingPlanObjectiveRefMapper;

    @Resource
    private TeachingPlanObjectiveAssessmentMapper teachingPlanObjectiveAssessmentMapper;

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
    private TeachingPlanMapper teachingPlanMapper;

    @Resource
    private TeachingPlanTextbookMapper teachingPlanTextbookMapper;

    @Resource
    private TeachingPlanConditionMapper teachingPlanConditionMapper;

    @Resource
    private TeachingPlanProcessStepMapper teachingPlanProcessStepMapper;

    @Resource
    private TeachingPlanSectionMapper teachingPlanSectionMapper;

    @Resource
    private TeachingPlanRefMapper teachingPlanRefMapper;

    @Resource
    private CourseMapper courseMapper;

    @Resource
    private TrainingSchemeCourseScheduleMapper trainingSchemeCourseScheduleMapper;

    @Resource
    private TrainingSchemeRefCourseMapper trainingSchemeRefCourseMapper;

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


    @Override
    public List<TeachingPlanSchemeVo> listSchemes(Long sourceCourseId) {
        if (sourceCourseId == null) {
            return new ArrayList<>();
        }
        List<TeachingPlanSchemeVo> list =
                trainingSchemeCourseScheduleMapper.selectQuoteSchemesBySourceCourseId(sourceCourseId);
        return list == null ? new ArrayList<>() : list;
    }

    // ============ 7. 教学计划目标 ============

    /**
     * 源课是否为公共基础课程：course_Module == DictContent.GENERAL_EDUCATION_COURSES_SCHEDULE。
     * 公共基础：目标/毕业要求按 plan 单组（scheme_id 恒为 null），不按培养方案拆分。
     */
    private boolean isPublicFoundationCourse(Long sourceCourseId) {
        if (sourceCourseId == null) {
            return false;
        }
        CourseVo course = courseMapper.selectCourseById(sourceCourseId);
        if (course == null || StringUtils.isBlank(course.getCourseModule())) {
            return false;
        }
        return Objects.equals(course.getCourseModule(), DictContent.GENERAL_EDUCATION_COURSES_SCHEDULE);
    }

    /** 教学计划对应源课是否为公共基础课程 */
    private boolean isPublicFoundationPlan(Long planId) {
        if (planId == null) {
            return false;
        }
        TeachingPlan plan = teachingPlanMapper.selectById(planId);
        if (plan == null) {
            return false;
        }
        return isPublicFoundationCourse(plan.getSourceCourseId());
    }

    @Override
    public List<TeachingPlanObjective> listObjective(Long planId, Long schemeId) {
        boolean onlyNull = isPublicFoundationPlan(planId);
        // 公共基础：忽略入参 schemeId，只取 scheme_id IS NULL 单组
        return teachingPlanObjectiveMapper.selectByPlanAndScheme(
                planId, onlyNull ? null : schemeId, onlyNull);
    }

    @Override
    public List<TeachingPlanObjectiveTreeVo> listObjectiveTree(Long planId, Long schemeId, String objectiveTypeCode) {
        if (planId == null) {
            throw new IllegalArgumentException("planId 不能为空");
        }
        // schemeId 可选：传则按 scheme 过滤；不传则不过滤 scheme。
        // 不按课程模块判定是否必填——源课模块与引用后展示模块可能不一致，前端有 tab 就传、无 tab 就不传。
        // 1) 目标类型字典（顶层节点顺序以字典为准）
        List<SysDictData> typeDicts = CurDictUtils.getDictData(DICT_PLAN_TARGET_TYPE);
        if (ObjectUtils.isEmpty(typeDicts)) {
            typeDicts = Collections.emptyList();
        }
        // 可选：按目标类型过滤字典顶层
        if (StringUtils.isNotBlank(objectiveTypeCode)) {
            typeDicts = typeDicts.stream()
                    .filter(d -> objectiveTypeCode.equals(d.getDictValue()))
                    .collect(Collectors.toList());
        }

        // 2) 目标内容（可按类型过滤；schemeId 空=plan 下全量）
        List<TeachingPlanObjective> objectives =
                teachingPlanObjectiveMapper.selectByPlanAndSchemeAndType(
                        planId, schemeId, objectiveTypeCode, false);
        Map<String, List<TeachingPlanObjective>> objectivesByType = new LinkedHashMap<>();
        if (ObjectUtils.isNotEmpty(objectives)) {
            for (TeachingPlanObjective obj : objectives) {
                String typeCode = obj.getObjectiveTypeCode() == null ? "" : obj.getObjectiveTypeCode();
                objectivesByType.computeIfAbsent(typeCode, k -> new ArrayList<>()).add(obj);
            }
        }

        // 3) 支撑毕业要求按 objectiveId 分组（一次查出，避免 N+1）
        List<TeachingPlanObjectiveRef> allRefs =
                teachingPlanObjectiveRefMapper.selectByPlanAndScheme(planId, schemeId, false);
        Map<Long, List<TeachingPlanObjectiveRef>> refsByObjectiveId = new LinkedHashMap<>();
        if (ObjectUtils.isNotEmpty(allRefs)) {
            for (TeachingPlanObjectiveRef ref : allRefs) {
                if (ref.getObjectiveId() == null) {
                    continue;
                }
                refsByObjectiveId.computeIfAbsent(ref.getObjectiveId(), k -> new ArrayList<>()).add(ref);
            }
        }

        // 4) 组装树：类型 -> 目标(children) -> 支撑毕业要求(children)
        //    对齐 viewTrainingCourseKnowLedge：setChildren(...)
        List<TeachingPlanObjectiveTreeVo> tree = new ArrayList<>();
        // 字典有定义的类型优先按字典顺序输出
        for (SysDictData dict : typeDicts) {
            String typeCode = dict.getDictValue();
            TeachingPlanObjectiveTreeVo typeNode = new TeachingPlanObjectiveTreeVo();
            typeNode.setId(typeCode);
            typeNode.setName(dict.getDictLabel());
            typeNode.setObjectiveTypeCode(typeCode);
            typeNode.setObjectiveTypeName(dict.getDictLabel());
            typeNode.setPlanId(planId);
            typeNode.setSchemeId(schemeId);
            typeNode.setSort(dict.getDictSort() == null ? null : dict.getDictSort().intValue());
            typeNode.setChildren(buildObjectiveChildren(
                    objectivesByType.remove(typeCode), planId, schemeId, typeCode, dict.getDictLabel(), refsByObjectiveId));
            tree.add(typeNode);
        }
        // 字典未覆盖但库中仍存在的类型（兜底，避免数据丢失）
        if (!objectivesByType.isEmpty()) {
            for (Map.Entry<String, List<TeachingPlanObjective>> entry : objectivesByType.entrySet()) {
                String typeCode = entry.getKey();
                List<TeachingPlanObjective> list = entry.getValue();
                String typeName = null;
                if (ObjectUtils.isNotEmpty(list) && StringUtils.isNotBlank(list.get(0).getObjectiveTypeName())) {
                    typeName = list.get(0).getObjectiveTypeName();
                }
                if (StringUtils.isBlank(typeName)) {
                    typeName = typeCode;
                }
                TeachingPlanObjectiveTreeVo typeNode = new TeachingPlanObjectiveTreeVo();
                typeNode.setId(typeCode);
                typeNode.setName(typeName);
                typeNode.setObjectiveTypeCode(typeCode);
                typeNode.setObjectiveTypeName(typeName);
                typeNode.setPlanId(planId);
                typeNode.setSchemeId(schemeId);
                typeNode.setChildren(buildObjectiveChildren(list, planId, schemeId, typeCode, typeName, refsByObjectiveId));
                tree.add(typeNode);
            }
        }
        return tree;
    }

    /**
     * 目标层节点列表；每个目标 children = 支撑毕业要求列表。
     */
    private List<TeachingPlanObjectiveTreeVo> buildObjectiveChildren(
            List<TeachingPlanObjective> objectives,
            Long planId,
            Long schemeId,
            String typeCode,
            String typeName,
            Map<Long, List<TeachingPlanObjectiveRef>> refsByObjectiveId) {
        if (ObjectUtils.isEmpty(objectives)) {
            return new ArrayList<>();
        }
        List<TeachingPlanObjectiveTreeVo> nodes = new ArrayList<>(objectives.size());
        for (TeachingPlanObjective obj : objectives) {
            TeachingPlanObjectiveTreeVo node = new TeachingPlanObjectiveTreeVo();
            node.setId(obj.getId() == null ? null : obj.getId().toString());
            node.setName(obj.getContent());
            node.setContent(obj.getContent());
            node.setObjectiveId(obj.getId());
            node.setObjectiveTypeCode(StringUtils.isNotBlank(obj.getObjectiveTypeCode()) ? obj.getObjectiveTypeCode() : typeCode);
            node.setObjectiveTypeName(StringUtils.isNotBlank(obj.getObjectiveTypeName()) ? obj.getObjectiveTypeName() : typeName);
            node.setPlanId(obj.getPlanId() != null ? obj.getPlanId() : planId);
            node.setSchemeId(obj.getSchemeId() != null ? obj.getSchemeId() : schemeId);
            node.setMajorId(obj.getMajorId());
            node.setSort(obj.getSort());
            List<TeachingPlanObjectiveRef> refs = refsByObjectiveId.getOrDefault(obj.getId(), Collections.emptyList());
            node.setChildren(refs);
            nodes.add(node);
        }
        return nodes;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long addObjective(TeachingPlanObjective objective) {
        boolean publicFoundation = forceNullSchemeIfPublicFoundation(objective);
        validateObjectiveForInsert(objective, publicFoundation);
        UserUtils.reflash(objective);
        teachingPlanObjectiveMapper.insert(objective);
        return objective.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveObjectivesBatch(TeachingPlanObjectiveBatchSaveVo saveVo) {
        if (saveVo == null || saveVo.getPlanId() == null) {
            throw new IllegalArgumentException("planId 不能为空");
        }
        TeachingPlan plan = teachingPlanMapper.selectById(saveVo.getPlanId());
        if (plan == null) {
            throw new IllegalArgumentException("教学计划不存在: " + saveVo.getPlanId());
        }
        List<TeachingPlanObjectiveSaveVo> rows = saveVo.getObjectives();
        if (plan.getPlanType() != null && plan.getPlanType() == 1 && ObjectUtils.isNotEmpty(rows)) {
            BigDecimal total = BigDecimal.ZERO;
            for (TeachingPlanObjectiveSaveVo row : rows) {
                if (row == null || row.getObjective() == null || row.getObjective().getWeight() == null) {
                    throw new IllegalArgumentException("普通课程每个课程目标都必须填写权重");
                }
                if (row.getObjective().getWeight().compareTo(BigDecimal.ZERO) < 0) {
                    throw new IllegalArgumentException("课程目标权重不能小于0");
                }
                total = total.add(row.getObjective().getWeight());
            }
            if (total.compareTo(BigDecimal.ONE) != 0) {
                throw new IllegalArgumentException("课程目标权重合计必须等于1，当前为" + total.stripTrailingZeros().toPlainString());
            }
        }
        teachingPlanObjectiveRefMapper.deleteByPlanId(saveVo.getPlanId());
        teachingPlanObjectiveMapper.deleteByPlanId(saveVo.getPlanId());
        if (ObjectUtils.isEmpty(rows)) {
            return;
        }
        int sort = 1;
        for (TeachingPlanObjectiveSaveVo row : rows) {
            if (row == null || row.getObjective() == null) {
                continue;
            }
            TeachingPlanObjective objective = row.getObjective();
            objective.setId(null);
            objective.setPlanId(saveVo.getPlanId());
            if (objective.getSchemeId() == null) {
                objective.setSchemeId(saveVo.getSchemeId());
            }
            boolean publicFoundation = forceNullSchemeIfPublicFoundation(objective);
            if (publicFoundation) {
                objective.setSchemeId(null);
            }
            validateObjectiveForInsert(objective, publicFoundation);
            if (objective.getSort() == null) {
                objective.setSort(sort);
            }
            sort++;
            UserUtils.reflash(objective);
            teachingPlanObjectiveMapper.insert(objective);
            insertObjectiveRefs(objective.getId(), objective.getPlanId(), objective.getSchemeId(), row.getRefs());
        }
    }

    /**
     * 新增目标必填校验：planId/content 必填；
     * 非公共基础需指定 schemeId（scheme_id 为空的目标在总览树/列表中不可见）。
     */
    private void validateObjectiveForInsert(TeachingPlanObjective objective, boolean publicFoundation) {
        if (objective == null) {
            throw new IllegalArgumentException("教学目标不能为空");
        }
        if (objective.getPlanId() == null) {
            throw new IllegalArgumentException("planId 不能为空");
        }
        if (StringUtils.isBlank(objective.getContent())) {
            throw new IllegalArgumentException("目标内容不能为空");
        }
        if (!publicFoundation && objective.getSchemeId() == null) {
            throw new IllegalArgumentException("非公共基础课程新增目标必须指定 schemeId");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateObjective(TeachingPlanObjective objective) {
        if (objective == null || objective.getId() == null) {
            throw new IllegalArgumentException("教学目标id不能为空");
        }
        boolean publicFoundation = forceNullSchemeIfPublicFoundation(objective);
        UserUtils.reflash(objective);
        teachingPlanObjectiveMapper.updateById(objective);
        // update 动态 SQL 不会把 null 写进 scheme_id，公共基础需显式清空
        if (publicFoundation && objective.getId() != null) {
            teachingPlanObjectiveMapper.clearSchemeIdById(objective.getId());
        }
    }

    /**
     * 公共基础：目标强制 scheme_id = null（不区分培养方案）。
     * @return 是否公共基础（调用方用于决定是否 clearSchemeId）
     */
    private boolean forceNullSchemeIfPublicFoundation(TeachingPlanObjective objective) {
        if (objective == null) {
            return false;
        }
        Long planId = objective.getPlanId();
        if (planId == null && objective.getId() != null) {
            TeachingPlanObjective existing = teachingPlanObjectiveMapper.selectById(objective.getId());
            if (existing != null) {
                planId = existing.getPlanId();
            }
        }
        if (isPublicFoundationPlan(planId)) {
            objective.setSchemeId(null);
            return true;
        }
        return false;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteObjective(Long id) {
        // 删目标时同步逻辑删除其支撑毕业要求，避免孤儿绑定
        if (id != null) {
            teachingPlanObjectiveRefMapper.deleteByObjectiveId(id);
        }
        teachingPlanObjectiveMapper.deleteById(id);
    }

    // ============ 8. 课程绑定毕业要求（候选列表） ============

    @Override
    public List<StandardGraduation> listCourseGraduation(Long courseId) {
        // 公共基础：始终只用源课公共毕业要求
        if (isPublicFoundationCourse(courseId)) {
            return listGraduationsByCourseId(courseId);
        }
        // 全部调用课绑定；若调用课均无绑定则回退源课公共毕业要求
        List<StandardGraduation> fromQuotes = listGraduationsByQuoteCourseIds(resolveQuoteCourseIds(courseId));
        if (ObjectUtils.isNotEmpty(fromQuotes)) {
            return fromQuotes;
        }
        return listGraduationsByCourseId(courseId);
    }

    @Override
    public List<StandardGraduation> listCourseGraduationByScheme(Long courseId, Long schemeId) {
        if (courseId == null) {
            return new ArrayList<>();
        }
        // 公共基础：忽略 schemeId，只取源课 t_csys_course_ref_graduation
        if (isPublicFoundationCourse(courseId)) {
            return listGraduationsByCourseId(courseId);
        }
        // 1) 查该培养方案下被调用课（经 t_csys_training_scheme_ref_course）
        List<Long> quoteIds;
        if (schemeId != null) {
            quoteIds = trainingSchemeRefCourseMapper.selectQuoteCourseIdsBySourceAndScheme(courseId, schemeId);
        } else {
            quoteIds = resolveQuoteCourseIds(courseId);
        }
        // 2) 被调用课在 t_csys_course_ref_graduation 的绑定
        List<StandardGraduation> fromQuotes = listGraduationsByQuoteCourseIds(quoteIds);
        if (ObjectUtils.isNotEmpty(fromQuotes)) {
            return fromQuotes;
        }
        // 3) 被调用课均未绑定 → 回退源课自身绑定的公共毕业要求
        return listGraduationsByCourseId(courseId);
    }

    /** 源课全部调用课 id（c2.source_id = 源课） */
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
        return listGraduationsByCourseIds(quoteCourseIds);
    }

    /** 单课（含源课）在 t_csys_course_ref_graduation 上的毕业要求 */
    private List<StandardGraduation> listGraduationsByCourseId(Long courseId) {
        if (courseId == null) {
            return new ArrayList<>();
        }
        return listGraduationsByCourseIds(Collections.singletonList(courseId));
    }

    private List<StandardGraduation> listGraduationsByCourseIds(List<Long> courseIds) {
        if (ObjectUtils.isEmpty(courseIds)) {
            return new ArrayList<>();
        }
        List<CourseRefGraduation> refs =
                courseRefGraduationMapper.selectCourseTargetRefGraduationByCourseIds(courseIds);
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
        List<StandardGraduation> list = standardGraduationMapper.selectStandardGraduationByIds(graduationIds);
        if (list == null) {
            return new ArrayList<>();
        }
        // 前端绑定弹框需要树：补全祖先后挂 children
        return buildGraduationTree(list);
    }

    /**
     * 将扁平毕业要求补全父链后构建树（children）。
     * 若父节点不在当前绑定集合中，向上查询补齐，避免 buildRootTree 根为空。
     */
    private List<StandardGraduation> buildGraduationTree(List<StandardGraduation> flat) {
        if (ObjectUtils.isEmpty(flat)) {
            return new ArrayList<>();
        }
        Map<Long, StandardGraduation> byId = new HashMap<>();
        for (StandardGraduation g : flat) {
            if (g == null || g.getId() == null) {
                continue;
            }
            // 清空旧 children，避免脏数据
            g.setChildren(new ArrayList<>());
            byId.put(g.getId(), g);
        }
        // 补全祖先
        Set<Long> missingParents = new HashSet<>();
        for (StandardGraduation g : byId.values()) {
            Long pid = g.getParentId();
            if (pid != null && pid != -1L && !byId.containsKey(pid)) {
                missingParents.add(pid);
            }
        }
        int guard = 0;
        while (!missingParents.isEmpty() && guard++ < 20) {
            List<Long> batch = new ArrayList<>(missingParents);
            missingParents.clear();
            List<StandardGraduation> parents = standardGraduationMapper.selectStandardGraduationByIds(batch);
            if (ObjectUtils.isEmpty(parents)) {
                break;
            }
            for (StandardGraduation p : parents) {
                if (p == null || p.getId() == null || byId.containsKey(p.getId())) {
                    continue;
                }
                p.setChildren(new ArrayList<>());
                byId.put(p.getId(), p);
                Long pid = p.getParentId();
                if (pid != null && pid != -1L && !byId.containsKey(pid)) {
                    missingParents.add(pid);
                }
            }
        }
        List<StandardGraduation> all = new ArrayList<>(byId.values());
        List<StandardGraduation> tree = TreeBuilderUtils.buildRootTree(all);
        if (ObjectUtils.isEmpty(tree)) {
            // 兜底：父不在集合且非 -1 时，把「父不在 map 中」的节点当根
            List<StandardGraduation> roots = new ArrayList<>();
            Map<Long, StandardGraduation> map = new HashMap<>();
            for (StandardGraduation g : all) {
                map.put(g.getId(), g);
                if (g.getChildren() == null) {
                    g.setChildren(new ArrayList<>());
                }
            }
            for (StandardGraduation g : all) {
                Long pid = g.getParentId();
                if (pid == null || pid == -1L || !map.containsKey(pid)) {
                    roots.add(g);
                } else {
                    StandardGraduation parent = map.get(pid);
                    if (parent.getChildren() == null) {
                        parent.setChildren(new ArrayList<>());
                    }
                    parent.getChildren().add(g);
                }
            }
            return roots;
        }
        return tree;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long saveObjectiveWithRefs(TeachingPlanObjectiveSaveVo saveVo) {
        if (saveVo == null || saveVo.getObjective() == null) {
            throw new IllegalArgumentException("教学目标不能为空");
        }
        TeachingPlanObjective objective = saveVo.getObjective();
        boolean publicFoundation = forceNullSchemeIfPublicFoundation(objective);
        UserUtils.reflash(objective);
        if (objective.getId() == null) {
            validateObjectiveForInsert(objective, publicFoundation);
            teachingPlanObjectiveMapper.insert(objective);
        } else {
            teachingPlanObjectiveMapper.updateById(objective);
            if (publicFoundation) {
                teachingPlanObjectiveMapper.clearSchemeIdById(objective.getId());
            }
            teachingPlanObjectiveRefMapper.deleteByObjectiveId(objective.getId());
        }
        // 更新时入参可能不带 planId/schemeId，从库中回读，保证 ref 归属正确
        Long planId = objective.getPlanId();
        Long schemeId = objective.getSchemeId();
        if (planId == null || (!publicFoundation && schemeId == null)) {
            TeachingPlanObjective db = teachingPlanObjectiveMapper.selectById(objective.getId());
            if (db != null) {
                if (planId == null) {
                    planId = db.getPlanId();
                }
                if (schemeId == null) {
                    schemeId = db.getSchemeId();
                }
            }
        }
        insertObjectiveRefs(objective.getId(), planId, schemeId, saveVo.getRefs());
        return objective.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveObjectiveRefs(TeachingPlanObjectiveRefSaveVo saveVo) {
        if (saveVo == null || saveVo.getObjectiveId() == null) {
            throw new IllegalArgumentException("objectiveId 不能为空");
        }
        Long objectiveId = saveVo.getObjectiveId();
        // 校验目标存在；绑定归属(planId/schemeId)以目标库中记录为准，不信任前端传值
        TeachingPlanObjective objective = teachingPlanObjectiveMapper.selectById(objectiveId);
        if (objective == null) {
            throw new IllegalArgumentException("教学目标不存在: " + objectiveId);
        }
        Long planId = objective.getPlanId() != null ? objective.getPlanId() : saveVo.getPlanId();
        Long schemeId = objective.getSchemeId() != null ? objective.getSchemeId() : saveVo.getSchemeId();
        // 公共基础：绑定也强制 scheme_id 为空
        if (isPublicFoundationPlan(planId)) {
            schemeId = null;
        }

        // 重建绑定：先逻辑删除旧 ref，再按列表插入（空列表=清空）
        teachingPlanObjectiveRefMapper.deleteByObjectiveId(objectiveId);
        insertObjectiveRefs(objectiveId, planId, schemeId, saveVo.getRefs());
    }

    /**
     * 批量写入目标-毕业要求绑定；refs 可空表示不写。
     * planId/schemeId 强制取目标归属（不信任前端 ref 上的值，避免与目标不一致导致树查询丢失）；
     * graduationId 必填且必须真实存在，编码/名称快照缺省时从毕业要求回填；
     * 同一目标重复绑定同一毕业要求自动去重。公共基础：schemeId 强制 null。
     */
    private void insertObjectiveRefs(Long objectiveId, Long planId, Long schemeId,
                                     List<TeachingPlanObjectiveRef> refs) {
        if (ObjectUtils.isEmpty(refs)) {
            return;
        }
        boolean publicFoundation = isPublicFoundationPlan(planId);
        if (publicFoundation) {
            schemeId = null;
        }
        // 校验毕业要求存在
        List<Long> graduationIds = new ArrayList<>();
        for (TeachingPlanObjectiveRef ref : refs) {
            if (ref == null) {
                continue;
            }
            if (ref.getGraduationId() == null) {
                throw new IllegalArgumentException("毕业要求id(graduationId)不能为空");
            }
            if (!graduationIds.contains(ref.getGraduationId())) {
                graduationIds.add(ref.getGraduationId());
            }
        }
        Map<Long, StandardGraduation> gradMap = loadGraduationMap(graduationIds);
        Set<Long> bound = new HashSet<>();
        int sort = 1;
        for (TeachingPlanObjectiveRef ref : refs) {
            if (ref == null) {
                continue;
            }
            StandardGraduation g = gradMap.get(ref.getGraduationId());
            if (g == null) {
                throw new IllegalArgumentException("毕业要求不存在: " + ref.getGraduationId());
            }
            if (!bound.add(ref.getGraduationId())) {
                continue;
            }
            ref.setId(null);
            ref.setObjectiveId(objectiveId);
            ref.setPlanId(planId);
            ref.setSchemeId(schemeId);
            if (ref.getSourceGraduationId() == null) {
                ref.setSourceGraduationId(g.getSourceId());
            }
            if (StringUtils.isBlank(ref.getGraduationCode())) {
                ref.setGraduationCode(g.getCode());
            }
            if (StringUtils.isBlank(ref.getGraduationName())) {
                ref.setGraduationName(g.getName());
            }
            if (ref.getSort() == null) {
                ref.setSort(sort);
            }
            sort++;
            // 默认绑定来源标记：课程毕业要求关联
            if (StringUtils.isBlank(ref.getGraduationBindSource())) {
                ref.setGraduationBindSource("course_ref_graduation");
            }
            UserUtils.reflash(ref);
            teachingPlanObjectiveRefMapper.insert(ref);
        }
    }

    private Map<Long, StandardGraduation> loadGraduationMap(List<Long> graduationIds) {
        if (ObjectUtils.isEmpty(graduationIds)) {
            return Collections.emptyMap();
        }
        List<StandardGraduation> list = standardGraduationMapper.selectStandardGraduationByIds(graduationIds);
        if (ObjectUtils.isEmpty(list)) {
            return Collections.emptyMap();
        }
        return list.stream()
                .filter(g -> g != null && g.getId() != null)
                .collect(Collectors.toMap(StandardGraduation::getId, g -> g, (a, b) -> a));
    }

    // ============ 9. 教学计划目标支撑毕业要求 ============

    @Override
    public List<TeachingPlanObjectiveRef> listObjectiveRef(Long objectiveId) {
        return teachingPlanObjectiveRefMapper.selectByObjectiveId(objectiveId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long addObjectiveRef(TeachingPlanObjectiveRef ref) {
        if (ref == null || ref.getObjectiveId() == null) {
            throw new IllegalArgumentException("objectiveId 不能为空");
        }
        TeachingPlanObjective objective = teachingPlanObjectiveMapper.selectById(ref.getObjectiveId());
        if (objective == null) {
            throw new IllegalArgumentException("教学目标不存在: " + ref.getObjectiveId());
        }
        // 重复绑定拒绝；sort 缺省接在已有绑定之后
        List<TeachingPlanObjectiveRef> existing =
                teachingPlanObjectiveRefMapper.selectByObjectiveId(ref.getObjectiveId());
        if (ref.getGraduationId() != null && ObjectUtils.isNotEmpty(existing)) {
            for (TeachingPlanObjectiveRef e : existing) {
                if (e != null && ref.getGraduationId().equals(e.getGraduationId())) {
                    throw new IllegalArgumentException("该目标已绑定该毕业要求");
                }
            }
        }
        if (ref.getSort() == null) {
            ref.setSort((existing == null ? 0 : existing.size()) + 1);
        }
        // planId/schemeId/存在性校验统一走 insertObjectiveRefs（归属取目标）
        insertObjectiveRefs(objective.getId(), objective.getPlanId(), objective.getSchemeId(),
                Collections.singletonList(ref));
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

    @Override
    public List<TeachingPlanObjectiveAssessment> listObjectiveAssessment(Long planId, Long schemeId) {
        if (planId == null) {
            return new ArrayList<>();
        }
        return teachingPlanObjectiveAssessmentMapper.selectByPlanAndScheme(planId, schemeId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveObjectiveAssessmentBatch(TeachingPlanObjectiveAssessmentSaveVo saveVo) {
        if (saveVo == null || saveVo.getPlanId() == null) {
            throw new IllegalArgumentException("planId 不能为空");
        }
        teachingPlanObjectiveAssessmentMapper.deleteByPlanAndScheme(saveVo.getPlanId(), saveVo.getSchemeId());
        if (ObjectUtils.isEmpty(saveVo.getItems())) {
            return;
        }
        List<TeachingPlanObjectiveAssessment> items = new ArrayList<>();
        for (TeachingPlanObjectiveAssessment item : saveVo.getItems()) {
            if (item == null || item.getObjectiveId() == null) {
                throw new IllegalArgumentException("objectiveId 不能为空");
            }
            item.setId(null);
            item.setPlanId(saveVo.getPlanId());
            item.setSchemeId(saveVo.getSchemeId());
            UserUtils.reflash(item);
            items.add(item);
        }
        teachingPlanObjectiveAssessmentMapper.insertBatch(items);
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
    public List<TeachingPlanTargetDesign> listTargetDesign(Long planId, Long schemeId, String designTypeCode) {
        // 用户可能未建计划直接进入达成设计 tab：planId 为空时返回空列表，避免 SQL 报错
        if (planId == null) {
            return new ArrayList<>();
        }
        List<TeachingPlanTargetDesign> list =
                teachingPlanTargetDesignMapper.selectByPlanSchemeAndType(planId, schemeId, designTypeCode);
        if (ObjectUtils.isEmpty(list)) {
            return new ArrayList<>();
        }
        for (TeachingPlanTargetDesign d : list) {
            fillKnowledgePointsFromJson(d);
        }
        return list;
    }

    @Override
    public List<CourseKnowledgeUnit> listKnowledgeUnitInit(Long courseId) {
        List<CourseKnowledgeUnit> units = teachingPlanTargetDesignMapper.selectKnowledgeUnitInitByCourseId(courseId);
        return units == null ? new ArrayList<>() : units;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long addTargetDesign(TeachingPlanTargetDesign design) {
        prepareTargetDesignForWrite(design);
        UserUtils.reflash(design);
        teachingPlanTargetDesignMapper.insert(design);
        return design.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateTargetDesign(TeachingPlanTargetDesign design) {
        prepareTargetDesignForWrite(design);
        UserUtils.reflash(design);
        teachingPlanTargetDesignMapper.updateById(design);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteTargetDesign(Long id) {
        teachingPlanTargetDesignMapper.deleteById(id);
    }

    /**
     * 教学计划下某类型目标选项：按 content 去重，同名只保留一条。
     * planId 为空返回空列表。
     * 公共基础：只取 scheme_id IS NULL 单组。
     */
    @Override
    public List<TeachingPlanObjectiveOptionVo> listObjectiveOptions(Long planId, Long schemeId,
                                                                    String objectiveTypeCode) {
        if (planId == null) {
            return new ArrayList<>();
        }
        boolean onlyNull = isPublicFoundationPlan(planId);
        List<TeachingPlanObjective> list =
                teachingPlanObjectiveMapper.selectByPlanAndSchemeAndType(
                        planId, onlyNull ? null : schemeId, objectiveTypeCode, onlyNull);
        if (ObjectUtils.isEmpty(list)) {
            return new ArrayList<>();
        }
        // 按 content 字符串去重（trim 后比较），保留首次出现的类型信息
        Map<String, TeachingPlanObjectiveOptionVo> dedup = new LinkedHashMap<>();
        for (TeachingPlanObjective o : list) {
            if (o == null || StringUtils.isBlank(o.getContent())) {
                continue;
            }
            String key = o.getContent().trim();
            if (dedup.containsKey(key)) {
                continue;
            }
            TeachingPlanObjectiveOptionVo vo = new TeachingPlanObjectiveOptionVo();
            vo.setContent(key);
            vo.setObjectiveTypeCode(o.getObjectiveTypeCode());
            vo.setObjectiveTypeName(o.getObjectiveTypeName());
            dedup.put(key, vo);
        }
        return new ArrayList<>(dedup.values());
    }

    /**
     * 写库前：knowledgePoints ↔ knowledgePointsJson；
     * 有多知识点时回填首项到 unit/point 兼容列；
     * 仅单点兼容字段时也可组装成 knowledgePoints。
     */
    private void prepareTargetDesignForWrite(TeachingPlanTargetDesign design) {
        if (design == null) {
            return;
        }
        List<TeachingPlanTargetDesign.KnowledgePointItem> points = design.getKnowledgePoints();
        // 前端只传了旧单字段时，组装成列表
        if (ObjectUtils.isEmpty(points)
                && (design.getKnowledgePointId() != null || StringUtils.isNotBlank(design.getKnowledgePointName()))) {
            TeachingPlanTargetDesign.KnowledgePointItem item = new TeachingPlanTargetDesign.KnowledgePointItem();
            item.setKnowledgeUnitId(design.getKnowledgeUnitId());
            item.setKnowledgeUnitName(design.getKnowledgeUnitName());
            item.setKnowledgePointId(design.getKnowledgePointId());
            item.setKnowledgePointName(design.getKnowledgePointName());
            points = Collections.singletonList(item);
            design.setKnowledgePoints(points);
        }
        if (ObjectUtils.isNotEmpty(points)) {
            design.setKnowledgePointsJson(toKnowledgePointsJson(points));
            // 首项回填兼容列，便于旧查询/Word 单字段展示
            TeachingPlanTargetDesign.KnowledgePointItem first = points.get(0);
            if (first != null) {
                if (design.getKnowledgeUnitId() == null) {
                    design.setKnowledgeUnitId(first.getKnowledgeUnitId());
                }
                if (StringUtils.isBlank(design.getKnowledgeUnitName())) {
                    design.setKnowledgeUnitName(first.getKnowledgeUnitName());
                }
                if (design.getKnowledgePointId() == null) {
                    design.setKnowledgePointId(first.getKnowledgePointId());
                }
                if (StringUtils.isBlank(design.getKnowledgePointName())) {
                    // 多知识点时名称列用顿号拼接，方便列表一眼看到
                    design.setKnowledgePointName(joinKnowledgePointNames(points));
                }
            }
        } else if (design.getKnowledgePointsJson() == null && design.getKnowledgePoints() != null) {
            // 显式传空列表：清空 JSON
            design.setKnowledgePointsJson("[]");
        }
    }

    private void fillKnowledgePointsFromJson(TeachingPlanTargetDesign design) {
        if (design == null) {
            return;
        }
        List<TeachingPlanTargetDesign.KnowledgePointItem> points =
                parseKnowledgePointsJson(design.getKnowledgePointsJson());
        if (ObjectUtils.isEmpty(points)
                && (design.getKnowledgePointId() != null || StringUtils.isNotBlank(design.getKnowledgePointName()))) {
            TeachingPlanTargetDesign.KnowledgePointItem item = new TeachingPlanTargetDesign.KnowledgePointItem();
            item.setKnowledgeUnitId(design.getKnowledgeUnitId());
            item.setKnowledgeUnitName(design.getKnowledgeUnitName());
            item.setKnowledgePointId(design.getKnowledgePointId());
            item.setKnowledgePointName(design.getKnowledgePointName());
            points = Collections.singletonList(item);
        }
        design.setKnowledgePoints(points == null ? new ArrayList<>() : points);
    }

    private static String toKnowledgePointsJson(List<TeachingPlanTargetDesign.KnowledgePointItem> points) {
        if (points == null) {
            return null;
        }
        try {
            return OBJECT_MAPPER.writeValueAsString(points);
        } catch (Exception e) {
            throw new IllegalArgumentException("knowledgePoints 序列化失败: " + e.getMessage(), e);
        }
    }

    private static List<TeachingPlanTargetDesign.KnowledgePointItem> parseKnowledgePointsJson(String json) {
        if (StringUtils.isBlank(json)) {
            return new ArrayList<>();
        }
        try {
            List<TeachingPlanTargetDesign.KnowledgePointItem> list = OBJECT_MAPPER.readValue(json, KP_LIST_TYPE);
            return list == null ? new ArrayList<>() : list;
        } catch (Exception e) {
            // 脏数据不阻断列表：返回空
            return new ArrayList<>();
        }
    }

    private static String joinKnowledgePointNames(List<TeachingPlanTargetDesign.KnowledgePointItem> points) {
        if (ObjectUtils.isEmpty(points)) {
            return null;
        }
        return points.stream()
                .map(TeachingPlanTargetDesign.KnowledgePointItem::getKnowledgePointName)
                .filter(StringUtils::isNotBlank)
                .collect(Collectors.joining("、"));
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
        List<TeachingPlanAssessment> list = teachingPlanAssessmentMapper.selectByPlanId(planId);
        // 列表回传主表计分规则（非 assessment 表字段，便于考核页回显）
        if (planId != null && ObjectUtils.isNotEmpty(list)) {
            TeachingPlan plan = teachingPlanMapper.selectById(planId);
            if (plan != null && plan.getScoreRule() != null) {
                for (TeachingPlanAssessment a : list) {
                    a.setScoreRule(plan.getScoreRule());
                }
            }
        }
        return list;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long addAssessment(TeachingPlanAssessment assessment) {
        UserUtils.reflash(assessment);
        teachingPlanAssessmentMapper.insert(assessment);
        // scoreRule 为非本表透传字段：有值时回写主表 t_csys_teaching_plan.score_rule
        writeBackScoreRule(assessment.getPlanId(), assessment.getScoreRule());
        return assessment.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateAssessment(TeachingPlanAssessment assessment) {
        UserUtils.reflash(assessment);
        teachingPlanAssessmentMapper.updateById(assessment);
        // scoreRule 为非本表透传字段：有值时回写主表；planId 未传时无法定位主表则跳过
        writeBackScoreRule(assessment.getPlanId(), assessment.getScoreRule());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteAssessment(Long id) {
        teachingPlanAssessmentMapper.deleteById(id);
    }

    /**
     * 将考核评价请求中的计分规则回写主表。
     * scoreRule == null 表示本次不改主表计分规则；空串允许清空。
     */
    private void writeBackScoreRule(Long planId, String scoreRule) {
        if (planId == null || scoreRule == null) {
            return;
        }
        TeachingPlan plan = new TeachingPlan();
        plan.setId(planId);
        plan.setScoreRule(scoreRule);
        UserUtils.reflash(plan);
        teachingPlanMapper.updateById(plan);
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
    @Transactional(rollbackFor = Exception.class)
    public List<TeachingPlanCondition> listCondition(Long planId) {
        if (planId == null) {
            throw new IllegalArgumentException("planId 不能为空");
        }
        // 含已删除(sysflag=2)在内都没有数据时，按字典初始化 9 条条件类型
        int total = teachingPlanConditionMapper.countAllByPlanId(planId);
        if (total <= 0) {
            initDefaultConditions(planId);
        }
        // 列表只返回有效数据(sysflag=0)
        return teachingPlanConditionMapper.selectByPlanId(planId);
    }

    /**
     * 按字典 sys_condition_type 初始化条件保障。
     * 字典有几条就初始化几条；若字典缺失则按常见 9 类兜底。
     */
    private void initDefaultConditions(Long planId) {
        List<SysDictData> dictList = CurDictUtils.getDictData(DICT_CONDITION_TYPE);
        List<TeachingPlanCondition> rows = new ArrayList<>();
        if (ObjectUtils.isNotEmpty(dictList)) {
            int index = 1;
            for (SysDictData dict : dictList) {
                if (dict == null || StringUtils.isBlank(dict.getDictValue())) {
                    continue;
                }
                TeachingPlanCondition row = new TeachingPlanCondition();
                row.setPlanId(planId);
                row.setConditionType(dict.getDictValue());
                row.setRequirement("");
                Integer sort = dict.getDictSort() == null ? index : dict.getDictSort().intValue();
                row.setSort(sort);
                UserUtils.reflash(row);
                row.setSysflag(0);
                rows.add(row);
                index++;
            }
        }
        // 字典异常或为空时，按 9 条占位初始化，保证页面可展示
        if (rows.isEmpty()) {
            for (int i = 1; i <= 9; i++) {
                TeachingPlanCondition row = new TeachingPlanCondition();
                row.setPlanId(planId);
                row.setConditionType(String.valueOf(i));
                row.setRequirement("");
                row.setSort(i);
                UserUtils.reflash(row);
                row.setSysflag(0);
                rows.add(row);
            }
        }
        if (rows.size() == 1) {
            teachingPlanConditionMapper.insert(rows.get(0));
        } else {
            teachingPlanConditionMapper.insertBatch(rows);
        }
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

    /**
     * 条件保障大保存（整表重建）。
     * 先按 planId 逻辑删除该教学计划下全部旧条件，再按 conditions 批量写入；
     * conditions 空/null = 清空。planId 以入参顶层为准覆盖每行，sort 缺省按序回填。
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveConditions(TeachingPlanConditionSaveVo saveVo) {
        if (saveVo == null || saveVo.getPlanId() == null) {
            throw new IllegalArgumentException("planId 不能为空");
        }
        Long planId = saveVo.getPlanId();
        // 过滤空行
        List<TeachingPlanCondition> rows = new ArrayList<>();
        if (ObjectUtils.isNotEmpty(saveVo.getConditions())) {
            for (TeachingPlanCondition c : saveVo.getConditions()) {
                if (c != null) {
                    rows.add(c);
                }
            }
        }
        // 整表重建：先逻辑删除旧记录
        teachingPlanConditionMapper.deleteByPlanId(planId);
        if (rows.isEmpty()) {
            return;
        }
        // planId 以顶层为准；补 sort/sysflag/审计字段
        int index = 0;
        for (TeachingPlanCondition c : rows) {
            c.setId(null);
            c.setPlanId(planId);
            if (c.getSort() == null) {
                c.setSort(++index);
            } else {
                index = c.getSort();
            }
            UserUtils.reflash(c);
            c.setSysflag(0);
        }
        if (rows.size() == 1) {
            teachingPlanConditionMapper.insert(rows.get(0));
        } else {
            teachingPlanConditionMapper.insertBatch(rows);
        }
    }

    // ============ 18b. 实践项目组织与实施大保存(type4) ============

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveOrganization(TeachingPlanOrganizationSaveVo saveVo) {
        if (saveVo == null || saveVo.getPlanId() == null) {
            throw new IllegalArgumentException("planId 不能为空");
        }
        Long planId = saveVo.getPlanId();
        // 团队规模 / 分工方式：复用 section，按 planId+sectionTitle upsert。null 不改，空串清空。
        upsertSection(planId, "团队规模", "team_scale", saveVo.getTeamScale());
        upsertSection(planId, "分工方式", "division", saveVo.getDivision());

        // 项目步骤：整表重建（先逻辑删除旧步骤，再批量写入）
        List<TeachingPlanProcessStep> steps = new ArrayList<>();
        if (ObjectUtils.isNotEmpty(saveVo.getSteps())) {
            for (TeachingPlanProcessStep s : saveVo.getSteps()) {
                if (s != null) {
                    steps.add(s);
                }
            }
        }
        teachingPlanProcessStepMapper.deleteByPlanId(planId);
        if (steps.isEmpty()) {
            return;
        }
        int index = 0;
        for (TeachingPlanProcessStep s : steps) {
            s.setId(null);
            s.setPlanId(planId);
            if (s.getSort() == null) {
                s.setSort(++index);
            } else {
                index = s.getSort();
            }
            UserUtils.reflash(s);
            s.setSysflag(0);
        }
        if (steps.size() == 1) {
            teachingPlanProcessStepMapper.insert(steps.get(0));
        } else {
            teachingPlanProcessStepMapper.insertBatch(steps);
        }
    }

    /**
     * 按 planId + sectionTitle upsert 一条 section。
     * value 为 null：不改原值；为空串：清空 content（保留行）；非空：写入/更新 content。
     */
    private void upsertSection(Long planId, String sectionTitle, String sectionCode, String value) {
        if (value == null) {
            return;
        }
        TeachingPlanSection exist = teachingPlanSectionMapper.selectByPlanIdAndTitle(planId, sectionTitle);
        if (exist != null) {
            exist.setContent(value);
            UserUtils.reflash(exist);
            teachingPlanSectionMapper.updateById(exist);
            return;
        }
        TeachingPlanSection s = new TeachingPlanSection();
        s.setPlanId(planId);
        s.setSectionTitle(sectionTitle);
        s.setSectionCode(sectionCode);
        s.setContent(value);
        s.setSort(0);
        UserUtils.reflash(s);
        s.setSysflag(0);
        teachingPlanSectionMapper.insert(s);
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
