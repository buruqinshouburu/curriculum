package com.doinner.csys.service.impl;


import com.alibaba.fastjson.JSONObject;
import com.doinner.common.core.constant.SymbolConstants;
import com.doinner.common.core.domain.Message;
import com.doinner.common.core.exception.DataFormatException;
import com.doinner.common.core.utils.PageUtils;
import com.doinner.common.core.utils.StringUtils;
import com.doinner.common.core.utils.poi.ExcelUtil;
import com.doinner.csys.constant.CourseConstant;
import com.doinner.csys.constant.DomainExceptionConstant;
import com.doinner.csys.constant.DomainFieldConstant;
import com.doinner.csys.dao.*;
import com.doinner.csys.domain.*;
import com.doinner.csys.domain.vo.*;
import com.doinner.csys.io.utils.ExcelUtils;
import com.doinner.csys.service.CurriculumService;
import com.doinner.csys.utils.GenerateCourseCodeUtils;
import com.doinner.csys.utils.TreeBuilderUtils;
import com.doinner.csys.utils.UserUtils;
import com.doinner.kg.domain.Dictionary;
import com.doinner.kg.service.RemoteKgService;
import com.doinner.system.domain.custom.CustomDept;
import com.doinner.system.domain.entity.SysDept;
import com.doinner.system.domain.entity.SysDictData;
import com.doinner.system.service.DoinnerDeptService;
import com.doinner.system.service.DoinnerDictDataService;
import com.google.common.collect.Lists;
import com.google.common.util.concurrent.AtomicDouble;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * 课程Service业务层处理
 *
 * @author doinner
 */
@Service
@Transactional(readOnly = true)
public class CurriculumServiceImpl implements CurriculumService {
    @Resource
    protected CourseMapper courseMapper;
    @Resource
    private CourseTeachingTheoryPlanMapper courseTeachingTheoryPlanMapper;
    @Resource
    private CourseTeachingPracticePlanMapper courseTeachingPracticePlanMapper;
    @Resource
    private CourseChapterMapper courseChapterMapper;
    @Resource
    private KnowledgePointMapper knowledgePointMapper;
    @Resource
    private KnowledgeUnitMapper knowledgeUnitMapper;
    @Resource
    private CourseRefKeUnitMapper courseRefKeUnitMapper;
    @Resource
    private KnowledgeUnitRefPointMapper knowledgeUnitRefPointMapper;
    @Resource
    private KnowledgeDomainMapper knowledgeDomainMapper;
    @Resource
    private CourseTextbookMapper courseTextbookMapper;
    @Resource
    private TrainingSchemeMapper trainingSchemeMapper;
    @Resource
    private KnowledgeUnitRefStdCultivationMapper knowledgeUnitRefStdCultivationMapper;
    @Resource
    private CourseReviewMapper courseReviewMapper;
    @Resource
    private StandardCultivationMapper standardCultivationMapper;
    @Resource
    private KnowledgeNoCheckLogMapper knowledgeNoCheckLogMapper;
    @Resource
    private KnowledgeChekTotalMapper knowledgeChekTotalMapper;
    @Resource
    private KnowledgeCheckLogMapper knowledgeCheckLogMapper;
    @Resource
    private TrainingSchemeRefCourseMapper trainingSchemeRefCourseMapper;
    @Resource
    private TrainingSchemeCourseScheduleMapper trainingSchemeCourseScheduleMapper;
    @Autowired
    private RemoteKgService remoteKgService;

    @Resource
    private DoinnerDeptService doinnerDeptService;

    @Resource
    private CourseRefSourceDomainMapper courseRefSourceDomainMapper;
    @Resource
    private CourseDomainRefSourceUnitMapper courseDomainRefSourceUnitMapper;
    @Resource
    private CourseUnitRefSourcePointMapper courseUnitRefSourcePointMapper;
    @Resource
    private CourseRefGraduationMapper courseRefGraduationMapper;
    @Resource
    private CourseRefAbilityMapper courseRefAbilityMapper;
    @Resource
    private CourseRefQualityMapper courseRefQualityMapper;

    @Resource
    private StandardMajorMapper standardMajorMapper;

    @Resource
    private SourceDomainMapper sourceDomainMapper;

    @Resource
    private TrainingSchemeCategoryMapper trainingSchemeCategoryMapper;

    @Resource
    private StandardGraduationMapper standardGraduationMapper;

    @Resource
    private StandardAbilityMapper standardAbilityMapper;

    @Resource
    private DoinnerDictDataService doinnerDictDataService;

    @Resource
    private CourseTargetMapper courseTargetMapper;

    @Resource
    private TrainingSchemeRefCourseMapper courseRefCourseMapper;

    @Value("${kg.dictionary.courseModuleType:69a7f3162dc370362ef3ee6d}")
    private String kgCourseModuleType;

    /**
     * 查询课程
     *
     * @param id 课程主键
     * @return 课程
     */
    @Override
    public CourseVo selectCourseById(Long id) {
        CourseVo courseVo = courseMapper.selectCourseAndRelevanceById(id);
        if (StringUtils.isNotBlank(courseVo.getBeforeCourseId())) {
            courseVo.setBeforeCourseList(courseMapper.selectCoursesIdAndNameByIds(getList(courseVo.getBeforeCourseId())));
        }
        if (StringUtils.isNotBlank(courseVo.getAfterCourseId())) {
            courseVo.setAfterCourseList(courseMapper.selectCoursesIdAndNameByIds(getList(courseVo.getAfterCourseId())));
        }
        List<KnowledgeDomainVo> knowledgeDomainVoList = courseVo.getKnowledgeDomainVoList();
        if (CollectionUtils.isNotEmpty(knowledgeDomainVoList)) {
            for (KnowledgeDomainVo knowledgeDomainVo : knowledgeDomainVoList) {
                knowledgeDomainVo.setChildren(knowledgeDomainVo.getKnowledgeUnitVoList());
                List<KnowledgeUnitVo> knowledgeUnitVoList = knowledgeDomainVo.getKnowledgeUnitVoList();
                if (CollectionUtils.isNotEmpty(knowledgeUnitVoList)) {
                    for (KnowledgeUnitVo knowledgeUnitVo : knowledgeUnitVoList) {
                        knowledgeUnitVo.setDomainId(knowledgeDomainVo.getId());
                        /*knowledgeUnitVo.setChildren(knowledgeUnitVo.getKnowledgePointVoList());
                        List<KnowledgePointVo> knowledgePointVoList = knowledgeUnitVo.getKnowledgePointVoList();
                        if (CollectionUtils.isNotEmpty(knowledgePointVoList)){
                            for (KnowledgePointVo knowledgePointVo : knowledgePointVoList) {
                                knowledgePointVo.setUnitId(knowledgeUnitVo.getId());
                            }
                        }*/
                    }
                }
            }
        }
        if (courseVo.getMajorId() != null) {
            StandardMajor standardMajor = standardMajorMapper.selectStandardMajorById(courseVo.getMajorId());
            if (standardMajor != null) {
                courseVo.setMajorName(standardMajor.getName());
            }
        }
        if (courseVo.getSubMajorId() != null) {
            StandardMajor standardMajor = standardMajorMapper.selectStandardMajorById(courseVo.getSubMajorId());
            if (standardMajor != null) {
                courseVo.setSubMajorName(standardMajor.getName());
            }
        }
        List<CourseTarget> courseTargets = courseTargetMapper.selectCourseTargetByCourseId(courseVo.getId());
        courseVo.setCourseTargetList(courseTargets);
        if (CollectionUtils.isNotEmpty(courseVo.getCourseTargetList())) {
            for (CourseTarget courseTarget : courseVo.getCourseTargetList()) {
                // 查询知识领域，知识单元，知识点
                List<SourceDomain> sourceDomains = sourceDomainMapper.selectSourceDomainByCourseTargetId(courseTarget.getCourseTargetId());
                courseTarget.setSourceKnowledgeVoList(sourceDomainToCourseSourceKnowledgeVoList(sourceDomains));
                // 组织毕业标准树
                structureGraduation(courseTarget);
                // 组织能力树
                structureAbility(courseTarget);
                // 组织素质树
                structureQuality(courseTarget);
            }
        }
        return courseVo;
    }

    private void structureGraduation(CourseTarget courseTarget) {
        List<CourseRefGraduation> courseRefGraduation = courseRefGraduationMapper.selectCourseRefGraduationByCourseTargetId(courseTarget.getCourseTargetId());
        if (CollectionUtils.isNotEmpty(courseRefGraduation)) {
            List<StandardGraduation> standardGraduations = standardGraduationMapper.selectStandardGraduationByIds(courseRefGraduation.stream().map(a -> a.getGraduationId()).collect(Collectors.toList()));
            List<GraduationTreeVo> graduationTreeVoList = new ArrayList<>();
            if (CollectionUtils.isNotEmpty(standardGraduations)) {
                for (StandardGraduation standardGraduation : standardGraduations) {
                    GraduationTreeVo graduationTreeVo = new GraduationTreeVo();
                    graduationTreeVo.setId(standardGraduation.getId());
                    graduationTreeVo.setName(standardGraduation.getName());
                    graduationTreeVo.setParentId(standardGraduation.getParentId());
                    graduationTreeVoList.add(graduationTreeVo);
                }
                courseTarget.setGraduationTreeVoList(TreeBuilderUtils.buildRootTree(graduationTreeVoList));
            }
        }
    }

    private void structureAbility(CourseTarget courseTarget) {
        List<CourseRefAbility> list = courseRefAbilityMapper.selectCourseRefAbilityByCourseTargetId(courseTarget.getCourseTargetId());
        if (CollectionUtils.isNotEmpty(list)) {
            List<StandardAbility> standardAbilityList = standardAbilityMapper.selectStandardAbilityByIds(list.stream().map(a -> a.getAbilityId()).collect(Collectors.toList()), 2);
            List<StandardTreeVo> AbilityTreeVoList = new ArrayList<>();
            if (CollectionUtils.isNotEmpty(standardAbilityList)) {
                for (StandardAbility standardAbility : standardAbilityList) {
                    StandardTreeVo AbilityTreeVo = new StandardTreeVo();
                    AbilityTreeVo.setId(standardAbility.getId());
                    AbilityTreeVo.setName(standardAbility.getName());
                    AbilityTreeVo.setParentId(standardAbility.getParentId());
                    AbilityTreeVoList.add(AbilityTreeVo);
                }
                courseTarget.setAbilityVoList(TreeBuilderUtils.buildRootTree(AbilityTreeVoList));
            }
        }
    }

    private void structureQuality(CourseTarget courseTarget) {
        List<CourseRefQuality> list = courseRefQualityMapper.selectCourseRefQualityByCourseTargetId(courseTarget.getCourseTargetId());
        if (CollectionUtils.isNotEmpty(list)) {
            List<StandardAbility> standardQualityList = standardAbilityMapper.selectStandardAbilityByIds(list.stream().map(a -> a.getQualityId()).collect(Collectors.toList()), 4);
            List<StandardTreeVo> QualityTreeVoList = new ArrayList<>();
            if (CollectionUtils.isNotEmpty(standardQualityList)) {
                for (StandardAbility standardQuality : standardQualityList) {
                    StandardTreeVo QualityTreeVo = new StandardTreeVo();
                    QualityTreeVo.setId(standardQuality.getId());
                    QualityTreeVo.setName(standardQuality.getName());
                    QualityTreeVo.setParentId(standardQuality.getParentId());
                    QualityTreeVoList.add(QualityTreeVo);
                }
                courseTarget.setQualityVoList(TreeBuilderUtils.buildRootTree(QualityTreeVoList));
            }
        }
    }

    private List<SourceKnowledgeVo> sourceDomainToCourseSourceKnowledgeVoList(List<SourceDomain> sourceDomains) {
        List<SourceKnowledgeVo> sourceKnowledgeVoList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(sourceDomains)) {
            for (SourceDomain reDomain : sourceDomains) {
                SourceKnowledgeVo sourceKnowledgeVo = new SourceKnowledgeVo();
                sourceKnowledgeVo.setId(reDomain.getId());
                // 后续改成一个查询 查询知识单元
                List<SourceKnowledgeVo> unitChildren = new ArrayList<>();
                if (CollectionUtils.isNotEmpty(reDomain.getSourceUnits())) {
                    List<SourceUnit> sourceUnits = reDomain.getSourceUnits();
                    for (SourceUnit sourceUnit : sourceUnits) {
                        SourceKnowledgeVo unitVo = new SourceKnowledgeVo();
                        unitVo.setId(sourceUnit.getId());
                        unitVo.setName(sourceUnit.getName());
                        List<SourceKnowledgeVo> pointChildren = new ArrayList<>();
                        if (CollectionUtils.isNotEmpty(sourceUnit.getSourcePoints())) {
                            for (SourcePoint sourcePoint : sourceUnit.getSourcePoints()) {
                                SourceKnowledgeVo pointVo = new SourceKnowledgeVo();
                                pointVo.setId(sourcePoint.getId());
                                pointVo.setName(sourcePoint.getName());
                                pointChildren.add(pointVo);
                            }
                        }
                        unitVo.setChildren(pointChildren);
                        unitChildren.add(unitVo);
                    }
                }
                sourceKnowledgeVo.setChildren(unitChildren);
                sourceKnowledgeVo.setName(reDomain.getName());
                sourceKnowledgeVoList.add(sourceKnowledgeVo);
            }
        }
        return sourceKnowledgeVoList;
    }

    private List<Long> getList(String courseIds) {
        String[] split = courseIds.split(SymbolConstants.COMMA);
        List<Long> courseIdList = new ArrayList<>();
        for (String s : split) {
            courseIdList.add(Long.valueOf(s));
        }
        return courseIdList;
    }


    /**
     * 查询课程通过知识单元ID
     *
     * @param id 课程主键
     * @return 课程
     */
    @Override
    public CourseVo selectCourseByKnowledgeUnitId(Long id) {
        return courseMapper.selectCourseByKnowledgeUnitId(id);
    }

    /**
     * 查询课程列表
     *
     * @param ids 课程主键
     * @return 课程
     */
    @Override
    public List<Course> selectCourseById(List<Long> ids) {
        return courseMapper.selectCoursesByIds(ids);
    }

    /**
     * 查询课程
     *
     * @param ids 课程主键
     * @return 课程
     */
    @Override
    public List<CourseVo> selectCourseVoById(List<Long> ids) {
        return courseMapper.selectCourseAndRelevanceByIds(ids);
    }

    @Override
    public List<CourseVo> selectCourseAndRelevanceList(Course course) {
        return courseMapper.selectCourseAndRelevanceList(course);
    }

    /**
     * 查询课程列表
     *
     * @param course 课程
     * @return 课程
     */
    @Override
    public List<Course> selectCourseList(CourseVo course) {
        PageUtils.startPage();
        if (StringUtils.isNotEmpty(course.getOrder())) {
            if (CourseConstant.CUR_SORT_ASC.equals(course.getOrder())) {
                course.setOrder("asc");
            }
            if (CourseConstant.CUR_SORT_DESC.equals(course.getOrder())) {
                course.setOrder("desc");
            }
        }
        // course_Module kgCourseModuleType
        Map<String, String> kgDictionaryIdToNameMap = getKgDictionaryIdToNameMap(kgCourseModuleType);
        List<Course> courses = courseMapper.selectCourseList(course);
        Map<Long, String> majorIdToNameMap = standardMajorMapper.selectStandardMajorList(null).stream().collect(Collectors.toMap(StandardMajor::getId, StandardMajor::getName));
        Map<Long, String> categoryToNameMap = trainingSchemeCategoryMapper.selectTrainingSchemeCategoryList(null).stream().collect(Collectors.toMap(TrainingSchemeCategory::getId, TrainingSchemeCategory::getName));
        Map<Long, String> deptIdNameMap = doinnerDeptService.list(new CustomDept()).getData().parallelStream().collect(Collectors.toMap(SysDept::getDeptId,SysDept::getDeptName,(a,b)->a));

        for (Course c : courses) {
            if (MapUtils.isNotEmpty(kgDictionaryIdToNameMap)) {
                if (StringUtils.isNotBlank(c.getCourseModule())) {
                    c.setCourseModuleName(kgDictionaryIdToNameMap.get(c.getCourseModule()));
                }
                if (StringUtils.isNotBlank(c.getCourseModuleChildren())) {
                    c.setCourseModuleChildrenName(kgDictionaryIdToNameMap.get(c.getCourseModuleChildren()));
                }
            }
            if (c.getMajorId() != null) {
                c.setMajorName(majorIdToNameMap.get(c.getMajorId()));
            }
            if (c.getCategoryId() != null) {
                c.setCategoryName(categoryToNameMap.get(c.getCategoryId()));
            }
            if (c.getCollegeId() != null){
                c.setCollegeName(deptIdNameMap.get(c.getCollegeId()));
            }
            if (c.getSubMajorId() != null){
                c.setSubMajorName(majorIdToNameMap.get(c.getSubMajorId()));
            }
            List<CourseTarget> courseTargets = courseTargetMapper.selectCourseTargetByCourseId(c.getId());
            c.setCourseTargetList(courseTargets);
            /*if (CollectionUtils.isNotEmpty(c.getCourseTargetList())) {
                for (CourseTarget courseTarget : c.getCourseTargetList()) {
                    // 查询知识领域，知识单元，知识点
                    List<SourceDomain> sourceDomains = sourceDomainMapper.selectSourceDomainByCourseTargetId(courseTarget.getCourseTargetId());
                    courseTarget.setSourceKnowledgeVoList(sourceDomainToCourseSourceKnowledgeVoList(sourceDomains));
                    // 组织毕业标准树
                    structureGraduation(courseTarget);
                }
            }*/
        }
        return courses;
    }

    @Override
    public List<Course> listSchemeCourse(CourseVo course) {
        // 排序方式归一化: ascending/descending -> asc/desc
        if (StringUtils.isNotBlank(course.getOrder())) {
            if (CourseConstant.CUR_SORT_ASC.equals(course.getOrder())) {
                course.setOrder("asc");
            } else if (CourseConstant.CUR_SORT_DESC.equals(course.getOrder())) {
                course.setOrder("desc");
            }
        }
        List<Course> courses = courseMapper.selectSchemeCourseList(course);
        // 补专业方向名称(SQL 不 join 名称表, 此处按 subMajorId 内存补全, 避免拖慢查询)
        if (ObjectUtils.isNotEmpty(courses)) {
            Map<Long, String> majorIdToNameMap = standardMajorMapper.selectStandardMajorList(null).stream()
                    .collect(Collectors.toMap(StandardMajor::getId, StandardMajor::getName, (a, b) -> a));
            Map<Long, String> categoryIdNameMap = trainingSchemeCategoryMapper.selectTrainingSchemeCategoryList(null)
                    .stream().collect(Collectors.toMap(TrainingSchemeCategory::getId, TrainingSchemeCategory::getName, (a, b) -> a));
            for (Course c : courses) {
                if (c.getSubMajorId() != null) {
                    c.setSubMajorName(majorIdToNameMap.get(c.getSubMajorId()));
                }
                if (c.getMajorId() != null) {
                    c.setMajorName(majorIdToNameMap.get(c.getSubMajorId()));
                }
                if(c.getCategoryId() != null){
                    c.setCategoryName(categoryIdNameMap.get(c.getCategoryId()));
                }
            }
        }
        return courses;
    }


    private final Map<String, String> courseModuleDictionaryIdToNameMap = new HashMap<>();
    private final Map<String, String> courseModuleDictionaryNameToIdMap = new HashMap<>();

    private Map<String, String> getKgDictionaryIdToNameMap(String kgCourseModuleType) {
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

    private Map<String, String> getKgDictionaryNameToIdMap(String kgCourseModuleType) {
        if (MapUtils.isEmpty(courseModuleDictionaryNameToIdMap)) {
            List<Dictionary> data = remoteKgService.findDictionaryByType(kgCourseModuleType).getData();
            if (CollectionUtils.isNotEmpty(data)) {
                for (Dictionary datum : data) {
                    courseModuleDictionaryNameToIdMap.put(datum.getName(), datum.getId().toString());
                }
            }
        }
        return courseModuleDictionaryNameToIdMap;
    }

    /**
     * 自动生成课表编号
     *
     * @param course
     * @return
     */
    private String getCourseCode(CourseVo course) {
        String propertyName = "";
        if (StringUtils.isNotBlank(course.getCourseModuleChildren())) {
            propertyName = remoteKgService.getDictionary(course.getCourseModuleChildren()).getData().getName();
            if (StringUtils.isBlank(propertyName) && StringUtils.isNotBlank(course.getCourseModule())) {
                propertyName = remoteKgService.getDictionary(course.getCourseModule()).getData().getName();
            }
        }
        if (StringUtils.isBlank(course.getCollegeName()) && course.getCollegeId() != null) {
            SysDept sysDept = doinnerDeptService.getInfo(course.getCollegeId()).getData();
            if (sysDept != null) {
                course.setCollegeName(sysDept.getDeptName());
            }
        }
        String prefixCode = GenerateCourseCodeUtils.getPrefixCode(course.getCollegeName(), propertyName, course.getCourseAttr());
        List<Course> courses = courseMapper.selectCoursesByCodeLike(prefixCode);
        if (CollectionUtils.isNotEmpty(courses)) {
            List<Integer> codeIds = new ArrayList<>();
            for (Course c : courses) {
                codeIds.add(Integer.parseInt(c.getCode().replace(prefixCode, "")));
            }
            return GenerateCourseCodeUtils.getCourseCode(prefixCode, Collections.max(codeIds));
        } else {
            return GenerateCourseCodeUtils.getCourseCode(prefixCode, 0);
        }
    }

    private String getCourseCode_new(CourseVo course) {
        //课程编号=版本（**）+培训层次(*)+开课单位(**)+流水号(****)
        String prefixCode = GenerateCourseCodeUtils.getPrefixCode_new(course.getVersion(), course.getCollegeName(), course.getEducationLevel());
        List<Course> courses = courseMapper.selectCoursesByCodeLike(prefixCode);
        if (CollectionUtils.isNotEmpty(courses)) {
            List<Integer> codeIds = new ArrayList<>();
            for (Course c : courses) {
                codeIds.add(Integer.parseInt(c.getCode().replace(prefixCode, "")));
            }
            return GenerateCourseCodeUtils.getCourseCode(prefixCode, Collections.max(codeIds));
        } else {
            return GenerateCourseCodeUtils.getCourseCode(prefixCode, 0);
        }
    }

    private boolean checkCourseRepetition(CourseVo course) {
        if (course.getTemplateType() == 2 || ObjectUtils.isNotEmpty(course.getSourceId())) {
            //调用的课程不进行名称查重
            return true;
        }
        Long count = courseMapper.selectByNameCount(course);
        if (count > 0) {
            // throw new RuntimeException(course.getName()+",课程存在重名，请更换一个名称！");
            return false;
        }
        return true;
    }

    /**
     * 新增课程
     *
     * @param course 课程
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = {Exception.class})
    public CourseVo insertCourse(CourseVo course) {
        if (!checkCourseRepetition(course)) {
            throw new RuntimeException(course.getName() + "存在重名，建议后缀加A/B/数字区分，如"+course.getName() +"A");
        }
        UserUtils.reflash(course);
        if (course.getId() != null) {
            courseMapper.updateCourse(course);
        } else {
            // 实践项目课程不用生成课程编号
            if (StringUtils.isNotBlank(course.getType()) && !"4".equals(course.getType())) {
                course.setCode(getCourseCode_new(course));
            }
            courseMapper.insertCourse(course);
        }
        dealCourseTarget(course);
        if (!"1".equals(course.getType())) {
            CourseTeachingPracticePlan courseTeachingPracticePlan = course.getCourseTeachingPracticePlanVo();
            if (courseTeachingPracticePlan == null || courseTeachingPracticePlan.getId() == null) {
                courseTeachingPracticePlan = new CourseTeachingPracticePlan();
                courseTeachingPracticePlan.setCourseId(course.getId());
                UserUtils.reflash(courseTeachingPracticePlan);
                courseTeachingPracticePlanMapper.insertCourseTeachingPracticePlan(courseTeachingPracticePlan);
                CourseTeachingPracticePlanVo courseTeachingPracticePlanVo = new CourseTeachingPracticePlanVo();
                BeanUtils.copyProperties(courseTeachingPracticePlan, courseTeachingPracticePlanVo);
                course.setCourseTeachingPracticePlanVo(courseTeachingPracticePlanVo);
            }
        }
        if (!"2".equals(course.getType())) {
            CourseTeachingTheoryPlan courseTeachingTheoryPlan = course.getCourseTeachingTheoryPlanVo();
            if (courseTeachingTheoryPlan == null || courseTeachingTheoryPlan.getId() == null) {
                courseTeachingTheoryPlan = new CourseTeachingTheoryPlan();
                courseTeachingTheoryPlan.setCourseId(course.getId());
                UserUtils.reflash(courseTeachingTheoryPlan);
                courseTeachingTheoryPlanMapper.insertCourseTeachingTheoryPlan(courseTeachingTheoryPlan);
                CourseTeachingTheoryPlanVo courseTeachingTheoryPlanVo = new CourseTeachingTheoryPlanVo();
                BeanUtils.copyProperties(courseTeachingTheoryPlan, courseTeachingTheoryPlanVo);
                course.setCourseTeachingTheoryPlanVo(courseTeachingTheoryPlanVo);
            }
        }
        return course;
    }

    /**
     * 新增时新增关联表信息
     */
    public void insertRelevance(CourseVo course) {
        // todo 注意这个方法是课程信息保存的公用方法
        //理论教学计划
        if (course.getCourseTeachingTheoryPlanVo() != null && course.getCourseTeachingTheoryPlanVo().getId() != null) {
            CourseTeachingTheoryPlanVo courseTeachingTheoryPlan = course.getCourseTeachingTheoryPlanVo();
            UserUtils.reflash(courseTeachingTheoryPlan);
            courseTeachingTheoryPlanMapper.updateCourseTeachingTheoryPlan(courseTeachingTheoryPlan);
        }
        //实践教学计划
        if (course.getCourseTeachingPracticePlanVo() != null && course.getCourseTeachingPracticePlanVo().getId() != null) {
            CourseTeachingPracticePlanVo courseTeachingPracticePlan = course.getCourseTeachingPracticePlanVo();
            UserUtils.reflash(courseTeachingPracticePlan);
            courseTeachingPracticePlanMapper.updateCourseTeachingPracticePlan(courseTeachingPracticePlan);
        }
        //知识领域修改
        /*List<KnowledgeDomainVo> knowledgeDomainVoList = course.getKnowledgeDomainVoList();
        if (CollectionUtils.isNotEmpty(knowledgeDomainVoList)) {
            for (KnowledgeDomainVo knowledgeDomainVo : knowledgeDomainVoList) {
                List<KnowledgeUnitVo> knowledgeUnitVoList = knowledgeDomainVo.getKnowledgeUnitVoList();
                if (CollectionUtils.isNotEmpty(knowledgeUnitVoList)) {
                    for (KnowledgeUnitVo knowledgeUnitVo : knowledgeUnitVoList) {
                        UserUtils.reflash(knowledgeUnitVo);
                        knowledgeUnitMapper.updateKnowledgeUnit(knowledgeUnitVo);
                        List<KnowledgePointVo> knowledgePointVoList = knowledgeUnitVo.getKnowledgePointVoList();
                        if (CollectionUtils.isNotEmpty(knowledgePointVoList)) {
                            KnowledgeUnitRefPoint knowledgeUnitRefPoint = new KnowledgeUnitRefPoint();
                            knowledgeUnitRefPoint.setUnitId(knowledgeUnitVo.getId());
                            List<Long> pointIds = knowledgeUnitRefPointMapper.selectKnowledgeUnitRefPointList(knowledgeUnitRefPoint).stream().map(KnowledgeUnitRefPoint::getPointId).filter(ObjectUtils::isNotEmpty).collect(Collectors.toList());
                            List<Long> deletePointIds = new ArrayList<>();
                            List<Long> existPointIds = new ArrayList<>();
                            for (KnowledgePointVo knowledgePointVo : knowledgePointVoList) {
                                if (knowledgePointVo.getId() != null) {
                                    existPointIds.add(knowledgePointVo.getId());
                                    UserUtils.reflash(knowledgePointVo);
                                    knowledgePointMapper.updateKnowledgePoint(knowledgePointVo);
                                } else {
                                    if (StringUtils.isNotBlank(knowledgePointVo.getName())) {
                                        UserUtils.reflash(knowledgePointVo);
                                        knowledgePointMapper.insertKnowledgePoint(knowledgePointVo);
                                        KnowledgeUnitRefPoint unitRefPoint = new KnowledgeUnitRefPoint();
                                        unitRefPoint.setUnitId(knowledgeUnitVo.getId());
                                        unitRefPoint.setPointId(knowledgePointVo.getId());
                                        knowledgeUnitRefPointMapper.insertKnowledgeUnitRefPoint(unitRefPoint);
                                    }
                                }
                            }
                            if (CollectionUtils.isNotEmpty(pointIds)) {
                                for (Long pointId : pointIds) {
                                    if (!existPointIds.contains(pointId)) {
                                        deletePointIds.add(pointId);
                                    }
                                }
                                if (CollectionUtils.isNotEmpty(deletePointIds)) {
                                    knowledgeUnitRefPointMapper.deleteKnowledgeUnitRefPointByPointIds(deletePointIds);
                                    knowledgePointMapper.deleteKnowledgePointByIds(deletePointIds);
                                }
                            }
                        }
                    }
                }
            }
        }*/

        // 处理课程目标
        dealCourseTarget(course);

        // dealSourceKnowledgeVoList(course.getSourceKnowledgeVoList(),course.getId());
        /*// 树结构 课程和毕业标准
        Long graduationCollegeId = null;
        Long graduationCategoryId = null;
        Long graduationMajorId = null;
        *//*if (CollectionUtils.isNotEmpty(course.getGraduationTreeVoList())) {
            graduationCollegeId = course.getGraduationTreeVoList().get(0).getCollegeId();
            graduationCategoryId = course.getGraduationTreeVoList().get(0).getCategoryId();
            graduationMajorId = course.getGraduationTreeVoList().get(0).getMajorId();
        }*//*
        courseRefGraduationMapper.deleteCourseRefGraduationByCourseId(course.getId(),graduationCollegeId,graduationCategoryId,graduationMajorId);
        if (CollectionUtils.isNotEmpty(course.getGraduationTreeVoList())){
            List<GraduationTreeVo> graduationTreeVos = TreeBuilderUtils.flattenTree(course.getGraduationTreeVoList());
            for (GraduationTreeVo graduationTreeVo : graduationTreeVos) {
                CourseRefGraduation courseRefGraduation = new CourseRefGraduation();
                courseRefGraduation.setCourseId(course.getId());
                courseRefGraduation.setGraduationId(graduationTreeVo.getId());
                courseRefGraduation.setCollegeId(graduationTreeVo.getCollegeId());
                courseRefGraduation.setCategoryId(graduationTreeVo.getCategoryId());
                courseRefGraduation.setMajorId(graduationTreeVo.getMajorId());
                courseRefGraduationMapper.insertCourseRefGraduation(courseRefGraduation);
            }
        }
        // 树结构 课程和能力
        Long abilityCollegeId = null;
        Long abilityCategoryId = null;
        Long abilityMajorId = null;
        *//*if (CollectionUtils.isNotEmpty(course.getAbilityVoList())) {
            abilityCollegeId = course.getAbilityVoList().get(0).getCollegeId();
            abilityCategoryId = course.getAbilityVoList().get(0).getCategoryId();
            abilityMajorId = course.getAbilityVoList().get(0).getMajorId();
        }*//*
        courseRefAbilityMapper.deleteCourseRefAbilityByCourseId(course.getId(),abilityCollegeId,abilityCategoryId,abilityMajorId);
        if (CollectionUtils.isNotEmpty(course.getAbilityVoList())){
            List<StandardTreeVo> requestList = TreeBuilderUtils.flattenTree(course.getAbilityVoList());
            for (StandardTreeVo request : requestList) {
                CourseRefAbility vo = new CourseRefAbility();
                vo.setCourseId(course.getId());
                vo.setAbilityId(request.getId());
                vo.setCollegeId(request.getCollegeId());
                vo.setCategoryId(request.getCategoryId());
                vo.setMajorId(request.getMajorId());
                courseRefAbilityMapper.insertCourseRefAbility(vo);
            }
        }
        // 树结构 课程和素质
        Long qualityCollegeId = null;
        Long qualityCategoryId = null;
        Long qualityMajorId = null;
        if (CollectionUtils.isNotEmpty(course.getQualityVoList())) {
            qualityCollegeId = course.getQualityVoList().get(0).getCollegeId();
            qualityCategoryId = course.getQualityVoList().get(0).getCategoryId();
            qualityMajorId = course.getQualityVoList().get(0).getMajorId();
        }
        courseRefQualityMapper.deleteCourseRefQualityByCourseId(course.getId(),qualityCollegeId,qualityCategoryId,qualityMajorId);
        if (CollectionUtils.isNotEmpty(course.getQualityVoList())){
            List<StandardTreeVo> requestList = TreeBuilderUtils.flattenTree(course.getQualityVoList());
            for (StandardTreeVo request : requestList) {
                CourseRefQuality vo = new CourseRefQuality();
                vo.setCourseId(course.getId());
                vo.setQualityId(request.getId());
                vo.setCollegeId(request.getCollegeId());
                vo.setCategoryId(request.getCategoryId());
                vo.setMajorId(request.getMajorId());
                courseRefQualityMapper.insertCourseRefQuality(vo);
            }
        }*/
        //课程章节表
        CourseChapter chapter = new CourseChapter();
        chapter.setCourseId(course.getId());
        List<Long> courseChapters = courseChapterMapper.selectCourseChapterList(chapter).stream().map(CourseChapter::getId).filter(ObjectUtils::isNotEmpty).collect(Collectors.toList());
        List<Long> deleteCourseChapters = new ArrayList<>();
        List<Long> existCourseChapters = new ArrayList<>();
        List<CourseChapterVo> courseChapterList = course.getCourseChapterVoList();
        if (CollectionUtils.isNotEmpty(courseChapterList)) {
            for (CourseChapter courseChapter : courseChapterList) {
                UserUtils.reflash(courseChapter);
                if (courseChapter.getId() == null) {
                    courseChapter.setCourseId(course.getId());
                    courseChapterMapper.insertCourseChapter(courseChapter);
                } else {
                    existCourseChapters.add(courseChapter.getId());
                    courseChapterMapper.updateCourseChapter(courseChapter);
                }
            }
            course.setCourseChapterVoList(courseChapterList);
        }
        if (CollectionUtils.isNotEmpty(courseChapters)) {
            for (Long courseChapter : courseChapters) {
                if (!existCourseChapters.contains(courseChapter)) {
                    deleteCourseChapters.add(courseChapter);
                }
            }
            if (CollectionUtils.isNotEmpty(deleteCourseChapters)) {
                courseChapterMapper.deleteCourseChapterByIds(deleteCourseChapters);
            }
        }
        //课程教材参考书
        CourseTextbook textBook = new CourseTextbook();
        textBook.setCourseId(course.getId());
        List<Long> courseTextbookIds = courseTextbookMapper.selectCourseTextbookList(textBook).stream().map(CourseTextbook::getId).filter(ObjectUtils::isNotEmpty).collect(Collectors.toList());
        List<Long> deleteTextbookIds = new ArrayList<>();
        List<Long> existBookIds = new ArrayList<>();
        List<CourseTextbookVo> courseTextbookList = course.getCourseTextbookVoList();
        if (CollectionUtils.isNotEmpty(courseTextbookList)) {
            for (CourseTextbook courseTextbook : courseTextbookList) {
                UserUtils.reflash(courseTextbook);
                if (courseTextbook.getId() == null) {
                    courseTextbook.setCourseId(course.getId());
                    courseTextbookMapper.insertCourseTextbook(courseTextbook);
                } else {
                    existBookIds.add(courseTextbook.getId());
                    courseTextbookMapper.updateCourseTextbook(courseTextbook);
                }
            }
            course.setCourseTextbookVoList(courseTextbookList);
        }
        if (CollectionUtils.isNotEmpty(courseTextbookIds)) {
            for (Long courseTextbookId : courseTextbookIds) {
                if (!existBookIds.contains(courseTextbookId)) {
                    deleteTextbookIds.add(courseTextbookId);
                }
            }
            if (CollectionUtils.isNotEmpty(deleteTextbookIds)) {
                courseTextbookMapper.deleteCourseTextbookByIds(deleteTextbookIds);
            }
        }
        this.deleteAllCheckLog(course.getId());
    }

    private void dealCourseTarget(CourseVo course) {
        if (CollectionUtils.isNotEmpty(course.getCourseTargetList())) {
            List<CourseTarget> courseTargets = courseTargetMapper.selectCourseTargetByCourseId(course.getId());
            List<Long> dbIds = courseTargets.stream().map(CourseTarget::getCourseTargetId).collect(Collectors.toList());
            List<Long> existIds = new ArrayList<>();
            for (CourseTarget courseTarget : course.getCourseTargetList()) {
                UserUtils.reflash(courseTarget);
                courseTarget.setCourseId(course.getId());
                if (courseTarget.getCourseTargetId() == null) {
                    courseTargetMapper.insertCourseTarget(courseTarget);
                } else {
                    existIds.add(courseTarget.getCourseTargetId());
                    courseTargetMapper.updateCourseTarget(courseTarget);
                }
            }
            List<Long> deleteIds = new ArrayList<>();
            if (CollectionUtils.isNotEmpty(dbIds)) {
                for (Long dbId : dbIds) {
                    if (!existIds.contains(dbId)) {
                        deleteIds.add(dbId);
                    }
                }
            }
            if (CollectionUtils.isNotEmpty(deleteIds)) {
                courseTargetMapper.deleteCourseTargetByIds(deleteIds);
            }
        }
    }

    /**
     * 处理新的课程和知识领域知识单元对应关系
     *
     * @param sourceKnowledgeVoList
     * @param courseId
     */
    private void dealSourceKnowledgeVoList(List<SourceKnowledgeVo> sourceKnowledgeVoList, Long courseId, Long courseTargetId) {
        // 先删
        //  删除的时候带 学院 门类 专业条件 ，不能删其他专业和该课程的关联数据 判空
        Long collegeId = null;
        Long categoryId = null;
        Long majorId = null;
        /*if (CollectionUtils.isNotEmpty(sourceKnowledgeVoList)) {
            collegeId = sourceKnowledgeVoList.get(0).getCollegeId();
            categoryId = sourceKnowledgeVoList.get(0).getCategoryId();
            majorId = sourceKnowledgeVoList.get(0).getMajorId();
        }*/
        courseUnitRefSourcePointMapper.deleteCourseUnitRefSourcePointByCourseId(courseId, courseTargetId, collegeId, categoryId, majorId);
        courseDomainRefSourceUnitMapper.deleteCourseDomainRefSourceUnitByCourseId(courseId, courseTargetId, collegeId, categoryId, majorId);
        courseRefSourceDomainMapper.deleteCourseRefSourceDomainByCourseId(courseId, courseTargetId, collegeId, categoryId, majorId);
        // 后增
        if (CollectionUtils.isNotEmpty(sourceKnowledgeVoList)) {
            for (SourceKnowledgeVo sourceKnowledgeVo : sourceKnowledgeVoList) {
                // 第一层是知识领域
                CourseRefSourceDomain courseRefSourceDomain = new CourseRefSourceDomain();
                courseRefSourceDomain.setCourseId(courseId);
                courseRefSourceDomain.setDomainId(sourceKnowledgeVo.getId());
                courseRefSourceDomain.setCollegeId(sourceKnowledgeVo.getCollegeId());
                courseRefSourceDomain.setCategoryId(sourceKnowledgeVo.getCategoryId());
                courseRefSourceDomain.setMajorId(sourceKnowledgeVo.getMajorId());
                courseRefSourceDomain.setCourseTargetId(courseTargetId);
                List<SourceKnowledgeVo> unitChildren = sourceKnowledgeVo.getChildren();
                if (CollectionUtils.isNotEmpty(unitChildren)) {
                    // 第二层知识单元
                    for (SourceKnowledgeVo unitChild : unitChildren) {
                        CourseDomainRefSourceUnit courseDomainRefSourceUnit = new CourseDomainRefSourceUnit();
                        courseDomainRefSourceUnit.setCourseId(courseId);
                        courseDomainRefSourceUnit.setDomainId(sourceKnowledgeVo.getId());
                        courseDomainRefSourceUnit.setUnitId(unitChild.getId());
                        courseDomainRefSourceUnit.setCollegeId(unitChild.getCollegeId());
                        courseDomainRefSourceUnit.setCategoryId(unitChild.getCategoryId());
                        courseDomainRefSourceUnit.setMajorId(unitChild.getMajorId());
                        courseDomainRefSourceUnit.setCourseTargetId(courseTargetId);
                        // 第三层知识点
                        List<SourceKnowledgeVo> pointChildren = unitChild.getChildren();
                        if (CollectionUtils.isNotEmpty(pointChildren)) {
                            for (SourceKnowledgeVo pointChild : pointChildren) {
                                CourseUnitRefSourcePoint courseUnitRefSourcePoint = new CourseUnitRefSourcePoint();
                                courseUnitRefSourcePoint.setCourseId(courseId);
                                courseUnitRefSourcePoint.setUnitId(unitChild.getId());
                                courseUnitRefSourcePoint.setPointId(pointChild.getId());
                                courseUnitRefSourcePoint.setCollegeId(pointChild.getCollegeId());
                                courseUnitRefSourcePoint.setCategoryId(pointChild.getCategoryId());
                                courseUnitRefSourcePoint.setMajorId(pointChild.getMajorId());
                                courseUnitRefSourcePoint.setCourseTargetId(courseTargetId);
                                courseUnitRefSourcePointMapper.insertCourseUnitRefSourcePoint(courseUnitRefSourcePoint);
                            }
                        }
                        courseDomainRefSourceUnitMapper.insertCourseDomainRefSourceUnit(courseDomainRefSourceUnit);
                    }
                }
                if (courseRefSourceDomain.getDomainId() != null) {
                    courseRefSourceDomainMapper.insertCourseRefSourceDomain(courseRefSourceDomain);
                }
            }
        }
    }


    @Override
    @Transactional(rollbackFor = {Exception.class})
    public CourseTarget courseTargetConfiguration(CourseTarget courseTarget) {
        dealSourceKnowledgeVoList(courseTarget.getSourceKnowledgeVoList(), courseTarget.getCourseId(), courseTarget.getCourseTargetId());
        // 树结构 课程和毕业标准
        Long graduationCollegeId = null;
        Long graduationCategoryId = null;
        Long graduationMajorId = null;
        /*if (CollectionUtils.isNotEmpty(course.getGraduationTreeVoList())) {
            graduationCollegeId = course.getGraduationTreeVoList().get(0).getCollegeId();
            graduationCategoryId = course.getGraduationTreeVoList().get(0).getCategoryId();
            graduationMajorId = course.getGraduationTreeVoList().get(0).getMajorId();
        }*/
        courseRefGraduationMapper.deleteCourseRefGraduationByCourseId(courseTarget.getCourseId(), courseTarget.getCourseTargetId(), graduationCollegeId, graduationCategoryId, graduationMajorId);
        if (CollectionUtils.isNotEmpty(courseTarget.getGraduationTreeVoList())) {
            List<GraduationTreeVo> graduationTreeVos = TreeBuilderUtils.flattenTree(courseTarget.getGraduationTreeVoList());
            for (GraduationTreeVo graduationTreeVo : graduationTreeVos) {
                CourseRefGraduation courseRefGraduation = new CourseRefGraduation();
                courseRefGraduation.setCourseId(courseTarget.getCourseId());
                courseRefGraduation.setCourseTargetId(courseTarget.getCourseTargetId());
                courseRefGraduation.setGraduationId(graduationTreeVo.getId());
                courseRefGraduation.setCollegeId(graduationTreeVo.getCollegeId());
                courseRefGraduation.setCategoryId(graduationTreeVo.getCategoryId());
                courseRefGraduation.setMajorId(graduationTreeVo.getMajorId());
                courseRefGraduationMapper.insertCourseRefGraduation(courseRefGraduation);
            }
        }
        // 树结构 课程和能力
        Long abilityCollegeId = null;
        Long abilityCategoryId = null;
        Long abilityMajorId = null;
        /*if (CollectionUtils.isNotEmpty(course.getAbilityVoList())) {
            abilityCollegeId = course.getAbilityVoList().get(0).getCollegeId();
            abilityCategoryId = course.getAbilityVoList().get(0).getCategoryId();
            abilityMajorId = course.getAbilityVoList().get(0).getMajorId();
        }*/
        courseRefAbilityMapper.deleteCourseRefAbilityByCourseId(courseTarget.getCourseId(), courseTarget.getCourseTargetId(), abilityCollegeId, abilityCategoryId, abilityMajorId);
        if (CollectionUtils.isNotEmpty(courseTarget.getAbilityVoList())) {
            List<StandardTreeVo> requestList = TreeBuilderUtils.flattenTree(courseTarget.getAbilityVoList());
            for (StandardTreeVo request : requestList) {
                CourseRefAbility vo = new CourseRefAbility();
                vo.setCourseId(courseTarget.getCourseId());
                vo.setCourseTargetId(courseTarget.getCourseTargetId());
                vo.setAbilityId(request.getId());
                vo.setCollegeId(request.getCollegeId());
                vo.setCategoryId(request.getCategoryId());
                vo.setMajorId(request.getMajorId());
                courseRefAbilityMapper.insertCourseRefAbility(vo);
            }
        }
        // 树结构 课程和素质
        Long qualityCollegeId = null;
        Long qualityCategoryId = null;
        Long qualityMajorId = null;
        /*if (CollectionUtils.isNotEmpty(courseTarget.getQualityVoList())) {
            qualityCollegeId = courseTarget.getQualityVoList().get(0).getCollegeId();
            qualityCategoryId = courseTarget.getQualityVoList().get(0).getCategoryId();
            qualityMajorId = courseTarget.getQualityVoList().get(0).getMajorId();
        }*/
        courseRefQualityMapper.deleteCourseRefQualityByCourseId(courseTarget.getCourseId(), courseTarget.getCourseTargetId(), qualityCollegeId, qualityCategoryId, qualityMajorId);
        if (CollectionUtils.isNotEmpty(courseTarget.getQualityVoList())) {
            List<StandardTreeVo> requestList = TreeBuilderUtils.flattenTree(courseTarget.getQualityVoList());
            for (StandardTreeVo request : requestList) {
                CourseRefQuality vo = new CourseRefQuality();
                vo.setCourseId(courseTarget.getCourseId());
                vo.setCourseTargetId(courseTarget.getCourseTargetId());
                vo.setQualityId(request.getId());
                vo.setCollegeId(request.getCollegeId());
                vo.setCategoryId(request.getCategoryId());
                vo.setMajorId(request.getMajorId());
                courseRefQualityMapper.insertCourseRefQuality(vo);
            }
        }
        dealBindStatus(courseTarget);
        return courseTarget;
    }

    private void dealBindStatus(CourseTarget courseTarget) {
        // 设置课程绑定状态 判断课程每一个课程目标都需要绑定课程和毕业要求，训练只需要绑定毕业要求
        CourseVo courseVo = courseMapper.selectCourseById(courseTarget.getCourseId());
        if ("1".equals(courseVo.getType()) || "3".equals(courseVo.getType())) {
            List<CourseTarget> courseTargets = courseTargetMapper.selectCourseTargetByCourseId(courseVo.getId());
            if (CollectionUtils.isNotEmpty(courseTargets)) {
                boolean bindStatusFlag = true;
                for (CourseTarget target : courseTargets) {
                    if (!target.getCourseTargetId().equals(courseTarget.getCourseTargetId())) {
                        List<SourceDomain> sourceDomains = sourceDomainMapper.selectSourceDomainByCourseTargetId(target.getCourseTargetId());
                        if (CollectionUtils.isEmpty(sourceDomains)) {
                            bindStatusFlag = false;
                        }
                        List<CourseRefGraduation> courseRefGraduation = courseRefGraduationMapper.selectCourseRefGraduationByCourseTargetId(target.getCourseTargetId());
                        if (CollectionUtils.isEmpty(courseRefGraduation)) {
                            bindStatusFlag = false;
                        }
                    }
                }
                if (bindStatusFlag && CollectionUtils.isNotEmpty(courseTarget.getSourceKnowledgeVoList()) && CollectionUtils.isNotEmpty(courseTarget.getGraduationTreeVoList())) {
                    courseVo.setBindStatus(1);
                    courseMapper.updateCourse(courseVo);
                } else {
                    courseVo.setBindStatus(2);
                    courseMapper.updateCourse(courseVo);
                }
            }
        } else if ("2".equals(courseVo.getType())) {
            List<CourseTarget> courseTargets = courseTargetMapper.selectCourseTargetByCourseId(courseVo.getId());
            if (CollectionUtils.isNotEmpty(courseTargets)) {
                boolean bindStatusFlag = true;
                for (CourseTarget target : courseTargets) {
                    if (!target.getCourseTargetId().equals(courseTarget.getCourseTargetId())) {
                        List<CourseRefGraduation> courseRefGraduation = courseRefGraduationMapper.selectCourseRefGraduationByCourseTargetId(target.getCourseTargetId());
                        if (CollectionUtils.isEmpty(courseRefGraduation)) {
                            bindStatusFlag = false;
                        }
                    }
                }
                if (bindStatusFlag && CollectionUtils.isNotEmpty(courseTarget.getGraduationTreeVoList())) {
                    courseVo.setBindStatus(1);
                    courseMapper.updateCourse(courseVo);
                } else {
                    courseVo.setBindStatus(2);
                    courseMapper.updateCourse(courseVo);
                }
            }
        }
    }


    @Override
    @Transactional(rollbackFor = {Exception.class})
    public int deleteFile(Long id) {
        return courseMapper.deleteFile(id);
    }

    /**
     * 新增课程
     * 给导入使用，只有name,type,code,collegeId4个字段
     *
     * @param courseList 课程集合
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = {Exception.class})
    public int insertCourse(List<Course> courseList) {
        if (ObjectUtils.isEmpty(courseList)) {
            return 1;
        }
        List<String> codeList = courseList.parallelStream().map(Course::getCode).collect(Collectors.toList());
        List<String> existsCodeList = courseMapper.selectCodeByCodes(codeList);
        if (ObjectUtils.isNotEmpty(existsCodeList)) {
            existsCodeList = existsCodeList.parallelStream().distinct().collect(Collectors.toList());
            String errMessage = String.format(DomainExceptionConstant.COURSE_CODE_IS_EXISTS, existsCodeList.parallelStream().collect(Collectors.joining(SymbolConstants.COMMA)));
            throw new DataFormatException(errMessage);
        }
        for (Course course : courseList) {
            UserUtils.reflash(course);
        }
        courseMapper.insertCourses(courseList);

        List<CourseTeachingPracticePlan> courseTeachingPracticePlanList = Lists.newArrayList();
        for (Course course : courseList) {
            if ("1".equals(course.getType())) {
                continue;
            }
            CourseTeachingPracticePlan courseTeachingPracticePlan = new CourseTeachingPracticePlan();
            courseTeachingPracticePlan.setCourseId(course.getId());
            UserUtils.reflash(courseTeachingPracticePlan);
            courseTeachingPracticePlanList.add(courseTeachingPracticePlan);
        }

        List<CourseTeachingTheoryPlan> courseTeachingTheoryPlanList = Lists.newArrayList();
        for (Course course : courseList) {
            if ("1".equals(course.getType())) {
                continue;
            }
            CourseTeachingTheoryPlan courseTeachingTheoryPlan = new CourseTeachingTheoryPlan();
            courseTeachingTheoryPlan.setCourseId(course.getId());
            UserUtils.reflash(courseTeachingTheoryPlan);
            courseTeachingTheoryPlanList.add(courseTeachingTheoryPlan);
        }
        if (CollectionUtils.isNotEmpty(courseTeachingTheoryPlanList)) {
            courseTeachingTheoryPlanMapper.insertCourseTeachingTheoryPlans(courseTeachingTheoryPlanList);
        }
        if (CollectionUtils.isNotEmpty(courseTeachingPracticePlanList)) {
            courseTeachingPracticePlanMapper.insertCourseTeachingPracticePlans(courseTeachingPracticePlanList);
        }
        return 1;
    }

    /**
     * 修改课程
     *
     * @param course 课程
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = {Exception.class})
    public CourseVo updateCourse(CourseVo course) {
//        if (course.getCollegeId() != null){
//            checkCourseRepetition(course);
//        }
        if (!checkCourseRepetition(course)) {
            throw new RuntimeException(course.getName() + "存在重名，请更换一个名称！");
        }
        ;
        CourseVo dbCourse = courseMapper.selectCourseById(course.getId());
        UserUtils.checkDataPermission(dbCourse);
        //查看版本（**）+培训层次(*)+开课单位(**)是否有修改 修改的话更新课程编号
        updateCourseCode(dbCourse,course);
        updateInvokeCourse(course);
        TrainingSchemeRefCourse query = new TrainingSchemeRefCourse();
        query.setCourseId(dbCourse.getId());
//        List<TrainingSchemeRefCourse> trainingSchemeRefCourses = courseRefCourseMapper.selectTrainingSchemeRefCourseList(query);
//        if (CollectionUtils.isNotEmpty(trainingSchemeRefCourses)) {
//            List<TrainingSchemeVo> trainingSchemeVos = trainingSchemeMapper.selectTrainingSchemeByIds(trainingSchemeRefCourses.stream().map(a -> a.getSchemeId()).collect(Collectors.toList()));
//            throw new RuntimeException("课程已被培养方案引用:" + trainingSchemeVos.stream().map(TrainingSchemeVo::getName).collect(Collectors.joining(",")) + "，不能修改！");
//        }
        // this.deleteRelevance(course);
        course.setStatus(0);
        UserUtils.reflash(course);
        courseMapper.updateCourse(course);
        this.insertRelevance(course);
        return course;
    }

    private void updateInvokeCourse(CourseVo newCourse) {
        courseMapper.updateInvokeCourse(newCourse);
    }

    private void updateCourseCode(CourseVo sourceCourse, CourseVo newCourse) {
        if(sourceCourse.getVersion().equals(newCourse.getVersion())
                &&sourceCourse.getEducationLevel().equals(newCourse.getEducationLevel())
                &&sourceCourse.getCollegeId()==newCourse.getCollegeId()) {
            return;
        }
        String courseCodeNew = getCourseCode_new(newCourse);
        newCourse.setCode(courseCodeNew);
    }


    /**
     * 修改时将课程原来关联表信息删除
     *
     * @param course
     */
    public void deleteRelevance(CourseVo course) {
        //课程章节表
        List<CourseChapterVo> courseChapterList = course.getCourseChapterVoList();
        if (CollectionUtils.isNotEmpty(courseChapterList)) {
            List<Long> courseChapterIds = courseChapterList.stream().map(CourseChapter::getId).filter(ObjectUtils::isNotEmpty).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(courseChapterIds)) {
                courseChapterMapper.deleteCourseChapterByIds(courseChapterIds);
            }
        }
        //课程教材参考书
        List<CourseTextbookVo> courseTextbookList = course.getCourseTextbookVoList();
        if (CollectionUtils.isNotEmpty(courseTextbookList)) {
            List<Long> courseTextbookIds = courseTextbookList.stream().map(CourseTextbook::getId).filter(ObjectUtils::isNotEmpty).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(courseTextbookIds)) {
                courseTextbookMapper.deleteCourseTextbookByIds(courseTextbookIds);
            }
        }
        //知识单元 和知识单元对应关系中间表
        List<KnowledgeUnitVo> knowledgeUnitList = course.getKnowledgeUnitVoList();
        if (CollectionUtils.isNotEmpty(knowledgeUnitList)) {
            for (KnowledgeUnit knowledgeUnit : knowledgeUnitList) {
                //知识点和知识点关联表
                List<KnowledgePoint> knowledgePointList = knowledgeUnit.getKnowledgePointList();
                if (CollectionUtils.isNotEmpty(knowledgePointList)) {
                    List<Long> knowledgePointIds = knowledgePointList.stream().map(KnowledgePoint::getId).filter(ObjectUtils::isNotEmpty).collect(Collectors.toList());
                    //knowledgeUnitRefPointMapper.deleteKnowledgeUnitRefPointByUnitId(knowledgeUnitId);
                    knowledgePointMapper.deleteKnowledgePointByIds(knowledgePointIds);
                }
            }
            //List<Long> knowledgeUnitIds = knowledgeUnitList.stream().map(KnowledgeUnit::getId).filter(ObjectUtils::isNotEmpty).collect(Collectors.toList());
            //knowledgeUnitMapper.deleteKnowledgeUnitByIds(knowledgeUnitIds);
            //courseRefKeUnitMapper.deleteCourseRefKeUnitByCourseId(course.getId());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CourseVo saveCourse(CourseVo course) {
        //课程本身
        UserUtils.reflash(course);
        courseMapper.updateCourse(course);

        //章节
        List<CourseChapterVo> courseChapterVoList = course.getCourseChapterVoList();
        if (CollectionUtils.isNotEmpty(courseChapterVoList)) {
            courseChapterMapper.deleteCourseChapterByCourseId(course.getId());
            for (CourseChapterVo courseChapterVo : courseChapterVoList) {
                UserUtils.reflash(courseChapterVo);
            }
            if (ObjectUtils.isNotEmpty(courseChapterVoList)) {
                courseChapterMapper.insertCourseChapters(courseChapterVoList);
            }
        }

        //教材
        List<CourseTextbookVo> courseTextbookList = course.getCourseTextbookVoList();
        if (CollectionUtils.isNotEmpty(courseTextbookList)) {
            courseTextbookMapper.deleteCourseTextbookByCourseId(course.getId());
            for (CourseTextbookVo courseTextbookVo : courseTextbookList) {
                UserUtils.reflash(courseTextbookVo);
            }
            if (ObjectUtils.isNotEmpty(courseTextbookList)) {
                courseTextbookMapper.insertCourseTextbooks(courseTextbookList);
            }
        }


        //实践
        CourseTeachingPracticePlanVo courseTeachingPracticePlanVo = course.getCourseTeachingPracticePlanVo();
        if (ObjectUtils.isNotEmpty(courseTeachingPracticePlanVo)) {
            UserUtils.reflash(courseTeachingPracticePlanVo);
            courseTeachingPracticePlanMapper.updateCourseTeachingPracticePlan(courseTeachingPracticePlanVo);
        }

        //理论
        CourseTeachingTheoryPlanVo courseTeachingTheoryPlanVo = course.getCourseTeachingTheoryPlanVo();
        if (ObjectUtils.isNotEmpty(courseTeachingTheoryPlanVo)) {
            UserUtils.reflash(courseTeachingTheoryPlanVo);
            courseTeachingTheoryPlanMapper.updateCourseTeachingTheoryPlan(courseTeachingTheoryPlanVo);
        }
        if (ObjectUtils.isEmpty(course.getKnowledgeDomainVoList())) {
            return course;
        }

        List<KnowledgePoint> updatePointList = new ArrayList<>();
        List<Long> deletePointIdList = new ArrayList<>();

        //知识单元
        List<KnowledgeDomainVo> knowledgeDomainVoList = course.getKnowledgeDomainVoList();
        List<KnowledgeUnitVo> knowledgeUnitVoList = knowledgeDomainVoList.parallelStream().filter(domain -> ObjectUtils.isNotEmpty(domain.getKnowledgeUnitVoList()))
                .flatMap(domain -> domain.getKnowledgeUnitVoList().stream()).collect(Collectors.toList());
        //没有id的新增
        List<KnowledgeUnitVo> insertKnowledgeUnitVoList = knowledgeUnitVoList.parallelStream().filter(unit -> ObjectUtils.isEmpty(unit.getId()))
                .collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(insertKnowledgeUnitVoList)) {
            for (KnowledgeUnitVo knowledgeUnitVo : insertKnowledgeUnitVoList) {
                UserUtils.reflash(knowledgeUnitVo);
            }
            knowledgeUnitMapper.insertKnowledgeUnits(insertKnowledgeUnitVoList);
        }
        List<CourseRefKeUnit> courseRefUnitList = insertKnowledgeUnitVoList.parallelStream().map(unit -> {
            CourseRefKeUnit courseRefKeUnit = new CourseRefKeUnit();
            courseRefKeUnit.setCourseId(course.getId());
            courseRefKeUnit.setUnitId(unit.getId());
            return courseRefKeUnit;
        }).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(courseRefUnitList)) {
            courseRefKeUnitMapper.insertCourseRefKeUnits(courseRefUnitList);
        }

        //有id的修改
        Map<Long, KnowledgeUnitVo> updateKnowledgeUnitVoMap = knowledgeUnitVoList.parallelStream().filter(unit -> ObjectUtils.isNotEmpty(unit.getId()))
                .collect(Collectors.toMap(KnowledgeUnitVo::getId, knowledgeUnitVo -> knowledgeUnitVo));
        for (KnowledgeUnitVo value : updateKnowledgeUnitVoMap.values()) {
            UserUtils.reflash(value);
        }
        knowledgeUnitMapper.updateKnowledgeUnits(updateKnowledgeUnitVoMap.values());
        List<CourseRefKeUnit> existsCourseRefKeUnit = courseRefKeUnitMapper.selectCourseRefKeUnitByCourseId(course.getId());
        List<KnowledgePointVo> pointList = updateKnowledgeUnitVoMap.values().parallelStream().flatMap(knowledgeUnitVo -> knowledgeUnitVo.getKnowledgePointVoList().stream()).collect(Collectors.toList());
        List<Long> updateUnitIds = existsCourseRefKeUnit.stream().filter(courseRefKeUnit -> updateKnowledgeUnitVoMap.containsKey(courseRefKeUnit.getUnitId()))
                .map(CourseRefKeUnit::getUnitId).collect(Collectors.toList());
        List<KnowledgeUnitRefPoint> knowledgeUnitRefPointList = knowledgeUnitRefPointMapper.selectKnowledgeUnitRefPointByUnitIds(updateUnitIds);
        List<KnowledgePointVo> _updatePointList = pointList.parallelStream().filter(point -> ObjectUtils.isNotEmpty(point.getId())).collect(Collectors.toList());
        updatePointList.addAll(_updatePointList);
        List<Long> updatePointIds = _updatePointList.parallelStream().map(KnowledgePointVo::getId).collect(Collectors.toList());

        deletePointIdList.addAll(knowledgeUnitRefPointList.parallelStream().filter(unitRefPoint -> updatePointIds.contains(unitRefPoint.getPointId()))
                .map(KnowledgeUnitRefPoint::getPointId).collect(Collectors.toList()));


        //原有的没传进来删除
        List<CourseRefKeUnit> deleteCourseRefUnitList = existsCourseRefKeUnit.parallelStream().filter(courseRefKeUnit -> !updateKnowledgeUnitVoMap.containsKey(courseRefKeUnit.getUnitId()))
                .collect(Collectors.toList());
        List<Long> deleteUnitIds = deleteCourseRefUnitList.parallelStream().map(CourseRefKeUnit::getUnitId).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(deleteUnitIds)) {
            courseRefKeUnitMapper.deleteCourseRefKeUnitByCourseIdAndUnitIds(course.getId(), deleteUnitIds);
            //暂时没有复用,直接删除
            knowledgeUnitMapper.deleteKnowledgeUnitByIds(deleteUnitIds);
            List<KnowledgeUnitRefPoint> deleteKnowledgeUnitRefPointList = knowledgeUnitRefPointMapper.selectKnowledgeUnitRefPointByUnitIds(deleteUnitIds);
            deletePointIdList.addAll(deleteKnowledgeUnitRefPointList.parallelStream().map(KnowledgeUnitRefPoint::getPointId).collect(Collectors.toList()));
        }


        //知识领域
        knowledgeDomainMapper.deleteKnowledgeDomainByCourseId(course.getId());
        //修改课程id及单元id集合
        knowledgeDomainVoList.parallelStream().forEach(domain -> {
            if (ObjectUtils.isEmpty(domain.getKnowledgeUnitVoList())) {
                return;
            }
            String unitIds = domain.getKnowledgeUnitVoList().parallelStream().map(KnowledgeUnitVo::getId).map(String::valueOf)
                    .collect(Collectors.joining(SymbolConstants.COMMA));
            domain.setUnitIds(unitIds);
            UserUtils.reflash(domain);
        });
        if (CollectionUtils.isNotEmpty(knowledgeDomainVoList)) {
            knowledgeDomainMapper.insertKnowledgeDomains(knowledgeDomainVoList);
        }

        //新增知识点
        List<KnowledgePointVo> insertPointList = knowledgeUnitVoList.parallelStream().flatMap(knowledgeUnitVo -> {
            return knowledgeUnitVo.getKnowledgePointVoList().parallelStream().filter(point -> ObjectUtils.isEmpty(point.getId()))
                    .map(point -> {
                        point.setUnitId(knowledgeUnitVo.getId());
                        point.setCourseId(course.getId());
                        UserUtils.reflash(point);
                        return point;
                    });
        }).collect(Collectors.toList());
        knowledgePointMapper.insertKnowledgePoints(insertPointList);
        List<KnowledgeUnitRefPoint> insertUnitRefPointList = insertPointList.parallelStream().map(point -> {
            KnowledgeUnitRefPoint knowledgeUnitRefPoint = new KnowledgeUnitRefPoint();
            knowledgeUnitRefPoint.setUnitId(point.getUnitId());
            knowledgeUnitRefPoint.setPointId(point.getId());
            return knowledgeUnitRefPoint;
        }).collect(Collectors.toList());
        //新增知识点关联
        if (CollectionUtils.isNotEmpty(insertUnitRefPointList)) {
            knowledgeUnitRefPointMapper.insertKnowledgeUnitRefPoints(insertUnitRefPointList);
        }

        //删除知识点及关联,暂时没有复用知识点，不判断直接删除
        if (CollectionUtils.isNotEmpty(deletePointIdList)) {
            knowledgeUnitRefPointMapper.deleteKnowledgeUnitRefPointByPointIds(deletePointIdList);
            knowledgePointMapper.deleteKnowledgePointByIds(deletePointIdList);
        }

        //修改知识点
        if (CollectionUtils.isNotEmpty(updatePointList)) {
            UserUtils.reflash(updatePointList);
            knowledgePointMapper.updateKnowledgePoints(updatePointList);
        }
        return null;
    }


    /**
     * 批量删除课程
     *
     * @param ids 需要删除的课程主键
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = {Exception.class})
    public int deleteCourseByIds(List<Long> ids) {
        List<Course> courses = courseMapper.selectCoursesByIds(ids);
        for (Course dbCourse : courses) {
            UserUtils.checkDataPermission(dbCourse);
            TrainingSchemeRefCourse query = new TrainingSchemeRefCourse();
            query.setCourseId(dbCourse.getId());
            List<TrainingSchemeRefCourse> trainingSchemeRefCourses = courseRefCourseMapper.selectTrainingSchemeRefCourseList(query);
            if (CollectionUtils.isNotEmpty(trainingSchemeRefCourses)) {
                throw new RuntimeException("课程已被培养方案引用，不能删除！");
            }
        }
        this.deleteCourseRelevance(ids);
        for (Long id : ids) {
            TrainingSchemeRefCourse trainingSchemeRefCourse = new TrainingSchemeRefCourse();
            trainingSchemeRefCourse.setCourseId(id);
            List<TrainingSchemeRefCourse> trainingSchemeRefCourseList = trainingSchemeRefCourseMapper.selectTrainingSchemeRefCourseList(trainingSchemeRefCourse);
            if (CollectionUtils.isNotEmpty(trainingSchemeRefCourseList)) {
                for (TrainingSchemeRefCourse schemeRefCourse : trainingSchemeRefCourseList) {
                    trainingSchemeRefCourseMapper.deleteTrainingSchemeRefCourseById(schemeRefCourse.getId());
                }
            }
            TrainingSchemeCourseSchedule trainingSchemeCourseSchedule = new TrainingSchemeCourseSchedule();
            trainingSchemeCourseSchedule.setCourseId(id);
            List<TrainingSchemeCourseScheduleVo> trainingSchemeCourseScheduleVoList = trainingSchemeCourseScheduleMapper.selectTrainingSchemeCourseScheduleList(trainingSchemeCourseSchedule);
            if (CollectionUtils.isNotEmpty(trainingSchemeCourseScheduleVoList)) {
                for (TrainingSchemeCourseScheduleVo trainingSchemeCourseScheduleVo : trainingSchemeCourseScheduleVoList) {
                    trainingSchemeCourseScheduleMapper.deleteTrainingSchemeCourseScheduleById(trainingSchemeCourseScheduleVo.getId());
                }
            }
        }
        return courseMapper.deleteCourseByIds(ids);
    }

    /**
     * 删除课程关联表对象数据
     *
     * @param ids
     */
    public void deleteCourseRelevance(List<Long> ids) {
        //        for (Long id : ids) {
//            KnowledgeDomain knowledgeDomain = new KnowledgeDomain();
//            knowledgeDomain.setCourseId(id);
//            courseRefKeUnitMapper.selectCourseRefKeUnitByCourseIds(ids);
//            knowledgeDomainMapper.deleteKnowledgeDomainByCourseId(id);
//            courseRefAbilityMapper.deleteCourseRefAbilityByCourseIds(ids.toArray(Long[]::new));
//            courseRefQualityMapper.deleteCourseRefQualityByCourseIds(ids.toArray(Long[]::new));
//
//            courseRefSourceDomainMapper.deleteCourseRefSourceDomainByCourseIds(ids.toArray(Long[]::new));
//            courseDomainRefSourceUnitMapper.deleteByCourseIds(ids.toArray(Long[]::new));
//            courseUnitRefSourcePointMapper.deleteByCourseIds(ids.toArray(Long[]::new));
//        }
//        List<CourseRefKeUnit> courseRefKeUnitList = courseRefKeUnitMapper.selectCourseRefKeUnitByCourseIds(ids);
//        if (CollectionUtils.isNotEmpty(courseRefKeUnitList)) {
//            for (CourseRefKeUnit courseRefKeUnit : courseRefKeUnitList) {
//                KnowledgeUnitRefPoint knowledgeUnitRefPoint = new KnowledgeUnitRefPoint();
//                knowledgeUnitRefPoint.setUnitId(courseRefKeUnit.getUnitId());
//                List<KnowledgeUnitRefPoint> knowledgeUnitRefPointList = knowledgeUnitRefPointMapper.selectKnowledgeUnitRefPointList(knowledgeUnitRefPoint);
//                if (CollectionUtils.isNotEmpty(knowledgeUnitRefPointList)) {
//                    for (KnowledgeUnitRefPoint unitRefPoint : knowledgeUnitRefPointList) {
//                        knowledgePointMapper.deleteKnowledgePointById(unitRefPoint.getPointId());
//                    }
//                }
//                knowledgeUnitRefPointMapper.deleteKnowledgeUnitRefPointByUnitId(courseRefKeUnit.getUnitId());
//                knowledgeUnitRefStdCultivationMapper.deleteKnowledgeUnitRefStdCultivationByUnitId(courseRefKeUnit.getUnitId());
//                knowledgeUnitMapper.deleteKnowledgeUnitById(courseRefKeUnit.getUnitId());
//                courseRefKeUnitMapper.deleteCourseRefKeUnitByCourseId(courseRefKeUnit.getCourseId());
//            }
//        }
        //删除课程目标关联知识单元
        courseDomainRefSourceUnitMapper.deleteByCourseIds(ids.toArray(new Long[0]));
        courseRefGraduationMapper.deleteCourseRefGraduationByCourseIds(ids.toArray(new Long[0]));
        courseTargetMapper.deleteCourseTargetByCourseIds(ids);
    }

    /**
     * 新增理论教学计划
     *
     * @param courseTeachingTheoryPlan 理论教学计划
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = {Exception.class})
    public CourseTeachingTheoryPlan insertCourseTeachingTheoryPlan(CourseTeachingTheoryPlan courseTeachingTheoryPlan) {
        UserUtils.reflash(courseTeachingTheoryPlan);
        courseTeachingTheoryPlanMapper.insertCourseTeachingTheoryPlan(courseTeachingTheoryPlan);
        return courseTeachingTheoryPlan;
    }

    /**
     * 修改理论教学计划
     *
     * @param courseTeachingTheoryPlan 理论教学计划
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = {Exception.class})
    public CourseTeachingTheoryPlan updateCourseTeachingTheoryPlan(CourseTeachingTheoryPlan courseTeachingTheoryPlan) {
        UserUtils.reflash(courseTeachingTheoryPlan);
        courseTeachingTheoryPlanMapper.updateCourseTeachingTheoryPlan(courseTeachingTheoryPlan);
        return courseTeachingTheoryPlan;
    }

    /**
     * 批量删除理论教学计划
     *
     * @param ids 需要删除的理论教学计划主键
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = {Exception.class})
    public int deleteCourseTeachingTheoryPlanByIds(Long[] ids) {
        return courseTeachingTheoryPlanMapper.deleteCourseTeachingTheoryPlanByIds(ids);
    }

    /**
     * 新增实践教学计划
     *
     * @param courseTeachingPracticePlan 实践教学计划
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = {Exception.class})
    public CourseTeachingPracticePlan insertCourseTeachingPracticePlan(CourseTeachingPracticePlan courseTeachingPracticePlan) {
        UserUtils.reflash(courseTeachingPracticePlan);
        courseTeachingPracticePlanMapper.insertCourseTeachingPracticePlan(courseTeachingPracticePlan);
        return courseTeachingPracticePlan;
    }

    /**
     * 修改实践教学计划
     *
     * @param courseTeachingPracticePlan 实践教学计划
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = {Exception.class})
    public CourseTeachingPracticePlan updateCourseTeachingPracticePlan(CourseTeachingPracticePlan courseTeachingPracticePlan) {
        UserUtils.reflash(courseTeachingPracticePlan);
        courseTeachingPracticePlanMapper.updateCourseTeachingPracticePlan(courseTeachingPracticePlan);
        return courseTeachingPracticePlan;
    }

    /**
     * 批量删除实践教学计划
     *
     * @param ids 需要删除的实践教学计划主键
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = {Exception.class})
    public int deleteCourseTeachingPracticePlanByIds(Long[] ids) {
        return courseTeachingPracticePlanMapper.deleteCourseTeachingPracticePlanByIds(ids);
    }


    /**
     * 新增课程章节
     *
     * @param courseChapter 课程章节
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = {Exception.class})
    public CourseChapter insertCourseChapter(CourseChapter courseChapter) {
        UserUtils.reflash(courseChapter);
        courseChapterMapper.insertCourseChapter(courseChapter);
        return courseChapter;
    }

    /**
     * 修改课程章节
     *
     * @param courseChapter 课程章节
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = {Exception.class})
    public CourseChapter updateCourseChapter(CourseChapter courseChapter) {
        UserUtils.reflash(courseChapter);
        courseChapterMapper.updateCourseChapter(courseChapter);
        return courseChapter;
    }


    /**
     * 新增知识点
     *
     * @param knowledgePoint 知识点
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = {Exception.class})
    public KnowledgePoint insertKnowledgePoint(KnowledgePointVo knowledgePoint) {
        UserUtils.reflash(knowledgePoint);
        knowledgePointMapper.insertKnowledgePoint(knowledgePoint);
        KnowledgeUnitRefPoint knowledgeUnitRefPoint = new KnowledgeUnitRefPoint();
        knowledgeUnitRefPoint.setUnitId(knowledgePoint.getUnitId());
        knowledgeUnitRefPoint.setPointId(knowledgePoint.getId());
        knowledgeUnitRefPointMapper.insertKnowledgeUnitRefPoint(knowledgeUnitRefPoint);
        return knowledgePoint;
    }

    /**
     * 修改知识点
     *
     * @param knowledgePoint 知识点
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = {Exception.class})
    public KnowledgePoint updateKnowledgePoint(KnowledgePoint knowledgePoint) {
        UserUtils.reflash(knowledgePoint);
        knowledgePointMapper.updateKnowledgePoint(knowledgePoint);
        return knowledgePoint;
    }


    /**
     * 新增知识单元
     *
     * @param knowledgeUnit 知识单元
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = {Exception.class})
    public KnowledgeUnit insertKnowledgeUnit(KnowledgeUnitVo knowledgeUnit) {
        UserUtils.reflash(knowledgeUnit);
        knowledgeUnitMapper.insertKnowledgeUnit(knowledgeUnit);
        KnowledgeDomain domain = knowledgeDomainMapper.selectKnowledgeDomainById(knowledgeUnit.getDomainId());
        if (StringUtils.isBlank(domain.getUnitIds())) {
            domain.setUnitIds(knowledgeUnit.getId().toString());
        } else {
            String unitIds = domain.getUnitIds() + SymbolConstants.COMMA + knowledgeUnit.getId();
            domain.setUnitIds(unitIds);
        }
        knowledgeDomainMapper.updateKnowledgeDomain(domain);
        CourseRefKeUnit courseRefKeUnit = new CourseRefKeUnit();
        courseRefKeUnit.setCourseId(domain.getCourseId());
        courseRefKeUnit.setUnitId(knowledgeUnit.getId());
        courseRefKeUnitMapper.insertCourseRefKeUnit(courseRefKeUnit);
        this.deleteAllCheckLog(domain.getCourseId());
        return knowledgeUnit;
    }

    @Override
    @Transactional(rollbackFor = {Exception.class})
    public int deleteUnitById(KnowledgeUnitVo knowledgeUnit) {
        KnowledgeDomain domain = knowledgeDomainMapper.selectKnowledgeDomainById(knowledgeUnit.getDomainId());
        String unitIds = domain.getUnitIds();
        String[] unitIdArr = unitIds.split(SymbolConstants.COMMA);
        List<String> unitList = Arrays.asList(unitIdArr);
        String newUnitIds = "";
        Iterator it = unitList.iterator();
        while (it.hasNext()) {
            String unitId = (String) it.next();
            if (!StringUtils.equals(unitId, knowledgeUnit.getId().toString())) {
                newUnitIds += unitId + SymbolConstants.COMMA;
            }
        }
        if (newUnitIds.length() > 1) {
            domain.setUnitIds(newUnitIds.substring(0, newUnitIds.length() - 1));
        } else {
            domain.setUnitIds(newUnitIds);
        }
        UserUtils.reflash(domain);
        knowledgeDomainMapper.updateKnowledgeDomain(domain);
        knowledgeUnit.setSysflag(DomainFieldConstant.DEL_FLAG_DELETE_VALUE);
        KnowledgeUnitRefPoint knowledgeUnitRefPoint = new KnowledgeUnitRefPoint();
        knowledgeUnitRefPoint.setUnitId(knowledgeUnit.getId());
        List<Long> points = knowledgeUnitRefPointMapper.selectKnowledgeUnitRefPointList(knowledgeUnitRefPoint).stream().map(KnowledgeUnitRefPoint::getPointId).filter(ObjectUtils::isNotEmpty).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(points)) {
            knowledgeUnitRefPointMapper.deleteKnowledgeUnitRefPointByUnitId(knowledgeUnit.getId());
            knowledgePointMapper.deleteKnowledgePointByIds(points);
        }
        KnowledgeUnitRefStdCultivation knowledgeUnitRefStdCultivation = new KnowledgeUnitRefStdCultivation();
        knowledgeUnitRefStdCultivation.setUnitId(knowledgeUnit.getId());
        List<KnowledgeUnitRefStdCultivation> knowledgeUnitRefStdCultivations = knowledgeUnitRefStdCultivationMapper.selectKnowledgeUnitRefStdCultivationList(knowledgeUnitRefStdCultivation);
        if (CollectionUtils.isNotEmpty(knowledgeUnitRefStdCultivations)) {
            knowledgeUnitRefStdCultivationMapper.deleteKnowledgeUnitRefStdCultivationByUnitId(knowledgeUnit.getId());
        }
        UserUtils.reflash(knowledgeUnit);
        courseRefKeUnitMapper.deleteCourseRefKeUnitByCourseIdAndUnitId(domain.getCourseId(), knowledgeUnit.getId());
        this.deleteAllCheckLog(domain.getCourseId());
        return knowledgeUnitMapper.updateKnowledgeUnit(knowledgeUnit);
    }

    /**
     * 修改知识单元
     *
     * @param knowledgeUnit 知识单元
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = {Exception.class})
    public KnowledgeUnit updateKnowledgeUnit(KnowledgeUnit knowledgeUnit) {
        UserUtils.reflash(knowledgeUnit);
        knowledgeUnitMapper.updateKnowledgeUnit(knowledgeUnit);
        return knowledgeUnit;
    }

    /**
     * 新增课程与知识单元关联
     *
     * @param courseRefKeUnit 课程与知识单元关联
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = {Exception.class})
    public CourseRefKeUnit insertCourseRefKeUnit(CourseRefKeUnit courseRefKeUnit) {
        courseRefKeUnitMapper.insertCourseRefKeUnit(courseRefKeUnit);
        return courseRefKeUnit;
    }

    /**
     * 删除课程与知识单元关联信息
     *
     * @param courseId 课程与知识单元关联主键
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = {Exception.class})
    public int deleteCourseRefKeUnitByCourseIdAndUnitId(Long courseId, Long unitId) {
        if (unitId == null) {
            return courseRefKeUnitMapper.deleteCourseRefKeUnitByCourseId(courseId);
        } else {
            return courseRefKeUnitMapper.deleteCourseRefKeUnitByCourseIdAndUnitId(courseId, unitId);
        }
    }

    /**
     * 新增知识单元与知识点关联
     *
     * @param knowledgeUnitRefPoint 知识单元与知识点关联
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = {Exception.class})
    public KnowledgeUnitRefPoint insertKnowledgeUnitRefPoint(KnowledgeUnitRefPoint knowledgeUnitRefPoint) {
        knowledgeUnitRefPointMapper.insertKnowledgeUnitRefPoint(knowledgeUnitRefPoint);
        return knowledgeUnitRefPoint;
    }

    /**
     * 删除知识单元与知识点关联信息
     *
     * @param unitId 知识单元与知识点关联主键
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = {Exception.class})
    public int deleteKnowledgeUnitRefPointByUnitIdAndPointId(Long unitId, Long pointId) {
        if (pointId == null) {
            return knowledgeUnitRefPointMapper.deleteKnowledgeUnitRefPointByUnitId(unitId);
        } else {
            return knowledgeUnitRefPointMapper.deleteKnowledgeUnitRefPointByUnitIdAndPointId(unitId, pointId);
        }
    }

    /**
     * 新增课程教材
     *
     * @param courseTextbook 课程教材
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = {Exception.class})
    public CourseTextbook insertCourseTextbook(CourseTextbook courseTextbook) {
        UserUtils.reflash(courseTextbook);
        courseTextbookMapper.insertCourseTextbook(courseTextbook);
        return courseTextbook;
    }

    /**
     * 修改课程教材
     *
     * @param courseTextbook 课程教材
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = {Exception.class})
    public CourseTextbook updateCourseTextbook(CourseTextbook courseTextbook) {
        UserUtils.reflash(courseTextbook);
        courseTextbookMapper.updateCourseTextbook(courseTextbook);
        return courseTextbook;
    }


    @Override
    public List<KnowledgeUnit> selectKnowledgeUnitListByCourseId(Long courseId) {
        return knowledgeUnitMapper.selectKnowledgeUnitListByCourseId(courseId);
    }

    @Override
    public List<KnowledgePoint> selectKnowledgePointByUnitId(Long unitId) {
        return knowledgePointMapper.selectKnowledgePointByUnitId(unitId);
    }

    @Override
    public List<KnowledgeViewVo> selectKnowledgeTreeByCourseId(Long courseId) {
        CourseVo courseVo = courseMapper.selectCourseAndRelevanceById(courseId);
        List<KnowledgeViewVo> knowledgeViewVoList = new ArrayList<>();
        knowledgeViewVoList.add(new KnowledgeViewVo(courseVo));
        List<KnowledgeUnitVo> knowledgeUnitVoList = courseVo.getKnowledgeUnitVoList();
        for (KnowledgeUnitVo knowledgeUnitVo : knowledgeUnitVoList) {
            knowledgeViewVoList.add(new KnowledgeViewVo(knowledgeUnitVo, courseId));
            List<KnowledgePointVo> knowledgePointVoList = knowledgeUnitVo.getKnowledgePointVoList();
            for (KnowledgePointVo knowledgePointVo : knowledgePointVoList) {
                knowledgeViewVoList.add(new KnowledgeViewVo(knowledgePointVo, knowledgeUnitVo.getId()));
            }
        }
        courseVo.setKnowledgeUnitVoList(null);
        courseVo.setCourseTextbookVoList(null);
        courseVo.setCourseTeachingTheoryPlanVo(null);
        courseVo.setCourseTeachingPracticePlanVo(null);
        courseVo.setKnowledgeDomainVoList(null);
        return knowledgeViewVoList;
    }

    @Override
    @Transactional(rollbackFor = {Exception.class})
    public KnowledgeDomain insertKnowledgeDomain(KnowledgeDomain knowledgeDomain) {
        UserUtils.reflash(knowledgeDomain);
        knowledgeDomainMapper.insertKnowledgeDomain(knowledgeDomain);
        this.deleteAllCheckLog(knowledgeDomain.getCourseId());
        return knowledgeDomain;
    }

    @Override
    @Transactional(rollbackFor = {Exception.class})
    public KnowledgeDomain updateKnowledgeDomain(KnowledgeDomain knowledgeDomain) {
        UserUtils.reflash(knowledgeDomain);
        knowledgeDomainMapper.updateKnowledgeDomain(knowledgeDomain);
        this.deleteAllCheckLog(knowledgeDomain.getCourseId());
        return knowledgeDomain;
    }

    @Override
    @Transactional(rollbackFor = {Exception.class})
    public int deleteDomainById(Long id) {
        KnowledgeDomain knowledgeDomain = knowledgeDomainMapper.selectKnowledgeDomainById(id);
        String unitIds = knowledgeDomain.getUnitIds();
        if (StringUtils.isNotBlank(unitIds)) {
            List<Long> unitList = getList(unitIds);
            if (CollectionUtils.isNotEmpty(unitList)) {
                for (Long utilId : unitList) {
                    KnowledgeUnitRefPoint knowledgeUnitRefPoint = new KnowledgeUnitRefPoint();
                    knowledgeUnitRefPoint.setUnitId(utilId);
                    List<Long> points = knowledgeUnitRefPointMapper.selectKnowledgeUnitRefPointList(knowledgeUnitRefPoint).stream().map(KnowledgeUnitRefPoint::getPointId).filter(ObjectUtils::isNotEmpty).collect(Collectors.toList());
                    if (CollectionUtils.isNotEmpty(points)) {
                        knowledgePointMapper.deleteKnowledgePointByIds(points);
                        knowledgeUnitRefPointMapper.deleteKnowledgeUnitRefPointByUnitId(utilId);
                    }
                    courseRefKeUnitMapper.deleteCourseRefKeUnitByCourseIdAndUnitId(knowledgeDomain.getCourseId(), utilId);
                }
                knowledgeUnitMapper.deleteKnowledgeUnitByIds(unitList);
            }
        }
        KnowledgeDomain domain = new KnowledgeDomain();
        domain.setId(id);
        domain.setSysflag(DomainFieldConstant.DEL_FLAG_DELETE_VALUE);
        this.deleteAllCheckLog(knowledgeDomain.getCourseId());
        return knowledgeDomainMapper.updateKnowledgeDomain(domain);
    }

    @Override
    public Map courseStatistics(Long schemeId) {
        Map map = new HashMap();
        List<Long> courseIds = courseMapper.selectCourseCount(schemeId);
        TrainingSchemeVo trainingSchemeVo = trainingSchemeMapper.selectTrainingSchemeById(schemeId);
        map.put("curriculumNum", courseIds.size());
        if (CollectionUtils.isNotEmpty(courseIds)) {
            List<Map<String, Long>> maps = courseMapper.selectKnowledgeNum(courseIds, trainingSchemeVo.getMajorId());
            if (ObjectUtils.isNotEmpty(maps)) {
                Map<String, Long> stringLongMap = maps.get(0);
                map.put("knowledgeUnitNum", stringLongMap.get("unitCount"));
                map.put("knowledgePointNum", stringLongMap.get("pointCount"));
            } else {
                map.put("knowledgeUnitNum", 0);
                map.put("knowledgePointNum", 0);
            }
//            List<Long> unitIds = courseMapper.selectUnitCount(courseIds,trainingSchemeVo.getMajorId());
//            map.put("knowledgeUnitNum", unitIds.size());
//            if (CollectionUtils.isNotEmpty(unitIds)) {
//                List<Long> pointIds = courseMapper.selectPointCount(unitIds);
//                map.put("knowledgePointNum", pointIds.size());
//            } else {
//                map.put("knowledgePointNum", 0);
//            }
        } else {
            map.put("knowledgeUnitNum", 0);
            map.put("knowledgePointNum", 0);
        }
        Long firstLevelPowerCountNum = trainingSchemeMapper.firstLevelPowerCountNum(schemeId);
        Long secondLevelPowerCountNum = trainingSchemeMapper.secondLevelPowerCountNum(schemeId);
        Long thirdLevelPowerCountNum = trainingSchemeMapper.thirdLevelPowerCountNum(schemeId);
        //分母
        int denominator = 0;
        //分子
        int numerator = 0;
        KnowledgeUnitRefStdCultivation knowledgeUnitRefStdCultivation = new KnowledgeUnitRefStdCultivation();
        knowledgeUnitRefStdCultivation.setSchemeId(schemeId);
        List<Long> cultivationList = knowledgeUnitRefStdCultivationMapper.selectKnowledgeUnitRefStdCultivationList(knowledgeUnitRefStdCultivation).stream().map(KnowledgeUnitRefStdCultivation::getCultivationId).filter(ObjectUtils::isNotEmpty).collect(Collectors.toList());
        //去重
        List<Long> cultivationIds = cultivationList.stream().distinct().collect(Collectors.toList());
        numerator = cultivationIds.size();
        if (trainingSchemeVo != null) {
            Long standardId = trainingSchemeVo.getStandardId();
            if (standardId != null) {
                List<StandardCultivation> standardCultivations = standardCultivationMapper.selectStandardCultivationAndLeafAll(standardId);
                if (CollectionUtils.isNotEmpty(standardCultivations)) {
                    denominator = standardCultivations.size();
                }
            }
        }
        /**
         * 重写课程支撑度  改为课程目标满足的毕业标准 分母是全部的毕业标准条数 分子是绑定的毕业标准条数
         */
        //根据专业查找对应的毕业要求
        List<StandardGraduation> standardGraduations = standardGraduationMapper.selectStandardGraduationByMajorId(trainingSchemeVo);
        if (ObjectUtils.isNotEmpty(standardGraduations)) {
            denominator = standardGraduations.size();
        }
        //查找培养方案安排的课程
        List<TrainingSchemeCourseVo> trainingSchemeCourseVos = trainingSchemeMapper.selectTrainingSchemeCoursesById(schemeId);
        //根据课程id 查询绑定的毕业标准数量
        if (ObjectUtils.isNotEmpty(trainingSchemeCourseVos)) {
            numerator = courseTargetMapper.selectBoundGraduationByCourseId(trainingSchemeCourseVos.stream().map(t -> t.getId()).collect(Collectors.toList()));
        }
        map.put("numerator", numerator);
        map.put("denominator", denominator);
        map.put("firstLevelPowerCountNum", firstLevelPowerCountNum);
        map.put("secondLevelPowerCountNum", secondLevelPowerCountNum);
        map.put("thirdLevelPowerCountNum", thirdLevelPowerCountNum);
        return map;
    }


    @Override
    public List<TrainingSchemeCourseScheduleRankingVo> courseRanking() {
        return courseMapper.courseRanking();
    }

    @Override
    public List courseSelectStatistics(String courseName, List<Integer> types) {
        List<TrainingSchemeCourseScheduleRankingVo> trainingSchemeCourseScheduleRankingVoList = courseMapper.courseSelectStatistics(courseName, types);
        if (CollectionUtils.isNotEmpty(trainingSchemeCourseScheduleRankingVoList)) {
            for (TrainingSchemeCourseScheduleRankingVo trainingSchemeCourseScheduleRankingVo : trainingSchemeCourseScheduleRankingVoList) {
                //根据课程查培养方案
                List<TrainingSchemeScheduleVo> trainingSchemeScheduleVos = trainingSchemeMapper.selectTrainingSchemeListByCourseId(trainingSchemeCourseScheduleRankingVo.getCourseId(), types);
                if (CollectionUtils.isNotEmpty(trainingSchemeScheduleVos)) {
                    for (TrainingSchemeScheduleVo trainingSchemeScheduleVo : trainingSchemeScheduleVos) {
                        if (trainingSchemeScheduleVo.getTerm() != null) {
                            trainingSchemeScheduleVo.setTermName(DomainFieldConstant.TERM_NUMBER_NAME_MAP.get(trainingSchemeScheduleVo.getTerm()));
                        }
                    }
                }
                trainingSchemeCourseScheduleRankingVo.setTrainingSchemeList(trainingSchemeScheduleVos);
            }
        }
        return trainingSchemeCourseScheduleRankingVoList;
    }

    @Override
    public Map countCollegeCourse() {
        Map map = new HashMap();
        map.put("curriculumNum", courseMapper.countCourse());
        map.put("programNum", courseMapper.countScheme());
        map.put("curriculums", courseMapper.countCollegeCourse());
        map.put("collegePrograms", courseMapper.countCollegeScheme());
        map.put("typePrograms", courseMapper.countCategory());
        List<CurriculumSelectionVo> curriculumSelectionList = courseMapper.countSemesterScheduling();
        if (CollectionUtils.isNotEmpty(curriculumSelectionList)) {
            for (CurriculumSelectionVo curriculumSelectionVo : curriculumSelectionList) {
                if (curriculumSelectionVo.getTerm() != null) {
                    curriculumSelectionVo.setTermName(DomainFieldConstant.TERM_NUMBER_NAME_MAP.get(curriculumSelectionVo.getTerm()));
                }
            }
        }
        map.put("curriculumSelection", curriculumSelectionList);
        return map;
    }


    @Override
    @Transactional(rollbackFor = {Exception.class})
    public void updateFileById(String fileId, String fileName, Long id) {
        courseMapper.updateFileById(fileId, fileName, id);
    }

    @Override
    @Transactional(rollbackFor = {Exception.class})
    public void updateStatusByIds(List<Long> ids) {
        List<CourseReview> reviewList = new ArrayList<>();
        for (Long id : ids) {
            CourseReview courseReview = this.getCourseReview(id);
            reviewList.add(courseReview);
        }
        courseReviewMapper.insertReview(reviewList);
        courseMapper.updateStatusByIds(ids);
    }

    /**
     * 根据课程id组装版本信息对象
     *
     * @param id
     */
    public CourseReview getCourseReview(Long id) {
        CourseReview courseReview = new CourseReview();
        UserUtils.reflash(courseReview);
        courseReview.setCourseId(id);
        CourseVo courseVo = courseMapper.selectCourseAndRelevanceById(id);
        if (StringUtils.isNotBlank(courseVo.getBeforeCourseId())) {
            courseVo.setBeforeCourseList(courseMapper.selectCoursesIdAndNameByIds(getList(courseVo.getBeforeCourseId())));
        }
        if (StringUtils.isNotBlank(courseVo.getAfterCourseId())) {
            courseVo.setAfterCourseList(courseMapper.selectCoursesIdAndNameByIds(getList(courseVo.getAfterCourseId())));
        }
        String review = JSONObject.toJSONString(courseVo);
        courseReview.setReview(review);
        return courseReview;
    }

    @Override
    @Transactional(rollbackFor = {Exception.class})
    public void deleteKnowledgePointByIds(List<Long> ids) {
        knowledgePointMapper.deleteKnowledgePointByIds(ids);
    }

    @Override
    public List<CourseReview> selectReview(CourseReview courseReview) {
        return courseReviewMapper.selectCourseReviewList(courseReview);
    }

    @Override
    public CourseReview reviewById(Long id) {
        return courseReviewMapper.selectCourseReviewById(id);
    }

    //删除查重记录
    public void deleteAllCheckLog(Long sourceDomainId) {
        if (ObjectUtils.isNotEmpty(sourceDomainId)) {
            knowledgeCheckLogMapper.deleteBySourceDomainId(sourceDomainId);
            knowledgeNoCheckLogMapper.deleteBySourceDomainId(sourceDomainId);
            knowledgeChekTotalMapper.deleteBySourceDomainId(sourceDomainId);
        }
    }

    /**
     * @param id
     */
    @Override
    @Transactional(rollbackFor = {Exception.class})
    public CourseVo fallbackReviewById(Long id, Long courseId) {
        if (id == null) {
            CourseReview courseReview = this.getCourseReview(courseId);
            courseReviewMapper.insertCourseReview(courseReview);
            List<Long> ids = new ArrayList<>();
            ids.add(courseId);
            courseMapper.updateStatusByIds(ids);
            return selectCourseById(courseId);
        } else {
            CourseReview courseReview = courseReviewMapper.selectCourseReviewById(id);
            //将原来的记录存为历史
            CourseReview old = getCourseReview(courseId);
            courseReviewMapper.insertCourseReview(old);
            //将选中的版本作为新的对象。 将原来关联数据清除，重新组织新对象
            List<Long> courses = new ArrayList<>();
            courses.add(courseId);
            this.deleteCourseRelevance(courses);
            CourseVo newCourseVo = JSONObject.parseObject(courseReview.getReview(), CourseVo.class);
            newCourseVo.setStatus(DomainFieldConstant.COURSE_STATUS_NO_EXAMINE);
            UserUtils.reflash(newCourseVo);
            courseMapper.updateCourse(newCourseVo);
            this.insertRelevance(newCourseVo);
            return newCourseVo;
        }
    }

    @Override
    @Transactional(rollbackFor = {Exception.class})
    public void deleteReview(Long id) {
        CourseReview courseReview = new CourseReview();
        courseReview.setId(id);
        courseReview.setSysflag(DomainFieldConstant.DEL_FLAG_DELETE_VALUE);
        courseReviewMapper.updateCourseReview(courseReview);
    }


    @Override
    public void exportCourseTemplate(HttpServletResponse response) {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("template/course/course.xlsx");
        ExcelUtils.exportTemplate(inputStream, response);
    }

    /**
     * 字典 value to value 映射关系
     *
     * @param dictType
     * @return
     */
    private Map<String, String> getDictDataValueToLabelMap(String dictType) {
        return doinnerDictDataService.dictType(dictType).getData().parallelStream().collect(Collectors.toMap(SysDictData::getDictValue, SysDictData::getDictLabel, (a, b) -> a));
    }

    private Map<String, String> getDictDataLabelToValueMap(String dictType) {
        return doinnerDictDataService.dictType(dictType).getData().parallelStream().collect(Collectors.toMap(SysDictData::getDictLabel, SysDictData::getDictValue, (a, b) -> a));
    }


    @Override
    @Transactional(rollbackFor = {Exception.class})
    public Message importCourse(MultipartFile file, String courseModule, String courseModuleChildren, Long majorId, String type, Integer templateType, String version, Long categoryId,Long subMajorId) {
        ExcelUtil<CourseExcelVo> excelUtil = new ExcelUtil<>(CourseExcelVo.class);
        try {
            List<CourseExcelVo> excelVos = excelUtil.importExcel(file.getInputStream()).stream()
                    .filter(c -> ObjectUtils.isNotEmpty(c) && ObjectUtils.isNotEmpty(c.getName())).collect(Collectors.toList());
            if (ObjectUtils.isEmpty(excelVos)) {
                return Message.error(DomainExceptionConstant.DATA_IMPORT_TEMPLATE_ERROR);
            }
            Map<String, Long> deptIdNameMap = doinnerDeptService.list(new CustomDept()).getData().parallelStream().collect(Collectors.toMap(SysDept::getDeptName,SysDept::getDeptId,(a,b)->a));
            // Map<String, Long> majorIdToNameMap = standardMajorMapper.selectStandardMajorList(null).parallelStream().collect(Collectors.toMap(StandardMajor::getName, StandardMajor::getId,(a,b)->a));
            // Map<String, Long> categoryIdToNameMap = trainingSchemeCategoryMapper.selectTrainingSchemeCategoryList(null).parallelStream().collect(Collectors.toMap(TrainingSchemeCategory::getName, TrainingSchemeCategory::getId));
            Map<String, String> semesterMap = getDictDataLabelToValueMap("cur_semester_arrange");
            Map<String, String> springAutumnMap = getDictDataLabelToValueMap("cur_semester_arrange_season");
            // Map<String, String> kgDictionaryIdToNameMap = getKgDictionaryNameToIdMap(kgCourseModuleType);
            //List<Course> courseList = new ArrayList<>();
            if (CollectionUtils.isNotEmpty(excelVos)) {
                for (CourseExcelVo excelVo : excelVos) {
                    CourseVo courseVo = new CourseVo();
                    BeanUtils.copyProperties(excelVo, courseVo);
                    courseVo.setVersion(version);
                    courseVo.setCode(getCourseCode_new(courseVo));
                    // 处理学院
                    courseVo.setCollegeId(deptIdNameMap.get(excelVo.getCollegeName()));
                    // 处理 多个字典合成一列的
                    // dealCourseModuleAndChildrenName(excelVo.getCourseModuleAndChildrenName(), courseVo,kgDictionaryIdToNameMap);
                    courseVo.setCourseModule(courseModule);
                    courseVo.setCourseModuleChildren(courseModuleChildren);
                    courseVo.setMajorId(majorId);
                    courseVo.setCategoryId(categoryId);
                    courseVo.setType("1");
                    courseVo.setTemplateType(templateType);
                    //dealSemesterScheduleAndSpringAutumnName(excelVo.getSemesterScheduleAndSpringAutumnName(), courseVo, semesterMap, springAutumnMap);
                    UserUtils.reflash(courseVo);
                    courseVo.setSysflag(0);
                    courseVo.setStatus(0);
                    courseVo.setCourseAttr(excelVo.getCourseAttr());
                    Double totalHour = (excelVo.getTeachHours() == null ? 0 : excelVo.getTeachHours() )+ (excelVo.getPracticeHours() == null ? 0 : excelVo.getPracticeHours());
                    courseVo.setHours(totalHour);
                    courseVo.setCredit(new BigDecimal(totalHour/16).setScale(2, RoundingMode.HALF_UP).doubleValue());
                    courseVo.setCode(getCourseCode_new(courseVo));
                    courseVo.setSubMajorId(subMajorId);
                    checkCourseRepetition(courseVo);
                    courseMapper.insertCourse(courseVo);
                    //courseList.add(courseVo);
                }
                //checkCourseRepetition(courseList.stream().map(Course::getName).collect(Collectors.toList()), version, null);
                //courseMapper.insertCourseList(courseList);
            }
        } catch (IOException e) {
            throw new RuntimeException("导入课程体系数据异常：" + e.getMessage());
        }
        return Message.success();
    }

    private void checkCourseRepetition(List<String> courseNames, String version, String type) {
        if (courseNames.size() != courseNames.stream().distinct().count()) {
            throw new RuntimeException("您导入的课程名称已存在相同的名称，请更换!");
        }
        List<String> existNames = courseMapper.selectByNamesCount(courseNames, version, type);
        if (CollectionUtils.isNotEmpty(existNames)) {
            throw new RuntimeException("您导入的课程名称已存在相同的名称，请更换:" + String.join(",", existNames));
        }
    }


    private void dealCourseModuleAndChildrenName(String courseModuleAndChildrenName, CourseVo courseVo, Map<String, String> kgDictionaryIdToNameMap) {
        if (StringUtils.isNotBlank(courseModuleAndChildrenName)) {
            String[] split = courseModuleAndChildrenName.split("-");
            courseVo.setCourseModule(kgDictionaryIdToNameMap.get(split[0]));
            courseVo.setCourseModuleChildren(split.length > 1 ? kgDictionaryIdToNameMap.get(split[1]) : null);
        }
    }


    private void dealSemesterScheduleAndSpringAutumnName(String semesterScheduleAndSpringAutumnName, CourseVo courseVo,
                                                         Map<String, String> semesterMap, Map<String, String> springAutumnMap) {
        if (StringUtils.isNotBlank(semesterScheduleAndSpringAutumnName)) {
            String[] split = semesterScheduleAndSpringAutumnName.split("-");
            courseVo.setSemesterSchedule(semesterMap.get(split[0]));
            courseVo.setSpringAutumn(split.length > 1 ? springAutumnMap.get(split[1]) : null);
        }
    }


    @Override
    public void exportCourse(HttpServletResponse response, List<Long> ids) {
        ExcelUtil<CourseExcelVo> excelUtil = new ExcelUtil<>(CourseExcelVo.class);
        List<CourseExcelVo> volist = new ArrayList<>();
        List<Course> courses = courseMapper.selectCoursesByIds(ids);
        if (CollectionUtils.isNotEmpty(courses)) {
            Map<String, String> semesterMap = getDictDataValueToLabelMap("cur_semester_arrange");
            Map<String, String> springAutumnMap = getDictDataValueToLabelMap("cur_semester_arrange_season");
            Map<String, String> kgDictionaryIdToNameMap = getKgDictionaryIdToNameMap(kgCourseModuleType);
            Map<Long, String> deptIdToNameMap = doinnerDeptService.list(new CustomDept()).getData().parallelStream().collect(Collectors.toMap(SysDept::getDeptId, SysDept::getDeptName, (a, b) -> a));
            for (Course course : courses) {
                CourseExcelVo courseExcelVo = new CourseExcelVo();
                BeanUtils.copyProperties(course, courseExcelVo);
                courseExcelVo.setCollegeName(deptIdToNameMap.get(course.getCollegeId()));
                // 处理多级字典
                dealCourseModuleAndChildrenName(course.getCourseModule(), course.getCourseModuleChildren(), courseExcelVo, kgDictionaryIdToNameMap);
                /*List<String> semesterScheduleAndSpringAutumnName = new ArrayList<>();
                dealSemesterScheduleAndSpringAutumnName(semesterScheduleAndSpringAutumnName, course.getSemesterSchedule(), semesterMap);
                dealSemesterScheduleAndSpringAutumnName(semesterScheduleAndSpringAutumnName, course.getSpringAutumn(), springAutumnMap);
                if (CollectionUtils.isNotEmpty(semesterScheduleAndSpringAutumnName)) {
                    courseExcelVo.setSemesterScheduleAndSpringAutumnName(semesterScheduleAndSpringAutumnName.stream().collect(Collectors.joining("-")));
                }*/
                volist.add(courseExcelVo);
            }
        }
        excelUtil.exportExcel(response, volist, "课程体系");
    }

    private void dealSemesterScheduleAndSpringAutumnName(List<String> semesterScheduleAndSpringAutumnName, String key, Map<String, String> map) {
        if (StringUtils.isNotBlank(key) && StringUtils.isNotBlank(map.get(key))) {
            semesterScheduleAndSpringAutumnName.add(map.get(key));
        }
    }


    private void dealCourseModuleAndChildrenName(String courseModule, String courseModuleChildren, CourseExcelVo courseExcelVo, Map<String, String> kgDictionaryIdToNameMap) {
        List<String> courseModuleAndChildrenName = new ArrayList<>();
        if (StringUtils.isNotBlank(courseModule)) {
            courseModuleAndChildrenName.add(kgDictionaryIdToNameMap.getOrDefault(courseModule, ""));
        }
        if (StringUtils.isNotBlank(courseModuleChildren)) {
            courseModuleAndChildrenName.add(kgDictionaryIdToNameMap.getOrDefault(courseModuleChildren, ""));
        }
        if (CollectionUtils.isNotEmpty(courseModuleAndChildrenName)) {
            courseExcelVo.setCourseModuleAndChildrenName(courseModuleAndChildrenName.stream().collect(Collectors.joining("-")));
        }
    }


    @Override
    public void exportTrainingTemplate(HttpServletResponse response) {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("template/course/training.xlsx");
        ExcelUtils.exportTemplate(inputStream, response);
    }

    @Override
    @Transactional(rollbackFor = {Exception.class})
    public Message importTraining(MultipartFile file, Long majorId, String type, Integer templateType, String version) {
        ExcelUtil<TrainingExcelVo> excelUtil = new ExcelUtil<>(TrainingExcelVo.class);
        try {
            List<TrainingExcelVo> excelVos = excelUtil.importExcel(file.getInputStream()).stream()
                    .filter(t -> ObjectUtils.isNotEmpty(t.getName())).collect(Collectors.toList());
            if (ObjectUtils.isEmpty(excelVos)) {
                return Message.error(DomainExceptionConstant.DATA_IMPORT_TEMPLATE_ERROR);
            }
            Map<String, Long> deptIdNameMap = doinnerDeptService.list(new CustomDept()).getData().parallelStream().collect(Collectors.toMap(SysDept::getDeptName, SysDept::getDeptId, (a, b) -> a));
            //Map<String, String> semesterMap = getDictDataLabelToValueMap("cur_semester_arrange");
            //Map<String, String> springAutumnMap = getDictDataLabelToValueMap("cur_semester_arrange_season");
            if (CollectionUtils.isNotEmpty(excelVos)) {
                StringBuilder courseNameBuilder = new StringBuilder();
                for (TrainingExcelVo excelVo : excelVos) {
                    CourseVo courseVo = new CourseVo();
                    courseVo.setVersion(version);
                    BeanUtils.copyProperties(excelVo, courseVo);
                    courseVo.setType("2");
                    // 处理学院
                    courseVo.setCollegeId(deptIdNameMap.get(excelVo.getCollegeName()));
                    // 处理 多个字典合成一列的
                    //dealSemesterScheduleAndSpringAutumnName(excelVo.getSemesterScheduleAndSpringAutumnName(), courseVo, semesterMap, springAutumnMap);
                    courseVo.setTemplateType(templateType);
                    courseVo.setCredit(excelVo.getTimeWeek());
                    UserUtils.reflash(courseVo);
                    //checkCourseRepetition(courseVo);
                    if (!checkCourseRepetition(courseVo)) {
                        courseNameBuilder.append(courseVo.getName()).append(",");
                        continue;
                    }
                    courseVo.setLocation(excelVo.getSubjectModule());
                    courseMapper.insertCourse(courseVo);
                }
                if (ObjectUtils.isNotEmpty(courseNameBuilder)) {
                    throw new RuntimeException(courseNameBuilder.deleteCharAt(courseNameBuilder.length() - 1) + "存在重名，请更换一个名称！");
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("导入训练课程数据异常：" + e.getMessage());
        }
        return Message.success();
    }

    @Override
    public void exportTraining(HttpServletResponse response, List<Long> ids) {
        ExcelUtil<TrainingExcelVo> excelUtil = new ExcelUtil<>(TrainingExcelVo.class);
        List<TrainingExcelVo> volist = new ArrayList<>();
        List<Course> courses = courseMapper.selectCoursesByIds(ids);
        if (CollectionUtils.isNotEmpty(courses)) {
            //Map<String, String> semesterMap = getDictDataValueToLabelMap("cur_semester_arrange");
            //Map<String, String> springAutumnMap = getDictDataValueToLabelMap("cur_semester_arrange_season");
            Map<Long, String> deptIdToNameMap = doinnerDeptService.list(new CustomDept()).getData().parallelStream().collect(Collectors.toMap(SysDept::getDeptId, SysDept::getDeptName, (a, b) -> a));
            for (Course course : courses) {
                TrainingExcelVo excelVo = new TrainingExcelVo();
                BeanUtils.copyProperties(course, excelVo);
                excelVo.setCollegeName(deptIdToNameMap.get(course.getCollegeId()));
                excelVo.setSubjectModule(course.getLocation());
                // 处理多级字典
                /*List<String> semesterScheduleAndSpringAutumnName = new ArrayList<>();
                dealSemesterScheduleAndSpringAutumnName(semesterScheduleAndSpringAutumnName, course.getSemesterSchedule(), semesterMap);
                dealSemesterScheduleAndSpringAutumnName(semesterScheduleAndSpringAutumnName, course.getSpringAutumn(), springAutumnMap);
                if (CollectionUtils.isNotEmpty(semesterScheduleAndSpringAutumnName)) {
                    excelVo.setSemesterScheduleAndSpringAutumnName(semesterScheduleAndSpringAutumnName.stream().collect(Collectors.joining("-")));
                }*/
                volist.add(excelVo);
            }
        }
        excelUtil.exportExcel(response, volist, "训练课程");
    }


    @Override
    public void exportPracticeTemplate(HttpServletResponse response) {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("template/course/practice.xlsx");
        ExcelUtils.exportTemplate(inputStream, response);
    }

    @Override
    @Transactional(rollbackFor = {Exception.class})
    public Message importPractice(MultipartFile file, Long majorId, String type, Integer templateType, String version, Long categoryId, Long subMajorId) {
        ExcelUtil<PracticeExcelVo> excelUtil = new ExcelUtil<>(PracticeExcelVo.class);
        try {
            List<PracticeExcelVo> excelVos = excelUtil.importExcel(file.getInputStream()).stream()
                    .filter(t -> ObjectUtils.isNotEmpty(t.getName())).collect(Collectors.toList());
            if (ObjectUtils.isEmpty(excelVos)) {
                return Message.error(DomainExceptionConstant.DATA_IMPORT_TEMPLATE_ERROR);
            }
            Map<String, Long> deptIdNameMap = doinnerDeptService.list(new CustomDept()).getData().parallelStream().collect(Collectors.toMap(SysDept::getDeptName,SysDept::getDeptId,(a,b)->a));
            //Map<String, String> semesterMap = getDictDataLabelToValueMap("cur_semester_arrange");
            //Map<String, String> springAutumnMap = getDictDataLabelToValueMap("cur_semester_arrange_season");
            if (CollectionUtils.isNotEmpty(excelVos)) {
                StringBuilder courseNameBuilder = new StringBuilder();
                for (PracticeExcelVo excelVo : excelVos) {
                    CourseVo courseVo = new CourseVo();
                    courseVo.setVersion(version);
                    BeanUtils.copyProperties(excelVo, courseVo);
                    courseVo.setType("4");
                    // 处理学院
                    courseVo.setCollegeId(deptIdNameMap.get(excelVo.getCollegeName()));
                    courseVo.setMajorId(majorId);
                    courseVo.setCategoryId(categoryId);
                    courseVo.setTemplateType(templateType);
                    courseVo.setCredit(excelVo.getTimeWeek());
                    courseVo.setSubMajorId(subMajorId);
                    // 处理 多个字典合成一列的
                    //dealSemesterScheduleAndSpringAutumnName(excelVo.getSemesterScheduleAndSpringAutumnName(), courseVo, semesterMap, springAutumnMap);
                    UserUtils.reflash(courseVo);
                    if (!checkCourseRepetition(courseVo)) {
                        courseNameBuilder.append(courseVo.getName()).append(",");
                    }
                    courseMapper.insertCourse(courseVo);
                }
                if (ObjectUtils.isNotEmpty(courseNameBuilder)) {
                    throw new RuntimeException(courseNameBuilder.deleteCharAt(courseNameBuilder.length() - 1) + " 存在重名，请更换一个名称！");
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("导入实践项目数据异常：" + e.getMessage());
        }
        return Message.success();
    }

    @Override
    public void exportPractice(HttpServletResponse response, List<Long> ids) {
        ExcelUtil<PracticeExcelVo> excelUtil = new ExcelUtil<>(PracticeExcelVo.class);
        List<PracticeExcelVo> volist = new ArrayList<>();
        List<Course> courses = courseMapper.selectCoursesByIds(ids);
        if (CollectionUtils.isNotEmpty(courses)) {
            Map<String, String> semesterMap = getDictDataValueToLabelMap("cur_semester_arrange");
            Map<String, String> springAutumnMap = getDictDataValueToLabelMap("cur_semester_arrange_season");
            Map<Long, String> deptIdToNameMap = doinnerDeptService.list(new CustomDept()).getData().parallelStream().collect(Collectors.toMap(SysDept::getDeptId, SysDept::getDeptName, (a, b) -> a));

            for (Course course : courses) {
                PracticeExcelVo excelVo = new PracticeExcelVo();
                BeanUtils.copyProperties(course, excelVo);
                excelVo.setCollegeName(deptIdToNameMap.get(course.getCollegeId()));
                // 处理多级字典
                /*List<String> semesterScheduleAndSpringAutumnName = new ArrayList<>();
                dealSemesterScheduleAndSpringAutumnName(semesterScheduleAndSpringAutumnName, course.getSemesterSchedule(), semesterMap);
                dealSemesterScheduleAndSpringAutumnName(semesterScheduleAndSpringAutumnName, course.getSpringAutumn(), springAutumnMap);
                if (CollectionUtils.isNotEmpty(semesterScheduleAndSpringAutumnName)) {
                    excelVo.setSemesterScheduleAndSpringAutumnName(semesterScheduleAndSpringAutumnName.stream().collect(Collectors.joining("-")));
                }*/
                volist.add(excelVo);
            }
        }
        excelUtil.exportExcel(response, volist, "实践项目");
    }

    @Override
    @Transactional(rollbackFor = {Exception.class})
    public void insertCourseByTemplate(CourseTemplateVo courseTemplateVo) {
        for (Long id : courseTemplateVo.getIds()) {
            CourseVo courseVo = selectCourseById(id);
            courseVo.setSourceId(id);
            courseVo.setId(null);
            courseVo.setCollegeId(courseTemplateVo.getCollegeId());
            courseVo.setCategoryId(courseTemplateVo.getCategoryId());
            courseVo.setMajorId(courseTemplateVo.getMajorId());
            courseVo.setSubMajorId(courseTemplateVo.getSubMajorId());
            courseVo.setTemplateType(2);
            courseVo.setCode(getCourseCode_new(courseVo));
            courseVo.setCreator(null);
            courseVo.setCreateTime(null);
            UserUtils.reflash(courseVo);
            courseMapper.insertCourse(courseVo);
            if (CollectionUtils.isNotEmpty(courseVo.getCourseTargetList())) {
                // Map<Long,Long> oldCourseTargetToNewMap = new HashMap<>();
                for (CourseTarget courseTarget : courseVo.getCourseTargetList()) {
                    // Long oldCourseTargetId = courseTarget.getCourseTargetId();
                    courseTarget.setCourseId(courseVo.getId());
                    courseTarget.setCourseTargetId(null);
                    courseTargetMapper.insertCourseTarget(courseTarget);
                    // oldCourseTargetToNewMap.put(oldCourseTargetId,courseTarget.getCourseTargetId());
                    // 知识
                    if (CollectionUtils.isNotEmpty(courseTarget.getSourceKnowledgeVoList())) {
                        // 领域
                        for (SourceKnowledgeVo domain : courseTarget.getSourceKnowledgeVoList()) {
                            CourseRefSourceDomain courseRefSourceDomain = new CourseRefSourceDomain();
                            courseRefSourceDomain.setCourseId(courseVo.getId());
                            courseRefSourceDomain.setDomainId(domain.getId());
                            courseRefSourceDomain.setCollegeId(courseTemplateVo.getCollegeId());
                            courseRefSourceDomain.setCategoryId(courseTemplateVo.getCategoryId());
                            courseRefSourceDomain.setMajorId(courseTemplateVo.getMajorId());
                            courseRefSourceDomain.setCourseTargetId(courseTarget.getCourseTargetId());
                            courseRefSourceDomainMapper.insertCourseRefSourceDomain(courseRefSourceDomain);
                            if (CollectionUtils.isNotEmpty(domain.getChildren())) {
                                // 单元
                                for (SourceKnowledgeVo unit : domain.getChildren()) {
                                    CourseDomainRefSourceUnit courseDomainRefSourceUnit = new CourseDomainRefSourceUnit();
                                    courseDomainRefSourceUnit.setCourseId(courseVo.getId());
                                    courseDomainRefSourceUnit.setDomainId(domain.getId());
                                    courseDomainRefSourceUnit.setUnitId(unit.getId());
                                    courseDomainRefSourceUnit.setCollegeId(courseTemplateVo.getCollegeId());
                                    courseDomainRefSourceUnit.setCategoryId(courseTemplateVo.getCategoryId());
                                    courseDomainRefSourceUnit.setMajorId(courseTemplateVo.getMajorId());
                                    courseDomainRefSourceUnit.setCourseTargetId(courseTarget.getCourseTargetId());
                                    courseDomainRefSourceUnitMapper.insertCourseDomainRefSourceUnit(courseDomainRefSourceUnit);
                                    // 没到知识点这一层
                                    if (CollectionUtils.isNotEmpty(unit.getChildren())) {
                                        for (SourceKnowledgeVo point : unit.getChildren()) {
                                            CourseUnitRefSourcePoint courseUnitRefSourcePoint = new CourseUnitRefSourcePoint();
                                            courseUnitRefSourcePoint.setCourseId(courseVo.getId());
                                            courseUnitRefSourcePoint.setPointId(point.getId());
                                            courseUnitRefSourcePoint.setUnitId(unit.getId());
                                            courseUnitRefSourcePoint.setCollegeId(courseTemplateVo.getCollegeId());
                                            courseUnitRefSourcePoint.setCategoryId(courseTemplateVo.getCategoryId());
                                            courseUnitRefSourcePoint.setMajorId(courseTemplateVo.getMajorId());
                                            courseUnitRefSourcePoint.setCourseTargetId(courseTarget.getCourseTargetId());
                                            courseUnitRefSourcePointMapper.insertCourseUnitRefSourcePoint(courseUnitRefSourcePoint);
                                        }
                                    }
                                }
                            }
                        }
                    }
                    // 毕业目标
                    if (CollectionUtils.isNotEmpty(courseTarget.getGraduationTreeVoList())) {
                        List<GraduationTreeVo> graduationTreeVos = TreeBuilderUtils.flattenTree(courseTarget.getGraduationTreeVoList());
                        for (GraduationTreeVo graduationTreeVo : graduationTreeVos) {
                            CourseRefGraduation courseRefGraduation = new CourseRefGraduation();
                            courseRefGraduation.setCourseId(courseVo.getId());
                            courseRefGraduation.setCourseTargetId(courseTarget.getCourseTargetId());
                            courseRefGraduation.setGraduationId(graduationTreeVo.getId());
                            courseRefGraduation.setCollegeId(courseTemplateVo.getCollegeId());
                            courseRefGraduation.setCategoryId(courseTemplateVo.getCategoryId());
                            courseRefGraduation.setMajorId(courseTemplateVo.getMajorId());
                            courseRefGraduationMapper.insertCourseRefGraduation(courseRefGraduation);
                        }
                    }
                    // 能力
                    if (CollectionUtils.isNotEmpty(courseTarget.getAbilityVoList())) {
                        List<StandardTreeVo> standardTreeVos = TreeBuilderUtils.flattenTree(courseTarget.getAbilityVoList());
                        for (StandardTreeVo standardTreeVo : standardTreeVos) {
                            CourseRefAbility courseRefAbility = new CourseRefAbility();
                            courseRefAbility.setCourseId(courseVo.getId());
                            courseRefAbility.setCourseTargetId(courseTarget.getCourseTargetId());
                            courseRefAbility.setAbilityId(standardTreeVo.getId());
                            courseRefAbility.setCollegeId(courseTemplateVo.getCollegeId());
                            courseRefAbility.setCategoryId(courseTemplateVo.getCategoryId());
                            courseRefAbility.setMajorId(courseTemplateVo.getMajorId());
                            courseRefAbilityMapper.insertCourseRefAbility(courseRefAbility);
                        }
                    }
                    // 素质
                    if (CollectionUtils.isNotEmpty(courseTarget.getQualityVoList())) {
                        List<StandardTreeVo> qualityTreeVos = TreeBuilderUtils.flattenTree(courseTarget.getQualityVoList());
                        for (StandardTreeVo qualityTreeVo : qualityTreeVos) {
                            CourseRefQuality courseRefQuality = new CourseRefQuality();
                            courseRefQuality.setCourseId(courseVo.getId());
                            courseRefQuality.setCourseTargetId(courseTarget.getCourseTargetId());
                            courseRefQuality.setQualityId(qualityTreeVo.getId());
                            courseRefQuality.setCollegeId(courseTemplateVo.getCollegeId());
                            courseRefQuality.setCategoryId(courseTemplateVo.getCategoryId());
                            courseRefQuality.setMajorId(courseTemplateVo.getMajorId());
                            courseRefQualityMapper.insertCourseRefQuality(courseRefQuality);
                        }
                    }
                }
            }
        }
    }

    @Override
    @Transactional(rollbackFor = {Exception.class})
    public void insertCourseByTemplate_new(CourseTemplateVo courseTemplateVo) {
        //查询所有选择的课程
        List<Course> oldCourseList = courseMapper.selectCoursesByIds(courseTemplateVo.getIds());
        //查询目标专业是否有已经调用的课程
        List<Long> quotedCourseIdList = courseMapper.selectQuotedCourse(courseTemplateVo);
        //过滤已经调用过的课程
        List<Course> quoteCourseList = oldCourseList.stream().filter(c -> !quotedCourseIdList.contains(c.getId())).collect(Collectors.toList());
        if (ObjectUtils.isEmpty(quoteCourseList)) {
            return;
        }
        List<Long> quoteCourseIds = quoteCourseList.stream().map(Course::getId).collect(Collectors.toList());
        /**复制课程*/
        List<Course> new_courseList = new ArrayList<>();
        quoteCourseList.forEach(c -> {
            Course course = copyCourse(courseTemplateVo, c);
            new_courseList.add(course);
        });
        //课程批量保存
        courseMapper.insertCourseList(new_courseList);
        //新旧课程id对应 sourceId-旧ID id-新ID
        Map<Long, Long> oldIdToNewIdMap = new_courseList.stream().collect(Collectors.toMap(Course::getSourceId, Course::getId));
        /** 复制课程知识单元知识点*/
        //查询课程目标
        List<CourseTarget> courseTargets = courseTargetMapper.selectCourseTargetByCourseIds(quoteCourseIds);
        ArrayList<CourseTarget> newTargetList = new ArrayList<>();
        for (CourseTarget oldTarget : courseTargets) {
            CourseTarget courseTarget = new CourseTarget();
            BeanUtils.copyProperties(oldTarget, courseTarget);
            courseTarget.setCourseTargetId(null);
            courseTarget.setCourseId(oldIdToNewIdMap.get(oldTarget.getCourseId()));
            courseTarget.setSourceId(oldTarget.getCourseTargetId());
            UserUtils.clearAndRefreshObj(courseTarget);
            newTargetList.add(courseTarget);
        }
        if (ObjectUtils.isNotEmpty(newTargetList)) {
            //课程目标批量保存
            courseTargetMapper.insertCourseTargets(newTargetList);
            //新旧课程目标id对应 sourceId-旧ID courseTargetId-新ID
            Map<Long, Long> oldIdToNewTargetIdMap = newTargetList.stream().collect(Collectors.toMap(CourseTarget::getSourceId, CourseTarget::getCourseTargetId));
            /**
             * 复制课程目标绑定的知识图谱 添加三张关联表数据
             *   courseUnitRefSourcePointMapper.
             *   courseDomainRefSourceUnitMapper
             *   courseRefSourceDomainMapper
             */
            List<CourseUnitRefSourcePoint> oldCourseTargetRefPointList = courseUnitRefSourcePointMapper.selectCourseTargetRefSourcePointByCourseIds(quoteCourseIds);
            if (ObjectUtils.isNotEmpty(oldCourseTargetRefPointList)) {
                List<CourseUnitRefSourcePoint> newCourseTargetRefPointList = copyTargetRef(oldCourseTargetRefPointList, courseTemplateVo, oldIdToNewIdMap, oldIdToNewTargetIdMap, CourseUnitRefSourcePoint::new);
                courseUnitRefSourcePointMapper.insertCourseUnitRefSourcePointList(newCourseTargetRefPointList);
            }

            List<CourseDomainRefSourceUnit> oldCourseTargetRefUnitList = courseDomainRefSourceUnitMapper.selectCourseTargetRefSourceUnitByCourseIds(quoteCourseIds);
            if (ObjectUtils.isNotEmpty(oldCourseTargetRefUnitList)) {
                List<CourseDomainRefSourceUnit> newCourseTargetRefUnitList = copyTargetRef(oldCourseTargetRefUnitList, courseTemplateVo, oldIdToNewIdMap, oldIdToNewTargetIdMap, CourseDomainRefSourceUnit::new);
                courseDomainRefSourceUnitMapper.insertCourseUnitRefSourceUnitList(newCourseTargetRefUnitList);
            }

            List<CourseRefSourceDomain> oldCourseTargetRefDomainList = courseRefSourceDomainMapper.selectCourseTargetRefSourceDomainByCourseIds(quoteCourseIds);
            if (ObjectUtils.isNotEmpty(oldCourseTargetRefDomainList)) {
                List<CourseRefSourceDomain> newCourseTargetRefDomainList = copyTargetRef(oldCourseTargetRefDomainList, courseTemplateVo, oldIdToNewIdMap, oldIdToNewTargetIdMap, CourseRefSourceDomain::new);
                courseRefSourceDomainMapper.insertCourseUnitRefSourceDomainList(newCourseTargetRefDomainList);
            }
            /**
             * 复制课程目标绑定的毕业标准
             * courseRefGraduationMapper
             */
            List<CourseRefGraduation> oldCourseTargetRefGraduationList = courseRefGraduationMapper.selectCourseTargetRefGraduationByCourseIds(quoteCourseIds);
            if (ObjectUtils.isNotEmpty(oldCourseTargetRefGraduationList)) {
                List<CourseRefGraduation> newCourseTargetRefGraduationList = copyTargetRef(oldCourseTargetRefGraduationList, courseTemplateVo, oldIdToNewIdMap, oldIdToNewTargetIdMap, CourseRefGraduation::new);
                courseRefGraduationMapper.insertCourseTargetRefGraduationList(newCourseTargetRefGraduationList);
            }
            /**
             * 复制课程目标绑定的能力
             * courseRefAbilityMapper
             */
            List<CourseRefAbility> oldCourseTargetRefAbilityList = courseRefAbilityMapper.selectCourseTargetRefAbilityByCourseIds(quoteCourseIds);
            if (ObjectUtils.isNotEmpty(oldCourseTargetRefAbilityList)) {
                List<CourseRefAbility> newCourseTargetRefGraduationList = copyTargetRef(oldCourseTargetRefAbilityList, courseTemplateVo, oldIdToNewIdMap, oldIdToNewTargetIdMap, CourseRefAbility::new);
                courseRefAbilityMapper.insertCourseTargetRefAbilityList(newCourseTargetRefGraduationList);
            }
            /**
             * 复制课程目标绑定的素质
             * courseRefQualityMapper
             */
            List<CourseRefQuality> oldCourseTargetRefQualityList = courseRefQualityMapper.selectCourseTargetRefQualityByCourseIds(quoteCourseIds);
            if (ObjectUtils.isNotEmpty(oldCourseTargetRefQualityList)) {
                List<CourseRefQuality> newCourseTargetRefQualityList = copyTargetRef(oldCourseTargetRefQualityList, courseTemplateVo, oldIdToNewIdMap, oldIdToNewTargetIdMap, CourseRefQuality::new);
                courseRefQualityMapper.insertCourseTargetRefQualityList(newCourseTargetRefQualityList);
            }
        }
    }

    private <T extends BaseCourseTargetRefEntity> List<T> copyTargetRef(List<T> oldList,
                                                                        CourseTemplateVo courseTemplateVo,
                                                                        Map<Long, Long> oldIdToNewIdMap,
                                                                        Map<Long, Long> oldIdToNewTargetIdMap,
                                                                        Supplier<T> entityCreator) {
        List<T> newList = new ArrayList<>();
        if (ObjectUtils.isEmpty(oldList)) {
            return Collections.emptyList();
        }
        for (T old : oldList) {
            T newRef = entityCreator.get();
            BeanUtils.copyProperties(old, newRef);
            newRef.setCourseId(oldIdToNewIdMap.get(old.getCourseId()));
            newRef.setCourseTargetId(oldIdToNewTargetIdMap.get(old.getCourseTargetId()));
            newRef.setMajorId(courseTemplateVo.getMajorId());
            newRef.setCategoryId(courseTemplateVo.getCategoryId());
            newRef.setCollegeId(courseTemplateVo.getCollegeId());
            newList.add(newRef);
        }
        return newList;
    }

    @NotNull
    private static Course copyCourse(CourseTemplateVo courseTemplateVo, Course c) {
        Course course = new Course();
        BeanUtils.copyProperties(c, course);
        course.setSourceId(c.getId());
        course.setMajorId(courseTemplateVo.getMajorId());
        course.setCollegeId(courseTemplateVo.getCollegeId());
        course.setCategoryId(courseTemplateVo.getCategoryId());
        course.setVersion(courseTemplateVo.getVersion());
        course.setTemplateType(2);
        UserUtils.clearAndRefreshObj(course);
        return course;
    }

    @Override
    public TreeVo courseTreeById(Long id, Integer type) {
        TreeVo treeVo = new TreeVo();
        CourseVo courseVo = courseMapper.selectCourseAndRelevanceById(id);
        buildCourse(treeVo, courseVo, type);
        return treeVo;
    }

    private void buildCourse(TreeVo treeVo, Course courseVo, Integer type) {
        treeVo.setId(courseVo.getId());
        treeVo.setName(courseVo.getName());
        List<CourseTarget> courseTargets = courseTargetMapper.selectCourseTargetByCourseId(courseVo.getId());
        if (CollectionUtils.isNotEmpty(courseTargets)) {
            List<TreeVo> courseTargetTreeList = new ArrayList<>();
            for (CourseTarget courseTarget : courseTargets) {
                TreeVo courseTargetTreeVo = new TreeVo();
                courseTargetTreeVo.setId(courseTarget.getCourseTargetId());
                courseTargetTreeVo.setName(courseTarget.getName());
                if (type == 1) {
                    // 知识
                    List<SourceDomain> sourceDomains = sourceDomainMapper.selectSourceDomainByCourseTargetId(courseTarget.getCourseTargetId());
                    if (CollectionUtils.isNotEmpty(sourceDomains)) {
                        // 知识领域
                        List<TreeVo> domainTreeList = new ArrayList<>();
                        for (SourceDomain sourceDomain : sourceDomains) {
                            TreeVo domainTreeVo = new TreeVo();
                            domainTreeVo.setId(sourceDomain.getId());
                            domainTreeVo.setName(sourceDomain.getName());
                            if (CollectionUtils.isNotEmpty(sourceDomain.getSourceUnits())) {
                                // 知识单元
                                List<TreeVo> unitTreeList = new ArrayList<>();
                                for (SourceUnit sourceUnit : sourceDomain.getSourceUnits()) {
                                    TreeVo unitTreeVo = new TreeVo();
                                    unitTreeVo.setId(sourceUnit.getId());
                                    unitTreeVo.setName(sourceUnit.getName());
                                    if (CollectionUtils.isNotEmpty(sourceUnit.getSourcePoints())) {
                                        // 知识点
                                        List<TreeVo> pointTreeList = new ArrayList<>();
                                        for (SourcePoint sourcePoint : sourceUnit.getSourcePoints()) {
                                            TreeVo pointTreeVo = new TreeVo();
                                            pointTreeVo.setId(sourcePoint.getId());
                                            pointTreeVo.setName(sourcePoint.getName());
                                            pointTreeList.add(pointTreeVo);
                                        }
                                        unitTreeVo.setChildren(pointTreeList);
                                    }
                                    unitTreeList.add(unitTreeVo);
                                }
                                domainTreeVo.setChildren(unitTreeList);
                            }
                            domainTreeList.add(domainTreeVo);
                        }
                        courseTargetTreeVo.setChildren(domainTreeList);
                    }
                } else {
                    // 毕业要求
                    List<CourseRefGraduation> courseRefGraduation = courseRefGraduationMapper.selectCourseRefGraduationByCourseTargetId(courseTarget.getCourseTargetId());
                    if (CollectionUtils.isNotEmpty(courseRefGraduation)) {
                        List<StandardGraduation> standardGraduations = standardGraduationMapper.selectStandardGraduationByIds(courseRefGraduation.stream().map(a -> a.getGraduationId()).collect(Collectors.toList()));
                        List<TreeVo> graduationTreeVoList = new ArrayList<>();
                        if (CollectionUtils.isNotEmpty(standardGraduations)) {
                            for (StandardGraduation standardGraduation : standardGraduations) {
                                TreeVo graduationTreeVo = new TreeVo();
                                graduationTreeVo.setId(standardGraduation.getId());
                                graduationTreeVo.setName(standardGraduation.getName());
                                graduationTreeVo.setParentId(standardGraduation.getParentId());
                                graduationTreeVoList.add(graduationTreeVo);
                            }
                            courseTargetTreeVo.setChildren(TreeBuilderUtils.buildRootTree(graduationTreeVoList));
                        }
                    }
                }
                courseTargetTreeList.add(courseTargetTreeVo);
            }
            treeVo.setChildren(courseTargetTreeList);
        }
    }


    @Override
    public TreeVo courseTreeByMajorId(Long majorId, Integer type, String courseType) {
        TreeVo treeVo = new TreeVo();
        StandardMajor standardMajor = standardMajorMapper.selectStandardMajorById(majorId);
        treeVo.setId(standardMajor.getId());
        treeVo.setName(standardMajor.getName());
        Course course = new Course();
        course.setMajorId(majorId);
        course.setType(courseType);
        course.setTemplateType(2);
        List<CourseVo> courses = courseMapper.selectCourseAndRelevanceList(course);
        if (CollectionUtils.isNotEmpty(courses)) {
            List<TreeVo> courseTrees = new ArrayList<>();
            for (Course c : courses) {
                TreeVo courseTree = new TreeVo();
                buildCourse(courseTree, c, type);
                courseTrees.add(courseTree);
            }
            treeVo.setChildren(courseTrees);
        }
        return treeVo;
    }

    @Override
    public Map<String, Object> getSchemeCourseInfo(Long schemeId) {
        HashMap<String, Object> data = new HashMap<>();
        AtomicInteger pointNum = new AtomicInteger(0);
        HashMap<Long, KnowledgeUnitVo> unitNumMap = new HashMap<>();
        //查询培养方案绑定的课程
        List<TrainingSchemeRefCourse> trainingSchemeRefCourses = trainingSchemeRefCourseMapper.selectTrainingSchemeRefCourseByTrainingSchemeVoId(schemeId);
        //查询课程信息
        if (ObjectUtils.isNotEmpty(trainingSchemeRefCourses)) {
            List<Course> courses = courseMapper.selectCoursesByIds(trainingSchemeRefCourses.stream().map(a -> a.getCourseId()).collect(Collectors.toList()));
            //查询课程关联的知识单元、知识点数量
            List<Map<String, Long>> maps = courseMapper.selectKnowledgeNum(courses.stream().map(c->c.getId()).collect(Collectors.toList()), 0L);
            if (ObjectUtils.isNotEmpty(maps)) {
                Map<String, Long> stringLongMap = maps.get(0);
                data.put("courseKnowLedgeUnitNum", stringLongMap.get("unitCount"));
                data.put("courseKnowLedgePointNum", stringLongMap.get("pointCount"));
            } else {
                data.put("courseKnowLedgeUnitNum", 0);
                data.put("courseKnowLedgePointNum", 0);
            }
            //统计训练课目与实践项目的总学时、总学分
            AtomicDouble trainingWeeks = new AtomicDouble(0);
            AtomicDouble trainingCredits = new AtomicDouble(0);
            AtomicDouble practicalWeeks = new AtomicDouble(0);
            AtomicDouble practicalCredits = new AtomicDouble(0);
            courses.stream().filter(c -> c.getType().equals("2")).forEach(courseVo -> {
                trainingWeeks.addAndGet(courseVo.getTimeWeek() == null ? 0 : courseVo.getTimeWeek());
                trainingCredits.addAndGet(courseVo.getCredit() == null ? 0 : courseVo.getCredit());
            });
            courses.stream().filter(c -> c.getType().equals("4")).forEach(courseVo -> {
                practicalWeeks.addAndGet(courseVo.getTimeWeek() == null ? 0 : courseVo.getTimeWeek());
                practicalCredits.addAndGet(courseVo.getCredit() == null ? 0 : courseVo.getCredit());
            });
            //返回课程相关统计
            data.put("courseNum", courses.stream().filter(c -> c.getType().equals("1") || c.getType().equals("3")).count());
//            data.put("courseKnowLedgeUnitNum", unitNumMap.size());
//            data.put("courseKnowLedgePointNum", pointNum.get());
            //返回训练课目相关统计
            data.put("trainingNum", courses.stream().filter(c -> c.getType().equals("2")).count());
            data.put("trainingHours", trainingWeeks.get());
            data.put("trainingCredits", trainingCredits.get());
            //返回实践训练相关统计
            data.put("practicalNum", courses.stream().filter(c -> c.getType().equals("4")).count());
            data.put("practicalHours", practicalWeeks.get());
            data.put("practicalCredits", practicalCredits.get());
        }
        return data;
    }

    @Override
    public List<Course> checkSchemeCourse(TrainingCourseVo trainingCourseVo) {
        // 排序方式归一化: ascending/descending -> asc/desc
        if (StringUtils.isNotBlank(trainingCourseVo.getOrder())) {
            if (CourseConstant.CUR_SORT_ASC.equals(trainingCourseVo.getOrder())) {
                trainingCourseVo.setOrder("asc");
            } else if (CourseConstant.CUR_SORT_DESC.equals(trainingCourseVo.getOrder())) {
                trainingCourseVo.setOrder("desc");
            }
        }
        Map<String, String> kgDictionaryIdToNameMap = getKgDictionaryIdToNameMap(kgCourseModuleType);
        List<TrainingSchemeRefCourse> trainingSchemeRefCourses = trainingSchemeRefCourseMapper.selectTrainingSchemeRefCourseByTrainingSchemeVoIdAndCourseType(trainingCourseVo);
        if (ObjectUtils.isEmpty(trainingSchemeRefCourses)) {
            return Collections.emptyList();
        }
        PageUtils.startPage();
        List<Course> courses = courseMapper.selectCoursesByIdsWithSort(
                trainingSchemeRefCourses.stream().map(a -> a.getCourseId()).collect(Collectors.toList()),
                trainingCourseVo.getDatabase_prop(),
                trainingCourseVo.getOrder());
        Map<Long, String> majorIdToNameMap = standardMajorMapper.selectStandardMajorList(null).stream().collect(Collectors.toMap(StandardMajor::getId, StandardMajor::getName));
        Map<Long, String> categoryToNameMap = trainingSchemeCategoryMapper.selectTrainingSchemeCategoryList(null).stream().collect(Collectors.toMap(TrainingSchemeCategory::getId, TrainingSchemeCategory::getName));

        for (Course c : courses) {
            if (MapUtils.isNotEmpty(kgDictionaryIdToNameMap)) {
                if (StringUtils.isNotBlank(c.getCourseModule())) {
                    c.setCourseModuleName(kgDictionaryIdToNameMap.get(c.getCourseModule()));
                }
                if (StringUtils.isNotBlank(c.getCourseModuleChildren())) {
                    c.setCourseModuleChildrenName(kgDictionaryIdToNameMap.get(c.getCourseModuleChildren()));
                }
            }
            if (c.getMajorId() != null) {
                c.setMajorName(majorIdToNameMap.get(c.getMajorId()));
            }
            if (c.getSubMajorId() != null) {
                c.setSubMajorName(majorIdToNameMap.get(c.getSubMajorId()));
            }
            if (c.getCategoryId() != null) {
                c.setCategoryName(categoryToNameMap.get(c.getCategoryId()));
            }
        }
        //单位ID替换

        for (Course course : courses) {

        }
        return courses;
    }

}
