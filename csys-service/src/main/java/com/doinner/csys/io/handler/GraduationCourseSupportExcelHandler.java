package com.doinner.csys.io.handler;

import com.doinner.csys.domain.vo.GraduationCourseSupportVo;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.util.List;

/**
 * 毕业要求与课程支撑矩阵 Excel 导出处理器。
 * <p>
 * 布局参照 毕业要求与课程支撑矩阵.xlsx 模板：
 * <pre>
 *  行1: A1:末列  合并 -> 「毕业要求与课程支撑矩阵」(标题)
 *  行2: A2:C3   合并 -> 「毕业要求」; D2:末列2 合并 -> 「支撑课程」
 *  行3: D3.. = 课程1, 课程2, ..., 课程N (N = 最大课程数)
 *  行4起: A 列=根(知识/能力/素质, 按根合并), B 列=一级指标(按一级合并), C 列=叶子名, D~=课程名
 * </pre>
 * 为保证合并单元格也有完整边框，先对所用区域全部单元格设置样式，再写值，最后合并。
 *
 * @author doinner
 */
public class GraduationCourseSupportExcelHandler {

    /** 「毕业要求」固定占 3 列(A/B/C) */
    private static final int REQUIREMENT_COL_COUNT = 3;

    private final GraduationCourseSupportVo data;

    public GraduationCourseSupportExcelHandler(GraduationCourseSupportVo data) {
        this.data = data;
    }

    public XSSFWorkbook create() {
        XSSFWorkbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("毕业要求与课程支撑矩阵");

        XSSFCellStyle style = buildStyle(workbook, false);
        XSSFCellStyle titleStyle = buildStyle(workbook, true);

        int courseColCount = data.getMaxCourseCount() == null ? 0 : Math.max(data.getMaxCourseCount(), 0);
        int totalCols = Math.max(REQUIREMENT_COL_COUNT + courseColCount, REQUIREMENT_COL_COUNT);
        int lastColIndex = totalCols - 1;

        // 统计数据行数(叶子数)
        int dataRowCount = countRequirements();
        int totalRows = 3 + dataRowCount; // 3 行表头 + 数据行

        // 1. 预建所有单元格并套用基础样式(含边框)，保证合并区域也有边框
        for (int r = 0; r < totalRows; r++) {
            Row row = sheet.createRow(r);
            for (int c = 0; c < totalCols; c++) {
                Cell cell = row.createCell(c);
                cell.setCellStyle(style);
            }
        }

        // 2. 行1：标题(合并)
        setCellValue(sheet, 0, 0, "毕业要求与课程支撑矩阵", titleStyle);
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, lastColIndex));

        // 3. 行2-3：「毕业要求」(A2:C3) / 「支撑课程」(D2:末列2) / 课程1..N
        setCellValue(sheet, 1, 0, "毕业要求", style);
        sheet.addMergedRegion(new CellRangeAddress(1, 2, 0, REQUIREMENT_COL_COUNT - 1));
        if (courseColCount > 0) {
            setCellValue(sheet, 1, REQUIREMENT_COL_COUNT, "支撑课程", style);
            sheet.addMergedRegion(new CellRangeAddress(1, 1, REQUIREMENT_COL_COUNT, lastColIndex));
            for (int i = 0; i < courseColCount; i++) {
                setCellValue(sheet, 2, REQUIREMENT_COL_COUNT + i, "课程" + (i + 1), style);
            }
        }

        // 4. 数据行(从行4起，索引3)
        int rowIdx = 3;
        if (data.getGroups() != null) {
            for (GraduationCourseSupportVo.SupportGroupVo group : data.getGroups()) {
                int groupStartRow = rowIdx;
                for (GraduationCourseSupportVo.SupportFirstLevelVo first : group.getFirstLevels()) {
                    int firstStartRow = rowIdx;
                    for (GraduationCourseSupportVo.SupportRequirementVo req : first.getRequirements()) {
                        // C 列：叶子名
                        setCellValue(sheet, rowIdx, 2, req.getName(), style);
                        // D~：课程名
                        List<GraduationCourseSupportVo.SupportCourseVo> courses = req.getCourses();
                        if (CollectionUtils.isNotEmpty(courses)) {
                            for (int i = 0; i < courses.size() && i < courseColCount; i++) {
                                setCellValue(sheet, rowIdx, REQUIREMENT_COL_COUNT + i,
                                        courses.get(i).getName(), style);
                            }
                        }
                        rowIdx++;
                    }
                    // B 列：一级指标合并
                    int firstEndRow = rowIdx - 1;
                    if (firstEndRow >= firstStartRow) {
                        setCellValue(sheet, firstStartRow, 1, first.getName(), style);
                        if (firstEndRow > firstStartRow) {
                            sheet.addMergedRegion(new CellRangeAddress(firstStartRow, firstEndRow, 1, 1));
                        }
                    }
                }
                // A 列：根(知识/能力/素质)合并
                int groupEndRow = rowIdx - 1;
                if (groupEndRow >= groupStartRow) {
                    setCellValue(sheet, groupStartRow, 0, group.getRootName(), style);
                    if (groupEndRow > groupStartRow) {
                        sheet.addMergedRegion(new CellRangeAddress(groupStartRow, groupEndRow, 0, 0));
                    }
                }
            }
        }

        // 5. 列宽：参照模板 B=18, C=34
        sheet.setColumnWidth(0, 6 * 256);
        sheet.setColumnWidth(1, 18 * 256);
        sheet.setColumnWidth(2, 34 * 256);
        for (int i = 0; i < courseColCount; i++) {
            sheet.setColumnWidth(REQUIREMENT_COL_COUNT + i, 16 * 256);
        }

        return workbook;
    }

    private int countRequirements() {
        int count = 0;
        if (data == null || data.getGroups() == null) {
            return 0;
        }
        for (GraduationCourseSupportVo.SupportGroupVo group : data.getGroups()) {
            if (group.getFirstLevels() == null) {
                continue;
            }
            for (GraduationCourseSupportVo.SupportFirstLevelVo first : group.getFirstLevels()) {
                if (first.getRequirements() != null) {
                    count += first.getRequirements().size();
                }
            }
        }
        return count;
    }

    private XSSFCellStyle buildStyle(XSSFWorkbook workbook, boolean bold) {
        XSSFCellStyle style = workbook.createCellStyle();
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setWrapText(true);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        XSSFFont font = workbook.createFont();
        font.setFontName("等线");
        font.setFontHeightInPoints((short) 11);
        font.setBold(bold);
        style.setFont(font);
        return style;
    }

    private void setCellValue(Sheet sheet, int rowIdx, int colIdx, String value, CellStyle style) {
        Row row = sheet.getRow(rowIdx);
        if (row == null) {
            row = sheet.createRow(rowIdx);
        }
        Cell cell = row.getCell(colIdx);
        if (cell == null) {
            cell = row.createCell(colIdx);
        }
        cell.setCellValue(StringUtils.isBlank(value) ? "" : value);
        cell.setCellStyle(style);
    }
}
