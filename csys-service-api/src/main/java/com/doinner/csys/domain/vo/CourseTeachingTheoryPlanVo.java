package com.doinner.csys.domain.vo;

import com.doinner.csys.domain.CourseTeachingTheoryPlan;

import java.util.List;

/**
 * @author wzg
 * @date 2023/3/17 10:49
 */
public class CourseTeachingTheoryPlanVo extends CourseTeachingTheoryPlan {
    private String exaMethodName;
    private List<Table> tables;
    private List<Table> chapterTables;

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

    public List<Table> getChapterTables() {
        return chapterTables;
    }

    public void setChapterTables(List<Table> chapterTables) {
        this.chapterTables = chapterTables;
    }
}
