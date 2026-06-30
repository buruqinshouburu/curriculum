package com.doinner.csys.entity.csys.model;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TrainingSchemeCourseModel {
    private String name;
    private Double hours;
    private Double credits;
    private Double teachHours;
    private Double practiceHours;
    private Double courseTypeId;
    private String courseTypeName;
    //修复要求
    private String courseAttr;
    //开课学期
    private List<Integer> openTerm;
    //各开课学期对应的拆分学时（讲授+实践）：key=学期(1-8)，value=该学期学时
    private Map<Integer, Double> termHoursMap;
    //第一、二、三、四学年
    private String semesterSchedule;
    //春秋
    private String springAutumn;
    //课程模块  政治理论、军事基础、基础科学
    private String courseModeChildrenName;
    private String courseModeChildrenId;
    private Integer childrenModelSort;
    //课程模块子模块
    private String courseModeFourLevelName;
    private Integer courseModeFourLevelSort;
    //通识课、专业课、实践训练科目
    private String courseModelName;
    private String courseModelId;
    private Long majorId;

    private String majorName;
    private String subMajorName;
    private Long submajorId;
    //项目层级
    private String projectLevelId;
    private String projectLevelName;
    private Integer projectLevelSort;
    //时间安排
    private Double timeWeek;
    //时间单位(周/学时 等中文，由字典 sys_course_unit 转换)
    private String unit;
    //备注
    private String remark;
    //支撑课程
    private String supportingCourseIds;
    private String supportingCourseNames;
    //课目模块
    private String trainingCourseModelId;
    private String trainingCourseModelName;

    /**
     * 考核方式
     */
    private String exaMethod;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getHours() {
        return hours;
    }

    public void setHours(Double hours) {
        this.hours = hours;
    }

    public Double getTeachHours() {
        return teachHours;
    }

    public void setTeachHours(Double teachHours) {
        this.teachHours = teachHours;
    }

    public Double getPracticeHours() {
        return practiceHours;
    }

    public void setPracticeHours(Double practiceHours) {
        this.practiceHours = practiceHours;
    }

    public Double getCourseTypeId() {
        return courseTypeId;
    }

    public void setCourseTypeId(Double courseTypeId) {
        this.courseTypeId = courseTypeId;
    }

    public String getCourseTypeName() {
        return courseTypeName;
    }

    public void setCourseTypeName(String courseTypeName) {
        this.courseTypeName = courseTypeName;
    }

    public String getCourseAttr() {
        return courseAttr;
    }

    public void setCourseAttr(String courseAttr) {
        this.courseAttr = courseAttr;
    }

    public List<Integer> getOpenTerm() {
        return openTerm;
    }

    public void setOpenTerm(List<Integer> openTerm) {
        this.openTerm = openTerm;
    }

    public Map<Integer, Double> getTermHoursMap() {
        return termHoursMap;
    }

    public void setTermHoursMap(Map<Integer, Double> termHoursMap) {
        this.termHoursMap = termHoursMap;
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

    public String getCourseModelName() {
        return courseModelName;
    }

    public void setCourseModelName(String courseModelName) {
        this.courseModelName = courseModelName;
    }

    public String getCourseModeChildrenName() {
        return courseModeChildrenName;
    }

    public void setCourseModeChildrenName(String courseModeChildrenName) {
        this.courseModeChildrenName = courseModeChildrenName;
    }

    public String getMajorName() {
        return majorName;
    }

    public void setMajorName(String majorName) {
        this.majorName = majorName;
    }

    public String getExaMethod() {
        return exaMethod;
    }

    public void setExaMethod(String exaMethod) {
        this.exaMethod = exaMethod;
    }

    public String getCourseModeChildrenId() {
        return courseModeChildrenId;
    }

    public void setCourseModeChildrenId(String courseModeChildrenId) {
        this.courseModeChildrenId = courseModeChildrenId;
    }

    public String getCourseModelId() {
        return courseModelId;
    }

    public void setCourseModelId(String courseModelId) {
        this.courseModelId = courseModelId;
    }

    public Long getMajorId() {
        return majorId;
    }

    public void setMajorId(Long majorId) {
        this.majorId = majorId;
    }

    public String getSubMajorName() {
        return subMajorName;
    }

    public void setSubMajorName(String subMajorName) {
        this.subMajorName = subMajorName;
    }

    public Long getSubmajorId() {
        return submajorId;
    }

    public void setSubmajorId(Long submajorId) {
        this.submajorId = submajorId;
    }

    public Integer getChildrenModelSort() {
        return childrenModelSort;
    }

    public void setChildrenModelSort(Integer childrenModelSort) {
        this.childrenModelSort = childrenModelSort;
    }

    public String getProjectLevelId() {
        return projectLevelId;
    }

    public void setProjectLevelId(String projectLevelId) {
        this.projectLevelId = projectLevelId;
    }

    public String getProjectLevelName() {
        return projectLevelName;
    }

    public void setProjectLevelName(String projectLevelName) {
        this.projectLevelName = projectLevelName;
    }

    public Integer getProjectLevelSort() {
        return projectLevelSort;
    }

    public void setProjectLevelSort(Integer projectLevelSort) {
        this.projectLevelSort = projectLevelSort;
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

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getSupportingCourseIds() {
        return supportingCourseIds;
    }

    public void setSupportingCourseIds(String supportingCourseIds) {
        this.supportingCourseIds = supportingCourseIds;
    }

    public String getSupportingCourseNames() {
        return supportingCourseNames;
    }

    public void setSupportingCourseNames(String supportingCourseNames) {
        this.supportingCourseNames = supportingCourseNames;
    }

    public String getCourseModeFourLevelName() {
        return courseModeFourLevelName;
    }

    public void setCourseModeFourLevelName(String courseModeFourLevelName) {
        this.courseModeFourLevelName = courseModeFourLevelName;
    }

    public Integer getCourseModeFourLevelSort() {
        return courseModeFourLevelSort;
    }

    public void setCourseModeFourLevelSort(Integer courseModeFourLevelSort) {
        this.courseModeFourLevelSort = courseModeFourLevelSort;
    }

    public Double getCredits() {
        return credits;
    }

    public void setCredits(Double credits) {
        this.credits = credits;
    }

    public String getTrainingCourseModelId() {
        return trainingCourseModelId;
    }

    public void setTrainingCourseModelId(String trainingCourseModelId) {
        this.trainingCourseModelId = trainingCourseModelId;
    }

    public String getTrainingCourseModelName() {
        return trainingCourseModelName;
    }

    public void setTrainingCourseModelName(String trainingCourseModelName) {
        this.trainingCourseModelName = trainingCourseModelName;
    }

    /**
     * 对课程列表进行双重分组
     * 一级分组：根据 courseModeChildrenName 分组，然后根据 modeChildrenNameSort 排序
     * 二级分组：根据 modeFourLevelName 分组（为空时默认为"1"），然后根据 modeFourLevelSort 排序
     *
     * @param courses 课程列表
     * @return 二级分组后的Map
     */
    public static Map<String, Map<String, List<TrainingSchemeCourseModel>>> groupCourses(List<TrainingSchemeCourseModel> courses) {
        if (courses == null || courses.isEmpty()) {
            return new LinkedHashMap<>();
        }

        // 提取一级分组的排序字段（保留遇到的顺序）
        Map<String, Integer> modeSortMap = new LinkedHashMap<>();
        for (TrainingSchemeCourseModel course : courses) {
            String modeName = course.getCourseModeChildrenName();
            Integer modeSort = course.getChildrenModelSort();
            if (modeName != null && modeSort != null && !modeSortMap.containsKey(modeName)) {
                modeSortMap.put(modeName, modeSort);
            }
        }

        // 按 modeChildrenNameSort 升序排序
        List<String> sortedModeNames = modeSortMap.entrySet().stream()
                .sorted(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        // 构建结果
        LinkedHashMap<String, Map<String, List<TrainingSchemeCourseModel>>> result = new LinkedHashMap<>();

        for (String modeName : sortedModeNames) {
            List<TrainingSchemeCourseModel> modeCourses = courses.stream()
                    .filter(c -> modeName.equals(c.getCourseModeChildrenName()))
                    .collect(Collectors.toList());

            // 提取二级分组排序字段，null 和空字符串映射为 "1"
            // 对于没有 modeFourLevelSort 的情况，使用默认值 0
            Map<String, Integer> fourLevelSortMap = new LinkedHashMap<>();
            for (TrainingSchemeCourseModel course : modeCourses) {
                String fourLevelName = getDefaultFourLevelName(course.getCourseModeFourLevelName());
                Integer fourLevelSort = course.getCourseModeFourLevelSort();
                // 获取排序值，null 时使用 0 作为默认值
                int sortValue = (fourLevelSort == null) ? 0 : fourLevelSort;
                if (!fourLevelSortMap.containsKey(fourLevelName)) {
                    fourLevelSortMap.put(fourLevelName, sortValue);
                }
            }

            // 按 modeFourLevelSort 升序排序二级分组
            LinkedHashMap<String, List<TrainingSchemeCourseModel>> fourLevelGroup = new LinkedHashMap<>();
            for (Map.Entry<String, Integer> entry : fourLevelSortMap.entrySet()) {
                String fourLevelName = entry.getKey();
                List<TrainingSchemeCourseModel> filtered = modeCourses.stream()
                        .filter(c -> fourLevelName.equals(getDefaultFourLevelName(c.getCourseModeFourLevelName())))
                        .sorted(Comparator.comparing(
                                c -> {
                                    Integer sort = c.getCourseModeFourLevelSort();
                                    return (sort == null) ? 0 : sort;
                                }))
                        .collect(Collectors.toList());
                fourLevelGroup.put(fourLevelName, filtered);
            }

            result.put(modeName, fourLevelGroup);
        }

        return result;
    }

    /**
     * 获取默认的 modeFourLevelName，null 或空字符串返回 "-1"
     */
    private static String getDefaultFourLevelName(String name) {
        return name == null || name.isEmpty() ? DictContent.FOUR_LEVEL_NAME_NULL : name;
    }
}
