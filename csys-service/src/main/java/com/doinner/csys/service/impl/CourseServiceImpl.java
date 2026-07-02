package com.doinner.csys.service.impl;

import com.doinner.common.core.domain.Message;
import com.doinner.common.core.utils.PageUtils;
import com.doinner.csys.constant.CourseConstant;
import com.doinner.csys.dao.*;
import com.doinner.csys.domain.Course;
import com.doinner.csys.domain.CourseInvokeDeleteLog;
import com.doinner.csys.domain.CourseRefGraduation;
import com.doinner.csys.domain.StandardGraduation;
import com.doinner.csys.domain.TrainingSchemeRefCourse;
import com.doinner.csys.domain.vo.*;
import com.doinner.csys.entity.csys.model.CourseChooseStatusModel;
import com.doinner.csys.entity.csys.po.CourseRefKnowledgeUnit;
import com.doinner.csys.io.service.ExportService;
import com.doinner.csys.service.CourseService;
import com.doinner.csys.service.TrainingService;
import com.doinner.csys.utils.UserUtils;
import com.doinner.kg.domain.Dictionary;
import com.doinner.kg.service.RemoteKgService;
import com.doinner.common.security.utils.SecurityUtils;
import com.doinner.system.domain.view.LoginUser;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class CourseServiceImpl implements CourseService {
    @Resource
    protected CourseMapper courseMapper;
    @Resource
    private CourseKnowledgeUnitMapper courseknowledgeUnitMapper;
    @Resource
    private CourseKnowledgePointMapper courseknowledgePointMapper;
    @Resource
    private CourseRefKnowledgeUnitMapper courseRefKnowledgeUnitMapper;
    @Resource
    private TrainingSchemeCourseScheduleMapper trainingSchemeCourseScheduleMapper;
    @Resource
    private TrainingSchemeRefCourseMapper trainingSchemeRefCourseMapper;
    @Resource
    private TrainingSchemeMapper trainingSchemeMapper;
    @Resource
    private TrainingService trainingService;
    @Resource
    private CourseRefGraduationMapper courseRefGraduationMapper;
    @Resource
    private RemoteKgService remoteKgService;
    @Resource
    private StandardGraduationMapper standardGraduationMapper;

    @Resource
    private CourseInvokeDeleteLogMapper courseInvokeDeleteLogMapper;

    @Resource
    private ExportService exportService;

    @Override
    @Transactional
    public void removeStorageCourse(List<Long> ids) {
        List<Course> courses = courseMapper.selectCoursesByIds(ids);
        for (Course course : courses) {
            UserUtils.checkDataPermission(course);
            //检查课程是否被调用
            List<Course> courseList=courseMapper.selectCourseBySourceId(course.getId());
            if(ObjectUtils.isNotEmpty(courseList)){
                throw new RuntimeException("课程已被调用，不能删除！");
            }
        }
        //删除关联知识单元知识点
        List<CourseRefKnowledgeUnit> courseRefKnowledgeUnits = courseRefKnowledgeUnitMapper.selectByCourseIds(ids);
        List<Long> unitIds = courseRefKnowledgeUnits.stream().map(c -> c.getCourseUnitId()).collect(Collectors.toList());
        if(ObjectUtils.isNotEmpty(unitIds)){
            courseknowledgePointMapper.deleteByUnitIds(unitIds);
            courseknowledgeUnitMapper.deleteByIds(unitIds);
            courseRefKnowledgeUnitMapper.deleteByCourseIds(ids);
        }
        courseMapper.deleteCourseByIds(ids);
    }

    @Override
    @Transactional
    public void removeInvokeCourse(List<Long> ids) {
        List<Course> courses = courseMapper.selectCoursesByIds(ids);
        for (Course course : courses) {
            UserUtils.checkDataPermission(course);
        }
        //记录调用课程删除日志，便于追溯调用课程被删除的问题
        recordInvokeCourseDeleteLog(courses);
        //删除关联知识单元
        courseRefKnowledgeUnitMapper.deleteByCourseIds(ids);
        //删除课程关联培养方案以及课表
        trainingSchemeCourseScheduleMapper.deleteByCourseIds(ids);
        trainingSchemeRefCourseMapper.deleteTrainingSchemeRefCourseByCourseIds(ids);
        //删除课程
        courseMapper.removeCourseByIds(ids);
    }

    /**
     * 记录调用课程删除日志。
     * 同一次删除操作共用一个 deleteBatchId，便于按批次回溯。
     *
     * @param courses 本次被删除的调用课程集合
     */
    private void recordInvokeCourseDeleteLog(List<Course> courses) {
        if (CollectionUtils.isEmpty(courses)) {
            return;
        }
        //按课程id反查所属培养方案，建立 courseId -> schemeId 映射
        List<Long> courseIds = courses.stream().map(Course::getId).collect(Collectors.toList());
        List<TrainingSchemeRefCourse> refCourses = trainingSchemeRefCourseMapper.selectByCourseIds(courseIds);
        Map<Long, Long> courseIdToSchemeIdMap = new HashMap<>();
        if (CollectionUtils.isNotEmpty(refCourses)) {
            for (TrainingSchemeRefCourse refCourse : refCourses) {
                courseIdToSchemeIdMap.put(refCourse.getCourseId(), refCourse.getSchemeId());
            }
        }
        String operator = currentOperator();
        LocalDateTime operationTime = LocalDateTime.now();
        String deleteBatchId = UUID.randomUUID().toString().replace("-", "");
        List<CourseInvokeDeleteLog> logs = new ArrayList<>();
        for (Course course : courses) {
            CourseInvokeDeleteLog log = new CourseInvokeDeleteLog();
            log.setDeleteBatchId(deleteBatchId);
            log.setCourseId(course.getId());
            log.setCourseName(course.getName());
            log.setCourseCode(course.getCode());
            log.setSourceId(course.getSourceId());
            log.setSchemeId(courseIdToSchemeIdMap.get(course.getId()));
            log.setTemplateType(course.getTemplateType());
            log.setMajorId(course.getMajorId());
            log.setCategoryId(course.getCategoryId());
            log.setVersion(course.getVersion());
            log.setOperator(operator);
            log.setOperationTime(operationTime);
            log.setRemark("删除调用课程");
            logs.add(log);
        }
        courseInvokeDeleteLogMapper.insertBatch(logs);
    }

    /**
     * 获取当前操作人用户名，与 UserUtils 中的取值逻辑保持一致。
     */
    private String currentOperator() {
        try {
            String userName = SecurityUtils.getUsername();
            if (StringUtils.isBlank(userName)) {
                LoginUser loginUser = SecurityUtils.getLoginUser();
                if (loginUser != null && StringUtils.isNotBlank(loginUser.getUsername())) {
                    userName = loginUser.getUsername();
                }
            }
            return userName;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    @Transactional
    public void disableCourse(Long courseId) {
        courseMapper.updateEnableFlag(courseId, CourseConstant.COURSE_ENABLE_FLAG_DISABLE);
    }

    @Override
    @Transactional
    public void startCourse(Long courseId) {
        courseMapper.updateEnableFlag(courseId,CourseConstant.COURSE_ENABLE_FLAG_START);
    }

    @Override
    @Transactional
    public Message courseInvoke(CourseTemplateVo courseTemplateVo) {
        //查询该培养方案是否已经调用该课程
        List<TrainingSchemeRefCourse> trainingSchemeRefCourses = trainingSchemeRefCourseMapper.selectTrainingSchemeRefCourseByTrainingSchemeVoId(courseTemplateVo.getTrainingSchemeId());
        List<Long> hasInvokeCourseIds = trainingSchemeRefCourses.stream().map(t -> t.getCourseId()).collect(Collectors.toList());
        List<Long> quoteCourseIds=courseTemplateVo.getIds();
        if(ObjectUtils.isNotEmpty(hasInvokeCourseIds)){
            List<Course> hasInvokeCourses = courseMapper.selectCoursesByIds(hasInvokeCourseIds);
            List<Long> quotedCourseIdList=hasInvokeCourses.stream().map(c->c.getSourceId()).collect(Collectors.toList());
            //过滤已经调用过的课程
            quoteCourseIds = quoteCourseIds.stream().filter(courseId -> !quotedCourseIdList.contains(courseId)).collect(Collectors.toList());
            if(ObjectUtils.isEmpty(quoteCourseIds)){
                return Message.error("所选课程均已被调用");
            }
        }
        //查询未被调用的课程
        List<Course> quoteCourseList = courseMapper.selectCoursesByIds(quoteCourseIds);
        //查询培养方案
        TrainingSchemeVo trainingSchemeVo = trainingSchemeMapper.selectTrainingSchemeById(courseTemplateVo.getTrainingSchemeId());

        /**复制课程*/
        List<Course> new_courseList =new ArrayList<>();
        quoteCourseList.forEach(c -> {
            Course course = copyCourse(trainingSchemeVo, c);
            new_courseList.add(course);
        });
        //课程批量保存
        courseMapper.insertCourseList(new_courseList);
        //新旧课程id对应 sourceId-旧ID id-新ID
        Map<Long, Long> oldIdToNewIdMap = new_courseList.stream().collect(Collectors.toMap(Course::getSourceId, Course::getId));
        /**复制知识单元关联关系*/
        List<CourseRefKnowledgeUnit> courseRefKnowledgeUnits = courseRefKnowledgeUnitMapper.selectByCourseIds(quoteCourseIds);
        if(ObjectUtils.isNotEmpty(courseRefKnowledgeUnits)){
            List<CourseRefKnowledgeUnit> new_courseRefKnowledgeUnitList = new ArrayList<>();
            for (CourseRefKnowledgeUnit courseRefKnowledgeUnit : courseRefKnowledgeUnits) {
                courseRefKnowledgeUnit.setCourseId(oldIdToNewIdMap.get(courseRefKnowledgeUnit.getCourseId()));
                new_courseRefKnowledgeUnitList.add(courseRefKnowledgeUnit);
            }
            courseRefKnowledgeUnitMapper.insertBatch(new_courseRefKnowledgeUnitList);
        }
        /**创建课程与培养方案关系并排课*/
        //创建培养方案课程关联
        List<TrainingSchemeRefCourse> new_trainingSchemeRefCourses = new ArrayList<>();
        for (Course course : new_courseList) {
            TrainingSchemeRefCourse trainingSchemeRefCourse = new TrainingSchemeRefCourse();
            trainingSchemeRefCourse.setSchemeId(trainingSchemeVo.getId());
            trainingSchemeRefCourse.setCourseId(course.getId());
            new_trainingSchemeRefCourses.add(trainingSchemeRefCourse);
        }
        if (CollectionUtils.isNotEmpty(new_trainingSchemeRefCourses)) {
            trainingSchemeRefCourseMapper.insertTrainingSchemeRefCourses(new_trainingSchemeRefCourses);
        }
        //排课
        trainingService.setCourseSchedule(trainingSchemeVo,new_courseList);
        return Message.success();
    }

    @Override
    @Transactional
    public Message boundGraduation(CourseBoundGraduationVo courseBoundGraduationVo) {
        //删除现有关联
        courseRefGraduationMapper.deleteCourseRefGraduationByCourseId(courseBoundGraduationVo.getCourseId());
        if(ObjectUtils.isEmpty(courseBoundGraduationVo.getGraduationIds())){
            //解除了所有毕业要求绑定
            courseMapper.updateBindStatusById(courseBoundGraduationVo.getCourseId(),CourseConstant.COURSE_BIND_STATUS_FALSE);
            return Message.success();
        }
        //新建关系
        ArrayList<CourseRefGraduation> courseRefGraduations = new ArrayList<>();
        for (Long graduationId : courseBoundGraduationVo.getGraduationIds()) {
            CourseRefGraduation courseRefGraduation = new CourseRefGraduation();
            courseRefGraduation.setCourseId(courseBoundGraduationVo.getCourseId());
            courseRefGraduation.setGraduationId(graduationId);
            courseRefGraduations.add(courseRefGraduation);
        }
        courseRefGraduationMapper.insertCourseTargetRefGraduationList(courseRefGraduations);
        //修改课程绑定状态
        courseMapper.updateBindStatusById(courseBoundGraduationVo.getCourseId(), CourseConstant.COURSE_BIND_STATUS_TRUE);
        return Message.success();
    }

    @Override
    public List<CourseKnowledgeViewVo> viewCourseKnowledgeList(Long trainingSchemeId) {
        PageUtils.startPage();
        List<CourseKnowledgeViewVo> courseKnowledgeViewVos = courseMapper.selectCourseKnowledgeList(trainingSchemeId);
        //替换字典值
        for (CourseKnowledgeViewVo courseKnowledgeViewVo : courseKnowledgeViewVos) {
            Dictionary data = remoteKgService.getDictionary(courseKnowledgeViewVo.getCourseModelId()).getData();
            courseKnowledgeViewVo.setCourseModelName(data.getName());
        }
        return courseKnowledgeViewVos;
    }

    @Override
    public Map<String,List<Long>> courseGraduation(Long courseId) {
        List<CourseRefGraduation> courseRefGraduations = courseRefGraduationMapper.selectCourseRefGraduationByCourseId(courseId);
        HashMap<String, List<Long>> result = new HashMap<>();
        result.put("graduationIds",courseRefGraduations.stream().map(CourseRefGraduation::getGraduationId).collect(Collectors.toList()));
        return result;
    }

    @Override
    public List<StandardGraduation> viewCourseGraduation(Long courseId) {
        List<CourseRefGraduation> courseRefGraduations = courseRefGraduationMapper.selectCourseRefGraduationByCourseId(courseId);
        if (CollectionUtils.isEmpty(courseRefGraduations)) {
            return new ArrayList<>();
        }
        List<StandardGraduation> standardGraduations = standardGraduationMapper.selectStandardGraduationByIds(courseRefGraduations.stream().map(CourseRefGraduation::getGraduationId).collect(Collectors.toList()));
        return standardGraduations;
    }

    @Override
    public CourseChooseStatusModel getCourseChooseStatus(Long sourceCourseId) {
        if (sourceCourseId == null) {
            return null;
        }
        List<CourseChooseStatusModel> models = exportService.buildCourseChooseStatusModels(Collections.singletonList(sourceCourseId));
        return ObjectUtils.isEmpty(models) ? null : models.get(0);
    }

    @NotNull
    private Course copyCourse(TrainingSchemeVo trainingSchemeVo, Course c) {
        Course course = new Course();
        BeanUtils.copyProperties(c, course);
        course.setSourceId(c.getId());
        course.setMajorId(c.getMajorId() == null ? trainingSchemeVo.getMajorId() : c.getMajorId());
        course.setCategoryId(c.getCategoryId() == null ? trainingSchemeVo.getCategoryId() : c.getCategoryId());
        course.setVersion(trainingSchemeVo.getVersion());
        course.setTemplateType(2);
        course.setId(null);
        UserUtils.clearAndRefreshObj(course);
        return course;
    }
}
