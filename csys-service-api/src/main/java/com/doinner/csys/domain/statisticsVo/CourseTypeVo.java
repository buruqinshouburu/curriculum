package com.doinner.csys.domain.statisticsVo;

import com.doinner.csys.constant.DomainFieldConstant;
import org.apache.commons.lang3.ObjectUtils;

public class CourseTypeVo {

    private Integer termId;

    private String termName;

    private Long publicRequiredCourseCount = 0l;

    private Long subjectRequiredCourseCount = 0l;

    private Long specialityRequiredCourseCount = 0l;

    private Long publicElectiveCourseCount = 0l;

    private Long subjectElectiveCourseCount = 0l;

    public Integer getTermId() {
        return termId;
    }

    public void setTermId(Integer termId) {
        this.termId = termId;
    }

    public void setPublicRequiredCourseCount(Long publicRequiredCourseCount) {
        this.publicRequiredCourseCount = publicRequiredCourseCount;
    }

    public void setSubjectRequiredCourseCount(Long subjectRequiredCourseCount) {
        this.subjectRequiredCourseCount = subjectRequiredCourseCount;
    }

    public void setSpecialityRequiredCourseCount(Long specialityRequiredCourseCount) {
        this.specialityRequiredCourseCount = specialityRequiredCourseCount;
    }

    public void setPublicElectiveCourseCount(Long publicElectiveCourseCount) {
        this.publicElectiveCourseCount = publicElectiveCourseCount;
    }

    public void setSubjectElectiveCourseCount(Long subjectElectiveCourseCount) {
        this.subjectElectiveCourseCount = subjectElectiveCourseCount;
    }

    public Long getRequiredCourseCount() {
        return publicRequiredCourseCount + subjectRequiredCourseCount + specialityRequiredCourseCount;
    }

    public Long getElectiveCourseCount() {
        return publicElectiveCourseCount + subjectElectiveCourseCount;
    }

    public String getTermName() {
        return ObjectUtils.isEmpty(this.getTermId())?null:DomainFieldConstant.TERM_NUMBER_NAME_MAP.get(this.getTermId());
    }

    public void setTermName(String termName) {
        this.termName = termName;
    }

}
