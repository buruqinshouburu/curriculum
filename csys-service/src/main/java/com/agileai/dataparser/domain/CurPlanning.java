package com.agileai.dataparser.domain;

import com.doinner.common.core.annotation.Excel;
import com.doinner.common.core.web.domain.BaseEntity;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;


/**
 * 培养规划对象 cur_planning
 *
 *
 * @author agileai
 * @date 2022-06-02
 */
public class CurPlanning extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键 */
    private Long id;

    /** 培养规划名称 */
    @Excel(name = "培养规划名称")
    private String name;

    /** 培养目标id */
    @Excel(name = "培养目标id")
    private String targetId;

    /** 必修课程id */
    @Excel(name = "必修课程id")
    private String obligatoryCurriculumIds;


    /** 选修课程id */
    @Excel(name = "选修课程id")
    private String electiveCurriculumIds;

    /** 学时 */
    @Excel(name = "学时")
    private String hours;

    /** 课程能力关系 */
    @Excel(name = "课程能力关系")
    private String relationships;

    /** 选课详情(json) */
    @Excel(name = "选课详情(json)")
    private String courseSelection;

    /** 公共基础必修课程 */
    @Excel(name = "公共基础必修课程")
    private String pfrcIds;

    /** 学科基础必修课程 */
    @Excel(name = "学科基础必修课程")
    private String sfrcIds;

    /** 专业必修课程 */
    @Excel(name = "专业必修课程")
    private String prcIds;

    /** 公共基础选修课程 */
    @Excel(name = "公共基础选修课程")
    private String pfecIds;

    /** 学科基础、专业选修课程 */
    @Excel(name = "学科基础、专业选修课程")
    private String sfpecIds;

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
    public void setTargetId(String targetId)
    {
        this.targetId = targetId;
    }

    public String getTargetId()
    {
        return targetId;
    }


    public void setObligatoryCurriculumIds(String obligatoryCurriculumIds)
    {
        this.obligatoryCurriculumIds = obligatoryCurriculumIds;
    }

    public String getObligatoryCurriculumIds()
    {
        return obligatoryCurriculumIds;
    }
    public void setElectiveCurriculumIds(String electiveCurriculumIds)
    {
        this.electiveCurriculumIds = electiveCurriculumIds;
    }

    public String getElectiveCurriculumIds()
    {
        return electiveCurriculumIds;
    }
    public void setHours(String hours)
    {
        this.hours = hours;
    }

    public String getHours()
    {
        return hours;
    }
    public void setRelationships(String relationships)
    {
        this.relationships = relationships;
    }

    public String getRelationships()
    {
        return relationships;
    }
    public void setCourseSelection(String courseSelection)
    {
        this.courseSelection = courseSelection;
    }

    public String getCourseSelection()
    {
        return courseSelection;
    }
    public void setPfrcIds(String pfrcIds)
    {
        this.pfrcIds = pfrcIds;
        if(StringUtils.isEmpty(this.obligatoryCurriculumIds)){
            this.obligatoryCurriculumIds = pfrcIds;
        }else{
            this.obligatoryCurriculumIds = this.obligatoryCurriculumIds + "," + pfrcIds;
        }
    }

    public String getPfrcIds()
    {
        return pfrcIds;
    }
    public void setSfrcIds(String sfrcIds)
    {
        this.sfrcIds = sfrcIds;
        if(StringUtils.isEmpty(this.obligatoryCurriculumIds)){
            this.obligatoryCurriculumIds = sfrcIds;
        }else{
            this.obligatoryCurriculumIds = this.obligatoryCurriculumIds + "," + sfrcIds;
        }

    }

    public String getSfrcIds()
    {
        return sfrcIds;
    }
    public void setPrcIds(String prcIds)
    {
        this.prcIds = prcIds;
        if(StringUtils.isEmpty(this.obligatoryCurriculumIds)){
            this.obligatoryCurriculumIds = prcIds;
        }else{
            this.obligatoryCurriculumIds = this.obligatoryCurriculumIds + "," + prcIds;
        }

    }

    public String getPrcIds()
    {
        return prcIds;
    }
    public void setPfecIds(String pfecIds)
    {
        this.pfecIds = pfecIds;
        if(StringUtils.isEmpty(this.electiveCurriculumIds)){
            this.electiveCurriculumIds = pfecIds;
        }else{
            this.electiveCurriculumIds = this.electiveCurriculumIds + "," + pfecIds;
        }
    }

    public String getPfecIds()
    {
        return pfecIds;
    }
    public void setSfpecIds(String sfpecIds)
    {
        this.sfpecIds = sfpecIds;
        if(StringUtils.isEmpty(this.electiveCurriculumIds)){
            this.electiveCurriculumIds = sfpecIds;
        }else{
            this.electiveCurriculumIds = this.electiveCurriculumIds + "," + sfpecIds;
        }
    }

    public String getSfpecIds()
    {
        return sfpecIds;
    }


    public String getCollege() {
        return college;
    }

    public void setCollege(String college) {
        this.college = college;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("name", getName())
            .append("targetId", getTargetId())
            .append("obligatoryCurriculumIds", getObligatoryCurriculumIds())
            .append("electiveCurriculumIds", getElectiveCurriculumIds())
            .append("hours", getHours())
            .append("relationships", getRelationships())
            .append("courseSelection", getCourseSelection())
            .append("pfrcIds", getPfrcIds())
            .append("sfrcIds", getSfrcIds())
            .append("prcIds", getPrcIds())
            .append("pfecIds", getPfecIds())
            .append("sfpecIds", getSfpecIds())
            .toString();
    }
}
