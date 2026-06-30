package com.doinner.csys.domain;

import com.doinner.common.core.domain.AbstractEntity;

/**
 * 知识单元与培养标准关联对象 t_csys_knowledge_unit_ref_std_cultivation
 *
 * @author doinner
 * @date 2023-03-14
 */
public class KnowledgeUnitRefStdCultivation extends AbstractEntity {

    private static final long serialVersionUID = 1710481373485289109L;

    /**
     * 培养规划id
     */
    private Long schemeId;

    /**
     * 课程id
     */
    private Long courseId;

    /**
     * 知识单元id
     */
    private Long unitId;

    /**
     * 培养标准id
     */
    private Long cultivationId;

    public Long getSchemeId() {
        return schemeId;
    }

    public void setSchemeId(Long schemeId) {
        this.schemeId = schemeId;
    }

    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

    public void setUnitId(Long unitId) {
        this.unitId = unitId;
    }

    public Long getUnitId() {
        return unitId;
    }

    public void setCultivationId(Long cultivationId) {
        this.cultivationId = cultivationId;
    }

    public Long getCultivationId() {
        return cultivationId;
    }

}
