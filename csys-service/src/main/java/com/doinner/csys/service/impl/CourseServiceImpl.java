package com.doinner.csys.service.impl;

import com.doinner.common.core.domain.Message;
import com.doinner.common.core.utils.PageUtils;
import com.doinner.csys.constant.CourseConstant;
import com.doinner.csys.dao.*;
import com.doinner.csys.domain.Course;
import com.doinner.csys.domain.CourseSchedule;
import com.doinner.csys.domain.CourseInvokeDeleteLog;
import com.doinner.csys.domain.CourseRefGraduation;
import com.doinner.csys.domain.StandardGraduation;
import com.doinner.csys.domain.TrainingSchemeCourseSchedule;
import com.doinner.csys.domain.TrainingSchemeRefCourse;
import com.doinner.csys.domain.vo.*;
import com.doinner.csys.entity.csys.model.CourseChooseStatusModel;
import com.doinner.csys.entity.csys.model.DictContent;
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
    private CourseScheduleMapper courseScheduleMapper;
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
            String targetCourseModule = courseTemplateVo.getCourseModule();
            String targetCourseModuleChildren = courseTemplateVo.getCourseModuleChildren();
            Long targetSubMajorId = courseTemplateVo.getSubMajorId();
            //过滤已经调用过的课程：课程模块为专业课程时同一专业方向(subMajor)下同一门课只能调用一次，
            //否则同一课程模块(及子模块)下同一门课只能调用一次
            quoteCourseIds = quoteCourseIds.stream()
                    .filter(courseId -> !isCourseAlreadyInvoked(courseId, hasInvokeCourses, targetCourseModule, targetCourseModuleChildren, targetSubMajorId))
                    .collect(Collectors.toList());
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
            Course course = copyCourse(trainingSchemeVo, c,courseTemplateVo);
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
        /**复制课程排课关联(t_csys_course_ref_schedule)，供后续 setCourseSchedule 排课取值*/
        for (Course course : new_courseList) {
            List<CourseSchedule> sourceScheduleList = course.getCourseScheduleList();
            if (CollectionUtils.isNotEmpty(sourceScheduleList)) {
                for (CourseSchedule sourceSchedule : sourceScheduleList) {
                    CourseSchedule newSchedule = new CourseSchedule();
                    BeanUtils.copyProperties(sourceSchedule, newSchedule);
                    newSchedule.setId(null);
                    newSchedule.setCourseId(course.getId());
                    courseScheduleMapper.insert(newSchedule);
                }
            }
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

    /**
     * 判断源课程在当前调用范围下是否已被该培养方案调用。
     * 课程模块为专业课程时按专业方向(subMajorId)判断同一门课是否已调用，
     * 其余课程模块按课程模块(courseModule)及子模块(courseModuleChildren)判断同一门课是否已调用。
     *
     * @param sourceCourseId           本次待调用的源课程id
     * @param invokedCourses           该培养方案下已调用的课程集合
     * @param targetCourseModule       目标课程模块
     * @param targetCourseModuleChildren 目标课程子模块
     * @param targetSubMajorId         目标专业方向
     * @return 已调用返回true，否则false
     */
    private boolean isCourseAlreadyInvoked(Long sourceCourseId, List<Course> invokedCourses,
                                           String targetCourseModule, String targetCourseModuleChildren,
                                           Long targetSubMajorId) {
        boolean professional = DictContent.MAJOR_COURSES_SCHEDULE.equals(targetCourseModule);
        for (Course invoked : invokedCourses) {
            if (!Objects.equals(invoked.getSourceId(), sourceCourseId)) {
                continue;
            }
            if (professional) {
                //专业课程按专业方向去重，subMajorId 为空时无法判定同一专业方向，不视为重复
                if (targetSubMajorId != null && targetSubMajorId.equals(invoked.getSubMajorId())) {
                    return true;
                }
            } else if (Objects.equals(invoked.getCourseModule(), targetCourseModule)
                    && Objects.equals(invoked.getCourseModuleChildren(), targetCourseModuleChildren)) {
                //非专业课程按课程模块及子模块去重
                return true;
            }
        }
        return false;
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
    private Course copyCourse(TrainingSchemeVo trainingSchemeVo, Course c, CourseTemplateVo courseTemplateVo) {
        Course course = new Course();
        BeanUtils.copyProperties(c, course);
        course.setSourceId(c.getId());
        course.setMajorId(c.getMajorId() == null ? trainingSchemeVo.getMajorId() : c.getMajorId());
        course.setCategoryId(trainingSchemeVo.getCategoryId() == null ? c.getCategoryId():trainingSchemeVo.getCategoryId());
        course.setVersion(trainingSchemeVo.getVersion());
        course.setTemplateType(2);
        course.setId(null);
        if(ObjectUtils.isNotEmpty(courseTemplateVo.getCourseModule())){
            course.setCourseModule(courseTemplateVo.getCourseModule());
        }
        if(ObjectUtils.isNotEmpty(courseTemplateVo.getCourseModuleChildren())){
            course.setCourseModuleChildren(courseTemplateVo.getCourseModuleChildren());
        }
        if(ObjectUtils.isNotEmpty(courseTemplateVo.getMajorId())){
            course.setMajorId(courseTemplateVo.getMajorId());
        }
        if(ObjectUtils.isNotEmpty(courseTemplateVo.getSubMajorId())){
            course.setSubMajorId(courseTemplateVo.getSubMajorId());
        }
        if(ObjectUtils.isNotEmpty(courseTemplateVo.getLocation())){
            course.setLocation(courseTemplateVo.getLocation());
        }
        if(ObjectUtils.isNotEmpty(courseTemplateVo.getProgramLevel())){
            course.setProgramLevel(courseTemplateVo.getProgramLevel());
        }
        UserUtils.clearAndRefreshObj(course);
        return course;
    }

    /**
     * 刷新历史学年安排为 6(贯穿4年)/7(多学期排课) 的课程，转成 t_csys_course_ref_schedule 多行(1-5)格式。
     * 取数：t_csys_training_scheme_course_schedule 已展开的排课明细。
     *   - 调用的课程(source_id 非空)：取自身 course_id 的排课明细
     *   - 源课程(source_id 为空)：取其被调用课程(source_id = 该源课程)的排课明细
     * 幂等：若课程已存在 1-5 的有效关联行且无 6/7 行，则跳过。
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> refreshLegacySchedule() {
        Map<String, Object> summary = new LinkedHashMap<>();
        int refreshed = 0, skippedAlready = 0, noData = 0, rowsInserted = 0;

        // 1. 检测历史 6/7 课程(课程表字段或关联表行命中其一)
        List<Long> courseIds = courseMapper.selectLegacyScheduleCourseIds();
        if (ObjectUtils.isEmpty(courseIds)) {
            summary.put("detected", 0);
            summary.put("refreshed", 0);
            summary.put("skippedAlready", 0);
            summary.put("noData", 0);
            summary.put("rowsInserted", 0);
            return summary;
        }
        // 复用 selectCoursesByIds：已 left join t_csys_course_ref_schedule 填充 courseScheduleList，并带 sourceId/hours/timeWeek/unit
        List<Course> courses = courseMapper.selectCoursesByIds(courseIds);

        for (Course course : courses) {
            List<CourseSchedule> existing = course.getCourseScheduleList();
            boolean hasValid = false, hasLegacy = false;
            if (ObjectUtils.isNotEmpty(existing)) {
                for (CourseSchedule cs : existing) {
                    String s = cs.getSemesterSchedule();
                    if ("6".equals(s) || "7".equals(s)) {
                        hasLegacy = true;
                    } else if (ObjectUtils.isNotEmpty(s)) {
                        hasValid = true;
                    }
                }
            }
            // 已是 1-5 多行且无 6/7 -> 已刷新过，跳过(幂等)
            if (hasValid && !hasLegacy) {
                skippedAlready++;
                continue;
            }
            // 清掉残留的 6/7 行
            if (hasLegacy) {
                courseScheduleMapper.deleteLegacyByCourseId(course.getId());
            }

            // 2. 取排课明细：调用课程用自身，源课程用其被调用课程
            List<Long> lookupCourseIds = new ArrayList<>();
            if (course.getSourceId() != null) {
                // 调用课程
                lookupCourseIds.add(course.getId());
            } else {
                // 源课程：取被调用课程
                List<Course> copies = courseMapper.selectCourseBySourceId(course.getId());
                if (ObjectUtils.isNotEmpty(copies)) {
                    for (Course copy : copies) {
                        lookupCourseIds.add(copy.getId());
                    }
                }
            }
            List<TrainingSchemeCourseSchedule> detailRows =
                    ObjectUtils.isNotEmpty(lookupCourseIds)
                            ? trainingSchemeCourseScheduleMapper.selectByCourseIds(lookupCourseIds)
                            : Collections.emptyList();
            if (ObjectUtils.isEmpty(detailRows)) {
                noData++;
                continue;
            }

            // 3. 按 semesterSchedule 分组：term->semesterSchedule = (term+1)/2；学期归属按组内 term 奇偶推断
            Map<Integer, List<TrainingSchemeCourseSchedule>> grouped = new LinkedHashMap<>();
            for (TrainingSchemeCourseSchedule r : detailRows) {
                if (r.getTerm() == null) {
                    continue;
                }
                int semester = (r.getTerm() + 1) / 2;
                grouped.computeIfAbsent(semester, k -> new ArrayList<>()).add(r);
            }
            if (grouped.isEmpty()) {
                noData++;
                continue;
            }

            // 4. 每组生成一条 t_csys_course_ref_schedule 行
            for (Map.Entry<Integer, List<TrainingSchemeCourseSchedule>> entry : grouped.entrySet()) {
                int semester = entry.getKey();
                List<TrainingSchemeCourseSchedule> group = entry.getValue();
                boolean hasOdd = false, hasEven = false;
                Double teachHours = null, practiceHours = null;
                for (TrainingSchemeCourseSchedule r : group) {
                    int t = r.getTerm();
                    if (t % 2 == 0) {
                        hasEven = true;
                    } else {
                        hasOdd = true;
                    }
                    if (teachHours == null) {
                        teachHours = r.getTeachHours();
                    }
                    if (practiceHours == null) {
                        practiceHours = r.getPracticeHours();
                    }
                }
                String springAutumn = (hasOdd && hasEven) ? "5" : (hasEven ? "2" : "1");
                CourseSchedule ns = new CourseSchedule();
                ns.setCourseId(course.getId());
                ns.setSemesterSchedule(String.valueOf(semester));
                ns.setSpringAutumn(springAutumn);
                ns.setTeachHours(teachHours);
                ns.setPracticeHours(practiceHours);
                ns.setTimeWeek(course.getTimeWeek());
                ns.setUnit(course.getUnit());
                courseScheduleMapper.insert(ns);
                rowsInserted++;
            }
            refreshed++;
        }

        summary.put("detected", courses.size());
        summary.put("refreshed", refreshed);
        summary.put("skippedAlready", skippedAlready);
        summary.put("noData", noData);
        summary.put("rowsInserted", rowsInserted);
        return summary;
    }
}
