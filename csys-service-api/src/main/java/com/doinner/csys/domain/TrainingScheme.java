package com.doinner.csys.domain;

import com.doinner.common.core.domain.db.AbstractDoinnerLogicalDelBaseEntity;
import com.doinner.csys.domain.vo.TrainingSchemeVo;
import com.doinner.common.core.annotation.Excel;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 培养方案对象 t_csys_training_scheme
 * 
 * @author doinner
 * @date 2023-03-14
 */
public class TrainingScheme extends AbstractDoinnerLogicalDelBaseEntity
{
    private static final long serialVersionUID = 1710481373485289109L;

    /** 排序字段: 前端属性名 -> 数据库列名(列名与 selectTrainingSchemeVoList 中别名一致) */
    private static Map<String, String> propMapping = new HashMap<String, String>();
    static {
        propMapping.put("name", "ts.name");                 // 培养方案名称
        propMapping.put("categoryName", "ts.category_id");  // 学科门类(按 id 排序)
        propMapping.put("categoryId", "ts.category_id");  // 学科门类(按 id 排序)
        propMapping.put("majorName", "ts.major_id");        // 专业类(按 id 排序)
        propMapping.put("majorId", "ts.major_id");        // 专业类(按 id 排序)
        propMapping.put("collegeName", "ts.college_id");    // 所属学院(按 id 排序)
        propMapping.put("collegeId", "ts.college_id");    // 所属学院(按 id 排序)
        propMapping.put("subMajorName", "ts.major_id");     // 适用专业(按 major_id 排序)
        propMapping.put("subMajorId", "ts.major_id");     // 适用专业(按 major_id 排序)
        propMapping.put("educationLevel", "ts.education_level"); // 适用对象
    }

    /** 排序字段(前端属性名) */
    private String prop;
    /** 排序字段对应数据库列名 */
    private String database_prop;
    /** 排序方式 */
    private String order;


    /** 培养规划案名称 */
    @Excel(name = "培养规划名称")
    private String name;

    /** 教学计划名称 */
    private String planName;

    /** 培养方案名称 */
    @Excel(name = "培养方案名称")
    private String programName;

    /**培养标准(standard_id)*/
    private Long standardId;

    /** 门类id */
    @Excel(name = "门类id")
    private Long categoryId;

    /** 门类url */
    private String categoryUrl;

    /** 学院id */
    @Excel(name = "学院id")
    private Long collegeId;

    /** 文件id */
    private String fileId;

    /** 文件名称 */
    private String fileName;

    /** 年度 */
    private Integer year;

    /**
     * 专业(major)
     */
    @Excel(name = "专业(major)")
    private Long majorId;

    /**
     * 细分专业(sub_major)
     */
    @Excel(name = "细分专业(sub_major)")
    private Long subMajorId;

    /**
     * 技术指挥分类(class)    0：未分类、1：技术类、2：指挥类
     */
    @Excel(name = "技术指挥分类(class)    0：未分类、1：技术类、2：指挥类")
    private String classId;


    // 培养规划版本
    private String version;

    // 培养方案来源版本
    private Long historyVersionId;
    private String historyVersion;

    //培养对象（cur_object_type）objectType
    private String objectType;

    //培养对象学历(cur_object_education)education
    private String education;

    //培养对象学制类型(cur_academic_type)academicType
    private String academicType;

    //培养对象学制年限（cur_duration_type）durationType
    private String durationType;

    //毕业授予学士学位类型（cur_object_degree）degree
    private String degree;

    // 教学大纲id,t_csys_teaching_programme_instance
    private Long instanceId;
    private String instanceName;
    private String instanceVersion;

    private String educationLevel;


    // 详细规划
    private List<SchemeDetail> details;


    public TrainingScheme() {
    }

    public TrainingScheme(TrainingSchemeVo trainingSchemeVo) {
        super();
        this.id = trainingSchemeVo.getId();
        this.name = trainingSchemeVo.getName();
        this.planName = trainingSchemeVo.getPlanName();
        this.programName = trainingSchemeVo.getProgramName();
        this.standardId = trainingSchemeVo.getStandardId();
        this.categoryId = trainingSchemeVo.getCategoryId();
        this.collegeId = trainingSchemeVo.getCollegeId();
        this.fileId = trainingSchemeVo.getFileId();
        this.fileName = trainingSchemeVo.getFileName();
        this.year = trainingSchemeVo.getYear();
        this.majorId = trainingSchemeVo.getMajorId();
        this.classId = trainingSchemeVo.getClassId();
        this.sysflag = trainingSchemeVo.getSysflag();

        this.version = trainingSchemeVo.getVersion();
        this.historyVersion = trainingSchemeVo.getHistoryVersion();
        this.historyVersionId = trainingSchemeVo.getHistoryVersionId();
        this.objectType = trainingSchemeVo.getObjectType();
        this.education = trainingSchemeVo.getEducation();
        this.academicType = trainingSchemeVo.getAcademicType();
        this.durationType = trainingSchemeVo.getDurationType();
        this.degree = trainingSchemeVo.getDegree();
        this.details = trainingSchemeVo.getDetails();
        this.instanceId = trainingSchemeVo.getInstanceId();
        this.instanceName = trainingSchemeVo.getInstanceName();
        this.instanceVersion = trainingSchemeVo.getInstanceVersion();
        this.educationLevel=trainingSchemeVo.getEducationLevel();
    }

    public String getEducationLevel() {
        return educationLevel;
    }

    public void setEducationLevel(String educationLevel) {
        this.educationLevel = educationLevel;
    }

    public String getHistoryVersion() {
        return historyVersion;
    }

    public void setHistoryVersion(String historyVersion) {
        this.historyVersion = historyVersion;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }

    public void setName(String name) 
    {
        this.name = name;
    }

    public String getName() 
    {
        return name;
    }

    public Long getStandardId() {
        return standardId;
    }

    public void setStandardId(Long standardId) {
        this.standardId = standardId;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public Long getCollegeId() {
        return collegeId;
    }

    public void setCollegeId(Long collegeId) {
        this.collegeId = collegeId;
    }

    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
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

    public String getClassId() {
        return classId;
    }

    public void setClassId(String classId) {
        this.classId = classId;
    }

    public String getPlanName() {
        return planName;
    }

    public void setPlanName(String planName) {
        this.planName = planName;
        this.programName=planName;
    }

    public String getProgramName() {
        return programName;
    }

    public void setProgramName(String programName) {
        this.programName = programName;
    }

    public String getCategoryUrl() {
        return categoryUrl;
    }

    public void setCategoryUrl(String categoryUrl) {
        this.categoryUrl = categoryUrl;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getObjectType() {
        return objectType;
    }

    public void setObjectType(String objectType) {
        this.objectType = objectType;
    }

    public String getEducation() {
        return education;
    }

    public void setEducation(String education) {
        this.education = education;
    }

    public String getAcademicType() {
        return academicType;
    }

    public void setAcademicType(String academicType) {
        this.academicType = academicType;
    }

    public String getDurationType() {
        return durationType;
    }

    public void setDurationType(String durationType) {
        this.durationType = durationType;
    }

    public String getDegree() {
        return degree;
    }

    public void setDegree(String degree) {
        this.degree = degree;
    }

    public List<SchemeDetail> getDetails() {
        return details;
    }

    public void setDetails(List<SchemeDetail> details) {
        this.details = details;
    }

    public Long getInstanceId() {
        return instanceId;
    }

    public void setInstanceId(Long instanceId) {
        this.instanceId = instanceId;
    }

    public String getInstanceName() {
        return instanceName;
    }

    public void setInstanceName(String instanceName) {
        this.instanceName = instanceName;
    }

    public String getInstanceVersion() {
        return instanceVersion;
    }

    public void setInstanceVersion(String instanceVersion) {
        this.instanceVersion = instanceVersion;
    }

    public Long getHistoryVersionId() {
        return historyVersionId;
    }

    public void setHistoryVersionId(Long historyVersionId) {
        this.historyVersionId = historyVersionId;
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
