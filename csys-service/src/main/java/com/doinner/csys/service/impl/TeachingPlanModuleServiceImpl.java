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
import com.doinner.csys.dao.TeachingPlanPracticeItemDetailMapper;
import com.doinner.csys.dao.TeachingPlanPracticeItemMapper;
import com.doinner.csys.dao.TeachingPlanProcessStepMapper;
import com.doinner.csys.dao.TeachingPlanRefMapper;
import com.doinner.csys.dao.TeachingPlanTargetDesignMapper;
import com.doinner.csys.dao.TeachingPlanTextbookMapper;
import com.doinner.csys.domain.Course;
import com.doinner.csys.domain.CourseRefGraduation;
import com.doinner.csys.domain.StandardGraduation;
import com.doinner.csys.domain.TeachingPlan;
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
import com.doinner.csys.domain.vo.TeachingPlanObjectiveOptionVo;
import com.doinner.csys.domain.vo.TeachingPlanObjectiveRefSaveVo;
import com.doinner.csys.domain.vo.TeachingPlanObjectiveSaveVo;
import com.doinner.csys.domain.vo.TeachingPlanObjectiveTreeVo;
import com.doinner.csys.domain.vo.TeachingPlanSchemeVo;
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
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

    /** 教学目标类型字典 type（知识/能力/素质等） */
    private static final String DICT_PLAN_TARGET_TYPE = "sys_plan_target_type";

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final TypeReference<List<TeachingPlanTargetDesign.KnowledgePointItem>> KP_LIST_TYPE =
            new TypeReference<List<TeachingPlanTargetDesign.KnowledgePointItem>>() {};

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
    private TeachingPlanMapper teachingPlanMapper;

    @Resource
    private TeachingPlanTextbookMapper teachingPlanTextbookMapper;

    @Resource
    private TeachingPlanConditionMapper teachingPlanConditionMapper;

    @Resource
    private TeachingPlanProcessStepMapper teachingPlanProcessStepMapper;

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

    @Override
    public List<TeachingPlanObjective> listObjective(Long planId, Long schemeId) {
        return teachingPlanObjectiveMapper.selectByPlanAndScheme(planId, schemeId);
    }

    @Override
    public List<TeachingPlanObjectiveTreeVo> listObjectiveTree(Long planId, Long schemeId, String objectiveTypeCode) {
        if (planId == null || schemeId == null) {
            throw new IllegalArgumentException("planId 与 schemeId 不能为空");
        }
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

        // 2) 目标内容（可按类型过滤）
        List<TeachingPlanObjective> objectives =
                teachingPlanObjectiveMapper.selectByPlanAndSchemeAndType(planId, schemeId, objectiveTypeCode);
        Map<String, List<TeachingPlanObjective>> objectivesByType = new LinkedHashMap<>();
        if (ObjectUtils.isNotEmpty(objectives)) {
            for (TeachingPlanObjective obj : objectives) {
                String typeCode = obj.getObjectiveTypeCode() == null ? "" : obj.getObjectiveTypeCode();
                objectivesByType.computeIfAbsent(typeCode, k -> new ArrayList<>()).add(obj);
            }
        }

        // 3) 支撑毕业要求按 objectiveId 分组（一次查出，避免 N+1）
        List<TeachingPlanObjectiveRef> allRefs =
                teachingPlanObjectiveRefMapper.selectByPlanAndScheme(planId, schemeId);
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
        // 删目标时同步逻辑删除其支撑毕业要求，避免孤儿绑定
        if (id != null) {
            teachingPlanObjectiveRefMapper.deleteByObjectiveId(id);
        }
        teachingPlanObjectiveMapper.deleteById(id);
    }

    // ============ 8. 课程绑定毕业要求（候选列表） ============

    @Override
    public List<StandardGraduation> listCourseGraduation(Long courseId) {
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
        return list == null ? new ArrayList<>() : list;
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
            teachingPlanObjectiveRefMapper.deleteByObjectiveId(objective.getId());
        }
        insertObjectiveRefs(objective.getId(), objective.getPlanId(), objective.getSchemeId(), saveVo.getRefs());
        return objective.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveObjectiveRefs(TeachingPlanObjectiveRefSaveVo saveVo) {
        if (saveVo == null || saveVo.getObjectiveId() == null) {
            throw new IllegalArgumentException("objectiveId 不能为空");
        }
        Long objectiveId = saveVo.getObjectiveId();
        // 校验目标存在，并补全 planId/schemeId
        TeachingPlanObjective objective = teachingPlanObjectiveMapper.selectById(objectiveId);
        if (objective == null) {
            throw new IllegalArgumentException("教学目标不存在: " + objectiveId);
        }
        Long planId = saveVo.getPlanId() != null ? saveVo.getPlanId() : objective.getPlanId();
        Long schemeId = saveVo.getSchemeId() != null ? saveVo.getSchemeId() : objective.getSchemeId();

        // 重建绑定：先逻辑删除旧 ref，再按列表插入（空列表=清空）
        teachingPlanObjectiveRefMapper.deleteByObjectiveId(objectiveId);
        insertObjectiveRefs(objectiveId, planId, schemeId, saveVo.getRefs());
    }

    /**
     * 批量写入目标-毕业要求绑定；refs 可空表示不写。
     * 缺省 planId/schemeId/objectiveId 由参数回填；graduation 编码名称由前端快照传入。
     */
    private void insertObjectiveRefs(Long objectiveId, Long planId, Long schemeId,
                                     List<TeachingPlanObjectiveRef> refs) {
        if (ObjectUtils.isEmpty(refs)) {
            return;
        }
        int sort = 1;
        for (TeachingPlanObjectiveRef ref : refs) {
            if (ref == null) {
                continue;
            }
            ref.setId(null);
            ref.setObjectiveId(objectiveId);
            if (ref.getPlanId() == null) {
                ref.setPlanId(planId);
            }
            if (ref.getSchemeId() == null) {
                ref.setSchemeId(schemeId);
            }
            if (ref.getSort() == null) {
                ref.setSort(sort++);
            }
            // 默认绑定来源标记：课程毕业要求关联
            if (StringUtils.isBlank(ref.getGraduationBindSource())) {
                ref.setGraduationBindSource("course_ref_graduation");
            }
            UserUtils.reflash(ref);
            teachingPlanObjectiveRefMapper.insert(ref);
        }
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
     */
    @Override
    public List<TeachingPlanObjectiveOptionVo> listObjectiveOptions(Long planId, Long schemeId,
                                                                    String objectiveTypeCode) {
        if (planId == null) {
            return new ArrayList<>();
        }
        List<TeachingPlanObjective> list =
                teachingPlanObjectiveMapper.selectByPlanAndSchemeAndType(planId, schemeId, objectiveTypeCode);
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
