package com.doinner.csys.domain;

import com.doinner.common.core.domain.db.AbstractLogicalDelTreeBaseEntity;
import com.doinner.common.core.domain.db.AbstractTreeBaseEntity;
import com.doinner.common.core.annotation.Excel;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 毕业标准对象 t_csys_std_graduation
 *
 * @author doinner
 * @date 2023-03-21
 */
public class StandardGraduation {

    private static final long serialVersionUID = 1710481373485289109L;

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
     * 培养目标id
     */
    @Excel(name = "培养目标id")
    private Long cultivationTargetId;

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

    private String version;

    /**
     * 学科门类
     */
    private Long categoryId;

    private String categoryName;

    private List<Long> cultivationTargetIds;

    private List<StandardGraduation> children;

    // 1毕业要求模板，2毕业要求
    private Integer type;

    // 1:知识，2：能力，3：素质
    private String graduationType;

    // 源id
    private Long sourceId;

    private Long id ;

    private Integer sysflag;
    private Long parentId;
    private String parentName;
    private String url;
    private Integer level;
    private Integer leaf;
    private Integer order;
    private String creator;
    private String educationLevel;
    @JsonFormat(
            pattern = "yyyy-MM-dd HH:mm:ss",
            timezone = "GMT+8"
    )
    private LocalDateTime createTime;
    private String lastModifier;
    @JsonFormat(
            pattern = "yyyy-MM-dd HH:mm:ss",
            timezone = "GMT+8"
    )
    private LocalDateTime lastModifiedTime;
    private String remark;

    // 培养方案id
    private Long schemeId;

    private Integer firstLevelIndicatorsNumber = 0;

    private Integer secondLevelIndicatorsNumber = 0;


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

    public Long getCultivationTargetId() {
        return cultivationTargetId;
    }

    public void setCultivationTargetId(Long cultivationTargetId) {
        this.cultivationTargetId = cultivationTargetId;
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

    public List<StandardGraduation> getChildren() {
        return children;
    }

    public void setChildren(List<StandardGraduation> children) {
        this.children = children;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
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

    public List<Long> getCultivationTargetIds() {
        return cultivationTargetIds;
    }

    public void setCultivationTargetIds(List<Long> cultivationTargetIds) {
        this.cultivationTargetIds = cultivationTargetIds;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getGraduationType() {
        return graduationType;
    }

    public void setGraduationType(String graduationType) {
        this.graduationType = graduationType;
    }

    public Long getSourceId() {
        return sourceId;
    }

    public void setSourceId(Long sourceId) {
        this.sourceId = sourceId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getSysflag() {
        return sysflag;
    }

    public void setSysflag(Integer sysflag) {
        this.sysflag = sysflag;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public String getParentName() {
        return parentName;
    }

    public void setParentName(String parentName) {
        this.parentName = parentName;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public Integer getLeaf() {
        return leaf;
    }

    public void setLeaf(Integer leaf) {
        this.leaf = leaf;
    }

    public Integer getOrder() {
        return order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public String getLastModifier() {
        return lastModifier;
    }

    public void setLastModifier(String lastModifier) {
        this.lastModifier = lastModifier;
    }

    public LocalDateTime getLastModifiedTime() {
        return lastModifiedTime;
    }

    public void setLastModifiedTime(LocalDateTime lastModifiedTime) {
        this.lastModifiedTime = lastModifiedTime;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Integer getFirstLevelIndicatorsNumber() {
        return firstLevelIndicatorsNumber;
    }

    public void setFirstLevelIndicatorsNumber(Integer firstLevelIndicatorsNumber) {
        this.firstLevelIndicatorsNumber = firstLevelIndicatorsNumber;
    }

    public Integer getSecondLevelIndicatorsNumber() {
        return secondLevelIndicatorsNumber;
    }

    public void setSecondLevelIndicatorsNumber(Integer secondLevelIndicatorsNumber) {
        this.secondLevelIndicatorsNumber = secondLevelIndicatorsNumber;
    }

    public Long getSchemeId() {
        return schemeId;
    }

    public void setSchemeId(Long schemeId) {
        this.schemeId = schemeId;
    }

    public String getEducationLevel() {
        return educationLevel;
    }

    public void setEducationLevel(String educationLevel) {
        this.educationLevel = educationLevel;
    }
}
