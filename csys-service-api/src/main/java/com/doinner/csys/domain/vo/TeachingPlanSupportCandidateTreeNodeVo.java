package com.doinner.csys.domain.vo;

import java.util.ArrayList;
import java.util.List;

/**
 * 实践项目第二部分支撑候选树节点。
 *
 * <p>固定返回三层：支撑课程/训练课目 -> 培养方案/通识通用 -> 可绑定条目。</p>
 */
public class TeachingPlanSupportCandidateTreeNodeVo {

    /** 前端树节点唯一键。 */
    private String key;

    /** 当前节点业务主键：根节点为课程ID，方案节点为方案ID，叶子节点为目标/目的/content ID。 */
    private Long id;

    /** 节点展示名称。 */
    private String name;

    /** course/trainingSubject/scheme/objective/purpose/knowledgeSystem/trainingContent。 */
    private String nodeType;

    /** 1支撑课程，2支撑训练课目。 */
    private Integer refType;

    /** 当前分支所属的支撑课程或训练课目ID。 */
    private Long courseId;

    /** 当前分支培养方案ID；通识通用为 null。 */
    private Long schemeId;

    /** 培养方案版本；通识通用为空。 */
    private String schemeVersion;

    /** 课程目标类型名称；其他节点为空。 */
    private String typeName;

    /** 只有第三层叶子节点可选择。 */
    private Boolean selectable;

    /** 子节点。 */
    private List<TeachingPlanSupportCandidateTreeNodeVo> children = new ArrayList<>();

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

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

    public String getNodeType() {
        return nodeType;
    }

    public void setNodeType(String nodeType) {
        this.nodeType = nodeType;
    }

    public Integer getRefType() {
        return refType;
    }

    public void setRefType(Integer refType) {
        this.refType = refType;
    }

    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

    public Long getSchemeId() {
        return schemeId;
    }

    public void setSchemeId(Long schemeId) {
        this.schemeId = schemeId;
    }

    public String getSchemeVersion() {
        return schemeVersion;
    }

    public void setSchemeVersion(String schemeVersion) {
        this.schemeVersion = schemeVersion;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public Boolean getSelectable() {
        return selectable;
    }

    public void setSelectable(Boolean selectable) {
        this.selectable = selectable;
    }

    public List<TeachingPlanSupportCandidateTreeNodeVo> getChildren() {
        return children;
    }

    public void setChildren(List<TeachingPlanSupportCandidateTreeNodeVo> children) {
        this.children = children;
    }
}
