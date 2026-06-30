package com.doinner.csys.domain;

import com.doinner.common.core.domain.db.AbstractDoinnerLogicalDelBaseEntity;
import com.doinner.common.core.annotation.Excel;

/**
 * 知识点对象 t_csys_knowledge_point
 *
 * @author doinner
 * @date 2023-03-14
 */
public class KnowledgePoint extends AbstractDoinnerLogicalDelBaseEntity {

    private static final long serialVersionUID = -8447178447233726697L;


    /**
     * 知识点名称
     */
    @Excel(name = "知识点名称")
    private String name;


    /**
     * 创建部门ID
     */
    private Long deptBy;


    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public Long getDeptBy() {
        return deptBy;
    }

    public void setDeptBy(Long deptBy) {
        this.deptBy = deptBy;
    }

}
