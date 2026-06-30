package com.doinner.csys.service.audit;

import com.baomidou.mybatisplus.extension.service.IService;
import com.doinner.csys.domain.vo.audit.AuditApproveVo;
import com.doinner.csys.domain.vo.audit.AuditSubmitVo;
import com.doinner.csys.domain.vo.audit.AuditTaskVo;
import com.doinner.csys.entity.audit.AuditInstance;
import com.doinner.csys.entity.audit.AuditInstanceNode;


import javax.servlet.http.HttpServletResponse;
import java.util.List;

public interface AuditInstanceService extends IService<AuditInstance> {

    AuditInstance submit(AuditSubmitVo request);

    AuditInstance approve(AuditApproveVo request);

    AuditInstance reject(AuditApproveVo request);

    AuditInstance getInstanceById(Long instanceId);

    List<AuditInstance> getMySubmitted(AuditTaskVo auditTaskVo);

    List<AuditInstanceNode> getMyTodoList(String auditorId);

    void cancelInstance(Long instanceId, String operatorId);

    List<AuditInstanceNode> getMyDoneList(String auditorId);

    List<AuditInstance> getTodoList(AuditTaskVo auditTaskVo);

    List<AuditInstance> getDoneList(AuditTaskVo auditTaskVo);

    List<AuditInstance> approveBatch(AuditApproveVo request);

    List<AuditInstance> rejectBatch(AuditApproveVo request);

    void exportNodes(Long instanceId, HttpServletResponse response);
}