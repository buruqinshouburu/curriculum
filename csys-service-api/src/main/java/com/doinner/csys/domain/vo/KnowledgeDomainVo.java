package com.doinner.csys.domain.vo;

import com.doinner.csys.domain.KnowledgeDomain;

import java.util.List;

/**
 * @author wzg
 * @date 2023/3/21 10:47
 */
public class KnowledgeDomainVo extends KnowledgeDomain {
    /**
     * 知识单元
     */
    private List<KnowledgeUnitVo> knowledgeUnitVoList;

    private List<KnowledgeUnitVo> children;

    public List<KnowledgeUnitVo> getChildren() {
        return children;
    }

    public void setChildren(List<KnowledgeUnitVo> children) {
        this.children = children;
    }

    public List<KnowledgeUnitVo> getKnowledgeUnitVoList() {
        return knowledgeUnitVoList;
    }

    public void setKnowledgeUnitVoList(List<KnowledgeUnitVo> knowledgeUnitVoList) {
        this.knowledgeUnitVoList = knowledgeUnitVoList;
    }
}
