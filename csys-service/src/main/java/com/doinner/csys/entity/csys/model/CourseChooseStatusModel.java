package com.doinner.csys.entity.csys.model;

import java.util.ArrayList;
import java.util.List;

/**
 * 课程被选用情况表 - 课程模型
 * <p>
 * 一门课程对应一个模型，包含课程基本信息(名称/编号)以及若干"选用"行。
 * 每个选用行代表一个(选用单位/选用专业类/选用专业)组合，并携带该组合在各学期的学时安排。
 *
 * @author doinner
 */
public class CourseChooseStatusModel {

    /** 课程名称 */
    private String courseName;

    /** 课程编号 */
    private String courseCode;

    /** 选用行(每个选用单位/专业类/专业组合一行) */
    private List<CourseSelectionRow> rows = new ArrayList<>();

    public CourseChooseStatusModel() {
    }

    public CourseChooseStatusModel(String courseName, String courseCode) {
        this.courseName = courseName;
        this.courseCode = courseCode;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode;
    }

    public List<CourseSelectionRow> getRows() {
        return rows;
    }

    public void setRows(List<CourseSelectionRow> rows) {
        this.rows = rows;
    }

    /**
     * 课程被选用情况表 - 选用行
     * <p>
     * 一个(选用单位/选用专业类/选用专业)组合及其在8个学期的学时安排。
     * termHours 下标 0-7 依次对应：第一学年秋/春、第二学年秋/春、第三学年秋/春、第四学年秋/春。
     */
    public static class CourseSelectionRow {

        /** 选用单位 */
        private String selectUnit;

        /** 选用专业类 */
        private String selectMajorCategory;

        /** 选用专业 */
        private String selectMajor;

        /** 各学期学时(讲授+实践)，长度8 */
        private Double[] termHours = {0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0};

        public CourseSelectionRow() {
        }

        public CourseSelectionRow(String selectUnit, String selectMajorCategory, String selectMajor) {
            this.selectUnit = selectUnit;
            this.selectMajorCategory = selectMajorCategory;
            this.selectMajor = selectMajor;
        }

        /**
         * 给指定学期累加学时
         *
         * @param termIndex 学期下标 0-7
         * @param hours     学时
         */
        public void addTermHours(int termIndex, Double hours) {
            if (termIndex < 0 || termIndex > 7 || hours == null) {
                return;
            }
            this.termHours[termIndex] += hours;
        }

        public String getSelectUnit() {
            return selectUnit;
        }

        public void setSelectUnit(String selectUnit) {
            this.selectUnit = selectUnit;
        }

        public String getSelectMajorCategory() {
            return selectMajorCategory;
        }

        public void setSelectMajorCategory(String selectMajorCategory) {
            this.selectMajorCategory = selectMajorCategory;
        }

        public String getSelectMajor() {
            return selectMajor;
        }

        public void setSelectMajor(String selectMajor) {
            this.selectMajor = selectMajor;
        }

        public Double[] getTermHours() {
            return termHours;
        }

        public void setTermHours(Double[] termHours) {
            this.termHours = termHours;
        }
    }
}
