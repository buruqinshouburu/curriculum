package com.example.cscy.generator;

import com.example.cscy.entity.scheme.TrainingSchemeCourseVo;
import org.apache.poi.xwpf.usermodel.*;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STMerge;

import java.math.BigInteger;
import java.util.*;

/**
 * 课程表格生成器
 *
 * 功能：
 * 1. 通识类课程：按courseModeChildrenName（课程模块）分组，根据每组课程数量合并行
 * 2. 专业大类课程教学安排：第一列是课程名称
 * 3. 专业方向课程教学安排：按majorName分组，第一列是课程专业，根据每组课程数量合并行
 */
public class CourseTableGenerator {

    // 单元格宽度常量
    private static final String CELL_WIDTH_MODULE = "1200";    // 课程模块列宽
    private static final String CELL_WIDTH_NAME = "2000";      // 课程名称列宽
    private static final String CELL_WIDTH_REQUIRE = "800";    // 修读要求列宽
    private static final String CELL_WIDTH_ASSESS = "800";     // 考核方式列宽
    private static final String CELL_WIDTH_TOTAL = "600";      // 学时-小计列宽
    private static final String CELL_WIDTH_THEORY = "600";     // 学时-讲授列宽
    private static final String CELL_WIDTH_PRACTICE = "600";   // 学时-实践列宽
    private static final String CELL_WIDTH_YEAR = "700";       // 学年列宽
    private static final String CELL_WIDTH_SEMESTER = "600";   // 学期列宽

    /**
     * 创建通识类课程表格
     * 按courseModeChildrenName分组，根据每组课程数量合并行
     *
     * @param document XWPFDocument对象
     * @param courses 课程列表
     * @return XWPFTable表格对象
     */
    public static XWPFTable createGeneralEducationTable(XWPFDocument document, List<TrainingSchemeCourseVo> courses) {
        if (courses == null || courses.isEmpty()) {
            return createEmptyTable(document, 4, 13);
        }

        // 按课程模块分组
        Map<String, List<TrainingSchemeCourseVo>> moduleMap = groupByModule(courses);

        // 计算总行数：表头3行 + 各模块行 + 模块间隔行 + 小计行1行
        int headerRows = 3;
        int subtotalRow = 1;
        int contentRows = 0;
        List<ModuleInfo> moduleInfos = new ArrayList<>();

        for (Map.Entry<String, List<TrainingSchemeCourseVo>> entry : moduleMap.entrySet()) {
            int moduleRowCount = entry.getValue().size();
            contentRows += moduleRowCount;
            moduleInfos.add(new ModuleInfo(entry.getKey(), moduleRowCount));
        }

        int totalRows = headerRows + contentRows + subtotalRow;
        XWPFTable table = createTable(document, totalRows, 13);

        // 填充表头
        fillGeneralEducationHeader(table);

        // 填充课程内容
        int currentRow = headerRows;
        for (ModuleInfo moduleInfo : moduleInfos) {
            // 填充模块名（合并行）
            fillModuleCell(table, currentRow, moduleInfo.moduleName, moduleInfo.rowCount);

            // 填充该模块下的课程
            List<TrainingSchemeCourseVo> moduleCourses = moduleMap.get(moduleInfo.moduleName);
            for (int i = 0; i < moduleInfo.rowCount; i++) {
                TrainingSchemeCourseVo course = moduleCourses.get(i);
                fillCourseRow(table, currentRow + i, course);
            }

            currentRow += moduleInfo.rowCount;
        }

        // 填充小计行
        fillGeneralEducationSubtotal(table, totalRows - 1);

        return table;
    }

    /**
     * 创建专业大类课程教学安排表格
     * 第一列是课程名称
     *
     * @param document XWPFDocument对象
     * @param courses 课程列表
     * @return XWPFTable表格对象
     */
    public static XWPFTable createMajorTypeCourseTable(XWPFDocument document, List<TrainingSchemeCourseVo> courses) {
        if (courses == null || courses.isEmpty()) {
            return createEmptyTable(document, 2, 13);
        }

        int totalRows = courses.size() + 1; // 数据行 + 表头
        XWPFTable table = createTable(document, totalRows, 13);

        // 填充表头
        fillMajorTypeHeader(table);

        // 填充课程数据
        for (int i = 0; i < courses.size(); i++) {
            TrainingSchemeCourseVo course = courses.get(i);
            fillMajorTypeCourseRow(table, i + 1, course);
        }

        return table;
    }

    /**
     * 创建专业方向课程教学安排表格
     * 按majorName分组，第一列是课程专业，根据每组课程数量合并行
     *
     * @param document XWPFDocument对象
     * @param courses 课程列表
     * @return XWPFTable表格对象
     */
    public static XWPFTable createMajorDirectionTable(XWPFDocument document, List<TrainingSchemeCourseVo> courses) {
        if (courses == null || courses.isEmpty()) {
            return createEmptyTable(document, 4, 13);
        }

        // 按专业方向分组
        Map<String, List<TrainingSchemeCourseVo>> majorMap = groupByMajor(courses);

        // 计算总行数：表头3行 + 各专业行 + 专业间隔行 + 小计行1行
        int headerRows = 3;
        int subtotalRow = 1;
        int contentRows = 0;
        List<MajorInfo> majorInfos = new ArrayList<>();

        for (Map.Entry<String, List<TrainingSchemeCourseVo>> entry : majorMap.entrySet()) {
            int majorRowCount = entry.getValue().size();
            contentRows += majorRowCount;
            majorInfos.add(new MajorInfo(entry.getKey(), majorRowCount));
        }

        int totalRows = headerRows + contentRows + subtotalRow;
        XWPFTable table = createTable(document, totalRows, 13);

        // 填充表头
        fillMajorDirectionHeader(table);

        // 填充课程内容
        int currentRow = headerRows;
        for (MajorInfo majorInfo : majorInfos) {
            // 填充专业名（合并行）
            fillMajorNameCell(table, currentRow, majorInfo.majorName, majorInfo.rowCount);

            // 填充该专业下的课程
            List<TrainingSchemeCourseVo> majorCourses = majorMap.get(majorInfo.majorName);
            for (int i = 0; i < majorInfo.rowCount; i++) {
                TrainingSchemeCourseVo course = majorCourses.get(i);
                fillMajorDirectionCourseRow(table, currentRow + i, course);
            }

            currentRow += majorInfo.rowCount;
        }

        // 填充小计行
        fillMajorDirectionSubtotal(table, totalRows - 1);

        return table;
    }

    /**
     * 按课程模块分组
     */
    private static Map<String, List<TrainingSchemeCourseVo>> groupByModule(List<TrainingSchemeCourseVo> courses) {
        Map<String, List<TrainingSchemeCourseVo>> map = new LinkedHashMap<>();
        for (TrainingSchemeCourseVo course : courses) {
            String moduleName = course.getCourseModeChildrenName();
            if (moduleName == null) moduleName = "其他";

            map.computeIfAbsent(moduleName, k -> new ArrayList<>()).add(course);
        }
        return map;
    }

    /**
     * 按专业方向分组
     */
    private static Map<String, List<TrainingSchemeCourseVo>> groupByMajor(List<TrainingSchemeCourseVo> courses) {
        Map<String, List<TrainingSchemeCourseVo>> map = new LinkedHashMap<>();
        for (TrainingSchemeCourseVo course : courses) {
            String majorName = course.getMajorName();
            if (majorName == null) majorName = "其他";

            map.computeIfAbsent(majorName, k -> new ArrayList<>()).add(course);
        }
        return map;
    }

    /**
     * 创建表格
     */
    private static XWPFTable createTable(XWPFDocument document, int rows, int columns) {
        XWPFTable table = document.createTable(rows, columns);
        table.setWidthType(TableWidthType.PCT);
        table.setWidth("100%");
        return table;
    }

    /**
     * 创建空表格
     */
    private static XWPFTable createEmptyTable(XWPFDocument document, int rows, int columns) {
        return createTable(document, rows, columns);
    }

    // ================== 通识类课程表格处理 ==================

    /**
     * 填充通识类课程表头
     */
    private static void fillGeneralEducationHeader(XWPFTable table) {
        // 表头第1行
        // 列0：课程模块，跨行3行
        setCellText(table.getRow(0).getCell(0), "课程模块", true, CELL_WIDTH_MODULE);
        mergeCellsVertical(table, 0, 0, 2);

        // 列1：课程名称，跨行3行
        setCellText(table.getRow(0).getCell(1), "课程名称", true, CELL_WIDTH_NAME);
        mergeCellsVertical(table, 1, 0, 2);

        // 列2：修读要求，跨行3行
        setCellText(table.getRow(0).getCell(2), "修读要求", true, CELL_WIDTH_REQUIRE);
        mergeCellsVertical(table, 2, 0, 2);

        // 列3：考核方式，跨行3行
        setCellText(table.getRow(0).getCell(3), "考核方式", true, CELL_WIDTH_ASSESS);
        mergeCellsVertical(table, 3, 0, 2);

        // 列4-6：学时安排
        setCellText(table.getRow(0).getCell(4), "学时", true, CELL_WIDTH_TOTAL);
        mergeCellsHorizontal(table, 0, 4, 6);
        mergeCellsVertical(table, 4, 0, 2);

        // 列7-12：学期安排
        setCellText(table.getRow(0).getCell(7), "学期安排", true, CELL_WIDTH_SEMESTER);
        mergeCellsHorizontal(table, 0, 7, 12);
        mergeCellsVertical(table, 7, 0, 2);

        // 表头第2行
        // 列4-6：学日子项
        setCellText(table.getRow(1).getCell(4), "小计", true, CELL_WIDTH_TOTAL);
        mergeCellsVertical(table, 4, 1, 2);
        setCellText(table.getRow(1).getCell(5), "讲授", true, CELL_WIDTH_THEORY);
        mergeCellsVertical(table, 5, 1, 2);
        setCellText(table.getRow(1).getCell(6), "实践", true, CELL_WIDTH_PRACTICE);
        mergeCellsVertical(table, 6, 1, 2);

        // 列7-8：第一学年
        setCellText(table.getRow(1).getCell(7), "第一学年", true, CELL_WIDTH_YEAR);
        mergeCellsHorizontal(table, 1, 7, 8);
        // 列9-10：第二学年
        setCellText(table.getRow(1).getCell(8), "第二学年", true, CELL_WIDTH_YEAR);
        mergeCellsHorizontal(table, 1, 8, 9);
        // 列11-12：第三学年
        setCellText(table.getRow(1).getCell(9), "第三学年", true, CELL_WIDTH_YEAR);
        mergeCellsHorizontal(table, 1, 9, 10);
        // 列13-14：第四学年
        setCellText(table.getRow(1).getCell(10), "第四学年", true, CELL_WIDTH_YEAR);
        mergeCellsHorizontal(table, 1, 10, 11);

        // 表头第3行
        setCellText(table.getRow(2).getCell(7), "秋", true, CELL_WIDTH_SEMESTER);
        setCellText(table.getRow(2).getCell(8), "春", true, CELL_WIDTH_SEMESTER);
        setCellText(table.getRow(2).getCell(9), "秋", true, CELL_WIDTH_SEMESTER);
        setCellText(table.getRow(2).getCell(10), "春", true, CELL_WIDTH_SEMESTER);
        setCellText(table.getRow(2).getCell(11), "秋", true, CELL_WIDTH_SEMESTER);
        setCellText(table.getRow(2).getCell(12), "春", true, CELL_WIDTH_SEMESTER);
    }

    /**
     * 填充模块单元格（合并行）
     */
    private static void fillModuleCell(XWPFTable table, int row, String moduleName, int mergeCount) {
        setCellText(table.getRow(row).getCell(0), moduleName, true, CELL_WIDTH_MODULE);
        mergeCellsVertical(table, 0, row, row + mergeCount - 1);
    }

    /**
     * 填充课程行（通识类）
     */
    private static void fillCourseRow(XWPFTable table, int row, TrainingSchemeCourseVo course) {
        // 课程名称（列1）
        setCellText(table.getRow(row).getCell(1), course.getName(), false, CELL_WIDTH_NAME);

        // 修读要求（列2）
        setCellText(table.getRow(row).getCell(2), course.getCourseAttrName(), false, CELL_WIDTH_REQUIRE);

        // 考核方式（列3）
        setCellText(table.getRow(row).getCell(3), course.getCourseTypeName(), false, CELL_WIDTH_ASSESS);

        // 学时（列4-6）
        setCellText(table.getRow(row).getCell(4), course.getHours() != null ? course.getHours().toString() : "", false, CELL_WIDTH_TOTAL);
        setCellText(table.getRow(row).getCell(5), course.getTheoryHours() != null ? course.getTheoryHours().toString() : "", false, CELL_WIDTH_THEORY);
        setCellText(table.getRow(row).getCell(6), course.getPracticeHours() != null ? course.getPracticeHours().toString() : "", false, CELL_WIDTH_PRACTICE);

        // 学期安排（列7-12）
        setCellText(table.getRow(row).getCell(7), course.getSemesterSchedule(), false, CELL_WIDTH_YEAR);
    }

    /**
     * 填充通识类课程小计行
     */
    private static void fillGeneralEducationSubtotal(XWPFTable table, int row) {
        // 小计行跨列4列（列0-3）
        setCellText(table.getRow(row).getCell(0), "小计", true, CELL_WIDTH_MODULE);
        mergeCellsHorizontal(table, row, 0, 3);

        // 学时小计
        setCellText(table.getRow(row).getCell(4), "", false, CELL_WIDTH_TOTAL);
        setCellText(table.getRow(row).getCell(5), "", false, CELL_WIDTH_THEORY);
        setCellText(table.getRow(row).getCell(6), "", false, CELL_WIDTH_PRACTICE);
    }

    // ================== 专业大类课程表格处理 ==================

    /**
     * 填充专业大类课程表头
     */
    private static void fillMajorTypeHeader(XWPFTable table) {
        // 行0：表头第1行
        // 课程名称
        setCellText(table.getRow(0).getCell(0), "课程名称", true, CELL_WIDTH_NAME);
        mergeCellsVertical(table, 0, 0, 1);

        // 修读要求
        setCellText(table.getRow(0).getCell(1), "修读要求", true, CELL_WIDTH_REQUIRE);
        mergeCellsVertical(table, 1, 0, 1);

        // 考核方式
        setCellText(table.getRow(0).getCell(2), "考核方式", true, CELL_WIDTH_ASSESS);
        mergeCellsVertical(table, 2, 0, 1);

        // 学时
        setCellText(table.getRow(0).getCell(3), "学时", true, CELL_WIDTH_TOTAL);
        mergeCellsHorizontal(table, 0, 3, 5);
        mergeCellsVertical(table, 3, 0, 1);

        // 学期安排
        setCellText(table.getRow(0).getCell(6), "学期安排", true, CELL_WIDTH_SEMESTER);
        mergeCellsHorizontal(table, 0, 6, 12);
        mergeCellsVertical(table, 6, 0, 1);

        // 行1：表头第2行
        setCellText(table.getRow(1).getCell(3), "小计", true, CELL_WIDTH_TOTAL);
        setCellText(table.getRow(1).getCell(4), "讲授", true, CELL_WIDTH_THEORY);
        setCellText(table.getRow(1).getCell(5), "实践", true, CELL_WIDTH_PRACTICE);

        setCellText(table.getRow(1).getCell(6), "第一学年", true, CELL_WIDTH_YEAR);
        mergeCellsHorizontal(table, 1, 6, 7);
        setCellText(table.getRow(1).getCell(7), "第二学年", true, CELL_WIDTH_YEAR);
        mergeCellsHorizontal(table, 1, 7, 8);
        setCellText(table.getRow(1).getCell(8), "第三学年", true, CELL_WIDTH_YEAR);
        mergeCellsHorizontal(table, 1, 8, 9);
        setCellText(table.getRow(1).getCell(9), "第四学年", true, CELL_WIDTH_YEAR);
        mergeCellsHorizontal(table, 1, 9, 10);

        // 行2：表头第3行（学期）
        setCellText(table.getRow(2).getCell(6), "秋", true, CELL_WIDTH_SEMESTER);
        setCellText(table.getRow(2).getCell(7), "春", true, CELL_WIDTH_SEMESTER);
        setCellText(table.getRow(2).getCell(8), "秋", true, CELL_WIDTH_SEMESTER);
        setCellText(table.getRow(2).getCell(9), "春", true, CELL_WIDTH_SEMESTER);
        setCellText(table.getRow(2).getCell(10), "秋", true, CELL_WIDTH_SEMESTER);
        setCellText(table.getRow(2).getCell(11), "春", true, CELL_WIDTH_SEMESTER);
        setCellText(table.getRow(2).getCell(12), "学期", true, CELL_WIDTH_SEMESTER);
    }

    /**
     * 填充专业大类课程行
     */
    private static void fillMajorTypeCourseRow(XWPFTable table, int row, TrainingSchemeCourseVo course) {
        // 课程名称
        setCellText(table.getRow(row).getCell(0), course.getName(), false, CELL_WIDTH_NAME);

        // 修读要求
        setCellText(table.getRow(row).getCell(1), course.getCourseAttrName(), false, CELL_WIDTH_REQUIRE);

        // 考核方式
        setCellText(table.getRow(row).getCell(2), course.getCourseTypeName(), false, CELL_WIDTH_ASSESS);

        // 学时
        setCellText(table.getRow(row).getCell(3), course.getHours() != null ? course.getHours().toString() : "", false, CELL_WIDTH_TOTAL);
        setCellText(table.getRow(row).getCell(4), course.getTheoryHours() != null ? course.getTheoryHours().toString() : "", false, CELL_WIDTH_THEORY);
        setCellText(table.getRow(row).getCell(5), course.getPracticeHours() != null ? course.getPracticeHours().toString() : "", false, CELL_WIDTH_PRACTICE);

        // 学期安排
        setCellText(table.getRow(row).getCell(6), course.getSemesterSchedule(), false, CELL_WIDTH_YEAR);
    }

    // ================== 专业方向课程表格处理 ==================

    /**
     * 填充专业方向课程表头
     */
    private static void fillMajorDirectionHeader(XWPFTable table) {
        // 表头第1行
        // 列0：课程专业，跨行3行
        setCellText(table.getRow(0).getCell(0), "课程专业", true, CELL_WIDTH_NAME);
        mergeCellsVertical(table, 0, 0, 2);

        // 列1：课程名称，跨行3行
        setCellText(table.getRow(0).getCell(1), "课程名称", true, CELL_WIDTH_NAME);
        mergeCellsVertical(table, 1, 0, 2);

        // 列2：修读要求，跨行3行
        setCellText(table.getRow(0).getCell(2), "修读要求", true, CELL_WIDTH_REQUIRE);
        mergeCellsVertical(table, 2, 0, 2);

        // 列3：考核方式，跨行3行
        setCellText(table.getRow(0).getCell(3), "考核方式", true, CELL_WIDTH_ASSESS);
        mergeCellsVertical(table, 3, 0, 2);

        // 列4-6：学时安排
        setCellText(table.getRow(0).getCell(4), "学时", true, CELL_WIDTH_TOTAL);
        mergeCellsHorizontal(table, 0, 4, 6);
        mergeCellsVertical(table, 4, 0, 2);

        // 列7-12：学期安排
        setCellText(table.getRow(0).getCell(7), "学期安排", true, CELL_WIDTH_SEMESTER);
        mergeCellsHorizontal(table, 0, 7, 12);
        mergeCellsVertical(table, 7, 0, 2);

        // 表头第2行
        setCellText(table.getRow(1).getCell(4), "小计", true, CELL_WIDTH_TOTAL);
        mergeCellsVertical(table, 4, 1, 2);
        setCellText(table.getRow(1).getCell(5), "讲授", true, CELL_WIDTH_THEORY);
        mergeCellsVertical(table, 5, 1, 2);
        setCellText(table.getRow(1).getCell(6), "实践", true, CELL_WIDTH_PRACTICE);
        mergeCellsVertical(table, 6, 1, 2);

        setCellText(table.getRow(1).getCell(7), "第一学年", true, CELL_WIDTH_YEAR);
        mergeCellsHorizontal(table, 1, 7, 8);
        setCellText(table.getRow(1).getCell(8), "第二学年", true, CELL_WIDTH_YEAR);
        mergeCellsHorizontal(table, 1, 8, 9);
        setCellText(table.getRow(1).getCell(9), "第三学年", true, CELL_WIDTH_YEAR);
        mergeCellsHorizontal(table, 1, 9, 10);
        setCellText(table.getRow(1).getCell(10), "第四学年", true, CELL_WIDTH_YEAR);
        mergeCellsHorizontal(table, 1, 10, 11);

        // 表头第3行
        setCellText(table.getRow(2).getCell(7), "秋", true, CELL_WIDTH_SEMESTER);
        setCellText(table.getRow(2).getCell(8), "春", true, CELL_WIDTH_SEMESTER);
        setCellText(table.getRow(2).getCell(9), "秋", true, CELL_WIDTH_SEMESTER);
        setCellText(table.getRow(2).getCell(10), "春", true, CELL_WIDTH_SEMESTER);
        setCellText(table.getRow(2).getCell(11), "秋", true, CELL_WIDTH_SEMESTER);
        setCellText(table.getRow(2).getCell(12), "春", true, CELL_WIDTH_SEMESTER);
    }

    /**
     * 填充专业名单元格（合并行）
     */
    private static void fillMajorNameCell(XWPFTable table, int row, String majorName, int mergeCount) {
        setCellText(table.getRow(row).getCell(0), majorName, true, CELL_WIDTH_NAME);
        mergeCellsVertical(table, 0, row, row + mergeCount - 1);
    }

    /**
     * 填充专业方向课程行
     */
    private static void fillMajorDirectionCourseRow(XWPFTable table, int row, TrainingSchemeCourseVo course) {
        // 课程名称（列1）
        setCellText(table.getRow(row).getCell(1), course.getName(), false, CELL_WIDTH_NAME);

        // 修读要求（列2）
        setCellText(table.getRow(row).getCell(2), course.getCourseAttrName(), false, CELL_WIDTH_REQUIRE);

        // 考核方式（列3）
        setCellText(table.getRow(row).getCell(3), course.getCourseTypeName(), false, CELL_WIDTH_ASSESS);

        // 学时（列4-6）
        setCellText(table.getRow(row).getCell(4), course.getHours() != null ? course.getHours().toString() : "", false, CELL_WIDTH_TOTAL);
        setCellText(table.getRow(row).getCell(5), course.getTheoryHours() != null ? course.getTheoryHours().toString() : "", false, CELL_WIDTH_THEORY);
        setCellText(table.getRow(row).getCell(6), course.getPracticeHours() != null ? course.getPracticeHours().toString() : "", false, CELL_WIDTH_PRACTICE);

        // 学期安排（列7-12）
        setCellText(table.getRow(row).getCell(7), course.getSemesterSchedule(), false, CELL_WIDTH_YEAR);
    }

    /**
     * 填充专业方向课程小计行
     */
    private static void fillMajorDirectionSubtotal(XWPFTable table, int row) {
        // 小计行跨列4列（列0-3）
        setCellText(table.getRow(row).getCell(0), "小计", true, CELL_WIDTH_NAME);
        mergeCellsHorizontal(table, row, 0, 3);

        // 学时小计
        setCellText(table.getRow(row).getCell(4), "", false, CELL_WIDTH_TOTAL);
        setCellText(table.getRow(row).getCell(5), "", false, CELL_WIDTH_THEORY);
        setCellText(table.getRow(row).getCell(6), "", false, CELL_WIDTH_PRACTICE);
    }

    // ================== 工具方法 ==================

    /**
     * 设置单元格内容
     */
    private static void setCellText(XWPFTableCell cell, String text, boolean isBold, String width) {
        // 清空单元格默认段落
        while (cell.getParagraphs() != null && !cell.getParagraphs().isEmpty()) {
            cell.removeParagraph(0);
        }
        XWPFParagraph paragraph = cell.addParagraph();
        paragraph.setAlignment(ParagraphAlignment.CENTER);
        cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);

        if (width != null && !width.isEmpty()) {
            cell.setWidth(width);
        }

        XWPFRun run = paragraph.createRun();
        run.setFontFamily("宋体");
        run.setText(text);
        run.setFontSize(10);
        if (isBold) {
            run.setBold(true);
        }
    }

    /**
     * 合并表格单元格（水平合并）
     */
    public static void mergeCellsHorizontal(XWPFTable table, int row, int fromCol, int toCol) {
        for (int cellIndex = fromCol; cellIndex <= toCol; cellIndex++) {
            XWPFTableCell cell = table.getRow(row).getCell(cellIndex);
            if (cellIndex == fromCol) {
                cell.getCTTc().addNewTcPr().addNewGridSpan().setVal(BigInteger.valueOf(toCol - fromCol + 1));
            } else {
                cell.getCTTc().addNewTcPr().addNewHMerge().setVal(STMerge.CONTINUE);
            }
        }
    }

    /**
     * 合并表格单元格（垂直合并）
     */
    public static void mergeCellsVertical(XWPFTable table, int col, int fromRow, int toRow) {
        for (int rowIndex = fromRow; rowIndex <= toRow; rowIndex++) {
            XWPFTableCell cell = table.getRow(rowIndex).getCell(col);
            if (rowIndex == fromRow) {
                cell.getCTTc().addNewTcPr().addNewVMerge().setVal(STMerge.RESTART);
            } else {
                cell.getCTTc().addNewTcPr().addNewVMerge().setVal(STMerge.CONTINUE);
            }
        }
    }

    // ================== 内部类 ==================

    /**
     * 模块信息
     */
    private static class ModuleInfo {
        String moduleName;
        int rowCount;

        ModuleInfo(String moduleName, int rowCount) {
            this.moduleName = moduleName;
            this.rowCount = rowCount;
        }
    }

    /**
     * 专业信息
     */
    private static class MajorInfo {
        String majorName;
        int rowCount;

        MajorInfo(String majorName, int rowCount) {
            this.majorName = majorName;
            this.rowCount = rowCount;
        }
    }
}
