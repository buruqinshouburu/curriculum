package com.doinner.csys.domain.statisticsVo;

import java.util.List;

public class StatisticsExcelMultiVo {

    private String schemeName;

    private List<StatisticsExcelVo> statisticsExcelVos;

    public String getSchemeName() {
        return schemeName;
    }

    public void setSchemeName(String schemeName) {
        this.schemeName = schemeName;
    }

    public List<StatisticsExcelVo> getStatisticsExcelVos() {
        return statisticsExcelVos;
    }

    public void setStatisticsExcelVos(List<StatisticsExcelVo> statisticsExcelVos) {
        this.statisticsExcelVos = statisticsExcelVos;
    }
}
