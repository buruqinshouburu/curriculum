package com.doinner.csys.domain.vo;

import java.util.ArrayList;
import java.util.List;

/** 实践项目支撑候选的培养方案分组。 */
public class TeachingPlanSupportCandidateGroupVo {
    private Long schemeId;
    private String schemeName;
    private String schemeVersion;
    private Boolean sameScheme;
    private List<TeachingPlanSupportCandidateItem> objectives = new ArrayList<>();
    private List<TeachingPlanSupportCandidateItem> purposes = new ArrayList<>();
    private List<TeachingPlanSupportCandidateItem> knowledgePoints = new ArrayList<>();
    private List<TeachingPlanSupportCandidateItem> trainingContents = new ArrayList<>();

    public Long getSchemeId() { return schemeId; }
    public void setSchemeId(Long schemeId) { this.schemeId = schemeId; }
    public String getSchemeName() { return schemeName; }
    public void setSchemeName(String schemeName) { this.schemeName = schemeName; }
    public String getSchemeVersion() { return schemeVersion; }
    public void setSchemeVersion(String schemeVersion) { this.schemeVersion = schemeVersion; }
    public Boolean getSameScheme() { return sameScheme; }
    public void setSameScheme(Boolean sameScheme) { this.sameScheme = sameScheme; }
    public List<TeachingPlanSupportCandidateItem> getObjectives() { return objectives; }
    public void setObjectives(List<TeachingPlanSupportCandidateItem> objectives) { this.objectives = objectives; }
    public List<TeachingPlanSupportCandidateItem> getPurposes() { return purposes; }
    public void setPurposes(List<TeachingPlanSupportCandidateItem> purposes) { this.purposes = purposes; }
    public List<TeachingPlanSupportCandidateItem> getKnowledgePoints() { return knowledgePoints; }
    public void setKnowledgePoints(List<TeachingPlanSupportCandidateItem> knowledgePoints) { this.knowledgePoints = knowledgePoints; }
    public List<TeachingPlanSupportCandidateItem> getTrainingContents() { return trainingContents; }
    public void setTrainingContents(List<TeachingPlanSupportCandidateItem> trainingContents) { this.trainingContents = trainingContents; }
}
