package com.doinner.csys.domain;

import com.doinner.common.core.domain.db.AbstractIdEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.doinner.common.core.annotation.Excel;
import com.doinner.common.core.web.domain.BaseEntity;

/**
 * 没有重复知识点的ID组合对象 t_csys_knowledge_no_check_log
 *
 * @author doinner
 * @date 2023-03-27
 */
public class KnowledgeNoCheckLog extends AbstractIdEntity
{
    private static final long serialVersionUID = 1L;


    /** 一个课程的ID */
    @Excel(name = "一个课程的ID")
    private Long sourceCourseId;

    /** 另外一个课程的ID */
    @Excel(name = "另外一个课程的ID")
    private Long targetCourseId;

    public Long getSourceCourseId() {
        return sourceCourseId;
    }

    public void setSourceCourseId(Long sourceCourseId) {
        this.sourceCourseId = sourceCourseId;
    }

    public Long getTargetCourseId() {
        return targetCourseId;
    }

    public void setTargetCourseId(Long targetCourseId) {
        this.targetCourseId = targetCourseId;
    }
}
