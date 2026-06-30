package com.doinner.csys.domain.vo;

import com.doinner.csys.domain.KnowledgeUnit;

import java.util.List;

/**
 * 知识单元Vo对象
 * */
public class KnowledgeUnitVo  extends KnowledgeUnit {
    /**
     * 初始掌握程度
     */
    private String initLevelName;
    /**
     * 要求掌握程度
     */
    private String requireLevelName;

    private Long domainId;

    //@JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
    private List<KnowledgePointVo> knowledgePointVoList;

    //@JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
    private List<KnowledgePointVo> children;

    public String getInitLevelName() {
        return initLevelName;
    }

    public void setInitLevelName(String initLevelName) {
        this.initLevelName = initLevelName;
    }

    public String getRequireLevelName() {
        return requireLevelName;
    }

    public void setRequireLevelName(String requireLevelName) {
        this.requireLevelName = requireLevelName;
    }

    public List<KnowledgePointVo> getChildren() {
        return children;
    }

    public void setChildren(List<KnowledgePointVo> children) {
        this.children = children;
    }

    public List<KnowledgePointVo> getKnowledgePointVoList() {
        return knowledgePointVoList;
    }

    public void setKnowledgePointVoList(List<KnowledgePointVo> knowledgePointVoList) {
        this.knowledgePointVoList = knowledgePointVoList;
    }

    public Long getDomainId() {
        return domainId;
    }

    public void setDomainId(Long domainId) {
        this.domainId = domainId;
    }
}
