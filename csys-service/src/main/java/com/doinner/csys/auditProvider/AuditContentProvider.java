package com.doinner.csys.auditProvider;

import java.util.Map;

/**
 * 审核内容提供者接口
 * 每个业务类型实现此接口以提供内容查看
 */
public interface AuditContentProvider {

    /**
     * 获取支持的业务类型
     * @return 业务类型标识（与 audit_instance.business_type 对应）
     */
    String getBusinessType();

    /**
     * 根据业务 ID 获取审核内容
     * @param businessId 业务 ID
     * @return 审核内容数据
     */
    Map<String, Object> getAuditContent(Long businessId);

    /**
     * 获取内容标题
     * @param businessId 业务 ID
     * @return 标题
     */
    String getContentTitle(Long businessId);
}