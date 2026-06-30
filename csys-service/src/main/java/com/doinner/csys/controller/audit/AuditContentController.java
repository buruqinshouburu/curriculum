package com.doinner.csys.controller.audit;

import com.doinner.common.core.domain.DataSet;
import com.doinner.csys.service.audit.AuditContentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/audit/content")
public class AuditContentController {

    @Autowired
    private AuditContentService auditContentService;

    /**
     * 根据业务类型和 ID 获取审核内容
     */
    @GetMapping("/business")
    public DataSet<Map<String, Object>> getContentByBusiness(
            @RequestParam String businessType,
            @RequestParam Long businessId) {
        return DataSet.success(auditContentService.getAuditContent(businessType, businessId));
    }

    /**
     * 根据审核实例 ID 获取审核内容
     */
    @GetMapping("/instance/{instanceId}")
    public DataSet<Map<String, Object>> getContentByInstance(@PathVariable Long instanceId) {
        return DataSet.success(auditContentService.getAuditContentByInstance(instanceId));
    }

    /**
     * 根据审核实例节点 ID 获取审核内容（审核人使用）
     */
    @GetMapping("/node/{instanceNodeId}")
    public DataSet<Map<String, Object>> getContentByNode(@PathVariable Long instanceNodeId) {
        return DataSet.success(auditContentService.getAuditContentByNode(instanceNodeId));
    }

    /**
     * 获取所有支持的业务类型
     */
    @GetMapping("/supported-types")
    public DataSet<List<String>> getSupportedTypes() {
        return DataSet.success(auditContentService.getSupportedBusinessTypes());
    }
}