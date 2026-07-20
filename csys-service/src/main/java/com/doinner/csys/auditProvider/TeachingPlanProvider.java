package com.doinner.csys.auditProvider;

import com.doinner.csys.dao.TeachingPlanContextMapper;
import com.doinner.csys.dao.TeachingPlanMapper;
import com.doinner.csys.domain.TeachingPlan;
import com.doinner.csys.domain.TeachingPlanContext;
import com.doinner.csys.domain.vo.TeachingPlanDetailVo;
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
 * <p>
 * 业务类型与流程编码一致：TEACHING_PLAN_AUDIT（与培养方案 TRAINING_SCHEME_AUDIT 同模式）。
 * 审核查看时按教学计划 id 返回详情 + 上下文 tab 列表 + 各模块摘要，供审核页展示。
 */
@Component
public class TeachingPlanProvider implements AuditContentProvider {

    /** 与 audit_flow.flow_code / audit_instance.business_type 一致 */
    public static final String BUSINESS_TYPE = "TEACHING_PLAN_AUDIT";

    @Resource
    private TeachingPlanMapper teachingPlanMapper;

    @Resource
    private TeachingPlanContextMapper teachingPlanContextMapper;

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
        List<TeachingPlanContext> contexts = teachingPlanContextMapper.selectByPlanId(businessId);

        Map<String, Object> content = new HashMap<>();
        content.put("plan", plan);
        content.put("detail", detail);
        content.put("contexts", contexts);
        // 模块摘要，便于审核页一眼看全（不按 context 展开目标时取全部 context 下目标需前端再调）
        content.put("teachers", teachingPlanService.listTeacher(businessId));
        content.put("sections", teachingPlanService.listSection(businessId));
        content.put("contents", teachingPlanModuleService.listContent(businessId));
        content.put("practiceItems", teachingPlanModuleService.listPracticeItem(businessId));
        content.put("assessments", teachingPlanModuleService.listAssessment(businessId));
        content.put("textbooks", teachingPlanModuleService.listTextbook(businessId));
        content.put("conditions", teachingPlanModuleService.listCondition(businessId));
        content.put("processSteps", teachingPlanModuleService.listProcessStep(businessId));
        content.put("refs", teachingPlanModuleService.listRef(businessId, null));
        if (ObjectUtils.isNotEmpty(contexts)) {
            Long contextId = contexts.get(0).getId();
            content.put("objectives", teachingPlanModuleService.listObjective(businessId, contextId));
            content.put("defaultContextId", contextId);
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
