package com.doinner.csys.controller.audit;


import com.doinner.common.core.domain.DataSet;
import com.doinner.common.core.domain.Message;
import com.doinner.csys.entity.audit.AuditNode;
import com.doinner.csys.service.audit.AuditNodeService;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/audit/node")
public class AuditNodeController {

    @Autowired
    private AuditNodeService auditNodeService;

    /**
     * 根据流程 ID 获取节点列表
     */
    @GetMapping("/flow/{flowId}")
    public DataSet<List<AuditNode>> getNodesByFlowId(@PathVariable Long flowId) {
        return DataSet.success(auditNodeService.getNodesByFlowId(flowId));
    }

    /**
     * 根据节点 ID 获取节点详情
     */
    @GetMapping("/{nodeId}")
    public DataSet<AuditNode> getNodeById(@PathVariable Long nodeId) {
        return DataSet.success(auditNodeService.getNodeById(nodeId));
    }

    /**
     * 获取下一节点信息（用于送审时展示）
     */
    @GetMapping("/next/{instanceId}")
    public DataSet<AuditNode> getNextNode(@PathVariable Long instanceId) {
        return DataSet.success(auditNodeService.getNextNode(instanceId));
    }

    /**
     * 添加节点
     */
    @PostMapping("/add")
    public DataSet<AuditNode> addNode(@RequestBody AuditNode node) {
        if (ObjectUtils.isEmpty(node.getFlowId())) {
            return DataSet.error("流程 ID 不能为空");
        }
        if (ObjectUtils.isEmpty(node.getNodeKey())) {
            return DataSet.error("节点标识不能为空");
        }
        if (ObjectUtils.isEmpty(node.getNodeName())) {
            return DataSet.error("节点名称不能为空");
        }
        if (ObjectUtils.isEmpty(node.getRoleId())) {
            return DataSet.error("审核角色不能为空");
        }
        return DataSet.success(auditNodeService.addNode(node));
    }

    /**
     * 批量添加节点
     */
    @PostMapping("/batch-add")
    public DataSet<List<AuditNode>> addNodes(@RequestBody List<AuditNode> nodes) {
        if (ObjectUtils.isEmpty(nodes)) {
            return DataSet.error("节点列表不能为空");
        }
        return DataSet.success(auditNodeService.addNodes(nodes));
    }

    /**
     * 更新节点
     */
    @PutMapping("/update")
    public DataSet<AuditNode> updateNode(@RequestBody AuditNode node) {
        if (node.getId() == null) {
            return DataSet.error("节点 ID 不能为空");
        }
        return DataSet.success(auditNodeService.updateNode(node));
    }

    /**
     * 删除节点
     */
    @DeleteMapping("/delete/{nodeId}")
    public Message deleteNode(@PathVariable Long nodeId) {
        auditNodeService.deleteNode(nodeId);
        return Message.success();
    }

    /**
     * 启用/禁用节点
     */
    @PostMapping("/toggle-status")
    public Message toggleNodeStatus(@RequestParam Long nodeId, @RequestParam Integer status) {
        if (status != 0 && status != 1) {
            return Message.error("状态值必须为 0 或 1");
        }
        auditNodeService.toggleNodeStatus(nodeId, status);
        return Message.success();
    }

    /**
     * 调整节点顺序
     */
    @PostMapping("/adjust-order")
    public Message adjustNodeOrder(
            @RequestParam Long nodeId,
            @RequestParam Integer newOrder) {
        if (newOrder < 1) {
            return Message.error("顺序值必须大于 0");
        }
        auditNodeService.adjustNodeOrder(nodeId, newOrder);
        return Message.success();
    }
}