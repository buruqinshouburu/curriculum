package com.doinner.csys.domain;

import com.doinner.common.core.annotation.Excel;
import com.doinner.common.core.domain.db.AbstractDoinnerLogicalDelBaseEntity;

/**
 * 教学计划对象 t_csys_teaching_program
 *
 * @author wzg
 * @date 2026-02-26
 */
public class TeachingProgram extends AbstractDoinnerLogicalDelBaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    private Long id;

    /**
     * 课程id
     */
    @Excel(name = "课程id")
    private Long courseId;

    /**
     * 教学计划版本
     */
    @Excel(name = "教学计划版本")
    private String version;

    /**
     * 学期
     */
    @Excel(name = "学期")
    private String term;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getTerm() {
        return term;
    }

    public void setTerm(String term) {
        this.term = term;
    }

}
