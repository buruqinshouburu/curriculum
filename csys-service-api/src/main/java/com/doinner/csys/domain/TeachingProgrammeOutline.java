package com.doinner.csys.domain;

import com.doinner.common.core.annotation.Excel;
import com.doinner.common.core.domain.db.AbstractDoinnerLogicalDelBaseEntity;

import java.util.List;

/**
 * 教学大纲模板实例对象 t_csys_teaching_programme_outline
 *
 * @author wzg
 * @date 2026-02-28
 */
public class TeachingProgrammeOutline extends AbstractDoinnerLogicalDelBaseEntity{

    private static final long serialVersionUID=1L;

    /** $column.columnComment */
    private Long id;

    /** 大纲名称模板 */
            @Excel(name = "大纲名称模板")
    private String name;

    // 大纲属性
    private List<TeachingProgrammeTemplate> attributeInstances;

    private String startTime;

    private String endTime;


    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<TeachingProgrammeTemplate> getAttributeInstances() {
        return attributeInstances;
    }

    public void setAttributeInstances(List<TeachingProgrammeTemplate> attributeInstances) {
        this.attributeInstances = attributeInstances;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }
}
