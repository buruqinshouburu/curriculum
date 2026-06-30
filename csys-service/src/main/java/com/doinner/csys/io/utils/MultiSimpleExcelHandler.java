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

public class MultiSimpleExcelHandler<T> extends SimpleExcelHandler<T> {

    private List<String> sheetNames = new ArrayList<>();

    private Map<String, List<T>> data = new HashMap<>();

    public MultiSimpleExcelHandler(List<String> sheetNames, Map<String, List<T>> data) {
        super(null);
        this.sheetNames = sheetNames;
        this.data = data;
    }

    public MultiSimpleExcelHandler<T> setSheetName(String sheetName){
        this.sheetNames.add(sheetName);
        return this;
    }

    public void writeSheet(){
        sheetNames.forEach(sheetName -> {
            super.setSheetName(sheetName);
            super.data = data.get(sheetName);
            super.writeSheet();
        });
    }

}
