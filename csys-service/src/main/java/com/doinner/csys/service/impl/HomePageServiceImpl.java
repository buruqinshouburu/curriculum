package com.doinner.csys.service.impl;

import com.alibaba.nacos.shaded.org.checkerframework.checker.units.qual.K;
import com.doinner.csys.dao.*;
import com.doinner.csys.domain.StandardMajor;
import com.doinner.csys.domain.TrainingSchemeWeek;
import com.doinner.csys.domain.vo.CourseAndSpecializedVo;
import com.doinner.csys.domain.vo.HourStatisticsVo;
import com.doinner.csys.domain.vo.OverQuoteCourseInfo;
import com.doinner.csys.io.utils.MultiSimpleExcelHandler;
import com.doinner.csys.io.utils.SimpleExcelHandler;
import com.doinner.csys.service.HomePageService;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * @author wzg
 * @date 2023/4/3 10:20
 */
@Service
public class HomePageServiceImpl implements HomePageService {
    @Resource
    private CourseMapper courseMapper;
    @Resource
    private TrainingSchemeMapper trainingSchemeMapper;
    @Resource
    private StandardMajorMapper standardMajorMapper;
    @Resource
    private TrainingSchemeCategoryMapper trainingSchemeCategoryMapper;
    @Resource
    private WordCloudHandler wordCloudHandler;
    @Resource
    private TrainingSchemeWeekMapper trainingSchemeWeekMapper;

    @Override
    public List<CourseAndSpecializedVo> courseAndSpecializedStatistics() {
        List<CourseAndSpecializedVo> courseAndSpecializedVoList = courseMapper.courseAndSpecializedStatistics();
//        if (CollectionUtils.isNotEmpty(courseAndSpecializedVoList)){
//            for (CourseAndSpecializedVo courseAndSpecializedVo : courseAndSpecializedVoList) {
//                List<Long> majorIds = this.getStandardMajorList(courseAndSpecializedVo.getCollectId()).stream().map(StandardMajor::getId).filter(ObjectUtils::isNotEmpty).collect(Collectors.toList());;
//                HourStatisticsVo hourStatisticsVo = trainingSchemeMapper.classHourStatisticsList(majorIds);
//                courseAndSpecializedVo.setCourseCount(hourStatisticsVo.getCourseCount());
//            }
//        }
        return courseAndSpecializedVoList;
    }

    @Override
    public List categoryAndSchedule() {
        // TODO 四大类和细门类（count）

        List<Long> categoryIds = new ArrayList<>();
        //根据细门类查询培养方案总数 categoryIds
        Long aLong = trainingSchemeMapper.selectSchemeCountByCategoryIds(categoryIds);

        return null;
    }

    @Override
    public List<StandardMajor> selectMajorBySubCategory(Long categoryId){
        List<StandardMajor> standardMajorList = standardMajorMapper.selectStandardMajorByCategory(categoryId);
        return standardMajorList;
    }

    @Override
    public List<StandardMajor> selectMajorBySubCategories(List<Long> categoryIds){
        List<StandardMajor> standardMajorList = standardMajorMapper.selectStandardMajorByCategories(categoryIds);
        return standardMajorList;
    }

    @Override
    public List<StandardMajor> selectMajorBySystemId(Long systemId) {
        List<Long> categoryIds = trainingSchemeCategoryMapper.selectTrainingSchemeCategoryIdsBySystemId(systemId);
        List<StandardMajor> standardMajorList = selectMajorBySubCategories(categoryIds);
        return standardMajorList;
    }

    @Override
    public Map<String, Long> countSchemeAndCourse() {

        Long courseCount = courseMapper.countCourse();
        Long schemeCount = trainingSchemeMapper.countMajorHasScheme();
        Map<String, Long> countMap = Map.of("courseCount", courseCount, "schemeCount", schemeCount);
        return countMap;
    }

    @Override
    public List<HourStatisticsVo> classAllHourStatistics() {

        List<HourStatisticsVo> hourStatisticsVoList = trainingSchemeMapper.countAndSumHoursGroupByCollege();
        return hourStatisticsVoList;
    }

    @Override
    public List<HourStatisticsVo> classHourStatistics(Long collegeId) {

        List<HourStatisticsVo> hourStatisticsVoList = trainingSchemeMapper.countAndSumHoursByCollegeId(collegeId);
        return hourStatisticsVoList;

//        List<HourStatisticsVo> hourStatisticsVoList = new ArrayList<>();
//        List<StandardMajor> standardMajorList = this.getStandardMajorList(collegeId);
//        if (CollectionUtils.isNotEmpty(standardMajorList)){
//            for (StandardMajor standardMajor : standardMajorList) {
//                List<HourStatisticsVo> courseIdList = trainingSchemeMapper.classHourStatistics(standardMajor.getId());
//                HourStatisticsVo hourStatisticsVo = new HourStatisticsVo();
//                hourStatisticsVo.setCollegeId(collegeId);
//                hourStatisticsVo.setMajorId(standardMajor.getId());
//                hourStatisticsVo.setMajorName(standardMajor.getName());
//                //去重
//                Long hourCount =0L;
//                Long courseCount =0L;
//                List<Long> courseIds = new ArrayList<>();
//                if (CollectionUtils.isNotEmpty(courseIdList)){
//                    for (HourStatisticsVo statisticsVo : courseIdList) {
//                        if (!courseIds.contains(statisticsVo.getCourseId())){
//                            if (statisticsVo.getHours() !=null){
//                                hourCount+=statisticsVo.getHours();
//                            }
//                            courseCount++;
//                            courseIds.add(statisticsVo.getCourseId());
//                        }
//                    }
//                }
//                hourStatisticsVo.setHourCount(hourCount);
//                hourStatisticsVo.setCourseCount(courseCount);
//                hourStatisticsVoList.add(hourStatisticsVo);
//            }
//        }
//        return hourStatisticsVoList;
    }

    public List<StandardMajor> getStandardMajorList(Long collectId){
        StandardMajor requestStandardMajor = new StandardMajor();
        requestStandardMajor.setCollegeId(collectId);
        List<StandardMajor> standardMajorList = standardMajorMapper.selectStandardMajorList(requestStandardMajor);
        return standardMajorList;
    }


    @Override
    public Map<String, AtomicInteger> standardTargetWordCloud(Integer limit){
//        wordCloudHandler.random();
        Map<String, AtomicInteger> standardCultivationTargetData = WordCloudHandler.standardCultivationTargetData;
        standardCultivationTargetData = standardCultivationTargetData.entrySet().stream().sorted(Comparator.comparingInt(entry -> ((Map.Entry<String, AtomicInteger>)entry).getValue().get()).reversed())
                .limit(limit).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        return standardCultivationTargetData;
    }


    @Override
    public Map<String, Map<String, AtomicInteger>> standardGraduationWordCloud(Integer limit){
//        wordCloudHandler.random();
        Map<String, Map<String, AtomicInteger>> standardGraduationData = WordCloudHandler.standardGraduationData;
        if (standardGraduationData == null){
            return null;
        }
        standardGraduationData.keySet().forEach(key -> {
            Map<String, AtomicInteger> data = standardGraduationData.get(key);
            data = data.entrySet().stream().sorted(Comparator.comparingInt(entry -> ((Map.Entry<String, AtomicInteger>)entry).getValue().get()).reversed())
                    .limit(limit).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
            standardGraduationData.put(key, data);
        });
        return standardGraduationData;
    }

    @Override
    public void standardTargetWordCloudExport(HttpServletResponse response) {
        Map<String, AtomicInteger> data = standardTargetWordCloud(100000);
        List<Map.Entry<String, AtomicInteger>> entryList = data.entrySet().parallelStream()
                .sorted(Comparator.comparingInt(entry -> ((Map.Entry<String, AtomicInteger>)entry).getValue().get()).reversed())
                .collect(Collectors.toList());
        SimpleExcelHandler<Map.Entry> simpleExcelHandler = new SimpleExcelHandler(entryList);
        XSSFWorkbook xssfWorkbook = simpleExcelHandler.addHeader("短语").addMappingFunction(Map.Entry::getKey)
                .addHeader("词频").addMappingFunction(Map.Entry::getValue).setSheetName("词频").writeToExcel();
        try {
            xssfWorkbook.write(response.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void standardGraduationWordCloudExport(HttpServletResponse response) {
        Map<String, Map<String, AtomicInteger>> data = standardGraduationWordCloud(100000);
        Map<String, List<Map.Entry<String, AtomicInteger>>> frequencyMap = data.entrySet().stream().collect(Collectors.toMap(entry -> entry.getKey(), entry -> entry.getValue().entrySet().parallelStream()
                .sorted(Comparator.comparingInt(_entry -> ((Map.Entry<String, AtomicInteger>) _entry).getValue().get()).reversed())
                .collect(Collectors.toList())
        ));

        MultiSimpleExcelHandler<Map.Entry> multiSimpleExcelHandler = new MultiSimpleExcelHandler(List.of("知识", "素质", "能力"), frequencyMap);
        XSSFWorkbook xssfWorkbook = multiSimpleExcelHandler.addHeader("短语").addMappingFunction(Map.Entry::getKey)
                .addHeader("词频").addMappingFunction(Map.Entry::getValue).writeToExcel();
        try {
            xssfWorkbook.write(response.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Map<String,Object> selectCourseQuoteInfo(String version) {
        //查询课程总库中全部的公共基础课
        HashMap<String, Object> data = new HashMap<>();
        Integer generateCourseCount=courseMapper.selectGenerateCourse(version);
        //查询超出承载课程数的课程
        List<Long> ids =courseMapper.selectOverQuoteCourse(version);
        data.put("generateCourseCount",generateCourseCount);
        data.put("overQuoteCourseCount",ids.size());
        data.put("overQuoteCourseIds",ids);
        return data;
    }

    @Override
    public List<OverQuoteCourseInfo> selectCourseQuoteInfoDetail(List<Long> ids) {
        List<OverQuoteCourseInfo> resultList = new ArrayList<>();
        if(ObjectUtils.isNotEmpty(ids)){
            resultList=courseMapper.selectQuoteCourseInfo(ids);
        }
        return resultList;
    }

    @Override
    public TrainingSchemeWeek selectWeekBySchemeId(Long schemeId) {
        TrainingSchemeWeek  week = trainingSchemeWeekMapper.selectTrainingSchemeWeekBySchemeId(schemeId);
        if(ObjectUtils.isEmpty(week)){
            week = new TrainingSchemeWeek();
            week.setSchemeId(schemeId);
            week.setCourseTeaching(148L);
            week.setPracticeTeaching(31L);
            week.setVacation(20L);
            week.setMotorDriven(4L);
            trainingSchemeWeekMapper.insertTrainingSchemeWeek(week);
        }
        return week;
    }

    @Override
    public void updateTrainingSchemeWeek(TrainingSchemeWeek trainingSchemeWeek) {
        trainingSchemeWeekMapper.updateTrainingSchemeWeek(trainingSchemeWeek);
    }
}
