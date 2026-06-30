package com.doinner.csys.domain;

import com.doinner.common.core.annotation.Excel;
import com.doinner.common.core.domain.db.AbstractDoinnerLogicalDelBaseEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * 能力素质对象 t_csys_std_ability
 *
 * @author doinner
 * @date 2023-03-14
 */
public class StandardAbility extends AbstractDoinnerLogicalDelBaseEntity {

    private static final long serialVersionUID = -5113704936985397832L;

    /**
     * 名称
     */
    @Excel(name = "名称")
    private String name;

    /**
     * 分类  1 能力模板 2 能力图谱  3.素质模板 4.素质图谱
     */
    @Excel(name = "分类")
    private Integer type;

    /**
     * 编码
     */
    @Excel(name = "编码")
    private String code;

    /**
     * 创建部门
     */
    @Excel(name = "创建部门")
    private Long collegeId;

    /**
     * 专业
     */
    @Excel(name = "专业")
    private Long majorId;
    private String majorName;

    /**
     * 细分专业
     */
    @Excel(name = "细分专业")
    private Long subMajorId;
    private String subMajorName;

    private Long categoryId;
    private String categoryName;

    /**
     * 技术指挥分类 0：未分类、1：技术类、2：指挥类
     */
    @Excel(name = "技术指挥分类  0：未分类、1：技术类、2：指挥类")
    private Long classId;

    private Long abilitySystemId;

    private Long oldId;

    // 版本
    private String version;

    private List<StandardAbilityLevel> levels;

    protected Long parentId;
    protected String parentName;
    protected String url;
    protected Integer level;
    protected Integer leaf;
    protected Integer order;
    protected List<StandardAbility> children = new ArrayList<>();

    // 毕业要求
    private List<Long> graduationIds;

    private Long sourceId;

    public List<Long> getGraduationIds() {
        return graduationIds;
    }

    public void setGraduationIds(List<Long> graduationIds) {
        this.graduationIds = graduationIds;
    }

    public Long getOldId() {
        return oldId;
    }

    public void setOldId(Long oldId) {
        this.oldId = oldId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getType() {
        return type;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
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

    public Long getSubMajorId() {
        return subMajorId;
    }

    public void setSubMajorId(Long subMajorId) {
        this.subMajorId = subMajorId;
    }

    public Long getClassId() {
        return classId;
    }

    public void setClassId(Long classId) {
        this.classId = classId;
    }

    public Long getAbilitySystemId() {
        return abilitySystemId;
    }

    public void setAbilitySystemId(Long abilitySystemId) {
        this.abilitySystemId = abilitySystemId;
    }

    public List<StandardAbilityLevel> getLevels() {
        return levels;
    }

    public void setLevels(List<StandardAbilityLevel> levels) {
        this.levels = levels;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
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

    public List<StandardAbility> getChildren() {
        return children;
    }

    public void setChildren(List<StandardAbility> children) {
        this.children = children;
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

    public Long getSourceId() {
        return sourceId;
    }

    public void setSourceId(Long sourceId) {
        this.sourceId = sourceId;
    }
}
