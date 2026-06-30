package com.doinner.csys.service.audit.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.doinner.common.core.utils.poi.ExcelUtil;
import com.doinner.csys.constant.audit.AuditStatusEnum;
import com.doinner.csys.constant.audit.BusinessStatusEnum;
import com.doinner.csys.constant.audit.NodeStatusEnum;
import com.doinner.csys.dao.audit.AuditFlowMapper;
import com.doinner.csys.dao.audit.AuditInstanceMapper;
import com.doinner.csys.dao.audit.AuditInstanceNodeMapper;
import com.doinner.csys.dao.audit.AuditNodeMapper;
import com.doinner.csys.domain.KnowledgeCheckLog;
import com.doinner.csys.domain.vo.audit.AuditApproveVo;
import com.doinner.csys.domain.vo.audit.AuditSubmitVo;
import com.doinner.csys.domain.vo.audit.AuditTaskVo;
import com.doinner.csys.entity.audit.AuditFlow;
import com.doinner.csys.entity.audit.AuditInstance;
import com.doinner.csys.entity.audit.AuditInstanceNode;
import com.doinner.csys.entity.audit.AuditNode;
import com.doinner.csys.service.audit.AuditInstanceService;
import com.doinner.csys.utils.WordUtil;
import com.doinner.csys.utils.audit.BusinessTableUpdater;
import org.apache.commons.lang3.ObjectUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public  class AuditInstanceServiceImpl extends ServiceImpl<AuditInstanceMapper, AuditInstance> implements AuditInstanceService {

    @Autowired
    private AuditInstanceMapper auditInstanceMapper;

    @Autowired
    private AuditFlowMapper auditFlowMapper;

    @Autowired
    private AuditNodeMapper auditNodeMapper;

    @Autowired
    private AuditInstanceNodeMapper auditInstanceNodeMapper;

    @Autowired
    private BusinessTableUpdater businessTableUpdater;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AuditInstance submit(AuditSubmitVo request) {
        AuditFlow flow = auditFlowMapper.selectByCode(request.getFlowCode());
        if (flow == null) {
            throw new RuntimeException("流程不存在：" + request.getFlowCode());
        }
        if (flow.getStatus() != 1) {
            throw new RuntimeException("流程已禁用：" + request.getFlowCode());
        }

        List<AuditNode> nodes = auditNodeMapper.selectByFlowId(flow.getId());
        if (nodes == null || nodes.isEmpty()) {
            throw new RuntimeException("流程未配置节点：" + request.getFlowCode());
        }

        AuditInstance instance = new AuditInstance();
        instance.setFlowCode(request.getFlowCode());
        //BusinessType与FlowCode值设为相同 减少默认值
        instance.setBusinessType(request.getFlowCode());
        instance.setBusinessId(request.getBusinessId());
        instance.setBusinessTitle(request.getBusinessTitle());
        instance.setSubmitterId(request.getSubmitterId());
        instance.setSubmitterName(request.getSubmitterName());
        instance.setSubmitTime(LocalDateTime.now());
        instance.setInstanceStatus(AuditStatusEnum.PENDING.getCode());
        instance.setCreateTime(LocalDateTime.now());
        instance.setUpdateTime(LocalDateTime.now());
        instance.setFlowName(flow.getName());
        instance.setFlowId(flow.getId());

        AuditNode firstNode = nodes.get(0);
        auditInstanceMapper.insert(instance);

        for (AuditNode node : nodes) {
            AuditInstanceNode instanceNode = new AuditInstanceNode();
            instanceNode.setInstanceId(instance.getId());
            instanceNode.setFlowId(flow.getId());
            instanceNode.setNodeId(node.getId());
            instanceNode.setNodeKey(node.getNodeKey());
            instanceNode.setNodeName(node.getNodeName());
            instanceNode.setNodeOrder(node.getNodeOrder());
            instanceNode.setRoleId(node.getRoleId());
            instanceNode.setNodeStatus(NodeStatusEnum.PENDING.getCode());
            instanceNode.setCreateTime(LocalDateTime.now());
            instanceNode.setUpdateTime(LocalDateTime.now());
            instanceNode.setReceiveTime(LocalDateTime.now());
            // 为第一个节点设置审核人
            if (node == firstNode && request.getAuditorId() != null) {
                instanceNode.setAuditorId(request.getAuditorId());
                instanceNode.setAuditorName(request.getAuditorName());
            }
            auditInstanceNodeMapper.insert(instanceNode);
            if (node == firstNode && request.getAuditorId() != null) {
                instance.setCurrentNodeId(instanceNode.getId());
                instance.setCurrentNodeKey(instanceNode.getNodeKey());
            }
        }
        auditInstanceMapper.updateById(instance);
        businessTableUpdater.updateBusinessStatus(
                flow.getTargetTable(),
                flow.getTargetIdField(),
                flow.getTargetStatusField(),
                instance.getBusinessId(),
                BusinessStatusEnum.UNDER_REVIEW.getCode()
        );
        return instance;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AuditInstance approve(AuditApproveVo request) {
        AuditInstance instance = approveAudit(request);
        return auditInstanceMapper.selectById(instance.getId());
    }

    @NotNull
    private AuditInstance approveAudit(AuditApproveVo request) {
        AuditInstanceNode instanceNode = auditInstanceNodeMapper.selectById(request.getInstanceNodeId());
        if (instanceNode == null) {
            throw new RuntimeException("审核节点不存在：" + request.getInstanceNodeId());
        }

        AuditInstance instance = auditInstanceMapper.selectById(instanceNode.getInstanceId());
        if (instance == null) {
            throw new RuntimeException("审核实例不存在：" + instanceNode.getInstanceId());
        }

        if (instance.getInstanceStatus() != AuditStatusEnum.PENDING.getCode()) {
            throw new RuntimeException("审核实例已完成：" + instance.getId());
        }

        auditInstanceNodeMapper.updateNodeStatus(
                instanceNode.getId(),
                NodeStatusEnum.APPROVED.getCode(),
                1,
                request.getAuditOpinion(),
                LocalDateTime.now()
        );

        AuditFlow flow = auditFlowMapper.selectById(instanceNode.getFlowId());
        AuditNode currentNode = auditNodeMapper.selectById(instanceNode.getNodeId());
        AuditNode nextNode = auditNodeMapper.selectNextNode(flow.getId(), currentNode.getNodeOrder());

        if (nextNode != null) {
            AuditInstanceNode nextInstanceNode = auditInstanceNodeMapper.selectOne(
                    new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<AuditInstanceNode>()
                            .eq(AuditInstanceNode::getInstanceId, instance.getId())
                            .eq(AuditInstanceNode::getNodeId, nextNode.getId())
            );
            instance.setCurrentNodeId(nextInstanceNode.getId());
            instance.setCurrentNodeKey(nextInstanceNode.getNodeKey());
            instance.setUpdateTime(LocalDateTime.now());
            auditInstanceMapper.updateById(instance);

            if (nextInstanceNode != null) {
                nextInstanceNode.setReceiveTime(LocalDateTime.now());
                // 为下一个节点设置审核人
                if (request.getNextAuditorId() != null) {
                    nextInstanceNode.setAuditorId(request.getNextAuditorId());
                    nextInstanceNode.setAuditorName(request.getNextAuditorName());
                }
                auditInstanceNodeMapper.updateById(nextInstanceNode);
            }
        } else {
            auditInstanceMapper.updateInstanceStatus(
                    instance.getId(),
                    AuditStatusEnum.APPROVED.getCode(),
                    1,
                    LocalDateTime.now()
            );

            businessTableUpdater.updateBusinessStatus(
                    flow.getTargetTable(),
                    flow.getTargetIdField(),
                    flow.getTargetStatusField(),
                    instance.getBusinessId(),
                    BusinessStatusEnum.APPROVED.getCode()
            );
        }
        return instance;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AuditInstance reject(AuditApproveVo request) {
        AuditInstance instance = rejectAudit(request);

        return auditInstanceMapper.selectById(instance.getId());
    }

    @NotNull
    private AuditInstance rejectAudit(AuditApproveVo request) {
        AuditInstanceNode instanceNode = auditInstanceNodeMapper.selectById(request.getInstanceNodeId());
        if (instanceNode == null) {
            throw new RuntimeException("审核节点不存在：" + request.getInstanceNodeId());
        }

        AuditInstance instance = auditInstanceMapper.selectById(instanceNode.getInstanceId());
        if (instance == null) {
            throw new RuntimeException("审核实例不存在：" + instanceNode.getInstanceId());
        }

        if (instance.getInstanceStatus() != AuditStatusEnum.PENDING.getCode()) {
            throw new RuntimeException("审核实例已完成：" + instance.getId());
        }

        auditInstanceNodeMapper.updateNodeStatus(
                instanceNode.getId(),
                NodeStatusEnum.REJECTED.getCode(),
                0,
                request.getAuditOpinion(),
                LocalDateTime.now()
        );

        auditInstanceMapper.updateInstanceStatus(
                instance.getId(),
                AuditStatusEnum.REJECTED.getCode(),
                0,
                LocalDateTime.now()
        );

        AuditFlow flow = auditFlowMapper.selectById(instanceNode.getFlowId());
        businessTableUpdater.updateBusinessStatus(
                flow.getTargetTable(),
                flow.getTargetIdField(),
                flow.getTargetStatusField(),
                instance.getBusinessId(),
                BusinessStatusEnum.REJECTED.getCode()
        );
        return instance;
    }

    @Override
    public AuditInstance getInstanceById(Long instanceId) {
        return auditInstanceMapper.selectById(instanceId);
    }

    @Override
    public List<AuditInstance> getMySubmitted(AuditTaskVo auditTaskVo) {
        List<AuditInstance> instances = auditInstanceMapper.selectBySubmitter(auditTaskVo);
        List<Long> instanceIds = instances.stream().map(i -> i.getId()).collect(Collectors.toList());
        if(ObjectUtils.isNotEmpty(instanceIds)){
            List<AuditInstanceNode> auditInstanceNodes = auditInstanceNodeMapper.selectList(new QueryWrapper<AuditInstanceNode>()
                    .in("instance_id", instanceIds)
                    .isNotNull("auditor_id"));
            Map<Long, List<AuditInstanceNode>> nodeGroupByInstanceId = auditInstanceNodes.stream()
                    .collect(Collectors.groupingBy(AuditInstanceNode::getInstanceId));
            instances.forEach(instance -> {
                List<AuditInstanceNode> nodes = nodeGroupByInstanceId.get(instance.getId());
                instance.setNodes(nodes);
            });
        }
        return instances;
    }


    @Override
    public List<AuditInstanceNode> getMyTodoList(String auditorId) {
        return auditInstanceNodeMapper.selectTodoByAuditor(auditorId);
    }

    @Override
    public List<AuditInstanceNode> getMyDoneList(String auditorId) {
        return auditInstanceNodeMapper.selectDoneByAuditor(auditorId);
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancelInstance(Long instanceId, String operatorId) {
        AuditInstance instance = auditInstanceMapper.selectById(instanceId);
        if (instance == null) {
            throw new RuntimeException("审核实例不存在：" + instanceId);
        }

        if (!instance.getSubmitterId().equals(operatorId)) {
            throw new RuntimeException("只有送审人可以撤销审核");
        }

        if (instance.getInstanceStatus() != AuditStatusEnum.PENDING.getCode()) {
            throw new RuntimeException("审核实例已完成，无法撤销");
        }

        auditInstanceMapper.updateInstanceStatus(
                instance.getId(),
                AuditStatusEnum.CANCELLED.getCode(),
                null,
                LocalDateTime.now()
        );

        AuditFlow flow = auditFlowMapper.selectByCode(instance.getFlowCode());
        businessTableUpdater.updateBusinessStatus(
                flow.getTargetTable(),
                flow.getTargetIdField(),
                flow.getTargetStatusField(),
                instance.getBusinessId(),
                BusinessStatusEnum.PENDING.getCode()
        );
    }

    public List<AuditInstance> getTodoList(AuditTaskVo auditTaskVo){
        return baseMapper.getTodoList(auditTaskVo);
    }

    public List<AuditInstance> getDoneList(AuditTaskVo auditTaskVo){
        return baseMapper.getDoneList(auditTaskVo);
    }

    @Override
    public List<AuditInstance> approveBatch(AuditApproveVo request) {
        ArrayList<Long> instanceIds = new ArrayList<>();
        request.getInstanceNodeIds().forEach(nodeId->{
            AuditApproveVo auditApproveVo = setRequest(request, nodeId);
            AuditInstance auditInstance = approveAudit(auditApproveVo);
            instanceIds.add(auditInstance.getId());
        });
        return auditInstanceMapper.selectBatchIds(instanceIds);
    }

    @Override
    public List<AuditInstance> rejectBatch(AuditApproveVo request) {
        ArrayList<Long> instanceIds = new ArrayList<>();
        request.getInstanceNodeIds().forEach(nodeId->{
            AuditApproveVo auditApproveVo = setRequest(request, nodeId);
            AuditInstance auditInstance = rejectAudit(auditApproveVo);
            instanceIds.add(auditInstance.getId());
        });
        return auditInstanceMapper.selectBatchIds(instanceIds);
    }

    @Override
    public void exportNodes(Long instanceId, HttpServletResponse response) {
        try {
            List<AuditInstanceNode> auditInstanceNodes = auditInstanceNodeMapper.selectList(new QueryWrapper<AuditInstanceNode>()
                    .eq("instance_id", instanceId)
                    .isNotNull("auditor_id"));
            response.setContentType("application/x-download");
            String fileName = "审核记录.xlsx";
            response.setHeader("Content-disposition", "attachment;filename*=UTF-8''" + URLEncoder.encode(fileName, "utf-8"));
            ExcelUtil<AuditInstanceNode> util = new ExcelUtil<>(AuditInstanceNode.class);
            util.exportExcel(response, auditInstanceNodes, "审核记录");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    @NotNull
    private static AuditApproveVo setRequest(AuditApproveVo request, Long nodeId) {
        AuditApproveVo auditApproveVo = new AuditApproveVo();
        BeanUtils.copyProperties(request,auditApproveVo);
        auditApproveVo.setInstanceNodeId(nodeId);
        return auditApproveVo;
    }

    @NotNull
    private List<AuditInstance> getAuditInstances(List<AuditInstanceNode> todoNodes) {
        List<Long> instanceIds = todoNodes.stream().map(node -> node.getInstanceId()).collect(Collectors.toList());
        List<AuditInstance> instances = auditInstanceMapper.selectList(new QueryWrapper<AuditInstance>().in("id", instanceIds));
        Map<Long, List<AuditInstanceNode>> nodeGroupByInstanceId = todoNodes.stream()
                .collect(Collectors.groupingBy(AuditInstanceNode::getInstanceId));
        instances.forEach(instance -> {
            List<AuditInstanceNode> nodes = nodeGroupByInstanceId.get(instance.getId());
            instance.setNodes(nodes);
        });
        return instances;
    }
}