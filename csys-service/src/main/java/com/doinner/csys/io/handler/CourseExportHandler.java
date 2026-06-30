package com.doinner.csys.io.handler;

import com.doinner.csys.constant.DomainFieldConstant;
import com.doinner.csys.domain.vo.TreeTableVo;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class CourseExportHandler {

    private String excelName;

    private String sheetName;

    private Sheet sheet;

    private XSSFCellStyle cellStyle;

    private List<String> courseTitleList = List.of(DomainFieldConstant.EXCEL_TITLE_SERIAL_NUMBER,
            DomainFieldConstant.EXCEL_TITLE_COURSE_NAME, DomainFieldConstant.EXCEL_TITLE_CHARGE,
            DomainFieldConstant.EXCEL_TITLE_AUTHORS, DomainFieldConstant.EXCEL_TITLE_HOURS,
            DomainFieldConstant.EXCEL_TITLE_THEORY_HOURS, DomainFieldConstant.EXCEL_TITLE_PRACTICE_HOURS,
            DomainFieldConstant.EXCEL_TITLE_EXA_METHOD, DomainFieldConstant.EXCEL_TITLE_TEXT_BOOK,
            DomainFieldConstant.EXCEL_TITLE_REFERENCE_BOOK, DomainFieldConstant.EXCEL_TITLE_BEFORE_COURSE,
            DomainFieldConstant.EXCEL_TITLE_AFTER_COURSE, DomainFieldConstant.EXCEL_TITLE_SUMMARY);

    private List<String> unitTitleList = List.of(DomainFieldConstant.EXCEL_TITLE_UNIT, DomainFieldConstant.EXCEL_TITLE_UNIT_REMARK);

    private List<String> pointTitleList = List.of(DomainFieldConstant.EXCEL_TITLE_POINT, DomainFieldConstant.EXCEL_TITLE_POINT_POWER,
            DomainFieldConstant.EXCEL_TITLE_POINT_BEFORE_KNOWLEDGE);

    private List<TreeTableVo> data;

    private List<CellRangeAddress> cellRangeAddresseList;

    public CourseExportHandler(List<TreeTableVo> data, String excelName, String sheetName) {
        this.excelName = excelName;
        this.sheetName = sheetName;
        this.data = data;
    }

    public CourseExportHandler() {

    }

    public XSSFWorkbook writeVerticalTreeTable(){
        cellRangeAddresseList = new ArrayList<>();
        XSSFWorkbook xssfWorkbook = new XSSFWorkbook();
        cellStyle = xssfWorkbook.createCellStyle();
        cellStyle.setWrapText(true);
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        sheet = xssfWorkbook.createSheet(sheetName);
        //写入首行标题
        Integer rowIndex = 0;
        Row row = sheet.createRow(rowIndex++);
        int cellNumber = 0;
        for (int i = 0; i < courseTitleList.size(); i++) {
            Cell cell = row.createCell(cellNumber + i);
            cell.setCellStyle(cellStyle);
            cell.setCellValue(courseTitleList.get(i));
        }
        cellNumber+=courseTitleList.size();
        for (int i = 0; i < unitTitleList.size(); i++) {
            Cell cell = row.createCell(cellNumber + i);
            cell.setCellStyle(cellStyle);
            cell.setCellValue(unitTitleList.get(i));
        }
        cellNumber+=unitTitleList.size();
        for (int i = 0; i < pointTitleList.size(); i++) {
            Cell cell = row.createCell(cellNumber + i);
            cell.setCellStyle(cellStyle);
            cell.setCellValue(pointTitleList.get(i));
        }
        while(true){
            List<String> rowData = getCourseData(this.data, rowIndex - 1);
            if(ObjectUtils.isEmpty(rowData)){
                break;
            }
            row = sheet.createRow(rowIndex++);
            AtomicInteger cellIndex = new AtomicInteger(0);
            for (String rowDatum : rowData) {
                Cell cell = row.createCell(cellIndex.getAndIncrement());
                cell.setCellStyle(cellStyle);
                cell.setCellValue(rowDatum);
            }
        }
        cellRangeAddresseList.forEach(cellAddresses -> {
            sheet.addMergedRegion(cellAddresses);
        });
        return xssfWorkbook;
    }

    private List<String> getCourseData(List<TreeTableVo> data, int rowNumber){
        List<String> values = new ArrayList<>();
        int index = 0;
        TreeTableVo currentVo = data.get(index++);
        int startRowNumber = rowNumber;
        while(rowNumber >= currentVo.getSize()){
            if(index >= data.size()){
                return null;
            }
            rowNumber -= currentVo.getSize();
            currentVo = data.get(index++);
        }
        if(rowNumber == 0) {
            values.add(String.valueOf(data.indexOf(currentVo) + 1));
            for (int i = 0; i < courseTitleList.size(); i++) {
                String title = courseTitleList.get(i);
                if (title.equals(DomainFieldConstant.EXCEL_TITLE_COURSE_NAME)) {
                    values.add(currentVo.getName());
                }else if (currentVo.getParams().containsKey(title)) {
                    values.add(currentVo.getParams().get(title));
                }else{
                    if (!title.equals(DomainFieldConstant.EXCEL_TITLE_SERIAL_NUMBER)){
                        values.add("");
                    }
                }
                if(currentVo.getSize() > 1) {
                    CellRangeAddress cellAddresses = new CellRangeAddress(startRowNumber + 1, startRowNumber + currentVo.getSize(), values.size() - 1, values.size() - 1);
                    cellRangeAddresseList.add(cellAddresses);
                }
            }
        }else{
            for (int i = 0; i < courseTitleList.size(); i++) {
                values.add("");
            }
        }
        if(ObjectUtils.isNotEmpty(currentVo.getChildren())) {
            List<String> unitData = getUnitData(currentVo.getChildren(), rowNumber, courseTitleList.size(), startRowNumber);
            if(ObjectUtils.isNotEmpty(unitData)) {
                values.addAll(unitData);
            }
        }
        return values;
    }

    private List<String> getUnitData(List<TreeTableVo> data, int rowNumber, int columnStart, int rowStart){
        List<String> values = new ArrayList<>();
        int index = 0;
        TreeTableVo currentVo = data.get(index++);
        while(rowNumber >= currentVo.getSize()){
            if(index >= data.size()){
                return null;
            }
            currentVo = data.get(index++);
            rowNumber -= currentVo.getSize();
        }
        if(rowNumber == 0) {
            for (int i = 0; i < unitTitleList.size(); i++) {
                String title = unitTitleList.get(i);
                if (title.equals(DomainFieldConstant.EXCEL_TITLE_UNIT)) {
                    values.add(currentVo.getName());
                }else if (currentVo.getParams().containsKey(title)) {
                    values.add(currentVo.getParams().get(title));
                }else{
                    values.add("");
                }
                if(currentVo.getSize() > 1) {
                    CellRangeAddress cellAddresses = new CellRangeAddress(rowStart + 1, rowStart + currentVo.getSize(), columnStart + values.size() - 1, columnStart + values.size() - 1);
                    cellRangeAddresseList.add(cellAddresses);
                }
            }
        }else{
            for (int i = 0; i < unitTitleList.size(); i++) {
                values.add("");
            }
        }
        if(ObjectUtils.isNotEmpty(currentVo.getChildren())) {
            values.addAll(getPointData(currentVo.getChildren(), rowNumber));
        }
        return values;
    }

    private List<String> getPointData(List<TreeTableVo> data, int rowNumber){
        List<String> values = new ArrayList<>();
        int index = 0;
        TreeTableVo currentVo = data.get(index++);
        while(rowNumber >= currentVo.getSize()){
            if(index > data.size()){
                return null;
            }
            currentVo = data.get(index++ - 1);
            rowNumber -= currentVo.getSize();
        }
        if(rowNumber == 0) {
            for (int i = 0; i < pointTitleList.size(); i++) {
                String title = pointTitleList.get(i);
                if (title.equals(DomainFieldConstant.EXCEL_TITLE_POINT)) {
                    values.add(currentVo.getName());
                    continue;
                }
                if (currentVo.getParams().containsKey(title)) {
                    values.add(currentVo.getParams().get(title));
                }else{
                    values.add("");
                }
            }
        }else{
            for (int i = 0; i < pointTitleList.size(); i++) {
                values.add("");
            }
        }
        return values;
    }


    public String getExcelName() {
        return excelName;
    }

    public void setExcelName(String excelName) {
        this.excelName = excelName;
    }

    public String getSheetName() {
        return sheetName;
    }

    public void setSheetName(String sheetName) {
        this.sheetName = sheetName;
    }
}
