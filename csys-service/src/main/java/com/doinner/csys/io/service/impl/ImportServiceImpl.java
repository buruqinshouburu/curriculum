package com.doinner.csys.io.service.impl;


import com.doinner.common.core.exception.DataFormatException;
import com.doinner.csys.constant.DomainFieldConstant;
import com.doinner.csys.constant.DomainExceptionConstant;
import com.doinner.csys.constant.StandardConstant;
import com.doinner.csys.dao.*;
import com.doinner.csys.domain.*;
import com.doinner.csys.domain.vo.StandardCultivationImportVo;
import com.doinner.csys.domain.vo.TrainingSchemeVo;
import com.doinner.csys.domain.vo.TreeTableVo;
import com.doinner.csys.exception.FileException;
import com.doinner.csys.io.utils.ExcelUtils;
import com.doinner.csys.io.service.ImportService;
import com.doinner.csys.service.CurriculumService;
import com.doinner.csys.service.StandardService;
import com.doinner.csys.service.TrainingService;
import com.doinner.csys.utils.UserUtils;
import com.doinner.system.domain.custom.CustomDept;
import com.doinner.system.domain.entity.SysDept;
import com.doinner.system.domain.entity.SysDictData;
import com.doinner.system.service.DoinnerDeptService;
import com.doinner.system.service.DoinnerDictDataService;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;


@Service
@Transactional(readOnly = true)
public class ImportServiceImpl implements ImportService{

    @Resource
    private CourseMapper courseMapper;

    @Resource
    private TrainingService trainingService;

    @Resource
    private TrainingSchemeCourseScheduleMapper trainingSchemeCourseScheduleMapper;

    @Resource
    private TrainingSchemeMapper trainingSchemeMapper;

    @Resource
    private CurriculumService curriculumService;

    @Resource
    private DoinnerDeptService doinnerDeptService;

    @Resource
    private DoinnerDictDataService doinnerDictDataService;

    @Resource
    private StandardService stdService;

    @Resource
    private StandardMajorMapper standardMajorMapper;

    @Resource
    private TrainingSchemeCategoryMapper trainingSchemeCategoryMapper;

    @Resource
    private TrainingSchemeRefCourseMapper trainingSchemeRefCourseMapper;

    @Resource
    private CourseRefKeUnitMapper courseRefKeUnitMapper;

    @Autowired
    private ResourceLoader resourceLoader;

    @Resource
    private KnowledgeUnitRefStdCultivationMapper knowledgeUnitRefStdCultivationMapper;

    @Resource
    private StandardCultivationTargetMapper standardCultivationTargetMapper;
    @Resource
    private StandardGraduationMapper standardGraduationMapper;
    @Resource
    private StandardCultivationMapper standardCultivationMapper;
    @Resource
    private StandardGraduationRefCultivationTargetMapper standardGraduationRefCultivationTargetMapper;
    @Resource
    private StandardCultivationRefGraduationMapper standardCultivationRefGraduationMapper;

    @Override
    @Transactional(rollbackFor = {Exception.class})
    public void courseImport(MultipartFile file, Long collegeId) {
        List<Map<String, String>> excelData = ExcelUtils.readExcel(file);
        if(ObjectUtils.isEmpty(excelData)){
            return;
        }
        //查出全部部门
        CustomDept sysDept = new CustomDept();
        List<SysDept> list = doinnerDeptService.list(sysDept).getData();
        Map<String, List<SysDept>> deptNameIdMap = list.parallelStream().collect(Collectors.groupingBy(SysDept::getDeptName));
        //查出课程类型
        List<SysDictData> dictDateList = doinnerDictDataService.dictType(DomainFieldConstant.COURSE_DICT_TYPE).getData();
        Map<String, String> dictCodeValueMap = dictDateList.parallelStream().collect(Collectors.toMap(SysDictData::getDictLabel, SysDictData::getDictValue));
        Map<String,String> codeMap = new ConcurrentHashMap<>();
        List<Course> courseList = excelData.stream().map(rowData -> {
            Course course = new Course();
            if(!rowData.containsKey(DomainFieldConstant.COURSE)){
                throw new DataFormatException(DomainExceptionConstant.COURSE_NAME_CAN_NOT_NULL);
            }
            course.setName(rowData.get(DomainFieldConstant.COURSE));
            if(rowData.containsKey(DomainFieldConstant.COURSE_TYPE) && dictCodeValueMap.containsKey(rowData.get(DomainFieldConstant.COURSE_TYPE))){
                course.setType(Integer.parseInt(dictCodeValueMap.get(rowData.get(DomainFieldConstant.COURSE_TYPE)))+"");
            }else{
                String errMessage = null;
                if(!rowData.containsKey(DomainFieldConstant.COURSE_TYPE) || StringUtils.isBlank(rowData.get(DomainFieldConstant.COURSE_TYPE))){
                    errMessage = String.format(DomainExceptionConstant.COURSE_TYPE_IS_EMPTY, course.getName());
                }else{
                    errMessage = String.format(DomainExceptionConstant.COURSE_TYPE_NOT_EXISTS, course.getName(), rowData.get(DomainFieldConstant.COURSE_TYPE));
                }
                throw new DataFormatException(errMessage);
            }

            if(rowData.containsKey(DomainFieldConstant.COURSE_CODE) && StringUtils.isNotBlank(rowData.get(DomainFieldConstant.COURSE_CODE))){
                course.setCode(rowData.get(DomainFieldConstant.COURSE_CODE));
                if(codeMap.containsKey(course.getCode())){
                    String errMessage = String.format(DomainExceptionConstant.COURSE_CODE_HAS_EXISTS, course.getName(), codeMap.get(course.getCode()), course.getCode());
                    throw new DataFormatException(errMessage);
                }
                codeMap.put(course.getCode(), course.getName());
            }else{
                String errMessage = String.format(DomainExceptionConstant.COURSE_CODE_NOT_EXISTS, course.getName());
                throw new DataFormatException(errMessage);
            }

            if(rowData.containsKey(DomainFieldConstant.COURSE_COLLEGE) && deptNameIdMap.containsKey(rowData.get(DomainFieldConstant.COURSE_COLLEGE))){
                List<SysDept> sysDepts = deptNameIdMap.get(rowData.get(DomainFieldConstant.COURSE_COLLEGE));
                course.setCollegeId(sysDepts.get(0).getDeptId());
            }else{
                course.setCollegeId(collegeId);
            }
            return course;
        }).collect(Collectors.toList());
        curriculumService.insertCourse(courseList.stream().filter(c->ObjectUtils.isNotEmpty(c.getName())).collect(Collectors.toList()));
    }

    /**
     * 能力素质导入
     * @param file
     * @param collegeId
     */
    @Override
    @Transactional(rollbackFor = {Exception.class})
    public void importAbility(MultipartFile file, Long collegeId) {
        List<TreeTableVo> treeTableVos = ExcelUtils.readTreeTable(file, 0, 1, 2, 3);
        List<StandardAbility> standardAbilityList = treeTableVos.parallelStream().map(TreeTableVo::toStandardAbility)
                .filter(s->ObjectUtils.isNotEmpty(s.getName())).collect(Collectors.toList());
        setStandardAbilityCommonFiled(standardAbilityList,collegeId,null);
        stdService.insertStandardAbilityTree(standardAbilityList);
    }

    private void setStandardAbilityCommonFiled(List<StandardAbility> standardAbilityList, Long collegeId, StandardAbility parent){
        standardAbilityList.parallelStream().forEach(standardAbility -> {
            standardAbility.setCollegeId(collegeId);
            if (standardAbility.getType() == null){
                standardAbility.setType(StandardConstant.ABILITY_TYPE_QUALITY);
            }
            if(ObjectUtils.isNotEmpty(parent)){
                standardAbility.setMajorId(parent.getMajorId());
                standardAbility.setSubMajorId(parent.getSubMajorId());
                standardAbility.setClassId(parent.getClassId());
            }
            if(ObjectUtils.isNotEmpty(standardAbility.getChildren())){
                setStandardAbilityCommonFiled((List<StandardAbility>)standardAbility.getChildren(), collegeId, standardAbility);
            }
        });
    }

    /**
     * 培养目标导入
     * @param file
     * @param schemeId
     */
    @Override
    @Transactional(rollbackFor = {Exception.class})
    public void importStandardCultivationTarget(MultipartFile file, Long schemeId) {
        List<TreeTableVo> treeTableVos = ExcelUtils.readTreeTable(file, 0, 1);
//        setStandardMajor(treeTableVos, collegeId);
        List<StandardCultivationTarget> standardCultivationTargetList = treeTableVos.parallelStream().map(TreeTableVo::toStandardCultivationTarget).collect(Collectors.toList());
        if(ObjectUtils.isEmpty(standardCultivationTargetList)){
            return;
        }
        TrainingSchemeVo trainingSchemeVo = trainingSchemeMapper.selectTrainingSchemeById(schemeId);
        //删除现有培养目标
        standardCultivationTargetMapper.deleteStandardCultivationTargetBySchemeId(trainingSchemeVo.getId());
        //添加导入
        setStandardCultivationTargetCommonFiled(standardCultivationTargetList, trainingSchemeVo,null);
        stdService.insertStandardCultivationTargetTree(standardCultivationTargetList);
    }

    public void setStandardMajor(List<TreeTableVo> treeTableVos, Long collegeId){
        treeTableVos.parallelStream().forEach(treeTableVo -> {
            String majorName = treeTableVo.getParams().get(DomainFieldConstant.STANDARD_MAJOR_NAME);
            if(StringUtils.isBlank(majorName)){
                String errMessage = String.format(DomainExceptionConstant.TSTANDARD_HAVENT_MAJOR, treeTableVo.getParams().get(DomainFieldConstant.STANDARD_CULTIVATION_TARGET_NAME));
                throw new DataFormatException(errMessage);
            }
            StandardMajor standardMajor = new StandardMajor();
            standardMajor.setName(majorName);
            standardMajor.setLevel(1l);
            standardMajor.setCollegeId(collegeId);
            List<StandardMajor> standardMajors = standardMajorMapper.selectStandardMajorAccurate(standardMajor);
            if(ObjectUtils.isEmpty(standardMajors)){
                String errMessage = String.format(DomainExceptionConstant.MAJOR_OF_STANDARD_NOT_EXISTS, treeTableVo.getParams().get(DomainFieldConstant.STANDARD_MAJOR_NAME));
                throw new DataFormatException(errMessage);
            }
            treeTableVo.getParams().put(DomainFieldConstant.STANDARD_MAJOR_NAME, standardMajors.get(0).getId().toString());
            if(treeTableVo.getParams().containsKey(DomainFieldConstant.STANDARD_SUB_MAJOR_NAME) && StringUtils.isNotBlank(treeTableVo.getParams().get(DomainFieldConstant.STANDARD_SUB_MAJOR_NAME))) {
                String subMajorName = treeTableVo.getParams().get(DomainFieldConstant.STANDARD_SUB_MAJOR_NAME);
                standardMajor.setLevel(null);
                standardMajor.setName(subMajorName);
                standardMajor.setParentId(standardMajors.get(0).getId());
                standardMajors = standardMajorMapper.selectStandardMajorAccurate(standardMajor);
                if(ObjectUtils.isEmpty(standardMajors)){
                    String errMessage = String.format(DomainExceptionConstant.SUB_MAJOR_OF_STANDARD_NOT_EXISTS, treeTableVo.getParams().get(DomainFieldConstant.STANDARD_SUB_MAJOR_NAME));
                    throw new DataFormatException(errMessage);
                }
                treeTableVo.getParams().put(DomainFieldConstant.STANDARD_SUB_MAJOR_NAME, standardMajors.get(0).getId().toString());
            }

        });
    }

    private void setStandardCultivationTargetCommonFiled(List<StandardCultivationTarget> standardCultivationTargetList
            , TrainingSchemeVo trainingSchemeVo
            , StandardCultivationTarget parent){
        standardCultivationTargetList.parallelStream().forEach(standardCultivationTarget -> {
            standardCultivationTarget.setCollegeId(trainingSchemeVo.getCollegeId());
            standardCultivationTarget.setMajorId(trainingSchemeVo.getMajorId());
            standardCultivationTarget.setCategoryId(trainingSchemeVo.getCategoryId());
            standardCultivationTarget.setVersion(trainingSchemeVo.getVersion());
            standardCultivationTarget.setTrainingSchemeId(trainingSchemeVo.getId());
            if(ObjectUtils.isNotEmpty(parent)){
                standardCultivationTarget.setSubMajorId(parent.getSubMajorId());
                standardCultivationTarget.setClassId(parent.getClassId());
            }
            if(ObjectUtils.isEmpty(parent)){
                standardCultivationTarget.setRemark(standardCultivationTarget.getName());
                standardCultivationTarget.setName(trainingSchemeVo.getName()+"--"+"培养目标");
            }
            if(ObjectUtils.isNotEmpty(standardCultivationTarget.getChildren())){
                setStandardCultivationTargetCommonFiled((List<StandardCultivationTarget>)standardCultivationTarget.getChildren()
                        , trainingSchemeVo
                        , standardCultivationTarget);
            }
        });
    }

    /**
     * 毕业标准导入
     * @param file
     * @param collegeId
     */
    @Override
    @Transactional(rollbackFor = {Exception.class})
    public void importStandardGraduation(MultipartFile file, Long collegeId,Long categoryId,Long majorId,String version,String graduationType,Integer type,Long schemeId,String educationLevel) {
        List<TreeTableVo> treeTableVos = ExcelUtils.readTreeTable(file, 0, 1, 2);
//        setStandardMajor(treeTableVos, collegeId);
        List<StandardGraduation> standardGraduationList = treeTableVos.parallelStream().map(TreeTableVo::toStandardGraduation).collect(Collectors.toList());
        if (schemeId != null){
            stdService.deleteStandardGraduationBySchemeId(schemeId);
            TrainingSchemeVo trainingSchemeVo = trainingSchemeMapper.selectTrainingSchemeById(schemeId);
            if (StringUtils.isBlank(educationLevel) && trainingSchemeVo.getEducationLevel()!=null){
                educationLevel = trainingSchemeVo.getEducationLevel()+"";
            }
        }
        setStandardGraduationCommonFiled(standardGraduationList, collegeId, null,categoryId,majorId,version,graduationType,type,schemeId,educationLevel);
        stdService.insertStandardGraduationTree(standardGraduationList);
    }

    private void setStandardGraduationCommonFiled(List<StandardGraduation> standardGraduationList, Long collegeId, StandardGraduation parent,Long categoryId,Long majorId,String version,String graduationType,Integer type,Long schemeId,String educationLevel){
        standardGraduationList.parallelStream().forEach(standardGraduation -> {
            standardGraduation.setCollegeId(collegeId);
            standardGraduation.setMajorId(majorId);
            standardGraduation.setCategoryId(categoryId);
            standardGraduation.setVersion(version);
            //standardGraduation.setGraduationType(graduationType);
            standardGraduation.setType(type);
            UserUtils.reflash(standardGraduation);
            standardGraduation.setSchemeId(schemeId);
            standardGraduation.setEducationLevel(educationLevel);
            if(ObjectUtils.isNotEmpty(parent)){
                standardGraduation.setSubMajorId(parent.getSubMajorId());
                standardGraduation.setClassId(parent.getClassId());
            }
            if(ObjectUtils.isNotEmpty(standardGraduation.getChildren())){
                setStandardGraduationCommonFiled((List<StandardGraduation>)standardGraduation.getChildren(), collegeId, standardGraduation,categoryId,majorId,version,graduationType,type,schemeId,educationLevel);
            }
        });
    }

    /**
     * 培养标准导入
     * @param file
     * @param collegeId
     */
    @Override
    @Transactional(rollbackFor = {Exception.class})
    public void importStandardCultivation(MultipartFile file, Long collegeId) {
        List<TreeTableVo> treeTableVos = ExcelUtils.readTreeTable(file, 0, 1, 3, 5);
//        setStandardMajor(treeTableVos, collegeId);
        List<StandardCultivation> standardCultivationList = treeTableVos.parallelStream().map(TreeTableVo::toStandardCultivation).collect(Collectors.toList());
        setStandardCultivationCommonFiled(standardCultivationList, collegeId, null);
        stdService.insertStandardCultivationTree(standardCultivationList);
    }

    private void setStandardCultivationCommonFiled(List<StandardCultivation> standardCultivationList, Long collegeId, StandardCultivation parent){
        standardCultivationList.parallelStream().forEach(standardCultivation -> {
            standardCultivation.setCollegeId(collegeId);
            if(ObjectUtils.isNotEmpty(parent)){
                standardCultivation.setMajorId(parent.getMajorId());
                standardCultivation.setSubMajorId(parent.getSubMajorId());
                standardCultivation.setClassId(parent.getClassId());
            }
            if(ObjectUtils.isNotEmpty(standardCultivation.getChildren())){
                setStandardCultivationCommonFiled((List<StandardCultivation>)standardCultivation.getChildren(), collegeId, standardCultivation);
            }
        });
    }

    /**
     * 培养方案导入(含选课，排课)
     * @param file
     */
    @Override
    @Transactional(rollbackFor = {Exception.class})
    public void importTrainingScheme(MultipartFile file, Long collegeId) {
        List<TreeTableVo> treeTableVos = ExcelUtils.readTreeTable(file, 0, 5, 6);
        setStandardMajor(treeTableVos, collegeId);
        if(ObjectUtils.isEmpty(treeTableVos)){
            throw new FileException(DomainExceptionConstant.EXCEL_IS_EMPTY);
        }
        TreeTableVo trainingSchemeVo = treeTableVos.get(0);
        if(StringUtils.isBlank(trainingSchemeVo.getParams().get(DomainFieldConstant.TRAINING_SCHEME_TYPE_NAME))){
            String errMessage = String.format(DomainExceptionConstant.CATEGORY_OF_TRAINING_SCHEME_IS_EMPTY,
                    trainingSchemeVo.getParams().get(DomainFieldConstant.EXCEL_TRAINING_SCHEME_NAME));
            throw new DataFormatException(errMessage);
        }
        TrainingScheme _trainingScheme = new TrainingScheme();
        String schemeName = trainingSchemeVo.getParams().get(DomainFieldConstant.EXCEL_TRAINING_SCHEME_NAME);
        if(!schemeName.endsWith(DomainFieldConstant.TRAINING_SCHEME_END)){
            schemeName += DomainFieldConstant.TRAINING_SCHEME_END;
        }
        _trainingScheme.setName(schemeName);
        _trainingScheme.setCollegeId(collegeId);
        List<TrainingScheme> trainingSchemes = trainingSchemeMapper.selectTrainingSchemeListAccurate(_trainingScheme);
        TrainingScheme trainingScheme;
        if(StringUtils.isNotBlank(trainingSchemeVo.getParams().get(DomainFieldConstant.TRAINING_SCHEME_TYPE_NAME))){
            String schemeType = trainingSchemeVo.getParams().get(DomainFieldConstant.TRAINING_SCHEME_TYPE_NAME);
            TrainingSchemeCategory trainingSchemeCategory = new TrainingSchemeCategory();
            trainingSchemeCategory.setName(schemeType);
            trainingSchemeCategory.setLeaf(1);
            List<TrainingSchemeCategory> trainingSchemeCategories = trainingSchemeCategoryMapper.selectTrainingSchemeCategoryList(trainingSchemeCategory);
            if(ObjectUtils.isNotEmpty(trainingSchemeCategories)){
                _trainingScheme.setCategoryId(trainingSchemeCategories.get(0).getId());
            }else{
                String errMessage = String.format(DomainExceptionConstant.CATEGORY_OF_TRAINING_SCHEME_NOT_REF_CULTIVATION, schemeType);
                throw new DataFormatException(errMessage);
            }
        }
        if(ObjectUtils.isEmpty(trainingSchemes)){
            trainingScheme = new TrainingScheme();
            trainingScheme.setName(_trainingScheme.getName());
            String planName = _trainingScheme.getName().replace(DomainFieldConstant.TRAINING_SCHEME_END,DomainFieldConstant.TRAINING_PLAN_END);
            trainingScheme.setPlanName(planName);
            trainingScheme.setCategoryId(_trainingScheme.getCategoryId());
            trainingScheme.setCollegeId(collegeId);
            trainingScheme.setMajorId(Long.valueOf(trainingSchemeVo.getParams().get(DomainFieldConstant.STANDARD_MAJOR_NAME)));
            if(trainingSchemeVo.getParams().containsKey(DomainFieldConstant.STANDARD_SUB_MAJOR_NAME) &&
                    StringUtils.isNotBlank(trainingSchemeVo.getParams().get(DomainFieldConstant.STANDARD_SUB_MAJOR_NAME))) {
                trainingScheme.setSubMajorId(Long.valueOf(trainingSchemeVo.getParams().get(DomainFieldConstant.STANDARD_SUB_MAJOR_NAME)));
            }
            if(trainingSchemeVo.getParams().containsKey(DomainFieldConstant.STANDARD_CLASS_NAME)) {
                if(StringUtils.isNotBlank(trainingSchemeVo.getParams().get(DomainFieldConstant.STANDARD_CLASS_NAME))){
                    String className = trainingSchemeVo.getParams().get(DomainFieldConstant.STANDARD_CLASS_NAME);
                    if(!DomainFieldConstant.STANDARD_CLASS_MAP.containsKey(className)){
                        String errMessage = String.format(DomainExceptionConstant.CLASS_OF_STANDARD_NOT_EXISTS, className);
                        throw new DataFormatException(errMessage);
                    }
                    trainingScheme.setClassId(DomainFieldConstant.STANDARD_CLASS_MAP.get(className)+"");
                }else{
                    trainingScheme.setClassId(DomainFieldConstant.CLASS_NO_TYPE+"");
                }
            }
            trainingSchemeMapper.insertTrainingScheme(trainingScheme);
        }else {
            trainingScheme = trainingSchemes.get(0);
            if(ObjectUtils.isNotEmpty(_trainingScheme.getCategoryId())) {
                trainingScheme.setCategoryId(_trainingScheme.getCategoryId());
            }
            trainingScheme.setMajorId(Long.valueOf(trainingSchemeVo.getParams().get(DomainFieldConstant.STANDARD_MAJOR_NAME)));
            if(trainingSchemeVo.getParams().containsKey(DomainFieldConstant.STANDARD_SUB_MAJOR_NAME) &&
                    StringUtils.isNotBlank(trainingSchemeVo.getParams().get(DomainFieldConstant.STANDARD_SUB_MAJOR_NAME))) {
                trainingScheme.setSubMajorId(Long.valueOf(trainingSchemeVo.getParams().get(DomainFieldConstant.STANDARD_SUB_MAJOR_NAME)));
            }
            if(trainingSchemeVo.getParams().containsKey(DomainFieldConstant.STANDARD_CLASS_NAME)) {
                if(StringUtils.isNotBlank(trainingSchemeVo.getParams().get(DomainFieldConstant.STANDARD_CLASS_NAME))){
                    String className = trainingSchemeVo.getParams().get(DomainFieldConstant.STANDARD_CLASS_NAME);
                    if(!DomainFieldConstant.STANDARD_CLASS_MAP.containsKey(className)){
                        String errMessage = String.format(DomainExceptionConstant.CLASS_OF_STANDARD_NOT_EXISTS, className);
                        throw new DataFormatException(errMessage);
                    }
                    trainingScheme.setClassId(DomainFieldConstant.STANDARD_CLASS_MAP.get(className)+"");
                }else{
                    trainingScheme.setClassId(DomainFieldConstant.CLASS_NO_TYPE+"");
                }
            }
            trainingSchemeMapper.updateTrainingScheme(trainingSchemes.get(0));
        }
        if(ObjectUtils.isEmpty(trainingScheme.getCategoryId())){
            String errMessage = String.format(DomainExceptionConstant.CATEGORY_OF_TRAINING_SCHEME_IS_EMPTY, trainingScheme.getName());
            throw new DataFormatException(errMessage);
        }
        List<TreeTableVo> courseTypeList = trainingSchemeVo.getChildren();
        List<TrainingSchemeRefCourse> trainingSchemeRefCourseList = new ArrayList<>();
        List<TrainingSchemeCourseSchedule> trainingSchemeCourseScheduleList = new ArrayList<>();
        courseTypeList.forEach(vo -> {
            assembleRef(vo, trainingScheme, trainingSchemeRefCourseList, trainingSchemeCourseScheduleList);
        });
        trainingService.insertTrainingSchemeRefCourses(trainingScheme.getId(), trainingSchemeRefCourseList);
        if(ObjectUtils.isNotEmpty(trainingSchemeCourseScheduleList)) {
            trainingService.insertTrainingSchemeCourseSchedules(trainingScheme.getId(), trainingSchemeCourseScheduleList);
        }
    }

    private void assembleRef(TreeTableVo treeTableVo, TrainingScheme trainingScheme, List<TrainingSchemeRefCourse> trainingSchemeRefCourseList,
                             List<TrainingSchemeCourseSchedule> trainingSchemeCourseScheduleList){
        if(ObjectUtils.isEmpty(treeTableVo.getChildren())){
            return;
        }
        String courseTypeName = treeTableVo.getParams().get(DomainFieldConstant.EXCEL_COURSE_TYPE);
        Integer courseType = DomainFieldConstant.COURSE_TYPE_MAP.get(courseTypeName);
        if(ObjectUtils.isEmpty(courseType)){
            throw new FileException(DomainExceptionConstant.COURSE_TYPE_IS_WRONG);
        }
        List<String> courseNames = treeTableVo.getChildren().parallelStream().map(item -> {
            Map<String, String> params = item.getParams();
            String courseName = params.remove(DomainFieldConstant.EXCEL_COURSE_NAME);
            item.setName(courseName);
            return courseName;
        }).filter(StringUtils::isNotBlank).distinct().collect(Collectors.toList());
        List<Course> courseList;
        if(ObjectUtils.isEmpty(courseNames)){
            return;
        }else {
            courseList = courseMapper.selectCourseListByNameIn(courseNames);
        }
        Map<String, List<Course>> courseNameIdMap = courseList.parallelStream().collect(Collectors.groupingBy(Course::getName));
        //培养方案-课程关联对象
        trainingSchemeRefCourseList.addAll(treeTableVo.getChildren().parallelStream().map(TreeTableVo::getName).distinct().map(courseName -> {
            List<Course> _courseList = courseNameIdMap.get(courseName);
            if (ObjectUtils.isEmpty(_courseList)) {
                return null;
            }
            Course course = _courseList.get(0);
            TrainingSchemeRefCourse trainingSchemeRefCourse = new TrainingSchemeRefCourse();
            trainingSchemeRefCourse.setCourseId(course.getId());
            trainingSchemeRefCourse.setCourseTypeId(courseType);
            trainingSchemeRefCourse.setSchemeId(trainingScheme.getId());
            return trainingSchemeRefCourse;
        }).filter(ObjectUtils::isNotEmpty).collect(Collectors.toList()));
        //选课对象
        Map<String, List<TreeTableVo>> scheduleMap = treeTableVo.getChildren().parallelStream().collect(Collectors.groupingBy(TreeTableVo::getName));
        trainingSchemeCourseScheduleList.addAll(scheduleMap.keySet().parallelStream().flatMap(key -> {
            List<TreeTableVo> treeTableVos = scheduleMap.get(key);
            List<Course> _courseList = courseNameIdMap.get(key);
            if (ObjectUtils.isEmpty(_courseList)) {
                return null;
            }
            Course course = _courseList.get(0);
            Map<String, List<Map.Entry<String, String>>> termMap = treeTableVos.parallelStream().flatMap(treeTaleVo -> {
                return treeTaleVo.getParams().entrySet().stream();
            }).collect(Collectors.groupingBy(Map.Entry::getKey));
//            Double hours = course.getHours();
            return termMap.keySet().parallelStream().map(term -> {
                if(ObjectUtils.isEmpty(termMap.get(term)) || StringUtils.isBlank(termMap.get(term).get(0).getValue())){
                    return null;
                }
                Integer termType = DomainFieldConstant.TERM_NAME_NUMBER_MAP.get(term);
                if (ObjectUtils.isEmpty(termType)) {
                    return null;
                }
                TrainingSchemeCourseSchedule trainingSchemeCourseSchedule = new TrainingSchemeCourseSchedule();
                trainingSchemeCourseSchedule.setCourseId(course.getId());
                trainingSchemeCourseSchedule.setSchemeId(trainingScheme.getId());
                trainingSchemeCourseSchedule.setTerm(termType);
                trainingSchemeCourseSchedule.setType(course.getCourseModuleChildren());
                trainingSchemeCourseSchedule.setChecked(1);
                List<Map.Entry<String, String>> termEntries = termMap.get(term);
                Double _practiceHours = course.getPracticeHours();
                Double _theoryHours = course.getTheoryHours();
                Double _teachHours = course.getTeachHours();
                if(StringUtils.isNotBlank(termEntries.get(0).getValue())){
                    String[] _hours = termEntries.get(0).getValue().split(",");
                    if(_hours.length == 2){
                        try {
                            _practiceHours = Double.valueOf(_hours[1]);
                            _theoryHours = Double.valueOf(_hours[0]);
                            _teachHours = Double.valueOf(_hours[0]);
                        } catch (NumberFormatException e) {

                        }
                    }
                }
//                trainingSchemeCourseSchedule.setHours(_hours);
                trainingSchemeCourseSchedule.setPracticeHours(_practiceHours);
                trainingSchemeCourseSchedule.setTheoryHours(_theoryHours);
                trainingSchemeCourseSchedule.setTeachHours(_teachHours);
                return trainingSchemeCourseSchedule;
            }).filter(ObjectUtils::isNotEmpty);
        }).filter(ObjectUtils::isNotEmpty).collect(Collectors.toList()));
    }

    @Override
    @Transactional(rollbackFor = {Exception.class})
    public void importStandardGraduationRefCultivationTarget(MultipartFile file, Long collegeId,Long graduationId) {
        Map<String,Object> data = ExcelUtils.readGraduationRefCultivationTargetExcel(file);
        Map firstMap = (Map)data.get("first");
        String graName = (String)firstMap.get(DomainFieldConstant.STANDARD_GRADUATION);
        StandardGraduation query = new StandardGraduation();
        query.setParentId(DomainFieldConstant.ROOT_NODE_LONG_ID);
        query.setName(graName);
        List<StandardGraduation> gra = stdService.selectStandardGraduationList(query);
        if(CollectionUtils.isEmpty(gra)){
            throw new DataFormatException(DomainExceptionConstant.GRADUATION_NOT_EXISTS + ": " + graName);
        }
        StandardGraduation rootGra = gra.get(0);

        String targetName = (String)firstMap.get(DomainFieldConstant.STANDARD_CULTIVATION_TARGET);
        StandardCultivationTarget queryTarget = new StandardCultivationTarget();
        queryTarget.setParentId(DomainFieldConstant.ROOT_NODE_LONG_ID);
        queryTarget.setName(targetName);
        List<StandardCultivationTarget> target = stdService.selectStandardCultivationTargetList(queryTarget);
        if(CollectionUtils.isEmpty(target)){
            throw new DataFormatException(DomainExceptionConstant.CULTIVATION_TARGET_NOT_EXISTS + ": " + targetName);
        }
        StandardCultivationTarget rootTarget = target.get(0);
        rootGra.setCultivationTargetId(rootTarget.getId());
        standardGraduationMapper.updateStandardGraduation(rootGra);

        Map vertical = (Map)data.get("vertical");
        Map graMap = Maps.newHashMap();
        for(Object graObj:vertical.values()){
            String graStr = (String)graObj;
            if(StringUtils.isBlank(graStr)){
                continue;
            }
            StandardGraduation stdGra = standardGraduationMapper.selectStandardGraduationByRoot(rootGra.getId(),graStr);
            stdGra.setCultivationTargetId(rootTarget.getId());
            standardGraduationMapper.updateStandardGraduation(stdGra);
            graMap.put(graStr,stdGra.getId());
        }

        Map horizontal = (Map)data.get("horizontal");
        Map targetMap = Maps.newHashMap();
        for(Object targetObj:horizontal.values()){
            String targetStr = (String)targetObj;
            if(StringUtils.isBlank(targetStr)){
                continue;
            }
            StandardCultivationTarget stdTarget = standardCultivationTargetMapper.selectStdCultivationTargetByRoot(rootTarget.getId(),targetStr);
            targetMap.put(targetStr,stdTarget.getId());
        }
        List dataList = (List)data.get("dataList");
        List<StandardGraduationRefCultivationTarget> insertInfo = Lists.newArrayList();
        List<Long> graIds = Lists.newArrayList();
        dataList.stream().forEach(item ->{
            String itemStr = (String)item;
            String[] refs = itemStr.split(",");
            graIds.add((Long)graMap.get(refs[0]));
            StandardGraduationRefCultivationTarget grct = new StandardGraduationRefCultivationTarget();
            grct.setGraduationId((Long)graMap.get(refs[0]));
            grct.setCultivationTargetId((Long)targetMap.get(refs[1]));
            insertInfo.add(grct);
        });
        standardGraduationRefCultivationTargetMapper.deleteByGraduationIds(graIds);
        standardGraduationRefCultivationTargetMapper.insetList(insertInfo);

    }

    @Override
    @Transactional(rollbackFor = {Exception.class})
    public void importCultivationRefGraduation(MultipartFile file, Long collegeId,Long cultivationId) {
        Map<String,Object> data = ExcelUtils.readCultivationRefGraduationExcel(file);
        Map firstMap = (Map)data.get("first");
        String graName = (String)firstMap.get(DomainFieldConstant.STANDARD_GRADUATION);
        StandardGraduation query = new StandardGraduation();
        query.setParentId(DomainFieldConstant.ROOT_NODE_LONG_ID);
        query.setName(graName);
        List<StandardGraduation> gra = stdService.selectStandardGraduationList(query);
        if(CollectionUtils.isEmpty(gra)){
            throw new DataFormatException(DomainExceptionConstant.GRADUATION_NOT_EXISTS + ": " + graName);
        }
        StandardGraduation rootGra = gra.get(0);
        Map horizontal = (Map)data.get("horizontal");
        Map graMap = Maps.newHashMap();
        for(Object graObj:horizontal.values()){
            String graStr = (String)graObj;
            if(StringUtils.isBlank(graStr)){
                continue;
            }
            StandardGraduation stdGra = standardGraduationMapper.selectStandardGraduationByRoot(rootGra.getId(),graStr);
            graMap.put(graStr,stdGra.getId());
        }

        String culName = (String)firstMap.get(DomainFieldConstant.STANDARD_CULTIVATION);
        StandardCultivation queryCul = new StandardCultivation();
        queryCul.setParentId(DomainFieldConstant.ROOT_NODE_LONG_ID);
        queryCul.setName(culName);
        List<StandardCultivation> cul = stdService.selectStandardCultivationList(queryCul);
        if(CollectionUtils.isEmpty(cul)){
            throw new DataFormatException(DomainExceptionConstant.CULTIVATION_NOT_EXISTS + ": " + culName);
        }
        StandardCultivation rootCul = cul.get(0);
        rootCul.setGraduationId(rootGra.getId());
        standardCultivationMapper.updateStandardCultivation(rootCul);

        Map vertical = (Map)data.get("vertical");
        Map culMap = Maps.newHashMap();
        for(Object culObj:vertical.values()){
            String culStr = (String)culObj;
            if(StringUtils.isBlank(culStr)){
                continue;
            }
            StandardCultivation stdCul = standardCultivationMapper.selectStandardCultivationByRoot(rootCul.getId(),culStr);
            stdCul.setGraduationId(rootGra.getId());
            standardCultivationMapper.updateStandardCultivation(stdCul);
            culMap.put(culStr,stdCul.getId());
        }
        List dataList = (List)data.get("dataList");
        List<StandardCultivationRefGraduation> insertInfo = Lists.newArrayList();
        List<Long> culIds = Lists.newArrayList();
        dataList.stream().forEach(item ->{
            String itemStr = (String)item;
            String[] refs = itemStr.split(",");
            culIds.add((Long)culMap.get(refs[0]));
            StandardCultivationRefGraduation scrg = new StandardCultivationRefGraduation();
            scrg.setCultivationId((Long)culMap.get(refs[0]));
            scrg.setGraduationId((Long)graMap.get(refs[1]));
            insertInfo.add(scrg);
        });
        standardCultivationRefGraduationMapper.deleteByCultivationIdsIds(culIds);
        standardCultivationRefGraduationMapper.insetList(insertInfo);
    }

    @Override
    @Transactional(rollbackFor = {Exception.class})
    public void importTrainingSchemeRef(MultipartFile file, Long collegeId, Long schemeId) {
        if(ObjectUtils.isEmpty(schemeId)){
            throw new DataFormatException(DomainExceptionConstant.STANDRAD_ID_CAN_NOT_EMPTY);
        }
        List<Map<String, String>> data = ExcelUtils.readExcel(file);
        if(!data.get(0).containsKey(DomainFieldConstant.STANDARD_CULTIVATION_NAME)){
            throw new DataFormatException(DomainExceptionConstant.NAME_OF_STANDARD_CULTIVATION_CAN_NOT_EMPTY);
        }
        StandardCultivation tmpStandardCultivation = new StandardCultivation();
        tmpStandardCultivation.setName(data.get(0).get(DomainFieldConstant.STANDARD_CULTIVATION_NAME));
        List<StandardCultivation> standardCultivationList = stdService.selectStandardCultivationList(tmpStandardCultivation);
        for (int i = 0; i < data.size(); i++) {
            Map<String, String> currentRow = data.get(i);
            if(StringUtils.isBlank(currentRow.get(DomainFieldConstant.STANDARD_CULTIVATION_FIRST))){
                if(i == 0){
                    throw new DataFormatException(DomainExceptionConstant.EXCEL_FILE_FORMAT_WRONG);
                }
                currentRow.put(DomainFieldConstant.STANDARD_CULTIVATION_FIRST, data.get(i - 1).get(DomainFieldConstant.STANDARD_CULTIVATION_FIRST));
            }
            if(StringUtils.isBlank(currentRow.get(DomainFieldConstant.STANDARD_CULTIVATION_SECOND))){
                if(i == 0 && StringUtils.isNotBlank(currentRow.get(DomainFieldConstant.STANDARD_CULTIVATION_THIRD))){
                    throw new DataFormatException(DomainExceptionConstant.EXCEL_FILE_FORMAT_WRONG);
                }
                if(StringUtils.equals(currentRow.get(DomainFieldConstant.STANDARD_CULTIVATION_FIRST), data.get(i - 1).get(DomainFieldConstant.STANDARD_CULTIVATION_FIRST))) {
                    currentRow.put(DomainFieldConstant.STANDARD_CULTIVATION_SECOND, data.get(i - 1).get(DomainFieldConstant.STANDARD_CULTIVATION_SECOND));
                }
            }
        }

        TrainingSchemeVo trainingSchemeVo = trainingSchemeMapper.selectTrainingSchemeById(schemeId);
        Optional<StandardCultivation> tmpStc = standardCultivationList.parallelStream().filter(stdc -> StringUtils.equals(stdc.getName(), tmpStandardCultivation.getName())).findFirst();
        if(tmpStc.isEmpty()){
            String errMessage = String.format(DomainExceptionConstant.STANDARD_CULTIVATION_NOT_EXISTS, tmpStandardCultivation.getName());
            throw new DataFormatException(errMessage);
        }
        trainingSchemeVo.setStandardId(tmpStc.get().getId());
        List<TrainingSchemeRefCourse> trainingSchemeRefCourseList = trainingSchemeRefCourseMapper.selectTrainingSchemeRefCourseByTrainingSchemeVoId(schemeId);
        List<Long> courseIds = trainingSchemeRefCourseList.parallelStream().map(TrainingSchemeRefCourse::getCourseId).distinct().collect(Collectors.toList());
        if(ObjectUtils.isEmpty(courseIds)){
            return;
        }
        List<Course> courseList = courseMapper.selectCoursesByIds(courseIds);
        Map<String, List<Course>> courseMap = courseList.parallelStream().collect(Collectors.groupingBy(Course::getName));
        trainingSchemeMapper.updateTrainingScheme(new TrainingScheme(trainingSchemeVo));
        StandardCultivation standardCultivation = stdService.selectStandardCultivationTree(tmpStc.get().getId());
        StandardCultivationImportVo standardCultivationImportVo = transToVo(standardCultivation);
        List<KnowledgeUnitRefStdCultivation> knowledgeUnitRefStdCultivationList = data.stream().flatMap(map -> {
            if (StringUtils.isBlank(map.get(DomainFieldConstant.EXCEL_COURSE_NAME))) {
                return new ArrayList<KnowledgeUnitRefStdCultivation>().stream();
            }
            List<String> courseNames = Arrays.asList(map.get(DomainFieldConstant.EXCEL_COURSE_NAME).split(",|，|;|；"));
            List<Course> _courseList = courseNames.parallelStream().map(courseName -> {
                if(!courseMap.containsKey(courseName)){
                    return null;
                }
                return courseMap.get(courseName).get(0);
            }).filter(ObjectUtils::isNotEmpty).collect(Collectors.toList());
            if (ObjectUtils.isEmpty(courseList)) {
                return new ArrayList<KnowledgeUnitRefStdCultivation>().stream();
            }

            StandardCultivationImportVo _standardCultivation = null;
            String firstLevelStdName = map.get(DomainFieldConstant.STANDARD_CULTIVATION_FIRST);
            List<StandardCultivationImportVo> firstLevelStdList = standardCultivationImportVo.getChildren().get(firstLevelStdName);
            if(StringUtils.isNotBlank(map.get(DomainFieldConstant.STANDARD_CULTIVATION_SECOND))){
                List<StandardCultivationImportVo> firstLevelList = firstLevelStdList.stream().filter(secondVo -> secondVo.getLeaf().equals(0)).collect(Collectors.toList());
                f1:for (int i = 0; i < firstLevelList.size(); i++) {
                    StandardCultivationImportVo firstVo = firstLevelList.get(i);
                    List<StandardCultivationImportVo> secondLevelList = firstVo.getChildren().get(map.get(DomainFieldConstant.STANDARD_CULTIVATION_SECOND));

                    if(StringUtils.isNotBlank(map.get(DomainFieldConstant.STANDARD_CULTIVATION_THIRD))){
                        if(ObjectUtils.isEmpty(secondLevelList)){
                            String errMessage = String.format(DomainExceptionConstant.STANDARD_CULTIVATION_NOT_EXISTS, map.get(DomainFieldConstant.STANDARD_CULTIVATION_SECOND));
                            throw new DataFormatException(errMessage);
                        }
                        List<StandardCultivationImportVo> secondLevelVoList = secondLevelList.stream().filter(vo -> vo.getLeaf().equals(0)).collect(Collectors.toList());
                        for (int j = 0; j < secondLevelVoList.size(); j++) {
                            StandardCultivationImportVo secondVo = secondLevelVoList.get(j);
                            List<StandardCultivationImportVo> thirdLevelList = secondVo.getChildren().get(map.get(DomainFieldConstant.STANDARD_CULTIVATION_THIRD));
                            if(ObjectUtils.isEmpty(thirdLevelList)){
                                String errMessage = String.format(DomainExceptionConstant.STANDARD_CULTIVATION_NOT_EXISTS, map.get(DomainFieldConstant.STANDARD_CULTIVATION_THIRD));
                                throw new DataFormatException(errMessage);
                            }
                            Optional<StandardCultivationImportVo> third = thirdLevelList.stream().filter(vo -> vo.getLeaf().equals(1)).findFirst();
                            if(third.isPresent()){
                                _standardCultivation = third.get();
                                if(_standardCultivation.getLeaf() == 0){
                                    String errMessage = String.format(DomainExceptionConstant.REF_COUESE_STANDARD_CAN_NOT_REF, _standardCultivation.getName());
                                    throw new DataFormatException(errMessage);
                                }
                                break f1;
                            }
                        }

                    }else{
                        Optional<StandardCultivationImportVo> second = secondLevelList.stream().filter(vo -> vo.getLeaf().equals(1)).findFirst();
                        if(second.isPresent()){
                            _standardCultivation = second.get();
                            if(_standardCultivation.getLeaf() == 0){
                                String errMessage = String.format(DomainExceptionConstant.REF_COUESE_STANDARD_CAN_NOT_REF, _standardCultivation.getName());
                                throw new DataFormatException(errMessage);
                            }
                            break f1;
                        }
                    }
                }
            }else{
                Optional<StandardCultivationImportVo> first = firstLevelStdList.stream().filter(vo -> vo.getLeaf().equals(1)).findFirst();
                if(first.isPresent()){
                    _standardCultivation = first.get();
                }
            }
            if (ObjectUtils.isEmpty(_standardCultivation)) {
                String errMessage = String.format(DomainExceptionConstant.STANDARD_CULTIVATION_ERROR, 1);
                throw new DataFormatException(errMessage);
            }
            StandardCultivationImportVo std = _standardCultivation;
            List<Long> _courseIds = _courseList.parallelStream().map(Course::getId).collect(Collectors.toList());
            if(ObjectUtils.isEmpty(_courseIds)){
                return new ArrayList<KnowledgeUnitRefStdCultivation>().stream();
            }
            List<CourseRefKeUnit> courseRefKeUnits = courseRefKeUnitMapper.selectCourseRefKeUnitByCourseIds(_courseIds);
            return courseRefKeUnits.parallelStream().map(ref -> {
                KnowledgeUnitRefStdCultivation knowledgeUnitRefStdCultivation = new KnowledgeUnitRefStdCultivation();
                knowledgeUnitRefStdCultivation.setUnitId(ref.getUnitId());
                knowledgeUnitRefStdCultivation.setCourseId(ref.getCourseId());
                knowledgeUnitRefStdCultivation.setCultivationId(std.getId());
                knowledgeUnitRefStdCultivation.setSchemeId(schemeId);
                return knowledgeUnitRefStdCultivation;
            });
        }).collect(Collectors.toList());
        if(ObjectUtils.isEmpty(knowledgeUnitRefStdCultivationList)){
            return;
        }
        knowledgeUnitRefStdCultivationMapper.deleteKnowledgeUnitRefStdCultivationBySchemeId(schemeId);
        knowledgeUnitRefStdCultivationMapper.insertKnowledgeUnitRefStdCultivations(knowledgeUnitRefStdCultivationList);
    }

    private StandardCultivationImportVo transToVo(StandardCultivation standardCultivation){
        StandardCultivationImportVo standardCultivationImportVo = new StandardCultivationImportVo();
        standardCultivationImportVo.setId(standardCultivation.getId());
        standardCultivationImportVo.setName(standardCultivation.getName());
        standardCultivationImportVo.setLeaf(standardCultivation.getLeaf());
        if(ObjectUtils.isNotEmpty(standardCultivation.getChildren())){
            Map<String, List<StandardCultivationImportVo>> children = ((List<StandardCultivation>) (standardCultivation.getChildren())).parallelStream().map(child -> transToVo(child)).collect(Collectors.groupingBy(StandardCultivationImportVo::getName));
            standardCultivationImportVo.setChildren(children);
        }
        return standardCultivationImportVo;
    }

    @Override
    public void getTemplete(int type, HttpServletResponse response) {
        InputStream inputStream = null;
        ServletOutputStream outputStream = null;
        try
        {
            String xlsPath = "";
            String fileName = "";
            if(type == 0 ){
                xlsPath = "template/courseImport.xlsx";
                fileName = "课程体系数据导入模板.xlsx";
            }
            if(type == 1){
                xlsPath = "template/cultivationTargetImport_new2.xlsx";
                fileName = "培养目标数据导入模板.xlsx";
            }
            if(type == 2){
                xlsPath = "template/standardGraduationImport_new.xlsx";
                fileName = "毕业要求数据导入模板.xlsx";
            }
            if(type == 3){
                xlsPath = "template/standardCultivationImport.xls";
                fileName = "培养标准数据导入模板.xls";
            }
            if(type == 4){
                xlsPath = "template/trainingSchemeImport.xls";
                fileName = "培养规划数据（含排课）导入模板.xls";
            }
            if(type == 5){
                xlsPath = "template/courseRefSchemeImport.xls";
                fileName = "课程与标准映射数据导入模板.xls";
            }
            if(type == 6){
                xlsPath = "template/standardGraduationRefCultivationTargetImport.xls";
                fileName = "毕业标准与培养目标数据导入模板.xls";
            }
            if(type == 7){
                xlsPath = "template/cultivationRefGraduation.xls";
                fileName = "培养标准与毕业标准数据导入模板.xls";
            }
            if(type == 8){
                xlsPath = "template/abilityImport.xls";
                fileName = "能力数据导入模板.xls";
            }

            org.springframework.core.io.Resource resource = resourceLoader.getResource("classpath:"+xlsPath);
            response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
            response.addHeader("Cache-Control","no-cache,no-store,must-revalidate");
            response.addHeader("charset","utf-8");
            response.addHeader("Pragma","no-cache");
            String encodeName = URLEncoder.encode(fileName, StandardCharsets.UTF_8.toString());
            response.setHeader("Content-Disposition","attachment; filename=\""+encodeName+"\"; filename*=utf-8''"+encodeName);

            inputStream = resource.getInputStream();
            outputStream = response.getOutputStream();
            IOUtils.copy(inputStream,outputStream);
            response.flushBuffer();
        }catch (Exception e)
        {
            e.printStackTrace();
        }finally {
            try{
                if(inputStream != null){
                    inputStream.close();
                }
                if(outputStream != null){
                    outputStream.close();
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }

    }

}
