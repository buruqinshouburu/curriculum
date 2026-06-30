package com.doinner.csys.domain.statisticsVo;

public class CreditStaticticsVo {

    private Integer termId;

    private String term;

    private Double creditCount;

    private String percent;

    public Integer getTermId() {
        return termId;
    }

    public void setTermId(Integer termId) {
        this.termId = termId;
    }

    public String getTerm() {
        return term;
    }

    public void setTerm(String term) {
        this.term = term;
    }

    public Double getCreditCount() {
        return creditCount;
    }

    public void setCreditCount(Double creditCount) {
        this.creditCount = creditCount;
    }

    public String getPercent() {
        return percent;
    }

    public void setPercent(String percent) {
        this.percent = percent;
    }
}
