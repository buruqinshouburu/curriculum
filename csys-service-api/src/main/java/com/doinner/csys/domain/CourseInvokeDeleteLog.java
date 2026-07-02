package com.doinner.csys.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.time.LocalDateTime;

/**
 * 调用课程删除日志对象 t_csys_course_invoke_delete_log
 * <p>
 * 用于记录调用课程(template_type=2)的物理删除动作，便于追溯"调用课程被莫名其妙删除"的问题。
 *
 * @author doinner
 */
public class CourseInvokeDeleteLog {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 删除批次ID，同一次删除操作的多条记录相同
     */
    private String deleteBatchId;

    /**
     * 被删除的调用课程ID
     */
    private Long courseId;

    /**
     * 被删除的课程名称
     */
    private String courseName;

    /**
     * 被删除的课程编号
     */
    private String courseCode;

    /**
     * 源课程ID
     */
    private Long sourceId;

    /**
     * 所属培养方案ID
     */
    private Long schemeId;

    /**
     * 模板类型 1-总库课程 2-调用课程
     */
    private Integer templateType;

    /**
     * 专业ID
     */
    private Long majorId;

    /**
     * 门类ID
     */
    private Long categoryId;

    /**
     * 版本
     */
    private String version;

    /**
     * 操作人
     */
    private String operator;

    /**
     * 操作时间
     */
    private LocalDateTime operationTime;

    /**
     * 备注
     */
    private String remark;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDeleteBatchId() {
        return deleteBatchId;
    }

    public void setDeleteBatchId(String deleteBatchId) {
        this.deleteBatchId = deleteBatchId;
    }

    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode;
    }

    public Long getSourceId() {
        return sourceId;
    }

    public void setSourceId(Long sourceId) {
        this.sourceId = sourceId;
    }

    public Long getSchemeId() {
        return schemeId;
    }

    public void setSchemeId(Long schemeId) {
        this.schemeId = schemeId;
    }

    public Integer getTemplateType() {
        return templateType;
    }

    public void setTemplateType(Integer templateType) {
        this.templateType = templateType;
    }

    public Long getMajorId() {
        return majorId;
    }

    public void setMajorId(Long majorId) {
        this.majorId = majorId;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public LocalDateTime getOperationTime() {
        return operationTime;
    }

    public void setOperationTime(LocalDateTime operationTime) {
        this.operationTime = operationTime;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("id", getId())
                .append("deleteBatchId", getDeleteBatchId())
                .append("courseId", getCourseId())
                .append("courseName", getCourseName())
                .append("courseCode", getCourseCode())
                .append("sourceId", getSourceId())
                .append("schemeId", getSchemeId())
                .append("templateType", getTemplateType())
                .append("majorId", getMajorId())
                .append("categoryId", getCategoryId())
                .append("version", getVersion())
                .append("operator", getOperator())
                .append("operationTime", getOperationTime())
                .append("remark", getRemark())
                .toString();
    }
}
