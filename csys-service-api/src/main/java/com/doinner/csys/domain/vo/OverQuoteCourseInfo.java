package com.doinner.csys.domain.vo;

import com.doinner.common.core.annotation.Excel;
import org.springframework.beans.factory.annotation.Value;

public class OverQuoteCourseInfo {
    @Excel(name="课程名称")
    private String courseName;
    @Excel(name="最高承载班次")
    private Integer maxQuoteCount;
    @Excel(name="被选中次数")
    private Integer quoteCount;
    @Excel(name="选用专业名称")
    private String majorNames;

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public Integer getMaxQuoteCount() {
        return maxQuoteCount;
    }

    public void setMaxQuoteCount(Integer maxQuoteCount) {
        this.maxQuoteCount = maxQuoteCount;
    }

    public Integer getQuoteCount() {
        return quoteCount;
    }

    public void setQuoteCount(Integer quoteCount) {
        this.quoteCount = quoteCount;
    }

    public String getMajorNames() {
        return majorNames;
    }

    public void setMajorNames(String majorNames) {
        this.majorNames = majorNames;
    }
}
