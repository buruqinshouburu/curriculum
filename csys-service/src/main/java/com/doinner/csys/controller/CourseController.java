package com.doinner.csys.controller;

import com.doinner.common.core.domain.DataSet;
import com.doinner.common.core.domain.Message;
import com.doinner.csys.domain.PageDataTable;
import com.doinner.csys.domain.StandardGraduation;
import com.doinner.csys.domain.vo.*;
import com.doinner.csys.entity.csys.model.CourseChooseStatusModel;
import com.doinner.csys.service.CourseService;
import com.doinner.csys.service.CurriculumService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * 课程Controller
 *
 * @author doinner
 */
@RestController
@RequestMapping("/course")
@Api(value = "/course", tags = "course-controller")
public class CourseController {
    @Resource
    private CourseService courseService;

    /**
     * 删除总库课程
     */
    @DeleteMapping("/storage")
    @ApiOperation("删除总库课程")
    public Message removeStorageCourse(@RequestBody List<Long> ids) {
        courseService.removeStorageCourse(ids);
        return Message.success();
    }

    /**
     * 删除调用课程
     */
    @DeleteMapping("/invoke")
    @ApiOperation("删除调用课程")
    public Message removeInvokeCourse(@RequestBody List<Long> ids) {
        courseService.removeInvokeCourse(ids);
        return Message.success();
    }

    /**
     * 禁用课程
     */
    @GetMapping("/disable/{courseId}")
    @ApiOperation("禁用课程")
    public Message disableCourse(@PathVariable("courseId") Long courseId) {
        courseService.disableCourse(courseId);
        return Message.success();
    }

    /**
     * 开启课程
     */
    @GetMapping("/start/{courseId}")
    @ApiOperation("开启课程")
    public Message start(@PathVariable("courseId") Long courseId) {
        courseService.startCourse(courseId);
        return Message.success();
    }

    @PostMapping("/invoke")
    @ApiOperation("课程调用")
    public Message courseInvoke(@RequestBody CourseTemplateVo courseTemplateVo) {
        return courseService.courseInvoke(courseTemplateVo);
    }

    @PostMapping("/boundGraduation")
    @ApiOperation("绑定毕业要求")
    public Message boundGraduation(@RequestBody CourseBoundGraduationVo courseBoundGraduationVo) {
        return courseService.boundGraduation(courseBoundGraduationVo);
    }

    @GetMapping("/courseGraduation/{courseId}")
    @ApiOperation("课程绑定毕业要求查看")
    public DataSet<Map<String,List<Long>>> courseGraduation(@PathVariable("courseId") Long courseId) {
        return DataSet.success(courseService.courseGraduation(courseId));
    }


    @GetMapping("/viewCourseKnowledgeList/{trainingSchemeId}")
    @ApiOperation("编辑培养方案-知识图谱查询")
    public DataSet<List<CourseKnowledgeViewVo>> viewCourseKnowledgeList(@PathVariable("trainingSchemeId") Long trainingSchemeId) {
        return PageDataTable.success(courseService.viewCourseKnowledgeList(trainingSchemeId));
    }

    @GetMapping("/viewCourseGraduation/{courseId}")
    @ApiOperation("课程查看毕业要求")
    public DataSet<List<StandardGraduation>> viewCourseGraduation(@PathVariable("courseId") Long courseId) {
        return PageDataTable.success(courseService.viewCourseGraduation(courseId));
    }

    @ApiOperation("课程被选用情况表查询(单个源课程)")
    @GetMapping("/chooseStatus/{sourceCourseId}")
    public CourseChooseStatusModel getCourseChooseStatus(@PathVariable("sourceCourseId") Long sourceCourseId){
        return courseService.getCourseChooseStatus(sourceCourseId);
    }

    @ApiOperation("刷新历史6/7学年排课数据(转 t_csys_course_ref_schedule 多行格式)")
    @PostMapping("/refreshLegacySchedule")
    public DataSet<Map<String, Object>> refreshLegacySchedule() {
        return DataSet.success(courseService.refreshLegacySchedule());
    }

    @ApiOperation("同步毕业要求(总库课程绑定关系追加到培养方案课程，不覆盖已存在)")
    @GetMapping("/syncGraduation/{trainingSchemeId}")
    public DataSet<Map<String, Object>> syncGraduation(@PathVariable("trainingSchemeId") Long trainingSchemeId) {
        return DataSet.success(courseService.syncGraduationFromSource(trainingSchemeId));
    }


}
