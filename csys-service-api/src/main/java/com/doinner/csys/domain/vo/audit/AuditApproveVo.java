package com.doinner.csys.domain.vo.audit;

import java.io.Serializable;
import java.util.List;

public class AuditApproveVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long instanceNodeId;

    private String auditorId;

    private String auditorName;

    private String auditOpinion;
    /**
     * 下一个节点的审核人 ID（由前端传入）
     */
    private String nextAuditorId;

    /**
     * 下一个节点的审核人姓名
     */
    private String nextAuditorName;

    private List<Long> instanceNodeIds;

    public Long getInstanceNodeId() {
        return instanceNodeId;
    }

    public void setInstanceNodeId(Long instanceNodeId) {
        this.instanceNodeId = instanceNodeId;
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

    public String getAuditOpinion() {
        return auditOpinion;
    }

    public void setAuditOpinion(String auditOpinion) {
        this.auditOpinion = auditOpinion;
    }

    public String getNextAuditorId() {
        return nextAuditorId;
    }

    public void setNextAuditorId(String nextAuditorId) {
        this.nextAuditorId = nextAuditorId;
    }

    public String getNextAuditorName() {
        return nextAuditorName;
    }

    public void setNextAuditorName(String nextAuditorName) {
        this.nextAuditorName = nextAuditorName;
    }

    public List<Long> getInstanceNodeIds() {
        return instanceNodeIds;
    }

    public void setInstanceNodeIds(List<Long> instanceNodeIds) {
        this.instanceNodeIds = instanceNodeIds;
    }
}