package com.doinner.csys;

import com.alibaba.fastjson2.JSONArray;
import com.doinner.common.core.context.SecurityContextHolder;
import com.doinner.common.core.utils.SpringUtils;
import com.doinner.common.redis.service.RedisService;
import com.doinner.common.security.utils.DictUtils;
import com.doinner.csys.dao.TeachingPlanAssessmentMapper;
import com.doinner.csys.dao.TeachingPlanMapper;
import com.doinner.csys.domain.TeachingPlan;
import com.doinner.csys.domain.TeachingPlanAssessment;
import com.doinner.csys.domain.vo.TeachingPlanAssessmentSaveVo;
import com.doinner.csys.domain.vo.TeachingPlanImportResultVo;
import com.doinner.csys.entity.csys.CourseTeachingPlanGenerator;
import com.doinner.csys.entity.csys.TeachingPlanWordImporter;
import com.doinner.csys.entity.csys.model.CourseTeachingPlanModel;
import com.doinner.csys.service.impl.TeachingPlanModuleServiceImpl;
import com.doinner.csys.service.impl.TeachingPlanServiceImpl;
import com.doinner.csys.utils.AssessmentCriteriaCodec;
import com.doinner.system.domain.entity.SysDictData;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.*;

/** Pure POI and mapper tests: no application bootstrap, database or remote writes. */
public class TeachingPlanAssessmentCriteriaTest {
    private static final ObjectMapper JSON = new ObjectMapper();
    private final Map<String, JSONArray> dictionaries = new LinkedHashMap<>();
    private TeachingPlanModuleServiceImpl service;
    private TeachingPlanAssessmentMapper assessments;
    private TeachingPlanMapper plans;
    private Object previousBeans;

    @BeforeEach
    void setup() {
        RedisService redis = mock(RedisService.class);
        when(redis.getCacheObject(anyString())).thenAnswer(call -> dictionaries.get(call.getArgument(0)));
        ConfigurableListableBeanFactory beans = mock(ConfigurableListableBeanFactory.class);
        when(beans.getBean(RedisService.class)).thenReturn(redis);
        previousBeans = ReflectionTestUtils.getField(SpringUtils.class, "beanFactory");
        ReflectionTestUtils.setField(SpringUtils.class, "beanFactory", beans);
        SecurityContextHolder.setUserName("assessment-test");
        service = new TeachingPlanModuleServiceImpl();
        assessments = mock(TeachingPlanAssessmentMapper.class);
        plans = mock(TeachingPlanMapper.class);
        ReflectionTestUtils.setField(service, "teachingPlanAssessmentMapper", assessments);
        ReflectionTestUtils.setField(service, "teachingPlanMapper", plans);
        when(plans.selectById(101L)).thenReturn(new TeachingPlan());
    }

    @AfterEach
    void cleanup() {
        ReflectionTestUtils.setField(SpringUtils.class, "beanFactory", previousBeans);
        SecurityContextHolder.remove();
    }

    @Test
    void teacherCrudAndBatchRejectLegacyOrMissingActiveCriteriaBeforeWriting() throws Exception {
        for (String mechanism : new String[]{"2", "3", "4"}) {
            TeachingPlanAssessment invalid = assessment(mechanism, "1,2");
            assertThrows(IllegalArgumentException.class, () -> service.addAssessment(invalid));
            assertThrows(IllegalArgumentException.class, () -> service.updateAssessment(invalid));
            TeachingPlanAssessmentSaveVo batch = new TeachingPlanAssessmentSaveVo();
            batch.setPlanId(101L);
            batch.setAssessments(Arrays.asList(assessment("1", "legacy"), invalid));
            assertThrows(IllegalArgumentException.class, () -> service.saveAssessments(batch));
        }
        assertThrows(IllegalArgumentException.class, () -> service.addAssessment(assessment("3",
                JSON.writeValueAsString(Collections.singletonList(criterion("6", "合格", "描述"))))));
        verifyNoInteractions(assessments);
        verify(plans, never()).updateById(any());
    }

    @Test
    void actualMechanismDictionaryOverridesFallbackCodesAndGradeLabelsKeepCustomCodes() throws Exception {
        dictionary("sys_assessment_mechanism", "1", "五级制", "P", "百分制", "T", "两级制", "F", "四级制");
        dictionary("sys_evaluation_standard", "excellent", "优秀", "fail", "不及格");
        assertThrows(IllegalArgumentException.class, () -> service.addAssessment(assessment("1", "1,2")));
        for (String mechanism : new String[]{"1", "五级制", "F", "四级制", "T", "两级制"}) {
            String standard = allCriteria();
            TeachingPlanAssessment valid = assessment(mechanism, standard);
            service.addAssessment(valid);
            assertEquals(standard, valid.getStandard(), "Teacher's dictionary values and labels stay untouched");
        }
        TeachingPlanAssessment percentage = assessment("P", "  legacy, text: exact\n");
        service.updateAssessment(percentage);
        assertEquals("  legacy, text: exact\n", percentage.getStandard());
    }

    @Test
    void inactiveGradesMayBeEmptyButActiveGradesDuplicatesAndMalformedEntriesAreRejected() throws Exception {
        List<Map<String, String>> entries = new ArrayList<>();
        entries.add(criterion("a", "优秀", "好"));
        entries.add(criterion("b", "良好", "好"));
        entries.add(criterion("c", "中等", ""));
        entries.add(criterion("d", "及格", "好"));
        entries.add(criterion("e", "不及格", "差"));
        String standard = JSON.writeValueAsString(entries);
        service.addAssessment(assessment("4", standard));
        assertThrows(IllegalArgumentException.class, () -> service.addAssessment(assessment("2", standard)));
        entries.add(criterion("f", "优秀", "重复"));
        assertThrows(IllegalArgumentException.class, () -> service.addAssessment(assessment("4", JSON.writeValueAsString(entries))));
        for (String invalid : new String[]{"[]", "{}", "[{", "[1]", "[{\"value\":6,\"label\":\"合格\",\"description\":\"x\"}]"}) {
            assertThrows(IllegalArgumentException.class, () -> service.addAssessment(assessment("3", invalid)));
        }
    }

    @Test
    void serviceDictionaryConversionNeverSplitsCriteriaJson() throws Exception {
        dictionary("sys_evaluation_standard", "1", "优秀", "2", "良好");
        TeachingPlanServiceImpl exports = new TeachingPlanServiceImpl();
        String standard = "[ {\"value\":\"1\", \"label\":\"优秀\", \"description\":\"1, 2: x\"} ]";
        TeachingPlanAssessment row = assessment("2", standard);
        ReflectionTestUtils.invokeMethod(exports, "translateAssessmentDictFields", Collections.singletonList(row));
        assertEquals(standard, row.getStandard());
        assertEquals(standard, ReflectionTestUtils.invokeMethod(exports, "reverseImportDict", standard, "sys_evaluation_standard"));
    }

    @Test
    void percentageAndProjectOutcomeKeepTheirLegacyStandardExactly() {
        String standard = "  plain: text, with，punctuation\n\n  ";
        TeachingPlanAssessment percentage = assessment("1", standard);
        service.addAssessment(percentage);
        assertEquals(standard, percentage.getStandard());
        TeachingPlanAssessment outcome = assessment(null, standard);
        outcome.setAssessmentCategory(5);
        outcome.setOutcomeType(1);
        service.addAssessment(outcome);
        assertEquals(standard, outcome.getStandard());
        assertNull(outcome.getMechanism());
    }

    @Test
    void freeTextStandardsAreNotReformattedByDictionaryTranslation() {
        dictionary("sys_evaluation_standard", "1", "优秀", "2", "良好");
        TeachingPlanServiceImpl exports = new TeachingPlanServiceImpl();
        String original = "  分析:充分, 良好\n\n说明  ";
        TeachingPlanAssessment row = assessment("1", original);
        ReflectionTestUtils.invokeMethod(exports, "translateAssessmentDictFields", Collections.singletonList(row));
        assertEquals(original, row.getStandard());
        assertEquals(original, ReflectionTestUtils.invokeMethod(exports, "reverseImportDict", original, "sys_evaluation_standard"));
    }

    @Test
    void wordImportUsesActualDictionaryValuesAndLegacyCsvStaysLegacy() throws Exception {
        Map<String, String> dictionary = new LinkedHashMap<>();
        TeachingPlanWordImporter.putDict(dictionary, "sys_evaluation_standard", "合格", "pass-custom");
        TeachingPlanWordImporter.putDict(dictionary, "sys_evaluation_standard", "不合格", "fail-custom");
        TeachingPlanWordImporter.putDict(dictionary, "sys_assessment_mechanism", "两级制", "binary-custom");
        String imported = AssessmentCriteriaCodec.fromWord("合格：解释: A,B\n不合格：未完成", dictionary);
        assertEquals("pass-custom", JSON.readTree(imported).get(0).path("value").asText());
        assertEquals("解释: A,B", JSON.readTree(imported).get(0).path("description").asText());
        assertEquals("binary-custom", AssessmentCriteriaCodec.mechanismFromWord("两级制", dictionary));
        assertEquals("1,2", AssessmentCriteriaCodec.fromWord("1,2", dictionary));
        assertEquals("1,2", AssessmentCriteriaCodec.fromWord(AssessmentCriteriaCodec.toWord("1,2"), Collections.emptyMap()));
        TeachingPlanWordImporter.putDict(dictionary, "sys_assessment_mechanism", "五级制", "3");
        assertEquals("3", AssessmentCriteriaCodec.mechanismFromWord("3", dictionary), "An actual dictionary code wins over fallback codes");
        TeachingPlanWordImporter.putDict(dictionary, "sys_assessment_mechanism", "二级制", "two-custom");
        assertEquals("two-custom", AssessmentCriteriaCodec.mechanismFromWord("二级制", dictionary));
        assertEquals("two-custom", AssessmentCriteriaCodec.mechanismFromWord("two-custom", dictionary));
    }

    @Test
    void percentageAndOutcomeWordRoundTripPreservesPlainTextIncludingBlankLines() throws Exception {
        String original = "合格：普通说明: a,b\n\n  plain text\n末行  \n";
        for (int docType : new int[]{1, 4}) {
            TeachingPlanAssessment row = assessment(docType == 4 ? null : "1", original);
            row.setAssessmentCategory(docType == 4 ? 5 : 1);
            row.setOutcomeType(docType == 4 ? 1 : null);
            CourseTeachingPlanModel model = new CourseTeachingPlanModel();
            model.setDocType(docType);
            model.setCourseName("Legacy assessment regression");
            model.setAssessments(Collections.singletonList(row));
            try (InputStream in = new CourseTeachingPlanGenerator().generate(model)) {
                TeachingPlanWordImporter.ParseContext context = new TeachingPlanWordImporter.ParseContext();
                context.expectedDocType = docType;
                TeachingPlanAssessment imported = new TeachingPlanWordImporter().parse(in, context).assessments.get(0);
                assertEquals(original, imported.getStandard());
                if (docType == 4) assertNull(imported.getMechanism());
            }
        }
    }

    @Test
    void validBatchKeepsInactiveCriteriaAndTeacherDictionaryCodes() throws Exception {
        String standard = allCriteria();
        TeachingPlanAssessment row = assessment("四级制", standard);
        TeachingPlanAssessmentSaveVo batch = new TeachingPlanAssessmentSaveVo();
        batch.setPlanId(101L);
        batch.setAssessments(Collections.singletonList(row));
        service.saveAssessments(batch);
        verify(assessments).deleteByPlanId(101L);
        verify(assessments).insert(row);
        assertEquals(standard, row.getStandard());
        assertThrows(IllegalArgumentException.class, () -> service.addAssessment(assessment("四级制", standard + " trailing")));
    }

    @Test
    void percentageRetainsCachedCriteriaThroughWordRoundTrip() throws Exception {
        String standard = JSON.writeValueAsString(Arrays.asList(
                criterion("6", "合格", "之前填写\r\n第二行"), criterion("7", "不合格", "需改进")));
        CourseTeachingPlanModel model = new CourseTeachingPlanModel();
        model.setDocType(1);
        model.setCourseName("Cached criteria regression");
        model.setAssessments(Collections.singletonList(assessment("1", standard)));
        try (InputStream in = new CourseTeachingPlanGenerator().generate(model)) {
            TeachingPlanAssessment imported = new TeachingPlanWordImporter()
                    .parse(in, new TeachingPlanWordImporter.ParseContext()).assessments.get(0);
            assertEquals(standard, imported.getStandard());
        }
    }

    @Test
    void experimentUsesScoreSystemBeforeMechanismForSaveAndWord() throws Exception {
        String standard = JSON.writeValueAsString(Arrays.asList(
                criterion("6", "合格", "通过"), criterion("7", "不合格", "未通过")));
        TeachingPlanAssessment row = assessment("1", standard);
        row.setAssessmentCategory(3);
        row.setScoreSystem("3");
        service.addAssessment(row);
        row.setStandard("[{");
        assertThrows(IllegalArgumentException.class, () -> service.addAssessment(row));
        row.setStandard(standard);
        TeachingPlanServiceImpl exports = new TeachingPlanServiceImpl();
        ReflectionTestUtils.invokeMethod(exports, "translateAssessmentDictFields", Collections.singletonList(row));
        CourseTeachingPlanModel model = new CourseTeachingPlanModel();
        model.setDocType(3);
        model.setCourseName("Experiment");
        model.setAssessments(Collections.singletonList(row));
        try (InputStream in = new CourseTeachingPlanGenerator().generate(model)) {
            TeachingPlanWordImporter.ParseContext context = new TeachingPlanWordImporter.ParseContext();
            context.expectedDocType = 3;
            TeachingPlanAssessment imported = new TeachingPlanWordImporter().parse(in, context).assessments.get(0);
            assertEquals("3", imported.getMechanism());
            assertEquals(JSON.readTree(standard), JSON.readTree(imported.getStandard()));
        }
        TeachingPlanAssessment other = assessment("1", "[{");
        other.setScoreSystem("3");
        service.addAssessment(other);
    }

    @Test
    void partialGradeDictionaryAllowsSameValueForDifferentLabelsOnSaveAndImport() throws Exception {
        String standard = JSON.writeValueAsString(Arrays.asList(
                criterion("2", "优秀", "优秀说明"), criterion("2", "良好", "良好说明"),
                criterion("3", "中等", "中等说明"), criterion("4", "及格", "及格说明"),
                criterion("5", "不及格", "不及格说明")));
        service.addAssessment(assessment("2", standard));
        Map<String, String> dictionary = new LinkedHashMap<>();
        TeachingPlanWordImporter.putDict(dictionary, "sys_evaluation_standard", "优秀", "2");
        String imported = AssessmentCriteriaCodec.fromWord(AssessmentCriteriaCodec.toWord(standard), "2", dictionary);
        assertEquals(JSON.readTree(standard), JSON.readTree(imported));
        service.addAssessment(assessment("2", imported));
        CourseTeachingPlanModel model = new CourseTeachingPlanModel();
        model.setDocType(1);
        model.setCourseName("Partial dictionary");
        model.setAssessments(Collections.singletonList(assessment("2", standard)));
        try (InputStream in = new CourseTeachingPlanGenerator().generate(model)) {
            TeachingPlanWordImporter.ParseContext context = new TeachingPlanWordImporter.ParseContext();
            context.dictLabelToValue.putAll(dictionary);
            TeachingPlanWordImporter.ParseResult parsed = new TeachingPlanWordImporter().parse(in, context);
            TeachingPlanAssessment wordRow = parsed.assessments.get(0);
            assertEquals(JSON.readTree(standard), JSON.readTree(wordRow.getStandard()));
            TeachingPlanServiceImpl imports = new TeachingPlanServiceImpl();
            ReflectionTestUtils.setField(imports, "teachingPlanAssessmentMapper", assessments);
            ReflectionTestUtils.invokeMethod(imports, "persistImportData",
                    null, null, 1, false, parsed, new TeachingPlanImportResultVo());
            verify(assessments).insert(wordRow);
            assertEquals(JSON.readTree(standard), JSON.readTree(wordRow.getStandard()));
        }
    }

    @Test
    void percentageAndOutcomeStandardsStayExactAcrossServiceAndWord() throws Exception {
        dictionary("sys_evaluation_standard", "1", "优秀", "2", "良好");
        for (String standard : new String[]{"1,2", "[满分100]", "合格：\n  原文\n不合格：\n  原文", "优秀", "首行\r\n末行\r"}) {
            for (int category : new int[]{1, 3, 5}) {
                TeachingPlanAssessment row = assessment(category == 3 ? "3" : "1", standard);
                row.setAssessmentCategory(category);
                if (category == 3) row.setScoreSystem("1");
                if (category == 5) row.setOutcomeType(1);
                service.addAssessment(row);
                TeachingPlanServiceImpl exports = new TeachingPlanServiceImpl();
                ReflectionTestUtils.invokeMethod(exports, "translateAssessmentDictFields", Collections.singletonList(row));
                assertEquals(standard, row.getStandard());
                CourseTeachingPlanModel model = new CourseTeachingPlanModel();
                int docType = category == 3 ? 3 : category == 5 ? 4 : 1;
                model.setDocType(docType);
                model.setCourseName("Plain standard");
                model.setAssessments(Collections.singletonList(row));
                try (InputStream in = new CourseTeachingPlanGenerator().generate(model)) {
                    TeachingPlanWordImporter.ParseContext context = new TeachingPlanWordImporter.ParseContext();
                    context.expectedDocType = docType;
                    TeachingPlanWordImporter.putDict(context.dictLabelToValue, "sys_evaluation_standard", "优秀", "1");
                    TeachingPlanAssessment imported = new TeachingPlanWordImporter().parse(in, context).assessments.get(0);
                    assertEquals(standard, imported.getStandard(), "category " + category);
                    service.addAssessment(imported);
                    assertEquals(standard, imported.getStandard());
                    TeachingPlanWordImporter.ParseResult parsed = new TeachingPlanWordImporter.ParseResult();
                    parsed.assessments.add(imported);
                    ReflectionTestUtils.setField(exports, "teachingPlanAssessmentMapper", assessments);
                    ReflectionTestUtils.invokeMethod(exports, "persistImportData",
                            null, null, docType, false, parsed, new TeachingPlanImportResultVo());
                    assertEquals(standard, imported.getStandard());
                }
            }
        }
    }

    @Test
    void unknownAndBlankMechanismsKeepLegacyStandardsOnSave() {
        for (String mechanism : new String[]{null, "", "process", "comprehensive"}) {
            TeachingPlanAssessment row = assessment(mechanism, "[满分100]");
            service.addAssessment(row);
            assertEquals("[满分100]", row.getStandard());
        }
        verify(assessments, times(4)).insert(any());
    }

    @Test
    void unknownMechanismWordImportKeepsLegacyText() {
        Map<String, String> dictionary = new LinkedHashMap<>();
        TeachingPlanWordImporter.putDict(dictionary, "sys_evaluation_standard", "优秀", "1");
        assertEquals("优秀", AssessmentCriteriaCodec.fromWord("优秀", "process", dictionary));
        assertEquals("合格：说明", AssessmentCriteriaCodec.fromWord("合格：说明", "process", dictionary));
    }

    @Test
    void gradedLegacyFreeTextAndBracketTextAreNotReformatted() throws Exception {
        for (String standard : new String[]{"完整的旧评价文字，包含标点", "[满分100]", "合格：完整的旧评价文字"}) {
            TeachingPlanAssessment row = assessment("2", standard);
            TeachingPlanServiceImpl exports = new TeachingPlanServiceImpl();
            ReflectionTestUtils.invokeMethod(exports, "translateAssessmentDictFields", Collections.singletonList(row));
            assertEquals(standard, row.getStandard());
            CourseTeachingPlanModel model = new CourseTeachingPlanModel();
            model.setDocType(1);
            model.setAssessments(Collections.singletonList(row));
            try (InputStream in = new CourseTeachingPlanGenerator().generate(model)) {
                TeachingPlanAssessment imported = new TeachingPlanWordImporter()
                        .parse(in, new TeachingPlanWordImporter.ParseContext()).assessments.get(0);
                assertEquals(standard, imported.getStandard());
            }
        }
    }

    @Test
    void gradeCriteriaAreReadableAndRoundTripInEveryGradedWordTable() throws Exception {
        String description = "  分析:准确,完整，理由充分\n\n良好：这是说明中的文字\n末行  \n";
        String standard = JSON.writeValueAsString(Arrays.asList(
                criterion("6", "合格", description), criterion("7", "不合格", "需改进")));
        for (int docType : new int[]{1, 2, 3}) {
            TeachingPlanAssessment row = assessment("3", standard);
            row.setAssessmentCategory(docType == 1 ? 1 : (docType == 2 ? 4 : 3));
            CourseTeachingPlanModel model = new CourseTeachingPlanModel();
            model.setDocType(docType);
            model.setCourseName("Assessment regression");
            model.setAssessments(Collections.singletonList(row));
            byte[] bytes;
            try (InputStream in = new CourseTeachingPlanGenerator().generate(model)) {
                bytes = in.readAllBytes();
            }
            try (XWPFDocument doc = new XWPFDocument(new ByteArrayInputStream(bytes))) {
                String text = doc.getTables().stream().map(t -> t.getText()).reduce("", String::concat);
                assertFalse(text.contains("\"description\""), "Word must not expose JSON, template " + docType);
                assertTrue(text.contains("合格："));
                assertTrue(text.contains("分析:准确,完整，理由充分"));
            }
            TeachingPlanWordImporter.ParseContext context = new TeachingPlanWordImporter.ParseContext();
            context.expectedDocType = docType;
            TeachingPlanWordImporter.ParseResult parsed = new TeachingPlanWordImporter()
                    .parse(new ByteArrayInputStream(bytes), context);
            assertEquals(1, parsed.assessments.size());
            assertEquals(JSON.readTree(standard), JSON.readTree(parsed.assessments.get(0).getStandard()));
            TeachingPlanWordImporter.putDict(context.dictLabelToValue, "sys_assessment_mechanism", "两级制", "binary");
            TeachingPlanWordImporter.putDict(context.dictLabelToValue, "sys_evaluation_standard", "合格", "pass");
            TeachingPlanWordImporter.putDict(context.dictLabelToValue, "sys_evaluation_standard", "不合格", "fail");
            TeachingPlanAssessment custom = new TeachingPlanWordImporter()
                    .parse(new ByteArrayInputStream(bytes), context).assessments.get(0);
            assertEquals("binary", custom.getMechanism());
            assertEquals("pass", JSON.readTree(custom.getStandard()).get(0).path("value").asText());
            assertEquals("fail", JSON.readTree(custom.getStandard()).get(1).path("value").asText());
            assertEquals(description, JSON.readTree(custom.getStandard()).get(0).path("description").asText());
        }
    }

    private static TeachingPlanAssessment assessment(String mechanism, String standard) {
        TeachingPlanAssessment row = new TeachingPlanAssessment();
        row.setPlanId(101L);
        row.setAssessmentCategory(1);
        row.setAssessmentItem("任务考核");
        row.setMechanism(mechanism);
        row.setStandard(standard);
        return row;
    }

    private static Map<String, String> criterion(String value, String label, String description) {
        Map<String, String> entry = new LinkedHashMap<>();
        entry.put("value", value);
        entry.put("label", label);
        entry.put("description", description);
        return entry;
    }

    private static String allCriteria() throws Exception {
        List<Map<String, String>> entries = new ArrayList<>();
        for (String label : new String[]{"优秀", "良好", "中等", "及格", "不及格", "合格", "不合格"}) {
            entries.add(criterion("custom-" + label, label, "说明：" + label));
        }
        return JSON.writeValueAsString(entries);
    }

    private void dictionary(String type, String... pairs) {
        JSONArray rows = new JSONArray();
        for (int i = 0; i < pairs.length; i += 2) {
            SysDictData row = new SysDictData();
            row.setDictValue(pairs[i]);
            row.setDictLabel(pairs[i + 1]);
            rows.add(row);
        }
        dictionaries.put(DictUtils.getCacheKey(type), rows);
    }
}
