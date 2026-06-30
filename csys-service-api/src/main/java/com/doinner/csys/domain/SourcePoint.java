package com.doinner.csys.domain;

import com.doinner.common.core.annotation.Excel;
import com.doinner.common.core.domain.db.AbstractDoinnerLogicalDelBaseEntity;

/**
 * 源知识点对象 t_csys_source_point
 *
 * @author wzg
 * @date 2026-02-26
 */
public class SourcePoint extends AbstractDoinnerLogicalDelBaseEntity {

    private static final long serialVersionUID=1L;

    /** 主键 */
    private Long id;

    /** 知识点名称 */
            @Excel(name = "知识点名称")
    private String name;

    /** 创建部门id */
            @Excel(name = "创建部门id")
    private Long deptBy;

    private Long unitId;

    private Integer type;
    private Long sourceId;

    public Long getUnitId() {
        return unitId;
    }

    public void setUnitId(Long unitId) {
        this.unitId = unitId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getDeptBy() {
        return deptBy;
    }

    public void setDeptBy(Long deptBy) {
        this.deptBy = deptBy;
    }

    public SourcePoint() {
    }

    public SourcePoint(String name) {
        this.name = name;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Long getSourceId() {
        return sourceId;
    }

    public void setSourceId(Long sourceId) {
        this.sourceId = sourceId;
    }
}
