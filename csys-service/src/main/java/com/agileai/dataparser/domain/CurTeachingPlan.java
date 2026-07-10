package com.agileai.dataparser.domain;

import com.doinner.common.core.annotation.Excel;
import com.doinner.common.core.web.domain.BaseEntity;

import java.util.Objects;

/**
 * 选课详情对象 cur_teaching_plan
 *
 * @author agileai
 * @date 2022-04-25
 */
public class CurTeachingPlan extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键 */
    private Long id;

    /** 名称 */
    @Excel(name = "名称")
    private String name;

    /** 培养规划id */
    @Excel(name = "培养规划id")
    private Long planId;

    /** 培养规划 */
    @Excel(name = "培养规划")
    private CurPlanning curPlanning;

    /** 选课详情（json） */
    @Excel(name = "选课详情", readConverterExp = "j=son")
    private String courseSelection;

    private String college;

    public void setId(Long id)
    {
        this.id = id;
    }

    public Long getId()
    {
        return id;
    }
    public void setName(String name)
    {
        this.name = name;
    }

    public String getName()
    {
        return name;
    }
    public void setPlanId(Long planId)
    {
        this.planId = planId;
    }

    public Long getPlanId()
    {
        return planId;
    }
    public void setCourseSelection(String courseSelection)
    {
        this.courseSelection = courseSelection;
    }

    public String getCourseSelection()
    {
        return courseSelection;
    }

    public CurPlanning getCurPlanning() {
        return curPlanning;
    }

    public void setCurPlanning(CurPlanning curPlanning) {
        this.curPlanning = curPlanning;
    }

    public String getCollege() {
        return college;
    }

    public void setCollege(String college) {
        this.college = college;
    }

    @Override
    public String toString() {
        return "CurTeachingPlan{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", planId=" + planId +
                ", curPlanning=" + curPlanning +
                ", courseSelection='" + courseSelection + '\'' +
                "} " + super.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CurTeachingPlan that = (CurTeachingPlan) o;
        return Objects.equals(id, that.id) && Objects.equals(name, that.name) && Objects.equals(planId, that.planId) && Objects.equals(curPlanning, that.curPlanning) && Objects.equals(courseSelection, that.courseSelection);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, planId, curPlanning, courseSelection);
    }
}
