package com.doinner.csys.domain.statisticsVo;

import com.doinner.csys.constant.DomainFieldConstant;
import org.apache.commons.lang3.ObjectUtils;

public class CourseTypeVo {

    private Integer termId;

    /** 修读性质为必修（course_attr=1）的课程数。 */
    private Long requiredCourseCount = 0L;

    /** 修读性质为限选或任选（course_attr in 2,3）的课程数。 */
    private Long electiveCourseCount = 0L;

    public Integer getTermId() {
        return termId;
    }

    public void setTermId(Integer termId) {
        this.termId = termId;
    }

    public Long getRequiredCourseCount() {
        return requiredCourseCount;
    }

    public void setRequiredCourseCount(Long requiredCourseCount) {
        this.requiredCourseCount = requiredCourseCount;
    }

    public Long getElectiveCourseCount() {
        return electiveCourseCount;
    }

    public void setElectiveCourseCount(Long electiveCourseCount) {
        this.electiveCourseCount = electiveCourseCount;
    }

    public String getTermName() {
        return ObjectUtils.isEmpty(this.getTermId())?null:DomainFieldConstant.TERM_NUMBER_NAME_MAP.get(this.getTermId());
    }

}
