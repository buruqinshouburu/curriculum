package com.doinner.csys.domain;

import com.doinner.common.core.domain.db.AbstractDoinnerLogicalDelBaseEntity;
import com.doinner.common.core.annotation.Excel;

import java.util.List;

/**
 * 学院专业对象 t_csys_std_major
 *
 * @author doinner
 * @date 2023-03-21
 */
public class StandardMajor extends AbstractDoinnerLogicalDelBaseEntity {

    private static final long serialVersionUID = 3866969099379583810L;


    /**
     * 名称
     */
    @Excel(name = "名称")
    private String name;

    /**
     * 编码
     */
    @Excel(name = "编码")
    private String code;

    /**
     * 父级id
     */
    @Excel(name = "父级id")
    private Long parentId;
    private String parentName;

    /**
     * 层级(level)
     */
    @Excel(name = "层级(level)")
    private Long level;

    // 是否叶子(leaf) 1叶子 0非叶子
    private Integer leaf;

    /**
     * 学院(college_id)
     */
    @Excel(name = "学院(college_id)")
    private Long collegeId;
    private String collegeName;

    private Long categoryId;
    private String categoryName;
    private String categoryUrl;

    // 细分专业分类(0:未分类,1:技术类,2:指挥类,3:技术/技术类)
    private String classId;

    /**
     * 适用对象(字典表 sys_education_level)
     */
    @Excel(name = "适用对象")
    private String applicableObject;

    private List<StandardMajor> children;

    private Integer pageNum;

    private Integer pageSize;

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setLevel(Long level) {
        this.level = level;
    }

    public Long getLevel() {
        return level;
    }

    public void setCollegeId(Long collegeId) {
        this.collegeId = collegeId;
    }

    public Long getCollegeId() {
        return collegeId;
    }

    public String getParentName() {
        return parentName;
    }

    public void setParentName(String parentName) {
        this.parentName = parentName;
    }

    public String getCollegeName() {
        return collegeName;
    }

    public void setCollegeName(String collegeName) {
        this.collegeName = collegeName;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getClassId() {
        return classId;
    }

    public void setClassId(String classId) {
        this.classId = classId;
    }

    public String getApplicableObject() {
        return applicableObject;
    }

    public void setApplicableObject(String applicableObject) {
        this.applicableObject = applicableObject;
    }

    public Integer getLeaf() {
        return leaf;
    }

    public void setLeaf(Integer leaf) {
        this.leaf = leaf;
    }

    public Integer getPageNum() {
        return pageNum;
    }

    public void setPageNum(Integer pageNum) {
        this.pageNum = pageNum;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public List<StandardMajor> getChildren() {
        return children;
    }

    public void setChildren(List<StandardMajor> children) {
        this.children = children;
    }

    public String getCategoryUrl() {
        return categoryUrl;
    }

    public void setCategoryUrl(String categoryUrl) {
        this.categoryUrl = categoryUrl;
    }
}
