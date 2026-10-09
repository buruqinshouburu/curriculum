package com.doinner.csys.domain.vo;

/**
 * @author wzg
 * @date 2023/3/24 16:43
 */
public class TrainingSchemeScheduleVo {
    private Long id;
    private Integer term;
    /** 排课课程模块/子模块字典 ID，保留原 type 字段名。 */
    private String type;
    private String name;
    private String schemeName;
    private String termName;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getTerm() {
        return term;
    }

    public void setTerm(Integer term) {
        this.term = term;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSchemeName() {
        return schemeName;
    }

    public void setSchemeName(String schemeName) {
        this.schemeName = schemeName;
    }

    public String getTermName() {
        return termName;
    }

    public void setTermName(String termName) {
        this.termName = termName;
    }
}
