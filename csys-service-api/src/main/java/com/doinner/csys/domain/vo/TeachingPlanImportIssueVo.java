package com.doinner.csys.domain.vo;

/**
 * 教学计划 Word 导入单条问题。
 */
public class TeachingPlanImportIssueVo {

    /** 章节/模块，如「四、课程目标」 */
    private String section;

    /** 行定位提示，如「第3行」或目标内容摘要 */
    private String rowHint;

    /** 字段名 */
    private String field;

    /** 问题说明 */
    private String message;

    /** WARN=跳过该条继续；ERROR=关键路径失败，整单不落库 */
    private String severity;

    public TeachingPlanImportIssueVo() {
    }

    public TeachingPlanImportIssueVo(String section, String rowHint, String field, String message, String severity) {
        this.section = section;
        this.rowHint = rowHint;
        this.field = field;
        this.message = message;
        this.severity = severity;
    }

    public static TeachingPlanImportIssueVo warn(String section, String rowHint, String field, String message) {
        return new TeachingPlanImportIssueVo(section, rowHint, field, message, "WARN");
    }

    public static TeachingPlanImportIssueVo error(String section, String rowHint, String field, String message) {
        return new TeachingPlanImportIssueVo(section, rowHint, field, message, "ERROR");
    }

    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }

    public String getRowHint() {
        return rowHint;
    }

    public void setRowHint(String rowHint) {
        this.rowHint = rowHint;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSeverity() {
        return severity;
    }

    public void setSeverity(String severity) {
        this.severity = severity;
    }
}
