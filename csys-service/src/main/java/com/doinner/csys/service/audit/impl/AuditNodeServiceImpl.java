package com.doinner.csys.service.audit.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.doinner.csys.dao.audit.AuditFlowMapper;
import com.doinner.csys.dao.audit.AuditInstanceMapper;
import com.doinner.csys.dao.audit.AuditInstanceNodeMapper;
import com.doinner.csys.dao.audit.AuditNodeMapper;
import com.doinner.csys.entity.audit.AuditFlow;
import com.doinner.csys.entity.audit.AuditInstance;
import com.doinner.csys.entity.audit.AuditInstanceNode;
import com.doinner.csys.entity.audit.AuditNode;
import com.doinner.csys.service.audit.AuditNodeService;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public  class AuditNodeServiceImpl extends ServiceImpl<AuditNodeMapper, AuditNode> implements AuditNodeService {

    @Autowired
    private AuditNodeMapper auditNodeMapper;

    @Autowired
    private AuditInstanceMapper auditInstanceMapper;

    @Autowired
    private AuditInstanceNodeMapper auditInstanceNodeMapper;

    @Autowired
    private AuditFlowMapper auditFlowMapper;

    @Override
    public List<AuditNode> getNodesByFlowId(Long flowId) {
        LambdaQueryWrapper<AuditNode> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AuditNode::getFlowId, flowId)
               .orderByAsc(AuditNode::getNodeOrder);
        return auditNodeMapper.selectList(wrapper);
    }

    @Override
    public AuditNode getNodeById(Long nodeId) {
        return auditNodeMapper.selectById(nodeId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AuditNode addNode(AuditNode node) {
        // 如果未设置顺序，自动设置为最大顺序 +1
        if (node.getNodeOrder() == null) {
            List<AuditNode> existingNodes = getNodesByFlowId(node.getFlowId());
            int maxOrder = existingNodes.stream()
                    .mapToInt(AuditNode::getNodeOrder)
                    .max()
                    .orElse(0);
            node.setNodeOrder(maxOrder + 1);
        }

        node.setStatus(1);
        node.setCreateTime(LocalDateTime.now());
        node.setUpdateTime(LocalDateTime.now());
        auditNodeMapper.insert(node);
        return node;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<AuditNode> addNodes(List<AuditNode> nodes) {
        if (nodes == null || nodes.isEmpty()) {
            throw new RuntimeException("节点列表不能为空");
        }
        Long flowId = nodes.get(0).getFlowId();
        //删除流程下的所有节点
        auditNodeMapper.deleteByFlowId(flowId);
        // 批量插入
        for (AuditNode node : nodes) {
            node.setStatus(1);
            node.setCreateTime(LocalDateTime.now());
            node.setUpdateTime(LocalDateTime.now());
            auditNodeMapper.insert(node);
        }

        return nodes;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AuditNode updateNode(AuditNode node) {
        AuditNode existingNode = auditNodeMapper.selectById(node.getId());
        if (existingNode == null) {
            throw new RuntimeException("节点不存在：" + node.getId());
        }

        node.setUpdateTime(LocalDateTime.now());
        auditNodeMapper.updateById(node);
        return auditNodeMapper.selectById(node.getId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteNode(Long nodeId) {
        AuditNode node = auditNodeMapper.selectById(nodeId);
        if (node == null) {
            throw new RuntimeException("节点不存在：" + nodeId);
        }

        // 删除节点
        auditNodeMapper.deleteById(nodeId);

        // 重新调整后续节点的顺序
        LambdaQueryWrapper<AuditNode> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AuditNode::getFlowId, node.getFlowId())
               .gt(AuditNode::getNodeOrder, node.getNodeOrder())
               .orderByAsc(AuditNode::getNodeOrder);
        List<AuditNode> subsequentNodes = auditNodeMapper.selectList(wrapper);

        for (AuditNode subsequentNode : subsequentNodes) {
            subsequentNode.setNodeOrder(subsequentNode.getNodeOrder() - 1);
            subsequentNode.setUpdateTime(LocalDateTime.now());
            auditNodeMapper.updateById(subsequentNode);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void toggleNodeStatus(Long nodeId, Integer status) {
        AuditNode node = auditNodeMapper.selectById(nodeId);
        if (node == null) {
            throw new RuntimeException("节点不存在：" + nodeId);
        }

        node.setStatus(status);
        node.setUpdateTime(LocalDateTime.now());
        auditNodeMapper.updateById(node);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void adjustNodeOrder(Long nodeId, Integer newOrder) {
        AuditNode targetNode = auditNodeMapper.selectById(nodeId);
        if (targetNode == null) {
            throw new RuntimeException("节点不存在：" + nodeId);
        }

        Integer oldOrder = targetNode.getNodeOrder();
        if (oldOrder.equals(newOrder)) {
            return;
        }

        List<AuditNode> allNodes = getNodesByFlowId(targetNode.getFlowId());

        if (newOrder > oldOrder) {
            // 向后移动：中间的节点向前移
            for (AuditNode node : allNodes) {
                if (node.getNodeOrder() > oldOrder && node.getNodeOrder() <= newOrder) {
                    node.setNodeOrder(node.getNodeOrder() - 1);
                    node.setUpdateTime(LocalDateTime.now());
                    auditNodeMapper.updateById(node);
                }
            }
        } else {
            // 向前移动：中间的节点向后移
            for (AuditNode node : allNodes) {
                if (node.getNodeOrder() >= newOrder && node.getNodeOrder() < oldOrder) {
                    node.setNodeOrder(node.getNodeOrder() + 1);
                    node.setUpdateTime(LocalDateTime.now());
                    auditNodeMapper.updateById(node);
                }
            }
        }

        targetNode.setNodeOrder(newOrder);
        targetNode.setUpdateTime(LocalDateTime.now());
        auditNodeMapper.updateById(targetNode);
    }

    @Override
    public AuditNode getNextNode(Long instanceId) {
        // 获取审核实例
        AuditInstance instance = auditInstanceMapper.selectById(instanceId);
        if (instance == null) {
            throw new RuntimeException("审核实例不存在：" + instanceId);
        }

        // 获取当前节点
        AuditInstanceNode currentNode = auditInstanceNodeMapper.selectPendingNode(instanceId);
        if (currentNode == null) {
            return null;
        }

        // 获取流程信息
        AuditFlow flow = auditFlowMapper.selectById(currentNode.getFlowId());
        if (flow == null) {
            return null;
        }

        // 获取下一节点
        AuditNode nextNode = auditNodeMapper.selectNextNode(flow.getId(), currentNode.getNodeOrder());

        return nextNode;
    }

    @Override
    public boolean verify(Long flowId){
        AuditFlow auditFlow = auditFlowMapper.selectById(flowId);
        if(auditFlow.getStatus()==1){
            return false;
        }else {
            return true;
        }
    }
}