package com.doinner.csys.controller.audit;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.doinner.common.core.domain.DataSet;
import com.doinner.common.core.domain.DataTable;
import com.doinner.common.core.domain.Message;
import com.doinner.common.core.utils.PageUtils;
import com.doinner.csys.domain.vo.audit.AuditFlowVo;
import com.doinner.csys.entity.audit.AuditFlow;
import com.doinner.csys.entity.audit.AuditNode;
import com.doinner.csys.service.audit.AuditFlowService;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/audit/flow")
public class AuditFlowController {

    @Autowired
    private AuditFlowService auditFlowService;

    @GetMapping
    public DataSet<List<AuditFlow>> getAuditFlowList(AuditFlowVo auditFlowVo) {
        PageUtils.startPage();
        List<AuditFlow> auditFlows = auditFlowService.getAuditFlowList(auditFlowVo);
        return DataTable.success(auditFlows,new PageInfo<>(auditFlows).getTotal());
    }

    /**
     * 根据流程Id获取流程信息
     */
    @GetMapping("/{flowId}")
    public DataSet<AuditFlow> getByFlowId(@PathVariable Long flowId) {
        AuditFlow flow = auditFlowService.getById(flowId);
        if (flow == null) {
            return DataSet.error("流程不存在");
        }
        List<AuditNode> nodes = auditFlowService.getNodesByFlowId(flowId);
        flow.setNodes(nodes);
        return DataSet.success(flow);
    }

    /**
     * 根据code获取审核流
     * @param code
     * @return
     */
    @GetMapping("/code/{code}")
    public DataSet<AuditFlow> getByCode(@PathVariable String code) {
        AuditFlow flow = auditFlowService.getByCode(code);
        if (flow == null) {
            return DataSet.error("流程不存在");
        }
        List<AuditNode> nodes = auditFlowService.getNodesByFlowId(flow.getId());
        flow.setNodes(nodes);
        return DataSet.success(flow);
    }

    /**
     * 创建审核流程
     */
    @PostMapping("/create")
    public ResponseEntity<Map<String, Object>> createFlow(@RequestBody Map<String, Object> request) {
        Map<String, Object> result = new HashMap<>();
        try {
            AuditFlow flow = new AuditFlow();
            flow.setCode((String) request.get("code"));
            flow.setName((String) request.get("name"));
            flow.setDescription((String) request.get("description"));
            flow.setTargetTable((String) request.get("targetTable"));
            flow.setTargetIdField((String) request.get("targetIdField"));
            flow.setTargetStatusField((String) request.get("targetStatusField"));

            List<Map<String, Object>> nodeMaps = (List<Map<String, Object>>) request.get("nodes");
            List<AuditNode> nodes = null;
            if (nodeMaps != null) {
                nodes = new java.util.ArrayList<>();
                for (Map<String, Object> nodeMap : nodeMaps) {
                    AuditNode node = new AuditNode();
                    node.setNodeKey((String) nodeMap.get("nodeKey"));
                    node.setNodeName((String) nodeMap.get("nodeName"));
                    node.setNodeOrder((Integer) nodeMap.get("nodeOrder"));
                    node.setRoleId((Long) nodeMap.get("roleId"));
                    node.setRoleName((String) nodeMap.get("roleName"));
                    node.setIsAutoPass((Integer) nodeMap.getOrDefault("isAutoPass", 0));
                    node.setTimeoutHours((Integer) nodeMap.get("timeoutHours"));
                    nodes.add(node);
                }
            }

            AuditFlow savedFlow = auditFlowService.createFlow(flow, nodes);
            result.put("success", true);
            result.put("data", savedFlow);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
        }
        return ResponseEntity.ok(result);
    }

    /**
     * 禁用流程
     */
    @PostMapping("/disable/{flowId}")
    public Message disableFlow(@PathVariable Long flowId) {
        auditFlowService.disableFlow(flowId);
        return Message.success();
    }

    /**
     * 开启流程
     */
    @PostMapping("/start/{flowId}")
    public Message start(@PathVariable Long flowId) {
        auditFlowService.startFlow(flowId);
        return Message.success();
    }
}