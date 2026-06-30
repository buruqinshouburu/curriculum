package com.doinner.csys.domain;

import com.doinner.common.core.annotation.Excel;
import com.doinner.common.core.domain.db.AbstractDoinnerLogicalDelBaseEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * 教学大纲模板对象 t_csys_teaching_programme_template
 *
 * @author wzg
 * @date 2026-02-27
 */
public class TeachingProgrammeTemplate extends AbstractDoinnerLogicalDelBaseEntity{

    private static final long serialVersionUID=1L;

    /** id */
    private Long id;

    // 模板实例id
    private Long outlineId;

    /** 模板名称 */
            @Excel(name = "模板名称")
    private String attributeName;


    /** 父id */
            @Excel(name = "父id")
    private Long parentId;

    /** 层级 */
            @Excel(name = "层级")
    private Long level;

    /** 是否叶子节点 */
            @Excel(name = "是否叶子节点")
    private Long leaf;

    private Integer sort;

    private List<TeachingProgrammeTemplate> children = new ArrayList<>();

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public Long getLevel() {
        return level;
    }

    public void setLevel(Long level) {
        this.level = level;
    }

    public Long getLeaf() {
        return leaf;
    }

    public void setLeaf(Long leaf) {
        this.leaf = leaf;
    }

    public Long getOutlineId() {
        return outlineId;
    }

    public void setOutlineId(Long outlineId) {
        this.outlineId = outlineId;
    }

    public String getAttributeName() {
        return attributeName;
    }

    public void setAttributeName(String attributeName) {
        this.attributeName = attributeName;
    }

    public List<TeachingProgrammeTemplate> getChildren() {
        return children;
    }

    public void setChildren(List<TeachingProgrammeTemplate> children) {
        this.children = children;
    }

    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }
}
