package com.doinner.csys.domain.statisticsVo;

/**
 * 培养方案内按课程模块（优先子模块）汇总的课程数量。
 */
public class CourseModuleStatisticsVo {

    /** 实际参与分组的模块字典 ID；优先 course_Module_Children。 */
    private String moduleId;

    /** 模块字典名称，字典不可用时回传 moduleId。 */
    private String moduleName;

    /** 该模块下的去重课程数。 */
    private Long courseCount = 0L;

    /** 当前培养方案内参与统计的去重课程总数。 */
    private Long totalCourseCount = 0L;

    public String getModuleId() {
        return moduleId;
    }

    public void setModuleId(String moduleId) {
        this.moduleId = moduleId;
    }

    public String getModuleName() {
        return moduleName;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    public Long getCourseCount() {
        return courseCount;
    }

    public void setCourseCount(Long courseCount) {
        this.courseCount = courseCount;
    }

    public Long getTotalCourseCount() {
        return totalCourseCount;
    }

    public void setTotalCourseCount(Long totalCourseCount) {
        this.totalCourseCount = totalCourseCount;
    }

    public Double getProportion() {
        if (totalCourseCount == null || totalCourseCount == 0L || courseCount == null) {
            return 0D;
        }
        return courseCount.doubleValue() / totalCourseCount.doubleValue();
    }
}
