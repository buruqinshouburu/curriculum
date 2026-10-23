package com.doinner.csys;

import com.doinner.csys.domain.TeachingPlanAssessment;
import com.doinner.csys.domain.TeachingPlanObjective;
import com.doinner.csys.domain.TeachingPlanObjectiveAssessment;
import com.doinner.csys.domain.TeachingPlanObjectiveRef;
import com.doinner.csys.domain.vo.TeachingPlanSchemeVo;
import com.doinner.csys.entity.csys.CourseTeachingPlanGenerator;
import com.doinner.csys.entity.csys.TeachingPlanWordImporter;
import com.doinner.csys.entity.csys.model.CourseTeachingPlanModel;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/** Real generator/parser round trips, without Spring, file service or database. */
public class TeachingPlanObjectiveWordTest {
    @Test
    void allFourTemplatesGenerateWithoutSpringOrDatabase() throws Exception {
        for (int type = 1; type <= 4; type++) {
            CourseTeachingPlanModel model = new CourseTeachingPlanModel();
            model.setDocType(type);
            model.setCourseName("Template smoke test");
            try (InputStream input = new CourseTeachingPlanGenerator().generate(model);
                 XWPFDocument document = new XWPFDocument(input)) {
                assertFalse(document.getTables().isEmpty(), "Template " + type);
            }
        }
    }

    @Test
    void schemeObjectivesRoundTripWithSameTypeAndIndependentRefs() throws Exception {
        assertObjectiveRoundTrip(false);
    }

    @Test
    void foundationObjectivesRoundTripWithNullSchemeAndIndependentRefs() throws Exception {
        assertObjectiveRoundTrip(true);
    }

    private void assertObjectiveRoundTrip(boolean publicFoundation) throws Exception {
        TeachingPlanObjective first = objective(11L, "Existing objective", "0.4");
        TeachingPlanObjective second = objective(100L, "Appended objective", "0.6");
        Long schemeId = publicFoundation ? null : 10L;
        first.setSchemeId(schemeId);
        second.setSchemeId(schemeId);
        Map<Long, java.util.List<TeachingPlanObjectiveRef>> refs = new LinkedHashMap<>();
        refs.put(first.getId(), Collections.singletonList(ref(first.getId(), "First graduation")));
        refs.put(second.getId(), Collections.singletonList(ref(second.getId(), "Second graduation")));
        CourseTeachingPlanModel.SchemeObjectiveGroup group = new CourseTeachingPlanModel.SchemeObjectiveGroup();
        group.setSchemeId(schemeId);
        group.setObjectives(Arrays.asList(first, second));
        group.setObjectiveRefMap(refs);
        CourseTeachingPlanModel model = new CourseTeachingPlanModel();
        model.setDocType(1);
        model.setCourseName("Objective regression");
        model.setSchemeObjectiveGroups(Collections.singletonList(group));
        TeachingPlanAssessment exam = new TeachingPlanAssessment();
        exam.setId(91L);
        exam.setAssessmentCategory(1);
        exam.setAssessmentItem("Exam");
        exam.setWeight(BigDecimal.ONE);
        model.setAssessments(Collections.singletonList(exam));
        TeachingPlanObjectiveAssessment existingLink = new TeachingPlanObjectiveAssessment();
        existingLink.setObjectiveId(11L);
        existingLink.setAssessmentId(91L);
        existingLink.setAssessmentItem("Exam");
        existingLink.setWeight(new BigDecimal("0.25"));
        existingLink.setAssessmentItemContent("Existing assessment");
        model.setObjectiveAssessments(Collections.singletonList(existingLink));
        TeachingPlanWordImporter.ParseContext context = new TeachingPlanWordImporter.ParseContext();
        context.publicFoundation = publicFoundation;
        context.expectedDocType = 1;
        TeachingPlanSchemeVo scheme = new TeachingPlanSchemeVo();
        scheme.setSchemeId(10L);
        context.schemes = Collections.singletonList(scheme);
        TeachingPlanWordImporter.putDict(context.dictLabelToValue, "sys_plan_target_type",
                "\u77e5\u8bc6\u76ee\u6807", "knowledge");
        try (InputStream input = new CourseTeachingPlanGenerator().generate(model)) {
            TeachingPlanWordImporter.ParseResult parsed = new TeachingPlanWordImporter().parse(input, context);
            assertEquals(2, parsed.objectives.size());
            TeachingPlanWordImporter.ParsedObjective one = parsed.objectives.get(0);
            TeachingPlanWordImporter.ParsedObjective two = parsed.objectives.get(1);
            assertEquals(first.getContent(), one.objective.getContent());
            assertEquals(second.getContent(), two.objective.getContent());
            assertEquals("knowledge", one.objective.getObjectiveTypeCode());
            assertEquals("knowledge", two.objective.getObjectiveTypeCode());
            assertEquals(schemeId, one.objective.getSchemeId());
            assertEquals(schemeId, two.objective.getSchemeId());
            assertEquals(Collections.singletonList("First graduation"), one.graduationNames);
            assertEquals(Collections.singletonList("Second graduation"), two.graduationNames);
            assertEquals("First graduation", one.graduationRaw);
            assertEquals("Second graduation", two.graduationRaw);
            assertEquals(0, new BigDecimal("0.4").compareTo(one.objective.getWeight()));
            assertEquals(0, new BigDecimal("0.6").compareTo(two.objective.getWeight()));
            assertEquals(1, one.assessments.size());
            assertEquals("Exam", one.assessments.get(0).assessmentItem);
            assertEquals(0, new BigDecimal("0.25").compareTo(one.assessments.get(0).weight));
            assertEquals("Existing assessment", one.assessments.get(0).assessmentItemContent);
            assertTrue(two.assessments.isEmpty());
        }
    }

    private TeachingPlanObjective objective(Long id, String content, String weight) {
        TeachingPlanObjective objective = new TeachingPlanObjective();
        objective.setId(id);
        objective.setObjectiveTypeCode("knowledge");
        objective.setObjectiveTypeName("\u77e5\u8bc6\u76ee\u6807");
        objective.setContent(content);
        objective.setWeight(new BigDecimal(weight));
        return objective;
    }

    private TeachingPlanObjectiveRef ref(Long objectiveId, String graduationName) {
        TeachingPlanObjectiveRef ref = new TeachingPlanObjectiveRef();
        ref.setObjectiveId(objectiveId);
        ref.setGraduationName(graduationName);
        return ref;
    }

    public static void main(String[] args) throws Exception {
        TeachingPlanObjectiveWordTest test = new TeachingPlanObjectiveWordTest();
        test.allFourTemplatesGenerateWithoutSpringOrDatabase();
        System.out.println("PASS allFourTemplatesGenerateWithoutSpringOrDatabase");
        test.schemeObjectivesRoundTripWithSameTypeAndIndependentRefs();
        System.out.println("PASS schemeObjectivesRoundTripWithSameTypeAndIndependentRefs");
        test.foundationObjectivesRoundTripWithNullSchemeAndIndependentRefs();
        System.out.println("PASS foundationObjectivesRoundTripWithNullSchemeAndIndependentRefs");
        System.out.println("Tests passed: 3");
    }
}
