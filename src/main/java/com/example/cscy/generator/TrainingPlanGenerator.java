package com.example.cscy.generator;

import com.example.cscy.entity.scheme.model.*;
import com.example.cscy.utils.WordUtils;
import org.apache.poi.xwpf.usermodel.*;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * 培养方案文档生成器 - 基于TrainingPlanModel生成培养方案Word文档
 *
 * 包含四个一级标题：
 * 1. 培养目标
 * 2. 毕业要求
 * 3. 修业时间与学时学分
 * 4. 教学训练体系与安排
 */
public class TrainingPlanGenerator {
    /**
     * 生成培养方案文档
     *
     * @param model      培养方案数据模型
     * @param outputPath 输出文件路径
     * @throws IOException 生成异常
     */
    public void generate(TrainingPlanModel model, String outputPath) throws IOException {
        XWPFDocument document = new XWPFDocument();

        try {
            // 1. 创建标题
            WordUtils.createTitle(document, model.getTrainingPlanName());

            // 2. 培养目标
            generateTrainingTarget(model, document);

            // 3. 毕业要求
            generateStandardGraduations(model, document);

            // 4. 修业时间与学时学分
            generateDurationAndCredits(model, document);

            // 5. 教学训练体系与安排（课程信息）
            generateCourseArrangements(model, document);

            // 输出文档
            FileOutputStream out = new FileOutputStream(outputPath);
            document.write(out);
            out.close();

        } finally {
            document.close();
        }
    }

    /**
     * 生成培养目标部分
     */
    private void generateTrainingTarget(TrainingPlanModel model, XWPFDocument document) {
        TrainingTargetModel target = model.getTrainingTarget();

        // 一级标题
        WordUtils.createHeading(document, target.getFirstLevelTitle(), 1);

        // 一级内容
        WordUtils.createParagraph(document, target.getFirstLevelContent(), null);

        // 二级标题1
        WordUtils.createHeading(document, target.getSecondLevelTitle1(), 2);
        WordUtils.createParagraph(document, target.getSecondLevelContent1(), null);

        // 二级标题2
        WordUtils.createHeading(document, target.getSecondLevelTitle2(), 2);
        WordUtils.createParagraph(document, target.getSecondLevelContent2(), null);
    }

    /**
     * 生成毕业要求部分
     */
    private void generateStandardGraduations(TrainingPlanModel model, XWPFDocument document) {
        // 一级标题
        WordUtils.createHeading(document, "二、毕业要求", 1);

        // 毕业要求内容说明
        WordUtils.createParagraph(document, model.getStandardGraduationContent(), null);

        // 处理多级毕业要求结构
        List<StandardGraduationModel> graduations = model.getStandardGraduations();
        if (graduations != null && !graduations.isEmpty()) {
            // 按id排序，确保父项在前
            graduations.sort((a, b) -> {
                if (a.getId() == null) return -1;
                if (b.getId() == null) return 1;
                return a.getId().compareTo(b.getId());
            });

            // 找到所有level=1的项（parentId为-1L或null）
            for (StandardGraduationModel item : graduations) {
                if ("1".equals(item.getLevel())) {
                    WordUtils.createHeading(document, item.getName(), 2);

                    // 查找其子项（level=2），条件是parentId等于当前项的id
                    List<StandardGraduationModel> level2Items = findChildrenById(graduations, item.getId());
                    for (StandardGraduationModel level2 : level2Items) {
                        if ("2".equals(level2.getLevel())) {
                            WordUtils.createHeading(document, level2.getName(), 3);

                            // 查找其子项（level=3）- 具体内容
                            List<StandardGraduationModel> level3Items = findChildrenById(graduations, level2.getId());
                            for (StandardGraduationModel level3 : level3Items) {
                                if ("3".equals(level3.getLevel())) {
                                    WordUtils.createParagraph(document, level3.getName(), null);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * 根据父项ID查找子项
     */
    private List<StandardGraduationModel> findChildrenById(List<StandardGraduationModel> items, Long parentId) {
        List<StandardGraduationModel> children = new ArrayList<>();
        if (parentId == null) {
            return children;
        }
        for (StandardGraduationModel item : items) {
            if (parentId.equals(item.getParentId())) {
                children.add(item);
            }
        }
        return children;
    }

    /**
     * 生成修业时间与学时学分部分
     */
    private void generateDurationAndCredits(TrainingPlanModel model, XWPFDocument document) {
        DurationAndCreditsModel dac = model.getDurationAndCredits();

        // 一级标题
        WordUtils.createHeading(document, "三、修业时间与学时学分", 1);

        // （一）修业时间安排
        WordUtils.createHeading(document, dac.getFirstLevelTitle1(), 2);
        WordUtils.createParagraph(document, dac.getFirstLevelContent1(), null);

        // （二）学时学分要求
        WordUtils.createHeading(document, dac.getFirstLevelTitle2(), 2);
        WordUtils.createParagraph(document, dac.getFirstLevelContent2(), null);

        // （三）学分冲抵机制
        WordUtils.createHeading(document, dac.getFirstLevelTitle3(), 2);
        WordUtils.createParagraph(document, dac.getFirstLevelContent3(), null);

        // 学分冲抵表格
        generateDurationAndCreditsTable(dac, document);
    }

    /**
     * 生成修业时间与学时学分表格
     */
    private XWPFTable generateDurationAndCreditsTable(DurationAndCreditsModel model, XWPFDocument document) {
        // 这里可以根据需要生成具体的表格
        // 计算总行数：表头3行 + 数据行 + 小计1行
        int totalRows = 16;
        int totalCols = 8; // 原始8列，删除第6列和第11列后实际13列

        XWPFTable table = document.createTable(totalRows, totalCols);
        table.setWidthType(TableWidthType.PCT);
        table.setWidth("100%");

        // 创建表格
        // ========== 表头第1行（行号0） ==========
        // 列0：课程模块，跨行3行
        setCellText(table.getRow(0).getCell(0), "课程体系", true, "5000");
        WordUtils.mergeCellsHorizontal(table, 0, 0, 1);
        WordUtils.mergeCellsHorizontal(table, 1, 0, 1);
        WordUtils.mergeCellsHorizontal(table, 2, 0, 1);
        WordUtils.mergeCellsVertical(table, 0, 0, 2);


        // 列1：学时学分要求，跨行6列
        setCellText(table.getRow(0).getCell(1), "学时学分要求", true, "9000");
        WordUtils.mergeCellsHorizontal(table, 0, 1, 6);

        // ========== 表头第2行（行号1） ==========
        // 列2：必修，跨行2行
        setCellText(table.getRow(1).getCell(1), "必修", true, "3000");
        WordUtils.mergeCellsHorizontal(table, 1, 1, 2);

        // 列3：选修，跨行2行
        setCellText(table.getRow(1).getCell(2), "选修", true, "3000");
        WordUtils.mergeCellsHorizontal(table, 1, 2,3);

        // 列3：小计，跨行2行
        setCellText(table.getRow(1).getCell(3), "小计", true, "3000");
        WordUtils.mergeCellsHorizontal(table, 1, 3, 4);
        // ========== 表头第2行（行号2） ==========
        setCellText(table.getRow(2).getCell(1), "学时", true, "1500");
        setCellText(table.getRow(2).getCell(2), "学分", true, "1500");
        setCellText(table.getRow(2).getCell(3), "学时", true, "1500");
        setCellText(table.getRow(2).getCell(4), "学分", true, "1500");
        setCellText(table.getRow(2).getCell(5), "学时", true, "1500");
        setCellText(table.getRow(2).getCell(6), "学分", true, "1500");
        // ========== 内容 ==========
        setCellText(table.getRow(3).getCell(0), "通识课程", true, "1500");
        WordUtils.mergeCellsVertical(table, 0, 3, 7);
        // ========== 内容 第一行：3 ==========
        setCellText(table.getRow(3).getCell(1), "政治理论", true, "3500");
        // ========== 内容 第二行：4 ==========
        setCellText(table.getRow(4).getCell(1), "军事基础", true, "3500");
        // ========== 内容 第三行：5 ==========
        setCellText(table.getRow(5).getCell(1), "基础科学", true, "3500");
        // ========== 内容 第四行：6 ==========
        setCellText(table.getRow(6).getCell(1), "人文与社会科学", true, "3500");
        // ========== 内容 第五行：7 ==========
        setCellText(table.getRow(7).getCell(1), "人工智能与信息技术", true, "3500");

        setCellText(table.getRow(8).getCell(0), "专业课程", true, "1500");
        WordUtils.mergeCellsVertical(table, 0, 8, 9);
        // ========== 内容 第六行：8 ==========
        setCellText(table.getRow(8).getCell(1), "专业大类", true, "3500");
        // ========== 内容 第七行：9 ==========
        setCellText(table.getRow(9).getCell(1), "专业方向", true, "3500");
        //小计
        setTotalRow(table, 10);
        //=========表2================
        setCellText(table.getRow(11).getCell(0), "实践训练体系", true, "5000");
        WordUtils.mergeCellsVertical(table, 0, 11, 13);
        WordUtils.mergeCellsHorizontal(table, 11, 0, 1);
        WordUtils.mergeCellsHorizontal(table, 12, 0, 1);
        WordUtils.mergeCellsHorizontal(table, 13, 0, 1);
        // 列1：学时学分要求，跨行6列
        setCellText(table.getRow(11).getCell(1), "学时学分要求", true, "9000");
        WordUtils.mergeCellsHorizontal(table, 11, 1, 6);

        // ========== 表头第2行（行号1） ==========
        // 列2：必修，跨行2行
        setCellText(table.getRow(12).getCell(1), "必修", true, "3000");
        WordUtils.mergeCellsHorizontal(table, 12, 1, 2);

        // 列3：选修，跨行2行
        setCellText(table.getRow(12).getCell(2), "选修", true, "3000");
        WordUtils.mergeCellsHorizontal(table, 12, 2, 3);

        // 列3：小计，跨行2行
        setCellText(table.getRow(12).getCell(3), "小计", true, "3000");
        WordUtils.mergeCellsHorizontal(table, 12, 3, 4);
        setCellText(table.getRow(13).getCell(1), "学时", true, "1500");
        setCellText(table.getRow(13).getCell(2), "学分", true, "1500");
        setCellText(table.getRow(13).getCell(3), "学时", true, "1500");
        setCellText(table.getRow(13).getCell(4), "学分", true, "1500");
        setCellText(table.getRow(13).getCell(5), "学时", true, "1500");
        setCellText(table.getRow(13).getCell(6), "学分", true, "1500");
        //小计
        setCellText(table.getRow(14).getCell(0), "小计", true, "4000");
        WordUtils.mergeCellsHorizontal(table, 14, 0, 1);
        //合计
        setCellText(table.getRow(15).getCell(0), "合计", true, "4000");
        WordUtils.mergeCellsHorizontal(table, 15, 0, 1);

        deleteCol(table,0,2,totalCols);
        deleteCol(table,1,4,totalCols);
        deleteCol(table,2,7,totalCols);
        deleteCol(table,10,7,totalCols);
        deleteCol(table,11,2,totalCols);
        deleteCol(table,12,4,totalCols);
        deleteCol(table,13,7,totalCols);
        deleteCol(table,14,7,totalCols);
        return table;
    }

    /**
     * 生成教学训练体系与安排（课程信息表格）
     */
    private void generateCourseArrangements(TrainingPlanModel model, XWPFDocument document) {
        // 一级标题
        WordUtils.createHeading(document, "四、教学训练体系与安排", 1);
        WordUtils.createParagraph(document,"通识课程任选课程安排表见附录1、专业任选课程安排表见附录2、其他课程和实践训练安排如下：",null);
        //二级标题
        WordUtils.createHeading(document, "（一）课程安排", 2);

        // 通识类课程
        if (model.getGeneralEducationCourses() != null && !model.getGeneralEducationCourses().isEmpty()) {
            XWPFTable table = generateCourseTable(document, "1.通识课程教学安排", model.getGeneralEducationCourses());
            WordUtils.createParagraph(document, "", null); // 空行
        }
        // 添加备注
        XWPFParagraph remarkParagraph = document.createParagraph();
        XWPFRun remarkRun = remarkParagraph.createRun();
        remarkRun.setText("备注：B代表必修课程、X代表限选课程、R代表任选课程；S代表考试，C代表考查（下同）。");
        remarkRun.setFontSize(10);

        // 专业大类课程教学安排
        if (model.getMajorCategoryCourseArrangements() != null && !model.getMajorCategoryCourseArrangements().isEmpty()) {
            XWPFTable table = majorCategoryCourseArrangementTable(document, "2.专业大类课程教学安排", model.getMajorCategoryCourseArrangements());
            WordUtils.createParagraph(document, "", null); // 空行
        }

        // 专业方向课程教学安排
        if (model.getMajorDirectionCourseArrangements() != null && !model.getMajorDirectionCourseArrangements().isEmpty()) {
            XWPFTable table = majorDirectionCourseArrangementTable(document, "3.专业方向课程教学安排", model.getMajorDirectionCourseArrangements());
            WordUtils.createParagraph(document, "", null); // 空行
        }
        //二级标题
        WordUtils.createHeading(document, "（二）实践训练安排", 2);
        //todo （二）实践训练安排


    }

    /**
     * 生成课程教学安排表格 - 严格按照TableCreateGenerator中的表头结构
     *
     * 表格结构：
     * - 列数：15列（删除第6列和第11列后实际为13列）
     * - 行数：表头3行 + 模块行 + 小计1行
     *
     * 表头结构（3行）：
     * 行0：课程模块 | 课程名称 | 修读要求 | 考核方式 | 学时安排(跨3列) | 学期安排(跨8列)
     * 行1：[ Module行跨3行 ] | | | | 小计 | 讲授 | 实践 | 第一学年(跨2列) | 第二学年(跨2列) | 第三学年(跨2列) | 第四学年(跨2列)
     * 行2：[ Module行跨3行 ] | | | | | | | 秋 | 春 | 秋 | 春 | 秋 | 春 | 秋 | 春
     *
     * 删除第6列（空列）和第11列（空列），所以实际使用13列
     */
    private XWPFTable generateCourseTable(XWPFDocument document, String title, List<TrainingSchemeCourseModel> courses) {
        // 创建表格标题
        WordUtils.createHeading(document, title, 3);

        // 计算总行数：表头3行 + 数据行 + 小计1行
        int dataRows = courses.size();
        int totalRows = 3 + dataRows + 1;
        int totalCols = 15; // 原始15列，删除第6列和第11列后实际13列

        XWPFTable table = document.createTable(totalRows, totalCols);
        table.setWidthType(TableWidthType.PCT);
        table.setWidth("100%");

        // 生成表头（3行）
        generalEducationCoursesTableHeader(table);


        int dataRowStart = 3;

        Map<String, List<TrainingSchemeCourseModel>> couseMap = courses.stream().collect(Collectors.groupingBy(course -> course.getCourseModeChildrenName()));
        // 处理每一门课程，确定其所属模块
        AtomicInteger dataRow = new AtomicInteger(dataRowStart);
        couseMap.forEach((modelName,courseModels) -> {
            int moduleRowStart = dataRow.get();
            int moduleRowEnd = dataRow.get()+courseModels.size();
            for (int i = 0; i < courseModels.size(); i++) {
                TrainingSchemeCourseModel course = courseModels.get(i);
                // 设置课程数据行
                int dataRowIndex = moduleRowStart + i;
                setCourseDataRow(table, dataRowIndex, course,1);
            }
            XWPFTableRow row = table.getRow(moduleRowStart);
            XWPFTableCell cell1 = row.getCell(0);
            WordUtils.setCellText(cell1, modelName, false, "1134");
            WordUtils.mergeCellsVertical(table, 0, moduleRowStart,  moduleRowEnd - 1);
            dataRow.set(moduleRowEnd);
        });
        // 设置小计行
        int totalRow = totalRows - 1;
        setTotalRow(table, totalRow);

        return table;
    }

    private XWPFTable majorCategoryCourseArrangementTable(XWPFDocument document, String title, List<TrainingSchemeCourseModel> courses) {
        // 创建表格标题
        WordUtils.createHeading(document, title, 3);

        // 计算总行数：表头3行 + 数据行 + 小计1行
        int dataRows = courses.size();
        int totalRows = 3 + dataRows + 1;
        int totalCols = 15; // 原始15列，删除第6列和第11列后实际13列

        XWPFTable table = document.createTable(totalRows, totalCols);
        table.setWidthType(TableWidthType.PCT);
        table.setWidth("100%");

        // 生成表头（3行）
        majorCategoryCourseArrangementTableHeader(table);


        int dataRowStart = 3;

        // 处理每一门课程，确定其所属模块
        for (int i = 0; i < courses.size(); i++) {
            TrainingSchemeCourseModel course = courses.get(i);
            // 设置课程数据行
            int dataRowIndex = dataRowStart + i;
            setCourseDataRow(table, dataRowIndex, course,0);
        }

        // 设置小计行
        int totalRow = totalRows - 1;
        setTotalRow(table, totalRow);

        return table;
    }

    private XWPFTable majorDirectionCourseArrangementTable(XWPFDocument document, String title, List<TrainingSchemeCourseModel> courses) {
        // 创建表格标题
        WordUtils.createHeading(document, title, 3);

        Map<String, List<TrainingSchemeCourseModel>> couseMap = courses.stream().collect(Collectors.groupingBy(course -> course.getMajorName()));

        // 计算总行数：表头3行 + 数据行 + 小计1行
        int dataRows = courses.size();
        int totalRows = 3 + dataRows +couseMap.size();
        int totalCols = 15; // 原始15列，删除第6列和第11列后实际13列

        XWPFTable table = document.createTable(totalRows, totalCols);
        table.setWidthType(TableWidthType.PCT);
        table.setWidth("100%");

        // 生成表头（3行）
        majorDirectionCourseArrangementTableHeader(table);


        int dataRowStart = 3;

        // 处理每一门课程，确定其所属模块
        AtomicInteger dataRow = new AtomicInteger(dataRowStart);
        couseMap.forEach((majorName,courseModels) -> {
            int moduleRowStart = dataRow.get();
            int moduleRowEnd = dataRow.get()+courseModels.size();
            for (int i = 0; i < courseModels.size(); i++) {
                TrainingSchemeCourseModel course = courseModels.get(i);
                // 设置课程数据行
                int dataRowIndex = moduleRowStart + i;
                setCourseDataRow(table, dataRowIndex, course,1);
            }
            XWPFTableRow row = table.getRow(moduleRowStart);
            XWPFTableCell cell1 = row.getCell(0);
            WordUtils.setCellText(cell1, majorName, false, "1134");
            WordUtils.mergeCellsVertical(table, 0, moduleRowStart,  moduleRowEnd - 1);
            // 设置小计行
            setTotalRow(table, moduleRowEnd);
            dataRow.set(moduleRowEnd+1);
        });
        return table;
    }



    /**
     * 填充通识课程表格表头（3行）
     */
    private void generalEducationCoursesTableHeader(XWPFTable table) {
        // ========== 表头第1行（行号0） ==========
        // 列0：课程模块，跨行3行
        setCellText(table.getRow(0).getCell(0), "课程\n模块", true, "1134");
        WordUtils.mergeCellsVertical(table, 0, 0, 2);

        // 列1：课程名称，跨行3行
        setCellText(table.getRow(0).getCell(1), "课程名称", true, "1985");
        WordUtils.mergeCellsVertical(table, 1, 0, 2);

        // 列2：修读要求，跨行3行
        setCellText(table.getRow(0).getCell(2), "修读\n要求", true, "1021");
        WordUtils.mergeCellsVertical(table, 2, 0, 2);

        // 列3：考核方式，跨行3行
        setCellText(table.getRow(0).getCell(3), "考核\n方式", true, "1021");
        WordUtils.mergeCellsVertical(table, 3, 0, 2);

        // 列4-6：学时安排，跨列3列（小计、讲授、实践）
        setCellText(table.getRow(0).getCell(4), "学时安排", true, "2553");
        WordUtils.mergeCellsHorizontal(table, 0, 4, 6);

        // 列7-14：学期安排，跨列8列（4个学年，每个学年2个学期）
        setCellText(table.getRow(0).getCell(5), "学期安排", true, "5440");
        WordUtils.mergeCellsHorizontal(table, 0, 5, 12);

        // ========== 表头第2行（行号1） ==========
        // 列4-6：学时子项
        setCellText(table.getRow(1).getCell(4), "小计", true, "851");
        WordUtils.mergeCellsVertical(table, 4, 1, 2);
        setCellText(table.getRow(1).getCell(5), "讲授", true, "851");
        WordUtils.mergeCellsVertical(table, 5, 1, 2);
        setCellText(table.getRow(1).getCell(6), "实践", true, "851");
        WordUtils.mergeCellsVertical(table, 6, 1, 2);

        // 列7-8：第一学年，跨列2列
        setCellText(table.getRow(1).getCell(7), "第一学年", true, "1360");
        WordUtils.mergeCellsHorizontal(table, 1, 7, 8);
        // 列9-10：第二学年，跨列2列
        setCellText(table.getRow(1).getCell(8), "第二学年", true, "1360");
        WordUtils.mergeCellsHorizontal(table, 1, 8, 9);
        // 列11-12：第三学年，跨列2列
        setCellText(table.getRow(1).getCell(9), "第三学年", true, "1360");
        WordUtils.mergeCellsHorizontal(table, 1, 9, 10);
        // 列13-14：第四学年，跨列2列
        setCellText(table.getRow(1).getCell(10), "第四学年", true, "1360");
        WordUtils.mergeCellsHorizontal(table, 1, 10, 11);

        // ========== 表头第3行（行号2） ==========
        // 列7-14：学期子项（秋、春交替）
        setCellText(table.getRow(2).getCell(7), "秋", true, "680");
        setCellText(table.getRow(2).getCell(8), "春", true, "680");
        setCellText(table.getRow(2).getCell(9), "秋", true, "680");
        setCellText(table.getRow(2).getCell(10), "春", true, "680");
        setCellText(table.getRow(2).getCell(11), "秋", true, "680");
        setCellText(table.getRow(2).getCell(12), "春", true, "680");
        setCellText(table.getRow(2).getCell(13), "秋", true, "680");
        setCellText(table.getRow(2).getCell(14), "春", true, "680");

        // 删除第一行第二行多余的列
        deleteCol(table,0,6,15); // 删除第6列（索引5）
        deleteCol(table,1,11,15);// 删除第11列（索引10）
    }

    /**
     * 专业大类课程教学安排
     * @param table
     */
    private void majorCategoryCourseArrangementTableHeader(XWPFTable table) {
        // ========== 表头第1行（行号0） ==========

        // 列1：课程名称，跨行3行
        setCellText(table.getRow(0).getCell(0), "课程名称", true, "1985");
        WordUtils.mergeCellsVertical(table, 0, 0, 2);

        // 列2：修读要求，跨行3行
        setCellText(table.getRow(0).getCell(1), "修读\n要求", true, "1021");
        WordUtils.mergeCellsVertical(table, 1, 0, 2);

        // 列3：考核方式，跨行3行
        setCellText(table.getRow(0).getCell(2), "考核\n方式", true, "1021");
        WordUtils.mergeCellsVertical(table, 2, 0, 2);

        // 列4-6：学时安排，跨列3列（小计、讲授、实践）
        setCellText(table.getRow(0).getCell(3), "学时安排", true, "2553");
        WordUtils.mergeCellsHorizontal(table, 0, 3, 5);

        // 列7-14：学期安排，跨列8列（4个学年，每个学年2个学期）
        setCellText(table.getRow(0).getCell(4), "学期安排", true, "5440");
        WordUtils.mergeCellsHorizontal(table, 0, 4, 11);

        // ========== 表头第2行（行号1） ==========
        // 列4-6：学时子项
        setCellText(table.getRow(1).getCell(3), "小计", true, "851");
        WordUtils.mergeCellsVertical(table, 3, 1, 2);
        setCellText(table.getRow(1).getCell(4), "讲授", true, "851");
        WordUtils.mergeCellsVertical(table, 4, 1, 2);
        setCellText(table.getRow(1).getCell(5), "实践", true, "851");
        WordUtils.mergeCellsVertical(table, 5, 1, 2);

        // 列7-8：第一学年，跨列2列
        setCellText(table.getRow(1).getCell(6), "第一学年", true, "1360");
        WordUtils.mergeCellsHorizontal(table, 1, 6, 7);
        // 列9-10：第二学年，跨列2列
        setCellText(table.getRow(1).getCell(7), "第二学年", true, "1360");
        WordUtils.mergeCellsHorizontal(table, 1, 7, 8);
        // 列11-12：第三学年，跨列2列
        setCellText(table.getRow(1).getCell(8), "第三学年", true, "1360");
        WordUtils.mergeCellsHorizontal(table, 1, 8, 9);
        // 列13-14：第四学年，跨列2列
        setCellText(table.getRow(1).getCell(9), "第四学年", true, "1360");
        WordUtils.mergeCellsHorizontal(table, 1, 9, 10);

        // ========== 表头第3行（行号2） ==========
        // 列7-14：学期子项（秋、春交替）
        setCellText(table.getRow(2).getCell(6), "秋", true, "680");
        setCellText(table.getRow(2).getCell(7), "春", true, "680");
        setCellText(table.getRow(2).getCell(8), "秋", true, "680");
        setCellText(table.getRow(2).getCell(9), "春", true, "680");
        setCellText(table.getRow(2).getCell(10), "秋", true, "680");
        setCellText(table.getRow(2).getCell(11), "春", true, "680");
        setCellText(table.getRow(2).getCell(12), "秋", true, "680");
        setCellText(table.getRow(2).getCell(13), "春", true, "680");
        // 删除第一行第二行多余的列
        deleteCol(table,0,5,15); // 删除第6列（索引5）
        deleteCol(table,1,10,15);// 删除第11列（索引10）
    }
    /**
     * 专业方向课程教学安排
     * @param table
     */
    private void majorDirectionCourseArrangementTableHeader(XWPFTable table) {
        // ========== 表头第1行（行号0） ==========
        // 列0：课程模块，跨行3行
        setCellText(table.getRow(0).getCell(0), "专业方向", true, "1985");
        WordUtils.mergeCellsVertical(table, 0, 0, 2);

        // 列1：课程名称，跨行3行
        setCellText(table.getRow(0).getCell(1), "课程名称", true, "1985");
        WordUtils.mergeCellsVertical(table, 1, 0, 2);

        // 列2：修读要求，跨行3行
        setCellText(table.getRow(0).getCell(2), "修读\n要求", true, "1021");
        WordUtils.mergeCellsVertical(table, 2, 0, 2);

        // 列3：考核方式，跨行3行
        setCellText(table.getRow(0).getCell(3), "考核\n方式", true, "1021");
        WordUtils.mergeCellsVertical(table, 3, 0, 2);

        // 列4-6：学时安排，跨列3列（小计、讲授、实践）
        setCellText(table.getRow(0).getCell(4), "学时安排", true, "2553");
        WordUtils.mergeCellsHorizontal(table, 0, 4, 6);

        // 列7-14：学期安排，跨列8列（4个学年，每个学年2个学期）
        setCellText(table.getRow(0).getCell(5), "学期安排", true, "5440");
        WordUtils.mergeCellsHorizontal(table, 0, 5, 12);

        // ========== 表头第2行（行号1） ==========
        // 列4-6：学时子项
        setCellText(table.getRow(1).getCell(4), "小计", true, "851");
        WordUtils.mergeCellsVertical(table, 4, 1, 2);
        setCellText(table.getRow(1).getCell(5), "讲授", true, "851");
        WordUtils.mergeCellsVertical(table, 5, 1, 2);
        setCellText(table.getRow(1).getCell(6), "实践", true, "851");
        WordUtils.mergeCellsVertical(table, 6, 1, 2);

        // 列7-8：第一学年，跨列2列
        setCellText(table.getRow(1).getCell(7), "第一学年", true, "1360");
        WordUtils.mergeCellsHorizontal(table, 1, 7, 8);
        // 列9-10：第二学年，跨列2列
        setCellText(table.getRow(1).getCell(8), "第二学年", true, "1360");
        WordUtils.mergeCellsHorizontal(table, 1, 8, 9);
        // 列11-12：第三学年，跨列2列
        setCellText(table.getRow(1).getCell(9), "第三学年", true, "1360");
        WordUtils.mergeCellsHorizontal(table, 1, 9, 10);
        // 列13-14：第四学年，跨列2列
        setCellText(table.getRow(1).getCell(10), "第四学年", true, "1360");
        WordUtils.mergeCellsHorizontal(table, 1, 10, 11);

        // ========== 表头第3行（行号2） ==========
        // 列7-14：学期子项（秋、春交替）
        setCellText(table.getRow(2).getCell(7), "秋", true, "680");
        setCellText(table.getRow(2).getCell(8), "春", true, "680");
        setCellText(table.getRow(2).getCell(9), "秋", true, "680");
        setCellText(table.getRow(2).getCell(10), "春", true, "680");
        setCellText(table.getRow(2).getCell(11), "秋", true, "680");
        setCellText(table.getRow(2).getCell(12), "春", true, "680");
        setCellText(table.getRow(2).getCell(13), "秋", true, "680");
        setCellText(table.getRow(2).getCell(14), "春", true, "680");

        // 删除第一行第二行多余的列
        deleteCol(table,0,6,15); // 删除第6列（索引5）
        deleteCol(table,1,11,15);// 删除第11列（索引10）
    }

    /**
     * 删除指定行列的单元格（用于删除空列）
     */
    private void deleteCol(XWPFTable table, int row, int startCol, int endCol) {
        XWPFTableRow currentRow = table.getRow(row);
        for (int col = endCol; col >= startCol; col--) {
            if (currentRow.getCell(col) != null) {
                currentRow.removeCell(col); // 删除指定列的冗余单元格
            }
        }
    }

    /**
     * 设置表格单元格内容 - 带所有属性
     */
    private void setCellText(XWPFTableCell cell, String text, boolean isBold, String width) {
        // 清空单元格默认段落
        while (cell.getParagraphs() != null && !cell.getParagraphs().isEmpty()) {
            cell.removeParagraph(0);
        }
        XWPFParagraph paragraph = cell.addParagraph();
        // 水平居中
        paragraph.setAlignment(ParagraphAlignment.CENTER);
        // 垂直居中
        cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
        // 设置单元格宽度
        if (width != null && !width.isEmpty()) {
            cell.setWidth(width);
        }
        // 设置文本
        XWPFRun run = paragraph.createRun();
        run.setFontFamily("宋体");
        run.setText(text);
        run.setFontSize(9);
        if (isBold) {
            run.setBold(true);
        }
    }

    /**
     * 获取模块的起始行（相对于数据行）
     */
    private int getModuleRowStart(String moduleName) {
        if ("政治理论".equals(moduleName)) {
            return 0;
        } else if ("军事基础".equals(moduleName)) {
            return 6;
        } else if ("基础科学".equals(moduleName)) {
            return 13;
        } else if ("人文与社会科学".equals(moduleName)) {
            return 25;
        } else if ("人工智能与信息技术".equals(moduleName)) {
            return 28;
        } else {
            return 0;
        }
    }

    /**
     * 获取模块的结束行（相对于数据行）
     */
    private int getModuleRowEnd(String moduleName) {
        if ("政治理论".equals(moduleName)) {
            return 5;
        } else if ("军事基础".equals(moduleName)) {
            return 12;
        } else if ("基础科学".equals(moduleName)) {
            return 18;
        } else if ("人文与社会科学".equals(moduleName)) {
            return 21;
        } else if ("人工智能与信息技术".equals(moduleName)) {
            return 22;
        } else {
            return 0;
        }
    }

    /**
     * 设置课程数据行
     */
    private void setCourseDataRow(XWPFTable table, int rowIndex, TrainingSchemeCourseModel course,int initCell) {
        XWPFTableRow row = table.getRow(rowIndex);

        // 列1：课程名称
        XWPFTableCell cell1 = row.getCell(initCell);
        WordUtils.setCellText(cell1, course.getName(), false, "1985");

        // 列2：修读要求（B=必修，X=限选，R=任选）
        XWPFTableCell cell2 = row.getCell(initCell+1);
        String attrText = getAttrText(course.getCourseAttrName());
        WordUtils.setCellText(cell2, attrText, false, "1021");

        // 列3：考核方式（S=考试，C=考查）
        XWPFTableCell cell3 = row.getCell(initCell+2);
        String assessText = getAssessText(course.getOpenTerm());
        WordUtils.setCellText(cell3, assessText, false, "1021");

        // 列4：小计
        XWPFTableCell cell4 = row.getCell(initCell+3);
        WordUtils.setCellText(cell4, course.getHours() != null ? course.getHours().toString() : "", false, "851");

        // 列5：讲授
        XWPFTableCell cell5 = row.getCell(initCell+4);
        WordUtils.setCellText(cell5, course.getTheoryHours() != null ? course.getTheoryHours().toString() : "", false, "851");

        // 列6：实践
        XWPFTableCell cell6 = row.getCell(initCell+5);
        WordUtils.setCellText(cell6, course.getPracticeHours() != null ? course.getPracticeHours().toString() : "", false, "851");

        // 列7-12：学期安排（根据semesterSchedule和springAutumn判断是否打勾）
        setTermCheckmarks(row, course,initCell+6);
    }

    /**
     * 设置学期检查标记
     * 格式：根据semesterSchedule与springAutumn判断是否要打勾
     * ""","","✔","","","✔","",""（根据semesterSchedule与springAutumn判断是否要打勾）
     */
    private void setTermCheckmarks(XWPFTableRow row, TrainingSchemeCourseModel course,int initCell) {
        String semesterSchedule = course.getSemesterSchedule(); // 如："第一学年第二学期,第三学年第一学期"
        String springAutumn = course.getSpringAutumn(); // 如："秋、春"

        // 删除第6列和第11列后，列索引调整为：
        // 列7：第一学年秋，列8：第一学年春，列9：第二学年秋，列10：第二学年春，列11：第三学年秋，列12：第三学年春
        // 但实际上根据原表结构，列7-14对应8个学期（4个学年*.2学期）
        // 删除第6列和第11列后，列索引变为：7,8,9,10,11,12（对应前6个学期）
        // 由于删除了列，我们需要重新计算

        // 默认值
        String[] termValues = {"", "", "", "", "", "","",""};

        // 如果有semesterSchedule和springAutumn，根据它们判断打勾
        if (semesterSchedule != null && !semesterSchedule.isEmpty() && springAutumn != null && !springAutumn.isEmpty()) {
            // 简化处理：直接根据semesterSchedule判断
            for (int i = 0; i < termValues.length; i++) {
                // 根据semesterSchedule判断是否打勾
                // 0:第一学年秋, 1:第一学年春, 2:第二学年秋, 3:第二学年春, 4:第三学年秋, 5:第三学年春
                if (semesterSchedule.contains("第一学年") && (i == 0 || i == 1)) {
                    if (springAutumn.contains("秋") && i == 0) termValues[i] = "✔";
                    if (springAutumn.contains("春") && i == 1) termValues[i] = "✔";
                } else if (semesterSchedule.contains("第二学年") && (i == 2 || i == 3)) {
                    if (springAutumn.contains("秋") && i == 2) termValues[i] = "✔";
                    if (springAutumn.contains("春") && i == 3) termValues[i] = "✔";
                } else if (semesterSchedule.contains("第三学年") && (i == 4 || i == 5)) {
                    if (springAutumn.contains("秋") && i == 4) termValues[i] = "✔";
                    if (springAutumn.contains("春") && i == 5) termValues[i] = "✔";
                }else if (semesterSchedule.contains("第四学年") && (i == 6 || i == 7)) {
                    if (springAutumn.contains("秋") && i == 6) termValues[i] = "✔";
                    if (springAutumn.contains("春") && i == 7) termValues[i] = "✔";
                }
            }
        }

        // 设置单元格（删除列后：列7-12）
        XWPFTableCell cell7 = row.getCell(initCell);
        WordUtils.setCellText(cell7, termValues[0], false, "680");
        XWPFTableCell cell8 = row.getCell(initCell+1);
        WordUtils.setCellText(cell8, termValues[1], false, "680");
        XWPFTableCell cell9 = row.getCell(initCell+2);
        WordUtils.setCellText(cell9, termValues[2], false, "680");
        XWPFTableCell cell10 = row.getCell(initCell+3);
        WordUtils.setCellText(cell10, termValues[3], false, "680");
        XWPFTableCell cell11 = row.getCell(initCell+4);
        WordUtils.setCellText(cell11, termValues[4], false, "680");
        XWPFTableCell cell12 = row.getCell(initCell+5);
        WordUtils.setCellText(cell12, termValues[5], false, "680");
        XWPFTableCell cell13 = row.getCell(initCell+6);
        WordUtils.setCellText(cell13, termValues[6], false, "680");
        XWPFTableCell cell14 = row.getCell(initCell+7);
        WordUtils.setCellText(cell14, termValues[7], false, "680");
    }

    /**
     * 设置小计行
     */
    private void setTotalRow(XWPFTable table, int rowIndex) {
        XWPFTableRow row = table.getRow(rowIndex);

        // 列0：小计
        XWPFTableCell cell0 = row.getCell(0);
        WordUtils.setCellText(cell0, "小  计", true, "1985");

        // 合并列0-3
        WordUtils.mergeCellsHorizontal(table, rowIndex, 0, 1);
    }

    /**
     * 获取修读要求文本（B=必修，X=限选，R=任选）
     */
    private String getAttrText(String attrName) {
        if (attrName == null) {
            return "";
        }
        // 根据课程属性名称获取对应代码
        if (attrName.contains("必修") || attrName.contains("必修课程")) {
            return "B";
        } else if (attrName.contains("限选") || attrName.contains("限定选修")) {
            return "X";
        } else if (attrName.contains("任选") || attrName.contains("任意选修")) {
            return "R";
        }
        return attrName;
    }

    /**
     * 获取考核方式文本（S=考试，C=考查）
     */
    private String getAssessText(String openTerm) {
        if (openTerm == null) {
            return "";
        }
        // 根据开课学期判断考核方式
        if (openTerm.contains("考试") || openTerm.contains("S")) {
            return "S";
        } else if (openTerm.contains("考查") || openTerm.contains("C")) {
            return "C";
        }
        return openTerm;
    }

    /**
     * 课程模块内部类
     */
    private static class CourseModule {
        String name;
        int startRow;
        int endRow;

        CourseModule(String name, int startRow, int endRow) {
            this.name = name;
            this.startRow = startRow;
            this.endRow = endRow;
        }
    }
}
