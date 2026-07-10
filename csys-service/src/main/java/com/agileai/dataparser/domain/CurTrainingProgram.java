package com.agileai.dataparser.domain;

import com.doinner.common.core.annotation.Excel;
import com.doinner.common.core.domain.db.AbstractTreeBaseEntity;

import java.util.Objects;


/**
 * 培养方案对象 cur_training_program
 *
 * @author agileai
 * @date 2022-05-13
 */
public class CurTrainingProgram extends AbstractTreeBaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键 */
    private Long id;

    /** 教学计划id */
    @Excel(name = "教学计划id")
    private Long teachingPlanId;

    /** 名称 */
    @Excel(name = "名称")
    private String name;

    /** 文件id */
    @Excel(name = "文件id")
    private String fileId;

    /** 文件名称 */
    @Excel(name = "文件名称")
    private String fileName;

    /** 教学计划 */
    private CurTeachingPlan curTeachingPlan;

    private String college;

    private Integer type;

    @Override
    public String toString() {
        return "CurTrainingProgram{" +
                "id=" + id +
                ", teachingPlanId=" + teachingPlanId +
                ", name='" + name + '\'' +
                ", fileId='" + fileId + '\'' +
                ", fileName='" + fileName + '\'' +
                ", curTeachingPlan=" + curTeachingPlan +
                ", college='" + college + '\'' +
                ", type=" + type +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CurTrainingProgram that = (CurTrainingProgram) o;
        return Objects.equals(id, that.id) && Objects.equals(teachingPlanId, that.teachingPlanId) && Objects.equals(name, that.name) && Objects.equals(fileId, that.fileId) && Objects.equals(fileName, that.fileName) && Objects.equals(curTeachingPlan, that.curTeachingPlan) && Objects.equals(college, that.college) && Objects.equals(type, that.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, teachingPlanId, name, fileId, fileName, curTeachingPlan, college, type);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getTeachingPlanId() {
        return teachingPlanId;
    }

    public void setTeachingPlanId(Long teachingPlanId) {
        this.teachingPlanId = teachingPlanId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public CurTeachingPlan getCurTeachingPlan() {
        return curTeachingPlan;
    }

    public void setCurTeachingPlan(CurTeachingPlan curTeachingPlan) {
        this.curTeachingPlan = curTeachingPlan;
    }

    public String getCollege() {
        return college;
    }

    public void setCollege(String college) {
        this.college = college;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }
}
