package com.doinner.csys.service.audit;

import com.baomidou.mybatisplus.extension.service.IService;
import com.doinner.csys.entity.audit.AuditNode;

import java.util.List;

public interface AuditNodeService extends IService<AuditNode> {

    /**
     * 根据流程 ID 获取节点列表
     */
    List<AuditNode> getNodesByFlowId(Long flowId);

    /**
     * 根据节点 ID 获取节点
     */
    AuditNode getNodeById(Long nodeId);

    /**
     * 添加节点
     */
    AuditNode addNode(AuditNode node);

    /**
     * 批量添加节点
     */
    List<AuditNode> addNodes(List<AuditNode> nodes);

    /**
     * 更新节点
     */
    AuditNode updateNode(AuditNode node);

    /**
     * 删除节点
     */
    void deleteNode(Long nodeId);

    /**
     * 启用/禁用节点
     */
    void toggleNodeStatus(Long nodeId, Integer status);

    /**
     * 调整节点顺序
     */
    void adjustNodeOrder(Long nodeId, Integer newOrder);

    /**
     * 获取下一节点信息（用于送审时展示）
     */
    AuditNode getNextNode(Long instanceId);

    boolean verify(Long flowId);
}