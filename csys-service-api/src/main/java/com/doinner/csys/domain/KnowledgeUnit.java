package com.doinner.csys.domain;

import com.doinner.common.core.domain.db.AbstractDoinnerLogicalDelBaseEntity;
import com.doinner.common.core.annotation.Excel;

import java.util.List;

/**
 * 知识单元对象 t_csys_knowledge_unit
 *
 * @author doinner
 */
public class KnowledgeUnit extends AbstractDoinnerLogicalDelBaseEntity {

    private static final long serialVersionUID = 7208992356023155818L;

    /**
     * 知识单元名称
     */
    @Excel(name = "知识单元名称")
    private String name;

    /**
     * 初始掌握程度
     */
    @Excel(name = "初始掌握程度")
    private Integer initLevel;

    /**
     * 要求掌握程度
     */
    @Excel(name = "要求掌握程度")
    private Integer requireLevel;

    /**
     * 学习目标
     */
    @Excel(name = "学习目标")
    private String learnTarget;

    /**
     * 实现环节
     */
    @Excel(name = "实现环节")
    private String realizeLink;

    /**
     * 创建部门ID
     */
    private Long deptBy;

    private List<KnowledgePoint> knowledgePointList;


    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setInitLevel(Integer initLevel) {
        this.initLevel = initLevel;
    }

    public Integer getInitLevel() {
        return initLevel;
    }

    public void setRequireLevel(Integer requireLevel) {
        this.requireLevel = requireLevel;
    }

    public Integer getRequireLevel() {
        return requireLevel;
    }

    public void setLearnTarget(String learnTarget) {
        this.learnTarget = learnTarget;
    }

    public String getLearnTarget() {
        return learnTarget;
    }

    public void setRealizeLink(String realizeLink) {
        this.realizeLink = realizeLink;
    }

    public String getRealizeLink() {
        return realizeLink;
    }

    public List<KnowledgePoint> getKnowledgePointList() {
        return knowledgePointList;
    }

    public void setKnowledgePointList(List<KnowledgePoint> knowledgePointList) {
        this.knowledgePointList = knowledgePointList;
    }

    public Long getDeptBy() {
        return deptBy;
    }

    public void setDeptBy(Long deptBy) {
        this.deptBy = deptBy;
    }


}
