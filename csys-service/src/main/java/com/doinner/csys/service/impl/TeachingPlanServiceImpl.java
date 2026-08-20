package com.doinner.csys.service.impl;

import com.doinner.csys.dao.CourseMapper;
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
import com.doinner.csys.dao.TeachingPlanTeacherMapper;
import com.doinner.csys.dao.TeachingPlanTextbookMapper;
import com.doinner.csys.dao.StandardMajorMapper;
import com.doinner.csys.dao.TrainingSchemeCourseScheduleMapper;
import com.doinner.csys.domain.Course;
import com.doinner.csys.domain.CourseSchedule;
import com.doinner.csys.domain.TeachingPlanContext;
import com.doinner.csys.domain.StandardGraduation;
import com.doinner.csys.domain.StandardMajor;
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
import com.doinner.csys.domain.TeachingPlanSection;
import com.doinner.csys.domain.TeachingPlanTargetDesign;
import com.doinner.csys.domain.TeachingPlanTaskBackground;
import com.doinner.csys.domain.TeachingPlanTaskBackgroundRef;
import com.doinner.csys.domain.TeachingPlanTrainingPurpose;
import com.doinner.csys.domain.TeachingPlanTrainingPurposeRef;
import com.doinner.csys.domain.TeachingPlanContentPurpose;
import com.doinner.csys.domain.TeachingPlanSupportObjective;
import com.doinner.csys.domain.TeachingPlanSupportContent;
import com.doinner.csys.domain.TeachingPlanTeacher;
import com.doinner.csys.domain.TeachingPlanTextbook;
import com.doinner.csys.domain.vo.CourseIdAndName;
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
import com.doinner.csys.domain.vo.TeachingPlanSupportCandidateItem;
import com.doinner.csys.domain.vo.TeachingPlanSupportCandidateVo;
import com.doinner.csys.domain.vo.TeachingPlanSupportContentSaveVo;
import com.doinner.csys.domain.vo.TeachingPlanSupportObjectiveSaveVo;
import com.doinner.csys.domain.vo.TeachingPlanSupportingAggVo;
import com.doinner.csys.domain.vo.TeachingPlanSupportingCourseVo;
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
import java.util.LinkedHashSet;
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
    /** 目标/达成设计类型字典 type（知识/能力/素质目标，dict_value 落 design_type_code） */
    private static final String DICT_PLAN_TARGET_TYPE = "sys_plan_target_type";
    /** 训练内容与时间安排模块字典 type（type2 第四部分「模块」列，dict_value 落 content.title） */
    private static final String DICT_PLAN_TRAINING_MODULE = "sys_plan_training_module";
    /** 组织实施“实施步骤”列字典 type（实践训练课目 plan_type=3；历史字段 stage_name 存该列的 dict_value） */
    private static final String DICT_PLAN_IMPLEMENTATION_STEP = "sys_plan_implementation_step";
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
    /** 实践项目成果类型：字典值保存到 outcome_type。 */
    private static final String DICT_PLAN_OUTCOME_TYPE = "sys_plan_outcome_type";
    /** 教材性质字典 type */
    private static final String DICT_TEXTBOOK_NATURE = "sys_textbook_nature";
    /** 出版方式字典 type */
    private static final String DICT_PUBLICATION_METHOD = "sys_publication_method";
    /** 条件类型字典 type */
    private static final String DICT_CONDITION_TYPE = "sys_condition_type";
    /** 开课学期字典 type */
    private static final String DICT_OPEN_SEMESTER = "cur_open_semester";
    /** 修读性质/课程属性字典 type */
    private static final String DICT_COURSE_ATTRIBUTE = "cur_course_attribute";
    /** 学年安排字典 type（第一学年…第五学年/贯穿四年/多学期安排） */
    private static final String DICT_SEMESTER_ARRANGE = "cur_semester_arrange";
    /** 学期安排季节字典 type（秋/春） */
    private static final String DICT_SEMESTER_ARRANGE_SEASON = "cur_semester_arrange_season";
    /** 时间安排单位字典 type（sys_course_unit：1=周 2=学时） */
    private static final String DICT_SYS_COURSE_UNIT = "sys_course_unit";
    /**
     * 教员职称字典 type 候选（线上可能命名不一，按顺序合并；命中即译）。
     * 未配置时保留原值，避免空字典把编码冲掉。
     */
    private static final String[] DICT_PROFESSIONAL_TITLE_CANDIDATES = {
            "sys_teacher_professional",
            "sys_teacher_professional_title",
            "sys_professional_title",
            "sys_teacher_title",
            "sys_plan_professional_title",
            "sys_job_title",
            "sys_user_title",
            "sys_title",
            "professional_title",
            "teacher_title",
            "sys_plan_title",
            "csys_professional_title",
            "user_professional_title"
    };
    /** 教员职责字典 type 候选 */
    private static final String[] DICT_TEACHER_DUTY_CANDIDATES = {
            "sys_teacher_duty",
            "sys_plan_teacher_duty",
            "sys_plan_duty",
            "sys_duty",
            "sys_teacher_responsibility",
            "teacher_duty",
            "plan_teacher_duty",
            "sys_responsibility"
    };

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
     * 开课学期字典 1-10：第一学年秋~第五学年春（统一无括号形式「第N学年秋/春」）。
     * 与 t_csys_training_scheme_course_schedule.term 字典值对齐；下标 0 占位。
     * term 奇数=秋、偶数=春：1第一学年秋,2第一学年春,3第二学年秋,…,10第五学年春。
     */
    private static final String[] SCHEDULE_TERM_LABELS = {
            "",
            "第一学年秋", "第一学年春",
            "第二学年秋", "第二学年春",
            "第三学年秋", "第三学年春",
            "第四学年秋", "第四学年春",
            "第五学年秋", "第五学年春"
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
            // 开课学期：优先 cur_open_semester 字典；未命中再回退 1-10 学期硬编码标签
            detail.setTerm(translateOpenSemester(detail.getTerm()));
            // 修读性质：cur_course_attribute
            detail.setCourseAttr(translateDictJoined(
                    detail.getCourseAttr(), dictValueToLabelMap(DICT_COURSE_ATTRIBUTE)));
            // 适用对象：一律取源课程 education_level（SQL 已改为取 course.education_level），字典 sys_education_level 译为 label
            detail.setEducationLevel(translateDictJoined(
                    detail.getEducationLevel(), dictValueToLabelMap(DICT_EDUCATION_LEVEL)));
            // 课程模块：被引用课程 course_Module 多值顿号拼接，KG 字典 id 译为 name；未命中保留原值
            String rawModule = detail.getCourseModule();
            String moduleName = translateJoinedCodes(rawModule, getCourseModuleIdToNameMap());
            detail.setCourseModule(StringUtils.isNotBlank(moduleName) ? moduleName : rawModule);
            // 源课程：用于公共基础判定 + 适用专业「通识通用」规则（适用对象已由 SQL 取源课程值）
            Long srcCourseId = courseId != null ? courseId : detail.getCourseId();
            CourseVo srcCourse = srcCourseId == null ? null : courseMapper.selectCourseById(srcCourseId);
            // 公共基础标记：按源课 course_Module 原 id 判定（detail.courseModule 已是名称）
            detail.setPublicFoundation(srcCourse != null
                    && isPublicFoundationCourseModuleValue(srcCourse.getCourseModule()));
            // 适用专业：普通课程(type1/3)聚合课程模块「全部」为公共基础、或实践训练课目(type2)课目模块(location)值∈{1,2,3,9}
            // 时固定「通识通用」；否则取聚合适用专业。rawModule 为 detail 聚合的被引用课程 c2 多值串(因不同方案引用模块可能不同)
            detail.setMajorName(resolveApplicableMajor(srcCourse, rawModule, detail.getMajorName()));
            // type=4 实践项目：补时间安排 + 支撑课程或实践训练科目名称字符串
            if (isPracticeProjectType(detail.getType())) {
                Long mainCourseId = courseId != null ? courseId : detail.getCourseId();
                detail.setTimeArrangement(resolveMainTimeArrangement(mainCourseId));
                detail.setSupportingCourses(flattenSupportingCourses(buildSupportingCourses(mainCourseId)));
            }
        }
        return detail;
    }

    /** 课程类型是否为实践项目(type=4) */
    private boolean isPracticeProjectType(String type) {
        return "4".equals(StringUtils.trimToEmpty(type));
    }

    /** 课程模块字段是否表示公共基础（等值、多值包含、或名称含「公共基础」）。 */
    private boolean isPublicFoundationCourseModuleValue(String courseModule) {
        if (StringUtils.isBlank(courseModule)) {
            return false;
        }
        String raw = courseModule.trim();
        String generalId = DictContent.GENERAL_EDUCATION_COURSES_SCHEDULE;
        if (Objects.equals(raw, generalId)) {
            return true;
        }
        // 多值：id 用 、/, 分隔
        for (String part : raw.split("[、,/，]")) {
            if (StringUtils.isNotBlank(part) && Objects.equals(part.trim(), generalId)) {
                return true;
            }
        }
        // 已译中文名时的兜底
        return raw.contains("公共基础");
    }

    /** 适用专业固定取「通识通用」的取值（普通课程仅公共基础 / 实践训练课目课目模块命中 1,2,3,9 时） */
    private static final String GENERAL_APPLICABLE_MAJOR = "通识通用";
    /** 实践训练课目(type2)课目模块(location, 字典 sys_subject_module)命中下列值之一时适用专业取「通识通用」 */
    private static final Set<String> SUBJECT_MODULE_GENERAL_VALUES =
            new HashSet<>(Arrays.asList("1", "2", "3", "9"));

    /**
     * 适用专业取值规则（基于 detail 聚合的课程模块多值串，因课程被不同培养方案引用模块可能不同）：
     * - 普通课程(type 1/3)：聚合课程模块「全部」为公共基础课 -> 「通识通用」
     * - 实践训练课目(type 2)：课目模块(location, 字典 sys_subject_module)值 ∈ {1,2,3,9} -> 「通识通用」
     * - 其余 -> 沿用聚合适用专业(被引用培养方案 major_id 聚合, 回退源课 major_id)
     *
     * @param detailCourseModuleMulti detail 查出的被引用课程 c2 聚合 course_Module 多值串(id 或中文均可)
     */
    private String resolveApplicableMajor(CourseVo sourceCourse, String detailCourseModuleMulti,
                                          String aggregatedMajorName) {
        if (sourceCourse == null) {
            return aggregatedMajorName;
        }
        String type = StringUtils.trimToEmpty(sourceCourse.getType());
        if ("1".equals(type) || "3".equals(type)) {
            if (isOnlyPublicFoundationCourseModule(detailCourseModuleMulti)) {
                return GENERAL_APPLICABLE_MAJOR;
            }
        } else if ("2".equals(type)) {
            String location = sourceCourse.getLocation();
            if (StringUtils.isNotBlank(location)
                    && SUBJECT_MODULE_GENERAL_VALUES.contains(location.trim())) {
                return GENERAL_APPLICABLE_MAJOR;
            }
        }
        return aggregatedMajorName;
    }

    /**
     * 课程模块是否只属于公共基础课：非空且拆分(、/,，)后每一项均为公共基础模块。
     * 兼容 id(hex) 与已译名称(含「公共基础」)两种存值形态。
     */
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

    /**
     * 开课学期翻译：统一为「第N学年秋/春」无括号形式。
     * 数字片段(1-10)走 {@link #SCHEDULE_TERM_LABELS}；非数字(open_term 文本回退)原样保留。
     * 不再让 cur_open_semester 字典覆盖数字值，保证教学计划学期安排格式统一。
     */
    private String translateOpenSemester(String raw) {
        if (StringUtils.isBlank(raw)) {
            return raw;
        }
        return translateScheduleTerms(raw);
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

    // ============================ type=4 实践项目：支撑课程/时间安排 ============================

    /**
     * 主课程(实践项目)时间安排：优先被调用课程 c2 的 (time_Week, unit) 对去重拼接；无被调用回退总库课程自身。
     * unit 字典 sys_course_unit 译中，形如「16周」。
     */
    private String resolveMainTimeArrangement(Long courseId) {
        if (courseId == null) {
            return null;
        }
        Map<String, String> unitMap = dictValueToLabelMap(DICT_SYS_COURSE_UNIT);
        List<CourseSchedule> pairs = teachingPlanMapper.selectSupportingTimePairs(Collections.singletonList(courseId));
        if (CollectionUtils.isNotEmpty(pairs)) {
            String joined = pairs.stream()
                    .map(p -> formatTimeWeekUnit(p.getTimeWeek(), p.getUnit(), unitMap))
                    .filter(StringUtils::isNotBlank)
                    .distinct()
                    .collect(Collectors.joining("、"));
            if (StringUtils.isNotBlank(joined)) {
                return joined;
            }
        }
        CourseVo self = courseMapper.selectCourseById(courseId);
        return self == null ? null : formatTimeWeekUnit(self.getTimeWeek(), self.getUnit(), unitMap);
    }

    /**
     * 组装支撑课程或实践训练科目列表(type=4)。
     * 源课 before_course_id(支撑课程,refType=1) + after_course_id(支撑训练课目,refType=2) 解析为列表；
     * 每条带回 学期安排/时间安排/修读性质，优先被调用课程 c2 多值拼接，无则回退自身。
     */
    private List<TeachingPlanSupportingCourseVo> buildSupportingCourses(Long sourceCourseId) {
        if (sourceCourseId == null) {
            return Collections.emptyList();
        }
        CourseVo source = courseMapper.selectCourseById(sourceCourseId);
        if (source == null) {
            return Collections.emptyList();
        }
        List<Long> beforeIds = parseCourseIdCsv(source.getBeforeCourseId());
        List<Long> afterIds = parseCourseIdCsv(source.getAfterCourseId());
        if (beforeIds.isEmpty() && afterIds.isEmpty()) {
            return Collections.emptyList();
        }
        // 去重 id 集合(保序)，供批量查询
        List<Long> allIds = new ArrayList<>(new LinkedHashSet<>(beforeIds));
        allIds.addAll(afterIds);
        List<Long> distinctIds = new ArrayList<>(new LinkedHashSet<>(allIds));

        Map<Long, String> nameMap = new HashMap<>();
        List<CourseIdAndName> idAndNames = courseMapper.selectCoursesIdAndNameByIds(distinctIds);
        if (CollectionUtils.isNotEmpty(idAndNames)) {
            for (CourseIdAndName cn : idAndNames) {
                if (cn != null && cn.getId() != null) {
                    nameMap.putIfAbsent(cn.getId(), cn.getName());
                }
            }
        }
        Map<Long, TeachingPlanSupportingAggVo> aggMap = toMapByCourseId(
                teachingPlanMapper.selectSupportingAgg(distinctIds));
        Map<Long, List<CourseSchedule>> timePairsMap = groupByCourseId(
                teachingPlanMapper.selectSupportingTimePairs(distinctIds));
        Map<Long, List<CourseSchedule>> refSchedMap = groupByCourseId(
                teachingPlanMapper.selectCourseRefScheduleBatch(distinctIds));
        Map<Long, Course> selfMap = new HashMap<>();
        List<Course> selfCourses = courseMapper.selectCoursesByIds(distinctIds);
        if (CollectionUtils.isNotEmpty(selfCourses)) {
            for (Course c : selfCourses) {
                if (c != null && c.getId() != null) {
                    selfMap.putIfAbsent(c.getId(), c);
                }
            }
        }

        Map<String, String> attrMap = dictValueToLabelMap(DICT_COURSE_ATTRIBUTE);
        Map<String, String> unitMap = dictValueToLabelMap(DICT_SYS_COURSE_UNIT);
        Map<String, String> semMap = dictValueToLabelMap(DICT_SEMESTER_ARRANGE);
        Map<String, String> seasonMap = dictValueToLabelMap(DICT_SEMESTER_ARRANGE_SEASON);

        List<TeachingPlanSupportingCourseVo> result = new ArrayList<>();
        for (Long id : beforeIds) {
            result.add(buildSupportingVo(id, 1, nameMap, aggMap, timePairsMap, refSchedMap, selfMap,
                    attrMap, unitMap, semMap, seasonMap));
        }
        for (Long id : afterIds) {
            result.add(buildSupportingVo(id, 2, nameMap, aggMap, timePairsMap, refSchedMap, selfMap,
                    attrMap, unitMap, semMap, seasonMap));
        }
        return result;
    }

    private TeachingPlanSupportingCourseVo buildSupportingVo(Long id, int refType,
                                                              Map<Long, String> nameMap,
                                                              Map<Long, TeachingPlanSupportingAggVo> aggMap,
                                                              Map<Long, List<CourseSchedule>> timePairsMap,
                                                              Map<Long, List<CourseSchedule>> refSchedMap,
                                                              Map<Long, Course> selfMap,
                                                              Map<String, String> attrMap,
                                                              Map<String, String> unitMap,
                                                              Map<String, String> semMap,
                                                              Map<String, String> seasonMap) {
        TeachingPlanSupportingCourseVo vo = new TeachingPlanSupportingCourseVo();
        vo.setCourseId(id);
        vo.setRefType(refType);
        vo.setCourseName(nameMap.get(id));

        TeachingPlanSupportingAggVo agg = aggMap.get(id);
        // 修读性质：c2 聚合回退自身(SQL 已 COALESCE，这里再兜底 self)
        String attrRaw = agg == null ? null : agg.getCourseAttrRaw();
        if (StringUtils.isBlank(attrRaw)) {
            Course self = selfMap.get(id);
            if (self != null) {
                attrRaw = self.getCourseAttr();
            }
        }
        vo.setCourseAttr(translateDictJoined(attrRaw, attrMap));

        // 学期安排：优先 tcs.term(1-10) 译「第N学年秋/春」；无被调用回退子表 semester_Schedule+spring_Autumn
        String termRaw = agg == null ? null : agg.getTermRaw();
        String term;
        if (StringUtils.isNotBlank(termRaw)) {
            term = translateScheduleTerms(termRaw);
        } else {
            term = buildTermFromRefSchedule(refSchedMap.get(id), semMap, seasonMap);
        }
        vo.setTerm(term);

        // 时间安排：c2 (time_Week,unit) 对去重拼接；无被调用回退自身
        vo.setTimeArrangement(buildTimeArrangement(id, timePairsMap, selfMap.get(id), unitMap));
        return vo;
    }

    /** 学期安排回退：课程子表 semester_Schedule(学年) + spring_Autumn(季节) 拼成「第N学年秋/春」，多行去重顿号。 */
    private String buildTermFromRefSchedule(List<CourseSchedule> rows, Map<String, String> semMap,
                                            Map<String, String> seasonMap) {
        if (CollectionUtils.isEmpty(rows)) {
            return null;
        }
        return rows.stream()
                .map(r -> formatSemesterSeason(
                        r == null ? null : r.getSemesterSchedule(),
                        r == null ? null : r.getSpringAutumn(), semMap, seasonMap))
                .filter(StringUtils::isNotBlank)
                .distinct()
                .collect(Collectors.joining("、"));
    }

    /** 学年+季节 -> 「第一学年春」；学年为非第X学年值(贯穿四年/多学期安排)时原样返回学年。 */
    private String formatSemesterSeason(String semesterCode, String seasonCode,
                                        Map<String, String> semMap, Map<String, String> seasonMap) {
        String year = dictLabel(semMap, semesterCode);
        String season = dictLabel(seasonMap, seasonCode);
        if (StringUtils.isBlank(year)) {
            return season;
        }
        if (StringUtils.isBlank(season)) {
            return year;
        }
        return year + season;
    }

    /** 时间安排：c2 对去重拼接；无 c2 回退自身 time_Week+unit。 */
    private String buildTimeArrangement(Long courseId, Map<Long, List<CourseSchedule>> timePairsMap,
                                        Course self, Map<String, String> unitMap) {
        List<CourseSchedule> pairs = timePairsMap == null ? null : timePairsMap.get(courseId);
        if (CollectionUtils.isNotEmpty(pairs)) {
            String joined = pairs.stream()
                    .map(p -> formatTimeWeekUnit(p.getTimeWeek(), p.getUnit(), unitMap))
                    .filter(StringUtils::isNotBlank)
                    .distinct()
                    .collect(Collectors.joining("、"));
            if (StringUtils.isNotBlank(joined)) {
                return joined;
            }
        }
        if (self == null) {
            return null;
        }
        return formatTimeWeekUnit(self.getTimeWeek(), self.getUnit(), unitMap);
    }

    /** time_Week + unit(字典译中) -> 「16周」。time_Week 去尾零；unit 未命中字典保留原值。 */
    private String formatTimeWeekUnit(Double timeWeek, String unitCode, Map<String, String> unitMap) {
        String tw = "";
        if (timeWeek != null) {
            try {
                tw = BigDecimal.valueOf(timeWeek).stripTrailingZeros().toPlainString();
            } catch (NumberFormatException ignore) {
                tw = String.valueOf(timeWeek);
            }
        }
        return tw + dictLabel(unitMap, unitCode);
    }

    /** 字典 value->label 查询，未命中返回原值(null 安全)。 */
    private String dictLabel(Map<String, String> valueToLabel, String code) {
        if (StringUtils.isBlank(code) || MapUtils.isEmpty(valueToLabel)) {
            return StringUtils.defaultString(code);
        }
        String label = valueToLabel.get(code);
        if (StringUtils.isNotBlank(label)) {
            return label;
        }
        return code;
    }

    /** 逗号分隔课程id串 -> Long 列表(过滤空白)。 */
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

    private Map<Long, TeachingPlanSupportingAggVo> toMapByCourseId(List<TeachingPlanSupportingAggVo> list) {
        Map<Long, TeachingPlanSupportingAggVo> map = new HashMap<>();
        if (CollectionUtils.isEmpty(list)) {
            return map;
        }
        for (TeachingPlanSupportingAggVo v : list) {
            if (v != null && v.getCourseId() != null) {
                map.putIfAbsent(v.getCourseId(), v);
            }
        }
        return map;
    }

    private Map<Long, List<CourseSchedule>> groupByCourseId(List<CourseSchedule> list) {
        Map<Long, List<CourseSchedule>> map = new LinkedHashMap<>();
        if (CollectionUtils.isEmpty(list)) {
            return map;
        }
        for (CourseSchedule s : list) {
            if (s == null || s.getCourseId() == null) {
                continue;
            }
            map.computeIfAbsent(s.getCourseId(), k -> new ArrayList<>()).add(s);
        }
        return map;
    }

    /**
     * 将支撑课程列表扁平化为 Word 单元格文本：
     * 「支撑课程：A、B；支撑训练课目：C」(无则对应部分省略；全空返回空串)。
     */
    private String flattenSupportingCourses(List<TeachingPlanSupportingCourseVo> list) {
        if (CollectionUtils.isEmpty(list)) {
            return "";
        }
        String supporting = list.stream()
                .filter(v -> v != null && v.getRefType() != null && v.getRefType() == 1)
                .map(TeachingPlanSupportingCourseVo::getCourseName)
                .filter(StringUtils::isNotBlank)
                .collect(Collectors.joining("、"));
        String training = list.stream()
                .filter(v -> v != null && v.getRefType() != null && v.getRefType() == 2)
                .map(TeachingPlanSupportingCourseVo::getCourseName)
                .filter(StringUtils::isNotBlank)
                .collect(Collectors.joining("、"));
        StringBuilder sb = new StringBuilder();
        if (StringUtils.isNotBlank(supporting)) {
            sb.append(supporting);
        }
        if (StringUtils.isNotBlank(training)) {
            if (sb.length() > 0) {
                sb.append("；");
            }
            sb.append(training);
        }
        return sb.toString();
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
        // type=4 实践项目：补时间安排 + 支撑课程名称字符串（buildModel 读取写入 Word 基本信息表）
        if (detail != null && isPracticeProjectType(detail.getType())) {
            detail.setTimeArrangement(resolveMainTimeArrangement(courseId));
            detail.setSupportingCourses(flattenSupportingCourses(buildSupportingCourses(courseId)));
        }

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
        m.setDocType(mapDocType(course.getType(),plan.getPlanType()));
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
        // 开课学期：detail 已译；回退 course.openTerm 时再译为「第N学年秋/春」
        String rawTerm = nz(detail == null ? null : detail.getTerm(), course.getOpenTerm());
        m.setTerm(translateOpenSemester(rawTerm));
        // 课程模块：detail 已译 name；若仍是 id 串则再译（多值、/, 分隔），未命中保留原值
        String rawCourseModule = nz(detail == null ? null : detail.getCourseModule(), course.getCourseModule());
        String courseModuleName = translateJoinedCodes(rawCourseModule, getCourseModuleIdToNameMap());
        m.setCourseModule(StringUtils.isNotBlank(courseModuleName) ? courseModuleName : rawCourseModule);
        // 适用专业：普通课程(type1/3)聚合课程模块「全部」为公共基础、或实践训练课目(type2)课目模块(location)值∈{1,2,3,9}
        // 时固定「通识通用」；否则取聚合适用专业（detail 聚合值回退 course 自身）。
        // rawCourseModule 为 detail 聚合的被引用课程 c2 多值串(因不同方案引用模块可能不同)
        String aggregatedMajor = nz(detail == null ? null : detail.getMajorName(), course.getMajorName());
        m.setMajorName(resolveApplicableMajor(course, rawCourseModule, aggregatedMajor));
        // 修读性质：detail 已译；回退 course 原值时再译 cur_course_attribute
        String rawCourseAttr = nz(detail == null ? null : detail.getCourseAttr(), course.getCourseAttr());
        m.setCourseAttr(translateDictJoined(rawCourseAttr, dictValueToLabelMap(DICT_COURSE_ATTRIBUTE)));
        m.setScoreRule(plan == null ? null : plan.getScoreRule());
        // 时间安排(type=4 基本信息表用)：detail 已取则用，否则回退 course.timeWeek+unit 译字典
        String timeArrangement = detail == null ? null : detail.getTimeArrangement();
        if (StringUtils.isBlank(timeArrangement)) {
            timeArrangement = formatTimeWeekUnit(course.getTimeWeek(), course.getUnit(),
                    dictValueToLabelMap(DICT_SYS_COURSE_UNIT));
        }
        m.setTimeArrangement(timeArrangement);
        // 支撑课程或实践训练课目(type=4 基本信息表 R5)：详情已组合为文本
        m.setSupportingCourses(detail == null ? null : detail.getSupportingCourses());

        if (planId != null) {
            List<TeachingPlanTeacher> teachers = listTeacher(planId);
            // 职称/职责：字典 value 译为名称后再写入 Word
            translateTeacherDictFields(teachers);
            m.setTeachers(teachers);
            m.setSections(listSection(planId));
            // 实施步骤/项目步骤（实践训练课目第五部分「实施步骤|阶段划分|有关要求」；type4 项目步骤数据行）
            m.setProcessSteps(teachingPlanModuleService.listProcessStep(planId));
            // 实践训练课目(plan_type=3)：stageName=实施步骤类别编码，stepName=阶段划分；生成 Word 前只翻译 stageName
            if (plan != null && plan.getPlanType() != null && plan.getPlanType() == 3) {
                Map<String, String> stepMap = dictValueToLabelMap(DICT_PLAN_IMPLEMENTATION_STEP);
                List<TeachingPlanProcessStep> steps = m.getProcessSteps();
                if (MapUtils.isNotEmpty(stepMap) && ObjectUtils.isNotEmpty(steps)) {
                    for (TeachingPlanProcessStep s : steps) {
                        if (s != null) {
                            s.setStageName(translateDictJoined(s.getStageName(), stepMap));
                        }
                    }
                }
            }

            // 第四节目标分组（数据驱动）：按目标实际 scheme_id 分布分组，不再按课程模块预判。
            // 只有 scheme_id 为空的目标 -> 单组单表(无小标题)；
            // 出现任一非空 scheme_id -> 每个 scheme_id 一张表(null 组标题「通用」，非 null 组带方案小标题)。
            List<CourseTeachingPlanModel.SchemeObjectiveGroup> groups =
                    buildSchemeObjectiveGroups(planId, schemes);
            m.setSchemeObjectiveGroups(groups);
            // 兼容旧字段：保留首组，供 type2/3 等仍读 objectives 的逻辑使用
            if (!groups.isEmpty()) {
                m.setObjectives(groups.get(0).getObjectives());
                m.setObjectiveRefMap(groups.get(0).getObjectiveRefMap());
            } else {
                m.setObjectives(new ArrayList<>());
                m.setObjectiveRefMap(new HashMap<>());
            }

            // 第三节任务背景分组（type=3 实验课程，对标第四节目标）：
            // 数据驱动，按任务背景实际 scheme_id 分布分组。只有 scheme_id 为空 -> 单组单表(无小标题)；
            // 出现任一非空 scheme_id -> 每个 scheme_id 一张表(null 组标题「通用」，非 null 组带方案小标题)。
            m.setSchemeTaskBackgroundGroups(buildSchemeTaskBackgroundGroups(planId, schemes));

            // 第二节训练目的分组（type=2 实践训练课目，对标第三节任务背景）：
            // 数据驱动，按训练目的实际 scheme_id 分布分组。只有 scheme_id 为空 -> 单组单表(无小标题)；
            // 通识通用（课目模块仅∈{1,2,3,9}）强制单组；否则含非空 scheme_id 时每个 scheme_id 一张表。
            m.setSchemeTrainingPurposeGroups(buildSchemeTrainingPurposeGroups(planId, schemes));

            List<TeachingPlanContent> contents = teachingPlanModuleService.listContent(planId);
            // 实践训练课目(plan_type=3)第四部分「模块」列：content.title 存字典 value(编码)，生成 Word 前译为 label
            if (plan != null && plan.getPlanType() != null && plan.getPlanType() == 3) {
                Map<String, String> moduleMap = dictValueToLabelMap(DICT_PLAN_TRAINING_MODULE);
                if (MapUtils.isNotEmpty(moduleMap) && ObjectUtils.isNotEmpty(contents)) {
                    for (TeachingPlanContent c : contents) {
                        if (c != null) {
                            c.setTitle(translateDictJoined(c.getTitle(), moduleMap));
                        }
                    }
                }
            }
            m.setContents(contents);
            // 实践项目第二节支撑绑定（type4）：支撑的课程目标/训练目的 + 知识体系/训练内容（计划级多选）
            m.setSupportObjectives(teachingPlanModuleService.listSupportObjective(planId));
            m.setSupportContents(teachingPlanModuleService.listSupportContent(planId));
            // 目标达成设计：design_type_code 现存字典 value，不再按中文 type 精确查询；
            // 一次取 plan 下全量，由生成器按 designTypeName(字典 label) 分流到知识/能力/素质三表
            List<TeachingPlanTargetDesign> designs =
                    teachingPlanModuleService.listTargetDesign(planId, null, null);
            // 表内教学环节/教法/学法 + 设计类型：字典编码译为 label（可多值顿号/逗号分隔）
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
            // 普通课程第八点新增目标达成考核表所需关联数据
            if (plan.getPlanType() != null && plan.getPlanType() == 1) {
                m.setObjectiveAssessments(teachingPlanModuleService.listObjectiveAssessment(planId, null));
            } else {
                m.setObjectiveAssessments(new ArrayList<>());
            }
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

    /** 教学计划对应源课是否公共基础（type1/3 按聚合 course_Module 判定，与适用专业口径一致）。 */
    private boolean isPublicFoundationPlan(Long planId) {
        if (planId == null) {
            return false;
        }
        TeachingPlan plan = teachingPlanMapper.selectById(planId);
        return plan != null
                && teachingPlanModuleService.isPublicFoundationCourse(plan.getSourceCourseId());
    }

    /** 教学计划对应源课（type2）是否属通识通用课目模块（聚合 location 均∈{1,2,3,9}）。 */
    private boolean isGeneralSubjectPlan(Long planId) {
        if (planId == null) {
            return false;
        }
        TeachingPlan plan = teachingPlanMapper.selectById(planId);
        return plan != null
                && teachingPlanModuleService.isGeneralSubjectModuleCourse(plan.getSourceCourseId());
    }

    /**
     * 第四节目标分组（数据驱动）：查 plan 下全部目标，按 scheme_id 分组。
     * - 无目标：返回一个空组，渲染空表结构。
     * - 公共基础（聚合 course_Module 全部为公共基础）：强制单组单表(无小标题)。
     * - 仅 scheme_id 为空的目标：单组单表(无小标题)。
     * - 含非空 scheme_id：每个 scheme_id 一组(多表)。null 组排最前(标题「通用」)，非 null 组按 schemes 列表顺序；
     *   schemes 未覆盖的 scheme_id 兜底追加(标题「培养方案 {id}」)。
     * 非公共基础时由目标数据 scheme_id 分布决定单组/多组。
     */
    private List<CourseTeachingPlanModel.SchemeObjectiveGroup> buildSchemeObjectiveGroups(
            Long planId, List<TeachingPlanSchemeVo> schemes) {
        List<CourseTeachingPlanModel.SchemeObjectiveGroup> groups = new ArrayList<>();
        if (planId == null) {
            return groups;
        }
        // onlyNullScheme=false + schemeId=null -> 不过滤 scheme，全量取 plan 下目标
        List<TeachingPlanObjective> all =
                teachingPlanObjectiveMapper.selectByPlanAndScheme(planId, null, false);
        if (ObjectUtils.isEmpty(all)) {
            CourseTeachingPlanModel.SchemeObjectiveGroup g =
                    new CourseTeachingPlanModel.SchemeObjectiveGroup();
            g.setObjectives(new ArrayList<>());
            g.setObjectiveRefMap(new HashMap<>());
            groups.add(g);
            return groups;
        }
        // 公共基础（聚合 course_Module 全部为公共基础）：强制单组单表，不按培养方案区分，
        // 兼容历史数据仍带 scheme_id 的场景（合并进单组，标题不显示）
        if (isPublicFoundationPlan(planId)) {
            CourseTeachingPlanModel.SchemeObjectiveGroup g =
                    new CourseTeachingPlanModel.SchemeObjectiveGroup();
            g.setSchemeId(null);
            g.setSchemeTitle(null);
            g.setObjectives(all);
            Map<Long, List<TeachingPlanObjectiveRef>> refMap = new HashMap<>();
            for (TeachingPlanObjective o : all) {
                if (o == null || o.getId() == null) {
                    continue;
                }
                refMap.put(o.getId(), teachingPlanModuleService.listObjectiveRef(o.getId()));
            }
            g.setObjectiveRefMap(refMap);
            groups.add(g);
            return groups;
        }
        // 按 scheme_id 分组（LinkedHashMap 按目标首次出现顺序保序）
        Map<Long, List<TeachingPlanObjective>> byScheme = new LinkedHashMap<>();
        for (TeachingPlanObjective o : all) {
            if (o == null) {
                continue;
            }
            byScheme.computeIfAbsent(o.getSchemeId(), k -> new ArrayList<>()).add(o);
        }
        // 是否多组：存在任一非空 scheme_id
        boolean multi = byScheme.keySet().stream().anyMatch(Objects::nonNull);
        // 排序：null 组最前；非 null 组按 schemes 列表顺序；schemes 未覆盖的兜底追加
        List<Long> orderedSchemeIds = new ArrayList<>();
        if (byScheme.containsKey(null)) {
            orderedSchemeIds.add(null);
        }
        if (schemes != null) {
            for (TeachingPlanSchemeVo s : schemes) {
                if (s != null && s.getSchemeId() != null
                        && byScheme.containsKey(s.getSchemeId())
                        && !orderedSchemeIds.contains(s.getSchemeId())) {
                    orderedSchemeIds.add(s.getSchemeId());
                }
            }
        }
        for (Long sid : byScheme.keySet()) {
            if (sid != null && !orderedSchemeIds.contains(sid)) {
                orderedSchemeIds.add(sid);
            }
        }
        for (Long sid : orderedSchemeIds) {
            List<TeachingPlanObjective> objs = byScheme.get(sid);
            CourseTeachingPlanModel.SchemeObjectiveGroup g =
                    new CourseTeachingPlanModel.SchemeObjectiveGroup();
            g.setSchemeId(sid);
            if (sid == null) {
                // 多组时 null 组给「通用」小标题，单组时不显示标题
                g.setSchemeTitle(multi ? "通用" : null);
            } else {
                g.setSchemeTitle(resolveSchemeTitle(schemes, sid));
            }
            g.setObjectives(objs == null ? new ArrayList<>() : objs);
            Map<Long, List<TeachingPlanObjectiveRef>> refMap = new HashMap<>();
            if (ObjectUtils.isNotEmpty(objs)) {
                for (TeachingPlanObjective o : objs) {
                    if (o == null || o.getId() == null) {
                        continue;
                    }
                    refMap.put(o.getId(), teachingPlanModuleService.listObjectiveRef(o.getId()));
                }
            }
            g.setObjectiveRefMap(refMap);
            groups.add(g);
        }
        return groups;
    }

    /**
     * 实验课程任务背景分组（数据驱动）：查 plan 下全部任务背景，按 scheme_id 分组。
     * 策略与 {@link #buildSchemeObjectiveGroups} 一致：
     * - 无任务背景：返回一个空组，渲染空表结构。
     * - 仅 scheme_id 为空：单组单表(无小标题)。
     * - 含非空 scheme_id：每个 scheme_id 一组(多表)。null 组排最前(标题「通用」)，非 null 组按 schemes 列表顺序；
     *   schemes 未覆盖的 scheme_id 兜底追加(标题「培养方案 {id}」)。
     */
    private List<CourseTeachingPlanModel.SchemeTaskBackgroundGroup> buildSchemeTaskBackgroundGroups(
            Long planId, List<TeachingPlanSchemeVo> schemes) {
        List<CourseTeachingPlanModel.SchemeTaskBackgroundGroup> groups = new ArrayList<>();
        if (planId == null) {
            return groups;
        }
        // onlyNullScheme=false + schemeId=null -> 不过滤 scheme，全量取 plan 下任务背景
        List<TeachingPlanTaskBackground> all =
                teachingPlanTaskBackgroundMapper.selectByPlanAndScheme(planId, null, false);
        if (ObjectUtils.isEmpty(all)) {
            CourseTeachingPlanModel.SchemeTaskBackgroundGroup g =
                    new CourseTeachingPlanModel.SchemeTaskBackgroundGroup();
            g.setTaskBackgrounds(new ArrayList<>());
            g.setTaskBackgroundRefMap(new HashMap<>());
            groups.add(g);
            return groups;
        }
        // 公共基础（聚合 course_Module 全部为公共基础）：强制单组单表，不按培养方案区分，
        // 兼容历史数据仍带 scheme_id 的场景（合并进单组，标题不显示）
        if (isPublicFoundationPlan(planId)) {
            CourseTeachingPlanModel.SchemeTaskBackgroundGroup g =
                    new CourseTeachingPlanModel.SchemeTaskBackgroundGroup();
            g.setSchemeId(null);
            g.setSchemeTitle(null);
            g.setTaskBackgrounds(all);
            Map<Long, List<TeachingPlanTaskBackgroundRef>> refMap = new HashMap<>();
            for (TeachingPlanTaskBackground tb : all) {
                if (tb == null || tb.getId() == null) {
                    continue;
                }
                refMap.put(tb.getId(),
                        teachingPlanTaskBackgroundRefMapper.selectByTaskBackgroundId(tb.getId()));
            }
            g.setTaskBackgroundRefMap(refMap);
            groups.add(g);
            return groups;
        }
        // 按 scheme_id 分组（LinkedHashMap 按任务背景首次出现顺序保序）
        Map<Long, List<TeachingPlanTaskBackground>> byScheme = new LinkedHashMap<>();
        for (TeachingPlanTaskBackground tb : all) {
            if (tb == null) {
                continue;
            }
            byScheme.computeIfAbsent(tb.getSchemeId(), k -> new ArrayList<>()).add(tb);
        }
        // 是否多组：存在任一非空 scheme_id
        boolean multi = byScheme.keySet().stream().anyMatch(Objects::nonNull);
        // 排序：null 组最前；非 null 组按 schemes 列表顺序；schemes 未覆盖的兜底追加
        List<Long> orderedSchemeIds = new ArrayList<>();
        if (byScheme.containsKey(null)) {
            orderedSchemeIds.add(null);
        }
        if (schemes != null) {
            for (TeachingPlanSchemeVo s : schemes) {
                if (s != null && s.getSchemeId() != null
                        && byScheme.containsKey(s.getSchemeId())
                        && !orderedSchemeIds.contains(s.getSchemeId())) {
                    orderedSchemeIds.add(s.getSchemeId());
                }
            }
        }
        for (Long sid : byScheme.keySet()) {
            if (sid != null && !orderedSchemeIds.contains(sid)) {
                orderedSchemeIds.add(sid);
            }
        }
        for (Long sid : orderedSchemeIds) {
            List<TeachingPlanTaskBackground> taskBackgrounds = byScheme.get(sid);
            CourseTeachingPlanModel.SchemeTaskBackgroundGroup g =
                    new CourseTeachingPlanModel.SchemeTaskBackgroundGroup();
            g.setSchemeId(sid);
            if (sid == null) {
                // 多组时 null 组给「通用」小标题，单组时不显示标题
                g.setSchemeTitle(multi ? "通用" : null);
            } else {
                g.setSchemeTitle(resolveSchemeTitle(schemes, sid));
            }
            g.setTaskBackgrounds(taskBackgrounds == null ? new ArrayList<>() : taskBackgrounds);
            Map<Long, List<TeachingPlanTaskBackgroundRef>> refMap = new HashMap<>();
            if (ObjectUtils.isNotEmpty(taskBackgrounds)) {
                for (TeachingPlanTaskBackground tb : taskBackgrounds) {
                    if (tb == null || tb.getId() == null) {
                        continue;
                    }
                    refMap.put(tb.getId(),
                            teachingPlanTaskBackgroundRefMapper.selectByTaskBackgroundId(tb.getId()));
                }
            }
            g.setTaskBackgroundRefMap(refMap);
            groups.add(g);
        }
        return groups;
    }

    /**
     * 实践训练课目训练目的分组（数据驱动）：查 plan 下全部训练目的，按 scheme_id 分组。
     * 策略与 {@link #buildSchemeTaskBackgroundGroups} 一致：
     * - 无训练目的：返回一个空组，渲染空表结构。
     * - 通识通用（聚合 location 均∈{1,2,3,9}）：强制单组单表(无小标题)，不按培养方案区分，
     *   兼容历史数据仍带 scheme_id 的场景（合并进单组，标题不显示）。
     * - 仅 scheme_id 为空：单组单表(无小标题)。
     * - 含非空 scheme_id：每个 scheme_id 一组(多表)。null 组排最前(标题「通用」)，非 null 组按 schemes 列表顺序；
     *   schemes 未覆盖的 scheme_id 兜底追加(标题「培养方案 {id}」)。
     */
    private List<CourseTeachingPlanModel.SchemeTrainingPurposeGroup> buildSchemeTrainingPurposeGroups(
            Long planId, List<TeachingPlanSchemeVo> schemes) {
        List<CourseTeachingPlanModel.SchemeTrainingPurposeGroup> groups = new ArrayList<>();
        if (planId == null) {
            return groups;
        }
        // onlyNullScheme=false + schemeId=null -> 不过滤 scheme，全量取 plan 下训练目的
        List<TeachingPlanTrainingPurpose> all =
                teachingPlanTrainingPurposeMapper.selectByPlanAndScheme(planId, null, false);
        if (ObjectUtils.isEmpty(all)) {
            CourseTeachingPlanModel.SchemeTrainingPurposeGroup g =
                    new CourseTeachingPlanModel.SchemeTrainingPurposeGroup();
            g.setPurposes(new ArrayList<>());
            g.setPurposeRefMap(new HashMap<>());
            groups.add(g);
            return groups;
        }
        // 通识通用：强制单组单表，不按培养方案区分
        if (isGeneralSubjectPlan(planId)) {
            CourseTeachingPlanModel.SchemeTrainingPurposeGroup g =
                    new CourseTeachingPlanModel.SchemeTrainingPurposeGroup();
            g.setSchemeId(null);
            g.setSchemeTitle(null);
            g.setPurposes(all);
            Map<Long, List<TeachingPlanTrainingPurposeRef>> refMap = new HashMap<>();
            for (TeachingPlanTrainingPurpose p : all) {
                if (p == null || p.getId() == null) {
                    continue;
                }
                refMap.put(p.getId(),
                        teachingPlanTrainingPurposeRefMapper.selectByPurposeId(p.getId()));
            }
            g.setPurposeRefMap(refMap);
            groups.add(g);
            return groups;
        }
        // 按 scheme_id 分组（LinkedHashMap 按训练目的首次出现顺序保序）
        Map<Long, List<TeachingPlanTrainingPurpose>> byScheme = new LinkedHashMap<>();
        for (TeachingPlanTrainingPurpose p : all) {
            if (p == null) {
                continue;
            }
            byScheme.computeIfAbsent(p.getSchemeId(), k -> new ArrayList<>()).add(p);
        }
        // 是否多组：存在任一非空 scheme_id
        boolean multi = byScheme.keySet().stream().anyMatch(Objects::nonNull);
        // 排序：null 组最前；非 null 组按 schemes 列表顺序；schemes 未覆盖的兜底追加
        List<Long> orderedSchemeIds = new ArrayList<>();
        if (byScheme.containsKey(null)) {
            orderedSchemeIds.add(null);
        }
        if (schemes != null) {
            for (TeachingPlanSchemeVo s : schemes) {
                if (s != null && s.getSchemeId() != null
                        && byScheme.containsKey(s.getSchemeId())
                        && !orderedSchemeIds.contains(s.getSchemeId())) {
                    orderedSchemeIds.add(s.getSchemeId());
                }
            }
        }
        for (Long sid : byScheme.keySet()) {
            if (sid != null && !orderedSchemeIds.contains(sid)) {
                orderedSchemeIds.add(sid);
            }
        }
        for (Long sid : orderedSchemeIds) {
            List<TeachingPlanTrainingPurpose> purposes = byScheme.get(sid);
            CourseTeachingPlanModel.SchemeTrainingPurposeGroup g =
                    new CourseTeachingPlanModel.SchemeTrainingPurposeGroup();
            g.setSchemeId(sid);
            if (sid == null) {
                // 多组时 null 组给「通用」小标题，单组时不显示标题
                g.setSchemeTitle(multi ? "通用" : null);
            } else {
                g.setSchemeTitle(resolveSchemeTitle(schemes, sid));
            }
            g.setPurposes(purposes == null ? new ArrayList<>() : purposes);
            Map<Long, List<TeachingPlanTrainingPurposeRef>> refMap = new HashMap<>();
            if (ObjectUtils.isNotEmpty(purposes)) {
                for (TeachingPlanTrainingPurpose p : purposes) {
                    if (p == null || p.getId() == null) {
                        continue;
                    }
                    refMap.put(p.getId(),
                            teachingPlanTrainingPurposeRefMapper.selectByPurposeId(p.getId()));
                }
            }
            g.setPurposeRefMap(refMap);
            groups.add(g);
        }
        return groups;
    }

    /** 按 schemeId 在 schemes 列表中匹配培养方案小标题；未匹配兜底「培养方案 {id}」。 */
    private String resolveSchemeTitle(List<TeachingPlanSchemeVo> schemes, Long schemeId) {
        if (schemes != null && schemeId != null) {
            for (TeachingPlanSchemeVo s : schemes) {
                if (s != null && schemeId.equals(s.getSchemeId())) {
                    return buildSchemeTitle(s);
                }
            }
        }
        return "培养方案 " + schemeId;
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
     * 教员团队：职称/职责 字典编码译为 label（可多值顿号/逗号分隔）。
     * 已是 label 或未命中字典时保留原文。
     */
    private void translateTeacherDictFields(List<TeachingPlanTeacher> teachers) {
        if (ObjectUtils.isEmpty(teachers)) {
            return;
        }
        Map<String, String> titleMap = dictValueToLabelMapCandidates(DICT_PROFESSIONAL_TITLE_CANDIDATES);
        Map<String, String> dutyMap = dictValueToLabelMapCandidates(DICT_TEACHER_DUTY_CANDIDATES);
        for (TeachingPlanTeacher t : teachers) {
            if (t == null) {
                continue;
            }
            t.setProfessionalTitle(translateDictJoined(t.getProfessionalTitle(), titleMap));
            t.setDuty(translateDictJoined(t.getDuty(), dutyMap));
        }
    }

    /** 多个字典 type 合并为 value->label；后出现的 type 不覆盖已有 value。 */
    private Map<String, String> dictValueToLabelMapCandidates(String... dictTypes) {
        if (dictTypes == null || dictTypes.length == 0) {
            return Collections.emptyMap();
        }
        Map<String, String> map = new LinkedHashMap<>();
        for (String type : dictTypes) {
            if (StringUtils.isBlank(type)) {
                continue;
            }
            Map<String, String> one = dictValueToLabelMap(type);
            if (MapUtils.isEmpty(one)) {
                continue;
            }
            for (Map.Entry<String, String> e : one.entrySet()) {
                if (e.getKey() == null || map.containsKey(e.getKey())) {
                    continue;
                }
                map.put(e.getKey(), e.getValue());
            }
        }
        return map;
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
        Map<String, String> targetTypeMap = dictValueToLabelMap(DICT_PLAN_TARGET_TYPE);
        for (TeachingPlanTargetDesign d : designs) {
            if (d == null) {
                continue;
            }
            d.setTeachingLink(translateDictJoined(d.getTeachingLink(), linkMap));
            d.setTeachingMethod(translateDictJoined(d.getTeachingMethod(), methodMap));
            d.setLearningMethod(translateDictJoined(d.getLearningMethod(), learningMap));
            // design_type_code 存字典 value(如 1/2/3)，译成中文 label 填 designTypeName，
            // 供生成器按"知识/能力/素质"分流；已是中文或字典未命中时保留原文
            d.setDesignTypeName(translateDictJoined(d.getDesignTypeCode(), targetTypeMap));
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
        Map<String, String> outcomeTypeMap = dictValueToLabelMap(DICT_PLAN_OUTCOME_TYPE);
        for (TeachingPlanAssessment a : assessments) {
            if (a == null) {
                continue;
            }
            if (Objects.equals(a.getAssessmentCategory(), 5)) {
                String code = a.getOutcomeType() == null ? null : String.valueOf(a.getOutcomeType());
                String name = translateDictJoined(code, outcomeTypeMap);
                if (Objects.equals(code, name)) {
                    name = "1".equals(code) ? "个人成果"
                            : ("2".equals(code) ? "团队成果" : ("3".equals(code) ? "过程成果" : name));
                }
                a.setOutcomeTypeName(name);
                continue;
            }
            a.setAssessmentItem(translateDictJoined(a.getAssessmentItem(), itemMap));
            a.setMethod(translateDictJoined(a.getMethod(), methodMap));
            a.setMechanism(translateDictJoined(a.getMechanism(), mechanismMap));
            // 成绩评定制也可能存字典 value，写入评定机制列前一并翻译
            a.setScoreSystem(translateDictJoined(a.getScoreSystem(), mechanismMap));
            // 评价标准多值存逗号串：按 sys_evaluation_standard 译 label 后仍用 , 拼接
            a.setStandard(translateDictJoined(a.getStandard(), standardMap, ","));
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

    /** 字典 type → value:label 映射（保持字典顺序；同时登记 trim 后的 key） */
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
            String value = d.getDictValue().trim();
            String label = StringUtils.defaultIfBlank(d.getDictLabel(), value).trim();
            map.putIfAbsent(value, label);
            // 兼容前后空格 / 全角空格存库
            map.putIfAbsent(d.getDictValue(), label);
            if (StringUtils.isNotBlank(label)) {
                // 已是中文名称时，翻译仍返回名称
                map.putIfAbsent(label, label);
            }
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
     * 多值字典编码翻译：支持 、,，;；/| 分隔；命中 map 用 label，否则原样保留。
     * 默认用顿号拼接。
     */
    private String translateDictJoined(String raw, Map<String, String> valueToLabel) {
        return translateDictJoined(raw, valueToLabel, "、");
    }

    /**
     * 多值字典编码翻译，可指定拼接符（评价标准用逗号）。
     */
    private String translateDictJoined(String raw, Map<String, String> valueToLabel, String joiner) {
        if (StringUtils.isBlank(raw)) {
            return raw;
        }
        if (MapUtils.isEmpty(valueToLabel)) {
            return raw;
        }
        String join = joiner == null ? "、" : joiner;
        String trimmed = raw.trim();
        // 若整串已是某个 label / 已登记 key，直接返回名称
        if (valueToLabel.containsKey(trimmed)) {
            return valueToLabel.get(trimmed);
        }
        if (valueToLabel.containsValue(trimmed)) {
            return trimmed;
        }
        return Arrays.stream(raw.split("[、,，;；/|]"))
                .map(String::trim)
                .map(s -> s.replace("　", "").trim())
                .filter(StringUtils::isNotBlank)
                .map(code -> {
                    String label = valueToLabel.get(code);
                    if (StringUtils.isNotBlank(label)) {
                        return label;
                    }
                    // 忽略大小写再试一次
                    for (Map.Entry<String, String> e : valueToLabel.entrySet()) {
                        if (e.getKey() != null && e.getKey().equalsIgnoreCase(code)) {
                            return e.getValue();
                        }
                    }
                    return code;
                })
                .collect(Collectors.joining(join));
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
    /**
     * 教学计划类型 plan_type → 文档模板类型 docType（与课程库 t_csys_course.type 编号一致）的映射。
     * 两者 2/3 对调，需显式转换：
     * plan_type 编号(前后端统一)：1课程 2实验课程 3实践训练课目 4实践项目；
     * docType 编号(课程库 type)： 1课程 2实践训练课目 3实验课程 4实践项目。
     */
    private Integer planTypeToDocType(Integer planType) {
        if (planType == null) {
            return null;
        }
        switch (planType) {
            case 2:
                return CourseTeachingPlanGenerator.DOC_TYPE_EXPERIMENT_COURSE;   // 3 实验课程
            case 3:
                return CourseTeachingPlanGenerator.DOC_TYPE_PRACTICE_SUBJECT;    // 2 实践训练课目
            case 4:
                return CourseTeachingPlanGenerator.DOC_TYPE_PRACTICE_PROJECT;    // 4 实践项目
            default:
                return CourseTeachingPlanGenerator.DOC_TYPE_COURSE;              // 1 课程
        }
    }

    private Integer mapDocType(String type,Integer planType) {
        // 文档模板以教学计划 plan_type 为准（同一源课可挂多类型计划）；
        // 课程库 type=1/3 同为「课程」类别，不在课程上区分，由教学计划区分课程/实验课程。
        Integer docType = planTypeToDocType(planType);
        if (docType != null) {
            return docType;
        }
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
        if (fallback == null) {
            return "";
        }
        // Double 常见 32.0 -> 32
        return BigDecimal.valueOf(fallback).stripTrailingZeros().toPlainString();
    }

    // ============ 教员团队 ============

        @Override
    public List<TeachingPlanTeacher> listTeacher(Long planId) {
        if (planId == null) {
            throw new IllegalArgumentException("planId cannot be null");
        }
        List<TeachingPlanTeacher> teachers = teachingPlanTeacherMapper.selectByPlanId(planId);
        if (ObjectUtils.isNotEmpty(teachers)) {
            return teachers;
        }
        // No plan teachers: read-only fallback from course authors, do not insert
        return buildTeachersFromCourse(planId);
    }

    private List<TeachingPlanTeacher> buildTeachersFromCourse(Long planId) {
        TeachingPlan plan = teachingPlanMapper.selectById(planId);
        if (plan == null || plan.getSourceCourseId() == null) {
            return Collections.emptyList();
        }
        CourseVo course = courseMapper.selectCourseById(plan.getSourceCourseId());
        if (course == null) {
            return Collections.emptyList();
        }
        String[] names = splitCsv(course.getAuthors());
        String[] ids = splitCsv(course.getAuthorIds());
        if (names.length == 0 && ids.length == 0) {
            return Collections.emptyList();
        }
        int size = Math.max(names.length, ids.length);
        List<TeachingPlanTeacher> result = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            String name = i < names.length ? names[i] : null;
            String teacherId = i < ids.length ? ids[i] : null;
            if (StringUtils.isBlank(name) && StringUtils.isBlank(teacherId)) {
                continue;
            }
            TeachingPlanTeacher teacher = new TeachingPlanTeacher();
            teacher.setPlanId(planId);
            teacher.setTeacherId(StringUtils.trimToNull(teacherId));
            teacher.setTeacherName(StringUtils.defaultIfBlank(StringUtils.trimToNull(name), teacherId));
            teacher.setSort(i + 1);
            teacher.setSysflag(0);
            result.add(teacher);
        }
        return result;
    }

    private String[] splitCsv(String raw) {
        if (StringUtils.isBlank(raw)) {
            return new String[0];
        }
        return Arrays.stream(raw.split("[,，、;；]"))
                .map(String::trim)
                .filter(StringUtils::isNotBlank)
                .toArray(String[]::new);
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
            throw new IllegalArgumentException("planType 必须为 1-4（1课程/2实验课程/3实践训练课目/4实践项目）");
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

        // ERROR 表示文档类型、培养方案归属等关键路径无法确定。必须在新建计划或清空旧模块前返回，
        // 保证整单不落库；WARN 仍按原策略跳过单条并继续导入。
        if (hasImportError(parsed.issues)) {
            TeachingPlanImportResultVo rejected = new TeachingPlanImportResultVo();
            rejected.setCourseId(courseId);
            rejected.setDocType(planType);
            rejected.setCreatedPlan(false);
            TeachingPlan current = teachingPlanMapper.selectBySourceCourseIdAndPlanType(courseId, planType);
            rejected.setPlanId(current == null ? null : current.getId());
            rejected.setIssues(parsed.issues);
            return rejected;
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

        persistImportData(planId, courseId, planType, ctx.publicFoundation, parsed, result);
        if (ObjectUtils.isNotEmpty(result.getIssues())) {
            log.warn("教学计划 Word 导入完成但存在 {} 条问题, courseId={}, planId={}, planType={}",
                    result.getIssues().size(), courseId, planId, planType);
        }
        return result;
    }

    private boolean hasImportError(List<TeachingPlanImportIssueVo> issues) {
        return ObjectUtils.isNotEmpty(issues) && issues.stream()
                .anyMatch(issue -> issue != null && "ERROR".equalsIgnoreCase(issue.getSeverity()));
    }

    private TeachingPlanWordImporter.ParseContext buildImportParseContext(Long courseId, CourseVo course,
                                                                          Integer planType) {
        TeachingPlanWordImporter.ParseContext ctx = new TeachingPlanWordImporter.ParseContext();
        // 类型由前端传入(plan_type 编号)，Word 识别基于 docType 编号(与课程库 type 一致)，两者 2/3 对调，先转换再对照 WARN
        ctx.expectedDocType = planTypeToDocType(planType);
        // 是否按培养方案区分：type2 实践训练课目用课目模块(location)∈{1,2,3,9}判定，
        // 普通课程(type1/3)用聚合 course_Module 全部分公共基础判定（口径与适用专业一致）。
        if ("2".equals(StringUtils.trimToEmpty(course.getType()))) {
            ctx.publicFoundation = teachingPlanModuleService.isGeneralSubjectModuleCourse(courseId);
        } else {
            ctx.publicFoundation = teachingPlanModuleService.isPublicFoundationCourse(courseId);
        }
        List<TeachingPlanSchemeVo> schemes = teachingPlanModuleService.listSchemes(courseId);
        ctx.schemes = schemes == null ? new ArrayList<>() : schemes;
        List<CourseKnowledgeUnit> units = teachingPlanModuleService.listKnowledgeUnitInit(courseId);
        ctx.knowledgeUnits = units == null ? new ArrayList<>() : units;

        String[] dictTypes = {
                "sys_plan_target_type",
                DICT_PLAN_TEACHING_LINK, DICT_PLAN_TEACHING_METHOD, DICT_PLAN_LEARNING_METHOD,
                DICT_ASSESSMENT_ITEM, DICT_ASSESSMENT_METHOD, DICT_ASSESSMENT_MECHANISM, DICT_EVALUATION_STANDARD,
                DICT_PLAN_OUTCOME_TYPE,
                DICT_TEXTBOOK_NATURE, DICT_PUBLICATION_METHOD, DICT_CONDITION_TYPE,
                DICT_PLAN_TRAINING_MODULE, DICT_PLAN_IMPLEMENTATION_STEP
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
        teachingPlanObjectiveAssessmentMapper.deleteByPlanId(planId);
        teachingPlanObjectiveRefMapper.deleteByPlanId(planId);
        teachingPlanObjectiveMapper.deleteByPlanId(planId);
        teachingPlanTaskBackgroundRefMapper.deleteByPlanId(planId);
        teachingPlanTaskBackgroundMapper.deleteByPlanId(planId);
        teachingPlanTrainingPurposeRefMapper.deleteByPlanId(planId);
        teachingPlanTrainingPurposeMapper.deleteByPlanId(planId);
        teachingPlanContentPurposeMapper.deleteByPlanId(planId);
        teachingPlanSupportObjectiveMapper.deleteByPlanId(planId);
        teachingPlanSupportContentMapper.deleteByPlanId(planId);
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

    /** 导入用引用课程ID：优先计划 context.quote_course_id（与生成时快照一致），
     *  缺失时回退源课的第一个调用课，再无则源课自身。 */
    private Long resolveImportQuoteCourseId(Long planId, Long courseId) {
        if (planId != null) {
            List<TeachingPlanContext> ctxs = teachingPlanContextMapper.selectByPlanId(planId);
            if (ObjectUtils.isNotEmpty(ctxs) && ctxs.get(0).getQuoteCourseId() != null) {
                return ctxs.get(0).getQuoteCourseId();
            }
        }
        if (courseId != null) {
            List<Course> quotes = courseMapper.selectCourseBySourceId(courseId);
            if (ObjectUtils.isNotEmpty(quotes) && quotes.get(0).getId() != null) {
                return quotes.get(0).getId();
            }
            return courseId;
        }
        return null;
    }

    private void persistImportData(Long planId, Long courseId, Integer planType, boolean publicFoundation,
                                   TeachingPlanWordImporter.ParseResult parsed,
                                   TeachingPlanImportResultVo result) {
        // practice_item.item_type NOT NULL，但 Word 不编码项目类型：
        // 实践项目计划(type4) 兜底 2(实践项目)，其余按 1(实验)
        int defaultItemType = (planType != null && planType == 4) ? 2 : 1;
        // 引用课程ID快照：objective_ref 该列 NOT NULL，取自计划 context（导出时的调用课程），
        // 无 context 时回退源课的第一个调用课/源课自身，保证导入不因空快照抛 SQL 异常
        Long quoteCourseId = resolveImportQuoteCourseId(planId, courseId);
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
        // 项目步骤（type4 组织与实施）
        if (ObjectUtils.isNotEmpty(parsed.processSteps)) {
            for (TeachingPlanProcessStep step : parsed.processSteps) {
                step.setId(null);
                step.setPlanId(planId);
                prepareEntity(step);
                teachingPlanProcessStepMapper.insert(step);
            }
            result.addCount("processStep", parsed.processSteps.size());
        }
        // 目标 + 支撑毕业要求
        int objCount = 0;
        int refCount = 0;
        Map<String, TeachingPlanObjective> objectiveMap = new LinkedHashMap<>();
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
                objectiveMap.putIfAbsent(normalizeImportKey(o.getContent()), o);
                if (ObjectUtils.isEmpty(po.graduationNames) || o.getId() == null) {
                    continue;
                }
                Long schemeId = o.getSchemeId();
                // 候选毕业要求与页面绑定弹框同源：listCourseGraduationByScheme
                Map<String, List<StandardGraduation>> nameMap = schemeGradCache.computeIfAbsent(
                        schemeId == null ? -1L : schemeId,
                        k -> buildGraduationNameMap(courseId, schemeId));
                // 整串优先，未命中则对原文贪心最长前缀匹配：
                // 名称自身可含「、，；」（如"具有良好的职业道德、团队合作与终身学习意识"），不能先按分隔符拆碎再逐段匹配
                int sort = 1;
                List<StandardGraduation> boundGrads = resolveGraduationBindings(
                        po.graduationRaw, "四、课程目标与支撑毕业要求", o.getContent(), nameMap, result.getIssues());
                for (StandardGraduation g : boundGrads) {
                    if (g == null || g.getId() == null) {
                        continue;
                    }
                    TeachingPlanObjectiveRef ref = new TeachingPlanObjectiveRef();
                    ref.setPlanId(planId);
                    ref.setObjectiveId(o.getId());
                    ref.setSchemeId(schemeId);
                    ref.setQuoteCourseId(quoteCourseId);
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

        // 任务背景 + 支撑毕业要求（type3 实验课程）
        int tbCount = 0;
        int tbRefCount = 0;
        if (ObjectUtils.isNotEmpty(parsed.taskBackgrounds)) {
            Map<Long, Map<String, List<StandardGraduation>>> tbSchemeGradCache = new HashMap<>();
            for (TeachingPlanWordImporter.ParsedTaskBackground ptb : parsed.taskBackgrounds) {
                if (ptb == null || ptb.taskBackground == null
                        || StringUtils.isBlank(ptb.taskBackground.getBackgroundDesc())) {
                    continue;
                }
                TeachingPlanTaskBackground tb = ptb.taskBackground;
                tb.setId(null);
                tb.setPlanId(planId);
                if (publicFoundation) {
                    tb.setSchemeId(null);
                }
                prepareEntity(tb);
                teachingPlanTaskBackgroundMapper.insert(tb);
                tbCount++;
                if (ObjectUtils.isEmpty(ptb.graduationNames) || tb.getId() == null) {
                    continue;
                }
                Long tbSchemeId = tb.getSchemeId();
                // 候选毕业要求与页面绑定弹框同源：listCourseGraduationByScheme
                Map<String, List<StandardGraduation>> nameMap = tbSchemeGradCache.computeIfAbsent(
                        tbSchemeId == null ? -1L : tbSchemeId,
                        k -> buildGraduationNameMap(courseId, tbSchemeId));
                // 整串优先，未命中则对原文贪心最长前缀匹配（支持名称自身含「、，；」）
                int tbSort = 1;
                List<StandardGraduation> boundTbGrads = resolveGraduationBindings(
                        ptb.graduationRaw, "三、任务背景与目标", ptb.taskBackground.getBackgroundDesc(), nameMap, result.getIssues());
                for (StandardGraduation g : boundTbGrads) {
                    if (g == null || g.getId() == null) {
                        continue;
                    }
                    TeachingPlanTaskBackgroundRef ref = new TeachingPlanTaskBackgroundRef();
                    ref.setPlanId(planId);
                    ref.setTaskBackgroundId(tb.getId());
                    ref.setSchemeId(tbSchemeId);
                    ref.setQuoteCourseId(quoteCourseId);
                    ref.setGraduationId(g.getId());
                    ref.setSourceGraduationId(g.getSourceId());
                    ref.setGraduationCode(g.getCode());
                    ref.setGraduationName(g.getName());
                    ref.setGraduationBindSource("course_ref_graduation");
                    ref.setSort(tbSort++);
                    prepareEntity(ref);
                    teachingPlanTaskBackgroundRefMapper.insert(ref);
                    tbRefCount++;
                }
            }
        }
        result.addCount("taskBackground", tbCount);
        result.addCount("taskBackgroundRef", tbRefCount);

        // 训练目的 + 支撑毕业要求（type2 实践训练课目第二节）
        int tpCount = 0;
        int tpRefCount = 0;
        if (ObjectUtils.isNotEmpty(parsed.trainingPurposes)) {
            Map<Long, Map<String, List<StandardGraduation>>> tpSchemeGradCache = new HashMap<>();
            for (TeachingPlanWordImporter.ParsedTrainingPurpose ptp : parsed.trainingPurposes) {
                if (ptp == null || ptp.purpose == null
                        || StringUtils.isBlank(ptp.purpose.getPurpose())) {
                    continue;
                }
                TeachingPlanTrainingPurpose p = ptp.purpose;
                p.setId(null);
                p.setPlanId(planId);
                if (publicFoundation) {
                    p.setSchemeId(null);
                }
                prepareEntity(p);
                teachingPlanTrainingPurposeMapper.insert(p);
                tpCount++;
                if (ObjectUtils.isEmpty(ptp.graduationNames) || p.getId() == null) {
                    continue;
                }
                Long tpSchemeId = p.getSchemeId();
                // 候选毕业要求与页面绑定弹框同源：listCourseGraduationByScheme
                Map<String, List<StandardGraduation>> nameMap = tpSchemeGradCache.computeIfAbsent(
                        tpSchemeId == null ? -1L : tpSchemeId,
                        k -> buildGraduationNameMap(courseId, tpSchemeId));
                // 整串优先，未命中则对原文贪心最长前缀匹配（支持名称自身含「、，；」）
                int tpSort = 1;
                List<StandardGraduation> boundTpGrads = resolveGraduationBindings(
                        ptp.graduationRaw, "二、训练目的与支撑毕业要求", p.getPurpose(), nameMap, result.getIssues());
                for (StandardGraduation g : boundTpGrads) {
                    if (g == null || g.getId() == null) {
                        continue;
                    }
                    TeachingPlanTrainingPurposeRef ref = new TeachingPlanTrainingPurposeRef();
                    ref.setPlanId(planId);
                    ref.setPurposeId(p.getId());
                    ref.setSchemeId(tpSchemeId);
                    ref.setQuoteCourseId(quoteCourseId);
                    ref.setGraduationId(g.getId());
                    ref.setSourceGraduationId(g.getSourceId());
                    ref.setGraduationCode(g.getCode());
                    ref.setGraduationName(g.getName());
                    ref.setGraduationBindSource("course_ref_graduation");
                    ref.setSort(tpSort++);
                    prepareEntity(ref);
                    teachingPlanTrainingPurposeRefMapper.insert(ref);
                    tpRefCount++;
                }
            }
        }
        result.addCount("trainingPurpose", tpCount);
        result.addCount("trainingPurposeRef", tpRefCount);

        // 教学内容；实践训练课目第四部分「目的」整格保存到 content.purpose，不建立 ID 绑定。
        if (ObjectUtils.isNotEmpty(parsed.contents)) {
            for (TeachingPlanContent c : parsed.contents) {
                c.setId(null);
                c.setPlanId(planId);
                prepareEntity(c);
                teachingPlanContentMapper.insert(c);
            }
            result.addCount("content", parsed.contents.size());
        }
        // 实践项目(type4)第二节支撑绑定：支撑的课程目标或训练目的 / 涉及的知识体系或训练内容。
        // 按 Word 单元格文本匹配候选（与页面绑定弹框同源 listSupportCandidates），未命中记警告跳过；
        // 命中后交模块 Service 整表重建（与页面保存同口径，含 refType/refPlanId 快照）。
        if (StringUtils.isNotBlank(parsed.supportObjectiveRaw)
                || StringUtils.isNotBlank(parsed.supportContentRaw)) {
            TeachingPlanSupportCandidateVo supportCandidates =
                    teachingPlanModuleService.listSupportCandidates(courseId);
            if (StringUtils.isNotBlank(parsed.supportObjectiveRaw)) {
                List<Long> objectiveIds = new ArrayList<>();
                List<Long> purposeIds = new ArrayList<>();
                Set<Long> boundObjective = new HashSet<>();
                Set<Long> boundPurpose = new HashSet<>();
                Map<String, TeachingPlanSupportCandidateItem> objNameIndex = new HashMap<>();
                Map<String, TeachingPlanSupportCandidateItem> purposeNameIndex = new HashMap<>();
                if (supportCandidates != null) {
                    for (TeachingPlanSupportCandidateItem it : supportCandidates.getObjectives()) {
                        if (it != null && StringUtils.isNotBlank(it.getName())) {
                            objNameIndex.putIfAbsent(it.getName().trim(), it);
                        }
                    }
                    for (TeachingPlanSupportCandidateItem it : supportCandidates.getPurposes()) {
                        if (it != null && StringUtils.isNotBlank(it.getName())) {
                            purposeNameIndex.putIfAbsent(it.getName().trim(), it);
                        }
                    }
                }
                // 整串优先，未命中则对原文贪心最长前缀匹配：
                // 支撑目标名自身可含「、，；」（如"掌握面向对象程序设计的基本思想与类、继承、多态等核心机制"），
                // 若先按分隔符拆碎再逐段匹配会把单个名称拆成多段全部失配。
                List<TeachingPlanSupportCandidateItem> boundItems = resolveSupportRaw(
                        parsed.supportObjectiveRaw, objNameIndex, purposeNameIndex, result.getIssues(),
                        "二、任务背景与目标", parsed.supportObjectiveRaw, "supportObjective");
                for (TeachingPlanSupportCandidateItem it : boundItems) {
                    if (it == null || it.getId() == null) {
                        continue;
                    }
                    if (objNameIndex.containsKey(it.getName()) && boundObjective.add(it.getId())) {
                        objectiveIds.add(it.getId());
                    } else if (purposeNameIndex.containsKey(it.getName()) && boundPurpose.add(it.getId())) {
                        purposeIds.add(it.getId());
                    }
                }
                TeachingPlanSupportObjectiveSaveVo saveVo = new TeachingPlanSupportObjectiveSaveVo();
                saveVo.setPlanId(planId);
                saveVo.setObjectiveIds(objectiveIds);
                saveVo.setPurposeIds(purposeIds);
                teachingPlanModuleService.saveSupportObjectives(saveVo);
                result.addCount("supportObjective", objectiveIds.size() + purposeIds.size());
            }
            if (StringUtils.isNotBlank(parsed.supportContentRaw)) {
                List<Long> contentIds = new ArrayList<>();
                Set<Long> boundContent = new HashSet<>();
                Map<String, TeachingPlanSupportCandidateItem> kpNameIndex = new HashMap<>();
                Map<String, TeachingPlanSupportCandidateItem> tcNameIndex = new HashMap<>();
                if (supportCandidates != null) {
                    for (TeachingPlanSupportCandidateItem it : supportCandidates.getKnowledgePoints()) {
                        if (it != null && StringUtils.isNotBlank(it.getName())) {
                            kpNameIndex.putIfAbsent(it.getName().trim(), it);
                        }
                    }
                    for (TeachingPlanSupportCandidateItem it : supportCandidates.getTrainingContents()) {
                        if (it != null && StringUtils.isNotBlank(it.getName())) {
                            tcNameIndex.putIfAbsent(it.getName().trim(), it);
                        }
                    }
                }
                // 与支撑目标同口径：整串优先 + 贪心最长前缀匹配（名称可含分隔符）
                List<TeachingPlanSupportCandidateItem> boundContentItems = resolveSupportRaw(
                        parsed.supportContentRaw, kpNameIndex, tcNameIndex, result.getIssues(),
                        "二、任务背景与目标", parsed.supportContentRaw, "supportContent");
                for (TeachingPlanSupportCandidateItem it : boundContentItems) {
                    if (it != null && it.getId() != null && boundContent.add(it.getId())) {
                        contentIds.add(it.getId());
                    }
                }
                TeachingPlanSupportContentSaveVo saveVo = new TeachingPlanSupportContentSaveVo();
                saveVo.setPlanId(planId);
                saveVo.setContentIds(contentIds);
                teachingPlanModuleService.saveSupportContents(saveVo);
                result.addCount("supportContent", contentIds.size());
            }
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
                                "目标「" + d.getObjectiveText() + "」的知识点 JSON 序列化失败，已置空，"
                                        + "导出时该行知识点可能为空；请手工核对/补录该行知识点"));
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
                if (item.getItemType() == null) {
                    item.setItemType(defaultItemType);
                }
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

        // 考核。关联表使用 Word 中的考核项目名称，因此在字典反查前先保留名称索引；
        // 落库后再补充编码索引，兼容字典项名称与编码两种来源。
        Map<String, TeachingPlanAssessment> assessmentMap = new LinkedHashMap<>();
        if (ObjectUtils.isNotEmpty(parsed.assessments)) {
            for (TeachingPlanAssessment a : parsed.assessments) {
                a.setId(null);
                a.setPlanId(planId);
                String importedAssessmentItem = a.getAssessmentItem();
                // 字典反查已在解析阶段尽量完成；此处再补 assessment 字段
                if (!Objects.equals(a.getAssessmentCategory(), 5)) {
                    a.setAssessmentItem(reverseImportDict(a.getAssessmentItem(), DICT_ASSESSMENT_ITEM));
                    a.setMethod(reverseImportDict(a.getMethod(), DICT_ASSESSMENT_METHOD));
                    a.setMechanism(reverseImportDict(a.getMechanism(), DICT_ASSESSMENT_MECHANISM));
                    a.setStandard(reverseImportDict(a.getStandard(), DICT_EVALUATION_STANDARD));
                }
                if (a.getWeight() != null && (a.getWeight().compareTo(BigDecimal.ZERO) < 0
                        || a.getWeight().compareTo(BigDecimal.ONE) > 0)) {
                    result.getIssues().add(TeachingPlanImportIssueVo.warn(
                            "考核评价", importedAssessmentItem, "weight",
                            "权重必须为0到1之间的小数，该考核项已跳过"));
                    continue;
                }
                prepareEntity(a);
                teachingPlanAssessmentMapper.insert(a);
                if (StringUtils.isNotBlank(importedAssessmentItem)) {
                    assessmentMap.putIfAbsent(normalizeImportKey(importedAssessmentItem), a);
                }
                if (StringUtils.isNotBlank(a.getAssessmentItem())) {
                    assessmentMap.putIfAbsent(normalizeImportKey(a.getAssessmentItem()), a);
                }
            }
            result.addCount("assessment", parsed.assessments.size());
        }
        // 课程目标达成考核评价设计：目标和考核项均已落库并取得 ID 后再写关联
        if (ObjectUtils.isNotEmpty(parsed.objectives) && ObjectUtils.isNotEmpty(parsed.assessments)) {
            List<TeachingPlanObjectiveAssessment> relations = new ArrayList<>();
            for (TeachingPlanWordImporter.ParsedObjective parsedObjective : parsed.objectives) {
                if (parsedObjective == null || parsedObjective.objective == null
                        || ObjectUtils.isEmpty(parsedObjective.assessments)) {
                    continue;
                }
                TeachingPlanObjective objective = objectiveMap.get(
                        normalizeImportKey(parsedObjective.objective.getContent()));
                if (objective == null || objective.getId() == null) {
                    result.getIssues().add(TeachingPlanImportIssueVo.warn(
                            "八、考核评价", parsedObjective.objective.getContent(), "objectiveId",
                            "达成考核设计中的课程目标「" + parsedObjective.objective.getContent()
                                    + "」未匹配到已导入的课程目标（可能文字与「四、课程目标」单元格不一致），"
                                    + "该目标的达成考核关联已跳过"));
                    continue;
                }
                for (TeachingPlanWordImporter.ParsedObjectiveAssessment parsedRelation
                        : parsedObjective.assessments) {
                    if (parsedRelation == null || StringUtils.isBlank(parsedRelation.assessmentItem)
                            || parsedRelation.weight == null) {
                        continue;
                    }
                    TeachingPlanAssessment assessment = assessmentMap.get(
                            normalizeImportKey(parsedRelation.assessmentItem));
                    if (assessment == null || assessment.getId() == null) {
                        List<String> assessmentNames = assessmentMap.values().stream()
                                .filter(a -> a != null && StringUtils.isNotBlank(a.getAssessmentItem()))
                                .map(TeachingPlanAssessment::getAssessmentItem)
                                .collect(Collectors.toList());
                        result.getIssues().add(TeachingPlanImportIssueVo.warn(
                                "八、考核评价", parsedObjective.objective.getContent(), "assessmentItem",
                                "达成考核设计引用了考核评价项「" + parsedRelation.assessmentItem
                                        + "」，但已导入考核项中不存在" + candidateContext(assessmentNames)
                                        + "；该关联已跳过，请核对文字一致性"));
                        continue;
                    }
                    TeachingPlanObjectiveAssessment relation = new TeachingPlanObjectiveAssessment();
                    relation.setPlanId(planId);
                    relation.setSchemeId(objective.getSchemeId());
                    relation.setObjectiveId(objective.getId());
                    relation.setAssessmentId(assessment.getId());
                    relation.setAssessmentItem(assessment.getAssessmentItem());
                    relation.setWeight(parsedRelation.weight);
                    relation.setAssessmentItemContent(parsedRelation.assessmentItemContent);
                    prepareEntity(relation);
                    relations.add(relation);
                }
            }
            if (ObjectUtils.isNotEmpty(relations)) {
                teachingPlanObjectiveAssessmentMapper.insertBatch(relations);
                result.addCount("objectiveAssessment", relations.size());
            }
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
        // 接口已返回树：索引时打平 children，避免只登记根节点名称
        List<StandardGraduation> flat = new ArrayList<>();
        flattenGraduationTree(list, flat);
        for (StandardGraduation g : flat) {
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

    private void flattenGraduationTree(List<StandardGraduation> nodes, List<StandardGraduation> out) {
        if (ObjectUtils.isEmpty(nodes)) {
            return;
        }
        for (StandardGraduation g : nodes) {
            if (g == null) {
                continue;
            }
            out.add(g);
            flattenGraduationTree(g.getChildren(), out);
        }
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

    /**
     * 解析单元格内毕业要求：整串优先；未命中时对原文贪心最长前缀匹配。
     * 毕业要求名称自身可含「、，；」（如"具有良好的职业道德、团队合作与终身学习意识"），
     * 若先按分隔符拆碎再逐段匹配会导致部分名称失配跳过，这里改为直接从原文消费最长已知名称，
     * 天然支持名称含分隔符、多名称同格（顿号分隔）的场景。同名多条默认取第一条并加提示。
     */
    private List<StandardGraduation> resolveGraduationBindings(String raw, String sourceSection, String sourceText,
                                                               Map<String, List<StandardGraduation>> nameMap,
                                                               List<TeachingPlanImportIssueVo> issues) {
        List<StandardGraduation> whole = lookupGraduations(nameMap, raw);
        if (whole != null) {
            return whole;
        }
        List<String> candidates = new ArrayList<>(nameMap.keySet());
        candidates.removeIf(c -> c == null || StringUtils.isBlank(c));
        candidates.sort((a, b) -> b.length() - a.length());
        String remaining = raw == null ? "" : raw;
        List<StandardGraduation> result = new ArrayList<>();
        Set<Long> seen = new HashSet<>();
        while (StringUtils.isNotBlank(remaining)) {
            String cur = remaining.trim();
            // 跳过分隔符
            if (cur.length() > 0 && "、,，;；/|".indexOf(cur.charAt(0)) >= 0) {
                remaining = cur.substring(1);
                continue;
            }
            boolean matched = false;
            for (String cand : candidates) {
                if (cur.startsWith(cand)) {
                    List<StandardGraduation> list = nameMap.get(cand);
                    if (ObjectUtils.isEmpty(list)) {
                        continue;
                    }
                    StandardGraduation g = list.get(0);
                    if (g != null && g.getId() != null && seen.add(g.getId())) {
                        if (list.size() > 1) {
                            log.warn("教学计划导入毕业要求同名多条, section={}, name={}, 命中{}条, 默认绑定第一条 id={}",
                                    sourceSection, cand, list.size(), g.getId());
                            issues.add(TeachingPlanImportIssueVo.warn(
                                    sourceSection, sourceText, "graduationName",
                                    "毕业要求「" + cand + "」同名存在 " + list.size()
                                            + " 条，默认绑定第一条(id=" + g.getId() + ")，其余同名条目不参与该目标绑定"));
                        }
                        result.add(g);
                    }
                    remaining = cur.substring(cand.length());
                    matched = true;
                    break;
                }
            }
            if (!matched) {
                issues.add(TeachingPlanImportIssueVo.warn(
                        sourceSection, sourceText, "graduationName",
                        "未匹配到毕业要求「" + cur + "」" + candidateContext(candidates)
                                + "；该段绑定已跳过，其后的毕业要求继续匹配。请核对文档文字与系统中毕业要求名称（含顿号/空格等）是否一致，"
                                + "否则该目标将缺少对应的毕业要求绑定"));
                // 跳过当前未匹配段（到下一个分隔符为止），继续匹配后续候选，避免首个失配后剩余内容被静默丢弃
                remaining = skipUnmatchedSegment(cur);
            }
        }
        return result;
    }

    /**
     * 支撑目标/支撑内容单元格贪心最长前缀匹配：候选名称自身可含「、，；」，
     * 整串命中优先；未命中时从原文逐个消费最长的已知候选名，天然支持多候选同格。
     * 返回按原文顺序命中的候选项（同一 id 只返回一次，路由/去重由调用方完成）。
     */
    private List<TeachingPlanSupportCandidateItem> resolveSupportRaw(String raw,
            Map<String, TeachingPlanSupportCandidateItem> firstIndex,
            Map<String, TeachingPlanSupportCandidateItem> secondIndex,
            List<TeachingPlanImportIssueVo> issues, String section, String sourceText, String issueField) {
        List<TeachingPlanSupportCandidateItem> result = new ArrayList<>();
        if (StringUtils.isBlank(raw)) {
            return result;
        }
        String remaining = raw.trim();
        // 整串优先
        TeachingPlanSupportCandidateItem whole = firstIndex.get(remaining);
        if (whole == null) {
            whole = secondIndex.get(remaining);
        }
        if (whole != null && whole.getId() != null) {
            result.add(whole);
            return result;
        }
        // 未命中：按名称长度降序，从原文贪心消费最长已知候选名
        List<String> candidates = new ArrayList<>(firstIndex.keySet());
        candidates.addAll(secondIndex.keySet());
        candidates.removeIf(c -> c == null || StringUtils.isBlank(c));
        candidates.sort((a, b) -> b.length() - a.length());
        Set<Long> seen = new HashSet<>();
        while (StringUtils.isNotBlank(remaining)) {
            String cur = remaining.trim();
            // 跳过分隔符
            if (cur.length() > 0 && "、,，;；/|".indexOf(cur.charAt(0)) >= 0) {
                remaining = cur.substring(1);
                continue;
            }
            boolean matched = false;
            for (String cand : candidates) {
                if (cur.startsWith(cand)) {
                    TeachingPlanSupportCandidateItem item = firstIndex.get(cand);
                    if (item == null) {
                        item = secondIndex.get(cand);
                    }
                    if (item != null && item.getId() != null && seen.add(item.getId())) {
                        result.add(item);
                    }
                    remaining = cur.substring(cand.length());
                    matched = true;
                    break;
                }
            }
            if (!matched) {
                issues.add(TeachingPlanImportIssueVo.warn(section, sourceText, issueField,
                        "未匹配到候选课程目标或训练目的「" + cur + "」" + candidateContext(candidates)
                                + "；该段支撑绑定已跳过（Word 原文仍保留展示），其后的候选继续匹配。"
                                + "请核对文字一致性，或在页面「支撑绑定」中重新选择"));
                remaining = skipUnmatchedSegment(cur);
            }
        }
        return result;
    }

    /**
     * 内容行「目的」单元格 -> 训练目的绑定（与 resolveSupportRaw 同套路）：
     * 整串优先；未命中则按名称长度降序，从原文贪心消费最长已知目的名，按出现顺序去重返回。
     * 目的名自身可含「、，；」，先拆碎再逐段精确匹配会把单个名称拆成多段全部失配。
     */
    private List<TeachingPlanTrainingPurpose> resolveContentPurposeList(String raw,
            Map<String, TeachingPlanTrainingPurpose> index, List<TeachingPlanImportIssueVo> issues,
            String section, String sourceText, String issueField) {
        List<TeachingPlanTrainingPurpose> result = new ArrayList<>();
        if (StringUtils.isBlank(raw) || index == null || index.isEmpty()) {
            return result;
        }
        String remaining = raw.trim();
        TeachingPlanTrainingPurpose whole = index.get(remaining);
        if (whole != null && whole.getId() != null) {
            result.add(whole);
            return result;
        }
        List<String> candidates = new ArrayList<>(index.keySet());
        candidates.removeIf(c -> c == null || StringUtils.isBlank(c));
        candidates.sort((a, b) -> b.length() - a.length());
        Set<Long> seen = new HashSet<>();
        while (StringUtils.isNotBlank(remaining)) {
            String cur = remaining.trim();
            if (cur.length() > 0 && "、,，;；/|".indexOf(cur.charAt(0)) >= 0) {
                remaining = cur.substring(1);
                continue;
            }
            boolean matched = false;
            for (String cand : candidates) {
                if (cur.startsWith(cand)) {
                    TeachingPlanTrainingPurpose p = index.get(cand);
                    if (p != null && p.getId() != null && seen.add(p.getId())) {
                        result.add(p);
                    }
                    remaining = cur.substring(cand.length());
                    matched = true;
                    break;
                }
            }
            if (!matched) {
                issues.add(TeachingPlanImportIssueVo.warn(section, sourceText, issueField,
                        "未匹配到训练目的「" + cur + "」" + candidateContext(candidates)
                                + "；该段绑定已跳过，目的列仍显示原文，其后的训练目的继续匹配。"
                                + "请核对文字一致性，或先在本计划的训练目的中录入该目的"));
                remaining = skipUnmatchedSegment(cur);
            }
        }
        return result;
    }

    /** 候选集概况：共 N 条候选，附前 3 个示例，供导入问题说明使用。 */
    private String candidateContext(List<String> names) {
        if (names == null || names.isEmpty()) {
            return "（当前候选集为空，请先在系统中维护对应数据后再导入）";
        }
        List<String> sample = names.stream().distinct()
                .filter(StringUtils::isNotBlank).limit(3).collect(Collectors.toList());
        return "（候选共 " + names.size() + " 条，如：" + String.join("、", sample) + "）";
    }

    /** 跳过原文中当前未匹配段（到下一个分隔符为止），返回剩余待匹配文本；无后续分隔符时返回空串。 */
    private String skipUnmatchedSegment(String cur) {
        if (cur == null) {
            return "";
        }
        for (int i = 0; i < cur.length(); i++) {
            if ("、,，;；/|".indexOf(cur.charAt(i)) >= 0) {
                return cur.substring(i + 1);
            }
        }
        return "";
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
        Map<String, String> labelToValue = new LinkedHashMap<>();
        for (SysDictData d : list) {
            if (d == null || StringUtils.isBlank(d.getDictValue())) {
                continue;
            }
            String value = d.getDictValue().trim();
            String lbl = StringUtils.defaultIfBlank(d.getDictLabel(), value).trim();
            labelToValue.putIfAbsent(lbl, value);
            // 已是字典 value（编码）时原样保留，避免 label 恰为编码的情况误反转
            labelToValue.putIfAbsent(value, value);
        }
        String trimmed = label.trim();
        if (labelToValue.containsKey(trimmed)) {
            return labelToValue.get(trimmed);
        }
        // 多值标签（评价标准等逗号/顿号串）：逐段反查，保留原分隔符，未命中段原样保留
        if (!labelToValue.isEmpty() && label.matches(".*[、,，;；/|].*")) {
            StringBuilder sb = new StringBuilder();
            int last = 0;
            java.util.regex.Matcher m = java.util.regex.Pattern.compile("[、,，;；/|]").matcher(label);
            while (m.find()) {
                String part = label.substring(last, m.start()).trim();
                if (StringUtils.isNotBlank(part)) {
                    sb.append(labelToValue.getOrDefault(part, part));
                }
                sb.append(m.group());
                last = m.end();
            }
            String tail = label.substring(last).trim();
            if (StringUtils.isNotBlank(tail)) {
                sb.append(labelToValue.getOrDefault(tail, tail));
            }
            return sb.toString();
        }
        return label;
    }

    private String normalizeImportKey(String value) {
        return StringUtils.defaultString(value).replaceAll("\\s+", "").trim();
    }
}

