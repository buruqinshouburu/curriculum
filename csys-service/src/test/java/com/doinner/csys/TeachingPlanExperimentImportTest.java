package com.doinner.csys;

import com.doinner.csys.domain.TeachingPlanAssessment;
import com.doinner.csys.domain.TeachingPlanPracticeItem;
import com.doinner.csys.domain.TeachingPlanPracticeItemDetail;
import com.doinner.csys.domain.TeachingPlanTaskBackground;
import com.doinner.csys.entity.csys.CourseTeachingPlanGenerator;
import com.doinner.csys.entity.csys.TeachingPlanWordImporter;
import com.doinner.csys.entity.csys.model.CourseTeachingPlanModel;
import org.apache.poi.xwpf.usermodel.IBodyElement;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/** Real Word parsing only: no application context, mocks, database or remote services. */
public class TeachingPlanExperimentImportTest {
    private static final String EXPERIMENT_TITLE = "\u5b9e\u9a8c\u8bfe\u7a0b\u6559\u5b66\u8ba1\u5212";

    @Test
    void allFourGeneratedTemplatesAcceptTheirMatchingDocumentType() throws Exception {
        for (int docType = 1; docType <= 4; docType++) {
            CourseTeachingPlanModel model = new CourseTeachingPlanModel();
            model.setDocType(docType);
            model.setCourseName("Import template check");
            TeachingPlanWordImporter.ParseResult parsed = parse(generate(model), docType);
            assertEquals(docType, parsed.docType);
            assertNoErrors(parsed);
        }
    }

    @Test
    void physicsExperimentRoundTripRetainsBackgroundDetailsArrangementAndAssessment() throws Exception {
        TeachingPlanWordImporter.ParseResult parsed = parse(generate(physicsExperiment()), 3);
        assertNoErrors(parsed);
        assertEquals(3, parsed.docType);
        assertEquals(1, parsed.taskBackgrounds.size());
        assertEquals("Verify mechanics and electromagnetic laws",
                parsed.taskBackgrounds.get(0).taskBackground.getBackgroundDesc());
        assertEquals("Measure force and acceleration",
                parsed.taskBackgrounds.get(0).taskBackground.getTechnicalGoal());
        assertEquals("Analyze experimental errors",
                parsed.taskBackgrounds.get(0).taskBackground.getAbilityGoal());
        assertEquals(2, parsed.practiceItems.size());
        for (int i = 0; i < 2; i++) {
            TeachingPlanWordImporter.ParsedPracticeItem item = parsed.practiceItems.get(i);
            assertEquals(i == 0 ? "Newton second law" : "Electromagnetic induction", item.item.getName());
            assertEquals(0, new BigDecimal("2").compareTo(item.item.getHours()));
            assertEquals("Two students", item.item.getGroupInfo());
            assertEquals("Verification", item.item.getExperimentNature());
            assertEquals("Required", item.item.getStudyNature());
            assertEquals(5, item.details.size());
            assertEquals("purpose_task", item.details.get(0).getDetailType());
            assertEquals("Collect measurements " + i, item.details.get(0).getContent());
        }
        assertEquals(1, parsed.assessments.size());
        TeachingPlanAssessment assessment = parsed.assessments.get(0);
        assertEquals(3, assessment.getAssessmentCategory());
        assertEquals("Newton second law", assessment.getAssessmentItem());
        assertEquals("Lab report", assessment.getMethod());
        assertEquals(0, BigDecimal.ONE.compareTo(assessment.getWeight()));
        assertEquals("Accurate results and error analysis", assessment.getStandard());
    }

    @Test
    void experimentDocumentUploadedAsOrdinaryCourseProducesBlockingTypeError() throws Exception {
        TeachingPlanWordImporter.ParseResult parsed = parse(generate(physicsExperiment()), 1);
        assertTrue(parsed.issues.stream().anyMatch(issue ->
                "ERROR".equals(issue.getSeverity()) && "docType".equals(issue.getField())));
    }

    private void localExperimentTemplateRetainsItsActualProjectTable(Path template) throws Exception {
        byte[] bytes;
        try (InputStream input = Files.newInputStream(template);
             XWPFDocument document = new XWPFDocument(input)) {
            List<IBodyElement> elements = document.getBodyElements();
            int start = -1;
            int end = elements.size();
            for (int i = 0; i < elements.size(); i++) {
                if (!(elements.get(i) instanceof XWPFParagraph)) continue;
                String text = ((XWPFParagraph) elements.get(i)).getText();
                if (start < 0 && text.contains(EXPERIMENT_TITLE)) {
                    start = i;
                } else if (start >= 0 && text.contains("\u6559\u5b66\u8ba1\u5212")
                        && text.startsWith("\u300a")) {
                    end = i;
                    break;
                }
            }
            assertTrue(start >= 0, "Missing experiment section in " + template);
            // Keep the original experiment XML and merged cells; never rewrite the source template.
            for (int i = elements.size() - 1; i >= 0; i--) {
                if (i < start || i >= end) document.removeBodyElement(i);
            }
            try (ByteArrayOutputStream output = new ByteArrayOutputStream()) {
                document.write(output);
                bytes = output.toByteArray();
            }
        }
        TeachingPlanWordImporter.ParseResult parsed = parse(bytes, 3);
        assertNoErrors(parsed);
        assertEquals(3, parsed.docType);
        assertFalse(parsed.taskBackgrounds.isEmpty());
        TeachingPlanWordImporter.ParsedPracticeItem project = parsed.practiceItems.stream()
                .filter(item -> "XX\u8bbe\u8ba1\u5b9e\u9a8c".equals(item.item.getName()))
                .findFirst().orElseThrow(() -> new AssertionError("Actual experiment project was not parsed"));
        assertTrue(project.details.stream().anyMatch(detail ->
                "ability_point".equals(detail.getDetailType()) && !detail.getContent().isEmpty()));
    }

    private static CourseTeachingPlanModel physicsExperiment() {
        CourseTeachingPlanModel model = new CourseTeachingPlanModel();
        model.setDocType(3);
        model.setCourseName("\u5927\u5b66\u7269\u7406\u5b9e\u9a8c");
        TeachingPlanTaskBackground background = new TeachingPlanTaskBackground();
        background.setBackgroundDesc("Verify mechanics and electromagnetic laws");
        background.setTechnicalGoal("Measure force and acceleration");
        background.setAbilityGoal("Analyze experimental errors");
        CourseTeachingPlanModel.SchemeTaskBackgroundGroup group =
                new CourseTeachingPlanModel.SchemeTaskBackgroundGroup();
        group.setTaskBackgrounds(Collections.singletonList(background));
        model.setSchemeTaskBackgroundGroups(Collections.singletonList(group));
        TeachingPlanPracticeItem first = project(11L, "Newton second law");
        TeachingPlanPracticeItem second = project(12L, "Electromagnetic induction");
        model.setPracticeItems(Arrays.asList(first, second));
        TeachingPlanPracticeItemDetail firstDetail = new TeachingPlanPracticeItemDetail();
        firstDetail.setDetailType("purpose_task");
        firstDetail.setContent("Collect measurements 0");
        TeachingPlanPracticeItemDetail secondDetail = new TeachingPlanPracticeItemDetail();
        secondDetail.setDetailType("purpose_task");
        secondDetail.setContent("Collect measurements 1");
        java.util.Map<Long, List<TeachingPlanPracticeItemDetail>> details = new java.util.LinkedHashMap<>();
        details.put(11L, Collections.singletonList(firstDetail));
        details.put(12L, Collections.singletonList(secondDetail));
        model.setItemDetailMap(details);
        TeachingPlanAssessment assessment = new TeachingPlanAssessment();
        assessment.setAssessmentCategory(3);
        assessment.setAssessmentItem("Newton second law");
        assessment.setMethod("Lab report");
        assessment.setMechanism("\u767e\u5206\u5236");
        assessment.setWeight(BigDecimal.ONE);
        assessment.setStandard("Accurate results and error analysis");
        model.setAssessments(Collections.singletonList(assessment));
        return model;
    }

    private static TeachingPlanPracticeItem project(Long id, String name) {
        TeachingPlanPracticeItem item = new TeachingPlanPracticeItem();
        item.setId(id);
        item.setItemType(1);
        item.setName(name);
        item.setHours(new BigDecimal("2"));
        item.setGroupInfo("Two students");
        item.setExperimentNature("Verification");
        item.setStudyNature("Required");
        return item;
    }

    private static byte[] generate(CourseTeachingPlanModel model) throws Exception {
        try (InputStream input = new CourseTeachingPlanGenerator().generate(model)) {
            return input.readAllBytes();
        }
    }

    private static TeachingPlanWordImporter.ParseResult parse(byte[] bytes, int expectedDocType) throws Exception {
        TeachingPlanWordImporter.ParseContext context = new TeachingPlanWordImporter.ParseContext();
        context.expectedDocType = expectedDocType;
        context.publicFoundation = true;
        return new TeachingPlanWordImporter().parse(new ByteArrayInputStream(bytes), context);
    }

    private static void assertNoErrors(TeachingPlanWordImporter.ParseResult parsed) {
        assertFalse(parsed.issues.stream().anyMatch(issue -> "ERROR".equals(issue.getSeverity())),
                () -> parsed.issues.stream().map(issue -> issue.getField() + ": " + issue.getMessage())
                        .collect(java.util.stream.Collectors.joining("; ")));
    }

    public static void main(String[] args) throws Exception {
        TeachingPlanExperimentImportTest test = new TeachingPlanExperimentImportTest();
        test.allFourGeneratedTemplatesAcceptTheirMatchingDocumentType();
        System.out.println("PASS allFourGeneratedTemplatesAcceptTheirMatchingDocumentType");
        test.physicsExperimentRoundTripRetainsBackgroundDetailsArrangementAndAssessment();
        System.out.println("PASS physicsExperimentRoundTripRetainsBackgroundDetailsArrangementAndAssessment");
        test.experimentDocumentUploadedAsOrdinaryCourseProducesBlockingTypeError();
        System.out.println("PASS experimentDocumentUploadedAsOrdinaryCourseProducesBlockingTypeError");
        for (String template : args) {
            test.localExperimentTemplateRetainsItsActualProjectTable(Path.of(template));
            System.out.println("PASS localExperimentTemplate: " + Path.of(template).getFileName());
        }
        System.out.println("Tests passed: " + (3 + args.length));
    }
}
