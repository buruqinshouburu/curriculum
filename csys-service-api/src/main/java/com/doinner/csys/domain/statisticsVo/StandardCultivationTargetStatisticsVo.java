package com.doinner.csys.domain.statisticsVo;

import org.apache.commons.lang3.ObjectUtils;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class StandardCultivationTargetStatisticsVo {

    private Long denominatorId;

    private Long denominatorCount;

    private AtomicReference<Long> denominatorCountS;

    private String denominatorName;

    private Long numeratorCount = 0L;

    private List<SubCourseVo> subCourseVoList;

    public List<SubCourseVo> getSubCourseVoList() {
        return subCourseVoList;
    }

    public void setSubCourseVoList(List<SubCourseVo> subCourseVoList) {
        this.subCourseVoList = subCourseVoList;
    }

    public Long getDenominatorId() {
        return denominatorId;
    }

    public void setDenominatorId(Long denominatorId) {
        this.denominatorId = denominatorId;
    }

    public Long getDenominatorCount() {
        if (ObjectUtils.isNotEmpty(denominatorCountS)) {
            this.denominatorCount = denominatorCountS.get();
        }
        return denominatorCount;
    }

    public void setDenominatorCount(Long denominatorCount) {
        this.denominatorCount = denominatorCount;
    }

    public String getDenominatorName() {

        return denominatorName;
    }

    public void setDenominatorName(String denominatorName) {
        this.denominatorName = denominatorName;
    }

    public Long getNumeratorCount() {
        return numeratorCount;
    }

    public void setNumeratorCount(Long numeratorCount) {
        this.numeratorCount = numeratorCount;
    }

    public AtomicReference<Long> getDenominatorCountS() {
        return denominatorCountS;
    }

    public void setDenominatorCountS(AtomicReference<Long> denominatorCountS) {
        this.denominatorCountS = denominatorCountS;
    }
}
