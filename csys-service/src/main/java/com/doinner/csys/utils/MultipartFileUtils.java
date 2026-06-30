package com.doinner.csys.utils;

import com.doinner.file.api.constant.DomainFieldConstants;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import java.io.*;

public class MultipartFileUtils {

    public static MultipartFile getMultipartFile(File file) {
        if (ObjectUtils.isEmpty(file)){
            throw new IllegalArgumentException("文件转换错误：file参数为空");
        }
        FileItem fileItem = new DiskFileItemFactory().createItem(DomainFieldConstants.FILE,
                MediaType.MULTIPART_FORM_DATA_VALUE,
                true, file.getName());
        try (InputStream inputStream = FileUtils.openInputStream(file); OutputStream os = fileItem.getOutputStream()) {
            IOUtils.copyLarge(inputStream, os);
        } catch (IOException e) {
            throw new IllegalArgumentException("文件转换错误：" + e, e);
        }
        return new CommonsMultipartFile(fileItem);
    }

    public static MultipartFile getMultipartFile(InputStream fileInputStream, String fileName) {
        if (ObjectUtils.isEmpty(fileInputStream)){
            throw new IllegalArgumentException("文件转换错误：fileInputStream参数为空");
        }
        if (StringUtils.isBlank(fileName)){
            throw new IllegalArgumentException("文件转换错误：fileName参数为空");
        }
        FileItem fileItem = new DiskFileItemFactory().createItem(DomainFieldConstants.FILE,
                MediaType.MULTIPART_FORM_DATA_VALUE,
                true, fileName);
        try (InputStream in = fileInputStream; OutputStream os = fileItem.getOutputStream()) {
            IOUtils.copyLarge(in, os);
        } catch (IOException e) {
            throw new IllegalArgumentException("文件转换错误：" + e, e);
        }
        return new CommonsMultipartFile(fileItem);
    }

    public static MultipartFile transform2Word(InputStream fileInputStream, String fileName) {
        if (ObjectUtils.isEmpty(fileInputStream)){
            throw new IllegalArgumentException("文件转换错误：fileInputStream参数为空");
        }
        if (StringUtils.isBlank(fileName)){
            throw new IllegalArgumentException("文件转换错误：fileName参数为空");
        }
        FileItem fileItem = new DiskFileItemFactory().createItem(DomainFieldConstants.FILE,
                "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
                true, fileName);
        try (InputStream in = fileInputStream; OutputStream os = fileItem.getOutputStream()) {
            IOUtils.copyLarge(in, os);
        } catch (IOException e) {
            throw new IllegalArgumentException("文件转换错误：" + e, e);
        }
        return new CommonsMultipartFile(fileItem);
    }

    public static MultipartFile getMultipartFile(byte[] fileContentByte, String fileName) {
        if (ArrayUtils.isEmpty(fileContentByte)){
            throw new IllegalArgumentException("文件转换错误：fileContentByte参数为空");
        }
        return getMultipartFile(new ByteArrayInputStream(fileContentByte), fileName);
    }

}
