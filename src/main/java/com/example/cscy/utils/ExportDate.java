package com.example.cscy.utils;

import fr.opensagres.xdocreport.document.IXDocReport;
import fr.opensagres.xdocreport.template.IContext;
import fr.opensagres.xdocreport.template.formatter.FieldsMetadata;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

public class ExportDate {
    private IXDocReport report;
    private IContext context;
    private FieldsMetadata metadata;

    public ExportDate(IXDocReport report, IContext context) {
        this.report = report;
        this.context = context;
        this.metadata = report.getFieldsMetadata();
        if (this.metadata == null) {
            this.metadata = new FieldsMetadata();
        }
    }

    public void setDate(String key,Object value) {
        context.put(key, value);
    }

    public void setDataStrings(String key, List<String> strings) {
        String value;
        if(strings==null||strings.size()==0){
            value="";
        }else {
            value=String.join(",", strings);
        }
        context.put(key, value);
    }

    public void setTable(String key,List<SoMap> maps) {
        if (maps == null || maps.isEmpty()) {
            context.put(key, maps);
            return;
        }
        SoMap map = maps.get(0);
        for (String kk : map.keySet()) {
            metadata.addFieldAsList(key + "." + kk);
        }
        report.setFieldsMetadata(metadata);
        context.put(key, map);
    }

    /**
     * 设置表格数据，支持嵌套字段
     *
     * @param key        表格键名
     * @param maps       表格数据
     * @param fieldNames 字段名列表（用于元数据注册）
     */
    public void setTableWithMetadata(String key, List<SoMap> maps, String[] fieldNames) {
        if (maps == null || maps.isEmpty()) {
            context.put(key, maps);
            return;
        }

        // 注册字段元数据
        for (String fieldName : fieldNames) {
            metadata.addFieldAsList(key + "." + fieldName);
        }
        report.setFieldsMetadata(metadata);

        // 将数据放入上下文
        context.put(key, maps);
    }

    /**
     * 设置小计数据
     *
     * @param key          小计键名
     * @param subtotalData 小计数据
     */
    public void setSubtotal(String key, SoMap subtotalData) {
        if (subtotalData == null) {
            context.put(key, new SoMap());
            return;
        }
        context.put(key, subtotalData);
    }

    /**
     * 批量设置表格数据
     *
     * @param tableDataMap 表格数据映射
     */
    public void setTables(Map<String, List<SoMap>> tableDataMap) {
        if (tableDataMap == null || tableDataMap.isEmpty()) {
            return;
        }

        for (Map.Entry<String, List<SoMap>> entry : tableDataMap.entrySet()) {
            String key = entry.getKey();
            List<SoMap> maps = entry.getValue();

            if (maps != null && !maps.isEmpty()) {
                SoMap map = maps.get(0);
                for (String kk : map.keySet()) {
                    metadata.addFieldAsList(key + "." + kk);
                }
            }
        }

        report.setFieldsMetadata(metadata);

        // 将所有表格数据放入上下文
        for (Map.Entry<String, List<SoMap>> entry : tableDataMap.entrySet()) {
            context.put(entry.getKey(), entry.getValue());
        }
    }

    public byte[] getByteArr() {

        try (ByteArrayOutputStream out = new ByteArrayOutputStream()){
            report.process(context, out);
            return out.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void process(OutputStream outputStream) {
        try {
            report.process(context,outputStream);
        }  catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
