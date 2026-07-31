package com.doinner.csys.controller;


import com.doinner.common.core.domain.DataSet;
import com.doinner.common.core.domain.DataTable;
import com.doinner.common.core.domain.Message;
import com.doinner.common.core.utils.poi.ExcelUtil;
import com.doinner.csys.constant.DomainFieldConstant;
import com.doinner.csys.dao.TrainingSchemeRefCourseMapper;
import com.doinner.csys.domain.*;
import com.doinner.csys.domain.vo.*;
import com.doinner.csys.io.utils.ExcelUtils;
import com.doinner.csys.service.CurriculumService;
import com.doinner.csys.service.KnowledgeCheckLogService;
import com.doinner.csys.service.KnowledgeSourceService;
import com.doinner.csys.service.TrainingService;
import com.doinner.file.api.constant.DomainFieldConstants;
import com.doinner.file.api.domain.FileInfo;
import com.doinner.file.api.service.RemoteFileInfoService;
import com.doinner.common.core.utils.PageUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.ibatis.annotations.Param;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * 课程Controller
 *
 * @author doinner
 */
@RestController
@RequestMapping("/curriculum")
@Api(value = "/curriculum", tags = "curriculum-controller")
public class CurriculumController {
    @Resource
    private CurriculumService courseService;
    @Resource
    private RemoteFileInfoService remoteFileInfoService;

    @Resource
    private KnowledgeCheckLogService knowLedgeCheckLogService;

    @Resource
    private KnowledgeSourceService knowledgeSourceService;

    @Resource
    private TrainingService trainingService;
    @Autowired
    private TrainingSchemeRefCourseMapper trainingSchemeRefCourseMapper;

    /**
     * 查询课程列表
     */
    @GetMapping("/course/list")
    public DataTable<Course> listCourse(CourseVo course) {
        List<Course> list = courseService.selectCourseList(course);
        return PageDataTable.success(list);

    }

    /**
     * 根据培养方案查询课程相关
     */
    @GetMapping("/course/scheme/list")
    public DataSet<List<Course>> listSchemeCourse(CourseVo course) {
        List<Course> list = courseService.listSchemeCourse(course);
        return DataSet.success(list);

    }

    /**
     * 获取课程详细信息
     */
    @GetMapping(value = "/course/{id}")
    public DataSet<CourseVo> getCourse(@PathVariable("id") Long id) {
        return DataSet.success(courseService.selectCourseById(id));
    }


    /**
     * 获取课程详细信息，通过知识单元主键
     */
    @GetMapping(value = "/course/knowledgeUnit/{id}")
    public DataSet<CourseVo> getCourseByKnowledgeUnitId(@PathVariable("id") Long id) {
        return DataSet.success(courseService.selectCourseByKnowledgeUnitId(id));
    }


    /**
     * 新增课程
     */
    @PostMapping(value = "/course")
    public DataSet addCourse(@RequestBody CourseVo course) {
        return DataSet.success(courseService.insertCourse(course));
    }

    /**
     * 修改课程
     */
    @PutMapping(value = "/course")
    public DataSet editCourse(@RequestBody CourseVo course) {
        return DataSet.success(courseService.updateCourse(course));
    }

    /**
     * 统一刷新缺课程编号(code 为空)的源库课程编号。
     * 规则同新增(版本-培训层次-开课单位-流水号)；version/collegeName/educationLevel 任一为空则跳过该条。
     * 用于补 type=2/4 等历史缺编号老数据。
     */
    @ApiOperation("刷新缺课程编号的源库课程(按规则补编号,字段空跳过)")
    @PostMapping("/course/refreshCode")
    public DataSet<Map<String, Object>> refreshCourseCode() {
        return DataSet.success(courseService.refreshCourseCode());
    }

    @PostMapping(value = "/course/target/configuration")
    public DataSet courseTargetConfiguration(@RequestBody CourseTarget courseTarget) {
        return DataSet.success(courseService.courseTargetConfiguration(courseTarget));
    }

    /**
     * 清除fileId，fileName
     */
    @DeleteMapping(value = "/course/deleteFile/{id}")
    public DataSet deleteFile(@PathVariable("id") Long id) {
        return DataSet.success(courseService.deleteFile(id));
    }

    /**
     * 删除课程
     */
    @DeleteMapping("/course")
    public Message removeCourse(@RequestBody List<Long> ids) {
        courseService.deleteCourseByIds(ids);
        return Message.success();
    }

    /**
     * 批量修改课程的审核状态
     */
    @PostMapping("/course/updateStatus")
    public Message updateStatus(@RequestBody List<Long> ids) {
        courseService.updateStatusByIds(ids);
        return Message.success();
    }

    @GetMapping("/review/list")
    @ApiOperation("查询课程版本信息")
    public DataSet<List<CourseReview>> list(CourseReview courseReview) {
        List<CourseReview> list = courseService.selectReview(courseReview);
        return DataSet.success(list);
    }

    @GetMapping(value = "/review/{id}")
    @ApiOperation("根据查询课程版本信息")
    public DataSet<CourseReview> getReviewInfo(@PathVariable("id") Long id) {
        return DataSet.success(courseService.reviewById(id));
    }

    @DeleteMapping(value = "/review/{id}")
    @ApiOperation("删除版本信息")
    public Message deleteReview(@PathVariable("id") Long id) {
        courseService.deleteReview(id);
        return DataSet.success();
    }

    @GetMapping(value = "/review/fallback")
    @ApiOperation("版本回滚")
    public DataSet<CourseVo> fallbackReviewInfo(@RequestParam(value = "id", required = false) Long id, @RequestParam("courseId") Long courseId) {
        return DataSet.success(courseService.fallbackReviewById(id, courseId));
    }

    /**
     * 课程相关文档上传
     */
    @PostMapping("/course/upload")
    public Message upload(@RequestParam("file") MultipartFile file, @RequestParam("id") Long id) {
        //文件目录后续需要写成配置
        DataSet<FileInfo> upload = remoteFileInfoService.upload(file, DomainFieldConstants.COURSE_CATEGORY_ID);
        courseService.updateFileById(upload.getData().getFileId(), upload.getData().getFileName(), id);
        return DataSet.success(upload.getData());
    }

    /**
     * 文件查询
     */
    @GetMapping("/course/file/{fileId}")
    public Message findFileByFileId(@PathVariable String fileId) {
        DataSet fileById = remoteFileInfoService.getFileById(fileId);
        return DataSet.success(fileById.getData());
    }

    /**
     * 新增理论教学计划
     */
    @PostMapping("/teachingTheoryPlan")
    public DataSet<CourseTeachingTheoryPlan> addTeachingTheoryPlan(@RequestBody CourseTeachingTheoryPlan courseTeachingTheoryPlan) {
        return DataSet.success(courseService.insertCourseTeachingTheoryPlan(courseTeachingTheoryPlan));
    }

    /**
     * 修改理论教学计划
     */
    @PutMapping("/teachingTheoryPlan")
    public DataSet<CourseTeachingTheoryPlan> editTeachingTheoryPlan(@RequestBody CourseTeachingTheoryPlan courseTeachingTheoryPlan) {
        return DataSet.success(courseService.updateCourseTeachingTheoryPlan(courseTeachingTheoryPlan));
    }

    /**
     * 删除理论教学计划
     */
    @DeleteMapping("/teachingTheoryPlan")
    public Message removeTeachingTheoryPlan(@RequestBody Long[] ids) {
        courseService.deleteCourseTeachingTheoryPlanByIds(ids);
        return Message.success();
    }

    /**
     * 新增实践教学计划
     */
    @PostMapping(value = "/teachingPracticePlan")
    public DataSet<CourseTeachingPracticePlan> addTeachingPracticePlan(@RequestBody CourseTeachingPracticePlan courseTeachingPracticePlan) {
        return DataSet.success(courseService.insertCourseTeachingPracticePlan(courseTeachingPracticePlan));
    }

    /**
     * 修改实践教学计划
     */
    @PutMapping("/teachingPracticePlan")
    public DataSet<CourseTeachingPracticePlan> teachingPracticePlanEdit(@RequestBody CourseTeachingPracticePlan courseTeachingPracticePlan) {
        return DataSet.success(courseService.updateCourseTeachingPracticePlan(courseTeachingPracticePlan));
    }

    /**
     * 删除实践教学计划
     */
    @DeleteMapping("/teachingPracticePlan")
    public Message removeTeachingPracticePlan(@RequestBody Long[] ids) {
        courseService.deleteCourseTeachingPracticePlanByIds(ids);
        return Message.success();
    }


    /**
     * 新增课程章节
     */
    @PostMapping("/courseChapter")
    public DataSet<CourseChapter> add(@RequestBody CourseChapter courseChapter) {
        return DataSet.success(courseService.insertCourseChapter(courseChapter));
    }

    /**
     * 修改课程章节
     */
    @PutMapping("/courseChapter")
    public DataSet<CourseChapter> courseChapterEdit(@RequestBody CourseChapter courseChapter) {
        return DataSet.success(courseService.updateCourseChapter(courseChapter));
    }

    /**
     * 根据单元ID查询知识点
     */
    @GetMapping("/knowledgePoint/{id}")
    public DataSet<List<KnowledgePoint>> listKnowledgePoint(@PathVariable("id") Long id) {
        return DataSet.success(courseService.selectKnowledgePointByUnitId(id));
    }

    /**
     * 新增知识点
     */
    @PostMapping(value = "/knowledgePoint")
    public DataSet<KnowledgePoint> addKnowledgePoint(@RequestBody KnowledgePointVo knowledgePoint) {
        return DataSet.success(courseService.insertKnowledgePoint(knowledgePoint));
    }

    /**
     * 修改知识点
     */
    @PutMapping("/knowledgePoint")
    public DataSet<KnowledgePoint> edit(@RequestBody KnowledgePoint knowledgePoint) {
        return DataSet.success(courseService.updateKnowledgePoint(knowledgePoint));
    }

    /**
     * 删除知识点
     */
    @DeleteMapping("/knowledgePoint")
    public Message removeKnowledgePoint(@RequestBody List<Long> ids) {
        courseService.deleteKnowledgePointByIds(ids);
        return Message.success();
    }

    /**
     * 根据课程ID查询知识单元
     */
    @GetMapping("/knowledgeUnit/{id}")
    public DataSet<List<KnowledgeUnit>> getKnowledgeUnit(@PathVariable("id") Long id) {
        List<KnowledgeUnit> knowledgeUnitList = courseService.selectKnowledgeUnitListByCourseId(id);
        return DataSet.success(knowledgeUnitList);
    }

    /**
     * 新增知识单元
     */
    @PostMapping(value = "/knowledgeUnit")
    public DataSet<KnowledgeUnit> addKnowledgeUnit(@RequestBody KnowledgeUnitVo knowledgeUnit) {
        return DataSet.success(courseService.insertKnowledgeUnit(knowledgeUnit));
    }

    /**
     * 修改知识单元
     */
    @PutMapping("/knowledgeUnit")
    public DataSet<KnowledgeUnit> editKnowledgeUnit(@RequestBody KnowledgeUnit knowledgeUnit) {
        return DataSet.success(courseService.updateKnowledgeUnit(knowledgeUnit));
    }

    /**
     * 删除知识单元
     */
    @DeleteMapping("/knowledgeUnit")
    public Message deleteKnowledgeUnit(@RequestBody KnowledgeUnitVo knowledgeUnit) {
        courseService.deleteUnitById(knowledgeUnit);
        return Message.success();
    }

    /**
     * 新增课程与知识单元关联
     */
    @PostMapping("/courseRefKeUnit/add")
    public DataSet<CourseRefKeUnit> addCourseRefKeUnit(@RequestBody CourseRefKeUnit courseRefKeUnit) {
        return DataSet.success(courseService.insertCourseRefKeUnit(courseRefKeUnit));
    }

    /**
     * 删除课程与知识单元关联
     */
    @DeleteMapping("/courseRefKeUnit/delete")
    public Message removeCourseRefKeUnit(@RequestParam Long courseId, @RequestParam(required = false) Long unitId) {
        courseService.deleteCourseRefKeUnitByCourseIdAndUnitId(courseId, unitId);
        return Message.success();
    }

    /**
     * 新增知识单元与知识点关联
     */
    @PostMapping("/knowledgeUnitRefPoint/add")
    public DataSet<KnowledgeUnitRefPoint> addKnowledgeUnitRefPoint(@RequestBody KnowledgeUnitRefPoint knowledgeUnitRefPoint) {
        return DataSet.success(courseService.insertKnowledgeUnitRefPoint(knowledgeUnitRefPoint));
    }


    /**
     * 删除知识单元与知识点关联
     */
    @DeleteMapping("/knowledgeUnitRefPoint/delete")
    public Message knowledgeUnitRefPointRemove(@RequestParam Long unitId, @RequestParam(required = false) Long pointId) {
        courseService.deleteKnowledgeUnitRefPointByUnitIdAndPointId(unitId, pointId);
        return Message.success();
    }


    /**
     * 新增课程教材
     */
    @PostMapping("/courseTextbook/add")
    public DataSet<CourseTextbook> addCourseTextbook(@RequestBody CourseTextbook courseTextbook) {
        return DataSet.success(courseService.insertCourseTextbook(courseTextbook));
    }

    /**
     * 修改课程教材
     */
    @PutMapping("/courseTextbook/edit")
    public DataSet<CourseTextbook> edit(@RequestBody CourseTextbook courseTextbook) {
        return DataSet.success(courseService.updateCourseTextbook(courseTextbook));
    }


    /**
     * 查询知识单元树
     */
    @GetMapping("/knowledge/listKnowledgeView/{id}")
    public DataSet<List<KnowledgeViewVo>> listKnowledgeView(@PathVariable("id") Long id) {
        return DataSet.success(courseService.selectKnowledgeTreeByCourseId(id));
    }


    /**
     * 根据课程ID知识点查重
     */
    @PostMapping("/knowledge/check")
    @ApiOperation("根据知识领域ID知识点查重")
    public Message KnowledgePointCheck(@RequestBody List<Long> ids) {
        knowLedgeCheckLogService.knowledgePointCheck(ids);
        return Message.success();
    }

    /**
     * 根据学院ID知识点查重
     */
    @GetMapping("/knowledge/checkAll")
    @ApiOperation("根据学院ID知识点查重")
    public Message KnowledgePointCheckAll(Long collegeId, Long majorId) {
        SourceDomain sourceDomain = new SourceDomain();
        sourceDomain.setCollegeId(collegeId);
        sourceDomain.setMajorId(majorId);
        List<SourceDomain> sourceDomains = knowledgeSourceService.selectSourceDomainList(sourceDomain);
        // List<Long> ids = knowLedgeCheckLogService.getCourseIdsByCollegeId(collegeId);
        if (CollectionUtils.isNotEmpty(sourceDomains)) {
            knowLedgeCheckLogService.knowledgePointCheck(sourceDomains.stream().map(a -> a.getId()).collect(Collectors.toList()));
        }
        return Message.success();
    }

    /**
     * 根据课程ID查看知识点查重结果
     */
    @PostMapping("/knowledge/checkLog")
    @ApiOperation("根据课程ID查看知识点查重结果")
    public DataSet<KnowledgeChekTotalVo> selectCheckPointLog(@RequestBody List<Long> ids) {
        if (ObjectUtils.isEmpty(ids)) {
            return DataSet.error("数据为空！");
        }
        KnowledgeChekTotalVo vo = knowLedgeCheckLogService.selectCheckPointLog(ids);
        return DataSet.success(vo);
    }

    /**
     * 根据学院ID查看知识点查重结果
     */
    @PostMapping("/knowledge/checkAllLog")
    @ApiOperation("根据学院ID查看知识点查重结果")
    public DataSet<KnowledgeChekTotalVo> selectCheckPointLog(Long collegeId, Long majorId) {
        //List<Long> courseIds = knowLedgeCheckLogService.getCourseIdsByCollegeId(collegeId);
        SourceDomain sourceDomain = new SourceDomain();
        sourceDomain.setCollegeId(collegeId);
        sourceDomain.setMajorId(majorId);
        List<SourceDomain> sourceDomains = knowledgeSourceService.selectSourceDomainList(sourceDomain);
        if (CollectionUtils.isNotEmpty(sourceDomains)) {
            return DataSet.success(knowLedgeCheckLogService.selectCheckPointLog(sourceDomains.stream().map(a -> a.getId()).collect(Collectors.toList())));
        }
        return DataSet.success(null);
    }


    /**
     * 根据培养方案ID查看知识点查重结果
     */
    @GetMapping("/course/checkPoint")
    @ApiOperation("根据培养方案ID查看知识点查重统计结果")
    public DataSet<KnowledgeChekTotalVo> checkPoint(Long schemeId) {
        //查询培养方案信息
        TrainingSchemeVo trainingSchemeVo = trainingService.selectTrainingSchemeById(schemeId);
        //查询培养方案下的知识领域
        List<TrainingSchemeRefCourse> trainingSchemeRefCourses = trainingSchemeRefCourseMapper.selectTrainingSchemeRefCourseByTrainingSchemeVoId(schemeId);
        return DataSet.success(knowLedgeCheckLogService.selectCheckPointLogNoList(trainingSchemeRefCourses.stream().map(t->t.getCourseId()).collect(Collectors.toList())));
    }

    /**
     * 根据培养方案ID查看知识点查重结果
     */
    @GetMapping("/knowledge/checkLogBySchemeId")
    @ApiOperation("根据培养方案ID查看知识点查重统计结果+知识点查重list")
    public DataSet<KnowledgeChekTotalVo> selectCheckPointLogBySchemeId(Long schemeId) {
        if (ObjectUtils.isEmpty(schemeId)) {
            return DataSet.error("数据为空！");
        }
        List<Long> courseIds = knowLedgeCheckLogService.getCourseIdsBySchemeId(schemeId);
        //knowLedgeCheckLogService.knowledgePointCheck(courseIds);
        KnowledgeChekTotalVo vo = knowLedgeCheckLogService.selectCheckPointLog(courseIds);
        return DataSet.success(vo);
    }


    /**
     * 导出知识点查重结果
     */
    @GetMapping("/knowledge/checkLogBySchemeId/export")
    @ApiOperation("导出知识点查重结果")
    public void selectCheckPointLogExportBySchemeId(HttpServletResponse response, Long schemeId) {
        // todo 培养方案和知识领域
        List<Long> courseIds = knowLedgeCheckLogService.getCourseIdsBySchemeId(schemeId);
        List<KnowledgeCheckLog> voList = knowLedgeCheckLogService.selectCheckPointLogListBySchemeId(courseIds);
        response.setContentType("application/x-download");
        String fileName = DomainFieldConstant.KNOWLEDGE_CHECK_EXCEL_NAME;
        try {
            response.setHeader("Content-disposition", "attachment;filename*=UTF-8''" + URLEncoder.encode(fileName, "utf-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        XSSFWorkbook xssfWorkbook = ExcelUtils.getKnowledgeCheckLog(voList);
        try (OutputStream outputStream = response.getOutputStream()) {
            xssfWorkbook.write(outputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * 相似确认
     */
    @PutMapping("/knowledge/checkLog")
    public Message similePoint(@RequestBody KnowledgeCheckLog knowledgeCheckLog) {
        knowLedgeCheckLogService.similePoint(knowledgeCheckLog);
        return Message.success();
    }


    /**
     * 统计课程数，知识单元，知识点总数1.2.3级标准，和课程支撑度
     */
    @GetMapping("/course/statistics")
    @ApiOperation("统计课程数，知识单元，知识点总数1.2.3级标准，和课程支撑度")
    public DataSet courseStatistics(Long schemeId) {
        Map map = courseService.courseStatistics(schemeId);
        return DataSet.success(map);
    }


    /**
     * 课程排行榜
     */
    @GetMapping("/course/ranking")
    @ApiOperation("课程排行榜")
    public DataTable<Object> courseRanking() {
        PageUtils.startPage();
        return PageDataTable.success(courseService.courseRanking());
    }


    /**
     * 课程选用统计
     *
     * @return
     */
    @GetMapping("/course/courseSelectStatistics")
    @ApiOperation("课程选用统计")
    public DataTable<List> courseSelectStatistics(@RequestParam(required = false) String courseName, @RequestParam(required = false) List<Integer> types) {
        PageUtils.startPage();
        return DataTable.success(courseService.courseSelectStatistics(courseName, types));
    }

    /**
     * 培养方案体系首页(第二个页面)统计
     *
     * @return
     */
    @ApiOperation("培养方案体系首页(第二个页面)统计")
    @GetMapping("/course/homePage/statistics")
    public DataSet homePageStatistics() {
        return DataSet.success(courseService.countCollegeCourse());
    }

    /**
     * 新增知识领域
     */
    @PostMapping(value = "/knowledgeDomain")
    public DataSet<KnowledgeDomain> addKnowledgeDomain(@RequestBody KnowledgeDomain knowledgeDomain) {
        return DataSet.success(courseService.insertKnowledgeDomain(knowledgeDomain));
    }

    /**
     * 修改知识领域
     */
    @PutMapping("/knowledgeDomain")
    public DataSet<KnowledgeDomain> editKnowledgeDomain(@RequestBody KnowledgeDomain knowledgeDomain) {
        return DataSet.success(courseService.updateKnowledgeDomain(knowledgeDomain));
    }

    /**
     * 删除知识领域
     */
    @DeleteMapping("/knowledgeDomain")
    public Message deleteKnowledgeDomain(@RequestBody KnowledgeDomain knowledgeDomain) {
        courseService.deleteDomainById(knowledgeDomain.getId());
        return Message.success();
    }


    /**
     * 导出课程体系模板
     *
     * @param
     * @return
     */
    @RequestMapping("/course/template")
    public void exportCourseTemplate(HttpServletResponse response) {
        courseService.exportCourseTemplate(response);
    }

    /**
     * 导入课程体系
     *
     * @param file
     * @return
     */
    @PostMapping("/course/import")
    public Message importCourse(MultipartFile file, String courseModule, String courseModuleChildren, Long majorId,String type,Integer templateType,String version,Long categoryId,Long subMajorId) {
        return courseService.importCourse(file, courseModule, courseModuleChildren, majorId,type,templateType,version,categoryId,subMajorId);
    }

    /**
     * 导出课程体系
     *
     * @param response
     * @param ids
     */
    @PostMapping("/course/export")
    public void exportCourse(HttpServletResponse response, @RequestBody(required = false) List<Long> ids) {
        courseService.exportCourse(response, ids);
    }


    /**
     * 导出训练科目模板
     *
     * @param
     * @return
     */
    @RequestMapping("/training/template")
    public void exportTrainingTemplate(HttpServletResponse response) {
        courseService.exportTrainingTemplate(response);
    }

    /**
     * 导入训练科目
     *
     * @param file
     * @return
     */
    @PostMapping("/training/import")
    public Message importTraining(MultipartFile file, Long majorId,String type,Integer templateType,String version) {
        return courseService.importTraining(file,majorId,type,templateType,version);
    }

    /**
     * 导出训练科目
     *
     * @param response
     * @param ids
     */
    @PostMapping("/training/export")
    public void exportTraining(HttpServletResponse response, @RequestBody(required = false) List<Long> ids) {
        courseService.exportTraining(response, ids);
    }


    /**
     * 导出实践项目模板
     *
     * @param
     * @return
     */
    @RequestMapping("/practice/template")
    public void exportPracticeTemplate(HttpServletResponse response) {
        courseService.exportPracticeTemplate(response);
    }

    /**
     * 导入实践项目
     *
     * @param file
     * @return
     */
    @PostMapping("/practice/import")
    public Message importPractice(MultipartFile file, Long majorId,String type,Integer templateType,String version,Long categoryId,Long subMajorId) {
        return courseService.importPractice(file,majorId,type,templateType,version,categoryId,subMajorId);
    }

    /**
     * 导出实践项目
     *
     * @param response
     * @param ids
     */
    @PostMapping("/practice/export")
    public void exportPractice(HttpServletResponse response, @RequestBody(required = false) List<Long> ids) {
        courseService.exportPractice(response, ids);
    }


    @PostMapping("/course/byTemplate")
    @ApiOperation("根据模板新增课程")
    public Message insertCourseByTemplate(@RequestBody CourseTemplateVo courseTemplateVo) {
        courseService.insertCourseByTemplate_new(courseTemplateVo);
        return Message.success();
    }

    /**
     * 根据学院ID查看知识点查重结果
     */
    @PostMapping("/knowledge/export")
    @ApiOperation("知识点查重导出")
    public void exportCheckPointLog(@RequestBody KCheckExportVo kCheckExportVo, HttpServletResponse response) {
        try {
            ArrayList<KnowledgeCheckLog> knowledgeCheckLogList = new ArrayList<>();
            if (ObjectUtils.isNotEmpty(kCheckExportVo.getSchemeId())) {
                List<Long> courseIds=knowledgeSourceService.KnowledgePointCheckAllBySchemeId(kCheckExportVo.getSchemeId());
                if (CollectionUtils.isNotEmpty(courseIds)) {
                    knowledgeCheckLogList.addAll(knowLedgeCheckLogService.selectKnowledgeCheckLogs(courseIds));
                }
            }
            response.setContentType("application/x-download");
            String fileName = "知识点查重.xlsx";
            response.setHeader("Content-disposition", "attachment;filename*=UTF-8''" + URLEncoder.encode(fileName, "utf-8"));
            ExcelUtil<KnowledgeCheckLog> util = new ExcelUtil<>(KnowledgeCheckLog.class);
            util.exportExcel(response, knowledgeCheckLogList, "知识点查重");
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }


    @GetMapping(value = "/course/tree")
    public DataSet<TreeVo> courseTreeById(Long id,Integer type) {
        return DataSet.success(courseService.courseTreeById(id,type));
    }

    @GetMapping(value = "/course/tree/majorId")
    public DataSet<TreeVo> courseTreeByMajorId(Long majorId,Integer type,String courseType) {
        return DataSet.success(courseService.courseTreeByMajorId(majorId,type,courseType));
    }


    /**
     * 获取课程详细信息
     */
    @GetMapping(value = "/schemeCourseInfo/{schemeId}")
    public DataSet<Map> getSchemeCourseInfo(@PathVariable("schemeId") Long schemeId) {
        return DataSet.success(courseService.getSchemeCourseInfo(schemeId));
    }

    /**
     * 根据学院ID知识点查重
     */
    @GetMapping("/knowledge/checkAll/{schemeId}")
    @ApiOperation("根据培养方案ID知识点查重")
    public Message KnowledgePointCheckAllBySchemeId(@PathVariable("schemeId") Long schemeId) {
          List<Long> courseIds=knowledgeSourceService.KnowledgePointCheckAllBySchemeId(schemeId);
        if (CollectionUtils.isNotEmpty(courseIds)) {
            knowLedgeCheckLogService.knowledgePointCheck(courseIds);
        }
        return Message.success();
    }

    @ApiOperation("查询培养方案课程 type:1=课程,2=训练课,4=实践课")
    @GetMapping("/checkSchemeCourseList")
    public DataTable<Course> checkSchemeCourse(TrainingCourseVo trainingCourseVo) {
        List<Course> list = courseService.checkSchemeCourse(trainingCourseVo);
        return PageDataTable.success(list);

    }




}
