package com.doinner.csys.utils;

import com.doinner.common.core.exception.DataFormatException;
import com.doinner.csys.domain.Course;
import com.doinner.csys.domain.CourseSchedule;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/** Credit calculations for source-library writes; authorization stays with the caller. */
public final class CourseCreditRules {
    private static final BigDecimal HOURS_PER_CREDIT = BigDecimal.valueOf(16);
    private static final BigDecimal HALF = new BigDecimal("0.5");

    private CourseCreditRules() {}

    public static void normalize(Course course) {
        normalize(course, null);
    }

    public static void normalize(Course course, Course stored) {
        if (course.getSourceId() != null || (stored != null && stored.getSourceId() != null)) return;
        String type = course.getType() != null ? course.getType() : stored == null ? null : stored.getType();
        String attribute = course.getCourseAttr() != null ? course.getCourseAttr()
                : stored == null ? null : stored.getCourseAttr();
        if ("1".equals(type) || "3".equals(type)) {
            normalizeHours(course, "3".equals(attribute));
        } else if ("2".equals(type) || "4".equals(type)) {
            normalizeTime(course);
        }
    }

    private static void normalizeHours(Course course, boolean firstOnly) {
        List<CourseSchedule> rows = course.getCourseScheduleList();
        BigDecimal lecture = number(course.getTeachHours(), true, "讲授学时");
        BigDecimal practice = number(course.getPracticeHours(), true, "实践学时");
        if (rows != null && !rows.isEmpty()) {
            lecture = BigDecimal.ZERO;
            practice = BigDecimal.ZERO;
            for (int i = 0; i < rows.size(); i++) {
                CourseSchedule row = requireRow(rows.get(i), i);
                String field = "第" + (i + 1) + "组安排";
                BigDecimal rowLecture = number(row.getTeachHours(), true, field + "讲授学时");
                BigDecimal rowPractice = number(row.getPracticeHours(), true, field + "实践学时");
                if (!firstOnly || i == 0) {
                    lecture = lecture.add(rowLecture);
                    practice = practice.add(rowPractice);
                }
            }
        } else if (course.getTeachHours() == null && course.getPracticeHours() == null) {
            return;
        }
        BigDecimal total = lecture.add(practice);
        course.setTeachHours(lecture.doubleValue());
        course.setPracticeHours(practice.doubleValue());
        course.setHours(total.doubleValue());
        course.setCredit(round(total.divide(HOURS_PER_CREDIT)));
    }

    private static void normalizeTime(Course course) {
        List<CourseSchedule> rows = course.getCourseScheduleList();
        BigDecimal totalTime = BigDecimal.ZERO;
        BigDecimal rawCredits = BigDecimal.ZERO;
        String firstUnit;
        if (rows != null && !rows.isEmpty()) {
            firstUnit = unit(requireRow(rows.get(0), 0).getUnit());
            for (int i = 0; i < rows.size(); i++) {
                CourseSchedule row = requireRow(rows.get(i), i);
                String rowUnit = unit(row.getUnit());
                BigDecimal time = number(row.getTimeWeek(), "2".equals(rowUnit), "第" + (i + 1) + "组安排时间");
                totalTime = totalTime.add(time);
                rawCredits = rawCredits.add("2".equals(rowUnit) ? time.divide(HOURS_PER_CREDIT) : time);
            }
        } else {
            if (course.getTimeWeek() == null) return;
            firstUnit = unit(course.getUnit());
            totalTime = number(course.getTimeWeek(), "2".equals(firstUnit), "时间安排");
            rawCredits = "2".equals(firstUnit) ? totalTime.divide(HOURS_PER_CREDIT) : totalTime;
        }
        // Mixed-unit top-level time is a raw subtotal, never an input to credit conversion.
        course.setTimeWeek(totalTime.doubleValue());
        course.setUnit(firstUnit);
        course.setCredit(round(rawCredits));
    }

    private static CourseSchedule requireRow(CourseSchedule row, int index) {
        if (row == null) throw new DataFormatException("第" + (index + 1) + "组课程安排不能为空");
        return row;
    }

    private static String unit(String unit) {
        if (unit == null || unit.isEmpty()) return "1";
        if (!"1".equals(unit) && !"2".equals(unit))
            throw new DataFormatException("时间安排单位必须为周(1)或学时(2)");
        return unit;
    }

    private static BigDecimal number(Double value, boolean integer, String field) {
        if (value == null) return BigDecimal.ZERO;
        if (!Double.isFinite(value) || value < 0 || (integer && value != Math.rint(value)))
            throw new DataFormatException(field + (integer ? "必须为非负整数" : "必须为非负数"));
        return BigDecimal.valueOf(value);
    }

    private static double round(BigDecimal value) {
        BigDecimal whole = value.setScale(0, RoundingMode.DOWN);
        int comparison = value.subtract(whole).compareTo(HALF);
        return (comparison == 0 ? whole.add(HALF) : comparison > 0 ? whole.add(BigDecimal.ONE) : whole)
                .doubleValue();
    }
}
