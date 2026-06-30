package com.doinner.csys.domain.vo;

import com.doinner.csys.domain.StandardCultivationTarget;

import java.util.List;

public class StandardCultivationTargetVo extends StandardCultivationTarget {
    /**
     * 是否被关联
     */
    private boolean isRef;

    private String typeName;

    private List<String> parentNames;


    private Long graduationId;

    private Long cultivationTargetId;

    public boolean isRef() {
        return isRef;
    }

    public void setRef(boolean ref) {
        isRef = ref;
    }


    public Long getGraduationId() {
        return graduationId;
    }

    public void setGraduationId(Long graduationId) {
        this.graduationId = graduationId;
    }

    public Long getCultivationTargetId() {
        return cultivationTargetId;
    }

    public void setCultivationTargetId(Long cultivationTargetId) {
        this.cultivationTargetId = cultivationTargetId;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public List<String> getParentNames() {
        return parentNames;
    }

    public void setParentNames(List<String> parentNames) {
        this.parentNames = parentNames;
    }
}
