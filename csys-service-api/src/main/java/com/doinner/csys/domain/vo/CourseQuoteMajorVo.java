package com.doinner.csys.domain.vo;

/**
 * 课程被引用专业类VO。
 * <p>
 * 给定源课程id，通过 source_id 定位被各培养方案引用排课的课程实例(逻辑参照
 * {@code /chooseStatus/{sourceCourseId}})，按培养方案维度聚合出引用该课程的专业类，
 * 去重后每个专业类一行。门类/专业类取自排课所属培养方案(ts)的 category_id / major_id。
 *
 * @author doinner
 */
public class CourseQuoteMajorVo {

    /** 学科门类 -> t_csys_training_scheme_category.name */
    private String categoryName;

    /** 专业类 -> t_csys_std_major.name */
    private String majorName;

    /** 专业类ID -> t_csys_training_scheme.major_id */
    private Long majorId;

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getMajorName() {
        return majorName;
    }

    public void setMajorName(String majorName) {
        this.majorName = majorName;
    }

    public Long getMajorId() {
        return majorId;
    }

    public void setMajorId(Long majorId) {
        this.majorId = majorId;
    }
}
