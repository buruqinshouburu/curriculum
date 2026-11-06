package com.doinner.csys;

import com.doinner.common.core.context.SecurityContextHolder;
import com.doinner.csys.dao.CourseMapper;
import com.doinner.csys.dao.TeachingPlanContentMapper;
import com.doinner.csys.dao.TeachingPlanContextMapper;
import com.doinner.csys.dao.TeachingPlanTargetDesignMapper;
import com.doinner.csys.domain.TeachingPlanContent;
import com.doinner.csys.domain.TeachingPlanTargetDesign;
import com.doinner.csys.domain.vo.TeachingPlanImportResultVo;
import com.doinner.csys.entity.csys.CourseTeachingPlanGenerator;
import com.doinner.csys.entity.csys.TeachingPlanWordImporter;
import com.doinner.csys.entity.csys.model.CourseTeachingPlanModel;
import com.doinner.csys.service.impl.TeachingPlanModuleServiceImpl;
import com.doinner.csys.service.impl.TeachingPlanServiceImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/** Real import persistence and Word parsing, with mapper boundaries replaced in memory. */
public class TeachingPlanTargetDesignContentImportTest {
    private static final String WHOLE_COURSE = "\u6574\u95e8\u8bfe\u7a0b";
    private final TeachingPlanServiceImpl service = new TeachingPlanServiceImpl();
    private final TeachingPlanContentMapper contents = mock(TeachingPlanContentMapper.class);
    private final TeachingPlanTargetDesignMapper designs = mock(TeachingPlanTargetDesignMapper.class);
    private final List<TeachingPlanTargetDesign> saved = new ArrayList<>();
    private final AtomicLong generatedId = new AtomicLong(9000);

    @BeforeEach
    void setup() {
        SecurityContextHolder.setUserName("target-content-import-test");
        ReflectionTestUtils.setField(service, "courseMapper", mock(CourseMapper.class));
        ReflectionTestUtils.setField(service, "teachingPlanContextMapper", mock(TeachingPlanContextMapper.class));
        ReflectionTestUtils.setField(service, "teachingPlanContentMapper", contents);
        ReflectionTestUtils.setField(service, "teachingPlanTargetDesignMapper", designs);
        when(contents.insert(any())).thenAnswer(call -> {
            TeachingPlanContent content = call.getArgument(0);
            assertNull(content.getId(), "Old Word IDs must be cleared before inserting");
            assertEquals(101L, content.getPlanId());
            content.setId(generatedId.incrementAndGet());
            return 1;
        });
        when(designs.insert(any())).thenAnswer(call -> {
            TeachingPlanTargetDesign design = call.getArgument(0);
            assertEquals(101L, design.getPlanId());
            saved.add(design);
            return 1;
        });
    }

    @AfterEach
    void cleanup() {
        SecurityContextHolder.remove();
    }

    @Test
    void abilityResolvesMultipleBodiesInSnapshotOrderUsingNewIds() {
        assertBindings("2", "Body B\nBody A", "[9002,9001]",
                chapter("A", "Body A"), chapter("B", "Body B"));
        org.mockito.InOrder order = inOrder(contents, designs);
        order.verify(contents, times(2)).insert(any());
        order.verify(designs).insert(any());
    }

    @Test
    void qualityAcceptsTitlesAndBodiesAndDeduplicatesTheSameRecord() {
        assertBindings("3", "B\nBody A\nB", "[9002,9001]",
                chapter("A", "Body A"), chapter("B", "Body B"));
    }

    @Test
    void completeChinesePunctuationInsideABodyIsNotSplit() {
        String body = "\u7c7b\u3001\u7ee7\u627f\uff0c\u591a\u6001\uff1b\u5c01\u88c5, interfaces; A/B|C";
        assertBindings("2", body, "[9001]", chapter("Chapter", body),
                chapter("\u7c7b", ""), chapter("\u7ee7\u627f", ""));
    }

    @Test
    void punctuationJoinedNamesRemainCustomWithoutAnExactSnapshot() {
        assertBindings("3", "A\u3001B\uff0cC\uff1bD, E; F/G|H", "[]",
                chapter("A", ""), chapter("B", ""), chapter("C", ""), chapter("D", ""),
                chapter("E", ""), chapter("F", ""), chapter("G", ""), chapter("H", ""));
    }

    @Test
    void duplicateTitlesRemainCustom() {
        assertBindings("2", "Duplicate", "[]",
                chapter("Duplicate", "First body"), chapter("Duplicate", "Second body"));
    }

    @Test
    void duplicateBodiesRemainCustom() {
        assertBindings("3", "Same body", "[]",
                chapter("A", "Same body"), chapter("B", "Same body"));
    }

    @Test
    void titleAndAnotherRecordsBodyCollisionRemainCustom() {
        assertBindings("2", "Collision", "[]",
                chapter("Collision", "First body"), chapter("B", "Collision"));
    }

    @Test
    void uniqueBodyStillResolvesWhenItsTitleIsDuplicated() {
        assertBindings("3", "Second body", "[9002]",
                chapter("Duplicate", "First body"), chapter("Duplicate", "Second body"));
    }

    @Test
    void identicalTitleAndBodyOnOneRecordAreNotAmbiguous() {
        assertBindings("2", "Same", "[9001]", chapter("Same", "Same"));
    }

    @Test
    void multilineBodiesTakePrecedenceOverTheirIndividualLines() {
        assertBindings("2", "Part A\nPart B\nTail", "[9001,9004]",
                chapter("Multiline", "Part A\nPart B"),
                chapter("Part A", ""), chapter("Part B", ""), chapter("Last", "Tail"));
    }

    @Test
    void abilityOverlappingBodiesDoNotBindAnUnselectedSuffixAlias() {
        assertBindings("2", "A\nB\nA\nB\nA", "[9001,9002]",
                chapter("First", "A\nB"), chapter("Second", "A\nB\nA"),
                chapter("B", "Unselected body"));
    }

    @Test
    void qualityOverlappingBodiesDoNotBindAnUnselectedSuffixAlias() {
        assertBindings("3", "A\nB\nA\nB\nA", "[9001,9002]",
                chapter("First", "A\nB"), chapter("Second", "A\nB\nA"),
                chapter("B", "Unselected body"));
    }

    @Test
    void equalCoverageParsesKeepOnlyTheirCommonIdsInSnapshotOrder() {
        assertBindings("2", "Intro\nA\nB\nC\nTail", "[9001,9006]",
                chapter("Common first", "Intro"), chapter("First", "A\nB"),
                chapter("Second", "C"), chapter("Third", "A"),
                chapter("Fourth", "B\nC"), chapter("Common last", "Tail"));
    }

    @Test
    void ambiguousMultilineBodyDoesNotFallBackToBindingItsFragments() {
        assertBindings("3", "Part A\nPart B\nTail", "[9005]",
                chapter("First", "Part A\nPart B"), chapter("Second", "Part A\nPart B"),
                chapter("Part A", ""), chapter("Part B", ""), chapter("Last", "Tail"));
    }

    @Test
    void mixedKnownChaptersAndCustomTextKeepTheOriginalText() {
        assertBindings("2", "Body B\nTeacher's custom exercise\nBody A", "[9002,9001]",
                chapter("A", "Body A"), chapter("B", "Body B"));
    }

    @Test
    void customTextDoesNotBindBySubstring() {
        assertBindings("3", "Discuss Body A with the class", "[]", chapter("A", "Body A"));
    }

    @Test
    void missingChaptersLeaveAllTextCustom() {
        assertBindings("2", "Unlisted chapter\nTeacher's custom exercise", "[]");
    }

    @Test
    void missingGeneratedKeysNeverReuseOldIdsOrInventNewOnes() {
        doReturn(1).when(contents).insert(any());
        TeachingPlanContent content = chapter("A", "Body A");
        assertBindings("3", "Body A", "[]", content);
        assertNull(content.getId());
    }

    @Test
    void explicitWholeCourseNeverBindsEvenToAChapterWithTheSameName() {
        assertBindings("2", WHOLE_COURSE, "[]", chapter(WHOLE_COURSE, WHOLE_COURSE));
    }

    @Test
    void wholeCourseMixedWithChaptersStaysCustomInsteadOfCreatingConflictingSelections() {
        assertBindings("3", WHOLE_COURSE + "\nBody A", "[]", chapter("A", "Body A"));
    }

    @Test
    void matchingNormalizesOnlyLineEndingsAndOuterLineWhitespaceWithoutChangingTheSnapshot() {
        assertBindings("3", "  Body A \r\n \r\n Body B  ", "[9001,9002]",
                chapter("A", "Body A"), chapter("B", "Body B"));
    }

    @Test
    void blankAndNullSnapshotsHaveNoBindings() {
        assertBindings("2", null, "[]", chapter("A", "Body A"));
        generatedId.set(9000);
        assertBindings("3", " \r\n ", "[]", chapter("A", "Body A"));
    }

    @Test
    void knowledgeDesignContentAndObjectiveLinkageRemainUnchanged() {
        TeachingPlanTargetDesign design = design("1", "Body A");
        design.setContentIds("[777]");
        TeachingPlanTargetDesign.KnowledgePointItem point = new TeachingPlanTargetDesign.KnowledgePointItem();
        point.setKnowledgeUnitId(11L);
        point.setKnowledgePointId(22L);
        point.setKnowledgePointName("Point");
        design.setKnowledgePoints(Collections.singletonList(point));
        persist(parsed(design, chapter("A", "Body A")));
        assertEquals("[777]", design.getContentIds());
        assertEquals("Body A", design.getContentText());
        assertTrue(design.getKnowledgePointsJson().contains("\"knowledgePointId\":22"));
        assertEquals(88L, design.getObjectiveId());
        assertEquals(66L, design.getSchemeId());
        assertEquals("Original objective", design.getObjectiveText());
    }

    @Test
    void dictionaryUnavailableAbilityAndQualityFallbackTypesAlsoResolve() {
        assertBindings("\u80fd\u529b\u76ee\u6807", "Body A", "[9001]", chapter("A", "Body A"));
        generatedId.set(9000);
        assertBindings("\u7d20\u8d28\u76ee\u6807", "Body A", "[9001]", chapter("A", "Body A"));
    }

    @Test
    void actualWordRoundTripRestoresSelectionsThroughTheExistingListService() throws Exception {
        String snapshot = "Body B\nCustom exercise\nBody A";
        CourseTeachingPlanModel model = new CourseTeachingPlanModel();
        model.setDocType(1);
        model.setCourseName("Target content import");
        model.setContents(Arrays.asList(chapter("A", "Body A"), chapter("B", "Body B")));
        TeachingPlanTargetDesign ability = design("2", snapshot);
        ability.setDesignTypeName("\u80fd\u529b\u76ee\u6807");
        TeachingPlanTargetDesign quality = design("3", WHOLE_COURSE);
        quality.setDesignTypeName("\u7d20\u8d28\u76ee\u6807");
        model.setTargetDesigns(Arrays.asList(ability, quality));
        TeachingPlanWordImporter.ParseContext context = new TeachingPlanWordImporter.ParseContext();
        context.expectedDocType = 1;
        context.publicFoundation = true;
        TeachingPlanWordImporter.putDict(context.dictLabelToValue, "sys_plan_target_type",
                "\u80fd\u529b\u76ee\u6807", "2");
        TeachingPlanWordImporter.putDict(context.dictLabelToValue, "sys_plan_target_type",
                "\u7d20\u8d28\u76ee\u6807", "3");
        TeachingPlanWordImporter.ParseResult parsed;
        try (InputStream input = new CourseTeachingPlanGenerator().generate(model)) {
            parsed = new TeachingPlanWordImporter().parse(input, context);
        }
        assertEquals(2, parsed.contents.size());
        assertEquals(2, parsed.targetDesigns.size());
        assertEquals(snapshot, parsed.targetDesigns.get(0).getContentText());
        persist(parsed);

        TeachingPlanModuleServiceImpl modules = new TeachingPlanModuleServiceImpl();
        ReflectionTestUtils.setField(modules, "teachingPlanTargetDesignMapper", designs);
        when(designs.selectByPlanSchemeAndType(101L, null, "2"))
                .thenReturn(Collections.singletonList(saved.get(0)));
        TeachingPlanTargetDesign restored = modules.listTargetDesign(101L, null, "2").get(0);
        assertEquals("[9002,9001]", restored.getContentIds());
        assertEquals(snapshot, restored.getContentText());
        assertEquals("[]", saved.get(1).getContentIds());
        assertEquals(WHOLE_COURSE, saved.get(1).getContentText());
    }

    private void assertBindings(String type, String snapshot, String expected, TeachingPlanContent... chapters) {
        TeachingPlanTargetDesign design = design(type, snapshot);
        persist(parsed(design, chapters));
        assertEquals(snapshot, design.getContentText(), "The original snapshot must not be rewritten");
        assertEquals(88L, design.getObjectiveId());
        assertEquals(66L, design.getSchemeId());
        assertEquals("Original objective", design.getObjectiveText());
        assertEquals(expected, design.getContentIds());
    }

    private void persist(TeachingPlanWordImporter.ParseResult parsed) {
        TeachingPlanImportResultVo result = new TeachingPlanImportResultVo();
        ReflectionTestUtils.invokeMethod(service, "persistImportData", 101L, 8L, 1, true, parsed, result);
        assertTrue(result.getIssues().isEmpty(), "Custom text is valid and must not produce import errors");
    }

    private static TeachingPlanWordImporter.ParseResult parsed(TeachingPlanTargetDesign design,
                                                              TeachingPlanContent... chapters) {
        TeachingPlanWordImporter.ParseResult parsed = new TeachingPlanWordImporter.ParseResult();
        parsed.contents.addAll(Arrays.asList(chapters));
        parsed.targetDesigns.add(design);
        return parsed;
    }

    private static TeachingPlanContent chapter(String title, String body) {
        TeachingPlanContent content = new TeachingPlanContent();
        content.setId(123L);
        content.setTitle(title);
        content.setContent(body);
        content.setContentType(1);
        return content;
    }

    private static TeachingPlanTargetDesign design(String type, String snapshot) {
        TeachingPlanTargetDesign design = new TeachingPlanTargetDesign();
        design.setDesignTypeCode(type);
        design.setContentText(snapshot);
        design.setObjectiveId(88L);
        design.setSchemeId(66L);
        design.setObjectiveText("Original objective");
        design.setObservationPoint("Observation");
        design.setTeachingDesign("Design");
        return design;
    }
}
