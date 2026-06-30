package com.doinner.csys.service.audit.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.doinner.csys.dao.audit.AuditFlowMapper;
import com.doinner.csys.dao.audit.AuditNodeMapper;
import com.doinner.csys.domain.vo.audit.AuditFlowVo;
import com.doinner.csys.entity.audit.AuditFlow;
import com.doinner.csys.entity.audit.AuditNode;
import com.doinner.csys.service.audit.AuditFlowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public  class AuditFlowServiceImpl extends ServiceImpl<AuditFlowMapper, AuditFlow> implements AuditFlowService {

    @Autowired
    private AuditFlowMapper auditFlowMapper;

    @Autowired
    private AuditNodeMapper auditNodeMapper;

    @Override
    public AuditFlow getByCode(String code) {
        return auditFlowMapper.selectByCode(code);
    }

    @Override
    public List<AuditNode> getNodesByFlowId(Long flowId) {
        return auditNodeMapper.selectByFlowId(flowId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AuditFlow createFlow(AuditFlow flow, List<AuditNode> nodes) {
        flow.setStatus(1);
        flow.setVersion(1);
        flow.setCreateTime(LocalDateTime.now());
        flow.setUpdateTime(LocalDateTime.now());
        baseMapper.insert(flow);

        if (nodes != null && !nodes.isEmpty()) {
            for (AuditNode node : nodes) {
                node.setFlowId(flow.getId());
                node.setStatus(1);
                node.setCreateTime(LocalDateTime.now());
                node.setUpdateTime(LocalDateTime.now());
                auditNodeMapper.insert(node);
            }
        }
        return flow;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void disableFlow(Long flowId) {
        AuditFlow flow = baseMapper.selectById(flowId);
        if (flow != null) {
            flow.setStatus(0);
            flow.setUpdateTime(LocalDateTime.now());
            baseMapper.updateById(flow);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void startFlow(Long flowId) {
        AuditFlow flow = baseMapper.selectById(flowId);
        if (flow != null) {
            flow.setStatus(1);
            flow.setUpdateTime(LocalDateTime.now());
            baseMapper.updateById(flow);
        }
    }


    @Override
    public List<AuditFlow> getAuditFlowList(AuditFlowVo auditFlowVo) {
        return auditFlowMapper.getAuditFlowList(auditFlowVo);
    }
}