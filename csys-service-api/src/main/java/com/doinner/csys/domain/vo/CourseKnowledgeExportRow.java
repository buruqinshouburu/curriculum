package com.doinner.csys.domain.vo;

/**
 * 知识单元知识点导出行: 课程名称 / 知识单元 / 知识点
 * <p>
 * 由 course -> ref -> unit -> point 一条 join SQL 展开为扁平行,
 * courseId/unitId 作为单元格合并的分组键。
 */
public class CourseKnowledgeExportRow {

    /** 课程ID(合并分组键) */
    private Long courseId;
    /** 课程名称 */
    private String courseName;
    /** 知识单元ID(合并分组键) */
    private Long unitId;
    /** 知识单元名称 */
    private String unitName;
    /** 知识点ID */
    private Long pointId;
    /** 知识点名称 */
    private String pointName;

    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public Long getUnitId() {
        return unitId;
    }

    public void setUnitId(Long unitId) {
        this.unitId = unitId;
    }

    public String getUnitName() {
        return unitName;
    }

    public void setUnitName(String unitName) {
        this.unitName = unitName;
    }

    public Long getPointId() {
        return pointId;
    }

    public void setPointId(Long pointId) {
        this.pointId = pointId;
    }

    public String getPointName() {
        return pointName;
    }

    public void setPointName(String pointName) {
        this.pointName = pointName;
    }
}
