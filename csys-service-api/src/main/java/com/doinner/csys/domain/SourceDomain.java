package com.doinner.csys.domain;

import com.doinner.common.core.annotation.Excel;
import com.doinner.common.core.domain.db.AbstractDoinnerLogicalDelBaseEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * 源知识领域对象 t_csys_source_domain
 *
 * @author wzg
 * @date 2026-02-26
 */
public class SourceDomain extends AbstractDoinnerLogicalDelBaseEntity {

    private static final long serialVersionUID=1L;

    /** 主键id */
    private Long id;

    /** 名称 */
            @Excel(name = "名称")
    private String name;

    /** 学院id */
            @Excel(name = "学院id")
    private Long collegeId;
    private String collegeName;

    /** 专业id */
            @Excel(name = "专业id")
    private Long majorId;
    private String majorName;

    // 知识点数量
    private Integer pointCount;
    private Integer unitCount;

    private List<SourceUnit> sourceUnits;

    private String version;

    // 审核状态：0未审核1审核中2审核通过3审核失败
    private Integer status;

    private Long categoryId;
    private String categoryName;
    private Integer type;
    private Long sourceId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getCollegeId() {
        return collegeId;
    }

    public void setCollegeId(Long collegeId) {
        this.collegeId = collegeId;
    }

    public Long getMajorId() {
        return majorId;
    }

    public void setMajorId(Long majorId) {
        this.majorId = majorId;
    }

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

    public List<SourceUnit> getSourceUnits() {
        return sourceUnits;
    }

    public void setSourceUnits(List<SourceUnit> sourceUnits) {
        this.sourceUnits = sourceUnits;
    }

    public Integer getPointCount() {
        return pointCount;
    }

    public void setPointCount(Integer pointCount) {
        this.pointCount = pointCount;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public Integer getUnitCount() {
        return unitCount;
    }

    public void setUnitCount(Integer unitCount) {
        this.unitCount = unitCount;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Long getSourceId() {
        return sourceId;
    }

    public void setSourceId(Long sourceId) {
        this.sourceId = sourceId;
    }

    public SourceDomain(String name) {
        this.name = name;
        this.sourceUnits = new ArrayList<SourceUnit>();
    }

    public SourceDomain() {
    }


}
