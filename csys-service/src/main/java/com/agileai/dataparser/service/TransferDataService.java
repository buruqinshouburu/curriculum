package com.agileai.dataparser.service;

import com.agileai.dataparser.constant.ConstantTrainingScheme;
import com.agileai.dataparser.constant.DataMapConstant;
import com.agileai.dataparser.domain.CurPlanning;
import com.agileai.dataparser.domain.CurTeachingPlan;
import com.agileai.dataparser.domain.CurTrainingProgram;
import com.agileai.dataparser.mapper.*;
import com.agileai.dataparser.utils.MongodbUtils;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.doinner.common.core.domain.DataSet;
import com.doinner.csys.constant.DomainFieldConstant;
import com.doinner.csys.dao.*;
import com.doinner.csys.domain.*;
import com.doinner.csys.domain.vo.TrainingSchemeCourseScheduleVo;
import com.doinner.csys.utils.MultipartFileUtils;
import com.doinner.file.api.constant.DomainFieldConstants;
import com.doinner.file.api.domain.FileInfo;
import com.doinner.file.api.service.RemoteFileInfoService;
import com.doinner.kg.domain.*;
import com.doinner.kg.service.RemoteKgService;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.gridfs.model.GridFSFile;
import io.swagger.models.auth.In;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class TransferDataService {
    private static final Logger log = LoggerFactory.getLogger(TransferDataService.class);
    @Resource
    private CurPlanningMapper curPlanningMapper;
    @Resource
    private CurTeachingPlanMapper curTeachingPlanMapper;
    @Resource
    private TrainingSchemeMapper trainingSchemeMapper;
    @Resource
    private CurTrainingProgramMapper curTrainingProgramMapper;
    @Resource
    private TempCourseService tempCourseService;
    @Resource
    private StdService stdService;
    @Resource
    private TrainingSchemeRefCourseMapper trainingSchemeRefCourseMapper;
    @Resource
    private TrainingSchemeCourseScheduleMapper trainingSchemeCourseScheduleMapper;
    @Resource
    private MongoGridFSRepository gridFSRepository;
    @Resource
    private RemoteFileInfoService remoteFileInfoService;
    @Resource
    private CourseMapper courseMapper;
    @Resource
    private KnowledgePointMapper knowledgePointMapper;
    @Resource
    private KnowledgeUnitMapper knowledgeUnitMapper;
    @Resource
    private CourseRefKeUnitMapper courseRefKeUnitMapper;
    @Resource
    private KnowledgeUnitRefPointMapper knowledgeUnitRefPointMapper;
    @Resource
    private RemoteKgService remoteKgService;
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
    @Resource
    private KnowledgeUnitRefStdCultivationMapper unitRefStdCultivationMapper;

    private Ontology ontology = new Ontology();

    private Concept course = new Concept();

    private Concept unit = new Concept();

    private Concept point = new Concept();

    private Relationship courseUnit = new Relationship();

    private Relationship unitPoint = new Relationship();

    private Concept cultivationTarget = new Concept();

    private Concept graduation = new Concept();

    private Concept cultivation = new Concept();

    private Relationship cultivationTargetGraduation = new Relationship();

    private Relationship graduationCultivation = new Relationship();

    private Relationship unitCultivation = new Relationship();

    private Map<Long,ConceptInstance> unitMap;

    @Transactional
    public void transferToKgData(){
        initOntology();
        doCourseUnit();
        doStandards();
    }

    @Transactional
    public void transferPlan(){
        tempCourseService.batchCourse();
        tempCourseService.batchUpdateCourse();
//        stdService.sync();

        List<CurTrainingProgram> curTrainingPrograms = curTrainingProgramMapper.selectCurTrainingProgramList(null);
        curTrainingPrograms.stream().forEach(x -> {
            if(ConstantTrainingScheme.TYPE_IS_LEAF.equals(x.getType())){
                CurTeachingPlan curTeachingPlan = curTeachingPlanMapper.selectCurTeachingPlanById(x.getTeachingPlanId());
                CurPlanning curPlanning = null;
                if(ObjectUtils.isNotEmpty(curTeachingPlan)){
                    curPlanning = curPlanningMapper.selectCurPlanningById(curTeachingPlan.getPlanId());
                }
                TrainingScheme scheme = new TrainingScheme();
                scheme.setName(x.getName());
                scheme.setCategoryId(x.getParentId());
                scheme.setCollegeId(Long.valueOf(x.getCollege()));
                scheme.setFileId(x.getFileId());
                scheme.setFileName(x.getFileName());
                trainingSchemeMapper.insertTrainingScheme(scheme);
                if(StringUtils.isNotBlank(scheme.getFileId())){
                    GridFSFile fsFile = gridFSRepository.find(scheme.getFileId());
                    if(ObjectUtils.isNotEmpty(fsFile)){
                        InputStream inputStream = gridFSRepository.getFileInputStream(scheme.getFileId());
                        MultipartFile file = MultipartFileUtils.getMultipartFile(inputStream,scheme.getFileName());
                        DataSet<FileInfo> upload = remoteFileInfoService.upload(file, DomainFieldConstants.COURSE_CATEGORY_ID);
                        scheme.setFileId(upload.getData().getFileId());
                        scheme.setFileName(upload.getData().getFileName());
                        trainingSchemeMapper.updateTrainingScheme(scheme);
                        log.info("培养方案:"+scheme.getName()+"同步文件成功");
                    }
                }
                if(ObjectUtils.isNotEmpty(curPlanning)){
                    processPlan(curPlanning,scheme);
                }
            }
        });
        log.info("培养方案同步成功");
    }

    private void processPlan(CurPlanning curPlanning, TrainingScheme scheme) {
        //公共基础
        String pfrc = curPlanning.getPfrcIds();
        if(StringUtils.isNotBlank(pfrc)){
            transCourseStr(pfrc,scheme.getId(),1);
        }
        //学科基础
        String sfrc = curPlanning.getSfrcIds();
        if(StringUtils.isNotBlank(sfrc)){
            transCourseStr(sfrc,scheme.getId(),2);
        }
        //专业必修
        String prc = curPlanning.getPrcIds();
        if(StringUtils.isNotBlank(prc)){
            transCourseStr(prc,scheme.getId(),3);
        }
        //公共选修
        String pfec = curPlanning.getPfecIds();
        if(StringUtils.isNotBlank(pfec)){
            transCourseStr(pfec,scheme.getId(),4);
        }
        //学科专业选修
        String sfpec = curPlanning.getSfpecIds();
        if(StringUtils.isNotBlank(sfpec)){
            transCourseStr(sfpec,scheme.getId(),5);
        }
        //排课
        String schedule = curPlanning.getCourseSelection();
        if(StringUtils.isNotBlank(schedule)){
            scheduleCourse(schedule,scheme.getId());
        }
    }


    /**  处理5类课程选课信息*/
    private void transCourseStr(String courseStr,Long schemeId,Integer courseType){
        String[] courseArr = courseStr.split(",");
        List<TrainingSchemeRefCourse> list = Lists.newArrayList();
        for(String courseId:courseArr){
            Long nowId = DataMapConstant.courseIdMap.get(new ObjectId(courseId));
            if(ObjectUtils.isNotEmpty(nowId)){
                TrainingSchemeRefCourse tsrf = new TrainingSchemeRefCourse();
                tsrf.setSchemeId(schemeId);
                tsrf.setCourseTypeId(courseType);
                tsrf.setCourseId(nowId);
                list.add(tsrf);
            }
        }
        if(CollectionUtils.isNotEmpty(list)){
            trainingSchemeRefCourseMapper.insertTrainingSchemeRefCourses(list);
        }
        log.info("培养方案选课信息同步成功");
    }

    /** 处理排课信息*/
    private void scheduleCourse(String schedule,Long schemeId){
        List<TrainingSchemeCourseSchedule> list = Lists.newArrayList();
        log.info("培养方案排课信息同步开始:"+schemeId);
        JSONArray jsonArray = JSONArray.parseArray(schedule);
        jsonArray.stream().forEach(arr ->{
            JSONObject obj = (JSONObject) arr;
            String term = (String) obj.get("name");
            Integer termInt = 0;
            if(DomainFieldConstant.FERSHMAN_YEAR_FIRST_SEMESTER.equals(term)){
                termInt = 1;
            }
            if(DomainFieldConstant.FERSHMAN_YEAR_SECOND_SEMESTER.equals(term)){
                termInt = 2;
            }
            if(DomainFieldConstant.SOPHOMORE_YEAR_FIRST_SEMESTER.equals(term)){
                termInt = 3;
            }
            if(DomainFieldConstant.SOPHOMORE_YEAR_SECOND_SEMESTER.equals(term)){
                termInt = 4;
            }
            if(DomainFieldConstant.JUNIOR_YEAR_FIRST_SEMESTER.equals(term)){
                termInt = 5;
            }
            if(DomainFieldConstant.JUNIOR_YEAR_SECOND_SEMESTER.equals(term)){
                termInt = 6;
            }
            if(DomainFieldConstant.SENIOR_YEAR_FIRST_SEMESTER.equals(term)){
                termInt = 7;
            }
            if(DomainFieldConstant.SENIOR_YEAR_SECOND_SEMESTER.equals(term)){
                termInt = 8;
            }
            JSONArray subjects = (JSONArray) obj.get("subject");
            if(subjects.size()>0){
                List<Course> courses = Lists.newArrayList();
                subjects.stream().forEach(course ->{
                    JSONObject object = (JSONObject)course;
                    Course cur = courseMapper.selectCourseById(DataMapConstant.courseIdMap.get(new ObjectId((String)object.get("id"))));
                    if(ObjectUtils.isNotEmpty(cur) && ObjectUtils.isNotEmpty(cur.getId())){
                        if(ObjectUtils.isNotEmpty(object.get("time"))){
                            Integer time = (Integer) object.get("time");
                            cur.setHours(time.doubleValue());
                        }
                        if(ObjectUtils.isNotEmpty(object.get("theoryHour"))){
                            Integer theoryHour = (Integer) object.get("theoryHour");
                            cur.setTheoryHours(theoryHour.doubleValue());
                        }
                        if(ObjectUtils.isNotEmpty(object.get("practiceHour"))){
                            Integer practiceHour = (Integer) object.get("practiceHour");
                            cur.setPracticeHours(practiceHour.doubleValue());
                        }
                        courses.add(cur);
                    }
                });

                for(Course course:courses){
                    TrainingSchemeCourseSchedule courseSchedule = new TrainingSchemeCourseSchedule();
                    TrainingSchemeRefCourse query = new TrainingSchemeRefCourse();
                    query.setCourseId(course.getId());
                    query.setSchemeId(schemeId);
                    List<TrainingSchemeRefCourse> refCourses = trainingSchemeRefCourseMapper.selectTrainingSchemeRefCourseList(query);
                    if(refCourses.size() != 1){
                        continue;
                    }
                    courseSchedule.setTerm(termInt);
                    courseSchedule.setCourseId(course.getId());
                    courseSchedule.setSchemeId(schemeId);
                    courseSchedule.setType(course.getCourseModuleChildren());
                    courseSchedule.setHours(course.getHours());
                    courseSchedule.setTheoryHours(course.getTheoryHours());
                    courseSchedule.setTeachHours(course.getTeachHours());
                    courseSchedule.setPracticeHours(course.getPracticeHours());
                    courseSchedule.setChecked(1);
                    list.add(courseSchedule);
                }
            }
        });

        if(CollectionUtils.isNotEmpty(list)){
            trainingSchemeCourseScheduleMapper.insertTrainingSchemeCourseSchedules(list);
        }
        log.info("培养方案排课信息同步成功"+schemeId);
    }

    @Transactional
    public void updateBeforeAndAfterCourse(){
        List<Course> courses = courseMapper.selectCourseBeforeAndAfterList("null","null");
        courses.stream().forEach(course -> {
            if(StringUtils.isNotBlank(course.getBeforeCourseId())){
                String[] befores = course.getBeforeCourseId().split(",");
                String before = "";
                for(String bef:befores){
                    if("null".equals(bef)){
                        continue;
                    }
                    before += bef + ",";
                }
                if(before.length() > 1){
                    before = before.substring(0,before.length()-1);
                }
                log.info("课程前续：" + course.getBeforeCourseId() + "更新后：" + before);
                course.setBeforeCourseId(before);
            }
            if(StringUtils.isNotBlank(course.getAfterCourseId())){
                String[] afters = course.getAfterCourseId().split(",");
                String after = "";
                for(String af:afters){
                    if("null".equals(af)){
                        continue;
                    }
                    after += af + ",";
                }
                if(after.length() > 1){
                    after = after.substring(0,after.length()-1);
                }
                log.info("课程后续：" + course.getAfterCourseId() + "更新后：" + after);
                course.setAfterCourseId(after);
            }
            courseMapper.updateCourse(course);
            log.info("课程前后续更新成功");
        });

    }

    @Transactional
    public void updateSchedule(){
        List<TrainingSchemeCourseScheduleVo> schedules = trainingSchemeCourseScheduleMapper.selectTrainingSchemeCourseScheduleList(null);
        schedules.stream().forEach(schedule ->{
            log.info("排课课时更新开始：" + schedule.getId());
            Course course = courseMapper.selectCourseById(schedule.getCourseId());
            if (ObjectUtils.isEmpty(course) || (ObjectUtils.isEmpty(course.getHours())
                    && ObjectUtils.isEmpty(course.getTheoryHours()) && ObjectUtils.isEmpty(course.getPracticeHours()))) {
                return;
            }
            TrainingSchemeCourseSchedule update = new TrainingSchemeCourseSchedule();
            update.setId(schedule.getId());
            update.setHours(course.getHours());
            update.setTheoryHours(course.getTheoryHours());
            update.setTeachHours(course.getTeachHours());
            update.setPracticeHours(course.getPracticeHours());
            trainingSchemeCourseScheduleMapper.updateTrainingSchemeCourseSchedule(update);
            log.info("排课课时更新结束：" + schedule.getId());
        });
    }

    private void initOntology() {
        ontology.setName("培养方案本体");
        DataSet<Ontology> ontologyDataSet = remoteKgService.insertOntology(ontology);
        ontology = ontologyDataSet.getData();
        System.out.println("培养方案本体："+ ontology.getName() + "-----" + ontology.getId());
        course.setName("课程");
        course.setParentId(ontology.getRootConceptId());
        course.setOntologyId(ontology.getId());
        DataSet<Concept> courseDataSet = remoteKgService.insertConcept(course);
        course = courseDataSet.getData();
        System.out.println("课程概念："+ course.getName() + "-----" + course.getId());
        unit.setName("知识单元");
        unit.setParentId(course.getId());
        unit.setOntologyId(ontology.getId());
        DataSet<Concept> unitDataSet = remoteKgService.insertConcept(unit);
        unit = unitDataSet.getData();
        System.out.println("知识单元概念："+ unit.getName() + "-----" + unit.getId());
        point.setName("知识点");
        point.setParentId(unit.getId());
        point.setOntologyId(ontology.getId());
        DataSet<Concept> pointDataSet = remoteKgService.insertConcept(point);
        point = pointDataSet.getData();
        System.out.println("知识点概念："+ point.getName() + "-----" + point.getId());

        courseUnit.setName("课程知识单元");
        courseUnit.setOntologyId(ontology.getId());
        courseUnit.setLeaf(true);
        courseUnit.setParentId(ontology.getRootRelationshipId());
        courseUnit.setSourceConceptIds(List.of(course.getId()));
        courseUnit.setTargetConceptIds(List.of(unit.getId()));
        DataSet<Relationship> courseUnitSet = remoteKgService.insertRelationship(courseUnit);
        courseUnit = courseUnitSet.getData();

        unitPoint.setName("知识单元知识点");
        unitPoint.setOntologyId(ontology.getId());
        unitPoint.setLeaf(true);
        unitPoint.setParentId(ontology.getRootRelationshipId());
        unitPoint.setSourceConceptIds(List.of(unit.getId()));
        unitPoint.setTargetConceptIds(List.of(point.getId()));
        DataSet<Relationship> unitPointSet = remoteKgService.insertRelationship(unitPoint);
        unitPoint = unitPointSet.getData();

        cultivationTarget.setName("培养目标");
        cultivationTarget.setParentId(ontology.getRootConceptId());
        cultivationTarget.setOntologyId(ontology.getId());
        DataSet<Concept> cultivationTargetDataSet = remoteKgService.insertConcept(cultivationTarget);
        cultivationTarget = cultivationTargetDataSet.getData();

        graduation.setName("毕业标准");
        graduation.setParentId(ontology.getRootConceptId());
        graduation.setOntologyId(ontology.getId());
        DataSet<Concept> graduationDataSet = remoteKgService.insertConcept(graduation);
        graduation = graduationDataSet.getData();

        cultivation.setName("培养标准");
        cultivation.setParentId(ontology.getRootConceptId());
        cultivation.setOntologyId(ontology.getId());
        DataSet<Concept> cultivationDataSet = remoteKgService.insertConcept(cultivation);
        cultivation = cultivationDataSet.getData();

        cultivationTargetGraduation.setName("培养目标毕业标准");
        cultivationTargetGraduation.setOntologyId(ontology.getId());
        cultivationTargetGraduation.setLeaf(true);
        cultivationTargetGraduation.setParentId(ontology.getRootRelationshipId());
        cultivationTargetGraduation.setSourceConceptIds(List.of(cultivationTarget.getId()));
        cultivationTargetGraduation.setTargetConceptIds(List.of(graduation.getId()));
        DataSet<Relationship> cultivationTargetGraduationSet = remoteKgService.insertRelationship(cultivationTargetGraduation);
        cultivationTargetGraduation = cultivationTargetGraduationSet.getData();

        graduationCultivation.setName("毕业标准培养标准");
        graduationCultivation.setOntologyId(ontology.getId());
        graduationCultivation.setLeaf(true);
        graduationCultivation.setParentId(ontology.getRootRelationshipId());
        graduationCultivation.setSourceConceptIds(List.of(graduation.getId()));
        graduationCultivation.setTargetConceptIds(List.of(cultivation.getId()));
        DataSet<Relationship> graduationCultivationSet = remoteKgService.insertRelationship(graduationCultivation);
        graduationCultivation = graduationCultivationSet.getData();

        unitCultivation.setName("知识单元培养标准");
        unitCultivation.setOntologyId(ontology.getId());
        unitCultivation.setLeaf(true);
        unitCultivation.setParentId(ontology.getRootRelationshipId());
        unitCultivation.setSourceConceptIds(List.of(unit.getId()));
        unitCultivation.setTargetConceptIds(List.of(cultivation.getId()));
        DataSet<Relationship> unitCultivationSet = remoteKgService.insertRelationship(unitCultivation);
        unitCultivation = unitCultivationSet.getData();
        System.out.println("本体概念关系定义创建完成！");
    }

    private void doCourseUnit(){
        System.out.println("课程相关开始---------------------");
        List<Course> courses = courseMapper.selectCourseList(null);
        Map<Long,ConceptInstance> courseMap = transCourse(courses);
        System.out.println("课程数----" + courseMap.keySet().size());

        //知识单元还要与培养标准建立关系所以用成员变量
        List<KnowledgeUnit> units = knowledgeUnitMapper.selectKnowledgeUnitList(null);
        unitMap = transUnit(units);
        System.out.println("知识单元数----" + unitMap.keySet().size());

        List<KnowledgePoint> points = knowledgePointMapper.selectKnowledgePointList(null);
        Map<Long,ConceptInstance> pointMap = transPoint(points);
        System.out.println("知识点数----" + pointMap.keySet().size());

        //课程与知识单元
        List<CourseRefKeUnit> courseRefKeUnits = courseRefKeUnitMapper.selectCourseRefKeUnitList(null);
        List<RelationshipInstance> courseRefUnit = transCourseRefUnit(courseRefKeUnits,courseMap,unitMap);
        courseRefUnit.stream().forEach(relationshipInstance -> {
            remoteKgService.insertRelationshipInstance(relationshipInstance);
        });

        //知识单元与知识点
        List<KnowledgeUnitRefPoint> unitRefPoints = knowledgeUnitRefPointMapper.selectKnowledgeUnitRefPointList(null);
        List<RelationshipInstance> unitRefPoint = transUnitRefPoint(unitRefPoints,unitMap,pointMap);
        unitRefPoint.stream().forEach(relationshipInstance -> {
            remoteKgService.insertRelationshipInstance(relationshipInstance);
        });
        System.out.println("课程相关结束---------------------");
    }

    private void doStandards(){
        System.out.println("培养目标等相关开始---------------------");
        List<StandardCultivationTarget> cultivationTargets = standardCultivationTargetMapper.selectStandardCultivationTargetList(null);
        Map<Long, ConceptInstance> cultivationTargetMap = transCultivationTarget(cultivationTargets);
        System.out.println("培养目标数----"+ cultivationTargetMap.keySet().size());

        List<StandardGraduation> graduations = standardGraduationMapper.selectStandardGraduationList(null);
        Map<Long, ConceptInstance> graduationMap = transGraduation(graduations);
        System.out.println("毕业标准数----"+ graduationMap.keySet().size());

        List<StandardCultivation> cultivations = standardCultivationMapper.selectStandardCultivationList(null);
        Map<Long, ConceptInstance> cultivationMap = transCultivation(cultivations);
        System.out.println("培养标准数----"+ cultivationMap.keySet().size());

        //培养目标与毕业标识
        List<StandardGraduationRefCultivationTarget> graduationRefCultivationTargets = standardGraduationRefCultivationTargetMapper.selectAll();
        List<RelationshipInstance> graduationRefCultivationTarget = transGraduationRefCultivationTarget(graduationRefCultivationTargets,cultivationTargetMap,graduationMap);
        graduationRefCultivationTarget.stream().forEach(relationshipInstance -> {
            remoteKgService.insertRelationshipInstance(relationshipInstance);
        });

        //毕业标识与培养标准
        List<StandardCultivationRefGraduation> cultivationRefGraduations = standardCultivationRefGraduationMapper.selectStandardCultivationRefGraduationList(null);
        List<RelationshipInstance> cultivationRefGraduation = transCultivationRefGraduation(cultivationRefGraduations,graduationMap,cultivationMap);
        cultivationRefGraduation.stream().forEach(relationshipInstance -> {
            remoteKgService.insertRelationshipInstance(relationshipInstance);
        });

        //知识单元与培养标准
        List<KnowledgeUnitRefStdCultivation> unitRefStdCultivations = unitRefStdCultivationMapper.selectKnowledgeUnitRefStdCultivationList(null);
        List<RelationshipInstance> unitRefStdCultivation = transUnitRefStdCultivation(unitRefStdCultivations,unitMap,cultivationMap);
        unitRefStdCultivation.stream().forEach(relationshipInstance -> {
            remoteKgService.insertRelationshipInstance(relationshipInstance);
        });
        System.out.println("培养目标等相关结束---------------------");
    }

    private Map<Long, ConceptInstance> transCultivationTarget(List<StandardCultivationTarget> cultivationTargets) {
        Map<Long, ConceptInstance> cultivationTargetMap = Maps.newHashMap();
        cultivationTargets.stream().forEach(ct -> {
            ConceptInstance conceptInstance = new ConceptInstance();
            conceptInstance.setName(ct.getName());
            conceptInstance.setOntologyId(ontology.getId());
            conceptInstance.setOntologyName(ontology.getName());
            conceptInstance.setConceptId(cultivationTarget.getId());
            conceptInstance.setConceptName(cultivationTarget.getName());

            DataSet<ConceptInstance> ctData = remoteKgService.insertConceptInstance(conceptInstance);
            cultivationTargetMap.put(ct.getId(),ctData.getData());
        });
        return cultivationTargetMap;
    }

    private Map<Long, ConceptInstance> transGraduation(List<StandardGraduation> graduations) {
        Map<Long,ConceptInstance> graduationMap = Maps.newHashMap();
        graduations.stream().forEach(g -> {
            ConceptInstance conceptInstance = new ConceptInstance();
            conceptInstance.setName(g.getName());
            conceptInstance.setOntologyId(ontology.getId());
            conceptInstance.setOntologyName(ontology.getName());
            conceptInstance.setConceptId(graduation.getId());
            conceptInstance.setConceptName(graduation.getName());

            DataSet<ConceptInstance> gData = remoteKgService.insertConceptInstance(conceptInstance);
            graduationMap.put(g.getId(),gData.getData());
        });
        return graduationMap;
    }

    private Map<Long, ConceptInstance> transCultivation(List<StandardCultivation> cultivations) {
        Map<Long,ConceptInstance> cultivationMap = Maps.newHashMap();
        cultivations.stream().forEach(c -> {
            ConceptInstance conceptInstance = new ConceptInstance();
            conceptInstance.setName(c.getName());
            conceptInstance.setOntologyId(ontology.getId());
            conceptInstance.setOntologyName(ontology.getName());
            conceptInstance.setConceptId(cultivation.getId());
            conceptInstance.setConceptName(cultivation.getName());

            DataSet<ConceptInstance> cData = remoteKgService.insertConceptInstance(conceptInstance);
            cultivationMap.put(c.getId(),cData.getData());
        });
        return cultivationMap;
    }

    private List<RelationshipInstance> transGraduationRefCultivationTarget(List<StandardGraduationRefCultivationTarget> graduationRefCultivationTargets, Map<Long, ConceptInstance> cultivationTargetMap, Map<Long, ConceptInstance> graduationMap) {
        List<RelationshipInstance> graduationRefCultivationTarget = Lists.newArrayList();
        for(StandardGraduationRefCultivationTarget standardGraduationRefCultivationTarget:graduationRefCultivationTargets){
            ConceptInstance graduationInstance = graduationMap.get(standardGraduationRefCultivationTarget.getGraduationId());
            ConceptInstance cultivationTargetInstance = cultivationTargetMap.get(standardGraduationRefCultivationTarget.getCultivationTargetId());
            if(ObjectUtils.isEmpty(graduationInstance) || ObjectUtils.isEmpty(cultivationTargetInstance)){
                continue;
            }
            RelationshipInstance relationshipInstance = new RelationshipInstance();
            relationshipInstance.setOntologyId(ontology.getId());
            relationshipInstance.setOntologyName(ontology.getName());

            relationshipInstance.setRelationshipId(cultivationTargetGraduation.getId());
            relationshipInstance.setRelationshipName(cultivationTargetGraduation.getName());

            relationshipInstance.setSourceConceptId(cultivationTarget.getId());
            relationshipInstance.setSourceConceptInstanceId(cultivationTargetInstance.getId());
            relationshipInstance.setSourceConceptInstanceName(cultivationTargetInstance.getName());

            relationshipInstance.setTargetConceptId(graduation.getId());
            relationshipInstance.setTargetConceptInstanceId(graduationInstance.getId());
            relationshipInstance.setTargetConceptInstanceName(graduationInstance.getName());
            graduationRefCultivationTarget.add(relationshipInstance);
        }
        return graduationRefCultivationTarget;
    }

    private List<RelationshipInstance> transCultivationRefGraduation(List<StandardCultivationRefGraduation> cultivationRefGraduations, Map<Long, ConceptInstance> graduationMap, Map<Long, ConceptInstance> cultivationMap) {
        List<RelationshipInstance> cultivationRefGraduation = Lists.newArrayList();
        for(StandardCultivationRefGraduation standardCultivationRefGraduation:cultivationRefGraduations){
            ConceptInstance graduationInstance = graduationMap.get(standardCultivationRefGraduation.getGraduationId());
            ConceptInstance cultivationInstance = cultivationMap.get(standardCultivationRefGraduation.getCultivationId());
            if(ObjectUtils.isEmpty(graduationInstance) || ObjectUtils.isEmpty(cultivationInstance)){
                continue;
            }
            RelationshipInstance relationshipInstance = new RelationshipInstance();
            relationshipInstance.setOntologyId(ontology.getId());
            relationshipInstance.setOntologyName(ontology.getName());

            relationshipInstance.setRelationshipId(graduationCultivation.getId());
            relationshipInstance.setRelationshipName(graduationCultivation.getName());

            relationshipInstance.setSourceConceptId(graduation.getId());
            relationshipInstance.setSourceConceptInstanceId(graduationInstance.getId());
            relationshipInstance.setSourceConceptInstanceName(graduationInstance.getName());

            relationshipInstance.setTargetConceptId(cultivation.getId());
            relationshipInstance.setTargetConceptInstanceId(cultivationInstance.getId());
            relationshipInstance.setTargetConceptInstanceName(cultivationInstance.getName());
            cultivationRefGraduation.add(relationshipInstance);
        }
        return cultivationRefGraduation;
    }

    private List<RelationshipInstance> transUnitRefStdCultivation(List<KnowledgeUnitRefStdCultivation> unitRefStdCultivations, Map<Long, ConceptInstance> unitMap, Map<Long, ConceptInstance> cultivationMap) {
        List<RelationshipInstance> unitRefStdCultivation = Lists.newArrayList();
        for(KnowledgeUnitRefStdCultivation knowledgeUnitRefStdCultivation:unitRefStdCultivations){
            ConceptInstance unitInstance = unitMap.get(knowledgeUnitRefStdCultivation.getUnitId());
            ConceptInstance cultivationInstance = cultivationMap.get(knowledgeUnitRefStdCultivation.getCultivationId());
            if(ObjectUtils.isEmpty(unitInstance) || ObjectUtils.isEmpty(cultivationInstance)){
                continue;
            }
            RelationshipInstance relationshipInstance = new RelationshipInstance();
            relationshipInstance.setOntologyId(ontology.getId());
            relationshipInstance.setOntologyName(ontology.getName());

            relationshipInstance.setRelationshipId(unitCultivation.getId());
            relationshipInstance.setRelationshipName(unitCultivation.getName());

            relationshipInstance.setSourceConceptId(unit.getId());
            relationshipInstance.setSourceConceptInstanceId(unitInstance.getId());
            relationshipInstance.setSourceConceptInstanceName(unitInstance.getName());

            relationshipInstance.setTargetConceptId(cultivation.getId());
            relationshipInstance.setTargetConceptInstanceId(cultivationInstance.getId());
            relationshipInstance.setTargetConceptInstanceName(cultivationInstance.getName());
            unitRefStdCultivation.add(relationshipInstance);
        }
        return unitRefStdCultivation;
    }

    private Map<Long,ConceptInstance> transCourse(List<Course> courses){
        Map<Long,ConceptInstance> courseMap = Maps.newHashMap();
        courses.stream().forEach(c -> {
            ConceptInstance conceptInstance = new ConceptInstance();
            conceptInstance.setName(c.getName());
            conceptInstance.setOntologyId(ontology.getId());
            conceptInstance.setOntologyName(ontology.getName());
            conceptInstance.setConceptId(course.getId());
            conceptInstance.setConceptName(course.getName());

            DataSet<ConceptInstance> courseData = remoteKgService.insertConceptInstance(conceptInstance);
            courseMap.put(c.getId(),courseData.getData());
        });
        return courseMap;
    }

    private Map<Long,ConceptInstance> transUnit(List<KnowledgeUnit> units){
        Map<Long,ConceptInstance> unitMap = Maps.newHashMap();
        units.stream().forEach(u -> {
            ConceptInstance conceptInstance = new ConceptInstance();
            conceptInstance.setName(u.getName());
            conceptInstance.setOntologyId(ontology.getId());
            conceptInstance.setOntologyName(ontology.getName());
            conceptInstance.setConceptId(unit.getId());
            conceptInstance.setConceptName(unit.getName());

            DataSet<ConceptInstance> unitData = remoteKgService.insertConceptInstance(conceptInstance);
            unitMap.put(u.getId(),unitData.getData());
        });
        return unitMap;
    }

    private Map<Long,ConceptInstance> transPoint(List<KnowledgePoint> points){
        Map<Long,ConceptInstance> pointMap = Maps.newHashMap();
        points.stream().forEach(p -> {
            ConceptInstance conceptInstance = new ConceptInstance();
            conceptInstance.setName(p.getName());
            conceptInstance.setOntologyId(ontology.getId());
            conceptInstance.setOntologyName(ontology.getName());
            conceptInstance.setConceptId(point.getId());
            conceptInstance.setConceptName(point.getName());

            DataSet<ConceptInstance> pointData = remoteKgService.insertConceptInstance(conceptInstance);
            pointMap.put(p.getId(),pointData.getData());
        });
        return pointMap;
    }

    private List<RelationshipInstance> transCourseRefUnit(List<CourseRefKeUnit> courseRefKeUnits, Map<Long,ConceptInstance> courseMap,Map<Long,ConceptInstance> unitMap){
        List<RelationshipInstance> courseRefUnits = Lists.newArrayList();
        for(CourseRefKeUnit courseRefKeUnit:courseRefKeUnits){
            ConceptInstance courseInstance = courseMap.get(courseRefKeUnit.getCourseId());
            ConceptInstance unitInstance = unitMap.get(courseRefKeUnit.getUnitId());
            if(ObjectUtils.isEmpty(unitInstance) || ObjectUtils.isEmpty(courseInstance)){
                continue;
            }
            RelationshipInstance relationshipInstance = new RelationshipInstance();
            relationshipInstance.setOntologyId(ontology.getId());
            relationshipInstance.setOntologyName(ontology.getName());
            relationshipInstance.setRelationshipId(courseUnit.getId());
            relationshipInstance.setRelationshipName(courseUnit.getName());
            relationshipInstance.setSourceConceptId(course.getId());
            relationshipInstance.setSourceConceptInstanceId(courseInstance.getId());
            relationshipInstance.setSourceConceptInstanceName(courseInstance.getName());
            relationshipInstance.setTargetConceptId(unit.getId());
            relationshipInstance.setTargetConceptInstanceId(unitInstance.getId());
            relationshipInstance.setTargetConceptInstanceName(unitInstance.getName());
            courseRefUnits.add(relationshipInstance);
        }
        return courseRefUnits;
    }

    private List<RelationshipInstance> transUnitRefPoint(List<KnowledgeUnitRefPoint> unitRefPoints, Map<Long,ConceptInstance> unitMap,Map<Long,ConceptInstance> pointMap){
        List<RelationshipInstance> unitRefPoint = Lists.newArrayList();
        for(KnowledgeUnitRefPoint knowledgeUnitRefPoint:unitRefPoints){
            ConceptInstance unitInstance = unitMap.get(knowledgeUnitRefPoint.getUnitId());
            ConceptInstance pointInstance = pointMap.get(knowledgeUnitRefPoint.getPointId());
            if(ObjectUtils.isEmpty(unitInstance) || ObjectUtils.isEmpty(pointInstance)){
                continue;
            }
            RelationshipInstance relationshipInstance = new RelationshipInstance();
            relationshipInstance.setOntologyId(ontology.getId());
            relationshipInstance.setOntologyName(ontology.getName());
            relationshipInstance.setRelationshipId(unitPoint.getId());
            relationshipInstance.setRelationshipName(unitPoint.getName());
            relationshipInstance.setSourceConceptId(unit.getId());
            relationshipInstance.setSourceConceptInstanceId(unitInstance.getId());
            relationshipInstance.setSourceConceptInstanceName(unitInstance.getName());
            relationshipInstance.setTargetConceptId(point.getId());
            relationshipInstance.setTargetConceptInstanceId(pointInstance.getId());
            relationshipInstance.setTargetConceptInstanceName(pointInstance.getName());
            unitRefPoint.add(relationshipInstance);
        }
        return unitRefPoint;
    }




}
