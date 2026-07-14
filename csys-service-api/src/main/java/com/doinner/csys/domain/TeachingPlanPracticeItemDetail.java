package com.doinner.csys.domain;

/**
 * 实验/实践项目明细实体
 * 实验/实践项目的目的、原理、内容、结果、教学设计等明细
 */
public class TeachingPlanPracticeItemDetail {

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 实验/实践项目ID
     */
    private Long itemId;

    /**
     * 明细类型：purpose_task、ability_point、principle、content_requirement、
     * result_requirement、teaching_design、complex_problem、main_task、
     * overall_design、outcome_requirement
     */
    private String detailType;

    /**
     * 训练能力点或支撑目标ID
     */
    private Long objectiveId;

    /**
     * 明细内容
     */
    private String content;

    /**
     * 排序
     */
    private Integer sort;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getItemId() {
        return itemId;
    }

    public void setItemId(Long itemId) {
        this.itemId = itemId;
    }

    public String getDetailType() {
        return detailType;
    }

    public void setDetailType(String detailType) {
        this.detailType = detailType;
    }

    public Long getObjectiveId() {
        return objectiveId;
    }

    public void setObjectiveId(Long objectiveId) {
        this.objectiveId = objectiveId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }
}