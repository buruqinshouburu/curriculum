package com.doinner.csys.domain.vo;

import com.doinner.csys.domain.StandardGraduation;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.data.annotation.AccessType;

import java.util.List;

public class StandardGraduationVo extends StandardGraduation {

    private boolean isRef;

    private String typeName;

    private List<String> parentNames;

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

    private List<StandardGraduationRefTargetVo> standardGraduationRefTargetVoList;


    public List<StandardGraduationRefTargetVo> getStandardGraduationRefTargetVoList() {
        return standardGraduationRefTargetVoList;
    }

    public void setStandardGraduationRefTargetVoList(List<StandardGraduationRefTargetVo> standardGraduationRefTargetVoList) {
        this.standardGraduationRefTargetVoList = standardGraduationRefTargetVoList;
    }

    public boolean isRef() {
        return ObjectUtils.isNotEmpty(standardGraduationRefTargetVoList);
    }

    public void setRef(boolean ref) {
        isRef = ref;
    }


}
