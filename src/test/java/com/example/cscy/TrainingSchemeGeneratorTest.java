package com.example.cscy;

import com.example.cscy.generator.FreemarkerDocxGenerator;
import com.example.cscy.generator.TrainingSchemeGenerator;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

/**
 * 培养方案文档生成器测试类
 * 测试完全还原scheme.docx模板
 */
public class TrainingSchemeGeneratorTest {

    @Test
    public void testGenerateDocument() throws Exception {
        // 模板路径
        String templatePath = "src/main/resources/template/scheme.docx";

        // 生成文档 - 使用FreeMarker处理模板
        TrainingSchemeGenerator generator = new TrainingSchemeGenerator();
        String outputPath = "target/output/scheme_generated.docx";
        new File("target/output").mkdirs();

        // 生成新文档（不直接复制，使用FreeMarker处理）
        generator.generate(templatePath, outputPath, false);

        System.out.println("文档生成成功: " + outputPath);

        // 验证文件存在且不为空
        File file = new File(outputPath);
        assert file.exists() : "生成的文件不存在";
        assert file.length() > 0 : "生成的文件为空";

        System.out.println("文件大小: " + file.length() + " 字节");

        // 比较文件大小（应该与原模板相近）
        File template = new File(templatePath);
        System.out.println("模板文件大小: " + template.length() + " 字节");

        // 当使用FreeMarker处理时，由于ZIP压缩效率可能不同，文件大小会有轻微差异
        // 但解压缩后的XML内容应该相同
        System.out.println("注意: 使用FreeMarker处理时，文件大小可能因ZIP压缩差异而不同");

        // 比较ZIP内容结构
        compareZipContents(templatePath, outputPath);
    }

    @Test
    public void testGenerateWithCopy() throws Exception {
        // 模板路径
        String templatePath = "src/main/resources/template/scheme.docx";

        // 生成文档 - 直接复制模板
        TrainingSchemeGenerator generator = new TrainingSchemeGenerator();
        String outputPath = "target/output/scheme_copy.docx";
        new File("target/output").mkdirs();

        // 复制模板生成新文档
        generator.generate(templatePath, outputPath, true);

        System.out.println("文档复制成功: " + outputPath);

        // 验证文件存在且不为空
        File file = new File(outputPath);
        assert file.exists() : "生成的文件不存在";
        assert file.length() > 0 : "生成的文件为空";

        // 比较文件大小
        File template = new File(templatePath);
        System.out.println("模板文件大小: " + template.length() + " 字节");
        System.out.println("复制文件大小: " + file.length() + " 字节");
        System.out.println("文件大小相等: " + (file.length() == template.length()));
    }

    /**
     * 使用 FreeMarker 直接生成 Word 文档的测试方法
     * 该方法从 FreeMarker 模板（scheme.ftl）直接渲染出完整的 DOCX 文件
     */
    @Test
    public void testGenerateDocumentWithFreemarker() throws Exception {
        // FreeMarker 模板路径
        String templatePath = "src/main/resources/template/scheme.ftl";

        // 输出 DOCX 文件路径
        String outputPath = "target/output/scheme_freemarker.docx";
        new File("target/output").mkdirs();

        // 使用 FreeMarker 直接生成 DOCX
        FreemarkerDocxGenerator generator = new FreemarkerDocxGenerator();
        generator.generateWithFreemarker(templatePath, outputPath);

        System.out.println("FreeMarker 文档生成成功: " + outputPath);

        // 验证文件存在且不为空
        File file = new File(outputPath);
        assert file.exists() : "生成的文件不存在";
        assert file.length() > 0 : "生成的文件为空";

        System.out.println("文件大小: " + file.length() + " 字节");

        // 验证生成的 DOCX 是有效的
        verifyDocxFile(outputPath);
    }

    /**
     * 验证 DOCX 文件的有效性
     */
    private void verifyDocxFile(String docxPath) throws Exception {
        System.out.println("\n=== 验证 DOCX 文件 ===");

        try (ZipFile zf = new ZipFile(docxPath)) {
            // 检查必需的文件
            String[] requiredFiles = {
                "[Content_Types].xml",
                "_rels/.rels",
                "docProps/app.xml",
                "docProps/core.xml",
                "word/document.xml",
                "word/styles.xml"
            };

            for (String filename : requiredFiles) {
                ZipEntry entry = zf.getEntry(filename);
                if (entry == null) {
                    System.out.println("缺少必需文件: " + filename);
                    assert false : "缺少必需文件: " + filename;
                } else {
                    System.out.println("找到文件: " + filename + " (" + entry.getSize() + " 字节)");
                }
            }

            // 验证 document.xml 内容
            ZipEntry docEntry = zf.getEntry("word/document.xml");
            byte[] content = new byte[(int) docEntry.getSize()];
            try (InputStream is = zf.getInputStream(docEntry)) {
                is.read(content);
            }
            String xmlContent = new String(content, "UTF-8");

            // 检查关键内容
            String[] keyPhrases = {
                "人才培养方案",
                "培养目标",
                "毕业要求",
                "知识要求",
                "能力要求",
                "素质要求"
            };

            for (String phrase : keyPhrases) {
                if (xmlContent.contains(phrase)) {
                    System.out.println("包含关键内容: " + phrase);
                } else {
                    System.out.println("缺少关键内容: " + phrase);
                }
            }

            // 统计中文字符
            int chineseCount = 0;
            for (int i = 0; i < xmlContent.length(); i++) {
                char c = xmlContent.charAt(i);
                if ((c >= 0x4E00 && c <= 0x9FA5) ||
                    (c >= 0x3400 && c <= 0x4DBF)) {
                    chineseCount++;
                }
            }
            System.out.println("中文字符总数: " + chineseCount);
        }
    }

    /**
     * 比较两个ZIP文件的内容
     */
    private void compareZipContents(String zip1, String zip2) throws Exception {
        System.out.println("\n=== 比较ZIP内容 ===");

        try (ZipFile zf1 = new ZipFile(zip1);
             ZipFile zf2 = new ZipFile(zip2)) {

            java.util.Enumeration<? extends ZipEntry> entries1 = zf1.entries();
            while (entries1.hasMoreElements()) {
                ZipEntry entry = entries1.nextElement();
                String name = entry.getName();

                if (zf2.getEntry(name) != null) {
                    ZipEntry entry2 = zf2.getEntry(name);
                    byte[] content1 = getEntryBytes(zf1, entry);
                    byte[] content2 = getEntryBytes(zf2, entry2);

                    if (content1.length != content2.length) {
                        System.out.println("文件大小不同: " + name + " (" + content1.length + " vs " + content2.length + ")");
                    }

                    // 检查document.xml
                    if ("word/document.xml".equals(name)) {
                        String s1 = new String(content1, "UTF-8");
                        String s2 = new String(content2, "UTF-8");

                        // 检查是否包含相同的中文字符
                        int count1 = countChineseChars(s1);
                        int count2 = countChineseChars(s2);
                        System.out.println("中文字符数: 原模板=" + count1 + ", 生成文件=" + count2);

                        // 检查关键内容是否存在
                        String[] keyPhrases = {
                            "人才培养方案",
                            "培养目标",
                            "毕业要求",
                            "知识要求",
                            "能力要求",
                            "素质要求",
                            "毕业学时学分要求"
                        };

                        for (String phrase : keyPhrases) {
                            if (s1.contains(phrase) && !s2.contains(phrase)) {
                                System.out.println("警告: 关键内容丢失 - \"" + phrase + "\"");
                            }
                        }
                    }
                } else {
                    System.out.println("文件缺失: " + name);
                }
            }
        }
    }

    /**
     * 读取ZIP条目内容为字节数组
     */
    private byte[] getEntryBytes(ZipFile zf, ZipEntry entry) throws Exception {
        try (java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
             java.io.InputStream is = zf.getInputStream(entry)) {
            byte[] buffer = new byte[8192];
            int len;
            while ((len = is.read(buffer)) != -1) {
                baos.write(buffer, 0, len);
            }
            return baos.toByteArray();
        }
    }

    /**
     * 统计中文字符数量
     */
    private int countChineseChars(String str) {
        if (str == null) return 0;
        int count = 0;
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            // 常用中文字符范围
            if ((c >= 0x4E00 && c <= 0x9FA5) ||
                (c >= 0x3400 && c <= 0x4DBF) ||
                (c >= 0x20000 && c <= 0x2A6DF)) {
                count++;
            }
        }
        return count;
    }
}
