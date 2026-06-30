package com.doinner.csys.domain.vo;

import java.util.List;

public class CollegeSchemeVo {

    private Long id;

    private String name;

    private List<TrainingSchemeVo> trainingSchemeVos;

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

    public List<TrainingSchemeVo> getTrainingSchemeVos() {
        return trainingSchemeVos;
    }

    public void setTrainingSchemeVos(List<TrainingSchemeVo> trainingSchemeVos) {
        this.trainingSchemeVos = trainingSchemeVos;
    }
}
