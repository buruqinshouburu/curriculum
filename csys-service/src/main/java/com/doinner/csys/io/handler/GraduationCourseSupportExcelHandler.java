package com.doinner.csys.io.handler;

import com.doinner.csys.domain.vo.GraduationCourseSupportVo;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.util.List;

/**
 * 毕业要求与课程支撑矩阵 Excel 导出。
 *
 * 布局(每行一条具体毕业要求)：
 * 毕业要求类型 | 一级指标 | 具体毕业要求 | 支撑课程1 | 支撑课程2 | ...
 * 相同类型、相同一级指标的单元格纵向合并。
 */
public class GraduationCourseSupportExcelHandler {

    private final GraduationCourseSupportVo vo;

    private XSSFWorkbook workBook;

    private Sheet sheet;

    private XSSFCellStyle cellStyle;

    /** 支撑课程列数(至少 1 列) */
    private int courseColumnCount;

    public GraduationCourseSupportExcelHandler(GraduationCourseSupportVo vo) {
        this.vo = vo;
    }

    public XSSFWorkbook create() {
        init();
        writeHeader();
        writeBody();
        return this.workBook;
    }

    private void init() {
        workBook = new XSSFWorkbook();
        sheet = workBook.createSheet("毕业要求与课程支撑矩阵");
        cellStyle = workBook.createCellStyle();
        cellStyle.setWrapText(true);
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        cellStyle.setAlignment(HorizontalAlignment.CENTER);
        this.courseColumnCount = vo != null && vo.getMaxCourseCount() > 0 ? vo.getMaxCourseCount() : 1;
    }

    private void writeHeader() {
        Row row = getRow(0);
        setCell(row, 0, "毕业要求类型");
        setCell(row, 1, "一级指标");
        setCell(row, 2, "具体毕业要求");
        for (int i = 0; i < courseColumnCount; i++) {
            setCell(row, 3 + i, "支撑课程" + (i + 1));
        }
    }

    private void writeBody() {
        if (vo == null || ObjectUtils.isEmpty(vo.getGroups())) {
            return;
        }
        int currentRow = 1;
        for (GraduationCourseSupportVo.SupportGroupVo group : vo.getGroups()) {
            int groupStart = currentRow;
            List<GraduationCourseSupportVo.SupportFirstLevelVo> firstLevels = group.getFirstLevels();
            if (ObjectUtils.isEmpty(firstLevels)) {
                Row row = getRow(currentRow);
                setCell(row, 0, resolveGroupName(group));
                currentRow++;
                continue;
            }
            for (GraduationCourseSupportVo.SupportFirstLevelVo first : firstLevels) {
                int firstStart = currentRow;
                List<GraduationCourseSupportVo.SupportRequirementVo> requirements = first.getRequirements();
                if (ObjectUtils.isEmpty(requirements)) {
                    Row row = getRow(currentRow);
                    setCell(row, 1, first.getName());
                    currentRow++;
                } else {
                    for (GraduationCourseSupportVo.SupportRequirementVo req : requirements) {
                        Row row = getRow(currentRow);
                        setCell(row, 2, req.getName());
                        List<GraduationCourseSupportVo.SupportCourseVo> courses = req.getCourses();
                        if (ObjectUtils.isNotEmpty(courses)) {
                            for (int i = 0; i < courses.size(); i++) {
                                setCell(row, 3 + i, courses.get(i).getName());
                            }
                        }
                        currentRow++;
                    }
                    // 合并一级指标单元格
                    mergeVertical(firstStart, currentRow - 1, 1);
                    setCell(getRow(firstStart), 1, first.getName());
                }
            }
            // 合并毕业要求类型单元格
            mergeVertical(groupStart, currentRow - 1, 0);
            setCell(getRow(groupStart), 0, resolveGroupName(group));
        }
    }

    private String resolveGroupName(GraduationCourseSupportVo.SupportGroupVo group) {
        if (StringUtils.isNotBlank(group.getRootName())) {
            return group.getRootName();
        }
        if ("1".equals(group.getGraduationType())) {
            return "知识";
        } else if ("2".equals(group.getGraduationType())) {
            return "能力";
        } else if ("3".equals(group.getGraduationType())) {
            return "素质";
        }
        return "";
    }

    private void mergeVertical(int firstRow, int lastRow, int column) {
        if (lastRow > firstRow) {
            sheet.addMergedRegion(new CellRangeAddress(firstRow, lastRow, column, column));
        }
    }

    private Row getRow(int rowNumber) {
        Row row = sheet.getRow(rowNumber);
        if (ObjectUtils.isNotEmpty(row)) {
            return row;
        }
        return sheet.createRow(rowNumber);
    }

    private void setCell(Row row, int column, String value) {
        Cell cell = row.getCell(column);
        if (ObjectUtils.isEmpty(cell)) {
            cell = row.createCell(column);
        }
        cell.setCellStyle(cellStyle);
        if (StringUtils.isNotBlank(value)) {
            cell.setCellValue(value);
        }
    }
}
