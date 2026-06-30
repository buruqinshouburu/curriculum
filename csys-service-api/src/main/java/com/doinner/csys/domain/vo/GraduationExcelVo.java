package com.doinner.csys.domain.vo;

import com.doinner.common.core.annotation.Excel;

/**
 * 毕业要求导出对象
 *
 * @author doinner
 */
public class GraduationExcelVo {

    @Excel(name = "学院")
    private String collegeName;

    @Excel(name = "专业")
    private String majorName;

    @Excel(name = "学科门类")
    private String categoryName;

    @Excel(name = "版本")
    private String version;

    @Excel(name = "一级名称")
    private String firstName;

    @Excel(name = "一级编码")
    private String firstCode;

    @Excel(name = "二级名称")
    private String secondName;

    @Excel(name = "二级编码")
    private String secondCode;

    @Excel(name = "三级名称")
    private String thirdName;

    @Excel(name = "三级编码")
    private String thirdCode;

    @Excel(name = "备注")
    private String remark;

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

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getFirstCode() {
        return firstCode;
    }

    public void setFirstCode(String firstCode) {
        this.firstCode = firstCode;
    }

    public String getSecondName() {
        return secondName;
    }

    public void setSecondName(String secondName) {
        this.secondName = secondName;
    }

    public String getSecondCode() {
        return secondCode;
    }

    public void setSecondCode(String secondCode) {
        this.secondCode = secondCode;
    }

    public String getThirdName() {
        return thirdName;
    }

    public void setThirdName(String thirdName) {
        this.thirdName = thirdName;
    }

    public String getThirdCode() {
        return thirdCode;
    }

    public void setThirdCode(String thirdCode) {
        this.thirdCode = thirdCode;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
