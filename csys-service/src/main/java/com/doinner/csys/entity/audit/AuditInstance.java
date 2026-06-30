package com.doinner.csys.entity.audit;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 审核实例表
 */
@TableName("audit_instance")
public class AuditInstance implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键 ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 流程编码
     */
    private String flowCode;

    /**
     * 业务类型
     */
    private String businessType;

    /**
     * 业务数据 ID
     */
    private Long businessId;

    /**
     * 业务标题
     */
    private String businessTitle;

    /**
     * 送审人 ID
     */
    private String submitterId;

    /**
     * 送审人姓名
     */
    private String submitterName;

    /**
     * 送审时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime submitTime;

    /**
     * 当前节点 ID
     */
    private Long currentNodeId;

    /**
     * 当前节点标识
     */
    private String currentNodeKey;

    /**
     * 实例状态 0-审核中 1-审核通过 2-审核驳回 3-已撤销
     */
    private Integer instanceStatus;

    /**
     * 最终结果 0-驳回 1-通过
     */
    private Integer finalResult;

    /**
     * 完成时间
     */
    private LocalDateTime finishTime;

    /**
     * 备注
     */
    private String remark;

    private String flowName;

    private Long flowId;

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

    @TableField(exist = false)
    private List<AuditInstanceNode> nodes;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFlowCode() {
        return flowCode;
    }

    public void setFlowCode(String flowCode) {
        this.flowCode = flowCode;
    }

    public String getBusinessType() {
        return businessType;
    }

    public void setBusinessType(String businessType) {
        this.businessType = businessType;
    }

    public Long getBusinessId() {
        return businessId;
    }

    public void setBusinessId(Long businessId) {
        this.businessId = businessId;
    }

    public String getBusinessTitle() {
        return businessTitle;
    }

    public void setBusinessTitle(String businessTitle) {
        this.businessTitle = businessTitle;
    }

    public String getSubmitterId() {
        return submitterId;
    }

    public void setSubmitterId(String submitterId) {
        this.submitterId = submitterId;
    }

    public String getSubmitterName() {
        return submitterName;
    }

    public void setSubmitterName(String submitterName) {
        this.submitterName = submitterName;
    }

    public LocalDateTime getSubmitTime() {
        return submitTime;
    }

    public void setSubmitTime(LocalDateTime submitTime) {
        this.submitTime = submitTime;
    }

    public Long getCurrentNodeId() {
        return currentNodeId;
    }

    public void setCurrentNodeId(Long currentNodeId) {
        this.currentNodeId = currentNodeId;
    }

    public String getCurrentNodeKey() {
        return currentNodeKey;
    }

    public void setCurrentNodeKey(String currentNodeKey) {
        this.currentNodeKey = currentNodeKey;
    }

    public Integer getInstanceStatus() {
        return instanceStatus;
    }

    public void setInstanceStatus(Integer instanceStatus) {
        this.instanceStatus = instanceStatus;
    }

    public Integer getFinalResult() {
        return finalResult;
    }

    public void setFinalResult(Integer finalResult) {
        this.finalResult = finalResult;
    }

    public LocalDateTime getFinishTime() {
        return finishTime;
    }

    public void setFinishTime(LocalDateTime finishTime) {
        this.finishTime = finishTime;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
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

    public List<AuditInstanceNode> getNodes() {
        return nodes;
    }
    public void setNodes(List<AuditInstanceNode> nodes) {
        this.nodes = nodes;
    }

    public String getFlowName() {
        return flowName;
    }

    public void setFlowName(String flowName) {
        this.flowName = flowName;
    }

    public Long getFlowId() {
        return flowId;
    }

    public void setFlowId(Long flowId) {
        this.flowId = flowId;
    }





    @Override
    public String toString() {
        return "AuditInstance{" +
                "id=" + id +
                ", flowCode='" + flowCode + '\'' +
                ", businessType='" + businessType + '\'' +
                ", businessId=" + businessId +
                ", businessTitle='" + businessTitle + '\'' +
                ", submitterId='" + submitterId + '\'' +
                ", submitterName='" + submitterName + '\'' +
                ", submitTime=" + submitTime +
                ", currentNodeId=" + currentNodeId +
                ", currentNodeKey='" + currentNodeKey + '\'' +
                ", instanceStatus=" + instanceStatus +
                ", finalResult=" + finalResult +
                ", finishTime=" + finishTime +
                ", remark='" + remark + '\'' +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                '}';
    }
}