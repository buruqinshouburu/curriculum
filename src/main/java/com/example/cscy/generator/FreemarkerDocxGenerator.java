package com.example.cscy.generator;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * 直接使用 FreeMarker 生成 Word 文档的生成器
 *
 * 该生成器使用 FreeMarker 模板引擎处理 scheme.docx 中的 document.xml，
 * 替换其中的变量后创建完整的 DOCX 文件。
 */
public class FreemarkerDocxGenerator {

    /**
     * 使用 FreeMarker 直接生成 Word 文档
     *
     * @param templatePath FreeMarker 模板路径 (scheme.ftl)
     * @param outputPath 输出的 DOCX 文件路径
     * @throws IOException 生成异常
     * @throws TemplateException 模板异常
     */
    public void generateWithFreemarker(String templatePath, String outputPath)
            throws IOException, TemplateException {

        // 创建 FreeMarker 配置
        Configuration cfg = new Configuration(Configuration.VERSION_2_3_31);
        cfg.setDefaultEncoding("UTF-8");
        cfg.setTemplateExceptionHandler(freemarker.template.TemplateExceptionHandler.RETHROW_HANDLER);
        cfg.setLogTemplateExceptions(false);

        // 加载 FreeMarker 模板
        Template template = new Template(
            "scheme", new FileReader(templatePath), cfg);

        // 准备数据模型
        Map<String, Object> data = getFormData();

        // 渲染模板得到 document.xml 内容
        StringWriter writer = new StringWriter();
        template.process(data, writer);
        String documentXmlContent = writer.toString();

        // 确定 DOCX 模板路径（scheme.docx）
        String docxTemplatePath = templatePath.replace(".ftl", ".docx");
        File docxTemplate = new File(docxTemplatePath);
        if (!docxTemplate.exists()) {
            // 如果没有同名 docx 文件，使用默认路径
            docxTemplatePath = "src/main/resources/template/scheme.docx";
        }

        // 将渲染后的 document.xml 写入临时文件
        String tempDir = System.getProperty("java.io.tmpdir") + "freemarker_docx_" + System.currentTimeMillis();
        Files.createDirectories(Paths.get(tempDir, "word"));
        Files.createDirectories(Paths.get(tempDir, "_rels"));
        Files.createDirectories(Paths.get(tempDir, "docProps"));
        Files.createDirectories(Paths.get(tempDir, "word", "_rels"));
        Files.createDirectories(Paths.get(tempDir, "word", "theme"));

        // 写入 document.xml
        Files.write(Paths.get(tempDir, "word", "document.xml"), documentXmlContent.getBytes("UTF-8"));

        // 从原始 DOCX 模板复制其他必需文件
        copyDocxFiles(docxTemplatePath, tempDir);

        // 压缩为 DOCX 文件
        compressDocx(tempDir, outputPath);

        // 清理临时目录
        deleteDirectory(new File(tempDir));
    }

    /**
     * 从原始模板复制 DOCX 文件
     */
    private void copyDocxFiles(String templatePath, String tempDir) throws IOException {
        try (java.util.zip.ZipFile zf = new java.util.zip.ZipFile(templatePath)) {
            java.util.Enumeration<? extends java.util.zip.ZipEntry> entries = zf.entries();
            while (entries.hasMoreElements()) {
                java.util.zip.ZipEntry entry = entries.nextElement();
                String name = entry.getName();

                // 跳过 document.xml，因为我们已经渲染了
                if (name.equals("word/document.xml")) {
                    continue;
                }

                // 复制其他文件
                File destFile = new File(tempDir, name);
                if (entry.isDirectory()) {
                    destFile.mkdirs();
                } else {
                    destFile.getParentFile().mkdirs();
                    try (InputStream is = zf.getInputStream(entry);
                         OutputStream os = new FileOutputStream(destFile)) {
                        byte[] buffer = new byte[8192];
                        int len;
                        while ((len = is.read(buffer)) != -1) {
                            os.write(buffer, 0, len);
                        }
                    }
                }
            }
        }
    }

    /**
     * 压缩为 DOCX 文件
     */
    private void compressDocx(String sourceDir, String outputPath) throws IOException {
        try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(outputPath))) {
            addDirectoryToZip(zos, new File(sourceDir), "");
        }
    }

    /**
     * 递归添加目录到ZIP
     */
    private void addDirectoryToZip(ZipOutputStream zos, File dir, String parentPath) throws IOException {
        String currentPath = parentPath.isEmpty() ? "" : parentPath + "/";

        // 先添加当前目录（即使是空目录）
        if (!parentPath.isEmpty()) {
            ZipEntry dirEntry = new ZipEntry(currentPath);
            zos.putNextEntry(dirEntry);
            zos.closeEntry();
        }

        File[] files = dir.listFiles();
        if (files != null) {
            for (File file : files) {
                String entryName = currentPath + file.getName();
                if (file.isDirectory()) {
                    addDirectoryToZip(zos, file, entryName);
                } else {
                    ZipEntry entry = new ZipEntry(entryName);
                    zos.putNextEntry(entry);
                    try (FileInputStream fis = new FileInputStream(file)) {
                        byte[] buffer = new byte[8192];
                        int length;
                        while ((length = fis.read(buffer)) != -1) {
                            zos.write(buffer, 0, length);
                        }
                    }
                    zos.closeEntry();
                }
            }
        }
    }

    /**
     * 递归删除目录
     */
    private void deleteDirectory(File dir) {
        if (dir.isDirectory()) {
            File[] files = dir.listFiles();
            if (files != null) {
                for (File file : files) {
                    deleteDirectory(file);
                }
            }
        }
        dir.delete();
    }

    /**
     * 获取表单数据
     */
    private Map<String, Object> getFormData() {
        Map<String, Object> data = new HashMap<>();

        // 专业和类名
        data.put("majorType", "×××类");
        data.put("majorName", "×××专业");
        data.put("wildcard", "******");

        return data;
    }
}
