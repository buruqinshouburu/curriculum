package com.doinner.csys.controller.audit;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.conditions.query.QueryChainWrapper;
import com.doinner.common.core.domain.DataSet;
import com.doinner.common.core.domain.DataTable;
import com.doinner.common.core.utils.PageUtils;
import com.doinner.csys.domain.vo.audit.AuditTaskVo;
import com.doinner.csys.entity.audit.AuditInstance;
import com.doinner.csys.entity.audit.AuditInstanceNode;
import com.doinner.csys.service.audit.AuditInstanceService;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


import java.util.List;


@RestController
@RequestMapping("/audit/task")
public class AuditTaskController {

    @Autowired
    private AuditInstanceService auditInstanceService;

    /**
     * 获取待办任务列表（带实例信息）
     */
    @GetMapping("/todo-list")
    public DataSet<List<AuditInstance>> getTodoList(AuditTaskVo auditTaskVo) {
        PageUtils.startPage();
        List<AuditInstance> todoList = auditInstanceService.getTodoList(auditTaskVo);
        return DataTable.success(todoList,new PageInfo<>(todoList).getTotal());
    }

    /**
     * 获取已办任务列表
     */
    @GetMapping("/done-list")
    public DataSet<List<AuditInstance>> getDoneList(AuditTaskVo auditTaskVo) {
        PageUtils.startPage();
        List<AuditInstance> doneList = auditInstanceService.getDoneList(auditTaskVo);
        return DataTable.success(doneList,new PageInfo<>(doneList).getTotal());
    }
}