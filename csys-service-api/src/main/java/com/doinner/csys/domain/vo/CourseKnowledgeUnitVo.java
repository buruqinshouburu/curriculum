package com.doinner.csys.domain.vo;

import java.util.List;

/**
 * 知识单元DTO（用于查看和保存）
 */

public class CourseKnowledgeUnitVo {

    /**
     * 知识单元ID（null表示新增）
     */
    private Long id;

    /**
     * 知识单元名称
     */
    private String name;

    /**
     * 排序序号
     */
    private Integer sort;


    /**
     * 知识点列表
     */
    private List<CourseKnowledgePointVo> knowledgePoints;

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

    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }

    public List<CourseKnowledgePointVo> getKnowledgePoints() {
        return knowledgePoints;
    }

    public void setKnowledgePoints(List<CourseKnowledgePointVo> knowledgePoints) {
        this.knowledgePoints = knowledgePoints;
    }
}
