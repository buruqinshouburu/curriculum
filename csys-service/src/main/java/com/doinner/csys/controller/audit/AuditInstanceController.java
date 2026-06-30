package com.doinner.csys.controller.audit;


import com.doinner.common.core.domain.DataSet;
import com.doinner.common.core.domain.DataTable;
import com.doinner.common.core.domain.Message;
import com.doinner.common.core.utils.PageUtils;
import com.doinner.csys.domain.vo.audit.AuditApproveVo;
import com.doinner.csys.domain.vo.audit.AuditSubmitVo;
import com.doinner.csys.domain.vo.audit.AuditTaskVo;
import com.doinner.csys.entity.audit.AuditInstance;
import com.doinner.csys.entity.audit.AuditInstanceNode;
import com.doinner.csys.service.audit.AuditInstanceService;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


import javax.servlet.http.HttpServletResponse;
import java.util.List;

@RestController
@RequestMapping("/audit/instance")
public class AuditInstanceController {

    @Autowired
    private AuditInstanceService auditInstanceService;

    /**
     * 提交审核
     */
    @PostMapping("/submit")
    public DataSet<AuditInstance> submit(@RequestBody AuditSubmitVo request) {
        return DataSet.success(auditInstanceService.submit(request));
    }

    /**
     * 审核通过
     */
    @PostMapping("/approve")
    public DataSet<AuditInstance> approve(@RequestBody AuditApproveVo request) {
        return DataSet.success(auditInstanceService.approve(request));
    }

    /**
     * 批量审核通过
     * @param request
     * @return
     */
    @PostMapping("/approveBatch")
    public DataSet<List<AuditInstance>> approveBatch(@RequestBody AuditApproveVo request) {
        return DataSet.success(auditInstanceService.approveBatch(request));
    }

    /**
     * 审核驳回
     */
    @PostMapping("/reject")
    public DataSet<AuditInstance> reject(@RequestBody AuditApproveVo request) {
        return DataSet.success(auditInstanceService.reject(request));
    }


    /**
     * 审核批量驳回
     */
    @PostMapping("/rejectBatch")
    public DataSet<List<AuditInstance>> rejectBatch(@RequestBody AuditApproveVo request) {
        return DataSet.success(auditInstanceService.rejectBatch(request));
    }

    /**
     * 获取审核实例详情
     */
    @GetMapping("/{instanceId}")
    public DataSet<AuditInstance> getInstance(@PathVariable Long instanceId) {
        return DataSet.success(auditInstanceService.getInstanceById(instanceId));
    }

    /**
     * 获取我提交的审核
     */
    @GetMapping("/my-submitted")
    public DataSet<List<AuditInstance>> getMySubmitted(AuditTaskVo auditTaskVo) {
        PageUtils.startPage();
        List<AuditInstance> mySubmitted = auditInstanceService.getMySubmitted(auditTaskVo);
        return DataTable.success(mySubmitted,new PageInfo<>(mySubmitted).getTotal());
    }

    /**
     * 获取我的待办审核
     */
//    @GetMapping("/my-todo")
//    public DataSet<List<AuditInstanceNode>> getMyTodoList(@RequestParam String auditorId) {
//        PageUtils.startPage();
//        List<AuditInstanceNode> myTodoList = auditInstanceService.getMyTodoList(auditorId);
//        return DataTable.success(myTodoList,new PageInfo<>(myTodoList).getTotal());
//    }

    /**
     * 撤销审核
     */
    @PostMapping("/cancel/{instanceId}")
    public Message cancelInstance(
            @PathVariable Long instanceId,
            @RequestParam String operatorId) {
        auditInstanceService.cancelInstance(instanceId, operatorId);
        return Message.success();
    }

    /**
     * 审核内容导出
     * @param instanceId
     * @param response
     */
    @PostMapping("/node/export")
    public void export(Long instanceId, HttpServletResponse response) {
        auditInstanceService.exportNodes(instanceId,response);
    }
}