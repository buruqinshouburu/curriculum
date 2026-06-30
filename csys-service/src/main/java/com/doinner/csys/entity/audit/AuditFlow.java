package com.doinner.csys.entity.audit;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 审核流程表
 */
@TableName("audit_flow")
public class AuditFlow implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键 ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 流程编码，区分不同流程类型
     */
    private String code;

    /**
     * 流程名称
     */
    private String name;

    /**
     * 流程描述
     */
    private String description;

    /**
     * 目标业务表名
     */
    private String targetTable;

    /**
     * 目标表主键字段名
     */
    private String targetIdField;

    /**
     * 目标表状态字段名
     */
    private String targetStatusField;

    /**
     * 流程状态 0-禁用 1-启用
     */
    private Integer status;

    /**
     * 版本号
     */
    private Integer version;

    /**
     * 创建人
     */
    private String createBy;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新人
     */
    private String updateBy;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableField(exist = false)
    private List<AuditNode> nodes;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTargetTable() {
        return targetTable;
    }

    public void setTargetTable(String targetTable) {
        this.targetTable = targetTable;
    }

    public String getTargetIdField() {
        return targetIdField;
    }

    public void setTargetIdField(String targetIdField) {
        this.targetIdField = targetIdField;
    }

    public String getTargetStatusField() {
        return targetStatusField;
    }

    public void setTargetStatusField(String targetStatusField) {
        this.targetStatusField = targetStatusField;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public String getCreateBy() {
        return createBy;
    }

    public void setCreateBy(String createBy) {
        this.createBy = createBy;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public String getUpdateBy() {
        return updateBy;
    }

    public void setUpdateBy(String updateBy) {
        this.updateBy = updateBy;
    }

    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }

    public List<AuditNode> getNodes() {
        return nodes;
    }

    public void setNodes(List<AuditNode> nodes) {
        this.nodes = nodes;
    }

    @Override
    public String toString() {
        return "AuditFlow{" +
                "id=" + id +
                ", code='" + code + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", targetTable='" + targetTable + '\'' +
                ", targetIdField='" + targetIdField + '\'' +
                ", targetStatusField='" + targetStatusField + '\'' +
                ", status=" + status +
                ", version=" + version +
                ", createBy='" + createBy + '\'' +
                ", createTime=" + createTime +
                ", updateBy='" + updateBy + '\'' +
                ", updateTime=" + updateTime +
                '}';
    }
}