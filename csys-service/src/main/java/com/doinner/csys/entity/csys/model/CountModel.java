package com.doinner.csys.entity.csys.model;

import java.math.BigDecimal;

public class CountModel {
    private Double totalHours;
    private Double teachHours;
    private Double practiceHours;
    private Double requireHours;
    private Double requireCredit;
    private Double optionalHours;
    private Double optionalCredit;
    private Double totalCredit;
    //课程学分小计（公共基础/学科基础/专业课程表的学分列合计）
    private Double totalCredits;
    //各学期对应的学时小计：0-7 对应第一学年秋季~第四学年春季，8-9 对应第五学年秋季/春季（五年制时使用）
    private Double[] termHours = {0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0};


    public Double getTotalHours() {
        return totalHours;
    }

    public void setTotalHours(Double totalHours) {
        this.totalHours = new BigDecimal(totalHours).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    public Double getTeachHours() {
        return teachHours;
    }

    public void setTeachHours(Double teachHours) {
        this.teachHours = new BigDecimal(teachHours).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    public Double getPracticeHours() {
        return practiceHours;
    }

    public void setPracticeHours(Double practiceHours) {
        this.practiceHours = new BigDecimal(practiceHours).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    public Double getRequireHours() {
        return requireHours;
    }

    public void setRequireHours(Double requireHours) {
        this.requireHours= new BigDecimal(requireHours).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    public Double getRequireCredit() {
        return requireCredit;
    }

    public void setRequireCredit(Double requireCredit) {
        this.requireCredit = new BigDecimal(requireCredit).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    public Double getOptionalHours() {
        return optionalHours;
    }

    public void setOptionalHours(Double optionalHours) {
        this.optionalHours = new BigDecimal(optionalHours).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    public Double getOptionalCredit() {
        return optionalCredit;
    }

    public void setOptionalCredit(Double optionalCredit) {
        this.optionalCredit = new BigDecimal(optionalCredit).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    public Double getTotalCredit() {
        return totalCredit;
    }

    public void setTotalCredit(Double totalCredit) {
        this.totalCredit= new BigDecimal(totalCredit).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    public Double getTotalCredits() {
        return totalCredits;
    }

    public void setTotalCredits(Double totalCredits) {
        this.totalCredits = new BigDecimal(totalCredits).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    public Double[] getTermHours() {
        return termHours;
    }

    /**
     * 累加某个学期的学时
     *
     * @param index 学期索引（0-7 对应第一学年秋季~第四学年春季；8-9 对应第五学年秋季/春季）
     * @param hours 学时
     */
    public void addTermHours(int index, Double hours) {
        if (hours == null) {
            hours = 0.0;
        }
        if (index < 0 || index >= termHours.length) {
            return;
        }
        this.termHours[index] = new BigDecimal(this.termHours[index] + hours)
                .setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    public CountModel() {
        this.totalHours = 0.0;
        this.teachHours = 0.0;
        this.practiceHours = 0.0;
        this.requireHours= 0.0;
        this.requireCredit= 0.0;
        this.optionalHours= 0.0;
        this.optionalCredit= 0.0;
        this.totalCredit= 0.0;
        this.totalCredits= 0.0;
        for (int i = 0; i < this.termHours.length; i++) {
            this.termHours[i] = 0.0;
        }
    }
}
