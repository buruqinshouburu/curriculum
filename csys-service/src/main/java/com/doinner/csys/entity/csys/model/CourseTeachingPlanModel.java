package com.doinner.csys.entity.csys.model;

import com.doinner.csys.domain.StandardGraduation;
import com.doinner.csys.domain.TeachingPlanAssessment;
import com.doinner.csys.domain.TeachingPlanCondition;
import com.doinner.csys.domain.TeachingPlanContent;
import com.doinner.csys.domain.TeachingPlanObjective;
import com.doinner.csys.domain.TeachingPlanObjectiveRef;
import com.doinner.csys.domain.TeachingPlanObjectiveAssessment;
import com.doinner.csys.domain.TeachingPlanPracticeItem;
import com.doinner.csys.domain.TeachingPlanPracticeItemDetail;
import com.doinner.csys.domain.TeachingPlanProcessStep;
import com.doinner.csys.domain.TeachingPlanSection;
import com.doinner.csys.domain.TeachingPlanTargetDesign;
import com.doinner.csys.domain.TeachingPlanTaskBackground;
import com.doinner.csys.domain.TeachingPlanTaskBackgroundRef;
import com.doinner.csys.domain.TeachingPlanTeacher;
import com.doinner.csys.domain.TeachingPlanTextbook;
import com.doinner.csys.domain.TeachingPlanTrainingPurpose;
import com.doinner.csys.domain.TeachingPlanTrainingPurposeRef;
import com.doinner.csys.domain.TeachingPlanSupportObjective;
import com.doinner.csys.domain.TeachingPlanSupportContent;

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
    /** 时间安排（type=4 实践项目基本信息表用，time_Week + unit 译中，形如「16周」） */
    private String timeArrangement;
    /** 支撑课程或实践训练课目（实践项目模板基本信息表用，文本） */
    private String supportingCourses;

    // ============================ 各模块列表（按 planId 查询，plan 不存在时为空） ============================
    /** 教员团队 */
    private List<TeachingPlanTeacher> teachers;
    /** 文本章节（任务背景/总体设计/课程概述/组织方式等大段文本） */
    private List<TeachingPlanSection> sections;
    /** 实施步骤/项目步骤（type4 组织与实施「项目步骤|有关要求」数据行） */
    private List<TeachingPlanProcessStep> processSteps;
    /** 教学计划目标（知识/能力/素质）；兼容单方案，多方案时优先用 schemeObjectiveGroups */
    private List<TeachingPlanObjective> objectives;
    /** 目标支撑毕业要求：objectiveId -> refs（与 objectives 对应的兼容字段） */
    private Map<Long, List<TeachingPlanObjectiveRef>> objectiveRefMap;
    /**
     * 按培养方案分组的「课程目标与支撑毕业要求」。
     * 源课被多个培养方案引用时，每组对应 Word 中一张表。
     */
    private List<SchemeObjectiveGroup> schemeObjectiveGroups;
    /**
     * 按培养方案分组的「任务背景与目标」。
     * 实验课程（type=3）第三节用，每组对应 Word 中一张表。
     */
    private List<SchemeTaskBackgroundGroup> schemeTaskBackgroundGroups;
    /**
     * 按培养方案分组的「训练目的与支撑毕业要求」。
     * 实践训练课目（type=2）第二节用，每组对应 Word 中一张表。
     * 通识通用（课目模块仅∈{1,2,3,9}）时合并为单组且 schemeTitle 为 null。
     */
    private List<SchemeTrainingPurposeGroup> schemeTrainingPurposeGroups;
    /**
     * 训练内容 -> 绑定的训练目的（type2 第四节「目的」列多选）。
     * 遍历 contents 时按 contentId 取绑定列表，拼接目的文本填入「目的」列。
     */
    /**
     * 实践项目(type=4)第二节「支撑的课程目标或训练目的」绑定（计划级多选快照）。
     * 来源：支撑课程(before_course_id)的课程目标(第四部分，同专业优先) + 支撑训练课目(after_course_id)的训练目的(第二部分)。
     */
    private List<TeachingPlanSupportObjective> supportObjectives;
    /**
     * 实践项目(type=4)第二节「涉及的知识体系或训练内容」绑定（计划级多选快照）。
     * 来源：支撑课程的课程知识单元知识点 + 支撑训练课目的训练内容(第四部分)。
     */
    private List<TeachingPlanSupportContent> supportContents;
    /** 教学内容与学时安排 */
    private List<TeachingPlanContent> contents;
    /** 目标达成设计（知识/能力/素质，生成器按 designTypeCode 分组） */
    private List<TeachingPlanTargetDesign> targetDesigns;
    /**
     * 第六节「说明」- 教学环节说明行（取自字典 sys_plan_teaching_link 的全部 label）。
     * 形如：教学环节主要包括：课前预习（准备）、理论教学、……
     */
    private String teachingLinkNote;
    /**
     * 第六节「说明」- 教法说明行（sys_plan_teaching_method）。
     */
    private String teachingMethodNote;
    /**
     * 第六节「说明」- 学法说明行（sys_plan_learning_method）。
     */
    private String learningMethodNote;
    /** 实验/实践项目 */
    private List<TeachingPlanPracticeItem> practiceItems;
    /** 实验项目明细：itemId -> details */
    private Map<Long, List<TeachingPlanPracticeItemDetail>> itemDetailMap;
    /** 考核评价 */
    private List<TeachingPlanAssessment> assessments;
    /** 普通课程的课程目标-考核评价关联，用于第八点新增达成分析表 */
    private List<TeachingPlanObjectiveAssessment> objectiveAssessments;
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

    public String getTimeArrangement() {
        return timeArrangement;
    }

    public void setTimeArrangement(String timeArrangement) {
        this.timeArrangement = timeArrangement;
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

    public List<TeachingPlanProcessStep> getProcessSteps() {
        return processSteps;
    }

    public void setProcessSteps(List<TeachingPlanProcessStep> processSteps) {
        this.processSteps = processSteps;
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

    public List<SchemeObjectiveGroup> getSchemeObjectiveGroups() {
        return schemeObjectiveGroups;
    }

    public void setSchemeObjectiveGroups(List<SchemeObjectiveGroup> schemeObjectiveGroups) {
        this.schemeObjectiveGroups = schemeObjectiveGroups;
    }

    public List<SchemeTaskBackgroundGroup> getSchemeTaskBackgroundGroups() {
        return schemeTaskBackgroundGroups;
    }

    public void setSchemeTaskBackgroundGroups(List<SchemeTaskBackgroundGroup> schemeTaskBackgroundGroups) {
        this.schemeTaskBackgroundGroups = schemeTaskBackgroundGroups;
    }

    public List<SchemeTrainingPurposeGroup> getSchemeTrainingPurposeGroups() {
        return schemeTrainingPurposeGroups;
    }

    public void setSchemeTrainingPurposeGroups(List<SchemeTrainingPurposeGroup> schemeTrainingPurposeGroups) {
        this.schemeTrainingPurposeGroups = schemeTrainingPurposeGroups;
    }

    public List<TeachingPlanSupportObjective> getSupportObjectives() {
        return supportObjectives;
    }

    public void setSupportObjectives(List<TeachingPlanSupportObjective> supportObjectives) {
        this.supportObjectives = supportObjectives;
    }

    public List<TeachingPlanSupportContent> getSupportContents() {
        return supportContents;
    }

    public void setSupportContents(List<TeachingPlanSupportContent> supportContents) {
        this.supportContents = supportContents;
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

    public String getTeachingLinkNote() {
        return teachingLinkNote;
    }

    public void setTeachingLinkNote(String teachingLinkNote) {
        this.teachingLinkNote = teachingLinkNote;
    }

    public String getTeachingMethodNote() {
        return teachingMethodNote;
    }

    public void setTeachingMethodNote(String teachingMethodNote) {
        this.teachingMethodNote = teachingMethodNote;
    }

    public String getLearningMethodNote() {
        return learningMethodNote;
    }

    public void setLearningMethodNote(String learningMethodNote) {
        this.learningMethodNote = learningMethodNote;
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

    public List<TeachingPlanObjectiveAssessment> getObjectiveAssessments() {
        return objectiveAssessments;
    }

    public void setObjectiveAssessments(List<TeachingPlanObjectiveAssessment> objectiveAssessments) {
        this.objectiveAssessments = objectiveAssessments;
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

    /**
     * 单个培养方案下的课程目标 + 支撑毕业要求。
     * Word「四、课程目标与支撑毕业要求」中每个 group 对应一张表。
     */
    public static class SchemeObjectiveGroup {
        private Long schemeId;
        /** 展示标题，如培养方案名称（可带版本） */
        private String schemeTitle;
        private List<TeachingPlanObjective> objectives;
        private Map<Long, List<TeachingPlanObjectiveRef>> objectiveRefMap;

        public Long getSchemeId() {
            return schemeId;
        }

        public void setSchemeId(Long schemeId) {
            this.schemeId = schemeId;
        }

        public String getSchemeTitle() {
            return schemeTitle;
        }

        public void setSchemeTitle(String schemeTitle) {
            this.schemeTitle = schemeTitle;
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
    }

    /**
     * 单个培养方案下的任务背景 + 支撑毕业要求。
     * 实验课程（type=3）Word「三、任务背景与目标」中每个 group 对应一张表。
     */
    public static class SchemeTaskBackgroundGroup {
        private Long schemeId;
        /** 展示标题，如培养方案名称（可带版本） */
        private String schemeTitle;
        private List<TeachingPlanTaskBackground> taskBackgrounds;
        private Map<Long, List<TeachingPlanTaskBackgroundRef>> taskBackgroundRefMap;

        public Long getSchemeId() {
            return schemeId;
        }

        public void setSchemeId(Long schemeId) {
            this.schemeId = schemeId;
        }

        public String getSchemeTitle() {
            return schemeTitle;
        }

        public void setSchemeTitle(String schemeTitle) {
            this.schemeTitle = schemeTitle;
        }

        public List<TeachingPlanTaskBackground> getTaskBackgrounds() {
            return taskBackgrounds;
        }

        public void setTaskBackgrounds(List<TeachingPlanTaskBackground> taskBackgrounds) {
            this.taskBackgrounds = taskBackgrounds;
        }

        public Map<Long, List<TeachingPlanTaskBackgroundRef>> getTaskBackgroundRefMap() {
            return taskBackgroundRefMap;
        }

        public void setTaskBackgroundRefMap(Map<Long, List<TeachingPlanTaskBackgroundRef>> taskBackgroundRefMap) {
            this.taskBackgroundRefMap = taskBackgroundRefMap;
        }
    }

    /**
     * 单个培养方案下的训练目的 + 支撑毕业要求。
     * 实践训练课目（type=2）Word「二、训练目的与支撑毕业要求」中每个 group 对应一张表。
     */
    public static class SchemeTrainingPurposeGroup {
        private Long schemeId;
        /** 展示标题，如培养方案名称（可带版本）；通识通用单组时为 null */
        private String schemeTitle;
        private List<TeachingPlanTrainingPurpose> purposes;
        private Map<Long, List<TeachingPlanTrainingPurposeRef>> purposeRefMap;

        public Long getSchemeId() {
            return schemeId;
        }

        public void setSchemeId(Long schemeId) {
            this.schemeId = schemeId;
        }

        public String getSchemeTitle() {
            return schemeTitle;
        }

        public void setSchemeTitle(String schemeTitle) {
            this.schemeTitle = schemeTitle;
        }

        public List<TeachingPlanTrainingPurpose> getPurposes() {
            return purposes;
        }

        public void setPurposes(List<TeachingPlanTrainingPurpose> purposes) {
            this.purposes = purposes;
        }

        public Map<Long, List<TeachingPlanTrainingPurposeRef>> getPurposeRefMap() {
            return purposeRefMap;
        }

        public void setPurposeRefMap(Map<Long, List<TeachingPlanTrainingPurposeRef>> purposeRefMap) {
            this.purposeRefMap = purposeRefMap;
        }
    }
}
