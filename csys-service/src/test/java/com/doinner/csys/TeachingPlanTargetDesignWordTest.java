package com.doinner.csys;

import com.alibaba.fastjson2.JSONArray;
import com.doinner.common.core.utils.SpringUtils;
import com.doinner.common.redis.service.RedisService;
import com.doinner.common.security.utils.DictUtils;
import com.doinner.csys.dao.TeachingPlanObjectiveMapper;
import com.doinner.csys.dao.TeachingPlanSectionMapper;
import com.doinner.csys.dao.TeachingPlanTaskBackgroundMapper;
import com.doinner.csys.dao.TeachingPlanTeacherMapper;
import com.doinner.csys.dao.TeachingPlanTrainingPurposeMapper;
import com.doinner.csys.domain.TeachingPlan;
import com.doinner.csys.domain.TeachingPlanTargetDesign;
import com.doinner.csys.domain.TeachingPlanObjective;
import com.doinner.csys.domain.vo.CourseKnowledgePointVo;
import com.doinner.csys.domain.vo.CourseKnowledgeUnitVo;
import com.doinner.csys.domain.vo.CourseVo;
import com.doinner.csys.domain.vo.TeachingPlanDetailVo;
import com.doinner.csys.domain.vo.TeachingPlanSchemeVo;
import com.doinner.csys.entity.csys.CourseTeachingPlanGenerator;
import com.doinner.csys.entity.csys.TeachingPlanWordImporter;
import com.doinner.csys.entity.csys.model.CourseTeachingPlanModel;
import com.doinner.csys.service.IKnowledgeUnitService;
import com.doinner.csys.service.TeachingPlanModuleService;
import com.doinner.csys.service.impl.TeachingPlanServiceImpl;
import com.doinner.csys.utils.WordUtil;
import com.doinner.system.domain.entity.SysDictData;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.test.util.ReflectionTestUtils;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/** Assert the actual DOCX XML and parser results, without Spring or a database. */
public class TeachingPlanTargetDesignWordTest {
    private static final String W = "http://schemas.openxmlformats.org/wordprocessingml/2006/main";
    private static final String KNOWLEDGE = "\u77e5\u8bc6";
    private static final String ABILITY = "\u80fd\u529b";
    private static final String QUALITY = "\u7d20\u8d28";
    private static final List<String> KNOWLEDGE_HEADER = Arrays.asList(
            "\u77e5\u8bc6\u5355\u5143", "\u77e5\u8bc6\u70b9", "\u652f\u6491\u7684\u8bfe\u7a0b\u77e5\u8bc6\u76ee\u6807",
            "\u6559\u5b66\u5185\u5bb9", "\u6559\u5b66\u73af\u8282", "\u6559\u6cd5", "\u5b66\u6cd5", "\u5b66\u65f6");

    @Test
    @SuppressWarnings("unchecked")
    void serviceBuildModelLoadsFrontendKnowledgeTreeAndDictionaryLabelsOnlyForCourseTemplate() throws Exception {
        TeachingPlanServiceImpl service = new TeachingPlanServiceImpl();
        TeachingPlanModuleService modules = mock(TeachingPlanModuleService.class);
        IKnowledgeUnitService knowledge = mock(IKnowledgeUnitService.class);
        ReflectionTestUtils.setField(service, "teachingPlanModuleService", modules);
        ReflectionTestUtils.setField(service, "knowledgeUnitService", knowledge);
        ReflectionTestUtils.setField(service, "teachingPlanTeacherMapper", mock(TeachingPlanTeacherMapper.class));
        ReflectionTestUtils.setField(service, "teachingPlanSectionMapper", mock(TeachingPlanSectionMapper.class));
        ReflectionTestUtils.setField(service, "teachingPlanObjectiveMapper", mock(TeachingPlanObjectiveMapper.class));
        ReflectionTestUtils.setField(service, "teachingPlanTaskBackgroundMapper", mock(TeachingPlanTaskBackgroundMapper.class));
        ReflectionTestUtils.setField(service, "teachingPlanTrainingPurposeMapper", mock(TeachingPlanTrainingPurposeMapper.class));
        ((Map<String, String>) ReflectionTestUtils.getField(service, "courseModuleDictionaryIdToNameMap")).put("test", "Test");
        TeachingPlanTargetDesign design = design(1L, "1", "Objective", point(1L, "Unit", 11L, "Point"));
        design.setSchemeId(10L);
        design.setTeachingLink("L");
        design.setTeachingMethod("M,N,M");
        design.setLearningMethod("A,A");
        when(modules.listTargetDesign(101L, null, null)).thenReturn(Collections.singletonList(design));
        when(knowledge.listByCourseId(8L)).thenReturn(Collections.singletonList(
                unit(1L, "Unit", meta(11L, "Point"), meta(12L, "Missing"))));
        Map<String, JSONArray> dictionaries = new LinkedHashMap<>();
        dictionaries.put(DictUtils.getCacheKey("sys_plan_target_type"), dictionary("1", KNOWLEDGE));
        dictionaries.put(DictUtils.getCacheKey("sys_plan_teaching_link"), dictionary("L", "Link label"));
        dictionaries.put(DictUtils.getCacheKey("sys_plan_teaching_method"), dictionary("M", "Method one", "N", "Method two"));
        dictionaries.put(DictUtils.getCacheKey("sys_plan_learning_method"), dictionary("A", "Learning label"));
        RedisService redis = mock(RedisService.class);
        when(redis.getCacheObject(anyString())).thenAnswer(call -> dictionaries.get(call.getArgument(0)));
        ConfigurableListableBeanFactory beans = mock(ConfigurableListableBeanFactory.class);
        when(beans.getBean(RedisService.class)).thenReturn(redis);
        Object previous = ReflectionTestUtils.getField(SpringUtils.class, "beanFactory");
        ReflectionTestUtils.setField(SpringUtils.class, "beanFactory", beans);
        try {
            CourseVo course = new CourseVo();
            course.setId(8L);
            course.setName("Model integration");
            course.setType("1");
            TeachingPlan plan = new TeachingPlan();
            plan.setId(101L);
            plan.setSourceCourseId(8L);
            plan.setPlanType(1);
            CourseTeachingPlanModel model = ReflectionTestUtils.invokeMethod(service, "buildModel",
                    course, plan, null, 101L, Collections.singletonList(scheme(10L)));
            Element table = table(generate(model), KNOWLEDGE_HEADER.get(0));
            assertEquals(Arrays.asList("Point", "Missing"), column(table, 1));
            assertEquals("Link label", text(cell(row(table, 1), 4)));
            assertEquals("Method one\u3001Method two", text(cell(row(table, 1), 5)));
            assertEquals("Learning label", text(cell(row(table, 1), 6)));
            for (int planType = 2; planType <= 4; planType++) {
                plan.setPlanType(planType);
                CourseTeachingPlanModel other = ReflectionTestUtils.invokeMethod(service, "buildModel",
                        course, plan, null, 101L, Collections.emptyList());
                assertNull(other.getKnowledgeUnits(), "Other templates do not add a knowledge query");
                try (XWPFDocument document = new XWPFDocument(new ByteArrayInputStream(generate(other)))) {
                    assertFalse(document.getTables().isEmpty());
                }
            }
            verify(knowledge).listByCourseId(8L);
            verifyNoMoreInteractions(knowledge);
            verify(modules, never()).listKnowledgeUnitInit(anyLong());
        } finally {
            ReflectionTestUtils.setField(SpringUtils.class, "beanFactory", previous);
        }
    }

    @Test
    void teachingMethodSlashLabelRoundTripsFromRepeatedCodes() throws Exception {
        assertMethodLabelRoundTrip("M,N,M,D", "A", "PBL/CBL\u3001Other method", "Self/Peer", "M\u3001N", "A");
    }

    @Test
    void learningMethodSlashLabelRoundTripsFromRepeatedCodes() throws Exception {
        assertMethodLabelRoundTrip("M", "A,B,A,C", "PBL/CBL", "Self/Peer\u3001Other learning", "M", "A\u3001B");
    }

    @Test
    void rawCompleteTeachingMethodSlashLabelRoundTripsWithoutSplitting() throws Exception {
        assertMethodLabelRoundTrip(" PBL/CBL ", "A", "PBL/CBL", "Self/Peer", "M", "A");
    }

    @Test
    void rawCompleteLearningMethodSlashLabelRoundTripsWithoutSplitting() throws Exception {
        assertMethodLabelRoundTrip("M", " Self/Peer ", "PBL/CBL", "Self/Peer", "M", "A");
    }

    @Test
    void nonPublicDesignsFollowSchemeOrderBeforeInterleavedSortForAllThreeTables() throws Exception {
        List<TeachingPlanTargetDesign> designs = new ArrayList<>();
        for (String type : Arrays.asList(KNOWLEDGE, ABILITY, QUALITY)) {
            designs.add(scopedDesign((long) designs.size() + 1, type, 20L, type + "-20-first", 1));
            designs.add(scopedDesign((long) designs.size() + 1, type, 10L, type + "-10-first", 2));
            designs.add(scopedDesign((long) designs.size() + 1, type, 20L, type + "-20-second", 3));
            designs.add(scopedDesign((long) designs.size() + 1, type, 10L, type + "-10-second", 4));
        }
        CourseTeachingPlanModel model = buildServiceModel(designs, detail(false), "professional", 1,
                Arrays.asList(scheme(10L), scheme(20L), scheme(10L)));
        byte[] bytes = generate(model);
        assertEquals(Arrays.asList(KNOWLEDGE + "-10-first", KNOWLEDGE + "-10-second",
                KNOWLEDGE + "-20-first", KNOWLEDGE + "-20-second"), column(table(bytes, KNOWLEDGE_HEADER.get(0)), 2));
        assertEquals(Arrays.asList(ABILITY + "-10-first", ABILITY + "-10-second",
                ABILITY + "-20-first", ABILITY + "-20-second"), column(table(bytes, "\u8bfe\u7a0b\u80fd\u529b\u76ee\u6807"), 0));
        assertEquals(Arrays.asList(QUALITY + "-10-first", QUALITY + "-10-second",
                QUALITY + "-20-first", QUALITY + "-20-second"), column(table(bytes, "\u8bfe\u7a0b\u7d20\u8d28\u76ee\u6807"), 0));
    }

    @Test
    void nonPublicCourseExcludesNullAndUnreferencedSchemesAcrossAllDesignTypes() throws Exception {
        List<TeachingPlanTargetDesign> designs = new ArrayList<>();
        for (String type : Arrays.asList(KNOWLEDGE, ABILITY, QUALITY)) {
            designs.add(scopedDesign((long) designs.size() + 1, type, null, "Null scheme", 1));
            designs.add(scopedDesign((long) designs.size() + 1, type, 99L, "Unknown scheme", 2));
            designs.add(scopedDesign((long) designs.size() + 1, type, 10L, "Referenced scheme", 3));
        }
        byte[] bytes = generate(buildServiceModel(designs, detail(false), "professional", 1,
                Collections.singletonList(scheme(10L))));
        assertEquals(Collections.singletonList("Referenced scheme"), column(table(bytes, KNOWLEDGE_HEADER.get(0)), 2));
        assertEquals(Collections.singletonList("Referenced scheme"), column(table(bytes, "\u8bfe\u7a0b\u80fd\u529b\u76ee\u6807"), 0));
        assertEquals(Collections.singletonList("Referenced scheme"), column(table(bytes, "\u8bfe\u7a0b\u7d20\u8d28\u76ee\u6807"), 0));
    }

    @Test
    void nonPublicCourseWithoutSchemesKeepsKnowledgePlaceholdersButNoConfiguredDesigns() throws Exception {
        List<TeachingPlanTargetDesign> designs = Arrays.asList(
                scopedDesign(1L, KNOWLEDGE, null, "Null scheme", 1),
                scopedDesign(2L, KNOWLEDGE, 10L, "Unreferenced scheme", 2));
        for (List<TeachingPlanSchemeVo> schemes : Arrays.asList(null, Collections.<TeachingPlanSchemeVo>emptyList(),
                Collections.singletonList(scheme(null)))) {
            CourseTeachingPlanModel model = buildServiceModel(designs, detail(false), "professional", 1, schemes);
            assertTrue(model.getTargetDesigns().isEmpty());
            assertEquals(Collections.singletonList("-"), column(table(generate(model), KNOWLEDGE_HEADER.get(0)), 2));
        }
    }

    @Test
    void explicitPublicFlagKeepsAllSchemesInOriginalQueryOrder() throws Exception {
        List<TeachingPlanTargetDesign> designs = Arrays.asList(
                scopedDesign(1L, KNOWLEDGE, 20L, "Scheme 20", 1),
                scopedDesign(2L, KNOWLEDGE, null, "Null scheme", 2),
                scopedDesign(3L, KNOWLEDGE, 99L, "Unknown scheme", 3),
                scopedDesign(4L, KNOWLEDGE, 10L, "Scheme 10", 4));
        CourseTeachingPlanModel model = buildServiceModel(designs, detail(true), "professional", 1,
                Collections.singletonList(scheme(10L)));
        assertEquals(objectives(designs), column(table(generate(model), KNOWLEDGE_HEADER.get(0)), 2));
    }

    @Test
    void explicitNonPublicFlagOverridesSourceCourseModule() throws Exception {
        List<TeachingPlanTargetDesign> designs = Arrays.asList(
                scopedDesign(1L, KNOWLEDGE, null, "Null scheme", 1),
                scopedDesign(2L, KNOWLEDGE, 10L, "Referenced scheme", 2));
        CourseTeachingPlanModel model = buildServiceModel(designs, detail(false), "\u516c\u5171\u57fa\u7840", 1,
                Collections.singletonList(scheme(10L)));
        assertEquals(Collections.singletonList("Referenced scheme"), column(table(generate(model), KNOWLEDGE_HEADER.get(0)), 2));
    }

    @Test
    void absentPublicFlagUsesSameSourceCourseModuleHelperAsDetailApi() throws Exception {
        List<TeachingPlanTargetDesign> designs = Arrays.asList(
                scopedDesign(1L, KNOWLEDGE, null, "Null scheme", 1),
                scopedDesign(2L, KNOWLEDGE, 99L, "Unknown scheme", 2));
        for (TeachingPlanDetailVo detail : Arrays.asList(null, new TeachingPlanDetailVo())) {
            CourseTeachingPlanModel model = buildServiceModel(designs, detail, "\u516c\u5171\u57fa\u7840", 1,
                    Collections.emptyList());
            assertEquals(objectives(designs), column(table(generate(model), KNOWLEDGE_HEADER.get(0)), 2));
        }
    }

    @Test
    void nonCourseTemplatesKeepAllDesignsRegardlessOfSchemeScopeAndOrder() throws Exception {
        List<TeachingPlanTargetDesign> designs = Arrays.asList(
                scopedDesign(1L, KNOWLEDGE, 20L, "Scheme 20", 1),
                scopedDesign(2L, KNOWLEDGE, null, "Null scheme", 2),
                scopedDesign(3L, KNOWLEDGE, 99L, "Unknown scheme", 3),
                scopedDesign(4L, KNOWLEDGE, 10L, "Scheme 10", 4));
        for (int planType = 2; planType <= 4; planType++) {
            CourseTeachingPlanModel model = buildServiceModel(designs, detail(false), "professional", planType,
                    Collections.singletonList(scheme(10L)));
            assertEquals(designs, model.getTargetDesigns());
            try (XWPFDocument document = new XWPFDocument(new ByteArrayInputStream(generate(model)))) {
                assertFalse(document.getTables().isEmpty());
            }
        }
    }

    @Test
    void writesMultiDesignAndPlaceholderSampleToTestTarget() throws Exception {
        CourseTeachingPlanModel model = model(
                design(1L, KNOWLEDGE, "First design", point(2L, "Z unit", 22L, "Z point"), point(2L, "Z unit", 21L, "A point")),
                design(2L, KNOWLEDGE, "Independent design", point(2L, "Z unit", 22L, "Z point")),
                design(3L, ABILITY, "Ability"), design(4L, QUALITY, "Quality"));
        model.setKnowledgeUnits(Arrays.asList(unit(2L, "Z unit", meta(22L, "Z point"), meta(21L, "A point")),
                unit(1L, "A unit", meta(11L, "Unconfigured point"))));
        byte[] bytes = generate(model);
        Element table = table(bytes, KNOWLEDGE_HEADER.get(0));
        assertEquals(Arrays.asList("Z point", "A point", "Z point", "Unconfigured point"), column(table, 1));
        assertEquals(Arrays.asList("First design", "First design", "Independent design", "-"), column(table, 2));
        Path output = Path.of(System.getProperty("teachingPlan.regression.output", "target/target-design-regression"));
        Files.createDirectories(output);
        Files.write(output.resolve("target-design-alignment-sample.docx"), bytes);
    }

    @Test
    void emptyDesignsStillExportEveryKnowledgePointInTreeOrder() throws Exception {
        CourseTeachingPlanModel model = model();
        model.setKnowledgeUnits(Arrays.asList(unit(2L, "Z unit", meta(22L, "Z point"), meta(21L, "A point")),
                unit(1L, "A unit", meta(11L, "B point"))));
        Element table = table(generate(model), KNOWLEDGE_HEADER.get(0));
        assertEquals(Arrays.asList("Z point", "A point", "B point"), column(table, 1));
        assertEquals(Arrays.asList("Z unit", "", "A unit"), column(table, 0));
        assertEquals(Arrays.asList("restart", "continue", ""), merges(table, 0));
        for (int r = 1; r <= 3; r++) {
            for (int c = 2; c < 8; c++) assertEquals("-", text(cell(row(table, r), c)));
        }
    }

    @Test
    void partialConfigurationAppendsOnlyMissingPointsAfterConfiguredRows() throws Exception {
        CourseTeachingPlanModel model = model(design(1L, KNOWLEDGE, "Configured",
                point(1L, "A unit", 11L, "Configured point")));
        model.setKnowledgeUnits(Arrays.asList(unit(2L, "Z unit", meta(22L, "Missing first"), meta(21L, "Missing second")),
                unit(1L, "A unit", meta(11L, "Configured point"))));
        Element table = table(generate(model), KNOWLEDGE_HEADER.get(0));
        assertEquals(Arrays.asList("Configured point", "Missing first", "Missing second"), column(table, 1));
        assertEquals(Arrays.asList("Configured", "-", "-"), column(table, 2));
    }

    @Test
    void duplicateMetadataAndSnapshotsAreDeduplicatedButDifferentUnitsRemain() throws Exception {
        CourseTeachingPlanModel model = model(design(1L, KNOWLEDGE, "Configured",
                point(1L, "Unit", 11L, "Point"), point(1L, "Unit", 11L, "Point")));
        model.setKnowledgeUnits(Arrays.asList(unit(1L, "Unit", meta(11L, "Point"), meta(12L, "Last value wins")),
                unit(1L, "Unit", meta(12L, "Updated point")), unit(2L, "Other", meta(11L, "Shared ID"))));
        Element table = table(generate(model), KNOWLEDGE_HEADER.get(0));
        assertEquals(Arrays.asList("Point", "Updated point", "Shared ID"), column(table, 1));
        assertEquals(Arrays.asList("Configured", "-", "-"), column(table, 2));
    }

    @Test
    void staleIdsMatchOnlyUniqueFullNamePathAndKeepSnapshotText() throws Exception {
        CourseTeachingPlanModel model = model(design(1L, KNOWLEDGE, "Snapshot",
                point(9L, "Unit", 99L, "Point")));
        model.setKnowledgeUnits(Arrays.asList(unit(1L, "Unit", meta(11L, "Point")),
                unit(2L, "Other", meta(22L, "Point"))));
        Element table = table(generate(model), KNOWLEDGE_HEADER.get(0));
        assertEquals(Arrays.asList("Point", "Point"), column(table, 1));
        assertEquals(Arrays.asList("Unit", "Other"), column(table, 0));
        assertEquals(Arrays.asList("Snapshot", "-"), column(table, 2));
    }

    @Test
    void ambiguousFullNamePathDoesNotSuppressAnyPlaceholder() throws Exception {
        CourseTeachingPlanModel model = model(design(1L, KNOWLEDGE, "Snapshot",
                point(9L, "Unit", 99L, "Point")));
        model.setKnowledgeUnits(Arrays.asList(unit(1L, "Unit", meta(11L, "Point")),
                unit(2L, "Unit", meta(22L, "Point"))));
        Element table = table(generate(model), KNOWLEDGE_HEADER.get(0));
        assertEquals(Arrays.asList("Snapshot", "-", "-"), column(table, 2));
        assertEquals(Arrays.asList("", "", ""), merges(table, 0));
    }

    @Test
    void pointIdMatchingPrefersSameUnitThenGloballyUniqueId() throws Exception {
        CourseTeachingPlanModel model = model(design(1L, KNOWLEDGE, "Snapshot",
                point(1L, "Old unit", 11L, "Old point"), point(99L, "Moved unit", 22L, "Moved point")));
        model.setKnowledgeUnits(Arrays.asList(unit(1L, "Current", meta(11L, "Current point")),
                unit(2L, "Other", meta(11L, "Other point")), unit(3L, "Moved", meta(22L, "Moved"))));
        Element table = table(generate(model), KNOWLEDGE_HEADER.get(0));
        assertEquals(Arrays.asList("Old point", "Moved point", "Other point"), column(table, 1));
        assertEquals(Arrays.asList("Snapshot", "Snapshot", "-"), column(table, 2));
    }

    @Test
    void placeholderRowsAreNotImportedAsConfiguredDesigns() throws Exception {
        CourseTeachingPlanModel model = model();
        model.setKnowledgeUnits(Collections.singletonList(unit(1L, "Unit", meta(11L, "Point"))));
        byte[] bytes = generate(model);
        assertEquals("-", text(cell(row(table(bytes, KNOWLEDGE_HEADER.get(0)), 1), 2)));
        assertTrue(parse(bytes).targetDesigns.isEmpty());
    }

    @Test
    void emptyKnowledgeSnapshotsDoNotProduceSpuriousConfiguredRows() throws Exception {
        CourseTeachingPlanModel model = model(design(1L, KNOWLEDGE, "Invalid snapshot"));
        model.setKnowledgeUnits(Collections.singletonList(unit(1L, "Unit", meta(11L, "Point"))));
        Element table = table(generate(model), KNOWLEDGE_HEADER.get(0));
        assertEquals(Collections.singletonList("Point"), column(table, 1));
        assertEquals(Collections.singletonList("-"), column(table, 2));
    }

    @Test
    void knowledgeColumnsAndInputOrderMatchFrontend() throws Exception {
        TeachingPlanTargetDesign first = design(9L, KNOWLEDGE, "First",
                point(2L, "Z unit", 22L, "Z point"), point(1L, "A unit", 11L, "A point"));
        TeachingPlanTargetDesign second = design(1L, KNOWLEDGE, "Second",
                point(2L, "Z unit", 21L, "B point"));
        Element table = table(generate(model(first, second)), KNOWLEDGE_HEADER.get(0));
        assertEquals(KNOWLEDGE_HEADER, texts(row(table, 0)));
        assertEquals(Arrays.asList("Z point", "A point", "B point"), column(table, 1));
        assertEquals(Arrays.asList("First", "First", "Second"), column(table, 2));
        assertEquals(Arrays.asList("2.5", "2.5", "2.5"), column(table, 7));
    }

    @Test
    void duplicateDesignIdsAndSnapshotPointsDoNotRemoveIndependentDesigns() throws Exception {
        TeachingPlanTargetDesign first = design(5L, KNOWLEDGE, "First",
                point(2L, "Unit", 21L, "Point"), point(2L, "Unit", 21L, "Point"),
                point(2L, "Unit", 22L, "Other"));
        TeachingPlanTargetDesign repeated = design(5L, KNOWLEDGE, "Repeated",
                point(2L, "Unit", 23L, "Ignored"));
        TeachingPlanTargetDesign independent = design(6L, KNOWLEDGE, "Independent",
                point(2L, "Unit", 21L, "Point"));
        Element table = table(generate(model(first, repeated, independent)), KNOWLEDGE_HEADER.get(0));
        assertEquals(Arrays.asList("Point", "Other", "Point"), column(table, 1));
        assertEquals(Arrays.asList("First", "First", "Independent"), column(table, 2));
        assertEquals("restart", merge(cell(row(table, 1), 0)));
        assertEquals("continue", merge(cell(row(table, 3), 0)));
        for (int r = 1; r <= 3; r++) {
            for (int c = 1; c < 8; c++) {
                assertEquals("", merge(cell(row(table, r), c)), "Only the unit column may merge");
            }
        }
    }

    @Test
    void unitMergeRequiresConsecutiveMatchingIdAndName() throws Exception {
        TeachingPlanTargetDesign first = design(1L, KNOWLEDGE, "First",
                point(1L, "Shared", 11L, "One"), point(1L, "Shared", 12L, "Two"),
                point(2L, "Shared", 21L, "Three"), point(1L, "Shared", 13L, "Four"),
                point(1L, "Renamed", 14L, "Five"));
        TeachingPlanTargetDesign second = design(2L, KNOWLEDGE, "Second",
                point(1L, "Renamed", 15L, "Six"));
        Element table = table(generate(model(first, second)), KNOWLEDGE_HEADER.get(0));
        assertEquals(Arrays.asList("restart", "continue", "", "", "restart", "continue"),
                merges(table, 0));
        assertEquals(Arrays.asList("One", "Two", "Three", "Four", "Five", "Six"), column(table, 1));
        for (int r = 1; r <= 6; r++) {
            for (int c = 1; c < 8; c++) assertEquals("", merge(cell(row(table, r), c)));
        }
    }

    @Test
    void legacySinglePointAndMissingFieldsUseFrontendDash() throws Exception {
        TeachingPlanTargetDesign legacy = design(1L, KNOWLEDGE, null);
        legacy.setKnowledgeUnitId(1L);
        legacy.setKnowledgeUnitName("Unit");
        legacy.setKnowledgePointId(11L);
        legacy.setKnowledgePointName("Point");
        legacy.setContentText("Content only");
        legacy.setTeachingLink(null);
        legacy.setTeachingMethod("");
        legacy.setLearningMethod(null);
        legacy.setHours(BigDecimal.ZERO);
        Element table = table(generate(model(legacy)), KNOWLEDGE_HEADER.get(0));
        assertEquals(Arrays.asList("Unit", "Point", "-", "Content only", "-", "-", "-", "0"),
                texts(row(table, 1)));
    }

    @Test
    void objectiveIdFallbackUsesAssociatedObjectiveNeverTeachingContent() throws Exception {
        TeachingPlanTargetDesign knowledge = design(1L, KNOWLEDGE, "",
                point(1L, "Unit", 11L, "Point"));
        TeachingPlanTargetDesign ability = design(2L, ABILITY, null);
        TeachingPlanTargetDesign quality = design(3L, QUALITY, "Snapshot wins");
        for (TeachingPlanTargetDesign design : Arrays.asList(knowledge, ability, quality)) design.setObjectiveId(77L);
        TeachingPlanObjective objective = new TeachingPlanObjective();
        objective.setId(77L);
        objective.setContent("Associated objective");
        CourseTeachingPlanModel.SchemeObjectiveGroup group = new CourseTeachingPlanModel.SchemeObjectiveGroup();
        group.setObjectives(Collections.singletonList(objective));
        CourseTeachingPlanModel model = model(knowledge, ability, quality);
        model.setSchemeObjectiveGroups(Collections.singletonList(group));
        byte[] bytes = generate(model);
        assertEquals("Associated objective", text(cell(row(table(bytes, KNOWLEDGE_HEADER.get(0)), 1), 2)));
        assertEquals("Associated objective", text(cell(row(table(bytes, "\u8bfe\u7a0b\u80fd\u529b\u76ee\u6807"), 1), 0)));
        assertEquals("Snapshot wins", text(cell(row(table(bytes, "\u8bfe\u7a0b\u7d20\u8d28\u76ee\u6807"), 1), 0)));
    }

    @Test
    void abilityAndQualityColumnsPreserveRowsAndNeverMerge() throws Exception {
        TeachingPlanTargetDesign ability = design(8L, ABILITY, "Ability Z");
        TeachingPlanTargetDesign other = design(1L, ABILITY, "Ability A");
        TeachingPlanTargetDesign quality = design(9L, QUALITY, "Quality");
        quality.setObservationPoint(null);
        quality.setTeachingDesign("");
        byte[] bytes = generate(model(ability, quality, other));
        Element abilityTable = table(bytes, "\u8bfe\u7a0b\u80fd\u529b\u76ee\u6807");
        assertEquals(Arrays.asList("\u8bfe\u7a0b\u80fd\u529b\u76ee\u6807", "\u89c2\u6d4b\u70b9",
                "\u6559\u5b66\u5185\u5bb9", "\u6559\u5b66\u8bbe\u8ba1"), texts(row(abilityTable, 0)));
        assertEquals(Arrays.asList("Ability Z", "Observation", "Content", "Design"), texts(row(abilityTable, 1)));
        assertEquals("Ability A", text(cell(row(abilityTable, 2), 0)));
        assertEquals(0, abilityTable.getElementsByTagNameNS(W, "vMerge").getLength());
        Element qualityTable = table(bytes, "\u8bfe\u7a0b\u7d20\u8d28\u76ee\u6807");
        assertEquals(Arrays.asList("Quality", "-", "Content", "-"), texts(row(qualityTable, 1)));
        assertEquals(4, cells(row(qualityTable, 0)).size());
    }

    @Test
    void newUnnumberedWordImportsIndependentRowsAndCorrectColumns() throws Exception {
        TeachingPlanTargetDesign first = design(1L, KNOWLEDGE, "First",
                point(1L, "Unit", 11L, "One"), point(1L, "Unit", 12L, "Two"));
        TeachingPlanTargetDesign second = design(2L, KNOWLEDGE, "Second", point(1L, "Unit", 11L, "One"));
        byte[] bytes = generate(model(first, second, design(3L, ABILITY, "Ability"), design(4L, QUALITY, "Quality")));
        TeachingPlanWordImporter.ParseResult parsed = parse(bytes);
        assertEquals(5, parsed.targetDesigns.size());
        assertEquals(Arrays.asList("First", "First", "Second", "Ability", "Quality"),
                objectives(parsed.targetDesigns));
        assertEquals("Two", parsed.targetDesigns.get(1).getKnowledgePointName());
        assertEquals("Content", parsed.targetDesigns.get(2).getContentText());
        assertEquals("Link", parsed.targetDesigns.get(2).getTeachingLink());
        assertEquals("Method", parsed.targetDesigns.get(2).getTeachingMethod());
        assertEquals("Learning", parsed.targetDesigns.get(2).getLearningMethod());
        assertEquals(0, new BigDecimal("2.5").compareTo(parsed.targetDesigns.get(2).getHours()));
        assertEquals("Observation", parsed.targetDesigns.get(3).getObservationPoint());
        assertEquals("Design", parsed.targetDesigns.get(4).getTeachingDesign());
    }

    @Test
    void oldNumberedWordStillImportsGroupedKnowledgeAndTargetColumns() throws Exception {
        byte[] bytes;
        try (XWPFDocument doc = new XWPFDocument(); ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            doc.createParagraph().createRun().setText("\u300aLegacy\u300b\u8bfe\u7a0b\u6559\u5b66\u8ba1\u5212");
            doc.createParagraph().createRun().setText("\u516d\u3001\u8bfe\u7a0b\u76ee\u6807\u4e0e\u6559\u5b66\u5b9e\u73b0\u8bbe\u8ba1");
            XWPFTable knowledge = doc.createTable(4, 9);
            List<String> header = new ArrayList<>();
            header.add("\u5e8f\u53f7");
            header.addAll(KNOWLEDGE_HEADER);
            fill(knowledge, 0, header);
            fill(knowledge, 1, Arrays.asList("1", "Unit", "One", "First", "Content", "Link", "Method", "Learning", "2.5"));
            fill(knowledge, 2, Arrays.asList("", "", "Two", "", "", "", "", "", ""));
            fill(knowledge, 3, Arrays.asList("2", "Other", "Three", "Second", "Other content", "", "", "", "0"));
            for (int c : new int[]{0, 1, 3, 4, 5, 6, 7, 8}) WordUtil.mergeCellsVertical(knowledge, c, 1, 2);
            doc.createParagraph().createRun().setText("\uff08\u4e8c\uff09\u80fd\u529b\u76ee\u6807\u548c\u6559\u5b66\u5b9e\u73b0\u8bbe\u8ba1");
            XWPFTable ability = doc.createTable(2, 5);
            fill(ability, 0, Arrays.asList("\u5e8f\u53f7", "\u8bfe\u7a0b\u80fd\u529b\u76ee\u6807",
                    "\u89c2\u6d4b\u70b9", "\u6559\u5b66\u5185\u5bb9", "\u6559\u5b66\u8bbe\u8ba1"));
            fill(ability, 1, Arrays.asList("1", "Ability", "Observation", "Content", "Design"));
            doc.write(output);
            bytes = output.toByteArray();
        }
        assertEquals(9, cells(row(table(bytes, "\u5e8f\u53f7"), 0)).size());
        TeachingPlanWordImporter.ParseResult parsed = parse(bytes);
        assertEquals(3, parsed.targetDesigns.size());
        assertEquals(2, parsed.targetDesigns.get(0).getKnowledgePoints().size());
        assertEquals("Two", parsed.targetDesigns.get(0).getKnowledgePoints().get(1).getKnowledgePointName());
        assertEquals("Second", parsed.targetDesigns.get(1).getObjectiveText());
        assertEquals("Ability", parsed.targetDesigns.get(2).getObjectiveText());
        assertEquals("Observation", parsed.targetDesigns.get(2).getObservationPoint());
        assertEquals("Design", parsed.targetDesigns.get(2).getTeachingDesign());
    }

    private static CourseTeachingPlanModel model(TeachingPlanTargetDesign... designs) {
        CourseTeachingPlanModel model = new CourseTeachingPlanModel();
        model.setDocType(1);
        model.setCourseName("Target design regression");
        model.setTargetDesigns(Arrays.asList(designs));
        return model;
    }

    private static CourseTeachingPlanModel buildServiceModel(List<TeachingPlanTargetDesign> designs,
                                                            TeachingPlanDetailVo detail, String courseModule,
                                                            int planType, List<TeachingPlanSchemeVo> schemes) {
        return buildServiceModel(designs, detail, courseModule, planType, schemes, Collections.emptyMap());
    }

    @SuppressWarnings("unchecked")
    private static CourseTeachingPlanModel buildServiceModel(List<TeachingPlanTargetDesign> designs,
                                                            TeachingPlanDetailVo detail, String courseModule,
                                                            int planType, List<TeachingPlanSchemeVo> schemes,
                                                            Map<String, JSONArray> dictionaries) {
        TeachingPlanServiceImpl service = new TeachingPlanServiceImpl();
        TeachingPlanModuleService modules = mock(TeachingPlanModuleService.class);
        IKnowledgeUnitService knowledge = mock(IKnowledgeUnitService.class);
        ReflectionTestUtils.setField(service, "teachingPlanModuleService", modules);
        ReflectionTestUtils.setField(service, "knowledgeUnitService", knowledge);
        ReflectionTestUtils.setField(service, "teachingPlanTeacherMapper", mock(TeachingPlanTeacherMapper.class));
        ReflectionTestUtils.setField(service, "teachingPlanSectionMapper", mock(TeachingPlanSectionMapper.class));
        ReflectionTestUtils.setField(service, "teachingPlanObjectiveMapper", mock(TeachingPlanObjectiveMapper.class));
        ReflectionTestUtils.setField(service, "teachingPlanTaskBackgroundMapper", mock(TeachingPlanTaskBackgroundMapper.class));
        ReflectionTestUtils.setField(service, "teachingPlanTrainingPurposeMapper", mock(TeachingPlanTrainingPurposeMapper.class));
        ((Map<String, String>) ReflectionTestUtils.getField(service, "courseModuleDictionaryIdToNameMap")).put("test", "Test");
        when(modules.listTargetDesign(101L, null, null)).thenReturn(designs);
        when(knowledge.listByCourseId(8L)).thenReturn(Collections.singletonList(unit(1L, "Unit", meta(11L, "Point"))));
        RedisService redis = mock(RedisService.class);
        when(redis.getCacheObject(anyString())).thenAnswer(call -> dictionaries.get(call.getArgument(0)));
        ConfigurableListableBeanFactory beans = mock(ConfigurableListableBeanFactory.class);
        when(beans.getBean(RedisService.class)).thenReturn(redis);
        Object previous = ReflectionTestUtils.getField(SpringUtils.class, "beanFactory");
        ReflectionTestUtils.setField(SpringUtils.class, "beanFactory", beans);
        try {
            CourseVo course = new CourseVo();
            course.setId(8L);
            course.setName("Scheme scope regression");
            course.setType("1");
            course.setCourseModule(courseModule);
            TeachingPlan plan = new TeachingPlan();
            plan.setId(101L);
            plan.setSourceCourseId(8L);
            plan.setPlanType(planType);
            CourseTeachingPlanModel model = ReflectionTestUtils.invokeMethod(service, "buildModel",
                    course, plan, detail, 101L, schemes);
            verify(modules).listTargetDesign(101L, null, null);
            verify(modules, times(1)).listTargetDesign(anyLong(), nullable(Long.class), nullable(String.class));
            return model;
        } finally {
            ReflectionTestUtils.setField(SpringUtils.class, "beanFactory", previous);
        }
    }

    private static void assertMethodLabelRoundTrip(String rawTeaching, String rawLearning,
                                                   String teachingLabel, String learningLabel,
                                                   String teachingCodes, String learningCodes) throws Exception {
        Map<String, JSONArray> dictionaries = new LinkedHashMap<>();
        dictionaries.put(DictUtils.getCacheKey("sys_plan_teaching_method"),
                dictionary("M", "PBL/CBL", "N", "Other method", "D", "PBL/CBL"));
        dictionaries.put(DictUtils.getCacheKey("sys_plan_learning_method"),
                dictionary("A", "Self/Peer", "B", "Other learning", "C", "Self/Peer"));
        TeachingPlanTargetDesign design = scopedDesign(1L, KNOWLEDGE, 10L, "Objective", 1);
        design.setTeachingMethod(rawTeaching);
        design.setLearningMethod(rawLearning);
        CourseTeachingPlanModel model = buildServiceModel(Collections.singletonList(design), detail(false),
                "professional", 1, Collections.singletonList(scheme(10L)), dictionaries);
        byte[] bytes = generate(model);
        Element table = table(bytes, KNOWLEDGE_HEADER.get(0));
        TeachingPlanWordImporter.ParseContext context = new TeachingPlanWordImporter.ParseContext();
        context.expectedDocType = 1;
        for (String type : Arrays.asList("sys_plan_teaching_method", "sys_plan_learning_method")) {
            for (Object value : dictionaries.get(DictUtils.getCacheKey(type))) {
                SysDictData dict = (SysDictData) value;
                TeachingPlanWordImporter.putDict(context.dictLabelToValue, type, dict.getDictLabel(), dict.getDictValue());
            }
        }
        TeachingPlanWordImporter.ParseResult parsed =
                new TeachingPlanWordImporter().parse(new ByteArrayInputStream(bytes), context);
        assertEquals(1, parsed.targetDesigns.size());
        TeachingPlanTargetDesign imported = parsed.targetDesigns.get(0);
        assertAll(
                () -> assertEquals(teachingLabel, text(cell(row(table, 1), 5)), "Teaching label in DOCX XML"),
                () -> assertEquals(learningLabel, text(cell(row(table, 1), 6)), "Learning label in DOCX XML"),
                () -> assertEquals(teachingCodes, imported.getTeachingMethod(), "Teaching codes after import"),
                () -> assertEquals(learningCodes, imported.getLearningMethod(), "Learning codes after import"));
    }

    private static TeachingPlanDetailVo detail(boolean publicFoundation) {
        TeachingPlanDetailVo detail = new TeachingPlanDetailVo();
        detail.setPublicFoundation(publicFoundation);
        return detail;
    }

    private static TeachingPlanSchemeVo scheme(Long id) {
        TeachingPlanSchemeVo scheme = new TeachingPlanSchemeVo();
        scheme.setSchemeId(id);
        return scheme;
    }

    private static TeachingPlanTargetDesign scopedDesign(Long id, String type, Long schemeId, String objective, int sort) {
        TeachingPlanTargetDesign design = design(id, type, objective, point(1L, "Unit", 11L, "Point"));
        design.setSchemeId(schemeId);
        design.setSort(sort);
        return design;
    }

    private static TeachingPlanTargetDesign design(Long id, String type, String objective,
                                                   TeachingPlanTargetDesign.KnowledgePointItem... points) {
        TeachingPlanTargetDesign design = new TeachingPlanTargetDesign();
        design.setId(id);
        design.setDesignTypeCode(type);
        design.setObjectiveText(objective);
        design.setContentText("Content");
        design.setObservationPoint("Observation");
        design.setTeachingDesign("Design");
        design.setTeachingLink("Link");
        design.setTeachingMethod("Method");
        design.setLearningMethod("Learning");
        design.setHours(new BigDecimal("2.500"));
        design.setKnowledgePoints(Arrays.asList(points));
        return design;
    }

    private static TeachingPlanTargetDesign.KnowledgePointItem point(Long unitId, String unit, Long pointId, String name) {
        TeachingPlanTargetDesign.KnowledgePointItem item = new TeachingPlanTargetDesign.KnowledgePointItem();
        item.setKnowledgeUnitId(unitId);
        item.setKnowledgeUnitName(unit);
        item.setKnowledgePointId(pointId);
        item.setKnowledgePointName(name);
        return item;
    }

    private static CourseKnowledgeUnitVo unit(Long id, String name, CourseKnowledgePointVo... points) {
        CourseKnowledgeUnitVo unit = new CourseKnowledgeUnitVo();
        unit.setId(id);
        unit.setName(name);
        unit.setKnowledgePoints(Arrays.asList(points));
        return unit;
    }

    private static CourseKnowledgePointVo meta(Long id, String name) {
        CourseKnowledgePointVo point = new CourseKnowledgePointVo();
        point.setId(id);
        point.setName(name);
        return point;
    }

    private static JSONArray dictionary(String... pairs) {
        JSONArray array = new JSONArray();
        for (int i = 0; i < pairs.length; i += 2) {
            SysDictData dict = new SysDictData();
            dict.setDictValue(pairs[i]);
            dict.setDictLabel(pairs[i + 1]);
            array.add(dict);
        }
        return array;
    }

    private static byte[] generate(CourseTeachingPlanModel model) throws Exception {
        try (InputStream input = new CourseTeachingPlanGenerator().generate(model)) {
            return input.readAllBytes();
        }
    }

    private static TeachingPlanWordImporter.ParseResult parse(byte[] bytes) throws Exception {
        TeachingPlanWordImporter.ParseContext context = new TeachingPlanWordImporter.ParseContext();
        context.expectedDocType = 1;
        return new TeachingPlanWordImporter().parse(new ByteArrayInputStream(bytes), context);
    }

    private static List<String> objectives(List<TeachingPlanTargetDesign> designs) {
        List<String> values = new ArrayList<>();
        for (TeachingPlanTargetDesign design : designs) values.add(design.getObjectiveText());
        return values;
    }

    private static void fill(XWPFTable table, int row, List<String> values) {
        for (int c = 0; c < values.size(); c++) table.getRow(row).getCell(c).setText(values.get(c));
    }

    private static Element table(byte[] docx, String firstHeader) throws Exception {
        try (ZipInputStream zip = new ZipInputStream(new ByteArrayInputStream(docx))) {
            for (ZipEntry entry; (entry = zip.getNextEntry()) != null;) {
                if (!"word/document.xml".equals(entry.getName())) continue;
                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                factory.setNamespaceAware(true);
                factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
                NodeList tables = factory.newDocumentBuilder()
                        .parse(new ByteArrayInputStream(zip.readAllBytes())).getElementsByTagNameNS(W, "tbl");
                for (int i = 0; i < tables.getLength(); i++) {
                    Element table = (Element) tables.item(i);
                    // On the red baseline, the old header still begins with a sequence column.
                    if (texts(row(table, 0)).contains(firstHeader)) return table;
                }
            }
        }
        throw new AssertionError("Missing table " + firstHeader);
    }

    private static List<Element> children(Element parent, String name) {
        List<Element> result = new ArrayList<>();
        for (Node node = parent.getFirstChild(); node != null; node = node.getNextSibling()) {
            if (node instanceof Element && W.equals(node.getNamespaceURI()) && name.equals(node.getLocalName())) {
                result.add((Element) node);
            }
        }
        return result;
    }

    private static Element row(Element table, int index) {
        return children(table, "tr").get(index);
    }

    private static List<Element> cells(Element row) {
        return children(row, "tc");
    }

    private static Element cell(Element row, int index) {
        return cells(row).get(index);
    }

    private static String text(Element cell) {
        NodeList runs = cell.getElementsByTagNameNS(W, "t");
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < runs.getLength(); i++) result.append(runs.item(i).getTextContent());
        return result.toString();
    }

    private static List<String> texts(Element row) {
        List<String> result = new ArrayList<>();
        for (Element cell : cells(row)) result.add(text(cell));
        return result;
    }

    private static List<String> column(Element table, int column) {
        List<String> result = new ArrayList<>();
        List<Element> rows = children(table, "tr");
        for (int i = 1; i < rows.size(); i++) result.add(text(cell(rows.get(i), column)));
        return result;
    }

    private static String merge(Element cell) {
        NodeList merges = cell.getElementsByTagNameNS(W, "vMerge");
        if (merges.getLength() == 0) return "";
        String value = ((Element) merges.item(0)).getAttributeNS(W, "val");
        return value.isEmpty() ? "continue" : value;
    }

    private static List<String> merges(Element table, int column) {
        List<String> result = new ArrayList<>();
        List<Element> rows = children(table, "tr");
        for (int i = 1; i < rows.size(); i++) result.add(merge(cell(rows.get(i), column)));
        return result;
    }

    public static void main(String[] args) throws Exception {
        TeachingPlanTargetDesignWordTest test = new TeachingPlanTargetDesignWordTest();
        int passed = 0;
        List<String> failed = new ArrayList<>();
        for (Method method : TeachingPlanTargetDesignWordTest.class.getDeclaredMethods()) {
            if (!method.isAnnotationPresent(Test.class)) continue;
            try {
                method.invoke(test);
                System.out.println("PASS " + method.getName());
                passed++;
            } catch (InvocationTargetException exception) {
                Throwable cause = exception.getCause();
                System.out.println("FAIL " + method.getName() + ": " + cause);
                failed.add(method.getName());
            }
        }
        System.out.println("Tests passed: " + passed + ", failed: " + failed.size());
        if (!failed.isEmpty()) throw new AssertionError("Failed tests: " + failed);
    }
}
