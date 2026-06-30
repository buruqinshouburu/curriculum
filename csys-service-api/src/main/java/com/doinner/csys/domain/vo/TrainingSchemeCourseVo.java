package com.doinner.csys.domain.vo;

import com.doinner.csys.domain.StandardGraduation;

import java.util.List;

public class TrainingSchemeCourseVo {

    /**主键(id)*/
    private Long id;

    /**课程名(name)*/
    private String name;

    /**
     * 审核状态：0未审核1已审核
     */
    private Integer status;

    /** 课程总课时 */
    private Double hours;

    /** 课程理论课时 */
    private Double theoryHours;
    /**
     * 讲授学时
     */
    private Double teachHours;

    /** 课程实践课时 */
    private Double practiceHours;

    /**
     * 文件id(file_id)
     */
    private String fileId;

    /**
     * 文件名称(file_name)
     */
    private String fileName;
    /**
     * 课程类型:1 必修 2：选修 3：任选
     * */
    private String courseAttr;
    /**
     * 课程种类：1：课程 2：训练科目 4：实践项目
     */
    private String type;

    private String courseModel;

    private String courseModelChildren;
    /**
     * 考核方式
     */
    private String exaMethod;


    private List<StandardGraduationVo> graduationVoList;

    private List<CourseKnowledgeUnitVo> courseKnowledgeUnitVoList;

    private Double credit;

    private Integer academicTermsNumber;

    private String semesterSchedule;


    private List<?> children;

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

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Double getHours() {
        return hours;
    }

    public void setHours(Double hours) {
        this.hours = hours;
    }

    public Double getTheoryHours() {
        return theoryHours;
    }

    public void setTheoryHours(Double theoryHours) {
        this.theoryHours = theoryHours;
    }

    public Double getPracticeHours() {
        return practiceHours;
    }

    public void setPracticeHours(Double practiceHours) {
        this.practiceHours = practiceHours;
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

    public String getCourseAttr() {
        return courseAttr;
    }

    public void setCourseAttr(String courseAttr) {
        this.courseAttr = courseAttr;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<StandardGraduationVo> getGraduationVoList() {
        return graduationVoList;
    }

    public void setGraduationVoList(List<StandardGraduationVo> graduationVoList) {
        this.graduationVoList = graduationVoList;
    }

    public boolean isCourse() {
        return true;
    }

    public String getCourseModel() {
        return courseModel;
    }

    public void setCourseModel(String courseModel) {
        this.courseModel = courseModel;
    }

    public String getCourseModelChildren() {
        return courseModelChildren;
    }

    public void setCourseModelChildren(String courseModelChildren) {
        this.courseModelChildren = courseModelChildren;
    }

    public String getExaMethod() {
        return exaMethod;
    }

    public void setExaMethod(String exaMethod) {
        this.exaMethod = exaMethod;
    }

    public Double getTeachHours() {
        return teachHours;
    }

    public void setTeachHours(Double teachHours) {
        this.teachHours = teachHours;
    }

    public List<?> getChildren() {
        return children;
    }

    public void setChildren(List<?> children) {
        this.children = children;
    }

    public Double getCredit() {
        return credit;
    }

    public void setCredit(Double credit) {
        this.credit = credit;
    }

    public List<CourseKnowledgeUnitVo> getCourseKnowledgeUnitVoList() {
        return courseKnowledgeUnitVoList;
    }

    public void setCourseKnowledgeUnitVoList(List<CourseKnowledgeUnitVo> courseKnowledgeUnitVoList) {
        this.courseKnowledgeUnitVoList = courseKnowledgeUnitVoList;
    }

    public Integer getAcademicTermsNumber() {
        return academicTermsNumber;
    }

    public void setAcademicTermsNumber(Integer academicTermsNumber) {
        this.academicTermsNumber = academicTermsNumber;
    }

    public String getSemesterSchedule() {
        return semesterSchedule;
    }

    public void setSemesterSchedule(String semesterSchedule) {
        this.semesterSchedule = semesterSchedule;
    }
}
