package com.doinner.csys.entity.csys.model;

import java.math.BigDecimal;
import java.util.List;

public class CreditsDetailModel {
    private String modelName;
    private Integer modelNameSort;
    private String childrenNameModelName;
    private Integer childrenNameModelSort;
    private Double requiredCredits;
    private Double requiredHours;
    private Double optionalCredits;
    private Double optionalHours;
    private Double totalCredits;
    private Double totalHours;

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }


    public Double getRequiredCredits() {
        return requiredCredits;
    }

    public void setRequiredCredits(Double requiredCredits) {
        this.requiredCredits = new BigDecimal(requiredCredits).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    public Double getRequiredHours() {
        return requiredHours;
    }

    public void setRequiredHours(Double requiredHours) {
        this.requiredHours = new BigDecimal(requiredHours).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    public Double getOptionalCredits() {
        return optionalCredits;
    }

    public void setOptionalCredits(Double optionalCredits) {
        this.optionalCredits = new BigDecimal(optionalCredits).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    public Double getOptionalHours() {
        return optionalHours;
    }

    public void setOptionalHours(Double optionalHours) {
        this.optionalHours = new BigDecimal(optionalHours).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    public Double getTotalCredits() {
        return totalCredits;
    }

    public void setTotalCredits(Double totalCredits) {
        this.totalCredits = new BigDecimal(totalCredits).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    public Double getTotalHours() {
        return totalHours;
    }

    public void setTotalHours(Double totalHours) {
        this.totalHours = new BigDecimal(totalHours).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    public Integer getModelNameSort() {
        return modelNameSort;
    }

    public void setModelNameSort(Integer modelNameSort) {
        this.modelNameSort = modelNameSort;
    }

    public String getChildrenNameModelName() {
        return childrenNameModelName;
    }

    public void setChildrenNameModelName(String childrenNameModelName) {
        this.childrenNameModelName = childrenNameModelName;
    }

    public Integer getChildrenNameModelSort() {
        return childrenNameModelSort;
    }

    public void setChildrenNameModelSort(Integer childrenNameModelSort) {
        this.childrenNameModelSort = childrenNameModelSort;
    }
}
