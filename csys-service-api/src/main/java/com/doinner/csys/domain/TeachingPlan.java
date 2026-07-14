package com.doinner.csys.domain;

import com.doinner.common.core.domain.db.AbstractDoinnerLogicalDelBaseEntity;

import java.math.BigDecimal;

/**
 * 课程教学计划主表实体
 * 绑定总库课程，一门总库课程对应一份教学计划
 */
public class TeachingPlan extends AbstractDoinnerLogicalDelBaseEntity {

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 同一教学计划版本根ID
     */
    private Long rootPlanId;

    /**
     * 总库课程ID，关联t_csys_course.id
     */
    private Long sourceCourseId;

    /**
     * 计划类型：1普通课程 2实验课程 3实践训练课目 4实践项目
     */
    private Integer planType;

    /**
     * 教学计划版本，如2026、V1.0
     */
    private String version;

    /**
     * 是否当前版本：1是 0否
     */
    private Integer currentFlag;

    /**
     * 启用时间，如2026年春季学期
     */
    private String enabledTerm;

    /**
     * 状态：0草稿 1审核中 2通过 3退回 9停用
     */
    private Integer status;

    /**
     * 总库课程名称快照
     */
    private String sourceCourseName;

    /**
     * 总库课程编号快照
     */
    private String sourceCourseCode;

    /**
     * 总库课程英文名快照
     */
    private String sourceCourseEnName;

    /**
     * 总库课程总学时快照
     */
    private BigDecimal sourceHours;

    /**
     * 总库课程讲授学时快照
     */
    private BigDecimal sourceTeachHours;

    /**
     * 总库课程实践/实验学时快照
     */
    private BigDecimal sourcePracticeHours;

    /**
     * 总库课程学分快照
     */
    private BigDecimal sourceCredit;

    /**
     * 计分规则
     */
    private String scoreRule;

    /**
     * 生成或上传文件ID
     */
    private String fileId;

    /**
     * 文件名称
     */
    private String fileName;

    /**
     * 下载地址
     */
    private String downloadUrl;

    /**
     * 预览地址
     */
    private String previewUrl;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getRootPlanId() {
        return rootPlanId;
    }

    public void setRootPlanId(Long rootPlanId) {
        this.rootPlanId = rootPlanId;
    }

    public Long getSourceCourseId() {
        return sourceCourseId;
    }

    public void setSourceCourseId(Long sourceCourseId) {
        this.sourceCourseId = sourceCourseId;
    }

    public Integer getPlanType() {
        return planType;
    }

    public void setPlanType(Integer planType) {
        this.planType = planType;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public Integer getCurrentFlag() {
        return currentFlag;
    }

    public void setCurrentFlag(Integer currentFlag) {
        this.currentFlag = currentFlag;
    }

    public String getEnabledTerm() {
        return enabledTerm;
    }

    public void setEnabledTerm(String enabledTerm) {
        this.enabledTerm = enabledTerm;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getSourceCourseName() {
        return sourceCourseName;
    }

    public void setSourceCourseName(String sourceCourseName) {
        this.sourceCourseName = sourceCourseName;
    }

    public String getSourceCourseCode() {
        return sourceCourseCode;
    }

    public void setSourceCourseCode(String sourceCourseCode) {
        this.sourceCourseCode = sourceCourseCode;
    }

    public String getSourceCourseEnName() {
        return sourceCourseEnName;
    }

    public void setSourceCourseEnName(String sourceCourseEnName) {
        this.sourceCourseEnName = sourceCourseEnName;
    }

    public BigDecimal getSourceHours() {
        return sourceHours;
    }

    public void setSourceHours(BigDecimal sourceHours) {
        this.sourceHours = sourceHours;
    }

    public BigDecimal getSourceTeachHours() {
        return sourceTeachHours;
    }

    public void setSourceTeachHours(BigDecimal sourceTeachHours) {
        this.sourceTeachHours = sourceTeachHours;
    }

    public BigDecimal getSourcePracticeHours() {
        return sourcePracticeHours;
    }

    public void setSourcePracticeHours(BigDecimal sourcePracticeHours) {
        this.sourcePracticeHours = sourcePracticeHours;
    }

    public BigDecimal getSourceCredit() {
        return sourceCredit;
    }

    public void setSourceCredit(BigDecimal sourceCredit) {
        this.sourceCredit = sourceCredit;
    }

    public String getScoreRule() {
        return scoreRule;
    }

    public void setScoreRule(String scoreRule) {
        this.scoreRule = scoreRule;
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
}