package com.doinner.csys.domain;

import com.doinner.common.core.domain.db.AbstractDoinnerLogicalDelBaseEntity;
import com.doinner.common.core.annotation.Excel;

/**
 * 知识领域表对象 t_csys_knowledge_domain
 *
 * @author doinner
 * @date 2023-03-16
 */
public class KnowledgeDomain extends AbstractDoinnerLogicalDelBaseEntity {


    private static final long serialVersionUID = -7757811871040998676L;

    /**
     * 课程ID
     */
    @Excel(name = "课程ID")
    private Long courseId;

    /**
     * 知识领域名称
     */
    @Excel(name = "知识领域名称")
    private String name;

    /**
     * 知识单元Ids
     */
    @Excel(name = "知识单元Ids")
    private String unitIds;


    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

    public Long getCourseId() {
        return courseId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }


    public void setUnitIds(String unitIds) {
        this.unitIds = unitIds;
    }

    public String getUnitIds() {
        return unitIds;
    }


}
