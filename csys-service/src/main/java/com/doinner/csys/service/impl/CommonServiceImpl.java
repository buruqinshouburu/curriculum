package com.doinner.csys.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.doinner.common.core.domain.DataSet;
import com.doinner.common.core.exception.DataFormatException;
import com.doinner.common.core.utils.uuid.UUID;
import com.doinner.csys.constant.CourseConstant;
import com.doinner.csys.constant.CourseTextbookConstant;
import com.doinner.csys.constant.DomainFieldConstant;
import com.doinner.csys.dao.CourseMapper;
import com.doinner.csys.dao.CourseTeachingPracticePlanMapper;
import com.doinner.csys.dao.CourseTeachingTheoryPlanMapper;
import com.doinner.csys.dao.StandardMajorMapper;
import com.doinner.csys.domain.Course;
import com.doinner.csys.domain.CourseTeachingPracticePlan;
import com.doinner.csys.domain.CourseTeachingTheoryPlan;
import com.doinner.csys.domain.StandardMajor;
import com.doinner.csys.domain.vo.*;
import com.doinner.csys.service.CommonService;
import com.doinner.csys.service.CurriculumService;
import com.doinner.csys.utils.*;
import com.doinner.file.api.domain.FileInfo;
import com.doinner.file.api.service.RemoteFileInfoService;
import com.doinner.system.domain.custom.CustomDept;
import com.doinner.system.domain.entity.SysDept;
import com.doinner.system.domain.entity.SysDictData;
import com.doinner.system.service.DoinnerDeptService;
import com.doinner.system.service.DoinnerDictDataService;
import com.google.common.collect.Lists;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.xwpf.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CommonServiceImpl implements CommonService {

    private static final Logger log = LoggerFactory.getLogger(CommonServiceImpl.class);

    @Resource
    private RemoteFileInfoService remoteFileInfoService;

    @Resource
    private DoinnerDictDataService doinnerDictDataService;

    @Resource
    private CourseMapper courseMapper;

    @Resource
    private CourseTeachingTheoryPlanMapper theoryPlanMapper;

    @Resource
    private CourseTeachingPracticePlanMapper practicePlanMapper;

    @Resource
    private DoinnerDeptService doinnerDeptService;

    @Resource
    private StandardMajorMapper standardMajorMapper;

    @Resource
    private CurriculumService curriculumService;

    @Override
    @Transactional(rollbackFor = {Exception.class})
    public StandardMajor addStandardMajor(StandardMajor standardMajor) {
        StandardMajor parentStandardMajor = null;
        if (standardMajor.getParentId() == null){
            standardMajor.setParentId(-1L);
            standardMajor.setLevel(1L);
        }else {
            parentStandardMajor = standardMajorMapper.selectStandardMajorById(standardMajor.getParentId());
            standardMajor.setLevel(parentStandardMajor.getLevel() + 1);
        }
        standardMajor.setLeaf(1);
        UserUtils.reflash(standardMajor);
        standardMajorMapper.insertStandardMajor(standardMajor);
        // 维护父级的层级
        if (parentStandardMajor != null){
            parentStandardMajor.setLeaf(0);
            standardMajorMapper.updateStandardMajor(parentStandardMajor);
        }
        return standardMajor;
    }

    @Override
    @Transactional(rollbackFor = {Exception.class})
    public StandardMajor updateStandardMajor(StandardMajor standardMajor) {
        UserUtils.reflash(standardMajor);
        standardMajorMapper.updateStandardMajor(standardMajor);
        return standardMajor;
    }

    @Override
    @Transactional(rollbackFor = {Exception.class})
    public void deleteStandardMajor(Long id) {
        StandardMajor standardMajor = standardMajorMapper.selectStandardMajorById(id);
        List<StandardMajor> standardMajors = standardMajorMapper.selectStandardMajorByParentId(standardMajor.getParentId());
        if (standardMajors.size() == 1){
            StandardMajor parentStandardMajor = standardMajorMapper.selectStandardMajorById(standardMajor.getParentId());
            parentStandardMajor.setLeaf(1);
            standardMajorMapper.updateStandardMajor(parentStandardMajor);
        }
        standardMajorMapper.deleteStandardMajorByParentId(id);
        standardMajorMapper.deleteStandardMajorById(id);
    }

    @Override
    public List<StandardMajor> selectStandardMajorListByCollegeId(Long collegeId) {
        StandardMajor query = new StandardMajor();
        query.setCollegeId(collegeId);
        query.setParentId(-1L);
        List<StandardMajor> standardMajors = standardMajorMapper.selectStandardMajorList(query);
        for (StandardMajor standardMajor : standardMajors) {
            CustomDept sysDept = new CustomDept();
            List<SysDept> list = doinnerDeptService.list(sysDept).getData();
            Map<Long, String> deptIdNameMap = list.parallelStream().collect(Collectors.toMap(SysDept::getDeptId,SysDept::getDeptName));
            standardMajor.setCollegeName(deptIdNameMap.get(standardMajor.getCollegeId()));
        }
        return standardMajors;
    }

    @Override
    public Page<StandardMajor> treeSubMajorList(StandardMajor standardMajor) {
        List<StandardMajor> standardMajors = standardMajorMapper.selectStandardMajorAccurate(standardMajor);
        if (CollectionUtils.isNotEmpty(standardMajors)){
            List<StandardMajor> treeStandardMajors = TreeBuilderUtils.buildRootTree(standardMajors);
            Page<StandardMajor> page = PaginationUtils.getPage(treeStandardMajors, standardMajor.getPageNum() == null ? 1 : standardMajor.getPageNum(), standardMajor.getPageSize() == null ? 20 : standardMajor.getPageSize());
            return page;
        }
        return new PageImpl<>(new ArrayList<>(), Pageable.unpaged(), 0);
    }

    @Override
    public List<StandardMajor> selectStandardMajorListByParentId(Long parentId) {
        StandardMajor query = new StandardMajor();
        query.setParentId(parentId);
        return standardMajorMapper.selectStandardMajorList(query);
    }

    @Override
    @Transactional(rollbackFor = {Exception.class})
    public String analysisTeachPlan(String id, Long courseId) {
        try{
            XWPFDocument document = getXWPFDocument(id);
            List<XWPFParagraph> paragraphs = document.getParagraphs();
            CourseVo course = getCourse(courseId);

            //解析表格内容
            List<XWPFTable> tables = document.getTables();
            if(!validateTable(tables)){
                log.error("表格格式或内容不正确，请确认！");
                throw new DataFormatException("表格格式或内容不正确，请确认！");
            }
            List<CourseChapterVo> courseChapters = Lists.newArrayList();
            String arrayStr = analysisTable(tables,courseId,courseChapters);
            course.getCourseTeachingTheoryPlanVo().setContentJson(arrayStr);
            course.setCourseChapterVoList(courseChapters);
            List<CourseTextbookVo> books = analysisBookTable(tables,courseId);
            course.setCourseTextbookVoList(books);

            //课程性质地位内容
            List<XWPFParagraph> curNatureStatusContents = Lists.newArrayList();
            boolean isCurNatureStatusContent = false;
            //课程内容简介内容
            List<XWPFParagraph> briefContents = Lists.newArrayList();
            boolean isBriefContent = false;
            //课程知识目标内容
            List<XWPFParagraph> curKnowledgeTargetContents = Lists.newArrayList();
            boolean isCurKnowledgeTargetContent = false;
            //课程能力目标内容
            List<XWPFParagraph> curAbilityTargetContents = Lists.newArrayList();
            boolean isCurAbilityTargetContent = false;
            //课程思政目标内容
            List<XWPFParagraph> curIdeoPoliTargetContents = Lists.newArrayList();
            boolean isCurIdeoPoliTargetContent = false;
            //教学方法内容
            List<XWPFParagraph> teachMethodContents = Lists.newArrayList();
            boolean isTeachMethodContent = false;
            //课程学习目标和学习实现环节
            List<XWPFParagraph> studyContents = Lists.newArrayList();
            boolean isStudyContent = false;

            for(XWPFParagraph paragraph:paragraphs){
                List<XWPFRun> runs = paragraph.getRuns();
                if(CollectionUtils.isEmpty(runs)){
                    continue;
                }
                String paragraphString = doRunsContents(runs);
                if(paragraphString.contains("：")){
                    paragraphString = paragraphString.replace("：",":");
                }
                if(paragraphString.contains("，")){
                    paragraphString = paragraphString.replace("，",",");
                }
                if(paragraphString.contains("执笔人:") && paragraphString.contains("审阅学院:")){
                    String writingName = paragraphString.substring(paragraphString.indexOf("执笔人:")+4,paragraphString.indexOf("审阅学院:"));
                    course.setAuthors(writingName);
                    String reviewCollege = paragraphString.substring(paragraphString.indexOf("审阅学院:")+5,paragraphString.indexOf("）"));
                    CustomDept query = new CustomDept();
                    query.setDeptName(reviewCollege);
                    List<SysDept> result = doinnerDeptService.list(query).getData();
                    if(!CollectionUtils.isEmpty(result)){
                        course.setCollegeId(result.get(0).getDeptId());
                    }
                }else{
                    if(paragraphString.contains("执笔人:")){
                        String[] writingName = paragraphString.split(":");
                        if(writingName.length>1){
                            course.setAuthors(writingName[1]);
                        }
                    }
                    if(paragraphString.contains("审阅学院:")){
                        String[] reviewCollege = paragraphString.split(":");
                        if(reviewCollege.length>1){
                            CustomDept query = new CustomDept();
                            query.setDeptName(reviewCollege[1].trim());
                            List<SysDept> result = doinnerDeptService.list(query).getData();
                            if(!CollectionUtils.isEmpty(result)){
                                course.setCollegeId(result.get(0).getDeptId());
                            }
                        }
                    }
                }
                if(paragraphString.contains("课程编号:")){
                    String[] curCode = paragraphString.split(":");
                    if(curCode.length>1){
                        course.setCode(curCode[1]);
                    }
                }
                if(paragraphString.contains("英文名称:")){
                    String[] curNameEn = paragraphString.split(":");
                    if(curNameEn.length>1){
                        course.setEnName(curNameEn[1]);
                    }
                }
                if(paragraphString.contains("预修课程:")){
                    String[] cur = paragraphString.split(":");
                    if(cur.length>1){
                        String[] names = cur[1].split(",");
                        if(names.length>1){
                            String ids = "";
                            for(String name:names){
                                CourseVo query = new CourseVo();
                                query.setName(name);
                                List<Course> courses = courseMapper.selectCourseList(query);
                                if(CollectionUtils.isEmpty(courses)){
                                    continue;
                                }
                                ids += courses.get(0).getId().toString() + ",";
                            }
                            if(StringUtils.isNotBlank(ids)){
                                ids = ids.substring(0,ids.length()-1);
                            }
                            course.setBeforeCourseId(ids);
                        }
                    }
                }
                if(paragraphString.contains("后续课程:")){
                    String[] cur = paragraphString.split(":");
                    if(cur.length>1){
                        String[] names = cur[1].split(",");
                        if(names.length>1){
                            String ids = "";
                            for(String name:names){
                                CourseVo query = new CourseVo();
                                query.setName(name);
                                List<Course> courses = courseMapper.selectCourseList(query);
                                if(CollectionUtils.isEmpty(courses)){
                                    continue;
                                }
                                ids += courses.get(0).getId().toString() + ",";
                            }
                            if(StringUtils.isNotBlank(ids)){
                                ids = ids.substring(0,ids.length()-1);
                            }
                            course.setAfterCourseId(ids);
                        }
                    }
                }
                if(paragraphString.contains("学时安排:")){
                    String[] hours = paragraphString.split(":");
                    if(hours.length>1){
                        course.setHours(Double.valueOf(getNumStr(hours[1])));
                    }
                }
                if(paragraphString.contains("理论学时:")){
                    String[] hoursTh = paragraphString.split(":");
                    if(hoursTh.length>1){
                        course.setTheoryHours(Double.valueOf(getNumStr(hoursTh[1])));
                    }
                }
                if(paragraphString.contains("实践（实验）学时:")){
                    String[] hoursPa = paragraphString.split(":");
                    if(hoursPa.length>1){
                        course.setPracticeHours(Double.valueOf(getNumStr(hoursPa[1])));
                    }
                }
                if(paragraphString.contains("学分:")){
                    String[] score = paragraphString.split(":");
                    if(score.length>1){
                        course.setCredit(Double.valueOf(getNumStr(score[1])));
                    }
                }
                if(paragraphString.contains("课程性质:")){
                    String[] nature = paragraphString.split(":");
                    if(nature.length>1){
                        List<SysDictData> coursePropList = doinnerDictDataService.dictType(DomainFieldConstant.COURSE_DICT_PROP).getData();
                        Map<String, String> coursePropMap = coursePropList.parallelStream().collect(Collectors.toMap(SysDictData::getDictLabel, SysDictData::getDictValue));
                        if(ObjectUtils.isNotEmpty(coursePropMap.get(nature[1]))){
                            course.setCourseProp(Integer.parseInt(coursePropMap.get(nature[1])));
                        }
                    }
                }
                if(paragraphString.contains("开课学期:")){
                    String[] term = paragraphString.split(":");
                    if(term.length>1){
                        course.setOpenTerm(term[1]);
                    }
                }
                if(paragraphString.contains("周学时:")){
                    String[] weekHours = paragraphString.split(":");
                    if(weekHours.length>1){
                        course.setWeekHours(Double.valueOf(getNumStr(weekHours[1])));
                    }
                }
                if(paragraphString.contains("讲授学时:")){
                    String[] teachHours = paragraphString.split(":");
                    if(teachHours.length>1){
                        course.setTeachHours(Double.valueOf(getNumStr(teachHours[1])));
                    }
                }
                if(paragraphString.contains("考核学时:")){
                    String[] examHours = paragraphString.split(":");
                    if(examHours.length>1){
                        course.setExamineHours(Double.valueOf(getNumStr(examHours[1])));
                    }
                }
                if(paragraphString.contains("其他学时:")){
                    String[] otherHours = paragraphString.split(":");
                    if(otherHours.length>1){
                        course.setOtherHours(Double.valueOf(getNumStr(otherHours[1])));
                    }
                }
                if(paragraphString.contains("学时单位:")){
                    String[] hourUnit = paragraphString.split(":");
                    if(hourUnit.length>1){
                        course.setHoursUnit(hourUnit[1]);
                    }
                }
                if(paragraphString.contains("课程大类:")){
                    String[] curType = paragraphString.split(":");
                    if(curType.length>1){
                        List<SysDictData> dictDateList = doinnerDictDataService.dictType(DomainFieldConstant.COURSE_DICT_BRO).getData();
                        Map<String, String> dictCodeValueMap = dictDateList.parallelStream().collect(Collectors.toMap(SysDictData::getDictLabel, SysDictData::getDictValue));
                        if(ObjectUtils.isNotEmpty(dictCodeValueMap.get(curType[1]))){
                            course.setCourseType(Integer.parseInt(dictCodeValueMap.get(curType[1])));
                        }
                    }
                }
                if(paragraphString.contains("课程属性:")){
                    String[] curAttr = paragraphString.split(":");
                    if(curAttr.length>1){
                        List<SysDictData> courseAttrList = doinnerDictDataService.dictType(DomainFieldConstant.COURSE_DICT_ATTR).getData();
                        Map<String, String> courseAttrMap = courseAttrList.parallelStream().collect(Collectors.toMap(SysDictData::getDictLabel, SysDictData::getDictValue));
                        if(ObjectUtils.isNotEmpty(courseAttrMap.get(curAttr[1]))){
                            course.setCourseAttr(Integer.parseInt(courseAttrMap.get(curAttr[1]))+"");
                        }
                    }
                }
                if(paragraphString.contains("一、课程性质地位")){
                    isCurNatureStatusContent = true;
                    continue;
                }
                if(isCurNatureStatusContent && !paragraphString.contains("二、课程目标")){
                    curNatureStatusContents.add(paragraph);
                }else{
                    isCurNatureStatusContent = false;
                }
                if(paragraphString.contains("（一）课程知识目标")){
                    isCurKnowledgeTargetContent = true;
                    continue;
                }
                if(isCurKnowledgeTargetContent && !paragraphString.contains("（二）课程能力目标")){
                    curKnowledgeTargetContents.add(paragraph);
                }else{
                    isCurKnowledgeTargetContent = false;
                }
                if(paragraphString.contains("（二）课程能力目标")){
                    isCurAbilityTargetContent = true;
                    continue;
                }
                if(isCurAbilityTargetContent && !paragraphString.contains("（三）课程思政目标")){
                    curAbilityTargetContents.add(paragraph);
                }else{
                    isCurAbilityTargetContent = false;
                }
                if(paragraphString.contains("（三）课程思政目标")){
                    isCurIdeoPoliTargetContent = true;
                    continue;
                }
                if(isCurIdeoPoliTargetContent && !paragraphString.contains("三、教学方法")){
                    curIdeoPoliTargetContents.add(paragraph);
                }else{
                    isCurIdeoPoliTargetContent = false;
                }
                if(paragraphString.contains("三、教学方法")){
                    isTeachMethodContent = true;
                    continue;
                }
                if(isTeachMethodContent && !paragraphString.contains("四、课程学习内容与时间节点")){
                    teachMethodContents.add(paragraph);
                }else{
                    isTeachMethodContent = false;
                }
                if(paragraphString.contains("五、课程学习目标和学习实现环节")){
                    isStudyContent = true;
                    continue;
                }
                if(isStudyContent && !paragraphString.contains("六、课程综合计分方法")){
                    studyContents.add(paragraph);
                }else{
                    isStudyContent = false;
                }
                if(paragraphString.contains("内容简介:") || paragraphString.contains("八、课程内容简介")){
                    isBriefContent = true;
                    continue;
                }
                if(isBriefContent){
                    briefContents.add(paragraph);
                }
                if(paragraphString.contains("考核方式:")){
                    String[] text = paragraphString.split(":");
                    if(text.length>1){
                        List<SysDictData> courseAttrExamList = doinnerDictDataService.dictType(DomainFieldConstant.COURSE_DICT_ATTR_EXAM).getData();
                        Map<String, String> courseAttrExamMap = courseAttrExamList.parallelStream().collect(Collectors.toMap(SysDictData::getDictLabel, SysDictData::getDictValue));
                        if(ObjectUtils.isNotEmpty(courseAttrExamMap.get(text[1]))){
                            course.getCourseTeachingTheoryPlanVo().setExaMethod(Integer.parseInt(courseAttrExamMap.get(text[1])));
                        }
                    }
                }
                if(paragraphString.contains("组织方式:")){
                    String[] text = paragraphString.split(":");
                    if(text.length>1){
                        course.getCourseTeachingTheoryPlanVo().setOrgMethod(text[1]);
                    }
                }
                if(paragraphString.contains("成绩评定:")){
                    String[] text = paragraphString.split(":");
                    if(text.length>1){
                        course.getCourseTeachingTheoryPlanVo().setPerformance(text[1]);
                    }
                }
                if(paragraphString.contains("记分标准:")){
                    String[] text = paragraphString.split(":");
                    if(text.length>1){
                        course.getCourseTeachingTheoryPlanVo().setStandard(text[1]);
                    }
                }
            }

            String briefContent = doCommonContents(briefContents);
            course.setSummary(briefContent);

            String curNatureStatusContent = doCommonContents(curNatureStatusContents);
            course.getCourseTeachingTheoryPlanVo().setNatureLevel(curNatureStatusContent);
            String curKnowledgeTargetContent = doTheoryChapterContents(curKnowledgeTargetContents);
            course.getCourseTeachingTheoryPlanVo().setKnowLevel(curKnowledgeTargetContent);
            String curAbilityTargetContent = doTheoryChapterContents(curAbilityTargetContents);
            course.getCourseTeachingTheoryPlanVo().setAbilityLevel(curAbilityTargetContent);
            String curIdeoPoliTargetContent = doTheoryChapterContents(curIdeoPoliTargetContents);
            course.getCourseTeachingTheoryPlanVo().setPoliticsLevel(curIdeoPoliTargetContent);
            String teachMethodContent = doCommonContents(teachMethodContents);
            course.getCourseTeachingTheoryPlanVo().setTeachingMethod(teachMethodContent);

            String studyContent = doCommonContents(studyContents);
            List<KnowledgeDomainVo> domains = Lists.newArrayList();

            if(studyContent.contains("知识领域")){
                String[] sContents = studyContent.split("知识领域");
                for(String sContent:sContents){
                    if(StringUtils.isBlank(sContent)){
                        continue;
                    }
                    sContent = "知识领域" + sContent;
                    String lyStr = StringUtils.substringBetween(sContent,"知识领域","知识单元");
                    KnowledgeDomainVo domain = new KnowledgeDomainVo();
                    domain.setCourseId(courseId);
                    domain.setName(lyStr);
                    domains.add(domain);
                    sContent = sContent.substring(sContent.indexOf("知识单元"));
                    getTheoryChapterForStudy(sContent,domain);
                }
            }else{
                //生成默认章节
                KnowledgeDomainVo domain = new KnowledgeDomainVo();
                domain.setCourseId(courseId);
                domain.setName("默认专题");
                domains.add(domain);
                getTheoryChapterForStudy(studyContent,domain);
            }
            course.setKnowledgeDomainVoList(domains);

            course.setStatus(CourseConstant.CUR_STATUS_NO);
            course.setAnalysisStatus(CourseConstant.CUR_ANA_STATUS_YES);
            curriculumService.saveCourse(course);
            return CourseConstant.ANALYSIS_SUCCESS;
        }catch (Exception e){
            log.error(e.getMessage());
            e.printStackTrace();
            return e.getMessage();
        }
    }

    @Override
    @Transactional(rollbackFor = {Exception.class})
    public String analysisTeachPlanPractice(String id, Long courseId) {
        try{
            XWPFDocument document = getXWPFDocument(id);
            List<XWPFParagraph> paragraphs = document.getParagraphs();
            CourseVo course = getCourse(courseId);

            //任务背景描述内容
            List<XWPFParagraph> backContents = Lists.newArrayList();
            boolean isBackContent = false;
            //实践目标内容
            List<XWPFParagraph> praTargetContents = Lists.newArrayList();
            boolean isPraTargetContent = false;
            //主要内容和要求内容
            List<XWPFParagraph> requireContents = Lists.newArrayList();
            boolean isRequireContent = false;
            //实践环节单位概况及与实践环节内容和要求的关联情况
            List<XWPFParagraph> conRequireContents = Lists.newArrayList();
            boolean isConRequireContent = false;
            //实践方式
            List<XWPFParagraph> praTypeContents = Lists.newArrayList();
            boolean isPraTypeContent = false;
            //时间及地点安排
            List<XWPFParagraph> timeAddressContents = Lists.newArrayList();
            boolean isTimeAddressContent = false;
            //考核与评价
            List<XWPFParagraph> checkedContents = Lists.newArrayList();
            boolean isCheckedContent = false;
            //有关要求
            List<XWPFParagraph> requireOfContents = Lists.newArrayList();
            boolean isRequireOfContent = false;

            for(XWPFParagraph paragraph:paragraphs){
                List<XWPFRun> runs = paragraph.getRuns();
                if(CollectionUtils.isEmpty(runs)){
                    continue;
                }
                String paragraphString = doRunsContents(runs);
                if(paragraphString.contains("：")){
                    paragraphString = paragraphString.replace("：",":");
                }
                if(paragraphString.contains("，")){
                    paragraphString = paragraphString.replace("，",",");
                }
                if(paragraphString.contains("执笔人:") && paragraphString.contains("审阅学院:")){
                    String writingName = paragraphString.substring(paragraphString.indexOf("执笔人:")+4,paragraphString.indexOf("审阅学院:"));
                    course.setAuthors(writingName);
                    String reviewCollege = paragraphString.substring(paragraphString.indexOf("审阅学院:")+5,paragraphString.indexOf("）"));
                    CustomDept query = new CustomDept();
                    query.setDeptName(reviewCollege);
                    List<SysDept> result = doinnerDeptService.list(query).getData();
                    if(!CollectionUtils.isEmpty(result)){
                        course.setCollegeId(result.get(0).getDeptId());
                    }
                }else{
                    if(paragraphString.contains("执笔人:")){
                        String[] writingName = paragraphString.split(":");
                        if(writingName.length>1){
                            course.setAuthors(writingName[1]);
                        }
                    }
                    if(paragraphString.contains("审阅学院:")|| paragraphString.contains("所属学院:")){
                        String[] reviewCollege = paragraphString.split(":");
                        if(reviewCollege.length>1){
                            CustomDept query = new CustomDept();
                            query.setDeptName(reviewCollege[1].trim());
                            List<SysDept> result = doinnerDeptService.list(query).getData();
                            if(!CollectionUtils.isEmpty(result)){
                                course.setCollegeId(result.get(0).getDeptId());
                            }
                        }
                    }
                }
                if(paragraphString.contains("适用对象:")){
                    String[] obj = paragraphString.split(":");
                    if(obj.length>1){
                        course.getCourseTeachingPracticePlanVo().setSuit(obj[1]);
                    }
                }
                if(paragraphString.contains("课程编号:")){
                    String[] curCode = paragraphString.split(":");
                    if(curCode.length>1){
                        course.setCode(curCode[1]);
                    }
                }
                if(paragraphString.contains("施教学院:")){
                    String[] teachCollege = paragraphString.split(":");
                    if(teachCollege.length>1){
                        CustomDept query = new CustomDept();
                        query.setDeptName(teachCollege[1].trim());
                        List<SysDept> result = doinnerDeptService.list(query).getData();
                        if(!CollectionUtils.isEmpty(result)){
                            course.setTeachCollegeId(result.get(0).getDeptId());
                        }
                    }
                }
                if(paragraphString.contains("预修课程:")){
                    String[] cur = paragraphString.split(":");
                    if(cur.length>1){
                        String[] names = cur[1].split(",");
                        if(names.length>1){
                            String ids = "";
                            for(String name:names){
                                CourseVo query = new CourseVo();
                                query.setName(name);
                                List<Course> courses = courseMapper.selectCourseList(query);
                                if(CollectionUtils.isEmpty(courses)){
                                    continue;
                                }
                                ids += courses.get(0).getId().toString() + ",";
                            }
                            if(StringUtils.isNotBlank(ids)){
                                ids = ids.substring(0,ids.length()-1);
                            }
                            course.setBeforeCourseId(ids);
                        }
                    }
                }
                if(paragraphString.contains("后续课程:")){
                    String[] cur = paragraphString.split(":");
                    if(cur.length>1){
                        String[] names = cur[1].split(",");
                        if(names.length>1){
                            String ids = "";
                            for(String name:names){
                                CourseVo query = new CourseVo();
                                query.setName(name);
                                List<Course> courses = courseMapper.selectCourseList(query);
                                if(CollectionUtils.isEmpty(courses)){
                                    continue;
                                }
                                ids += courses.get(0).getId().toString() + ",";
                            }
                            if(StringUtils.isNotBlank(ids)){
                                ids = ids.substring(0,ids.length()-1);
                            }
                            course.setAfterCourseId(ids);
                        }
                    }
                }
                if(paragraphString.contains("学时安排:")){
                    String[] hours = paragraphString.split(":");
                    if(hours.length>1){
                        course.setHours(Double.valueOf(getNumStr(hours[1])));
                    }
                }
                if(paragraphString.contains("实践学时:")){
                    String[] hoursPa = paragraphString.split(":");
                    if(hoursPa.length>1){
                        course.setPracticeHours(Double.valueOf(getNumStr(hoursPa[1])));
                    }
                }
                if(paragraphString.contains("学分:")){
                    String[] score = paragraphString.split(":");
                    if(score.length>1){
                        course.setCredit(Double.valueOf(getNumStr(score[1])));
                    }
                }
                if(paragraphString.contains("周学时:")){
                    String[] weekHours = paragraphString.split(":");
                    if(weekHours.length>1){
                        course.setWeekHours(Double.valueOf(getNumStr(weekHours[1])));
                    }
                }
                if(paragraphString.contains("讲授学时:")){
                    String[] teachHours = paragraphString.split(":");
                    if(teachHours.length>1){
                        course.setTeachHours(Double.valueOf(getNumStr(teachHours[1])));
                    }
                }
                if(paragraphString.contains("考核学时:")){
                    String[] examHours = paragraphString.split(":");
                    if(examHours.length>1){
                        course.setExamineHours(Double.valueOf(getNumStr(examHours[1])));
                    }
                }
                if(paragraphString.contains("其他学时:")){
                    String[] otherHours = paragraphString.split(":");
                    if(otherHours.length>1){
                        course.setOtherHours(Double.valueOf(getNumStr(otherHours[1])));
                    }
                }
                if(paragraphString.contains("学时单位:")){
                    String[] hourUnit = paragraphString.split(":");
                    if(hourUnit.length>1){
                        course.setHoursUnit(hourUnit[1]);
                    }
                }
                if(paragraphString.contains("课程大类:")){
                    String[] curType = paragraphString.split(":");
                    if(curType.length>1){
                        List<SysDictData> dictDateList = doinnerDictDataService.dictType(DomainFieldConstant.COURSE_DICT_BRO).getData();
                        Map<String, String> dictCodeValueMap = dictDateList.parallelStream().collect(Collectors.toMap(SysDictData::getDictLabel, SysDictData::getDictValue));
                        if(ObjectUtils.isNotEmpty(dictCodeValueMap.get(curType[1]))){
                            course.setCourseType(Integer.parseInt(dictCodeValueMap.get(curType[1])));
                        }
                    }
                }
                if(paragraphString.contains("开课学期:")){
                    String[] term = paragraphString.split(":");
                    if(term.length>1){
                        course.setOpenTerm(term[1]);
                    }
                }
                if(paragraphString.contains("课程属性:")){
                    String[] curAttr = paragraphString.split(":");
                    if(curAttr.length>1){
                        List<SysDictData> courseAttrList = doinnerDictDataService.dictType(DomainFieldConstant.COURSE_DICT_ATTR).getData();
                        Map<String, String> courseAttrMap = courseAttrList.parallelStream().collect(Collectors.toMap(SysDictData::getDictLabel, SysDictData::getDictValue));
                        if(ObjectUtils.isNotEmpty(courseAttrMap.get(curAttr[1]))){
                            course.setCourseAttr(Integer.parseInt(courseAttrMap.get(curAttr[1]))+"");
                        }
                    }
                }
                if(paragraphString.contains("实施地点:")){
                    String[] address = paragraphString.split(":");
                    if(address.length>1){
                        course.setLocation(address[1]);
                    }
                }
                if(paragraphString.contains("（一）任务背景描述")){
                    isBackContent = true;
                    continue;
                }
                if(isBackContent && !paragraphString.contains("（二）实践目标")){
                    backContents.add(paragraph);
                }else{
                    isBackContent = false;
                }
                if(paragraphString.contains("（二）实践目标")){
                    isPraTargetContent = true;
                    continue;
                }
                if(isPraTargetContent && !paragraphString.contains("二、主要内容和要求")){
                    praTargetContents.add(paragraph);
                }else{
                    isPraTargetContent = false;
                }
                if(paragraphString.contains("二、主要内容和要求")){
                    isRequireContent = true;
                    continue;
                }
                if(isRequireContent && !paragraphString.contains("三、实践环节单位概况及与实践环节内容和要求的关联情况")){
                    requireContents.add(paragraph);
                }else{
                    isRequireContent = false;
                }
                if(paragraphString.contains("三、实践环节单位概况及与实践环节内容和要求的关联情况")){
                    isConRequireContent = true;
                    continue;
                }
                if(isConRequireContent && !paragraphString.contains("四、实践方式")){
                    conRequireContents.add(paragraph);
                }else{
                    isConRequireContent = false;
                }
                if(paragraphString.contains("四、实践方式")){
                    isPraTypeContent = true;
                    continue;
                }
                if(isPraTypeContent && !paragraphString.contains("五、时间及地点安排")){
                    praTypeContents.add(paragraph);
                }else{
                    isPraTypeContent = false;
                }
                if(paragraphString.contains("五、时间及地点安排")){
                    isTimeAddressContent = true;
                    continue;
                }
                if(isTimeAddressContent && !paragraphString.contains("六、考核与评价")){
                    timeAddressContents.add(paragraph);
                }else{
                    isTimeAddressContent = false;
                }
                if(paragraphString.contains("六、考核与评价")){
                    isCheckedContent = true;
                    continue;
                }
                if(isCheckedContent && !paragraphString.contains("七、有关要求")){
                    checkedContents.add(paragraph);
                }else{
                    isCheckedContent = false;
                }
                if(paragraphString.contains("七、有关要求")){
                    isRequireOfContent = true;
                    continue;
                }
                if(isRequireOfContent){
                    requireOfContents.add(paragraph);
                }
            }

            String backContent = doTheoryChapterContents(backContents);
            course.getCourseTeachingPracticePlanVo().setTaskDescribe(backContent);

            String praTargetContent = doCommonContents(praTargetContents);
            String praKnowledgeTargetContent = StringUtils.substringBetween(praTargetContent,"知识目标:","能力目标:");
            course.getCourseTeachingPracticePlanVo().setKnowLevel(praKnowledgeTargetContent);
            String praAbilityTargetContent = StringUtils.substringBetween(praTargetContent,"能力目标:","思政目标:");
            course.getCourseTeachingPracticePlanVo().setAbilityLevel(praAbilityTargetContent);
            String praIdeoPoliTargetContent = StringUtils.substringAfter(praTargetContent,"思政目标:");
            course.getCourseTeachingPracticePlanVo().setPoliticsLevel(praIdeoPoliTargetContent);

            String requireContent = doCommonContents(requireContents);
            course.getCourseTeachingPracticePlanVo().setContent(requireContent);
            String conRequireContent = doCommonContents(conRequireContents);
            course.getCourseTeachingPracticePlanVo().setOverview(conRequireContent);
            String praTypeContent = doCommonContents(praTypeContents);
            course.getCourseTeachingPracticePlanVo().setPracticeMethod(praTypeContent);
            String timeAddressContent = doCommonContents(timeAddressContents);
            course.getCourseTeachingPracticePlanVo().setArrange(timeAddressContent);

            String checkedContent = doCommonContents(checkedContents);
            String checkType = StringUtils.substringBetween(checkedContent,"考核方式:","组织方式:");
            List<SysDictData> courseAttrExamList = doinnerDictDataService.dictType(DomainFieldConstant.COURSE_DICT_ATTR_EXAM).getData();
            Map<String, String> courseAttrExamMap = courseAttrExamList.parallelStream().collect(Collectors.toMap(SysDictData::getDictLabel, SysDictData::getDictValue));
            if(ObjectUtils.isNotEmpty(courseAttrExamMap.get(checkType))){
                course.getCourseTeachingPracticePlanVo().setExaMethod(Integer.parseInt(courseAttrExamMap.get(checkType))+"");
            }
            String orgMethod = StringUtils.substringBetween(checkedContent,"组织方式:","成绩评定:");
            course.getCourseTeachingPracticePlanVo().setOrgMethod(orgMethod);
            String achievement = StringUtils.substringBetween(checkedContent,"成绩评定:","记分标准:");
            course.getCourseTeachingPracticePlanVo().setPerformance(achievement);
            String scoreStandard = StringUtils.substringAfter(checkedContent,"记分标准:");
            course.getCourseTeachingPracticePlanVo().setStandard(scoreStandard);
            String requireOfContent = doCommonContents(requireOfContents);
            course.getCourseTeachingPracticePlanVo().setAsk(requireOfContent);

            course.setStatus(CourseConstant.CUR_STATUS_NO);
            course.setAnalysisStatus(CourseConstant.CUR_ANA_STATUS_YES);
            curriculumService.saveCourse(course);
            return CourseConstant.ANALYSIS_SUCCESS;
        }catch (Exception e){
            log.error(e.getMessage());
            return e.getMessage();
        }
    }

    @Override
    @Transactional(rollbackFor = {Exception.class})
    public String analysisTeachPlanAll(String id, Long courseId) {
        try{
            XWPFDocument document = getXWPFDocument(id);
            List<XWPFParagraph> paragraphs = document.getParagraphs();
            CourseVo course = getCourse(courseId);

            //解析表格内容
            List<XWPFTable> tables = document.getTables();
            if(!validateTable(tables)){
                log.error("表格格式或内容不正确，请确认！");
                throw new DataFormatException("表格格式或内容不正确，请确认！");
            }

            List<CourseChapterVo> courseChapters = Lists.newArrayList();
            String arrayStr = analysisTable(tables,courseId,courseChapters);
            course.getCourseTeachingTheoryPlanVo().setContentJson(arrayStr);
            course.setCourseChapterVoList(courseChapters);
            List<CourseTextbookVo> books = analysisBookTable(tables,courseId);
            course.setCourseTextbookVoList(books);

            //课程性质地位内容
            List<XWPFParagraph> curNatureStatusContents = Lists.newArrayList();
            boolean isCurNatureStatusContent = false;
            //课程内容简介内容
            List<XWPFParagraph> briefContents = Lists.newArrayList();
            boolean isBriefContent = false;
            //课程知识目标内容
            List<XWPFParagraph> curKnowledgeTargetContents = Lists.newArrayList();
            boolean isCurKnowledgeTargetContent = false;
            //课程能力目标内容
            List<XWPFParagraph> curAbilityTargetContents = Lists.newArrayList();
            boolean isCurAbilityTargetContent = false;
            //课程思政目标内容
            List<XWPFParagraph> curIdeoPoliTargetContents = Lists.newArrayList();
            boolean isCurIdeoPoliTargetContent = false;
            //教学方法内容
            List<XWPFParagraph> teachMethodContents = Lists.newArrayList();
            boolean isTeachMethodContent = false;
            //课程学习目标和学习实现环节
            List<XWPFParagraph> studyContents = Lists.newArrayList();
            boolean isStudyContent = false;
            //课程综合计分方法
            List<XWPFParagraph> checkContents = Lists.newArrayList();
            boolean isCheckContent = false;
            //任务背景描述内容
            List<XWPFParagraph> backContents = Lists.newArrayList();
            boolean isBackContent = false;
            //实践目标内容
            List<XWPFParagraph> praTargetContents = Lists.newArrayList();
            boolean isPraTargetContent = false;
            //主要内容和要求内容
            List<XWPFParagraph> requireContents = Lists.newArrayList();
            boolean isRequireContent = false;
            //实践环节单位概况及与实践环节内容和要求的关联情况
            List<XWPFParagraph> conRequireContents = Lists.newArrayList();
            boolean isConRequireContent = false;
            //实践方式
            List<XWPFParagraph> praTypeContents = Lists.newArrayList();
            boolean isPraTypeContent = false;
            //时间及地点安排
            List<XWPFParagraph> timeAddressContents = Lists.newArrayList();
            boolean isTimeAddressContent = false;
            //考核与评价
            List<XWPFParagraph> checkedContents = Lists.newArrayList();
            boolean isCheckedContent = false;
            //有关要求
            List<XWPFParagraph> requireOfContents = Lists.newArrayList();
            boolean isRequireOfContent = false;

            for(XWPFParagraph paragraph:paragraphs){
                List<XWPFRun> runs = paragraph.getRuns();
                if(CollectionUtils.isEmpty(runs)){
                    continue;
                }
                String paragraphString = doRunsContents(runs);
                if(paragraphString.contains("：")){
                    paragraphString = paragraphString.replace("：",":");
                }
                if(paragraphString.contains("，")){
                    paragraphString = paragraphString.replace("，",",");
                }
                if(paragraphString.contains("执笔人:") && paragraphString.contains("审阅学院:")){
                    String writingName = paragraphString.substring(paragraphString.indexOf("执笔人:")+4,paragraphString.indexOf("审阅学院:"));
                    course.setAuthors(writingName);
                    String reviewCollege = paragraphString.substring(paragraphString.indexOf("审阅学院:")+5,paragraphString.indexOf("）"));
                    CustomDept query = new CustomDept();
                    query.setDeptName(reviewCollege);
                    List<SysDept> result = doinnerDeptService.list(query).getData();
                    if(!CollectionUtils.isEmpty(result)){
                        course.setCollegeId(result.get(0).getDeptId());
                    }
                }else{
                    if(paragraphString.contains("执笔人:")){
                        String[] writingName = paragraphString.split(":");
                        if(writingName.length>1){
                            course.setAuthors(writingName[1]);
                        }
                    }
                    if(paragraphString.contains("审阅学院:")){
                        String[] reviewCollege = paragraphString.split(":");
                        if(reviewCollege.length>1){
                            CustomDept query = new CustomDept();
                            query.setDeptName(reviewCollege[1].trim());
                            List<SysDept> result = doinnerDeptService.list(query).getData();
                            if(!CollectionUtils.isEmpty(result)){
                                course.setCollegeId(result.get(0).getDeptId());
                            }
                        }
                    }
                }
                if(paragraphString.contains("课程编号:")){
                    String[] curCode = paragraphString.split(":");
                    if(curCode.length>1){
                        course.setCode(curCode[1]);
                    }
                }
                if(paragraphString.contains("英文名称:")){
                    String[] curNameEn = paragraphString.split(":");
                    if(curNameEn.length>1){
                        course.setEnName(curNameEn[1]);
                    }
                }
                if(paragraphString.contains("预修课程:")){
                    String[] cur = paragraphString.split(":");
                    if(cur.length>1){
                        String[] names = cur[1].split(",");
                        if(names.length>1){
                            String ids = "";
                            for(String name:names){
                                CourseVo query = new CourseVo();
                                query.setName(name);
                                List<Course> courses = courseMapper.selectCourseList(query);
                                if(CollectionUtils.isEmpty(courses)){
                                    continue;
                                }
                                ids += courses.get(0).getId().toString() + ",";
                            }
                            if(StringUtils.isNotBlank(ids)){
                                ids = ids.substring(0,ids.length()-1);
                            }
                            course.setBeforeCourseId(ids);
                        }
                    }
                }
                if(paragraphString.contains("后续课程:")){
                    String[] cur = paragraphString.split(":");
                    if(cur.length>1){
                        String[] names = cur[1].split(",");
                        if(names.length>1){
                            String ids = "";
                            for(String name:names){
                                CourseVo query = new CourseVo();
                                query.setName(name);
                                List<Course> courses = courseMapper.selectCourseList(query);
                                if(CollectionUtils.isEmpty(courses)){
                                    continue;
                                }
                                ids += courses.get(0).getId().toString() + ",";
                            }
                            if(StringUtils.isNotBlank(ids)){
                                ids = ids.substring(0,ids.length()-1);
                            }
                            course.setAfterCourseId(ids);
                        }
                    }
                }
                if(paragraphString.contains("学时安排:")){
                    String[] hours = paragraphString.split(":");
                    if(hours.length>1){
                        course.setHours(Double.valueOf(getNumStr(hours[1])));
                    }
                }
                if(paragraphString.contains("理论学时:")){
                    String[] hoursTh = paragraphString.split(":");
                    if(hoursTh.length>1){
                        course.setTheoryHours(Double.valueOf(getNumStr(hoursTh[1])));
                    }
                }
                if(paragraphString.contains("实践（实验）学时:")){
                    String[] hoursPa = paragraphString.split(":");
                    if(hoursPa.length>1){
                        course.setPracticeHours(Double.valueOf(getNumStr(hoursPa[1])));
                    }
                }
                if(paragraphString.contains("学分:")){
                    String[] score = paragraphString.split(":");
                    if(score.length>1){
                        course.setCredit(Double.valueOf(getNumStr(score[1])));
                    }
                }
                if(paragraphString.contains("课程性质:")){
                    String[] nature = paragraphString.split(":");
                    if(nature.length>1){
                        List<SysDictData> coursePropList = doinnerDictDataService.dictType(DomainFieldConstant.COURSE_DICT_PROP).getData();
                        Map<String, String> coursePropMap = coursePropList.parallelStream().collect(Collectors.toMap(SysDictData::getDictLabel, SysDictData::getDictValue));
                        if(ObjectUtils.isNotEmpty(coursePropMap.get(nature[1]))){
                            course.setCourseProp(Integer.parseInt(coursePropMap.get(nature[1])));
                        }
                    }
                }
                if(paragraphString.contains("开课学期:")){
                    String[] term = paragraphString.split(":");
                    if(term.length>1){
                        course.setOpenTerm(term[1]);
                    }
                }
                if(paragraphString.contains("周学时:")){
                    String[] weekHours = paragraphString.split(":");
                    if(weekHours.length>1){
                        course.setWeekHours(Double.valueOf(getNumStr(weekHours[1])));
                    }
                }
                if(paragraphString.contains("讲授学时:")){
                    String[] teachHours = paragraphString.split(":");
                    if(teachHours.length>1){
                        course.setTeachHours(Double.valueOf(getNumStr(teachHours[1])));
                    }
                }
                if(paragraphString.contains("考核学时:")){
                    String[] examHours = paragraphString.split(":");
                    if(examHours.length>1){
                        course.setExamineHours(Double.valueOf(getNumStr(examHours[1])));
                    }
                }
                if(paragraphString.contains("其他学时:")){
                    String[] otherHours = paragraphString.split(":");
                    if(otherHours.length>1){
                        course.setOtherHours(Double.valueOf(getNumStr(otherHours[1])));
                    }
                }
                if(paragraphString.contains("学时单位:")){
                    String[] hourUnit = paragraphString.split(":");
                    if(hourUnit.length>1){
                        course.setHoursUnit(hourUnit[1]);
                    }
                }
                if(paragraphString.contains("课程大类:")){
                    String[] curType = paragraphString.split(":");
                    if(curType.length>1){
                        List<SysDictData> dictDateList = doinnerDictDataService.dictType(DomainFieldConstant.COURSE_DICT_BRO).getData();
                        Map<String, String> dictCodeValueMap = dictDateList.parallelStream().collect(Collectors.toMap(SysDictData::getDictLabel, SysDictData::getDictValue));
                        if(ObjectUtils.isNotEmpty(dictCodeValueMap.get(curType[1]))){
                            course.setCourseType(Integer.parseInt(dictCodeValueMap.get(curType[1])));
                        }
                    }
                }
                if(paragraphString.contains("课程属性:")){
                    String[] curAttr = paragraphString.split(":");
                    if(curAttr.length>1){
                        List<SysDictData> courseAttrList = doinnerDictDataService.dictType(DomainFieldConstant.COURSE_DICT_ATTR).getData();
                        Map<String, String> courseAttrMap = courseAttrList.parallelStream().collect(Collectors.toMap(SysDictData::getDictLabel, SysDictData::getDictValue));
                        if(ObjectUtils.isNotEmpty(courseAttrMap.get(curAttr[1]))){
                            course.setCourseAttr(Integer.parseInt(courseAttrMap.get(curAttr[1]))+"");
                        }
                    }
                }
                if(paragraphString.contains("施教学院:")){
                    String[] teachCollege = paragraphString.split(":");
                    if(teachCollege.length>1){
                        CustomDept query = new CustomDept();
                        query.setDeptName(teachCollege[1].trim());
                        List<SysDept> result = doinnerDeptService.list(query).getData();
                        if(!CollectionUtils.isEmpty(result)){
                            course.setTeachCollegeId(result.get(0).getDeptId());
                        }
                    }
                }
                if(paragraphString.contains("实施地点:")){
                    String[] address = paragraphString.split(":");
                    if(address.length>1){
                        course.setLocation(address[1]);
                    }
                }
                if(paragraphString.contains("适用对象:")){
                    String[] obj = paragraphString.split(":");
                    if(obj.length>1){
                        course.getCourseTeachingPracticePlanVo().setSuit(obj[1]);
                    }
                }
                if(paragraphString.contains("一、课程性质地位")){
                    isCurNatureStatusContent = true;
                    continue;
                }
                if(isCurNatureStatusContent && !paragraphString.contains("二、课程目标")){
                    curNatureStatusContents.add(paragraph);
                }else{
                    isCurNatureStatusContent = false;
                }
                if(paragraphString.contains("（一）课程知识目标")){
                    isCurKnowledgeTargetContent = true;
                    continue;
                }
                if(isCurKnowledgeTargetContent && !paragraphString.contains("（二）课程能力目标")){
                    curKnowledgeTargetContents.add(paragraph);
                }else{
                    isCurKnowledgeTargetContent = false;
                }
                if(paragraphString.contains("（二）课程能力目标")){
                    isCurAbilityTargetContent = true;
                    continue;
                }
                if(isCurAbilityTargetContent && !paragraphString.contains("（三）课程思政目标")){
                    curAbilityTargetContents.add(paragraph);
                }else{
                    isCurAbilityTargetContent = false;
                }
                if(paragraphString.contains("（三）课程思政目标")){
                    isCurIdeoPoliTargetContent = true;
                    continue;
                }
                if(isCurIdeoPoliTargetContent && !paragraphString.contains("三、教学方法")){
                    curIdeoPoliTargetContents.add(paragraph);
                }else{
                    isCurIdeoPoliTargetContent = false;
                }
                if(paragraphString.contains("三、教学方法")){
                    isTeachMethodContent = true;
                    continue;
                }
                if(isTeachMethodContent && !paragraphString.contains("四、课程学习内容与时间节点")){
                    teachMethodContents.add(paragraph);
                }else{
                    isTeachMethodContent = false;
                }
                if(paragraphString.contains("五、课程学习目标和学习实现环节")){
                    isStudyContent = true;
                    continue;
                }
                if(isStudyContent && !paragraphString.contains("六、课程综合计分方法")){
                    studyContents.add(paragraph);
                }else{
                    isStudyContent = false;
                }
                if(paragraphString.contains("六、课程综合计分方法")){
                    isCheckContent = true;
                    continue;
                }
                if(isCheckContent && !paragraphString.contains("七、教材及推荐参考书")){
                    checkContents.add(paragraph);
                }else{
                    isCheckContent = false;
                }
                if(paragraphString.contains("内容简介:") || paragraphString.contains("八、课程内容简介")){
                    isBriefContent = true;
                    continue;
                }
                if(isBriefContent && !paragraphString.contains(course.getName() + "实践环节教学计划") && !paragraphString.contains("《"+course.getName() + "》实践环节教学计划")){
                    briefContents.add(paragraph);
                }else{
                    isBriefContent = false;
                }

                if(paragraphString.contains("（一）任务背景描述")){
                    isBackContent = true;
                    continue;
                }
                if(isBackContent && !paragraphString.contains("（二）实践目标")){
                    backContents.add(paragraph);
                }else{
                    isBackContent = false;
                }
                if(paragraphString.contains("（二）实践目标")){
                    isPraTargetContent = true;
                    continue;
                }
                if(isPraTargetContent && !paragraphString.contains("二、主要内容和要求")){
                    praTargetContents.add(paragraph);
                }else{
                    isPraTargetContent = false;
                }
                if(paragraphString.contains("二、主要内容和要求")){
                    isRequireContent = true;
                    continue;
                }
                if(isRequireContent && !paragraphString.contains("三、实践环节单位概况及与实践环节内容和要求的关联情况")){
                    requireContents.add(paragraph);
                }else{
                    isRequireContent = false;
                }
                if(paragraphString.contains("三、实践环节单位概况及与实践环节内容和要求的关联情况")){
                    isConRequireContent = true;
                    continue;
                }
                if(isConRequireContent && !paragraphString.contains("四、实践方式")){
                    conRequireContents.add(paragraph);
                }else{
                    isConRequireContent = false;
                }
                if(paragraphString.contains("四、实践方式")){
                    isPraTypeContent = true;
                    continue;
                }
                if(isPraTypeContent && !paragraphString.contains("五、时间及地点安排")){
                    praTypeContents.add(paragraph);
                }else{
                    isPraTypeContent = false;
                }
                if(paragraphString.contains("五、时间及地点安排")){
                    isTimeAddressContent = true;
                    continue;
                }
                if(isTimeAddressContent && !paragraphString.contains("六、考核与评价")){
                    timeAddressContents.add(paragraph);
                }else{
                    isTimeAddressContent = false;
                }
                if(paragraphString.contains("六、考核与评价")){
                    isCheckedContent = true;
                    continue;
                }
                if(isCheckedContent && !paragraphString.contains("七、有关要求")){
                    checkedContents.add(paragraph);
                }else{
                    isCheckedContent = false;
                }
                if(paragraphString.contains("七、有关要求")){
                    isRequireOfContent = true;
                    continue;
                }
                if(isRequireOfContent){
                    requireOfContents.add(paragraph);
                }
            }

            String briefContent = doCommonContents(briefContents);
            course.setSummary(briefContent);

            String curNatureStatusContent = doCommonContents(curNatureStatusContents);
            course.getCourseTeachingTheoryPlanVo().setNatureLevel(curNatureStatusContent);
            String curKnowledgeTargetContent = doTheoryChapterContents(curKnowledgeTargetContents);
            course.getCourseTeachingTheoryPlanVo().setKnowLevel(curKnowledgeTargetContent);
            String curAbilityTargetContent = doTheoryChapterContents(curAbilityTargetContents);
            course.getCourseTeachingTheoryPlanVo().setAbilityLevel(curAbilityTargetContent);
            String curIdeoPoliTargetContent = doTheoryChapterContents(curIdeoPoliTargetContents);
            course.getCourseTeachingTheoryPlanVo().setPoliticsLevel(curIdeoPoliTargetContent);
            String teachMethodContent = doCommonContents(teachMethodContents);
            course.getCourseTeachingTheoryPlanVo().setTeachingMethod(teachMethodContent);


            String checkContent = doCommonContents(checkContents);
            String checkTypeTheory = StringUtils.substringBetween(checkContent,"考核方式:","组织方式:");
            List<SysDictData> courseAttrExamList = doinnerDictDataService.dictType(DomainFieldConstant.COURSE_DICT_ATTR_EXAM).getData();
            Map<String, String> courseAttrExamMap = courseAttrExamList.parallelStream().collect(Collectors.toMap(SysDictData::getDictLabel, SysDictData::getDictValue));
            if(ObjectUtils.isNotEmpty(courseAttrExamMap.get(checkTypeTheory))){
                course.getCourseTeachingTheoryPlanVo().setExaMethod(Integer.parseInt(courseAttrExamMap.get(checkTypeTheory)));
            }
            String examTypeTheory = StringUtils.substringBetween(checkContent,"组织方式:","成绩评定:");
            course.getCourseTeachingTheoryPlanVo().setOrgMethod(examTypeTheory);
            String achievementTheory = StringUtils.substringBetween(checkContent,"成绩评定:","记分标准:");
            course.getCourseTeachingTheoryPlanVo().setPerformance(achievementTheory);
            String scoreStandardTheory = StringUtils.substringAfter(checkContent,"记分标准:");
            course.getCourseTeachingTheoryPlanVo().setStandard(scoreStandardTheory);

            String studyContent = doCommonContents(studyContents);
            List<KnowledgeDomainVo> domains = Lists.newArrayList();

            if(studyContent.contains("知识领域")){
                String[] sContents = studyContent.split("知识领域");
                for(String sContent:sContents){
                    if(StringUtils.isBlank(sContent)){
                        continue;
                    }
                    sContent = "知识领域" + sContent;
                    String lyStr = StringUtils.substringBetween(sContent,"知识领域","知识单元");
                    KnowledgeDomainVo domain = new KnowledgeDomainVo();
                    domain.setCourseId(courseId);
                    domain.setName(lyStr);
                    domains.add(domain);
                    sContent = sContent.substring(sContent.indexOf("知识单元"));
                    getTheoryChapterForStudy(sContent,domain);
                }
            }else{
                //生成默认章节
                KnowledgeDomainVo domain = new KnowledgeDomainVo();
                domain.setCourseId(courseId);
                domain.setName("默认专题");
                domains.add(domain);
                getTheoryChapterForStudy(studyContent,domain);
            }

            String backContent = doTheoryChapterContents(backContents);
            course.getCourseTeachingPracticePlanVo().setTaskDescribe(backContent);

            String praTargetContent = doCommonContents(praTargetContents);
            String praKnowledgeTargetContent = StringUtils.substringBetween(praTargetContent,"知识目标:","能力目标:");
            course.getCourseTeachingPracticePlanVo().setKnowLevel(praKnowledgeTargetContent);
            String praAbilityTargetContent = StringUtils.substringBetween(praTargetContent,"能力目标:","思政目标:");
            course.getCourseTeachingPracticePlanVo().setAbilityLevel(praAbilityTargetContent);
            String praIdeoPoliTargetContent = StringUtils.substringAfter(praTargetContent,"思政目标:");
            course.getCourseTeachingPracticePlanVo().setPoliticsLevel(praIdeoPoliTargetContent);

            String requireContent = doCommonContents(requireContents);
            course.getCourseTeachingPracticePlanVo().setContent(requireContent);
            String conRequireContent = doCommonContents(conRequireContents);
            course.getCourseTeachingPracticePlanVo().setOverview(conRequireContent);
            String praTypeContent = doCommonContents(praTypeContents);
            course.getCourseTeachingPracticePlanVo().setPracticeMethod(praTypeContent);
            String timeAddressContent = doCommonContents(timeAddressContents);
            course.getCourseTeachingPracticePlanVo().setArrange(timeAddressContent);

            String checkedContent = doCommonContents(checkedContents);
            String checkType = StringUtils.substringBetween(checkedContent,"考核方式:","组织方式:");
            if(ObjectUtils.isNotEmpty(courseAttrExamMap.get(checkType))){
                course.getCourseTeachingPracticePlanVo().setExaMethod(Integer.parseInt(courseAttrExamMap.get(checkType))+"");
            }
            String orgMethod = StringUtils.substringBetween(checkedContent,"组织方式:","成绩评定:");
            course.getCourseTeachingPracticePlanVo().setOrgMethod(orgMethod);
            String achievement = StringUtils.substringBetween(checkedContent,"成绩评定:","记分标准:");
            course.getCourseTeachingPracticePlanVo().setPerformance(achievement);
            String scoreStandard = StringUtils.substringAfter(checkedContent,"记分标准:");
            course.getCourseTeachingPracticePlanVo().setStandard(scoreStandard);
            String requireOfContent = doCommonContents(requireOfContents);
            course.getCourseTeachingPracticePlanVo().setAsk(requireOfContent);

            course.setKnowledgeDomainVoList(domains);
            course.setStatus(CourseConstant.CUR_STATUS_NO);
            course.setAnalysisStatus(CourseConstant.CUR_ANA_STATUS_YES);
            curriculumService.saveCourse(course);
            return CourseConstant.ANALYSIS_SUCCESS;
        }catch (Exception e){
            e.printStackTrace();
            log.error(e.getMessage());
            return e.getMessage();
        }
    }

    @Override
    public String uploadFile(InputStream inputStream,String fileName,String categoryId){
        MultipartFile multipartFile = MultipartFileUtils.transform2Word(inputStream, fileName);
        FileInfo fileInfo = remoteFileInfoService.upload(multipartFile, categoryId).getData();
        return fileInfo.getFileId();
    }

    @Override
    public List getSubMajorList() {
        return standardMajorMapper.selectSubMajorList();
    }


    private CourseVo getCourse(Long courseId){
        CourseVo vo = new CourseVo();
        Course course = courseMapper.selectCourseById(courseId);
        BeanUtils.copyProperties(course,vo);

        CourseTeachingTheoryPlan theoryPlan = theoryPlanMapper.selectCourseTeachingTheoryPlanByCourseId(courseId);
        if(ObjectUtils.isNotEmpty(theoryPlan)){
            CourseTeachingTheoryPlanVo theoryPlanVo = new CourseTeachingTheoryPlanVo();
            BeanUtils.copyProperties(theoryPlan,theoryPlanVo);
            vo.setCourseTeachingTheoryPlanVo(theoryPlanVo);
        }

        CourseTeachingPracticePlan practicePlan = practicePlanMapper.selectCourseTeachingPracticePlanByCourseId(courseId);
        if(ObjectUtils.isNotEmpty(practicePlan)){
            CourseTeachingPracticePlanVo practicePlanVo = new CourseTeachingPracticePlanVo();
            BeanUtils.copyProperties(practicePlan,practicePlanVo);
            vo.setCourseTeachingPracticePlanVo(practicePlanVo);
        }

        return vo;
    }

    private XWPFDocument getXWPFDocument(String fileId){
        DataSet<byte[]> bytes = remoteFileInfoService.getFileById(fileId);
        InputStream input = new ByteArrayInputStream(bytes.getData());
        XWPFDocument document = WordPdfUtils.readWords(input);
        return document;
    }

    private boolean validateTable(List<XWPFTable> tables){
        if(CollectionUtils.isEmpty(tables)){
            return false;
        }
        for(XWPFTable table:tables){
            if(CollectionUtils.isEmpty(table.getRows())){
                return false;
            }
            if(CollectionUtils.isEmpty(table.getRow(0).getTableCells())){
                return false;
            }
            if(StringUtils.isBlank(table.getRow(0).getTableCells().get(0).getText())){
                return false;
            }
        }
        return true;
    }

    private String analysisTable(List<XWPFTable> tables, Long courseId,List<CourseChapterVo> courseChapters){
        JSONArray array = new JSONArray();
        for(XWPFTable table:tables){
            JSONObject jsonObject = null;
            if("章节".equals(table.getRow(0).getTableCells().get(0).getText())){
                jsonObject = getTheoryChapter(courseChapters,table,1,courseId,"章节");
            }else if("实践".equals(table.getRow(0).getTableCells().get(0).getText())){
                jsonObject = getTheoryChapter(courseChapters,table,2,courseId,"章节");
            }else if("专题项目".equals(table.getRow(0).getTableCells().get(0).getText())){
                jsonObject = getTheoryChapter(courseChapters,table,3,courseId,"章节");
            }
            if(ObjectUtils.isNotEmpty(jsonObject) && StringUtils.isNotBlank(jsonObject.toJSONString())){
                array.add(jsonObject);
            }
        }
        return array.toJSONString();
    }

    private JSONObject getTheoryChapter(List<CourseChapterVo> courseChapters,XWPFTable table,Integer type, Long courseId,String title) {
        JSONObject theoryChapter = new JSONObject();
        theoryChapter.put("isTable",true);

        //组成表头
        String headUUID1 = UUID.randomUUID().toString();
        String headUUID2 = UUID.randomUUID().toString();
        String headUUID3 = UUID.randomUUID().toString();
        JSONArray headers = new JSONArray();
        JSONObject head1 = new JSONObject();
        head1.put("title",title);
        head1.put("dataName",headUUID1);
        JSONObject head2 = new JSONObject();
        head2.put("title","内容");
        head2.put("dataName",headUUID2);
        JSONObject head3 = new JSONObject();
        head3.put("title","课程学时数");
        head3.put("dataName",headUUID3);
        headers.add(head1);
        headers.add(head2);
        headers.add(head3);
        theoryChapter.put("header",headers);

        JSONArray bodies = new JSONArray();

        //表内容
        List<XWPFTableRow> trs = table.getRows();
        for(int i=0;i<trs.size();i++){
            if(i == 0){
                continue;
            }
            XWPFTableRow row = trs.get(i);
            List<XWPFTableCell> cells = row.getTableCells();
            if(cells.size() != 3){
                continue;
            }
            if(!StringUtils.isNumeric(cells.get(2).getText())){
                continue;
            }
            CourseChapterVo chapter = new CourseChapterVo();
            chapter.setCourseId(courseId);
            chapter.setType(type);
            chapter.setName(cells.get(0).getText());
            chapter.setContent(cells.get(1).getText());
            courseChapters.add(chapter);
            JSONObject objData = new JSONObject();
            objData.put(headUUID1,cells.get(0).getText());
            objData.put(headUUID2,cells.get(1).getText());
            objData.put(headUUID3,cells.get(2).getText());
            bodies.add(objData);
        }
        theoryChapter.put("body",bodies);
        return theoryChapter;
    }

    private List<CourseTextbookVo> analysisBookTable(List<XWPFTable> tables, Long courseId){
        List<CourseTextbookVo> books = Lists.newArrayList();
        for(XWPFTable table:tables){
            String title = table.getRow(0).getTableCells().get(0).getText();
            if("名称".equals(title)){
                getBooks(books,table,courseId);
            }
        }
        return books;
    }

    private void getBooks(List<CourseTextbookVo> books,XWPFTable table,Long courseId){
        List<XWPFTableRow> trs = table.getRows();
        for(int i=0;i<trs.size();i++){
            if(i == 0){
                continue;
            }
            XWPFTableRow row = trs.get(i);
            List<XWPFTableCell> cells = row.getTableCells();
            CourseTextbookVo book = new CourseTextbookVo();
            book.setCourseId(courseId);
            book.setName(cells.get(0).getText());
            if("教材".equals(cells.get(1).getText())){
                book.setType(CourseTextbookConstant.BOOK_TYPE_TEACH);
            }
            if("参考书".equals(cells.get(1).getText())){
                book.setType(CourseTextbookConstant.BOOK_TYPE_REF);
            }
            book.setAuthor(cells.get(2).getText());
            book.setPressName(cells.get(3).getText());
            book.setPressVersion(cells.get(4).getText());
            book.setPressTime(cells.get(5).getText());
            books.add(book);
        }
    }

    private String doRunsContents(List<XWPFRun> runs){
        String result = "";
        for(XWPFRun run:runs){
            result += run.text();
        }
        return result;
    }

    private String doCommonContents(List<XWPFParagraph> paragraphs){
        String result = "";
        for(XWPFParagraph item:paragraphs){
            for(XWPFRun run:item.getRuns()){
                result += run.text();
            }
        }
        if(result.contains("：")){
            result = result.replace("：",":");
        }
        if(result.contains("，")){
            result = result.replace("，",",");
        }
        return result;
    }

    private String doTheoryChapterContents(List<XWPFParagraph> paragraphs){
        String result = "";
        for(XWPFParagraph item:paragraphs){
            for(XWPFRun run:item.getRuns()){
                result += run.text();
            }
            result +=  "@";
        }
        if(result.length()>1){
            result = result.substring(0,result.length()-1);
        }
        if(result.contains("：")){
            result = result.replace("：",":");
        }
        if(result.contains("，")){
            result = result.replace("，",",");
        }
        return result;
    }

    private void getTheoryChapterForStudy(String studyContent, KnowledgeDomainVo domain) {
        List<String> studies = getStudiesStr(studyContent);
        List<KnowledgeUnitVo> units = Lists.newArrayList();
        for(String study:studies){
            KnowledgeUnitVo unit = new KnowledgeUnitVo();
            List<KnowledgePointVo> result = Lists.newArrayList();
            //组装知识单元、知识点
            setStudyParam(study,unit,result);
            unit.setKnowledgePointVoList(result);
            units.add(unit);
        }
        domain.setKnowledgeUnitVoList(units);
    }

    private List<String> getStudiesStr(String studyContent){
        List<String> list = Lists.newArrayList();
        int i = 1;
        while(true){
            String str1 = StringUtils.substringBetween(studyContent,"知识单元" + i,"知识单元" + (i+1));
            if(StringUtils.isEmpty(str1)){
                String str2 = StringUtils.substringAfter(studyContent,"知识单元" +i);
                if(StringUtils.isNotEmpty(str2)){
                    if(str2.contains("综合实践单元")){
                        String[] lastStr = str2.split("综合实践单元");
                        if(lastStr.length == 2){
                            list.add("知识单元" + i + lastStr[0]);
                            list.add("综合实践单元" + lastStr[1]);
                        }
                    }else{
                        list.add("知识单元" + i + str2);
                    }
                }
                break;
            }
            list.add("知识单元" + i + str1);
            i++;
        }
        return list;
    }

    private void setStudyParam(String study, KnowledgeUnitVo unit,List<KnowledgePointVo> points) {
        if(study.contains("：")){
            study = study.replace("：",":");
        }
        if(study.contains("，")){
            study = study.replace("，",",");
        }
        String unitName = StringUtils.substringBetween(study,"知识单元","知识点");
        if(StringUtils.isBlank(unitName)){
            unitName = StringUtils.substringBetween(study,"综合实践单元","任务");
        }
        unit.setName(unitName);
        String unitPoint = StringUtils.substringBetween(study,"知识点","单元掌握程度:");
        //获取知识点
        if(StringUtils.isNotBlank(unitPoint)){
            getUnitPointStr("知识点" + unitPoint,"知识点",points);
        }else{
            unitPoint = StringUtils.substringBetween(study,"任务","单元掌握程度:");
            if(StringUtils.isNotBlank(unitPoint)){
                getUnitPointStr("任务" + unitPoint,"任务",points);
            }
        }

        String control = StringUtils.substringBetween(study,"单元掌握程度:","单元学习目标:");
        if(StringUtils.isNotBlank(control)){
            String[]  controls = control.split(",");
            String begin = controls[0];
            String[] begins = begin.split(":");
            if(begins.length == 2){
                List<SysDictData> courseAttrExamList = doinnerDictDataService.dictType(DomainFieldConstant.UNIT_DICT_ATTR_CSCD).getData();
                Map<String, String> courseAttrExamMap = courseAttrExamList.parallelStream().collect(Collectors.toMap(SysDictData::getDictLabel, SysDictData::getDictValue));
                if(ObjectUtils.isNotEmpty(courseAttrExamMap.get(begins[1]))){
                    unit.setInitLevel(Integer.parseInt(courseAttrExamMap.get(begins[1])));
                }
            }
            String require = controls[1];
            String[] requires = require.split(":");
            if(requires.length == 2){
                List<SysDictData> courseAttrExamList = doinnerDictDataService.dictType(DomainFieldConstant.UNIT_DICT_ATTR_YQCD).getData();
                Map<String, String> courseAttrExamMap = courseAttrExamList.parallelStream().collect(Collectors.toMap(SysDictData::getDictLabel, SysDictData::getDictValue));
                if(ObjectUtils.isNotEmpty(courseAttrExamMap.get(requires[1]))){
                    unit.setRequireLevel(Integer.parseInt(courseAttrExamMap.get(requires[1])));
                }
            }
        }
        String target = StringUtils.substringBetween(study,"单元学习目标:","单元学习实现环节:");
        if(StringUtils.isBlank(target)){
            target = StringUtils.substringBetween(study,"单元学习目标:","单元学习实现方式:");
        }
        if(StringUtils.isNotBlank(target)){
            String[] targets = target.split("\\d+、");
            String result = "";
            for(String str:targets){
                result += str + "@";
            }
            result = result.substring(0,result.length()-1);
            if(result.startsWith("@")){
                result = result.substring(1);
            }
            unit.setLearnTarget(result);
        }
        String realize = StringUtils.substringAfter(study,"单元学习实现环节:");
        if(StringUtils.isBlank(realize)){
            realize = StringUtils.substringAfter(study,"单元学习实现方式:");
        }
        if(StringUtils.isNotBlank(realize)){
            unit.setRealizeLink(realize);
        }
    }

    private void getUnitPointStr(String studyContent,String name,List<KnowledgePointVo> points){
        int i = 1;
        while(true){
            String str1 = StringUtils.substringBetween(studyContent,name + i,name + (i+1));
            if(StringUtils.isEmpty(str1)){
                String str2 = StringUtils.substringAfter(studyContent,name +i);
                if(StringUtils.isNotEmpty(str2)){
                    KnowledgePointVo point = new KnowledgePointVo();
                    point.setName(name + i + str2);
                    points.add(point);
                }
                break;
            }
            KnowledgePointVo point = new KnowledgePointVo();
            point.setName(name + i + str1);
            points.add(point);
            i++;
        }
    }

    private String getNumStr(String str){
        String result = "";
        str.trim();
        if(StringUtils.isNotBlank(str)){
            for(int i=0;i<str.length();i++){
                if(str.charAt(i) >= 48 && str.charAt(i) <= 57){
                    result += str.charAt(i);
                }
            }
        }
        if(StringUtils.isBlank(result)){
            result = "0";
        }
        return result;
    }

}
