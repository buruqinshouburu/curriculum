package com.doinner.csys.io.utils;

import com.doinner.common.core.exception.DataFormatException;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.IntStream;

public class SimpleExcelHandler<T> extends ExcelHandler<T> {

    private Map<String, Function<T, Object>> fieldMap = new HashMap<>();

    private List<String> keys = new ArrayList<>();

    private String sheetName;

    public SimpleExcelHandler(List<T> data) {
        super(data);
    }

    public SimpleExcelHandler<T> addHeader(String tableHeaderName){
        this.keys.add(tableHeaderName);
        return this;
    }

    public SimpleExcelHandler<T> setSheetName(String sheetName){
        this.sheetName = sheetName;
        return this;
    }

    public final SimpleExcelHandler<T> addMappingFunction(Function<T, Object> function){
        if(ObjectUtils.isEmpty(this.keys)){
            throw new DataFormatException("未设置key");
        }
        fieldMap.put(this.keys.get(this.keys.size() - 1), function);
        return this;
    }

    public void writeSheet(){
        Sheet sheet = super.createSheet(sheetName);
        Row titleRow = sheet.createRow(0);
        for (int i = 0; i < this.keys.size(); i++) {
            Cell cell = titleRow.createCell(i);
            cell.setCellValue(this.keys.get(i));
        }
        int[] columnMaxLengthArray = new int[keys.size()];
        IntStream.range(0, super.data.size()).forEach(index -> {
            Row row = sheet.createRow(index + 1);
            T t = this.data.get(index);
            for (int i = 0; i < this.keys.size(); i++) {
                Cell cell = row.createCell(i);
                Function<T, Object> mappingFunction = fieldMap.get(this.keys.get(i));
                Object value = mappingFunction.apply(t);
                if(ObjectUtils.isNotEmpty(value)) {
                    cell.setCellValue(value.toString());
                    if(columnMaxLengthArray[i] < value.toString().length()){
                        columnMaxLengthArray[i] = value.toString().length();
                    }
                }
            }
        });
        for (int i = 0; i < columnMaxLengthArray.length; i++) {
            String title = this.keys.get(i);
            if(title.length() > columnMaxLengthArray[i]){
                sheet.setColumnWidth(i, title.length() * 768);
            }else {
                sheet.setColumnWidth(i, columnMaxLengthArray[i] * 768);
            }
        }
    }

}
