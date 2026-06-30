package com.doinner.csys.domain;

import com.doinner.common.core.annotation.Excel;

/**
 * 文档抽取和专业关联对象 t_csys_teaching_programme_instance_ex_ref_major
 *
 * @author wzg
 * @date 2026-03-11
 */
public class TeachingProgrammeInstanceExRefMajor {
    private static final long serialVersionUID=1L;

    /** t_csys_teaching_programme_instance_extract表id */
            @Excel(name = "t_csys_teaching_programme_instance_extract表id")
    private Long extractId;

    /** 专业表id */
            @Excel(name = "专业表id")
    private Long majorId;

    public void setExtractId(Long extractId)
            {
            this.extractId = extractId;
            }

    public Long getExtractId()
            {
            return extractId;
            }
    public void setMajorId(Long majorId)
            {
            this.majorId = majorId;
            }

    public Long getMajorId()
            {
            return majorId;
            }


}
