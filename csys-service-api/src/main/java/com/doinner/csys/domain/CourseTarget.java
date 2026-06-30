package com.doinner.csys.domain;

import com.doinner.csys.domain.vo.GraduationTreeVo;
import com.doinner.csys.domain.vo.SourceKnowledgeVo;
import com.doinner.csys.domain.vo.StandardTreeVo;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class CourseTarget {

    private Long courseTargetId;

    private String name;

    private Long courseId;

    private Integer sysflag;

    private String creator;

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

    private Long sourceId;

    // 知识体系
    private List<SourceKnowledgeVo> sourceKnowledgeVoList = new ArrayList<>();

    // 毕业要求
    private List<GraduationTreeVo> graduationTreeVoList = new ArrayList<>();

    // 能力树
    private List<StandardTreeVo> abilityVoList = new ArrayList<>();

    // 素质树
    private List<StandardTreeVo> qualityVoList = new ArrayList<>();


    public Long getCourseTargetId() {
        return courseTargetId;
    }

    public void setCourseTargetId(Long courseTargetId) {
        this.courseTargetId = courseTargetId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

    public Integer getSysflag() {
        return sysflag;
    }

    public void setSysflag(Integer sysflag) {
        this.sysflag = sysflag;
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

    public List<SourceKnowledgeVo> getSourceKnowledgeVoList() {
        return sourceKnowledgeVoList;
    }

    public void setSourceKnowledgeVoList(List<SourceKnowledgeVo> sourceKnowledgeVoList) {
        this.sourceKnowledgeVoList = sourceKnowledgeVoList;
    }

    public List<GraduationTreeVo> getGraduationTreeVoList() {
        return graduationTreeVoList;
    }

    public void setGraduationTreeVoList(List<GraduationTreeVo> graduationTreeVoList) {
        this.graduationTreeVoList = graduationTreeVoList;
    }

    public List<StandardTreeVo> getAbilityVoList() {
        return abilityVoList;
    }

    public void setAbilityVoList(List<StandardTreeVo> abilityVoList) {
        this.abilityVoList = abilityVoList;
    }

    public List<StandardTreeVo> getQualityVoList() {
        return qualityVoList;
    }

    public void setQualityVoList(List<StandardTreeVo> qualityVoList) {
        this.qualityVoList = qualityVoList;
    }

    public Long getSourceId() {
        return sourceId;
    }

    public void setSourceId(Long sourceId) {
        this.sourceId = sourceId;
    }
}
