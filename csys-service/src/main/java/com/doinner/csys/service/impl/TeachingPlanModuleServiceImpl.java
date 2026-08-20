package com.doinner.csys.service.impl;

import com.doinner.csys.dao.CourseMapper;
import com.doinner.csys.dao.TrainingSchemeCourseScheduleMapper;
import com.doinner.csys.dao.TrainingSchemeRefCourseMapper;
import com.doinner.csys.dao.CourseRefGraduationMapper;
import com.doinner.csys.dao.StandardGraduationMapper;
import com.doinner.csys.dao.TeachingPlanAssessmentMapper;
import com.doinner.csys.dao.TeachingPlanConditionMapper;
import com.doinner.csys.dao.TeachingPlanContentMapper;
import com.doinner.csys.dao.TeachingPlanContextMapper;
import com.doinner.csys.dao.TeachingPlanMapper;
import com.doinner.csys.dao.TeachingPlanObjectiveMapper;
import com.doinner.csys.dao.TeachingPlanObjectiveRefMapper;
import com.doinner.csys.dao.TeachingPlanObjectiveAssessmentMapper;
import com.doinner.csys.dao.TeachingPlanTaskBackgroundMapper;
import com.doinner.csys.dao.TeachingPlanTaskBackgroundRefMapper;
import com.doinner.csys.dao.TeachingPlanTrainingPurposeMapper;
import com.doinner.csys.dao.TeachingPlanTrainingPurposeRefMapper;
import com.doinner.csys.dao.TeachingPlanContentPurposeMapper;
import com.doinner.csys.dao.TeachingPlanSupportObjectiveMapper;
import com.doinner.csys.dao.TeachingPlanSupportContentMapper;
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
import com.doinner.csys.domain.TeachingPlanContext;
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
import com.doinner.csys.domain.TeachingPlanTaskBackground;
import com.doinner.csys.domain.TeachingPlanTaskBackgroundRef;
import com.doinner.csys.domain.TeachingPlanTextbook;
import com.doinner.csys.domain.TeachingPlanTrainingPurpose;
import com.doinner.csys.domain.TeachingPlanTrainingPurposeRef;
import com.doinner.csys.domain.TeachingPlanContentPurpose;
import com.doinner.csys.domain.TeachingPlanSupportObjective;
import com.doinner.csys.domain.TeachingPlanSupportContent;
import com.doinner.csys.domain.vo.TeachingPlanConditionSaveVo;
import com.doinner.csys.domain.vo.TeachingPlanContentPurposeSaveVo;
import com.doinner.csys.domain.vo.CourseIdAndName;
import com.doinner.csys.domain.vo.TeachingPlanSupportCandidateItem;
import com.doinner.csys.domain.vo.TeachingPlanSupportCandidateVo;
import com.doinner.csys.domain.vo.TeachingPlanSupportCandidateGroupVo;
import com.doinner.csys.domain.vo.TeachingPlanSupportCandidateTreeNodeVo;
import com.doinner.csys.domain.vo.TeachingPlanSupportContentSaveVo;
import com.doinner.csys.domain.vo.TeachingPlanSupportObjectiveSaveVo;
import com.doinner.csys.domain.vo.TeachingPlanMajorVo;
import com.doinner.csys.domain.vo.TeachingPlanObjectiveOptionVo;
import com.doinner.csys.domain.vo.TeachingPlanObjectiveBatchSaveVo;
import com.doinner.csys.domain.vo.TeachingPlanObjectiveRefSaveVo;
import com.doinner.csys.domain.vo.TeachingPlanObjectiveSaveVo;
import com.doinner.csys.domain.vo.TeachingPlanObjectiveAssessmentSaveVo;
import com.doinner.csys.domain.vo.TeachingPlanObjectiveTreeVo;
import com.doinner.csys.domain.vo.TeachingPlanOrganizationSaveVo;
import com.doinner.csys.domain.vo.TeachingPlanPracticeProjectBackgroundSaveVo;
import com.doinner.csys.domain.vo.TeachingPlanPracticeProjectBackgroundVo;
import com.doinner.csys.domain.vo.TeachingPlanQuoteAggVo;
import com.doinner.csys.domain.vo.TeachingPlanSchemeVo;
import com.doinner.csys.domain.vo.TeachingPlanTaskBackgroundRefSaveVo;
import com.doinner.csys.domain.vo.TeachingPlanTrainingPurposeBatchSaveVo;
import com.doinner.csys.domain.vo.TeachingPlanTrainingPurposeRefSaveVo;
import com.doinner.csys.domain.vo.TeachingPlanTrainingPurposeSaveVo;
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
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
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

    /** 训练内容与时间安排模块字典 type（type2 content.title 存 value 编码） */
    private static final String DICT_PLAN_TRAINING_MODULE = "sys_plan_training_module";

    /** 实践项目成果类型字典，value 保存到 assessment.outcome_type。 */
    private static final String DICT_PLAN_OUTCOME_TYPE = "sys_plan_outcome_type";

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
    private TeachingPlanTaskBackgroundMapper teachingPlanTaskBackgroundMapper;

    @Resource
    private TeachingPlanTaskBackgroundRefMapper teachingPlanTaskBackgroundRefMapper;

    @Resource
    private TeachingPlanTrainingPurposeMapper teachingPlanTrainingPurposeMapper;

    @Resource
    private TeachingPlanTrainingPurposeRefMapper teachingPlanTrainingPurposeRefMapper;

    @Resource
    private TeachingPlanContentPurposeMapper teachingPlanContentPurposeMapper;

    @Resource
    private TeachingPlanSupportObjectiveMapper teachingPlanSupportObjectiveMapper;

    @Resource
    private TeachingPlanSupportContentMapper teachingPlanSupportContentMapper;

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
    private TeachingPlanContextMapper teachingPlanContextMapper;

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

    /** 实践训练课目(type2)课目模块(location, 字典 sys_subject_module)命中下列值之一即属通识通用 */
    private static final Set<String> SUBJECT_MODULE_GENERAL_VALUES =
            new HashSet<>(Arrays.asList("1", "2", "3", "9"));

    /**
     * 普通课程(type1/3)源课是否属公共基础：聚合被引用课程的 course_Module 多值串，
     * 拆分后每一项均为公共基础（兼容 id(hex) 与已译名称「含公共基础」两种形态）。
     * 聚合为空时回退源课自身 course_Module。
     * 公共基础：目标/任务背景按 plan 单组（scheme_id 恒为 null），不按培养方案拆分。
     */
    @Override
    public boolean isPublicFoundationCourse(Long sourceCourseId) {
        if (sourceCourseId == null) {
            return false;
        }
        CourseVo course = courseMapper.selectCourseById(sourceCourseId);
        if (course == null) {
            return false;
        }
        TeachingPlanQuoteAggVo agg = teachingPlanMapper.selectQuoteAggBySourceCourseId(sourceCourseId);
        String module = agg == null ? null : agg.getCourseModule();
        if (StringUtils.isBlank(module)) {
            module = course.getCourseModule();
        }
        return isOnlyPublicFoundationCourseModule(module);
    }

    /**
     * 实践训练课目(type2)是否属通识通用：聚合被引用课程的 location 多值串，
     * 拆分后每一项均∈{1,2,3,9}（即「课目模块只属于1,2,3,9」）。聚合为空时回退源课自身 location。
     * 通识通用：训练目的按 plan 单组（scheme_id 恒为 null），不按培养方案拆分。
     */
    @Override
    public boolean isGeneralSubjectModuleCourse(Long sourceCourseId) {
        if (sourceCourseId == null) {
            return false;
        }
        CourseVo course = courseMapper.selectCourseById(sourceCourseId);
        if (course == null) {
            return false;
        }
        TeachingPlanQuoteAggVo agg = teachingPlanMapper.selectQuoteAggBySourceCourseId(sourceCourseId);
        String location = agg == null ? null : agg.getLocation();
        if (StringUtils.isBlank(location)) {
            location = course.getLocation();
        }
        return isOnlyGeneralSubjectModuleValue(location);
    }

    /** course_Module 是否只属于公共基础：非空且拆分(、/,，)后每一项均为公共基础。 */
    private boolean isOnlyPublicFoundationCourseModule(String courseModule) {
        if (StringUtils.isBlank(courseModule)) {
            return false;
        }
        String generalId = DictContent.GENERAL_EDUCATION_COURSES_SCHEDULE;
        boolean any = false;
        for (String part : courseModule.trim().split("[、,/，]")) {
            String p = part.trim();
            if (StringUtils.isBlank(p)) {
                continue;
            }
            any = true;
            if (!Objects.equals(p, generalId) && !p.contains("公共基础")) {
                return false;
            }
        }
        return any;
    }

    /** location 是否只属于通识通用课目模块：非空且拆分后每一项∈{1,2,3,9}。 */
    private boolean isOnlyGeneralSubjectModuleValue(String location) {
        if (StringUtils.isBlank(location)) {
            return false;
        }
        boolean any = false;
        for (String part : location.trim().split("[、,/，]")) {
            String p = part.trim();
            if (StringUtils.isBlank(p)) {
                continue;
            }
            any = true;
            if (!SUBJECT_MODULE_GENERAL_VALUES.contains(p)) {
                return false;
            }
        }
        return any;
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

    /** 教学计划对应源课（type2）是否属通识通用课目模块 */
    private boolean isGeneralSubjectPlan(Long planId) {
        if (planId == null) {
            return false;
        }
        TeachingPlan plan = teachingPlanMapper.selectById(planId);
        if (plan == null) {
            return false;
        }
        return isGeneralSubjectModuleCourse(plan.getSourceCourseId());
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
        boolean batchPublicFoundation = isPublicFoundationPlan(saveVo.getPlanId());
        Long batchSchemeId = batchPublicFoundation ? null : saveVo.getSchemeId();
        if (!batchPublicFoundation && batchSchemeId == null) {
            throw new IllegalArgumentException("非公共基础课程批量保存目标必须指定 schemeId");
        }
        validateObjectiveBatchScheme(rows, batchSchemeId, batchPublicFoundation);
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
        teachingPlanObjectiveRefMapper.deleteByPlanAndScheme(
                saveVo.getPlanId(), batchSchemeId, batchPublicFoundation);
        teachingPlanObjectiveMapper.deleteByPlanAndScheme(
                saveVo.getPlanId(), batchSchemeId, batchPublicFoundation);
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
                objective.setSchemeId(batchSchemeId);
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

    /** 计划关联的引用课程ID（objective_ref.quote_course_id 快照）：优先计划 context，缺失时回退源课调用课/源课自身。 */
    private Long resolvePlanQuoteCourseId(Long planId) {
        if (planId == null) {
            return null;
        }
        List<TeachingPlanContext> ctxs = teachingPlanContextMapper.selectByPlanId(planId);
        if (ObjectUtils.isNotEmpty(ctxs) && ctxs.get(0).getQuoteCourseId() != null) {
            return ctxs.get(0).getQuoteCourseId();
        }
        TeachingPlan plan = teachingPlanMapper.selectById(planId);
        if (plan != null && plan.getSourceCourseId() != null) {
            List<Long> quoteIds = resolveQuoteCourseIds(plan.getSourceCourseId());
            if (ObjectUtils.isNotEmpty(quoteIds)) {
                return quoteIds.get(0);
            }
            return plan.getSourceCourseId();
        }
        return null;
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
            // objective_ref.quote_course_id NOT NULL：绑定弹框数据不带该字段，按计划 context 调用课程自动补快照
            if (ref.getQuoteCourseId() == null) {
                ref.setQuoteCourseId(resolvePlanQuoteCourseId(planId));
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

    // ============ 任务背景（实验课程第三节，对标课程目标） ============

    @Override
    public List<TeachingPlanTaskBackground> listTaskBackground(Long planId, Long schemeId) {
        boolean onlyNull = isPublicFoundationPlan(planId);
        if (!onlyNull && schemeId == null) {
            throw new IllegalArgumentException("非公共基础课程查询任务背景必须指定 schemeId");
        }
        // 公共基础：忽略入参 schemeId，只取 scheme_id IS NULL 单组
        return teachingPlanTaskBackgroundMapper.selectByPlanAndScheme(
                planId, onlyNull ? null : schemeId, onlyNull);
    }

    /**
     * 新增任务背景必填校验：planId/backgroundDesc/technicalGoal/abilityGoal 必填；
     * 非公共基础需指定 schemeId。
     */
    private void validateTaskBackgroundForInsert(TeachingPlanTaskBackground taskBackground, boolean publicFoundation) {
        if (taskBackground == null) {
            throw new IllegalArgumentException("任务背景不能为空");
        }
        if (taskBackground.getPlanId() == null) {
            throw new IllegalArgumentException("planId 不能为空");
        }
        if (StringUtils.isBlank(taskBackground.getBackgroundDesc())) {
            throw new IllegalArgumentException("任务背景描述不能为空");
        }
        if (StringUtils.isBlank(taskBackground.getTechnicalGoal())) {
            throw new IllegalArgumentException("技术目标不能为空");
        }
        if (StringUtils.isBlank(taskBackground.getAbilityGoal())) {
            throw new IllegalArgumentException("能力目标不能为空");
        }
        if (!publicFoundation && taskBackground.getSchemeId() == null) {
            throw new IllegalArgumentException("非公共基础课程新增任务背景必须指定 schemeId");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long addTaskBackground(TeachingPlanTaskBackground taskBackground) {
        boolean publicFoundation = forceNullSchemeIfPublicFoundation(taskBackground);
        if (publicFoundation) {
            taskBackground.setSchemeId(null);
        }
        validateTaskBackgroundForInsert(taskBackground, publicFoundation);
        UserUtils.reflash(taskBackground);
        teachingPlanTaskBackgroundMapper.insert(taskBackground);
        return taskBackground.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateTaskBackground(TeachingPlanTaskBackground taskBackground) {
        if (taskBackground == null || taskBackground.getId() == null) {
            throw new IllegalArgumentException("任务背景id不能为空");
        }
        TeachingPlanTaskBackground existing = teachingPlanTaskBackgroundMapper.selectById(taskBackground.getId());
        if (existing == null) {
            throw new IllegalArgumentException("任务背景不存在: " + taskBackground.getId());
        }
        TeachingPlanTaskBackground merged = new TeachingPlanTaskBackground();
        merged.setPlanId(taskBackground.getPlanId() == null ? existing.getPlanId() : taskBackground.getPlanId());
        merged.setSchemeId(taskBackground.getSchemeId() == null ? existing.getSchemeId() : taskBackground.getSchemeId());
        merged.setBackgroundDesc(taskBackground.getBackgroundDesc() == null
                ? existing.getBackgroundDesc() : taskBackground.getBackgroundDesc());
        merged.setTechnicalGoal(taskBackground.getTechnicalGoal() == null
                ? existing.getTechnicalGoal() : taskBackground.getTechnicalGoal());
        merged.setAbilityGoal(taskBackground.getAbilityGoal() == null
                ? existing.getAbilityGoal() : taskBackground.getAbilityGoal());
        boolean publicFoundation = forceNullSchemeIfPublicFoundation(taskBackground);
        validateTaskBackgroundForInsert(merged, publicFoundation);
        UserUtils.reflash(taskBackground);
        teachingPlanTaskBackgroundMapper.updateById(taskBackground);
        // update 动态 SQL 不会把 null 写进 scheme_id，公共基础需显式清空
        if (publicFoundation && taskBackground.getId() != null) {
            teachingPlanTaskBackgroundMapper.clearSchemeIdById(taskBackground.getId());
        }
    }

    /**
     * 公共基础：任务背景强制 scheme_id = null（不区分培养方案）。
     * @return 是否公共基础（调用方用于决定是否 clearSchemeId）
     */
    private boolean forceNullSchemeIfPublicFoundation(TeachingPlanTaskBackground taskBackground) {
        if (taskBackground == null) {
            return false;
        }
        Long planId = taskBackground.getPlanId();
        if (planId == null && taskBackground.getId() != null) {
            TeachingPlanTaskBackground existing = teachingPlanTaskBackgroundMapper.selectById(taskBackground.getId());
            if (existing != null) {
                planId = existing.getPlanId();
            }
        }
        if (isPublicFoundationPlan(planId)) {
            taskBackground.setSchemeId(null);
            return true;
        }
        return false;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteTaskBackground(Long id) {
        // 删任务背景时同步逻辑删除其绑定的毕业要求，避免孤儿绑定
        if (id != null) {
            teachingPlanTaskBackgroundRefMapper.deleteByTaskBackgroundId(id);
        }
        teachingPlanTaskBackgroundMapper.deleteById(id);
    }

    @Override
    public List<TeachingPlanTaskBackgroundRef> listTaskBackgroundRef(Long taskBackgroundId) {
        return teachingPlanTaskBackgroundRefMapper.selectByTaskBackgroundId(taskBackgroundId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveTaskBackgroundRefs(TeachingPlanTaskBackgroundRefSaveVo saveVo) {
        if (saveVo == null || saveVo.getTaskBackgroundId() == null) {
            throw new IllegalArgumentException("taskBackgroundId 不能为空");
        }
        Long taskBackgroundId = saveVo.getTaskBackgroundId();
        // 校验任务背景存在；绑定归属(planId/schemeId)以库中记录为准，不信任前端传值
        TeachingPlanTaskBackground taskBackground = teachingPlanTaskBackgroundMapper.selectById(taskBackgroundId);
        if (taskBackground == null) {
            throw new IllegalArgumentException("任务背景不存在: " + taskBackgroundId);
        }
        Long planId = taskBackground.getPlanId();
        Long schemeId = taskBackground.getSchemeId();
        if (planId == null) {
            throw new IllegalArgumentException("planId 不能为空");
        }
        // 公共基础：绑定也强制 scheme_id 为空
        if (isPublicFoundationPlan(planId)) {
            schemeId = null;
        }
        // 重建绑定：先逻辑删除旧 ref，再按列表插入（空列表=清空）
        teachingPlanTaskBackgroundRefMapper.deleteByTaskBackgroundId(taskBackgroundId);
        insertTaskBackgroundRefs(taskBackgroundId, planId, schemeId, saveVo.getGraduationIds());
    }

    /**
     * 批量写入任务背景-毕业要求绑定；graduationIds 可空表示不写。
     * planId/schemeId 强制取任务背景归属；graduationId 必须真实存在；
     * 同一任务背景重复绑定同一毕业要求自动去重。公共基础：schemeId 强制 null。
     */
    private void insertTaskBackgroundRefs(Long taskBackgroundId, Long planId, Long schemeId,
                                          List<Long> graduationIds) {
        if (ObjectUtils.isEmpty(graduationIds)) {
            return;
        }
        boolean publicFoundation = isPublicFoundationPlan(planId);
        if (publicFoundation) {
            schemeId = null;
        }
        // 校验毕业要求存在
        List<Long> distinctGraduationIds = new ArrayList<>();
        for (Long graduationId : graduationIds) {
            if (graduationId == null) {
                throw new IllegalArgumentException("毕业要求id(graduationId)不能为空");
            }
            if (!distinctGraduationIds.contains(graduationId)) {
                distinctGraduationIds.add(graduationId);
            }
        }
        TeachingPlan plan = teachingPlanMapper.selectById(planId);
        if (plan == null || plan.getSourceCourseId() == null) {
            throw new IllegalArgumentException("任务背景所属教学计划不存在或未关联源课程");
        }
        Set<Long> candidateIds = new HashSet<>();
        collectGraduationIds(listCourseGraduationByScheme(plan.getSourceCourseId(), schemeId), candidateIds);
        for (Long graduationId : distinctGraduationIds) {
            if (!candidateIds.contains(graduationId)) {
                throw new IllegalArgumentException("毕业要求不属于当前培养方案的可绑定范围: " + graduationId);
            }
        }
        Map<Long, StandardGraduation> gradMap = loadGraduationMap(distinctGraduationIds);
        int sort = 1;
        for (Long graduationId : distinctGraduationIds) {
            StandardGraduation g = gradMap.get(graduationId);
            if (g == null) {
                throw new IllegalArgumentException("毕业要求不存在: " + graduationId);
            }
            TeachingPlanTaskBackgroundRef ref = new TeachingPlanTaskBackgroundRef();
            ref.setTaskBackgroundId(taskBackgroundId);
            ref.setPlanId(planId);
            ref.setSchemeId(schemeId);
            ref.setGraduationId(graduationId);
            ref.setSourceGraduationId(g.getSourceId());
            ref.setGraduationCode(g.getCode());
            ref.setGraduationName(g.getName());
            ref.setSort(sort++);
            ref.setGraduationBindSource("course_ref_graduation");
            ref.setQuoteCourseId(resolvePlanQuoteCourseId(planId));
            UserUtils.reflash(ref);
            teachingPlanTaskBackgroundRefMapper.insert(ref);
        }
    }

    private void collectGraduationIds(List<StandardGraduation> graduations, Set<Long> target) {
        if (ObjectUtils.isEmpty(graduations)) {
            return;
        }
        for (StandardGraduation graduation : graduations) {
            if (graduation == null) {
                continue;
            }
            if (graduation.getId() != null && target.add(graduation.getId())) {
                collectGraduationIds(graduation.getChildren(), target);
            }
        }
    }

    // ============ 训练目的（实践训练课目 type2 第二节，对标任务背景） ============

    @Override
    public List<TeachingPlanTrainingPurpose> listTrainingPurpose(Long planId, Long schemeId) {
        boolean onlyNull = isGeneralSubjectPlan(planId);
        // 通识通用：忽略入参 schemeId，只取 scheme_id IS NULL 单组
        List<TeachingPlanTrainingPurpose> purposes = teachingPlanTrainingPurposeMapper.selectByPlanAndScheme(
                planId, onlyNull ? null : schemeId, onlyNull);
        if (ObjectUtils.isEmpty(purposes)) {
            return purposes;
        }

        // 先按 plan + scheme 锁定本次返回的训练目的，再按 purposeId 归并该 plan 下绑定。
        // purposeId 是绑定的直接外键；这样既不会跨目的/方案串数据，也兼容通识通用历史绑定仍带 scheme_id 的数据。
        Set<Long> visiblePurposeIds = purposes.stream()
                .filter(Objects::nonNull)
                .map(TeachingPlanTrainingPurpose::getId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        List<TeachingPlanTrainingPurposeRef> refs =
                teachingPlanTrainingPurposeRefMapper.selectByPlanAndScheme(planId, null, false);
        Map<Long, LinkedHashSet<String>> namesByPurposeId = new LinkedHashMap<>();
        if (ObjectUtils.isNotEmpty(refs)) {
            List<Long> missingNameGraduationIds = refs.stream()
                    .filter(Objects::nonNull)
                    .filter(ref -> StringUtils.isBlank(ref.getGraduationName()))
                    .map(TeachingPlanTrainingPurposeRef::getGraduationId)
                    .filter(Objects::nonNull)
                    .distinct()
                    .collect(Collectors.toList());
            Map<Long, StandardGraduation> graduationMap = loadGraduationMap(missingNameGraduationIds);
            for (TeachingPlanTrainingPurposeRef ref : refs) {
                if (ref == null || ref.getPurposeId() == null || !visiblePurposeIds.contains(ref.getPurposeId())) {
                    continue;
                }
                String name = ref.getGraduationName();
                if (StringUtils.isBlank(name) && ref.getGraduationId() != null) {
                    StandardGraduation graduation = graduationMap.get(ref.getGraduationId());
                    name = graduation == null ? null : graduation.getName();
                }
                if (StringUtils.isNotBlank(name)) {
                    namesByPurposeId.computeIfAbsent(ref.getPurposeId(), key -> new LinkedHashSet<>())
                            .add(name.trim());
                }
            }
        }
        for (TeachingPlanTrainingPurpose purpose : purposes) {
            LinkedHashSet<String> names = purpose == null ? null : namesByPurposeId.get(purpose.getId());
            if (purpose != null) {
                purpose.setGraduationRequirements(
                        names == null || names.isEmpty() ? "" : String.join("、", names));
            }
        }
        return purposes;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveTrainingPurposesBatch(TeachingPlanTrainingPurposeBatchSaveVo saveVo) {
        if (saveVo == null || saveVo.getPlanId() == null) {
            throw new IllegalArgumentException("planId 不能为空");
        }
        TeachingPlan plan = teachingPlanMapper.selectById(saveVo.getPlanId());
        if (plan == null) {
            throw new IllegalArgumentException("教学计划不存在: " + saveVo.getPlanId());
        }
        List<TeachingPlanTrainingPurposeSaveVo> rows = saveVo.getPurposes();
        boolean generalSubject = isGeneralSubjectPlan(saveVo.getPlanId());
        Long schemeId = generalSubject ? null : saveVo.getSchemeId();
        if (!generalSubject && schemeId == null && ObjectUtils.isNotEmpty(rows)) {
            TeachingPlanTrainingPurpose first = rows.stream()
                    .filter(Objects::nonNull)
                    .map(TeachingPlanTrainingPurposeSaveVo::getPurpose)
                    .filter(Objects::nonNull)
                    .findFirst().orElse(null);
            schemeId = first == null ? null : first.getSchemeId();
        }
        if (!generalSubject && schemeId != null && ObjectUtils.isNotEmpty(rows)) {
            for (TeachingPlanTrainingPurposeSaveVo row : rows) {
                if (row != null && row.getPurpose() != null
                        && row.getPurpose().getSchemeId() != null
                        && !schemeId.equals(row.getPurpose().getSchemeId())) {
                    throw new IllegalArgumentException("同一次批量保存不能混入不同培养方案的训练目的");
                }
            }
        }
        if (!generalSubject && schemeId == null) {
            throw new IllegalArgumentException("非通识通用课目批量保存训练目的必须指定 schemeId");
        }
        teachingPlanTrainingPurposeRefMapper.deleteByPlanAndScheme(saveVo.getPlanId(), schemeId, generalSubject);
        teachingPlanTrainingPurposeMapper.deleteByPlanAndScheme(saveVo.getPlanId(), schemeId, generalSubject);
        if (ObjectUtils.isEmpty(rows)) {
            return;
        }
        int sort = 1;
        for (TeachingPlanTrainingPurposeSaveVo row : rows) {
            if (row == null || row.getPurpose() == null) {
                continue;
            }
            TeachingPlanTrainingPurpose trainingPurpose = row.getPurpose();
            trainingPurpose.setId(null);
            trainingPurpose.setPlanId(saveVo.getPlanId());
            if (trainingPurpose.getSchemeId() == null) {
                trainingPurpose.setSchemeId(schemeId);
            }
            boolean rowGeneralSubject = forceNullSchemeIfGeneralSubject(trainingPurpose);
            if (rowGeneralSubject) {
                trainingPurpose.setSchemeId(null);
            }
            validateTrainingPurposeForInsert(trainingPurpose, rowGeneralSubject);
            if (trainingPurpose.getSort() == null) {
                trainingPurpose.setSort(sort);
            }
            sort++;
            UserUtils.reflash(trainingPurpose);
            teachingPlanTrainingPurposeMapper.insert(trainingPurpose);
            insertTrainingPurposeRefs(trainingPurpose.getId(), trainingPurpose.getPlanId(),
                    trainingPurpose.getSchemeId(), row.getRefs());
        }
    }

    /**
     * 新增训练目的必填校验：planId/purpose 必填；
     * 非通识通用需指定 schemeId。
     */
    private void validateTrainingPurposeForInsert(TeachingPlanTrainingPurpose trainingPurpose, boolean generalSubject) {
        if (trainingPurpose == null) {
            throw new IllegalArgumentException("训练目的不能为空");
        }
        if (trainingPurpose.getPlanId() == null) {
            throw new IllegalArgumentException("planId 不能为空");
        }
        if (StringUtils.isBlank(trainingPurpose.getPurpose())) {
            throw new IllegalArgumentException("训练目的不能为空");
        }
        if (!generalSubject && trainingPurpose.getSchemeId() == null) {
            throw new IllegalArgumentException("非通识通用课目新增训练目的必须指定 schemeId");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long addTrainingPurpose(TeachingPlanTrainingPurpose trainingPurpose) {
        boolean generalSubject = forceNullSchemeIfGeneralSubject(trainingPurpose);
        if (generalSubject) {
            trainingPurpose.setSchemeId(null);
        }
        validateTrainingPurposeForInsert(trainingPurpose, generalSubject);
        UserUtils.reflash(trainingPurpose);
        teachingPlanTrainingPurposeMapper.insert(trainingPurpose);
        return trainingPurpose.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateTrainingPurpose(TeachingPlanTrainingPurpose trainingPurpose) {
        if (trainingPurpose == null || trainingPurpose.getId() == null) {
            throw new IllegalArgumentException("训练目的id不能为空");
        }
        boolean generalSubject = forceNullSchemeIfGeneralSubject(trainingPurpose);
        UserUtils.reflash(trainingPurpose);
        teachingPlanTrainingPurposeMapper.updateById(trainingPurpose);
        // update 动态 SQL 不会把 null 写进 scheme_id，通识通用需显式清空
        if (generalSubject && trainingPurpose.getId() != null) {
            teachingPlanTrainingPurposeMapper.clearSchemeIdById(trainingPurpose.getId());
        }
    }

    /**
     * 通识通用：训练目的强制 scheme_id = null（不区分培养方案）。
     * @return 是否通识通用（调用方用于决定是否 clearSchemeId）
     */
    private boolean forceNullSchemeIfGeneralSubject(TeachingPlanTrainingPurpose trainingPurpose) {
        if (trainingPurpose == null) {
            return false;
        }
        Long planId = trainingPurpose.getPlanId();
        if (planId == null && trainingPurpose.getId() != null) {
            TeachingPlanTrainingPurpose existing = teachingPlanTrainingPurposeMapper.selectById(trainingPurpose.getId());
            if (existing != null) {
                planId = existing.getPlanId();
            }
        }
        if (isGeneralSubjectPlan(planId)) {
            trainingPurpose.setSchemeId(null);
            return true;
        }
        return false;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteTrainingPurpose(Long id) {
        // 删训练目的时同步逻辑删除其绑定的毕业要求，避免孤儿绑定
        if (id != null) {
            teachingPlanTrainingPurposeRefMapper.deleteByPurposeId(id);
        }
        teachingPlanTrainingPurposeMapper.deleteById(id);
    }

    @Override
    public List<TeachingPlanTrainingPurposeRef> listTrainingPurposeRef(Long purposeId) {
        return teachingPlanTrainingPurposeRefMapper.selectByPurposeId(purposeId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveTrainingPurposeRefs(TeachingPlanTrainingPurposeRefSaveVo saveVo) {
        if (saveVo == null || saveVo.getPurposeId() == null) {
            throw new IllegalArgumentException("purposeId 不能为空");
        }
        Long purposeId = saveVo.getPurposeId();
        // 校验训练目的存在；绑定归属(planId/schemeId)以库中记录为准，不信任前端传值
        TeachingPlanTrainingPurpose trainingPurpose = teachingPlanTrainingPurposeMapper.selectById(purposeId);
        if (trainingPurpose == null) {
            throw new IllegalArgumentException("训练目的不存在: " + purposeId);
        }
        Long planId = trainingPurpose.getPlanId() != null ? trainingPurpose.getPlanId() : saveVo.getPlanId();
        Long schemeId = trainingPurpose.getSchemeId() != null ? trainingPurpose.getSchemeId() : saveVo.getSchemeId();
        if (planId == null) {
            throw new IllegalArgumentException("planId 不能为空");
        }
        // 通识通用：绑定也强制 scheme_id 为空
        if (isGeneralSubjectPlan(planId)) {
            schemeId = null;
        }
        // 重建绑定：先逻辑删除旧 ref，再按列表插入（空列表=清空）
        teachingPlanTrainingPurposeRefMapper.deleteByPurposeId(purposeId);
        insertTrainingPurposeRefs(purposeId, planId, schemeId, saveVo.getRefs());
    }

    /**
     * 批量写入训练目的-毕业要求绑定；refs 可空表示不写。
     * planId/schemeId 强制取训练目的归属（不信任前端 ref 上的值）；
     * graduationId 必填且必须真实存在，编码/名称快照缺省时从毕业要求回填；
     * 同一训练目的重复绑定同一毕业要求自动去重。通识通用：schemeId 强制 null。
     */
    private void insertTrainingPurposeRefs(Long purposeId, Long planId, Long schemeId,
                                           List<TeachingPlanTrainingPurposeRef> refs) {
        if (ObjectUtils.isEmpty(refs)) {
            return;
        }
        boolean generalSubject = isGeneralSubjectPlan(planId);
        if (generalSubject) {
            schemeId = null;
        }
        // 校验毕业要求存在
        List<Long> graduationIds = new ArrayList<>();
        for (TeachingPlanTrainingPurposeRef ref : refs) {
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
        for (TeachingPlanTrainingPurposeRef ref : refs) {
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
            ref.setPurposeId(purposeId);
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
            teachingPlanTrainingPurposeRefMapper.insert(ref);
        }
    }

    // ============ 训练内容支撑训练目的（type2 第四节「目的」多选） ============

    @Override
    public List<TeachingPlanContentPurpose> listContentPurpose(Long contentId) {
        return teachingPlanContentPurposeMapper.selectByContentId(contentId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveContentPurposes(TeachingPlanContentPurposeSaveVo saveVo) {
        if (saveVo == null || saveVo.getContentId() == null) {
            throw new IllegalArgumentException("contentId 不能为空");
        }
        Long contentId = saveVo.getContentId();
        // 校验训练内容存在；归属(planId)以库中记录为准，不信任前端传值
        TeachingPlanContent content = teachingPlanContentMapper.selectById(contentId);
        if (content == null) {
            throw new IllegalArgumentException("训练内容不存在: " + contentId);
        }
        Long planId = content.getPlanId() != null ? content.getPlanId() : saveVo.getPlanId();
        if (planId == null) {
            throw new IllegalArgumentException("planId 不能为空");
        }
        // 重建绑定：先逻辑删除旧 ref，再按列表插入（空列表=清空）
        teachingPlanContentPurposeMapper.deleteByContentId(contentId);
        List<Long> purposeIds = saveVo.getPurposeIds();
        if (ObjectUtils.isEmpty(purposeIds)) {
            return;
        }
        // 校验训练目的存在
        List<TeachingPlanTrainingPurpose> purposes = new ArrayList<>();
        for (Long purposeId : purposeIds) {
            if (purposeId == null) {
                continue;
            }
            TeachingPlanTrainingPurpose p = teachingPlanTrainingPurposeMapper.selectById(purposeId);
            if (p == null) {
                throw new IllegalArgumentException("训练目的不存在: " + purposeId);
            }
            purposes.add(p);
        }
        int sort = 1;
        Set<Long> bound = new HashSet<>();
        for (TeachingPlanTrainingPurpose p : purposes) {
            if (p == null || p.getId() == null) {
                continue;
            }
            if (!bound.add(p.getId())) {
                continue;
            }
            TeachingPlanContentPurpose cp = new TeachingPlanContentPurpose();
            cp.setPlanId(planId);
            cp.setContentId(contentId);
            cp.setPurposeId(p.getId());
            cp.setSort(sort++);
            UserUtils.reflash(cp);
            teachingPlanContentPurposeMapper.insert(cp);
        }
    }

    // ============ 实践项目第二节支撑绑定（type4） ============

    @Override
    public TeachingPlanSupportCandidateVo listSupportCandidates(Long courseId) {
        TeachingPlanSupportCandidateVo vo = new TeachingPlanSupportCandidateVo();
        if (courseId == null) {
            return vo;
        }
        CourseVo source = courseMapper.selectCourseById(courseId);
        if (source == null) {
            return vo;
        }
        // before=支撑课程(取课程目标/知识体系)，after=支撑训练课目(取训练目的/训练内容)
        List<Long> beforeIds = parseCourseIdCsv(source.getBeforeCourseId());
        List<Long> afterIds = parseCourseIdCsv(source.getAfterCourseId());
        if (beforeIds.isEmpty() && afterIds.isEmpty()) {
            return vo;
        }
        // 「同专业」基准：项目首个培养方案的 majorId
        Long projectMajorId = resolveProjectMajorId(courseId);

        List<Long> allIds = new ArrayList<>(new LinkedHashSet<>(beforeIds));
        allIds.addAll(afterIds);
        List<Long> distinctIds = new ArrayList<>(new LinkedHashSet<>(allIds));
        Map<Long, String> nameMap = new HashMap<>();
        List<CourseIdAndName> idAndNames = courseMapper.selectCoursesIdAndNameByIds(distinctIds);
        if (ObjectUtils.isNotEmpty(idAndNames)) {
            for (CourseIdAndName cn : idAndNames) {
                if (cn != null && cn.getId() != null) {
                    nameMap.putIfAbsent(cn.getId(), cn.getName());
                }
            }
        }
        Map<Long, Course> selfMap = new HashMap<>();
        List<Course> selfCourses = courseMapper.selectCoursesByIds(distinctIds);
        if (ObjectUtils.isNotEmpty(selfCourses)) {
            for (Course c : selfCourses) {
                if (c != null && c.getId() != null) {
                    selfMap.putIfAbsent(c.getId(), c);
                }
            }
        }

        Set<Long> seenCourseIds = new HashSet<>();
        for (Long id : allIds) {
            if (id == null || !seenCourseIds.add(id)) {
                continue;
            }
            Course c = selfMap.get(id);
            if (c == null || StringUtils.isBlank(c.getType())) {
                continue;
            }
            Integer courseType = parseInt(c.getType());
            if (courseType == null) {
                continue;
            }
            // 课程库 type 与 plan_type 编号 2/3 对调：实践训练课目课程(type=2)的教学计划 plan_type=3；
            // 其余课程(type=1/3 课程类、4 实践项目)按原值查找（课程类默认取 plan_type=1）
            Integer lookupPlanType = Objects.equals(courseType, 2) ? 3 : courseType;
            TeachingPlan plan = teachingPlanMapper.selectBySourceCourseIdAndPlanType(id, lookupPlanType);
            if (plan == null || plan.getId() == null) {
                continue;
            }
            Long planId = plan.getId();
            String courseName = nameMap.get(id);
            if (Objects.equals(courseType, 2)) {
                // 支撑训练课目：训练目的(第二部分) + 训练内容(第四部分)
                List<TeachingPlanTrainingPurpose> purposes =
                        teachingPlanTrainingPurposeMapper.selectByPlanAndScheme(planId, null, false);
                if (ObjectUtils.isNotEmpty(purposes)) {
                    for (TeachingPlanTrainingPurpose p : purposes) {
                        if (p == null || p.getId() == null || StringUtils.isBlank(p.getPurpose())) {
                            continue;
                        }
                        TeachingPlanSupportCandidateItem item = new TeachingPlanSupportCandidateItem();
                        item.setId(p.getId());
                        item.setName(p.getPurpose());
                        item.setSourceCourseId(id);
                        item.setSourceCourseName(courseName);
                        item.setSameMajor(Boolean.FALSE);
                        vo.getPurposes().add(item);
                    }
                }
                appendContentCandidates(planId, id, courseName, vo.getTrainingContents());
            } else {
                // 支撑课程：课程目标(第四部分) + 知识体系(content 全部行，知识单元知识点)
                List<TeachingPlanObjective> objectives =
                        teachingPlanObjectiveMapper.selectByPlanAndScheme(planId, null, false);
                if (ObjectUtils.isNotEmpty(objectives)) {
                    for (TeachingPlanObjective o : objectives) {
                        if (o == null || o.getId() == null || StringUtils.isBlank(o.getContent())) {
                            continue;
                        }
                        TeachingPlanSupportCandidateItem item = new TeachingPlanSupportCandidateItem();
                        item.setId(o.getId());
                        item.setName(o.getContent());
                        item.setTypeName(o.getObjectiveTypeName());
                        item.setSourceCourseId(id);
                        item.setSourceCourseName(courseName);
                        item.setSameMajor(projectMajorId != null && projectMajorId.equals(o.getMajorId()));
                        vo.getObjectives().add(item);
                    }
                }
                appendContentCandidates(planId, id, courseName, vo.getKnowledgePoints());
            }
        }
        // 同专业优先：sameMajor=true 排前，其余保持来源顺序（List.sort 稳定）
        vo.getObjectives().sort(Comparator.comparing(
                (TeachingPlanSupportCandidateItem i) -> Boolean.FALSE.equals(i.getSameMajor())));
        return vo;
    }

    @Override
    public List<TeachingPlanSupportCandidateGroupVo> listSupportCandidateGroups(Long courseId, Long projectPlanId) {
        List<TeachingPlanSupportCandidateGroupVo> result = new ArrayList<>();
        CourseVo source = courseMapper.selectCourseById(courseId);
        if (source == null) return result;
        Long projectSchemeId = null;
        TeachingPlan projectPlan = projectPlanId == null ? null : teachingPlanMapper.selectById(projectPlanId);
        if (projectPlan != null) {
            List<TeachingPlanSchemeVo> projectSchemes = listSchemes(projectPlan.getSourceCourseId());
            if (ObjectUtils.isNotEmpty(projectSchemes)) projectSchemeId = projectSchemes.get(0).getSchemeId();
        }
        Map<Long, TeachingPlanSupportCandidateGroupVo> groups = new LinkedHashMap<>();
        List<Long> sourceIds = new ArrayList<>();
        sourceIds.addAll(parseCourseIdCsv(source.getBeforeCourseId()));
        sourceIds.addAll(parseCourseIdCsv(source.getAfterCourseId()));
        for (Long sourceId : new LinkedHashSet<>(sourceIds)) {
            Course c = courseMapper.selectCourseById(sourceId);
            if (c == null || StringUtils.isBlank(c.getType())) continue;
            Integer courseType = parseInt(c.getType());
            Integer lookupPlanType = Objects.equals(courseType, 2) ? 3 : courseType;
            TeachingPlan plan = teachingPlanMapper.selectBySourceCourseIdAndPlanType(sourceId, lookupPlanType);
            if (plan == null) continue;
            List<TeachingPlanSchemeVo> schemes = listSchemes(sourceId);
            Map<Long, TeachingPlanSchemeVo> schemeMap = new LinkedHashMap<>();
            if (ObjectUtils.isNotEmpty(schemes)) for (TeachingPlanSchemeVo s : schemes) {
                if (s != null) schemeMap.put(s.getSchemeId(), s);
            }
            List<Long> schemeIds = new ArrayList<>(schemeMap.keySet());
            if (schemeIds.isEmpty()) schemeIds.add(null);
            for (Long sid : schemeIds) {
                TeachingPlanSupportCandidateGroupVo group = groups.get(sid);
                if (group == null) {
                    group = new TeachingPlanSupportCandidateGroupVo();
                    group.setSchemeId(sid);
                    TeachingPlanSchemeVo scheme = schemeMap.get(sid);
                    if (scheme != null) { group.setSchemeName(scheme.getSchemeName()); group.setSchemeVersion(scheme.getSchemeVersion()); }
                    group.setSameScheme(Objects.equals(sid, projectSchemeId));
                    groups.put(sid, group);
                }
                if (Objects.equals(courseType, 2)) {
                    List<TeachingPlanTrainingPurpose> purposes =
                            teachingPlanTrainingPurposeMapper.selectByPlanAndScheme(plan.getId(), sid, sid == null);
                    if (ObjectUtils.isNotEmpty(purposes)) for (TeachingPlanTrainingPurpose p : purposes) {
                        TeachingPlanSupportCandidateItem item = new TeachingPlanSupportCandidateItem();
                        item.setId(p.getId()); item.setName(p.getPurpose()); item.setSourceCourseId(sourceId);
                        item.setSourceCourseName(c.getName());
                        item.setSchemeId(sid); item.setSchemeName(group.getSchemeName());
                        item.setSameMajor(Objects.equals(sid, projectSchemeId));
                        group.getPurposes().add(item);
                    }
                    appendContentCandidatesForScheme(plan.getId(), sourceId, c.getName(), sid,
                            group.getSchemeName(), group.getTrainingContents());
                } else {
                    List<TeachingPlanObjective> objectives =
                            teachingPlanObjectiveMapper.selectByPlanAndScheme(plan.getId(), sid, sid == null);
                    if (ObjectUtils.isNotEmpty(objectives)) for (TeachingPlanObjective o : objectives) {
                        TeachingPlanSupportCandidateItem item = new TeachingPlanSupportCandidateItem();
                        item.setId(o.getId()); item.setName(o.getContent()); item.setTypeName(o.getObjectiveTypeName());
                        item.setSourceCourseId(sourceId); item.setSourceCourseName(c.getName());
                        item.setSchemeId(sid); item.setSchemeName(group.getSchemeName());
                        item.setSameMajor(Objects.equals(sid, projectSchemeId));
                        group.getObjectives().add(item);
                    }
                    appendContentCandidatesForScheme(plan.getId(), sourceId, c.getName(), sid,
                            group.getSchemeName(), group.getKnowledgePoints());
                }
            }
        }
        result.addAll(groups.values());
        result.sort(Comparator.comparing(g -> !Boolean.TRUE.equals(g.getSameScheme())));
        return result;
    }

    @Override
    public List<TeachingPlanSupportCandidateTreeNodeVo> listSupportCandidateTree(Long courseId, Integer type) {
        if (courseId == null) {
            throw new IllegalArgumentException("courseId 不能为空");
        }
        if (!Objects.equals(type, 1) && !Objects.equals(type, 2)) {
            throw new IllegalArgumentException("type 只能为1（目标/目的）或2（知识体系/训练内容）");
        }

        CourseVo projectCourse = courseMapper.selectCourseById(courseId);
        if (projectCourse == null) {
            throw new IllegalArgumentException("实践项目课程不存在: " + courseId);
        }
        if (!Objects.equals(parseInt(projectCourse.getType()), 4)) {
            throw new IllegalArgumentException("仅实践项目课程可查询支撑候选树");
        }

        List<TeachingPlanSupportCandidateTreeNodeVo> result = new ArrayList<>();
        appendSupportCandidateRoots(result, parseCourseIdCsv(projectCourse.getBeforeCourseId()),
                1, type);
        appendSupportCandidateRoots(result, parseCourseIdCsv(projectCourse.getAfterCourseId()),
                2, type);
        return result;
    }

    private void appendSupportCandidateRoots(List<TeachingPlanSupportCandidateTreeNodeVo> result,
                                             List<Long> sourceCourseIds,
                                             Integer refType,
                                             Integer candidateType) {
        if (ObjectUtils.isEmpty(sourceCourseIds)) {
            return;
        }
        for (Long sourceCourseId : new LinkedHashSet<>(sourceCourseIds)) {
            TeachingPlanSupportCandidateTreeNodeVo root = buildSupportCandidateRoot(
                    sourceCourseId, refType, candidateType);
            if (root != null) {
                result.add(root);
            }
        }
    }

    private TeachingPlanSupportCandidateTreeNodeVo buildSupportCandidateRoot(
            Long sourceCourseId, Integer refType, Integer candidateType) {
        Course sourceCourse = courseMapper.selectCourseById(sourceCourseId);
        if (sourceCourse == null) {
            return null;
        }
        Integer planType = toTeachingPlanType(parseInt(sourceCourse.getType()));
        if (planType == null) {
            return null;
        }
        TeachingPlan plan = teachingPlanMapper.selectBySourceCourseIdAndPlanType(sourceCourseId, planType);
        if (plan == null || plan.getId() == null) {
            return null;
        }

        boolean general = Objects.equals(refType, 2)
                ? isGeneralSubjectModuleCourse(sourceCourseId)
                : isPublicFoundationCourse(sourceCourseId);
        String rootNodeType = Objects.equals(refType, 1) ? "course" : "trainingSubject";
        List<TeachingPlanSupportCandidateTreeNodeVo> schemeNodes = Objects.equals(candidateType, 1)
                ? buildGoalOrPurposeSchemeNodes(plan.getId(), sourceCourseId, refType, rootNodeType, general)
                : buildKnowledgeOrTrainingContentSchemeNodes(
                        plan.getId(), sourceCourseId, refType, rootNodeType, general);
        if (schemeNodes.isEmpty()) {
            return null;
        }

        TeachingPlanSupportCandidateTreeNodeVo root = newSupportCandidateNode(
                rootNodeType + ":" + sourceCourseId,
                sourceCourseId,
                StringUtils.defaultIfBlank(sourceCourse.getName(), plan.getSourceCourseName()),
                rootNodeType,
                refType,
                sourceCourseId,
                null,
                null,
                null,
                false);
        root.setChildren(schemeNodes);
        return root;
    }

    private List<TeachingPlanSupportCandidateTreeNodeVo> buildGoalOrPurposeSchemeNodes(
            Long planId, Long sourceCourseId, Integer refType, String rootNodeType, boolean general) {
        Map<Long, List<TeachingPlanSupportCandidateTreeNodeVo>> leavesByScheme = new LinkedHashMap<>();
        if (Objects.equals(refType, 2)) {
            List<TeachingPlanTrainingPurpose> purposes =
                    teachingPlanTrainingPurposeMapper.selectByPlanAndScheme(planId, null, general);
            if (ObjectUtils.isNotEmpty(purposes)) {
                for (TeachingPlanTrainingPurpose purpose : purposes) {
                    if (purpose == null || purpose.getId() == null || StringUtils.isBlank(purpose.getPurpose())) {
                        continue;
                    }
                    Long itemSchemeId = general ? null : purpose.getSchemeId();
                    leavesByScheme.computeIfAbsent(itemSchemeId, key -> new ArrayList<>()).add(
                            newSupportCandidateNode(
                            "purpose:" + purpose.getId(), purpose.getId(), purpose.getPurpose(), "purpose",
                            refType, sourceCourseId, itemSchemeId, null, null, true));
                }
            }
        } else {
            List<TeachingPlanObjective> objectives =
                    teachingPlanObjectiveMapper.selectByPlanAndScheme(planId, null, general);
            if (ObjectUtils.isNotEmpty(objectives)) {
                for (TeachingPlanObjective objective : objectives) {
                    if (objective == null || objective.getId() == null || StringUtils.isBlank(objective.getContent())) {
                        continue;
                    }
                    Long itemSchemeId = general ? null : objective.getSchemeId();
                    leavesByScheme.computeIfAbsent(itemSchemeId, key -> new ArrayList<>()).add(
                            newSupportCandidateNode(
                            "objective:" + objective.getId(), objective.getId(), objective.getContent(), "objective",
                            refType, sourceCourseId, itemSchemeId, null,
                            objective.getObjectiveTypeName(), true));
                }
            }
        }
        return buildSupportSchemeNodes(sourceCourseId, refType, rootNodeType, leavesByScheme);
    }

    private List<TeachingPlanSupportCandidateTreeNodeVo> buildKnowledgeOrTrainingContentSchemeNodes(
            Long planId, Long sourceCourseId, Integer refType, String rootNodeType, boolean general) {
        List<TeachingPlanContent> contents = teachingPlanContentMapper.selectByPlanId(planId);
        if (ObjectUtils.isEmpty(contents)) {
            return new ArrayList<>();
        }

        Map<Long, TeachingPlanSchemeVo> schemeMap = loadSupportSchemeMap(sourceCourseId);
        List<Long> schemeIds = general
                ? new ArrayList<>(Collections.singletonList(null))
                : new ArrayList<>(schemeMap.keySet());
        if (schemeIds.isEmpty()) {
            schemeIds.add(null);
        }

        Map<Long, List<TeachingPlanSupportCandidateTreeNodeVo>> leavesByScheme = new LinkedHashMap<>();
        String nodeType = Objects.equals(refType, 1) ? "knowledgeSystem" : "trainingContent";
        for (Long schemeId : schemeIds) {
            List<TeachingPlanSupportCandidateTreeNodeVo> leaves = new ArrayList<>();
            for (TeachingPlanContent content : contents) {
                if (content == null || content.getId() == null || StringUtils.isBlank(content.getTitle())) {
                    continue;
                }
                String name = Objects.equals(refType, 2)
                        ? translateTrainingModuleName(content.getTitle())
                        : content.getTitle();
                leaves.add(newSupportCandidateNode(
                        nodeType + ":" + content.getId() + ":scheme:"
                                + (schemeId == null ? "general" : schemeId),
                        content.getId(), name, nodeType,
                        refType, sourceCourseId, schemeId, null, null, true));
            }
            if (!leaves.isEmpty()) {
                leavesByScheme.put(schemeId, leaves);
            }
        }
        return buildSupportSchemeNodes(sourceCourseId, refType, rootNodeType, leavesByScheme, schemeMap);
    }

    private List<TeachingPlanSupportCandidateTreeNodeVo> buildSupportSchemeNodes(
            Long sourceCourseId,
            Integer refType,
            String rootNodeType,
            Map<Long, List<TeachingPlanSupportCandidateTreeNodeVo>> leavesByScheme) {
        return buildSupportSchemeNodes(
                sourceCourseId, refType, rootNodeType, leavesByScheme, loadSupportSchemeMap(sourceCourseId));
    }

    private List<TeachingPlanSupportCandidateTreeNodeVo> buildSupportSchemeNodes(
            Long sourceCourseId,
            Integer refType,
            String rootNodeType,
            Map<Long, List<TeachingPlanSupportCandidateTreeNodeVo>> leavesByScheme,
            Map<Long, TeachingPlanSchemeVo> schemeMap) {
        List<TeachingPlanSupportCandidateTreeNodeVo> result = new ArrayList<>();
        if (leavesByScheme.isEmpty()) {
            return result;
        }

        List<Long> orderedSchemeIds = new ArrayList<>();
        if (leavesByScheme.containsKey(null)) {
            orderedSchemeIds.add(null);
        }
        for (Long schemeId : schemeMap.keySet()) {
            if (leavesByScheme.containsKey(schemeId) && !orderedSchemeIds.contains(schemeId)) {
                orderedSchemeIds.add(schemeId);
            }
        }
        for (Long schemeId : leavesByScheme.keySet()) {
            if (!orderedSchemeIds.contains(schemeId)) {
                orderedSchemeIds.add(schemeId);
            }
        }

        for (Long schemeId : orderedSchemeIds) {
            TeachingPlanSchemeVo scheme = schemeMap.get(schemeId);
            String schemeName = schemeId == null
                    ? "通识通用"
                    : scheme == null
                            ? "培养方案" + schemeId
                            : StringUtils.defaultIfBlank(scheme.getSchemeName(), "培养方案" + schemeId);
            String schemeVersion = scheme == null ? null : scheme.getSchemeVersion();
            TeachingPlanSupportCandidateTreeNodeVo schemeNode = newSupportCandidateNode(
                    rootNodeType + ":" + sourceCourseId + ":scheme:"
                            + (schemeId == null ? "general" : schemeId),
                    schemeId,
                    schemeName,
                    "scheme",
                    refType,
                    sourceCourseId,
                    schemeId,
                    schemeVersion,
                    null,
                    false);
            schemeNode.setChildren(leavesByScheme.get(schemeId));
            result.add(schemeNode);
        }
        return result;
    }

    private Map<Long, TeachingPlanSchemeVo> loadSupportSchemeMap(Long sourceCourseId) {
        Map<Long, TeachingPlanSchemeVo> result = new LinkedHashMap<>();
        List<TeachingPlanSchemeVo> schemes = listSchemes(sourceCourseId);
        if (ObjectUtils.isNotEmpty(schemes)) {
            for (TeachingPlanSchemeVo scheme : schemes) {
                if (scheme != null && scheme.getSchemeId() != null) {
                    result.putIfAbsent(scheme.getSchemeId(), scheme);
                }
            }
        }
        return result;
    }

    private TeachingPlanSupportCandidateTreeNodeVo newSupportCandidateNode(
            String key, Long id, String name, String nodeType, Integer refType,
            Long courseId, Long schemeId, String schemeVersion, String typeName, boolean selectable) {
        TeachingPlanSupportCandidateTreeNodeVo node = new TeachingPlanSupportCandidateTreeNodeVo();
        node.setKey(key);
        node.setId(id);
        node.setName(name);
        node.setNodeType(nodeType);
        node.setRefType(refType);
        node.setCourseId(courseId);
        node.setSchemeId(schemeId);
        node.setSchemeVersion(schemeVersion);
        node.setTypeName(typeName);
        node.setSelectable(selectable);
        return node;
    }

    /** t_csys_course.type -> t_csys_teaching_plan.plan_type。 */
    private Integer toTeachingPlanType(Integer courseType) {
        if (Objects.equals(courseType, 1)) {
            return 1;
        }
        if (Objects.equals(courseType, 2)) {
            return 3;
        }
        if (Objects.equals(courseType, 3)) {
            return 2;
        }
        if (Objects.equals(courseType, 4)) {
            return 4;
        }
        return null;
    }

    private void appendContentCandidatesForScheme(Long planId, Long courseId, String courseName, Long schemeId,
                                                   String schemeName,
                                                   List<TeachingPlanSupportCandidateItem> target) {
        List<TeachingPlanContent> contents = teachingPlanContentMapper.selectByPlanId(planId);
        if (ObjectUtils.isEmpty(contents)) return;
        for (TeachingPlanContent c : contents) {
            if (c == null || c.getId() == null || StringUtils.isBlank(c.getTitle())) continue;
            TeachingPlanSupportCandidateItem item = new TeachingPlanSupportCandidateItem();
            item.setId(c.getId()); item.setName(translateTrainingModuleName(c.getTitle()));
            item.setSourceCourseId(courseId); item.setSourceCourseName(courseName);
            item.setSchemeId(schemeId); item.setSchemeName(schemeName);
            item.setSameMajor(Boolean.FALSE);
            target.add(item);
        }
    }

    private void validateObjectiveBatchScheme(List<TeachingPlanObjectiveSaveVo> rows, Long schemeId,
                                              boolean publicFoundation) {
        if (ObjectUtils.isEmpty(rows)) {
            return;
        }
        for (TeachingPlanObjectiveSaveVo row : rows) {
            if (row == null || row.getObjective() == null) {
                continue;
            }
            Long rowSchemeId = row.getObjective().getSchemeId();
            if (publicFoundation && rowSchemeId != null) {
                row.getObjective().setSchemeId(null);
            } else if (!publicFoundation && rowSchemeId != null && !schemeId.equals(rowSchemeId)) {
                throw new IllegalArgumentException("同一次批量保存不能混入不同培养方案的课程目标");
            }
        }
    }

    @Override
    public TeachingPlanPracticeProjectBackgroundVo getPracticeProjectBackground(Long planId) {
        validatePracticeProjectPlan(planId);
        TeachingPlanPracticeProjectBackgroundVo result = new TeachingPlanPracticeProjectBackgroundVo();
        result.setPlanId(planId);

        List<TeachingPlanSection> sections = teachingPlanSectionMapper.selectByPlanId(planId);
        result.setComplexProblem(findSectionContent(sections, "complex_problem", "拟解决的复杂问题"));
        result.setMainTask(findSectionContent(sections, "main_task", "主要任务"));

        List<TeachingPlanSupportObjective> objectives = listSupportObjective(planId);
        result.setSupportObjectives(objectives);
        result.setObjectiveIds(objectives.stream()
                .filter(row -> Objects.equals(row.getRefType(), 1) && row.getObjectiveId() != null)
                .map(TeachingPlanSupportObjective::getObjectiveId)
                .collect(Collectors.toList()));
        result.setPurposeIds(objectives.stream()
                .filter(row -> Objects.equals(row.getRefType(), 2) && row.getPurposeId() != null)
                .map(TeachingPlanSupportObjective::getPurposeId)
                .collect(Collectors.toList()));

        List<TeachingPlanSupportContent> contents = listSupportContent(planId);
        result.setSupportContents(contents);
        result.setContentIds(contents.stream()
                .map(TeachingPlanSupportContent::getContentId)
                .filter(Objects::nonNull)
                .collect(Collectors.toList()));
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void savePracticeProjectBackground(TeachingPlanPracticeProjectBackgroundSaveVo saveVo) {
        if (saveVo == null || saveVo.getPlanId() == null) {
            throw new IllegalArgumentException("planId 不能为空");
        }
        Long planId = saveVo.getPlanId();
        validatePracticeProjectPlan(planId);

        // 整页保存：正文 null 与空串均表示清空，不能遗留上一次提交的数据。
        upsertSection(planId, "拟解决的复杂问题", "complex_problem",
                StringUtils.defaultString(saveVo.getComplexProblem()), 1);
        upsertSection(planId, "主要任务", "main_task",
                StringUtils.defaultString(saveVo.getMainTask()), 2);

        TeachingPlanSupportObjectiveSaveVo objectiveSaveVo = new TeachingPlanSupportObjectiveSaveVo();
        objectiveSaveVo.setPlanId(planId);
        objectiveSaveVo.setObjectiveIds(saveVo.getObjectiveIds());
        objectiveSaveVo.setPurposeIds(saveVo.getPurposeIds());
        saveSupportObjectives(objectiveSaveVo);

        TeachingPlanSupportContentSaveVo contentSaveVo = new TeachingPlanSupportContentSaveVo();
        contentSaveVo.setPlanId(planId);
        contentSaveVo.setContentIds(saveVo.getContentIds());
        saveSupportContents(contentSaveVo);
    }

    private void validatePracticeProjectPlan(Long planId) {
        if (planId == null) {
            throw new IllegalArgumentException("planId 不能为空");
        }
        TeachingPlan plan = teachingPlanMapper.selectById(planId);
        if (plan == null) {
            throw new IllegalArgumentException("教学计划不存在: " + planId);
        }
        if (!Objects.equals(plan.getPlanType(), 4)) {
            throw new IllegalArgumentException("仅实践项目教学计划可保存任务背景与目标");
        }
    }

    private String findSectionContent(List<TeachingPlanSection> sections, String sectionCode, String sectionTitle) {
        if (ObjectUtils.isEmpty(sections)) {
            return "";
        }
        for (TeachingPlanSection section : sections) {
            if (section != null && (Objects.equals(sectionCode, section.getSectionCode())
                    || Objects.equals(sectionTitle, section.getSectionTitle()))) {
                return StringUtils.defaultString(section.getContent());
            }
        }
        return "";
    }

    @Override
    public List<TeachingPlanSupportObjective> listSupportObjective(Long planId) {
        if (planId == null) {
            return Collections.emptyList();
        }
        return teachingPlanSupportObjectiveMapper.selectByPlanId(planId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveSupportObjectives(TeachingPlanSupportObjectiveSaveVo saveVo) {
        if (saveVo == null || saveVo.getPlanId() == null) {
            throw new IllegalArgumentException("planId 不能为空");
        }
        Long planId = saveVo.getPlanId();
        teachingPlanSupportObjectiveMapper.deleteByPlanId(planId);
        List<Long> objectiveIds = saveVo.getObjectiveIds();
        List<Long> purposeIds = saveVo.getPurposeIds();
        if (ObjectUtils.isEmpty(objectiveIds) && ObjectUtils.isEmpty(purposeIds)) {
            return;
        }
        int sort = 1;
        Set<Long> boundObjective = new HashSet<>();
        if (ObjectUtils.isNotEmpty(objectiveIds)) {
            for (Long id : objectiveIds) {
                if (id == null || !boundObjective.add(id)) {
                    continue;
                }
                TeachingPlanObjective o = teachingPlanObjectiveMapper.selectById(id);
                if (o == null) {
                    throw new IllegalArgumentException("课程目标不存在: " + id);
                }
                TeachingPlanSupportObjective row = new TeachingPlanSupportObjective();
                row.setPlanId(planId);
                row.setRefType(1);
                row.setRefPlanId(o.getPlanId());
                row.setRefCourseId(resolveRefCourseId(o.getPlanId()));
                row.setObjectiveId(o.getId());
                row.setItemName(o.getContent());
                row.setItemTypeName(o.getObjectiveTypeName());
                row.setMajorId(o.getMajorId());
                row.setSort(sort++);
                UserUtils.reflash(row);
                teachingPlanSupportObjectiveMapper.insert(row);
            }
        }
        Set<Long> boundPurpose = new HashSet<>();
        if (ObjectUtils.isNotEmpty(purposeIds)) {
            for (Long id : purposeIds) {
                if (id == null || !boundPurpose.add(id)) {
                    continue;
                }
                TeachingPlanTrainingPurpose p = teachingPlanTrainingPurposeMapper.selectById(id);
                if (p == null) {
                    throw new IllegalArgumentException("训练目的不存在: " + id);
                }
                TeachingPlanSupportObjective row = new TeachingPlanSupportObjective();
                row.setPlanId(planId);
                row.setRefType(2);
                row.setRefPlanId(p.getPlanId());
                row.setRefCourseId(resolveRefCourseId(p.getPlanId()));
                row.setPurposeId(p.getId());
                row.setItemName(p.getPurpose());
                row.setSort(sort++);
                UserUtils.reflash(row);
                teachingPlanSupportObjectiveMapper.insert(row);
            }
        }
    }

    @Override
    public List<TeachingPlanSupportContent> listSupportContent(Long planId) {
        if (planId == null) {
            return Collections.emptyList();
        }
        List<TeachingPlanSupportContent> contents = teachingPlanSupportContentMapper.selectByPlanId(planId);
        if (ObjectUtils.isNotEmpty(contents)) {
            for (TeachingPlanSupportContent content : contents) {
                if (content != null && Objects.equals(content.getRefType(), 2)) {
                    content.setItemTitle(translateTrainingModuleName(content.getItemTitle()));
                }
            }
        }
        return contents;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveSupportContents(TeachingPlanSupportContentSaveVo saveVo) {
        if (saveVo == null || saveVo.getPlanId() == null) {
            throw new IllegalArgumentException("planId 不能为空");
        }
        Long planId = saveVo.getPlanId();
        teachingPlanSupportContentMapper.deleteByPlanId(planId);
        List<Long> contentIds = saveVo.getContentIds();
        if (ObjectUtils.isEmpty(contentIds)) {
            return;
        }
        int sort = 1;
        Set<Long> bound = new HashSet<>();
        for (Long id : contentIds) {
            if (id == null || !bound.add(id)) {
                continue;
            }
            TeachingPlanContent c = teachingPlanContentMapper.selectById(id);
            if (c == null) {
                throw new IllegalArgumentException("教学内容不存在: " + id);
            }
            TeachingPlan plan = c.getPlanId() == null ? null : teachingPlanMapper.selectById(c.getPlanId());
            // 实践训练课目(plan_type=3) content -> 训练内容(refType=2)；其余 -> 知识体系(refType=1)
            int refType = (plan != null && Objects.equals(plan.getPlanType(), 3)) ? 2 : 1;
            TeachingPlanSupportContent row = new TeachingPlanSupportContent();
            row.setPlanId(planId);
            row.setRefType(refType);
            row.setRefPlanId(c.getPlanId());
            row.setRefCourseId(plan == null ? null : plan.getSourceCourseId());
            row.setContentId(c.getId());
            // type2 content.title 存模块字典编码，快照存名称，供 type4 Word 直接回显
            row.setItemTitle(translateTrainingModuleName(c.getTitle()));
            row.setSort(sort++);
            UserUtils.reflash(row);
            teachingPlanSupportContentMapper.insert(row);
        }
    }

    /** 支撑课程/训练课目教学计划的 content 全部行作为候选（知识体系或训练内容）。 */
    private void appendContentCandidates(Long planId, Long courseId, String courseName,
                                         List<TeachingPlanSupportCandidateItem> target) {
        List<TeachingPlanContent> contents = teachingPlanContentMapper.selectByPlanId(planId);
        if (ObjectUtils.isEmpty(contents)) {
            return;
        }
        for (TeachingPlanContent c : contents) {
            if (c == null || c.getId() == null || StringUtils.isBlank(c.getTitle())) {
                continue;
            }
            TeachingPlanSupportCandidateItem item = new TeachingPlanSupportCandidateItem();
            item.setId(c.getId());
            item.setName(translateTrainingModuleName(c.getTitle()));
            item.setSourceCourseId(courseId);
            item.setSourceCourseName(courseName);
            item.setSameMajor(Boolean.FALSE);
            target.add(item);
        }
    }

    /**
     * type2 训练内容「模块」title 存字典 value(编码)，候选列表显示时译为名称；
     * 普通课程(type1/3) content.title 为自由文本专题名，未命中字典原样返回。
     */
    private String translateTrainingModuleName(String title) {
        if (StringUtils.isBlank(title)) {
            return title;
        }
        List<SysDictData> list = CurDictUtils.getDictData(DICT_PLAN_TRAINING_MODULE);
        if (ObjectUtils.isEmpty(list)) {
            return title;
        }
        for (SysDictData d : list) {
            if (d == null || StringUtils.isBlank(d.getDictValue())) {
                continue;
            }
            if (title.trim().equals(d.getDictValue().trim())) {
                return StringUtils.defaultIfBlank(d.getDictLabel(), title).trim();
            }
        }
        return title;
    }

    /** 由来源教学计划ID反查支撑课程/训练课目ID（来源计划 source_course_id）。 */
    private Long resolveRefCourseId(Long refPlanId) {
        if (refPlanId == null) {
            return null;
        }
        TeachingPlan plan = teachingPlanMapper.selectById(refPlanId);
        return plan == null ? null : plan.getSourceCourseId();
    }

    /** 项目首个培养方案的 majorId，作为「同专业优先」基准；无引用时为 null。 */
    private Long resolveProjectMajorId(Long courseId) {
        List<TeachingPlanSchemeVo> schemes = listSchemes(courseId);
        if (ObjectUtils.isNotEmpty(schemes)) {
            for (TeachingPlanSchemeVo s : schemes) {
                if (s != null && s.getMajorId() != null) {
                    return s.getMajorId();
                }
            }
        }
        return null;
    }

    /** 逗号/顿号/分号分隔的课程ID串解析为 Long 列表（与 TeachingPlanServiceImpl 同口径）。 */
    private List<Long> parseCourseIdCsv(String csv) {
        if (StringUtils.isBlank(csv)) {
            return Collections.emptyList();
        }
        return Arrays.stream(csv.split("[,，、;；]"))
                .map(String::trim)
                .filter(StringUtils::isNotBlank)
                .map(s -> {
                    try {
                        return Long.valueOf(s);
                    } catch (NumberFormatException e) {
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    /** 类型字符串转 Integer，非法返回 null。 */
    private Integer parseInt(String s) {
        if (StringUtils.isBlank(s)) {
            return null;
        }
        try {
            return Integer.valueOf(s.trim());
        } catch (NumberFormatException e) {
            return null;
        }
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
            item.setSysflag(0);
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
        fillOutcomeTypeNames(list);
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
        validateAssessment(assessment);
        UserUtils.reflash(assessment);
        teachingPlanAssessmentMapper.insert(assessment);
        // scoreRule 为非本表透传字段：有值时回写主表 t_csys_teaching_plan.score_rule
        writeBackScoreRule(assessment.getPlanId(), assessment.getScoreRule());
        return assessment.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateAssessment(TeachingPlanAssessment assessment) {
        validateAssessment(assessment);
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

    private void validateAssessment(TeachingPlanAssessment assessment) {
        if (assessment == null) {
            throw new IllegalArgumentException("考核评价不能为空");
        }
        BigDecimal weight = assessment.getWeight();
        if (weight != null && (weight.compareTo(BigDecimal.ZERO) < 0
                || weight.compareTo(BigDecimal.ONE) > 0)) {
            throw new IllegalArgumentException("考核权重必须为0到1之间的小数");
        }
        if (Objects.equals(assessment.getAssessmentCategory(), 5)) {
            if (assessment.getOutcomeType() == null) {
                throw new IllegalArgumentException("成果评价必须选择成果类型");
            }
            if (StringUtils.isBlank(assessment.getAssessmentItem())) {
                throw new IllegalArgumentException("成果评价必须填写成果形式");
            }
            validateOutcomeType(assessment.getOutcomeType());
            // 成果评价不复用普通考核的方式、机制、成绩评定字段。
            assessment.setMethod(null);
            assessment.setMechanism(null);
            assessment.setScoreSystem(null);
        }
    }

    private void validateOutcomeType(Integer outcomeType) {
        List<SysDictData> dicts = CurDictUtils.getDictData(DICT_PLAN_OUTCOME_TYPE);
        if (ObjectUtils.isEmpty(dicts)) {
            return;
        }
        boolean matched = dicts.stream().filter(Objects::nonNull)
                .anyMatch(d -> String.valueOf(outcomeType).equals(StringUtils.trim(d.getDictValue())));
        if (!matched) {
            throw new IllegalArgumentException("成果类型不在字典 " + DICT_PLAN_OUTCOME_TYPE + " 中: " + outcomeType);
        }
    }

    private void fillOutcomeTypeNames(List<TeachingPlanAssessment> list) {
        if (ObjectUtils.isEmpty(list)) {
            return;
        }
        Map<String, String> labels = new HashMap<>();
        List<SysDictData> dicts = CurDictUtils.getDictData(DICT_PLAN_OUTCOME_TYPE);
        if (ObjectUtils.isNotEmpty(dicts)) {
            for (SysDictData d : dicts) {
                if (d != null && StringUtils.isNotBlank(d.getDictValue())) {
                    labels.put(d.getDictValue().trim(), d.getDictLabel());
                }
            }
        }
        for (TeachingPlanAssessment assessment : list) {
            if (assessment != null && Objects.equals(assessment.getAssessmentCategory(), 5)
                    && assessment.getOutcomeType() != null) {
                String code = String.valueOf(assessment.getOutcomeType());
                assessment.setOutcomeTypeName(labels.getOrDefault(code, defaultOutcomeTypeName(code)));
            }
        }
    }

    private String defaultOutcomeTypeName(String code) {
        if ("1".equals(code)) {
            return "个人成果";
        }
        if ("2".equals(code)) {
            return "团队成果";
        }
        if ("3".equals(code)) {
            return "过程成果";
        }
        return code;
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
        // 含已删除(sysflag=2)在内都没有数据时，按字典初始化 9 条条件类型  不需要初始化
        /*int total = teachingPlanConditionMapper.countAllByPlanId(planId);
        if (total <= 0) {
            initDefaultConditions(planId);
        }*/
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
        upsertSection(planId, sectionTitle, sectionCode, value, 0);
    }

    private void upsertSection(Long planId, String sectionTitle, String sectionCode, String value, int sort) {
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
        s.setSort(sort);
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
