package com.doinner.csys.domain.vo;

import com.doinner.common.core.annotation.Excel;

/** 教学计划教材导出，一条有效教材记录对应一行。 */
public class TeachingPlanTextbookExportVo {
    @Excel(name = "学院", sort = 1)
    private String collegeName;
    @Excel(name = "课程名称", sort = 2)
    private String courseName;
    @Excel(name = "课程编号", sort = 3)
    private String courseCode;
    @Excel(name = "课程属性", sort = 4)
    private String courseAttr;
    @Excel(name = "教材名称", sort = 5)
    private String name;
    @Excel(name = "教材性质", sort = 6)
    private String materialNature;
    @Excel(name = "出版方式", sort = 7)
    private String publishMethod;
    @Excel(name = "第一作者", sort = 8)
    private String firstAuthor;
    @Excel(name = "版次", sort = 9)
    private String edition;
    @Excel(name = "出版（颁发）单位", sort = 10)
    private String publisher;
    @Excel(name = "出版（颁发）时间", sort = 11)
    private String publishTime;
    @Excel(name = "ISBN号（统一书号）", sort = 12)
    private String isbn;
    @Excel(name = "教材来源", sort = 13)
    private String textbookSource;

    public String getCollegeName() {
        return collegeName;
    }

    public void setCollegeName(String collegeName) {
        this.collegeName = collegeName;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode;
    }

    public String getCourseAttr() {
        return courseAttr;
    }

    public void setCourseAttr(String courseAttr) {
        this.courseAttr = courseAttr;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMaterialNature() {
        return materialNature;
    }

    public void setMaterialNature(String materialNature) {
        this.materialNature = materialNature;
    }

    public String getPublishMethod() {
        return publishMethod;
    }

    public void setPublishMethod(String publishMethod) {
        this.publishMethod = publishMethod;
    }

    public String getFirstAuthor() {
        return firstAuthor;
    }

    public void setFirstAuthor(String firstAuthor) {
        this.firstAuthor = firstAuthor;
    }

    public String getEdition() {
        return edition;
    }

    public void setEdition(String edition) {
        this.edition = edition;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getPublishTime() {
        return publishTime;
    }

    public void setPublishTime(String publishTime) {
        this.publishTime = publishTime;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getTextbookSource() {
        return textbookSource;
    }

    public void setTextbookSource(String textbookSource) {
        this.textbookSource = textbookSource;
    }
}
