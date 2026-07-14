package com.doinner.csys.entity.csys;

import com.doinner.csys.entity.csys.model.CellWidth;
import com.doinner.csys.entity.csys.model.CountModel;
import com.doinner.csys.entity.csys.model.CreditsDetailModel;
import com.doinner.csys.entity.csys.model.DictContent;
import com.doinner.csys.entity.csys.model.DurationAndCreditsModel;
import com.doinner.csys.entity.csys.model.TrainingPlanModel;
import com.doinner.csys.entity.csys.model.TrainingSchemeCourseModel;
import com.doinner.csys.utils.WordUtil;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.poi.xwpf.usermodel.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 军士职业技术教育学员 培养方案文档生成器（三年制）。
 *
 * 适用对象（培养层次 educationLevel）为 3（军士职业技术教育学员）时，
 * 由服务层 {@code TrainingServiceImpl.createTrainingPlanGenerator} 选择本子类实例化。
 *
 * 与父类 {@link TrainingPlanGenerator}（四年制）的差异：
 * 1. 学制为三年 -> 学期安排 6 列（第一~第三学年 秋/春），{@link #termColumnCount()} 返回 6。
 * 2. 课程部分按"军事职业教育"父模块下的 4 个子模块（政治理论/军事基础/任职基础/任职岗位）
 *    分别建表，表头列序与模板一致（无"修读要求""学分"列）。
 * 3. 学时学分表为模板的"各教学环节学时要求表"（科目 | 必修学时 | 小计学时/比例）。
 * 4. 培养目标 / 毕业要求 复用父类生成方式，但文案为军士专属（见本类常量）。
 *
 * poi 合并单元格后 cell 索引会塌缩，表头/数据行一律按"塌缩后索引"写（详见 skill 文档）。
 */
public class NcoTrainingPlanGenerator extends TrainingPlanGenerator {

    // ============================ 军士专属文案 ============================

    /**
     * 学时学分要求说明段落（"（二）学时学分要求"下，模板文案）。
     * {totalHour} 占位符在运行时用 DurationAndCreditsModel.getTotalHour() 统计的实际课程总学时替换
     * （见 {@link #generateDurationAndCredits}）。
     */
    private static final String DURATION_CONTENT2_NCO_TEMPLATE =
            "学员在校期间安排课程教学{totalHour}学时，按照政治理论、军事基础、任职基础和任职岗位 4 个模块设置课程。" +
                    "必修课程和讲座学时均计入课程学时。所有课程按照类别分为政治理论、军事基础、任职基础和任职岗位等4类。" +
                    "课程教学具体学时要求如下表。";

    /** 课程安排说明段落（"（一）课程安排"下） */
    private static final String COURSE_ARRANGEMENT_INTRO =
            "通识课程任选课程安排表见附录1、专业任选课程安排表见附录2、其他课程和实践训练安排如下：";

    /** 课程表下方备注段落（模板原文） */
    private static final String COURSE_TABLE_REMARK =
            "备注：（1）考核方式栏中，\"S\"表示考试，\"C\"表示考察。\n" +
                    "（2）实践学时包括课程拓展学习、实践探索、课题研究、论文撰写、小班研讨、实验、上机、野外作业、岗位见习等多种形式。";

    /** 毕业要求说明段落（"二、毕业要求"下，军士文案） */
    private static final String STANDARD_GRADUATION_CONTENT_NCO =
            "具有学籍的学员，在修业年限内完成本人才培养方案规定的全部课程，成绩合格；" +
                    "通过本专业人才培养方案规定的中级职业技能鉴定、实习结论为合格的，" +
                    "依据国防科技大学《高等教育生长军官学员、军士职业技术教育学员学籍管理规定实施细则（暂行）》，颁发毕业证书。\n" +
                    "学员未通过本人才培养方案规定的中级职业技能鉴定，但达到其他条件的，予以结业，退回原送学单位。" +
                    "学员实习结论为不合格的，不安排再次实习，予以肄业，实习结束后退回原送学单位。";

    /** 4 个子模块的固定展示顺序：子模块ID -> 展示名称 */
    private static final LinkedHashMap<String, String> NCO_SUB_MODULE_ORDER = new LinkedHashMap<String, String>() {{
        put(DictContent.POLITICAL_THEORY_NCO, DictContent.POLITICAL_THEORY_NCO_NAME);
        put(DictContent.MILITARY_FOUNDATION_NCO, DictContent.MILITARY_FOUNDATION_NCO_NAME);
        put(DictContent.POSITION_FOUNDATION, DictContent.POSITION_FOUNDATION_NAME);
        put(DictContent.DUTY_POSITION, DictContent.DUTY_POSITION_NAME);
    }};

    /** 各教学环节学时要求表 行顺序（展示名称 -> 行号顺序由遍历保持） */
    private static final List<String> NCO_DURATION_ROW_ORDER = Arrays.asList(
            DictContent.POLITICAL_THEORY_NCO_NAME + "课程",
            DictContent.MILITARY_FOUNDATION_NCO_NAME + "课程",
            DictContent.POSITION_FOUNDATION_NAME + "课程",
            DictContent.DUTY_POSITION_NAME + "课程"
    );

    /**
     * 三年制：学期安排 6 列（第一~第三学年，每学年秋/春）。
     */
    @Override
    protected int termColumnCount() {
        return 6;
    }

    // ============================ 一、培养目标 / 二、毕业要求 ============================
    // 复用父类 generateTrainingTarget / generateStandardGraduations 的结构，
    // 仅在需要军士文案处覆盖。父类生成方式不变，文案差异见模型字段（standardGraduationContent）。

    /**
     * 生成毕业要求部分 -- 覆盖说明段落文案为军士文案，树形结构仍走父类逻辑。
     */
    @Override
    protected void generateStandardGraduations(TrainingPlanModel model, XWPFDocument document) {
        WordUtil.createHeading(document, "二、毕业要求", 1);
        // 军士专属说明段落
        WordUtil.createParagraph(document, STANDARD_GRADUATION_CONTENT_NCO, null);

        List<com.doinner.csys.domain.StandardGraduation> standardGraduations =
                com.doinner.csys.utils.TreeBuilderUtils.buildRootTree(model.getStandardGraduations());
        if (ObjectUtils.isEmpty(standardGraduations)) {
            return;
        }
        for (com.doinner.csys.domain.StandardGraduation graduation : standardGraduations) {
            processFirstLevel(graduation, document);
        }
    }

    // ============================ 三、修业时间与学时学分 ============================

    /**
     * 生成修业时间与学时学分部分 -- 复用父类结构，覆盖学时学分说明文案。
     */
    @Override
    protected void generateDurationAndCredits(TrainingPlanModel model, XWPFDocument document) {
        DurationAndCreditsModel dac = model.getDurationAndCredits();

        WordUtil.createHeading(document, "三、修业时间及学时学分", 1);

        // （一）修业时间安排
        WordUtil.createHeading(document, dac.getFirstLevelTitle1(), 2);
        WordUtil.createParagraph(document, dac.getFirstLevelContent1(), null);

        // （二）学时学分要求
        WordUtil.createHeading(document, dac.getFirstLevelTitle2(), 2);
        // 实际课程总学时取统计值（DurationAndCreditsModel.totalHour，由各模块学时累加而来）
        String durationContent = DURATION_CONTENT2_NCO_TEMPLATE.replace("{totalHour}", formatHours(dac.getTotalHour()));
        WordUtil.createParagraph(document, durationContent, null);
        WordUtil.createParagraph(document, "各教学环节学时要求表", null);

        // 模板学时学分表（科目 | 必修学时 | 小计学时/比例）
        generateNcoDurationTable(dac, document);

        // （三）学分冲抵机制（沿用模型内容）
        WordUtil.createHeading(document, dac.getFirstLevelTitle3(), 2);
        WordUtil.createParagraph(document, dac.getFirstLevelContent3(), null);
    }

    /**
     * 军士学时学分表 -- 各教学环节学时要求表。
     * 表头（3行）：
     *   行0：科目(跨3行) | 学时学分要求(跨5列)
     *   行1：[科目续] | 必修(跨2列) | 小计(跨3列)
     *   行2：[科目续] | 学时 | 学分 | 学时 | 学分 | 比例
     * 行：政治理论课程 / 军事基础课程 / 任职基础课程 / 任职岗位课程 / 总计
     * 物理列：6（科目1 + 必修2 + 小计3）
     */
    private XWPFTable generateNcoDurationTable(DurationAndCreditsModel model, XWPFDocument document) {
        int dataRows = NCO_DURATION_ROW_ORDER.size();
        int totalRows = 3 + dataRows + 1; // 表头3行 + 模块行 + 总计行
        int totalCols = 6;
        CellWidth cellWidth = new CellWidth(6);

        XWPFTable table = document.createTable(totalRows, totalCols);
        WordUtil.initTableGrid(table, totalCols, 1000);
        table.setWidthType(TableWidthType.PCT);
        table.setWidth("100%");

        // ========== 表头第1行 ==========
        // 科目，跨3行
        setCellText(table.getRow(0).getCell(0), "科  目", true, cellWidth.getCellWidth(1));
        WordUtil.mergeCellsVertical(table, 0, 0, 2);
        // 学时学分要求，跨5列
        setCellText(table.getRow(0).getCell(1), "学时学分要求", true, cellWidth.getCellWidth(5));
        WordUtil.mergeCellsHorizontal(table, 0, 1, 5);

        // ========== 表头第2行 ==========
        // 必修，跨2列（塌缩后从1起）
        setCellText(table.getRow(1).getCell(1), "必修", true, cellWidth.getCellWidth(2));
        WordUtil.mergeCellsHorizontal(table, 1, 1, 2);
        // 小计，跨3列（塌缩后从2起）
        setCellText(table.getRow(1).getCell(2), "小计", true, cellWidth.getCellWidth(3));
        WordUtil.mergeCellsHorizontal(table, 1, 2, 4);

        // ========== 表头第3行（学时/学分/比例 标签） ==========
        setCellText(table.getRow(2).getCell(1), "学时", true, cellWidth.getCellWidth(1));
        setCellText(table.getRow(2).getCell(2), "学分", true, cellWidth.getCellWidth(1));
        setCellText(table.getRow(2).getCell(3), "学时", true, cellWidth.getCellWidth(1));
        setCellText(table.getRow(2).getCell(4), "学分", true, cellWidth.getCellWidth(1));
        setCellText(table.getRow(2).getCell(5), "比例", true, cellWidth.getCellWidth(1));

        // ========== 数据行 + 总计 ==========
        double totalRequiredHour = 0.0;
        double totalSubTotalHour = 0.0;
        int dataRow = 3;
        for (String rowName : NCO_DURATION_ROW_ORDER) {
            CreditsDetailModel detail = findCreditsDetail(model, rowName);
            double requiredHour = detail != null && detail.getRequiredHours() != null ? detail.getRequiredHours() : 0;
            double subTotalHour = detail != null && detail.getTotalHours() != null ? detail.getTotalHours() : 0;

            setCellText(table.getRow(dataRow).getCell(0), rowName, true, cellWidth.getCellWidth(1));
            setCellText(table.getRow(dataRow).getCell(1), formatNumber(requiredHour), false, cellWidth.getCellWidth(1));
            // 必修学分：模板以"/"占位
            setCellText(table.getRow(dataRow).getCell(2), "/", false, cellWidth.getCellWidth(1));
            setCellText(table.getRow(dataRow).getCell(3), formatNumber(subTotalHour), false, cellWidth.getCellWidth(1));
            // 小计学分：模板以"/"占位
            setCellText(table.getRow(dataRow).getCell(4), "/", false, cellWidth.getCellWidth(1));
            // 比例：总计算出后回填，先留空
            setCellText(table.getRow(dataRow).getCell(5), "", false, cellWidth.getCellWidth(1));

            totalRequiredHour += requiredHour;
            totalSubTotalHour += subTotalHour;
            dataRow++;
        }
        // 总计行
        setCellText(table.getRow(dataRow).getCell(0), "总计", true, cellWidth.getCellWidth(1));
        setCellText(table.getRow(dataRow).getCell(1), formatNumber(totalRequiredHour), true, cellWidth.getCellWidth(1));
        setCellText(table.getRow(dataRow).getCell(2), "0", true, cellWidth.getCellWidth(1));
        setCellText(table.getRow(dataRow).getCell(3), formatNumber(totalSubTotalHour), true, cellWidth.getCellWidth(1));
        setCellText(table.getRow(dataRow).getCell(4), "0", true, cellWidth.getCellWidth(1));
        setCellText(table.getRow(dataRow).getCell(5), totalSubTotalHour > 0 ? "100%" : "", true, cellWidth.getCellWidth(1));

        // 回填各行比例
        if (totalSubTotalHour > 0) {
            for (int i = 0; i < NCO_DURATION_ROW_ORDER.size(); i++) {
                CreditsDetailModel detail = findCreditsDetail(model, NCO_DURATION_ROW_ORDER.get(i));
                double subTotalHour = detail != null && detail.getTotalHours() != null ? detail.getTotalHours() : 0;
                double ratio = subTotalHour / totalSubTotalHour * 100;
                setCellText(table.getRow(3 + i).getCell(5), String.format("%.2f%%", ratio), false, cellWidth.getCellWidth(1));
            }
        }
        return table;
    }

    /**
     * 按模块展示名称从模型中查找对应 CreditsDetailModel。
     * 模型 generalCourses 的 modelName 为子模块字典名（可能带"(军士)"后缀），这里按"包含"匹配。
     */
    private CreditsDetailModel findCreditsDetail(DurationAndCreditsModel model, String rowName) {
        if (model.getGeneralCourses() == null) {
            return null;
        }
        // rowName 形如"政治理论课程"，modelName 形如"政治理论(军士)"，取前缀匹配
        String prefix = rowName.replace("课程", "");
        for (CreditsDetailModel detail : model.getGeneralCourses()) {
            if (detail.getModelName() != null && detail.getModelName().contains(prefix)) {
                return detail;
            }
        }
        return null;
    }

    // ============================ 四、课程设置 ============================

    /**
     * 生成教学训练体系与安排（军士：4 张子模块课程表 + 实践训练表）。
     */
    @Override
    protected void generateCourseArrangements(TrainingPlanModel model, XWPFDocument document) {
        WordUtil.createHeading(document, "四、课程设置", 1);
        WordUtil.createParagraph(document, COURSE_ARRANGEMENT_INTRO, null);
        WordUtil.createHeading(document, "（一）课程安排", 2);

        List<TrainingSchemeCourseModel> ncoCourses = model.getNcoCourses();
        if (ObjectUtils.isNotEmpty(ncoCourses)) {
            // 按子模块ID分组并按固定顺序排序
            Map<String, List<TrainingSchemeCourseModel>> subModuleMap = groupNcoCourses(ncoCourses);
            int index = 1;
            for (Map.Entry<String, List<TrainingSchemeCourseModel>> entry : subModuleMap.entrySet()) {
                String displayName = NCO_SUB_MODULE_ORDER.getOrDefault(entry.getKey(), entry.getValue().get(0).getCourseModeChildrenName());
                String title = index + "." + displayName + "课程教学安排";
                generateNcoCourseTable(document, title, displayName, entry.getValue());
                WordUtil.createParagraph(document, "", null); // 空行
                index++;
            }
        }

        // 备注
        XWPFParagraph remarkParagraph = document.createParagraph();
        XWPFRun remarkRun = remarkParagraph.createRun();
        remarkRun.setText(COURSE_TABLE_REMARK);
        remarkRun.setFontSize(10);

        // （二）实践训练安排
        WordUtil.createHeading(document, "（二）实践训练安排", 2);
        if (model.getTrainingSubjectCourses() != null && !model.getTrainingSubjectCourses().isEmpty()) {
            practicalProjectCourseTable(document, "实践训练课目与安排", model.getTrainingSubjectCourses());
            WordUtil.createParagraph(document, "", null);
        }
    }

    /**
     * 按子模块ID分组，并按 {@link #NCO_SUB_MODULE_ORDER} 的固定顺序排序。
     */
    private Map<String, List<TrainingSchemeCourseModel>> groupNcoCourses(List<TrainingSchemeCourseModel> courses) {
        Map<String, List<TrainingSchemeCourseModel>> grouped = courses.stream()
                .filter(c -> c.getCourseModeChildrenId() != null)
                .collect(Collectors.groupingBy(TrainingSchemeCourseModel::getCourseModeChildrenId, LinkedHashMap::new, Collectors.toList()));
        // 按固定顺序重排
        LinkedHashMap<String, List<TrainingSchemeCourseModel>> ordered = new LinkedHashMap<>();
        for (String subModuleId : NCO_SUB_MODULE_ORDER.keySet()) {
            if (grouped.containsKey(subModuleId)) {
                ordered.put(subModuleId, grouped.get(subModuleId));
            }
        }
        // 容纳未识别的子模块
        grouped.forEach((k, v) -> {
            if (!ordered.containsKey(k)) {
                ordered.put(k, v);
            }
        });
        return ordered;
    }

    /**
     * 军士单张课程教学安排表（3 年 6 学期）。
     * 表头：课程模块(跨3行) | 课程名称(跨3行) | 考核方式(跨3行) | 学时安排(跨3列) | 各学期学时分配(跨6列)
     * 物理列：模块1+名称1+考核1+学时3+学期6 = 12 列
     */
    protected XWPFTable generateNcoCourseTable(XWPFDocument document, String title, String moduleName,
                                                List<TrainingSchemeCourseModel> courses) {
        WordUtil.createHeading(document, title, 3);

        int dataRows = courses.size();
        int totalRows = 3 + dataRows + 1; // 表头3行 + 数据行 + 小计1行
        int totalCols = 12; // 模块1+名称1+考核1+学时3+学期6
        CellWidth cellWidth = new CellWidth(12);

        XWPFTable table = document.createTable(totalRows, totalCols);
        WordUtil.initTableGrid(table, totalCols, 1000);
        table.setWidthType(TableWidthType.PCT);
        table.setWidth("100%");

        // 表头（3行）
        generateNcoCourseHeader(table, cellWidth);

        int dataRowStart = 3;
        CountModel countModel = new CountModel();
        for (int i = 0; i < courses.size(); i++) {
            TrainingSchemeCourseModel course = courses.get(i);
            setNcoCourseDataRow(table, dataRowStart + i, course, countModel, cellWidth);
        }

        // 课程模块列纵向合并
        if (dataRows > 0) {
            XWPFTableCell moduleCell = table.getRow(dataRowStart).getCell(0);
            WordUtil.setCellText(moduleCell, moduleName, false, cellWidth.getCellWidth(1));
            if (dataRows > 1) {
                WordUtil.mergeCellsVertical(table, 0, dataRowStart, dataRowStart + dataRows - 1);
            }
        }

        // 小计行（学时三列起始=3，学期起始=6）
        setNcoTotalRow(table, totalRows - 1, countModel, 3, 6, cellWidth);

        return table;
    }

    /**
     * 军士课程表表头（3行，6 学期列）。
     */
    protected void generateNcoCourseHeader(XWPFTable table, CellWidth cellWidth) {
        // 行0：课程模块(跨3行) | 课程名称(跨3行) | 考核方式(跨3行) | 学时安排(跨3列) | 各学期学时分配(跨6列)
        // 物理列布局：模块(0) | 名称(1) | 考核(2) | 学时(3-5) | 学期(6-11)
        setCellText(table.getRow(0).getCell(0), "课程模块", true, cellWidth.getCellWidth(1));
        WordUtil.mergeCellsVertical(table, 0, 0, 2);

        setCellText(table.getRow(0).getCell(1), "课程名称", true, cellWidth.getCellWidth(1));
        WordUtil.mergeCellsVertical(table, 1, 0, 2);

        setCellText(table.getRow(0).getCell(2), "考核方式", true, cellWidth.getCellWidth(1));
        WordUtil.mergeCellsVertical(table, 2, 0, 2);

        // 学时安排，跨3列
        setCellText(table.getRow(0).getCell(3), "学时安排", true, cellWidth.getCellWidth(3));
        WordUtil.mergeCellsHorizontal(table, 0, 3, 5);

        // 各学期学时分配，跨6列
        setCellText(table.getRow(0).getCell(4), "各学期学时分配", true, cellWidth.getCellWidth(6));
        WordUtil.mergeCellsHorizontal(table, 0, 4, 9);

        // 行1：小计 | 讲授 | 实践 | 第一学年(跨2) | 第二学年(跨2) | 第三学年(跨2)
        // 塌缩后：学时子项从3起(小计3/讲授4/实践5)，学年从6起
        setCellText(table.getRow(1).getCell(3), "小计", true, cellWidth.getCellWidth(1));
        WordUtil.mergeCellsVertical(table, 3, 1, 2);
        setCellText(table.getRow(1).getCell(4), "讲授", true, cellWidth.getCellWidth(1));
        WordUtil.mergeCellsVertical(table, 4, 1, 2);
        setCellText(table.getRow(1).getCell(5), "实践", true, cellWidth.getCellWidth(1));
        WordUtil.mergeCellsVertical(table, 5, 1, 2);

        setCellText(table.getRow(1).getCell(6), "第一学年", true, cellWidth.getCellWidth(2));
        WordUtil.mergeCellsHorizontal(table, 1, 6, 7);
        setCellText(table.getRow(1).getCell(7), "第二学年", true, cellWidth.getCellWidth(2));
        WordUtil.mergeCellsHorizontal(table, 1, 7, 8);
        setCellText(table.getRow(1).getCell(8), "第三学年", true, cellWidth.getCellWidth(2));
        WordUtil.mergeCellsHorizontal(table, 1, 8, 9);

        // 行2：秋 | 春 | 秋 | 春 | 秋 | 春（学期子项，塌缩后从6起）
        setCellText(table.getRow(2).getCell(6), "秋", true, cellWidth.getCellWidth(1));
        setCellText(table.getRow(2).getCell(7), "春", true, cellWidth.getCellWidth(1));
        setCellText(table.getRow(2).getCell(8), "秋", true, cellWidth.getCellWidth(1));
        setCellText(table.getRow(2).getCell(9), "春", true, cellWidth.getCellWidth(1));
        setCellText(table.getRow(2).getCell(10), "秋", true, cellWidth.getCellWidth(1));
        setCellText(table.getRow(2).getCell(11), "春", true, cellWidth.getCellWidth(1));
    }

    /**
     * 军士课程表数据行。
     * 列序：课程名称@1 -> 考核方式@2 -> 小计@3 -> 讲授@4 -> 实践@5 -> 学期@6..11
     * 无"修读要求""学分"列（与父类四年制不同）。
     */
    protected void setNcoCourseDataRow(XWPFTable table, int rowIndex, TrainingSchemeCourseModel course,
                                       CountModel countModel, CellWidth cellWidth) {
        XWPFTableRow row = table.getRow(rowIndex);

        // 课程名称
        WordUtil.setCellText(row.getCell(1), course.getName(), false, cellWidth.getCellWidth(1));

        // 考核方式
        WordUtil.setCellText(row.getCell(2), getAssessText(course.getExaMethod()), false, cellWidth.getCellWidth(1));

        // 小计（课程总学时）
        WordUtil.setCellText(row.getCell(3), course.getHours() != null ? formatHours(course.getHours()) : "", false, cellWidth.getCellWidth(1));
        countModel.setTotalHours(countModel.getTotalHours() + (course.getHours() == null ? 0 : course.getHours()));

        // 讲授
        WordUtil.setCellText(row.getCell(4), course.getTeachHours() != null ? formatHours(course.getTeachHours()) : "", false, cellWidth.getCellWidth(1));
        countModel.setTeachHours(countModel.getTeachHours() + (course.getTeachHours() == null ? 0 : course.getTeachHours()));

        // 实践
        WordUtil.setCellText(row.getCell(5), course.getPracticeHours() != null ? formatHours(course.getPracticeHours()) : "", false, cellWidth.getCellWidth(1));
        countModel.setPracticeHours(countModel.getPracticeHours() + (course.getPracticeHours() == null ? 0 : course.getPracticeHours()));

        // 学期安排（6列，从第6列起）
        setTermCheckmarks(row, course, 6, cellWidth, countModel);
    }

    /**
     * 军士课程表小计行。
     * 模块+名称+考核三列(0..2)合并为"小计"标签，学时三列@3/4/5，学期@6起。
     * 注意：本表无"学分"列，与父类 setTotalRow 的 creditCol 语义不同，故单独实现。
     */
    protected void setNcoTotalRow(XWPFTable table, int rowIndex, CountModel countModel,
                                  int dataCell, int termStartCell, CellWidth cellWidth) {
        XWPFTableRow row = table.getRow(rowIndex);
        int termCount = termColumnCount();
        int labelEndCol = dataCell - 1; // 考核列并入标签区(0..labelEndCol)，labelEndCol=2

        // "小  计"标签覆盖 0..labelEndCol(模块+名称+考核)
        WordUtil.setCellText(row.getCell(0), "小  计", true, cellWidth.getCellWidth(labelEndCol + 1));
        // 学时小计/讲授/实践
        WordUtil.setCellText(row.getCell(dataCell), formatHours(countModel.getTotalHours()), true, cellWidth.getCellWidth(1));
        WordUtil.setCellText(row.getCell(dataCell + 1), formatHours(countModel.getTeachHours()), true, cellWidth.getCellWidth(1));
        WordUtil.setCellText(row.getCell(dataCell + 2), formatHours(countModel.getPracticeHours()), true, cellWidth.getCellWidth(1));
        // 学期各列总学时
        Double[] termHours = countModel.getTermHours();
        for (int i = 0; i < termCount; i++) {
            WordUtil.setCellText(row.getCell(termStartCell + i), formatHours(termHours[i]), true, cellWidth.getCellWidth(1));
        }

        // 合并"小计"标签区 0..labelEndCol
        if (labelEndCol > 0) {
            WordUtil.mergeCellsHorizontal(table, rowIndex, 0, labelEndCol);
        }
    }

    // ============================ 工具方法 ============================

    private String formatNumber(double v) {
        if (v == Math.rint(v) && !Double.isInfinite(v)) {
            return String.valueOf((long) v);
        }
        return String.valueOf(v);
    }
}
