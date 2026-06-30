package com.doinner.csys.domain.statisticsVo;

import com.doinner.csys.constant.DomainFieldConstant;
import com.doinner.csys.domain.vo.SchemeCountVo;

public class StatisticsExcelVo {

    private String name;

    private Double number1;

    private Double number2;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getNumber1() {
        return number1;
    }

    public void setNumber1(Double number1) {
        this.number1 = number1;
    }

    public Double getNumber2() {
        return number2;
    }

    public void setNumber2(Double number2) {
        this.number2 = number2;
    }

    public StatisticsExcelVo() {
    }

    public StatisticsExcelVo(CreditStaticticsVo staticticsVo) {
        this.name = staticticsVo.getTerm();
        this.number1 = staticticsVo.getCreditCount();
    }

    public StatisticsExcelVo(CourseTypeVo courseTypeVo) {
        this.name = DomainFieldConstant.TERM_NUMBER_NAME_MAP.get(courseTypeVo.getTermId());
        this.number1 = courseTypeVo.getRequiredCourseCount().doubleValue();
        this.number2 = courseTypeVo.getElectiveCourseCount().doubleValue();
    }

    public StatisticsExcelVo(SchemeCountVo schemeCountVo) {
        this.name = schemeCountVo.getCategoryName();
        this.number1 = Double.valueOf(schemeCountVo.getSchemeNum());
        this.number2 = schemeCountVo.getProportion();
    }
}
