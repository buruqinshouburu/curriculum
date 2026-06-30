package com.doinner.csys.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.doinner.common.core.annotation.Excel;
import com.doinner.common.core.web.domain.BaseEntity;

/**
 * 修业时间分配情况对象 t_csys_training_scheme_week
 */
public class TrainingSchemeWeek extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键id */
    private Long id;

    /** 培养方案id */
    @Excel(name = "培养方案id")
    private Long schemeId;

    /** 课程教学 */
    @Excel(name = "课程教学")
    private Long courseTeaching;

    /** 集中实践教学 */
    @Excel(name = "集中实践教学")
    private Long practiceTeaching;

    /** 假期休整 */
    @Excel(name = "假期休整")
    private Long vacation;

    /** 机动 */
    @Excel(name = "机动")
    private Long motorDriven;

    public void setId(Long id)
    {
        this.id = id;
    }

    public Long getId()
    {
        return id;
    }
    public void setSchemeId(Long schemeId)
    {
        this.schemeId = schemeId;
    }

    public Long getSchemeId()
    {
        return schemeId;
    }
    public void setCourseTeaching(Long courseTeaching)
    {
        this.courseTeaching = courseTeaching;
    }

    public Long getCourseTeaching()
    {
        return courseTeaching;
    }
    public void setPracticeTeaching(Long practiceTeaching)
    {
        this.practiceTeaching = practiceTeaching;
    }

    public Long getPracticeTeaching()
    {
        return practiceTeaching;
    }

    public Long getVacation() {
        return vacation;
    }

    public void setVacation(Long vacation) {
        this.vacation = vacation;
    }

    public void setMotorDriven(Long motorDriven)
    {
        this.motorDriven = motorDriven;
    }

    public Long getMotorDriven()
    {
        return motorDriven;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("schemeId", getSchemeId())
            .append("courseTeaching", getCourseTeaching())
            .append("practiceTeaching", getPracticeTeaching())
            .append("vacation", getVacation())
            .append("motorDriven", getMotorDriven())
            .toString();
    }
}
