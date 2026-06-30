package com.doinner.csys.domain.vo;

import java.util.List;

public class GraduationBindingSchemeVo {

    // 培养方案id
    private Long schemeId;

    // 毕业要求id集合
    private List<Long> graduationIds;

    public Long getSchemeId() {
        return schemeId;
    }

    public void setSchemeId(Long schemeId) {
        this.schemeId = schemeId;
    }

    public List<Long> getGraduationIds() {
        return graduationIds;
    }

    public void setGraduationIds(List<Long> graduationIds) {
        this.graduationIds = graduationIds;
    }
}
