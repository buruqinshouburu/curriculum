package com.doinner.csys.domain.vo;

/**
 * 教学计划专业查询返回。
 *
 * 根据教学计划id与课程id查询专业信息(id、名称、状态)。
 * 具体查询逻辑由 service 后续实现，此处仅承载返回字段。
 */
public class TeachingPlanMajorVo {

    /** 专业id */
    private Long id;

    /** 专业名称 */
    private String name;

    /** 状态 */
    private Integer status;

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

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
