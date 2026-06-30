package com.doinner.csys.domain;

import com.doinner.common.core.annotation.Excel;
import com.doinner.common.core.domain.db.AbstractDoinnerLogicalDelBaseEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * 教学大纲属性对象 t_csys_teaching_programme_attribute
 *
 * @author wzg
 * @date 2026-02-27
 */
public class TeachingProgrammeAttribute extends AbstractDoinnerLogicalDelBaseEntity{
    private static final long serialVersionUID=1L;

    /** id */
    private Long id;

    /** 教学大纲实例id */
            @Excel(name = "教学大纲实例id")
    private Long instanceId;

    /** 属性名称 */
            @Excel(name = "属性名称")
    private String attributeName;

    /** 属性值 */
            @Excel(name = "属性值")
    private String attributeValue;

    /** 对应t_csys_teaching_programme_template id */
            @Excel(name = "对应t_csys_teaching_programme_template id")
    private Long templateId;

    /** 对应t_csys_teaching_programme_template parentId 父id */
            @Excel(name = "父id")
    private Long templateParentId;
    private Long parentId;
    private Integer sort;

    private List<TeachingProgrammeAttribute> children = new ArrayList<>();

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public Long getInstanceId() {
        return instanceId;
    }

    public void setInstanceId(Long instanceId) {
        this.instanceId = instanceId;
    }

    public String getAttributeName() {
        return attributeName;
    }

    public void setAttributeName(String attributeName) {
        this.attributeName = attributeName;
    }

    public String getAttributeValue() {
        return attributeValue;
    }

    public void setAttributeValue(String attributeValue) {
        this.attributeValue = attributeValue;
    }

    public Long getTemplateId() {
        return templateId;
    }

    public void setTemplateId(Long templateId) {
        this.templateId = templateId;
    }

    public Long getTemplateParentId() {
        return templateParentId;
    }

    public void setTemplateParentId(Long templateParentId) {
        this.templateParentId = templateParentId;
    }

    public List<TeachingProgrammeAttribute> getChildren() {
        return children;
    }

    public void setChildren(List<TeachingProgrammeAttribute> children) {
        this.children = children;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }
}
