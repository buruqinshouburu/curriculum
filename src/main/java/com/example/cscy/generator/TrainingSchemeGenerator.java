package com.example.cscy.generator;

import java.io.*;
import java.util.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

/**
 * 培养方案文档生成器 - 完全还原scheme.docx模板
 *
 * 本生成器读取scheme.docx模板文件，使用FreeMarker模板引擎处理XML内容，
 * 保留原始格式、字体、表格结构，并替换动态占位符。
 */
public class TrainingSchemeGenerator {

    /**
     * 从模板生成培养方案文档
     *
     * @param templatePath 模板文件路径
     * @param outputPath 输出文件路径
     * @throws IOException 生成异常
     */
    public void generate(String templatePath, String outputPath) throws IOException {
        // 创建临时工作目录
        String tempDir = System.getProperty("java.io.tmpdir") + "scheme_gen_" + System.currentTimeMillis();
        new File(tempDir).mkdirs();

        try {
            // 解压模板到临时目录
            decompress(templatePath, tempDir);

            // 读取并处理document.xml
            String documentXmlPath = tempDir + "/word/document.xml";
            String documentXml = new String(Files.readAllBytes(Paths.get(documentXmlPath)), "UTF-8");

            // 使用FreeMarker处理document.xml
            String processedXml = processWithFreemarker(documentXml, getFormData());

            // 写回处理后的document.xml
            Files.write(Paths.get(documentXmlPath), processedXml.getBytes("UTF-8"));

            // 重新压缩为DOCX文件
            compress(tempDir, outputPath);

        } finally {
            // 清理临时目录
            deleteDirectory(new File(tempDir));
        }
    }

    /**
     * 使用FreeMarker处理XML模板
     */
    private String processWithFreemarker(String xmlContent, Map<String, Object> data) throws IOException {
        try {
            // 创建FreeMarker配置
            freemarker.template.Configuration cfg = new freemarker.template.Configuration(freemarker.template.Configuration.VERSION_2_3_31);
            cfg.setDefaultEncoding("UTF-8");
            cfg.setTemplateExceptionHandler(freemarker.template.TemplateExceptionHandler.RETHROW_HANDLER);
            cfg.setLogTemplateExceptions(false);

            // 从字符串创建模板
            freemarker.template.Template template = new freemarker.template.Template(
                "documentXml", new java.io.StringReader(xmlContent), cfg);

            // 处理模板
            java.io.StringWriter writer = new java.io.StringWriter();
            template.process(data, writer);

            return writer.toString();
        } catch (freemarker.template.TemplateException e) {
            throw new IOException("FreeMarker processing failed", e);
        }
    }

    /**
     * 获取表单数据
     */
    private Map<String, Object> getFormData() {
        Map<String, Object> data = new HashMap<>();

        // 基本信息 - 用于替换${wildcard}
        data.put("wildcard", "******");
        data.put("majorType", "×××类");
        data.put("majorName", "×××专业");

        // 培养目标
        data.put("talentObjective", "培养\"对党绝对忠诚、科技基础厚实、创新思维敏锐、军事素质过硬、作风纪律优良\"的高素质专业化新型军事人才，为造就通晓战争的科技专家和掌握科技的军事专家奠定坚实基础。");

        // 毕业要求
        data.put("graduationRequirements", "具有学籍的本科学员，在修业年限内完成本培养方案规定的教学训练，通过各项考核、达成以下毕业要求，依据国防科技大学《高等教育生长军官学员、军士职业技术教育学员学籍管理规定实施细则（暂行）》，颁发毕业证书；符合学位授予条件的，依据《国防科技大学学位评定委员会关于授予军队硕士专业学位的实施办法（试行）》等规定，颁发学位证书。");

        // 学时学分
        data.put("totalWeeks", "203");
        data.put("totalCredits", "160");
        data.put("courseCredits", "130");
        data.put("practiceCredits", "30");
        data.put("firstYearCredits", "40");

        return data;
    }

    /**
     * 解压DOCX文件
     */
    private void decompress(String zipPath, String destDir) throws IOException {
        try (ZipFile zipFile = new ZipFile(zipPath)) {
            java.util.Enumeration<? extends ZipEntry> entries = zipFile.entries();
            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();
                File entryFile = new File(destDir, entry.getName());
                entryFile.getParentFile().mkdirs();

                if (entry.isDirectory()) {
                    entryFile.mkdirs();
                } else {
                    try (InputStream in = zipFile.getInputStream(entry);
                         OutputStream out = new FileOutputStream(entryFile)) {
                        byte[] buffer = new byte[8192];
                        int length;
                        while ((length = in.read(buffer)) > 0) {
                            out.write(buffer, 0, length);
                        }
                    }
                }
            }
        }
    }

    /**
     * 压缩为DOCX文件
     */
    private void compress(String sourceDir, String zipPath) throws IOException {
        try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(zipPath))) {
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
                        while ((length = fis.read(buffer)) > 0) {
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
     * 从模板生成培养方案文档
     *
     * @param templatePath 模板文件路径
     * @param outputPath 输出文件路径
     * @param copy 是否直接复制模板（true则使用简单复制，false则使用FreeMarker处理）
     * @throws IOException 生成异常
     */
    public void generate(String templatePath, String outputPath, boolean copy) throws IOException {
        if (copy) {
            // 直接复制模板
            try (FileInputStream fis = new FileInputStream(templatePath);
                 FileOutputStream fos = new FileOutputStream(outputPath)) {
                byte[] buffer = new byte[8192];
                int length;
                while ((length = fis.read(buffer)) > 0) {
                    fos.write(buffer, 0, length);
                }
            }
        } else {
            generate(templatePath, outputPath);
        }
    }
}
