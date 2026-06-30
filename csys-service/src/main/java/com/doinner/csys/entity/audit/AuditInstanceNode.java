package com.doinner.csys.entity.audit;

import com.baomidou.mybatisplus.annotation.*;
import com.doinner.common.core.annotation.Excel;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 审核实例节点表
 */
@TableName("audit_instance_node")
public class AuditInstanceNode implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键 ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 审核实例 ID
     */
    private Long instanceId;

    /**
     * 流程 ID
     */
    private Long flowId;

    /**
     * 节点 ID
     */
    private Long nodeId;

    /**
     * 节点标识
     */
    private String nodeKey;

    /**
     * 节点名称
     */
    private String nodeName;

    /**
     * 节点顺序
     */
    @Excel(name = "序号")
    private Integer nodeOrder;

    /**
     * 审核角色编码
     */
    private Long roleId;

    /**
     * 审核人 ID
     */
    private String auditorId;

    /**
     * 审核人姓名
     */
    @Excel(name = "审核人")
    private String auditorName;

    /**
     * 节点状态 0-待审核 1-已通过 2-已驳回 3-已跳过
     */
    @Excel(name = "审核结果" ,readConverterExp = "0=待审核,1=已通过,2=已驳回,3=已跳过")
    private Integer nodeStatus;

    /**
     * 审核结果 0-驳回 1-通过
     */

    private Integer auditResult;

    /**
     * 审核意见
     */
    @Excel(name = "审核内容")
    private String auditOpinion;

    /**
     * 审核时间
     */
    @Excel(name = "审核时间")
    private LocalDateTime auditTime;

    /**
     * 接收时间
     */
    private LocalDateTime receiveTime;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getInstanceId() {
        return instanceId;
    }

    public void setInstanceId(Long instanceId) {
        this.instanceId = instanceId;
    }

    public Long getFlowId() {
        return flowId;
    }

    public void setFlowId(Long flowId) {
        this.flowId = flowId;
    }

    public Long getNodeId() {
        return nodeId;
    }

    public void setNodeId(Long nodeId) {
        this.nodeId = nodeId;
    }

    public String getNodeKey() {
        return nodeKey;
    }

    public void setNodeKey(String nodeKey) {
        this.nodeKey = nodeKey;
    }

    public String getNodeName() {
        return nodeName;
    }

    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
    }

    public Integer getNodeOrder() {
        return nodeOrder;
    }

    public void setNodeOrder(Integer nodeOrder) {
        this.nodeOrder = nodeOrder;
    }

    public Long getRoleId() {
        return roleId;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }

    public String getAuditorId() {
        return auditorId;
    }

    public void setAuditorId(String auditorId) {
        this.auditorId = auditorId;
    }

    public String getAuditorName() {
        return auditorName;
    }

    public void setAuditorName(String auditorName) {
        this.auditorName = auditorName;
    }

    public Integer getNodeStatus() {
        return nodeStatus;
    }

    public void setNodeStatus(Integer nodeStatus) {
        this.nodeStatus = nodeStatus;
    }

    public Integer getAuditResult() {
        return auditResult;
    }

    public void setAuditResult(Integer auditResult) {
        this.auditResult = auditResult;
    }

    public String getAuditOpinion() {
        return auditOpinion;
    }

    public void setAuditOpinion(String auditOpinion) {
        this.auditOpinion = auditOpinion;
    }

    public LocalDateTime getAuditTime() {
        return auditTime;
    }

    public void setAuditTime(LocalDateTime auditTime) {
        this.auditTime = auditTime;
    }

    public LocalDateTime getReceiveTime() {
        return receiveTime;
    }

    public void setReceiveTime(LocalDateTime receiveTime) {
        this.receiveTime = receiveTime;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }

    @Override
    public String toString() {
        return "AuditInstanceNode{" +
                "id=" + id +
                ", instanceId=" + instanceId +
                ", flowId=" + flowId +
                ", nodeId=" + nodeId +
                ", nodeKey='" + nodeKey + '\'' +
                ", nodeName='" + nodeName + '\'' +
                ", nodeOrder=" + nodeOrder +
                ", roleId='" + roleId + '\'' +
                ", auditorId='" + auditorId + '\'' +
                ", auditorName='" + auditorName + '\'' +
                ", nodeStatus=" + nodeStatus +
                ", auditResult=" + auditResult +
                ", auditOpinion='" + auditOpinion + '\'' +
                ", auditTime=" + auditTime +
                ", receiveTime=" + receiveTime +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                '}';
    }
}