package com.doinner.csys.entity.csys.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.doinner.common.core.domain.db.AbstractDoinnerLogicalDelBaseEntity;

import java.time.LocalDateTime;

/**
 * 课程知识点实体
 * 知识单元下有若干个知识点
 */
@TableName("t_csys_course_knowledge_point")
public class CourseKnowledgePoint  extends AbstractDoinnerLogicalDelBaseEntity {

    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
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