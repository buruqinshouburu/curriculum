package com.doinner.csys.domain.vo;

import com.doinner.csys.domain.StandardCultivationTarget;

import java.util.List;

public class StandardGraduationRefTargetListVo {


    private List<StandardGraduationVo> standardGraduationList;

    private Long targetTopId;



    public List<StandardGraduationVo> getStandardGraduationList() {
        return standardGraduationList;
    }

    public void setStandardGraduationList(List<StandardGraduationVo> standardGraduationList) {
        this.standardGraduationList = standardGraduationList;
    }

    public Long getTargetTopId() {
        return targetTopId;
    }

    public void setTargetTopId(Long targetTopId) {
        this.targetTopId = targetTopId;
    }
}
