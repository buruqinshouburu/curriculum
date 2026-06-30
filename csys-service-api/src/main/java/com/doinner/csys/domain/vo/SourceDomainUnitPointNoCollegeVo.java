package com.doinner.csys.domain.vo;

import com.doinner.common.core.annotation.Excel;

public class SourceDomainUnitPointNoCollegeVo {

    @Excel(name = "版本")
    private String version;

    @Excel(name = "*知识领域")
    private String domainName;

    @Excel(name = "*知识单元")
    private String unitName;

    @Excel(name = "知识点")
    private String pointName;

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getDomainName() {
        return domainName;
    }

    public void setDomainName(String domainName) {
        this.domainName = domainName;
    }

    public String getUnitName() {
        return unitName;
    }

    public void setUnitName(String unitName) {
        this.unitName = unitName;
    }

    public String getPointName() {
        return pointName;
    }

    public void setPointName(String pointName) {
        this.pointName = pointName;
    }

}
