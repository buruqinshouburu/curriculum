package com.doinner.csys.utils;

import fr.opensagres.xdocreport.document.IXDocReport;
import fr.opensagres.xdocreport.document.registry.XDocReportRegistry;
import fr.opensagres.xdocreport.template.IContext;
import fr.opensagres.xdocreport.template.TemplateEngineKind;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.poi.xwpf.usermodel.*;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.InputStream;
import java.math.BigInteger;

public class WordUtil {

    private static final Logger log = LoggerFactory.getLogger(WordUtil.class);

    /**
     * 获取 Word 模板的两个操作对象 IXDocReport 和 IContext
     *
     * @param path 模板的绝对地址
     * @return 模板数据对象
     */
    public static ExportData creatExportData(String path) {
        try {
            IXDocReport report = createReport(path);
            IContext context = report.createContext();
            return new ExportData(report, context);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * 加载模板的方法，主要是指定模板的路径和选择渲染数据的模板
     *
     * @param url 模板相对于类路径的地址
     * @return word 文档操作类
     */
    private static IXDocReport createReport(String url) {
        try (InputStream in = new FileInputStream(url)) {
            IXDocReport ix = XDocReportRegistry.getRegistry().loadReport(in, TemplateEngineKind.Freemarker);
            return ix;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }

    public static String readTemplatePath(String courseType) {
        String dir;
        String path = ClassLoader.getSystemResource("template").getPath();
        if ("1".equals(courseType)) {
            dir = path + "/form_work_theory.docx";
        } else if ("2".equals(courseType)) {
            dir = path + "/form_work_practice.docx";
        } else {
            dir = path + "/form_work_theory&practice.docx";
        }
        return dir;
    }

    public static String readTemplatesPath() {
        String path = System.getProperty("user.dir");
        return path + "/template/form_work_list.docx";
    }

    /**
     * 创建文档标题
     *
     * @param document XWPFDocument 对象
     * @param title    标题文本
     */
    public static void createTitle(XWPFDocument document, String title) {
        createTitle(document, title, "微软雅黑", 18);
    }

    /**
     * 创建文档标题（可指定字体字号）。教学计划可传黑体/五号。
     */
    public static void createTitle(XWPFDocument document, String title, String fontFamily, double fontSizePt) {
        XWPFParagraph titleParagraph = document.createParagraph();
        titleParagraph.setAlignment(ParagraphAlignment.CENTER);

        XWPFRun titleRun = titleParagraph.createRun();
        titleRun.setText(title == null ? "" : title);
        applyRunFont(titleRun, fontFamily, fontSizePt, true);

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
        createHeading(document, text, level, "微软雅黑", getFontSizeByLevel(level));
    }

    /**
     * 创建标题（可指定字体字号）。教学计划章节标题用黑体五号。
     */
    public static void createHeading(XWPFDocument document, String text, int level,
                                     String fontFamily, double fontSizePt) {
        XWPFParagraph paragraph = document.createParagraph();
        paragraph.setAlignment(ParagraphAlignment.LEFT);

        // 设置标题样式（使用字符串，避免依赖 ParagraphStyles 枚举）
        String styleId = getHeadingStyleId(level);
        if (styleId != null) {
            paragraph.setStyle(styleId);
        }

        CTPPr pPr = paragraph.getCTP().isSetPPr() ? paragraph.getCTP().getPPr() : paragraph.getCTP().addNewPPr();
        if (!pPr.isSetOutlineLvl()) {
            pPr.addNewOutlineLvl();
        }
        pPr.getOutlineLvl().setVal(BigInteger.valueOf(Math.max(level, 1) - 1));
        CTInd ind = pPr.isSetInd() ? pPr.getInd() : pPr.addNewInd();
        ind.setFirstLine(BigInteger.valueOf(120L * Math.max(level - 1, 0)));
        XWPFRun run = paragraph.createRun();
        run.setText(text == null ? "" : text);
        applyRunFont(run, fontFamily, fontSizePt, true);
    }

    /**
     * 根据标题级别获取字体大小
     *
     * @param level 标题级别
     * @return 字体大小
     */
    private static int getFontSizeByLevel(int level) {
        switch (level) {
            case 1:
                return 16;
            case 2:
                return 14;
            case 3:
                return 12;
            case 4:
                return 10;
            case 5:
                return 9;
            case 6:
                return 8;
            default:
                return 7;
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
     * 创建普通段落
     *
     * @param document XWPFDocument 对象
     * @param text     段落文本
     * @param style    样式（可为 null）
     */
    public static void createParagraph(XWPFDocument document, String text, String style) {
        createParagraph(document, text, style, "宋体", 12);
    }

    /**
     * 创建普通段落（可指定字体字号）。教学计划正文用仿宋_GB2312 五号。
     */
    public static void createParagraph(XWPFDocument document, String text, String style,
                                       String fontFamily, double fontSizePt) {
        XWPFParagraph paragraph = document.createParagraph();
        paragraph.setAlignment(ParagraphAlignment.LEFT);

        if (style != null) {
            paragraph.setStyle(style);
        }
        // 首行缩进2字符（firstLineChars=200）；同时给 firstLine 作兼容回退
        CTPPr pPr = paragraph.getCTP().isSetPPr() ? paragraph.getCTP().getPPr() : paragraph.getCTP().addNewPPr();
        CTInd ind = pPr.isSetInd() ? pPr.getInd() : pPr.addNewInd();
        ind.setFirstLineChars(BigInteger.valueOf(200));
        // 2 字符近似：按字号换算 twips（1pt≈20twips）
        long firstLineTwips = Math.round(fontSizePt * 20 * 2);
        ind.setFirstLine(BigInteger.valueOf(firstLineTwips));
        XWPFRun run = paragraph.createRun();
        run.setText(text == null ? "" : text);
        applyRunFont(run, fontFamily, fontSizePt, false);
    }

    /**
     * 合并表格单元格（水平合并）
     *
     * @param table   XWPFTable 对象
     * @param rowIndex     行号（从0开始）
     * @param startCol 起始列号
     * @param endCol   结束列号
     */
    public static void mergeCellsHorizontal(XWPFTable table, int rowIndex, int startCol, int endCol) {
        if (table == null || rowIndex < 0 || startCol < 0 || endCol <= startCol) {
            return;
        }
        XWPFTableRow tableRow = table.getRow(rowIndex);
        if (ObjectUtils.isEmpty(tableRow) || startCol >= tableRow.getTableCells().size()) {
            return;
        }
        endCol = Math.min(endCol, tableRow.getTableCells().size() - 1);
        if (endCol <= startCol) {
            return;
        }
        XWPFTableCell firstCell = tableRow.getCell(startCol);
        if (ObjectUtils.isEmpty(firstCell)) {
            return;
        }
        // 复用已有 TcPr，避免 addNewTcPr 产生重复 TcPr 元素导致 Word 渲染异常
        CTTcPr tcPr = firstCell.getCTTc().isSetTcPr() ? firstCell.getCTTc().getTcPr() : firstCell.getCTTc().addNewTcPr();
        if (tcPr.isSetGridSpan()) {
            tcPr.getGridSpan().setVal(BigInteger.valueOf(endCol - startCol + 1));
        } else {
            tcPr.addNewGridSpan().setVal(BigInteger.valueOf(endCol - startCol + 1));
        }
        for (int i = endCol; i > startCol; i--) {
            table.getRow(rowIndex).removeCell(i);
        }

    }

    /**
     * 合并表格单元格（垂直合并）
     *
     * @param table   XWPFTable 对象
     * @param colIndex     列号（从0开始）
     * @param startRow 起始行号
     * @param endRow   结束行号
     */
    public static void mergeCellsVertical(XWPFTable table, int colIndex, int startRow, int endRow) {
        if (table == null || colIndex < 0 || startRow < 0 || endRow < startRow) {
            return;
        }
        for (int r = startRow; r <= endRow; r++) {
            XWPFTableRow row = table.getRow(r);
            if (ObjectUtils.isEmpty(row) || colIndex >= row.getTableCells().size()) {
                continue;
            }
            XWPFTableCell cell = row.getCell(colIndex);
            if (cell != null) {
                CTTcPr tcPr = cell.getCTTc().isSetTcPr() ? cell.getCTTc().getTcPr() : cell.getCTTc().addNewTcPr();
                CTVMerge vMerge = tcPr.isSetVMerge() ? tcPr.getVMerge() : tcPr.addNewVMerge();

                if (r == startRow) {
                    vMerge.setVal(STMerge.RESTART);
                } else {
                    vMerge.setVal(STMerge.CONTINUE);
                    // 清空内容
                    while (cell.getParagraphs().size() > 0) {
                        cell.removeParagraph(0);
                    }
                    cell.addParagraph();
                }
            }
        }
    }

    /**
     * 设置表格单元格内容（单元格内文字居中、加粗、设置字体大小）
     *
     * @param cell   XWPFTableCell 对象
     * @param text   单元格文本
     * @param isBold 是否加粗
     * @param width  单元格宽度
     */
    public static void setCellText(XWPFTableCell cell, String text, boolean isBold, String width) {
        // 兼容旧调用：默认宋体 9pt
        setCellText(cell, text, isBold, width, "宋体", 9);
    }

    /**
     * 设置表格单元格内容（可指定字体字号）。
     * 教学计划：表头黑体五号，表内容宋体五号。
     */
    public static void setCellText(XWPFTableCell cell, String text, boolean isBold, String width,
                                   String fontFamily, double fontSizePt) {
        // 清空单元格默认段落
        if (ObjectUtils.isEmpty(cell)) {
            return;
        }
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
        // 设置文本（支持 \n 多行）
        String cellText = text == null ? "" : text;
        String[] lines = cellText.split("\n", -1);
        for (int i = 0; i < lines.length; i++) {
            if (i > 0) {
                paragraph.createRun().addBreak();
            }
            XWPFRun run = paragraph.createRun();
            run.setText(lines[i] == null ? "" : lines[i]);
            applyRunFont(run, fontFamily, fontSizePt, isBold);
        }
    }

    /**
     * 按列宽数组初始化表格网格（不等宽列，如实践表缩窄序号列）。
     */
    public static void initTableGrid(XWPFTable table, int[] colWidthsDxa) {
        if (table == null || colWidthsDxa == null || colWidthsDxa.length == 0) {
            return;
        }
        int totalCols = colWidthsDxa.length;
        CTTbl ctTbl = table.getCTTbl();

        CTTblPr tblPr = ctTbl.getTblPr() != null ? ctTbl.getTblPr() : ctTbl.addNewTblPr();
        CTTblWidth tblInd = tblPr.isSetTblInd() ? tblPr.getTblInd() : tblPr.addNewTblInd();
        tblInd.setType(STTblWidth.DXA);
        tblInd.setW(BigInteger.ZERO);

        int totalWidth = 0;
        for (int w : colWidthsDxa) {
            totalWidth += w;
        }
        CTTblWidth tblW = tblPr.isSetTblW() ? tblPr.getTblW() : tblPr.addNewTblW();
        tblW.setType(STTblWidth.DXA);
        tblW.setW(BigInteger.valueOf(totalWidth));

        CTTblLayoutType layout = tblPr.isSetTblLayout() ? tblPr.getTblLayout() : tblPr.addNewTblLayout();
        layout.setType(STTblLayoutType.FIXED);

        CTJcTable jc = tblPr.getJc() != null ? tblPr.getJc() : tblPr.addNewJc();
        jc.setVal(STJcTable.LEFT);

        CTTblBorders borders = tblPr.isSetTblBorders() ? tblPr.getTblBorders() : tblPr.addNewTblBorders();
        setTableBorders(borders);

        CTTblCellMar cellMar = tblPr.isSetTblCellMar() ? tblPr.getTblCellMar() : tblPr.addNewTblCellMar();
        setCellMargin(cellMar, 80, 80, 40, 40);

        if (ctTbl.getTblGrid() != null) {
            ctTbl.setTblGrid(null);
        }
        CTTblGrid tblGrid = ctTbl.addNewTblGrid();
        for (int w : colWidthsDxa) {
            CTTblGridCol gridCol = tblGrid.addNewGridCol();
            gridCol.setW(BigInteger.valueOf(w));
        }

        for (XWPFTableRow row : table.getRows()) {
            while (row.getTableCells().size() < totalCols) {
                row.addNewTableCell();
            }
            while (row.getTableCells().size() > totalCols) {
                row.removeCell(totalCols);
            }
            for (int c = 0; c < totalCols; c++) {
                XWPFTableCell cell = row.getTableCells().get(c);
                CTTcPr tcPr = cell.getCTTc().isSetTcPr() ? cell.getCTTc().getTcPr() : cell.getCTTc().addNewTcPr();
                CTTblWidth cellW = tcPr.isSetTcW() ? tcPr.getTcW() : tcPr.addNewTcW();
                cellW.setType(STTblWidth.DXA);
                cellW.setW(BigInteger.valueOf(colWidthsDxa[c]));

                for (XWPFParagraph p : cell.getParagraphs()) {
                    CTP ctp = p.getCTP();
                    CTPPr ppr = ctp.isSetPPr() ? ctp.getPPr() : ctp.addNewPPr();
                    CTInd ind = ppr.isSetInd() ? ppr.getInd() : ppr.addNewInd();
                    ind.setLeft(BigInteger.ZERO);
                    ind.setFirstLine(BigInteger.ZERO);
                }
            }
        }
    }

    /**
     * 同时设置 ascii / hAnsi / eastAsia 字体，避免中文在 Word 中回退。
     * 支持小数 pt（如五号 10.5）：通过 half-points 写入 sz/szCs。
     * 兼容 POI 5.x ooxml-lite：CTRPr 使用 *Array 访问器，而不是旧版 isSetSz/getSz。
     */
    public static void applyRunFont(XWPFRun run, String fontFamily, double fontSizePt, boolean bold) {
        if (run == null) {
            return;
        }
        String family = (fontFamily == null || fontFamily.isEmpty()) ? "宋体" : fontFamily;
        run.setFontFamily(family);
        run.setBold(bold);
        // 半磅：1pt = 2 half-points
        long halfPoints = Math.max(1, Math.round(fontSizePt * 2));
        CTR ctr = run.getCTR();
        CTRPr rPr = ctr.isSetRPr() ? ctr.getRPr() : ctr.addNewRPr();

        CTFonts fonts = (rPr.sizeOfRFontsArray() > 0) ? rPr.getRFontsArray(0) : rPr.addNewRFonts();
        fonts.setAscii(family);
        fonts.setHAnsi(family);
        fonts.setEastAsia(family);
        fonts.setCs(family);

        CTHpsMeasure sz = (rPr.sizeOfSzArray() > 0) ? rPr.getSzArray(0) : rPr.addNewSz();
        sz.setVal(BigInteger.valueOf(halfPoints));
        CTHpsMeasure szCs = (rPr.sizeOfSzCsArray() > 0) ? rPr.getSzCsArray(0) : rPr.addNewSzCs();
        szCs.setVal(BigInteger.valueOf(halfPoints));

        if (bold) {
            CTOnOff b = (rPr.sizeOfBArray() > 0) ? rPr.getBArray(0) : rPr.addNewB();
            b.setVal(true);
        }
    }

    public static String addEndDot(String str, String dot){
        if(ObjectUtils.isEmpty(str)){
            return str;
        }
        String trim = str.trim();
        char last = trim.charAt(trim.length() - 1);
        String punc=".,、。，:：？！“”‘’（）【】{}《》——-?!\"'()[]";
        if(punc.indexOf(last)==-1){
            return str+dot;
        }
        return str;
    }


    public static void initTableGrid(XWPFTable table, int totalCols, int colWidthDxa) {
        CTTbl ctTbl = table.getCTTbl();

        // ========== 1. 表格属性 ==========
        CTTblPr tblPr = ctTbl.getTblPr()!=null ? ctTbl.getTblPr() : ctTbl.addNewTblPr();

        // 1.1 表格左缩进=0（解决整体缩进问题）
        CTTblWidth tblInd = tblPr.isSetTblInd() ? tblPr.getTblInd() : tblPr.addNewTblInd();
        tblInd.setType(STTblWidth.DXA);
        tblInd.setW(BigInteger.ZERO);

        // 1.2 表格总宽度（固定宽度，比百分比更可靠）
        int totalWidth = totalCols * colWidthDxa;
        CTTblWidth tblW = tblPr.isSetTblW() ? tblPr.getTblW() : tblPr.addNewTblW();
        tblW.setType(STTblWidth.DXA);
        tblW.setW(BigInteger.valueOf(totalWidth));

        // 1.3 【核心】固定布局，禁止LibreOffice自动重排
        CTTblLayoutType layout = tblPr.isSetTblLayout() ? tblPr.getTblLayout() : tblPr.addNewTblLayout();
        layout.setType(STTblLayoutType.FIXED);

        // 1.4 表格左对齐
        CTJcTable jc = tblPr.getJc()!=null ? tblPr.getJc() : tblPr.addNewJc();
        jc.setVal(STJcTable.LEFT);

        // 1.5 表格边框（无边框容易网格错乱）
        CTTblBorders borders = tblPr.isSetTblBorders() ? tblPr.getTblBorders() : tblPr.addNewTblBorders();
        setTableBorders(borders);

        // 1.6 单元格内边距（解决文字往里缩的问题）
        CTTblCellMar cellMar = tblPr.isSetTblCellMar() ? tblPr.getTblCellMar() : tblPr.addNewTblCellMar();
        setCellMargin(cellMar, 80, 80, 40, 40);

        // ========== 2. 【最核心】表格列网格 tblGrid ==========
        // LibreOffice靠这个识别真实列数，没有必错乱
        if (ctTbl.getTblGrid() != null) {
            ctTbl.setTblGrid(null);
        }
        CTTblGrid tblGrid = ctTbl.addNewTblGrid();
        for (int i = 0; i < totalCols; i++) {
            CTTblGridCol gridCol = tblGrid.addNewGridCol();
            gridCol.setW(BigInteger.valueOf(colWidthDxa));
        }

        // ========== 3. 统一每行单元格 ==========
        for (XWPFTableRow row : table.getRows()) {
            // 3.1 确保每行单元格数量 = 总列数
            while (row.getTableCells().size() < totalCols) {
                row.addNewTableCell();
            }
            while (row.getTableCells().size() > totalCols) {
                row.removeCell(totalCols);
            }

            // 3.2 每个单元格设置固定宽度
            for (XWPFTableCell cell : row.getTableCells()) {
                CTTcPr tcPr = cell.getCTTc().isSetTcPr() ? cell.getCTTc().getTcPr() : cell.getCTTc().addNewTcPr();
                CTTblWidth cellW = tcPr.isSetTcW() ? tcPr.getTcW() : tcPr.addNewTcW();
                cellW.setType(STTblWidth.DXA);
                cellW.setW(BigInteger.valueOf(colWidthDxa));

                // 3.3 单元格内段落缩进清零
                for (XWPFParagraph p : cell.getParagraphs()) {
                    CTP ctp = p.getCTP();
                    CTPPr ppr = ctp.isSetPPr() ? ctp.getPPr() : ctp.addNewPPr();
                    CTInd ind = ppr.isSetInd() ? ppr.getInd() : ppr.addNewInd();
                    ind.setLeft(BigInteger.ZERO);
                    ind.setFirstLine(BigInteger.ZERO);
                }
            }

            // 3.4 默认行高
//            CTTrPr trPr = row.getCtRow().isSetTrPr() ? row.getCtRow().getTrPr() : row.getCtRow().addNewTrPr();
//            CTHeight trHeight = trPr.isSetTrHeight() ? trPr.getTrHeight() : trPr.addNewTrHeight();
//            trHeight.setVal(BigInteger.valueOf(400));
//            trHeight.setHRule(STHeightRule.AT_LEAST);
        }
    }


    private static void setTableBorders(CTTblBorders borders) {
        // 上
        CTBorder top = borders.isSetTop() ? borders.getTop() : borders.addNewTop();
        top.setVal(STBorder.SINGLE);
        top.setSz(BigInteger.valueOf(4));
        top.setColor("000000");
        // 下
        CTBorder bottom = borders.isSetBottom() ? borders.getBottom() : borders.addNewBottom();
        bottom.setVal(STBorder.SINGLE);
        bottom.setSz(BigInteger.valueOf(4));
        bottom.setColor("000000");
        // 左
        CTBorder left = borders.isSetLeft() ? borders.getLeft() : borders.addNewLeft();
        left.setVal(STBorder.SINGLE);
        left.setSz(BigInteger.valueOf(4));
        left.setColor("000000");
        // 右
        CTBorder right = borders.isSetRight() ? borders.getRight() : borders.addNewRight();
        right.setVal(STBorder.SINGLE);
        right.setSz(BigInteger.valueOf(4));
        right.setColor("000000");
        // 内部横
        CTBorder insideH = borders.isSetInsideH() ? borders.getInsideH() : borders.addNewInsideH();
        insideH.setVal(STBorder.SINGLE);
        insideH.setSz(BigInteger.valueOf(4));
        insideH.setColor("000000");
        // 内部竖
        CTBorder insideV = borders.isSetInsideV() ? borders.getInsideV() : borders.addNewInsideV();
        insideV.setVal(STBorder.SINGLE);
        insideV.setSz(BigInteger.valueOf(4));
        insideV.setColor("000000");
    }

    /**
     * 设置单元格内边距
     */
    private static void setCellMargin(CTTblCellMar cellMar, int left, int right, int top, int bottom) {
        CTTblWidth leftMar = cellMar.isSetLeft() ? cellMar.getLeft() : cellMar.addNewLeft();
        leftMar.setType(STTblWidth.DXA);
        leftMar.setW(BigInteger.valueOf(left));

        CTTblWidth rightMar = cellMar.isSetRight() ? cellMar.getRight() : cellMar.addNewRight();
        rightMar.setType(STTblWidth.DXA);
        rightMar.setW(BigInteger.valueOf(right));

        CTTblWidth topMar = cellMar.isSetTop() ? cellMar.getTop() : cellMar.addNewTop();
        topMar.setType(STTblWidth.DXA);
        topMar.setW(BigInteger.valueOf(top));

        CTTblWidth bottomMar = cellMar.isSetBottom() ? cellMar.getBottom() : cellMar.addNewBottom();
        bottomMar.setType(STTblWidth.DXA);
        bottomMar.setW(BigInteger.valueOf(bottom));
    }
}
