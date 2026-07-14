package com.doinner.csys.domain.vo;

import java.util.ArrayList;
import java.util.List;

/**
 * 毕业要求与课程支撑矩阵。
 *
 * 结构层级：培养方案 -> 毕业要求分组(知识/能力/素质) -> 一级指标 -> 具体毕业要求(叶子) -> 支撑课程。
 * 由 StandardServiceImpl.selectGraduationCourseSupport 组装, 供接口返回与 Excel 导出复用。
 */
public class GraduationCourseSupportVo {

    /** 培养方案id */
    private Long schemeId;

    /** 单个毕业要求下支撑课程的最大数量(用于导出时确定列数) */
    private int maxCourseCount;

    /** 毕业要求分组集合 */
    private List<SupportGroupVo> groups = new ArrayList<>();

    public Long getSchemeId() {
        return schemeId;
    }

    public void setSchemeId(Long schemeId) {
        this.schemeId = schemeId;
    }

    public int getMaxCourseCount() {
        return maxCourseCount;
    }

    public void setMaxCourseCount(int maxCourseCount) {
        this.maxCourseCount = maxCourseCount;
    }

    public List<SupportGroupVo> getGroups() {
        return groups;
    }

    public void setGroups(List<SupportGroupVo> groups) {
        this.groups = groups;
    }

    /**
     * 毕业要求分组(对应树的根节点, 如知识/能力/素质)。
     */
    public static class SupportGroupVo {

        /** 根节点id */
        private Long rootId;

        /** 根节点展示名 */
        private String rootName;

        /** 毕业要求类型(1知识/2能力/3素质) */
        private String graduationType;

        /** 一级指标集合 */
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
     * 一级指标(根节点的下一级)。
     */
    public static class SupportFirstLevelVo {

        /** 一级指标id */
        private Long id;

        /** 一级指标名称 */
        private String name;

        /** 该一级指标下的具体毕业要求(叶子)集合 */
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
     * 具体毕业要求(叶子节点)。
     */
    public static class SupportRequirementVo {

        /** 毕业要求id */
        private Long id;

        /** 毕业要求名称 */
        private String name;

        /** 支撑该毕业要求的课程集合 */
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
     * 支撑课程。
     */
    public static class SupportCourseVo {

        /** 课程id */
        private Long id;

        /** 课程名称 */
        private String name;

        /** 课程编号 */
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
