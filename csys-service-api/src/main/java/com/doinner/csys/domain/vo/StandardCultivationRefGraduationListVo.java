package com.doinner.csys.domain.vo;

import java.util.List;

public class StandardCultivationRefGraduationListVo {

    private List<StandardCultivationVo> standardCultivationList;

    private Long targetTopId;

    public List<StandardCultivationVo> getStandardCultivationList() {
        return standardCultivationList;
    }

    public void setStandardCultivationList(List<StandardCultivationVo> standardCultivationList) {
        this.standardCultivationList = standardCultivationList;
    }

    public Long getTargetTopId() {
        return targetTopId;
    }

    public void setTargetTopId(Long targetTopId) {
        this.targetTopId = targetTopId;
    }
}
