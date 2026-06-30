package com.doinner.csys.service.audit;

import java.util.List;
import java.util.Map;

public interface AuditContentService {

    /**
     * 根据业务类型和 ID 获取审核内容
     */
    Map<String, Object> getAuditContent(String businessType, Long businessId);

    /**
     * 根据审核实例 ID 获取审核内容
     */
    Map<String, Object> getAuditContentByInstance(Long instanceId);

    /**
     * 根据审核实例节点 ID 获取审核内容（审核人使用）
     */
    Map<String, Object> getAuditContentByNode(Long instanceNodeId);

    /**
     * 获取所有已支持的业务类型
     */
    List<String> getSupportedBusinessTypes();

    /**
     * 检查是否支持该业务类型
     */
    boolean supports(String businessType);
}