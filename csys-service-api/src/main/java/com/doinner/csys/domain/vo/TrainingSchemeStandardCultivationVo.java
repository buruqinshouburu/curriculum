package com.doinner.csys.domain.vo;

import java.util.ArrayList;
import java.util.List;

/** 培养标准 */
public class TrainingSchemeStandardCultivationVo {

    /** 主键 */
    private Long id;

    /** 父级id */
    private Long parentId;

    /** 真正的主键 */
    private Long sourceId;

    /** 名称 */
    private String name;

    /** 培养目标id */
    private Long targetId;

    /** 毕业标准 */
    private List<TrainingSchemeStandardGraduationVo> children;

    /** 知识单元 */
    private List<TrainingSchemeKnowledgeUnitVo> trainingSchemeKnowledgeUnitVos;

    private Integer type = 3;

    public TrainingSchemeStandardCultivationVo() {
    }

    public TrainingSchemeStandardCultivationVo(TrainingSchemeKnowledgeUnitVo trainingSchemeKnowledgeUnitVo) {
        this.id = trainingSchemeKnowledgeUnitVo.getId();
        this.sourceId = trainingSchemeKnowledgeUnitVo.getId();
        this.name = trainingSchemeKnowledgeUnitVo.getName();
        this.children = new ArrayList<>();
        this.type = 2;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSourceId() {
        return sourceId;
    }

    public void setSourceId(Long sourceId) {
        this.sourceId = sourceId;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getTargetId() {
        return targetId;
    }

    public void setTargetId(Long targetId) {
        this.targetId = targetId;
    }

    public List<TrainingSchemeStandardGraduationVo> getChildren() {
        return children;
    }

    public void setChildren(List<TrainingSchemeStandardGraduationVo> children) {
        this.children = children;
    }

    public List<TrainingSchemeKnowledgeUnitVo> getTrainingSchemeKnowledgeUnitVos() {
        return trainingSchemeKnowledgeUnitVos;
    }

    public void setTrainingSchemeKnowledgeUnitVos(List<TrainingSchemeKnowledgeUnitVo> trainingSchemeKnowledgeUnitVos) {
        this.trainingSchemeKnowledgeUnitVos = trainingSchemeKnowledgeUnitVos;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public boolean isCultivation() {
        return this.type == 3;
    }

    public boolean isUnit() {
        return this.type == 2;
    }
}
