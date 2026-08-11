package com.doinner.csys.domain.vo;

import java.util.ArrayList;
import java.util.List;

/**
 * 实践项目第二节支撑绑定候选数据（type4）。
 * <p>
 * 候选来源 = 项目支撑课程(源课 before_course_id) 与 支撑训练课目(源课 after_course_id) 各自教学计划：
 * <ul>
 *   <li>{@link #objectives}：支撑课程课程教学计划第四部分课程目标，同专业（与项目首个培养方案 major_id 一致）排前；</li>
 *   <li>{@link #purposes}：支撑训练课目第二部分训练目的；</li>
 *   <li>{@link #knowledgePoints}：支撑课程教学计划 content 全部内容行（知识单元知识点，含专题与知识点）；</li>
 *   <li>{@link #trainingContents}：支撑训练课目第四部分训练内容。</li>
 * </ul>
 */
public class TeachingPlanSupportCandidateVo {

    /** 课程目标候选（同专业优先） */
    private List<TeachingPlanSupportCandidateItem> objectives = new ArrayList<>();

    /** 训练目的候选 */
    private List<TeachingPlanSupportCandidateItem> purposes = new ArrayList<>();

    /** 知识体系候选（支撑课程 content 全部行） */
    private List<TeachingPlanSupportCandidateItem> knowledgePoints = new ArrayList<>();

    /** 训练内容候选（支撑训练课目 content） */
    private List<TeachingPlanSupportCandidateItem> trainingContents = new ArrayList<>();

    public List<TeachingPlanSupportCandidateItem> getObjectives() {
        return objectives;
    }

    public void setObjectives(List<TeachingPlanSupportCandidateItem> objectives) {
        this.objectives = objectives;
    }

    public List<TeachingPlanSupportCandidateItem> getPurposes() {
        return purposes;
    }

    public void setPurposes(List<TeachingPlanSupportCandidateItem> purposes) {
        this.purposes = purposes;
    }

    public List<TeachingPlanSupportCandidateItem> getKnowledgePoints() {
        return knowledgePoints;
    }

    public void setKnowledgePoints(List<TeachingPlanSupportCandidateItem> knowledgePoints) {
        this.knowledgePoints = knowledgePoints;
    }

    public List<TeachingPlanSupportCandidateItem> getTrainingContents() {
        return trainingContents;
    }

    public void setTrainingContents(List<TeachingPlanSupportCandidateItem> trainingContents) {
        this.trainingContents = trainingContents;
    }
}
