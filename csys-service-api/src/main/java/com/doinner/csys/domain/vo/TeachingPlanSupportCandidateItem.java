package com.doinner.csys.domain.vo;

/**
 * 实践项目第二节支撑绑定候选单条（type4）。
 * 统一承载课程目标 / 训练目的 / 知识体系 / 训练内容四类候选，用 name 展示、id 绑定。
 */
public class TeachingPlanSupportCandidateItem {

    /** 目标/目的/content 主键（绑定用） */
    private Long id;

    /** 展示文本：课程目标内容 / 训练目的文本 / content 名称 */
    private String name;

    /** 课程目标类型名称快照(知识/能力/素质目标)；训练目的、知识体系、训练内容为空 */
    private String typeName;

    /** 来源课程/课目ID（支撑课程 before_course_id / 支撑训练课目 after_course_id） */
    private Long sourceCourseId;

    /** 来源课程/课目名称 */
    private String sourceCourseName;

    /** 候选内容所属培养方案ID；通识通用为 null */
    private Long schemeId;

    /** 候选内容所属培养方案名称 */
    private String schemeName;

    /** 是否与项目同专业（仅课程目标有意义：目标 major_id == 项目首个培养方案 major_id） */
    private Boolean sameMajor;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public Long getSourceCourseId() {
        return sourceCourseId;
    }

    public void setSourceCourseId(Long sourceCourseId) {
        this.sourceCourseId = sourceCourseId;
    }

    public String getSourceCourseName() {
        return sourceCourseName;
    }

    public void setSourceCourseName(String sourceCourseName) {
        this.sourceCourseName = sourceCourseName;
    }

    public Long getSchemeId() {
        return schemeId;
    }

    public void setSchemeId(Long schemeId) {
        this.schemeId = schemeId;
    }

    public String getSchemeName() {
        return schemeName;
    }

    public void setSchemeName(String schemeName) {
        this.schemeName = schemeName;
    }

    public Boolean getSameMajor() {
        return sameMajor;
    }

    public void setSameMajor(Boolean sameMajor) {
        this.sameMajor = sameMajor;
    }
}
