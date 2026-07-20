package com.doinner.csys.domain.vo;

/**
 * 教学计划编辑页「培养方案 tab」一行。
 * 由源课被引用链路实时聚合：源课 -> 调用课 c2 -> 排课 tcs -> 培养方案 ts。
 * 同一培养方案下源课被引用多次（多学期/多排课）只占一个 tab。
 */
public class TeachingPlanSchemeVo {

    /** 培养方案ID */
    private Long schemeId;

    /** 培养方案名称 */
    private String schemeName;

    /** 培养方案版本 */
    private String schemeVersion;

    /** 适用对象/培养层次 */
    private String educationLevel;

    /** 专业类ID */
    private Long majorId;

    /** 专业类名称 */
    private String majorName;

    /** 该方案下引用本源课的调用课数量（同一课被引用多次会 >1） */
    private Integer quoteCourseCount;

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

    public String getSchemeVersion() {
        return schemeVersion;
    }

    public void setSchemeVersion(String schemeVersion) {
        this.schemeVersion = schemeVersion;
    }

    public String getEducationLevel() {
        return educationLevel;
    }

    public void setEducationLevel(String educationLevel) {
        this.educationLevel = educationLevel;
    }

    public Long getMajorId() {
        return majorId;
    }

    public void setMajorId(Long majorId) {
        this.majorId = majorId;
    }

    public String getMajorName() {
        return majorName;
    }

    public void setMajorName(String majorName) {
        this.majorName = majorName;
    }

    public Integer getQuoteCourseCount() {
        return quoteCourseCount;
    }

    public void setQuoteCourseCount(Integer quoteCourseCount) {
        this.quoteCourseCount = quoteCourseCount;
    }
}
