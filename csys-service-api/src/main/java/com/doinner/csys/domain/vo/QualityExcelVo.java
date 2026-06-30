package com.doinner.csys.domain.vo;

import com.doinner.common.core.annotation.Excel;

public class QualityExcelVo {
    @Excel(name = "*学院名称")
    private String collegeName;

    @Excel(name = "专业名称")
    private String majorName;

    /*@Excel(name = "专业方向名称")
    private String subMajorName;*/

    @Excel(name = "学科门类名称")
    private String categoryName;

    @Excel(name = "版本")
    private String version;

    @Excel(name = "*素质体系名称")
    private String name;

    @Excel(name = "素质点")
    private String secondName;

    /*@Excel(name = "二级素质描述")
    private String secondDescription;

    @Excel(name = "三级素质名称")
    private String threeName;

    @Excel(name = "三级素质描述")
    private String threeDescription;*/

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSecondName() {
        return secondName;
    }

    public void setSecondName(String secondName) {
        this.secondName = secondName;
    }

    /*public String getSecondDescription() {
        return secondDescription;
    }

    public void setSecondDescription(String secondDescription) {
        this.secondDescription = secondDescription;
    }

    public String getThreeName() {
        return threeName;
    }

    public void setThreeName(String threeName) {
        this.threeName = threeName;
    }

    public String getThreeDescription() {
        return threeDescription;
    }

    public void setThreeDescription(String threeDescription) {
        this.threeDescription = threeDescription;
    }*/

    public String getCollegeName() {
        return collegeName;
    }

    public void setCollegeName(String collegeName) {
        this.collegeName = collegeName;
    }

    public String getMajorName() {
        return majorName;
    }

    public void setMajorName(String majorName) {
        this.majorName = majorName;
    }

    /*public String getSubMajorName() {
        return subMajorName;
    }

    public void setSubMajorName(String subMajorName) {
        this.subMajorName = subMajorName;
    }
*/
    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}
