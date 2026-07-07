package com.doinner.csys.domain;

import com.doinner.common.core.domain.db.AbstractDoinnerLogicalDelBaseEntity;
import com.doinner.common.core.annotation.Excel;

/**
 * 培养方案排课对象 t_csys_training_scheme_course_schedule
 *
 * @author doinner
 * @date 2023-03-14
 */
public class TrainingSchemeCourseSchedule extends AbstractDoinnerLogicalDelBaseEntity {
    private static final long serialVersionUID = 8282674370753260857L;

    /**
     * 培养方案id
     */
    @Excel(name = "培养方案id")
    private Long schemeId;

    /**
     * 课程id
     */
    @Excel(name = "课程id")
    private Long courseId;

    /**
     * 课程类型:1:公共基础必修课程
     * 2:学科基础必修课程
     * 3:专业必修课程
     * 4:公共基础选修课程
     * 5:学科基础、专业选修课程
     */
    @Excel(name = "课程子模块")
    private String type;

    /**
     * 学期:1:大一上
     * 2:大一下
     * 3:大二上
     * 4:大二下
     * 5:大三上
     * 6:大三下
     * 7:大四上
     * 8:大四下
     */
    @Excel(name = "学期:1:大一上  2:大一下\n" +
            "            3:大二上\n" +
            "            4:大二下\n" +
            "            5:大三上\n" +
            "            6:大三下\n" +
            "            7:大四上\n" +
            "            8:大四下")
    private Integer term;

    /**
     * 总课时
     */
    @Excel(name = "总课时")
    private Double hours;

    /**
     * 理论课时
     */
    @Excel(name = "理论课时")
    private Double theoryHours;
    @Excel(name = "讲授学时")
    private Double teachHours;

    /**
     * 实践课时
     */
    @Excel(name = "实践课时")
    private Double practiceHours;

    /**
     * 课程是否已选
     */
    @Excel(name = "课程是否已选")
    private Integer checked;

    @Excel(name = "课程类型 :1 必修 2：选修 3：任选")
    private String courseAttr;
    @Excel(name = "学分")
    private Double credits;
    private Long sourceId;
    private Long subMajorId;


    public TrainingSchemeCourseSchedule() {
    }

    public TrainingSchemeCourseSchedule(TrainingSchemeCourseSchedule trainingSchemeCourseSchedule) {
        this.schemeId = trainingSchemeCourseSchedule.getSchemeId();
        this.courseId = trainingSchemeCourseSchedule.getCourseId();
        this.term = trainingSchemeCourseSchedule.getTerm();
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setSchemeId(Long schemeId) {
        this.schemeId = schemeId;
    }

    public Long getSchemeId() {
        return schemeId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

    public Long getCourseId() {
        return courseId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setTerm(Integer term) {
        this.term = term;
    }

    public Integer getTerm() {
        return term;
    }

    public void setHours(Double hours) {
        this.hours = hours;
    }

    public Double getHours() {
        return hours;
    }

    public void setTheoryHours(Double theoryHours) {
        this.theoryHours = theoryHours;
    }

    public Double getTheoryHours() {
        return theoryHours;
    }

    public void setPracticeHours(Double practiceHours) {
        this.practiceHours = practiceHours;
    }

    public Double getPracticeHours() {
        return practiceHours;
    }

    public Integer getChecked() {
        return checked;
    }

    public void setChecked(Integer checked) {
        this.checked = checked;
    }

    public Double getTeachHours() {
        return teachHours;
    }

    public void setTeachHours(Double teachHours) {
        this.teachHours = teachHours;
    }

    public String getCourseAttr() {
        return courseAttr;
    }

    public void setCourseAttr(String courseAttr) {
        this.courseAttr = courseAttr;
    }

    public Double getCredits() {
        return credits;
    }

    public void setCredits(Double credits) {
        this.credits = credits;
    }

    public Long getSourceId() {
        return sourceId;
    }

    public void setSourceId(Long sourceId) {
        this.sourceId = sourceId;
    }

    public Long getSubMajorId() {
        return subMajorId;
    }

    public void setSubMajorId(Long subMajorId) {
        this.subMajorId = subMajorId;
    }
}
