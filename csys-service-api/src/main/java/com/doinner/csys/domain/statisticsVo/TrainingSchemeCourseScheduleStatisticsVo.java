package com.doinner.csys.domain.statisticsVo;

public class TrainingSchemeCourseScheduleStatisticsVo {

    private Long schemeId;
    private Long sTheoryHours;
    private Long sTeachHours;
    private Long sPracticeHours;
    private Long courseId;
    private Long cTheoryHours =0L;
    private Long cPracticeHours=0L;
    private Long term;
    private String termName;

    public String getTermName() {
        return termName;
    }

    public void setTermName(String termName) {
        this.termName = termName;
    }

    public Long getSchemeId() {
        return schemeId;
    }

    public void setSchemeId(Long schemeId) {
        this.schemeId = schemeId;
    }

    public Long getsTheoryHours() {
        return sTheoryHours;
    }

    public void setsTheoryHours(Long sTheoryHours) {
        this.sTheoryHours = sTheoryHours;
    }

    public Long getsPracticeHours() {
        return sPracticeHours;
    }

    public void setsPracticeHours(Long sPracticeHours) {
        this.sPracticeHours = sPracticeHours;
    }

    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

    public Long getcTheoryHours() {
        return cTheoryHours;
    }

    public void setcTheoryHours(Long cTheoryHours) {
        this.cTheoryHours = cTheoryHours;
    }

    public Long getcPracticeHours() {
        return cPracticeHours;
    }

    public void setcPracticeHours(Long cPracticeHours) {
        this.cPracticeHours = cPracticeHours;
    }

    public Long getTerm() {
        return term;
    }

    public void setTerm(Long term) {
        this.term = term;
    }

    public Long getsTeachHours() {
        return sTeachHours;
    }

    public void setsTeachHours(Long sTeachHours) {
        this.sTeachHours = sTeachHours;
    }
}
