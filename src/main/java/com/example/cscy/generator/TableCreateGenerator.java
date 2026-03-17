package com.example.cscy.generator;

import org.apache.poi.xwpf.usermodel.*;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STMerge;

import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;

/**
 * 复刻模板表格的Word生成工具
 */
public class TableCreateGenerator {

    public static void main(String[] args) {
        // 1. 创建空白docx文档
        XWPFDocument document = new XWPFDocument();

        try {
            // 2. 添加标题段落
            XWPFParagraph titleParagraph = document.createParagraph();
            XWPFRun titleRun = titleParagraph.createRun();
            titleRun.setText("1.通识课程教学安排");
            titleRun.setBold(true);
            titleRun.setFontSize(14);

            // 3. 创建表格：总列数15列，总行数=表头3行 + 模块行24行 + 小计1行 = 28行（可按需调整行数）
            int totalCol = 15;
            int totalRow = 28;
            XWPFTable table = document.createTable(totalRow, totalCol);
            //XWPFTable table = document.createTable();
            // 设置表格宽度100%适配页面
            table.setWidthType(TableWidthType.PCT);
            table.setWidth("100%");

            // 4. 核心：处理表头合并与内容填充（表头共3行，行号0、1、2）
            fillTableHeader(table);

            // 5. 处理课程模块行的跨行合并（模板预留的空行，可按需填充内容）
            fillModuleRows(table);

            // 6. 处理小计行
            fillTotalRow(table);

            // 7. 添加备注段落
            XWPFParagraph remarkParagraph = document.createParagraph();
            XWPFRun remarkRun = remarkParagraph.createRun();
            remarkRun.setText("备注：B代表必修课程、X代表限选课程、R代表任选课程；S代表考试，C代表考查（下同）。");
            remarkRun.setFontSize(10);

            // 8. 输出文档到本地
            FileOutputStream out = new FileOutputStream("通识课程教学安排.docx");
            document.write(out);
            out.close();
            document.close();
            System.out.println("文档生成成功！");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 填充表头（3行），处理所有合并规则
     */
    private static void fillTableHeader(XWPFTable table) {
        // ========== 表头第1行（行号0） ==========
        // 列0：课程模块，跨行3行（行0、1、2）
        setCellText(table.getRow(0).getCell(0), "  课程\n模块", true,"1134");
        mergeCellsVertically(table, 0, 0, 2);

        // 列1：课程名称，跨行3行
        setCellText(table.getRow(0).getCell(1), "课程名称", true,"1985");
        mergeCellsVertically(table, 1, 0, 2);

        // 列2：修读要求，跨行3行
        setCellText(table.getRow(0).getCell(2), "  修读\n要求", true,"1021");
        mergeCellsVertically(table, 2, 0, 2);

        // 列3：考核方式，跨行3行
        setCellText(table.getRow(0).getCell(3), "  考核\n方式", true,"1021");
        mergeCellsVertically(table, 3, 0, 2);

        // 列4-6：学时安排，跨列3列
        setCellText(table.getRow(0).getCell(4), "学时安排", true,"2553");
        mergeCellsHorizontal(table, 0, 4, 6);

        // 列7-14：学期安排，跨列8列
        setCellText(table.getRow(0).getCell(5), "学期安排", true,"5440");
        mergeCellsHorizontal(table, 0, 5, 12);

        // ========== 表头第2行（行号1） ==========
        // 列4-6：学时子项
        setCellText(table.getRow(1).getCell(4), "小计", true,"851");
        mergeCellsVertically(table, 4, 1, 2);
        setCellText(table.getRow(1).getCell(5), "讲授", true,"851");
        mergeCellsVertically(table, 5, 1, 2);
        setCellText(table.getRow(1).getCell(6), "实践", true,"851");
        mergeCellsVertically(table, 6, 1, 2);

        // 列7-8：第一学年，跨列2列
        setCellText(table.getRow(1).getCell(7), "第一学年", true,"1360");
        mergeCellsHorizontal(table, 1, 7, 8);
        // 列9-10：第二学年，跨列2列
        setCellText(table.getRow(1).getCell(8), "第二学年", true,"1360");
        mergeCellsHorizontal(table, 1, 8, 9);
        // 列11-12：第三学年，跨列2列
        setCellText(table.getRow(1).getCell(9), "第三学年", true,"1360");
        mergeCellsHorizontal(table, 1, 9, 10);
        // 列13-14：第四学年，跨列2列
        setCellText(table.getRow(1).getCell(10), "第四学年", true,"1360");
        mergeCellsHorizontal(table, 1, 10, 11);

        // ========== 表头第3行（行号2） ==========
        // 列7-14：学期子项
        setCellText(table.getRow(2).getCell(7), "秋", true,"680");
        setCellText(table.getRow(2).getCell(8), "春", true,"680");
        setCellText(table.getRow(2).getCell(9), "秋", true,"680");
        setCellText(table.getRow(2).getCell(10), "春", true,"680");
        setCellText(table.getRow(2).getCell(11), "秋", true,"680");
        setCellText(table.getRow(2).getCell(12), "春", true,"680");
        setCellText(table.getRow(2).getCell(13), "秋", true,"680");
        setCellText(table.getRow(2).getCell(14), "春", true,"680");

        deleteCol(table,0,6,15);
        deleteCol(table,1,11,15);
    }

    private static void deleteCol(XWPFTable table, int row, int startCol, int endCol) {
        XWPFTableRow currentRow = table.getRow(row);
        for (int col = endCol; col >= startCol; col--) {
            if (currentRow.getCell(col) != null) {
                currentRow.removeCell(col); // 删除指定列的冗余单元格
            }
        }
    }

    /**
     * 填充课程模块行，处理跨行合并（与模板预留行数一致）
     */
    private static void fillModuleRows(XWPFTable table) {
        // 1. 政治理论模块：行3-行8，共6行，列0跨行合并
        setCellText(table.getRow(3).getCell(0), "政治\n理论", true,"150");
        mergeCellsVertically(table, 0, 3, 8);

        // 2. 军事基础模块：行9-行15，共7行，列0跨行合并
        setCellText(table.getRow(9).getCell(0), "军事\n基础", true,"150");
        mergeCellsVertically(table, 0, 9, 15);

        // 3. 基础科学模块：行16-行21，共6行，列0跨行合并
        setCellText(table.getRow(16).getCell(0), "基础\n科学", true,"150");
        mergeCellsVertically(table, 0, 16, 21);

        // 4. 人文与社会科学模块：行22-行24，共3行，列0跨行合并
        setCellText(table.getRow(22).getCell(0), "人文与社会科学", true,"150");
        mergeCellsVertically(table, 0, 22, 24);

        // 5. 人工智能与信息技术模块：行25-行26，共2行，列0跨行合并
        setCellText(table.getRow(25).getCell(0), "人工智能与信息技术", true,"150");
        mergeCellsVertically(table, 0, 25, 26);
    }

    /**
     * 填充小计行
     */
    private static void fillTotalRow(XWPFTable table) {
        int totalRowNum = 27;
        setCellText(table.getRow(totalRowNum).getCell(0), "小  计", true,"500");
        // 小计列跨列4列（列0-3）
        mergeCellsHorizontal(table, totalRowNum, 0, 3);
    }

    /**
     * 工具方法：设置单元格文本+居中样式+加粗
     */
    private static void setCellText(XWPFTableCell cell, String text, boolean isBold,String width) {
        // 清空单元格默认段落
        cell.removeParagraph(0);
        XWPFParagraph paragraph = cell.addParagraph();
        // 水平居中
        paragraph.setAlignment(ParagraphAlignment.CENTER);
        // 垂直居中
        cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
        //单元格宽度
        cell.setWidth(width);
        // 设置文本
        XWPFRun run = paragraph.createRun();
        run.setFontFamily("宋体");
        run.setText(text);
        //run.setBold(isBold);
        run.setFontSize(9);
    }

    /**
     * 工具方法：表格跨列合并
     * @param table 表格对象
     * @param row 行号（从0开始）
     * @param fromCol 起始列号
     * @param toCol 结束列号
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
     * 工具方法：表格跨行合并
     * @param table 表格对象
     * @param col 列号（从0开始）
     * @param fromRow 起始行号
     * @param toRow 结束行号
     */
    public static void mergeCellsVertically(XWPFTable table, int col, int fromRow, int toRow) {
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