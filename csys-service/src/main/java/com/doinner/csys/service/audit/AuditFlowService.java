package com.doinner.csys.service.audit;

import com.baomidou.mybatisplus.extension.service.IService;
import com.doinner.csys.domain.vo.audit.AuditFlowVo;
import com.doinner.csys.entity.audit.AuditFlow;
import com.doinner.csys.entity.audit.AuditNode;


import java.util.List;

public interface AuditFlowService extends IService<AuditFlow> {

    AuditFlow getByCode(String code);

    List<AuditNode> getNodesByFlowId(Long flowId);

    AuditFlow createFlow(AuditFlow flow, List<AuditNode> nodes);

    void disableFlow(Long flowId);

    void startFlow(Long flowId);

    List<AuditFlow> getAuditFlowList(AuditFlowVo auditFlowVo);
}