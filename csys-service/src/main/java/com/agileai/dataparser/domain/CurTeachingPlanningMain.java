package com.agileai.dataparser.domain;


import com.doinner.common.core.annotation.Excel;
import com.doinner.common.core.web.domain.BaseEntity;
import org.springframework.data.annotation.AccessType;
import org.springframework.data.annotation.Transient;

import java.util.ArrayList;
import java.util.List;

public class CurTeachingPlanningMain extends BaseEntity {

    /** 主键 */
//    @Excel(name = "主键id")
    private Long id;

    /** 课程名称 */
    @Excel(name = "课程名称")
    private String curName;

    /** 课程编号 */
    @Excel(name = "课程编号")
    private String curCode;

    /** 审阅学院 */
    @Excel(name = "开课单位")
    private String reviewCollege;

    /** 课程类型：1，理论；2，实践 */
//    @Excel(name = "课程类型")
    @AccessType(AccessType.Type.PROPERTY)
    private String curType;

    /** 课程类型名称 */
    @Transient
    @Excel(name = "课程类别")
    @AccessType(AccessType.Type.PROPERTY)
    private String curTypeName;

    /** 课程性质 */
    @Excel(name = "课程性质")
    private String nature;

    /** 课程大类：1，理论教学类；2，实验教学类；3，体育课；4，军体野外教学类；5，实践环节；6，毕业实习；7，素质课；8，外语教学类；9，毕业设计；10，出国成绩 */
    @Excel(name = "课程大类")
    private String curCategory;

    /** 课程属性：1，必修；2，选修；3，公选；4，限选 */
    @Excel(name = "课程属性")
    private String curAttribute;

    /** 学分 */
    @Excel(name = "学分")
    private String score;

    /** 学时单位 */
    @Excel(name = "学时单位")
    private String hoursUnit;

    /** 学时 */
    @Excel(name = "总学时")
    private String hours;

    /** 周学时 */
    @Excel(name = "周学时")
    private String hoursWeek;

    @Transient
    @Excel(name = "版本号")
    private String version;

    /** 考核方式 */
    @Transient
    @Excel(name = "考核方式")
    private String checkType;

    @Transient
    @Excel(name = "是否为网络课（是/否）")
    private String onlineCourse;

    @Transient
    @Excel(name = "通选课类别")
    private String allCourseType;

    /** 英文名称 */
    @Excel(name = "课程英文名称")
    private String curNameEn;

    @Transient
    @Excel(name = "课程层次")
    private String arrangement;

    @Transient
    @Excel(name = "课程分类")
    private String courseClazz;

    @Transient
    @Excel(name = "课程简称")
    private String abbr;

    @Transient
    @Excel(name = "对外课程中文名称")
    private String foreignCH;

    @Transient
    @Excel(name = "对外课程英文名称")
    private String foreignEN;

    @Transient
    @Excel(name = "是否可删教材信息")
    private String textbookInfo;

    /** 授课学时 */
    @Excel(name = "讲授学时")
    private String hoursTeach;

    @Transient
    @Excel(name = "讲授周学时")
    private String teachWeekHour;

    /** 实践学时 */
    @Excel(name = "实践学时")
    @AccessType(AccessType.Type.PROPERTY)
    private String hoursPa;

    @Transient
    @Excel(name = "实践周学时")
    private String practiceWeekHour;

    /** 考核学时 */
    @Excel(name = "考核学时")
    private String hoursExam;

    @Transient
    @Excel(name = "考核周学时")
    private String checkWeekHour;

    /** 其他学时 */
    @Excel(name = "其他学时")
    private String hoursOther;

    @Transient
    @Excel(name = "其它周学时")
    private String otherWeekHour;

    /** 理论学时 */
    @Excel(name = "理论学时")
    @AccessType(AccessType.Type.PROPERTY)
    private String hoursTh;

    /** 开课学期 */
    @Excel(name = "开课学期")
    private String term;

    /** 执笔人 */
    @Excel(name = "执笔人")
    private String writingName;

    /** 所属学院 */
//    @Excel(name = "所属学院")
    private String college;

    /** 预修课程 */
//    @Excel(name = "预修课程")
    @AccessType(AccessType.Type.PROPERTY)
    private String curPre;

    @Transient
    @Excel(name = "预修课程")
    private List<String> curPreNames = new ArrayList<>();

    /** 后续课程 */
//    @Excel(name = "后续课程")
    @AccessType(AccessType.Type.PROPERTY)
    private String curFollow;

    @Transient
    @Excel(name = "后续课程")
    private List<String> curFollowNames = new ArrayList<>();

    /** 内容简介 */
//    @Excel(name = "内容简介")
    private String brief;

    /** 文档id */
//    @Excel(name = "文档id")
    private String fileId;

    /** 文档名称 */
//    @Excel(name = "文档名称")
    private String fileName;

    /** 审核状态 */
//    @Excel(name = "审核状态")
    private Boolean review;

//    @Excel(name = "审核状态")
    private String status;

    /** 分析状态 */
    private String analysisStatus;

    public String getAnalysisStatus() {
        return analysisStatus;
    }

    public void setAnalysisStatus(String analysisStatus) {
        this.analysisStatus = analysisStatus;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCurName() {
        return curName;
    }

    public void setCurName(String curName) {
        this.curName = curName;
    }

    public String getCurType() {
        return curType;
    }

    public void setCurType(String curType) {
//        if ("2".equals(curType)){
//            this.curTypeName = "实践";
//        }else {
//            this.curTypeName = "理论";
//        }
        this.curType = curType;
    }

    public String getWritingName() {
        return writingName;
    }

    public void setWritingName(String writingName) {
        this.writingName = writingName;
    }

    public String getReviewCollege() {
        return reviewCollege;
    }

    public void setReviewCollege(String reviewCollege) {
        this.reviewCollege = reviewCollege;
    }

    public String getCollege() {
        return college;
    }

    public void setCollege(String college) {
        this.college = college;
    }

    public String getCurCode() {
        return curCode;
    }

    public void setCurCode(String curCode) {
        this.curCode = curCode;
    }

    public String getCurNameEn() {
        return curNameEn;
    }

    public void setCurNameEn(String curNameEn) {
        this.curNameEn = curNameEn;
    }

    public String getCurPre() {
        return curPre;
    }

    public void setCurPre(String curPre) {
//        if (null != curPre){
//            List<String> curPreNames = new ArrayList<>();
//            for (String curId:curPre.split(",|，")){
//                String name = OntologyInstanceUtils.getOntologyInstanceCache(curId);
//                if (null == name || "".equals(name)){
//                    continue;
//                }
//                curPreNames.add(name);
//            }
//            this.curPreNames = curPreNames;
//        }
        this.curPre = curPre;
    }

    public List<String> getCurPreNames() {
//        if (null == this.curPre){
//            return null;
//        }
//        for (String curId:this.curPre.split(",|，")){
//            String name = OntologyInstanceUtils.getOntologyInstanceCache(curId);
//            if (null == name || "".equals(name)){
//                continue;
//            }
//            curPreNames.add(name);
//        }
        return curPreNames;
    }

    public void setCurPreNames(List<String> curPreNames) {
        this.curPreNames = curPreNames;
    }

    public String getCurFollow() {
        return curFollow;
    }

    public void setCurFollow(String curFollow) {
//        if (null != curFollow){
//            List<String> curFollowNames = new ArrayList<>();
//            for (String curId:curFollow.split(",|，")){
//                String name = OntologyInstanceUtils.getOntologyInstanceCache(curId);
//                if (null == name || "".equals(name)){
//                    continue;
//                }
//                curFollowNames.add(name);
//            }
//            this.curFollowNames = curFollowNames;
//        }
        this.curFollow = curFollow;
    }

    public List<String> getCurFollowNames() {
//        if (null == this.curFollow){
//            return null;
//        }
//        for (String curId:this.curFollow.split(",|，")){
//            String name = OntologyInstanceUtils.getOntologyInstanceCache(curId);
//            if (null == name || "".equals(name)){
//                continue;
//            }
//            curFollowNames.add(name);
//        }
        return curFollowNames;
    }

    public void setCurFollowNames(List<String> curFollowNames) {
        this.curFollowNames = curFollowNames;
    }

    public String getHours() {
        return hours;
    }

    public void setHours(String hours) {
        this.hours = hours;
    }

    public String getHoursTh() {
        return hoursTh;
    }

    public void setHoursTh(String hoursTh) {
        this.hoursTh = hoursTh;
    }

    public String getHoursPa() {
        return hoursPa;
    }

    public void setHoursPa(String hoursPa) {
        this.hoursPa = hoursPa;
    }

    public String getCurTypeName() {
        return curTypeName;
    }

    public void setCurTypeName(String curTypeName) {
        this.curTypeName = curTypeName;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public String getNature() {
        return nature;
    }

    public void setNature(String nature) {
        this.nature = nature;
    }

    public String getTerm() {
        return term;
    }

    public void setTerm(String term) {
        this.term = term;
    }

    public String getBrief() {
        return brief;
    }

    public void setBrief(String brief) {
        this.brief = brief;
    }

    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Boolean getReview() {
        return review;
    }

    public void setReview(Boolean review) {
        this.review = review;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
    public String getCurCategory() {
        return curCategory;
    }

    public void setCurCategory(String curCategory) {
        this.curCategory = curCategory;
    }

    public String getCurAttribute() {
        return curAttribute;
    }

    public void setCurAttribute(String curAttribute) {
        this.curAttribute = curAttribute;
    }

    public String getHoursWeek() {
        return hoursWeek;
    }

    public void setHoursWeek(String hoursWeek) {
        this.hoursWeek = hoursWeek;
    }

    public String getHoursTeach() {
        return hoursTeach;
    }

    public void setHoursTeach(String hoursTeach) {
        this.hoursTeach = hoursTeach;
    }

    public String getHoursExam() {
        return hoursExam;
    }

    public void setHoursExam(String hoursExam) {
        this.hoursExam = hoursExam;
    }

    public String getCheckType() {
        return checkType;
    }

    public void setCheckType(String checkType) {
        this.checkType = checkType;
    }

    public String getHoursOther() {
        return hoursOther;
    }

    public void setHoursOther(String hoursOther) {
        this.hoursOther = hoursOther;
    }

    public String getHoursUnit() {
        return hoursUnit;
    }

    public void setHoursUnit(String hoursUnit) {
        this.hoursUnit = hoursUnit;
    }

}
