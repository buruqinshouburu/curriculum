package com.doinner.csys.domain.vo;

import java.util.List;
import java.util.Map;

public class Table {
    private String titleValue;
    private Map<String, String[]> rows;
    private List<String> inputValue;
    private boolean isTable;

    public String getTitleValue() {
        return titleValue;
    }

    public void setTitleValue(String titleValue) {
        this.titleValue = titleValue;
    }

    public Map<String, String[]> getRows() {
        return rows;
    }

    public void setRows(Map<String, String[]> rows) {
        this.rows = rows;
    }

    public List<String> getInputValue() {
        return inputValue;
    }

    public void setInputValue(List<String> inputValue) {
        this.inputValue = inputValue;
    }

    public boolean isTable() {
        return isTable;
    }

    public void setTable(boolean table) {
        isTable = table;
    }
}
