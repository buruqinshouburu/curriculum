package com.doinner.csys.domain;

import com.doinner.common.core.domain.db.AbstractDoinnerLogicalDelBaseEntity;
import com.doinner.common.core.annotation.Excel;
import com.doinner.csys.domain.vo.GraduationTreeVo;
import com.doinner.csys.domain.vo.StandardTreeVo;

import java.util.ArrayList;
import java.util.List;


/**
 * 课程对象 t_csys_course
 *
 * @author doinner
 * @date 2023-03-14
 */
public class Course extends AbstractDoinnerLogicalDelBaseEntity {

    private static final long serialVersionUID = -4203290278528654761L;

    /**
     * 课程名(name)
     */
    @Excel(name = "课程名(name)")
    private String name;

    /**
     * 课程编号(code)
     */
    @Excel(name = "课程编号(code)")
    private String code;

    /**
     * 课程类型(type)  1课程 3 课程含实践  type=2训练课type=4实践课
     */
    @Excel(name = "课程类型(type)")
    private String type;

    /**
     * 执笔人名称(authors)，多值拼接字符串
     */
    @Excel(name = "执笔人id(authors)")
    private String authors;

    /**
     * 执笔人ID(author_ids)，多值拼接字符串，对应 sys_user.user_id
     */
    private String authorIds;

    /**
     * 所属学院(college_id)
     */
   // @Excel(name = "所属学院(college_id)")
    private Long collegeId;

    private String collegeName;
    /**
     * 施教学院(teach_college_id)
     */
    @Excel(name = "施教学院(teach_college_id)")
    private Long teachCollegeId;

    /**
     * 英文名称(en_name)
     */
    @Excel(name = "英文名称(en_name)")
    private String enName;

    /**
     * 预修课程(before_course_id)
     */
    @Excel(name = "预修课程(before_course_id)")
    private String beforeCourseId;

    /**
     * 后续课程(after_course_id)
     */
    @Excel(name = "后续课程(after_course_id)")
    private String afterCourseId;

    /**
     * 学时安排(hours)
     */
    @Excel(name = "学时安排(hours)")
    private Double hours;

    /**
     * 理论学时(theory_hours)
     */
    @Excel(name = "理论学时(theory_hours)")
    private Double theoryHours;

    /**
     * 实践学时(practice_hours)
     */
    @Excel(name = "实践学时(practice_hours)")
    private Double practiceHours;

    /**
     * 周学时(week_hours)
     */
    @Excel(name = "周学时(week_hours)")
    private Double weekHours;

    /**
     * 讲授学时(teach_hours)
     */
    @Excel(name = "讲授学时(teach_hours)")
    private Double teachHours;

    /**
     * 考核学时(examine_hours)
     */
    @Excel(name = "考核学时(examine_hours)")
    private Double examineHours;

    /**
     * 其他学时(other_hours)
     */
    @Excel(name = "其他学时(other_hours)")
    private Double otherHours;

    /**
     * 学时单位(hours_unit)
     */
    @Excel(name = "学时单位(hours_unit)")
    private String hoursUnit;

    /**
     * 学分(credit)
     */
    @Excel(name = "学分(credit)")
    private Double credit;

    /**
     * 课程性质(course_prop)
     */
    @Excel(name = "课程性质(course_prop)")
    private Integer courseProp;

    /**
     * 课程大类(course_type)
     */
    @Excel(name = "课程大类(course_type)")
    private Integer courseType;

    /**
     * 课程属性(course_attr)
     */
    @Excel(name = "课程属性(course_attr)")
    private String courseAttr;

    /**
     * 实施地点(location)
     */
    @Excel(name = "实施地点(location)")
    private String location;

    /**
     * 开课学期(open_term)
     */
    @Excel(name = "开课学期(open_term)")
    private String openTerm;

    /**
     * 内容简介(summary)
     */
    @Excel(name = "内容简介(summary)")
    private String summary;

    /**
     * 文件id(file_id)
     */
    @Excel(name = "文件id(file_id)")
    private String fileId;

    /**
     * 文件名称(file_name)
     */
    @Excel(name = "文件名称(file_name)")
    private String fileName;

    /**
     * 审核状态：0未审核1已审核
     */
    private Integer status;

    /**
     * 分析状态：0，未分析；1，已分析
     */
    private Integer analysisStatus;

    /**
     * 创建部门
     */
    private Long deptBy;

    // 课程模块 多级字典
    private String courseModule;
    private String courseModuleName;
    private String courseModuleChildren;
    private String courseModuleChildrenName;

    // 第一、二、三、四 学年
    private String semesterSchedule;

    // 春 秋
    private String springAutumn;

    // 开课年份
    private String openYear;

    // 专业id
    private Long majorId;
    private String majorName;

    // 专业方向
    private Long subMajorId;
    private String subMajorName;

    // 门类id
    private Long categoryId;
    private String categoryName;

    // 考核方式
    private String exaMethod;

    // 项目层级字段名
    private String programLevel;

    // 时间安排(周)
    private Double timeWeek;

    // 时间单位(用于确定实践训练科目/项目 timeWeek 的具体单位)
    private String unit;

    // 毕业要求
    //private List<Long> graduationIds;
    // 毕业要求，又改成树结构了
    //private List<GraduationTreeVo> graduationTreeVoList = new ArrayList<>();

    // 能力树
    //private List<StandardTreeVo> abilityVoList = new ArrayList<>();

    // 素质树
    //private List<StandardTreeVo> qualityVoList = new ArrayList<>();


    // 课程目标
    private List<CourseTarget> courseTargetList = new ArrayList<>();

    private Long sourceId;

    private Integer templateType;

    private String version;

    // 绑定状态1已绑定，2未绑定
    private Integer bindStatus;
    //最大引用次数
    private Integer maxQuoteCount;

    private Integer enableFlag;
    /**
     * 培养层次
     */
    private String educationLevel;

    private Integer buildStatus;

    private Integer hasWork;

    private Integer academicTermsNumber;


    // private Boolean knowledgeBindingFlag = false;


    // private Boolean graduationBindingFlag = false;


    public List<CourseTarget> getCourseTargetList() {
        return courseTargetList;
    }

    public void setCourseTargetList(List<CourseTarget> courseTargetList) {
        this.courseTargetList = courseTargetList;
    }

    public Long getDeptBy() {
        return deptBy;
    }

    public void setDeptBy(Long deptBy) {
        this.deptBy = deptBy;
    }


    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }



    public void setAuthors(String authors) {
        this.authors = authors;
    }

    public String getAuthors() {
        return authors;
    }

    public void setAuthorIds(String authorIds) {
        this.authorIds = authorIds;
    }

    public String getAuthorIds() {
        return authorIds;
    }

    public void setCollegeId(Long collegeId) {
        this.collegeId = collegeId;
    }

    public Long getCollegeId() {
        return collegeId;
    }

    public void setTeachCollegeId(Long teachCollegeId) {
        this.teachCollegeId = teachCollegeId;
    }

    public Long getTeachCollegeId() {
        return teachCollegeId;
    }

    public void setEnName(String enName) {
        this.enName = enName;
    }

    public String getEnName() {
        return enName;
    }

    public void setBeforeCourseId(String beforeCourseId) {
        this.beforeCourseId = beforeCourseId;
    }

    public String getBeforeCourseId() {
        return beforeCourseId;
    }

    public void setAfterCourseId(String afterCourseId) {
        this.afterCourseId = afterCourseId;
    }

    public String getAfterCourseId() {
        return afterCourseId;
    }

    public void setHours(Double hours) {
        this.hours = hours;
    }

    public Double getHours() {
        return hours;
    }

    public void setTheoryHours(Double theoryHours) {
        this.theoryHours = theoryHours;
    }

    public Double getTheoryHours() {
        return theoryHours;
    }

    public void setPracticeHours(Double practiceHours) {
        this.practiceHours = practiceHours;
    }

    public Double getPracticeHours() {
        return practiceHours;
    }

    public void setWeekHours(Double weekHours) {
        this.weekHours = weekHours;
    }

    public Double getWeekHours() {
        return weekHours;
    }

    public void setTeachHours(Double teachHours) {
        this.teachHours = teachHours;
    }

    public Double getTeachHours() {
        return teachHours;
    }

    public void setExamineHours(Double examineHours) {
        this.examineHours = examineHours;
    }

    public Double getExamineHours() {
        return examineHours;
    }

    public void setOtherHours(Double otherHours) {
        this.otherHours = otherHours;
    }

    public Double getOtherHours() {
        return otherHours;
    }

    public void setHoursUnit(String hoursUnit) {
        this.hoursUnit = hoursUnit;
    }

    public String getHoursUnit() {
        return hoursUnit;
    }

    public void setCredit(Double credit) {
        this.credit = credit;
    }

    public Double getCredit() {
        return credit;
    }

    public void setCourseProp(Integer courseProp) {
        this.courseProp = courseProp;
    }

    public Integer getCourseProp() {
        return courseProp;
    }

    public void setCourseType(Integer courseType) {
        this.courseType = courseType;
    }

    public Integer getCourseType() {
        return courseType;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCourseAttr() {
        return courseAttr;
    }

    public void setCourseAttr(String courseAttr) {
        this.courseAttr = courseAttr;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getLocation() {
        return location;
    }

    public void setOpenTerm(String openTerm) {
        this.openTerm = openTerm;
    }

    public String getOpenTerm() {
        return openTerm;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getSummary() {
        return summary;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    public String getFileId() {
        return fileId;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileName() {
        return fileName;
    }

    public Integer getAnalysisStatus() {
        return analysisStatus;
    }

    public void setAnalysisStatus(Integer analysisStatus) {
        this.analysisStatus = analysisStatus;
    }

    public String getCourseModule() {
        return courseModule;
    }

    public void setCourseModule(String courseModule) {
        this.courseModule = courseModule;
    }

    public String getCourseModuleChildren() {
        return courseModuleChildren;
    }

    public void setCourseModuleChildren(String courseModuleChildren) {
        this.courseModuleChildren = courseModuleChildren;
    }

    public String getSemesterSchedule() {
        return semesterSchedule;
    }

    public void setSemesterSchedule(String semesterSchedule) {
        this.semesterSchedule = semesterSchedule;
    }

    public String getSpringAutumn() {
        return springAutumn;
    }

    public void setSpringAutumn(String springAutumn) {
        this.springAutumn = springAutumn;
    }

    public String getOpenYear() {
        return openYear;
    }

    public void setOpenYear(String openYear) {
        this.openYear = openYear;
    }

    public String getCourseModuleName() {
        return courseModuleName;
    }

    public void setCourseModuleName(String courseModuleName) {
        this.courseModuleName = courseModuleName;
    }

    public String getCourseModuleChildrenName() {
        return courseModuleChildrenName;
    }

    public void setCourseModuleChildrenName(String courseModuleChildrenName) {
        this.courseModuleChildrenName = courseModuleChildrenName;
    }

    public Long getMajorId() {
        return majorId;
    }

    public void setMajorId(Long majorId) {
        this.majorId = majorId;
    }

    public String getMajorName() {
        return majorName;
    }

    public void setMajorName(String majorName) {
        this.majorName = majorName;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }


    public String getExaMethod() {
        return exaMethod;
    }

    public void setExaMethod(String exaMethod) {
        this.exaMethod = exaMethod;
    }

    public String getProgramLevel() {
        return programLevel;
    }

    public void setProgramLevel(String programLevel) {
        this.programLevel = programLevel;
    }

    public Double getTimeWeek() {
        return timeWeek;
    }

    public void setTimeWeek(Double timeWeek) {
        this.timeWeek = timeWeek;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public Long getSubMajorId() {
        return subMajorId;
    }

    public void setSubMajorId(Long subMajorId) {
        this.subMajorId = subMajorId;
    }

    public String getSubMajorName() {
        return subMajorName;
    }

    public void setSubMajorName(String subMajorName) {
        this.subMajorName = subMajorName;
    }

    public Long getSourceId() {
        return sourceId;
    }

    public void setSourceId(Long sourceId) {
        this.sourceId = sourceId;
    }

    public Integer getTemplateType() {
        return templateType;
    }

    public void setTemplateType(Integer templateType) {
        this.templateType = templateType;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public Integer getBindStatus() {
        return bindStatus;
    }

    public void setBindStatus(Integer bindStatus) {
        this.bindStatus = bindStatus;
    }

    public Integer getMaxQuoteCount() {
        return maxQuoteCount;
    }

    public void setMaxQuoteCount(Integer maxQuoteCount) {
        this.maxQuoteCount = maxQuoteCount;
    }

    public Integer getEnableFlag() {
        return enableFlag;
    }

    public void setEnableFlag(Integer enableFlag) {
        this.enableFlag = enableFlag;
    }

    public String getEducationLevel() {
        return educationLevel;
    }

    public void setEducationLevel(String educationLevel) {
        this.educationLevel = educationLevel;
    }

    public Integer getBuildStatus() {
        return buildStatus;
    }

    public void setBuildStatus(Integer buildStatus) {
        this.buildStatus = buildStatus;
    }

    public Integer getHasWork() {
        return hasWork;
    }

    public void setHasWork(Integer hasWork) {
        this.hasWork = hasWork;
    }

    public Integer getAcademicTermsNumber() {
        return academicTermsNumber;
    }

    public void setAcademicTermsNumber(Integer academicTermsNumber) {
        this.academicTermsNumber = academicTermsNumber;
    }

    public String getCollegeName() {
        return collegeName;
    }

    public void setCollegeName(String collegeName) {
        this.collegeName = collegeName;
    }
}
