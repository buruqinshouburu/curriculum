package com.doinner.csys.domain.vo;

/**
 * @author wzg
 * @date 2023/3/24 10:15
 */
public class CurriculumSelectionVo {
    private Long collectId;
    private Integer term;
    private Long countTerm;
    private String collectName;
    private String termName;

    public String getTermName() {
        return termName;
    }

    public void setTermName(String termName) {
        this.termName = termName;
    }

    public Long getCollectId() {
        return collectId;
    }

    public void setCollectId(Long collectId) {
        this.collectId = collectId;
    }

    public Integer getTerm() {
        return term;
    }

    public void setTerm(Integer term) {
        this.term = term;
    }

    public Long getCountTerm() {
        return countTerm;
    }

    public void setCountTerm(Long countTerm) {
        this.countTerm = countTerm;
    }

    public String getCollectName() {
        return collectName;
    }

    public void setCollectName(String collectName) {
        this.collectName = collectName;
    }
}
