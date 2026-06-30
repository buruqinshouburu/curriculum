package com.doinner.csys.auditProvider;



import com.doinner.csys.domain.TeachingProgrammeInstance;
import com.doinner.csys.domain.vo.CourseVo;
import com.doinner.csys.service.CurriculumService;
import com.doinner.csys.service.TeachingProgrammeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
/**
 * 教学大纲审核内容提供者
 * 复用项目中已有的 CourseService
 */
@Component
public class TeachProgramContentProvider implements AuditContentProvider {

    @Autowired
    private TeachingProgrammeService teachingProgrammeService;

    @Override
    public String getBusinessType() {
        return "TEACH_PROGRAMME_AUDIT";
    }

    @Override
    public Map<String, Object> getAuditContent(Long businessId) {
        TeachingProgrammeInstance teachingProgrammeInstance = teachingProgrammeService.selectInstance(businessId);
        if (teachingProgrammeInstance == null) {
            throw new RuntimeException("教学大纲不存在：" + businessId);
        }

        Map<String, Object> content = new HashMap<>();
        content.put("teachingProgrammeInstance", teachingProgrammeInstance);
        return content;
    }

    @Override
    public String getContentTitle(Long businessId) {
        TeachingProgrammeInstance teachingProgrammeInstance = teachingProgrammeService.selectInstance(businessId);
        return teachingProgrammeInstance != null ? teachingProgrammeInstance.getName() : "未知教学大纲";
    }
}