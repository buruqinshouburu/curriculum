package com.doinner.csys.service.impl;

import com.doinner.csys.dao.StandardMajorMapper;
import com.doinner.csys.dao.TrainingSchemeCategoryMapper;
import com.doinner.csys.dao.TrainingSchemeMapper;
import com.doinner.csys.domain.vo.CategoryCountVo;
import com.doinner.csys.domain.vo.MajorCountVo;
import com.doinner.csys.domain.vo.SchemeCountVo;
import com.doinner.csys.service.MajorStatisticsService;
import com.doinner.system.domain.entity.SysDictData;
import com.doinner.system.service.DoinnerDictDataService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class MajorStatisticsServiceImpl implements MajorStatisticsService {
    @Resource
    private StandardMajorMapper standardMajorMapper;
    @Resource
    private TrainingSchemeMapper trainingSchemeMapper;
    @Resource
    private TrainingSchemeCategoryMapper trainingSchemeCategoryMapper;
    @Resource
    private DoinnerDictDataService doinnerDictDataService;

    @Override
    public List<MajorCountVo> majorCount() {
        return standardMajorMapper.majorCount();
    }

    @Override
    public List<SchemeCountVo> schemeCount() {
        return trainingSchemeMapper.schemeCount();
    }

    @Override
    public List<CategoryCountVo> systemTypeCount() {
        List<CategoryCountVo> categoryCountVos = trainingSchemeCategoryMapper.majorCount();
        Map<String, String> systemTypeMap = systemTypeMap();
        categoryCountVos.stream().forEach(categoryCountVo -> {
            categoryCountVo.setSystemName(systemTypeMap.get(categoryCountVo.getSystemId().toString()));
        });
        return categoryCountVos;
    }

    private Map<String, String> systemTypeMap(){
        String maxMajor = "kg_system_type";
        List<SysDictData> systemTypes = doinnerDictDataService.dictType(maxMajor).getData();
        Map<String, String> maxMajorMap = systemTypes.stream().collect(Collectors.toMap(sysDictData -> sysDictData.getDictValue(), sysDictData -> sysDictData.getDictLabel()));
        return maxMajorMap;
    }
}
