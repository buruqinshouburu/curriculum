package com.doinner.csys.entity.csys;

import com.doinner.csys.domain.StandardGraduation;
import com.doinner.csys.domain.TeachingPlanAssessment;
import com.doinner.csys.domain.TeachingPlanCondition;
import com.doinner.csys.domain.TeachingPlanContent;
import com.doinner.csys.domain.TeachingPlanObjective;
import com.doinner.csys.domain.TeachingPlanObjectiveRef;
import com.doinner.csys.domain.TeachingPlanPracticeItem;
import com.doinner.csys.domain.TeachingPlanPracticeItemDetail;
import com.doinner.csys.domain.TeachingPlanSection;
import com.doinner.csys.domain.TeachingPlanTargetDesign;
import com.doinner.csys.domain.TeachingPlanTeacher;
import com.doinner.csys.domain.TeachingPlanTextbook;
import com.doinner.csys.entity.csys.model.CourseTeachingPlanModel;
import com.doinner.csys.utils.WordUtil;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.xwpf.usermodel.TableWidthType;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 课程教学计划 Word 文档生成器。
 *
 * 入口 {@link #generate(CourseTeachingPlanModel)}：按 {@link CourseTeachingPlanModel#getDocType()} 分发到四套模板：
 * 1=课程教学计划 / 2=实践训练课目教学计划 / 3=实验课程教学计划 / 4=实践项目教学计划。
 *
 * 渲染沿用 {@link TrainingPlanGenerator} 的方式：{@link WordUtil} 画表 + 合并单元格 + initTableGrid。
 * 有数据则填、无数据留空单元格，保证产出文档结构与模板一致。
 */
public class CourseTeachingPlanGenerator {

    /** 文档类型常量，与 t_csys_course.type 一致 */
    public static final int DOC_TYPE_COURSE = 1;
    public static final int DOC_TYPE_PRACTICE_SUBJECT = 2;
    public static final int DOC_TYPE_EXPERIMENT_COURSE = 3;
    public static final int DOC_TYPE_PRACTICE_PROJECT = 4;

    /** 单列宽度(dxa)，合并列时按 span 倍数设置 */
    private static final int COL_W = 1000;

    /** 五号字 ≈ 10.5 磅 */
    private static final double FONT_SIZE_WUHAO = 10.5;
    /** 章节标题：黑体五号 */
    private static final String FONT_HEADING = "黑体";
    /** 正文：仿宋_GB2312 五号 */
    private static final String FONT_BODY = "仿宋_GB2312";
    /** 表头：黑体五号 */
    private static final String FONT_TABLE_HEADER = "黑体";
    /** 表内容：宋体五号 */
    private static final String FONT_TABLE_BODY = "宋体";

    /** 考核类别：1 总结性考核，2 形成性考核（展示文案按产品要求，非库注释「终结性」） */
    private static final int ASSESS_CAT_SUMMATIVE = 1;
    private static final int ASSESS_CAT_FORMATIVE = 2;

    /** 目标/达成设计类型关键字（objectiveTypeCode / designTypeCode 为字典编码，按包含匹配） */
    private static final String KEY_KNOWLEDGE = "知识";
    private static final String KEY_ABILITY = "能力";
    private static final String KEY_QUALITY = "素质";

    /** 实验项目明细类型 -> 展示标签（与模板样图文案对齐） */
    private static final Map<String, String> DETAIL_LABEL = new LinkedHashMap<String, String>() {{
        put("purpose_task", "实验目的与任务");
        put("ability_point", "训练的能力点");
        put("principle", "原理");
        put("content_requirement", "实验内容及要求");
        put("result_requirement", "实验结果及要求");
        put("teaching_design", "教学设计");
        put("complex_problem", "拟解决的复杂问题");
        put("main_task", "主要任务");
        put("overall_design", "总体设计");
        put("outcome_requirement", "成果形式及要求");
    }};

    /** 实验类项目明细优先顺序 */
    private static final List<String> EXPERIMENT_DETAIL_ORDER = Arrays.asList(
            "purpose_task", "content_requirement", "result_requirement", "teaching_design",
            "ability_point", "principle"
    );

    /** 实践类项目明细优先顺序 */
    private static final List<String> PRACTICE_DETAIL_ORDER = Arrays.asList(
            "complex_problem", "main_task", "overall_design", "outcome_requirement",
            "purpose_task", "content_requirement", "result_requirement", "teaching_design"
    );

    // ============================ 入口 ============================

    public InputStream generate(CourseTeachingPlanModel model) throws IOException {
        XWPFDocument document = new XWPFDocument();
        try {
            Integer t = model.getDocType();
            if (t == null) {
                t = DOC_TYPE_COURSE;
            }
            switch (t) {
                case DOC_TYPE_EXPERIMENT_COURSE:
                    generateExperimentCourseDoc(model, document);
                    break;
                case DOC_TYPE_PRACTICE_SUBJECT:
                    generatePracticeSubjectDoc(model, document);
                    break;
                case DOC_TYPE_PRACTICE_PROJECT:
                    generatePracticeProjectDoc(model, document);
                    break;
                default:
                    generateCourseDoc(model, document);
                    break;
            }
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            document.write(out);
            return new ByteArrayInputStream(out.toByteArray());
        } finally {
            document.close();
        }
    }

    // ============================ type=1 课程教学计划（9 节） ============================

    private void generateCourseDoc(CourseTeachingPlanModel m, XWPFDocument doc) {
        WordUtil.createTitle(doc, "《" + str(m.getCourseName()) + "》课程教学计划", FONT_HEADING, FONT_SIZE_WUHAO);

        // 一、课程基本信息
        h1(doc, "一、课程基本信息");
        courseBasicInfoTable(m, doc);

        // 二、课程教学团队
        h1(doc, "二、课程教学团队");
        teacherTable(m.getTeachers(), doc);

        // 三、课程概述
        h1(doc, "三、课程概述");
        renderSections(doc, m.getSections());

        // 四、课程目标与支撑毕业要求
        h1(doc, "四、课程目标与支撑毕业要求");
        objectiveTable(m, doc);

        // 五、课程教学内容与时间安排
        h1(doc, "五、课程教学内容与时间安排");
        contentTable(m.getContents(), doc);

        // 六、课程目标与教学实现设计
        h1(doc, "六、课程目标与教学实现设计");
        // 说明：教学环节/教法/学法取字典表 label 拼接（由 Service 预填）
        bodyParagraph(doc, "说明：");
        if (StringUtils.isNotBlank(m.getTeachingLinkNote())) {
            bodyParagraph(doc, m.getTeachingLinkNote());
        }
        if (StringUtils.isNotBlank(m.getTeachingMethodNote())) {
            bodyParagraph(doc, m.getTeachingMethodNote());
        }
        if (StringUtils.isNotBlank(m.getLearningMethodNote())) {
            bodyParagraph(doc, m.getLearningMethodNote());
        }
        h2(doc, "（一）知识目标和教学实现设计");
        targetDesignTable(filterDesign(m.getTargetDesigns(), KEY_KNOWLEDGE), doc);
        h2(doc, "（二）能力目标和教学实现设计");
        abilityQualityDesignTable(filterDesign(m.getTargetDesigns(), KEY_ABILITY), doc, "课程能力目标");
        h2(doc, "（三）素质目标和教学实现设计");
        abilityQualityDesignTable(filterDesign(m.getTargetDesigns(), KEY_QUALITY), doc, "课程素质目标");

        // 七、实验/实践环节教学设计
        h1(doc, "七、实验/实践环节教学设计");
        practiceItemTable(m.getPracticeItems(), m.getItemDetailMap(), doc);

        // 八、考核评价
        h1(doc, "八、考核评价");
        assessmentTable(m.getAssessments(), m.getScoreRule(), doc);

        // 九、教学条件
        h1(doc, "九、教学条件");
        h2(doc, "（一）配套教材");
        textbookTable(m.getTextbooks(), doc);
        h2(doc, "（二）教学条件及资源");
        conditionTable(m.getConditions(), doc);
    }

    // ============================ type=3 实验课程教学计划（7 节） ============================

    private void generateExperimentCourseDoc(CourseTeachingPlanModel m, XWPFDocument doc) {
        WordUtil.createTitle(doc, "《" + str(m.getCourseName()) + "》实验课程教学计划", FONT_HEADING, FONT_SIZE_WUHAO);

        h1(doc, "一、课程基本信息");
        experimentBasicInfoTable(m, doc);

        h1(doc, "二、课程教学团队");
        teacherTable(m.getTeachers(), doc);

        h1(doc, "三、任务背景与目标");
        // 表：任务背景描述 | 技术目标 | 能力目标 | 支撑的毕业要求
        taskBackgroundTable(m, doc, 4);

        h1(doc, "四、主要内容与基本要求");
        practiceItemTable(m.getPracticeItems(), m.getItemDetailMap(), doc);

        h1(doc, "五、实施安排");
        experimentArrangementTable(m.getPracticeItems(), doc);

        h1(doc, "六、考核与评价");
        assessmentTable(m.getAssessments(), m.getScoreRule(), doc);

        h1(doc, "七、实验教材或指导书");
        textbookTable(m.getTextbooks(), doc);
    }

    // ============================ type=2 实践训练课目教学计划（7 节） ============================

    private void generatePracticeSubjectDoc(CourseTeachingPlanModel m, XWPFDocument doc) {
        WordUtil.createTitle(doc, "《" + str(m.getCourseName()) + "》实践训练课目教学计划", FONT_HEADING, FONT_SIZE_WUHAO);

        h1(doc, "一、课目基本信息");
        practiceSubjectBasicInfoTable(m, doc);

        h1(doc, "二、训练目的与支撑毕业要求");
        trainingPurposeTable(m, doc);

        h1(doc, "三、训练任务与总体设计");
        trainingTaskTable(m, doc);

        h1(doc, "四、训练内容与时间安排");
        trainingContentTable(m.getContents(), doc);

        h1(doc, "五、组织实施");
        organizationTable(m, doc);

        h1(doc, "六、考核与评价");
        assessmentTable(m.getAssessments(), m.getScoreRule(), doc);

        h1(doc, "七、训练条件及资源");
        conditionTable(m.getConditions(), doc);
    }

    // ============================ type=4 实践项目教学计划（5 节） ============================

    private void generatePracticeProjectDoc(CourseTeachingPlanModel m, XWPFDocument doc) {
        WordUtil.createTitle(doc, "《" + str(m.getCourseName()) + "》实践项目教学计划", FONT_HEADING, FONT_SIZE_WUHAO);

        h1(doc, "一、项目基本信息");
        practiceProjectBasicInfoTable(m, doc);

        h1(doc, "二、任务背景与目标");
        projectBackgroundTable(m, doc);

        h1(doc, "三、组织与实施");
        projectOrganizationTable(m, doc);

        h1(doc, "四、成果与评价");
        projectOutcomeTable(m.getAssessments(), m.getScoreRule(), doc);

        h1(doc, "五、实践条件及资源");
        conditionTable(m.getConditions(), doc);
    }

    // ============================ 基本信息表（四套） ============================

    /** 课程基本信息表（type1）：物理 5 列（学时块 + 学分块对齐，无多余横竖线） */
    private void courseBasicInfoTable(CourseTeachingPlanModel m, XWPFDocument doc) {
        // 物理 5 列：学分与学时块自然对齐，避免 6 列再横合 credit 多出一横/一竖
        // R4/R5: | 学时(纵) | 讲授/实践 | 学时值 | 学分(纵) | 学分值(纵) |
        int cols = 5;
        int rows = 9;
        XWPFTable t = createTable(doc, rows, cols);
        // R0 课程名称
        labelValue(t, 0, 0, 1, "课程名称", m.getCourseName(), cols - 1);
        // R1 课程编号
        labelValue(t, 1, 0, 1, "课程编号", m.getCourseCode(), cols - 1);
        // R2 课程英文名称
        labelValue(t, 2, 0, 1, "课程英文名称", m.getCourseEnName(), cols - 1);
        // R3 启用时间
        labelValue(t, 3, 0, 1, "启用时间", m.getEnabledTerm(), cols - 1);
        // R4
        setCell(t, 4, 0, "学时", true);
        setCell(t, 4, 1, "讲授", true);
        setCell(t, 4, 2, formatHours(m.getTeachHours()), false);
        setCell(t, 4, 3, "学分", true);
        setCell(t, 4, 4, formatHours(m.getCredit()), false);
        // R5
        setCell(t, 5, 0, "", false);
        setCell(t, 5, 1, "实践", true);
        setCell(t, 5, 2, formatHours(m.getPracticeHours()), false);
        setCell(t, 5, 3, "", false);
        setCell(t, 5, 4, "", false);
        // 仅纵向合并学时标签、学分标签、学分值
        WordUtil.mergeCellsVertical(t, 0, 4, 5);
        WordUtil.mergeCellsVertical(t, 3, 4, 5);
        WordUtil.mergeCellsVertical(t, 4, 4, 5);
        // R6 适用对象 | 适用专业 | 开课学期 | 课程模块 | 修读性质
        setCell(t, 6, 0, "适用对象", true);
        setCell(t, 6, 1, "适用专业", true);
        setCell(t, 6, 2, "开课学期", true);
        setCell(t, 6, 3, "课程模块", true);
        setCell(t, 6, 4, "修读性质", true);
        // R7 值
        setCell(t, 7, 0, m.getEducationLevel(), false);
        setCell(t, 7, 1, m.getMajorName(), false);
        setCell(t, 7, 2, m.getTerm(), false);
        setCell(t, 7, 3, m.getCourseModule(), false);
        setCell(t, 7, 4, m.getCourseAttr(), false);
        // R8 备注
        setCell(t, 8, 0, "备注：①如果同1门课程适用于不同培训对象，则每个培训对象生成1条数据 / ②如果同1门课程对不对专业修读性质不一样，则每个专业生成1条数据。", false);
        WordUtil.mergeCellsHorizontal(t, 8, 0, cols - 1);
    }

    /** 实验课程基本信息表（type3）：5 列 */
    private void experimentBasicInfoTable(CourseTeachingPlanModel m, XWPFDocument doc) {
        int cols = 5;
        int rows = 8;
        XWPFTable t = createTable(doc, rows, cols);
        labelValue(t, 0, 0, 1, "课程名称", m.getCourseName(), cols - 1);
        labelValue(t, 1, 0, 1, "课程编号", m.getCourseCode(), cols - 1);
        labelValue(t, 2, 0, 1, "课程英文名称", m.getCourseEnName(), cols - 1);
        labelValue(t, 3, 0, 1, "课程教学计划启用时间", m.getEnabledTerm(), cols - 1);
        // R4 学时 | {hours} | 学分 | {credit} | (空)
        setCell(t, 4, 0, "学时", true);
        setCell(t, 4, 1, formatHours(m.getHours()), false);
        setCell(t, 4, 2, "学分", true);
        setCell(t, 4, 3, formatHours(m.getCredit()), false);
        setCell(t, 4, 4, "", false);
        // R5 适用对象 | 适用专业 | 开课学期 | 课程模块 | 修读性质
        setCell(t, 5, 0, "适用对象", true);
        setCell(t, 5, 1, "适用专业", true);
        setCell(t, 5, 2, "开课学期", true);
        setCell(t, 5, 3, "课程模块", true);
        setCell(t, 5, 4, "修读性质", true);
        // R6 值
        setCell(t, 6, 0, m.getEducationLevel(), false);
        setCell(t, 6, 1, m.getMajorName(), false);
        setCell(t, 6, 2, m.getTerm(), false);
        setCell(t, 6, 3, m.getCourseModule(), false);
        setCell(t, 6, 4, m.getCourseAttr(), false);
        // R7 备注
        setCell(t, 7, 0, "备注：①如果同1门课程适用于不同培训对象，则每个培训对象生成1条数据 / ②如果同1门课程对不对专业修读性质不一样，则每个专业生成1条数据。", false);
        WordUtil.mergeCellsHorizontal(t, 7, 0, cols - 1);
    }

    /** 实践训练课目基本信息表（type2）：5 列 */
    private void practiceSubjectBasicInfoTable(CourseTeachingPlanModel m, XWPFDocument doc) {
        int cols = 5;
        int rows = 6;
        XWPFTable t = createTable(doc, rows, cols);
        labelValue(t, 0, 0, 1, "课目名称", m.getCourseName(), cols - 1);
        labelValue(t, 1, 0, 1, "课目编号", m.getCourseCode(), cols - 1);
        labelValue(t, 2, 0, 1, "启用时间", m.getEnabledTerm(), cols - 1);
        // R3 适用对象 | 适用专业 | 时间安排 | 学期安排 | 修读性质
        setCell(t, 3, 0, "适用对象", true);
        setCell(t, 3, 1, "适用专业", true);
        setCell(t, 3, 2, "时间安排", true);
        setCell(t, 3, 3, "学期安排", true);
        setCell(t, 3, 4, "修读性质", true);
        // R4 值
        setCell(t, 4, 0, m.getEducationLevel(), false);
        setCell(t, 4, 1, m.getMajorName(), false);
        setCell(t, 4, 2, m.getHours(), false);
        setCell(t, 4, 3, m.getTerm(), false);
        setCell(t, 4, 4, m.getCourseAttr(), false);
        // R5 备注
        setCell(t, 5, 0, "备注：①如果同1课目适用于不同培训对象，则每个培训对象生成1条数据 / ②如果同1门课目对不对专业修读性质不一样，则每个专业生成1条数据。", false);
        WordUtil.mergeCellsHorizontal(t, 5, 0, cols - 1);
    }

    /** 实践项目基本信息表（type4）：5 列，含支撑课程行 */
    private void practiceProjectBasicInfoTable(CourseTeachingPlanModel m, XWPFDocument doc) {
        int cols = 5;
        int rows = 7;
        XWPFTable t = createTable(doc, rows, cols);
        labelValue(t, 0, 0, 1, "项目名称", m.getCourseName(), cols - 1);
        labelValue(t, 1, 0, 1, "项目编号", m.getCourseCode(), cols - 1);
        labelValue(t, 2, 0, 1, "启用时间", m.getEnabledTerm(), cols - 1);
        setCell(t, 3, 0, "适用对象", true);
        setCell(t, 3, 1, "适用专业", true);
        setCell(t, 3, 2, "时间安排", true);
        setCell(t, 3, 3, "学期安排", true);
        setCell(t, 3, 4, "修读性质", true);
        setCell(t, 4, 0, m.getEducationLevel(), false);
        setCell(t, 4, 1, m.getMajorName(), false);
        setCell(t, 4, 2, m.getHours(), false);
        setCell(t, 4, 3, m.getTerm(), false);
        setCell(t, 4, 4, m.getCourseAttr(), false);
        // R5 支撑课程或实践训练课目
        setCell(t, 5, 0, "支撑课程或实践训练课目", true);
        setCell(t, 5, 1, m.getSupportingCourses(), false);
        WordUtil.mergeCellsHorizontal(t, 5, 1, cols - 1);
        // R6 备注
        setCell(t, 6, 0, "备注：①如果同1门课程适用于不同培训对象，则每个培训对象生成1条数据 / ②如果同1门课程对不对专业修读性质不一样，则每个专业生成1条数据。", false);
        WordUtil.mergeCellsHorizontal(t, 6, 0, cols - 1);
    }

    // ============================ 共用表 ============================

    /** 教员团队表：序号 | 教员姓名 | 职称 | 职责 | 主讲内容 */
    private void teacherTable(List<TeachingPlanTeacher> teachers, XWPFDocument doc) {
        int cols = 5;
        int rows = 1 + size(teachers);
        XWPFTable t = createTable(doc, Math.max(rows, 2), cols);
        setCell(t, 0, 0, "序号", true);
        setCell(t, 0, 1, "教员姓名", true);
        setCell(t, 0, 2, "职称", true);
        setCell(t, 0, 3, "职责", true);
        setCell(t, 0, 4, "主讲内容", true);
        if (ObjectUtils.isNotEmpty(teachers)) {
            for (int i = 0; i < teachers.size(); i++) {
                TeachingPlanTeacher tc = teachers.get(i);
                int r = i + 1;
                setCell(t, r, 0, String.valueOf(i + 1), false);
                setCell(t, r, 1, tc.getTeacherName(), false);
                setCell(t, r, 2, tc.getProfessionalTitle(), false);
                setCell(t, r, 3, tc.getDuty(), false);
                setCell(t, r, 4, tc.getLectureContent(), false);
            }
        }
    }

    /**
     * 课程目标与支撑毕业要求（type1 四）。
     * 源课被多个培养方案引用时，每个培养方案单独一张表（目标类型 | 目标内容 | 支撑毕业要求）；
     * 多方案时每表前加二级标题（培养方案名称）。
     */
    private void objectiveTable(CourseTeachingPlanModel m, XWPFDocument doc) {
        List<CourseTeachingPlanModel.SchemeObjectiveGroup> groups = m.getSchemeObjectiveGroups();
        if (ObjectUtils.isNotEmpty(groups)) {
            boolean multi = groups.size() > 1;
            for (CourseTeachingPlanModel.SchemeObjectiveGroup g : groups) {
                if (g == null) {
                    continue;
                }
                // 多方案时加小标题，便于区分各组对应关系
                if (multi && StringUtils.isNotBlank(g.getSchemeTitle())) {
                    h2(doc, g.getSchemeTitle());
                } else if (multi) {
                    h2(doc, "培养方案" + (g.getSchemeId() == null ? "" : " " + g.getSchemeId()));
                }
                writeObjectiveTable(g.getObjectives(), g.getObjectiveRefMap(), doc);
            }
            return;
        }
        // 兼容：未组装 schemeObjectiveGroups 时走旧单表
        writeObjectiveTable(m.getObjectives(), m.getObjectiveRefMap(), doc);
    }

    /**
     * 写一张「目标类型 | 目标内容 | 支撑毕业要求」表。
     * 按 知识/能力/素质 分组，相同类型连续行合并“目标类型”列。
     */
    private void writeObjectiveTable(List<TeachingPlanObjective> objs,
                                     Map<Long, List<TeachingPlanObjectiveRef>> refMap,
                                     XWPFDocument doc) {
        int cols = 3;
        List<List<TeachingPlanObjective>> typeGroups = new ArrayList<>();
        typeGroups.add(filterObjective(objs, KEY_KNOWLEDGE));
        typeGroups.add(filterObjective(objs, KEY_ABILITY));
        typeGroups.add(filterObjective(objs, KEY_QUALITY));
        int total = typeGroups.stream().mapToInt(List::size).sum();
        int rows = 1 + Math.max(total, 1);
        XWPFTable t = createTable(doc, rows, cols);
        setCell(t, 0, 0, "目标类型", true);
        setCell(t, 0, 1, "目标内容", true);
        setCell(t, 0, 2, "支撑毕业要求", true);
        int r = 1;
        for (List<TeachingPlanObjective> g : typeGroups) {
            if (g.isEmpty()) {
                continue;
            }
            int start = r;
            String typeName = g.get(0).getObjectiveTypeName();
            if (StringUtils.isBlank(typeName)) {
                typeName = guessTypeName(g.get(0).getObjectiveTypeCode());
            }
            for (TeachingPlanObjective o : g) {
                setCell(t, r, 1, o.getContent(), false);
                setCell(t, r, 2, joinRefs(refMap, o.getId()), false);
                r++;
            }
            setCell(t, start, 0, typeName, true);
            if (r - 1 > start) {
                WordUtil.mergeCellsVertical(t, 0, start, r - 1);
            }
        }
        if (total == 0) {
            setCell(t, 1, 0, "", false);
            setCell(t, 1, 1, "", false);
            setCell(t, 1, 2, "", false);
        }
    }

    /** 教学内容与时间安排表：专题 | 内容 | 学时安排 */
    private void contentTable(List<TeachingPlanContent> contents, XWPFDocument doc) {
        int cols = 3;
        int rows = 1 + Math.max(size(contents), 1);
        XWPFTable t = createTable(doc, rows, cols);
        setCell(t, 0, 0, "专题", true);
        setCell(t, 0, 1, "内容", true);
        setCell(t, 0, 2, "学时安排", true);
        if (ObjectUtils.isNotEmpty(contents)) {
            for (int i = 0; i < contents.size(); i++) {
                TeachingPlanContent c = contents.get(i);
                int r = i + 1;
                setCell(t, r, 0, c.getTitle(), false);
                setCell(t, r, 1, c.getContent(), false);
                setCell(t, r, 2, formatHours(c.getHours()), false);
            }
        } else {
            setCell(t, 1, 0, "", false);
            setCell(t, 1, 1, "", false);
            setCell(t, 1, 2, "", false);
        }
    }

    /**
     * 知识目标达成设计表（type1 六(一)）：9 列
     * 序号 | 知识单元 | 知识点 | 支撑的课程知识目标 | 教学内容 | 教学环节 | 教法 | 学法 | 学时
     * <p>
     * 一条达成设计可绑定多个知识点（可跨知识单元）：按知识点展开多行。
     * - 序号 / 支撑目标 / 教学内容 / 教学环节 / 教法 / 学法 / 学时：同一条设计纵向合并
     * - 知识单元：同一条设计内连续相同单元纵向合并（不同单元各自成块）
     * - 知识点：每行一点，不合并
     */
    private void targetDesignTable(List<TeachingPlanTargetDesign> designs, XWPFDocument doc) {
        int cols = 9;
        List<KnowledgeDesignRow> flat = flattenKnowledgeDesignRows(designs);
        int rows = 1 + Math.max(flat.size(), 1);
        XWPFTable t = createTable(doc, rows, cols);
        setCell(t, 0, 0, "序号", true);
        setCell(t, 0, 1, "知识单元", true);
        setCell(t, 0, 2, "知识点", true);
        setCell(t, 0, 3, "支撑的课程知识目标", true);
        setCell(t, 0, 4, "教学内容", true);
        setCell(t, 0, 5, "教学环节", true);
        setCell(t, 0, 6, "教法", true);
        setCell(t, 0, 7, "学法", true);
        setCell(t, 0, 8, "学时", true);
        if (flat.isEmpty()) {
            for (int c = 0; c < cols; c++) {
                setCell(t, 1, c, "", false);
            }
            return;
        }
        for (int i = 0; i < flat.size(); i++) {
            KnowledgeDesignRow row = flat.get(i);
            int r = i + 1;
            // 后面几列不再纵向合并：每行独立写出
            setCell(t, r, 0, String.valueOf(row.designIndex), false);
            setCell(t, r, 3, row.objectiveText, false);
            setCell(t, r, 4, row.contentText, false);
            setCell(t, r, 5, row.teachingLink, false);
            setCell(t, r, 6, row.teachingMethod, false);
            setCell(t, r, 7, row.learningMethod, false);
            setCell(t, r, 8, row.hours, false);
            // 仅相同知识单元列合并：段首写值，其余空
            if (row.firstOfUnit) {
                setCell(t, r, 1, row.unitName, false);
            } else {
                setCell(t, r, 1, "", false);
            }
            setCell(t, r, 2, row.pointName, false);
        }
        // 只合并相同知识单元列
        mergeKnowledgeUnitColumn(t, flat);
    }

    /**
     * 将知识目标达成设计展开为「每知识点一行」。
     * 无 knowledgePoints 时回退单点兼容字段，仍至少 1 行。
     */
    private List<KnowledgeDesignRow> flattenKnowledgeDesignRows(List<TeachingPlanTargetDesign> designs) {
        List<KnowledgeDesignRow> flat = new ArrayList<>();
        if (ObjectUtils.isEmpty(designs)) {
            return flat;
        }
        int designIndex = 0;
        for (TeachingPlanTargetDesign d : designs) {
            if (d == null) {
                continue;
            }
            designIndex++;
            List<TeachingPlanTargetDesign.KnowledgePointItem> points = resolveKnowledgePointItems(d);
            String objectiveText = firstNonBlank(d.getObjectiveText(), d.getContentText());
            String contentText = str(d.getContentText());
            String teachingLink = str(d.getTeachingLink());
            String teachingMethod = str(d.getTeachingMethod());
            String learningMethod = str(d.getLearningMethod());
            String hours = formatHours(d.getHours());
            for (int p = 0; p < points.size(); p++) {
                TeachingPlanTargetDesign.KnowledgePointItem item = points.get(p);
                KnowledgeDesignRow row = new KnowledgeDesignRow();
                row.designIndex = designIndex;
                row.unitName = item == null ? "" : str(item.getKnowledgeUnitName());
                row.pointName = item == null ? "" : str(item.getKnowledgePointName());
                row.objectiveText = objectiveText;
                row.contentText = contentText;
                row.teachingLink = teachingLink;
                row.teachingMethod = teachingMethod;
                row.learningMethod = learningMethod;
                row.hours = hours;
                row.firstOfDesign = (p == 0);
                // 单元段首：全文连续相同单元只在段首写值
                flat.add(row);
            }
        }
        // 先按知识单元排序，再标记连续相同单元的段首，便于合并
        flat.sort((a, b) -> {
            String ua = StringUtils.defaultString(a.unitName);
            String ub = StringUtils.defaultString(b.unitName);
            int c = ua.compareTo(ub);
            if (c != 0) {
                return c;
            }
            // 同单元内保持设计序号、知识点相对顺序
            int d = Integer.compare(a.designIndex, b.designIndex);
            if (d != 0) {
                return d;
            }
            return StringUtils.defaultString(a.pointName).compareTo(StringUtils.defaultString(b.pointName));
        });
        for (int i = 0; i < flat.size(); i++) {
            KnowledgeDesignRow row = flat.get(i);
            if (i == 0) {
                row.firstOfUnit = true;
            } else {
                String prevUnit = flat.get(i - 1).unitName;
                row.firstOfUnit = !StringUtils.equals(
                        StringUtils.defaultString(prevUnit),
                        StringUtils.defaultString(row.unitName));
            }
        }
        return flat;
    }

    /**
     * 解析一条设计下的知识点列表；无多点 JSON 时用单点兼容列。
     */
    private List<TeachingPlanTargetDesign.KnowledgePointItem> resolveKnowledgePointItems(
            TeachingPlanTargetDesign d) {
        if (d != null && ObjectUtils.isNotEmpty(d.getKnowledgePoints())) {
            return d.getKnowledgePoints();
        }
        List<TeachingPlanTargetDesign.KnowledgePointItem> one = new ArrayList<>();
        TeachingPlanTargetDesign.KnowledgePointItem item = new TeachingPlanTargetDesign.KnowledgePointItem();
        if (d != null) {
            item.setKnowledgeUnitId(d.getKnowledgeUnitId());
            item.setKnowledgeUnitName(d.getKnowledgeUnitName());
            item.setKnowledgePointId(d.getKnowledgePointId());
            item.setKnowledgePointName(d.getKnowledgePointName());
        }
        one.add(item);
        return one;
    }

    /** 连续相同知识单元纵向合并（仅知识单元列；其它列不合并） */
    private void mergeKnowledgeUnitColumn(XWPFTable t, List<KnowledgeDesignRow> flat) {
        int i = 0;
        while (i < flat.size()) {
            int start = i;
            String unitName = StringUtils.defaultString(flat.get(i).unitName);
            i++;
            while (i < flat.size()
                    && StringUtils.equals(unitName, StringUtils.defaultString(flat.get(i).unitName))) {
                i++;
            }
            int end = i - 1;
            if (end > start) {
                WordUtil.mergeCellsVertical(t, 1, start + 1, end + 1);
            }
        }
    }

    /** 知识目标表展开行（每知识点一行） */
    private static class KnowledgeDesignRow {
        int designIndex;
        String unitName;
        String pointName;
        String objectiveText;
        String contentText;
        String teachingLink;
        String teachingMethod;
        String learningMethod;
        String hours;
        boolean firstOfDesign;
        boolean firstOfUnit;
    }

    /**
     * 能力/素质目标达成设计表（type1 六(二)(三)）：5 列
     * 序号 | 课程能力(素质)目标 | 观测点 | 教学内容 | 教学设计
     */
    private void abilityQualityDesignTable(List<TeachingPlanTargetDesign> designs, XWPFDocument doc, String targetColName) {
        int cols = 5;
        int rows = 1 + Math.max(size(designs), 1);
        XWPFTable t = createTable(doc, rows, cols);
        setCell(t, 0, 0, "序号", true);
        setCell(t, 0, 1, targetColName, true);
        setCell(t, 0, 2, "观测点", true);
        setCell(t, 0, 3, "教学内容", true);
        setCell(t, 0, 4, "教学设计", true);
        if (ObjectUtils.isNotEmpty(designs)) {
            for (int i = 0; i < designs.size(); i++) {
                TeachingPlanTargetDesign d = designs.get(i);
                int r = i + 1;
                setCell(t, r, 0, String.valueOf(i + 1), false);
                // 课程能力/素质目标列：优先 objectiveText
                setCell(t, r, 1, firstNonBlank(d.getObjectiveText(), d.getContentText()), false);
                setCell(t, r, 2, d.getObservationPoint(), false);
                setCell(t, r, 3, d.getContentText(), false);
                setCell(t, r, 4, d.getTeachingDesign(), false);
            }
        } else {
            for (int c = 0; c < cols; c++) {
                setCell(t, 1, c, "", false);
            }
        }
    }

    /**
     * 实验/实践环节教学设计表（type1 七、type3 四）：
     * 序号 | 项目名称 | 主要内容与教学设计(类型标签 | 内容)
     * 表头“主要内容与教学设计”横合两列；每个明细占一行；序号/项目名称纵向合并。
     */
    private void practiceItemTable(List<TeachingPlanPracticeItem> items, Map<Long, List<TeachingPlanPracticeItemDetail>> detailMap, XWPFDocument doc) {
        int cols = 4;
        // 序号列缩窄，类型标签/内容两列叉开展示
        int[] colWidths = new int[]{600, 1600, 1800, 3000};
        List<PracticeDetailRow> flat = flattenPracticeDetailRows(items, detailMap);
        int rows = 1 + Math.max(flat.size(), 1);
        XWPFTable t = createTable(doc, rows, cols, colWidths);
        setCell(t, 0, 0, "序号", true);
        setCell(t, 0, 1, "项目名称", true);
        setCell(t, 0, 2, "主要内容与教学设计", true);
        setCell(t, 0, 3, "", true);
        WordUtil.mergeCellsHorizontal(t, 0, 2, 3);
        if (flat.isEmpty()) {
            for (int c = 0; c < cols; c++) {
                setCell(t, 1, c, "", false);
            }
            return;
        }
        for (int i = 0; i < flat.size(); i++) {
            PracticeDetailRow row = flat.get(i);
            int r = i + 1;
            if (row.firstOfItem) {
                setCell(t, r, 0, String.valueOf(row.index), false);
                setCell(t, r, 1, row.name, false);
            } else {
                setCell(t, r, 0, "", false);
                setCell(t, r, 1, "", false);
            }
            setCell(t, r, 2, row.typeLabel, false);
            setCell(t, r, 3, row.content, false);
        }
        // 序号 / 项目名称 纵向合并
        int i = 0;
        while (i < flat.size()) {
            int start = i;
            int itemIndex = flat.get(i).index;
            i++;
            while (i < flat.size() && flat.get(i).index == itemIndex) {
                i++;
            }
            int end = i - 1;
            if (end > start) {
                WordUtil.mergeCellsVertical(t, 0, start + 1, end + 1);
                WordUtil.mergeCellsVertical(t, 1, start + 1, end + 1);
            }
        }
    }

    /** 将实验/实践项目展开为明细行（每 detailType 一行） */
    private List<PracticeDetailRow> flattenPracticeDetailRows(List<TeachingPlanPracticeItem> items,
                                                              Map<Long, List<TeachingPlanPracticeItemDetail>> detailMap) {
        List<PracticeDetailRow> flat = new ArrayList<>();
        if (ObjectUtils.isEmpty(items)) {
            return flat;
        }
        for (int i = 0; i < items.size(); i++) {
            TeachingPlanPracticeItem it = items.get(i);
            if (it == null) {
                continue;
            }
            List<TeachingPlanPracticeItemDetail> details = detailMap == null ? null : detailMap.get(it.getId());
            List<TeachingPlanPracticeItemDetail> ordered = orderPracticeDetails(it.getItemType(), details);
            if (ordered.isEmpty()) {
                PracticeDetailRow row = new PracticeDetailRow();
                row.index = i + 1;
                row.name = str(it.getName());
                row.typeLabel = "";
                row.content = "";
                row.firstOfItem = true;
                flat.add(row);
                continue;
            }
            for (int d = 0; d < ordered.size(); d++) {
                TeachingPlanPracticeItemDetail detail = ordered.get(d);
                PracticeDetailRow row = new PracticeDetailRow();
                row.index = i + 1;
                row.name = str(it.getName());
                String type = detail == null ? "" : str(detail.getDetailType());
                row.typeLabel = DETAIL_LABEL.getOrDefault(type, type);
                row.content = detail == null ? "" : str(detail.getContent());
                row.firstOfItem = (d == 0);
                flat.add(row);
            }
        }
        return flat;
    }

    /** 按项目类型给明细排序，并补齐样图中的固定行（无内容也占行） */
    private List<TeachingPlanPracticeItemDetail> orderPracticeDetails(Integer itemType,
                                                                      List<TeachingPlanPracticeItemDetail> details) {
        Map<String, TeachingPlanPracticeItemDetail> byType = new LinkedHashMap<>();
        if (ObjectUtils.isNotEmpty(details)) {
            for (TeachingPlanPracticeItemDetail d : details) {
                if (d == null || StringUtils.isBlank(d.getDetailType())) {
                    continue;
                }
                // 同类型保留首次
                byType.putIfAbsent(d.getDetailType().trim(), d);
            }
        }
        boolean practiceLike = itemType != null && itemType == 2;
        List<String> order = practiceLike ? PRACTICE_DETAIL_ORDER : EXPERIMENT_DETAIL_ORDER;
        // 实践项目：无「拟解决的复杂问题」行；实验仍固定 4 行
        List<String> required = practiceLike
                ? Arrays.asList("main_task", "overall_design", "outcome_requirement")
                : Arrays.asList("purpose_task", "content_requirement", "result_requirement", "teaching_design");
        List<TeachingPlanPracticeItemDetail> ordered = new ArrayList<>();
        Set<String> used = new LinkedHashSet<>();
        for (String type : required) {
            TeachingPlanPracticeItemDetail d = byType.get(type);
            if (d == null) {
                d = new TeachingPlanPracticeItemDetail();
                d.setDetailType(type);
                d.setContent("");
            }
            ordered.add(d);
            used.add(type);
        }
        // 其余类型按优先序追加（实践项目跳过 complex_problem）
        for (String type : order) {
            if (used.contains(type)) {
                continue;
            }
            if (practiceLike && "complex_problem".equals(type)) {
                continue;
            }
            TeachingPlanPracticeItemDetail d = byType.get(type);
            if (d != null) {
                ordered.add(d);
                used.add(type);
            }
        }
        for (Map.Entry<String, TeachingPlanPracticeItemDetail> e : byType.entrySet()) {
            if (practiceLike && "complex_problem".equals(e.getKey())) {
                continue;
            }
            if (!used.contains(e.getKey())) {
                ordered.add(e.getValue());
            }
        }
        return ordered;
    }

    private static class PracticeDetailRow {
        int index;
        String name;
        String typeLabel;
        String content;
        boolean firstOfItem;
    }

    /**
     * 实验实施安排表（type3 五）：序号 | 实验项目名称 | 学时 | 分组情况 | 实验性质 | 修读性质 | 备注
     */
    private void experimentArrangementTable(List<TeachingPlanPracticeItem> items, XWPFDocument doc) {
        int cols = 7;
        int rows = 1 + Math.max(size(items), 1);
        XWPFTable t = createTable(doc, rows, cols);
        String[] headers = {"序号", "实验项目名称", "学时", "分组情况", "实验性质", "修读性质", "备注"};
        for (int c = 0; c < cols; c++) {
            setCell(t, 0, c, headers[c], true);
        }
        if (ObjectUtils.isNotEmpty(items)) {
            for (int i = 0; i < items.size(); i++) {
                TeachingPlanPracticeItem it = items.get(i);
                int r = i + 1;
                setCell(t, r, 0, String.valueOf(i + 1), false);
                setCell(t, r, 1, it.getName(), false);
                setCell(t, r, 2, formatHours(it.getHours()), false);
                setCell(t, r, 3, it.getGroupInfo(), false);
                setCell(t, r, 4, it.getExperimentNature(), false);
                setCell(t, r, 5, it.getStudyNature(), false);
                setCell(t, r, 6, "", false);
            }
        } else {
            for (int c = 0; c < cols; c++) {
                setCell(t, 1, c, "", false);
            }
        }
    }

    /**
     * 考核评价表（type1 八、type3 六、type2 六）：
     * 物理 6 列：类别 | 项目 | 考核方式 | 评定机制 | 权重 | 评价标准
     * 表头“考核项目”横合前两列；同类别第一列纵向合并；底部计分规则行。
     */
    private void assessmentTable(List<TeachingPlanAssessment> assessments, String scoreRule, XWPFDocument doc) {
        int cols = 6;
        List<TeachingPlanAssessment> summative = new ArrayList<>();
        List<TeachingPlanAssessment> formative = new ArrayList<>();
        List<TeachingPlanAssessment> others = new ArrayList<>();
        if (ObjectUtils.isNotEmpty(assessments)) {
            for (TeachingPlanAssessment a : assessments) {
                if (a == null) {
                    continue;
                }
                Integer cat = a.getAssessmentCategory();
                if (cat != null && cat == ASSESS_CAT_SUMMATIVE) {
                    summative.add(a);
                } else if (cat != null && cat == ASSESS_CAT_FORMATIVE) {
                    formative.add(a);
                } else {
                    others.add(a);
                }
            }
        }
        int dataRows = summative.size() + formative.size() + others.size();
        if (dataRows == 0) {
            dataRows = 1;
        }
        int rows = 1 + dataRows + 1;
        XWPFTable t = createTable(doc, rows, cols);
        // 表头：考核项目(合0-1) | 考核方式 | 评定机制 | 权重 | 评价标准
        setCell(t, 0, 0, "考核项目", true);
        setCell(t, 0, 1, "", true);
        setCell(t, 0, 2, "考核方式", true);
        setCell(t, 0, 3, "评定机制", true);
        setCell(t, 0, 4, "权重", true);
        setCell(t, 0, 5, "评价标准", true);
        WordUtil.mergeCellsHorizontal(t, 0, 0, 1);

        int r = 1;
        boolean wrote = false;
        if (!summative.isEmpty()) {
            writeAssessmentCategoryRows(t, r, "总结性考核", summative);
            r += summative.size();
            wrote = true;
        }
        if (!formative.isEmpty()) {
            writeAssessmentCategoryRows(t, r, "形成性考核", formative);
            r += formative.size();
            wrote = true;
        }
        if (!others.isEmpty()) {
            for (TeachingPlanAssessment a : others) {
                writeAssessmentDataRow(t, r, "", a);
                r++;
                wrote = true;
            }
        }
        if (!wrote) {
            for (int c = 0; c < cols; c++) {
                setCell(t, r, c, "", false);
            }
            r++;
        }
        // 计分规则：标签合 0-1，内容合 2..5（先按物理列写文本，再合并）
        setCell(t, r, 0, "计分规则", true);
        setCell(t, r, 1, "", false);
        setCell(t, r, 2, StringUtils.isBlank(scoreRule) ? "" : scoreRule, false);
        setCell(t, r, 3, "", false);
        setCell(t, r, 4, "", false);
        setCell(t, r, 5, "", false);
        WordUtil.mergeCellsHorizontal(t, r, 0, 1);
        // 横合 0-1 后索引塌缩：原 col2 变为 col1
        WordUtil.mergeCellsHorizontal(t, r, 1, 4);
    }

    /** 写入同一考核类别的多行，并纵向合并类别列 */
    private void writeAssessmentCategoryRows(XWPFTable t, int startRow, String categoryName,
                                             List<TeachingPlanAssessment> list) {
        if (ObjectUtils.isEmpty(list)) {
            return;
        }
        for (int i = 0; i < list.size(); i++) {
            int r = startRow + i;
            String cat = (i == 0) ? categoryName : "";
            writeAssessmentDataRow(t, r, cat, list.get(i));
        }
        if (list.size() > 1) {
            WordUtil.mergeCellsVertical(t, 0, startRow, startRow + list.size() - 1);
        }
    }

    private void writeAssessmentDataRow(XWPFTable t, int r, String categoryName, TeachingPlanAssessment a) {
        if (a == null) {
            for (int c = 0; c < 6; c++) {
                setCell(t, r, c, "", false);
            }
            return;
        }
        setCell(t, r, 0, categoryName, true);
        setCell(t, r, 1, a.getAssessmentItem(), false);
        setCell(t, r, 2, a.getMethod(), false);
        // 评定机制：优先 mechanism，其次 scoreSystem（百分制/五级制等）
        setCell(t, r, 3, firstNonBlank(a.getMechanism(), a.getScoreSystem()), false);
        setCell(t, r, 4, formatHours(a.getWeight()), false);
        setCell(t, r, 5, a.getStandard(), false);
    }

    /** 教材表：教材性质 | 教材名称 | 第一作者 | 版次 | 出版（颁发）单位 | 出版（颁发）时间 | ISBN号（统一书号） | 出版方式 */
    private void textbookTable(List<TeachingPlanTextbook> books, XWPFDocument doc) {
        int cols = 8;
        int rows = 1 + Math.max(size(books), 1);
        XWPFTable t = createTable(doc, rows, cols);
        String[] headers = {"教材性质", "教材名称", "第一作者", "版次", "出版（颁发）单位", "出版（颁发）时间", "ISBN号（统一书号）", "出版方式"};
        for (int c = 0; c < cols; c++) {
            setCell(t, 0, c, headers[c], true);
        }
        if (ObjectUtils.isNotEmpty(books)) {
            for (int i = 0; i < books.size(); i++) {
                TeachingPlanTextbook b = books.get(i);
                int r = i + 1;
                setCell(t, r, 0, b.getMaterialNature(), false);
                setCell(t, r, 1, b.getName(), false);
                setCell(t, r, 2, b.getFirstAuthor(), false);
                setCell(t, r, 3, b.getEdition(), false);
                setCell(t, r, 4, b.getPublisher(), false);
                setCell(t, r, 5, b.getPublishTime(), false);
                setCell(t, r, 6, b.getIsbn(), false);
                setCell(t, r, 7, b.getPublishMethod(), false);
            }
        } else {
            for (int c = 0; c < cols; c++) {
                setCell(t, 1, c, "", false);
            }
        }
    }

    /** 条件保障表：条件类型 | 有关要求 */
    private void conditionTable(List<TeachingPlanCondition> conditions, XWPFDocument doc) {
        int cols = 2;
        int rows = 1 + Math.max(size(conditions), 1);
        XWPFTable t = createTable(doc, rows, cols);
        setCell(t, 0, 0, "条件类型", true);
        setCell(t, 0, 1, "有关要求", true);
        if (ObjectUtils.isNotEmpty(conditions)) {
            for (int i = 0; i < conditions.size(); i++) {
                TeachingPlanCondition c = conditions.get(i);
                int r = i + 1;
                setCell(t, r, 0, c.getConditionType(), false);
                setCell(t, r, 1, c.getRequirement(), false);
            }
        } else {
            setCell(t, 1, 0, "", false);
            setCell(t, 1, 1, "", false);
        }
    }

    // ============================ 实践训练课目/项目专属表 ============================

    /** 训练目的与支撑毕业要求表（type2 二）：训练目的 | 支撑毕业要求 */
    private void trainingPurposeTable(CourseTeachingPlanModel m, XWPFDocument doc) {
        int cols = 2;
        List<StandardGraduation> grads = m.getCourseGraduations();
        int rows = 1 + Math.max(size(grads), 1);
        XWPFTable t = createTable(doc, rows, cols);
        setCell(t, 0, 0, "训练目的", true);
        setCell(t, 0, 1, "支撑毕业要求", true);
        if (ObjectUtils.isNotEmpty(grads)) {
            for (int i = 0; i < grads.size(); i++) {
                StandardGraduation g = grads.get(i);
                int r = i + 1;
                setCell(t, r, 0, "", false);
                setCell(t, r, 1, g.getName(), false);
            }
        } else {
            setCell(t, 1, 0, "", false);
            setCell(t, 1, 1, "", false);
        }
    }

    /** 训练任务与总体设计表（type2 三）：标签 | 内容（训练任务/总体设计/配套支撑课程） */
    private void trainingTaskTable(CourseTeachingPlanModel m, XWPFDocument doc) {
        int cols = 2;
        int rows = 3;
        XWPFTable t = createTable(doc, rows, cols);
        Map<String, String> sec = sectionsMap(m.getSections());
        setCell(t, 0, 0, "训练任务", true);
        setCell(t, 0, 1, sec.getOrDefault("训练任务", sec.getOrDefault("task", "")), false);
        setCell(t, 1, 0, "总体设计", true);
        setCell(t, 1, 1, sec.getOrDefault("总体设计", sec.getOrDefault("overall_design", "")), false);
        setCell(t, 2, 0, "配套支撑课程", true);
        setCell(t, 2, 1, m.getSupportingCourses(), false);
    }

    /** 训练内容与时间安排表（type2 四）：模块 | 内容 | 目的 | 时间安排 */
    private void trainingContentTable(List<TeachingPlanContent> contents, XWPFDocument doc) {
        int cols = 4;
        int rows = 1 + Math.max(size(contents), 1);
        XWPFTable t = createTable(doc, rows, cols);
        setCell(t, 0, 0, "模块", true);
        setCell(t, 0, 1, "内容", true);
        setCell(t, 0, 2, "目的", true);
        setCell(t, 0, 3, "时间安排", true);
        if (ObjectUtils.isNotEmpty(contents)) {
            for (int i = 0; i < contents.size(); i++) {
                TeachingPlanContent c = contents.get(i);
                int r = i + 1;
                setCell(t, r, 0, c.getTitle(), false);
                setCell(t, r, 1, c.getContent(), false);
                setCell(t, r, 2, c.getPurpose(), false);
                setCell(t, r, 3, c.getTimeArrange(), false);
            }
        } else {
            for (int cc = 0; cc < cols; cc++) {
                setCell(t, 1, cc, "", false);
            }
        }
    }

    /** 组织实施表（type2 五）：组织方式(整行) + 实施步骤表头 + 步骤行。简化为 3 列：实施步骤 | 阶段划分 | 有关要求 */
    private void organizationTable(CourseTeachingPlanModel m, XWPFDocument doc) {
        int cols = 3;
        List<TeachingPlanSection> sections = m.getSections();
        int rows = 2 + size(sections);
        XWPFTable t = createTable(doc, Math.max(rows, 3), cols);
        setCell(t, 0, 0, "组织方式", true);
        WordUtil.mergeCellsHorizontal(t, 0, 0, cols - 1);
        setCell(t, 1, 0, "实施步骤", true);
        setCell(t, 1, 1, "阶段划分", true);
        setCell(t, 1, 2, "有关要求", true);
        if (ObjectUtils.isNotEmpty(sections)) {
            for (int i = 0; i < sections.size(); i++) {
                TeachingPlanSection s = sections.get(i);
                int r = i + 2;
                setCell(t, r, 0, s.getSectionTitle(), false);
                setCell(t, r, 1, "", false);
                setCell(t, r, 2, stripHtml(s.getContent()), false);
            }
        }
    }

    /** 实验课程任务背景表（type3 三）：cols 列（任务背景描述 | 技术目标 | 能力目标 | 支撑毕业要求） */
    private void taskBackgroundTable(CourseTeachingPlanModel m, XWPFDocument doc, int cols) {
        int rows = 2;
        XWPFTable t = createTable(doc, rows, cols);
        setCell(t, 0, 0, "任务背景描述", true);
        setCell(t, 0, 1, "技术目标", true);
        setCell(t, 0, 2, "能力目标", true);
        setCell(t, 0, 3, "支撑的毕业要求", true);
        Map<String, String> sec = sectionsMap(m.getSections());
        setCell(t, 1, 0, sec.getOrDefault("任务背景", sec.getOrDefault("task_background", "")), false);
        setCell(t, 1, 1, sec.getOrDefault("技术目标", ""), false);
        setCell(t, 1, 2, sec.getOrDefault("能力目标", ""), false);
        setCell(t, 1, 3, joinGraduations(m.getCourseGraduations()), false);
    }

    /** 实践项目任务背景表（type4 二）：标签 | 内容 */
    private void projectBackgroundTable(CourseTeachingPlanModel m, XWPFDocument doc) {
        int cols = 2;
        int rows = 4;
        XWPFTable t = createTable(doc, rows, cols);
        Map<String, String> sec = sectionsMap(m.getSections());
        setCell(t, 0, 0, "拟解决的复杂问题", true);
        setCell(t, 0, 1, sec.getOrDefault("拟解决的复杂问题", sec.getOrDefault("complex_problem", "")), false);
        setCell(t, 1, 0, "主要任务", true);
        setCell(t, 1, 1, sec.getOrDefault("主要任务", sec.getOrDefault("main_task", "")), false);
        setCell(t, 2, 0, "支撑的课程目标", true);
        setCell(t, 2, 1, "从支撑课程或实践训练课目设定的课程目标中选择", false);
        setCell(t, 3, 0, "涉及的知识体系", true);
        setCell(t, 3, 1, joinGraduations(m.getCourseGraduations()), false);
    }

    /** 实践项目组织与实施表（type4 三）：团队组织与管理 | 团队规模 + 项目步骤 | 项目步骤 | 有关要求 */
    private void projectOrganizationTable(CourseTeachingPlanModel m, XWPFDocument doc) {
        int cols = 3;
        List<TeachingPlanSection> sections = m.getSections();
        int rows = 2 + size(sections);
        XWPFTable t = createTable(doc, Math.max(rows, 3), cols);
        setCell(t, 0, 0, "团队组织与管理", true);
        setCell(t, 0, 1, "团队规模", true);
        setCell(t, 0, 2, "可根据学员数量灵活设置", false);
        setCell(t, 1, 0, "项目实施", true);
        setCell(t, 1, 1, "项目步骤", true);
        setCell(t, 1, 2, "有关要求", true);
        if (ObjectUtils.isNotEmpty(sections)) {
            for (int i = 0; i < sections.size(); i++) {
                TeachingPlanSection s = sections.get(i);
                int r = i + 2;
                setCell(t, r, 0, "", false);
                setCell(t, r, 1, s.getSectionTitle(), false);
                setCell(t, r, 2, stripHtml(s.getContent()), false);
            }
        }
    }

    /** 实践项目成果与评价表（type4 四）：成果类型 | 成果形式 | 评价的知识和能力 | 权重 | 评价准则 + 项目计分规则行 */
    private void projectOutcomeTable(List<TeachingPlanAssessment> assessments, String scoreRule, XWPFDocument doc) {
        int cols = 5;
        int rows = 1 + Math.max(size(assessments), 2) + 1;
        XWPFTable t = createTable(doc, rows, cols);
        setCell(t, 0, 0, "成果类型", true);
        setCell(t, 0, 1, "成果形式", true);
        setCell(t, 0, 2, "评价的知识和能力", true);
        setCell(t, 0, 3, "权重", true);
        setCell(t, 0, 4, "评价准则", true);
        int r = 1;
        if (ObjectUtils.isNotEmpty(assessments)) {
            for (TeachingPlanAssessment a : assessments) {
                setCell(t, r, 0, a.getAssessmentItem(), false);
                setCell(t, r, 1, a.getMethod(), false);
                setCell(t, r, 2, a.getAssessedContent(), false);
                setCell(t, r, 3, toStr(a.getWeight()), false);
                setCell(t, r, 4, a.getStandard(), false);
                r++;
            }
        } else {
            setCell(t, r, 0, "个人成果", false);
            setCell(t, r, 1, "", false);
            setCell(t, r, 2, "", false);
            setCell(t, r, 3, "", false);
            setCell(t, r, 4, "", false);
            r++;
            setCell(t, r, 0, "团队成果", false);
            setCell(t, r, 1, "", false);
            setCell(t, r, 2, "", false);
            setCell(t, r, 3, "", false);
            setCell(t, r, 4, "", false);
            r++;
        }
        setCell(t, r, 0, "项目计分规则", true);
        setCell(t, r, 1, StringUtils.isBlank(scoreRule) ? "示例：项目成绩为个人成果40%+团队成果60%加权求和。" : scoreRule, false);
        WordUtil.mergeCellsHorizontal(t, r, 1, cols - 1);
    }

    // ============================ 渲染小工具 ============================

    private void h1(XWPFDocument doc, String text) {
        WordUtil.createHeading(doc, text, 1, FONT_HEADING, FONT_SIZE_WUHAO);
    }

    private void h2(XWPFDocument doc, String text) {
        WordUtil.createHeading(doc, text, 2, FONT_HEADING, FONT_SIZE_WUHAO);
    }

    private void h3(XWPFDocument doc, String text) {
        WordUtil.createHeading(doc, text, 3, FONT_HEADING, FONT_SIZE_WUHAO);
    }

    private void bodyParagraph(XWPFDocument doc, String text) {
        WordUtil.createParagraph(doc, text, null, FONT_BODY, FONT_SIZE_WUHAO);
    }

    /**
     * 渲染文本章节：每段 h3 标题 + 内容段落（\n 分行）；富文本 HTML 标签剥离为纯文本。
     * 同名/同编码/规范化后相同的标题仅保留第一次，去掉重复的「课程概念」等小节。
     */
    private void renderSections(XWPFDocument doc, List<TeachingPlanSection> sections) {
        if (ObjectUtils.isEmpty(sections)) {
            return;
        }
        Set<String> seenKeys = new LinkedHashSet<>();
        for (TeachingPlanSection s : sections) {
            if (s == null) {
                continue;
            }
            String title = StringUtils.trimToEmpty(s.getSectionTitle());
            String code = StringUtils.trimToEmpty(s.getSectionCode());
            String key = normalizeSectionKey(title, code);
            if (StringUtils.isNotBlank(key)) {
                if (seenKeys.contains(key)) {
                    // 重复标题/编码整段跳过（含其内容）
                    continue;
                }
                seenKeys.add(key);
            }
            // 大标题已是「三、课程概述」时，不再输出同名小标题
            if (StringUtils.isNotBlank(title) && !isCourseOverviewTitle(title)) {
                h3(doc, title);
            }
            if (StringUtils.isNotBlank(s.getContent())) {
                String plain = stripHtml(s.getContent());
                String[] lines = plain.split("\n", -1);
                for (String line : lines) {
                    bodyParagraph(doc, line);
                }
            }
        }
    }

    /**
     * 章节去重 key：优先规范化标题，其次编码。
     * 去掉空白/全角空格，统一小写；标题含「课程概念」时归并为同一 key。
     */
    /** 是否与大标题「三、课程概述」重复的小标题 */
    private static boolean isCourseOverviewTitle(String title) {
        String t = StringUtils.trimToEmpty(title)
                .replace(" ", "")
                .replace("　", "")
                .replace("	", "");
        // 去掉常见序号前缀：三、 / 3. / （三）
        t = t.replaceAll("^[0-9一二三四五六七八九十]+[、.．]\\s*", "");
        t = t.replaceAll("^[（(][0-9一二三四五六七八九十]+[)）]", "");
        return "课程概述".equals(t) || "courseoverview".equalsIgnoreCase(t);
    }

    private static String normalizeSectionKey(String title, String code) {
        String t = StringUtils.trimToEmpty(title)
                .replace(" ", "")
                .replace("\u3000", "")
                .replace("\t", "")
                .toLowerCase();
        if (StringUtils.isNotBlank(t)) {
            // 仅归并「课程概念」类标题，避免英文 concept 误伤其它章节
            if (t.contains("课程概念") || t.contains("courseconcept") || t.contains("course_concept")
                    || t.equals("concept")) {
                return "section:课程概念";
            }
            return "title:" + t;
        }
        String c = StringUtils.trimToEmpty(code)
                .replace(" ", "")
                .replace("\u3000", "")
                .toLowerCase();
        if (StringUtils.isNotBlank(c)) {
            if (c.contains("课程概念") || c.contains("courseconcept") || c.contains("course_concept")
                    || c.equals("concept")) {
                return "section:课程概念";
            }
            return "code:" + c;
        }
        return "";
    }

    private XWPFTable createTable(XWPFDocument doc, int rows, int cols) {
        return createTable(doc, rows, cols, null);
    }

    private XWPFTable createTable(XWPFDocument doc, int rows, int cols, int[] colWidthsDxa) {
        XWPFTable table = doc.createTable(rows, cols);
        if (colWidthsDxa != null && colWidthsDxa.length == cols) {
            WordUtil.initTableGrid(table, colWidthsDxa);
        } else {
            WordUtil.initTableGrid(table, cols, COL_W);
        }
        table.setWidthType(TableWidthType.PCT);
        table.setWidth("100%");
        return table;
    }

    private void setCell(XWPFTable t, int row, int col, String text, boolean bold) {
        XWPFTableRow r = t.getRow(row);
        if (r == null) {
            r = t.createRow();
        }
        XWPFTableCell cell = r.getCell(col);
        // 表头黑体五号，表内容宋体五号；宽度交给 initTableGrid，避免实践表序号列被等宽冲掉
        String font = bold ? FONT_TABLE_HEADER : FONT_TABLE_BODY;
        WordUtil.setCellText(cell, str(text), bold, null, font, FONT_SIZE_WUHAO);
    }

    /** 标签列(labelCol) + 值(从 valueCol 起合并到 cols-1) */
    private void labelValue(XWPFTable t, int row, int labelCol, int valueCol, String label, String value, int lastCol) {
        setCell(t, row, labelCol, label, true);
        setCell(t, row, valueCol, value, false);
        if (lastCol > valueCol) {
            WordUtil.mergeCellsHorizontal(t, row, valueCol, lastCol);
        }
    }

    private String w(int span) {
        return String.valueOf((long) span * COL_W);
    }

    private static String str(String s) {
        return s == null ? "" : s;
    }

    /**
     * 学时/学分/权重等数值展示：整数不带小数；有小数则去掉尾随 0。
     * 兼容 BigDecimal / Number / 数字字符串。
     */
    private static String formatHours(Object value) {
        if (value == null) {
            return "";
        }
        if (value instanceof BigDecimal) {
            return ((BigDecimal) value).stripTrailingZeros().toPlainString();
        }
        if (value instanceof Number) {
            return BigDecimal.valueOf(((Number) value).doubleValue()).stripTrailingZeros().toPlainString();
        }
        String raw = StringUtils.trimToEmpty(String.valueOf(value));
        if (raw.isEmpty()) {
            return "";
        }
        try {
            return new BigDecimal(raw).stripTrailingZeros().toPlainString();
        } catch (NumberFormatException ignore) {
            return raw;
        }
    }

    private static String toStr(Object o) {
        return o == null ? "" : o.toString();
    }

    private static int size(List<?> list) {
        return list == null ? 0 : list.size();
    }

    private static String joinStr(String... parts) {
        return Arrays.stream(parts).filter(StringUtils::isNotBlank).collect(Collectors.joining(" / "));
    }

    /** 拼接目标绑定的毕业要求名称 */
    private String joinRefs(Map<Long, List<TeachingPlanObjectiveRef>> refMap, Long objectiveId) {
        if (refMap == null || objectiveId == null) {
            return "";
        }
        List<TeachingPlanObjectiveRef> refs = refMap.get(objectiveId);
        if (ObjectUtils.isEmpty(refs)) {
            return "";
        }
        return refs.stream().map(TeachingPlanObjectiveRef::getGraduationName).filter(StringUtils::isNotBlank).collect(Collectors.joining("、"));
    }

    private static String firstNonBlank(String a, String b) {
        if (StringUtils.isNotBlank(a)) {
            return a;
        }
        return b == null ? "" : b;
    }

    /** 拼接课程毕业要求名称（兼容树结构，递归 children） */
    private String joinGraduations(List<StandardGraduation> grads) {
        if (ObjectUtils.isEmpty(grads)) {
            return "";
        }
        List<String> names = new ArrayList<>();
        collectGraduationNames(grads, names);
        return names.stream().filter(StringUtils::isNotBlank).distinct().collect(Collectors.joining("、"));
    }

    private void collectGraduationNames(List<StandardGraduation> grads, List<String> names) {
        if (ObjectUtils.isEmpty(grads)) {
            return;
        }
        for (StandardGraduation g : grads) {
            if (g == null) {
                continue;
            }
            if (StringUtils.isNotBlank(g.getName())) {
                names.add(g.getName());
            }
            collectGraduationNames(g.getChildren(), names);
        }
    }

    private List<TeachingPlanObjective> filterObjective(List<TeachingPlanObjective> list, String key) {
        if (ObjectUtils.isEmpty(list)) {
            return new ArrayList<>();
        }
        return list.stream()
                .filter(o -> containsAny(o.getObjectiveTypeCode(), key) || containsAny(o.getObjectiveTypeName(), key))
                .collect(Collectors.toList());
    }

    private List<TeachingPlanTargetDesign> filterDesign(List<TeachingPlanTargetDesign> list, String key) {
        if (ObjectUtils.isEmpty(list)) {
            return new ArrayList<>();
        }
        return list.stream()
                .filter(d -> containsAny(d.getDesignTypeCode(), key))
                .collect(Collectors.toList());
    }

    private static boolean containsAny(String text, String key) {
        return StringUtils.isNotBlank(text) && text.contains(key);
    }

    private String guessTypeName(String code) {
        if (containsAny(code, KEY_KNOWLEDGE)) {
            return "知识目标";
        }
        if (containsAny(code, KEY_ABILITY)) {
            return "能力目标";
        }
        if (containsAny(code, KEY_QUALITY)) {
            return "素质目标";
        }
        return str(code);
    }

    /** sections -> {sectionTitle/sectionCode : content}，便于按标签取大段文本；内容去掉 HTML 标签 */
    private Map<String, String> sectionsMap(List<TeachingPlanSection> sections) {
        Map<String, String> map = new HashMap<>();
        if (ObjectUtils.isEmpty(sections)) {
            return map;
        }
        for (TeachingPlanSection s : sections) {
            String plain = stripHtml(s.getContent());
            if (StringUtils.isNotBlank(s.getSectionTitle())) {
                map.put(s.getSectionTitle(), plain);
            }
            if (StringUtils.isNotBlank(s.getSectionCode())) {
                map.put(s.getSectionCode(), plain);
            }
        }
        return map;
    }

    /**
     * 富文本 HTML 转纯文本。
     * 去掉标签，把常见换行标签还原为换行，并解码常见 HTML 实体。
     * 课程概述等字段来自前端富文本编辑器，可能带有 p 标签，生成 Word 时需要剥离。
     */
    private static String stripHtml(String html) {
        if (StringUtils.isBlank(html)) {
            return html == null ? "" : html;
        }
        String s = html;
        // 换行类标签先转成换行，再去掉其余标签
        s = s.replaceAll("(?i)<br\\s*/?>", "\n");
        s = s.replaceAll("(?i)</p>", "\n");
        s = s.replaceAll("(?i)</div>", "\n");
        s = s.replaceAll("(?i)</li>", "\n");
        s = s.replaceAll("(?i)</h[1-6]>", "\n");
        s = s.replaceAll("(?i)</tr>", "\n");
        s = s.replaceAll("<[^>]+>", "");
        s = s.replace("&nbsp;", " ")
                .replace("&lt;", "<")
                .replace("&gt;", ">")
                .replace("&amp;", "&")
                .replace("&quot;", "\"")
                .replace("&#39;", "'")
                .replace("&ldquo;", "\u201c")
                .replace("&rdquo;", "\u201d");
        // 压缩连续空行
        s = s.replaceAll("[\r\n]{3,}", "\n\n");
        return s.trim();
    }
}
