package com.doinner.csys.dao;

import com.doinner.csys.domain.TeachingPlanObjectiveAssessment;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface TeachingPlanObjectiveAssessmentMapper {
    int insertBatch(@Param("list") List<TeachingPlanObjectiveAssessment> list);
    List<TeachingPlanObjectiveAssessment> selectByPlanAndScheme(@Param("planId") Long planId, @Param("schemeId") Long schemeId);
    int deleteByPlanAndScheme(@Param("planId") Long planId, @Param("schemeId") Long schemeId);
}
