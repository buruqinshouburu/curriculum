package com.doinner.csys.domain;

import com.doinner.common.core.annotation.Excel;
import com.doinner.common.core.domain.db.AbstractDoinnerLogicalDelBaseEntity;
import com.doinner.csys.domain.vo.MajorVo;

import java.util.List;

/**
 * 教育大纲从文档抽取内容对象 t_csys_teaching_programme_instance_extract
 *
 * @author wzg
 * @date 2026-03-11
 */
public class TeachingProgrammeInstanceExtract extends AbstractDoinnerLogicalDelBaseEntity{
    private static final long serialVersionUID=1L;

    /** id */
    private Long id;

    /** t_csys_teaching_programme_instance表id */
            @Excel(name = "t_csys_teaching_programme_instance表id")
    private Long instanceId;
    private String instanceName;
    private String instanceVersion;
    private Long categoryId;
    private String categoryName;

    /** 学制 */
            @Excel(name = "学制")
    private String learnInstitution;

    /** 授予学位 */
            @Excel(name = "授予学位")
    private String degree;

    /** 总学时 */
            @Excel(name = "总学时")
    private String totalHour;

    /** 总学分 */
            @Excel(name = "总学分")
    private String totalCredit;

    /** 上下浮动比例 */
            @Excel(name = "上下浮动比例")
    private String floatRate;

    /** 政治理论模块学时 */
            @Excel(name = "政治理论模块学时")
    private String politicalHour;

    /** 军事基础模块学时 */
            @Excel(name = "军事基础模块学时")
    private String militaryHour;

    /** 科学文化模块学时 */
            @Excel(name = "科学文化模块学时")
    private String scienceHour;

    /** 学科基础模块学时 */
            @Excel(name = "学科基础模块学时")
    private String basicsHour;

    /** 抽取状态 0 默认 1 ai抽取中 2 抽取成功 9 抽取失败   */
            @Excel(name = "抽取状态")
    private Integer status;

    private Long collegeId;

    // 专业集合
    private List<Long> majorIds;

    private List<MajorVo> majorVos;

    private Integer pageNum;
    private Integer pageSize;

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public Long getInstanceId() {
        return instanceId;
    }

    public void setInstanceId(Long instanceId) {
        this.instanceId = instanceId;
    }

    public String getLearnInstitution() {
        return learnInstitution;
    }

    public void setLearnInstitution(String learnInstitution) {
        this.learnInstitution = learnInstitution;
    }

    public String getDegree() {
        return degree;
    }

    public void setDegree(String degree) {
        this.degree = degree;
    }

    public String getTotalHour() {
        return totalHour;
    }

    public void setTotalHour(String totalHour) {
        this.totalHour = totalHour;
    }

    public String getTotalCredit() {
        return totalCredit;
    }

    public void setTotalCredit(String totalCredit) {
        this.totalCredit = totalCredit;
    }

    public String getFloatRate() {
        return floatRate;
    }

    public void setFloatRate(String floatRate) {
        this.floatRate = floatRate;
    }

    public String getPoliticalHour() {
        return politicalHour;
    }

    public void setPoliticalHour(String politicalHour) {
        this.politicalHour = politicalHour;
    }

    public String getMilitaryHour() {
        return militaryHour;
    }

    public void setMilitaryHour(String militaryHour) {
        this.militaryHour = militaryHour;
    }

    public String getScienceHour() {
        return scienceHour;
    }

    public void setScienceHour(String scienceHour) {
        this.scienceHour = scienceHour;
    }

    public String getBasicsHour() {
        return basicsHour;
    }

    public void setBasicsHour(String basicsHour) {
        this.basicsHour = basicsHour;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public List<Long> getMajorIds() {
        return majorIds;
    }

    public void setMajorIds(List<Long> majorIds) {
        this.majorIds = majorIds;
    }

    public String getInstanceName() {
        return instanceName;
    }

    public void setInstanceName(String instanceName) {
        this.instanceName = instanceName;
    }

    public String getInstanceVersion() {
        return instanceVersion;
    }

    public void setInstanceVersion(String instanceVersion) {
        this.instanceVersion = instanceVersion;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public List<MajorVo> getMajorVos() {
        return majorVos;
    }

    public void setMajorVos(List<MajorVo> majorVos) {
        this.majorVos = majorVos;
    }

    public Integer getPageNum() {
        return pageNum;
    }

    public void setPageNum(Integer pageNum) {
        this.pageNum = pageNum;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public Long getCollegeId() {
        return collegeId;
    }

    public void setCollegeId(Long collegeId) {
        this.collegeId = collegeId;
    }
}
