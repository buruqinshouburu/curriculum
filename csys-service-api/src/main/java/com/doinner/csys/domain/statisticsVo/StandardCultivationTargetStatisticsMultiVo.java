package com.doinner.csys.domain.statisticsVo;

import java.util.List;

public class StandardCultivationTargetStatisticsMultiVo {

    private String schemeName;

    private List<StandardCultivationTargetStatisticsVo> standardCultivationTargetStatisticsVoList;

    public String getSchemeName() {
        return schemeName;
    }

    public void setSchemeName(String schemeName) {
        this.schemeName = schemeName;
    }

    public List<StandardCultivationTargetStatisticsVo> getStandardCultivationTargetStatisticsVoList() {
        return standardCultivationTargetStatisticsVoList;
    }

    public void setStandardCultivationTargetStatisticsVoList(List<StandardCultivationTargetStatisticsVo> standardCultivationTargetStatisticsVoList) {
        this.standardCultivationTargetStatisticsVoList = standardCultivationTargetStatisticsVoList;
    }
}
