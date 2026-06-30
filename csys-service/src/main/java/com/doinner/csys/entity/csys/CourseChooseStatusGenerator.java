package com.doinner.csys.entity.csys;

import com.doinner.csys.entity.csys.model.CourseChooseStatusModel;
import com.doinner.csys.entity.csys.model.CourseChooseStatusModel.CourseSelectionRow;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

/**
 * 课程被选用情况表生成器
 * <p>
 * 基于 classpath 下的模板 template/course/courseChooseStatus.xlsx 填充课程被选用情况数据。
 * <p>
 * 模板结构(0索引)：
 * <ul>
 *   <li>行0：标题"课程被选用情况表"(A1:M1合并)</li>
 *   <li>行1-3：表头(课程名称/课程编号/选用单位/选用专业类/选用专业 + 学期安排8列)</li>
 *   <li>行4+：数据区，每门课程合并课程名称/课程编号，每个选用组合一行，学期列填写该学期学时(讲授+实践)</li>
 * </ul>
 *
 * @author doinner
 */
public class CourseChooseStatusGenerator {

    private static final String TEMPLATE_PATH = "/template/course/courseChooseStatus.xlsx";

    /** 数据起始行(0索引)，前4行为标题+表头 */
    private static final int DATA_START_ROW = 4;

    /** 列索引：0课程名称 1课程编号 2选用单位 3选用专业类 4选用专业 5-12 学期安排8列 */
    private static final int COL_COURSE_NAME = 0;
    private static final int COL_COURSE_CODE = 1;
    private static final int COL_SELECT_UNIT = 2;
    private static final int COL_SELECT_MAJOR_CATEGORY = 3;
    private static final int COL_SELECT_MAJOR = 4;
    private static final int COL_TERM_START = 5;
    private static final int TERM_COUNT = 8;

    /**
     * 生成工作簿
     *
     * @param models 课程被选用情况模型集合
     * @return 填充后的工作簿
     * @throws IOException 模板读取异常
     */
    public XSSFWorkbook generate(List<CourseChooseStatusModel> models) throws IOException {
        try (InputStream templateIn = getClass().getResourceAsStream(TEMPLATE_PATH)) {
            if (templateIn == null) {
                throw new IOException("未找到课程被选用情况表模板: " + TEMPLATE_PATH);
            }
            XSSFWorkbook workbook = new XSSFWorkbook(templateIn);
            XSSFSheet sheet = workbook.getSheetAt(0);
            fillSheet(sheet, models);
            return workbook;
        }
    }

    /**
     * 生成并写入输出流
     */
    public void generate(List<CourseChooseStatusModel> models, OutputStream out) throws IOException {
        XSSFWorkbook workbook = generate(models);
        try {
            workbook.write(out);
        } finally {
            workbook.close();
        }
    }

    private void fillSheet(XSSFSheet sheet, List<CourseChooseStatusModel> models) {
        // 捕获模板中样例数据单元格样式，用于数据区复用(保留边框/对齐)
        CellStyle dataStyle = captureDataStyle(sheet);

        // 清除模板样例数据区(行4起)及其合并区域，仅保留标题与表头
        clearSampleData(sheet);

        int currentRow = DATA_START_ROW;
        if (models == null || models.isEmpty()) {
            return;
        }
        for (CourseChooseStatusModel model : models) {
            List<CourseSelectionRow> rows = model.getRows();
            int n = (rows == null || rows.isEmpty()) ? 1 : rows.size();
            int startRow = currentRow;
            int endRow = currentRow + n - 1;

            for (int i = 0; i < n; i++) {
                int rowIndex = startRow + i;
                Row row = sheet.createRow(rowIndex);
                CourseSelectionRow selectionRow = (rows == null || rows.isEmpty()) ? null : rows.get(i);

                // 课程名称/编号只在合并区顶部填写
                createCell(row, COL_COURSE_NAME, dataStyle, i == 0 ? model.getCourseName() : "");
                createCell(row, COL_COURSE_CODE, dataStyle, i == 0 ? model.getCourseCode() : "");

                createCell(row, COL_SELECT_UNIT, dataStyle, selectionRow == null ? "" : selectionRow.getSelectUnit());
                createCell(row, COL_SELECT_MAJOR_CATEGORY, dataStyle, selectionRow == null ? "" : selectionRow.getSelectMajorCategory());
                createCell(row, COL_SELECT_MAJOR, dataStyle, selectionRow == null ? "" : selectionRow.getSelectMajor());

                Double[] termHours = selectionRow == null ? null : selectionRow.getTermHours();
                for (int t = 0; t < TERM_COUNT; t++) {
                    Double hours = (termHours != null && t < termHours.length) ? termHours[t] : null;
                    createCell(row, COL_TERM_START + t, dataStyle, formatHours(hours));
                }
            }

            // 合并课程名称、课程编号列
            if (n > 1) {
                sheet.addMergedRegion(new CellRangeAddress(startRow, endRow, COL_COURSE_NAME, COL_COURSE_NAME));
                sheet.addMergedRegion(new CellRangeAddress(startRow, endRow, COL_COURSE_CODE, COL_COURSE_CODE));
            }
            currentRow = endRow + 1;
        }
    }

    /**
     * 捕获样例数据行(行4)的单元格样式，用于数据区复用
     */
    private CellStyle captureDataStyle(XSSFSheet sheet) {
        Row sampleRow = sheet.getRow(DATA_START_ROW);
        if (sampleRow != null) {
            Cell sampleCell = sampleRow.getCell(COL_SELECT_UNIT);
            if (sampleCell != null) {
                return sampleCell.getCellStyle();
            }
        }
        return sheet.getWorkbook().createCellStyle();
    }

    /**
     * 清除模板样例数据区：移除行4及之后的合并区域与行内容
     */
    private void clearSampleData(XSSFSheet sheet) {
        // 移除数据区的合并区域(课程名称/课程编号的纵向合并)
        for (int i = sheet.getNumMergedRegions() - 1; i >= 0; i--) {
            CellRangeAddress range = sheet.getMergedRegion(i);
            if (range.getFirstRow() >= DATA_START_ROW) {
                sheet.removeMergedRegion(i);
            }
        }
        // 移除样例数据行
        int lastRow = sheet.getLastRowNum();
        for (int r = lastRow; r >= DATA_START_ROW; r--) {
            Row row = sheet.getRow(r);
            if (row != null) {
                sheet.removeRow(row);
            }
        }
    }

    private void createCell(Row row, int col, CellStyle style, String value) {
        Cell cell = row.createCell(col);
        cell.setCellStyle(style);
        if (value != null) {
            cell.setCellValue(value);
        }
    }

    /**
     * 格式化学时：整数显示为整型，否则去尾零
     */
    private String formatHours(Double hours) {
        if (hours == null || hours == 0.0) {
            return "";
        }
        double v = hours;
        if (v == Math.rint(v) && !Double.isInfinite(v)) {
            return String.valueOf((long) v);
        }
        return new java.math.BigDecimal(v).stripTrailingZeros().toPlainString();
    }
}
