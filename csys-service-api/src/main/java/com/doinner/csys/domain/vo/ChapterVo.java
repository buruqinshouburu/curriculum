package com.doinner.csys.domain.vo;

import java.util.List;
import java.util.Map;

public class ChapterVo {

    private Boolean isTable;

    private List<TableHeaderVo> header;

    private List<Map<String, String>> body;

    public Boolean getTable() {
        return isTable;
    }

    public void setTable(Boolean table) {
        isTable = table;
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
}
