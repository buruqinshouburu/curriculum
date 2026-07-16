package com.doinner.csys.entity.csys.model;

import com.doinner.csys.domain.StandardGraduation;
import com.doinner.csys.domain.TeachingPlanAssessment;
import com.doinner.csys.domain.TeachingPlanCondition;
import com.doinner.csys.domain.TeachingPlanContent;
import com.doinner.csys.domain.TeachingPlanObjective;
import com.doinner.csys.domain.TeachingPlanObjectiveRef;
import com.doinner.csys.domain.TeachingPlanPracticeItem;
import com.doinner.csys.domain.TeachingPlanPracticeItemDetail;
import com.doinner.csys.domain.TeachingPlanSection;
import com.doinner.csys.domain.TeachingPlanTargetDesign;
import com.doinner.csys.domain.TeachingPlanTeacher;
import com.doinner.csys.domain.TeachingPlanTextbook;

import java.util.List;
import java.util.Map;

/**
 * 课程教学计划文档生成模型。
 *
 * 承载四套模板（课程/实验课程/实践训练课目/实践项目）渲染所需的全部数据，
 * 由 {@code TeachingPlanService.generateTeachingPlanWord} 取数后组装，再交由
 * {@link com.doinner.csys.entity.csys.CourseTeachingPlanGenerator} 按 {@link #docType} 分发生成 Word。
 *
 * 直接复用教学计划各模块领域实体，生成器读取其 getter 渲染，避免重复定义字段。
 */
public class CourseTeachingPlanModel {

    /**
     * 文档类型，与 t_csys_course.type 一致：1课程 2实践训练课目 3实验课程(课程含实践) 4实践项目
     */
    private Integer docType;

    // ============================ 基本信息块 ============================
    /** 课程/课目/项目名称 */
    private String courseName;
    /** 课程/课目/项目编号 */
    private String courseCode;
    /** 英文名称 */
    private String courseEnName;
    /** 启用时间，如 2026年春季学期 */
    private String enabledTerm;
    /** 讲授学时 */
    private String teachHours;
    /** 实践学时 */
    private String practiceHours;
    /** 总学时 */
    private String hours;
    /** 学分 */
    private String credit;
    /** 适用对象 */
    private String educationLevel;
    /** 适用专业名称 */
    private String majorName;
    /** 开课学期 */
    private String term;
    /** 课程模块 */
    private String courseModule;
    /** 修读性质 */
    private String courseAttr;
    /** 计分规则（来自教学计划 score_rule） */
    private String scoreRule;
    /** 支撑课程或实践训练课目（实践项目模板基本信息表用，文本） */
    private String supportingCourses;

    // ============================ 各模块列表（按 planId 查询，plan 不存在时为空） ============================
    /** 教员团队 */
    private List<TeachingPlanTeacher> teachers;
    /** 文本章节（任务背景/总体设计/课程概述/组织方式等大段文本） */
    private List<TeachingPlanSection> sections;
    /** 教学计划目标（知识/能力/素质） */
    private List<TeachingPlanObjective> objectives;
    /** 目标支撑毕业要求：objectiveId -> refs */
    private Map<Long, List<TeachingPlanObjectiveRef>> objectiveRefMap;
    /** 教学内容与学时安排 */
    private List<TeachingPlanContent> contents;
    /** 目标达成设计（知识/能力/素质，生成器按 designTypeCode 分组） */
    private List<TeachingPlanTargetDesign> targetDesigns;
    /** 实验/实践项目 */
    private List<TeachingPlanPracticeItem> practiceItems;
    /** 实验项目明细：itemId -> details */
    private Map<Long, List<TeachingPlanPracticeItemDetail>> itemDetailMap;
    /** 考核评价 */
    private List<TeachingPlanAssessment> assessments;
    /** 教材 */
    private List<TeachingPlanTextbook> textbooks;
    /** 条件保障 */
    private List<TeachingPlanCondition> conditions;
    /** 课程绑定的毕业要求（支撑毕业要求列） */
    private List<StandardGraduation> courseGraduations;

    // ============================ getter/setter ============================

    public Integer getDocType() {
        return docType;
    }

    public void setDocType(Integer docType) {
        this.docType = docType;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode;
    }

    public String getCourseEnName() {
        return courseEnName;
    }

    public void setCourseEnName(String courseEnName) {
        this.courseEnName = courseEnName;
    }

    public String getEnabledTerm() {
        return enabledTerm;
    }

    public void setEnabledTerm(String enabledTerm) {
        this.enabledTerm = enabledTerm;
    }

    public String getTeachHours() {
        return teachHours;
    }

    public void setTeachHours(String teachHours) {
        this.teachHours = teachHours;
    }

    public String getPracticeHours() {
        return practiceHours;
    }

    public void setPracticeHours(String practiceHours) {
        this.practiceHours = practiceHours;
    }

    public String getHours() {
        return hours;
    }

    public void setHours(String hours) {
        this.hours = hours;
    }

    public String getCredit() {
        return credit;
    }

    public void setCredit(String credit) {
        this.credit = credit;
    }

    public String getEducationLevel() {
        return educationLevel;
    }

    public void setEducationLevel(String educationLevel) {
        this.educationLevel = educationLevel;
    }

    public String getMajorName() {
        return majorName;
    }

    public void setMajorName(String majorName) {
        this.majorName = majorName;
    }

    public String getTerm() {
        return term;
    }

    public void setTerm(String term) {
        this.term = term;
    }

    public String getCourseModule() {
        return courseModule;
    }

    public void setCourseModule(String courseModule) {
        this.courseModule = courseModule;
    }

    public String getCourseAttr() {
        return courseAttr;
    }

    public void setCourseAttr(String courseAttr) {
        this.courseAttr = courseAttr;
    }

    public String getScoreRule() {
        return scoreRule;
    }

    public void setScoreRule(String scoreRule) {
        this.scoreRule = scoreRule;
    }

    public String getSupportingCourses() {
        return supportingCourses;
    }

    public void setSupportingCourses(String supportingCourses) {
        this.supportingCourses = supportingCourses;
    }

    public List<TeachingPlanTeacher> getTeachers() {
        return teachers;
    }

    public void setTeachers(List<TeachingPlanTeacher> teachers) {
        this.teachers = teachers;
    }

    public List<TeachingPlanSection> getSections() {
        return sections;
    }

    public void setSections(List<TeachingPlanSection> sections) {
        this.sections = sections;
    }

    public List<TeachingPlanObjective> getObjectives() {
        return objectives;
    }

    public void setObjectives(List<TeachingPlanObjective> objectives) {
        this.objectives = objectives;
    }

    public Map<Long, List<TeachingPlanObjectiveRef>> getObjectiveRefMap() {
        return objectiveRefMap;
    }

    public void setObjectiveRefMap(Map<Long, List<TeachingPlanObjectiveRef>> objectiveRefMap) {
        this.objectiveRefMap = objectiveRefMap;
    }

    public List<TeachingPlanContent> getContents() {
        return contents;
    }

    public void setContents(List<TeachingPlanContent> contents) {
        this.contents = contents;
    }

    public List<TeachingPlanTargetDesign> getTargetDesigns() {
        return targetDesigns;
    }

    public void setTargetDesigns(List<TeachingPlanTargetDesign> targetDesigns) {
        this.targetDesigns = targetDesigns;
    }

    public List<TeachingPlanPracticeItem> getPracticeItems() {
        return practiceItems;
    }

    public void setPracticeItems(List<TeachingPlanPracticeItem> practiceItems) {
        this.practiceItems = practiceItems;
    }

    public Map<Long, List<TeachingPlanPracticeItemDetail>> getItemDetailMap() {
        return itemDetailMap;
    }

    public void setItemDetailMap(Map<Long, List<TeachingPlanPracticeItemDetail>> itemDetailMap) {
        this.itemDetailMap = itemDetailMap;
    }

    public List<TeachingPlanAssessment> getAssessments() {
        return assessments;
    }

    public void setAssessments(List<TeachingPlanAssessment> assessments) {
        this.assessments = assessments;
    }

    public List<TeachingPlanTextbook> getTextbooks() {
        return textbooks;
    }

    public void setTextbooks(List<TeachingPlanTextbook> textbooks) {
        this.textbooks = textbooks;
    }

    public List<TeachingPlanCondition> getConditions() {
        return conditions;
    }

    public void setConditions(List<TeachingPlanCondition> conditions) {
        this.conditions = conditions;
    }

    public List<StandardGraduation> getCourseGraduations() {
        return courseGraduations;
    }

    public void setCourseGraduations(List<StandardGraduation> courseGraduations) {
        this.courseGraduations = courseGraduations;
    }
}
