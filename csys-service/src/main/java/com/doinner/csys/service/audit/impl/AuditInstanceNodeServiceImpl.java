package com.doinner.csys.service.audit.impl;


import com.doinner.csys.dao.audit.AuditInstanceNodeMapper;
import com.doinner.csys.entity.audit.AuditInstanceNode;
import com.doinner.csys.service.audit.AuditInstanceNodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuditInstanceNodeServiceImpl implements AuditInstanceNodeService {
    @Autowired
    private AuditInstanceNodeMapper auditInstanceNodeMapper;
    @Override
    public AuditInstanceNode getInstanceNodeById(Long instanceNodeId) {
        return auditInstanceNodeMapper.selectById(instanceNodeId);
    }
}
