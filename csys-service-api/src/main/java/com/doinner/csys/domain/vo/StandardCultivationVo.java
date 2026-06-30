package com.doinner.csys.domain.vo;

import com.doinner.csys.domain.StandardCultivation;
import org.apache.commons.lang3.ObjectUtils;

import java.util.List;

public class StandardCultivationVo extends StandardCultivation {

    private boolean isRef;

    private List<StandardCultivationRefGraduationVo> standardCultivationRefGraduationVoList;

    private List<String> parentNames;

    private   String typeName ;


    public boolean isRef() {
        return ObjectUtils.isNotEmpty(standardCultivationRefGraduationVoList);
    }

    public void setRef(boolean ref) {
        isRef = ref;
    }

    public List<StandardCultivationRefGraduationVo> getStandardCultivationRefGraduationVoList() {
        return standardCultivationRefGraduationVoList;
    }

    public void setStandardCultivationRefGraduationVoList(List<StandardCultivationRefGraduationVo> standardCultivationRefGraduationVoList) {
        this.standardCultivationRefGraduationVoList = standardCultivationRefGraduationVoList;
    }

    public List<String> getParentNames() {
        return parentNames;
    }

    public void setParentNames(List<String> parentNames) {
        this.parentNames = parentNames;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }
}
