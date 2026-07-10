package com.agileai.dataparser.domain;

public class CurTeachingPlanningPractice {

    /** 主键 */
    private Long id;

    /** 主表id */
    private Long mainId;

    /** 适用对象 */
    private String object;

    /** 施教学院 */
    private String teachCollege;

    /** 实施地点 */
    private String address;

    /** 任务背景 */
    private String backGround;

    /** 实践知识目标 */
    private String praKnowledgeTarget;

    /** 实践能力目标 */
    private String praAbilityTarget;

    /** 实践思政目标 */
    private String praIdeoPoliTarget;

    /** 主要内容 */
    private String content;

    /** 主要要求 */
    private String require;

    /** 实践环节单位概况 */
    private String unitDesc;

    /** 实践环节内容和要求关联情况 */
    private String contentRequire;

    /** 实践方式 */
    private String praType;

    /** 时间安排 */
    private String timePlan;

    /** 地点安排 */
    private String addressPlan;

    /** 考核 */
    private String check;

    /** 评价 */
    private String judge;

    /** 有关要求 */
    private String requireOf;

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

    public String getObject() {
        return object;
    }

    public void setObject(String object) {
        this.object = object;
    }

    public String getTeachCollege() {
        return teachCollege;
    }

    public void setTeachCollege(String teachCollege) {
        this.teachCollege = teachCollege;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getBackGround() {
        return backGround;
    }

    public void setBackGround(String backGround) {
        this.backGround = backGround;
    }

    public String getPraKnowledgeTarget() {
        return praKnowledgeTarget;
    }

    public void setPraKnowledgeTarget(String praKnowledgeTarget) {
        this.praKnowledgeTarget = praKnowledgeTarget;
    }

    public String getPraAbilityTarget() {
        return praAbilityTarget;
    }

    public void setPraAbilityTarget(String praAbilityTarget) {
        this.praAbilityTarget = praAbilityTarget;
    }

    public String getPraIdeoPoliTarget() {
        return praIdeoPoliTarget;
    }

    public void setPraIdeoPoliTarget(String praIdeoPoliTarget) {
        this.praIdeoPoliTarget = praIdeoPoliTarget;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getRequire() {
        return require;
    }

    public void setRequire(String require) {
        this.require = require;
    }

    public String getUnitDesc() {
        return unitDesc;
    }

    public void setUnitDesc(String unitDesc) {
        this.unitDesc = unitDesc;
    }

    public String getContentRequire() {
        return contentRequire;
    }

    public void setContentRequire(String contentRequire) {
        this.contentRequire = contentRequire;
    }

    public String getPraType() {
        return praType;
    }

    public void setPraType(String praType) {
        this.praType = praType;
    }

    public String getTimePlan() {
        return timePlan;
    }

    public void setTimePlan(String timePlan) {
        this.timePlan = timePlan;
    }

    public String getAddressPlan() {
        return addressPlan;
    }

    public void setAddressPlan(String addressPlan) {
        this.addressPlan = addressPlan;
    }

    public String getCheck() {
        return check;
    }

    public void setCheck(String check) {
        this.check = check;
    }

    public String getJudge() {
        return judge;
    }

    public void setJudge(String judge) {
        this.judge = judge;
    }

    public String getRequireOf() {
        return requireOf;
    }

    public void setRequireOf(String requireOf) {
        this.requireOf = requireOf;
    }
}
