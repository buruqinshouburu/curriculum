package com.doinner.csys.domain.vo;

import com.doinner.csys.domain.KnowledgeUnitRefStdCultivation;
import com.doinner.csys.domain.StandardCultivationRefGraduation;
import com.doinner.csys.domain.StandardGraduationRefCultivationTarget;

public class ExcelRelationshipVo {

    private String verticalNodeId;

    private String horizontalNodeId;

    public String getVerticalNodeId() {
        return verticalNodeId;
    }

    public void setVerticalNodeId(String verticalNodeId) {
        this.verticalNodeId = verticalNodeId;
    }

    public String getHorizontalNodeId() {
        return horizontalNodeId;
    }

    public void setHorizontalNodeId(String horizontalNodeId) {
        this.horizontalNodeId = horizontalNodeId;
    }

    public ExcelRelationshipVo() {
    }

    public ExcelRelationshipVo(StandardGraduationRefCultivationTarget standardGraduationRefCultivationTarget) {
        this.verticalNodeId = standardGraduationRefCultivationTarget.getGraduationId().toString();
        this.horizontalNodeId = standardGraduationRefCultivationTarget.getCultivationTargetId().toString();
    }

    public ExcelRelationshipVo(StandardCultivationRefGraduation standardCultivationRefGraduation) {
        this.verticalNodeId = standardCultivationRefGraduation.getCultivationId().toString();
        this.horizontalNodeId = standardCultivationRefGraduation.getGraduationId().toString();
    }

    public ExcelRelationshipVo(KnowledgeUnitRefStdCultivation knowledgeUnitRefStdCultivation) {
        this.verticalNodeId = knowledgeUnitRefStdCultivation.getCultivationId().toString();
        this.horizontalNodeId = "课程" + knowledgeUnitRefStdCultivation.getCourseId().toString();
    }
}
