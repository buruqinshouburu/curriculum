package com.doinner.csys.domain.vo;

import com.doinner.csys.domain.Course;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 课程Vo对象
 */
public class CourseVo extends Course {

    private static Map<String,String> propMapping=new HashMap<String,String>();

    static {
        // 列表同名称排序字段(前端属性名 -> 数据库列名)
        // 涉及 学科门类/专业类/学院/开课单位/组织实施单位/适用专业/专业方向 的排序统一用对应 id 列,
        // 避免为排序而 join 名称表拖慢查询。列名带 c. 前缀, 与课程主表别名 c. 一致。
        propMapping.put("collegeId","c.college_id");
        propMapping.put("name","c.name");
        propMapping.put("code","c.code");
        propMapping.put("courseModule","c.course_Module");
        propMapping.put("courseModuleChildren","c.course_Module_Children");
        propMapping.put("teachCollegeId","c.teach_college_id");      // 组织实施单位/开课单位
        propMapping.put("educationLevel","c.education_level");        // 适用对象
        propMapping.put("subMajorId","c.sub_Major_Id");               // 专业方向
        propMapping.put("categoryId","c.category_Id");                // 学科门类
        propMapping.put("majorId","c.major_Id");                      // 专业类
        propMapping.put("programLevel","c.program_Level");            // 项目层级
        propMapping.put("courseAttr","c.course_attr");                // 修读要求
        propMapping.put("hasWork","c.has_Work");                      // 是否提交大作业
        propMapping.put("location","c.location");                      // 科目模块
        // 名称排序统一回退到对应 id 列
        propMapping.put("collegeName","c.college_id");
        propMapping.put("categoryName","c.category_Id");
        propMapping.put("majorName","c.major_Id");
        propMapping.put("subMajorName","c.sub_Major_Id");
    }

    /** 所属学院名称 */
    private String collegeName;

    /** 施教学院名称 */
    private String teachCollegeName;

    /** 预修课程名称 */
    private List<String> beforeCourseName;

    /** 后续课程名称 */
    private List<String> afterCourseName;

    /** 课程性质名称 */
    private String coursePropName;

    /** 课程大类名称 */
    private String courseTypeName;

    /** 课程属性名称 */
    private String courseAttrName;

    /** 排序字段 */
    private String prop;
    private String database_prop;

    /** 排序方式 */
    private String order;

    private String version;

    private Integer enableFlag;

    private String educationLevel;

    private Integer buildStatus;

    private Integer academicTermsNumber;


    /** 知识单元 */
    private List<KnowledgeUnitVo> knowledgeUnitVoList;

    private List<KnowledgeUnitVo> children;

    /**
     * 章节集合
     */
    private List<CourseChapterVo> courseChapterVoList;

    /**
     * 教材推荐参考书集合
     */
    private List<CourseTextbookVo> courseTextbookVoList;

    /**
     * 理论教学计划
     */
    private CourseTeachingTheoryPlanVo courseTeachingTheoryPlanVo;

    /**
     * 实践教学计划
     */
    private CourseTeachingPracticePlanVo courseTeachingPracticePlanVo;

    /**
     * 知识领域
     */
    private List<KnowledgeDomainVo> knowledgeDomainVoList;

    /**
     *  知识体系里面的知识领域 新
     */
    //private List<SourceKnowledgeVo> sourceKnowledgeVoList;

    private Long graduationId;

    private Long schemeId;

    public CourseVo() {
        super();
    }

    public CourseVo(String collegeName, String teachCollegeName, List<String> beforeCourseName, List<String> afterCourseName, List<KnowledgeUnitVo> knowledgeUnitVoList, List<KnowledgeUnitVo> children, List<CourseChapterVo> courseChapterVoList, List<CourseTextbookVo> courseTextbookVoList, CourseTeachingTheoryPlanVo courseTeachingTheoryPlanVo, CourseTeachingPracticePlanVo courseTeachingPracticePlanVo, List<KnowledgeDomainVo> knowledgeDomainVoList, List<CourseIdAndName> beforeCourseList, List<CourseIdAndName> afterCourseList,Integer academicTermsNumber) {
        super();
        this.collegeName = collegeName;
        this.teachCollegeName = teachCollegeName;
        this.beforeCourseName = beforeCourseName;
        this.afterCourseName = afterCourseName;
        this.knowledgeUnitVoList = knowledgeUnitVoList;
        this.children = children;
        this.courseChapterVoList = courseChapterVoList;
        this.courseTextbookVoList = courseTextbookVoList;
        this.courseTeachingTheoryPlanVo = courseTeachingTheoryPlanVo;
        this.courseTeachingPracticePlanVo = courseTeachingPracticePlanVo;
        this.knowledgeDomainVoList = knowledgeDomainVoList;
        this.beforeCourseList = beforeCourseList;
        this.afterCourseList = afterCourseList;
        this.academicTermsNumber=academicTermsNumber;
    }

    public String getCollegeName() {
        return collegeName;
    }

    public void setCollegeName(String collegeName) {
        this.collegeName = collegeName;
    }

    public String getTeachCollegeName() {
        return teachCollegeName;
    }

    public void setTeachCollegeName(String teachCollegeName) {
        this.teachCollegeName = teachCollegeName;
    }

    public List<String> getBeforeCourseName() {
        return beforeCourseName;
    }

    public void setBeforeCourseName(List<String> beforeCourseName) {
        this.beforeCourseName = beforeCourseName;
    }

    public List<String> getAfterCourseName() {
        return afterCourseName;
    }

    public void setAfterCourseName(List<String> afterCourseName) {
        this.afterCourseName = afterCourseName;
    }

    public String getCoursePropName() {
        return coursePropName;
    }

    public void setCoursePropName(String coursePropName) {
        this.coursePropName = coursePropName;
    }

    public String getCourseTypeName() {
        return courseTypeName;
    }

    public void setCourseTypeName(String courseTypeName) {
        this.courseTypeName = courseTypeName;
    }

    public String getCourseAttrName() {
        return courseAttrName;
    }

    public void setCourseAttrName(String courseAttrName) {
        this.courseAttrName = courseAttrName;
    }

    private List<CourseIdAndName> beforeCourseList;

    private List<CourseIdAndName> afterCourseList;

    public List<CourseIdAndName> getBeforeCourseList() {
        return beforeCourseList;
    }

    public void setBeforeCourseList(List<CourseIdAndName> beforeCourseList) {
        this.beforeCourseList = beforeCourseList;
    }

    public List<CourseIdAndName> getAfterCourseList() {
        return afterCourseList;
    }

    public void setAfterCourseList(List<CourseIdAndName> afterCourseList) {
        this.afterCourseList = afterCourseList;
    }

    public List<CourseChapterVo> getCourseChapterVoList() {
        return courseChapterVoList;
    }

    public void setCourseChapterVoList(List<CourseChapterVo> courseChapterVoList) {
        this.courseChapterVoList = courseChapterVoList;
    }

    public List<CourseTextbookVo> getCourseTextbookVoList() {
        return courseTextbookVoList;
    }

    public void setCourseTextbookVoList(List<CourseTextbookVo> courseTextbookVoList) {
        this.courseTextbookVoList = courseTextbookVoList;
    }

    public CourseTeachingTheoryPlanVo getCourseTeachingTheoryPlanVo() {
        return courseTeachingTheoryPlanVo;
    }

    public void setCourseTeachingTheoryPlanVo(CourseTeachingTheoryPlanVo courseTeachingTheoryPlanVo) {
        this.courseTeachingTheoryPlanVo = courseTeachingTheoryPlanVo;
    }

    public CourseTeachingPracticePlanVo getCourseTeachingPracticePlanVo() {
        return courseTeachingPracticePlanVo;
    }

    public void setCourseTeachingPracticePlanVo(CourseTeachingPracticePlanVo courseTeachingPracticePlanVo) {
        this.courseTeachingPracticePlanVo = courseTeachingPracticePlanVo;
    }

    public List<KnowledgeUnitVo> getKnowledgeUnitVoList() {
        return knowledgeUnitVoList;
    }

    public void setKnowledgeUnitVoList(List<KnowledgeUnitVo> knowledgeUnitVoList) {
        this.knowledgeUnitVoList = knowledgeUnitVoList;
    }

    public List<KnowledgeUnitVo> getChildren() {
        return children;
    }

    public void setChildren(List<KnowledgeUnitVo> children) {
        this.children = children;
    }

    public List<KnowledgeDomainVo> getKnowledgeDomainVoList() {
        return knowledgeDomainVoList;
    }

    public void setKnowledgeDomainVoList(List<KnowledgeDomainVo> knowledgeDomainVoList) {
        this.knowledgeDomainVoList = knowledgeDomainVoList;
    }

    public String getProp() {
        return prop;
    }

    public void setProp(String prop) {
        this.prop = prop;
        this.database_prop=propMapping.get(prop);
    }

    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
    }


    public Long getGraduationId() {
        return graduationId;
    }

    public void setGraduationId(Long graduationId) {
        this.graduationId = graduationId;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    @Override
    public Integer getEnableFlag() {
        return enableFlag;
    }

    @Override
    public void setEnableFlag(Integer enableFlag) {
        this.enableFlag = enableFlag;
    }

    @Override
    public String getEducationLevel() {
        return educationLevel;
    }

    @Override
    public void setEducationLevel(String educationLevel) {
        this.educationLevel = educationLevel;
    }

    public Long getSchemeId() {
        return schemeId;
    }

    public void setSchemeId(Long schemeId) {
        this.schemeId = schemeId;
    }

    public Integer getBuildStatus() {
        return buildStatus;
    }

    public void setBuildStatus(Integer buildStatus) {
        this.buildStatus = buildStatus;
    }

    public Integer getAcademicTermsNumber() {
        return academicTermsNumber;
    }

    public void setAcademicTermsNumber(Integer academicTermsNumber) {
        this.academicTermsNumber = academicTermsNumber;
    }

    public String getDatabase_prop() {
        return database_prop;
    }

    public void setDatabase_prop(String database_prop) {
        this.database_prop = database_prop;
    }
}
