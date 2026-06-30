package com.doinner.csys.service.impl;

import com.doinner.csys.constant.DomainFieldConstant;
import com.doinner.csys.dao.StandardCultivationTargetMapper;
import com.doinner.csys.dao.StandardMajorMapper;
import com.doinner.csys.dao.TrainingSchemeCourseScheduleMapper;
import com.doinner.csys.dao.TrainingSchemeMapper;
import com.doinner.csys.domain.StandardMajor;
import com.doinner.csys.domain.TrainingScheme;
import com.doinner.csys.domain.statisticsVo.*;
import com.doinner.csys.domain.vo.TrainingSchemeVo;
import com.doinner.csys.domain.vo.TreeTableVo;
import com.doinner.csys.service.SchemeStatisticsService;
import com.google.common.collect.Lists;
import com.google.common.util.concurrent.AtomicDouble;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class SchemeStatisticsServiceImpl implements SchemeStatisticsService {

    @Resource
    private TrainingSchemeMapper trainingSchemeMapper;
    @Resource
    private StandardMajorMapper standardMajorMapper;
    @Resource
    private StandardCultivationTargetMapper standardCultivationTargetMapper;
    @Resource
    private TrainingSchemeCourseScheduleMapper trainingSchemeCourseScheduleMapper;

    @Override
    public List<TrainingScheme> schemeSub(Long majorId) {
        if (ObjectUtils.isEmpty(majorId)) {
            return null;
        }
        StandardMajor standardMajor = standardMajorMapper.selectStandardMajorById(majorId);
        if (ObjectUtils.isEmpty(standardMajor)) {
            return null;
        }
        StandardMajor _standardMajor = new StandardMajor();
        _standardMajor.setParentId(majorId);
        List<StandardMajor> standardMajorList = standardMajorMapper.selectStandardMajorList(_standardMajor);
        TreeTableVo rootNode = new TreeTableVo(standardMajor, standardMajorList);
        TrainingScheme trainingScheme = new TrainingScheme();
        trainingScheme.setMajorId(majorId);
        List<TrainingScheme> trainingSchemes = trainingSchemeMapper.selectTrainingSchemeCategoryList(trainingScheme);
        return trainingSchemes;
//        if (ObjectUtils.isEmpty(trainingSchemes)) {
//            return rootNode;
//        }
//        Map<Long, List<TrainingScheme>> trainingSchemeMap = trainingSchemes.parallelStream().collect(Collectors.groupingBy(TrainingScheme::getSubMajorId));
//        rootNode.getChildren().parallelStream().forEach(child -> {
//            if (!trainingSchemeMap.containsKey(child.getId())) {
//                return;
//            }
//            List<TrainingScheme> _trainingSchemes = trainingSchemeMap.get(child.getId());
//            List<TreeTableVo> typeList = child.getChildren();
//            Map<Long, TreeTableVo> typeMap = typeList.parallelStream().collect(Collectors.toMap(type -> Long.valueOf(type.getId()), type -> type));
//            _trainingSchemes.parallelStream().forEach(_trainingScheme -> {
//                if (!typeMap.containsKey(_trainingScheme.getClassId())) {
//                    return;
//                }
//                typeMap.get(_trainingScheme.getClassId()).getChildren().add(new TreeTableVo(_trainingScheme));
//            });
//        });
//        return rootNode;
    }


    @Override
    public List<CreditStaticticsVo> statisticsCredit(Long schemeId) {

        List<CreditStaticticsVo> creditStatisticsVoList = trainingSchemeMapper.sumCreditBySchemeId(schemeId);
        AtomicDouble atomicDouble = new AtomicDouble();
        creditStatisticsVoList.parallelStream().forEach(creditStatisticsVo -> {
            creditStatisticsVo.setTerm(DomainFieldConstant.TERM_NUMBER_NAME_MAP.get(creditStatisticsVo.getTermId()));
            if(ObjectUtils.isNotEmpty(creditStatisticsVo.getCreditCount())) {
                atomicDouble.addAndGet(creditStatisticsVo.getCreditCount());
            }
        });
        creditStatisticsVoList.parallelStream().forEach(creditStatisticsVo -> {
            if(ObjectUtils.isEmpty(creditStatisticsVo.getCreditCount())){
                creditStatisticsVo.setPercent("0");
            }else {
                creditStatisticsVo.setPercent(String.format("%.2f", creditStatisticsVo.getCreditCount() * 100 / atomicDouble.get()));
            }
        });
        return creditStatisticsVoList.parallelStream().sorted(Comparator.comparingInt(CreditStaticticsVo::getTermId)).collect(Collectors.toList());
    }

    @Override
    public List<StatisticsExcelMultiVo> statisticsCreditIn(List<Long> schemeIds) {
        List<StatisticsExcelMultiVo> result = Lists.newArrayList();
        schemeIds.stream().forEach(schemeId ->{
            StatisticsExcelMultiVo multiVo = new StatisticsExcelMultiVo();
            TrainingSchemeVo scheme = trainingSchemeMapper.selectTrainingSchemeById(schemeId);
            multiVo.setSchemeName(scheme.getProgramName());
            List<CreditStaticticsVo> creditStatisticsVoList = statisticsCredit(schemeId);
            if(CollectionUtils.isNotEmpty(creditStatisticsVoList)){
                List<StatisticsExcelVo> statisticsExcelVoList = creditStatisticsVoList.parallelStream().map(StatisticsExcelVo::new).collect(Collectors.toList());
                multiVo.setStatisticsExcelVos(statisticsExcelVoList);
                result.add(multiVo);
            }
        });
        return result;
    }

    @Override
    public List<StandardCultivationTargetStatisticsVo> selectTargetPropUpBySchemeId(Long schemeId) {
        List<StandardCultivationTargetStatisticsVo> voList = standardCultivationTargetMapper.selectTargetPropUpBySchemeId(schemeId);
        return filterTargetList(voList);
    }

    @Override
    public List<TrainingSchemeCourseScheduleStatisticsVo> selectHoursBySchemeId(Long schemeId) {
        List<TrainingSchemeCourseScheduleStatisticsVo> voList = trainingSchemeCourseScheduleMapper.selectHoursBySchemeId(schemeId);
        Map<Long, TrainingSchemeCourseScheduleStatisticsVo> hoursMap = new ConcurrentHashMap<>();
        voList.forEach(vo -> {
            Long term = vo.getTerm();
            TrainingSchemeCourseScheduleStatisticsVo mapVo = hoursMap.get(term);
            if (ObjectUtils.isEmpty(mapVo)) {
                TrainingSchemeCourseScheduleStatisticsVo newMapVo = new TrainingSchemeCourseScheduleStatisticsVo();
                newMapVo.setSchemeId(vo.getSchemeId());
                newMapVo.setTerm(vo.getTerm());
                newMapVo.setTermName(DomainFieldConstant.termName.get(vo.getTerm().intValue()));
                newMapVo.setsTheoryHours(0L);
                newMapVo.setsPracticeHours(0L);
                newMapVo = addHours(vo, newMapVo);
                hoursMap.put(newMapVo.getTerm(), newMapVo);
            } else {
                mapVo = addHours(vo, mapVo);
                hoursMap.put(mapVo.getTerm(), mapVo);
            }
        });
        return new ArrayList<>(hoursMap.values());

    }

    @Override
    public List<StatisticsExcelMultiVo> selectHoursBySchemeIdIn(List<Long> schemeIds) {
        List<StatisticsExcelMultiVo> result = Lists.newArrayList();
        schemeIds.stream().forEach(schemeId ->{
            StatisticsExcelMultiVo multiVo = new StatisticsExcelMultiVo();
            TrainingSchemeVo scheme = trainingSchemeMapper.selectTrainingSchemeById(schemeId);
            multiVo.setSchemeName(scheme.getProgramName());
            List<TrainingSchemeCourseScheduleStatisticsVo> voList = selectHoursBySchemeId(schemeId);
            if(CollectionUtils.isNotEmpty(voList)){
                List<StatisticsExcelVo> statisticsExcelVoList = new ArrayList<>();
                for (TrainingSchemeCourseScheduleStatisticsVo vo : voList) {
                    StatisticsExcelVo excelVo = new StatisticsExcelVo();
                    excelVo.setName(DomainFieldConstant.TERM_NUMBER_NAME_MAP.get((vo.getTerm()).intValue()));
                    excelVo.setNumber1((double) vo.getsTheoryHours());
                    excelVo.setNumber2((double) vo.getsPracticeHours());
                    statisticsExcelVoList.add(excelVo);
                }
                multiVo.setStatisticsExcelVos(statisticsExcelVoList);
                result.add(multiVo);
            }
        });
        return result;

    }



    @Override
    public List<StandardCultivationTargetStatisticsVo> selectCourseTypeBySchemeId(Long schemeId) throws Exception {
        List<StandardCultivationTargetStatisticsVo> voList1 = trainingSchemeCourseScheduleMapper.selectCourseTypeBySchemeId(schemeId);
        AtomicReference<Long> denominatorCount = new AtomicReference<>(0L);
        for (StandardCultivationTargetStatisticsVo vo1 : voList1) {
            denominatorCount.updateAndGet(v -> v + vo1.getNumeratorCount());
            vo1.setDenominatorCountS(denominatorCount);
            vo1.setDenominatorName(DomainFieldConstant.courseType.get(Math.toIntExact(vo1.getDenominatorId())));
            //写死
            if (vo1.getDenominatorId().equals(1L)) {
                List<SubCourseVo> subCourseVoList = new ArrayList<>();
                for (int i = 0; i < 4; i++) {
                    SubCourseVo vo = new SubCourseVo();
                    vo.setDenominatorCount(21L);
                    vo.setNumeratorCount(DomainFieldConstant.subCourseValue.get(i));
                    vo.setDenominatorName(DomainFieldConstant.subCourseName.get(i));
                    subCourseVoList.add(vo);
                }
                vo1.setSubCourseVoList(subCourseVoList);
            }
        }
        return voList1;
    }

    @Override
    public List<StandardCultivationTargetStatisticsMultiVo> selectCourseTypeBySchemeIdIn(List<Long> schemeIds){
        List<StandardCultivationTargetStatisticsMultiVo> result = Lists.newArrayList();
        schemeIds.stream().forEach(schemeId ->{
            StandardCultivationTargetStatisticsMultiVo multiVo = new StandardCultivationTargetStatisticsMultiVo();
            TrainingSchemeVo scheme = trainingSchemeMapper.selectTrainingSchemeById(schemeId);
            multiVo.setSchemeName(scheme.getProgramName());
            try{
                List<StandardCultivationTargetStatisticsVo> voList1 = selectCourseTypeBySchemeId(schemeId);
                if(CollectionUtils.isNotEmpty(voList1)){
                    multiVo.setStandardCultivationTargetStatisticsVoList(voList1);
                    result.add(multiVo);
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        });
        return result;
    }

    public TrainingSchemeCourseScheduleStatisticsVo addHours(TrainingSchemeCourseScheduleStatisticsVo vo, TrainingSchemeCourseScheduleStatisticsVo mapVo) {
        Long sTHours = vo.getsTheoryHours();
        Long sPHours = vo.getsPracticeHours();
        Long cTHours = vo.getcTheoryHours();
        Long cPHours = vo.getcPracticeHours();
        Long mapTHours = mapVo.getsTheoryHours();
        Long mapSPHours = mapVo.getsPracticeHours();
        if (ObjectUtils.isEmpty(sTHours)) {
            mapTHours += cTHours;
        } else {
            mapTHours += sTHours;
        }
        if (ObjectUtils.isEmpty(sPHours)) {
            mapSPHours += cPHours;
        } else {
            mapSPHours += sPHours;
        }
        mapVo.setsTheoryHours(mapTHours);
        mapVo.setsPracticeHours(mapSPHours);
        return mapVo;
    }

    public List<StandardCultivationTargetStatisticsVo> filterTargetList(List<StandardCultivationTargetStatisticsVo> voList) {
        return voList.parallelStream().filter(vo -> DomainFieldConstant.filterTargetList.contains(vo.getDenominatorName())).collect(Collectors.toList());
    }

    @Override
    public List<CourseTypeVo> courseType(Long schemeId) {
        List<CourseTypeVo> courseTypeVoList = trainingSchemeMapper.countCourseByType(schemeId);
        return courseTypeVoList.parallelStream().sorted(Comparator.comparingInt(CourseTypeVo::getTermId)).collect(Collectors.toList());
    }

    @Override
    public List<StatisticsExcelMultiVo> courseTypeIn(List<Long> schemeIds) {
        List<StatisticsExcelMultiVo> result = Lists.newArrayList();
        schemeIds.stream().forEach(schemeId ->{
            StatisticsExcelMultiVo multiVo = new StatisticsExcelMultiVo();
            TrainingSchemeVo scheme = trainingSchemeMapper.selectTrainingSchemeById(schemeId);
            multiVo.setSchemeName(scheme.getProgramName());
            List<CourseTypeVo> courseTypeVoList = courseType(schemeId);
            if(CollectionUtils.isNotEmpty(courseTypeVoList)){
                List<StatisticsExcelVo> statisticsExcelVoList = courseTypeVoList.parallelStream().map(StatisticsExcelVo::new).collect(Collectors.toList());
                multiVo.setStatisticsExcelVos(statisticsExcelVoList);
                result.add(multiVo);
            }
        });
        return result;
    }

}
