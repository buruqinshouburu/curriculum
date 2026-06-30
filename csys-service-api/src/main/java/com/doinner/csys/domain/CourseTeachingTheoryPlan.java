package com.doinner.csys.domain;

import com.doinner.common.core.domain.db.AbstractDoinnerLogicalDelBaseEntity;
import com.doinner.common.core.annotation.Excel;

/**
 * 理论教学计划对象 t_csys_course_teaching_theory_plan
 *
 * @author doinner
 * @date 2023-03-14
 */
public class CourseTeachingTheoryPlan extends AbstractDoinnerLogicalDelBaseEntity {

    private static final long serialVersionUID = 5598972295395058240L;

    /**
     * 课程id(course_id)
     */
    @Excel(name = "课程id(course_id)")
    private Long courseId;

    /**
     * 性质地位(nature_level)
     */
    @Excel(name = "性质地位(nature_level)")
    private String natureLevel;

    /**
     * 知识目标(know_level)
     */
    @Excel(name = "知识目标(know_level)")
    private String knowLevel;

    /**
     * 能力目标(atiityl_level)
     */
    @Excel(name = "能力目标(ability_level)")
    private String abilityLevel;

    /**
     * 思政目标(poltics_level)
     */
    @Excel(name = "思政目标(politics_level)")
    private String politicsLevel;

    /**
     * 教学方法(teaching_method)
     */
    @Excel(name = "教学方法(teaching_method)")
    private String teachingMethod;

    /**
     * 课程学习内容与时间节点json(content_json)
     */
    @Excel(name = "课程学习内容与时间节点json(content_json)")
    private String contentJson;

    /**
     * 课程章节id(chapter_id)
     */
    @Excel(name = "课程章节id(chapter_id)")
    private Long chapterId;

    /**
     * 考核方式(exa_method)
     */
    @Excel(name = "考核方式(exa_method)")
    private Integer exaMethod;

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
     * 评分标志json(standard_json)
     */
    @Excel(name = "评分标志json(standard_json)")
    private String standardJson;

    /**
     * 教材参考书id(reference_id)
     */
    @Excel(name = "教材参考书id(reference_id)")
    private Long referenceId;


    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

    public Long getCourseId() {
        return courseId;
    }

    public void setNatureLevel(String natureLevel) {
        this.natureLevel = natureLevel;
    }

    public String getNatureLevel() {
        return natureLevel;
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

    public void setTeachingMethod(String teachingMethod) {
        this.teachingMethod = teachingMethod;
    }

    public String getTeachingMethod() {
        return teachingMethod;
    }

    public void setContentJson(String contentJson) {
        this.contentJson = contentJson;
    }

    public String getContentJson() {
        return contentJson;
    }

    public void setChapterId(Long chapterId) {
        this.chapterId = chapterId;
    }

    public Long getChapterId() {
        return chapterId;
    }

    public void setExaMethod(Integer exaMethod) {
        this.exaMethod = exaMethod;
    }

    public Integer getExaMethod() {
        return exaMethod;
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

    public void setReferenceId(Long referenceId) {
        this.referenceId = referenceId;
    }

    public Long getReferenceId() {
        return referenceId;
    }

}
