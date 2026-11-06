package com.doinner.csys;

import com.alibaba.fastjson2.JSONArray;
import com.doinner.common.core.utils.SpringUtils;
import com.doinner.common.redis.service.RedisService;
import com.doinner.common.security.utils.DictUtils;
import com.doinner.csys.domain.TeachingPlanTextbook;
import com.doinner.csys.domain.vo.CourseVo;
import com.doinner.csys.entity.csys.CourseTeachingPlanGenerator;
import com.doinner.csys.entity.csys.TeachingPlanWordImporter;
import com.doinner.csys.entity.csys.model.CourseTeachingPlanModel;
import com.doinner.csys.service.TeachingPlanModuleService;
import com.doinner.csys.service.impl.TeachingPlanServiceImpl;
import com.doinner.system.domain.entity.SysDictData;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class TeachingPlanTextbookWordTest {
    private final Map<String, JSONArray> dictionaries = new LinkedHashMap<>();
    private Object previousBeans;

    @BeforeEach
    void setup() {
        RedisService redis = mock(RedisService.class);
        when(redis.getCacheObject(anyString())).thenAnswer(call -> dictionaries.get(call.getArgument(0)));
        ConfigurableListableBeanFactory beans = mock(ConfigurableListableBeanFactory.class);
        when(beans.getBean(RedisService.class)).thenReturn(redis);
        previousBeans = ReflectionTestUtils.getField(SpringUtils.class, "beanFactory");
        ReflectionTestUtils.setField(SpringUtils.class, "beanFactory", beans);
        dictionary("sys_textbook_nature", "basic", "基本教材");
        dictionary("sys_publication_method", "public", "公开出版");
        dictionary("sys_textbook_security_classification", "secret-custom", "秘密");
        dictionary("sys_textbook_political_theory", "no-custom", "否");
        dictionary("sys_textbook_source", "translated-custom", "选用境外教材国内翻译版");
    }

    @AfterEach
    void cleanup() {
        ReflectionTestUtils.setField(SpringUtils.class, "beanFactory", previousBeans);
    }

    @Test
    void ordinaryAndExperimentWordRoundTripAllThreeOptionalTextbookFieldsUsingActualDictionaries() throws Exception {
        for (int docType : new int[]{1, 3}) {
            TeachingPlanTextbook book = book();
            book.setSecurityClassification("secret-custom");
            book.setPoliticalTheoryTextbook("no-custom");
            book.setTextbookSource("translated-custom");
            TeachingPlanServiceImpl service = new TeachingPlanServiceImpl();
            ReflectionTestUtils.invokeMethod(service, "translateTextbookDictFields", Collections.singletonList(book));
            assertEquals("秘密", book.getSecurityClassification());
            assertEquals("否", book.getPoliticalTheoryTextbook());
            assertEquals("选用境外教材国内翻译版", book.getTextbookSource());
            CourseTeachingPlanModel model = model(docType, book);
            byte[] bytes;
            try (InputStream in = new CourseTeachingPlanGenerator().generate(model)) {
                bytes = in.readAllBytes();
            }
            try (XWPFDocument document = new XWPFDocument(new ByteArrayInputStream(bytes))) {
                XWPFTable table = document.getTables().stream()
                        .filter(item -> item.getRow(0).getCell(0).getText().equals("教材性质"))
                        .findFirst().orElseThrow();
                assertEquals(11, table.getRow(0).getTableCells().size());
                assertEquals("教材密级", table.getRow(0).getCell(8).getText());
                assertEquals("是否为政治理论教材", table.getRow(0).getCell(9).getText());
                assertEquals("教材来源", table.getRow(0).getCell(10).getText());
                assertEquals("秘密", table.getRow(1).getCell(8).getText());
                assertEquals("否", table.getRow(1).getCell(9).getText());
                assertEquals("选用境外教材国内翻译版", table.getRow(1).getCell(10).getText());
            }
            TeachingPlanWordImporter.ParseContext context = importContext(service, docType);
            TeachingPlanTextbook imported = new TeachingPlanWordImporter()
                    .parse(new ByteArrayInputStream(bytes), context).textbooks.get(0);
            assertEquals("basic", imported.getMaterialNature());
            assertEquals("public", imported.getPublishMethod());
            assertEquals("secret-custom", imported.getSecurityClassification());
            assertEquals("no-custom", imported.getPoliticalTheoryTextbook());
            assertEquals("translated-custom", imported.getTextbookSource());
            assertEquals("教材名称", imported.getName());
            assertEquals("2026.10", imported.getPublishTime());
            assertEquals("ISBN-1", imported.getIsbn());
        }
    }

    @Test
    void oldEightColumnWordImportsWithoutRequiringTheOptionalAdditions() throws Exception {
        byte[] bytes;
        try (XWPFDocument document = new XWPFDocument(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            document.createParagraph().createRun().setText("课程教学计划");
            XWPFTable table = document.createTable(2, 8);
            String[] headers = {"教材性质", "教材名称", "第一作者", "版次", "出版（颁发）单位", "出版（颁发）时间", "ISBN号（统一书号）", "出版方式"};
            String[] values = {"基本教材", "旧教材", "作者", "1", "出版社", "2026.10", "ISBN-OLD", "公开出版"};
            for (int col = 0; col < headers.length; col++) {
                table.getRow(0).getCell(col).setText(headers[col]);
                table.getRow(1).getCell(col).setText(values[col]);
            }
            document.write(out);
            bytes = out.toByteArray();
        }
        TeachingPlanTextbook imported = new TeachingPlanWordImporter().parse(new ByteArrayInputStream(bytes),
                importContext(new TeachingPlanServiceImpl(), 1)).textbooks.get(0);
        assertEquals("旧教材", imported.getName());
        assertEquals("basic", imported.getMaterialNature());
        assertEquals("public", imported.getPublishMethod());
        assertTrue(imported.getSecurityClassification() == null || imported.getSecurityClassification().isEmpty());
        assertTrue(imported.getPoliticalTheoryTextbook() == null || imported.getPoliticalTheoryTextbook().isEmpty());
        assertTrue(imported.getTextbookSource() == null || imported.getTextbookSource().isEmpty());
    }

    @Test
    void newWordLeavesOptionalTextbookFieldsBlankWhenTeacherHasNotFilledThem() throws Exception {
        for (int docType : new int[]{1, 3}) {
            try (InputStream in = new CourseTeachingPlanGenerator().generate(model(docType, book()))) {
                TeachingPlanTextbook imported = new TeachingPlanWordImporter().parse(in,
                        importContext(new TeachingPlanServiceImpl(), docType)).textbooks.get(0);
                assertTrue(imported.getSecurityClassification() == null || imported.getSecurityClassification().isEmpty());
                assertTrue(imported.getPoliticalTheoryTextbook() == null || imported.getPoliticalTheoryTextbook().isEmpty());
                assertTrue(imported.getTextbookSource() == null || imported.getTextbookSource().isEmpty());
            }
        }
    }

    private TeachingPlanWordImporter.ParseContext importContext(TeachingPlanServiceImpl service, int docType) {
        ReflectionTestUtils.setField(service, "teachingPlanModuleService", mock(TeachingPlanModuleService.class));
        CourseVo course = new CourseVo();
        course.setType(String.valueOf(docType));
        return ReflectionTestUtils.invokeMethod(service, "buildImportParseContext", 201L, course, docType == 3 ? 2 : 1);
    }

    private CourseTeachingPlanModel model(int docType, TeachingPlanTextbook book) {
        CourseTeachingPlanModel model = new CourseTeachingPlanModel();
        model.setDocType(docType);
        model.setCourseName("Textbook Word regression");
        model.setTextbooks(Collections.singletonList(book));
        return model;
    }

    private TeachingPlanTextbook book() {
        TeachingPlanTextbook book = new TeachingPlanTextbook();
        book.setMaterialNature("basic");
        book.setName("教材名称");
        book.setFirstAuthor("作者");
        book.setEdition("1");
        book.setPublisher("出版社");
        book.setPublishTime("2026.10");
        book.setIsbn("ISBN-1");
        book.setPublishMethod("public");
        return book;
    }

    private void dictionary(String type, String value, String label) {
        SysDictData row = new SysDictData();
        row.setDictValue(value);
        row.setDictLabel(label);
        JSONArray rows = new JSONArray();
        rows.add(row);
        dictionaries.put(DictUtils.getCacheKey(type), rows);
    }
}
