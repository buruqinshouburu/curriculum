package com.doinner.csys.utils;

import fr.opensagres.xdocreport.document.IXDocReport;
import fr.opensagres.xdocreport.document.images.ByteArrayImageProvider;
import fr.opensagres.xdocreport.document.images.IImageProvider;
import fr.opensagres.xdocreport.template.IContext;
import fr.opensagres.xdocreport.template.formatter.FieldsMetadata;
import org.springframework.core.io.ClassPathResource;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

public class ExportData {

    private IXDocReport report;
    private IContext context;

    /**
     * 构造方法
     * @param report
     * @param context
     */
    public ExportData(IXDocReport report, IContext context) {
        this.report = report;
        this.context = context;
    }

    /**
     * 设置普通数据， 包括基础数据类型，数组，对象
     * @param key 键
     * @param value 值
     */
    public void setData(String key, Object value){
        context.put(key,value);
    }

    public void setDataStrings(String key, List<String> strings){
        String value;
        if (null == strings || strings.size() == 0){
            value = "";
        }else {
            value = String.join("，",strings);
        }
        context.put(key,value);
    }

    /**
     * 设置表格数据，用来循环生成表格的 list 数据
     * @param key
     * @param maps
     */
    public void setTable(String key, List<SoMap> maps){
        FieldsMetadata metadata = report.getFieldsMetadata();
        metadata = metadata == null ? new FieldsMetadata() : metadata;
        SoMap map = maps.get(0);
        for (String kk:map.keySet()){
            metadata.addFieldAsList(key+"."+kk);
        }
        report.setFieldsMetadata(metadata);
        context.put(key, maps);
    }

    /**
     * 设置图片数据
     * @param key 键
     * @param url 图片地址
     */
    public void setImg(String key, String url){
        FieldsMetadata metadata = report.getFieldsMetadata();
        metadata = metadata == null ? new FieldsMetadata() : metadata;
        metadata.addFieldAsImage(key);
        report.setFieldsMetadata(metadata);
        try (InputStream in = new ClassPathResource(url).getInputStream()) {
            IImageProvider img = new ByteArrayImageProvider(in);
            context.put(key,img);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public byte[] getByteArr(){
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            report.process(context, out);
            return out.toByteArray();
        } catch (Exception e){
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }

    public void process(OutputStream outputStream){
        try {
            report.process(context, outputStream);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
