package com.doinner.csys.domain.vo;

import java.util.List;

public class TrainingSchemeKnowledgeUnitVo {

    /** 主键 */
    private Long id;

    /** 知识单元名称 */
    private String name;

    /** 课程id */
    private Long courseId;

    /** 培养标准 */
    private List<TrainingSchemeStandardCultivationVo> children;

    private boolean isRef;

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

    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

    public List<TrainingSchemeStandardCultivationVo> getChildren() {
        return children;
    }

    public void setChildren(List<TrainingSchemeStandardCultivationVo> children) {
        this.children = children;
    }

    public boolean isUnit() {
        return true;
    }

    public boolean isRef() {
        return !(children == null || children.size() == 0);
    }

    public void setRef(boolean ref) {
        isRef = ref;
    }
}
