package com.agileai.dataparser.service;

import com.agileai.dataparser.constant.CurriculumSystemConstants;
import com.agileai.dataparser.constant.DataMapConstant;
import com.agileai.dataparser.domain.*;
import com.agileai.dataparser.mapper.*;
import com.agileai.dataparser.utils.MongodbUtils;
import com.alibaba.fastjson2.JSONObject;
import com.doinner.common.core.domain.DataSet;
import com.doinner.csys.dao.*;
import com.doinner.csys.domain.*;
import com.doinner.csys.domain.vo.CourseTeachingPracticePlanVo;
import com.doinner.csys.domain.vo.CourseTeachingTheoryPlanVo;
import com.doinner.csys.utils.MultipartFileUtils;
import com.doinner.file.api.constant.DomainFieldConstants;
import com.doinner.file.api.domain.FileInfo;
import com.doinner.file.api.service.RemoteFileInfoService;
import com.google.common.collect.Lists;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.gridfs.model.GridFSFile;
import com.mongodb.client.model.Filters;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.InputStream;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.mongodb.client.model.Filters.eq;

/**
 * @author wzg
 * @date 2023/3/28 20:36
 */
@Service
public class TempCourseService {
    private static final Logger log = LoggerFactory.getLogger(TempCourseService.class);
    @Resource
    private CurTeachingPlanningBookMapper curTeachingPlanningBookMapper;
    @Resource
    private CurTeachingPlanningMainMapper curTeachingPlanningMainMapper;
    @Resource
    private CurTeachingPlanningPracticeMapper curTeachingPlanningPracticeMapper;
    @Resource
    private CurTeachingPlanningReviewMapper curTeachingPlanningReviewMapper;
    @Resource
    private CurTeachingPlanningTheoryChapterMapper curTeachingPlanningTheoryChapterMapper;
    @Resource
    private CurTeachingPlanningTheoryMapper curTeachingPlanningTheoryMapper;
    private MongodbUtils mongodbUtils;

    @Value("${mongodb.url}")
    private String mongoUrl;

    @Value("${mongodb.database}")
    private String mongoDatabase;

    private MongoCollection<Document> ontologyInstance;

    @Resource
    protected CourseMapper courseMapper;
    @Resource
    private CourseTeachingTheoryPlanMapper courseTeachingTheoryPlanMapper;
    @Resource
    private CourseTeachingPracticePlanMapper courseTeachingPracticePlanMapper;
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
    private MongoGridFSRepository gridFSRepository;
    @Resource
    private RemoteFileInfoService remoteFileInfoService;
//    @Resource
//    private MongodbUtils mongodbUtils;

    @Transactional(rollbackFor = {Exception.class})
    public void batchCourse(){
        mongodbUtils = new MongodbUtils(mongoUrl, mongoDatabase);
        ontologyInstance = mongodbUtils.getDBCollection("OntologyInstance");
        //CurTeachingPlanningMain param = new CurTeachingPlanningMain();
        //param.setId(6431L);
        //List<CurTeachingPlanningMain> curTeachingPlanningMains = curTeachingPlanningMainMapper.selectCurTeachingPlanningMainList(param);
        List<CurTeachingPlanningMain> curTeachingPlanningMains = curTeachingPlanningMainMapper.selectCurTeachingPlanningMainList(null);
        for (CurTeachingPlanningMain curTeachingPlanningMain : curTeachingPlanningMains) {
            log.info("课程："+curTeachingPlanningMain.getCurName()+"开始");
            CurTeachingPlanningPractice practice = curTeachingPlanningPracticeMapper.selectCurTeachingPlanningPracticeByMainId(curTeachingPlanningMain.getId());
            CurTeachingPlanningTheory theory = curTeachingPlanningTheoryMapper.selectCurTeachingPlanningTheoryByMainId(curTeachingPlanningMain.getId());
            Course course = new Course();
            course.setName(curTeachingPlanningMain.getCurName());
            course.setCode(curTeachingPlanningMain.getCurCode());
            if (StringUtils.isNotBlank(curTeachingPlanningMain.getCurType())){
                course.setType(Integer.parseInt(curTeachingPlanningMain.getCurType())+"");
            }

            if (StringUtils.isNotBlank(curTeachingPlanningMain.getReviewCollege())){
                course.setCollegeId(Long.valueOf(curTeachingPlanningMain.getReviewCollege()));
            }
            course.setBeforeCourseId(curTeachingPlanningMain.getCurPre());
            course.setAfterCourseId(curTeachingPlanningMain.getCurFollow());
            course.setAuthors(curTeachingPlanningMain.getWritingName());
            if (StringUtils.isNotBlank(curTeachingPlanningMain.getCurAttribute())){
                //课程属性：1，必修；2，选修；3，公选；4，限选
                course.setCourseAttr(this.getCourseAttrByName(curTeachingPlanningMain.getCurAttribute())+"");
            }
            if (StringUtils.isNotBlank(curTeachingPlanningMain.getNature())){
                course.setCourseProp(this.getCoursePropByName(curTeachingPlanningMain.getNature()));
            }
            if (StringUtils.isNotBlank(curTeachingPlanningMain.getCurCategory())){
                course.setCourseType(getCourseTypeByName(curTeachingPlanningMain.getCurCategory()));
            }
            if (ObjectUtils.isNotEmpty(practice) && StringUtils.isNotBlank(practice.getTeachCollege())){
                course.setTeachCollegeId(Long.valueOf(practice.getTeachCollege()));
            }
            course.setEnName(curTeachingPlanningMain.getCurNameEn());
            course.setHours( getInteger(curTeachingPlanningMain.getHours()));
            course.setTheoryHours( getInteger(curTeachingPlanningMain.getHoursTh()));
            course.setPracticeHours( getInteger(curTeachingPlanningMain.getHoursPa()));
            course.setWeekHours( getInteger(curTeachingPlanningMain.getHoursWeek()));
            course.setTeachHours( getInteger(curTeachingPlanningMain.getHoursTeach()));
            course.setExamineHours( getInteger(curTeachingPlanningMain.getHoursExam()));
            course.setOtherHours( getInteger(curTeachingPlanningMain.getHoursOther()));
            course.setHoursUnit(curTeachingPlanningMain.getHoursUnit());
            course.setCredit( getInteger(curTeachingPlanningMain.getScore()));
            //课程类型：1，理论；2，实践  practice
            if ("1".equals(curTeachingPlanningMain.getCurType())){

            }else {
                course.setLocation(practice.getAddress());
            }
            course.setOpenTerm(curTeachingPlanningMain.getTerm());
            course.setSummary(curTeachingPlanningMain.getBrief());
            course.setFileId(curTeachingPlanningMain.getFileId());
            course.setFileName(curTeachingPlanningMain.getFileName());
            if ("1".equals(curTeachingPlanningMain.getStatus())){
                course.setStatus(0);
            }else {
                course.setStatus(1);
            }
            if ("1".equals(curTeachingPlanningMain.getAnalysisStatus())){
                course.setAnalysisStatus(0);
            }else {
                course.setAnalysisStatus(1);
            }
            //TODO 创建部门
            if (StringUtils.isNotBlank(curTeachingPlanningMain.getCollege())){
                course.setDeptBy(Long.valueOf(curTeachingPlanningMain.getCollege()));
            }
            course.setCreator(curTeachingPlanningMain.getCreateBy());
            course.setLastModifier(curTeachingPlanningMain.getUpdateBy());
            if (curTeachingPlanningMain.getCurCode()==null){
                course.setCode("");
            }
            courseMapper.insertCourse(course);
            Document ontologyInstanceQuery = new Document(Map.of("cfgId", CurriculumSystemConstants.CURRICULUM_SYSTEM_ID,
                    "cfg", false, "name", course.getName()));
            FindIterable find = ontologyInstance.find(ontologyInstanceQuery);
            if(ObjectUtils.isNotEmpty(find)){
                Document first = ontologyInstance.find(ontologyInstanceQuery).first();
                if(ObjectUtils.isNotEmpty(first) && !first.isEmpty()){
                    DataMapConstant.courseIdMap.put(first.getObjectId("_id"), course.getId());
                }
            }
            //  课程类型：1，理论  theory；2，实践 Practice
            CourseTeachingPracticePlan  courseTeachingPracticePlan = new CourseTeachingPracticePlan();
            courseTeachingPracticePlan.setCourseId(course.getId());
            if("2".equals(curTeachingPlanningMain.getCurType())){
                if (practice!=null){
                    courseTeachingPracticePlan.setSuit(practice.getObject());
                    courseTeachingPracticePlan.setTaskDescribe(practice.getBackGround());
                    courseTeachingPracticePlan.setKnowLevel(practice.getPraKnowledgeTarget());
                    courseTeachingPracticePlan.setAbilityLevel(practice.getPraAbilityTarget());
                    courseTeachingPracticePlan.setPoliticsLevel(practice.getPraIdeoPoliTarget());
                    courseTeachingPracticePlan.setContent(practice.getContent());
                    courseTeachingPracticePlan.setOverview(practice.getUnitDesc());
                    courseTeachingPracticePlan.setPracticeMethod(practice.getPraType());
                    if (StringUtils.isNotBlank(practice.getCheck())){
                        courseTeachingPracticePlan.setExaMethod(this.getExaMethodByName(practice.getCheck())+"");
                    }
                    //TODO 组织方式
                    //courseTeachingPracticePlan.setOrgMethod(practice.get);
                    //TODO 成绩评定
                    //courseTeachingPracticePlan.setPerformance(practice.get);
                    //TODO 计分标准
                    //courseTeachingPracticePlan.setStandard(practice.get);
                    courseTeachingPracticePlan.setStandardJson(practice.getJudge());
                    courseTeachingPracticePlan.setAsk(practice.getRequireOf());
                    //TODO 事件和地点安排
                    //courseTeachingPracticePlan.setArrange(practice.get());
                }
            }
            courseTeachingPracticePlanMapper.insertCourseTeachingPracticePlan(courseTeachingPracticePlan);
            //理论教学计划
            CourseTeachingTheoryPlan courseTeachingTheoryPlan =new CourseTeachingTheoryPlan();
            courseTeachingTheoryPlan.setCourseId(course.getId());
            if("1".equals(curTeachingPlanningMain.getCurType())){
                if (theory != null){
                    courseTeachingTheoryPlan.setNatureLevel(theory.getCurNatureStatus());
                    courseTeachingTheoryPlan.setKnowLevel(theory.getCurKnowledgeTarget());
                    courseTeachingTheoryPlan.setAbilityLevel(theory.getCurAbilityTarget());
                    courseTeachingTheoryPlan.setPoliticsLevel(theory.getCurIdeoPoliTarget());
                    courseTeachingTheoryPlan.setTeachingMethod(theory.getTeachMethod());
                    if (StringUtils.isNotBlank(theory.getCheckType())){
                        courseTeachingTheoryPlan.setExaMethod(this.getExaMethodByName(theory.getCheckType()));
                    }
                    courseTeachingTheoryPlan.setOrgMethod(theory.getExamType());
                    courseTeachingTheoryPlan.setPerformance(theory.getAchievement());
                    courseTeachingTheoryPlan.setStandard(theory.getScoreStandard());
                    // TODO 评分标志json(standard_json)
                    //courseTeachingTheoryPlan.setStandardJson(theory.getScoreStandard());
                }
            }
            List<CurTeachingPlanningBook> books = curTeachingPlanningBookMapper.selectCurTeachingPlanningBookByMainId(course.getId());
            if (CollectionUtils.isNotEmpty(books)){
                for (CurTeachingPlanningBook oldBook : books) {
                    CourseTextbook book = new CourseTextbook();
                    book.setCourseId(course.getId());
                    book.setName(oldBook.getName());
                    if (StringUtils.isNotBlank(oldBook.getType())){
                        book.setType(Integer.parseInt(oldBook.getType()));
                    }
                    book.setAuthor(oldBook.getAutherName());
                    book.setPressName(oldBook.getPubName());
                    book.setPressVersion(oldBook.getVersion());
                    book.setPressTime(oldBook.getPubTime());
                    courseTextbookMapper.insertCourseTextbook(book);
                }
            }
            List<CurTeachingPlanningTheoryChapter> oldList = curTeachingPlanningTheoryChapterMapper.selectCurTeachingPlanningTheoryChapterByMainId(curTeachingPlanningMain.getId());
            List<CurTeachingPlanningTheoryChapter> chapterList =new ArrayList<>();
            if (CollectionUtils.isNotEmpty(oldList)){
                List<CurTeachingPlanningTheoryChapter> oldDomainList =new ArrayList<>();
                List<CurTeachingPlanningTheoryChapter> oldUnitList =new ArrayList<>();

                for (CurTeachingPlanningTheoryChapter curTeachingPlanningTheoryChapter : oldList) {
                    if ("1".equals(curTeachingPlanningTheoryChapter.getType())){
                        oldDomainList.add(curTeachingPlanningTheoryChapter);
                    }else if ("2".equals(curTeachingPlanningTheoryChapter.getType())){
                        oldUnitList.add(curTeachingPlanningTheoryChapter);
                    }else if ("4".equals(curTeachingPlanningTheoryChapter.getType())){
                        chapterList.add(curTeachingPlanningTheoryChapter);
                    }
                }
                //领域对象
                if (CollectionUtils.isNotEmpty(oldDomainList)){
                    for (CurTeachingPlanningTheoryChapter oldDomain : oldDomainList) {
                        KnowledgeDomain knowledgeDomain = new KnowledgeDomain();
                        knowledgeDomain.setCourseId(course.getId());
                        if (StringUtils.isBlank(oldDomain.getName())){
                            knowledgeDomain.setName("领域");
                        }else {
                            knowledgeDomain.setName(oldDomain.getName());
                        }

                        String unitIds = "";
                        //知识单元对象
                        if (CollectionUtils.isNotEmpty(oldUnitList)){
                            for (CurTeachingPlanningTheoryChapter oldUnit : oldUnitList) {
                                if (oldUnit.getParentId().equals(oldDomain.getId())){
                                    KnowledgeUnit knowledgeUnit = new KnowledgeUnit();
                                    knowledgeUnit.setInitLevel(this.getLevelByName(oldUnit.getMasterBeginLv()));
                                    knowledgeUnit.setRequireLevel(this.getLevelByName(oldUnit.getMasterRequireLv()));
                                    if (StringUtils.isBlank(oldUnit.getName())){
                                        knowledgeUnit.setName("知识单元");
                                    }else {
                                        knowledgeUnit.setName(oldUnit.getName());
                                    }
                                    knowledgeUnit.setLearnTarget(oldUnit.getLearnTarget());
                                    knowledgeUnit.setRealizeLink(oldUnit.getRealizeLink());

                                    knowledgeUnitMapper.insertKnowledgeUnit(knowledgeUnit);
                                    //知识点
                                    if(StringUtils.isNotBlank(oldUnit.getContent())){
                                        List<String> pointList = this.getList(oldUnit.getContent());
                                        for (String point : pointList) {
                                            KnowledgePoint knowledgePoint = new KnowledgePoint();
                                            knowledgePoint.setName(point);
                                            knowledgePointMapper.insertKnowledgePoint(knowledgePoint);
                                            //关联表
                                            KnowledgeUnitRefPoint knowledgeUnitRefPoint = new KnowledgeUnitRefPoint();
                                            knowledgeUnitRefPoint.setUnitId(knowledgeUnit.getId());
                                            knowledgeUnitRefPoint.setPointId(knowledgePoint.getId());
                                            knowledgeUnitRefPointMapper.insertKnowledgeUnitRefPoint(knowledgeUnitRefPoint);
                                        }
                                    }
                                    unitIds+=knowledgeUnit.getId()+",";
                                    //课程和知识单元关联
                                    CourseRefKeUnit courseRefKeUnit = new CourseRefKeUnit();
                                    courseRefKeUnit.setUnitId(knowledgeUnit.getId());
                                    courseRefKeUnit.setCourseId(course.getId());
                                    courseRefKeUnitMapper.insertCourseRefKeUnit(courseRefKeUnit);
                                }

                            }
                        }
                        if (unitIds.length() > 1) {
                            knowledgeDomain.setUnitIds(unitIds.substring(0, unitIds.length() - 1));
                        } else {
                            knowledgeDomain.setUnitIds(unitIds);
                        }
                        knowledgeDomainMapper.insertKnowledgeDomain(knowledgeDomain);
                    }
                }
            }
            //   章节信息
            if (CollectionUtils.isNotEmpty(chapterList)){
                courseTeachingTheoryPlan.setContentJson(chapterList.get(0).getContent());
            }
            courseTeachingTheoryPlanMapper.insertCourseTeachingTheoryPlan(courseTeachingTheoryPlan);
            log.info("课程："+curTeachingPlanningMain.getCurName()+"结束");
        }
        log.info("批量迁移数据成功");
    }


    @Transactional(rollbackFor = {Exception.class})
    public void batchUpdateCourse(){
        List<Course> courses = courseMapper.selectCourseList(null);
        courses.stream().forEach(course -> {
            log.info("课程："+course.getName()+"更新开始");
            CurTeachingPlanningMain main = new CurTeachingPlanningMain();
            main.setCurName(course.getName());
            List<CurTeachingPlanningMain> list = curTeachingPlanningMainMapper.selectCurTeachingPlanningMainList(main);
            if(list.size() != 1){
                return;
            }
            CurTeachingPlanningMain result = list.get(0);

            CurTeachingPlanningTheory theory = curTeachingPlanningTheoryMapper.selectCurTeachingPlanningTheoryByMainId(result.getId());
            CourseTeachingTheoryPlan theoryPlan = courseTeachingTheoryPlanMapper.selectCourseTeachingTheoryPlanByCourseId(course.getId());
            if(ObjectUtils.isNotEmpty(theory) && ObjectUtils.isNotEmpty(theoryPlan)){
                theoryPlan.setStandardJson(theory.getElseOther());
                courseTeachingTheoryPlanMapper.updateCourseTeachingTheoryPlan(theoryPlan);
            }

            List<CurTeachingPlanningTheoryChapter> theoryChapters = curTeachingPlanningTheoryChapterMapper.selectCurTeachingPlanningTheoryChapterByMainId(result.getId());
            List<KnowledgeUnit> units = knowledgeUnitMapper.selectKnowledgeUnitListByCourseId(course.getId());
            if(CollectionUtils.isNotEmpty(theoryChapters) && CollectionUtils.isNotEmpty(units)){
                for(CurTeachingPlanningTheoryChapter theoryChapter:theoryChapters){
                    if(!"2".equals(theoryChapter.getType()) || ObjectUtils.isEmpty(theoryChapter.getMasterBegin())
                            || ObjectUtils.isEmpty(theoryChapter.getMasterRequire())){
                        continue;
                    }
                    for(KnowledgeUnit unit:units){
                        if(StringUtils.isNotBlank(theoryChapter.getName()) && theoryChapter.getName().equals(unit.getName())){
                            if(ObjectUtils.isNotEmpty(theoryChapter.getMasterBegin())){
                                if("180".equals(theoryChapter.getMasterBegin())){
                                    unit.setInitLevel(1);
                                }
                                if("181".equals(theoryChapter.getMasterBegin())){
                                    unit.setInitLevel(2);
                                }
                                if("182".equals(theoryChapter.getMasterBegin())){
                                    unit.setInitLevel(3);
                                }
                                if("183".equals(theoryChapter.getMasterBegin())){
                                    unit.setInitLevel(4);
                                }
                                if("184".equals(theoryChapter.getMasterBegin())){
                                    unit.setInitLevel(5);
                                }
                                if("185".equals(theoryChapter.getMasterBegin())){
                                    unit.setInitLevel(6);
                                }
                            }
                            if(ObjectUtils.isNotEmpty(theoryChapter.getMasterRequire())){
                                if("180".equals(theoryChapter.getMasterRequire())){
                                    unit.setRequireLevel(1);
                                }
                                if("181".equals(theoryChapter.getMasterRequire())){
                                    unit.setRequireLevel(2);
                                }
                                if("182".equals(theoryChapter.getMasterRequire())){
                                    unit.setRequireLevel(3);
                                }
                                if("183".equals(theoryChapter.getMasterRequire())){
                                    unit.setRequireLevel(4);
                                }
                                if("184".equals(theoryChapter.getMasterRequire())){
                                    unit.setRequireLevel(5);
                                }
                                if("185".equals(theoryChapter.getMasterRequire())){
                                    unit.setRequireLevel(6);
                                }
                            }
                            knowledgeUnitMapper.updateKnowledgeUnit(unit);
                        }
                    }
                }
            }
            log.info("课程："+course.getName()+"更新结束");
        });
        log.info("课程更新完成！！！");
    }

    private Integer getLevelByName(String s){
        if (StringUtils.isNotBlank(s)){
            if ("L0（无）".equals(s)){
                return 0;
            }else if("L1（记忆/回忆）".equals(s)){
                return 1;
            }else if("L2（理解）".equals(s)){
                return 2;
            }else if("L3（应用）".equals(s)){
                return 3;
            }else if("L4（分析）".equals(s)){
                return 4;
            }else if("L5（评价）".equals(s)){
                return 5;
            }else if("L6（创造）".equals(s)){
                return 6;
            }
        }
        return null;
    }


    private Integer getExaMethodByName(String s){
        if (StringUtils.isNotBlank(s)){
            if ("考查".equals(s)){
                return 1;
            }else if("考试".equals(s)){
                return 2;
            }else if("其他".equals(s)){
                return 3;
            }
        }
        return null;
    }

    private Integer getCoursePropByName(String s){
        if (StringUtils.isNotBlank(s)){
            if ("公共基础课".equals(s)){
                return 1;
            }else if("学科基础课".equals(s)){
                return 2;
            }else if("专业基础课".equals(s)){
                return 3;
            }else if("实践教学环节课".equals(s)){
                return 4;
            }else if("自修课".equals(s)){
                return 5;
            }else if("公共选修课".equals(s)){
                return 6;
            }else if("公共必修课".equals(s)){
                return 7;
            }else if("专业选修课".equals(s)){
                return 8;
            }else if("专业必修课".equals(s)){
                return 9;
            }
        }
        return null;
    }

    /** 课程大类：1，理论教学类；2，实验教学类；3，体育课；4，军体野外教学类；5，实践环节；6，毕业实习；7，素质课；8，外语教学类；9，毕业设计；10，出国成绩 */
    private Integer getCourseTypeByName(String s){
        if ("理论教学类".equals(s)){
            return 1;
        }else if("实验教学类".equals(s)){
            return 2;
        }else if("体育课".equals(s)){
            return 3;
        }else if("军体野外教学类".equals(s)){
            return 4;
        }else if("实践环节".equals(s)){
            return 5;
        }else if("毕业实习".equals(s)){
            return 6;
        }else if("素质课".equals(s)){
            return 7;
        }else if("外语教学类".equals(s)){
            return 8;
        }else if("毕业设计".equals(s)){
            return 9;
        }else if("出国成绩".equals(s)){
            return 10;
        }
        return null;
    }


    //课程属性：1，必修；2，选修；3，公选；4，限选
    private Integer getCourseAttrByName(String s){
        if ("必修".equals(s)){
            return 1;
        }else if("选修".equals(s)){
            return 2;
        }else if("公选".equals(s)){
            return 3;
        }else if("限选".equals(s)){
            return 4;
        }
        return null;
    }

    private List<String> getList(String points) {
        String[] split = points.split("@");
        List<String> courseIdList = new ArrayList<>();
        for (String s : split) {
            courseIdList.add(s);
        }
        return courseIdList;
    }
    private Double getInteger(String s){
        if (s!=null ){
            String regEx = "[^0-9]";
            Pattern p = Pattern.compile(regEx);
            Matcher m = p.matcher(s);
            String result = m.replaceAll("").trim();
            if (StringUtils.isNotBlank(result)){
                return (double)Integer.parseInt(result);
            }
        }
        return null;
    }

    public static void main(String[] args) {
        String str = "12java3";
        String regEx = "[^0-9]";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(str);
        String result = m.replaceAll("").trim();
        System.out.println(result);
    }


   /* public void batchCourse(){
        Document ontologyInstance = new Document();
        ontologyInstance.put("name","wzg");
        ExampleMatcher exampleMatcher = ExampleMatcher.matching()
                .withMatcher("name",ExampleMatcher.GenericPropertyMatchers.contains());
        Example<Document> of = Example.of(ontologyInstance, exampleMatcher);
        MongoCollection<Document> conceptInstanceCollection= mongodbUtils.getDBCollection("ConceptInstance");
        Bson filter = Filters.eq("name","大学14");

        //FindIterable<Document> results =  conceptInstanceCollection.find(eq("name", "大学14"));
        FindIterable<Document> results =  conceptInstanceCollection.find(filter);
        for (Document row:results){
            System.out.println("_id:"+row.get("_id"));
            System.out.println("name:"+row.get("name"));
        }

    }*/
}
