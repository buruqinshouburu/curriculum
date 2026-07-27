package com.doinner.csys.service.impl;

import com.doinner.csys.dao.CourseMapper;
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
import com.doinner.csys.dao.TeachingPlanSectionMapper;
import com.doinner.csys.dao.TeachingPlanTargetDesignMapper;
import com.doinner.csys.dao.TeachingPlanTeacherMapper;
import com.doinner.csys.dao.TeachingPlanTextbookMapper;
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
import com.doinner.csys.domain.vo.TeachingPlanImportIssueVo;
import com.doinner.csys.domain.vo.TeachingPlanImportResultVo;
import com.doinner.csys.domain.vo.TeachingPlanListVo;
import com.doinner.csys.domain.vo.TeachingPlanQueryVo;
import com.doinner.csys.domain.vo.TeachingPlanQuoteAggVo;
import com.doinner.csys.domain.vo.TeachingPlanSaveVo;
import com.doinner.csys.domain.vo.TeachingPlanSchemeVo;
import com.doinner.csys.domain.vo.CourseQuoteMajorVo;
import com.doinner.csys.entity.csys.CourseTeachingPlanGenerator;
import com.doinner.csys.entity.csys.TeachingPlanWordImporter;
import com.doinner.csys.entity.csys.model.CourseTeachingPlanModel;
import com.doinner.csys.entity.csys.model.DictContent;
import com.doinner.csys.entity.csys.po.CourseKnowledgeUnit;
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
import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
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

    private static final ObjectMapper IMPORT_OBJECT_MAPPER = new ObjectMapper();

    /** 教学环节字典 type */
    private static final String DICT_PLAN_TEACHING_LINK = "sys_plan_teaching_link";
    /** 教法字典 type */
    private static final String DICT_PLAN_TEACHING_METHOD = "sys_plan_teaching_method";
    /** 学法字典 type */
    private static final String DICT_PLAN_LEARNING_METHOD = "sys_plan_learning_method";
    /** 适用对象/培养层次字典 type */
    private static final String DICT_EDUCATION_LEVEL = "sys_education_level";
    /** 考核项目字典 type */
    private static final String DICT_ASSESSMENT_ITEM = "sys_assessment_item";
    /** 考核方式字典 type */
    private static final String DICT_ASSESSMENT_METHOD = "sys_assessment_method";
    /** 评定机制字典 type */
    private static final String DICT_ASSESSMENT_MECHANISM = "sys_assessment_mechanism";
    /** 评价标准字典 type */
    private static final String DICT_EVALUATION_STANDARD = "sys_evaluation_standard";
    /** 教材性质字典 type */
    private static final String DICT_TEXTBOOK_NATURE = "sys_textbook_nature";
    /** 出版方式字典 type */
    private static final String DICT_PUBLICATION_METHOD = "sys_publication_method";
    /** 条件类型字典 type */
    private static final String DICT_CONDITION_TYPE = "sys_condition_type";

    @Resource
    private TeachingPlanMapper teachingPlanMapper;
    @Resource
    private TeachingPlanTeacherMapper teachingPlanTeacherMapper;

    @Resource
    private TeachingPlanSectionMapper teachingPlanSectionMapper;

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
            // 适用对象：被引用培养方案 education_level 多值顿号拼接，字典 sys_education_level 译为 label
            detail.setEducationLevel(translateDictJoined(
                    detail.getEducationLevel(), dictValueToLabelMap(DICT_EDUCATION_LEVEL)));
            // 课程模块：被引用课程 course_Module 多值顿号拼接，KG 字典 id 译为 name；未命中保留原值
            String rawModule = detail.getCourseModule();
            String moduleName = translateJoinedCodes(rawModule, getCourseModuleIdToNameMap());
            detail.setCourseModule(StringUtils.isNotBlank(moduleName) ? moduleName : rawModule);
            // 公共基础标记：按源课 course_Module 原 id 判定（detail.courseModule 已是名称）
            detail.setPublicFoundation(isPublicFoundationCourseModule(
                    courseId != null ? courseId : detail.getCourseId()));
        }
        return detail;
    }

    /**
     * 源课是否公共基础课程：t_csys_course.course_Module == DictContent.GENERAL_EDUCATION_COURSES_SCHEDULE。
     */
    private boolean isPublicFoundationCourseModule(Long sourceCourseId) {
        if (sourceCourseId == null) {
            return false;
        }
        CourseVo course = courseMapper.selectCourseById(sourceCourseId);
        if (course == null || StringUtils.isBlank(course.getCourseModule())) {
            return false;
        }
        return Objects.equals(course.getCourseModule(), DictContent.GENERAL_EDUCATION_COURSES_SCHEDULE);
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
        if (plan == null){
            throw new IllegalArgumentException("教学计划不存在,请先保存后再生成!" );
        }
        Long planId =  plan.getId();
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
        // 适用对象：detail 已译 label；若回退到 course 原值则再译一次（多值顿号分隔）
        String rawEducationLevel = nz(detail == null ? null : detail.getEducationLevel(), course.getEducationLevel());
        m.setEducationLevel(translateDictJoined(rawEducationLevel, dictValueToLabelMap(DICT_EDUCATION_LEVEL)));
        m.setMajorName(nz(detail == null ? null : detail.getMajorName(), course.getMajorName()));
        m.setTerm(nz(detail == null ? null : detail.getTerm(), course.getOpenTerm()));
        // 课程模块：detail 已译 name；若仍是 id 串则再译（多值、/, 分隔），未命中保留原值
        String rawCourseModule = nz(detail == null ? null : detail.getCourseModule(), course.getCourseModule());
        String courseModuleName = translateJoinedCodes(rawCourseModule, getCourseModuleIdToNameMap());
        m.setCourseModule(StringUtils.isNotBlank(courseModuleName) ? courseModuleName : rawCourseModule);
        m.setCourseAttr(nz(detail == null ? null : detail.getCourseAttr(), course.getCourseAttr()));
        m.setScoreRule(plan == null ? null : plan.getScoreRule());

        if (planId != null) {
            m.setTeachers(listTeacher(planId));
            m.setSections(listSection(planId));

            // 按培养方案分组加载目标 + 支撑毕业要求（多方案多表）
            // 公共基础：plan 级单组（scheme_id IS NULL），Word 只出一张表、无方案小标题
            List<CourseTeachingPlanModel.SchemeObjectiveGroup> groups = new ArrayList<>();
            boolean publicFoundation = isPublicFoundationCourseModule(course.getId())
                    || Objects.equals(course.getCourseModule(), DictContent.GENERAL_EDUCATION_COURSES_SCHEDULE);
            if (publicFoundation) {
                CourseTeachingPlanModel.SchemeObjectiveGroup g =
                        new CourseTeachingPlanModel.SchemeObjectiveGroup();
                // listObjective 内部已按公共基础 onlyNullScheme 取数
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
            } else if (ObjectUtils.isNotEmpty(schemes)) {
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
            // 目标达成设计：页面已按目标名称聚合多 scheme，落库为 plan 级一张表；
            // schemeId 传 null 不过滤，取全 plan 数据（listTargetDesign 已填充 knowledgePoints）
            List<TeachingPlanTargetDesign> designs = new ArrayList<>();
            designs.addAll(teachingPlanModuleService.listTargetDesign(planId, null, "知识目标"));
            designs.addAll(teachingPlanModuleService.listTargetDesign(planId, null, "能力目标"));
            designs.addAll(teachingPlanModuleService.listTargetDesign(planId, null, "素质目标"));
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
            // 考核/教材/条件：字典编码译为 label 再写入 Word
            List<TeachingPlanAssessment> assessments = teachingPlanModuleService.listAssessment(planId);
            translateAssessmentDictFields(assessments);
            m.setAssessments(assessments);
            List<TeachingPlanTextbook> textbooks = teachingPlanModuleService.listTextbook(planId);
            translateTextbookDictFields(textbooks);
            m.setTextbooks(textbooks);
            List<TeachingPlanCondition> conditions = teachingPlanModuleService.listCondition(planId);
            translateConditionDictFields(conditions);
            m.setConditions(conditions);
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

    /**
     * 考核评价表：考核项目/考核方式/评定机制/评价标准 字典编码译为 label。
     * 已是 label 或未命中字典时保留原文。
     */
    private void translateAssessmentDictFields(List<TeachingPlanAssessment> assessments) {
        if (ObjectUtils.isEmpty(assessments)) {
            return;
        }
        Map<String, String> itemMap = dictValueToLabelMap(DICT_ASSESSMENT_ITEM);
        Map<String, String> methodMap = dictValueToLabelMap(DICT_ASSESSMENT_METHOD);
        Map<String, String> mechanismMap = dictValueToLabelMap(DICT_ASSESSMENT_MECHANISM);
        Map<String, String> standardMap = dictValueToLabelMap(DICT_EVALUATION_STANDARD);
        for (TeachingPlanAssessment a : assessments) {
            if (a == null) {
                continue;
            }
            a.setAssessmentItem(translateDictJoined(a.getAssessmentItem(), itemMap));
            a.setMethod(translateDictJoined(a.getMethod(), methodMap));
            a.setMechanism(translateDictJoined(a.getMechanism(), mechanismMap));
            a.setStandard(translateDictJoined(a.getStandard(), standardMap));
        }
    }

    /**
     * 教材表：教材性质/出版方式 字典编码译为 label。
     * 已是 label 或未命中字典时保留原文。
     */
    private void translateTextbookDictFields(List<TeachingPlanTextbook> textbooks) {
        if (ObjectUtils.isEmpty(textbooks)) {
            return;
        }
        Map<String, String> natureMap = dictValueToLabelMap(DICT_TEXTBOOK_NATURE);
        Map<String, String> publishMap = dictValueToLabelMap(DICT_PUBLICATION_METHOD);
        for (TeachingPlanTextbook b : textbooks) {
            if (b == null) {
                continue;
            }
            b.setMaterialNature(translateDictJoined(b.getMaterialNature(), natureMap));
            b.setPublishMethod(translateDictJoined(b.getPublishMethod(), publishMap));
        }
    }

    /**
     * 条件保障表：条件类型 字典编码译为 label。
     * 已是 label 或未命中字典时保留原文。
     */
    private void translateConditionDictFields(List<TeachingPlanCondition> conditions) {
        if (ObjectUtils.isEmpty(conditions)) {
            return;
        }
        Map<String, String> typeMap = dictValueToLabelMap(DICT_CONDITION_TYPE);
        for (TeachingPlanCondition c : conditions) {
            if (c == null) {
                continue;
            }
            c.setConditionType(translateDictJoined(c.getConditionType(), typeMap));
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

    @Override
    @Transactional(rollbackFor = Exception.class)
    public TeachingPlanImportResultVo importWord(Long courseId, Integer planType, MultipartFile file) {
        if (courseId == null) {
            throw new IllegalArgumentException("courseId 不能为空");
        }
        if (planType == null || planType < TeachingPlanWordImporter.DOC_TYPE_COURSE
                || planType > TeachingPlanWordImporter.DOC_TYPE_PRACTICE_PROJECT) {
            throw new IllegalArgumentException("planType 必须为 1-4（1课程/2实践训练课目/3实验课程/4实践项目）");
        }
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Word 文件不能为空");
        }
        String originalName = file.getOriginalFilename();
        if (StringUtils.isNotBlank(originalName) && !originalName.toLowerCase().endsWith(".docx")) {
            throw new IllegalArgumentException("仅支持 .docx 文件");
        }
        CourseVo course = courseMapper.selectCourseById(courseId);
        if (course == null) {
            throw new IllegalArgumentException("课程不存在: " + courseId);
        }

        TeachingPlanWordImporter.ParseContext ctx = buildImportParseContext(courseId, course, planType);
        TeachingPlanWordImporter.ParseResult parsed;
        try (InputStream in = file.getInputStream()) {
            parsed = new TeachingPlanWordImporter().parse(in, ctx);
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            log.error("解析教学计划 Word 失败, courseId={}", courseId, e);
            throw new IllegalArgumentException("无法解析 Word 文件: " + e.getMessage());
        }

        // 类型以前端传入为准；严格按 (courseId, planType) 定位，不再回退其他类型的计划
        TeachingPlan existing = teachingPlanMapper.selectBySourceCourseIdAndPlanType(courseId, planType);
        if (existing != null && existing.getStatus() != null
                && (existing.getStatus() == 1 || existing.getStatus() == 2)) {
            throw new IllegalArgumentException("教学计划审核中或已通过，无法覆盖导入");
        }
        boolean created = false;
        Long planId;
        if (existing == null) {
            TeachingPlan plan = new TeachingPlan();
            plan.setSourceCourseId(courseId);
            plan.setPlanType(planType);
            plan.setStatus(0);
            plan.setCurrentFlag(1);
            plan.setSourceCourseName(course.getName());
            plan.setSourceCourseCode(course.getCode());
            plan.setSourceCourseEnName(StringUtils.isNotBlank(parsed.courseEnName)
                    ? parsed.courseEnName : course.getEnName());
            if (course.getHours() != null) {
                plan.setSourceHours(BigDecimal.valueOf(course.getHours()));
            }
            if (course.getTeachHours() != null) {
                plan.setSourceTeachHours(BigDecimal.valueOf(course.getTeachHours()));
            }
            if (course.getPracticeHours() != null) {
                plan.setSourcePracticeHours(BigDecimal.valueOf(course.getPracticeHours()));
            }
            if (course.getCredit() != null) {
                plan.setSourceCredit(BigDecimal.valueOf(course.getCredit()));
            }
            plan.setScoreRule(parsed.scoreRule);
            TeachingPlanSaveVo saveVo = new TeachingPlanSaveVo();
            saveVo.setPlan(plan);
            planId = saveTeachingPlan(saveVo);
            created = true;
        } else {
            planId = existing.getId();
            clearPlanModules(planId);
            TeachingPlan upd = new TeachingPlan();
            upd.setId(planId);
            if (StringUtils.isNotBlank(parsed.courseEnName)) {
                upd.setSourceCourseEnName(parsed.courseEnName);
            }
            if (parsed.scoreRule != null) {
                upd.setScoreRule(parsed.scoreRule);
            }
            UserUtils.reflash(upd);
            teachingPlanMapper.updateById(upd);
        }

        TeachingPlanImportResultVo result = new TeachingPlanImportResultVo();
        result.setCourseId(courseId);
        result.setPlanId(planId);
        result.setDocType(planType);
        result.setCreatedPlan(created);
        if (parsed.issues != null) {
            result.getIssues().addAll(parsed.issues);
        }

        persistImportData(planId, courseId, ctx.publicFoundation, parsed, result);
        if (ObjectUtils.isNotEmpty(result.getIssues())) {
            log.warn("教学计划 Word 导入完成但存在 {} 条问题, courseId={}, planId={}, planType={}",
                    result.getIssues().size(), courseId, planId, planType);
        }
        return result;
    }

    private TeachingPlanWordImporter.ParseContext buildImportParseContext(Long courseId, CourseVo course,
                                                                          Integer planType) {
        TeachingPlanWordImporter.ParseContext ctx = new TeachingPlanWordImporter.ParseContext();
        // 类型由前端传入，Word 识别仅用于对照 WARN
        ctx.expectedDocType = planType;
        ctx.publicFoundation = Objects.equals(course.getCourseModule(),
                DictContent.GENERAL_EDUCATION_COURSES_SCHEDULE);
        List<TeachingPlanSchemeVo> schemes = teachingPlanModuleService.listSchemes(courseId);
        ctx.schemes = schemes == null ? new ArrayList<>() : schemes;
        List<CourseKnowledgeUnit> units = teachingPlanModuleService.listKnowledgeUnitInit(courseId);
        ctx.knowledgeUnits = units == null ? new ArrayList<>() : units;

        String[] dictTypes = {
                "sys_plan_target_type",
                DICT_PLAN_TEACHING_LINK, DICT_PLAN_TEACHING_METHOD, DICT_PLAN_LEARNING_METHOD,
                DICT_ASSESSMENT_ITEM, DICT_ASSESSMENT_METHOD, DICT_ASSESSMENT_MECHANISM, DICT_EVALUATION_STANDARD,
                DICT_TEXTBOOK_NATURE, DICT_PUBLICATION_METHOD, DICT_CONDITION_TYPE
        };
        for (String type : dictTypes) {
            List<SysDictData> list = CurDictUtils.getDictData(type);
            if (ObjectUtils.isEmpty(list)) {
                continue;
            }
            for (SysDictData d : list) {
                if (d == null || StringUtils.isBlank(d.getDictLabel())) {
                    continue;
                }
                TeachingPlanWordImporter.putDict(ctx.dictLabelToValue, type, d.getDictLabel(), d.getDictValue());
            }
        }
        return ctx;
    }

    /** 覆盖导入：逻辑删除 plan 下各子模块（明细表物理删） */
    private void clearPlanModules(Long planId) {
        teachingPlanPracticeItemDetailMapper.deleteByPlanId(planId);
        teachingPlanObjectiveRefMapper.deleteByPlanId(planId);
        teachingPlanObjectiveMapper.deleteByPlanId(planId);
        teachingPlanTeacherMapper.deleteByPlanId(planId);
        teachingPlanSectionMapper.deleteByPlanId(planId);
        teachingPlanContentMapper.deleteByPlanId(planId);
        teachingPlanTargetDesignMapper.deleteByPlanId(planId);
        teachingPlanPracticeItemMapper.deleteByPlanId(planId);
        teachingPlanAssessmentMapper.deleteByPlanId(planId);
        teachingPlanTextbookMapper.deleteByPlanId(planId);
        teachingPlanConditionMapper.deleteByPlanId(planId);
        teachingPlanProcessStepMapper.deleteByPlanId(planId);
        teachingPlanRefMapper.deleteByPlanId(planId);
    }

    private void persistImportData(Long planId, Long courseId, boolean publicFoundation,
                                   TeachingPlanWordImporter.ParseResult parsed,
                                   TeachingPlanImportResultVo result) {
        // 教员
        if (ObjectUtils.isNotEmpty(parsed.teachers)) {
            for (TeachingPlanTeacher t : parsed.teachers) {
                t.setId(null);
                t.setPlanId(planId);
                prepareEntity(t);
                teachingPlanTeacherMapper.insert(t);
            }
            result.addCount("teacher", parsed.teachers.size());
        }
        // 章节
        if (ObjectUtils.isNotEmpty(parsed.sections)) {
            for (TeachingPlanSection s : parsed.sections) {
                s.setId(null);
                s.setPlanId(planId);
                prepareEntity(s);
                teachingPlanSectionMapper.insert(s);
            }
            result.addCount("section", parsed.sections.size());
        }
        // 目标 + 支撑毕业要求
        int objCount = 0;
        int refCount = 0;
        if (ObjectUtils.isNotEmpty(parsed.objectives)) {
            Map<Long, Map<String, List<StandardGraduation>>> schemeGradCache = new HashMap<>();
            for (TeachingPlanWordImporter.ParsedObjective po : parsed.objectives) {
                if (po == null || po.objective == null || StringUtils.isBlank(po.objective.getContent())) {
                    continue;
                }
                TeachingPlanObjective o = po.objective;
                o.setId(null);
                o.setPlanId(planId);
                if (publicFoundation) {
                    o.setSchemeId(null);
                }
                prepareEntity(o);
                teachingPlanObjectiveMapper.insert(o);
                objCount++;
                if (ObjectUtils.isEmpty(po.graduationNames) || o.getId() == null) {
                    continue;
                }
                Long schemeId = o.getSchemeId();
                // 候选毕业要求与页面绑定弹框同源：listCourseGraduationByScheme
                Map<String, List<StandardGraduation>> nameMap = schemeGradCache.computeIfAbsent(
                        schemeId == null ? -1L : schemeId,
                        k -> buildGraduationNameMap(courseId, schemeId));
                // 整串优先：名称本身含「、，；」时单元格原文可整体命中一条，避免被拆碎后全部失配
                List<String> names = po.graduationNames;
                if (StringUtils.isNotBlank(po.graduationRaw)
                        && lookupGraduations(nameMap, po.graduationRaw) != null) {
                    names = Collections.singletonList(po.graduationRaw);
                }
                int sort = 1;
                Set<Long> boundGradIds = new HashSet<>();
                for (String gName : names) {
                    List<StandardGraduation> matches = lookupGraduations(nameMap, gName);
                    if (ObjectUtils.isEmpty(matches)) {
                        result.getIssues().add(TeachingPlanImportIssueVo.warn(
                                "四、课程目标与支撑毕业要求", o.getContent(), "graduationName",
                                "未匹配到毕业要求，已跳过: " + gName));
                        continue;
                    }
                    // 同名多条：默认绑定第一条，记日志并返回提示
                    StandardGraduation g = matches.get(0);
                    if (matches.size() > 1) {
                        log.warn("教学计划导入毕业要求同名多条, planId={}, objectiveId={}, name={}, 命中{}条, 默认绑定第一条 id={}",
                                planId, o.getId(), gName, matches.size(), g.getId());
                        result.getIssues().add(TeachingPlanImportIssueVo.warn(
                                "四、课程目标与支撑毕业要求", o.getContent(), "graduationName",
                                "毕业要求同名存在 " + matches.size() + " 条，默认绑定第一条: " + gName));
                    }
                    // 同一目标重复绑定去重
                    if (g.getId() != null && !boundGradIds.add(g.getId())) {
                        continue;
                    }
                    TeachingPlanObjectiveRef ref = new TeachingPlanObjectiveRef();
                    ref.setPlanId(planId);
                    ref.setObjectiveId(o.getId());
                    ref.setSchemeId(schemeId);
                    ref.setGraduationId(g.getId());
                    ref.setSourceGraduationId(g.getSourceId());
                    ref.setGraduationCode(g.getCode());
                    ref.setGraduationName(g.getName());
                    ref.setGraduationBindSource("course_ref_graduation");
                    ref.setSort(sort++);
                    prepareEntity(ref);
                    teachingPlanObjectiveRefMapper.insert(ref);
                    refCount++;
                }
            }
        }
        result.addCount("objective", objCount);
        result.addCount("objectiveRef", refCount);

        // 教学内容
        if (ObjectUtils.isNotEmpty(parsed.contents)) {
            for (TeachingPlanContent c : parsed.contents) {
                c.setId(null);
                c.setPlanId(planId);
                prepareEntity(c);
                teachingPlanContentMapper.insert(c);
            }
            result.addCount("content", parsed.contents.size());
        }
        // 达成设计
        if (ObjectUtils.isNotEmpty(parsed.targetDesigns)) {
            for (TeachingPlanTargetDesign d : parsed.targetDesigns) {
                d.setId(null);
                d.setPlanId(planId);
                if (ObjectUtils.isNotEmpty(d.getKnowledgePoints())) {
                    try {
                        d.setKnowledgePointsJson(IMPORT_OBJECT_MAPPER.writeValueAsString(d.getKnowledgePoints()));
                    } catch (Exception e) {
                        d.setKnowledgePointsJson("[]");
                        result.getIssues().add(TeachingPlanImportIssueVo.warn(
                                "六、达成设计", d.getObjectiveText(), "knowledgePoints",
                                "知识点 JSON 序列化失败，已置空"));
                    }
                }
                prepareEntity(d);
                teachingPlanTargetDesignMapper.insert(d);
            }
            result.addCount("targetDesign", parsed.targetDesigns.size());
        }
        // 实践项目 + 明细
        int itemCount = 0;
        int detailCount = 0;
        if (ObjectUtils.isNotEmpty(parsed.practiceItems)) {
            for (TeachingPlanWordImporter.ParsedPracticeItem pi : parsed.practiceItems) {
                if (pi == null || pi.item == null || StringUtils.isBlank(pi.item.getName())) {
                    continue;
                }
                TeachingPlanPracticeItem item = pi.item;
                item.setId(null);
                item.setPlanId(planId);
                prepareEntity(item);
                teachingPlanPracticeItemMapper.insert(item);
                itemCount++;
                if (ObjectUtils.isNotEmpty(pi.details) && item.getId() != null) {
                    for (TeachingPlanPracticeItemDetail det : pi.details) {
                        det.setId(null);
                        det.setItemId(item.getId());
                        teachingPlanPracticeItemDetailMapper.insert(det);
                        detailCount++;
                    }
                }
            }
        }
        result.addCount("practiceItem", itemCount);
        result.addCount("practiceItemDetail", detailCount);

        // 考核
        if (ObjectUtils.isNotEmpty(parsed.assessments)) {
            for (TeachingPlanAssessment a : parsed.assessments) {
                a.setId(null);
                a.setPlanId(planId);
                // 字典反查已在解析阶段尽量完成；此处再补 assessment 字段
                a.setAssessmentItem(reverseImportDict(a.getAssessmentItem(), DICT_ASSESSMENT_ITEM));
                a.setMethod(reverseImportDict(a.getMethod(), DICT_ASSESSMENT_METHOD));
                a.setMechanism(reverseImportDict(a.getMechanism(), DICT_ASSESSMENT_MECHANISM));
                a.setStandard(reverseImportDict(a.getStandard(), DICT_EVALUATION_STANDARD));
                prepareEntity(a);
                teachingPlanAssessmentMapper.insert(a);
            }
            result.addCount("assessment", parsed.assessments.size());
        }
        // 教材
        if (ObjectUtils.isNotEmpty(parsed.textbooks)) {
            for (TeachingPlanTextbook b : parsed.textbooks) {
                b.setId(null);
                b.setPlanId(planId);
                prepareEntity(b);
                teachingPlanTextbookMapper.insert(b);
            }
            result.addCount("textbook", parsed.textbooks.size());
        }
        // 条件
        if (ObjectUtils.isNotEmpty(parsed.conditions)) {
            for (TeachingPlanCondition c : parsed.conditions) {
                c.setId(null);
                c.setPlanId(planId);
                prepareEntity(c);
                teachingPlanConditionMapper.insert(c);
            }
            result.addCount("condition", parsed.conditions.size());
        }
    }

    /**
     * 候选毕业要求名称索引（与页面绑定弹框同源 listCourseGraduationByScheme）。
     * 同名保留全部候选，落库时默认取第一条并提示。
     */
    private Map<String, List<StandardGraduation>> buildGraduationNameMap(Long courseId, Long schemeId) {
        List<StandardGraduation> list = teachingPlanModuleService.listCourseGraduationByScheme(courseId, schemeId);
        Map<String, List<StandardGraduation>> map = new HashMap<>();
        if (ObjectUtils.isEmpty(list)) {
            return map;
        }
        for (StandardGraduation g : list) {
            if (g == null || StringUtils.isBlank(g.getName())) {
                continue;
            }
            String trimmed = g.getName().trim();
            String compact = g.getName().replaceAll("\\s+", "");
            map.computeIfAbsent(trimmed, k -> new ArrayList<>()).add(g);
            if (!compact.equals(trimmed)) {
                map.computeIfAbsent(compact, k -> new ArrayList<>()).add(g);
            }
        }
        return map;
    }

    /** 名称查候选：先原样，再忽略空白差异；查不到返回 null */
    private List<StandardGraduation> lookupGraduations(Map<String, List<StandardGraduation>> nameMap, String name) {
        if (StringUtils.isBlank(name)) {
            return null;
        }
        List<StandardGraduation> matches = nameMap.get(name.trim());
        if (ObjectUtils.isEmpty(matches)) {
            matches = nameMap.get(name.replaceAll("\\s+", ""));
        }
        return ObjectUtils.isEmpty(matches) ? null : matches;
    }

    private void prepareEntity(Object entity) {
        UserUtils.reflash(entity);
        try {
            java.lang.reflect.Field f = getSysflagField(entity.getClass());
            if (f != null) {
                f.setAccessible(true);
                if (f.get(entity) == null) {
                    f.set(entity, 0);
                }
            }
        } catch (Exception ignored) {
            // ignore
        }
    }

    private java.lang.reflect.Field getSysflagField(Class<?> clazz) {
        Class<?> c = clazz;
        while (c != null && c != Object.class) {
            try {
                return c.getDeclaredField("sysflag");
            } catch (NoSuchFieldException e) {
                c = c.getSuperclass();
            }
        }
        return null;
    }

    private String reverseImportDict(String label, String dictType) {
        if (StringUtils.isBlank(label)) {
            return label;
        }
        List<SysDictData> list = CurDictUtils.getDictData(dictType);
        if (ObjectUtils.isEmpty(list)) {
            return label;
        }
        for (SysDictData d : list) {
            if (d != null && label.equals(d.getDictLabel())) {
                return d.getDictValue();
            }
        }
        return label;
    }
}

