package com.example.cscy.utils;

import org.apache.poi.xwpf.usermodel.*;

import java.math.BigInteger;
import java.util.List;

import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTcPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTVMerge;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STMerge;
import java.util.ArrayList;

public class WordUtils {
    // 字体大小常量（中文字号对应的磅值）
    private static final int SIZE_CHU_HAO = 42;  // 初号
    private static final int SIZE_YI_HAO = 32;   // 一号
    private static final int SIZE_ER_HAO = 28;   // 二号
    private static final int SIZE_SAN_HAO = 24;  // 三号
    private static final int SIZE_SI_HAO = 18;   // 四号
    private static final int SIZE_XIAO_SI_HAO = 16; // 小四
    private static final int SIZE_WU_HAO = 13;   // 五号
    private static final int SIZE_XIAO_WU_HAO = 11; // 小五
    /**
     * 创建文档标题
     *
     * @param document XWPFDocument 对象
     * @param title    标题文本
     */
    public static void createTitle(XWPFDocument document, String title) {
        XWPFParagraph titleParagraph = document.createParagraph();
        titleParagraph.setAlignment(ParagraphAlignment.CENTER);

        XWPFRun titleRun = titleParagraph.createRun();
        titleRun.setText(title);
        titleRun.setBold(true);
        titleRun.setFontSize(SIZE_SI_HAO);
        titleRun.setFontFamily("黑体");

        // 添加空行
        document.createParagraph();
    }

    /**
     * 创建标题（1-6 级）
     *
     * @param document XWPFDocument 对象
     * @param text     标题文本
     * @param level    标题级别（1-6）
     */
    public static void createHeading(XWPFDocument document, String text, int level) {
        XWPFParagraph paragraph = document.createParagraph();
        paragraph.setAlignment(ParagraphAlignment.LEFT);

        // 设置标题样式（使用字符串，避免依赖 ParagraphStyles 枚举）
        String styleId = getHeadingStyleId(level);
        if (styleId != null) {
            paragraph.setStyle(styleId);
        }
        paragraph.getCTP().addNewPPr().addNewOutlineLvl().setVal(BigInteger.valueOf(level-1));
        XWPFRun run = paragraph.createRun();
        run.setText(text);

        // 根据级别设置字体和大小
        // 一级标题：黑体加粗四号
        // 二级标题：楷体小四
        // 三级标题：宋体五号加粗
        // 四级及以下：宋体五号
        if (level == 1) {
            run.setFontFamily("黑体");
            run.setBold(true);
            run.setFontSize(SIZE_SI_HAO);
        } else if (level == 2) {
            run.setFontFamily("楷体");
            run.setBold(false);
            run.setFontSize(SIZE_XIAO_SI_HAO);
        } else if (level == 3) {
            run.setFontFamily("宋体");
            run.setBold(true);
            run.setFontSize(SIZE_WU_HAO);
        } else {
            run.setFontFamily("宋体");
            run.setBold(false);
            run.setFontSize(SIZE_WU_HAO);
        }
    }

    /**
     * 获取标题样式 ID（使用字符串，不依赖枚举）
     *
     * @param level 标题级别
     * @return 样式 ID 字符串
     */
    private static String getHeadingStyleId(int level) {
        switch (level) {
            case 1:
                return "Heading1";
            case 2:
                return "Heading2";
            case 3:
                return "Heading3";
            case 4:
                return "Heading4";
            case 5:
                return "Heading5";
            case 6:
                return "Heading6";
            default:
                return null;
        }
    }

    /**
     * 获取字体大小（废弃，使用常量）
     *
     * @param level 标题级别
     * @return 字体大小
     */
    @Deprecated
    private static int getFontSizeByLevel(int level) {
        return SIZE_WU_HAO;
    }

    /**
     * 创建普通段落
     *
     * @param document XWPFDocument 对象
     * @param text     段落文本
     * @param style    样式（可为 null）
     */
    public static void createParagraph(XWPFDocument document, String text, String style) {
        XWPFParagraph paragraph = document.createParagraph();
        paragraph.setAlignment(ParagraphAlignment.LEFT);

        if (style != null) {
            paragraph.setStyle(style);
        }
        paragraph.getCTP().addNewPPr().addNewInd().setFirstLine(BigInteger.valueOf(720));
        XWPFRun run = paragraph.createRun();
        run.setText(text);
        run.setFontSize(SIZE_WU_HAO); // 五号
        run.setFontFamily("宋体");
    }

    /**
     * 创建带背景色的段落（用于表格单元格）
     *
     * @param document XWPFDocument 对象
     * @param text     段落文本
     * @param bgColor  背景色（如 "F2F2F2"）
     * @return XWPFParagraph 对象
     */
    public static XWPFParagraph createParagraphWithBgColor(XWPFDocument document, String text, String bgColor) {
        XWPFParagraph paragraph = document.createParagraph();
        paragraph.setAlignment(ParagraphAlignment.CENTER);
        paragraph.getCTP().addNewPPr().addNewInd().setFirstLine(BigInteger.valueOf(0));

        XWPFRun run = paragraph.createRun();
        run.setText(text);
        run.setFontSize(12);
        run.setFontFamily("宋体");
        run.setBold(true);

        return paragraph;
    }

    /**
     * 创建表格段落（无首行缩进）
     *
     * @param document XWPFDocument 对象
     * @param text     段落文本
     * @param bold     是否加粗
     * @return XWPFParagraph 对象
     */
    public static XWPFParagraph createTableParagraph(XWPFDocument document, String text, boolean bold) {
        XWPFParagraph paragraph = document.createParagraph();
        paragraph.setAlignment(ParagraphAlignment.CENTER);
        paragraph.getCTP().addNewPPr().addNewInd().setFirstLine(BigInteger.valueOf(0));

        XWPFRun run = paragraph.createRun();
        run.setText(text);
        run.setFontSize(12);
        run.setFontFamily("宋体");
        if (bold) {
            run.setBold(true);
        }

        return paragraph;
    }

    /**
     * 设置表格单元格内容
     *
     * @param cell     XWPFTableCell 对象
     * @param text     单元格文本
     * @param bold     是否加粗
     * @param bgColor  背景色（可为 null）
     */
    public static void setTableCellContent(XWPFTableCell cell, String text, boolean bold, String bgColor) {
        if (cell == null) return;

        // 清除现有段落
        while (cell.getParagraphs() != null && !cell.getParagraphs().isEmpty()) {
            cell.removeParagraph(0);
        }

        XWPFParagraph paragraph = cell.addParagraph();
        paragraph.setAlignment(ParagraphAlignment.CENTER);

        XWPFRun run = paragraph.createRun();
        run.setText(text);
        run.setFontSize(12);
        run.setFontFamily("宋体");
        if (bold) {
            run.setBold(true);
        }

        // 设置单元格背景色
        if (bgColor != null) {
            cell.getCTTc().addNewTcPr().addNewShd().setFill(bgColor);
        }
    }

    /**
     * 创建带样式的段落（用于表头）
     *
     * @param document XWPFDocument 对象
     * @param text     段落文本
     * @return XWPFParagraph 对象
     */
    public static XWPFParagraph createHeaderParagraph(XWPFDocument document, String text) {
        XWPFParagraph paragraph = document.createParagraph();
        paragraph.setAlignment(ParagraphAlignment.CENTER);
        paragraph.getCTP().addNewPPr().addNewInd().setFirstLine(BigInteger.valueOf(0));

        XWPFRun run = paragraph.createRun();
        run.setText(text);
        run.setFontSize(12);
        run.setFontFamily("宋体");
        run.setBold(true);

        return paragraph;
    }

    /**
     * 创建表格
     *
     * @param document  XWPFDocument 对象
     * @param rows      行数
     * @param columns   列数
     * @param widths    每列宽度（百分比之和应为100）
     * @return XWPFTable 对象
     */
    public static XWPFTable createTable(XWPFDocument document, int rows, int columns, List<Integer> widths) {
        XWPFTable table = document.createTable(rows, columns);
        return table;
    }

    /**
     * 设置表格单元格内容（带对齐方式）
     *
     * @param cell     XWPFTableCell 对象
     * @param text     单元格文本
     * @param bold     是否加粗
     * @param bgColor  背景色（可为 null）
     * @param align    对齐方式
     */
    public static void setTableCellContent(XWPFTableCell cell, String text, boolean bold, String bgColor, ParagraphAlignment align) {
        if (cell == null) return;

        // 清除现有段落
        while (cell.getParagraphs() != null && !cell.getParagraphs().isEmpty()) {
            cell.removeParagraph(0);
        }

        XWPFParagraph paragraph = cell.addParagraph();
        if (align != null) {
            paragraph.setAlignment(align);
        } else {
            paragraph.setAlignment(ParagraphAlignment.CENTER);
        }

        XWPFRun run = paragraph.createRun();
        run.setText(text);
        run.setFontSize(11);
        run.setFontFamily("宋体");
        if (bold) {
            run.setBold(true);
        }

        // 设置单元格背景色
        if (bgColor != null) {
            cell.getCTTc().addNewTcPr().addNewShd().setFill(bgColor);
        }
    }

    /**
     * 创建表格标题行（加粗居中）
     *
     * @param table    XWPFTable 对象
     * @param headers  表头文本数组
     */
    public static void createTableHeader(XWPFTable table, String[] headers) {
        XWPFTableRow headerRow = table.getRow(0);
        if (headerRow == null) {
            return;
        }

        for (int i = 0; i < headers.length; i++) {
            XWPFTableCell cell = headerRow.getCell(i);
            if (cell == null) {
                cell = headerRow.addNewTableCell();
            }
            setTableCellContent(cell, headers[i], true, "F2F2F2", ParagraphAlignment.CENTER);
        }
    }

    /**
     * 创建带表头的表格
     *
     * @param document XWPFDocument 对象
     * @param headers  表头文本数组
     * @param rowsData 数据行（二维数组）
     * @param widths   列宽列表
     * @return XWPFTable 对象
     */
    public static XWPFTable createTableWithHeader(XWPFDocument document, String[] headers, String[][] rowsData, List<Integer> widths) {
        int totalRows = 1 + (rowsData != null ? rowsData.length : 0);
        XWPFTable table = createTable(document, totalRows, headers.length, widths);
        createTableHeader(table, headers);

        if (rowsData != null && rowsData.length > 0) {
            for (int i = 0; i < rowsData.length; i++) {
                XWPFTableRow row = table.getRow(i + 1);
                if (row == null) {
                    row = table.insertNewTableRow(i + 1);
                }
                for (int j = 0; j < headers.length; j++) {
                    String cellText = (j < rowsData[i].length) ? rowsData[i][j] : "";
                    XWPFTableCell cell = row.getCell(j);
                    if (cell == null) {
                        cell = row.addNewTableCell();
                    }
                    setTableCellContent(cell, cellText, false, null, ParagraphAlignment.CENTER);
                }
            }
        }

        return table;
    }

    /**
     * 在表格末尾添加一行小计行（加粗）
     *
     * @param table       XWPFTable 对象
     * @param cellTexts   每个单元格的文本
     * @param bgColor     背景色（可为 null）
     */
    public static void addTableSubtotalRow(XWPFTable table, String[] cellTexts, String bgColor) {
        // 获取列数（从第一行获取）
        int columns = table.getRows().get(0).getTableCells().size();
        XWPFTableRow newRow = table.insertNewTableRow(table.getRows().size());

        for (int i = 0; i < columns; i++) {
            String text = (i < cellTexts.length) ? cellTexts[i] : "";
            XWPFTableCell cell = newRow.getCell(i);
            if (cell == null) {
                cell = newRow.addNewTableCell();
            }
            setTableCellContent(cell, text, true, bgColor, ParagraphAlignment.RIGHT);
        }
    }

    /**
     * 设置表格单元格内容（单元格内文字居中、加粗、设置字体大小）
     *
     * @param cell    XWPFTableCell 对象
     * @param text    单元格文本
     * @param isBold  是否加粗
     * @param width   单元格宽度
     */
    public static void setCellText(XWPFTableCell cell, String text, boolean isBold, String width) {
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
     * 设置表格单元格内容（带字体大小设置）
     *
     * @param cell    XWPFTableCell 对象
     * @param text    单元格文本
     * @param isBold  是否加粗
     * @param width   单元格宽度
     * @param fontSize 字体大小
     */
    public static void setCellText(XWPFTableCell cell, String text, boolean isBold, String width, int fontSize) {
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
        run.setFontSize(fontSize);
        if (isBold) {
            run.setBold(true);
        }
    }

    /**
     * 合并表格单元格（水平合并）
     *
     * @param table   XWPFTable 对象
     * @param row     行号（从0开始）
     * @param fromCol 起始列号
     * @param toCol   结束列号
     */
    public static void mergeCellsHorizontal(XWPFTable table, int row, int fromCol, int toCol) {
        for (int cellIndex = fromCol; cellIndex <= toCol; cellIndex++) {
            XWPFTableCell cell = table.getRow(row).getCell(cellIndex);
            if (cellIndex == fromCol) {
                // 主单元格设置合并跨度
                cell.getCTTc().addNewTcPr().addNewGridSpan().setVal(BigInteger.valueOf(toCol - fromCol + 1));
            } else {
                // 合并单元格设置延续属性
                cell.getCTTc().addNewTcPr().addNewHMerge().setVal(STMerge.CONTINUE);
            }
        }
    }

    /**
     * 合并表格单元格（垂直合并）
     *
     * @param table   XWPFTable 对象
     * @param col     列号（从0开始）
     * @param fromRow 起始行号
     * @param toRow   结束行号
     */
    public static void mergeCellsVertical(XWPFTable table, int col, int fromRow, int toRow) {
        for (int rowIndex = fromRow; rowIndex <= toRow; rowIndex++) {
            XWPFTableCell cell = table.getRow(rowIndex).getCell(col);
            if (rowIndex == fromRow) {
                // 主单元格设置合并起始
                cell.getCTTc().addNewTcPr().addNewVMerge().setVal(STMerge.RESTART);
            } else {
                // 合并单元格设置延续属性
                cell.getCTTc().addNewTcPr().addNewVMerge().setVal(STMerge.CONTINUE);
            }
        }
    }
}