package com.doinner.csys.service.impl;

import com.doinner.csys.dao.TeachingPlanMapper;
import com.doinner.csys.dao.TeachingPlanContextMapper;
import com.doinner.csys.dao.TeachingPlanSectionMapper;
import com.doinner.csys.dao.TeachingPlanTeacherMapper;
import com.doinner.csys.domain.TeachingPlan;
import com.doinner.csys.domain.TeachingPlanContext;
import com.doinner.csys.domain.TeachingPlanSection;
import com.doinner.csys.domain.TeachingPlanTeacher;
import com.doinner.csys.domain.vo.TeachingPlanDetailVo;
import com.doinner.csys.domain.vo.TeachingPlanListVo;
import com.doinner.csys.domain.vo.TeachingPlanQueryVo;
import com.doinner.csys.domain.vo.TeachingPlanSaveVo;
import com.doinner.csys.service.TeachingPlanService;
import com.doinner.csys.utils.UserUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
 * 课程教学计划 Service 实现。
 *
 * @author codex
 */
@Service
@Transactional(readOnly = true)
public class TeachingPlanServiceImpl implements TeachingPlanService {

    @Resource
    private TeachingPlanMapper teachingPlanMapper;

    @Resource
    private TeachingPlanContextMapper teachingPlanContextMapper;

    @Resource
    private TeachingPlanTeacherMapper teachingPlanTeacherMapper;

    @Resource
    private TeachingPlanSectionMapper teachingPlanSectionMapper;

    @Override
    public List<TeachingPlanListVo> selectTeachingPlanPage(TeachingPlanQueryVo query) {
        if (query == null) {
            query = new TeachingPlanQueryVo();
        }
        return teachingPlanMapper.selectTeachingPlanPage(query);
    }

    @Override
    public TeachingPlanDetailVo getDetail(Long courseId, Long teachingPlanId) {
        // 教学计划id存在则取教学计划表 + 调用课程上下文；否则从总库课程取
        if (teachingPlanId != null) {
            return teachingPlanMapper.selectDetailByPlanId(teachingPlanId);
        }
        return teachingPlanMapper.selectDetailByCourseId(courseId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long saveTeachingPlan(TeachingPlanSaveVo saveVo) {
        if (saveVo == null || saveVo.getPlan() == null) {
            throw new IllegalArgumentException("教学计划信息不能为空");
        }
        TeachingPlan plan = saveVo.getPlan();
        UserUtils.reflash(plan);
        if (plan.getId() == null) {
            teachingPlanMapper.insert(plan);
        } else {
            teachingPlanMapper.updateById(plan);
        }

        TeachingPlanContext context = saveVo.getContext();
        if (context != null) {
            context.setPlanId(plan.getId());
            UserUtils.reflash(context);
            if (context.getId() == null) {
                teachingPlanContextMapper.insert(context);
            } else {
                teachingPlanContextMapper.updateById(context);
            }
        }
        return plan.getId();
    }

    // ============ 教员团队 ============

    @Override
    public List<TeachingPlanTeacher> listTeacher(Long planId) {
        return teachingPlanTeacherMapper.selectByPlanId(planId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long addTeacher(TeachingPlanTeacher teacher) {
        UserUtils.reflash(teacher);
        teachingPlanTeacherMapper.insert(teacher);
        return teacher.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateTeacher(TeachingPlanTeacher teacher) {
        UserUtils.reflash(teacher);
        teachingPlanTeacherMapper.updateById(teacher);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteTeacher(Long id) {
        teachingPlanTeacherMapper.deleteById(id);
    }

    // ============ 课程章节 ============

    @Override
    public List<TeachingPlanSection> listSection(Long planId) {
        return teachingPlanSectionMapper.selectByPlanId(planId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long addSection(TeachingPlanSection section) {
        UserUtils.reflash(section);
        teachingPlanSectionMapper.insert(section);
        return section.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateSection(TeachingPlanSection section) {
        UserUtils.reflash(section);
        teachingPlanSectionMapper.updateById(section);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteSection(Long id) {
        teachingPlanSectionMapper.deleteById(id);
    }
}
