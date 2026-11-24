package com.doinner.csys.utils;

import com.doinner.csys.domain.Course;
import com.doinner.csys.entity.csys.model.DictContent;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/** Invocation scope is distinct from a source course's applicability arrays. */
public final class CourseInvocationScope {
    private CourseInvocationScope() { }

    public static boolean hasExplicitDirections(Course course) {
        return course.getInvocationSubMajorIds() != null || course.getSubMajorId() != null;
    }

    public static boolean containsDirection(Course course, Long directionId) {
        if (course.getInvocationSubMajorIds() != null) return course.getInvocationSubMajorIds().contains(directionId);
        return course.getSubMajorId() == null || Objects.equals(course.getSubMajorId(), directionId);
    }

    public static boolean isDirectionScoped(Course course) {
        return "2".equals(course.getType()) || "4".equals(course.getType())
                || DictContent.MAJOR_COURSES_SCHEDULE.equals(course.getCourseModule())
                || (DictContent.MILITARY_VOCATIONAL_EDUCATION.equals(course.getCourseModule())
                && (DictContent.POSITION_FOUNDATION.equals(course.getCourseModuleChildren())
                || DictContent.DUTY_POSITION.equals(course.getCourseModuleChildren())));
    }

    public static boolean sameSourceScope(Course left, Course right) {
        return left.getSourceId() != null && Objects.equals(left.getSourceId(), right.getSourceId())
                && Objects.equals(left.getMajorId(), right.getMajorId())
                && Objects.equals(left.getCourseModule(), right.getCourseModule())
                && Objects.equals(left.getCourseModuleChildren(), right.getCourseModuleChildren())
                && Objects.equals(left.getLocation(), right.getLocation())
                && Objects.equals(left.getProgramLevel(), right.getProgramLevel());
    }

    public static <T extends Course> List<T> forDirection(List<T> courses, Long directionId, Long majorId) {
        List<T> result = new ArrayList<>();
        for (T course : courses) {
            if (directionId == null || "2".equals(course.getType())) {
                result.add(course);
                continue;
            }
            if (hasExplicitDirections(course)) {
                if (containsDirection(course, directionId)) result.add(course);
                continue;
            }
            if (isDirectionScoped(course) && course.getMajorId() != null
                    && majorId != null && !Objects.equals(course.getMajorId(), majorId)) continue;
            boolean overridden = courses.stream().anyMatch(other ->
                    hasExplicitDirections(other) && containsDirection(other, directionId) && sameSourceScope(course, other));
            if (!overridden) result.add(course);
        }
        return result;
    }
}
