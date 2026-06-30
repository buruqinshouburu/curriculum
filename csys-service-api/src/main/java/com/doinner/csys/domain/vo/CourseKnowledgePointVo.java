package com.doinner.csys.domain.vo;


/**
 * 知识点DTO（用于查看和保存）
 */
public class CourseKnowledgePointVo {

    /**
     * 知识点ID（null表示新增）
     */
    private Long id;

    /**
     * 知识点名称
     */
    private String name;

    /**
     * 排序序号
     */
    private Integer sort;

    /**
     * 知识单元ID
     */
    private Long unitId;

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

    public Long getUnitId() {
        return unitId;
    }

    public void setUnitId(Long unitId) {
        this.unitId = unitId;
    }
}
