package com.doinner.csys.domain.vo;

import java.util.List;

public class MatrixVo {
    private List<TreeTableVo> vertical;

    private List<TreeTableVo> horizontal;

    private List<ExcelRelationshipVo> relationshipVoList;

    private String totalTitle;

    private String partialTitle;

    public List<TreeTableVo> getVertical() {
        return vertical;
    }

    public void setVertical(List<TreeTableVo> vertical) {
        this.vertical = vertical;
    }

    public List<TreeTableVo> getHorizontal() {
        return horizontal;
    }

    public void setHorizontal(List<TreeTableVo> horizontal) {
        this.horizontal = horizontal;
    }

    public List<ExcelRelationshipVo> getRelationshipVoList() {
        return relationshipVoList;
    }

    public void setRelationshipVoList(List<ExcelRelationshipVo> relationshipVoList) {
        this.relationshipVoList = relationshipVoList;
    }

    public String getTotalTitle() {
        return totalTitle;
    }

    public void setTotalTitle(String totalTitle) {
        this.totalTitle = totalTitle;
    }

    public String getPartialTitle() {
        return partialTitle;
    }

    public void setPartialTitle(String partialTitle) {
        this.partialTitle = partialTitle;
    }

    public MatrixVo() {
    }

    public MatrixVo(List<TreeTableVo> vertical, List<TreeTableVo> horizontal, List<ExcelRelationshipVo> relationshipVoList, String totalTitle, String partialTitle) {
        this.vertical = vertical;
        this.horizontal = horizontal;
        this.relationshipVoList = relationshipVoList;
        this.totalTitle = totalTitle;
        this.partialTitle = partialTitle;
    }
}
