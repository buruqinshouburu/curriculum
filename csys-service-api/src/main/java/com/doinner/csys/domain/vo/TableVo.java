package com.doinner.csys.domain.vo;

import java.util.List;
import java.util.Map;

public class TableVo {

    private String titleValue;
    private List<TableHeaderVo> header;
    private List<Map<String, String>> body;
    private List<String> inputValue;
    private boolean isTable;

    public String getTitleValue() {
        return titleValue;
    }

    public void setTitleValue(String titleValue) {
        this.titleValue = titleValue;
    }

    public List<TableHeaderVo> getHeader() {
        return header;
    }

    public void setHeader(List<TableHeaderVo> header) {
        this.header = header;
    }

    public List<Map<String, String>> getBody() {
        return body;
    }

    public void setBody(List<Map<String, String>> body) {
        this.body = body;
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
