package com.doinner.csys.service.audit.impl;


import com.doinner.csys.auditProvider.AuditContentProvider;
import com.doinner.csys.auditProvider.AuditContentProviderRegistry;
import com.doinner.csys.entity.audit.AuditInstance;
import com.doinner.csys.entity.audit.AuditInstanceNode;
import com.doinner.csys.entity.audit.AuditNode;
import com.doinner.csys.service.audit.AuditContentService;
import com.doinner.csys.service.audit.AuditInstanceNodeService;
import com.doinner.csys.service.audit.AuditInstanceService;
import com.doinner.csys.service.audit.AuditNodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AuditContentServiceImpl implements AuditContentService {

    @Autowired
    private AuditContentProviderRegistry providerRegistry;

    @Autowired
    private AuditInstanceService auditInstanceService;

    @Autowired
    private AuditInstanceNodeService auditInstanceNodeService;

    @Autowired
    private AuditNodeService auditNodeService;

    @Override
    public Map<String, Object> getAuditContent(String businessType, Long businessId) {
        AuditContentProvider provider = providerRegistry.getProvider(businessType);
        return provider.getAuditContent(businessId);
    }

    @Override
    public Map<String, Object> getAuditContentByInstance(Long instanceId) {
        AuditInstance instance = auditInstanceService.getInstanceById(instanceId);
        if (instance == null) {
            throw new RuntimeException("审核实例不存在：" + instanceId);
        }

        Map<String, Object> content = getAuditContent(instance.getBusinessType(), instance.getBusinessId());

        // 添加审核实例信息
        content.put("instanceId", instance.getId());
        content.put("flowCode", instance.getFlowCode());
        content.put("currentNodeKey", instance.getCurrentNodeKey());
        content.put("instanceStatus", instance.getInstanceStatus());
        content.put("submitterId", instance.getSubmitterId());
        content.put("submitterName", instance.getSubmitterName());
        content.put("submitTime", instance.getSubmitTime());
        content.put("finalResult", instance.getFinalResult());
        content.put("finishTime", instance.getFinishTime());

        return content;
    }

    @Override
    public Map<String, Object> getAuditContentByNode(Long instanceNodeId) {
        AuditInstanceNode instanceNode = auditInstanceNodeService.getInstanceNodeById(instanceNodeId);
        if (instanceNode == null) {
            throw new RuntimeException("审核节点不存在：" + instanceNodeId);
        }

        AuditInstance instance = auditInstanceService.getInstanceById(instanceNode.getInstanceId());
        if (instance == null) {
            throw new RuntimeException("审核实例不存在：" + instanceNode.getInstanceId());
        }

        Map<String, Object> content = getAuditContent(instance.getBusinessType(), instance.getBusinessId());

        // 添加审核实例信息
        content.put("instanceId", instance.getId());
        content.put("flowCode", instance.getFlowCode());
        content.put("instanceStatus", instance.getInstanceStatus());
        content.put("submitterName", instance.getSubmitterName());
        content.put("submitTime", instance.getSubmitTime());

        // 添加节点信息
        content.put("instanceNodeId", instanceNode.getId());
        content.put("nodeId", instanceNode.getNodeId());
        content.put("nodeKey", instanceNode.getNodeKey());
        content.put("nodeName", instanceNode.getNodeName());
        content.put("nodeOrder", instanceNode.getNodeOrder());
        content.put("nodeStatus", instanceNode.getNodeStatus());
        content.put("roleId", instanceNode.getRoleId());
        content.put("auditorId", instanceNode.getAuditorId());
        content.put("auditorName", instanceNode.getAuditorName());
        content.put("auditOpinion", instanceNode.getAuditOpinion());
        content.put("auditTime", instanceNode.getAuditTime());
        content.put("receiveTime", instanceNode.getReceiveTime());

        // 添加下一节点信息（用于送审时展示）
        AuditNode nextNode = auditNodeService.getNextNode(instance.getId());
        if (nextNode != null) {
            Map<String, Object> nextNodeInfo = new HashMap<>();
            nextNodeInfo.put("nodeId", nextNode.getId());
            nextNodeInfo.put("nodeKey", nextNode.getNodeKey());
            nextNodeInfo.put("nodeName", nextNode.getNodeName());
            nextNodeInfo.put("nodeOrder", nextNode.getNodeOrder());
            nextNodeInfo.put("roleId", nextNode.getRoleId());
            nextNodeInfo.put("roleName", nextNode.getRoleName());
            content.put("nextNode", nextNodeInfo);
        }

        return content;
    }

    @Override
    public List<String> getSupportedBusinessTypes() {
        return providerRegistry.getRegisteredBusinessTypes();
    }

    @Override
    public boolean supports(String businessType) {
        return providerRegistry.supports(businessType);
    }
}