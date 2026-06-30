package com.doinner.csys.domain.vo;

/**
 * @author wzg
 * @date 2023/3/24 9:50
 */
public class CollegeProgramVo {

    private String collectName;
    private Integer schemeCount;
    private Long collectId;

    public Integer getSchemeCount() {
        return schemeCount;
    }

    public void setSchemeCount(Integer schemeCount) {
        this.schemeCount = schemeCount;
    }

    public String getCollectName() {
        return collectName;
    }

    public void setCollectName(String collectName) {
        this.collectName = collectName;
    }


    public Long getCollectId() {
        return collectId;
    }

    public void setCollectId(Long collectId) {
        this.collectId = collectId;
    }
}
