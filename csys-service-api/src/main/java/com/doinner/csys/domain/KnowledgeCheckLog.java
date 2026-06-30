package com.doinner.csys.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.doinner.common.core.annotation.Excel;

/**
 * 知识点查重意见对象 t_csys_knowledge_check_log
 *
 * @author doinner
 * @date 2023-03-23
 */
public class KnowledgeCheckLog  {
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    private String id;

    /**
     * 一个课程的ID
     */
    //@Excel(name = "一个知识领域的ID")
    private Long sourceCourseId;

    /**
     * 一个课程的名称
     */
    @Excel(name = "一个课程的名称")
    private String sourceCourseName;

    /**
     * 一个课程的知识单元ID
     */
    //@Excel(name = "一个知识领域的知识单元ID")
    private Long sourceUnitId;

    /**
     * 一个课程的知识单元ID
     */
    @Excel(name = "一个课程的知识单元名称")
    private String sourceUnitName;

    /**
     * 一个课程的知识点ID
     */
    //@Excel(name = "一个知识领域的知识点ID")
    private Long sourcePointId;

    /**
     * 一个课程的知识点名称
     */
    @Excel(name = "一个课程的知识点名称")
    private String sourcePointName;

    /**
     * 另外一个课程的ID
     */
    //@Excel(name = "另外一个知识领域的ID")
    private Long targetCourseId;

    /**
     * 另外一个课程的名称
     */
    @Excel(name = "另外一个课程的名称")
    private String targetCourseName;

    /**
     * 另外一个课程的知识单元ID
     */
   // @Excel(name = "另外一个知识领域的知识单元ID")
    private Long targetUnitId;

    /**
     * 另外一个课程的知识单元名称
     */
    @Excel(name = "另外一个课程的知识单元名称")
    private String targetUnitName;

    /**
     * 另外一个课程的知识点ID
     */
   // @Excel(name = "另外一个知识领域的知识点ID")
    private Long targetPointId;

    /**
     * 另外一个课程的知识点名称
     */
    @Excel(name = "另外一个课程的知识点名称")
    private String targetPointName;

    @Excel(name="审定意见")
    private String remark;

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    /**
     * 是否相似
     */
    @Excel(name = "是否确认相似",readConverterExp ="0=未确认,1=确认")
    private Integer start;

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return this.id;

    }



    public void setSourceUnitId(Long sourceUnitId) {
        this.sourceUnitId = sourceUnitId;
    }

    public Long getSourceUnitId() {
        return sourceUnitId;
    }

    public void setSourceUnitName(String sourceUnitName) {
        this.sourceUnitName = sourceUnitName;
    }

    public String getSourceUnitName() {
        return sourceUnitName;
    }

    public void setSourcePointId(Long sourcePointId) {
        this.sourcePointId = sourcePointId;
    }

    public Long getSourcePointId() {
        return sourcePointId;
    }

    public void setSourcePointName(String sourcePointName) {
        this.sourcePointName = sourcePointName;
    }

    public String getSourcePointName() {
        return sourcePointName;
    }


    public void setTargetUnitId(Long targetUnitId) {
        this.targetUnitId = targetUnitId;
    }

    public Long getTargetUnitId() {
        return targetUnitId;
    }

    public void setTargetUnitName(String targetUnitName) {
        this.targetUnitName = targetUnitName;
    }

    public String getTargetUnitName() {
        return targetUnitName;
    }

    public void setTargetPointId(Long targetPointId) {
        this.targetPointId = targetPointId;
    }

    public Long getTargetPointId() {
        return targetPointId;
    }

    public void setTargetPointName(String targetPointName) {
        this.targetPointName = targetPointName;
    }

    public String getTargetPointName() {
        return targetPointName;
    }

    public void setStart(Integer start) {
        this.start = start;
    }

    public Integer getStart() {
        return start;
    }

    public Long getSourceCourseId() {
        return sourceCourseId;
    }

    public void setSourceCourseId(Long sourceCourseId) {
        this.sourceCourseId = sourceCourseId;
    }

    public String getSourceCourseName() {
        return sourceCourseName;
    }

    public void setSourceCourseName(String sourceCourseName) {
        this.sourceCourseName = sourceCourseName;
    }

    public Long getTargetCourseId() {
        return targetCourseId;
    }

    public void setTargetCourseId(Long targetCourseId) {
        this.targetCourseId = targetCourseId;
    }

    public String getTargetCourseName() {
        return targetCourseName;
    }

    public void setTargetCourseName(String targetCourseName) {
        this.targetCourseName = targetCourseName;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("id", getId())
                .append("sourceDomainId", getSourceCourseId())
                .append("sourceDomainName", getSourceCourseName())
                .append("sourceUnitId", getSourceUnitId())
                .append("sourceUnitName", getSourceUnitName())
                .append("sourcePointId", getSourcePointId())
                .append("sourcePointName", getSourcePointName())
                .append("targetDomainId", getTargetCourseId())
                .append("targetDomainName", getTargetCourseName())
                .append("targetUnitId", getTargetUnitId())
                .append("targetUnitName", getTargetUnitName())
                .append("targetPointId", getTargetPointId())
                .append("targetPointName", getTargetPointName())
                .append("start", getStart())
                .append("remark", getRemark())
                .toString();
    }
}
