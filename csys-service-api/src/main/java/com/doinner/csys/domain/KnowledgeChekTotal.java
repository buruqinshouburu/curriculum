package com.doinner.csys.domain;

import com.doinner.common.core.domain.db.AbstractIdEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.doinner.common.core.annotation.Excel;
import com.doinner.common.core.web.domain.BaseEntity;

/**
 * 课程知识单元，知识点，统计对象 t_csys_knowledge_chek_total
 *
 * @author doinner
 * @date 2023-03-27
 */
public class KnowledgeChekTotal extends AbstractIdEntity {
    private static final long serialVersionUID = 1L;


    /**
     * 知识领域ID
     */
    @Excel(name = "课程ID")
    private Long sourceCourseId;

    /**
     * 知识单元数量
     */
    @Excel(name = "知识单元数量")
    private Long totalUnitNum;

    /**
     * 知识点数量
     */
    @Excel(name = "知识点数量")
    private Long totalPointNum;

    public Long getSourceCourseId() {
        return sourceCourseId;
    }

    public void setSourceCourseId(Long sourceCourseId) {
        this.sourceCourseId = sourceCourseId;
    }

    public void setTotalUnitNum(Long totalUnitNum) {
        this.totalUnitNum = totalUnitNum;
    }

    public Long getTotalUnitNum() {
        return totalUnitNum;
    }

    public void setTotalPointNum(Long totalPointNum) {
        this.totalPointNum = totalPointNum;
    }

    public Long getTotalPointNum() {
        return totalPointNum;
    }


}
