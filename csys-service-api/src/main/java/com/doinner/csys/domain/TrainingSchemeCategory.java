package com.doinner.csys.domain;

import com.doinner.common.core.domain.db.AbstractDoinnerLogicalDelBaseEntity;
import com.doinner.common.core.annotation.Excel;

/**
 * 培养方案门类对象 t_csys_training_scheme_category
 *
 * @author doinner
 * @date 2023-03-14
 */
public class TrainingSchemeCategory extends AbstractDoinnerLogicalDelBaseEntity {


    private static final long serialVersionUID = 424714752800023978L;

    /**
     * 父级id
     */
    @Excel(name = "父级id")
    private Long parentId;

    /**
     * 名称
     */
    @Excel(name = "名称")
    private String name;

    /**
     * 是否为叶子节点
     */
    private Integer leaf;

    /**
     * 层级
     */
    private Integer level;

    /**
     * 路径
     */
    private String url;


    /**
     * 下级节点数
     */
    private Integer count;

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public Integer getLeaf() {
        return leaf;
    }

    public void setLeaf(Integer leaf) {
        this.leaf = leaf;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }


    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }


}
