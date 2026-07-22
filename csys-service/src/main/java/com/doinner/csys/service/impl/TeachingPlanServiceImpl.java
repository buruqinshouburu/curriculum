package com.doinner.csys.service.impl;

import com.doinner.csys.dao.CourseMapper;
import com.doinner.csys.dao.TeachingPlanMapper;
import com.doinner.csys.dao.TeachingPlanSectionMapper;
import com.doinner.csys.dao.TeachingPlanTeacherMapper;
import com.doinner.csys.dao.StandardMajorMapper;
import com.doinner.csys.dao.TrainingSchemeCourseScheduleMapper;
import com.doinner.csys.domain.Course;
import com.doinner.csys.domain.StandardGraduation;
import com.doinner.csys.domain.StandardMajor;
import com.doinner.csys.domain.TeachingPlan;
import com.doinner.csys.domain.TeachingPlanAssessment;
import com.doinner.csys.domain.TeachingPlanCondition;
import com.doinner.csys.domain.TeachingPlanContent;
import com.doinner.csys.domain.TeachingPlanObjective;
import com.doinner.csys.domain.TeachingPlanObjectiveRef;
import com.doinner.csys.domain.TeachingPlanPracticeItem;
import com.doinner.csys.domain.TeachingPlanPracticeItemDetail;
import com.doinner.csys.domain.TeachingPlanSection;
import com.doinner.csys.domain.TeachingPlanTargetDesign;
import com.doinner.csys.domain.TeachingPlanTeacher;
import com.doinner.csys.domain.TeachingPlanTextbook;
import com.doinner.csys.domain.vo.CourseVo;
import com.doinner.csys.domain.vo.TeachingPlanDetailVo;
import com.doinner.csys.domain.vo.TeachingPlanListVo;
import com.doinner.csys.domain.vo.TeachingPlanQueryVo;
import com.doinner.csys.domain.vo.TeachingPlanQuoteAggVo;
import com.doinner.csys.domain.vo.TeachingPlanSaveVo;
import com.doinner.csys.domain.vo.TeachingPlanSchemeVo;
import com.doinner.csys.domain.vo.CourseQuoteMajorVo;
import com.doinner.csys.entity.csys.CourseTeachingPlanGenerator;
import com.doinner.csys.entity.csys.model.CourseTeachingPlanModel;
import com.doinner.csys.service.CommonService;
import com.doinner.csys.service.TeachingPlanModuleService;
import com.doinner.csys.service.TeachingPlanService;
import com.doinner.csys.utils.CurDictUtils;
import com.doinner.csys.utils.UserUtils;
import com.doinner.file.api.domain.FileInfo;
import com.doinner.file.api.domain.vo.FileInfoVO;
import com.doinner.file.api.service.RemoteFileInfoService;
import com.doinner.kg.domain.Dictionary;
import com.doinner.kg.service.RemoteKgService;
import com.doinner.system.domain.custom.CustomDept;
import com.doinner.system.domain.entity.SysDept;
import com.doinner.system.domain.entity.SysDictData;
import com.doinner.system.service.DoinnerDeptService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 课程教学计划 Service 实现。
 *
 * @author codex
 */
@Service
@Transactional(readOnly = true)
public class TeachingPlanServiceImpl implements TeachingPlanService {

    private static final Logger log = LoggerFactory.getLogger(TeachingPlanServiceImpl.class);

    /** 教学环节字典 type */
    private static final String DICT_PLAN_TEACHING_LINK = "sys_plan_teaching_link";
    /** 教法字典 type */
    private static final String DICT_PLAN_TEACHING_METHOD = "sys_plan_teaching_method";
    /** 学法字典 type */
    private static final String DICT_PLAN_LEARNING_METHOD = "sys_plan_learning_method";

    @Resource
    private TeachingPlanMapper teachingPlanMapper;
    @Resource
    private TeachingPlanTeacherMapper teachingPlanTeacherMapper;

    @Resource
    private TeachingPlanSectionMapper teachingPlanSectionMapper;

    @Resource
    private CourseMapper courseMapper;

    @Resource
    private TeachingPlanModuleService teachingPlanModuleService;

    @Resource
    private CommonService commonService;

    @Resource
    private RemoteFileInfoService remoteFileInfoService;

    @Autowired
    private RemoteKgService remoteKgService;

    @Resource
    private StandardMajorMapper standardMajorMapper;

    @Resource
    private TrainingSchemeCourseScheduleMapper trainingSchemeCourseScheduleMapper;

    @Resource
    private DoinnerDeptService doinnerDeptService;

    /** 课程模块字典类型(kg.dictionary.courseModuleType，与课程查询界面同源) */
    @Value("${kg.dictionary.courseModuleType:69a7f3162dc370362ef3ee6d}")
    private String kgCourseModuleType;

    /** 课程模块字典 id->name 缓存(单例, 首次远程加载) */
    private final Map<String, String> courseModuleDictionaryIdToNameMap = new HashMap<>();

    /** 专业 id->name 缓存(单例, 首次加载; 专业字典变更极少, 列表场景足够) */
    private volatile Map<Long, String> majorIdToNameCache;

    /** 部门 id->name 缓存(单例, 首次远程加载) */
    private volatile Map<Long, String> deptIdToNameCache;

    /** 教学计划生成文档文件分类ID（bootstrap.yml: category.TeachingPlan，未配置默认0） */
    @Value("${category.TeachingPlan:0}")
    private String teachingPlanCategoryId;

    /**
     * 开课学期字典 1-10：第一学年（秋）~第五学年（春）。
     * 与 t_csys_training_scheme_course_schedule.term 字典值对齐；下标 0 占位。
     */
    private static final String[] SCHEDULE_TERM_LABELS = {
            "",
            "第一学年（秋）", "第一学年（春）",
            "第二学年（秋）", "第二学年（春）",
            "第三学年（秋）", "第三学年（春）",
            "第四学年（秋）", "第四学年（春）",
            "第五学年（秋）", "第五学年（春）"
    };

    @Override
    public List<TeachingPlanListVo> selectTeachingPlanPage(TeachingPlanQueryVo query) {
        if (query == null) {
            query = new TeachingPlanQueryVo();
        }
        // 1) 主查询：总库课程 + left join 教学计划；被引用聚合字段不在 SQL 里做相关子查询
        List<TeachingPlanListVo> list = teachingPlanMapper.selectTeachingPlanPage(query);
        if (CollectionUtils.isEmpty(list)) {
            return list;
        }

        // 2) 仅对当前页 courseId 批量聚合被引用侧字段，覆盖主查询的总库回退值
        fillQuoteAggregate(list);

        // 3) 名称补全：课程模块(远程字典缓存)、适用专业/专业方向(本地专业缓存)、开课单位(部门远程缓存)
        Map<String, String> moduleMap = getCourseModuleIdToNameMap();
        Map<Long, String> majorIdToNameMap = getMajorIdToNameMap();
        Map<Long, String> deptIdNameMap = getDeptIdToNameMap();

        for (TeachingPlanListVo vo : list) {
            if (MapUtils.isNotEmpty(moduleMap)) {
                // courseModule 可能为被引用课程c2聚合的多值拼接串(、分隔),需逐个翻译为名称后重新拼接
                if (StringUtils.isNotBlank(vo.getCourseModule())) {
                    vo.setCourseModuleName(translateJoinedCodes(vo.getCourseModule(), moduleMap));
                }
                if (StringUtils.isNotBlank(vo.getCourseModuleChildren())) {
                    vo.setCourseModuleChildrenName(moduleMap.get(vo.getCourseModuleChildren()));
                }
            }
            // majorName 优先取被引用培养方案聚合; 无引用时按 majorId 兜底翻译
            if (StringUtils.isBlank(vo.getMajorName()) && vo.getMajorId() != null) {
                vo.setMajorName(majorIdToNameMap.get(vo.getMajorId()));
            }
            if (vo.getSubMajorId() != null) {
                vo.setSubMajorName(majorIdToNameMap.get(vo.getSubMajorId()));
            }
            // 开课单位：与 /course/list 一致，用 college_id 翻译部门名称
            if (vo.getCollegeId() != null) {
                vo.setCollegeName(deptIdNameMap.get(vo.getCollegeId()));
            }
        }
        return list;
    }

    /**
     * 对当前页列表批量回填被引用侧聚合字段。
     * 有引用则用聚合值覆盖；无引用保留主查询给出的总库课程自身字段。
     */
    private void fillQuoteAggregate(List<TeachingPlanListVo> list) {
        List<Long> courseIds = list.stream()
                .map(TeachingPlanListVo::getCourseId)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());
        if (CollectionUtils.isEmpty(courseIds)) {
            return;
        }
        List<TeachingPlanQuoteAggVo> aggList = teachingPlanMapper.selectQuoteAggByCourseIds(courseIds);
        if (CollectionUtils.isEmpty(aggList)) {
            return;
        }
        Map<Long, TeachingPlanQuoteAggVo> aggMap = aggList.stream()
                .filter(a -> a.getCourseId() != null)
                .collect(Collectors.toMap(TeachingPlanQuoteAggVo::getCourseId, a -> a, (a, b) -> a));
        for (TeachingPlanListVo vo : list) {
            TeachingPlanQuoteAggVo agg = aggMap.get(vo.getCourseId());
            if (agg == null) {
                continue;
            }
            if (StringUtils.isNotBlank(agg.getEducationLevel())) {
                vo.setEducationLevel(agg.getEducationLevel());
            }
            if (StringUtils.isNotBlank(agg.getMajorName())) {
                vo.setMajorName(agg.getMajorName());
            }
            if (StringUtils.isNotBlank(agg.getCourseAttr())) {
                vo.setCourseAttr(agg.getCourseAttr());
            }
            if (StringUtils.isNotBlank(agg.getCourseModule())) {
                vo.setCourseModule(agg.getCourseModule());
            }
        }
    }

    /** 课程模块字典 id->name(单例缓存, 首次远程加载, 与课程查询界面同源) */
    private Map<String, String> getCourseModuleIdToNameMap() {
        if (MapUtils.isEmpty(courseModuleDictionaryIdToNameMap)) {
            List<Dictionary> data = remoteKgService.findDictionaryByType(kgCourseModuleType).getData();
            if (CollectionUtils.isNotEmpty(data)) {
                for (Dictionary datum : data) {
                    courseModuleDictionaryIdToNameMap.put(datum.getId().toString(), datum.getName());
                }
            }
        }
        return courseModuleDictionaryIdToNameMap;
    }

    /** 专业 id->name(单例缓存, 首次本地加载) */
    private Map<Long, String> getMajorIdToNameMap() {
        Map<Long, String> local = majorIdToNameCache;
        if (local != null) {
            return local;
        }
        synchronized (this) {
            if (majorIdToNameCache == null) {
                List<StandardMajor> majors = standardMajorMapper.selectStandardMajorList(null);
                if (CollectionUtils.isEmpty(majors)) {
                    majorIdToNameCache = Collections.emptyMap();
                } else {
                    majorIdToNameCache = majors.stream()
                            .filter(m -> m.getId() != null)
                            .collect(Collectors.toMap(StandardMajor::getId, StandardMajor::getName, (a, b) -> a));
                }
            }
            return majorIdToNameCache;
        }
    }

    /** 部门 id->name(单例缓存, 首次远程加载) */
    private Map<Long, String> getDeptIdToNameMap() {
        Map<Long, String> local = deptIdToNameCache;
        if (local != null) {
            return local;
        }
        synchronized (this) {
            if (deptIdToNameCache == null) {
                List<SysDept> depts = doinnerDeptService.list(new CustomDept()).getData();
                if (CollectionUtils.isEmpty(depts)) {
                    deptIdToNameCache = Collections.emptyMap();
                } else {
                    deptIdToNameCache = depts.parallelStream()
                            .filter(d -> d.getDeptId() != null)
                            .collect(Collectors.toMap(SysDept::getDeptId, SysDept::getDeptName, (a, b) -> a));
                }
            }
            return deptIdToNameCache;
        }
    }

    /**
     * 将多值拼接的字典编码(、或,分隔)逐个翻译为名称后重新拼接。单值场景等价于直接查map。
     * 用于课程模块:SQL返回被引用课程c2聚合的多code串,需逐个翻译为名称再拼回展示。
     */
    private String translateJoinedCodes(String joinedCodes, Map<String, String> codeToName) {
        if (StringUtils.isBlank(joinedCodes) || MapUtils.isEmpty(codeToName)) {
            return null;
        }
        return Arrays.stream(joinedCodes.split("[、,]"))
                .map(String::trim)
                .filter(StringUtils::isNotBlank)
                .map(codeToName::get)
                .filter(StringUtils::isNotBlank)
                .collect(Collectors.joining("、"));
    }

    @Override
    public TeachingPlanDetailVo getDetail(Long courseId, Long teachingPlanId) {
        // 课程相关字段一律实时取 t_csys_course / 被引用侧聚合（含 enabledTerm=course.version）；plan 仅贡献 teachingPlanId/scoreRule
        TeachingPlanDetailVo detail = teachingPlanId != null
                ? teachingPlanMapper.selectDetailByPlanId(teachingPlanId)
                : teachingPlanMapper.selectDetailByCourseId(courseId);
        if (detail != null) {
            // 开课学期：SQL 返回 schedule.term 字典值拼接串（如 1、3、5），翻译为中文标签
            detail.setTerm(translateScheduleTerms(detail.getTerm()));
        }
        return detail;
    }

    /**
     * 将执行方案学期字典值拼接串翻译为中文标签。
     * 入参形如 "1、3、5"（GROUP_CONCAT 结果）或单值 "2"；
     * 无法解析为 1-10 的片段原样保留（兼容 open_term 文本回退）。
     */
    private String translateScheduleTerms(String raw) {
        if (StringUtils.isBlank(raw)) {
            return raw;
        }
        return Arrays.stream(raw.split("[、,]"))
                .map(String::trim)
                .filter(StringUtils::isNotBlank)
                .map(this::translateOneScheduleTerm)
                .filter(StringUtils::isNotBlank)
                .distinct()
                .collect(Collectors.joining("、"));
    }

    private String translateOneScheduleTerm(String code) {
        try {
            int n = Integer.parseInt(code);
            if (n >= 1 && n < SCHEDULE_TERM_LABELS.length) {
                return SCHEDULE_TERM_LABELS[n];
            }
        } catch (NumberFormatException ignore) {
            // 非数字：可能是 open_term 回退的中文文本，原样返回
        }
        return code;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long saveTeachingPlan(TeachingPlanSaveVo saveVo) {
        if (saveVo == null || saveVo.getPlan() == null) {
            throw new IllegalArgumentException("教学计划信息不能为空");
        }
        TeachingPlan plan = saveVo.getPlan();
        if (plan.getSourceCourseId() == null) {
            throw new IllegalArgumentException("总库课程id不能为空");
        }
        if (plan.getPlanType() == null) {
            throw new IllegalArgumentException("教学计划类型不能为空");
        }
        // 教学计划类型(plan_type)由前端传入,保留前端值,使单一课程可出现多类型教学计划。
        // 同一课程同一类型只能一条：应用层按 (source_course_id, plan_type) 查重，
        // 已存在则改为 update，避免前端未带 id 时重复 insert 触发唯一约束异常。
        // DB 唯一约束 uk_tp_source_course_plan_type 仍作为兜底。
        if (plan.getId() == null) {
            TeachingPlan existing = teachingPlanMapper.selectBySourceCourseIdAndPlanType(
                    plan.getSourceCourseId(), plan.getPlanType());
            if (existing != null) {
                plan.setId(existing.getId());
            }
        }
        UserUtils.reflash(plan);
        if (plan.getId() == null) {
            teachingPlanMapper.insert(plan);
        } else {
            teachingPlanMapper.updateById(plan);
        }

        // 不再写入 t_csys_teaching_plan_context；tab 使用培养方案 schemeId
        return plan.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public FileInfo generateTeachingPlanWord(Long courseId) {
        return generateTeachingPlanWord(courseId, null);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public FileInfo generateTeachingPlanWord(Long courseId, Long planIdParam) {
        CourseVo course = courseMapper.selectCourseById(courseId);
        if (course == null) {
            throw new IllegalArgumentException("课程不存在: " + courseId);
        }
        // 优先使用指定 planId；否则取该课程 current 一条
        TeachingPlan plan;
        if (planIdParam != null) {
            plan = teachingPlanMapper.selectById(planIdParam);
            if (plan == null) {
                throw new IllegalArgumentException("教学计划不存在: " + planIdParam);
            }
        } else {
            plan = teachingPlanMapper.selectBySourceCourseId(courseId);
        }
        Long planId = plan == null ? null : plan.getId();
        // 源课被引用的全部培养方案（多方案 → Word 中「课程目标与支撑毕业要求」多张表）
        List<TeachingPlanSchemeVo> schemes = teachingPlanModuleService.listSchemes(courseId);
        if (schemes == null) {
            schemes = new ArrayList<>();
        }
        TeachingPlanDetailVo detail = (planId != null)
                ? teachingPlanMapper.selectDetailByPlanId(planId)
                : teachingPlanMapper.selectDetailByCourseId(courseId);

        CourseTeachingPlanModel model = buildModel(course, plan, detail, planId, schemes);

        try {
            InputStream stream = new CourseTeachingPlanGenerator().generate(model);
            String oldFileId = courseMapper.selectPlanFileId(courseId);
            if (StringUtils.isNotBlank(oldFileId)) {
                try {
                    FileInfo oldInfo = remoteFileInfoService.getFileInfo(oldFileId).getData();
                    if (oldInfo != null && oldInfo.getId() != null) {
                        remoteFileInfoService.delete(oldInfo.getId().toString());
                    }
                } catch (Exception e) {
                    log.warn("清理旧课程教学计划文档失败, courseId={}, err={}", courseId, e.getMessage());
                }
            }
            String fileName = nz(course.getName(), "课程") + "教学计划.docx";
            String fileId = commonService.uploadFile(stream, fileName, teachingPlanCategoryId);
            FileInfo info = fetchFileInfo(fileId);
            String storeId = (info.getId() != null) ? info.getId().toString() : fileId;
            courseMapper.updatePlanFileById(storeId, fileName, info.getDownloadUrl(), info.getPreviewUrl(), courseId);
            // 同步回写教学计划表文件字段（有计划时）
            if (plan != null && plan.getId() != null) {
                plan.setFileId(storeId);
                plan.setFileName(fileName);
                plan.setDownloadUrl(info.getDownloadUrl());
                plan.setPreviewUrl(info.getPreviewUrl());
                UserUtils.reflash(plan);
                teachingPlanMapper.updateById(plan);
            }
            return info;
        } catch (IOException e) {
            throw new RuntimeException("生成课程教学计划文档失败: " + e.getMessage(), e);
        }
    }

    /** 组装生成模型：基本信息取自 detail，回退取自 course；模块按 planId 加载；目标按全部 scheme 分组 */
    private CourseTeachingPlanModel buildModel(CourseVo course, TeachingPlan plan, TeachingPlanDetailVo detail,
                                               Long planId, List<TeachingPlanSchemeVo> schemes) {
        CourseTeachingPlanModel m = new CourseTeachingPlanModel();
        m.setDocType(mapDocType(course.getType()));
        m.setCourseName(nz(detail == null ? null : detail.getCourseName(), course.getName()));
        m.setCourseCode(nz(detail == null ? null : detail.getCourseCode(), course.getCode()));
        m.setCourseEnName(nz(detail == null ? null : detail.getCourseEnName(), course.getEnName()));
        // 启用时间：与详情接口一致，取课程 version，不再用 plan.enabled_term
        m.setEnabledTerm(nz(detail == null ? null : detail.getEnabledTerm(), course.getVersion()));
        m.setTeachHours(toStr(detail == null ? null : detail.getTeachHours(), course.getTeachHours()));
        m.setPracticeHours(toStr(detail == null ? null : detail.getPracticeHours(), course.getPracticeHours()));
        m.setHours(toStr(detail == null ? null : detail.getHours(), course.getHours()));
        m.setCredit(toStr(detail == null ? null : detail.getCredit(), course.getCredit()));
        m.setEducationLevel(nz(detail == null ? null : detail.getEducationLevel(), course.getEducationLevel()));
        m.setMajorName(nz(detail == null ? null : detail.getMajorName(), course.getMajorName()));
        m.setTerm(nz(detail == null ? null : detail.getTerm(), course.getOpenTerm()));
        // 课程模块字段存的是字典id（可能多值用、或,拼接），生成文档时翻译为名称
        String rawCourseModule = nz(detail == null ? null : detail.getCourseModule(), course.getCourseModule());
        String courseModuleName = translateJoinedCodes(rawCourseModule, getCourseModuleIdToNameMap());
        m.setCourseModule(StringUtils.isNotBlank(courseModuleName) ? courseModuleName : rawCourseModule);
        m.setCourseAttr(nz(detail == null ? null : detail.getCourseAttr(), course.getCourseAttr()));
        m.setScoreRule(plan == null ? null : plan.getScoreRule());

        // 目标达成设计仍用首个 scheme（与历史行为一致；本节「目标与支撑毕业要求」已按全部 scheme 展开）
        Long firstSchemeId = ObjectUtils.isNotEmpty(schemes) ? schemes.get(0).getSchemeId() : null;

        if (planId != null) {
            m.setTeachers(listTeacher(planId));
            m.setSections(listSection(planId));

            // 按培养方案分组加载目标 + 支撑毕业要求（多方案多表）
            List<CourseTeachingPlanModel.SchemeObjectiveGroup> groups = new ArrayList<>();
            if (ObjectUtils.isNotEmpty(schemes)) {
                for (TeachingPlanSchemeVo s : schemes) {
                    if (s == null || s.getSchemeId() == null) {
                        continue;
                    }
                    CourseTeachingPlanModel.SchemeObjectiveGroup g =
                            new CourseTeachingPlanModel.SchemeObjectiveGroup();
                    g.setSchemeId(s.getSchemeId());
                    g.setSchemeTitle(buildSchemeTitle(s));
                    List<TeachingPlanObjective> objectives =
                            teachingPlanModuleService.listObjective(planId, s.getSchemeId());
                    g.setObjectives(objectives == null ? new ArrayList<>() : objectives);
                    Map<Long, List<TeachingPlanObjectiveRef>> refMap = new HashMap<>();
                    if (ObjectUtils.isNotEmpty(objectives)) {
                        for (TeachingPlanObjective o : objectives) {
                            if (o == null || o.getId() == null) {
                                continue;
                            }
                            refMap.put(o.getId(), teachingPlanModuleService.listObjectiveRef(o.getId()));
                        }
                    }
                    g.setObjectiveRefMap(refMap);
                    groups.add(g);
                }
            } else {
                // 无培养方案 tab：按 planId 全量目标（schemeId=null）落一组，无小标题
                CourseTeachingPlanModel.SchemeObjectiveGroup g =
                        new CourseTeachingPlanModel.SchemeObjectiveGroup();
                List<TeachingPlanObjective> objectives =
                        teachingPlanModuleService.listObjective(planId, null);
                g.setObjectives(objectives == null ? new ArrayList<>() : objectives);
                Map<Long, List<TeachingPlanObjectiveRef>> refMap = new HashMap<>();
                if (ObjectUtils.isNotEmpty(objectives)) {
                    for (TeachingPlanObjective o : objectives) {
                        if (o == null || o.getId() == null) {
                            continue;
                        }
                        refMap.put(o.getId(), teachingPlanModuleService.listObjectiveRef(o.getId()));
                    }
                }
                g.setObjectiveRefMap(refMap);
                groups.add(g);
            }
            m.setSchemeObjectiveGroups(groups);
            // 兼容旧字段：保留首组，供 type2/3 等仍读 objectives 的逻辑使用
            if (!groups.isEmpty()) {
                m.setObjectives(groups.get(0).getObjectives());
                m.setObjectiveRefMap(groups.get(0).getObjectiveRefMap());
            } else {
                m.setObjectives(new ArrayList<>());
                m.setObjectiveRefMap(new HashMap<>());
            }

            m.setContents(teachingPlanModuleService.listContent(planId));
            // 目标达成设计：知识/能力/素质 三类合并（仍按首个 scheme；listTargetDesign 已填充 knowledgePoints）
            List<TeachingPlanTargetDesign> designs = new ArrayList<>();
            if (firstSchemeId != null) {
                designs.addAll(teachingPlanModuleService.listTargetDesign(planId, firstSchemeId, "知识目标"));
                designs.addAll(teachingPlanModuleService.listTargetDesign(planId, firstSchemeId, "能力目标"));
                designs.addAll(teachingPlanModuleService.listTargetDesign(planId, firstSchemeId, "素质目标"));
            }
            // 表内教学环节/教法/学法：字典编码译为 label（可多值顿号/逗号分隔）
            translateTargetDesignDictFields(designs);
            m.setTargetDesigns(designs);
            // 第六节「说明」三段：取字典全部 label 拼接
            fillSectionSixNotes(m);
            // 实验/实践项目 + 明细
            List<TeachingPlanPracticeItem> items = teachingPlanModuleService.listPracticeItem(planId);
            m.setPracticeItems(items);
            Map<Long, List<TeachingPlanPracticeItemDetail>> detailMap = new HashMap<>();
            if (ObjectUtils.isNotEmpty(items)) {
                for (TeachingPlanPracticeItem it : items) {
                    detailMap.put(it.getId(), teachingPlanModuleService.listPracticeItemDetail(it.getId()));
                }
            }
            m.setItemDetailMap(detailMap);
            m.setAssessments(teachingPlanModuleService.listAssessment(planId));
            m.setTextbooks(teachingPlanModuleService.listTextbook(planId));
            m.setConditions(teachingPlanModuleService.listCondition(planId));
        }
        // 课程绑定的毕业要求（全调用课汇总，空则回退源课）
        m.setCourseGraduations(teachingPlanModuleService.listCourseGraduation(course.getId()));
        // 无 plan 时仍填充第六节说明（字典），保证空文档结构完整
        if (StringUtils.isBlank(m.getTeachingLinkNote())) {
            fillSectionSixNotes(m);
        }
        return m;
    }

    /**
     * 第六节「说明」：教学环节 / 教法 / 学法 三段，
     * 内容取自字典表全部 label（按 dictSort 顺序顿号拼接）。
     */
    private void fillSectionSixNotes(CourseTeachingPlanModel m) {
        if (m == null) {
            return;
        }
        String links = joinDictLabels(DICT_PLAN_TEACHING_LINK);
        String methods = joinDictLabels(DICT_PLAN_TEACHING_METHOD);
        String learnings = joinDictLabels(DICT_PLAN_LEARNING_METHOD);
        m.setTeachingLinkNote(StringUtils.isBlank(links)
                ? null : "教学环节主要包括：" + links + "。");
        m.setTeachingMethodNote(StringUtils.isBlank(methods)
                ? null : "教法主要包括：" + methods + "。");
        m.setLearningMethodNote(StringUtils.isBlank(learnings)
                ? null : "学法主要包括：" + learnings + "。");
    }

    /**
     * 目标达成设计表中教学环节/教法/学法字段：将字典编码（可多值、、或,分隔）译为 label。
     * 已是 label 或未命中字典时保留原文。
     */
    private void translateTargetDesignDictFields(List<TeachingPlanTargetDesign> designs) {
        if (ObjectUtils.isEmpty(designs)) {
            return;
        }
        Map<String, String> linkMap = dictValueToLabelMap(DICT_PLAN_TEACHING_LINK);
        Map<String, String> methodMap = dictValueToLabelMap(DICT_PLAN_TEACHING_METHOD);
        Map<String, String> learningMap = dictValueToLabelMap(DICT_PLAN_LEARNING_METHOD);
        for (TeachingPlanTargetDesign d : designs) {
            if (d == null) {
                continue;
            }
            d.setTeachingLink(translateDictJoined(d.getTeachingLink(), linkMap));
            d.setTeachingMethod(translateDictJoined(d.getTeachingMethod(), methodMap));
            d.setLearningMethod(translateDictJoined(d.getLearningMethod(), learningMap));
        }
    }

    /** 字典 type → value:label 映射（保持字典顺序） */
    private Map<String, String> dictValueToLabelMap(String dictType) {
        List<SysDictData> list = CurDictUtils.getDictData(dictType);
        if (ObjectUtils.isEmpty(list)) {
            return Collections.emptyMap();
        }
        Map<String, String> map = new LinkedHashMap<>();
        for (SysDictData d : list) {
            if (d == null || StringUtils.isBlank(d.getDictValue())) {
                continue;
            }
            map.put(d.getDictValue(), StringUtils.defaultIfBlank(d.getDictLabel(), d.getDictValue()));
        }
        return map;
    }

    /** 字典 type 全部 label 按顺序顿号拼接 */
    private String joinDictLabels(String dictType) {
        List<SysDictData> list = CurDictUtils.getDictData(dictType);
        if (ObjectUtils.isEmpty(list)) {
            return "";
        }
        return list.stream()
                .filter(Objects::nonNull)
                .map(SysDictData::getDictLabel)
                .filter(StringUtils::isNotBlank)
                .collect(Collectors.joining("、"));
    }

    /**
     * 多值字典编码翻译：支持 、 与 , 分隔；命中 map 用 label，否则原样保留。
     */
    private String translateDictJoined(String raw, Map<String, String> valueToLabel) {
        if (StringUtils.isBlank(raw)) {
            return raw;
        }
        if (MapUtils.isEmpty(valueToLabel)) {
            return raw;
        }
        // 若整串已是某个 label，直接返回
        if (valueToLabel.containsValue(raw.trim())) {
            return raw.trim();
        }
        return Arrays.stream(raw.split("[、,]"))
                .map(String::trim)
                .filter(StringUtils::isNotBlank)
                .map(code -> {
                    String label = valueToLabel.get(code);
                    return StringUtils.isNotBlank(label) ? label : code;
                })
                .collect(Collectors.joining("、"));
    }

    /** 培养方案小标题：名称（版本） */
    private static String buildSchemeTitle(TeachingPlanSchemeVo s) {
        if (s == null) {
            return "";
        }
        String name = StringUtils.defaultString(s.getSchemeName());
        if (StringUtils.isNotBlank(s.getSchemeVersion())) {
            if (StringUtils.isNotBlank(name)) {
                return name + "（" + s.getSchemeVersion() + "）";
            }
            return s.getSchemeVersion();
        }
        return name;
    }

    private FileInfo fetchFileInfo(String fileId) {
        FileInfoVO vo = new FileInfoVO();
        vo.setFileId(fileId);
        List<FileInfo> infos = remoteFileInfoService.list(vo).getData();
        if (ObjectUtils.isNotEmpty(infos)) {
            return infos.get(0);
        }
        FileInfo info = new FileInfo();
        info.setFileId(fileId);
        return info;
    }

    /** t_csys_course.type -> 文档类型：1课程 2实践训练课目 3实验课程 4实践项目 */
    private Integer mapDocType(String type) {
        if (StringUtils.isBlank(type)) {
            return CourseTeachingPlanGenerator.DOC_TYPE_COURSE;
        }
        switch (type.trim()) {
            case "2":
                return CourseTeachingPlanGenerator.DOC_TYPE_PRACTICE_SUBJECT;
            case "3":
                return CourseTeachingPlanGenerator.DOC_TYPE_EXPERIMENT_COURSE;
            case "4":
                return CourseTeachingPlanGenerator.DOC_TYPE_PRACTICE_PROJECT;
            default:
                return CourseTeachingPlanGenerator.DOC_TYPE_COURSE;
        }
    }

    private static String nz(String first, String second) {
        return StringUtils.isNotBlank(first) ? first : (second == null ? "" : second);
    }

    private static String toStr(BigDecimal val, Double fallback) {
        if (val != null) {
            return val.stripTrailingZeros().toPlainString();
        }
        return fallback == null ? "" : fallback.toString();
    }

    // ============ 教员团队 ============

    @Override
    public List<TeachingPlanTeacher> listTeacher(Long planId) {
        return teachingPlanTeacherMapper.selectByPlanId(planId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long addTeacher(TeachingPlanTeacher teacher) {
        UserUtils.reflash(teacher);
        teachingPlanTeacherMapper.insert(teacher);
        return teacher.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateTeacher(TeachingPlanTeacher teacher) {
        UserUtils.reflash(teacher);
        teachingPlanTeacherMapper.updateById(teacher);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteTeacher(Long id) {
        teachingPlanTeacherMapper.deleteById(id);
    }

    // ============ 课程章节 ============

    @Override
    public List<TeachingPlanSection> listSection(Long planId) {
        return teachingPlanSectionMapper.selectByPlanId(planId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long addSection(TeachingPlanSection section) {
        UserUtils.reflash(section);
        teachingPlanSectionMapper.insert(section);
        return section.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateSection(TeachingPlanSection section) {
        UserUtils.reflash(section);
        teachingPlanSectionMapper.updateById(section);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteSection(Long id) {
        teachingPlanSectionMapper.deleteById(id);
    }

    @Override
    public List<CourseQuoteMajorVo> listQuoteMajors(Long courseId) {
        if (courseId == null) {
            return new ArrayList<>();
        }
        return trainingSchemeCourseScheduleMapper.selectQuoteMajorsBySourceCourseId(courseId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteTeachingPlan(Long planId) {
        if (planId == null) {
            throw new IllegalArgumentException("教学计划id不能为空");
        }
        TeachingPlan plan = teachingPlanMapper.selectById(planId);
        if (plan == null) {
            throw new IllegalArgumentException("教学计划不存在: " + planId);
        }
        // status: 0草稿 1审核中 2通过 3退回 9停用 —— 审核中/通过不允许删（与培养方案一致）
        if (plan.getStatus() != null && (plan.getStatus() == 1 || plan.getStatus() == 2)) {
            throw new RuntimeException("教学计划审核中或已通过，无法删除");
        }
        teachingPlanMapper.deleteById(planId);
    }
}

