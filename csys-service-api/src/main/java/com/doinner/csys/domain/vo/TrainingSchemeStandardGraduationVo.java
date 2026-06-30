package com.doinner.csys.domain.vo;

import java.util.List;

/** 毕业标准对象 */
public class TrainingSchemeStandardGraduationVo {

    /** 主键 */
    private Long id;

    /** 名称 */
    private String name;

    private List<?> children;

    private Long parentId;

    /** 培养目标 */
    private List<TrainingSchemeStandardCultivationTargetVo> trainingSchemeStandardCultivationTargetVos;

    /** 培养标准 */
    private List<TrainingSchemeStandardCultivationVo> trainingSchemeStandardCultivationVos;

    private List<TrainingSchemeCourseVo> courseVos;

    private boolean isRef;

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

    public List<?> getChildren() {
        return children;
    }

    public void setChildren(List<?> children) {
        this.children = children;
    }

    public List<TrainingSchemeStandardCultivationTargetVo> getTrainingSchemeStandardCultivationTargetVos() {
        return trainingSchemeStandardCultivationTargetVos;
    }

    public void setTrainingSchemeStandardCultivationTargetVos(List<TrainingSchemeStandardCultivationTargetVo> trainingSchemeStandardCultivationTargetVos) {
        this.trainingSchemeStandardCultivationTargetVos = trainingSchemeStandardCultivationTargetVos;
    }

    public boolean isRef() {
        return !(children == null || children.size() == 0);
    }

    public void setRef(boolean ref) {
        isRef = ref;
    }

    public boolean isGraduation() {
        return true;
    }

    public List<TrainingSchemeStandardCultivationVo> getTrainingSchemeStandardCultivationVos() {
        return trainingSchemeStandardCultivationVos;
    }

    public void setTrainingSchemeStandardCultivationVos(List<TrainingSchemeStandardCultivationVo> trainingSchemeStandardCultivationVos) {
        this.trainingSchemeStandardCultivationVos = trainingSchemeStandardCultivationVos;
    }

    public List<TrainingSchemeCourseVo> getCourseVos() {
        return courseVos;
    }

    public void setCourseVos(List<TrainingSchemeCourseVo> courseVos) {
        this.courseVos = courseVos;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }
}
