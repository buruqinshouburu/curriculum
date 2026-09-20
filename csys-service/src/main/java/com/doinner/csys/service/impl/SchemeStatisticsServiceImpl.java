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
import com.doinner.kg.domain.Dictionary;
import com.doinner.kg.service.RemoteKgService;
import com.google.common.collect.Lists;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    @Resource
    private RemoteKgService remoteKgService;

    /** 课程模块及其子模块使用同一知识图谱字典。 */
    @Value("${kg.dictionary.courseModuleType:69a7f3162dc370362ef3ee6d}")
    private String kgCourseModuleType;

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
        double totalCredit = creditStatisticsVoList.stream()
                .map(CreditStaticticsVo::getCreditCount)
                .filter(java.util.Objects::nonNull)
                .mapToDouble(Double::doubleValue)
                .sum();
        for (CreditStaticticsVo statistic : creditStatisticsVoList) {
            statistic.setTerm(DomainFieldConstant.TERM_NUMBER_NAME_MAP.get(statistic.getTermId()));
            if (statistic.getCreditCount() == null) {
                statistic.setCreditCount(0.0);
            }
            statistic.setPercent(totalCredit <= 0 ? "0.00"
                    : String.format(java.util.Locale.ROOT, "%.2f", statistic.getCreditCount() * 100 / totalCredit));
        }
        return creditStatisticsVoList.stream()
                .sorted(Comparator.comparing(CreditStaticticsVo::getTermId, Comparator.nullsLast(Integer::compareTo)))
                .collect(Collectors.toList());
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
        voList.forEach(vo -> {
            vo.setTermName(DomainFieldConstant.TERM_NUMBER_NAME_MAP.get(vo.getTerm().intValue()));
        });
        return voList.stream()
                .sorted(Comparator.comparingLong(TrainingSchemeCourseScheduleStatisticsVo::getTerm))
                .collect(Collectors.toList());
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
                    excelVo.setNumber1((double) vo.getsTeachHours());
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
    public List<CourseModuleStatisticsVo> courseModule(Long schemeId) {
        return courseModule(schemeId, getCourseModuleNameMap());
    }

    private List<CourseModuleStatisticsVo> courseModule(Long schemeId, Map<String, String> moduleNameMap) {
        List<CourseModuleStatisticsVo> statistics = trainingSchemeMapper.countCourseByModule(schemeId);
        long totalCourseCount = statistics.stream()
                .map(CourseModuleStatisticsVo::getCourseCount)
                .filter(java.util.Objects::nonNull)
                .mapToLong(Long::longValue)
                .sum();
        for (CourseModuleStatisticsVo statistic : statistics) {
            statistic.setTotalCourseCount(totalCourseCount);
            String moduleId = statistic.getModuleId();
            if (moduleId == null || moduleId.trim().isEmpty()) {
                statistic.setModuleName("未设置课程模块");
            } else {
                statistic.setModuleName(moduleNameMap.getOrDefault(moduleId, moduleId));
            }
        }
        return statistics.stream()
                .sorted(Comparator.comparing(CourseModuleStatisticsVo::getModuleName,
                        Comparator.nullsLast(String::compareTo)))
                .collect(Collectors.toList());
    }

    @Override
    public List<StatisticsExcelMultiVo> courseModuleIn(List<Long> schemeIds) {
        List<StatisticsExcelMultiVo> result = Lists.newArrayList();
        Map<String, String> moduleNameMap = getCourseModuleNameMap();
        schemeIds.forEach(schemeId -> {
            List<CourseModuleStatisticsVo> statistics = courseModule(schemeId, moduleNameMap);
            if (CollectionUtils.isEmpty(statistics)) {
                return;
            }
            TrainingSchemeVo scheme = trainingSchemeMapper.selectTrainingSchemeById(schemeId);
            StatisticsExcelMultiVo multiVo = new StatisticsExcelMultiVo();
            multiVo.setSchemeName(scheme.getProgramName());
            multiVo.setStatisticsExcelVos(statistics.stream()
                    .map(StatisticsExcelVo::new)
                    .collect(Collectors.toList()));
            result.add(multiVo);
        });
        return result;
    }

    /**
     * 课程模块为 KG 字典 ID。名称解析异常不能阻断统计，未解析时由调用方回传原 ID。
     */
    private Map<String, String> getCourseModuleNameMap() {
        Map<String, String> moduleNameMap = new HashMap<>();
        try {
            List<Dictionary> dictionaries = remoteKgService.findDictionaryByType(kgCourseModuleType).getData();
            if (CollectionUtils.isNotEmpty(dictionaries)) {
                for (Dictionary dictionary : dictionaries) {
                    if (dictionary != null && dictionary.getId() != null && dictionary.getName() != null) {
                        moduleNameMap.put(dictionary.getId().toString(), dictionary.getName());
                    }
                }
            }
        } catch (RuntimeException ignored) {
            // 远程字典暂不可用时仍返回统计结果，模块 ID 作为名称回退。
        }
        return moduleNameMap;
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
