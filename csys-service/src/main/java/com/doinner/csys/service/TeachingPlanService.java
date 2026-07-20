package com.doinner.csys.service;

import com.doinner.csys.domain.TeachingPlanSection;
import com.doinner.csys.domain.TeachingPlanTeacher;
import com.doinner.csys.domain.vo.CourseQuoteMajorVo;
import com.doinner.csys.domain.vo.TeachingPlanDetailVo;
import com.doinner.csys.domain.vo.TeachingPlanListVo;
import com.doinner.csys.domain.vo.TeachingPlanQueryVo;
import com.doinner.csys.domain.vo.TeachingPlanSaveVo;
import com.doinner.file.api.domain.FileInfo;

import java.util.List;

/**
 * 课程教学计划 Service（主流程：列表/详情/保存/Word/教员/章节）。
 * 培养方案 tab 不再使用 context 表，见 {@link TeachingPlanModuleService#listSchemes(Long)}。
 */
public interface TeachingPlanService {

    List<TeachingPlanListVo> selectTeachingPlanPage(TeachingPlanQueryVo query);

    TeachingPlanDetailVo getDetail(Long courseId, Long teachingPlanId);

    /**
     * 保存教学计划主表。plan.id 为空时按 (sourceCourseId, planType) 查重。
     * 不再接收/写入 context 表。
     */
    Long saveTeachingPlan(TeachingPlanSaveVo saveVo);

    FileInfo generateTeachingPlanWord(Long courseId);

    FileInfo generateTeachingPlanWord(Long courseId, Long planId);

    List<TeachingPlanTeacher> listTeacher(Long planId);

    Long addTeacher(TeachingPlanTeacher teacher);

    void updateTeacher(TeachingPlanTeacher teacher);

    void deleteTeacher(Long id);

    List<TeachingPlanSection> listSection(Long planId);

    Long addSection(TeachingPlanSection section);

    void updateSection(TeachingPlanSection section);

    void deleteSection(Long id);

    List<CourseQuoteMajorVo> listQuoteMajors(Long courseId);

    /** 逻辑删除教学计划；审核中/已通过不可删 */
    void deleteTeachingPlan(Long planId);
}
