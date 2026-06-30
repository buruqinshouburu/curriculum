package com.doinner.csys.domain.vo;

import java.util.List;

public class KnowledgePointVoMaps {
    private Long cId;
    private List<KnowledgePointVo> knowledgePointVoList;

    public Long getcId() {
        return cId;
    }

    public void setcId(Long cId) {
        this.cId = cId;
    }

    public List<KnowledgePointVo> getKnowledgePointVoList() {
        return knowledgePointVoList;
    }

    public void setKnowledgePointVoList(List<KnowledgePointVo> knowledgePointVoList) {
        this.knowledgePointVoList = knowledgePointVoList;
    }
}
