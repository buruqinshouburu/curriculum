package com.doinner.csys.io.service.impl;


import com.doinner.common.core.constant.SymbolConstants;
import com.doinner.common.core.exception.DataFormatException;
import com.doinner.common.core.utils.poi.ExcelUtil;
import com.doinner.csys.constant.DomainExceptionConstant;
import com.doinner.csys.constant.DomainFieldConstant;
import com.doinner.csys.dao.*;
import com.doinner.csys.domain.*;
import com.doinner.csys.domain.vo.*;
import com.doinner.csys.entity.csys.CourseChooseStatusGenerator;
import com.doinner.csys.entity.csys.model.CourseChooseStatusModel;
import com.doinner.csys.entity.csys.model.CourseChooseStatusModel.CourseSelectionRow;
import com.doinner.csys.io.handler.CourseExportHandler;
import com.doinner.csys.io.handler.MatrixExportHandler;
import com.doinner.csys.io.service.ExportService;
import com.doinner.csys.io.utils.TreeEntityUtils;
import com.doinner.csys.service.CurriculumService;
import com.doinner.csys.service.StandardService;
import com.doinner.csys.service.TrainingService;

import com.doinner.csys.utils.TreeBuilderUtils;
import com.doinner.system.domain.custom.CustomDept;
import com.doinner.system.domain.entity.SysDept;
import com.doinner.system.domain.entity.SysDictData;
import com.doinner.system.service.DoinnerDeptService;
import com.doinner.system.service.DoinnerDictDataService;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.*;
import java.util.stream.Collectors;


@Service
public class ExportServiceImpl implements ExportService {

    @Resource
    private CurriculumService curriculumService;

    @Resource
    private DoinnerDeptService doinnerDeptService;

    @Resource
    private DoinnerDictDataService doinnerDictDataService;

    @Resource
    private StandardService standardService;

    @Resource
    private TrainingService trainingService;

    @Resource
    private TrainingSchemeCourseScheduleMapper trainingSchemeCourseScheduleMapper;

    @Resource
    private TrainingSchemeMapper trainingSchemeMapper;
    @Resource
    private StandardGraduationMapper standardGraduationMapper;
    @Resource
    private CourseMapper courseMapper;
    @Resource
    private StandardMajorMapper standardMajorMapper;
    @Resource
    private TrainingSchemeCategoryMapper trainingSchemeCategoryMapper;
    @Resource
    private CourseRefKnowledgeUnitMapper courseRefKnowledgeUnitMapper;

    @Override
    public List<CourseExportVo> courseExportConvert(List<Long> courseIds) {
        List<Course> courseList = curriculumService.selectCourseById(courseIds);
        //查询几个字典
        List<SysDictData> coursePropList = doinnerDictDataService.dictType(DomainFieldConstant.COURSE_DICT_PROP).getData();
        Map<String, String> coursePropMap = coursePropList.parallelStream().collect(Collectors.toMap(SysDictData::getDictValue, SysDictData::getDictLabel));

        List<SysDictData> courseBroList = doinnerDictDataService.dictType(DomainFieldConstant.COURSE_DICT_BRO).getData();
        Map<String, String> courseBroMap = courseBroList.parallelStream().collect(Collectors.toMap(SysDictData::getDictValue, SysDictData::getDictLabel));

        List<SysDictData> courseAttrList = doinnerDictDataService.dictType(DomainFieldConstant.COURSE_DICT_ATTR).getData();
        Map<String, String> courseAttrMap = courseAttrList.parallelStream().collect(Collectors.toMap(SysDictData::getDictValue, SysDictData::getDictLabel));

        //查出课程类型
        List<SysDictData> dictDateList = doinnerDictDataService.dictType(DomainFieldConstant.COURSE_DICT_TYPE).getData();
        Map<String, String> dictCodeValueMap = dictDateList.parallelStream().collect(Collectors.toMap(SysDictData::getDictValue, SysDictData::getDictLabel));

        //查出全部部门
        CustomDept sysDept = new CustomDept();
        List<SysDept> list = doinnerDeptService.list(sysDept).getData();
        Map<Long, String> deptIdNameMap = list.parallelStream().collect(Collectors.toMap(SysDept::getDeptId, SysDept::getDeptName));

        List<Long> relationdCourseIdList = courseList.parallelStream().flatMap(course -> {
            List<String> _courseIds = new ArrayList<>();
            if (StringUtils.isNotBlank(course.getBeforeCourseId())) {
                _courseIds.addAll(Arrays.asList(course.getBeforeCourseId().split(SymbolConstants.COMMA)));
            }
            if (StringUtils.isNotBlank(course.getAfterCourseId())) {
                _courseIds.addAll(Arrays.asList(course.getAfterCourseId().split(SymbolConstants.COMMA)));
            }
            return _courseIds.parallelStream().map(_id -> {
                try {
                    return Long.valueOf(_id);
                } catch (NumberFormatException e) {
                    return null;
                }
            });
        }).filter(ObjectUtils::isNotEmpty).collect(Collectors.toList());
        Map<Long, String> relationdCourseMap;
        if (ObjectUtils.isNotEmpty(relationdCourseIdList)) {
            List<Course> relationdCourseList = curriculumService.selectCourseById(relationdCourseIdList);
            relationdCourseMap = relationdCourseList.parallelStream().collect(Collectors.toMap(Course::getId, Course::getName));
        } else {
            relationdCourseMap = new HashMap<>();
        }
        List<CourseExportVo> courseExportVoList = courseList.parallelStream().map(course -> {
            CourseExportVo courseExportVo = new CourseExportVo(course);
            if (ObjectUtils.isNotEmpty(course.getType()) && dictCodeValueMap.containsKey(course.getType().toString())) {
                courseExportVo.setType(dictCodeValueMap.get(course.getType().toString()));
            }
            if (ObjectUtils.isNotEmpty(course.getCourseProp())) {
                courseExportVo.setCourseProp(coursePropMap.get(course.getCourseProp().toString()));
            }
            if (ObjectUtils.isNotEmpty(course.getCourseType())) {
                courseExportVo.setCourseType(courseBroMap.get(course.getCourseType().toString()));
            }
            if (ObjectUtils.isNotEmpty(course.getCourseAttr())) {
                courseExportVo.setCourseAttr(courseAttrMap.get(course.getCourseAttr().toString()));
            }
            if (ObjectUtils.isNotEmpty(course.getBeforeCourseId()) && ObjectUtils.isNotEmpty(relationdCourseMap)) {
                String beforeCourseNames = Arrays.stream(course.getBeforeCourseId().split(SymbolConstants.COMMA)).map(_id -> {
                    try {
                        return relationdCourseMap.get(Long.valueOf(_id));
                    } catch (NumberFormatException e) {
                        return null;
                    }
                }).filter(ObjectUtils::isNotEmpty).collect(Collectors.joining(SymbolConstants.COMMA));
                courseExportVo.setBeforeCourseNames(beforeCourseNames);
            }
            if (ObjectUtils.isNotEmpty(course.getAfterCourseId()) && ObjectUtils.isNotEmpty(relationdCourseMap)) {
                String afterCourseNames = Arrays.stream(course.getAfterCourseId().split(SymbolConstants.COMMA)).map(_id -> {
                    try {
                        return relationdCourseMap.get(Long.valueOf(_id));
                    } catch (NumberFormatException e) {
                        return null;
                    }
                }).filter(ObjectUtils::isNotEmpty).collect(Collectors.joining(SymbolConstants.COMMA));
                courseExportVo.setAfterCourseNames(afterCourseNames);
            }
            if (ObjectUtils.isNotEmpty(course.getTeachCollegeId()) && deptIdNameMap.containsKey(course.getTeachCollegeId())) {
                courseExportVo.setTeachCollege(deptIdNameMap.get(course.getTeachCollegeId()));
            }
            if (ObjectUtils.isNotEmpty(course.getCollegeId()) && deptIdNameMap.containsKey(course.getCollegeId())) {
                courseExportVo.setCollege(deptIdNameMap.get(course.getCollegeId()));
            }
            return courseExportVo;
        }).collect(Collectors.toList());
        return courseExportVoList;
    }

    @Override
    public void courseDataExportConvert(HttpServletResponse response, List<Long> courseIds) {
        List<CourseVo> courseVos = curriculumService.selectCourseVoById(courseIds);
        if (ObjectUtils.isEmpty(courseVos)) {
            return;
        }
        List<TreeTableVo> treeTableVoList = courseVos.parallelStream().map(TreeTableVo::new).collect(Collectors.toList());

        //将前置后置课程的id变成名称
        List<Long> refCourseIds = treeTableVoList.stream().flatMap(treeTableVo -> {
            List<String> courseList = new ArrayList<>();
            if (StringUtils.isNotBlank(treeTableVo.getParams().get(DomainFieldConstant.EXCEL_TITLE_BEFORE_COURSE))) {
                courseList.addAll(Arrays.asList(treeTableVo.getParams().get(DomainFieldConstant.EXCEL_TITLE_BEFORE_COURSE).split(SymbolConstants.COMMA)));
            }
            if (StringUtils.isNotBlank(treeTableVo.getParams().get(DomainFieldConstant.EXCEL_TITLE_AFTER_COURSE))) {
                courseList.addAll(Arrays.asList(treeTableVo.getParams().get(DomainFieldConstant.EXCEL_TITLE_AFTER_COURSE).split(SymbolConstants.COMMA)));
            }
            return courseList.stream();
        }).map(Long::valueOf).collect(Collectors.toList());
        Map<Long, String> courseMap = curriculumService.selectCourseById(refCourseIds).parallelStream().collect(Collectors.toMap(Course::getId, Course::getName));
        treeTableVoList.stream().forEach(treeTableVo -> {
            if (StringUtils.isNotBlank(treeTableVo.getParams().get(DomainFieldConstant.EXCEL_TITLE_BEFORE_COURSE))) {
                String beforeCourseNames = Arrays.asList(treeTableVo.getParams().get(DomainFieldConstant.EXCEL_TITLE_BEFORE_COURSE).split(SymbolConstants.COMMA)).parallelStream()
                        .map(idString -> courseMap.get(Long.valueOf(idString))).filter(ObjectUtils::isNotEmpty).collect(Collectors.joining(SymbolConstants.COMMA));
                treeTableVo.getParams().put(DomainFieldConstant.EXCEL_TITLE_BEFORE_COURSE, beforeCourseNames);
            }
            if (StringUtils.isNotBlank(treeTableVo.getParams().get(DomainFieldConstant.EXCEL_TITLE_AFTER_COURSE))) {
                String beforeCourseNames = Arrays.asList(treeTableVo.getParams().get(DomainFieldConstant.EXCEL_TITLE_AFTER_COURSE).split(SymbolConstants.COMMA)).parallelStream()
                        .map(idString -> courseMap.get(Long.valueOf(idString))).filter(ObjectUtils::isNotEmpty).collect(Collectors.joining(SymbolConstants.COMMA));
                treeTableVo.getParams().put(DomainFieldConstant.EXCEL_TITLE_AFTER_COURSE, beforeCourseNames);
            }
        });
        CourseExportHandler excelExportHandler = new CourseExportHandler(treeTableVoList, DomainFieldConstant.EXCEL_FILE_NAME, DomainFieldConstant.EXCEL_SHEET_NAME);
        XSSFWorkbook xssfWorkbook = excelExportHandler.writeVerticalTreeTable();
        try {
            xssfWorkbook.write(response.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public MatrixVo assembleMatrix(Long id, Integer type) {
        MatrixVo matrixVo = null;
        switch (type) {
            case 1:
                matrixVo = standardGraduationToCultivationTargetMatrix(id);
                break;
            case 2:
                matrixVo = standardCultivationToGraduationMatrix(id);
                break;
            case 3:
                matrixVo = trainingSchemeCourseToGraduation(id);
                break;
        }
        return matrixVo;
    }

    public MatrixVo standardGraduationToCultivationTargetMatrix(Long id) {
        List<StandardGraduation> standardGraduations = standardService.selectStandardGraduationAll(id);
        if (ObjectUtils.isEmpty(standardGraduations)) {
            throw new DataFormatException(DomainExceptionConstant.GRADUATION_NOT_EXISTS);
        }
        StandardGraduation standardGraduation = standardService.getGraduationTree(standardGraduations);
        //查找培养目标
        ArrayList<StandardCultivationTarget> cultivationTargetList = standardService.findCultivationTargetByGraduations(standardGraduations);
        if (ObjectUtils.isEmpty(cultivationTargetList)) {
            throw new DataFormatException(DomainExceptionConstant.GRADUATION_NOT_REF_CULTIVATION_TARGET);
        }
        List<TreeTableVo> vertical = ((List<StandardGraduation>) standardGraduation.getChildren()).stream().map(_standardGraduation -> new TreeTableVo(_standardGraduation, DomainFieldConstant.ROOT_NODE_LONG_ID)).collect(Collectors.toList());
        //StandardCultivationTarget standardCultivationTarget = standardService.selectStdCultivationTargetTree(standardGraduation.getCultivationTargetId());
        //默认拿第一个最顶级的培养目标
        StandardCultivationTarget standardCultivationTarget = cultivationTargetList.stream().filter(c -> c.getParentId() == -1).collect(Collectors.toList()).get(0);
        //将最顶级目标筛选出来 然后对子类做操作
        List<TreeTableVo> horizontal = cultivationTargetList.stream().filter(c -> c.getParentId() != -1).map(_standardCultivationTarget -> new TreeTableVo(_standardCultivationTarget, DomainFieldConstant.ROOT_NODE_LONG_ID)).collect(Collectors.toList());
        String title = String.format(DomainFieldConstant.TOTAL_TITLE, standardGraduation.getName(), standardCultivationTarget.getName());
        List<ExcelRelationshipVo> relationshipVoList = null;
        List<StandardGraduation> children = (List<StandardGraduation>) standardGraduation.getChildren();
        if (ObjectUtils.isNotEmpty(children)) {
            List<Long> stdIds = TreeBuilderUtils.flattenTree(standardGraduation.getChildren()).parallelStream().filter(std -> std.getLeaf().equals(1)).map(StandardGraduation::getId).collect(Collectors.toList());
            //List<Long> stdIds = TreeEntityUtils.toList(((List<StandardGraduation>) (standardGraduation.getChildren()))).parallelStream().filter(std -> std.getLeaf().equals(1)).map(StandardGraduation::getId).collect(Collectors.toList());
            List<StandardGraduationRefCultivationTarget> standardGraduationRefCultivationTargetList =
                    standardService.selectStandardGraduationRefCultivationTargetByStandardGraduationId(stdIds);
            relationshipVoList = standardGraduationRefCultivationTargetList.parallelStream().map(ExcelRelationshipVo::new).collect(Collectors.toList());
        }
        MatrixVo matrixVo = new MatrixVo(vertical, horizontal, relationshipVoList, title,
                standardGraduation.getName()+","+ standardCultivationTarget.getName());
        return matrixVo;
    }

    public MatrixVo standardCultivationToGraduationMatrix(Long id) {
        StandardCultivation standardCultivation = standardService.selectStandardCultivationTree(id);
        if (ObjectUtils.isEmpty(standardCultivation)) {
            throw new DataFormatException(DomainExceptionConstant.CULTIVATION_NOT_EXISTS);
        }
        if (ObjectUtils.isEmpty(standardCultivation.getGraduationId())) {
            throw new DataFormatException(DomainExceptionConstant.CULTIVATION_NOT_REF_GRADUATION);
        }
        List<TreeTableVo> vertical = ((List<StandardCultivation>) standardCultivation.getChildren()).stream().map(_standardCultivation -> new TreeTableVo(_standardCultivation, DomainFieldConstant.ROOT_NODE_LONG_ID)).collect(Collectors.toList());
        StandardGraduation standardGraduation = standardService.selectStandardGraduationTree(standardCultivation.getGraduationId());
        List<TreeTableVo> horizontal = ((List<StandardGraduation>) standardGraduation.getChildren()).stream().map(_standardGraduation -> new TreeTableVo(_standardGraduation, DomainFieldConstant.ROOT_NODE_LONG_ID)).collect(Collectors.toList());
        String title = String.format(DomainFieldConstant.TOTAL_TITLE, standardCultivation.getName(), standardGraduation.getName());
        List<ExcelRelationshipVo> relationshipVoList = null;
        List<StandardCultivation> children = (List<StandardCultivation>) standardCultivation.getChildren();
        if (ObjectUtils.isNotEmpty(children)) {
            List<Long> stdIds = TreeEntityUtils.toList(((List<StandardCultivation>) (standardCultivation.getChildren()))).parallelStream().filter(std -> std.getLeaf().equals(1)).map(StandardCultivation::getId).collect(Collectors.toList());
            List<StandardCultivationRefGraduation> standardCultivationRefGraduationList = standardService
                    .selectStandardCultivationRefGraduationByStandardCultivationId(stdIds);
            relationshipVoList = standardCultivationRefGraduationList.parallelStream().map(ExcelRelationshipVo::new).collect(Collectors.toList());
        }
        MatrixVo matrixVo = new MatrixVo(vertical, horizontal, relationshipVoList, title,
                DomainFieldConstant.PARTIAL_TITLE_STANDARD_CULTIVATION_TO_STANDARD_GRADUATION);
        return matrixVo;
    }

    private MatrixVo trainingSchemeCourseToGraduation(Long id) {
        //查询培养方案下的课程
        TrainingSchemeVo trainingSchemeVo = trainingSchemeMapper.selectTrainingSchemeCoursesAllById(id);
        if (ObjectUtils.isEmpty(trainingSchemeVo)) {
            throw new DataFormatException(DomainExceptionConstant.TRAINING_SCHEME_NOT_EXISTS);
        }
        if (ObjectUtils.isEmpty(trainingSchemeVo.getCourseVos())) {
            throw new DataFormatException(DomainExceptionConstant.TRAINING_SCHEME_COURSE_NOT_EXISTS);
        }
        List<TrainingSchemeCourseVo> courseVos = trainingSchemeVo.getCourseVos();
        //查询培养方案对应的毕业标准
        List<StandardGraduation> standardGraduations = standardGraduationMapper.selectStandardGraduationByMajorId(trainingSchemeVo);
        if (ObjectUtils.isEmpty(standardGraduations)) {
            throw new DataFormatException(DomainExceptionConstant.GRADUATION_NOT_EXISTS);
        }
        //查询毕业标准关联的课程  只展示三级的毕业标准关联的课程
        List<ExcelRelationshipVo> relationshipVoList = courseMapper.selectCourseRefGraduation(standardGraduations.stream().filter(s -> s.getLeaf() == 1).map(s -> s.getId()).collect(Collectors.toList()),
                courseVos.stream().map(c -> c.getId()).collect(Collectors.toList()));
        //构建表格行和列
        List<TreeTableVo> horizontal = new ArrayList<>();
        standardGraduations.stream().filter(g->g.getLeaf()==1).forEach(graduation -> {
            TreeTableVo treeTableVo = new TreeTableVo();
            treeTableVo.setId(graduation.getId().toString());
            treeTableVo.setName(graduation.getName());
            treeTableVo.setSize(treeTableVo.getSize()==null?1:treeTableVo.getSize()+1);
            horizontal.add(treeTableVo);
        });
        List<TreeTableVo> vertical = new ArrayList<>();
        courseVos.forEach(c -> {
            TreeTableVo treeTableVo = new TreeTableVo();
            treeTableVo.setId(c.getId().toString());
            treeTableVo.setName(c.getName());
            treeTableVo.setSize(treeTableVo.getSize()==null?1:treeTableVo.getSize()+1);
            vertical.add(treeTableVo);
        });
        MatrixVo matrixVo = new MatrixVo(vertical, horizontal, relationshipVoList, "课程对毕业要求关系矩阵",
                "毕业要求,课程");
        return matrixVo;
    }

    @Override
    public void exportMatrix(HttpServletResponse response, Long id, Integer type) throws UnsupportedEncodingException {
        MatrixVo matrixVo = assembleMatrix(id, type);
        MatrixExportHandler matrixExportHandler = new MatrixExportHandler(matrixVo);
        switch (type) {
            case 1:
                matrixExportHandler.setSheetName(DomainFieldConstant.SHEET_NAME_STANDARD_GRADUATION_TO_STANDARD_CULTIVATION_TARGET);
                matrixExportHandler.setVerticalSize(2);
                matrixExportHandler.setHorizontalSize(1);
                response.setHeader("Content-disposition", "attachment;filename*=UTF-8''" + URLEncoder.encode(DomainFieldConstant.EXCEL_NAME_STANDARD_GRADUATION_TO_STANDARD_CULTIVATION_TARGET, "utf-8"));
                break;
            case 2:
                matrixExportHandler.setSheetName(DomainFieldConstant.SHEET_NAME_STANDARD_CULTIVATION_TO_STANDARD_GRADUATION);
                matrixExportHandler.setVerticalSize(6);
                matrixExportHandler.setHorizontalSize(3);
                response.setHeader("Content-disposition", "attachment;filename*=UTF-8''" + URLEncoder.encode(DomainFieldConstant.EXCEL_NAME_STANDARD_CULTIVATION_TO_STANDARD_GRADUATION, "utf-8"));
                break;
            case 3:
                matrixExportHandler.setSheetName(DomainFieldConstant.PARTIAL_TITLE_COURSE_TO_STANDARD_GRADUATION);
                matrixExportHandler.setVerticalSize(1);
                matrixExportHandler.setHorizontalSize(1);
                matrixExportHandler.setPartialTitle("课程/毕业要求");
                matrixExportHandler.setTotalTitle(DomainFieldConstant.PARTIAL_TITLE_COURSE_TO_STANDARD_GRADUATION);
                //matrixExportHandler.setVerticalTitle(List.of("课程"));
                //matrixExportHandler.setVerticalTitle(List.of("编号", "一级标准", "编号", "二级标准", "编号", "三级标准"));
                //matrixExportHandler.setHorizontalTitle(List.of("毕业标准"));
                response.setHeader("Content-disposition", "attachment;filename*=UTF-8''" + URLEncoder.encode(DomainFieldConstant.EXCEL_NAME_COURSE_TO_STANDARD_GRADUATION, "utf-8"));
                break;
        }
        XSSFWorkbook xssfWorkbook = matrixExportHandler.create();
        try {
            xssfWorkbook.write(response.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void exportCourseChooseStatus(HttpServletResponse response, List<Long> sourceCourseIds) {
        if (ObjectUtils.isEmpty(sourceCourseIds)) {
            return;
        }
        List<CourseChooseStatusModel> models = buildCourseChooseStatusModels(sourceCourseIds);
        try {
            CourseChooseStatusGenerator generator = new CourseChooseStatusGenerator();
            XSSFWorkbook workbook = generator.generate(models);
            response.setContentType("application/x-download");
            String fileName = "课程被选用情况表.xlsx";
            response.setHeader("Content-disposition", "attachment;filename*=UTF-8''" + URLEncoder.encode(fileName, "utf-8"));
            workbook.write(response.getOutputStream());
            workbook.close();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * 组装课程被选用情况模型。
     * <p>
     * 入参为源课程ID集合；通过 source_id 定位被选用课程及其排课记录，
     * 课程名称/编号取自源课程，选用单位/选用专业类/选用专业取自排课所属培养方案，
     * 各学期学时按(选用单位/选用专业类/选用专业)分组聚合(讲授+实践)。
     *
     * @param sourceCourseIds 源课程id集合
     * @return 模型集合，按入参顺序输出
     */
    public List<CourseChooseStatusModel> buildCourseChooseStatusModels(List<Long> sourceCourseIds) {
        if (ObjectUtils.isEmpty(sourceCourseIds)) {
            return new ArrayList<>();
        }
        // 源课程基本信息(名称/编号)作为表格中"课程名称/课程编号"列，按入参顺序输出
        List<Course> sourceCourseList = curriculumService.selectCourseById(sourceCourseIds);
        Map<Long, Course> sourceCourseMap = new LinkedHashMap<>();
        if (ObjectUtils.isNotEmpty(sourceCourseList)) {
            for (Course course : sourceCourseList) {
                sourceCourseMap.put(course.getId(), course);
            }
        }

        // 通过 source_id 查询被选用课程及其排课记录；
        // 选用单位/选用专业类/选用专业取自排课所属培养方案(ts)关联字段。
        List<CourseChooseStatusVo> statusVos = trainingSchemeCourseScheduleMapper.selectCourseChooseStatus(sourceCourseIds);
        Map<Long, List<CourseChooseStatusVo>> statusBySource = ObjectUtils.isEmpty(statusVos)
                ? new HashMap<>()
                : statusVos.parallelStream().collect(Collectors.groupingBy(CourseChooseStatusVo::getSourceCourseId, LinkedHashMap::new, Collectors.toList()));

        // 按入参顺序组装模型，每个源课程一个模型
        List<CourseChooseStatusModel> models = new ArrayList<>();
        for (Long sourceCourseId : sourceCourseIds) {
            Course sourceCourse = sourceCourseMap.get(sourceCourseId);
            if (sourceCourse == null) {
                continue;
            }
            CourseChooseStatusModel model = new CourseChooseStatusModel(sourceCourse.getName(), sourceCourse.getCode());
            List<CourseChooseStatusVo> vos = statusBySource.getOrDefault(sourceCourseId, Collections.emptyList());

            // 按(选用单位/选用专业类/选用专业)分组，聚合各学期学时(讲授+实践)
            LinkedHashMap<String, CourseSelectionRow> rowMap = new LinkedHashMap<>();
            for (CourseChooseStatusVo vo : vos) {
                String unit = vo.getCollegeName() == null ? "" : vo.getCollegeName();
                String category = vo.getCategoryName() == null ? "" : vo.getCategoryName();
                String major = vo.getMajorName() == null ? "" : vo.getMajorName();
                String key = unit + "##" + category + "##" + major;
                CourseSelectionRow row = rowMap.computeIfAbsent(key, k -> new CourseSelectionRow(unit, category, major));
                Integer term = vo.getTerm();
                if (term == null || term < 1 || term > 8) {
                    continue;
                }
                double teachHours = vo.getTeachHours() == null ? 0.0 : vo.getTeachHours();
                double practiceHours = vo.getPracticeHours() == null ? 0.0 : vo.getPracticeHours();
                row.addTermHours(term - 1, teachHours + practiceHours);
            }
            model.getRows().addAll(rowMap.values());
            models.add(model);
        }
        return models;
    }

    @Override
    public void exportGraduation(HttpServletResponse response, List<Long> ids) {
        List<GraduationRow> rows = buildGraduationExportRows(ids);
        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            XSSFSheet sheet = workbook.createSheet("毕业要求");
            String[] headers = {"毕业要求", "适用对象", "一级指标", "具体要求"};
            XSSFCellStyle headStyle = buildHeadStyle(workbook);
            XSSFCellStyle bodyStyle = buildBodyStyle(workbook);

            // 表头
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headStyle);
            }

            // 数据行
            for (int i = 0; i < rows.size(); i++) {
                GraduationRow r = rows.get(i);
                Row row = sheet.createRow(i + 1);
                setCell(row, 0, r.graduationName, bodyStyle);
                setCell(row, 1, r.applicable, bodyStyle);
                setCell(row, 2, r.firstIndicator, bodyStyle);
                setCell(row, 3, r.specificReq, bodyStyle);
            }

            // 第一列(毕业要求)、第二列(适用对象) 按一级节点(level1Id)合并
            mergeConsecutive(sheet, rows, 0, true);
            mergeConsecutive(sheet, rows, 1, true);
            // 第三列(一级指标) 按二级节点(level2Id)合并
            mergeConsecutive(sheet, rows, 2, false);

            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }
            sheet.setColumnWidth(0,15 * 256);
            sheet.setColumnWidth(1, 30 * 256);
            sheet.setColumnWidth(2, 80 * 256);
            sheet.setColumnWidth(3, 255 * 256);
            //冻结表头
            sheet.createFreezePane(0,1);

            workbook.write(response.getOutputStream());
            response.getOutputStream().flush();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public void exportCourseKnowledge(HttpServletResponse response, List<Long> courseIds) {
        List<CourseKnowledgeExportRow> rows = courseRefKnowledgeUnitMapper.selectExportRowsByCourseIds(courseIds);
        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            XSSFSheet sheet = workbook.createSheet("知识单元知识点");
            String[] headers = {"课程名称", "知识单元", "知识点"};
            XSSFCellStyle headStyle = buildHeadStyle(workbook);
            XSSFCellStyle bodyStyle = buildBodyStyle(workbook);

            // 表头
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headStyle);
            }

            // 数据行
            for (int i = 0; i < rows.size(); i++) {
                CourseKnowledgeExportRow r = rows.get(i);
                Row row = sheet.createRow(i + 1);
                setCell(row, 0, r.getCourseName(), bodyStyle);
                setCell(row, 1, r.getUnitName(), bodyStyle);
                setCell(row, 2, r.getPointName(), bodyStyle);
            }

            // 第一列(课程名称) 按 courseId 连续合并
            List<Long> courseKeys = rows.stream().map(CourseKnowledgeExportRow::getCourseId).collect(Collectors.toList());
            mergeConsecutiveByKey(sheet, 0, courseKeys);
            // 第二列(知识单元) 按 unitId 连续合并
            List<Long> unitKeys = rows.stream().map(CourseKnowledgeExportRow::getUnitId).collect(Collectors.toList());
            mergeConsecutiveByKey(sheet, 1, unitKeys);

            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }
            sheet.setColumnWidth(0, 30 * 256);
            sheet.setColumnWidth(1, 30 * 256);
            sheet.setColumnWidth(2, 50 * 256);
            //冻结表头
            sheet.createFreezePane(0, 1);

            workbook.write(response.getOutputStream());
            response.getOutputStream().flush();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * 按分组键列表对指定列做连续行合并: 连续相同且非 null 的 key 合并为一个区域。
     * 行号 +1 偏移表头行(与毕业要求导出一致)。
     */
    private void mergeConsecutiveByKey(Sheet sheet, int col, List<Long> keys) {
        if (CollectionUtils.isEmpty(keys)) {
            return;
        }
        int start = 0;
        for (int i = 1; i <= keys.size(); i++) {
            Long cur = i < keys.size() ? keys.get(i) : null;
            Long seg = keys.get(start);
            boolean same = cur != null && seg != null && Objects.equals(cur, seg);
            if (!same) {
                int end = i - 1;
                if (end > start && seg != null) {
                    sheet.addMergedRegion(new CellRangeAddress(start + 1, end + 1, col, col));
                }
                start = i;
            }
        }
    }

    /**
     * 毕业要求导出行: 毕业要求名称 / 适用对象 / 一级指标 / 具体要求(二级要求)
     * 按树 DFS 展开, 每个叶子生成一行, 并记录所属的一级/二级节点 id 供单元格合并使用.
     */
    private List<GraduationRow> buildGraduationExportRows(List<Long> ids) {
        List<GraduationRow> rows = new ArrayList<>();
        if (CollectionUtils.isEmpty(ids)) {
            return rows;
        }
        // 收集所有传入id对应子树(包含自身)的节点, 去重.
        // 注意: selectStandardGraduationAll(id) 的结果已含 id 自身,
        // 因此不要预先占位 id, 否则根节点(level=1, parentId=-1)会被误去重丢弃.
        Set<Long> visited = new HashSet<>();
        List<StandardGraduation> allNodes = new ArrayList<>();
        for (Long id : ids) {
            if (id == null) {
                continue;
            }
            List<StandardGraduation> subNodes = standardGraduationMapper.selectStandardGraduationAll(id);
            if (CollectionUtils.isNotEmpty(subNodes)) {
                for (StandardGraduation g : subNodes) {
                    if (g.getId() != null && visited.add(g.getId())) {
                        allNodes.add(g);
                    }
                }
            }
        }
        if (CollectionUtils.isEmpty(allNodes)) {
            return rows;
        }

        // 培养层次(适用对象)字典: code -> label
        Map<String, String> eduLevelMap = buildDictMap("sys_education_level");
        // 毕业要求类型字典: code -> label (名称为空时回退使用)
        Map<String, String> gradTypeMap = buildDictMap("sys_graduation_type");

        // 构建父子关系
        Map<Long, List<StandardGraduation>> childrenMap = new HashMap<>();
        for (StandardGraduation g : allNodes) {
            childrenMap.computeIfAbsent(g.getParentId(), k -> new ArrayList<>()).add(g);
        }
        Set<Long> idSet = allNodes.stream().map(StandardGraduation::getId).collect(Collectors.toSet());
        List<StandardGraduation> roots = allNodes.stream()
                .filter(g -> g.getParentId() == null
                        || DomainFieldConstant.ROOT_NODE_LONG_ID.equals(g.getParentId())
                        || !idSet.contains(g.getParentId()))
                .collect(Collectors.toList());

        for (StandardGraduation root : roots) {
            collectGraduationRows(root, null, null, eduLevelMap, gradTypeMap, childrenMap, rows);
        }
        return rows;
    }

    /**
     * 深度优先遍历毕业要求树, 每个叶子生成一行.
     * level1=毕业要求(列1/列2), level2=一级指标(列3), level3叶子=具体要求(列4)
     */
    private void collectGraduationRows(StandardGraduation node, StandardGraduation level1,
                                       StandardGraduation level2, Map<String, String> eduLevelMap,
                                       Map<String, String> gradTypeMap,
                                       Map<Long, List<StandardGraduation>> childrenMap,
                                       List<GraduationRow> rows) {
        int level = (node.getLevel() == null) ? 1 : node.getLevel();
        StandardGraduation l1 = level1;
        StandardGraduation l2 = level2;
        if (level == 1) {
            l1 = node;
        } else if (level == 2) {
            l2 = node;
        }
        List<StandardGraduation> children = childrenMap.get(node.getId());
        if (CollectionUtils.isEmpty(children)) {
            GraduationRow row = new GraduationRow();
            if (l1 != null) {
                row.level1Id = l1.getId();
                row.graduationName = gradNameOrType(l1, gradTypeMap);
                row.applicable = dictLabel(l1.getEducationLevel(), eduLevelMap);
            }
            if (l2 != null) {
                row.level2Id = l2.getId();
                row.firstIndicator = l2.getName();
            }
            // 叶子为三级时, 具体要求取叶子名称; 叶子为二级(自身即一级指标)时具体要求留空
            if (level == 3) {
                row.specificReq = node.getName();
            }
            rows.add(row);
        } else {
            for (StandardGraduation child : children) {
                collectGraduationRows(child, l1, l2, eduLevelMap, gradTypeMap, childrenMap, rows);
            }
        }
    }

    /**
     * 按行分组连续合并: byLevel1=true 按一级节点id分组, 否则按二级节点id分组.
     * 仅在 level2Id 非空时合并第三列, 避免不同毕业要求下空的二级指标被误合并.
     */
    private void mergeConsecutive(Sheet sheet, List<GraduationRow> rows, int col, boolean byLevel1) {
        if (rows.isEmpty()) {
            return;
        }
        int start = 0;
        for (int i = 1; i <= rows.size(); i++) {
            GraduationRow cur = i < rows.size() ? rows.get(i) : null;
            GraduationRow seg = rows.get(start);
            Long segKey = byLevel1 ? seg.level1Id : seg.level2Id;
            boolean same = false;
            if (cur != null) {
                Long curKey = byLevel1 ? cur.level1Id : cur.level2Id;
                same = Objects.equals(curKey, segKey);
            }
            if (!same) {
                int end = i - 1;
                if (end > start && segKey != null) {
                    sheet.addMergedRegion(new CellRangeAddress(start + 1, end + 1, col, col));
                }
                start = i;
            }
        }
    }

    private String gradNameOrType(StandardGraduation g, Map<String, String> gradTypeMap) {
        if (StringUtils.isNotBlank(g.getName())) {
            return g.getName();
        }
        return dictLabel(g.getGraduationType(), gradTypeMap);
    }

    private String dictLabel(String code, Map<String, String> map) {
        if (StringUtils.isBlank(code)) {
            return "";
        }
        String label = map.get(code);
        return StringUtils.isNotBlank(label) ? label : code;
    }

    private Map<String, String> buildDictMap(String dictType) {
        try {
            List<SysDictData> list = doinnerDictDataService.dictType(dictType).getData();
            if (CollectionUtils.isEmpty(list)) {
                return new HashMap<>();
            }
            return list.stream().collect(Collectors.toMap(SysDictData::getDictValue, SysDictData::getDictLabel, (a, b) -> a));
        } catch (Exception e) {
            return new HashMap<>();
        }
    }

    private void setCell(Row row, int col, String value, CellStyle style) {
        Cell cell = row.createCell(col);
        cell.setCellValue(value == null ? "" : value);
        cell.setCellStyle(style);
    }

    private XSSFCellStyle buildHeadStyle(XSSFWorkbook workbook) {
        XSSFCellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        applyBorders(style);
        return style;
    }

    private XSSFCellStyle buildBodyStyle(XSSFWorkbook workbook) {
        XSSFCellStyle style = workbook.createCellStyle();
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        applyBorders(style);
        return style;
    }

    private void applyBorders(XSSFCellStyle style) {
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
    }

    /** 毕业要求导出行数据 */
    private static class GraduationRow {
        Long level1Id;
        Long level2Id;
        String graduationName;
        String applicable;
        String firstIndicator;
        String specificReq;
    }

    @Override
    public List<GraduationExcelVo> buildGraduationExcelVos(List<Long> ids) {
        List<GraduationExcelVo> result = new ArrayList<>();
        if (CollectionUtils.isEmpty(ids)) {
            return result;
        }
        // 收集所有传入id对应子树(包含自身)的节点, 去重
        Set<Long> visited = new HashSet<>();
        List<StandardGraduation> allNodes = new ArrayList<>();
        for (Long id : ids) {
            if (id == null || !visited.add(id)) {
                continue;
            }
            List<StandardGraduation> subNodes = standardGraduationMapper.selectStandardGraduationAll(id);
            if (CollectionUtils.isNotEmpty(subNodes)) {
                for (StandardGraduation g : subNodes) {
                    if (g.getId() != null && visited.add(g.getId())) {
                        allNodes.add(g);
                    }
                }
            }
        }
        if (CollectionUtils.isEmpty(allNodes)) {
            return result;
        }

        // 学院/专业/学科门类 名称映射
        List<SysDept> deptList = doinnerDeptService.list(new CustomDept()).getData();
        Map<Long, String> deptIdNameMap = CollectionUtils.isEmpty(deptList)
                ? new HashMap<>()
                : deptList.stream().collect(Collectors.toMap(SysDept::getDeptId, SysDept::getDeptName, (a, b) -> a));
        List<StandardMajor> majorList = standardMajorMapper.selectStandardMajorList(null);
        Map<Long, String> majorIdNameMap = CollectionUtils.isEmpty(majorList)
                ? new HashMap<>()
                : majorList.stream().collect(Collectors.toMap(StandardMajor::getId, StandardMajor::getName, (a, b) -> a));
        List<TrainingSchemeCategory> categoryList = trainingSchemeCategoryMapper.selectTrainingSchemeCategoryList(null);
        Map<Long, String> categoryIdNameMap = CollectionUtils.isEmpty(categoryList)
                ? new HashMap<>()
                : categoryList.stream().collect(Collectors.toMap(TrainingSchemeCategory::getId, TrainingSchemeCategory::getName, (a, b) -> a));

        // 构建父子关系
        Set<Long> idSet = allNodes.stream().map(StandardGraduation::getId).collect(Collectors.toSet());
        Map<Long, List<StandardGraduation>> childrenMap = new HashMap<>();
        for (StandardGraduation g : allNodes) {
            childrenMap.computeIfAbsent(g.getParentId(), k -> new ArrayList<>()).add(g);
        }
        // 根节点: parentId 为 -1 或不在当前导出集合中的节点
        List<StandardGraduation> roots = allNodes.stream()
                .filter(g -> g.getParentId() == null
                        || DomainFieldConstant.ROOT_NODE_LONG_ID.equals(g.getParentId())
                        || !idSet.contains(g.getParentId()))
                .collect(Collectors.toList());

        for (StandardGraduation root : roots) {
            buildGraduationRows(root, new ArrayList<>(), childrenMap,
                    deptIdNameMap, majorIdNameMap, categoryIdNameMap, result);
        }
        return result;
    }

    /**
     * 深度优先遍历毕业要求树, 每个叶子节点生成一行, 行中按层级填充各级祖先名称/编码
     */
    private void buildGraduationRows(StandardGraduation node, List<StandardGraduation> path,
                                     Map<Long, List<StandardGraduation>> childrenMap,
                                     Map<Long, String> deptIdNameMap, Map<Long, String> majorIdNameMap,
                                     Map<Long, String> categoryIdNameMap, List<GraduationExcelVo> result) {
        path.add(node);
        List<StandardGraduation> children = childrenMap.get(node.getId());
        if (CollectionUtils.isEmpty(children)) {
            result.add(toGraduationExcelVo(path, deptIdNameMap, majorIdNameMap, categoryIdNameMap));
        } else {
            for (StandardGraduation child : children) {
                buildGraduationRows(child, path, childrenMap,
                        deptIdNameMap, majorIdNameMap, categoryIdNameMap, result);
            }
        }
        path.remove(path.size() - 1);
    }

    private GraduationExcelVo toGraduationExcelVo(List<StandardGraduation> path,
                                                  Map<Long, String> deptIdNameMap,
                                                  Map<Long, String> majorIdNameMap,
                                                  Map<Long, String> categoryIdNameMap) {
        GraduationExcelVo vo = new GraduationExcelVo();
        for (StandardGraduation g : path) {
            int level = (g.getLevel() == null) ? 1 : g.getLevel();
            if (level == 1) {
                vo.setFirstName(g.getName());
                vo.setFirstCode(g.getCode());
            } else if (level == 2) {
                vo.setSecondName(g.getName());
                vo.setSecondCode(g.getCode());
            } else {
                vo.setThirdName(g.getName());
                vo.setThirdCode(g.getCode());
            }
        }
        // 学院/专业/学科门类/版本/备注 取叶子节点(同一子树内一致)
        StandardGraduation leaf = path.get(path.size() - 1);
        vo.setCollegeName(deptIdNameMap.get(leaf.getCollegeId()));
        vo.setMajorName(majorIdNameMap.get(leaf.getMajorId()));
        vo.setCategoryName(categoryIdNameMap.get(leaf.getCategoryId()));
        vo.setVersion(leaf.getVersion());
        vo.setRemark(leaf.getRemark());
        return vo;
    }
}
