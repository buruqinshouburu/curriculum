package com.doinner.csys.domain.vo;

import java.util.List;

/** 培养目标 */
public class TrainingSchemeStandardCultivationTargetVo {

    /** 主键 */
    private String id;

    /** 真正的主键 */
    private Long sourceId;

    private String parentId;

    /** 名称 */
    private String name;

    private Integer type = 0;
    /** 毕业标准对象 */
    private List<TrainingSchemeStandardGraduationVo> trainingSchemeStandardGraduationVos;

    public TrainingSchemeStandardCultivationTargetVo() {
    }

    public TrainingSchemeStandardCultivationTargetVo(Object o) {
        if (o instanceof TrainingSchemeStandardGraduationVo) {
            TrainingSchemeStandardGraduationVo trainingSchemeStandardGraduationVo = (TrainingSchemeStandardGraduationVo) o;
//            this.id = DomainFieldConstant.STANDARD_GRADUATION + trainingSchemeStandardGraduationVo.getId();
            this.sourceId = trainingSchemeStandardGraduationVo.getId();
            this.name = trainingSchemeStandardGraduationVo.getName();
            this.type = 4;
        }
        if (o instanceof TrainingSchemeStandardCultivationVo){
            TrainingSchemeStandardCultivationVo trainingSchemeStandardCultivationVo = (TrainingSchemeStandardCultivationVo) o;
//            this.id = DomainFieldConstant.STANDARD_CULTIVATION + trainingSchemeStandardCultivationVo.getId().toString();
            this.sourceId = trainingSchemeStandardCultivationVo.getId();
            this.name = trainingSchemeStandardCultivationVo.getName();
            this.type = 3;
        }
        if (o instanceof TrainingSchemeKnowledgeUnitVo){
            TrainingSchemeKnowledgeUnitVo trainingSchemeKnowledgeUnitVo = (TrainingSchemeKnowledgeUnitVo) o;
//            this.id = DomainFieldConstant.EXCEL_TITLE_UNIT + trainingSchemeKnowledgeUnitVo.getId().toString();
            this.sourceId = trainingSchemeKnowledgeUnitVo.getId();
            this.name = trainingSchemeKnowledgeUnitVo.getName();
            this.type = 2;
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<TrainingSchemeStandardGraduationVo> getTrainingSchemeStandardGraduationVos() {
        return trainingSchemeStandardGraduationVos;
    }

    public void setTrainingSchemeStandardGraduationVos(List<TrainingSchemeStandardGraduationVo> trainingSchemeStandardGraduationVos) {
        this.trainingSchemeStandardGraduationVos = trainingSchemeStandardGraduationVos;
    }

    public Long getSourceId() {
        return sourceId;
    }

    public void setSourceId(Long sourceId) {
        this.sourceId = sourceId;
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

    public boolean isGraduation() {
        return this.type == 4;
    }

    public boolean isCultivationTarget() {
        return this.type == 5;
    }

}
