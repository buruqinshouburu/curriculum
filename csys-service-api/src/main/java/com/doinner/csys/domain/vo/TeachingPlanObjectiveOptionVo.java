package com.doinner.csys.domain.vo;

/**
 * 教学计划目标选项（达成设计弹框用）。
 * 按目标内容字符串去重后返回，同名称只保留一条。
 */
public class TeachingPlanObjectiveOptionVo {

    /** 目标内容（去重键） */
    private String content;

    /** 目标类型字典编码 */
    private String objectiveTypeCode;

    /** 目标类型名称快照 */
    private String objectiveTypeName;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getObjectiveTypeCode() {
        return objectiveTypeCode;
    }

    public void setObjectiveTypeCode(String objectiveTypeCode) {
        this.objectiveTypeCode = objectiveTypeCode;
    }

    public String getObjectiveTypeName() {
        return objectiveTypeName;
    }

    public void setObjectiveTypeName(String objectiveTypeName) {
        this.objectiveTypeName = objectiveTypeName;
    }
}
