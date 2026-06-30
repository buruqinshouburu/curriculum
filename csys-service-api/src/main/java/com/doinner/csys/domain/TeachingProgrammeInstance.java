package com.doinner.csys.domain;

import com.doinner.common.core.annotation.Excel;
import com.doinner.common.core.domain.db.AbstractDoinnerLogicalDelBaseEntity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 教学大纲实例对象 t_csys_teaching_programme_instance
 *
 * @author wzg
 * @date 2026-02-27
 */
public class TeachingProgrammeInstance extends AbstractDoinnerLogicalDelBaseEntity{

    private static final long serialVersionUID=1L;

    /** id */
    private Long id;

    /** 名称 */
            @Excel(name = "名称")
    private String name;

    /** 模板id,顶层的 */
            @Excel(name = "模板id,顶层的")
    private Long outlineId;
    private String outlineName;


    private Long collegeId;

    private String collegeName;

    private String version;

    private String fileId;

    private String fileName;

    private Integer status;

    private String downloadUrl;

    private String previewUrl;
    private Long categoryId;
    private Long majorId;

    private String categoryName;

    private String majorName;

    private List<TeachingProgrammeAttribute> attributeInstances;

    /** 排序字段: 前端属性名 -> 数据库列名(列名与 selectTeachingProgrammeInstanceList 别名一致) */
    private static Map<String, String> propMapping = new HashMap<String, String>();
    static {
        propMapping.put("categoryName", "instance.category_id"); // 学科门类(按 id 排序)
        propMapping.put("categoryId", "instance.category_id"); // 学科门类(按 id 排序)
        propMapping.put("majorName", "instance.major_id");       // 专业类(按 id 排序)
        propMapping.put("majorId", "instance.major_id");       // 专业类(按 id 排序)
        propMapping.put("collegeName", "instance.college_Id");   // 所属学院(按 id 排序)
        propMapping.put("collegeId", "instance.college_Id");   // 所属学院(按 id 排序)
    }

    /** 排序字段(前端属性名) */
    private String prop;
    /** 排序字段对应数据库列名 */
    private String database_prop;
    /** 排序方式 */
    private String order;

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getOutlineId() {
        return outlineId;
    }

    public void setOutlineId(Long outlineId) {
        this.outlineId = outlineId;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public List<TeachingProgrammeAttribute> getAttributeInstances() {
        return attributeInstances;
    }

    public void setAttributeInstances(List<TeachingProgrammeAttribute> attributeInstances) {
        this.attributeInstances = attributeInstances;
    }

    public String getOutlineName() {
        return outlineName;
    }

    public void setOutlineName(String outlineName) {
        this.outlineName = outlineName;
    }

    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    public String getPreviewUrl() {
        return previewUrl;
    }

    public void setPreviewUrl(String previewUrl) {
        this.previewUrl = previewUrl;
    }

    public Long getCollegeId() {
        return collegeId;
    }

    public void setCollegeId(Long collegeId) {
        this.collegeId = collegeId;
    }

    public String getCollegeName() {
        return collegeName;
    }

    public void setCollegeName(String collegeName) {
        this.collegeName = collegeName;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public Long getMajorId() {
        return majorId;
    }

    public void setMajorId(Long majorId) {
        this.majorId = majorId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getMajorName() {
        return majorName;
    }

    public void setMajorName(String majorName) {
        this.majorName = majorName;
    }

    public String getProp() {
        return prop;
    }

    public void setProp(String prop) {
        this.prop = prop;
        this.database_prop = propMapping.get(prop);
    }

    public String getDatabase_prop() {
        return database_prop;
    }

    public void setDatabase_prop(String database_prop) {
        this.database_prop = database_prop;
    }

    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
    }
}
