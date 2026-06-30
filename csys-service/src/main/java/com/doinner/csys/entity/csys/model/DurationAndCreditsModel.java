package com.doinner.csys.entity.csys.model;

import com.doinner.common.core.annotation.Excel;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.poi.xwpf.usermodel.XWPFTable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DurationAndCreditsModel {
    private String firstLevelTitle1="（一）修业时间安排";
    private String firstLevelContent1="在校总时间约203周，其中入学入伍教育训练约6周、4个寒假约16周、2个暑假约8周、教学训练安排不少于164周，安排约130周课程教学（不含法定节假日）和约34周实践训练。具体教学训练及寒暑假安排根据学校教学周历确定。";
    private String firstLevelTitle2="（二）学时学分要求";
    private String firstLevelContent2;
    private String firstLevelTitle3="（三）学分冲抵机制";
    private String firstLevelContent3="1.学员修读新生研讨课所获学分可冲抵通识任选课程学分，新生研讨课教学安排见年度选课通知。\n" +
            "2.学员修读军事职业教育平台、学校智课平台在线课程，按照每20学时在线课时冲抵1学分任选课程。\n" +
            "3.学员在校期间参加学科竞赛、科技创新、文化活动、军事比武、运动会、俱乐部等实践活动并获奖、发表学术论文或取得专利，申报并完成创新实践项目或自主设计并完成创新实验，参加国际或国家组织的各类正规专业性资格认证或水平考试达到一定成绩等，可根据学校有关规定凭获奖证书冲抵选修实践训练学分。学科竞赛列表详见学校年度学科竞赛计划。";
    @Excel(name = "学制")
    private String learnInstitution;

    /** 授予学位 */
    @Excel(name = "授予学位")
    private String degree;

    /** 总学时 */
    @Excel(name = "总学时")
    private Double totalHour;
    /**
     * 必修
     */
    private Double requiredHour;
    /**
     * 选修
     */
    private Double optionalHour;

    /** 总学分 */
    @Excel(name = "总学分")
    private Double totalCredit;
    private Double requiredCredit;
    private Double optionalCredit;

    /**
     * 实践周
     */
    private Double projectTime;
    /**
     * 必修实践周
     */
    private Double requireTime;
    /**
     * 选修实践周
     */
    private Double optionalTime;

    /** 上下浮动比例 */
    @Excel(name = "上下浮动比例")
    private String floatRate;

    /** 政治理论模块学时 */
    @Excel(name = "政治理论模块学时")
    private String politicalHour;

    /** 军事基础模块学时 */
    @Excel(name = "军事基础模块学时")
    private String militaryHour;

    /** 科学文化模块学时 */
    @Excel(name = "科学文化模块学时")
    private String scienceHour;
    @Excel(name = "学科基础模块学时")
    private String basicsHour;

    private List<CreditsDetailModel> generalCourses;
    private Map<String,CreditsDetailModel> disciplineMajorCourseMap;
    private CreditsDetailModel trainingProjectCourses;

    private Integer dataSize;

    public String getFirstLevelTitle1() {
        return firstLevelTitle1;
    }

    public void setFirstLevelTitle1(String firstLevelTitle1) {
        this.firstLevelTitle1 = firstLevelTitle1;
    }

    public String getFirstLevelContent1() {
        return firstLevelContent1;
    }

    public void setFirstLevelContent1(String firstLevelContent1) {
        this.firstLevelContent1 = firstLevelContent1;
    }

    public String getFirstLevelTitle2() {
        return firstLevelTitle2;
    }

    public void setFirstLevelTitle2(String firstLevelTitle2) {
        this.firstLevelTitle2 = firstLevelTitle2;
    }

    public String getFirstLevelContent2() {
        return buildContent();
    }

    public void setFirstLevelContent2(String firstLevelContent2) {
        this.firstLevelContent2 = firstLevelContent2;
    }

    public String getFirstLevelTitle3() {
        return firstLevelTitle3;
    }

    public void setFirstLevelTitle3(String firstLevelTitle3) {
        this.firstLevelTitle3 = firstLevelTitle3;
    }

    public String getFirstLevelContent3() {
        return firstLevelContent3;
    }

    public void setFirstLevelContent3(String firstLevelContent3) {
        this.firstLevelContent3 = firstLevelContent3;
    }

    public String getLearnInstitution() {
        return learnInstitution;
    }

    public void setLearnInstitution(String learnInstitution) {
        this.learnInstitution = learnInstitution;
    }

    public String getDegree() {
        return degree;
    }

    public void setDegree(String degree) {
        this.degree = degree;
    }



    public String getFloatRate() {
        return floatRate;
    }

    public void setFloatRate(String floatRate) {
        this.floatRate = floatRate;
    }

    public String getPoliticalHour() {
        return politicalHour;
    }

    public void setPoliticalHour(String politicalHour) {
        this.politicalHour = politicalHour;
    }

    public String getMilitaryHour() {
        return militaryHour;
    }

    public void setMilitaryHour(String militaryHour) {
        this.militaryHour = militaryHour;
    }

    public String getScienceHour() {
        return scienceHour;
    }

    public void setScienceHour(String scienceHour) {
        this.scienceHour = scienceHour;
    }

    public String getBasicsHour() {
        return basicsHour;
    }

    public void setBasicsHour(String basicsHour) {
        this.basicsHour = basicsHour;
    }

    public List<CreditsDetailModel> getGeneralCourses() {
        return generalCourses;
    }

    public void setGeneralCourses(List<CreditsDetailModel> generalCourses) {
        this.generalCourses = generalCourses;
    }

    public Map<String, CreditsDetailModel> getDisciplineMajorCourseMap() {
        return disciplineMajorCourseMap;
    }

    public void setDisciplineMajorCourseMap(Map<String, CreditsDetailModel> disciplineMajorCourseMap) {
        this.disciplineMajorCourseMap = disciplineMajorCourseMap;
    }

    public CreditsDetailModel getTrainingProjectCourses() {
        return trainingProjectCourses;
    }

    public void setTrainingProjectCourses(CreditsDetailModel trainingProjectCourses) {
        this.trainingProjectCourses = trainingProjectCourses;
    }

    public Double getTotalHour() {
        return totalHour;
    }

    public void setTotalHour(Double totalHour) {
        this.totalHour = totalHour;
    }

    public Double getRequiredHour() {
        return requiredHour;
    }

    public void setRequiredHour(Double requiredHour) {
        this.requiredHour = requiredHour;
    }

    public Double getOptionalHour() {
        return optionalHour;
    }

    public void setOptionalHour(Double optionalHour) {
        this.optionalHour = optionalHour;
    }

    public Double getTotalCredit() {
        return totalCredit;
    }

    public void setTotalCredit(Double totalCredit) {
        this.totalCredit = totalCredit;
    }

    public Double getProjectTime() {
        return projectTime;
    }

    public void setProjectTime(Double projectTime) {
        this.projectTime = projectTime;
    }

    public Double getRequireTime() {
        return requireTime;
    }

    public void setRequireTime(Double requireTime) {
        this.requireTime = requireTime;
    }

    public Double getOptionalTime() {
        return optionalTime;
    }

    public void setOptionalTime(Double optionalTime) {
        this.optionalTime = optionalTime;
    }

    public Double getRequiredCredit() {
        return requiredCredit;
    }

    public void setRequiredCredit(Double requiredCredit) {
        this.requiredCredit = requiredCredit;
    }

    public Double getOptionalCredit() {
        return optionalCredit;
    }

    public void setOptionalCredit(Double optionalCredit) {
        this.optionalCredit = optionalCredit;
    }

    public Integer getDataSize() {
        return dataSize;
    }

    public void setDataSize(Integer dataSize) {
        this.dataSize = dataSize;
    }

    private String buildContent() {
        return "学员教学训练体系由课程体系和实践训练体系组成，课程体系包含公共基础课程和专业课程2个部分，实践训练体系包含实践项目和训练科目2个部分。" +
                "按照修读要求，所有课程、实践项目、训练课目分为必修和选修2种类别，其中，选修又区分为限定选修（以下简称“限选”）、任意选修（以下简称“任选”）2种类别，" +
                "限选即所有学员均须修读、任选即学员自主选择修读。\n" +
                "学员在校期间课程学习须修满"+totalHour+"学时，其中必修课程"+requiredHour+"学时、" +
                "选修课程"+optionalHour+"学时（含政治类**学时、军事类**学时、科技类**学时、人文与社会科学类**学时、学科专业选修**学时）；" +
                "实践训练须修满"+projectTime+"周，其中必修实践训练"+requireTime+"周、选修实践训练"+optionalTime+"周。课程教学按16学时折合1学分计算、训练课目按每周折合1学分计算、实践项目不再单独计算学分。" +
                "具体学时学分要求见下表。\n";
    }

    public void setGeneralCourse(CreditsDetailModel creditsDetailModel) {
            generalCourses.add(creditsDetailModel);
    }

    public void setDisciplineMajorCourse(CreditsDetailModel creditsDetailModel, String key) {
            disciplineMajorCourseMap.put(key, creditsDetailModel);
    }

    public DurationAndCreditsModel() {
        generalCourses = new ArrayList<>();
        disciplineMajorCourseMap = new HashMap<>();
        totalHour=0.0;
        requiredHour=0.0;
        optionalHour=0.0;
        totalCredit=0.0;
        requiredCredit=0.0;
        optionalCredit=0.0;
        projectTime=0.0;
        requireTime=0.0;
        optionalTime=0.0;
        dataSize=0;
    }
}
