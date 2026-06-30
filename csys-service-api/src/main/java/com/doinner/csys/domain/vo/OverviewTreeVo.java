package com.doinner.csys.domain.vo;

import com.doinner.csys.domain.SourceDomain;
import com.doinner.csys.domain.StandardAbility;

import java.util.ArrayList;
import java.util.List;

public class OverviewTreeVo {

    private String majorName;

    private List<StandardAbility> standardAbilitieList;

    private List<SourceDomainTreeVo> sourceDomainTreeVoList;

    public List<StandardAbility> getStandardAbilitieList() {
        return standardAbilitieList;
    }

    public void setStandardAbilitieList(List<StandardAbility> standardAbilitieList) {
        this.standardAbilitieList = standardAbilitieList;
    }

    public List<SourceDomainTreeVo> getSourceDomainTreeVoList() {
        return sourceDomainTreeVoList;
    }

    public void setSourceDomainTreeVoList(List<SourceDomainTreeVo> sourceDomainTreeVoList) {
        this.sourceDomainTreeVoList = sourceDomainTreeVoList;
    }

    public String getMajorName() {
        return majorName;
    }

    public void setMajorName(String majorName) {
        this.majorName = majorName;
    }

    public OverviewTreeVo() {
        this.standardAbilitieList = new ArrayList<>();
        this.sourceDomainTreeVoList = new ArrayList<>();
    }
}
