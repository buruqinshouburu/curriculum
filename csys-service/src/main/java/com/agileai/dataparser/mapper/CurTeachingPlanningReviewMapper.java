package com.agileai.dataparser.mapper;

import com.agileai.dataparser.domain.CurTeachingPlanningReview;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface CurTeachingPlanningReviewMapper {
    Long insertCurTeachingPlanningReview(CurTeachingPlanningReview curTeachingPlanningReview);
    List<CurTeachingPlanningReview> selectCurTeachingPlanningReviews(Long mainId);
    CurTeachingPlanningReview selectCurTeachingPlanningReviewById(Long id);
}
