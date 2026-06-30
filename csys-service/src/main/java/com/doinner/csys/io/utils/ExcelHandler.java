package com.doinner.csys.io.utils;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.util.List;

public abstract class ExcelHandler<T> {

    protected List<T> data;

    protected XSSFWorkbook xssfWorkbook;

    public ExcelHandler(List<T> data) {
        this.data = data;
    }

    abstract public void writeSheet();

    public XSSFWorkbook writeToExcel(){
        createWorkBook();
        writeSheet();
        return xssfWorkbook;
    }

    protected void createWorkBook(){
        xssfWorkbook = new XSSFWorkbook();
    }

    protected Sheet createSheet(String sheetName){
        Sheet sheet = xssfWorkbook.createSheet(sheetName);
        return sheet;
    }
}
