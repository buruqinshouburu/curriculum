package com.example.cscy.entity.scheme.model;

import java.util.*;
import java.util.stream.Collectors;
import java.util.Comparator;

public class TrainingSchemeCourseModel {
    private String name;
    private Double hours;
    private Double theoryHours;
    private Double practiceHours;
    private Double courseTypeId;
    private String courseTypeName;
    //修复要求
    private String courseAttrName;
    //开课学期
    private String openTerm;
    //第一、二、三、四学年
    private String semesterSchedule;
    //春秋
    private String springAutumn;
    //课程模块  政治理论、军事基础、基础科学
    private String courseModeChildrenName;
    private Integer modeChildrenNameSort;
    private Integer modeFourLevelSort;
    private String modeFourLevelName;
    //通识课、专业课、实践训练科目
    private String courseModelName;

    private String majorName;

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

    public Double getTheoryHours() {
        return theoryHours;
    }

    public void setTheoryHours(Double theoryHours) {
        this.theoryHours = theoryHours;
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

    public String getCourseAttrName() {
        return courseAttrName;
    }

    public void setCourseAttrName(String courseAttrName) {
        this.courseAttrName = courseAttrName;
    }

    public String getOpenTerm() {
        return openTerm;
    }

    public void setOpenTerm(String openTerm) {
        this.openTerm = openTerm;
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

    public Integer getModeChildrenNameSort() {
        return modeChildrenNameSort;
    }

    public void setModeChildrenNameSort(Integer modeChildrenNameSort) {
        this.modeChildrenNameSort = modeChildrenNameSort;
    }

    public Integer getModeFourLevelSort() {
        return modeFourLevelSort;
    }

    public void setModeFourLevelSort(Integer modeFourLevelSort) {
        this.modeFourLevelSort = modeFourLevelSort;
    }

    public String getModeFourLevelName() {
        return modeFourLevelName;
    }

    public void setModeFourLevelName(String modeFourLevelName) {
        this.modeFourLevelName = modeFourLevelName;
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
            Integer modeSort = course.getModeChildrenNameSort();
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
                String fourLevelName = getDefaultFourLevelName(course.getModeFourLevelName());
                Integer fourLevelSort = course.getModeFourLevelSort();
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
                        .filter(c -> fourLevelName.equals(getDefaultFourLevelName(c.getModeFourLevelName())))
                        .sorted(Comparator.comparing(
                                c -> {
                                    Integer sort = c.getModeFourLevelSort();
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
     * 获取默认的 modeFourLevelName，null 或空字符串返回 "1"
     */
    private static String getDefaultFourLevelName(String name) {
        return name == null || name.isEmpty() ? "1" : name;
    }
}


