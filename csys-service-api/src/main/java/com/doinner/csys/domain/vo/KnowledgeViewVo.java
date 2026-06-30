package com.doinner.csys.domain.vo;

import com.doinner.csys.domain.Course;
import com.doinner.csys.domain.KnowledgePoint;
import com.doinner.csys.domain.KnowledgeUnit;
import org.apache.ibatis.logging.Log;

public class KnowledgeViewVo {

    private String id;

    private String name;

    private String parentId;



    /**
     * 真ID
     */
    private Long sourceId;


    private String cfgId;

    private String cfgName;

    private String remark;

    public KnowledgeViewVo(CourseVo course) {
        this.id = "course" + course.getId();
        this.name = course.getName();
        this.parentId = "-1";
        this.sourceId = course.getId();
        this.cfgId = "-61";
        this.cfgName = "课程";
        this.remark = course.getRemark();

    }

    public KnowledgeViewVo(KnowledgeUnit unit, Long parentId) {
        this.id = "unit" + unit.getId();
        this.name = unit.getName();
        this.parentId = "course" + parentId;
        this.sourceId = unit.getId();
        this.cfgId = "-62";
        this.cfgName = "知识单元";
        this.remark = unit.getRemark();
    }


    public KnowledgeViewVo(KnowledgePoint point, Long parentId) {
        this.id = "point" + point.getId();
        this.name = point.getName();
        this.parentId = "unit" + parentId;
        this.sourceId = point.getId();
        this.cfgId = "-63";
        this.cfgName = "知识点";
        this.remark = point.getRemark();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public Long getSourceId() {
        return sourceId;
    }

    public void setSourceId(Long sourceId) {
        this.sourceId = sourceId;
    }

    public String getCfgId() {
        return cfgId;
    }

    public void setCfgId(String cfgId) {
        this.cfgId = cfgId;
    }

    public String getCfgName() {
        return cfgName;
    }

    public void setCfgName(String cfgName) {
        this.cfgName = cfgName;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
