package com.doinner.csys.controller;

import com.deepoove.poi.XWPFTemplate;
import com.doinner.common.core.domain.DataSet;
import com.doinner.common.core.domain.DataTable;
import com.doinner.common.core.domain.Message;
import com.doinner.common.datascope.annotation.DataScope;
import com.doinner.common.security.annotation.RequiresPermissions;
import com.doinner.csys.domain.*;
import com.doinner.csys.domain.vo.*;
import com.doinner.csys.service.CurriculumService;
import com.doinner.csys.service.TrainingService;
import com.github.pagehelper.PageInfo;
import com.doinner.common.core.utils.PageUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.formula.functions.IDStarAlgorithm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

@Api(tags = "培养方案/培养规划")
@RestController
@RequestMapping("/training")
public class TrainingController {
    private static final Logger log = LoggerFactory.getLogger(TrainingController.class);

    @Resource
    private TrainingService trainingService;

    @Resource
    private CurriculumService courseService;

    /** 新增培养方案 */
    @ApiOperation("新增培养方案")
    @PostMapping("/scheme")
    public DataSet<TrainingScheme> addTrainingScheme(@RequestBody TrainingSchemeVo trainingSchemeVo){
        return trainingService.insertTrainingScheme(trainingSchemeVo);
    }

    /** 删除培养方案 */
    @ApiOperation("删除培养方案")
    @DeleteMapping("/scheme/{id}")
    public Message deleteTrainingScheme(@PathVariable Long id){
        trainingService.deleteTrainingSchemeById(id);
        return Message.success();
    }

    /** 删除培养方案 */
    @ApiOperation("批量删除培养方案")
    @DeleteMapping("/deleteBatch/scheme")
    public Message deleteBatchTrainingScheme(@RequestBody List<Long> ids){
        return  trainingService.deleteTrainingSchemeByIds(ids);
    }


    /** 删除培养方案-首页 */
    @ApiOperation("删除培养方案-首页")
    @DeleteMapping("/scheme/index/{id}")
    public Message deleteTrainingSchemeIndex(@PathVariable Long id){
        trainingService.deleteTrainingSchemeIndex(id);
        return Message.success();
    }

//    /** 修改培养方案 */
//    @ApiOperation("修改培养方案和关联课程")
//    @PutMapping("/schemeRef")
//    public DataSet<TrainingScheme> editTrainingScheme(@RequestBody TrainingSchemeVo trainingSchemeVo){
//        return DataSet.success(trainingService.updateTrainingScheme(trainingSchemeVo));
//    }

    /** 修改培养方案 */
    @ApiOperation("修改培养方案")
    @PutMapping("/scheme")
    public DataSet<TrainingScheme> editTrainingScheme(@RequestBody TrainingSchemeVo trainingScheme){
        return DataSet.success(trainingService.updateTrainingScheme(trainingScheme));
    }

    /** 生成培养方案 */
    @ApiOperation("生成培养方案")
    @PutMapping("/scheme/create")
    public DataSet<TrainingScheme> createTrainingScheme(Long schemeId){
        return DataSet.success(trainingService.createTrainingScheme(schemeId));
    }

    /** 生成培养方案 */
    @ApiOperation("生成培养方案Word")
    @GetMapping("/scheme/createWord/{schemeId}")
    public DataSet<TrainingSchemeVo> createTrainingPlanWord(@PathVariable("schemeId")Long schemeId){
        return DataSet.success(trainingService.createTrainingPlanWord(schemeId));
    }

    /** 根据培养方案id查询培养方案 */
    @ApiOperation("根据培养方案id查询培养方案")
    @GetMapping("/scheme/{id}")
    public DataSet<TrainingSchemeVo> getTrainingScheme(@PathVariable Long id){
        return DataSet.success(trainingService.selectTrainingSchemeById(id));
    }

    /** 查询培养方案列表 */
    @ApiOperation("查询培养方案列表")
    @GetMapping("/scheme/list")
    public DataTable<List<TrainingSchemeVo>> listTrainingSchemes(TrainingScheme trainingScheme){
        PageUtils.startPage();
        List<TrainingSchemeVo> list = trainingService.selectTrainingSchemeList(trainingScheme);
        return DataTable.success(list,new PageInfo<>(list).getTotal());
    }

    /** 查询培养方案阅览列表 */
    @ApiOperation("查询培养方案阅览列表")
    @GetMapping("/scheme/look/list")
    public DataTable<List<TrainingScheme>> listTrainingSchemesLook(TrainingScheme trainingScheme){
        PageUtils.startPage();
        List<TrainingScheme> list = trainingService.listTrainingSchemesLook(trainingScheme);
        return DataTable.success(list,new PageInfo<>(list).getTotal());
    }

    /** 按学院查询培养方案列表 */
    @ApiOperation("按学院查询培养方案列表")
    @GetMapping("/college/scheme/list")
    public DataSet<List<CollegeSchemeVo>> listTrainingSchemesByCollege(){
        List<CollegeSchemeVo> list = trainingService.selectSchemeCollegeVoList();
        return DataSet.success(list);
    }

    /** 查询培养方案列表带门类url，统计任务数，审核数 */
    @ApiOperation("查询培养方案列表带门类url，统计任务数，审核数")
    @GetMapping("/scheme/categoryUrl/list")
    public DataTable<List<TrainingSchemeVo>> selectTrainingSchemeVoCategoryList(TrainingScheme trainingScheme){
        PageUtils.startPage();
        List<TrainingSchemeVo> list = trainingService.selectTrainingSchemeVoCategoryList(trainingScheme);
        return DataTable.success(list,new PageInfo<>(list).getTotal());
    }

    /** 查询培养方案列表 */
    @ApiOperation("查询培养方案列表带门类url")
    @GetMapping("/schemeVo/list")
    public DataSet<List<TrainingScheme>> listTrainingSchemesCategory(TrainingScheme trainingScheme){
        return DataSet.success(trainingService.selectTrainingSchemeCategoryList(trainingScheme));
    }

    /** 根据培养方案id查询课程列表 */
    @ApiOperation("根据培养方案id查询课程列表")
    @GetMapping("/scheme/courses/list/{id}")
    public DataSet<List<TrainingSchemeCourseVo>> listTrainingSchemeCoursesById(@PathVariable Long id){
        return DataSet.success(trainingService.selectTrainingSchemeCoursesById(id));
    }

    /** 根据培养方案id查看培养方案视图 */
    @ApiOperation("根据培养方案id查看培养方案视图")
    @GetMapping("/scheme/view/{id}")
    public DataSet<TrainingSchemeVo> viewTrainingScheme(@PathVariable("id") Long id,Long type){
        return DataSet.success(trainingService.viewTrainingScheme(id,type));
    }

    @ApiOperation("全景->培养目标")
    @GetMapping("/scheme/target/unit")
    public DataSet<List<TrainingSchemeStandardCultivationTargetVo>> getTarget(Long standardId){
        Long targetId = trainingService.selectTargetId(standardId);
        if (ObjectUtils.isEmpty(targetId)){
            return DataSet.success(null);
        }
        return DataSet.success(trainingService.selectStandardCultivationTargetVoAll(targetId));
    }

    @ApiOperation("查看->培养标准")
    @GetMapping("/scheme/cultivation/unit")
    public DataSet<List<TrainingSchemeStandardCultivationVo>> getTrainingSchemeStandardCultivationVo(Long cultivationId){
        return DataSet.success(trainingService.selectStandardCultivationVoAll(cultivationId));
    }

    /** 新增门类 */
    @ApiOperation("新增门类")
    @PostMapping("/scheme/category")
    public DataSet<TrainingSchemeCategory> addTrainingSchemeCategory(@RequestBody TrainingSchemeCategory trainingSchemeCategory){
        return DataSet.success(trainingService.insertTrainingSchemeCategory(trainingSchemeCategory));
    }

    /** 删除门类 */
    @ApiOperation("删除门类")
    @DeleteMapping("/scheme/category/{id}")
    public Message deleteTrainingSchemeCategoryById(@PathVariable Long id){
        trainingService.deleteTrainingSchemeCategoryById(id);
        return Message.success();
    }

    /** 修改门类 */
    @ApiOperation("修改门类")
    @PutMapping("/scheme/category")
    public DataSet<TrainingSchemeCategory> editTrainingSchemeCategory(@RequestBody TrainingSchemeCategory trainingSchemeCategory){
        return DataSet.success(trainingService.updateTrainingSchemeCategory(trainingSchemeCategory));
    }

    /** 查询门类 */
    @ApiOperation("查询门类")
    @GetMapping("/scheme/category/list")
    public DataTable<List<TrainingSchemeCategory>> listTrainingSchemeCategory(TrainingSchemeCategory trainingSchemeCategory){
        PageUtils.startPage();
        List<TrainingSchemeCategory> list = trainingService.selectTrainingSchemeCategoryList(trainingSchemeCategory);
        return DataTable.success(list,new PageInfo<>(list).getTotal());
    }

    /** 新增排课 */
    @ApiOperation("新增排课")
    @PostMapping("/scheme/course/schedule")
    public DataSet<TrainingSchemeCourseSchedule> addTrainingSchemeCourseSchedule(@RequestBody TrainingSchemeCourseSchedule trainingSchemeCourseSchedule){
        return DataSet.success(trainingService.insertTrainingSchemeCourseSchedule(trainingSchemeCourseSchedule));
    }

    /** 新增排课 */
    @ApiOperation("批量新增排课")
    @PostMapping("/scheme/course/schedule/list")
    public Message addTrainingSchemeCourseSchedules(@RequestBody List<TrainingSchemeCourseSchedule> trainingSchemeCourseSchedules){
        trainingService.insertTrainingSchemeCourseSchedules(trainingSchemeCourseSchedules);
        return DataSet.success();
    }

    /** 删除排课 */
    @ApiOperation("删除排课")
    @DeleteMapping("/scheme/course/schedule/{id}")
    public Message deleteTrainingSchemeCourseScheduleById(@PathVariable Long id){
        trainingService.deleteTrainingSchemeCourseScheduleById(id);
        return Message.success();
    }

    /** 修改排课 */
    @ApiOperation("修改排课")
    @PutMapping("/scheme/course/schedule")
    public DataSet<TrainingSchemeCourseSchedule> editTrainingSchemeCourseSchedule(@RequestBody TrainingSchemeCourseSchedule trainingSchemeCourseSchedule){
        return DataSet.success(trainingService.updateTrainingSchemeCourseSchedule(trainingSchemeCourseSchedule));
    }

    /** 查询排课 */
    @ApiOperation("查询排课")
    @GetMapping("/scheme/course/schedule/{id}")
    public DataSet<TrainingSchemeCourseSchedule> getTrainingSchemeCourseScheduleById(@PathVariable Long id){
        return DataSet.success(trainingService.selectTrainingSchemeCourseScheduleById(id));
    }

    /** 查询排课列表 */
    @ApiOperation("查询排课列表")
    @GetMapping("/scheme/course/schedules/list")
    public DataSet<List<TrainingSchemeCourseScheduleVo>> listTrainingSchemeCourseSchedules(TrainingSchemeCourseSchedule trainingSchemeCourseSchedule){
        return DataSet.success(trainingService.selectTrainingSchemeCourseScheduleList(trainingSchemeCourseSchedule));
    }

    /** 培养规划配置 */
    @ApiOperation("培养规划配置")
    @PostMapping("/scheme/dispose")
    public Message planDispose(@RequestBody TowerToTower towerToTower){
        trainingService.planDispose(towerToTower);
        return Message.success();
    }

    /** 清除培养规划与培养标准的关联 */
    @ApiOperation("清除培养规划与培养标准的关联")
    @DeleteMapping("/scheme/dispose/cultivationTop")
    public Message editCultivation(Long schemeId){
        trainingService.editCultivation(schemeId);
        return Message.success();
    }

    /** 学院列表，带统计课程 */
    @ApiOperation("学院列表，带统计课程")
    @GetMapping("/college/list")
    public DataSet<List<CollegeVo>> listCollegeVo(CollegeVo collegeVo){
        return DataSet.success(trainingService.selectCollegeList(collegeVo));
    }

    /** 根据培养方案id、课程id、知识单元id查询培养标准 */
    @ApiOperation("根据培养方案id、课程id、知识单元id查询培养标准")
    @GetMapping("/scheme/cultivation")
    public DataSet<List<TrainingSchemeStandardCultivationVo>> listCultivationVo(Long schemeId, Long courseId, Long unitId){
        return DataSet.success(trainingService.selectCultivation(schemeId, courseId, unitId));
    }

    @ApiOperation("教学计划导出为word文档")
    @GetMapping("/exportWord")
    public void exportWord(HttpServletResponse response, Long id){
        try {
            CourseVo courseVo = courseService.selectCourseById(id);
            String courseType = courseVo.getType()+"";
            OutputStream out = response.getOutputStream();
            byte[] data = trainingService.getWordByte(courseVo,courseType);
            String curName = courseVo.getName()!=null?courseVo.getName():"";
            String fileName = curName+"教学计划.docx";
            response.setHeader("Content-disposition","attachment;filename*=UTF-8''" + URLEncoder.encode(fileName, "utf-8"));
            out.write(data);
            out.flush();
            out.close();
        } catch (Exception e){
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }

    @ApiOperation("合并多个教学计划导出为word文档")
    @PostMapping("/exportWords")
    public void exportWords(HttpServletResponse response, @RequestBody List<Long> ids){
        List<CourseVo> courseVos;    // 1.理论课 2.实践课 3.理论课+实践课
        if (ids.size()>0){
            courseVos = trainingService.selectCourseVosByIds(ids);
        }else {
            return;
        }
        try {
            byte[] bytes = trainingService.getWordBytes(courseVos);
            Map<String, Object> data = trainingService.replacePlaceholder(courseVos);
            InputStream in = new ByteArrayInputStream(bytes);
            XWPFTemplate template = XWPFTemplate.compile(in);
            String fileName = "教学计划.docx";
            response.addHeader("Content-type","application/x-download");
//            response.addHeader("Content-Length",String.valueOf(data.length));
            response.addHeader("Content-Disposition","attachment;filename*=UTF-8''" + URLEncoder.encode(fileName, "utf-8"));
            OutputStream out = response.getOutputStream();
//            trainingService.process(out, courseVos);
            template.render(data,out);
//            out.write(bytes);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @ApiOperation("根据专业id查询专业详情")
    @GetMapping("/major/{id}")
    public DataSet<StandardMajor> getStandardMajor(@PathVariable("id") Long id){
        return DataSet.success(trainingService.selectStandardMajorById(id));
    }

    @ApiOperation("专业课程执行计划导出")
    @PostMapping("/trainingCourse/export")
    public void exportTrainingCourse(HttpServletResponse response, Long schemeId,Long subMajorId){
       trainingService.exportTrainingCourse(response,schemeId,subMajorId);
    }

    @ApiOperation("培养方案绑定课程")
    @PostMapping("/boundTrainingCourse")
    public Message BoundTrainingCourse(@RequestBody TrainingBoundCourseVo trainingBoundCourseVo){
        return trainingService.BoundTrainingCourse(trainingBoundCourseVo);
    }

    @ApiOperation("调用课程知识体系总览")
    @GetMapping("/viewTrainingCourseKnowLedge/{schemeId}")
    public DataSet<List<TrainingSchemeCourseVo>> viewTrainingCourseKnowLedge(@PathVariable("schemeId") Long schemeId){
        return DataSet.success(trainingService.viewTrainingCourseKnowLedge(schemeId));
    }

    @ApiOperation("调用课程毕业要求总览")
    @GetMapping("/viewTrainingCourseGraduation/{schemeId}")
    public DataSet<List<TrainingSchemeCourseVo>> viewTrainingCourseGraduation(@PathVariable("schemeId") Long schemeId,String type){
        return DataSet.success(trainingService.viewTrainingCourseGraduation(schemeId,type));
    }

    @ApiOperation("调用课程执行方案更新")
    @GetMapping("/updateSchedules/{schemeId}")
    public Message updateSchedules(@PathVariable("schemeId") Long schemeId){
        trainingService.updateSchedules(schemeId);
        return Message.success();
    }

    @ApiOperation("调用课程执行方案更新")
    @GetMapping("/updateCourseSchedule/{courseId}")
    public Message updateCourseSchedule(@PathVariable("courseId") Long courseId,Long schemeId){
        trainingService.updateCourseSchedule(courseId,schemeId);
        return Message.success();
    }

}
