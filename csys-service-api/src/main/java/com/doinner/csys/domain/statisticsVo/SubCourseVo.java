package com.doinner.csys.domain.statisticsVo;

public class SubCourseVo {

    private Long numeratorCount = 0L;

    private String denominatorName;

    private Long denominatorCount;


    public Long getNumeratorCount() {
        return numeratorCount;
    }

    public void setNumeratorCount(Long numeratorCount) {
        this.numeratorCount = numeratorCount;
    }

    public String getDenominatorName() {
        return denominatorName;
    }

    public void setDenominatorName(String denominatorName) {
        this.denominatorName = denominatorName;
    }

    public Long getDenominatorCount() {
        return denominatorCount;
    }

    public void setDenominatorCount(Long denominatorCount) {
        this.denominatorCount = denominatorCount;
    }

}
