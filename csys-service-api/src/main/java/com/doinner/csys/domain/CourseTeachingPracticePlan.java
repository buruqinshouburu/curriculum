package com.doinner.csys.domain;

import com.doinner.common.core.domain.db.AbstractDoinnerLogicalDelBaseEntity;
import com.doinner.common.core.annotation.Excel;

/**
 * 实践教学计划对象 t_csys_course_teaching_practice_plan
 *
 * @author doinner
 * @date 2023-03-14
 */
public class CourseTeachingPracticePlan extends AbstractDoinnerLogicalDelBaseEntity {


    private static final long serialVersionUID = -7524260591922420329L;

    /**
     * 课程id(course_id)
     */
    @Excel(name = "课程id(course_id)")
    private Long courseId;

    /**
     * 适用对象
     */
    @Excel(name = "适用对象")
    private String suit;

    /**
     * 任务背景描述
     */
    @Excel(name = "任务背景描述")
    private String taskDescribe;

    /**
     * 知识目标(know_level)
     */
    @Excel(name = "知识目标(know_level)")
    private String knowLevel;

    /**
     * 能力目标(ability_level)
     */
    @Excel(name = "能力目标(ability_level)")
    private String abilityLevel;

    /**
     * 思政目标(politics_level)
     */
    @Excel(name = "思政目标(politics_level)")
    private String politicsLevel;

    /**
     * 主要内容及要求(content)
     */
    @Excel(name = "主要内容及要求(content)")
    private String content;

    /**
     * 单位概况及关联情况(overview)
     */
    @Excel(name = "单位概况及关联情况(overview)")
    private String overview;

    /**
     * 实践方式(practice_method)
     */
    @Excel(name = "实践方式(practice_method)")
    private String practiceMethod;

    /**
     * 考核方式(exa_method)
     */
    @Excel(name = "考核方式(exa_method)")
    private String exaMethod;

    /**
     * 组织方式(org_method)
     */
    @Excel(name = "组织方式(org_method)")
    private String orgMethod;

    /**
     * 成绩评定(performance)
     */
    @Excel(name = "成绩评定(performance)")
    private String performance;

    /**
     * 计分标准(standard)
     */
    @Excel(name = "计分标准(standard)")
    private String standard;

    /**
     * 评分标准json(standard_json)
     */
    @Excel(name = "评分标准json(standard_json)")
    private String standardJson;

    /**
     * 有关要求(ask)
     */
    @Excel(name = "有关要求(ask)")
    private String ask;

    /**
     * 时间及地点安排(arrange)
     */
    @Excel(name = "时间及地点安排(arrange)")
    private String arrange;


    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

    public Long getCourseId() {
        return courseId;
    }

    public void setSuit(String suit) {
        this.suit = suit;
    }

    public String getSuit() {
        return suit;
    }

    public void setTaskDescribe(String taskDescribe) {
        this.taskDescribe = taskDescribe;
    }

    public String getTaskDescribe() {
        return taskDescribe;
    }

    public void setKnowLevel(String knowLevel) {
        this.knowLevel = knowLevel;
    }

    public String getKnowLevel() {
        return knowLevel;
    }

    public String getAbilityLevel() {
        return abilityLevel;
    }

    public void setAbilityLevel(String abilityLevel) {
        this.abilityLevel = abilityLevel;
    }

    public String getPoliticsLevel() {
        return politicsLevel;
    }

    public void setPoliticsLevel(String politicsLevel) {
        this.politicsLevel = politicsLevel;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public String getOverview() {
        return overview;
    }

    public void setPracticeMethod(String practiceMethod) {
        this.practiceMethod = practiceMethod;
    }

    public String getPracticeMethod() {
        return practiceMethod;
    }

    public String getExaMethod() {
        return exaMethod;
    }

    public void setExaMethod(String exaMethod) {
        this.exaMethod = exaMethod;
    }

    public void setOrgMethod(String orgMethod) {
        this.orgMethod = orgMethod;
    }

    public String getOrgMethod() {
        return orgMethod;
    }

    public void setPerformance(String performance) {
        this.performance = performance;
    }

    public String getPerformance() {
        return performance;
    }

    public void setStandard(String standard) {
        this.standard = standard;
    }

    public String getStandard() {
        return standard;
    }

    public void setStandardJson(String standardJson) {
        this.standardJson = standardJson;
    }

    public String getStandardJson() {
        return standardJson;
    }

    public void setAsk(String ask) {
        this.ask = ask;
    }

    public String getAsk() {
        return ask;
    }

    public void setArrange(String arrange) {
        this.arrange = arrange;
    }

    public String getArrange() {
        return arrange;
    }



}
