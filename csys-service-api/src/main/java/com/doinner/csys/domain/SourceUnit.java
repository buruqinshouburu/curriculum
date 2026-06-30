package com.doinner.csys.domain;

import com.doinner.common.core.annotation.Excel;
import com.doinner.common.core.domain.db.AbstractDoinnerLogicalDelBaseEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * 源知识单元对象 t_csys_source_unit
 *
 * @author wzg
 * @date 2026-02-26
 */
public class SourceUnit extends AbstractDoinnerLogicalDelBaseEntity {
    private static final long serialVersionUID=1L;

    /** 主键 */
    private Long id;

    /** 知识单元名称 */
            @Excel(name = "知识单元名称")
    private String name;

    /** 初始掌握程度 */
            @Excel(name = "初始掌握程度")
    private Long initLevel;

    /** 要求掌握程度 */
            @Excel(name = "要求掌握程度")
    private Long requireLevel;

    /** 学习目标 */
            @Excel(name = "学习目标")
    private String learnTarget;

    /** 实现环节 */
            @Excel(name = "实现环节")
    private String realizeLink;

    /** 创建部门id */
            @Excel(name = "创建部门id")
    private Long deptBy;

    private Long domainId;

    private Integer type;
    private Long sourceId;

    private List<SourcePoint> sourcePoints;


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

    public Long getInitLevel() {
        return initLevel;
    }

    public void setInitLevel(Long initLevel) {
        this.initLevel = initLevel;
    }

    public Long getRequireLevel() {
        return requireLevel;
    }

    public void setRequireLevel(Long requireLevel) {
        this.requireLevel = requireLevel;
    }

    public String getLearnTarget() {
        return learnTarget;
    }

    public void setLearnTarget(String learnTarget) {
        this.learnTarget = learnTarget;
    }

    public String getRealizeLink() {
        return realizeLink;
    }

    public void setRealizeLink(String realizeLink) {
        this.realizeLink = realizeLink;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public String getLastModifier() {
        return lastModifier;
    }

    public void setLastModifier(String lastModifier) {
        this.lastModifier = lastModifier;
    }

    public Long getDeptBy() {
        return deptBy;
    }

    public void setDeptBy(Long deptBy) {
        this.deptBy = deptBy;
    }

    public List<SourcePoint> getSourcePoints() {
        return sourcePoints;
    }

    public void setSourcePoints(List<SourcePoint> sourcePoints) {
        this.sourcePoints = sourcePoints;
    }

    public Long getDomainId() {
        return domainId;
    }

    public void setDomainId(Long domainId) {
        this.domainId = domainId;
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

    public SourceUnit() {
    }

    public SourceUnit(String name) {
        this.name = name;
        this.sourcePoints = new ArrayList<SourcePoint>();
    }
}
