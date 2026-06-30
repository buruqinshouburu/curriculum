package com.doinner.csys.domain.vo;

import com.doinner.csys.domain.CourseTeachingPracticePlan;

import java.util.List;

/**
 * @author wzg
 * @date 2023/3/17 10:49
 */
public class CourseTeachingPracticePlanVo extends CourseTeachingPracticePlan {
    private String exaMethodName;
    private List<Table> tables;
    private List<ArrangementVo> arrangementVos;

    public String getExaMethodName() {
        return exaMethodName;
    }

    public void setExaMethodName(String exaMethodName) {
        this.exaMethodName = exaMethodName;
    }

    public List<Table> getTables() {
        return tables;
    }

    public void setTables(List<Table> tables) {
        this.tables = tables;
    }

    public List<ArrangementVo> getArrangementVos() {
        return arrangementVos;
    }

    public void setArrangementVos(List<ArrangementVo> arrangementVos) {
        this.arrangementVos = arrangementVos;
    }
}
