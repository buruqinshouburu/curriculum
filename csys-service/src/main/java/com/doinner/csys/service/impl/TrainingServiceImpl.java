package com.doinner.csys.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.deepoove.poi.data.RowRenderData;
import com.deepoove.poi.data.Rows;
import com.deepoove.poi.data.Tables;
import com.doinner.common.core.constant.SymbolConstants;
import com.doinner.common.core.domain.DataSet;
import com.doinner.common.core.domain.Message;
import com.doinner.common.core.utils.poi.ExcelUtil;
import com.doinner.csys.constant.CourseConstant;
import com.doinner.csys.constant.DomainFieldConstant;
import com.doinner.csys.dao.*;
import com.doinner.csys.domain.*;
import com.doinner.csys.domain.vo.*;
import com.doinner.csys.entity.csys.FiveYearTrainingPlanGenerator;
import com.doinner.csys.entity.csys.TrainingPlanGenerator;
import com.doinner.csys.entity.csys.model.DictContent;
import com.doinner.csys.entity.csys.model.TrainingPlanModel;
import com.doinner.csys.entity.csys.model.TrainingSchemeCourseModel;
import com.doinner.csys.entity.csys.po.SchemeRefMajor;
import com.doinner.csys.service.CommonService;
import com.doinner.csys.service.StandardService;
import com.doinner.csys.service.TrainingService;
import com.doinner.csys.utils.*;
import com.doinner.file.api.domain.FileInfo;
import com.doinner.file.api.domain.vo.FileInfoVO;
import com.doinner.file.api.service.RemoteFileInfoService;
import com.doinner.kg.domain.Dictionary;
import com.doinner.kg.service.RemoteKgService;
import com.doinner.system.domain.entity.SysDictData;
import com.doinner.system.service.DoinnerDictDataService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageInfo;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class TrainingServiceImpl implements TrainingService {
    private static final Logger log = LoggerFactory.getLogger(TrainingServiceImpl.class);
    @Resource
    private TrainingSchemeMapper trainingSchemeMapper;
    @Resource
    private TrainingSchemeRefCourseMapper trainingSchemeRefCourseMapper;
    @Resource
    private TrainingSchemeCategoryMapper trainingSchemeCategoryMapper;
    @Resource
    private TrainingSchemeCourseScheduleMapper trainingSchemeCourseScheduleMapper;
    @Resource
    private KnowledgeUnitRefStdCultivationMapper knowledgeUnitRefStdCultivationMapper;
    @Resource
    private DoinnerDictDataService doinnerDictDataService;
    @Resource
    private CourseRefKeUnitMapper courseRefKeUnitMapper;
    @Resource
    private StandardMajorMapper standardMajorMapper;

    @Resource
    private SchemeDetailMapper schemeDetailMapper;

    @Resource
    private CourseMapper courseMapper;
    @Resource
    private StandardGraduationMapper standardGraduationMapper;
    @Resource
    private TeachingProgrammeInstanceExtractMapper instanceExtractMapper;
    @Resource
    private CommonService commonService;
    @Value("${category.trainingPlan:69ba4b5da5db953a32ac0c62}")
    private String trainingPlanCategoryId;
    @Resource
    private RemoteFileInfoService remoteFileInfoService;
    @Resource
    private RemoteKgService remoteKgService;
    @Resource
    private StandardCultivationTargetMapper standardCultivationTargetMapper;
    @Resource
    private CourseTargetMapper courseTargetMapper;
    @Resource
    private CourseRefGraduationMapper courseRefGraduationMapper;
    @Resource
    private CourseKnowledgeUnitMapper courseKnowledgeUnitMapper;
    @Resource
    private SchemeRefMajorMapper schemeRefMajorMapper;
    @Resource
    private CourseRefKnowledgeUnitMapper courseRefKnowledgeUnitMapper;
    @Resource
    private StandardService stdService;



    @Override
    public List<TrainingSchemeCourseVo> selectTrainingSchemeCoursesById(Long id, Long subMajorId) {
        return trainingSchemeMapper.selectTrainingSchemeCoursesByIdAndsubMajorId(id,subMajorId);
    }

    @Override
    public TrainingSchemeVo selectTrainingSchemeById(Long id) {
        TrainingSchemeVo trainingSchemeVo = trainingSchemeMapper.selectTrainingSchemeById(id);
        List<SchemeDetail> schemeDetails = schemeDetailMapper.selectSchemeIdDetailList(id);
        trainingSchemeVo.setDetails(schemeDetails);
        return trainingSchemeVo;
    }

    @Override
    public List<TrainingSchemeVo> selectTrainingSchemeList(TrainingScheme trainingScheme) {
        // 排序方式归一化: ascending/descending -> asc/desc
        if (StringUtils.isNotBlank(trainingScheme.getOrder())) {
            if (CourseConstant.CUR_SORT_ASC.equals(trainingScheme.getOrder())) {
                trainingScheme.setOrder("asc");
            } else if (CourseConstant.CUR_SORT_DESC.equals(trainingScheme.getOrder())) {
                trainingScheme.setOrder("desc");
            }
        }
        List<TrainingSchemeVo> trainingSchemeVos = trainingSchemeMapper.selectTrainingSchemeVoList(trainingScheme);
        // PageHelper 只对第一条 SQL 生效，这里保留了分页总数，避免被后续查询覆盖丢失
        long total = new PageInfo<>(trainingSchemeVos).getTotal();
        //查询专业方向
        List<Long> schemeIds = trainingSchemeVos.stream().map(t -> t.getId()).collect(Collectors.toList());
        //查询培养方案中课程是否全部已经分配完成
        List<TrainingSchemeVo> resultList = new ArrayList<>();
        if(ObjectUtils.isNotEmpty(schemeIds)){
            resultList= trainingSchemeMapper.selectTrainingSchemeVoByIds(schemeIds,trainingScheme.getOrder(),trainingScheme.getDatabase_prop());
            List<HashMap<String,Object>> resultMap = courseMapper.selectCourseAcademicStatus(schemeIds);
            Map<Long, TrainingSchemeVo> collect = resultList.stream().collect(Collectors.toMap(TrainingSchemeVo::getId, t -> t));
            for (HashMap<String, Object> result : resultMap) {
                Long scheme_id = (Long) result.get("scheme_id");
                Long status = (Long) result.get("status");
                collect.get(scheme_id).setSchedulingStatus(status);
            }
        }
        // 用第一条查询的分页总数包装结果，保证 DataTable 的 total 为总记录数而非当前页条数
        Page<TrainingSchemeVo> page = new Page<>();
        page.setTotal(total);
        page.addAll(resultList);
        return page;
    }

    @Override
    public List<TrainingScheme> listTrainingSchemesLook(TrainingScheme trainingScheme) {
        List<TrainingScheme> list = trainingSchemeMapper.selectTrainingSchemeCategoryList(trainingScheme);
        for (TrainingScheme scheme : list) {
            if (StringUtils.isNotBlank(scheme.getProgramName())) {
                scheme.setName(scheme.getProgramName());
            }
        }
        return list;
    }

    @Override
    public List<TrainingScheme> selectTrainingSchemeCategoryList(TrainingScheme trainingScheme) {
        return trainingSchemeMapper.selectTrainingSchemeCategoryList(trainingScheme);
    }

    @Override
    public List<TrainingSchemeVo> selectTrainingSchemeVoCategoryList(TrainingScheme trainingScheme) {
        return trainingSchemeMapper.selectTrainingSchemeVoCategoryList(trainingScheme);
    }

    /**
     * 课程类型:1:公共基础必修课程
     * 2:学科基础必修课程
     * 3:专业必修课程
     * 4:公共基础选修课程
     * 5:学科基础、专业选修课程
     */
    @Override
    @Transactional(rollbackFor = {Exception.class})
    public DataSet insertTrainingScheme(TrainingSchemeVo trainingSchemeVo) {
        TrainingScheme trainingScheme = new TrainingScheme(trainingSchemeVo);
        UserUtils.reflash(trainingScheme);
        trainingSchemeMapper.insertTrainingScheme(trainingScheme);
        //保存专业
        saveSubMajor(trainingSchemeVo, trainingScheme);
        return DataSet.success(trainingScheme);
    }

    private void saveSubMajor(TrainingSchemeVo trainingSchemeVo, TrainingScheme trainingScheme) {
        if(ObjectUtils.isEmpty(trainingSchemeVo.getSubMajorIds())) {
            return;
        }
        ArrayList<SchemeRefMajor> schemeRefMajors = new ArrayList<>();
        for (Long subMajorId : trainingSchemeVo.getSubMajorIds()) {
            SchemeRefMajor schemeRefMajor = new SchemeRefMajor();
            schemeRefMajor.setMajorId(subMajorId);
            schemeRefMajor.setSchemeId(trainingScheme.getId());
            schemeRefMajors.add(schemeRefMajor);
        }
        schemeRefMajorMapper.insertBatch(schemeRefMajors);
    }

    private Boolean checkCourse(List<Long> courseIds,String type) {
        if(ObjectUtils.isEmpty(courseIds)){
            return true;
        }
        //判断课程是否全部有课程目标
        List<CourseTarget> courseTargets = courseTargetMapper.selectCourseTargetByCourseIds(courseIds);
        List<Long> hasTargetCourseIds = courseTargets.stream().map(t -> t.getCourseId()).distinct().collect(Collectors.toList());
        if(courseIds.size()!=hasTargetCourseIds.size()){
            return false;
        }
        List<Long> targetIds = courseTargets.stream().map(t -> t.getCourseTargetId()).collect(Collectors.toList());
        if(ObjectUtils.isEmpty(targetIds)){
            return false;
        }
        //判断课程目标是否完全绑定毕业要求
        List<CourseTarget> notBoundG = courseTargetMapper.selectTargetNotInGraduationByTargetIds(targetIds);
        if(ObjectUtils.isNotEmpty(notBoundG)&&notBoundG.size()!=0){
            //课程目标未完全绑定毕业要求
            return false;
        }
        if("1".equals(type)){
            //判断课程目标是否完全绑定知识体系
            List<CourseTarget> notBoundK = courseTargetMapper.selectTargetNotInKnowLedgeByTargetIds(targetIds);
            if(ObjectUtils.isNotEmpty(notBoundK)&&notBoundK.size()!=0){
                //课程目标未完全绑定知识体系
                return false;
            }
        }
        return true;
    }

    private List<TrainingSchemeCourseVo> translateCourse(List<Course> courses) {
        ArrayList<TrainingSchemeCourseVo> trainingSchemeCourseVos = new ArrayList<>();
        for (Course course : courses) {
            TrainingSchemeCourseVo trainingSchemeCourseVo = new TrainingSchemeCourseVo();
            BeanUtils.copyProperties(course, trainingSchemeCourseVo);
            trainingSchemeCourseVos.add(trainingSchemeCourseVo);
        }
        return trainingSchemeCourseVos;
    }

    public void setCourseSchedule(TrainingSchemeVo trainingSchemeVo, List<Course> courses) {
        int[][] termArr = new int[][]{{1, 2}, {3, 4}, {5, 6}, {7, 8},{9,10}};
        ArrayList<TrainingSchemeCourseSchedule> trainingSchemeCourseSchedules = new ArrayList<>();
        //查询全部课程
       // List<Course> courses = courseMapper.selectCoursesByIds(trainingSchemeVo.getCourseVos().stream().map(c -> c.getId()).collect(Collectors.toList()));
//        Map<Long, String> CourseVoMap = trainingSchemeVo.getCourseVos().stream().filter(c->ObjectUtils.isNotEmpty(c.getCourseAttr())).collect(Collectors.toMap(c -> c.getId(), c -> c.getCourseAttr()));
        if (ObjectUtils.isNotEmpty(courses)) {
            courses.forEach(c -> {
                // todo c.getCourseScheduleList()  应该从关联表里拿数据
                if (ObjectUtils.isNotEmpty(c.getSemesterSchedule()) && ObjectUtils.isNotEmpty(c.getSpringAutumn())) {
                    if("6".equals(c.getSemesterSchedule())){
                        for (int i = 1; i < 5; i++) {
                            createCourseSchedule(trainingSchemeVo, c, termArr, trainingSchemeCourseSchedules,i);
                        }
                    }else if("7".equals(c.getSemesterSchedule())){
                        createCourseSchedule(trainingSchemeVo, c, termArr, trainingSchemeCourseSchedules,1);
                    }else{
                        createCourseSchedule(trainingSchemeVo, c, termArr, trainingSchemeCourseSchedules,Integer.valueOf(c.getSemesterSchedule()));
                    }
                }
            });
            trainingSchemeCourseScheduleMapper.insertTrainingSchemeCourseSchedules(trainingSchemeCourseSchedules);
        }
    }

    @Override
    public List<TrainingSchemeCourseVo> viewTrainingCourseGraduation(Long schemeId, String type) {
        //根据专业类与版本查询课程
        List<TrainingSchemeRefCourse> trainingSchemeRefCourses = trainingSchemeRefCourseMapper.selectTrainingSchemeRefCourseByTrainingIdAndType(schemeId,type);
        //查询课程关联的毕业要求
        if(ObjectUtils.isEmpty(trainingSchemeRefCourses)){
            return null;
        }
        List<TrainingSchemeCourseVo> trainingSchemeCourseVos = courseMapper.selectCourseGraduationByCourseIds(trainingSchemeRefCourses.stream().map(c->c.getCourseId()).collect(Collectors.toList()),type);
        for (TrainingSchemeCourseVo trainingSchemeCourseVo : trainingSchemeCourseVos) {
            trainingSchemeCourseVo.setChildren(trainingSchemeCourseVo.getGraduationVoList());
        }
        return trainingSchemeCourseVos;
    }

    @Override
    @Transactional
    public void updateSchedules(Long schemeId) {
        //查询培养方案
        TrainingSchemeVo trainingSchemeVo = trainingSchemeMapper.selectTrainingSchemeById(schemeId);
        UserUtils.checkDataPermission(trainingSchemeVo);
        //根据专业类与版本查询课程
        List<TrainingSchemeRefCourse> trainingSchemeRefCourses = trainingSchemeRefCourseMapper.selectTrainingSchemeRefCourseByTrainingSchemeVoId(schemeId);
        List<Long> courseIds = trainingSchemeRefCourses.stream().map(c -> c.getCourseId()).collect(Collectors.toList());
        List<Course> courseList = courseMapper.selectCoursesByIds(courseIds);
        //清空排课信息
        if(ObjectUtils.isNotEmpty(courseIds)){
            trainingSchemeCourseScheduleMapper.deleteByCourseIds(courseIds);
        }
        //排课
        setCourseSchedule(trainingSchemeVo,courseList);
    }

    @Override
    @Transactional
    public void updateCourseSchedule(Long courseId, Long schemeId) {
        TrainingSchemeVo trainingSchemeVo = trainingSchemeMapper.selectTrainingSchemeById(schemeId);
        List<Long> courseIds = new ArrayList<>();
        courseIds.add(courseId);
        List<Course> courseList = courseMapper.selectCoursesByIds(courseIds);
        //清空排课信息
        if(ObjectUtils.isNotEmpty(courseIds)){
            trainingSchemeCourseScheduleMapper.deleteByCourseIds(courseIds);
        }
        //排课
        setCourseSchedule(trainingSchemeVo,courseList);
    }

    private static void createCourseSchedule(TrainingSchemeVo trainingSchemeVo,
                                             Course c,
                                             int[][] termArr,
                                             ArrayList<TrainingSchemeCourseSchedule> trainingSchemeCourseSchedules,
                                             int semesterSchedule) {
        // springAutumn: 1=秋 2=春 3=寒假 4=暑假 5=两个学期都排课
        // 排课时 1、3 归第一学期(秋)，2、4 归第二学期(春)，5 则两个学期各排一条
        String springAutumn = c.getSpringAutumn();
        List<Integer> termIndexList;
        if ("2".equals(springAutumn) || "4".equals(springAutumn)) {
            termIndexList = Collections.singletonList(2);
        } else if ("5".equals(springAutumn)) {
            termIndexList = Arrays.asList(1, 2);
        } else {
            // 1、3 以及其它未知值默认排第一学期，保持原有行为
            termIndexList = Collections.singletonList(1);
        }
        for (int autumn : termIndexList) {
            TrainingSchemeCourseSchedule tss = new TrainingSchemeCourseSchedule();
            tss.setSchemeId(trainingSchemeVo.getId());
            tss.setCourseId(c.getId());
            tss.setType(c.getCourseModuleChildren());
            tss.setHours(c.getHours());
            tss.setPracticeHours(c.getPracticeHours());
            tss.setTeachHours(c.getTeachHours());
            tss.setChecked(1);
            tss.setCourseAttr(c.getCourseAttr());
            tss.setCredits(c.getCredit());
            try {
                tss.setTerm(termArr[semesterSchedule - 1][autumn - 1]);
            } catch (Exception e) {
            }
            UserUtils.reflash(tss);
            trainingSchemeCourseSchedules.add(tss);
        }
    }


    private List<TrainingSchemeRefCourse> insertTrainingSchemeRefCourses(TrainingSchemeVo trainingSchemeVo) {
        List<TrainingSchemeRefCourse> trainingSchemeRefCourses = new ArrayList<>();

        trainingSchemeVo.getCourseVos().forEach(c -> {
            TrainingSchemeRefCourse trainingSchemeRefCourse = new TrainingSchemeRefCourse();
            trainingSchemeRefCourse.setSchemeId(trainingSchemeVo.getId());
            trainingSchemeRefCourse.setCourseId(c.getId());
            trainingSchemeRefCourses.add(trainingSchemeRefCourse);
        });

//        List<TrainingSchemeCourseVo> compulsoryCourseList = trainingSchemeVo.getCompulsoryCourseList(); // 必修课程
//        setSchemeCourse(trainingSchemeRefCourses, compulsoryCourseList, ConstantTrainingScheme.COMPULSORY_COURSE, ConstantTrainingScheme.COURSE_TYPE, trainingSchemeVo.getId());
//        trainingSchemeVo.setCourseVos(compulsoryCourseList);
//
//        List<TrainingSchemeCourseVo> electiveCourseList = trainingSchemeVo.getElectiveCourseList();     // 选修课程
//        setSchemeCourse(trainingSchemeRefCourses, electiveCourseList, ConstantTrainingScheme.ELECTIVE_COURSE, ConstantTrainingScheme.COURSE_TYPE, trainingSchemeVo.getId());
//        trainingSchemeVo.setCourseVos(electiveCourseList);
//
//        List<TrainingSchemeCourseVo> optionalCourseList = trainingSchemeVo.getOptionalCourseList();     // 任选课程
//        setSchemeCourse(trainingSchemeRefCourses, optionalCourseList, ConstantTrainingScheme.OPTIONAL_COURSE, ConstantTrainingScheme.COURSE_TYPE, trainingSchemeVo.getId());
//        trainingSchemeVo.setCourseVos(optionalCourseList);
//
//        List<TrainingSchemeCourseVo> compulsoryTrainingSubjectList = trainingSchemeVo.getCompulsoryTrainingSubjectList(); // 必修训练课目
//        setSchemeCourse(trainingSchemeRefCourses, compulsoryTrainingSubjectList, ConstantTrainingScheme.COMPULSORY_COURSE, ConstantTrainingScheme.TRAINING_TYPE, trainingSchemeVo.getId());
//        trainingSchemeVo.setCourseVos(compulsoryTrainingSubjectList);
//
//        List<TrainingSchemeCourseVo> electiveTrainingSubjectList = trainingSchemeVo.getElectiveTrainingSubjectList();     // 选修训练课目
//        setSchemeCourse(trainingSchemeRefCourses, electiveTrainingSubjectList, ConstantTrainingScheme.ELECTIVE_COURSE, ConstantTrainingScheme.TRAINING_TYPE, trainingSchemeVo.getId());
//        trainingSchemeVo.setCourseVos(electiveTrainingSubjectList);
//
//        List<TrainingSchemeCourseVo> optionalTrainingSubjectList = trainingSchemeVo.getOptionalTrainingSubjectList();     // 任选训练课目
//        setSchemeCourse(trainingSchemeRefCourses, optionalTrainingSubjectList, ConstantTrainingScheme.OPTIONAL_COURSE, ConstantTrainingScheme.TRAINING_TYPE, trainingSchemeVo.getId());
//        trainingSchemeVo.setCourseVos(optionalTrainingSubjectList);
//
//        List<TrainingSchemeCourseVo> compulsoryPracticalProjectList = trainingSchemeVo.getCompulsoryPracticalProjectList(); // 必修实践项目
//        setSchemeCourse(trainingSchemeRefCourses, compulsoryPracticalProjectList, ConstantTrainingScheme.COMPULSORY_COURSE, ConstantTrainingScheme.PRACTICAL_TYPE, trainingSchemeVo.getId());
//        trainingSchemeVo.setCourseVos(compulsoryPracticalProjectList);
//
//        List<TrainingSchemeCourseVo> electivePracticalProjectList = trainingSchemeVo.getElectivePracticalProjectList();     // 选修实践项目
//        setSchemeCourse(trainingSchemeRefCourses, electivePracticalProjectList, ConstantTrainingScheme.ELECTIVE_COURSE, ConstantTrainingScheme.PRACTICAL_TYPE, trainingSchemeVo.getId());
//        trainingSchemeVo.setCourseVos(electivePracticalProjectList);
//
//        List<TrainingSchemeCourseVo> optionalPracticalProjectList = trainingSchemeVo.getOptionalPracticalProjectList();     // 任选实践项目
//        setSchemeCourse(trainingSchemeRefCourses, optionalPracticalProjectList, ConstantTrainingScheme.OPTIONAL_COURSE, ConstantTrainingScheme.PRACTICAL_TYPE, trainingSchemeVo.getId());
//        trainingSchemeVo.setCourseVos(optionalPracticalProjectList);

        if (CollectionUtils.isNotEmpty(trainingSchemeRefCourses)) {
            trainingSchemeRefCourseMapper.insertTrainingSchemeRefCourses(trainingSchemeRefCourses);
        }
        return trainingSchemeRefCourses;
    }

    @Override
    @Transactional(rollbackFor = {Exception.class})
    public void insertTrainingSchemeRefCourses(Long trainingSchemeId, List<TrainingSchemeRefCourse> trainingSchemeRefCourseList) {
        if (ObjectUtils.isEmpty(trainingSchemeRefCourseList)) {
            return;
        }
        TrainingSchemeRefCourse trainingSchemeRefCourse = new TrainingSchemeRefCourse();
        trainingSchemeRefCourse.setSchemeId(trainingSchemeId);
        List<TrainingSchemeRefCourse> existsTrainingSchemeRefCourseList = trainingSchemeRefCourseMapper.selectTrainingSchemeRefCourseList(trainingSchemeRefCourse);
        List<String> existsTrainingSchemeRefCourseKeyList = existsTrainingSchemeRefCourseList.parallelStream().map(_trainingSchemeRefCourse -> {
            Long courseId = _trainingSchemeRefCourse.getCourseId();
            Integer courseTypeId = _trainingSchemeRefCourse.getCourseTypeId();
            return courseTypeId + SymbolConstants.COMMA + courseId;
        }).collect(Collectors.toList());
        List<TrainingSchemeRefCourse> unExistsTrainingSchemeRefCourseList = trainingSchemeRefCourseList.parallelStream()
                .filter(_trainingSchemeRefCourse -> !existsTrainingSchemeRefCourseKeyList.contains(
                        _trainingSchemeRefCourse.getCourseTypeId() + SymbolConstants.COMMA + _trainingSchemeRefCourse.getCourseId()))
                .collect(Collectors.toList());
        if (ObjectUtils.isNotEmpty(unExistsTrainingSchemeRefCourseList)) {
            trainingSchemeRefCourseMapper.insertTrainingSchemeRefCourses(unExistsTrainingSchemeRefCourseList);
        }
    }

    @Override
    @Transactional(rollbackFor = {Exception.class})
    public TrainingScheme updateTrainingScheme(TrainingSchemeVo trainingSchemeVo) {
        TrainingScheme trainingScheme = new TrainingScheme(trainingSchemeVo);
        UserUtils.reflash(trainingScheme);
        trainingSchemeMapper.updateTrainingScheme(trainingScheme);
        //删除包含专业
        schemeRefMajorMapper.deleteBySchemeId(trainingScheme.getId());
        //新增包含专业
        saveSubMajor(trainingSchemeVo,trainingScheme);
        return trainingScheme;
    }

    @Override
    @Transactional(rollbackFor = {Exception.class})
    public TrainingScheme updateTrainingScheme(TrainingScheme trainingScheme) {
        if (StringUtils.isNotBlank(trainingScheme.getName())) {
            String planName = trainingScheme.getName().replace(DomainFieldConstant.TRAINING_SCHEME_END, DomainFieldConstant.TRAINING_PLAN_END);
            trainingScheme.setPlanName(planName);
        }
        UserUtils.reflash(trainingScheme);
        trainingSchemeMapper.updateTrainingScheme(trainingScheme);
        schemeDetailMapper.deleteSchemeDetailBySchemeId(trainingScheme.getId());
        if (CollectionUtils.isNotEmpty(trainingScheme.getDetails())) {
            for (SchemeDetail detail : trainingScheme.getDetails()) {
                detail.setSchemeId(trainingScheme.getId());
                schemeDetailMapper.insertSchemeDetail(detail);
            }
        }
        return trainingScheme;
    }


    @Override
    @Transactional(rollbackFor = {Exception.class})
    public TrainingScheme createTrainingScheme(Long id) {
        TrainingSchemeVo schemeVo = trainingSchemeMapper.selectTrainingSchemeById(id);
        TrainingScheme scheme = new TrainingScheme(schemeVo);
        String name = scheme.getName();
        if (name.endsWith(DomainFieldConstant.TRAINING_SCHEME_END)) {
            scheme.setProgramName(name.replace(DomainFieldConstant.TRAINING_SCHEME_END, DomainFieldConstant.TRAINING_PROGRAM_END));
        } else {
            scheme.setProgramName(name + DomainFieldConstant.TRAINING_PROGRAM_END);
        }
        trainingSchemeMapper.updateTrainingScheme(scheme);
        return scheme;
    }

    @Override
    @Transactional(rollbackFor = {Exception.class})
    public int deleteTrainingSchemeById(Long id) {
        //查询培养方案
        TrainingSchemeVo trainingSchemeVo = trainingSchemeMapper.selectTrainingSchemeById(id);
        UserUtils.checkDataPermission(trainingSchemeVo);
        if(trainingSchemeVo.getStatus()==1||trainingSchemeVo.getStatus()==2){
            return 0;
        }
        //删除调用的课程
        List<TrainingSchemeRefCourse> trainingSchemeRefCourses = trainingSchemeRefCourseMapper.selectTrainingSchemeRefCourseByTrainingSchemeVoId(id);
        List<Long> courseIds = trainingSchemeRefCourses.stream().map(TrainingSchemeRefCourse::getCourseId).collect(Collectors.toList());
        //删除关联知识单元
        if(ObjectUtils.isNotEmpty(courseIds)){
            courseRefKnowledgeUnitMapper.deleteByCourseIds(courseIds);
            //删除课程关联培养方案以及课表
            trainingSchemeCourseScheduleMapper.deleteByCourseIds(courseIds);
            trainingSchemeRefCourseMapper.deleteTrainingSchemeRefCourseByCourseIds(courseIds);
            //删除课程
            courseMapper.removeCourseByIds(courseIds);
        }
        schemeDetailMapper.deleteSchemeDetailBySchemeId(id);
        //删除培养目标
        standardCultivationTargetMapper.deleteStandardCultivationTargetBySchemeId(id);
        //删除毕业标准
        standardGraduationMapper.deleteStandardGraduationBySchemeId(id);
        //删除专业
        schemeRefMajorMapper.deleteBySchemeId(id);
        return trainingSchemeMapper.deleteTrainingScheme(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Message deleteTrainingSchemeByIds( List<Long> ids) {
      if(ObjectUtils.isNotEmpty(ids)){
          for (Long id : ids) {
              TrainingSchemeVo trainingSchemeVo = trainingSchemeMapper.selectTrainingSchemeById(id);
              UserUtils.checkDataPermission(trainingSchemeVo);
              if(trainingSchemeVo.getStatus()==1||trainingSchemeVo.getStatus()==2){
                  return Message.error(trainingSchemeVo.getName()+"：正在审核中或已通过，无法删除");
              }
              deleteTrainingSchemeById(id);
          }
      }
      return Message.success();
    }

    @Override
    public TrainingSchemeVo viewTrainingScheme(Long id, Long type) {
        if(type==1){
            TrainingSchemeVo trainingSchemeVo = trainingSchemeMapper.selectTrainingSchemeCoursesAllById(id);
            setCourse(trainingSchemeVo);
            trainingSchemeVo.setChildren(trainingSchemeVo.getCourseVos());
            return trainingSchemeVo;
        }else if(type==2){
            TrainingSchemeVo trainingSchemeVo = trainingSchemeMapper.selectTrainingSchemeById(id);
            setGraduation(trainingSchemeVo);
            return trainingSchemeVo;
        }
        return null;
    }

    private void setGraduation(TrainingSchemeVo trainingSchemeVo) {
        //根据培养方案查找对应的毕业标准
        List<StandardGraduation> standardGraduations = standardGraduationMapper.selectStandardGraduationByMajorId(trainingSchemeVo);
        //查找毕业标准对应的课程
        List<CourseVo> courseVos = courseMapper.selectCourseByGraduationIds(standardGraduations.stream().map(s -> s.getId()).collect(Collectors.toList()));
        Map<Long, List<CourseVo>> courseMap = courseVos.stream().collect(Collectors.groupingBy(CourseVo::getGraduationId));
        //List<StandardGraduation> standardGraduationList = TreeBuilderUtils.buildRootTree(standardGraduations);
        ArrayList<TrainingSchemeStandardGraduationVo> standardGraduationsList = new ArrayList<>();
        for (StandardGraduation standardGraduation : standardGraduations) {
            List<CourseVo> courseVoList = courseMap.get(standardGraduation.getId());
            TrainingSchemeStandardGraduationVo tsg = new TrainingSchemeStandardGraduationVo();
            tsg.setId(standardGraduation.getId());
            tsg.setName(standardGraduation.getName());
            tsg.setParentId(standardGraduation.getParentId());
            if(ObjectUtils.isNotEmpty(courseVoList)&&standardGraduation.getLeaf()==1){
                ArrayList<TrainingSchemeCourseVo> courseList = new ArrayList<>();
                for (CourseVo courseVo : courseVoList) {
                    TrainingSchemeCourseVo trainingSchemeCourseVo = new TrainingSchemeCourseVo();
                    trainingSchemeCourseVo.setId(courseVo.getId());
                    trainingSchemeCourseVo.setName(courseVo.getName());
                    courseList.add(trainingSchemeCourseVo);
                }
                tsg.setChildren(courseList);
            }
            standardGraduationsList.add(tsg);
        }
        trainingSchemeVo.setChildren( TreeBuilderUtils.buildRootTree(standardGraduationsList));
    }

    private void setCourse(TrainingSchemeVo trainingSchemeVo) {
        List<TrainingSchemeCourseVo> courseVos = trainingSchemeVo.getCourseVos();
        courseVos.stream().forEach(courseVo -> {
            List<StandardGraduationVo> standardGraduationVos = courseVo.getGraduationVoList();
            courseVo.setChildren(standardGraduationVos);
//            standardGraduationVos.stream().forEach(graduationVo -> {
//                child.setCourseId(courseVo.getId());
//                List<TrainingSchemeStandardCultivationVo> children1 = child.getChildren();
//                children1.stream().forEach(child1 -> {
//                    if (type != null && 1 == type) {
//                        child1.setChildren(null);
//                    }
//                });
//            });
        });
       // Map<String, List<TrainingSchemeCourseVo>> groupedMap = courseVos.parallelStream().collect(Collectors.groupingBy(courseVo -> courseVo.getCourseAttr() + "," + courseVo.getType()));
        // 根据分组键分配到对应集合
//        trainingSchemeVo.setCompulsoryCourseList(groupedMap.get(ConstantTrainingScheme.COMPULSORY_COURSE + "," + ConstantTrainingScheme.COURSE_TYPE)); // 必修课程
//        trainingSchemeVo.setCompulsoryTrainingSubjectList(groupedMap.get(ConstantTrainingScheme.COMPULSORY_COURSE + "," + ConstantTrainingScheme.TRAINING_TYPE)); // 必修训练课目
//        trainingSchemeVo.setCompulsoryPracticalProjectList(groupedMap.get(ConstantTrainingScheme.COMPULSORY_COURSE + "," + ConstantTrainingScheme.PRACTICAL_TYPE)); // 必修实践项目
//        trainingSchemeVo.setElectiveCourseList(groupedMap.get(ConstantTrainingScheme.ELECTIVE_COURSE + "," + ConstantTrainingScheme.COURSE_TYPE)); // 选修课程
//        trainingSchemeVo.setElectiveTrainingSubjectList(groupedMap.get(ConstantTrainingScheme.ELECTIVE_COURSE + "," + ConstantTrainingScheme.TRAINING_TYPE)); // 选修训练课目
//        trainingSchemeVo.setElectivePracticalProjectList(groupedMap.get(ConstantTrainingScheme.ELECTIVE_COURSE + "," + ConstantTrainingScheme.PRACTICAL_TYPE)); // 选修实践项目
//        trainingSchemeVo.setOptionalCourseList(groupedMap.get(ConstantTrainingScheme.OPTIONAL_COURSE + "," + ConstantTrainingScheme.COURSE_TYPE)); // 任选课程
//        trainingSchemeVo.setOptionalTrainingSubjectList(groupedMap.get(ConstantTrainingScheme.OPTIONAL_COURSE + "," + ConstantTrainingScheme.TRAINING_TYPE)); // 任选训练课目
//        trainingSchemeVo.setOptionalPracticalProjectList(groupedMap.get(ConstantTrainingScheme.OPTIONAL_COURSE + "," + ConstantTrainingScheme.PRACTICAL_TYPE)); // 任选实践项目
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public TrainingSchemeCategory insertTrainingSchemeCategory(TrainingSchemeCategory trainingSchemeCategory) {
        UserUtils.reflash(trainingSchemeCategory);
        String url = "";
        if (trainingSchemeCategory.getParentId() != -1){
            TrainingSchemeCategory parent = trainingSchemeCategoryMapper.selectTrainingSchemeCategoryById(trainingSchemeCategory.getParentId());
            url+=parent.getUrl()+","+trainingSchemeCategory.getParentId();
            trainingSchemeCategory.setLevel(parent.getLevel()+1);
        }else {
            trainingSchemeCategory.setLevel(1);
            url = "-1";
        }
        trainingSchemeCategory.setUrl(url);
        if(trainingSchemeCategory.getLeaf() == null){
            trainingSchemeCategory.setLeaf(1);
        }
        trainingSchemeCategoryMapper.insertTrainingSchemeCategory(trainingSchemeCategory);
        return trainingSchemeCategory;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteTrainingSchemeCategoryById(Long id) {
        //查询下级门类
        List<TrainingSchemeCategory> categories= trainingSchemeCategoryMapper.selectAllTrainingSchemeCategoryById(id);
        List<Long> categoriesIds = categories.stream().map(c -> c.getId()).collect(Collectors.toList());
        return trainingSchemeCategoryMapper.deleteTrainingSchemeCategoryByIdList(categoriesIds);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteTrainingSchemeCategoryByIds(Long[] ids) {
        return trainingSchemeCategoryMapper.deleteTrainingSchemeCategoryList(ids);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public TrainingSchemeCategory updateTrainingSchemeCategory(TrainingSchemeCategory trainingSchemeCategory) {
        UserUtils.reflash(trainingSchemeCategory);
        trainingSchemeCategoryMapper.updateTrainingSchemeCategory(trainingSchemeCategory);
        return trainingSchemeCategory;
    }

    @Override
    public List<TrainingSchemeCategory> selectTrainingSchemeCategoryList(TrainingSchemeCategory trainingSchemeCategory) {
        return trainingSchemeCategoryMapper.selectTrainingSchemeCategoryList(trainingSchemeCategory);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public TrainingSchemeCourseSchedule insertTrainingSchemeCourseSchedule(TrainingSchemeCourseSchedule trainingSchemeCourseSchedule) {
        TrainingSchemeCourseSchedule trainingSchemeCourseSchedule1 = new TrainingSchemeCourseSchedule(trainingSchemeCourseSchedule);
        List<TrainingSchemeCourseScheduleVo> trainingSchemeCourseScheduleVos = trainingSchemeCourseScheduleMapper.selectTrainingSchemeCourseScheduleList(trainingSchemeCourseSchedule1);
        if (CollectionUtils.isEmpty(trainingSchemeCourseScheduleVos)) {
            return null;
        }
        UserUtils.reflash(trainingSchemeCourseSchedule);
        trainingSchemeCourseScheduleMapper.insertTrainingSchemeCourseSchedule(trainingSchemeCourseSchedule);
        return trainingSchemeCourseSchedule;
    }

    /**
     * 先删除原有的选课信息，再添加新的
     *
     * @param trainingSchemeId
     * @param trainingSchemeCourseScheduleList
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void insertTrainingSchemeCourseSchedules(Long trainingSchemeId, List<TrainingSchemeCourseSchedule> trainingSchemeCourseScheduleList) {
        trainingSchemeCourseScheduleMapper.deleteTrainingSchemeCourseScheduleByTrainingSchemeId(trainingSchemeId);
        for (TrainingSchemeCourseSchedule trainingSchemeCourseSchedule : trainingSchemeCourseScheduleList) {
            UserUtils.reflash(trainingSchemeCourseSchedule);
        }
        trainingSchemeCourseScheduleMapper.insertTrainingSchemeCourseSchedules(trainingSchemeCourseScheduleList);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertTrainingSchemeCourseSchedules(List<TrainingSchemeCourseSchedule> trainingSchemeCourseSchedules) {
        for (TrainingSchemeCourseSchedule trainingSchemeCourseSchedule : trainingSchemeCourseSchedules) {
            UserUtils.reflash(trainingSchemeCourseSchedule);
        }
        return trainingSchemeCourseScheduleMapper.insertTrainingSchemeCourseSchedules(trainingSchemeCourseSchedules);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteTrainingSchemeCourseScheduleById(Long id) {
        return trainingSchemeCourseScheduleMapper.deleteTrainingSchemeCourseScheduleById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public TrainingSchemeCourseSchedule updateTrainingSchemeCourseSchedule(TrainingSchemeCourseSchedule trainingSchemeCourseSchedule) {
        UserUtils.reflash(trainingSchemeCourseSchedule);
        trainingSchemeCourseScheduleMapper.updateTrainingSchemeCourseSchedule(trainingSchemeCourseSchedule);
        return trainingSchemeCourseSchedule;
    }

    @Override
    public TrainingSchemeCourseSchedule selectTrainingSchemeCourseScheduleById(Long id) {
        return trainingSchemeCourseScheduleMapper.selectTrainingSchemeCourseScheduleById(id);
    }

    @Override
    public List<TrainingSchemeCourseScheduleVo> selectTrainingSchemeCourseScheduleList(TrainingSchemeCourseSchedule trainingSchemeCourseSchedule) {
        List<TrainingSchemeCourseScheduleVo> trainingSchemeCourseScheduleVos = trainingSchemeCourseScheduleMapper.selectTrainingSchemeCourseScheduleList(trainingSchemeCourseSchedule);

        Map<Long, TrainingSchemeCourseScheduleVo> map = trainingSchemeCourseScheduleVos.stream().collect(Collectors.toMap(TrainingSchemeCourseScheduleVo::getId, trainingSchemeCourseScheduleVo -> trainingSchemeCourseScheduleVo));
        Map<Long, Long> scIdMap = trainingSchemeCourseScheduleVos.stream().collect(Collectors.toMap(TrainingSchemeCourseScheduleVo::getId, TrainingSchemeCourseScheduleVo::getCourseId));
        Map<Long, Long> sourcecIdMap = trainingSchemeCourseScheduleVos.stream().filter(t->ObjectUtils.isNotEmpty(t.getSourceId())).collect(Collectors.toMap(TrainingSchemeCourseScheduleVo::getId, TrainingSchemeCourseScheduleVo::getSourceId));
        Map<Long, String> beforeIdMap = trainingSchemeCourseScheduleVos.stream().filter(trainingSchemeCourseScheduleVo -> StringUtils.isNotBlank(trainingSchemeCourseScheduleVo.getBeforeCourseId()))
                .collect(Collectors.toMap(TrainingSchemeCourseScheduleVo::getId, TrainingSchemeCourseScheduleVo::getBeforeCourseId));

        for (Map.Entry<Long, String> entry : beforeIdMap.entrySet()) {
            Long id = entry.getKey();
            TrainingSchemeCourseScheduleVo trainingSchemeCourseScheduleVo = map.get(id);
            Integer term = trainingSchemeCourseScheduleVo.getTerm();
            List<Long> errorCourseIds = new ArrayList<>();
            String[] beforeIds = entry.getValue().split(",|，");
            for (String beforeId : beforeIds) {
                Long _beforeId = Long.valueOf(beforeId);
                List<Long> sIds = new ArrayList<>();
                for (Map.Entry<Long, Long> entry1 : scIdMap.entrySet()) {
                    if (_beforeId.equals(entry1.getValue())) {
                        sIds.add(entry1.getKey());
                    }
                }
                for (Map.Entry<Long, Long> entry1 : sourcecIdMap.entrySet()) {
                    if (_beforeId.equals(entry1.getValue())) {
                        sIds.add(entry1.getKey());
                    }
                }
                for (Long sId : sIds) {
                    if(ObjectUtils.isNotEmpty(map.get(sId).getTerm())){
                        Integer beforeTerm = map.get(sId).getTerm();
                        if (ObjectUtils.isNotEmpty(term)&&beforeTerm > term) {
                            errorCourseIds.add(sId);
                        }
                    }
                }
            }
            trainingSchemeCourseScheduleVo.setErrorCourseIds(errorCourseIds);
        }
        //查询总体学时与分配学期数（专为多学期排课）
        List<Long> courseIds = trainingSchemeCourseScheduleVos.stream().map(t -> t.getCourseId()).collect(Collectors.toList());
        if(ObjectUtils.isNotEmpty(courseIds)){
            List<HashMap<String,Object>> result=courseMapper.selectCourseSourceHoursByTargetCourseIds(courseIds);
            Map<Long, List<TrainingSchemeCourseScheduleVo>> courseScheduleMap = trainingSchemeCourseScheduleVos.stream().collect(Collectors.groupingBy(TrainingSchemeCourseScheduleVo::getCourseId));
            for (HashMap<String, Object> resultMap : result) {
                Long id = (Long) resultMap.get("id");
                Double practice_hours = (Double) resultMap.get("practice_hours");
                Double teach_hours = (Double) resultMap.get("teach_hours");
                Integer academic_terms_number = (Integer) resultMap.get("academic_terms_number");
                List<TrainingSchemeCourseScheduleVo> trainingSchemeCourseScheduleVoList = courseScheduleMap.get(id);
                for (TrainingSchemeCourseScheduleVo vo : trainingSchemeCourseScheduleVoList) {
                    vo.setPracticeHoursAll(practice_hours);
                    vo.setTeachHoursAll(teach_hours);
                    vo.setAcademicTermsNumber(academic_terms_number);
                }
            }
        }

        return trainingSchemeCourseScheduleVos;
    }

    @Override
    public List<CollegeVo> selectCollegeList(CollegeVo collegeVo) {
        return trainingSchemeMapper.selectCollegeList(collegeVo);
    }

    @Override
    public byte[] getWordByte(CourseVo courseVo, String courseType) {
        if (null == courseVo) {
            throw new RuntimeException("该课程没有教学计划！");
        }
        ExportData evaluation = WordUtil.creatExportData(WordUtil.readTemplatePath(courseType));
        setData(courseVo, evaluation);
        return evaluation.getByteArr();
    }

    private void setData(CourseVo courseVo, ExportData evaluation) {
        CourseTeachingTheoryPlanVo theory = courseVo.getCourseTeachingTheoryPlanVo();    // 理论课
        CourseTeachingPracticePlanVo practice = courseVo.getCourseTeachingPracticePlanVo();    // 实践课
        List<KnowledgeUnitVo> units = courseVo.getKnowledgeUnitVoList();    // 知识章节（单元）
        List<CourseTextbookVo> books = courseVo.getCourseTextbookVoList();    // 教材

        evaluation.setData("curName", courseVo.getName() != null ? courseVo.getName() : "");
        evaluation.setData("writingName", courseVo.getAuthors() != null ? courseVo.getAuthors() : "");
        evaluation.setData("reviewCollege", courseVo.getCollegeName() != null ? courseVo.getCollegeName() : "");
        evaluation.setData("curCode", courseVo.getCode() != null ? courseVo.getCode() : "");
        evaluation.setData("curNameEn", courseVo.getEnName() != null ? courseVo.getEnName() : "");
        evaluation.setDataStrings("curPreNames", courseVo.getBeforeCourseName() != null ? courseVo.getBeforeCourseName() : new ArrayList<>());
        evaluation.setDataStrings("curFollowNames", courseVo.getAfterCourseName() != null ? courseVo.getAfterCourseName() : new ArrayList<>());
        evaluation.setData("hours", courseVo.getHours() != null ? courseVo.getHours() : "");
        evaluation.setData("hoursTh", courseVo.getTheoryHours() != null ? courseVo.getTheoryHours() : "");
        evaluation.setData("hoursPa", courseVo.getPracticeHours() != null ? courseVo.getPracticeHours() : "");
        evaluation.setData("score", courseVo.getCredit() != null ? courseVo.getCredit() : "");
        evaluation.setData("nature", courseVo.getCourseProp() != null ? courseVo.getCourseProp() : "");
        evaluation.setData("term", courseVo.getOpenTerm() != null ? courseVo.getOpenTerm() : "");
        evaluation.setData("brief", courseVo.getSummary() != null ? courseVo.getSummary() : "");

        if (!"1".equals(courseVo.getType())) {
            if (null == practice) {
                practice = new CourseTeachingPracticePlanVo();
            }
            evaluation.setData("object", practice.getSuit() != null ? practice.getSuit() : "");
            evaluation.setData("teachCollege", courseVo.getTeachCollegeName() != null ? courseVo.getTeachCollegeName() : "");
            evaluation.setData("address", courseVo.getLocation() != null ? courseVo.getLocation() : "");
            evaluation.setData("backGround", practice.getTaskDescribe() != null ? practice.getTaskDescribe().replace("@", "\n    ") : "");
            evaluation.setData("praKnowledgeTarget", practice.getKnowLevel() != null ? practice.getKnowLevel().replace("@", "\n    ") : "");
            evaluation.setData("praAbilityTarget", practice.getAbilityLevel() != null ? practice.getAbilityLevel().replace("@", "\n    ") : "");
            evaluation.setData("praIdeoPoliTarget", practice.getPoliticsLevel() != null ? practice.getPoliticsLevel().replace("@", "\n    ") : "");
            evaluation.setData("content", practice.getContent() != null ? practice.getContent() : "");
            evaluation.setData("unitDesc", practice.getOverview() != null ? practice.getOverview() : "");
            evaluation.setData("praType", practice.getPracticeMethod() != null ? practice.getPracticeMethod() : "");
            evaluation.setData("timePlan", practice.getArrange() != null ? "/" : "");
            evaluation.setData("check", practice.getExaMethodName() != null ? practice.getExaMethodName() : "");
            evaluation.setData("judge", practice.getPerformance() != null ? practice.getPerformance() : "");
            evaluation.setData("standardJson", practice.getStandardJson() != null ? "/" : "");
            evaluation.setData("requireOf", practice.getAsk() != null ? practice.getAsk() : "");
        }
        if (!"2".equals(courseVo.getType())) {
            if (null == theory) {
                theory = new CourseTeachingTheoryPlanVo();
            }
            evaluation.setData("curNatureStatus", theory.getNatureLevel() != null ? theory.getNatureLevel() : "");
            evaluation.setData("curKnowledgeTarget", theory.getKnowLevel() != null ? theory.getKnowLevel().replace("@", "\n    ") : "");
            evaluation.setData("curAbilityTarget", theory.getAbilityLevel() != null ? theory.getAbilityLevel().replace("@", "\n    ") : "");
            evaluation.setData("curIdeoPoliTarget", theory.getPoliticsLevel() != null ? theory.getPoliticsLevel().replace("@", "\n    ") : "");
            evaluation.setData("teachMethod", theory.getTeachingMethod() != null ? theory.getTeachingMethod() : "");
            evaluation.setData("checkType", theory.getExaMethodName() != null ? theory.getExaMethodName() : "");
            evaluation.setData("examType", theory.getOrgMethod() != null ? theory.getOrgMethod() : "");
            evaluation.setData("achievement", theory.getPerformance() != null ? theory.getPerformance() : "");
            evaluation.setData("scoreStandard", theory.getStandard() != null ? theory.getStandard() : "");
            evaluation.setData("threeProject", "无");
            evaluation.setData("internship", "无");
            evaluation.setData("examLast", "无");
            evaluation.setData("elseOther", "无");
        }

        List<SoMap> unitList = new ArrayList<>();
        List<SoMap> unitTable = new ArrayList<>();
        for (KnowledgeUnitVo unit : units) {
            unitList.add(new SoMap(unit));
        }
        if (unitList.size() > 0) {
            evaluation.setData("list", unitList);
        } else {
            KnowledgeUnitVo unit = new KnowledgeUnitVo();
            unitList.add(new SoMap(unit));
            evaluation.setData("list", unitList);
        }
        if (unitTable.size() > 0) {
            evaluation.setTable("table", unitTable);
        } else {
            KnowledgeUnitVo unit = new KnowledgeUnitVo();
            unitTable.add(new SoMap(unit));
            evaluation.setTable("table", unitTable);
        }

        List<SoMap> textbooks = new ArrayList<>();
        List<SoMap> rebooks = new ArrayList<>();
        for (CourseTextbookVo book : books) {
            if (1 == book.getType()) {    // 教材
                textbooks.add(new SoMap(book));
            } else if (2 == book.getType()) {    // 参考书
                rebooks.add(new SoMap(book));
            } else {
                textbooks.add(new SoMap(book));
            }
        }
        if (textbooks.size() > 0) {
            evaluation.setData("textbook", textbooks);
        } else {
            CourseTextbookVo book = new CourseTextbookVo();
            textbooks.add(new SoMap(book));
            evaluation.setData("textbook", textbooks);
        }
        if (rebooks.size() > 0) {
            evaluation.setData("rebook", rebooks);
        } else {
            CourseTextbookVo book = new CourseTextbookVo();
            rebooks.add(new SoMap(book));
            evaluation.setData("rebook", rebooks);
        }
    }

    @Override
    public byte[] getWordBytes(List<CourseVo> courseVos) {
        ExportData evaluation = setExportData(courseVos);
        return evaluation.getByteArr();
    }

    // word文件表格替换占位符
    @Override
    public Map<String, Object> replacePlaceholder(List<CourseVo> courseVos) {
        Map<String, Object> data = new HashMap<>();
        for (CourseVo courseVo : courseVos) {
            CourseTeachingTheoryPlanVo theory = courseVo.getCourseTeachingTheoryPlanVo();
            if (ObjectUtils.isNotEmpty(theory)) {
                List<Table> chapterTables = theory.getChapterTables();
                setRowData(chapterTables, data, theory.getCourseId() + "theChapter");
                List<Table> theTables = theory.getTables();
                setRowData(theTables, data, theory.getCourseId() + "the");
            }
            CourseTeachingPracticePlanVo practice = courseVo.getCourseTeachingPracticePlanVo();
            if (ObjectUtils.isNotEmpty(practice)) {
                List<Table> praTables = practice.getTables();
                setRowData(praTables, data, practice.getCourseId() + "pra");
            }
        }
        return data;
    }

    private void setRowData(List<Table> tables, Map<String, Object> data, String prefix) {
        if (CollectionUtils.isNotEmpty(tables)) {
            for (int i = 0; i < tables.size(); i++) {
                boolean isTable = tables.get(i).isTable();
                String titleValue = tables.get(i).getTitleValue();
                data.put(prefix + "titleValue" + i, titleValue);
                if (isTable) {
                    Map<String, String[]> rows = tables.get(i).getRows();
                    RowRenderData[] rowRenderDatas = new RowRenderData[rows.size()];
                    List<RowRenderData> rowRenderDataList = Arrays.asList(rowRenderDatas);
                    for (int r = 0; r < rows.size(); r++) {
                        rowRenderDataList.set(r, Rows.of(rows.get("row" + r)).create());
                    }
                    data.put(prefix + "rows" + i, Tables.create(rowRenderDataList.toArray(new RowRenderData[rowRenderDataList.size()])));
                } else {
                    List<String> inputValue = tables.get(i).getInputValue();
                    String join = StringUtils.join(inputValue, ",");
                    data.put(prefix + "inputValue" + i, join);
                }
            }
        }
    }

    @Override
    public void process(OutputStream outputStream, List<CourseVo> courseVos) {
        ExportData evaluation = setExportData(courseVos);
        evaluation.process(outputStream);
    }

    private ExportData setExportData(List<CourseVo> courseVos) {
        ExportData evaluation = WordUtil.creatExportData(WordUtil.readTemplatesPath());
        List<Map<String, Object>> maps = new ArrayList<>();
        for (CourseVo courseVo : courseVos) {
            Map<String, Object> map = setTeachingPlan(courseVo);
            maps.add(map);
        }
        evaluation.setData("curs", maps);
        return evaluation;
    }

    private Map<String, Object> setTeachingPlan(CourseVo courseVo) {
        Map<String, Object> map = new HashMap<>();
        CourseTeachingTheoryPlanVo theory = courseVo.getCourseTeachingTheoryPlanVo();    // 理论课
        CourseTeachingPracticePlanVo practice = courseVo.getCourseTeachingPracticePlanVo();    // 实践课
//        List<CourseChapterVo> chapters = courseVo.getCourseChapterVoList();    // 章节
        List<CourseTextbookVo> books = courseVo.getCourseTextbookVoList();    // 教材

        map.put("curType", courseVo.getType() != null ? courseVo.getType() : 1);
        map.put("curName", courseVo.getName() != null ? courseVo.getName() : "");
        map.put("writingName", courseVo.getAuthors() != null ? courseVo.getAuthors() : "");
        map.put("reviewCollege", courseVo.getCollegeName() != null ? courseVo.getCollegeName() : "");
        map.put("curCode", courseVo.getCode() != null ? courseVo.getCode() : "");
        map.put("curNameEn", courseVo.getEnName() != null ? courseVo.getEnName() : "");
        map.put("curPreNames", courseVo.getBeforeCourseName() != null ? String.join(",", courseVo.getBeforeCourseName()) : "");
        map.put("curFollowNames", courseVo.getAfterCourseName() != null ? String.join(",", courseVo.getAfterCourseName()) : "");
        map.put("hours", courseVo.getHours() != null ? courseVo.getHours() : "");
        map.put("hoursTh", courseVo.getTheoryHours() != null ? courseVo.getTheoryHours() : "");
        map.put("hoursPa", courseVo.getPracticeHours() != null ? courseVo.getPracticeHours() : "");
        map.put("score", courseVo.getCredit() != null ? courseVo.getCredit() : "");
        map.put("nature", courseVo.getCoursePropName() != null ? courseVo.getCoursePropName() : "");
        map.put("term", courseVo.getOpenTerm() != null ? courseVo.getOpenTerm() : "");
        map.put("brief", courseVo.getSummary() != null ? courseVo.getSummary() : "");
        map.put("teachCollege", courseVo.getTeachCollegeName() != null ? courseVo.getTeachCollegeName() : "");
        map.put("address", courseVo.getLocation() != null ? courseVo.getLocation() : "");
        map.put("weekHours", courseVo.getWeekHours() != null ? courseVo.getWeekHours() : "");
        map.put("teachHours", courseVo.getTeachHours() != null ? courseVo.getTeachHours() : "");
        map.put("examineHours", courseVo.getExamineHours() != null ? courseVo.getExamineHours() : "");
        map.put("otherHours", courseVo.getOtherHours() != null ? courseVo.getOtherHours() : "");
        map.put("hoursUnit", courseVo.getHoursUnit() != null ? courseVo.getHoursUnit() : "");
        map.put("courseTypeName", courseVo.getCourseTypeName() != null ? courseVo.getCourseTypeName() : "");
        map.put("courseAttrName", courseVo.getCourseAttrName() != null ? courseVo.getCourseAttrName() : "");

        if (null == practice) {
            practice = new CourseTeachingPracticePlanVo();
        }
        map.put("praTableList", setPlaceholder(practice.getTables(), practice.getCourseId() + "pra"));
        map.put("arrangementList", setArrangements(practice.getArrangementVos()));
        if (!"1".equals(courseVo.getType())) {
            map.put("object", practice.getSuit() != null ? practice.getSuit() : "");
            map.put("backGround", practice.getTaskDescribe() != null ? practice.getTaskDescribe().replace("@", "\n    ") : "");
            map.put("praKnowledgeTarget", practice.getKnowLevel() != null ? practice.getKnowLevel().replace("@", "\n    ") : "");
            map.put("praAbilityTarget", practice.getAbilityLevel() != null ? practice.getAbilityLevel().replace("@", "\n    ") : "");
            map.put("praIdeoPoliTarget", practice.getPoliticsLevel() != null ? practice.getPoliticsLevel().replace("@", "\n    ") : "");
            map.put("content", practice.getContent() != null ? practice.getContent() : "");
            map.put("unitDesc", practice.getOverview() != null ? practice.getOverview() : "");
            map.put("praType", practice.getPracticeMethod() != null ? practice.getPracticeMethod() : "");
//            map.put("timePlan", practice.getArrange() != null ? "/" : "");
            map.put("check", practice.getExaMethodName() != null ? practice.getExaMethodName() : "");
            map.put("judge", practice.getPerformance() != null ? practice.getPerformance() : "");
//            map.put("praStandardJson", practice.getStandardJson() != null ? practice.getStandardJson() : "");
            map.put("praStandardJson", "{{#" + courseVo.getId() + "_" + practice.getId() + "}}");
            map.put("requireOf", practice.getAsk() != null ? practice.getAsk() : "");
            map.put("praExamType", practice.getOrgMethod() != null ? practice.getOrgMethod() : "");
            map.put("praScoreStandard", practice.getStandard() != null ? practice.getStandard() : "");
        }
        if (null == theory) {
            theory = new CourseTeachingTheoryPlanVo();
        }
        map.put("theChapterTableList", setPlaceholder(theory.getChapterTables(), theory.getCourseId() + "theChapter"));
        map.put("theTableList", setPlaceholder(theory.getTables(), theory.getCourseId() + "the"));
        if (!"2".equals(courseVo.getType())) {
            map.put("curNatureStatus", theory.getNatureLevel() != null ? theory.getNatureLevel() : "");
            map.put("curKnowledgeTarget", theory.getKnowLevel() != null ? theory.getKnowLevel().replace("@", "\n    ") : "");
            map.put("curAbilityTarget", theory.getAbilityLevel() != null ? theory.getAbilityLevel().replace("@", "\n    ") : "");
            map.put("curIdeoPoliTarget", theory.getPoliticsLevel() != null ? theory.getPoliticsLevel().replace("@", "\n    ") : "");
            map.put("teachMethod", theory.getTeachingMethod() != null ? theory.getTeachingMethod() : "");
            map.put("checkType", theory.getExaMethodName() != null ? theory.getExaMethodName() : "");
            map.put("examType", theory.getOrgMethod() != null ? theory.getOrgMethod() : "");
            map.put("achievement", theory.getPerformance() != null ? theory.getPerformance() : "");
            map.put("scoreStandard", theory.getStandard() != null ? theory.getStandard() : "");
            map.put("threeProject", "无");
            map.put("internship", "无");
            map.put("examLast", "无");
            map.put("elseOther", "无");
//            map.put("theStandardJson", theory.getStandardJson() != null ? theory.getStandardJson() : "");
//            map.put("theStandardJson", "{{#"+courseVo.getId()+"_"+theory.getId()+"}}");
        }

        List<SoMap> domainList = new ArrayList<>();    // 知识领域
        List<KnowledgeDomainVo> knowledgeDomainVoList = courseVo.getKnowledgeDomainVoList();    // 知识领域
        for (KnowledgeDomainVo knowledgeDomainVo : knowledgeDomainVoList) {
            SoMap domainMap = new SoMap(knowledgeDomainVo);
            List<KnowledgeUnitVo> knowledgeUnitVoList = knowledgeDomainVo.getKnowledgeUnitVoList();    // 知识单元
            List<SoMap> unitList = new ArrayList<>();    // 知识单元
            for (KnowledgeUnitVo knowledgeUnitVo : knowledgeUnitVoList) {
                SoMap unitMap = new SoMap(knowledgeUnitVo);
                List<KnowledgePointVo> knowledgePointVoList = knowledgeUnitVo.getKnowledgePointVoList();    // 知识点
                List<SoMap> pointList = new ArrayList<>();    // 知识点
                for (KnowledgePointVo knowledgePointVo : knowledgePointVoList) {
                    pointList.add(new SoMap(knowledgePointVo));
                }
                if (pointList.size() > 0) {
                    unitMap.put("pointList", pointList);
                } else {
                    KnowledgePointVo point = new KnowledgePointVo();
                    pointList.add(new SoMap(point));
                    unitMap.put("pointList", pointList);
                }
                unitList.add(unitMap);
            }
            if (unitList.size() > 0) {
                domainMap.put("unitList", unitList);
            } else {
                KnowledgeUnitVo unit = new KnowledgeUnitVo();
                List<SoMap> pointList = new ArrayList<>();    // 知识点
                KnowledgePointVo point = new KnowledgePointVo();
                SoMap pointMap = new SoMap(point);
                pointList.add(pointMap);
                SoMap unitMap = new SoMap(unit);
                unitMap.put("pointList", pointList);
                unitList.add(unitMap);
                domainMap.put("unitList", unitList);
            }
            domainList.add(domainMap);
        }
        if (domainList.size() > 0) {
            map.put("domainList", domainList);
        } else {
            KnowledgeDomainVo knowledgeDomainVo = new KnowledgeDomainVo();
            SoMap domainMap = new SoMap(knowledgeDomainVo);
            List<SoMap> unitList = new ArrayList<>();    // 知识单元
            KnowledgeUnitVo unit = new KnowledgeUnitVo();
            SoMap unitMap = new SoMap(unit);
            List<SoMap> pointList = new ArrayList<>();    // 知识点
            KnowledgePointVo point = new KnowledgePointVo();
            SoMap pointMap = new SoMap(point);
            pointList.add(pointMap);
            unitMap.put("pointList", pointList);
            unitList.add(unitMap);
            domainMap.put("unitList", unitList);
            domainList.add(domainMap);
            map.put("domainList", domainList);
        }

        // 章节表格
//        List<SoMap> chapterTable = new ArrayList<>();
//        for (CourseChapterVo chapter : chapters) {
//            chapterTable.add(new SoMap(chapter));
//        }
//        if (chapterTable.size() > 0) {
//            map.put("table", chapterTable);
//        } else {
//            CourseChapterVo chapter = new CourseChapterVo();
//            chapterTable.add(new SoMap(chapter));
//            map.put("table", chapterTable);
//        }

        List<SoMap> textbooks = new ArrayList<>();
        List<SoMap> rebooks = new ArrayList<>();
        for (CourseTextbookVo book : books) {
            if (1 == book.getType()) {    // 教材
                textbooks.add(new SoMap(book));
            } else if (2 == book.getType()) {    // 参考书
                rebooks.add(new SoMap(book));
            } else {
                textbooks.add(new SoMap(book));
            }
        }
        if (textbooks.size() > 0) {
            map.put("textbook", textbooks);
        } else {
            CourseTextbookVo book = new CourseTextbookVo();
            textbooks.add(new SoMap(book));
            map.put("textbook", textbooks);
        }
        if (rebooks.size() > 0) {
            map.put("rebook", rebooks);
        } else {
            CourseTextbookVo book = new CourseTextbookVo();
            rebooks.add(new SoMap(book));
            map.put("rebook", rebooks);
        }
        return map;
    }

    private List<SoMap> setArrangements(List<ArrangementVo> arrangementVos) {
        List<SoMap> arrangementList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(arrangementVos)) {
            for (ArrangementVo arrangementVo : arrangementVos) {
                SoMap soMap = new SoMap();
                soMap.put("timePlan", arrangementVo.getTimePlan() != null ? arrangementVo.getTimePlan() : "");
                soMap.put("addressPlan", arrangementVo.getAddressPlan() != null ? arrangementVo.getAddressPlan() + "\n    " : "\n    ");
                arrangementList.add(soMap);
            }
        } else {
            SoMap soMap = new SoMap();
            soMap.put("timePlan", "");
            soMap.put("addressPlan", "");
            arrangementList.add(soMap);
        }
        return arrangementList;
    }

    // word文件生成占位符
    private List<SoMap> setPlaceholder(List<Table> tables, String prefix/*,String suffix*/) {
        List<SoMap> tableList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(tables)) {
            for (int i = 0; i < tables.size(); i++) {
                boolean isTable = tables.get(i).isTable();
                SoMap soMap = new SoMap();
                soMap.put("titleValue", i + 1 + "、 {{" + prefix + "titleValue" + i + "}}");
                if (isTable) {
                    soMap.put("rows", "{{#" + prefix + "rows" + i + "}}");
                    soMap.put("inputValue", "");
                } else {
                    soMap.put("rows", "");
                    soMap.put("inputValue", "{{" + prefix + "inputValue" + i + "}}");
                }
                tableList.add(soMap);
            }
        } else {
            SoMap soMap = new SoMap();
            soMap.put("titleValue", "");
            soMap.put("rows", "");
            soMap.put("inputValue", "");
            tableList.add(soMap);
        }
        return tableList;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int planDispose(TowerToTower towerToTower) {
        List<TowerLayer> towerLayers = towerToTower.getRefIdsInfo();
        List<Long> courseIds = towerLayers.stream().map(TowerLayer::getCourseId).collect(Collectors.toList());
        List<Long> unitIds = towerLayers.stream().map(TowerLayer::getSourceId).collect(Collectors.toList());
        knowledgeUnitRefStdCultivationMapper.deleteKnowledgeUnitRefStdCultivationByCourseIdsAndUnitIds(courseIds, unitIds);

        List<KnowledgeUnitRefStdCultivation> knowledgeUnitRefStdCultivations = new ArrayList<>();
        for (TowerLayer towerLayer : towerLayers) {
            List<Long> targetIds = towerLayer.getTargetIds();
            for (Long targetId : targetIds) {
                KnowledgeUnitRefStdCultivation knowledgeUnitRefStdCultivation = new KnowledgeUnitRefStdCultivation();
                knowledgeUnitRefStdCultivation.setSchemeId(towerToTower.getSourceTopId());
                knowledgeUnitRefStdCultivation.setCourseId(towerLayer.getCourseId());
                knowledgeUnitRefStdCultivation.setUnitId(towerLayer.getSourceId());
                knowledgeUnitRefStdCultivation.setCultivationId(targetId);
                knowledgeUnitRefStdCultivations.add(knowledgeUnitRefStdCultivation);
            }
        }
        if (CollectionUtils.isNotEmpty(knowledgeUnitRefStdCultivations)) {
            return knowledgeUnitRefStdCultivationMapper.insertKnowledgeUnitRefStdCultivations(knowledgeUnitRefStdCultivations);
        }
        return 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int editCultivation(Long schemeId) {
        return knowledgeUnitRefStdCultivationMapper.deleteKnowledgeUnitRefStdCultivationBySchemeId(schemeId);
    }

    @Override
    public Long selectTargetId(Long id) {
        return trainingSchemeMapper.selectTargetId(id);
    }

    @Override
    public List<KnowledgeUnitRefStdCultivation> selectKnowledgeUnitRefStdCultivationsByKnowledgeUnitIds(List<Long> knowledgeUnitIds) {
        return knowledgeUnitRefStdCultivationMapper.selectKnowledgeUnitRefStdCultivationsByKnowledgeUnitIds(knowledgeUnitIds);
    }

    @Override
    public List<TrainingSchemeStandardCultivationVo> selectCultivation(Long schemeId, Long courseId, Long unitId) {
        return trainingSchemeMapper.selectCultivation(schemeId, courseId, unitId);
    }

    @Override
    public List<TrainingSchemeStandardCultivationVo> selectStandardCultivationVoAll(Long cultivationId) {
        List<TrainingSchemeStandardCultivationVo> trainingSchemeStandardCultivationVoList = new ArrayList<>();
        List<TrainingSchemeStandardCultivationVo> trainingSchemeStandardCultivationVos = trainingSchemeMapper.selectStandardCultivationVoAll(cultivationId);
        trainingSchemeStandardCultivationVos.stream().forEach(trainingSchemeStandardCultivationVo -> {
            trainingSchemeStandardCultivationVo.setChildren(new ArrayList<>());
            List<TrainingSchemeKnowledgeUnitVo> trainingSchemeKnowledgeUnitVos = trainingSchemeStandardCultivationVo.getTrainingSchemeKnowledgeUnitVos();
            trainingSchemeKnowledgeUnitVos.stream().forEach(trainingSchemeKnowledgeUnitVo -> {
                TrainingSchemeStandardCultivationVo trainingSchemeStandardCultivationVo1 = new TrainingSchemeStandardCultivationVo(trainingSchemeKnowledgeUnitVo);
                trainingSchemeStandardCultivationVo1.setParentId(trainingSchemeStandardCultivationVo.getId());
                trainingSchemeStandardCultivationVoList.add(trainingSchemeStandardCultivationVo1);
            });
            trainingSchemeStandardCultivationVo.setType(1);
        });
        trainingSchemeStandardCultivationVoList.addAll(trainingSchemeStandardCultivationVos);
        return trainingSchemeStandardCultivationVoList;
    }

    @Override
    public List<TrainingSchemeStandardCultivationTargetVo> selectStandardCultivationTargetVoAll(Long cultivationTargetId) {
        List<TrainingSchemeStandardCultivationTargetVo> trainingSchemeStandardCultivationTargetVoList = new ArrayList<>();
        List<TrainingSchemeStandardCultivationTargetVo> trainingSchemeStandardCultivationTargetVos = trainingSchemeMapper.selectStandardCultivationTargetVoAll(cultivationTargetId);
        trainingSchemeStandardCultivationTargetVos.stream().forEach(trainingSchemeStandardCultivationTargetVo -> {
            trainingSchemeStandardCultivationTargetVo.setSourceId(Long.valueOf(trainingSchemeStandardCultivationTargetVo.getId()));
//            trainingSchemeStandardCultivationTargetVo.setId(DomainFieldConstant.STANDARD_CULTIVATION_TARGET + trainingSchemeStandardCultivationTargetVo.getId());
//            trainingSchemeStandardCultivationTargetVo.setParentId(DomainFieldConstant.STANDARD_CULTIVATION_TARGET + trainingSchemeStandardCultivationTargetVo.getParentId());
            trainingSchemeStandardCultivationTargetVo.setType(5);
            // 毕业标准
            List<TrainingSchemeStandardCultivationTargetVo> graduationVos = new ArrayList<>();
            List<TrainingSchemeStandardGraduationVo> trainingSchemeStandardGraduationVos = trainingSchemeStandardCultivationTargetVo.getTrainingSchemeStandardGraduationVos();
            trainingSchemeStandardGraduationVos.stream().forEach(trainingSchemeStandardGraduationVo -> {
                TrainingSchemeStandardCultivationTargetVo trainingSchemeStandardCultivationTargetVo1 = new TrainingSchemeStandardCultivationTargetVo(trainingSchemeStandardGraduationVo);
                trainingSchemeStandardCultivationTargetVo1.setId(trainingSchemeStandardCultivationTargetVo.getId() + "_" + trainingSchemeStandardGraduationVo.getId());
                trainingSchemeStandardCultivationTargetVo1.setParentId(trainingSchemeStandardCultivationTargetVo.getId());
                graduationVos.add(trainingSchemeStandardCultivationTargetVo1);
                // 培养标准
                List<TrainingSchemeStandardCultivationTargetVo> cultivationVos = new ArrayList<>();
                List<TrainingSchemeStandardCultivationVo> trainingSchemeStandardCultivationVos = trainingSchemeStandardGraduationVo.getTrainingSchemeStandardCultivationVos();
                trainingSchemeStandardCultivationVos.stream().forEach(trainingSchemeStandardCultivationVo -> {
                    TrainingSchemeStandardCultivationTargetVo trainingSchemeStandardCultivationTargetVo2 = new TrainingSchemeStandardCultivationTargetVo(trainingSchemeStandardCultivationVo);
                    trainingSchemeStandardCultivationTargetVo2.setId(trainingSchemeStandardCultivationTargetVo1.getId() + "_" + trainingSchemeStandardCultivationVo.getId());
//                    trainingSchemeStandardCultivationTargetVo2.setParentId(DomainFieldConstant.STANDARD_GRADUATION + trainingSchemeStandardGraduationVo.getId().toString());
                    trainingSchemeStandardCultivationTargetVo2.setParentId(trainingSchemeStandardCultivationTargetVo1.getId());
                    cultivationVos.add(trainingSchemeStandardCultivationTargetVo2);
                    // 知识单元
                    List<TrainingSchemeStandardCultivationTargetVo> unitVos = new ArrayList<>();
                    List<TrainingSchemeKnowledgeUnitVo> trainingSchemeKnowledgeUnitVos = trainingSchemeStandardCultivationVo.getTrainingSchemeKnowledgeUnitVos();
                    trainingSchemeKnowledgeUnitVos.stream().forEach(trainingSchemeKnowledgeUnitVo -> {
                        TrainingSchemeStandardCultivationTargetVo trainingSchemeStandardCultivationTargetVo3 = new TrainingSchemeStandardCultivationTargetVo(trainingSchemeKnowledgeUnitVo);
                        trainingSchemeStandardCultivationTargetVo3.setId(trainingSchemeStandardCultivationTargetVo2.getId() + "_" + trainingSchemeKnowledgeUnitVo.getId());
//                        trainingSchemeStandardCultivationTargetVo3.setParentId(DomainFieldConstant.STANDARD_CULTIVATION + trainingSchemeStandardCultivationVo.getId().toString());
                        trainingSchemeStandardCultivationTargetVo3.setParentId(trainingSchemeStandardCultivationTargetVo2.getId());
                        unitVos.add(trainingSchemeStandardCultivationTargetVo3);
                    });
                    trainingSchemeStandardCultivationTargetVoList.addAll(unitVos);
                });
                trainingSchemeStandardCultivationTargetVoList.addAll(cultivationVos);
            });
            trainingSchemeStandardCultivationTargetVoList.addAll(graduationVos);
        });
        trainingSchemeStandardCultivationTargetVoList.addAll(trainingSchemeStandardCultivationTargetVos);
        return trainingSchemeStandardCultivationTargetVoList;
    }

    @Override
    public List<CourseVo> selectCourseVosByIds(List<Long> ids) {
        List<CourseVo> courseVos = trainingSchemeMapper.selectCourseAndRelevanceByIds(ids);
        Map<String, String> initLevels = getInitLevels();
        Map<String, String> requireLevels = getRequireLevels();
        Map<String, String> exaMethod = getExaMethod();
        Map<String, String> courseProp = getCourseProp();
        Map<String, String> courseType = getCourseType();
        Map<String, String> courseAttr = getCourseAttr();
        List<Long> courseIds = new ArrayList<>();
        courseVos.stream().forEach(courseVo -> {
            if (CollectionUtils.isNotEmpty(courseVo.getCourseTextbookVoList())) {
                List<CourseTextbookVo> courseTextbookVoList = courseVo.getCourseTextbookVoList();
                for (CourseTextbookVo courseTextbookVo : courseTextbookVoList) {
                    StringBuilder remark = new StringBuilder();
                    if (StringUtils.isNotBlank(courseTextbookVo.getName())) {
                        remark.append(courseTextbookVo.getName()).append(SymbolConstants.COMMA);
                    }
                    if (StringUtils.isNotBlank(courseTextbookVo.getAuthor())) {
                        remark.append(courseTextbookVo.getAuthor()).append(SymbolConstants.COMMA);
                    }
                    if (StringUtils.isNotBlank(courseTextbookVo.getPressName())) {
                        remark.append(courseTextbookVo.getPressName()).append(SymbolConstants.COMMA);
                    }
                    if (StringUtils.isNotBlank(courseTextbookVo.getPressVersion())) {
                        remark.append(courseTextbookVo.getPressVersion()).append(SymbolConstants.COMMA);
                    }
                    if (StringUtils.isNotBlank(courseTextbookVo.getPressTime())) {
                        remark.append(courseTextbookVo.getPressTime());
                    }
                    courseTextbookVo.setName(remark.toString());
                }
            }
            String beforeCourseId = courseVo.getBeforeCourseId();
            if (StringUtils.isNotBlank(beforeCourseId)) {
                String[] beforeIds = beforeCourseId.split(",|，");
                for (String beforeId : beforeIds) {
                    courseIds.add(Long.valueOf(beforeId));
                }
            }
            String afterCourseId = courseVo.getAfterCourseId();
            if (StringUtils.isNotBlank(afterCourseId)) {
                String[] afterIds = afterCourseId.split(",|，");
                for (String afterId : afterIds) {
                    courseIds.add(Long.valueOf(afterId));
                }
            }
            List<KnowledgeDomainVo> knowledgeDomainVoList = courseVo.getKnowledgeDomainVoList();
            knowledgeDomainVoList.stream().forEach(knowledgeDomainVo -> {
                List<KnowledgeUnitVo> knowledgeUnitVoList = knowledgeDomainVo.getKnowledgeUnitVoList();
                knowledgeUnitVoList.stream().forEach(knowledgeUnitVo -> {
                    if (ObjectUtils.isNotEmpty(knowledgeUnitVo)) {
                        knowledgeUnitVo.setInitLevelName(initLevels.get(String.valueOf(knowledgeUnitVo.getInitLevel())));
                        knowledgeUnitVo.setRequireLevelName(requireLevels.get(String.valueOf(knowledgeUnitVo.getRequireLevel())));
                    }
                });
            });

            CourseTeachingTheoryPlanVo courseTeachingTheoryPlanVo = courseVo.getCourseTeachingTheoryPlanVo();
            if (ObjectUtils.isNotEmpty(courseTeachingTheoryPlanVo)) {
                if (StringUtils.isNotBlank(courseTeachingTheoryPlanVo.getContentJson()) && !courseTeachingTheoryPlanVo.getContentJson().equals("null")) {
//                    List<CourseChapterVo> chapters = json2Chapter(courseTeachingTheoryPlanVo.getContentJson());
//                    courseVo.setCourseChapterVoList(chapters);
                    List<Table> tables = json2Table(courseTeachingTheoryPlanVo.getContentJson());
                    courseTeachingTheoryPlanVo.setChapterTables(tables);
                }
                if (StringUtils.isNotBlank(courseTeachingTheoryPlanVo.getStandardJson()) && !courseTeachingTheoryPlanVo.getStandardJson().equals("null")) {
                    List<Table> tables = json2Table(courseTeachingTheoryPlanVo.getStandardJson());
                    courseTeachingTheoryPlanVo.setTables(tables);
                }
                courseTeachingTheoryPlanVo.setExaMethodName(exaMethod.get(String.valueOf(courseTeachingTheoryPlanVo.getExaMethod())));
            }
            CourseTeachingPracticePlanVo courseTeachingPracticePlanVo = courseVo.getCourseTeachingPracticePlanVo();
            if (ObjectUtils.isNotEmpty(courseTeachingPracticePlanVo)) {
                if (StringUtils.isNotBlank(courseTeachingPracticePlanVo.getStandardJson()) && !courseTeachingPracticePlanVo.getStandardJson().equals("null")) {
                    List<Table> tables = json2Table(courseTeachingPracticePlanVo.getStandardJson());
                    courseTeachingPracticePlanVo.setTables(tables);
                }
                if (StringUtils.isNotBlank(courseTeachingPracticePlanVo.getArrange()) && !courseTeachingPracticePlanVo.getArrange().equals("null")) {
                    List<ArrangementVo> arrangements = new ArrayList<>();
                    try {
                        arrangements = json2Arrangement(courseTeachingPracticePlanVo.getArrange());
                    } catch (Exception e) {
//                    throw new RuntimeException("《"+courseVo.getName()+"》时间及地点安排数据格式有误！");
                    }
                    courseTeachingPracticePlanVo.setArrangementVos(arrangements);
                }
                courseTeachingPracticePlanVo.setExaMethodName(exaMethod.get(String.valueOf(courseTeachingPracticePlanVo.getExaMethod())));
            }
            if (ObjectUtils.isNotEmpty(courseVo.getCourseProp())) {
                courseVo.setCoursePropName(courseProp.get(String.valueOf(courseVo.getCourseProp())));
            }
            if (ObjectUtils.isNotEmpty(courseVo.getCourseType())) {
                courseVo.setCourseTypeName(courseType.get(String.valueOf(courseVo.getCourseType())));
            }
            if (ObjectUtils.isNotEmpty(courseVo.getCourseAttr())) {
                courseVo.setCourseAttrName(courseAttr.get(String.valueOf(courseVo.getCourseAttr())));
            }
        });

        if (CollectionUtils.isNotEmpty(courseIds)) {
            List<CourseVo> courseVos1 = trainingSchemeMapper.selectCourseByIds(courseIds);
            Map<String, CourseVo> courseVoMap = courseVos1.stream().collect(Collectors.toMap(courseVo -> courseVo.getId().toString(), Function.identity()));

            courseVos.stream().forEach(courseVo -> {
                List<String> beforeCourseNames = new ArrayList<>();
                String beforeCourseId = courseVo.getBeforeCourseId();
                if (StringUtils.isNotBlank(beforeCourseId)) {
                    String[] beforeIds = beforeCourseId.split(",|，");
                    for (String beforeId : beforeIds) {
                        beforeCourseNames.add(courseVoMap.get(beforeId).getName());
                    }
                    courseVo.setBeforeCourseName(beforeCourseNames);
                }

                List<String> afterCourseNames = new ArrayList<>();
                String afterCourseId = courseVo.getAfterCourseId();
                if (StringUtils.isNotBlank(afterCourseId)) {
                    String[] afterIds = afterCourseId.split(",|，");
                    for (String afterId : afterIds) {
                        afterCourseNames.add(courseVoMap.get(afterId).getName());
                    }
                    courseVo.setAfterCourseName(afterCourseNames);
                }
            });
        }
        return courseVos;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteTrainingSchemeIndex(Long id) {
        trainingSchemeMapper.deleteTrainingSchemeIndex(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public TrainingSchemeVo createTrainingPlanWord(Long schemeId) {
        //查询培养方案
        TrainingSchemeVo trainingSchemeVo = trainingSchemeMapper.selectTrainingSchemeById(schemeId);
        //根据专业查找对应的毕业要求
        StandardGraduation standardGraduation = new StandardGraduation();
        standardGraduation.setSchemeId(schemeId);
        List<StandardGraduation> standardGraduations = stdService.selectStdGraduationSchemeListBy(standardGraduation);
        //查找教学大纲中的学时学分
        List<TeachingProgrammeInstanceExtract> teachingProgrammeInstanceExtracts = instanceExtractMapper.selectTeachingProgrammeInstanceExtractByInstanceId(trainingSchemeVo.getInstanceId());
        //查找培养方案安排的课程
        List<TrainingSchemeCourseVo> trainingSchemeCourseVos = trainingSchemeMapper.selectTrainingSchemeCoursesById(schemeId);
        //查询课表
        TrainingSchemeCourseSchedule trainingSchemeCourseSchedule = new TrainingSchemeCourseSchedule();
        trainingSchemeCourseSchedule.setSchemeId(schemeId);
        List<TrainingSchemeCourseScheduleVo> trainingSchemeCourseScheduleVos = trainingSchemeCourseScheduleMapper.selectTrainingSchemeCourseScheduleList(trainingSchemeCourseSchedule);
        //查询课程全部信息
        List<Course> courses = new ArrayList<>();
        if (ObjectUtils.isNotEmpty(trainingSchemeCourseVos)) {
            courses.addAll(courseMapper.selectCoursesByIds(trainingSchemeCourseVos.stream().map(tc -> tc.getId()).collect(Collectors.toList())));
        }
        //根据专业查找培养目标
        List<StandardCultivationTarget> standardCultivationTargetList = new ArrayList<>();
        List<StandardCultivationTarget> standardCultivationTargets = standardCultivationTargetMapper.selectCultivationTargetBySchemeId(schemeId);
        //取第一个
        if (ObjectUtils.isNotEmpty(standardCultivationTargets)) {
            standardCultivationTargetList.addAll(standardCultivationTargetMapper.selectStdCultivationTargetAll(standardCultivationTargets.get(0).getId()));
        }
        //更换适用对象（培养层次）名称
        SysDictData dictData = CurDictUtils.getDictData("sys_education_level", trainingSchemeVo.getEducationLevel());
        trainingSchemeVo.setEducationLevelName(dictData.getDictLabel());
        //创建培养方案导出模型类
        TrainingPlanModel trainingPlanModel = new TrainingPlanModel(standardGraduations,
                standardCultivationTargetList,
                teachingProgrammeInstanceExtracts,
                courses,
                trainingSchemeCourseScheduleVos,
                trainingSchemeVo);
        //模型类中字典表转换
        translateDict(trainingPlanModel);
        //完善学时学分
        trainingPlanModel.setDurationAndCredits();
        // 创建生成器：适用对象（培养层次）为 7/8 时启用第五学年，使用五年制子类
        TrainingPlanGenerator generator = createTrainingPlanGenerator(trainingSchemeVo.getEducationLevel());
        //测试
//        try {
//            generator.generate(trainingPlanModel,"D:\\download\\output.docx");
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
        try {
            if (ObjectUtils.isNotEmpty(trainingSchemeVo.getFileId())) {
                //查找文件
                FileInfo fileInfo = remoteFileInfoService.getFileInfo(trainingSchemeVo.getFileId()).getData();
                if (ObjectUtils.isNotEmpty(fileInfo)) {
                    //已有文件先删除
                    Message data = remoteFileInfoService.delete(fileInfo.getId().toString());
                    System.out.println(data);
                }
            }
            InputStream inputStream = generator.generate(trainingPlanModel);
            String fileId = commonService.uploadFile(inputStream, trainingSchemeVo.getName() + ".docx", trainingPlanCategoryId);
            //更新instance
            trainingSchemeVo.setFileId(fileId);
            setUrl(trainingSchemeVo);
            trainingSchemeMapper.updateTrainingSchemeFileId(trainingSchemeVo);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return trainingSchemeVo;
    }

    /**
     * 根据培养方案适用对象（培养层次 educationLevel）选择培养方案生成器：
     * 值为 7（英烈子女班）或 8（生长军官学员及英烈子女班）时使用五年制生成器（启用第五学年），否则使用四年制生成器。
     */
    private TrainingPlanGenerator createTrainingPlanGenerator(String educationLevel) {
        if ("7".equals(educationLevel) || "8".equals(educationLevel)) {
            return new FiveYearTrainingPlanGenerator();
        }
        return new TrainingPlanGenerator();
    }

    @Override
    public void exportTrainingCourse(HttpServletResponse response, Long schemeId, Long subMajorId) {
        try {
            ArrayList<TrainingCourseExportVo> exportCourseList = new ArrayList<>();
            //查询专业方向
            StandardMajor subMajor = standardMajorMapper.selectStandardMajorById(subMajorId);
            //查询关联课程
            List<TrainingSchemeCourseVo> trainingSchemeCourseVos = trainingSchemeMapper.selectTrainingSchemeCoursesById(schemeId);
            //查询课程全部信息
            if(ObjectUtils.isNotEmpty(trainingSchemeCourseVos)) {
                List<Course> courses = courseMapper.selectCoursesByIds(trainingSchemeCourseVos.stream().map(tc -> tc.getId()).collect(Collectors.toList()));
                List<Course> filterCourse = courses.stream().filter(c -> ObjectUtils.isEmpty(c.getSubMajorId()) || c.getSubMajorId() == subMajorId).collect(Collectors.toList());
                for (Course course : filterCourse) {
                    TrainingCourseExportVo trainingCourseExportVo = new TrainingCourseExportVo();
                    BeanUtils.copyProperties(course, trainingCourseExportVo);
                    exportCourseList.add(trainingCourseExportVo);
                }
            }
            response.setContentType("application/x-download");
            String fileName = subMajor.getName()+"课程体系.xlsx";
            response.setHeader("Content-disposition", "attachment;filename*=UTF-8''" + URLEncoder.encode(fileName, "utf-8"));
            ExcelUtil<TrainingCourseExportVo> util = new ExcelUtil<>(TrainingCourseExportVo.class);
            util.exportExcel(response, exportCourseList, subMajor.getName()+"课程体系");
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Message BoundTrainingCourse(TrainingBoundCourseVo trainingBoundCourseVo) {
        //查询培养方案
        TrainingSchemeVo trainingSchemeVo = trainingSchemeMapper.selectTrainingSchemeById(trainingBoundCourseVo.getTrainingSchemeId());
        List<Course> courseList = new ArrayList<>();
        if("1".equals(trainingBoundCourseVo.getType())) {
            //覆盖
            trainingSchemeRefCourseMapper.deleteTrainingSchemeRefCourseBySchemeId(trainingBoundCourseVo.getTrainingSchemeId());
            trainingSchemeCourseScheduleMapper.deleteTrainingSchemeCourseScheduleByTrainingSchemeId(trainingBoundCourseVo.getTrainingSchemeId());
            //课程信息查询
            if(ObjectUtils.isNotEmpty(trainingBoundCourseVo.getCourseIds())){
                courseList.addAll(courseMapper.selectCoursesByIds(trainingBoundCourseVo.getCourseIds()));
            }
        }else{
            //追加
            //查询培养方案对应课程
            List<TrainingSchemeCourseVo> trainingSchemeCourseVos = trainingSchemeMapper.selectTrainingSchemeCoursesById(trainingBoundCourseVo.getTrainingSchemeId());
            //课程对比
            List<Long> selectCourseIds = trainingSchemeCourseVos.stream().map(t -> t.getId()).collect(Collectors.toList());
            List<Long> filterCourseIds = trainingBoundCourseVo.getCourseIds().stream().filter(courseId -> !selectCourseIds.contains(courseId)).collect(Collectors.toList());
            //课程信息查询
            courseList.addAll(courseMapper.selectCoursesByIds(filterCourseIds));
        }
        List<Long> baseCourseIds = courseList.stream().filter(c -> ("1".equals(c.getType()) || "3".equals(c.getType()))).map(c -> c.getId()).collect(Collectors.toList());
        if(!checkCourse(baseCourseIds,"1")){
            return DataSet.error("课程目标没有完全绑定毕业要求与知识体系，或课程缺失课程目标");
        }
        List<Long> practiceCourseIds = courseList.stream().filter(c -> "2".equals(c.getType())).map(c -> c.getId()).collect(Collectors.toList());
        if(!checkCourse(practiceCourseIds,"2")){
            return DataSet.error("课程目标没有完全绑定毕业要求，或实践训练课目缺失课程目标");
        }
        //绑定课程
        List<TrainingSchemeCourseVo> t_course=translateCourse(courseList);
        trainingSchemeVo.setCourseVos(t_course);
        insertTrainingSchemeRefCourses(trainingSchemeVo);
        //排课
        setCourseSchedule(trainingSchemeVo,courseList);
        return Message.success();
    }

    @Override
    public List<TrainingSchemeCourseVo> viewTrainingCourseKnowLedge(Long schemeId) {
        //根据专业类与版本查询课程
        List<TrainingSchemeRefCourse> trainingSchemeRefCourses = trainingSchemeRefCourseMapper.selectTrainingSchemeRefCourseByTrainingSchemeVoId(schemeId);
        //查询知识单元知识点
        if(ObjectUtils.isEmpty(trainingSchemeRefCourses)) {
            return null;
        }
        List<TrainingSchemeCourseVo> trainingSchemeCourseVos = courseMapper.selectCourseKnowledgeByCourseIds(trainingSchemeRefCourses.stream().map(c->c.getCourseId()).collect(Collectors.toList()));
        for (TrainingSchemeCourseVo trainingSchemeCourseVo : trainingSchemeCourseVos) {
            trainingSchemeCourseVo.setChildren(trainingSchemeCourseVo.getCourseKnowledgeUnitVoList());
        }
        return trainingSchemeCourseVos;
    }

    private void translateDict(TrainingPlanModel trainingPlanModel) {
        //处理公共基础课程教学安排
        List<TrainingSchemeCourseModel> generalCourses = trainingPlanModel.getGeneralCourses();
        if (ObjectUtils.isNotEmpty(generalCourses)) {
            // 按课程模式子ID分组
            Map<String, List<TrainingSchemeCourseModel>> courseModelMap =
                    generalCourses.stream().filter(c -> ObjectUtils.isNotEmpty(c.getCourseModelId())&&ObjectUtils.isNotEmpty(c.getCourseModeChildrenId()))
                            .collect(Collectors.groupingBy(TrainingSchemeCourseModel::getCourseModeChildrenId));

            // 处理每个课程模式组
            courseModelMap.forEach(this::processCourseGroup);
        }
        //处理专业课程教学安排 专业方向转换
        List<TrainingSchemeCourseModel> majorCourses = trainingPlanModel.getMajorCourses();
        if (ObjectUtils.isNotEmpty(majorCourses)) {
            List<StandardMajor> standardMajors = standardMajorMapper.selectStandardMajorByIds(majorCourses.stream().map(m -> m.getSubmajorId()).collect(Collectors.toList()));
            Map<Long, String> majorMap = standardMajors.stream().collect(Collectors.toMap(StandardMajor::getId, StandardMajor::getName));
            majorCourses.forEach(m -> m.setSubMajorName(majorMap.get(m.getSubmajorId())));
        }
        //处理实践项目与安排 支撑课程转换 项目层级
        List<TrainingSchemeCourseModel> practicalProjectCourse = trainingPlanModel.getPracticalProjectCourse();
        if (ObjectUtils.isNotEmpty(practicalProjectCourse)) {
            //处理专业方向：根据 subMajorId 查询专业方向名称
            List<Long> subMajorIds = practicalProjectCourse.stream()
                    .map(TrainingSchemeCourseModel::getSubmajorId)
                    .filter(ObjectUtils::isNotEmpty)
                    .distinct()
                    .collect(Collectors.toList());
            if (ObjectUtils.isNotEmpty(subMajorIds)) {
                List<StandardMajor> standardMajors = standardMajorMapper.selectStandardMajorByIds(subMajorIds);
                Map<Long, String> subMajorMap = standardMajors.stream()
                        .collect(Collectors.toMap(StandardMajor::getId, StandardMajor::getName, (a, b) -> a));
                practicalProjectCourse.forEach(p -> p.setSubMajorName(subMajorMap.get(p.getSubmajorId())));
            }
            practicalProjectCourse.forEach(p -> {
                //处理支撑课程
                String supportingCourseIds = p.getSupportingCourseIds();
                if (ObjectUtils.isNotEmpty(supportingCourseIds)) {
                    List<Long> courseIds = Arrays.stream(supportingCourseIds.split(","))
                            .map(Long::valueOf)  // 将每个字符串转换为 Long
                            .collect(Collectors.toList());
                    String courseNames = courseMapper.selectCoursesByIds(courseIds).stream()
                            .map(Course::getName)                  // 提取每个 Course 的 name 属性
                            .collect(Collectors.joining(", "));
                    p.setSupportingCourseNames(courseNames);
                }
                //处理层级
                SysDictData dictData = CurDictUtils.getDictData(DictContent.CUR_PROGRAM_LEVEL, p.getProjectLevelId());
                if(ObjectUtils.isNotEmpty(dictData)) {
                    p.setProjectLevelName(dictData.getDictLabel());
                }
                //处理时间单位(sys_course_unit 1=周 2=学时) 转中文
                SysDictData unitDictData = CurDictUtils.getDictData(DictContent.SYS_COURSE_UNIT, p.getUnit());
                if(ObjectUtils.isNotEmpty(unitDictData)) {
                    p.setUnit(unitDictData.getDictLabel());
                }
            });
        }
        //处理实践项目课目科目模块
        List<TrainingSchemeCourseModel> trainingSubjectCourses = trainingPlanModel.getTrainingSubjectCourses();
        if(ObjectUtils.isNotEmpty(trainingSubjectCourses)) {
            trainingSubjectCourses.forEach(t->{
                //处理课目模块
                SysDictData dictData = CurDictUtils.getDictData(DictContent.CUR_TRAINING_MODULE,t.getTrainingCourseModelId() );
                if(ObjectUtils.isNotEmpty(dictData)) {
                    t.setTrainingCourseModelName(dictData.getDictLabel());
                }
                //处理时间单位(sys_course_unit 1=周 2=学时) 转中文
                SysDictData unitDictData = CurDictUtils.getDictData(DictContent.SYS_COURSE_UNIT, t.getUnit());
                if(ObjectUtils.isNotEmpty(unitDictData)) {
                    t.setUnit(unitDictData.getDictLabel());
                }
            });
        }
    }

    /**
     * 处理课程模式分组逻辑
     */
    private void processCourseGroup(String modeId, List<TrainingSchemeCourseModel> courses) {
        Dictionary data = remoteKgService.getDictionary(modeId).getData();

        if (data.getLevel() == 4) {
            handleFourLevelCourses(courses, data);
        } else {
            handleStandardLevelCourses(courses, data);
        }
    }

    /**
     * 处理四级课程模式
     */
    private void handleFourLevelCourses(List<TrainingSchemeCourseModel> courses, Dictionary data) {
        Dictionary parentData = remoteKgService.getDictionary(data.getParentId().toString()).getData();

        courses.forEach(course -> {
            course.setCourseModeChildrenName(parentData.getName());
            course.setChildrenModelSort(parentData.getOrder());
            course.setCourseModeFourLevelName(data.getName());
            course.setCourseModeFourLevelSort(data.getOrder());
        });
    }

    /**
     * 处理标准层级课程模式
     */
    private void handleStandardLevelCourses(List<TrainingSchemeCourseModel> courses, Dictionary data) {
        courses.forEach(course -> {
            course.setCourseModeChildrenName(data.getName());
            course.setChildrenModelSort(data.getOrder());
        });
    }

    private void setUrl(TrainingSchemeVo trainingSchemeVo) {
        FileInfoVO fileInfo = new FileInfoVO();
        fileInfo.setFileId(trainingSchemeVo.getFileId());
        List<FileInfo> fileInfos = remoteFileInfoService.list(fileInfo).getData();
        if (ObjectUtils.isNotEmpty(fileInfos)) {
            FileInfo file = fileInfos.get(0);
            trainingSchemeVo.setDownloadUrl(file.getDownloadUrl());
            trainingSchemeVo.setPreviewUrl(file.getPreviewUrl());
            trainingSchemeVo.setFileName(file.getFileName());
            //将fileId替换为id
            trainingSchemeVo.setFileId(file.getId().toString());
        }
    }

    @Override
    public List<CollegeSchemeVo> selectSchemeCollegeVoList() {

        return trainingSchemeMapper.selectSchemeCollegeVoList();
    }

    @Override
    public StandardMajor selectStandardMajorById(Long id) {
        return standardMajorMapper.selectStandardMajorById(id);
    }

    // 单元掌握初始程度
    private Map<String, String> getInitLevels() {
        String init = "kg_attr_cscd";
        List<SysDictData> initLevels = doinnerDictDataService.dictType(init).getData();
        Map<String, String> initLevelMap = initLevels.stream().collect(Collectors.toMap(sysDictData -> sysDictData.getDictValue(), sysDictData -> sysDictData.getDictLabel()));
        return initLevelMap;
    }

    // 单元掌握要求程度
    private Map<String, String> getRequireLevels() {
        String require = "kg_attr_yqcd";
        List<SysDictData> requireLevels = doinnerDictDataService.dictType(require).getData();
        Map<String, String> requireLevelMap = requireLevels.stream().collect(Collectors.toMap(sysDictData -> sysDictData.getDictValue(), sysDictData -> sysDictData.getDictLabel()));
        return requireLevelMap;
    }

    // 考核方式
    private Map<String, String> getExaMethod() {
        String exa = "kg_attr_exam";
        List<SysDictData> exaMethods = doinnerDictDataService.dictType(exa).getData();
        Map<String, String> exaMethodMap = exaMethods.stream().collect(Collectors.toMap(exaMethod -> exaMethod.getDictValue(), exaMethod -> exaMethod.getDictLabel()));
        return exaMethodMap;
    }

    // 课程性质
    private Map<String, String> getCourseProp() {
        String prop = "kg_attr_course_pro";
        List<SysDictData> courseProps = doinnerDictDataService.dictType(prop).getData();
        Map<String, String> coursePropMap = courseProps.stream().collect(Collectors.toMap(courseProp -> courseProp.getDictValue(), courseProp -> courseProp.getDictLabel()));
        return coursePropMap;
    }

    // 课程大类
    private Map<String, String> getCourseType() {
        String type = "kg_attr_course_bro";
        List<SysDictData> courseTypes = doinnerDictDataService.dictType(type).getData();
        Map<String, String> courseTypeMap = courseTypes.stream().collect(Collectors.toMap(courseType -> courseType.getDictValue(), courseType -> courseType.getDictLabel()));
        return courseTypeMap;
    }

    // 课程属性
    private Map<String, String> getCourseAttr() {
        String attr = "kg_attr_course_attr";
        List<SysDictData> courseAttrs = doinnerDictDataService.dictType(attr).getData();
        Map<String, String> courseAttrMap = courseAttrs.stream().collect(Collectors.toMap(courseAttr -> courseAttr.getDictValue(), courseAttr -> courseAttr.getDictLabel()));
        return courseAttrMap;
    }

    private List<CourseChapterVo> json2Chapter(String json) {
        List<CourseChapterVo> chapterVos = new ArrayList<>();
        List<ChapterVo> list = JSONArray.parseArray(json, ChapterVo.class);
        for (ChapterVo chapterVo : list) {
            List<TableHeaderVo> header = chapterVo.getHeader();
            List<Map<String, String>> body = chapterVo.getBody();
            List<Map<String, String>> maps = setKeyValue(header, body);
            for (Map<String, String> map : maps) {
                CourseChapterVo chapter = new CourseChapterVo();
                chapter.setName(map.get("name"));
                chapter.setContent(map.get("content"));
                chapter.setHour(map.get("hour"));
                chapterVos.add(chapter);
            }
        }
        return chapterVos;
    }

    private List<Map<String, String>> setKeyValue(List<TableHeaderVo> headers, List<Map<String, String>> bodies) {
        List<Map<String, String>> maps = new ArrayList<>();
        List<String> names = Arrays.asList(DomainFieldConstant.CHAPTER_NAMES);
        String content = DomainFieldConstant.CHAPTER_CONTENT;
        String hour = DomainFieldConstant.CHAPTER_HOUR;
        for (Map<String, String> body : bodies) {
            Map<String, String> map = new HashMap<>();
            for (TableHeaderVo header : headers) {
//                if (names.contains(header.getTitle())){
//                    map.put("name",body.get(header.getDataName()));
//                }
//                if (content.equals(header.getTitle())){
//                    map.put("content",body.get(header.getDataName()));
//                }
//                if (hour.equals(header.getTitle())){
//                    map.put("hour",body.get(header.getDataName()));
//                }

                for (Map.Entry<String, String> entry : body.entrySet()) {
                    if (header.getDataName().equals(entry.getKey())) {
                        if (names.contains(header.getTitle()) && StringUtils.isNotBlank(entry.getValue())) {
                            map.put("name", entry.getValue());
                        }
                        if (content.equals(header.getTitle()) && StringUtils.isNotBlank(entry.getValue())) {
                            map.put("content", entry.getValue());
                        }
                        if (hour.equals(header.getTitle()) && StringUtils.isNotBlank(entry.getValue())) {
                            map.put("hour", entry.getValue());
                        }
                    }
                }
            }
            if (ObjectUtils.isNotEmpty(map)) {
                maps.add(map);
            }
        }
        return maps;
    }

    private List<Table> json2Table(String json) {
        List<Table> tables = new ArrayList<>();
        List<TableVo> list = JSONArray.parseArray(json, TableVo.class);
        for (TableVo tableVo : list) {
            Table table = new Table();
            table.setTitleValue(tableVo.getTitleValue());
            table.setTable(tableVo.isTable());
            if (tableVo.isTable()) {
                List<TableHeaderVo> header = tableVo.getHeader();
                List<Map<String, String>> body = tableVo.getBody();
                Map<String, String[]> rows = new HashMap<>();
                if (CollectionUtils.isNotEmpty(header)) {
                    rows = setTableRows(header, body);
                }
                table.setRows(rows);
            } else {
                table.setInputValue(tableVo.getInputValue());
            }
            tables.add(table);
        }
        return tables;
    }

    private Map<String, String[]> setTableRows(List<TableHeaderVo> headers, List<Map<String, String>> bodies) {
        Map<String, String[]> rows = new HashMap<>();
        List<String> row0 = new ArrayList<>(headers.size());
        for (int i = 0; i < headers.size(); i++) {
            row0.add(i, headers.get(i).getTitle());
        }
        rows.put("row0", row0.toArray(new String[row0.size()]));
        int rowNum = 1;
        for (Map<String, String> body : bodies) {
            String[] strings = new String[headers.size()];
            List<String> row = Arrays.asList(strings);
            for (int i = 0; i < headers.size(); i++) {
                for (Map.Entry<String, String> entry : body.entrySet()) {
                    if (headers.get(i).getDataName().equals(entry.getKey())) {
                        row.set(i, entry.getValue());
                    }
                }
            }
            rows.put("row" + rowNum, row.toArray(new String[row.size()]));
            rowNum++;
        }
        return rows;
    }

    private List<ArrangementVo> json2Arrangement(String json) {
        return JSONArray.parseArray(json, ArrangementVo.class);
    }

}
