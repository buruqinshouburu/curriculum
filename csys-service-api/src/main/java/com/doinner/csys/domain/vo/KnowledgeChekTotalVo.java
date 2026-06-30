package com.doinner.csys.domain.vo;

import com.doinner.common.core.domain.DataTable;
import com.doinner.csys.domain.KnowledgeCheckLog;
import com.doinner.csys.domain.PageDataTable;

import java.util.List;

public class KnowledgeChekTotalVo {
    //总知识单元
    private Long totalUnitNum;
    //总知识点
    private Long totalPointNum;
    //总课程数
    private Integer totalCurriculumNum;
    //相似数量
    private Long repeatNum;
    //list
    private DataTable<KnowledgeCheckLog> artificialCheckResultList;

    public Long getTotalUnitNum() {
        return totalUnitNum;
    }

    public void setTotalUnitNum(Long totalUnitNum) {
        this.totalUnitNum = totalUnitNum;
    }

    public Long getTotalPointNum() {
        return totalPointNum;
    }

    public void setTotalPointNum(Long totalPointNum) {
        this.totalPointNum = totalPointNum;
    }

    public Integer getTotalCurriculumNum() {
        return totalCurriculumNum;
    }

    public void setTotalCurriculumNum(Integer totalCurriculumNum) {
        this.totalCurriculumNum = totalCurriculumNum;
    }

    public Long getRepeatNum() {
        return repeatNum;
    }

    public void setRepeatNum(Long repeatNum) {
        this.repeatNum = repeatNum;
    }

    public DataTable<KnowledgeCheckLog> getArtificialCheckResultList() {
        return artificialCheckResultList;
    }

    public void setArtificialCheckResultList(DataTable<KnowledgeCheckLog> artificialCheckResultList) {
        this.artificialCheckResultList = artificialCheckResultList;
    }
}
