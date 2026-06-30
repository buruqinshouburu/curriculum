package com.doinner.csys.domain;

import com.doinner.common.core.domain.db.AbstractLogicalDelTreeBaseEntity;
import com.doinner.common.core.domain.db.AbstractTreeBaseEntity;
import com.doinner.common.core.annotation.Excel;

import java.util.List;

/**
 * 培养目标对象 t_csys_std_cultivation_target
 *
 * @author doinner
 * @date 2023-03-21
 */
public class StandardCultivationTarget extends AbstractLogicalDelTreeBaseEntity {


    private static final long serialVersionUID = -2567447116163575176L;

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
     * 学院(college_id)
     */
    @Excel(name = "学院(college_id)")
    private Long collegeId;

    /**
     * 专业(major)
     */
    @Excel(name = "专业(major)")
    private Long majorId;

    private String majorName;

    /**
     * 细分专业(sub_major)
     */
    @Excel(name = "细分专业(sub_major)")
    private Long subMajorId;

    private String subMajorName;

    /**
     * 技术指挥分类(class)    0：未分类、1：技术类、2：指挥类
     */
    @Excel(name = "技术指挥分类(class)    0：未分类、1：技术类、2：指挥类")
    private Long classId;
    /**
     * 学科门类
     */
    private Long categoryId;

    public String categoryName;

    private String version;

    private Long trainingSchemeId;

    private List<StandardCultivationTarget> childrenList;

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

    public void setCollegeId(Long collegeId) {
        this.collegeId = collegeId;
    }

    public Long getCollegeId() {
        return collegeId;
    }

    public void setMajorId(Long majorId) {
        this.majorId = majorId;
    }

    public Long getMajorId() {
        return majorId;
    }

    public void setSubMajorId(Long subMajorId) {
        this.subMajorId = subMajorId;
    }

    public Long getSubMajorId() {
        return subMajorId;
    }

    public void setClassId(Long classId) {
        this.classId = classId;
    }

    public Long getClassId() {
        return classId;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public List<StandardCultivationTarget> getChildrenList() {
        return childrenList;
    }

    public void setChildrenList(List<StandardCultivationTarget> childrenList) {
        this.childrenList = childrenList;
    }

    public String getMajorName() {
        return majorName;
    }

    public void setMajorName(String majorName) {
        this.majorName = majorName;
    }

    public String getSubMajorName() {
        return subMajorName;
    }

    public void setSubMajorName(String subMajorName) {
        this.subMajorName = subMajorName;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public Long getTrainingSchemeId() {
        return trainingSchemeId;
    }

    public void setTrainingSchemeId(Long trainingSchemeId) {
        this.trainingSchemeId = trainingSchemeId;
    }
}
