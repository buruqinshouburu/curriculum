package com.agileai.dataparser.domain;

public class CurTeachingPlanningTheory {

    /** 主键 */
    private Long id;

    /** 主表id */
    private Long mainId;

    /** 课程性质地位 */
    private String curNatureStatus;

    /** 课程知识目标 */
    private String curKnowledgeTarget;

    /** 课程能力目标 */
    private String curAbilityTarget;

    /** 课程思政目标 */
    private String curIdeoPoliTarget;

    /** 教学方法 */
    private String teachMethod;

    /** 考核方式 */
    private String checkType;

    /** 组织方式-考试方式 */
    private String examType;

    /** 组织方式-开闭卷 */
    private String examOnOff;

    /** 成绩评定 */
    private String achievement;

    /** 记分标准 */
    private String scoreStandard;

    /** 评分标准-作业 */
    private String task;

    /** 评分标准-案例分析 */
    private String caseAnalysis;

    /** 评分标准-实验 */
    private String practice;

    /** 评分标准-三级项目 */
    private String threeProject;

    /** 评分标准-实习 */
    private String internship;

    /** 评分标准-期末考试 */
    private String examLast;

    /** 其他 */
    private String elseOther;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getMainId() {
        return mainId;
    }

    public void setMainId(Long mainId) {
        this.mainId = mainId;
    }

    public String getCurNatureStatus() {
        return curNatureStatus;
    }

    public void setCurNatureStatus(String curNatureStatus) {
        this.curNatureStatus = curNatureStatus;
    }

    public String getCurKnowledgeTarget() {
        return curKnowledgeTarget;
    }

    public void setCurKnowledgeTarget(String curKnowledgeTarget) {
        this.curKnowledgeTarget = curKnowledgeTarget;
    }

    public String getCurAbilityTarget() {
        return curAbilityTarget;
    }

    public void setCurAbilityTarget(String curAbilityTarget) {
        this.curAbilityTarget = curAbilityTarget;
    }

    public String getCurIdeoPoliTarget() {
        return curIdeoPoliTarget;
    }

    public void setCurIdeoPoliTarget(String curIdeoPoliTarget) {
        this.curIdeoPoliTarget = curIdeoPoliTarget;
    }

    public String getTeachMethod() {
        return teachMethod;
    }

    public void setTeachMethod(String teachMethod) {
        this.teachMethod = teachMethod;
    }

    public String getCheckType() {
        return checkType;
    }

    public void setCheckType(String checkType) {
        this.checkType = checkType;
    }

    public String getExamType() {
        return examType;
    }

    public void setExamType(String examType) {
        this.examType = examType;
    }

    public String getExamOnOff() {
        return examOnOff;
    }

    public void setExamOnOff(String examOnOff) {
        this.examOnOff = examOnOff;
    }

    public String getAchievement() {
        return achievement;
    }

    public void setAchievement(String achievement) {
        this.achievement = achievement;
    }

    public String getScoreStandard() {
        return scoreStandard;
    }

    public void setScoreStandard(String scoreStandard) {
        this.scoreStandard = scoreStandard;
    }

    public String getTask() {
        return task;
    }

    public void setTask(String task) {
        this.task = task;
    }

    public String getCaseAnalysis() {
        return caseAnalysis;
    }

    public void setCaseAnalysis(String caseAnalysis) {
        this.caseAnalysis = caseAnalysis;
    }

    public String getPractice() {
        return practice;
    }

    public void setPractice(String practice) {
        this.practice = practice;
    }

    public String getThreeProject() {
        return threeProject;
    }

    public void setThreeProject(String threeProject) {
        this.threeProject = threeProject;
    }

    public String getInternship() {
        return internship;
    }

    public void setInternship(String internship) {
        this.internship = internship;
    }

    public String getExamLast() {
        return examLast;
    }

    public void setExamLast(String examLast) {
        this.examLast = examLast;
    }

    public String getElseOther() {
        return elseOther;
    }

    public void setElseOther(String elseOther) {
        this.elseOther = elseOther;
    }
}
