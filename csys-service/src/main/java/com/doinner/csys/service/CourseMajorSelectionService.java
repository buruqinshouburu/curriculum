package com.doinner.csys.service;

import com.doinner.csys.dao.StandardMajorMapper;
import com.doinner.csys.domain.Course;
import com.doinner.csys.domain.StandardMajor;
import com.doinner.csys.domain.vo.TrainingSchemeVo;
import com.doinner.csys.entity.csys.model.DictContent;
import com.doinner.csys.permission.CoursePermissionException;
import com.doinner.csys.utils.CourseInvocationScope;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class CourseMajorSelectionService {
    private final StandardMajorMapper majorMapper;

    public CourseMajorSelectionService(StandardMajorMapper majorMapper) {
        this.majorMapper = majorMapper;
    }

    public void validateInvocation(Course request, Course stored, List<TrainingSchemeVo> schemes) {
        List<Long> directions = request.getInvocationSubMajorIds();
        if (directions == null) return;
        boolean unchanged = stored != null && stored.getInvocationSubMajorIds() != null
                && new LinkedHashSet<>(directions).equals(new LinkedHashSet<>(stored.getInvocationSubMajorIds()));
        String module = request.getCourseModule() != null ? request.getCourseModule() : stored == null ? null : stored.getCourseModule();
        String child = request.getCourseModuleChildren() != null ? request.getCourseModuleChildren()
                : stored == null ? null : stored.getCourseModuleChildren();
        if (stored == null || stored.getSourceId() == null || stored.getMajorId() == null
                || (!unchanged && (!DictContent.MILITARY_VOCATIONAL_EDUCATION.equals(module)
                || !(DictContent.POSITION_FOUNDATION.equals(child) || DictContent.DUTY_POSITION.equals(child))))
                || schemes.isEmpty()) {
            throw new CoursePermissionException(400, "仅方案中的军士任职基础/任职岗位引用课程可设置调用方向");
        }
        if (directions.isEmpty() || directions.stream().anyMatch(id -> id == null || id <= 0)) {
            throw new CoursePermissionException(400, "请至少选择一个有效的调用方向");
        }
        Map<Long, StandardMajor> options = majorMapper.selectStandardMajorByIds(new ArrayList<>(new LinkedHashSet<>(directions)))
                .stream().collect(Collectors.toMap(StandardMajor::getId, Function.identity()));
        for (TrainingSchemeVo scheme : schemes) {
            if (scheme == null || !Objects.equals(stored.getMajorId(), scheme.getMajorId())
                    || (request.getMajorId() != null && !Objects.equals(request.getMajorId(), scheme.getMajorId()))) {
                throw new CoursePermissionException(400, "调用课程不属于方案专业");
            }
            for (Long id : directions) {
                if (!options.containsKey(id) || !Objects.equals(options.get(id).getParentId(), scheme.getMajorId())
                        || (scheme.getSubMajorIds() != null && !scheme.getSubMajorIds().isEmpty()
                        && !scheme.getSubMajorIds().contains(id))) {
                    throw new CoursePermissionException(400, "调用方向不属于方案专业或所选方向");
                }
            }
        }
    }

    public void validate(Course course) {
        validate(course, null);
    }

    public void validate(Course course, Course stored) {
        String module = course.getCourseModule() != null || stored == null ? course.getCourseModule() : stored.getCourseModule();
        String child = course.getCourseModuleChildren() != null || stored == null
                || !Objects.equals(stored.getCourseModule(), module) ? course.getCourseModuleChildren() : stored.getCourseModuleChildren();
        if (DictContent.GENERAL_EDUCATION_COURSES_SCHEDULE.equals(module)
                || DictContent.DISCIPLINE_CORE_COURSES_SCHEDULE.equals(module)
                || (stored != null && stored.getSourceId() != null && stored.getMajorId() != null)
                || (isMilitaryModule(module) && !Arrays.asList(DictContent.POSITION_FOUNDATION, DictContent.DUTY_POSITION,
                "69a7f3982dc370362ef3ee79", "69a7f39e2dc370362ef3ee7a").contains(child))
                || (stored == null && module != null && !isMilitaryModule(module) && !DictContent.MAJOR_COURSES_SCHEDULE.equals(module))) return;
        List<Long> majors = course.getMajorIds();
        List<Long> directions = course.getSubMajorIds();
        if (majors == null && directions == null) return;
        List<Long> ids = new ArrayList<>();
        if (majors != null) ids.addAll(majors);
        if (directions != null) ids.addAll(directions);
        if (ids.isEmpty()) return;
        if (ids.stream().anyMatch(id -> id == null || id <= 0)) {
            throw new CoursePermissionException(400, "专业类或专业方向无效，请重新选择");
        }
        Map<Long, StandardMajor> options = majorMapper.selectStandardMajorByIds(new ArrayList<>(new LinkedHashSet<>(ids)))
                .stream().collect(Collectors.toMap(StandardMajor::getId, Function.identity()));
        if (!options.keySet().containsAll(ids)) {
            throw new CoursePermissionException(400, "专业类或专业方向不存在，请刷新后重新选择");
        }
        if (majors != null && !majors.isEmpty() && directions != null) {
            for (Long id : directions) {
                if (!majors.contains(options.get(id).getParentId())) {
                    throw new CoursePermissionException(400, "专业方向不属于所选专业类");
                }
            }
        }
        if (majors != null && course.getCategoryId() != null) {
            for (Long id : majors) {
                if (!Objects.equals(course.getCategoryId(), options.get(id).getCategoryId())) {
                    throw new CoursePermissionException(400, "专业类不属于所选学科门类");
                }
            }
        }
    }

    private static boolean isMilitaryModule(String module) {
        return Arrays.asList(DictContent.MILITARY_VOCATIONAL_EDUCATION, DictContent.POLITICAL_THEORY_NCO,
                DictContent.MILITARY_FOUNDATION_NCO, DictContent.POSITION_FOUNDATION, DictContent.DUTY_POSITION,
                "69a7f3922dc370362ef3ee78", "69a7f3982dc370362ef3ee79", "69a7f39e2dc370362ef3ee7a").contains(module);
    }

    public void validateInvocationOverlap(Course request, Course stored, List<Course> siblings) {
        if (request.getInvocationSubMajorIds() == null) return;
        for (Long direction : request.getInvocationSubMajorIds()) {
            // Existing explicit overlaps are historical data, not a reason to block an unrelated edit.
            if (CourseInvocationScope.sameSourceScope(request, stored)
                    && CourseInvocationScope.hasExplicitDirections(stored)
                    && CourseInvocationScope.containsDirection(stored, direction)) continue;
            for (Course sibling : siblings) {
                if (!Objects.equals(stored.getId(), sibling.getId())
                        && CourseInvocationScope.sameSourceScope(request, sibling)
                        && CourseInvocationScope.hasExplicitDirections(sibling)
                        && CourseInvocationScope.containsDirection(sibling, direction)) {
                    throw new CoursePermissionException(400, "新增调用方向已存在同源课程，请保留现有课程分别编辑");
                }
            }
        }
    }

    public void resolveNames(List<Course> courses) {
        LinkedHashSet<Long> ids = new LinkedHashSet<>();
        for (Course course : courses) {
            ids.addAll(directionIds(course));
            if (course.getSourceId() != null && course.getInvocationSubMajorIds() != null) ids.addAll(course.getInvocationSubMajorIds());
            if (course.getMajorIds() != null) ids.addAll(course.getMajorIds());
        }
        if (ids.isEmpty()) return;
        Map<Long, String> names = majorMapper.selectStandardMajorByIds(new ArrayList<>(ids)).stream()
                .collect(Collectors.toMap(StandardMajor::getId, StandardMajor::getName));
        for (Course course : courses) {
            applyNames(course, names);
        }
    }

    public static void applyNames(Course course, Map<Long, String> names) {
        course.setSubMajorNames(directionIds(course).stream().map(names::get).filter(Objects::nonNull)
                .collect(Collectors.toList()));
        if ((isMultiple(course) || course.getSourceId() != null) && course.getSubMajorIds() != null) course.setSubMajorName(String.join("、", course.getSubMajorNames()));
        if (course.getSourceId() != null && course.getInvocationSubMajorIds() != null) {
            course.setInvocationSubMajorNames(course.getInvocationSubMajorIds().stream().map(names::get)
                    .filter(Objects::nonNull).collect(Collectors.toList()));
            course.setSubMajorName(String.join("、", course.getInvocationSubMajorNames()));
        }
        if (isMultiple(course) && course.getMajorIds() != null) {
            course.setMajorName(course.getMajorIds().stream().map(names::get).filter(Objects::nonNull)
                    .collect(Collectors.joining("、")));
        }
    }

    private static List<Long> directionIds(Course course) {
        if (course.getSourceId() != null && !isMultiple(course) && course.getSubMajorId() != null) {
            return Collections.singletonList(course.getSubMajorId());
        }
        if ((isMultiple(course) || course.getSourceId() != null) && course.getSubMajorIds() != null) return course.getSubMajorIds();
        return course.getSubMajorId() == null ? Collections.emptyList() : Collections.singletonList(course.getSubMajorId());
    }

    private static boolean isMultiple(Course course) {
        return DictContent.DISCIPLINE_CORE_COURSES_SCHEDULE.equals(course.getCourseModule())
                || (DictContent.MAJOR_COURSES_SCHEDULE.equals(course.getCourseModule()) && "3".equals(course.getCourseAttr()));
    }
}
