package com.doinner.csys.auditProvider;

import com.doinner.csys.dao.TeachingPlanMapper;
import com.doinner.csys.domain.TeachingPlan;
import com.doinner.csys.domain.vo.TeachingPlanDetailVo;
import com.doinner.csys.domain.vo.TeachingPlanSchemeVo;
import com.doinner.csys.service.TeachingPlanModuleService;
import com.doinner.csys.service.TeachingPlanService;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 课程教学计划审核内容提供者。
 * businessType = TEACHING_PLAN_AUDIT；tab 维度为培养方案 schemeId。
 */
@Component
public class TeachingPlanProvider implements AuditContentProvider {

    public static final String BUSINESS_TYPE = "TEACHING_PLAN_AUDIT";

    @Resource
    private TeachingPlanMapper teachingPlanMapper;

    @Resource
    private TeachingPlanService teachingPlanService;

    @Resource
    private TeachingPlanModuleService teachingPlanModuleService;

    @Override
    public String getBusinessType() {
        return BUSINESS_TYPE;
    }

    @Override
    public Map<String, Object> getAuditContent(Long businessId) {
        TeachingPlan plan = teachingPlanMapper.selectById(businessId);
        if (plan == null) {
            throw new RuntimeException("教学计划不存在：" + businessId);
        }
        TeachingPlanDetailVo detail = teachingPlanService.getDetail(plan.getSourceCourseId(), plan.getId());
        List<TeachingPlanSchemeVo> schemes = teachingPlanModuleService.listSchemes(plan.getSourceCourseId());

        Map<String, Object> content = new HashMap<>();
        content.put("plan", plan);
        content.put("detail", detail);
        content.put("schemes", schemes);
        content.put("teachers", teachingPlanService.listTeacher(businessId));
        content.put("sections", teachingPlanService.listSection(businessId));
        content.put("contents", teachingPlanModuleService.listContent(businessId));
        content.put("practiceItems", teachingPlanModuleService.listPracticeItem(businessId));
        content.put("assessments", teachingPlanModuleService.listAssessment(businessId));
        content.put("textbooks", teachingPlanModuleService.listTextbook(businessId));
        content.put("conditions", teachingPlanModuleService.listCondition(businessId));
        content.put("processSteps", teachingPlanModuleService.listProcessStep(businessId));
        content.put("refs", teachingPlanModuleService.listRef(businessId, null));
        if (ObjectUtils.isNotEmpty(schemes)) {
            Long schemeId = schemes.get(0).getSchemeId();
            content.put("defaultSchemeId", schemeId);
            content.put("objectives", teachingPlanModuleService.listObjective(businessId, schemeId));
        }
        return content;
    }

    @Override
    public String getContentTitle(Long businessId) {
        TeachingPlan plan = teachingPlanMapper.selectById(businessId);
        if (plan == null) {
            return "未知教学计划";
        }
        if (StringUtils.isNotBlank(plan.getSourceCourseName())) {
            return plan.getSourceCourseName() + "教学计划";
        }
        return "教学计划#" + businessId;
    }
}
