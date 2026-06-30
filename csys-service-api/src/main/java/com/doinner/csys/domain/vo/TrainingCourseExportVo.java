package com.doinner.csys.domain.vo;

import com.doinner.common.core.annotation.Excel;

public class TrainingCourseExportVo {
    @Excel(name = "课程名(name)")
    private String name;

    /**
     * 课程编号(code)
     */
    @Excel(name = "课程编号(code)")
    private String code;

    /**
     * 课程类型(type)  1课程 3 课程含实践  type=2训练课type=4实践课
     */
    @Excel(name = "课程类型(type)",readConverterExp = "1=课程,3=课程含实践,2=训练课,4=实践课" )
    private String type;
    /**
     * 学时安排(hours)
     */
    @Excel(name = "学时安排(hours)")
    private Double hours;
    /**
     * 实践学时(practice_hours)
     */
    @Excel(name = "实践学时(practice_hours)")
    private Double practiceHours;
    /**
     * 讲授学时(teach_hours)
     */
    @Excel(name = "讲授学时(teach_hours)")
    private Double teachHours;
    /**
     * 学分(credit)
     */
    @Excel(name = "学分(credit)")
    private Double credit;

    @Excel(name = "开课年份")
    private String openYear;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Double getHours() {
        return hours;
    }

    public void setHours(Double hours) {
        this.hours = hours;
    }

    public Double getPracticeHours() {
        return practiceHours;
    }

    public void setPracticeHours(Double practiceHours) {
        this.practiceHours = practiceHours;
    }

    public Double getTeachHours() {
        return teachHours;
    }

    public void setTeachHours(Double teachHours) {
        this.teachHours = teachHours;
    }

    public Double getCredit() {
        return credit;
    }

    public void setCredit(Double credit) {
        this.credit = credit;
    }

    public String getOpenYear() {
        return openYear;
    }

    public void setOpenYear(String openYear) {
        this.openYear = openYear;
    }
}
