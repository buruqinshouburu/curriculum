package com.doinner.csys.service.impl;

import com.doinner.csys.dao.CourseMapper;
import com.doinner.csys.dao.TeachingPlanMapper;
import com.doinner.csys.dao.TeachingPlanContextMapper;
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
import com.doinner.csys.domain.TeachingPlanContext;
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
import com.doinner.csys.domain.vo.CourseQuoteMajorVo;
import com.doinner.csys.entity.csys.CourseTeachingPlanGenerator;
import com.doinner.csys.entity.csys.model.CourseTeachingPlanModel;
import com.doinner.csys.service.CommonService;
import com.doinner.csys.service.TeachingPlanModuleService;
import com.doinner.csys.service.TeachingPlanService;
import com.doinner.csys.utils.UserUtils;
import com.doinner.file.api.domain.FileInfo;
import com.doinner.file.api.domain.vo.FileInfoVO;
import com.doinner.file.api.service.RemoteFileInfoService;
import com.doinner.kg.domain.Dictionary;
import com.doinner.kg.service.RemoteKgService;
import com.doinner.system.domain.custom.CustomDept;
import com.doinner.system.domain.entity.SysDept;
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

    @Resource
    private TeachingPlanMapper teachingPlanMapper;

    @Resource
    private TeachingPlanContextMapper teachingPlanContextMapper;

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
            if (vo.getTeachCollegeId() != null) {
                vo.setTeachCollegeName(deptIdNameMap.get(vo.getTeachCollegeId()));
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
        // 教学计划id存在则取计划分支(plan基本信息 + c2/ts聚合)；否则从总库课程取
        if (teachingPlanId != null) {
            return teachingPlanMapper.selectDetailByPlanId(teachingPlanId);
        }
        return teachingPlanMapper.selectDetailByCourseId(courseId);
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

        TeachingPlanContext context = saveVo.getContext();
        if (context != null) {
            context.setPlanId(plan.getId());
            UserUtils.reflash(context);
            if (context.getId() == null) {
                teachingPlanContextMapper.insert(context);
            } else {
                teachingPlanContextMapper.updateById(context);
            }
        }
        return plan.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public FileInfo generateTeachingPlanWord(Long courseId) {
        CourseVo course = courseMapper.selectCourseById(courseId);
        if (course == null) {
            throw new IllegalArgumentException("课程不存在: " + courseId);
        }
        // 文档类型：t_csys_course.type -> 模板（1课程 2实践训练课目 3实验课程 4实践项目）
        Integer docType = mapDocType(course.getType());

        // 教学计划（可能不存在，不存在则仅渲染基本信息+空模板表）
        TeachingPlan plan = teachingPlanMapper.selectBySourceCourseId(courseId);
        Long planId = plan == null ? null : plan.getId();
        // 取一个上下文（tab）用于目标/达成设计查询；无上下文则这些表为空
        Long contextId = null;
        if (planId != null) {
            List<TeachingPlanContext> contexts = teachingPlanContextMapper.selectByPlanId(planId);
            if (ObjectUtils.isNotEmpty(contexts)) {
                contextId = contexts.get(0).getId();
            }
        }
        TeachingPlanDetailVo detail = teachingPlanMapper.selectDetailByCourseId(courseId);

        // 组装模型
        CourseTeachingPlanModel model = buildModel(course, plan, detail, planId, contextId);

        try {
            InputStream stream = new CourseTeachingPlanGenerator().generate(model);
            // 清理旧文档（失败不阻断生成）
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
            // 取下载/预览地址，fileId 以文件 id 为准（沿用 TeachingProgrammeServiceImpl.setUrl 约定）
            FileInfo info = fetchFileInfo(fileId);
            String storeId = (info.getId() != null) ? info.getId().toString() : fileId;
            courseMapper.updatePlanFileById(storeId, fileName, info.getDownloadUrl(), info.getPreviewUrl(), courseId);
            return info;
        } catch (IOException e) {
            throw new RuntimeException("生成课程教学计划文档失败: " + e.getMessage(), e);
        }
    }

    /** 组装生成模型：基本信息取自 detail，回退取自 course；模块按 planId/contextId 加载 */
    private CourseTeachingPlanModel buildModel(CourseVo course, TeachingPlan plan, TeachingPlanDetailVo detail,
                                               Long planId, Long contextId) {
        CourseTeachingPlanModel m = new CourseTeachingPlanModel();
        m.setDocType(mapDocType(course.getType()));
        m.setCourseName(nz(detail == null ? null : detail.getCourseName(), course.getName()));
        m.setCourseCode(nz(detail == null ? null : detail.getCourseCode(), course.getCode()));
        m.setCourseEnName(nz(detail == null ? null : detail.getCourseEnName(), course.getEnName()));
        m.setEnabledTerm(nz(detail == null ? null : detail.getEnabledTerm(), plan == null ? null : plan.getEnabledTerm()));
        m.setTeachHours(toStr(detail == null ? null : detail.getTeachHours(), course.getTeachHours()));
        m.setPracticeHours(toStr(detail == null ? null : detail.getPracticeHours(), course.getPracticeHours()));
        m.setHours(toStr(detail == null ? null : detail.getHours(), course.getHours()));
        m.setCredit(toStr(detail == null ? null : detail.getCredit(), course.getCredit()));
        m.setEducationLevel(nz(detail == null ? null : detail.getEducationLevel(), course.getEducationLevel()));
        m.setMajorName(nz(detail == null ? null : detail.getMajorName(), course.getMajorName()));
        m.setTerm(nz(detail == null ? null : detail.getTerm(), course.getOpenTerm()));
        m.setCourseModule(nz(detail == null ? null : detail.getCourseModule(), course.getCourseModule()));
        m.setCourseAttr(nz(detail == null ? null : detail.getCourseAttr(), course.getCourseAttr()));
        m.setScoreRule(plan == null ? null : plan.getScoreRule());

        if (planId != null) {
            m.setTeachers(listTeacher(planId));
            m.setSections(listSection(planId));
            // 目标 + 支撑毕业要求
            List<TeachingPlanObjective> objectives = (contextId == null)
                    ? new ArrayList<>() : teachingPlanModuleService.listObjective(planId, contextId);
            m.setObjectives(objectives);
            Map<Long, List<TeachingPlanObjectiveRef>> refMap = new HashMap<>();
            if (ObjectUtils.isNotEmpty(objectives)) {
                for (TeachingPlanObjective o : objectives) {
                    refMap.put(o.getId(), teachingPlanModuleService.listObjectiveRef(o.getId()));
                }
            }
            m.setObjectiveRefMap(refMap);
            m.setContents(teachingPlanModuleService.listContent(planId));
            // 目标达成设计：知识/能力/素质 三类合并
            List<TeachingPlanTargetDesign> designs = new ArrayList<>();
            if (contextId != null) {
                designs.addAll(teachingPlanModuleService.listTargetDesign(planId, contextId, "知识目标"));
                designs.addAll(teachingPlanModuleService.listTargetDesign(planId, contextId, "能力目标"));
                designs.addAll(teachingPlanModuleService.listTargetDesign(planId, contextId, "素质目标"));
            }
            m.setTargetDesigns(designs);
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
        // 课程绑定的毕业要求
        m.setCourseGraduations(teachingPlanModuleService.listCourseGraduation(course.getId()));
        return m;
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
        // 逻辑参照课程被选用情况(/chooseStatus)：source_id -> 被选用课程 -> 排课 -> 培养方案，
        // 按培养方案 major_id 去重，返回引用该课程的专业类(学科门类/专业类/专业类ID)。
        return trainingSchemeCourseScheduleMapper.selectQuoteMajorsBySourceCourseId(courseId);
    }
}
