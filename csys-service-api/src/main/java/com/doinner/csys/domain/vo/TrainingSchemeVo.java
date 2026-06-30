package com.doinner.csys.domain.vo;

import com.doinner.common.core.domain.db.AbstractLogicalDelBaseEntity;
import com.doinner.common.core.annotation.Excel;
import com.doinner.csys.domain.SchemeDetail;

import java.util.List;

/**
 * 培养方案Vo对象
 * */
public class TrainingSchemeVo extends AbstractLogicalDelBaseEntity {

    private Long id;

    /** 培养规划名称 */
    private String name;

    /** 教学计划名称 */
    private String planName;

    /** 培养方案名称 */
    private String programName;

    /**培养标准(standard_id)*/
    private Long standardId;

    /**培养标准名称 */
    private String standardName;

    /** 门类id */
    private Long categoryId;

    private String categoryName;

    /** 门类url */
    private String categoryUrl;

    /** 学院id */
    private Long collegeId;

    private String collegeName;

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

    private String majorName;

    /**
     * 细分专业(sub_major)
     */
    @Excel(name = "细分专业(sub_major)")
    private List<Long> subMajorIds;

    private List<String> subMajorNames;

    /**
     * 技术指挥分类(class)    0：未分类、1：技术类、2：指挥类
     */
    @Excel(name = "技术指挥分类(class)    0：未分类、1：技术类、2：指挥类")
    private String classId;

    /** 删除标志(sysflag) */
    private Integer sysflag;

    /** 总任务数 */
    private Integer countTask;

    /** 已审核任务数 */
    private Integer countReview;

    private String downloadUrl;
    private String previewUrl;
    private Integer status;

    private String educationLevel;
    private String educationLevelName;
    private Long schedulingStatus;
    private List<TrainingSchemeCourseVo> courseVos;
    private List<TrainingSchemeStandardGraduationVo> graduationVos;

//    /** 公共基础必修课程 */
//    private List<TrainingSchemeCourseVo> publicRequiredCourseVos;
//
//    /** 学科基础必修课程 */
//    private List<TrainingSchemeCourseVo> subjectRequiredCourseVos;
//
//    /** 专业必修课程 */
//    private List<TrainingSchemeCourseVo> specialityRequiredCourseVos;
//
//    /** 公共基础选修课程 */
//    private List<TrainingSchemeCourseVo> publicElectiveCourseVos;
//
//    /** 学科基础、专业选修课程 */
//    private List<TrainingSchemeCourseVo> subjectElectiveCourseVos;

//    private List<TrainingSchemeCourseVo> compulsoryCourseList;          // 必修课程
//    private List<TrainingSchemeCourseVo> electiveCourseList;            // 选修课程
//    private List<TrainingSchemeCourseVo> optionalCourseList;            // 任选课程
//    private List<TrainingSchemeCourseVo> compulsoryTrainingSubjectList; // 必修训练课目
//    private List<TrainingSchemeCourseVo> electiveTrainingSubjectList;   // 选修训练课目
//    private List<TrainingSchemeCourseVo> optionalTrainingSubjectList;   // 任选训练课目
//    private List<TrainingSchemeCourseVo> compulsoryPracticalProjectList; // 必修实践项目
//    private List<TrainingSchemeCourseVo> electivePracticalProjectList;   // 选修实践项目
//    private List<TrainingSchemeCourseVo> optionalPracticalProjectList;   // 任选实践项目


    // 培养规划版本
    private String version;

    // 培养方案来源版本
    private String historyVersion;
    private Long historyVersionId;

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
    private List<?> children;

    private String objectName;

    // 详细规划
    private List<SchemeDetail> details;

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

    public Long getStandardId() {
        return standardId;
    }

    public void setStandardId(Long standardId) {
        this.standardId = standardId;
    }

    public String getStandardName() {
        return standardName;
    }

    public void setStandardName(String standardName) {
        this.standardName = standardName;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryUrl() {
        return categoryUrl;
    }

    public void setCategoryUrl(String categoryUrl) {
        this.categoryUrl = categoryUrl;
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

    public List<Long> getSubMajorIds() {
        return subMajorIds;
    }

    public void setSubMajorIds(List<Long> subMajorIds) {
        this.subMajorIds = subMajorIds;
    }

    public List<String> getSubMajorNames() {
        return subMajorNames;
    }

    public void setSubMajorNames(List<String> subMajorNames) {
        this.subMajorNames = subMajorNames;
    }

    public String getClassId() {
        return classId;
    }

    public void setClassId(String classId) {
        this.classId = classId;
    }

    public Integer getSysflag() {
        return sysflag;
    }

    public void setSysflag(Integer sysflag) {
        this.sysflag = sysflag;
    }

    public Integer getCountTask() {
        return countTask;
    }

    public void setCountTask(Integer countTask) {
        this.countTask = countTask;
    }

    public Integer getCountReview() {
        return countReview;
    }

    public void setCountReview(Integer countReview) {
        this.countReview = countReview;
    }

    public List<TrainingSchemeCourseVo> getCourseVos() {
        return courseVos;
    }

    public void setCourseVos(List<TrainingSchemeCourseVo> courseVos) {
        this.courseVos = courseVos;
    }

    public List<TrainingSchemeStandardGraduationVo> getGraduationVos() {
        return graduationVos;
    }

    public void setGraduationVos(List<TrainingSchemeStandardGraduationVo> graduationVos) {
        this.graduationVos = graduationVos;
    }

    public List<?> getChildren() {
        return children;
    }

    public void setChildren(List<?> children) {
        this.children = children;
    }
    //
//    public List<TrainingSchemeCourseVo> getPublicRequiredCourseVos() {
//        return publicRequiredCourseVos;
//    }
//
//    public void setPublicRequiredCourseVos(List<TrainingSchemeCourseVo> publicRequiredCourseVos) {
//        this.publicRequiredCourseVos = publicRequiredCourseVos;
//    }
//
//    public List<TrainingSchemeCourseVo> getSubjectRequiredCourseVos() {
//        return subjectRequiredCourseVos;
//    }
//
//    public void setSubjectRequiredCourseVos(List<TrainingSchemeCourseVo> subjectRequiredCourseVos) {
//        this.subjectRequiredCourseVos = subjectRequiredCourseVos;
//    }
//
//    public List<TrainingSchemeCourseVo> getSpecialityRequiredCourseVos() {
//        return specialityRequiredCourseVos;
//    }
//
//    public void setSpecialityRequiredCourseVos(List<TrainingSchemeCourseVo> specialityRequiredCourseVos) {
//        this.specialityRequiredCourseVos = specialityRequiredCourseVos;
//    }
//
//    public List<TrainingSchemeCourseVo> getPublicElectiveCourseVos() {
//        return publicElectiveCourseVos;
//    }
//
//    public void setPublicElectiveCourseVos(List<TrainingSchemeCourseVo> publicElectiveCourseVos) {
//        this.publicElectiveCourseVos = publicElectiveCourseVos;
//    }
//
//    public List<TrainingSchemeCourseVo> getSubjectElectiveCourseVos() {
//        return subjectElectiveCourseVos;
//    }
//
//    public void setSubjectElectiveCourseVos(List<TrainingSchemeCourseVo> subjectElectiveCourseVos) {
//        this.subjectElectiveCourseVos = subjectElectiveCourseVos;
//    }


   /* public List<TrainingSchemeCourseVo> getCompulsoryCourseList() {
        return compulsoryCourseList;
    }

    public void setCompulsoryCourseList(List<TrainingSchemeCourseVo> compulsoryCourseList) {
        this.compulsoryCourseList = compulsoryCourseList;
    }

    public List<TrainingSchemeCourseVo> getElectiveCourseList() {
        return electiveCourseList;
    }

    public void setElectiveCourseList(List<TrainingSchemeCourseVo> electiveCourseList) {
        this.electiveCourseList = electiveCourseList;
    }

    public List<TrainingSchemeCourseVo> getOptionalCourseList() {
        return optionalCourseList;
    }

    public void setOptionalCourseList(List<TrainingSchemeCourseVo> optionalCourseList) {
        this.optionalCourseList = optionalCourseList;
    }

    public List<TrainingSchemeCourseVo> getCompulsoryTrainingSubjectList() {
        return compulsoryTrainingSubjectList;
    }

    public void setCompulsoryTrainingSubjectList(List<TrainingSchemeCourseVo> compulsoryTrainingSubjectList) {
        this.compulsoryTrainingSubjectList = compulsoryTrainingSubjectList;
    }

    public List<TrainingSchemeCourseVo> getElectiveTrainingSubjectList() {
        return electiveTrainingSubjectList;
    }

    public void setElectiveTrainingSubjectList(List<TrainingSchemeCourseVo> electiveTrainingSubjectList) {
        this.electiveTrainingSubjectList = electiveTrainingSubjectList;
    }

    public List<TrainingSchemeCourseVo> getOptionalTrainingSubjectList() {
        return optionalTrainingSubjectList;
    }

    public void setOptionalTrainingSubjectList(List<TrainingSchemeCourseVo> optionalTrainingSubjectList) {
        this.optionalTrainingSubjectList = optionalTrainingSubjectList;
    }

    public List<TrainingSchemeCourseVo> getCompulsoryPracticalProjectList() {
        return compulsoryPracticalProjectList;
    }

    public void setCompulsoryPracticalProjectList(List<TrainingSchemeCourseVo> compulsoryPracticalProjectList) {
        this.compulsoryPracticalProjectList = compulsoryPracticalProjectList;
    }

    public List<TrainingSchemeCourseVo> getElectivePracticalProjectList() {
        return electivePracticalProjectList;
    }

    public void setElectivePracticalProjectList(List<TrainingSchemeCourseVo> electivePracticalProjectList) {
        this.electivePracticalProjectList = electivePracticalProjectList;
    }

    public List<TrainingSchemeCourseVo> getOptionalPracticalProjectList() {
        return optionalPracticalProjectList;
    }

    public void setOptionalPracticalProjectList(List<TrainingSchemeCourseVo> optionalPracticalProjectList) {
        this.optionalPracticalProjectList = optionalPracticalProjectList;
    }*/

    public String getPlanName() {
        return planName;
    }

    public void setPlanName(String planName) {
        this.planName = planName;
    }

    public String getProgramName() {
        return programName;
    }

    public void setProgramName(String programName) {
        this.programName = programName;
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

    public String getHistoryVersion() {
        return historyVersion;
    }

    public void setHistoryVersion(String historyVersion) {
        this.historyVersion = historyVersion;
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

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
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

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getObjectName() {
        return objectName;
    }

    public void setObjectName(String objectName) {
        this.objectName = objectName;
    }

    public String getEducationLevel() {
        return educationLevel;
    }

    public void setEducationLevel(String educationLevel) {
        this.educationLevel = educationLevel;
    }

    public Long getSchedulingStatus() {
        return schedulingStatus;
    }

    public void setSchedulingStatus(Long schedulingStatus) {
        this.schedulingStatus = schedulingStatus;
    }

    public String getEducationLevelName() {
        return educationLevelName;
    }

    public void setEducationLevelName(String educationLevelName) {
        this.educationLevelName = educationLevelName;
    }
}
