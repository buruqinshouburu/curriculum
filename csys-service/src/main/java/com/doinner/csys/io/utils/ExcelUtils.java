package com.doinner.csys.io.utils;

import com.doinner.csys.constant.DomainExceptionConstant;
import com.doinner.csys.constant.DomainFieldConstant;
import com.doinner.csys.domain.KnowledgeCheckLog;
import com.doinner.csys.domain.statisticsVo.StandardCultivationTargetStatisticsMultiVo;
import com.doinner.csys.domain.statisticsVo.StandardCultivationTargetStatisticsVo;
import com.doinner.csys.domain.statisticsVo.StatisticsExcelMultiVo;
import com.doinner.csys.domain.statisticsVo.StatisticsExcelVo;
import com.doinner.csys.domain.vo.TreeTableVo;
import com.doinner.csys.exception.FileException;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.swing.*;
import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @Program: agileai
 * @ClassName: ExcelUtils
 * @Description: Excel工具类
 * @Author: hsy
 * @CreateDate: 2021-04-08 14:03
 **/
public class ExcelUtils {
    private static final Logger log = LoggerFactory.getLogger(ExcelUtils.class);

    /**
     * 读取Excel文件
     *
     * @param file
     * @return java.util.List<java.util.Map < java.lang.String, java.lang.Object>>
     */
    public static List<Map<String, String>> readExcel(MultipartFile file) {
        try (InputStream is = file.getInputStream(); Workbook workbook = WorkbookFactory.create(is)) {
            Sheet sheet = null;
            int index = 0;
            while (ObjectUtils.isEmpty(sheet) && index < workbook.getNumberOfSheets()) {
                sheet = workbook.getSheetAt(index++);
                if (sheet.getLastRowNum() < 1) {
                    sheet = null;
                }
            }
            return read(sheet, workbook);
        } catch (IOException e) {
            e.printStackTrace();
        }
        throw new FileException(file.getOriginalFilename() + DomainExceptionConstant.EXCEL_HAS_NO_SHEET);
    }

    /**
     * 解析数据
     *
     * @param sheet 表格sheet对象
     * @param book  用于流关闭
     * @return
     * @throws IOException
     */
    private static List<Map<String, String>> read(Sheet sheet, Workbook book) throws IOException {
        int rowStart = sheet.getFirstRowNum();  // 首行下标
        int rowEnd = sheet.getLastRowNum();  // 尾行下标
        // 如果首行与尾行相同，表明只有一行，直接返回空数组
        if (rowStart == rowEnd) {
            book.close();
            return Collections.emptyList();
        }
        // 获取第一行JSON对象键
        Row firstRow = sheet.getRow(rowStart);
        int cellStart = firstRow.getFirstCellNum();
        int cellEnd = firstRow.getLastCellNum();
        Map<Integer, String> keyMap = new HashMap<>();
        for (int j = cellStart; j < cellEnd; j++) {
            String flag = getValue(firstRow.getCell(j), rowStart, j, book, false);
            keyMap.put(j, flag);
        }
        // 获取每行JSON对象的值
        StringBuilder sb = new StringBuilder();
        List<Map<String, String>> mapList = new ArrayList<>(rowEnd);
        for (int i = rowStart + 1; i <= rowEnd; i++) {
            Row eachRow = sheet.getRow(i);
            if (eachRow != null) {
                Map<String, String> map = new LinkedHashMap<>(cellEnd);
                sb.delete(0, sb.length());
                for (int k = cellStart; k < cellEnd; k++) {
                    String val = getValue(eachRow.getCell(k), i, k, book, false);
                    sb.append(val);    // 所有数据添加到里面，用于判断该行是否为空
                    map.put(keyMap.get(k), val);
                }
                mapList.add(map);
            }
        }
        book.close();
        return mapList;
    }

    /**
     * 获取每个单元格的数据
     *
     * @param cell   单元格对象
     * @param rowNum 第几行
     * @param index  该行第几个
     * @param book   主要用于关闭流
     * @param isKey  是否为键：true-是，false-不是。 如果解析Json键，值为空时报错；如果不是Json键，值为空不报错
     * @return
     * @throws IOException
     */
    private static String getValue(Cell cell, int rowNum, int index, Workbook book, boolean isKey) {
        try {
            // 空白或空
            if (cell == null || cell.getCellType() == CellType.BLANK) {
                if (isKey) {
                    book.close();
                    throw new NullPointerException(String.format("the key on row %s index %s is null ", ++rowNum, ++index));
                } else {
                    return "";
                }
            }

            // 0. 数字 类型
            if (cell.getCellType() == CellType.NUMERIC) {
                if (DateUtil.isCellDateFormatted(cell)) {
                    Date date = cell.getDateCellValue();
                    DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    return df.format(date);
                }
                double numericCellValue = cell.getNumericCellValue();
                if (numericCellValue == (long) numericCellValue) {
                    return (long) numericCellValue + "";
                }
                return numericCellValue + "";
//                String val = cell.getNumericCellValue()+"";
//                val = val.toUpperCase();
//                if (val.contains("E")) {
//                    val = val.split("E")[0].replace(".", "");
//                }
//                return val;
            }

            // 1. String类型
            if (cell.getCellType() == CellType.STRING) {
                String val = cell.getStringCellValue();
                if (val == null || val.trim().length() == 0) {
                    if (book != null) {
                        book.close();
                    }
                    return "";
                }
                return val.trim();
            }

            // 2. 公式 CELL_TYPE_FORMULA
            if (cell.getCellType() == CellType.FORMULA) {
                return cell.getStringCellValue();
            }

            // 4. 布尔值 CELL_TYPE_BOOLEAN
            if (cell.getCellType() == CellType.BOOLEAN) {
                return cell.getBooleanCellValue() + "";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 5.	错误 CELL_TYPE_ERROR
        return "";
    }

    /**
     * 读取excel数据拼装成树
     *
     * @param file        excel
     * @param splitIndexs excel表格中每一级子级开始的列
     * @return
     */
    public static List<TreeTableVo> readTreeTable(MultipartFile file, Integer... splitIndexs) {
        Sheet sheet = getSheet(file);
        //表头
        Map<Integer, String> headerMap = readSheetHeaderToMap(sheet);
        //获取表格中父级与子级之间切分的列数
//        List<Integer> _splitIndexList = new ArrayList<>();
        List<TreeTableVo> treeTableVoList = new ArrayList<>();
        List<CellRangeAddress> mergedRegions = sheet.getMergedRegions();
        AtomicInteger maxMergeColumnNumber = new AtomicInteger(0);
        List<Integer> lastMergeStartList = new ArrayList<>();
//        //所有合并的单元格按照起始列分组
        Map<Integer, List<CellRangeAddress>> rangeColumnMap = mergedRegions.parallelStream()
                .collect(Collectors.groupingBy(CellRangeAddress::getFirstColumn));
        Iterator<Integer> iterator = rangeColumnMap.keySet().iterator();
        //取出所有与上一列合并方式不同的列
//        while(iterator.hasNext()){
//            Integer columnIndex = iterator.next();
//            List<CellRangeAddress> cellRangeAddresses = rangeColumnMap.get(columnIndex);
//            List<Integer> collect = cellRangeAddresses.parallelStream().map(CellRangeAddress::getFirstRow).sorted().collect(Collectors.toList());
//            if(collect.size() != lastMergeStartList.size()){
//                _splitIndexList.add(columnIndex);
//                lastMergeStartList = collect;
//            }
//        }
        Set<Integer> indexSet = new HashSet<>();
//        indexSet.add(Integer.valueOf(maxMergeColumnNumber.incrementAndGet()));
        indexSet.addAll(Arrays.asList(splitIndexs));
//        indexSet.addAll(_splitIndexList);
        List<Integer> splitIndexList = new ArrayList<>(indexSet);
        if (headerMap.size() < splitIndexList.get(splitIndexList.size() - 1)) {
            throw new FileException(DomainExceptionConstant.EXCEL_FILE_FORMAT_WRONG);
        }
        //读取数据组装
        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            readCell(row, splitIndexList, 1, treeTableVoList, headerMap, rangeColumnMap);
        }
        return treeTableVoList;
    }

    public static void readCell(Row row, List<Integer> splitIndexList, Integer level, List<TreeTableVo> items, Map<Integer, String> headerMap, Map<Integer, List<CellRangeAddress>> rangeColumnMap) {
        Integer startIndex = splitIndexList.get(level - 1);
        Integer endIndex = level == splitIndexList.size() ? Integer.valueOf(row.getLastCellNum()) : splitIndexList.get(level);
        Cell cell = null;
        TreeTableVo treeTableVo;
        List<CellRangeAddress> cellRangeAddresses = rangeColumnMap.get(startIndex);
        boolean newLine = true;
        if (level < splitIndexList.size() && ObjectUtils.isNotEmpty(cellRangeAddresses)) {
            int rowNum = row.getRowNum();
            Optional<CellRangeAddress> first = cellRangeAddresses.parallelStream().filter(cellAddresses -> cellAddresses.getFirstRow() < rowNum && rowNum <= cellAddresses.getLastRow()).findFirst();
            newLine = first.isEmpty();
        }
        if (newLine) {
            treeTableVo = new TreeTableVo();
            Map<String, String> params = new HashMap<>();
            for (int i = startIndex; i < endIndex; i++) {
                try {
                    params.put(headerMap.get(i), getCellData(row.getCell(i)));
                } catch (NullPointerException e) {
                    return;
                }
            }
            if (ObjectUtils.isEmpty(params.values().parallelStream().filter(StringUtils::isNotBlank).collect(Collectors.toList()))) {
                if (ObjectUtils.isEmpty(items)) {
                    return;
                }
                treeTableVo = items.get(items.size() - 1);
            } else {
                treeTableVo.setParams(params);
                treeTableVo.setChildren(new ArrayList<>());
                treeTableVo.setParentId(DomainFieldConstant.ROOT_NODE_STRING_ID);
                items.add(treeTableVo);
            }
        } else {
            if (ObjectUtils.isEmpty(items)) {
                return;
            }
            treeTableVo = items.get(items.size() - 1);
        }
        if (level < splitIndexList.size()) {
            readCell(row, splitIndexList, level + 1, treeTableVo.getChildren(), headerMap, rangeColumnMap);
        }
    }

    public static String getCellData(Cell cell) {
        String value = "";
        switch (cell.getCellType()) {
            case _NONE:
                break;
            case NUMERIC:
                double numericCellValue = cell.getNumericCellValue();
                if (numericCellValue == (long) numericCellValue) {
                    return (long) numericCellValue + "";
                }
                value = numericCellValue + "";
                break;
            case STRING:
                value = cell.getStringCellValue();
                break;
            case BOOLEAN:
                value = cell.getBooleanCellValue() ? "true" : "false";
                break;
            default:
                value = "";
        }
        return value;
    }

    public static Map<Integer, String> readSheetHeaderToMap(Sheet sheet) {
        Map<Integer, String> indexValueMap = new HashMap<>();
        if (ObjectUtils.isEmpty(sheet) || sheet.getLastRowNum() == 0) {
            throw new FileException(DomainExceptionConstant.EXCEL_IS_EMPTY);
        }
        Row row = sheet.getRow(0);
        for (int i = 0; i < row.getLastCellNum(); i++) {
            indexValueMap.put(i, row.getCell(i).getStringCellValue());
        }
        return indexValueMap;
    }

    public static Sheet getSheet(MultipartFile file) {
        try (InputStream is = file.getInputStream(); Workbook workbook = WorkbookFactory.create(is)) {
            Sheet sheet = null;
            int index = 0;
            while (ObjectUtils.isEmpty(sheet) && index < workbook.getNumberOfSheets()) {
                sheet = workbook.getSheetAt(index++);
                if (sheet.getLastRowNum() < 1) {
                    sheet = null;
                }
            }
            return sheet;
        } catch (IOException e) {
            e.printStackTrace();
        }
        throw new FileException(DomainExceptionConstant.EXCEL_HAS_NO_SHEET);
    }

    //课程分配比例
    public static XSSFWorkbook getSchemeType(List<StandardCultivationTargetStatisticsVo> voList) {
        XSSFWorkbook workBook = new XSSFWorkbook();

        XSSFSheet sheet = workBook.createSheet();
        XSSFRow row0 = sheet.createRow(0);

        XSSFCellStyle cellStyle = workBook.createCellStyle();
        cellStyle.setWrapText(true);
        cellStyle.setDataFormat(workBook.createDataFormat().getFormat("0.00%"));
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        cellStyle.setAlignment(HorizontalAlignment.CENTER);

        row0.createCell(0).setCellValue("课程类型");
        row0.createCell(1).setCellValue("课程占比");
        row0.createCell(2).setCellValue("细分");
        row0.createCell(3).setCellValue("细分占比");
        Map<Long, StandardCultivationTargetStatisticsVo> mapVo = new HashMap<>();
        if (ObjectUtils.isNotEmpty(voList)) {
            for (StandardCultivationTargetStatisticsVo vo : voList) {
                mapVo.put(vo.getDenominatorId(), vo);
            }
        }
        XSSFRow row1 = sheet.createRow(1);
        row1.createCell(0).setCellValue(DomainFieldConstant.courseType.get(1));
        row1.createCell(2).setCellValue("其中：政治理论");
        XSSFRow row2 = sheet.createRow(2);
        row2.createCell(2).setCellValue("其中：自然科学");
        XSSFRow row3 = sheet.createRow(3);
        row3.createCell(2).setCellValue("其中：人文科学");
        XSSFRow row4 = sheet.createRow(4);
        row4.createCell(2).setCellValue("其中：军事基础");
        if (ObjectUtils.isNotEmpty(mapVo.get(1L))) {
            Long Num = mapVo.get(1L).getNumeratorCount();
            Long Dec = mapVo.get(1L).getDenominatorCountS().get();
            double pre = (double) Num / Dec;
            XSSFCell cell1 = row1.createCell(1);
            cell1.setCellValue(pre);
            cell1.setCellStyle(cellStyle);

            double pre1 = (double) 6 / 21;
            XSSFCell cell3 = row1.createCell(3);
            cell3.setCellValue(pre1);
            cell3.setCellStyle(cellStyle);

            double pre2 = (double) 6 / 21;
            XSSFCell cell4 = row2.createCell(3);
            cell4.setCellValue(pre2);
            cell4.setCellStyle(cellStyle);

            double pre3 = (double) 2 / 21;
            XSSFCell cell5 = row3.createCell(3);
            cell5.setCellValue(pre3);
            cell5.setCellStyle(cellStyle);


            double pre4 = (double) 7 / 21;
            XSSFCell cell6 = row4.createCell(3);
            cell6.setCellValue(pre4);
            cell6.setCellStyle(cellStyle);
        }

        CellRangeAddress cellAddresses1 = new CellRangeAddress(1, 4, 0, 0);
        sheet.addMergedRegion(cellAddresses1);

        CellRangeAddress cellAddresses2 = new CellRangeAddress(1, 4, 1, 1);
        sheet.addMergedRegion(cellAddresses2);


        XSSFRow row5 = sheet.createRow(5);
        row5.createCell(0).setCellValue(DomainFieldConstant.courseType.get(2));
        if (ObjectUtils.isNotEmpty(mapVo.get(2L))) {
            Long Num = mapVo.get(2L).getNumeratorCount();
            Long Dec = mapVo.get(2L).getDenominatorCountS().get();
            double pre = (double) Num / Dec;
            XSSFCell cell1 = row5.createCell(1);
            cell1.setCellValue(pre);
            cell1.setCellStyle(cellStyle);
        }

        XSSFRow row6 = sheet.createRow(6);
        row6.createCell(0).setCellValue(DomainFieldConstant.courseType.get(3));
        if (ObjectUtils.isNotEmpty(mapVo.get(3L))) {
            Long Num = mapVo.get(3L).getNumeratorCount();
            Long Dec = mapVo.get(3L).getDenominatorCountS().get();
            double pre = (double) Num / Dec;
            XSSFCell cell1 = row6.createCell(1);
            cell1.setCellValue(pre);
            cell1.setCellStyle(cellStyle);

        }
        XSSFRow row7 = sheet.createRow(7);
        row7.createCell(0).setCellValue(DomainFieldConstant.courseType.get(4));
        if (ObjectUtils.isNotEmpty(mapVo.get(4L))) {
            Long Num = mapVo.get(4L).getNumeratorCount();
            Long Dec = mapVo.get(4L).getDenominatorCountS().get();
            double pre = (double) Num / Dec;
            XSSFCell cell1 = row7.createCell(1);
            cell1.setCellValue(pre);
            cell1.setCellStyle(cellStyle);
        }
        XSSFRow row8 = sheet.createRow(8);
        row8.createCell(0).setCellValue(DomainFieldConstant.courseType.get(5));
        if (ObjectUtils.isNotEmpty(mapVo.get(5L))) {
            Long Num = mapVo.get(5L).getNumeratorCount();
            Long Dec = mapVo.get(5L).getDenominatorCountS().get();
            double pre = (double) Num / Dec;
            XSSFCell cell1 = row8.createCell(1);
            cell1.setCellValue(pre);
            cell1.setCellStyle(cellStyle);
        }
        return workBook;
    }

    //课程分配比例
    public static XSSFWorkbook getSchemeTypeMulti(List<StandardCultivationTargetStatisticsMultiVo> voList) {
        XSSFWorkbook workBook = new XSSFWorkbook();

        XSSFSheet sheet = workBook.createSheet();
        XSSFRow row0 = sheet.createRow(0);

        XSSFCellStyle cellStyle = workBook.createCellStyle();
        cellStyle.setWrapText(true);
        cellStyle.setDataFormat(workBook.createDataFormat().getFormat("0.00%"));
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        cellStyle.setAlignment(HorizontalAlignment.CENTER);

        row0.createCell(0).setCellValue("培养方案名称");
        row0.createCell(1).setCellValue("课程类型");
        row0.createCell(2).setCellValue("课程占比");
        row0.createCell(3).setCellValue("细分");
        row0.createCell(4).setCellValue("细分占比");
        if (ObjectUtils.isNotEmpty(voList)) {
            int sumRow = 0;
            for(int i = 0; i < voList.size(); i++){

                Map<Long, StandardCultivationTargetStatisticsVo> mapVo = new HashMap<>();
                List<StandardCultivationTargetStatisticsVo> csList = voList.get(i).getStandardCultivationTargetStatisticsVoList();
                for (StandardCultivationTargetStatisticsVo vo : csList) {
                    mapVo.put(vo.getDenominatorId(), vo);
                }

                XSSFRow row1 = sheet.createRow(sumRow + 1);
                row1.createCell(0).setCellValue(voList.get(i).getSchemeName());
                row1.createCell(1).setCellValue(DomainFieldConstant.courseType.get(1));
                row1.createCell(3).setCellValue("其中：政治理论");
                XSSFRow row2 = sheet.createRow(sumRow + 2);
                row2.createCell(3).setCellValue("其中：自然科学");
                XSSFRow row3 = sheet.createRow(sumRow + 3);
                row3.createCell(3).setCellValue("其中：人文科学");
                XSSFRow row4 = sheet.createRow(sumRow + 4);
                row4.createCell(3).setCellValue("其中：军事基础");
                if (ObjectUtils.isNotEmpty(mapVo.get(1L))) {
                    Long Num = mapVo.get(1L).getNumeratorCount();
                    Long Dec = mapVo.get(1L).getDenominatorCountS().get();
                    double pre = (double) Num / Dec;
                    XSSFCell cell1 = row1.createCell(2);
                    cell1.setCellValue(pre);
                    cell1.setCellStyle(cellStyle);

                    double pre1 = (double) 6 / 21;
                    XSSFCell cell3 = row1.createCell(4);
                    cell3.setCellValue(pre1);
                    cell3.setCellStyle(cellStyle);

                    double pre2 = (double) 6 / 21;
                    XSSFCell cell4 = row2.createCell(4);
                    cell4.setCellValue(pre2);
                    cell4.setCellStyle(cellStyle);

                    double pre3 = (double) 2 / 21;
                    XSSFCell cell5 = row3.createCell(4);
                    cell5.setCellValue(pre3);
                    cell5.setCellStyle(cellStyle);


                    double pre4 = (double) 7 / 21;
                    XSSFCell cell6 = row4.createCell(4);
                    cell6.setCellValue(pre4);
                    cell6.setCellStyle(cellStyle);
                }

                CellRangeAddress cellAddresses1 = new CellRangeAddress(sumRow + 1, sumRow + 4, 1, 1);
                sheet.addMergedRegion(cellAddresses1);

                CellRangeAddress cellAddresses2 = new CellRangeAddress(sumRow + 1, sumRow + 4, 2, 2);
                sheet.addMergedRegion(cellAddresses2);


                XSSFRow row5 = sheet.createRow(sumRow + 5);
                row5.createCell(1).setCellValue(DomainFieldConstant.courseType.get(2));
                if (ObjectUtils.isNotEmpty(mapVo.get(2L))) {
                    Long Num = mapVo.get(2L).getNumeratorCount();
                    Long Dec = mapVo.get(2L).getDenominatorCountS().get();
                    double pre = (double) Num / Dec;
                    XSSFCell cell1 = row5.createCell(2);
                    cell1.setCellValue(pre);
                    cell1.setCellStyle(cellStyle);
                }

                XSSFRow row6 = sheet.createRow(sumRow + 6);
                row6.createCell(1).setCellValue(DomainFieldConstant.courseType.get(3));
                if (ObjectUtils.isNotEmpty(mapVo.get(3L))) {
                    Long Num = mapVo.get(3L).getNumeratorCount();
                    Long Dec = mapVo.get(3L).getDenominatorCountS().get();
                    double pre = (double) Num / Dec;
                    XSSFCell cell1 = row6.createCell(2);
                    cell1.setCellValue(pre);
                    cell1.setCellStyle(cellStyle);

                }
                XSSFRow row7 = sheet.createRow(sumRow + 7);
                row7.createCell(1).setCellValue(DomainFieldConstant.courseType.get(4));
                if (ObjectUtils.isNotEmpty(mapVo.get(4L))) {
                    Long Num = mapVo.get(4L).getNumeratorCount();
                    Long Dec = mapVo.get(4L).getDenominatorCountS().get();
                    double pre = (double) Num / Dec;
                    XSSFCell cell1 = row7.createCell(2);
                    cell1.setCellValue(pre);
                    cell1.setCellStyle(cellStyle);
                }
                XSSFRow row8 = sheet.createRow(sumRow + 8);
                row8.createCell(1).setCellValue(DomainFieldConstant.courseType.get(5));
                if (ObjectUtils.isNotEmpty(mapVo.get(5L))) {
                    Long Num = mapVo.get(5L).getNumeratorCount();
                    Long Dec = mapVo.get(5L).getDenominatorCountS().get();
                    double pre = (double) Num / Dec;
                    XSSFCell cell1 = row8.createCell(2);
                    cell1.setCellValue(pre);
                    cell1.setCellStyle(cellStyle);
                }
                CellRangeAddress cellAddresses3 = new CellRangeAddress(sumRow + 1, sumRow + 8, 0, 0);
                sheet.addMergedRegion(cellAddresses3);
                sumRow += (i+1)*8 +1;
            }
        }
        return workBook;
    }

    public static void writeExcelToResponse(HttpServletResponse response, XSSFWorkbook xssfWorkbook) {
        try (OutputStream outputStream = response.getOutputStream()) {
            xssfWorkbook.write(outputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 较为简单格式的excel导出，类似‘学分配比图’
     *
     * @param response
     * @param titleRow 表头
     * @param data     数据, name为第一列， number1为第二列， 第三列自动计算第二列的百分比，合计栏自动计算
     */
    public static void simpleStatisticsExport(HttpServletResponse response, List<String> titleRow, List<StatisticsExcelVo> data) {
        XSSFWorkbook xssfWorkbook = new XSSFWorkbook();
        XSSFSheet sheet = xssfWorkbook.createSheet(DomainFieldConstant.STATISTICS_SHEET_NAME);

        XSSFCellStyle cellStyle = xssfWorkbook.createCellStyle();
        cellStyle.setWrapText(true);
        cellStyle.setDataFormat(xssfWorkbook.createDataFormat().getFormat("0.00%"));

        XSSFCellStyle titleCellStyle = xssfWorkbook.createCellStyle();
        titleCellStyle.setWrapText(true);
        titleCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        titleCellStyle.setAlignment(HorizontalAlignment.CENTER);

        XSSFRow topRow = sheet.createRow(0);
        for (int i = 0; i < titleRow.size(); i++) {
            XSSFCell cell = topRow.createCell(i);
            cell.setCellValue(titleRow.get(i));
            cell.setCellStyle(titleCellStyle);
        }

        String colString = CellReference.convertNumToColString(1);
        for (int i = 0; i < data.size(); i++) {
            StatisticsExcelVo statisticsExcelVo = data.get(i);
            XSSFRow row = sheet.createRow(i + 1);
            row.createCell(0).setCellValue(statisticsExcelVo.getName());
            row.createCell(1).setCellValue(statisticsExcelVo.getNumber1());
            XSSFCell cell = row.createCell(2);
            cell.setCellFormula(colString + (i + 2) + "/" + colString + (data.size() + 2));
            cell.setCellStyle(cellStyle);
        }
        XSSFRow row = sheet.createRow(data.size() + 1);
        row.createCell(0).setCellValue("合计栏");
        row.createCell(1).setCellFormula("SUM(" + colString + "2:" + colString + (data.size() + 1) + ")");
        XSSFCell cell = row.createCell(2);
        cell.setCellFormula(colString + (data.size() + 2) + "/" + colString + (data.size() + 2));
        cell.setCellStyle(cellStyle);
        writeExcelToResponse(response, xssfWorkbook);
    }

    /**
     * 较为简单格式的excel导出，类似‘学分配比图’
     *
     * @param response
     * @param titleRow 表头
     * @param data     数据, name为第一列， number1为第二列， 第三列自动计算第二列的百分比，合计栏自动计算
     */
    public static void simpleStatisticsMultiExport(HttpServletResponse response, List<String> titleRow, List<StatisticsExcelMultiVo> data) {
        XSSFWorkbook xssfWorkbook = new XSSFWorkbook();
        XSSFSheet sheet = xssfWorkbook.createSheet(DomainFieldConstant.STATISTICS_SHEET_NAME);

        XSSFCellStyle cellStyle = xssfWorkbook.createCellStyle();
        cellStyle.setWrapText(true);
        cellStyle.setDataFormat(xssfWorkbook.createDataFormat().getFormat("0.00%"));

        XSSFCellStyle titleCellStyle = xssfWorkbook.createCellStyle();
        titleCellStyle.setWrapText(true);
        titleCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        titleCellStyle.setAlignment(HorizontalAlignment.CENTER);

        XSSFRow topRow = sheet.createRow(0);
        for (int i = 0; i < titleRow.size(); i++) {
            XSSFCell cell = topRow.createCell(i);
            cell.setCellValue(titleRow.get(i));
            cell.setCellStyle(titleCellStyle);
        }

        String colString = CellReference.convertNumToColString(2);
        int sumRow = 0;
        for(int m = 0; m < data.size(); m++) {
            StatisticsExcelMultiVo vo = data.get(m);
            if(ObjectUtils.isEmpty(vo)){
                continue;
            }
            for (int i = 0; i < vo.getStatisticsExcelVos().size(); i++) {
                StatisticsExcelVo statisticsExcelVo = vo.getStatisticsExcelVos().get(i);
                XSSFRow row = sheet.createRow(sumRow + i + 1);
                row.createCell(0).setCellValue(vo.getSchemeName());
                row.createCell(1).setCellValue(statisticsExcelVo.getName());
                row.createCell(2).setCellValue(statisticsExcelVo.getNumber1());
                XSSFCell cell = row.createCell(3);
                cell.setCellFormula(colString + (sumRow + i + 2) + "/" + colString + (sumRow + vo.getStatisticsExcelVos().size() + 2));
                cell.setCellStyle(cellStyle);
            }
            XSSFRow row = sheet.createRow(sumRow + vo.getStatisticsExcelVos().size() + 1);
            row.createCell(1).setCellValue("合计栏");
            row.createCell(2).setCellFormula("SUM(" + colString + (sumRow + 2) +":" + colString + (sumRow + vo.getStatisticsExcelVos().size() + 1) + ")");
            XSSFCell cell = row.createCell(3);
            cell.setCellFormula(colString + (sumRow + vo.getStatisticsExcelVos().size() + 2) + "/" + colString + (sumRow + vo.getStatisticsExcelVos().size() + 2));
            cell.setCellStyle(cellStyle);
            CellRangeAddress cellAddresses = new CellRangeAddress(sumRow + 1, sumRow + vo.getStatisticsExcelVos().size() + 1, 0, 0);
            sheet.addMergedRegion(cellAddresses);
            sumRow += vo.getStatisticsExcelVos().size() + 1;
        }

        writeExcelToResponse(response, xssfWorkbook);
    }

    /**
     * 较为复杂的excel导出，格式类似’必修与选修课比例‘或者‘讲授、实践课比例’
     *
     * @param response
     * @param topTitleRow 第一行的表头
     * @param titleRow    第二行的表头
     * @param data        数据, name为第一列， number1为第三列， number2为第四列；第二列小计及底部合计栏自动计算
     */
    public static void complexStatisticsExport(HttpServletResponse response, List<String> topTitleRow, List<String> titleRow, List<StatisticsExcelVo> data) {

        XSSFWorkbook xssfWorkbook = new XSSFWorkbook();
        XSSFSheet sheet = xssfWorkbook.createSheet(DomainFieldConstant.STATISTICS_SHEET_NAME);
        //设置一个基础的单元格格式
        XSSFCellStyle cellStyle = xssfWorkbook.createCellStyle();
        cellStyle.setWrapText(true);
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        cellStyle.setAlignment(HorizontalAlignment.CENTER);

        //第一行表头
        XSSFRow topRow = sheet.createRow(0);
        for (int i = 0; i < topTitleRow.size(); i++) {
            XSSFCell cell = topRow.createCell(i);
            cell.setCellValue(topTitleRow.get(i));
            cell.setCellStyle(cellStyle);
        }
        //第二行表头
        XSSFRow secondRow = sheet.createRow(1);
        for (int i = 0; i < titleRow.size(); i++) {
            XSSFCell cell = secondRow.createCell(i + 1);
            cell.setCellValue(titleRow.get(i));
            cell.setCellStyle(cellStyle);
        }

        //合并两个单元格
        CellRangeAddress cellAddresses1 = new CellRangeAddress(0, 1, 0, 0);
        CellRangeAddress cellAddresses2 = new CellRangeAddress(0, 0, 1, 3);
        sheet.addMergedRegion(cellAddresses1);
        sheet.addMergedRegion(cellAddresses2);

        //写内容
        String startColString = CellReference.convertNumToColString(2);
        String endColString = CellReference.convertNumToColString(3);
        for (int i = 0; i < data.size(); i++) {
            StatisticsExcelVo statisticsExcelVo = data.get(i);
            XSSFRow row = sheet.createRow(i + 2);
            row.createCell(0).setCellValue(statisticsExcelVo.getName());
            //第二列为第三列第四列之和
            row.createCell(1).setCellFormula("SUM(" + startColString + (i + 3) + ":" + endColString + (i + 3) + ")");
            row.createCell(2).setCellValue(statisticsExcelVo.getNumber1());
            row.createCell(3).setCellValue(statisticsExcelVo.getNumber2());
        }
        //最后一行求和
        XSSFRow row = sheet.createRow(data.size() + 2);
        row.createCell(0).setCellValue("合计栏");
        String colString1 = CellReference.convertNumToColString(1);
        row.createCell(1).setCellFormula("SUM(" + colString1 + "3:" + colString1 + (data.size() + 2) + ")");
        String colString2 = CellReference.convertNumToColString(2);
        row.createCell(2).setCellFormula("SUM(" + colString2 + "3:" + colString2 + (data.size() + 2) + ")");
        String colString3 = CellReference.convertNumToColString(3);
        row.createCell(3).setCellFormula("SUM(" + colString3 + "3:" + colString3 + (data.size() + 2) + ")");

        writeExcelToResponse(response, xssfWorkbook);
    }

    /**
     * 较为复杂的excel导出，格式类似’必修与选修课比例‘或者‘讲授、实践课比例’
     *
     * @param response
     * @param topTitleRow 第一行的表头
     * @param titleRow    第二行的表头
     * @param data        数据, name为第一列， number1为第三列， number2为第四列；第二列小计及底部合计栏自动计算
     */
    public static void complexStatisticsMultiExport(HttpServletResponse response, List<String> topTitleRow, List<String> titleRow, List<StatisticsExcelMultiVo> data) {

        XSSFWorkbook xssfWorkbook = new XSSFWorkbook();
        XSSFSheet sheet = xssfWorkbook.createSheet(DomainFieldConstant.STATISTICS_SHEET_NAME);
        //设置一个基础的单元格格式
        XSSFCellStyle cellStyle = xssfWorkbook.createCellStyle();
        cellStyle.setWrapText(true);
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        cellStyle.setAlignment(HorizontalAlignment.CENTER);

        //第一行表头
        XSSFRow topRow = sheet.createRow(0);
        for (int i = 0; i < topTitleRow.size(); i++) {
            XSSFCell cell = topRow.createCell(i);
            cell.setCellValue(topTitleRow.get(i));
            cell.setCellStyle(cellStyle);
        }
        //第二行表头
        XSSFRow secondRow = sheet.createRow(1);
        for (int i = 0; i < titleRow.size(); i++) {
            XSSFCell cell = secondRow.createCell(i + 2);
            cell.setCellValue(titleRow.get(i));
            cell.setCellStyle(cellStyle);
        }

        //合并单元格
        CellRangeAddress cellAddresses1 = new CellRangeAddress(0, 1, 0, 0);
        CellRangeAddress cellAddresses2 = new CellRangeAddress(0, 1, 1, 1);
        CellRangeAddress cellAddresses3 = new CellRangeAddress(0, 0, 2, 4);
        sheet.addMergedRegion(cellAddresses1);
        sheet.addMergedRegion(cellAddresses2);
        sheet.addMergedRegion(cellAddresses3);

        //写内容
        String startColString = CellReference.convertNumToColString(3);
        String endColString = CellReference.convertNumToColString(4);
        int sumRow = 0;
        for(int m = 0; m < data.size(); m++){
            StatisticsExcelMultiVo vo = data.get(m);
            for (int i = 0; i < vo.getStatisticsExcelVos().size(); i++) {
                StatisticsExcelVo statisticsExcelVo = vo.getStatisticsExcelVos().get(i);
                XSSFRow row = sheet.createRow(sumRow + i + 2);
                row.createCell(0).setCellValue(vo.getSchemeName());
                row.createCell(1).setCellValue(statisticsExcelVo.getName());
                //第二列为第三列第四列之和
                row.createCell(2).setCellFormula("SUM(" + startColString + (sumRow + i + 3) + ":" + endColString + (sumRow + i + 3) + ")");
                row.createCell(3).setCellValue(statisticsExcelVo.getNumber1());
                row.createCell(4).setCellValue(statisticsExcelVo.getNumber2());
            }

            //最后一行求和
            XSSFRow row = sheet.createRow(sumRow + vo.getStatisticsExcelVos().size() + 2);
            row.createCell(0).setCellValue(vo.getSchemeName());
            row.createCell(1).setCellValue("合计栏");
            String colString1 = CellReference.convertNumToColString(2);
            row.createCell(2).setCellFormula("SUM(" + colString1 + (sumRow + 3) + ":" + colString1 + (sumRow + vo.getStatisticsExcelVos().size() + 2) + ")");
            String colString2 = CellReference.convertNumToColString(3);
            row.createCell(3).setCellFormula("SUM(" + colString2 + (sumRow + 3) + ":" + colString2 + (sumRow + vo.getStatisticsExcelVos().size() + 2) + ")");
            String colString3 = CellReference.convertNumToColString(4);
            row.createCell(4).setCellFormula("SUM(" + colString3 + (sumRow + 3) + ":" + colString3 + (sumRow + vo.getStatisticsExcelVos().size() + 2) + ")");
            CellRangeAddress cellAddresses = new CellRangeAddress(sumRow + 2, sumRow + vo.getStatisticsExcelVos().size() + 2, 0, 0);
            sheet.addMergedRegion(cellAddresses);
            sumRow += vo.getStatisticsExcelVos().size() + 1;
        }

        writeExcelToResponse(response, xssfWorkbook);
    }

    /**
     * @param response
     * @param titleList 第一行的表头的集合
     * @param data      数据（按表头顺序）
     */
    public static void exportSimpleExcel(HttpServletResponse response, List<String> titleList, List<List<String>> data,boolean flag) {
        XSSFWorkbook xssfWorkbook = new XSSFWorkbook();
        XSSFSheet sheet = xssfWorkbook.createSheet(DomainFieldConstant.STATISTICS_SHEET_NAME);
        XSSFRow titleRow = sheet.createRow(0);

        //整数型的单元格格式
        XSSFCellStyle longCellStyle = xssfWorkbook.createCellStyle();
        longCellStyle.setWrapText(true);
        longCellStyle.setDataFormat(xssfWorkbook.createDataFormat().getFormat("0"));

        //浮点型的单元格格式
        XSSFCellStyle doubleCellStyle = xssfWorkbook.createCellStyle();
        doubleCellStyle.setWrapText(true);
        doubleCellStyle.setDataFormat(xssfWorkbook.createDataFormat().getFormat("0.00"));

        //表头
        for (int i = 0; i < titleList.size(); i++) {
            XSSFCell cell = titleRow.createCell(i);
            cell.setCellValue(titleList.get(i));
        }

        //写内容
        for (int i = 0; i < data.size(); i++) {
            List<String> line = data.get(i);
            XSSFRow row = sheet.createRow(i + 1);
            for (int j = 0; j < line.size(); j++) {
                XSSFCell cell = row.createCell(j);
                cell.setCellValue(line.get(j));
                try {
                    long value = Long.parseLong(line.get(j));
                    cell.setCellValue(value);
                    cell.setCellStyle(longCellStyle);
                } catch (NumberFormatException e) {
                    try {
                        double value = Double.parseDouble(line.get(j));
                        cell.setCellValue(value);
                        cell.setCellStyle(doubleCellStyle);
                    } catch (NumberFormatException e2) {
                        cell.setCellValue(line.get(j));
                    }
                }
            }
        }
        if (flag){
            String colString1 = CellReference.convertNumToColString(1);
            String colString2 = CellReference.convertNumToColString(2);
            XSSFRow row = sheet.createRow(data.size() + 1);
            row.createCell(0).setCellValue("合计");
            row.createCell(1).setCellFormula("SUM(" + colString1 + "2:" + colString1 + (data.size() + 1) + ")");
            row.createCell(2).setCellFormula("SUM(" + colString2 + "2:" + colString2 + (data.size() + 1) + ")");
        }
        writeExcelToResponse(response, xssfWorkbook);
    }

    public static XSSFWorkbook getKnowledgeCheckLog(List<KnowledgeCheckLog> knowledgeCheckLogList) {
        XSSFWorkbook xssfWorkbook = new XSSFWorkbook();
        XSSFSheet sheet = xssfWorkbook.createSheet("sheet1");

        XSSFCellStyle cellStyle = xssfWorkbook.createCellStyle();

        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        cellStyle.setAlignment(HorizontalAlignment.CENTER);

        XSSFRow row0 = sheet.createRow(0);
        row0.createCell(0).setCellValue("序号");
        row0.createCell(1).setCellValue("课程名称");
        row0.createCell(2).setCellValue("知识单元");
        row0.createCell(3).setCellValue("知识点");
        for (int i = 0; i < knowledgeCheckLogList.size(); i++) {
            int row1Index = i * 2 + 1;
            int row2Index = row1Index + 1;
            XSSFRow row1 = sheet.createRow(row1Index);
            XSSFRow row2 = sheet.createRow(row2Index);
            KnowledgeCheckLog vo = knowledgeCheckLogList.get(i);
            row1.createCell(0).setCellValue(i + 1);
            row1.createCell(1).setCellValue(vo.getSourceCourseName());
            row1.createCell(2).setCellValue(vo.getSourceUnitName());
            row1.createCell(3).setCellValue(vo.getSourcePointName());
            row2.createCell(1).setCellValue(vo.getTargetCourseName());
            row2.createCell(2).setCellValue(vo.getTargetUnitName());
            row2.createCell(3).setCellValue(vo.getTargetPointName());
            CellRangeAddress cellAddresses1 = new CellRangeAddress(row1Index, row2Index, 0, 0);
            sheet.addMergedRegion(cellAddresses1);
        }
        return xssfWorkbook;
    }

    /**
     * 读取Excel文件
     *
     * @param file
     * @return
     */
    public static Map<String,Object> readGraduationRefCultivationTargetExcel(MultipartFile file) {
        try (InputStream is = file.getInputStream(); Workbook workbook = WorkbookFactory.create(is)) {
            Sheet sheet = null;
            int index = 0;
            while (ObjectUtils.isEmpty(sheet) && index < workbook.getNumberOfSheets()) {
                sheet = workbook.getSheetAt(index++);
                if (sheet.getLastRowNum() < 1) {
                    sheet = null;
                }
            }
            return readRef(sheet);
        } catch (IOException e) {
            e.printStackTrace();
        }
        throw new FileException(file.getOriginalFilename() + DomainExceptionConstant.EXCEL_HAS_NO_SHEET);
    }

    /**
     * 解析数据
     *
     * @param sheet 表格sheet对象
     * @return
     * @throws IOException
     */
    private static Map<String,Object> readRef(Sheet sheet) throws IOException {
        Map<String,Object> result = Maps.newHashMap();
        //横向list
        Map<Integer, String> horizontal = Maps.newHashMap();
        //竖向list
        Map<Integer, String> vertical = Maps.newHashMap();
        //数据list
        List dataList = Lists.newArrayList();

        int rowStart = sheet.getFirstRowNum();  // 首行下标
        int rowEnd = sheet.getLastRowNum();  // 尾行下标
        for(int i = rowStart; i <= rowEnd; i++){
            Row row = sheet.getRow(i);
            if(i == 0){
                Cell cell = row.getCell(0);
                String val = cell.getStringCellValue();
                String[] vals = val.replace("支撑关系矩阵","").split(" 对 ");
                Map<String,String> map = Maps.newHashMap();
                map.put(DomainFieldConstant.STANDARD_GRADUATION,vals[0]);
                map.put(DomainFieldConstant.STANDARD_CULTIVATION_TARGET,vals[1]);
                result.put("first",map);
                continue;
            }
            int cellStart = row.getFirstCellNum();
            int cellEnd = row.getLastCellNum();
            if(i < 4){
                for(int j = cellStart; j <= cellEnd; j++){
                    if(j < 3){
                        continue;
                    }
                    Cell cell = row.getCell(j);
                    if(ObjectUtils.isEmpty(cell)){
                        continue;
                    }
                    horizontal.put(j,cell.getStringCellValue());
                }
            }else{
                for(int j = cellStart; j <= cellEnd; j++){
                    Cell cell = row.getCell(j);
                    if(ObjectUtils.isEmpty(cell)){
                        continue;
                    }
                    String cellValue = "";
                    if (cell.getCellType() == CellType.STRING) {
                        cellValue = cell.getStringCellValue();
                    }
                    if (cell.getCellType() == CellType.NUMERIC) {
                        cellValue = (int)cell.getNumericCellValue() + "";
                    }
                    if(j < 3 && StringUtils.isNotBlank(cellValue)){
                        vertical.put(i,cellValue);
                    }
                    if(j >= 3 && DomainFieldConstant.EXCEL_CHOOSE.equals(cellValue)){
                        dataList.add(vertical.get(i) + "," + horizontal.get(j));
                    }
                }
            }
        }
        result.put("horizontal",horizontal);
        result.put("vertical",vertical);
        result.put("dataList",dataList);
        return result;
    }

    /**
     * 读取Excel文件
     *
     * @param file
     * @return
     */
    public static Map<String,Object> readCultivationRefGraduationExcel(MultipartFile file) {
        try (InputStream is = file.getInputStream(); Workbook workbook = WorkbookFactory.create(is)) {
            Sheet sheet = null;
            int index = 0;
            while (ObjectUtils.isEmpty(sheet) && index < workbook.getNumberOfSheets()) {
                sheet = workbook.getSheetAt(index++);
                if (sheet.getLastRowNum() < 1) {
                    sheet = null;
                }
            }
            return readCultivationRefGraduation(sheet);
        } catch (IOException e) {
            e.printStackTrace();
        }
        throw new FileException(file.getOriginalFilename() + DomainExceptionConstant.EXCEL_HAS_NO_SHEET);
    }

    /**
     * 解析数据
     *
     * @param sheet 表格sheet对象
     * @return
     * @throws IOException
     */
    private static Map<String,Object> readCultivationRefGraduation(Sheet sheet) throws IOException {
        Map<String,Object> result = Maps.newHashMap();
        //横向list
        Map<Integer, String> horizontal = Maps.newHashMap();
        //竖向list
        Map<Integer, String> vertical = Maps.newHashMap();
        //数据list
        List dataList = Lists.newArrayList();

        int rowStart = sheet.getFirstRowNum();  // 首行下标
        int rowEnd = sheet.getLastRowNum();  // 尾行下标
        for(int i = rowStart; i <= rowEnd; i++){
            Row row = sheet.getRow(i);
            if(i == 0){
                Cell cell = row.getCell(0);
                String val = cell.getStringCellValue();
                String[] vals = val.replace("支撑关系矩阵","").split(" 对 ");
                Map<String,String> map = Maps.newHashMap();
                map.put(DomainFieldConstant.STANDARD_CULTIVATION,vals[0]);
                map.put(DomainFieldConstant.STANDARD_GRADUATION,vals[1]);
                result.put("first",map);
                continue;
            }
            int cellStart = row.getFirstCellNum();
            int cellEnd = row.getLastCellNum();
            if(i < 4){
                for(int j = cellStart; j <= cellEnd; j++){
                    if(j < 6){
                        continue;
                    }
                    Cell cell = row.getCell(j);
                    if(ObjectUtils.isEmpty(cell)){
                        continue;
                    }
                    horizontal.put(j,cell.getStringCellValue());
                }
            }else{
                for(int j = cellStart; j <= cellEnd; j++){
                    Cell cell = row.getCell(j);
                    if(ObjectUtils.isEmpty(cell)){
                        continue;
                    }
                    String cellValue = "";
                    if (cell.getCellType() == CellType.STRING) {
                        cellValue = cell.getStringCellValue();
                    }
                    if (cell.getCellType() == CellType.NUMERIC) {
                        cellValue = (int)cell.getNumericCellValue() + "";
                    }
                    if(j < 6 && StringUtils.isNotBlank(cellValue)){
                        vertical.put(i,cellValue);
                    }
                    if(j >= 6 && DomainFieldConstant.EXCEL_CHOOSE.equals(cellValue)){
                        dataList.add(vertical.get(i) + "," + horizontal.get(j));
                    }
                }
            }
        }
        result.put("horizontal",horizontal);
        result.put("vertical",vertical);
        result.put("dataList",dataList);
        return result;
    }


    /**
     *  根据inputStream 生成excel 文件
     * @param inputStream
     * @param response
     */
    public static void exportTemplate(InputStream inputStream, HttpServletResponse response){
        try {
            // 设置正确的响应头
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("Content-Disposition", "attachment; filename=template.xlsx");
            response.setCharacterEncoding("UTF-8");
            XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
            ServletOutputStream outputStream = response.getOutputStream();
            workbook.write(outputStream);
            workbook.close();
            inputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}







