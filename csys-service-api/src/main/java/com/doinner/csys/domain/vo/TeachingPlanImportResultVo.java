package com.doinner.csys.domain.vo;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 教学计划 Word 导入结果。
 * 覆盖式导入：成功计数 + 跳过/匹配失败问题列表。
 */
public class TeachingPlanImportResultVo {

    private Long planId;
    private Long courseId;
    /** 识别到的文档类型：1课程 2实践训练课目 3实验课程 4实践项目 */
    private Integer docType;
    /** 本次是否新建了教学计划主表 */
    private Boolean createdPlan;
    /** 各模块成功写入条数 */
    private Map<String, Integer> successCounts = new LinkedHashMap<>();
    /** 导入问题（匹配失败跳过等） */
    private List<TeachingPlanImportIssueVo> issues = new ArrayList<>();

    public Long getPlanId() {
        return planId;
    }

    public void setPlanId(Long planId) {
        this.planId = planId;
    }

    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

    public Integer getDocType() {
        return docType;
    }

    public void setDocType(Integer docType) {
        this.docType = docType;
    }

    public Boolean getCreatedPlan() {
        return createdPlan;
    }

    public void setCreatedPlan(Boolean createdPlan) {
        this.createdPlan = createdPlan;
    }

    public Map<String, Integer> getSuccessCounts() {
        return successCounts;
    }

    public void setSuccessCounts(Map<String, Integer> successCounts) {
        this.successCounts = successCounts;
    }

    public List<TeachingPlanImportIssueVo> getIssues() {
        return issues;
    }

    public void setIssues(List<TeachingPlanImportIssueVo> issues) {
        this.issues = issues;
    }

    public void addCount(String key, int delta) {
        if (successCounts == null) {
            successCounts = new LinkedHashMap<>();
        }
        successCounts.put(key, successCounts.getOrDefault(key, 0) + delta);
    }
}
