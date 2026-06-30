package com.doinner.csys.auditProvider;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 审核内容提供者注册中心
 * 自动收集所有 AuditContentProvider 实现类
 */
@Component
public class AuditContentProviderRegistry {

    private final Map<String, AuditContentProvider> providerMap = new ConcurrentHashMap<>();

    public AuditContentProviderRegistry(List<AuditContentProvider> providers) {
        for (AuditContentProvider provider : providers) {
            registerProvider(provider);
        }
    }

    /**
     * 注册内容提供者
     */
    private void registerProvider(AuditContentProvider provider) {
        String businessType = provider.getBusinessType();
        providerMap.put(businessType, provider);
    }

    /**
     * 根据业务类型获取内容提供者
     */
    public AuditContentProvider getProvider(String businessType) {
        AuditContentProvider provider = providerMap.get(businessType);
        if (provider == null) {
            throw new RuntimeException("未找到业务类型 [" + businessType + "] 的内容提供者，请创建对应的 Provider 实现类");
        }
        return provider;
    }

    /**
     * 获取所有已注册的业务类型
     */
    public List<String> getRegisteredBusinessTypes() {
        return List.copyOf(providerMap.keySet());
    }

    /**
     * 检查是否支持该业务类型
     */
    public boolean supports(String businessType) {
        return providerMap.containsKey(businessType);
    }
}