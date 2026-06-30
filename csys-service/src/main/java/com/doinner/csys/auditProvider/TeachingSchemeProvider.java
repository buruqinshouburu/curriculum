package com.doinner.csys.auditProvider;



import com.doinner.csys.dao.StandardMajorMapper;
import com.doinner.csys.dao.TrainingSchemeCategoryMapper;
import com.doinner.csys.domain.StandardAbility;
import com.doinner.csys.domain.StandardMajor;
import com.doinner.csys.domain.TrainingSchemeCategory;
import com.doinner.csys.domain.vo.TrainingSchemeCourseVo;
import com.doinner.csys.domain.vo.TrainingSchemeVo;
import com.doinner.csys.service.StandardService;
import com.doinner.csys.service.TrainingService;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 * 培养规划审核内容提供者
 * 复用项目中已有的 KnowledgeSourceService
 */
@Component
public class TeachingSchemeProvider implements AuditContentProvider {

    @Autowired
    private TrainingService trainingService;
    @Autowired
    private TrainingSchemeCategoryMapper trainingSchemeCategoryMapper;
    @Autowired
    private StandardMajorMapper standardMajorMapper;
    @Override
    public String getBusinessType() {
        return "TRAINING_SCHEME_AUDIT";
    }

    @Override
    public Map<String, Object> getAuditContent(Long businessId) {
        TrainingSchemeVo trainingSchemeVo = trainingService.viewTrainingScheme(businessId, 1L);
        List<TrainingSchemeCourseVo> trainingSchemeCourseVos = trainingService.selectTrainingSchemeCoursesById(businessId);
        if (trainingSchemeVo == null) {
            throw new RuntimeException("培养方案不存在：" + businessId);
        }
        trainingSchemeVo.setCourseVos(trainingSchemeCourseVos);
        Map<String, Object> content = new HashMap<>();
        content.put("trainingSchemeVo", trainingSchemeVo);
        if(ObjectUtils.isNotEmpty(trainingSchemeVo.getCategoryId())){
            //查询课程大类名称
            TrainingSchemeCategory trainingSchemeCategory = trainingSchemeCategoryMapper.selectTrainingSchemeCategoryById(trainingSchemeVo.getCategoryId());
            trainingSchemeVo.setCategoryName(trainingSchemeCategory.getName());
        }
        if(ObjectUtils.isNotEmpty(trainingSchemeVo.getMajorId())){
            //查询专业类名称
            StandardMajor standardMajor = standardMajorMapper.selectStandardMajorById(trainingSchemeVo.getMajorId());
            trainingSchemeVo.setMajorName(standardMajor.getName());
        }

        return content;
    }

    @Override
    public String getContentTitle(Long businessId) {
        TrainingSchemeVo trainingSchemeVo = trainingService.viewTrainingScheme(businessId, 1L);
        return trainingSchemeVo != null ? trainingSchemeVo.getName() : "未知培养规划";
    }
}