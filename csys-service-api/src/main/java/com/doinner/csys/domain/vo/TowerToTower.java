package com.doinner.csys.domain.vo;

import java.util.List;

/** 塔对塔 */
public class TowerToTower {
    /** 左端塔顶级节点id */
    private Long  sourceTopId;
    /** 右端塔顶级节点id */
    private Long  targetTopId;

    List<TowerLayer> refIdsInfo;

    public Long getTargetTopId() {
        return targetTopId;
    }

    public void setTargetTopId(Long targetTopId) {
        this.targetTopId = targetTopId;
    }

    public List<TowerLayer> getRefIdsInfo() {
        return refIdsInfo;
    }

    public void setRefIdsInfo(List<TowerLayer> refIdsInfo) {
        this.refIdsInfo = refIdsInfo;
    }

    public Long getSourceTopId() {
        return sourceTopId;
    }

    public void setSourceTopId(Long sourceTopId) {
        this.sourceTopId = sourceTopId;
    }
}
