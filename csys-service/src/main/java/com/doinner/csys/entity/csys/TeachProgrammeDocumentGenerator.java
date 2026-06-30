package com.doinner.csys.entity.csys;


import com.doinner.csys.domain.TeachingProgrammeAttribute;
import com.doinner.csys.domain.TeachingProgrammeInstance;
import com.doinner.csys.utils.WordUtil;
import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;

import java.io.*;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Word 文档生成工具类
 * 用于根据 TeachingProgrammeInstance 和 TeachingProgrammeAttribute 生成 Word 文档
 */
public class TeachProgrammeDocumentGenerator {

    /**
     * 根据 TeachingProgrammeInstance 生成 Word 文档流
     *
     * @param instance TeachingProgrammeInstance 实例
     * @return InputStream Word 文档输入流
     * @throws IOException IO 异常
     */
    public static InputStream generateWordDocument(TeachingProgrammeInstance instance) throws IOException {
        XWPFDocument document = new XWPFDocument();

        // 创建文档标题（一级标题）
        WordUtil.createTitle(document, instance.getName());

        // 处理属性列表，构建树形结构
       // List<TeachingProgrammeAttribute> rootAttributes = buildAttributeTree(instance.getAttributeInstances());

        // 递归添加属性内容到文档
        for (TeachingProgrammeAttribute attribute : instance.getAttributeInstances()) {
            addAttributeContent(document, attribute, 1);
        }

        // 转换为输入流
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        document.write(outputStream);
        document.close();

        return new ByteArrayInputStream(outputStream.toByteArray());
    }

    /**
     * 构建属性树形结构
     *
     * @param attributes 扁平的属性列表
     * @return 树形结构的属性列表
     */
    private static List<TeachingProgrammeAttribute> buildAttributeTree(List<TeachingProgrammeAttribute> attributes) {
        if (attributes == null || attributes.isEmpty()) {
            return Collections.emptyList();
        }

        List<TeachingProgrammeAttribute> sortedAttributes = attributes.stream()
                .sorted(Comparator.comparing(
                        attr -> attr.getSort() != null ? attr.getSort() : 0
                ))
                .collect(Collectors.toList());

        // 使用 Map 存储所有节点，key 为 id
        Map<Long, TeachingProgrammeAttribute> nodeMap = new HashMap<>();
        for (TeachingProgrammeAttribute attr : sortedAttributes) {
            nodeMap.put(attr.getId(), attr);
        }

        // 找到根节点（parentId 为 null 或等于自身 ID 的）
        List<TeachingProgrammeAttribute> rootNodes = new ArrayList<>();
        for (TeachingProgrammeAttribute attr : sortedAttributes) {
            Long parentId = attr.getParentId();
            // 根节点条件：parentId 为 null 或 parentId 等于自身 id
            if (parentId == null || parentId == -1 || parentId.equals(attr.getId())) {
                rootNodes.add(attr);
            } else {
                // 非根节点，找到父节点并添加为子节点
                TeachingProgrammeAttribute parent = nodeMap.get(parentId);
                if (parent != null) {
                    if (parent.getChildren() == null) {
                        parent.setChildren(new ArrayList<>());
                    }
                    parent.getChildren().add(attr);
                }
            }
        }

        return rootNodes;
    }

    /**
     * 递归添加属性内容到文档
     *
     * @param document  XWPFDocument 对象
     * @param attribute 当前属性
     * @param level     当前层级
     */
    private static void addAttributeContent(XWPFDocument document, TeachingProgrammeAttribute attribute, int level) {
        // 根据层级创建标题
        WordUtil.createHeading(document, attribute.getAttributeName(), level);

        // 添加属性值（attributeValue 中以 \n 分隔的多行内容需分行渲染，
        // POI 的 run.setText 不会把 \n 渲染成换行，因此按行拆分逐段写入）
        if (attribute.getAttributeValue() != null && !attribute.getAttributeValue().isEmpty()) {
            String[] lines = attribute.getAttributeValue().split("\n", -1);
            for (String line : lines) {
                WordUtil.createParagraph(document, line, null);
            }
        }

        // 处理子属性
        if (attribute.getChildren() != null && !attribute.getChildren().isEmpty()) {
            for (TeachingProgrammeAttribute child : attribute.getChildren()) {
                addAttributeContent(document, child, level + 1);
            }
        }
    }

    /**
     * 生成 Word 文档并保存到文件
     *
     * @param instance TeachingProgrammeInstance 实例
     * @param filePath 文件保存路径
     * @throws IOException IO 异常
     */
    public static void generateWordDocumentToFile(TeachingProgrammeInstance instance, String filePath) throws IOException {
        try (InputStream inputStream = generateWordDocument(instance);
             FileOutputStream outputStream = new FileOutputStream(filePath)) {
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
        }
    }

    /**
     * 生成 Word 文档并保存到文件
     *
     * @param instance TeachingProgrammeInstance 实例
     * @param folder   保存文件夹
     * @return 生成的文件绝对路径
     * @throws IOException IO 异常
     */
    public static String generateWordDocumentToFile(TeachingProgrammeInstance instance, File folder) throws IOException {
        if (!folder.exists()) {
            folder.mkdirs();
        }

        String fileName = generateFileName(instance.getName());
        String filePath = new File(folder, fileName).getAbsolutePath();
        generateWordDocumentToFile(instance, filePath);
        return filePath;
    }

    /**
     * 生成文件名
     *
     * @param instanceName 实例名称
     * @return 文件名
     */
    private static String generateFileName(String instanceName) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        String timestamp = sdf.format(new Date());
        // 移除文件名中的特殊字符
        String safeName = instanceName.replaceAll("[^\\w\\u4e00-\\u9fa5]", "_");
        return safeName + "_" + timestamp + ".docx";
    }
}