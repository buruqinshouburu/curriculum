package com.doinner.csys.domain.vo;

import java.util.ArrayList;
import java.util.List;

/**
 * 培养方案-毕业要求与课程支撑矩阵 展示对象。
 * <p>
 * 结构：培养方案 -> 毕业要求分组(知识/能力/素质) -> 一级指标 -> 叶子要求(绑定的课程)。
 * 前端可据此做单元格合并(根跨所有叶子行、一级指标跨其下叶子行)。
 * Excel 导出复用同一数据结构。
 *
 * @author doinner
 */
public class GraduationCourseSupportVo {

    /** 培养方案id */
    private Long schemeId;

    /** 所有叶子中绑定的最大课程数(用于表头"课程1..课程N"列数) */
    private Integer maxCourseCount = 0;

    /** 毕业要求分组(按根节点，如知识/能力/素质) */
    private List<SupportGroupVo> groups = new ArrayList<>();

    public Long getSchemeId() {
        return schemeId;
    }

    public void setSchemeId(Long schemeId) {
        this.schemeId = schemeId;
    }

    public Integer getMaxCourseCount() {
        return maxCourseCount;
    }

    public void setMaxCourseCount(Integer maxCourseCount) {
        this.maxCourseCount = maxCourseCount;
    }

    public List<SupportGroupVo> getGroups() {
        return groups;
    }

    public void setGroups(List<SupportGroupVo> groups) {
        this.groups = groups;
    }

    /**
     * 毕业要求分组(根节点：知识/能力/素质)
     */
    public static class SupportGroupVo {
        private Long rootId;
        /** 根名称(知识/能力/素质)，name 为空时由 graduationType 映射 */
        private String rootName;
        /** 1:知识 2:能力 3:素质 */
        private String graduationType;
        private List<SupportFirstLevelVo> firstLevels = new ArrayList<>();

        public Long getRootId() {
            return rootId;
        }

        public void setRootId(Long rootId) {
            this.rootId = rootId;
        }

        public String getRootName() {
            return rootName;
        }

        public void setRootName(String rootName) {
            this.rootName = rootName;
        }

        public String getGraduationType() {
            return graduationType;
        }

        public void setGraduationType(String graduationType) {
            this.graduationType = graduationType;
        }

        public List<SupportFirstLevelVo> getFirstLevels() {
            return firstLevels;
        }

        public void setFirstLevels(List<SupportFirstLevelVo> firstLevels) {
            this.firstLevels = firstLevels;
        }
    }

    /**
     * 一级指标(如"政治理论知识")
     */
    public static class SupportFirstLevelVo {
        private Long id;
        private String name;
        private List<SupportRequirementVo> requirements = new ArrayList<>();

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public List<SupportRequirementVo> getRequirements() {
            return requirements;
        }

        public void setRequirements(List<SupportRequirementVo> requirements) {
            this.requirements = requirements;
        }
    }

    /**
     * 叶子毕业要求(具体要求，绑定课程)
     */
    public static class SupportRequirementVo {
        private Long id;
        private String name;
        private List<SupportCourseVo> courses = new ArrayList<>();

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public List<SupportCourseVo> getCourses() {
            return courses;
        }

        public void setCourses(List<SupportCourseVo> courses) {
            this.courses = courses;
        }
    }

    /**
     * 支撑课程
     */
    public static class SupportCourseVo {
        private Long id;
        private String name;
        private String code;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }
    }
}
