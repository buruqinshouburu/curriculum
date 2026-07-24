package com.doinner.csys.entity.csys;

import com.doinner.csys.domain.TeachingPlanAssessment;
import com.doinner.csys.domain.TeachingPlanCondition;
import com.doinner.csys.domain.TeachingPlanContent;
import com.doinner.csys.domain.TeachingPlanObjective;
import com.doinner.csys.domain.TeachingPlanPracticeItem;
import com.doinner.csys.domain.TeachingPlanPracticeItemDetail;
import com.doinner.csys.domain.TeachingPlanSection;
import com.doinner.csys.domain.TeachingPlanTargetDesign;
import com.doinner.csys.domain.TeachingPlanTeacher;
import com.doinner.csys.domain.TeachingPlanTextbook;
import com.doinner.csys.domain.vo.TeachingPlanImportIssueVo;
import com.doinner.csys.domain.vo.TeachingPlanSchemeVo;
import com.doinner.csys.entity.csys.po.CourseKnowledgeUnit;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.xwpf.usermodel.BodyElementType;
import org.apache.poi.xwpf.usermodel.IBodyElement;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTcPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STMerge;

import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 教学计划 Word 解析器（与 {@link CourseTeachingPlanGenerator} 版式对称）。
 * 只做解析，不落库；匹配失败写入 issues，调用方决定跳过策略。
 */
public class TeachingPlanWordImporter {

    public static final int DOC_TYPE_COURSE = CourseTeachingPlanGenerator.DOC_TYPE_COURSE;
    public static final int DOC_TYPE_PRACTICE_SUBJECT = CourseTeachingPlanGenerator.DOC_TYPE_PRACTICE_SUBJECT;
    public static final int DOC_TYPE_EXPERIMENT_COURSE = CourseTeachingPlanGenerator.DOC_TYPE_EXPERIMENT_COURSE;
    public static final int DOC_TYPE_PRACTICE_PROJECT = CourseTeachingPlanGenerator.DOC_TYPE_PRACTICE_PROJECT;

    private static final Pattern H1 = Pattern.compile("^[一二三四五六七八九十]+[、.．].+");
    private static final Pattern H2_PAREN = Pattern.compile("^（[一二三四五六七八九十]+）.+");
    private static final Pattern H2_SCHEME = Pattern.compile(".+");

    /** 实践明细标签 → detailType */
    private static final Map<String, String> DETAIL_LABEL_TO_CODE = new LinkedHashMap<>();

    static {
        DETAIL_LABEL_TO_CODE.put("目的与任务", "purpose_task");
        DETAIL_LABEL_TO_CODE.put("训练的能力点", "ability_point");
        DETAIL_LABEL_TO_CODE.put("原理", "principle");
        DETAIL_LABEL_TO_CODE.put("内容及要求", "content_requirement");
        DETAIL_LABEL_TO_CODE.put("结果及要求", "result_requirement");
        DETAIL_LABEL_TO_CODE.put("教学设计", "teaching_design");
        DETAIL_LABEL_TO_CODE.put("拟解决的复杂问题", "complex_problem");
        DETAIL_LABEL_TO_CODE.put("主要任务", "main_task");
        DETAIL_LABEL_TO_CODE.put("总体设计", "overall_design");
        DETAIL_LABEL_TO_CODE.put("成果形式及要求", "outcome_requirement");
    }

    private static final Map<String, String> SECTION_TITLE_TO_CODE = new LinkedHashMap<>();

    static {
        SECTION_TITLE_TO_CODE.put("任务背景", "task_background");
        SECTION_TITLE_TO_CODE.put("任务背景描述", "task_background");
        SECTION_TITLE_TO_CODE.put("技术目标", "tech_target");
        SECTION_TITLE_TO_CODE.put("能力目标", "ability_target");
        SECTION_TITLE_TO_CODE.put("训练任务", "task");
        SECTION_TITLE_TO_CODE.put("总体设计", "overall_design");
        SECTION_TITLE_TO_CODE.put("拟解决的复杂问题", "complex_problem");
        SECTION_TITLE_TO_CODE.put("主要任务", "main_task");
    }

    /** 解析上下文（字典、方案、知识单元） */
    public static class ParseContext {
        public Map<String, String> dictLabelToValue = new HashMap<>();
        public List<TeachingPlanSchemeVo> schemes = new ArrayList<>();
        public boolean publicFoundation;
        public List<CourseKnowledgeUnit> knowledgeUnits = new ArrayList<>();
        /** 课程 type 映射出的期望 docType，仅用于对照 WARN */
        public Integer expectedDocType;
    }

    /** 解析结果 */
    public static class ParseResult {
        public int docType = DOC_TYPE_COURSE;
        public String courseEnName;
        public String scoreRule;
        public List<TeachingPlanTeacher> teachers = new ArrayList<>();
        public List<TeachingPlanSection> sections = new ArrayList<>();
        public List<ParsedObjective> objectives = new ArrayList<>();
        public List<TeachingPlanContent> contents = new ArrayList<>();
        public List<TeachingPlanTargetDesign> targetDesigns = new ArrayList<>();
        public List<ParsedPracticeItem> practiceItems = new ArrayList<>();
        public List<TeachingPlanAssessment> assessments = new ArrayList<>();
        public List<TeachingPlanTextbook> textbooks = new ArrayList<>();
        public List<TeachingPlanCondition> conditions = new ArrayList<>();
        public List<TeachingPlanImportIssueVo> issues = new ArrayList<>();
    }

    public static class ParsedObjective {
        public TeachingPlanObjective objective = new TeachingPlanObjective();
        public List<String> graduationNames = new ArrayList<>();
        public String schemeTitle;
    }

    public static class ParsedPracticeItem {
        public TeachingPlanPracticeItem item = new TeachingPlanPracticeItem();
        public List<TeachingPlanPracticeItemDetail> details = new ArrayList<>();
    }

    public ParseResult parse(InputStream in, ParseContext ctx) throws Exception {
        if (ctx == null) {
            ctx = new ParseContext();
        }
        ParseResult result = new ParseResult();
        try (XWPFDocument doc = new XWPFDocument(in)) {
            result.docType = detectDocType(doc, result.issues, ctx.expectedDocType);
            walk(doc, result, ctx);
        }
        return result;
    }

    // ============================ walk ============================

    private void walk(XWPFDocument doc, ParseResult result, ParseContext ctx) {
        String section = "";
        String subSection = "";
        String pendingSchemeTitle = null;
        // type1 课程概述：H3 累积
        String pendingSectionTitle = null;
        StringBuilder pendingSectionBody = new StringBuilder();

        List<IBodyElement> elements = doc.getBodyElements();
        for (int i = 0; i < elements.size(); i++) {
            IBodyElement el = elements.get(i);
            if (el.getElementType() == BodyElementType.PARAGRAPH) {
                XWPFParagraph p = (XWPFParagraph) el;
                String text = norm(p.getText());
                if (StringUtils.isBlank(text)) {
                    continue;
                }
                // 说明段落跳过
                if (text.startsWith("说明") || text.startsWith("教学环节主要包括")
                        || text.startsWith("教法主要包括") || text.startsWith("学法主要包括")) {
                    continue;
                }
                if (isH1(text, p)) {
                    flushOverviewSection(result, pendingSectionTitle, pendingSectionBody);
                    pendingSectionTitle = null;
                    pendingSectionBody.setLength(0);
                    section = text;
                    subSection = "";
                    pendingSchemeTitle = null;
                    continue;
                }
                if (isH2(text, p) || isSchemeSubtitle(section, text, p)) {
                    flushOverviewSection(result, pendingSectionTitle, pendingSectionBody);
                    pendingSectionTitle = null;
                    pendingSectionBody.setLength(0);
                    // 目标节下的方案小标题 或 六节下的（一）（二）（三）
                    if (section.contains("目标与支撑毕业要求") || section.contains("训练目的")) {
                        pendingSchemeTitle = text;
                    } else {
                        subSection = text;
                    }
                    continue;
                }
                // type1 三、课程概述：H3 或普通标题段落
                if (section.contains("课程概述")) {
                    if (isH3(text, p) || looksLikeSectionTitle(text)) {
                        flushOverviewSection(result, pendingSectionTitle, pendingSectionBody);
                        pendingSectionTitle = text;
                        pendingSectionBody.setLength(0);
                    } else if (pendingSectionTitle != null) {
                        if (pendingSectionBody.length() > 0) {
                            pendingSectionBody.append('\n');
                        }
                        pendingSectionBody.append(text);
                    } else {
                        // 无标题直接正文
                        pendingSectionTitle = "课程概述";
                        pendingSectionBody.append(text);
                    }
                    continue;
                }
            } else if (el.getElementType() == BodyElementType.TABLE) {
                flushOverviewSection(result, pendingSectionTitle, pendingSectionBody);
                pendingSectionTitle = null;
                pendingSectionBody.setLength(0);

                XWPFTable table = (XWPFTable) el;
                List<List<String>> grid = tableToGrid(table);
                if (grid.isEmpty()) {
                    continue;
                }
                dispatchTable(section, subSection, pendingSchemeTitle, grid, result, ctx);
                // 方案小标题只作用于紧随其后的目标表
                if (section.contains("目标与支撑毕业要求")) {
                    pendingSchemeTitle = null;
                }
            }
        }
        flushOverviewSection(result, pendingSectionTitle, pendingSectionBody);
    }

    private void flushOverviewSection(ParseResult result, String title, StringBuilder body) {
        if (StringUtils.isBlank(title) && (body == null || body.length() == 0)) {
            return;
        }
        TeachingPlanSection s = new TeachingPlanSection();
        s.setSectionTitle(StringUtils.defaultIfBlank(title, "课程概述"));
        s.setSectionCode(mapSectionCode(s.getSectionTitle(), result.sections.size() + 1));
        s.setContent(body == null ? "" : body.toString().trim());
        s.setSort(result.sections.size() + 1);
        result.sections.add(s);
    }

    private void dispatchTable(String section, String subSection, String schemeTitle,
                               List<List<String>> grid, ParseResult result, ParseContext ctx) {
        String header = joinHeader(grid.get(0));
        // 基本信息：含课程名称/课目名称/项目名称
        if (headerContains(grid, "课程名称") || headerContains(grid, "课目名称")
                || headerContains(grid, "项目名称") && headerContains(grid, "项目编号")
                || cellEquals(grid, 0, 0, "课程名称") || cellEquals(grid, 0, 0, "课目名称")
                || cellEquals(grid, 0, 0, "项目名称")) {
            parseBasicInfo(grid, result);
            return;
        }
        // 教学团队
        if (header.contains("教员姓名") || header.contains("主讲内容")) {
            parseTeachers(grid, result);
            return;
        }
        // 目标与支撑毕业要求
        if (header.contains("目标类型") && header.contains("支撑毕业要求")) {
            parseObjectives(grid, schemeTitle, result, ctx);
            return;
        }
        // 教学内容 type1
        if (header.contains("专题") && header.contains("学时")) {
            parseContentsType1(grid, result);
            return;
        }
        // 训练内容 type2
        if (header.contains("模块") && header.contains("目的") && header.contains("时间安排")) {
            parseContentsType2(grid, result);
            return;
        }
        // 知识达成设计
        if (header.contains("知识单元") && header.contains("知识点")) {
            parseKnowledgeDesign(grid, result, ctx);
            return;
        }
        // 能力/素质达成设计
        if (header.contains("观测点") && header.contains("教学设计")) {
            String designType = subSection.contains("素质") ? "素质目标"
                    : (subSection.contains("能力") ? "能力目标" : "能力目标");
            parseAbilityQualityDesign(grid, designType, result);
            return;
        }
        // 实践项目 + 明细
        if (header.contains("项目名称") && header.contains("主要内容")) {
            parsePracticeItems(grid, result);
            return;
        }
        // 实施安排 type3
        if (header.contains("实验项目名称") || (header.contains("分组情况") && header.contains("实验性质"))) {
            parseExperimentArrangement(grid, result);
            return;
        }
        // 考核
        if (header.contains("考核项目") || header.contains("成果类型") || header.contains("评价标准")
                || header.contains("评价准则")) {
            parseAssessments(grid, result);
            return;
        }
        // 教材
        if (header.contains("教材性质") || header.contains("ISBN")) {
            parseTextbooks(grid, result, ctx);
            return;
        }
        // 条件
        if (header.contains("条件类型") && header.contains("有关要求")) {
            parseConditions(grid, result, ctx);
            return;
        }
        // type3 任务背景表
        if (header.contains("任务背景") || header.contains("任务背景描述")) {
            parseTaskBackgroundTable(grid, result);
            return;
        }
        // type2 训练任务/总体设计 标签表
        if (cellEquals(grid, 0, 0, "训练任务") || cellEquals(grid, 0, 0, "总体设计")
                || cellEquals(grid, 0, 0, "拟解决的复杂问题") || cellEquals(grid, 0, 0, "主要任务")) {
            parseLabelContentTable(grid, result);
            return;
        }
        // type2/4 组织实施：实施步骤
        if (header.contains("实施步骤") || header.contains("项目步骤") || cellEquals(grid, 1, 0, "实施步骤")
                || cellEquals(grid, 1, 0, "项目步骤")) {
            parseOrganizationSteps(grid, result);
            return;
        }
        // type2 训练目的（只读毕业要求，跳过）
        if (header.contains("训练目的") && header.contains("支撑毕业要求")) {
            result.issues.add(TeachingPlanImportIssueVo.warn(section, null, "支撑毕业要求",
                    "训练目的表中的毕业要求为只读展示，不导入总库绑定"));
            return;
        }
    }

    // ============================ parsers ============================

    private void parseBasicInfo(List<List<String>> grid, ParseResult result) {
        for (List<String> row : grid) {
            for (int c = 0; c < row.size(); c++) {
                String cell = row.get(c);
                if (cell == null) {
                    continue;
                }
                if (cell.contains("英文名称") && c + 1 < row.size()) {
                    String v = row.get(c + 1);
                    if (StringUtils.isNotBlank(v) && !v.contains("英文名称")) {
                        result.courseEnName = v.trim();
                    }
                }
            }
            // 标签值同行：["课程英文名称", "xxx", ...]
            if (row.size() >= 2 && (row.get(0).contains("英文名称"))) {
                if (StringUtils.isNotBlank(row.get(1))) {
                    result.courseEnName = row.get(1).trim();
                }
            }
        }
    }

    private void parseTeachers(List<List<String>> grid, ParseResult result) {
        // 序号|教员姓名|职称|职责|主讲内容
        for (int r = 1; r < grid.size(); r++) {
            List<String> row = grid.get(r);
            String name = cell(row, 1);
            if (StringUtils.isBlank(name) || isAllBlank(row)) {
                continue;
            }
            TeachingPlanTeacher t = new TeachingPlanTeacher();
            t.setTeacherName(name);
            t.setProfessionalTitle(cell(row, 2));
            t.setDuty(cell(row, 3));
            t.setLectureContent(cell(row, 4));
            t.setSort(r);
            result.teachers.add(t);
        }
    }

    private void parseObjectives(List<List<String>> grid, String schemeTitle,
                                 ParseResult result, ParseContext ctx) {
        Long schemeId = resolveSchemeId(schemeTitle, result, ctx);
        if (schemeId == null && !ctx.publicFoundation && schemeTitle != null) {
            // 已记 ERROR，整组跳过
            return;
        }
        if (schemeId == null && !ctx.publicFoundation && ctx.schemes != null && ctx.schemes.size() > 1
                && StringUtils.isBlank(schemeTitle)) {
            result.issues.add(TeachingPlanImportIssueVo.error("四、课程目标与支撑毕业要求", null, "schemeId",
                    "多培养方案但目标表无方案小标题，整组跳过"));
            return;
        }
        if (schemeId == null && !ctx.publicFoundation && ctx.schemes != null && ctx.schemes.size() == 1) {
            schemeId = ctx.schemes.get(0).getSchemeId();
        }
        if (ctx.publicFoundation) {
            schemeId = null;
        }

        String lastType = "";
        for (int r = 1; r < grid.size(); r++) {
            List<String> row = grid.get(r);
            String type = cell(row, 0);
            if (StringUtils.isBlank(type)) {
                type = lastType;
            } else {
                lastType = type;
            }
            String content = cell(row, 1);
            String refs = cell(row, 2);
            if (StringUtils.isBlank(content)) {
                continue;
            }
            ParsedObjective po = new ParsedObjective();
            po.schemeTitle = schemeTitle;
            po.objective.setSchemeId(schemeId);
            po.objective.setContent(content.trim());
            po.objective.setObjectiveTypeName(type);
            po.objective.setObjectiveTypeCode(mapObjectiveType(type, ctx));
            po.objective.setSourceMode(2);
            po.objective.setSort(result.objectives.size() + 1);
            if (StringUtils.isNotBlank(refs)) {
                for (String part : refs.split("[、,，;；]")) {
                    if (StringUtils.isNotBlank(part)) {
                        po.graduationNames.add(part.trim());
                    }
                }
            }
            result.objectives.add(po);
        }
    }

    private Long resolveSchemeId(String schemeTitle, ParseResult result, ParseContext ctx) {
        if (ctx.publicFoundation) {
            return null;
        }
        if (ctx.schemes == null || ctx.schemes.isEmpty()) {
            return null;
        }
        if (StringUtils.isBlank(schemeTitle)) {
            return null;
        }
        String title = schemeTitle.trim();
        // 精确匹配 buildSchemeTitle
        for (TeachingPlanSchemeVo s : ctx.schemes) {
            if (s == null) {
                continue;
            }
            String built = buildSchemeTitle(s);
            if (title.equals(built)) {
                return s.getSchemeId();
            }
        }
        // 模糊：包含方案名
        for (TeachingPlanSchemeVo s : ctx.schemes) {
            if (s == null || StringUtils.isBlank(s.getSchemeName())) {
                continue;
            }
            if (title.contains(s.getSchemeName())) {
                return s.getSchemeId();
            }
        }
        result.issues.add(TeachingPlanImportIssueVo.error("四、课程目标与支撑毕业要求", schemeTitle, "schemeId",
                "无法匹配培养方案小标题，该组目标跳过: " + schemeTitle));
        return null;
    }

    private static String buildSchemeTitle(TeachingPlanSchemeVo s) {
        if (s == null) {
            return "";
        }
        String name = StringUtils.defaultString(s.getSchemeName());
        if (StringUtils.isNotBlank(s.getSchemeVersion())) {
            if (StringUtils.isNotBlank(name)) {
                return name + "（" + s.getSchemeVersion() + "）";
            }
            return s.getSchemeVersion();
        }
        return name;
    }

    private String mapObjectiveType(String typeName, ParseContext ctx) {
        if (StringUtils.isBlank(typeName)) {
            return typeName;
        }
        String code = reverseDict(typeName, "sys_plan_target_type", ctx);
        if (StringUtils.isNotBlank(code) && !code.equals(typeName)) {
            return code;
        }
        if (typeName.contains("知识")) {
            return "知识目标";
        }
        if (typeName.contains("能力")) {
            return "能力目标";
        }
        if (typeName.contains("素质")) {
            return "素质目标";
        }
        return typeName;
    }

    private void parseContentsType1(List<List<String>> grid, ParseResult result) {
        for (int r = 1; r < grid.size(); r++) {
            List<String> row = grid.get(r);
            if (isAllBlank(row)) {
                continue;
            }
            TeachingPlanContent c = new TeachingPlanContent();
            c.setTitle(cell(row, 0));
            c.setContent(cell(row, 1));
            c.setHours(parseDecimal(cell(row, 2)));
            c.setSort(result.contents.size() + 1);
            if (StringUtils.isBlank(c.getTitle()) && StringUtils.isBlank(c.getContent())) {
                continue;
            }
            result.contents.add(c);
        }
    }

    private void parseContentsType2(List<List<String>> grid, ParseResult result) {
        for (int r = 1; r < grid.size(); r++) {
            List<String> row = grid.get(r);
            if (isAllBlank(row)) {
                continue;
            }
            TeachingPlanContent c = new TeachingPlanContent();
            c.setTitle(cell(row, 0));
            c.setContent(cell(row, 1));
            c.setPurpose(cell(row, 2));
            c.setTimeArrange(cell(row, 3));
            c.setSort(result.contents.size() + 1);
            if (StringUtils.isBlank(c.getTitle()) && StringUtils.isBlank(c.getContent())) {
                continue;
            }
            result.contents.add(c);
        }
    }

    private void parseKnowledgeDesign(List<List<String>> grid, ParseResult result, ParseContext ctx) {
        // 序号|知识单元|知识点|支撑的课程知识目标|教学内容|教学环节|教法|学法|学时
        TeachingPlanTargetDesign current = null;
        List<TeachingPlanTargetDesign.KnowledgePointItem> points = null;
        Integer currentSeq = null;
        for (int r = 1; r < grid.size(); r++) {
            List<String> row = grid.get(r);
            if (isAllBlank(row)) {
                continue;
            }
            Integer seqNum = parseInt(cell(row, 0));
            boolean newGroup = current == null
                    || (seqNum != null && (currentSeq == null || !seqNum.equals(currentSeq)));
            if (newGroup) {
                if (current != null) {
                    finalizeKnowledgeDesign(current, points);
                    result.targetDesigns.add(current);
                }
                current = new TeachingPlanTargetDesign();
                points = new ArrayList<>();
                currentSeq = seqNum;
                current.setDesignTypeCode("知识目标");
                current.setSort(seqNum != null ? seqNum : result.targetDesigns.size() + 1);
                current.setObjectiveText(cell(row, 3));
                current.setContentText(cell(row, 4));
                current.setTeachingLink(reverseDictMulti(cell(row, 5), "sys_plan_teaching_link", ctx, result, "六、达成设计"));
                current.setTeachingMethod(reverseDictMulti(cell(row, 6), "sys_plan_teaching_method", ctx, result, "六、达成设计"));
                current.setLearningMethod(reverseDictMulti(cell(row, 7), "sys_plan_learning_method", ctx, result, "六、达成设计"));
                current.setHours(parseDecimal(cell(row, 8)));
            }
            TeachingPlanTargetDesign.KnowledgePointItem item = new TeachingPlanTargetDesign.KnowledgePointItem();
            String unitName = cell(row, 1);
            String pointName = cell(row, 2);
            item.setKnowledgeUnitName(unitName);
            item.setKnowledgePointName(pointName);
            matchKnowledgeUnit(item, unitName, ctx, result);
            if (points != null && (StringUtils.isNotBlank(unitName) || StringUtils.isNotBlank(pointName))) {
                points.add(item);
            }
        }
        if (current != null) {
            finalizeKnowledgeDesign(current, points);
            result.targetDesigns.add(current);
        }
    }

    private void finalizeKnowledgeDesign(TeachingPlanTargetDesign design,
                                         List<TeachingPlanTargetDesign.KnowledgePointItem> points) {
        if (points == null) {
            points = new ArrayList<>();
        }
        design.setKnowledgePoints(points);
        if (!points.isEmpty()) {
            TeachingPlanTargetDesign.KnowledgePointItem first = points.get(0);
            design.setKnowledgeUnitId(first.getKnowledgeUnitId());
            design.setKnowledgeUnitName(first.getKnowledgeUnitName());
            design.setKnowledgePointId(first.getKnowledgePointId());
            design.setKnowledgePointName(first.getKnowledgePointName());
        }
    }

    private void matchKnowledgeUnit(TeachingPlanTargetDesign.KnowledgePointItem item, String unitName,
                                    ParseContext ctx, ParseResult result) {
        if (StringUtils.isBlank(unitName) || ctx.knowledgeUnits == null) {
            return;
        }
        for (CourseKnowledgeUnit u : ctx.knowledgeUnits) {
            if (u != null && unitName.equals(u.getName())) {
                item.setKnowledgeUnitId(u.getId());
                return;
            }
        }
        result.issues.add(TeachingPlanImportIssueVo.warn("六、达成设计", unitName, "knowledgeUnit",
                "未匹配到知识单元，仅保存名称: " + unitName));
    }

    private void parseAbilityQualityDesign(List<List<String>> grid, String designType, ParseResult result) {
        // 序号|目标|观测点|教学内容|教学设计
        for (int r = 1; r < grid.size(); r++) {
            List<String> row = grid.get(r);
            if (isAllBlank(row)) {
                continue;
            }
            String objectiveText = cell(row, 1);
            if (StringUtils.isBlank(objectiveText) && StringUtils.isBlank(cell(row, 2))) {
                continue;
            }
            TeachingPlanTargetDesign d = new TeachingPlanTargetDesign();
            d.setDesignTypeCode(designType);
            d.setSort(parseInt(cell(row, 0)) != null ? parseInt(cell(row, 0)) : result.targetDesigns.size() + 1);
            d.setObjectiveText(objectiveText);
            d.setObservationPoint(cell(row, 2));
            d.setContentText(cell(row, 3));
            d.setTeachingDesign(cell(row, 4));
            result.targetDesigns.add(d);
        }
    }

    private void parsePracticeItems(List<List<String>> grid, ParseResult result) {
        // 序号|项目名称|主要内容与教学设计（合并行）
        String lastSeq = null;
        String lastName = null;
        ParsedPracticeItem current = null;
        int detailSort = 1;
        for (int r = 1; r < grid.size(); r++) {
            List<String> row = grid.get(r);
            String seq = cell(row, 0);
            String name = cell(row, 1);
            String detailCell = cell(row, 2);
            if (StringUtils.isNotBlank(seq)) {
                lastSeq = seq;
            }
            if (StringUtils.isNotBlank(name)) {
                lastName = name;
            }
            boolean newItem = StringUtils.isNotBlank(cell(row, 0)) || StringUtils.isNotBlank(cell(row, 1));
            if (newItem || current == null) {
                if (current != null && StringUtils.isNotBlank(current.item.getName())) {
                    result.practiceItems.add(current);
                }
                current = new ParsedPracticeItem();
                current.item.setName(StringUtils.defaultIfBlank(name, lastName));
                current.item.setSort(parseInt(StringUtils.defaultIfBlank(seq, lastSeq)) != null
                        ? parseInt(StringUtils.defaultIfBlank(seq, lastSeq))
                        : result.practiceItems.size() + 1);
                detailSort = 1;
            }
            if (StringUtils.isNotBlank(detailCell) && current != null) {
                TeachingPlanPracticeItemDetail d = new TeachingPlanPracticeItemDetail();
                int colon = indexOfColon(detailCell);
                if (colon > 0) {
                    String label = detailCell.substring(0, colon).trim();
                    String content = detailCell.substring(colon + 1).trim();
                    d.setDetailType(DETAIL_LABEL_TO_CODE.getOrDefault(label, label));
                    d.setContent(content);
                } else {
                    d.setDetailType("content_requirement");
                    d.setContent(detailCell);
                }
                d.setSort(detailSort++);
                current.details.add(d);
            }
        }
        if (current != null && StringUtils.isNotBlank(current.item.getName())) {
            result.practiceItems.add(current);
        }
    }

    private void parseExperimentArrangement(List<List<String>> grid, ParseResult result) {
        // 序号|实验项目名称|学时|分组情况|实验性质|修读性质|备注
        Map<String, ParsedPracticeItem> byName = new HashMap<>();
        for (ParsedPracticeItem p : result.practiceItems) {
            if (p.item != null && StringUtils.isNotBlank(p.item.getName())) {
                byName.put(p.item.getName().trim(), p);
            }
        }
        for (int r = 1; r < grid.size(); r++) {
            List<String> row = grid.get(r);
            String name = cell(row, 1);
            if (StringUtils.isBlank(name)) {
                continue;
            }
            ParsedPracticeItem p = byName.get(name.trim());
            if (p == null) {
                p = new ParsedPracticeItem();
                p.item.setName(name.trim());
                p.item.setSort(result.practiceItems.size() + 1);
                result.practiceItems.add(p);
                byName.put(name.trim(), p);
            }
            p.item.setHours(parseDecimal(cell(row, 2)));
            p.item.setGroupInfo(cell(row, 3));
            p.item.setExperimentNature(cell(row, 4));
            p.item.setStudyNature(cell(row, 5));
        }
    }

    private void parseAssessments(List<List<String>> grid, ParseResult result) {
        boolean isOutcome = joinHeader(grid.get(0)).contains("成果");
        for (int r = 1; r < grid.size(); r++) {
            List<String> row = grid.get(r);
            String c0 = cell(row, 0);
            if (StringUtils.isBlank(c0) && isAllBlank(row)) {
                continue;
            }
            if ("计分规则".equals(c0) || "项目计分规则".equals(c0)) {
                result.scoreRule = cell(row, 1);
                continue;
            }
            TeachingPlanAssessment a = new TeachingPlanAssessment();
            if (isOutcome) {
                // 成果类型|成果形式|评价的知识和能力|权重|评价准则
                Integer ot = parseInt(c0);
                if (ot != null) {
                    a.setOutcomeType(ot);
                }
                a.setAssessmentItem(StringUtils.isNotBlank(cell(row, 1)) ? cell(row, 1) : c0);
                a.setAssessedContent(cell(row, 2));
                a.setWeight(parseDecimal(cell(row, 3)));
                a.setStandard(cell(row, 4));
                a.setAssessmentCategory(5);
            } else {
                a.setAssessmentItem(c0);
                a.setMethod(cell(row, 1));
                splitMechanism(cell(row, 2), a);
                a.setWeight(parseDecimal(cell(row, 3)));
                a.setStandard(cell(row, 4));
            }
            if (StringUtils.isBlank(a.getAssessmentItem()) && a.getOutcomeType() == null) {
                continue;
            }
            a.setSort(result.assessments.size() + 1);
            result.assessments.add(a);
        }
    }

    private void splitMechanism(String raw, TeachingPlanAssessment a) {
        if (StringUtils.isBlank(raw)) {
            return;
        }
        String[] parts = raw.split("\\s*/\\s*", 2);
        if (parts.length == 2) {
            a.setMechanism(parts[0].trim());
            a.setScoreSystem(parts[1].trim());
        } else {
            a.setMechanism(raw.trim());
        }
    }

    private void parseTextbooks(List<List<String>> grid, ParseResult result, ParseContext ctx) {
        for (int r = 1; r < grid.size(); r++) {
            List<String> row = grid.get(r);
            if (isAllBlank(row)) {
                continue;
            }
            TeachingPlanTextbook b = new TeachingPlanTextbook();
            b.setMaterialNature(reverseDict(cell(row, 0), "sys_textbook_nature", ctx));
            b.setName(cell(row, 1));
            b.setFirstAuthor(cell(row, 2));
            b.setEdition(cell(row, 3));
            b.setPublisher(cell(row, 4));
            b.setPublishTime(cell(row, 5));
            b.setIsbn(cell(row, 6));
            b.setPublishMethod(reverseDict(cell(row, 7), "sys_publication_method", ctx));
            if (StringUtils.isBlank(b.getName()) && StringUtils.isBlank(b.getMaterialNature())) {
                continue;
            }
            b.setSort(result.textbooks.size() + 1);
            result.textbooks.add(b);
        }
    }

    private void parseConditions(List<List<String>> grid, ParseResult result, ParseContext ctx) {
        for (int r = 1; r < grid.size(); r++) {
            List<String> row = grid.get(r);
            if (isAllBlank(row)) {
                continue;
            }
            TeachingPlanCondition c = new TeachingPlanCondition();
            c.setConditionType(reverseDict(cell(row, 0), "sys_condition_type", ctx));
            c.setRequirement(cell(row, 1));
            if (StringUtils.isBlank(c.getConditionType()) && StringUtils.isBlank(c.getRequirement())) {
                continue;
            }
            c.setSort(result.conditions.size() + 1);
            result.conditions.add(c);
        }
    }

    private void parseTaskBackgroundTable(List<List<String>> grid, ParseResult result) {
        // 表头 + 一行值
        if (grid.size() < 2) {
            return;
        }
        List<String> headers = grid.get(0);
        List<String> values = grid.get(1);
        for (int c = 0; c < headers.size() && c < values.size(); c++) {
            String h = headers.get(c);
            if (StringUtils.isBlank(h) || h.contains("毕业要求")) {
                continue;
            }
            String v = values.get(c);
            if (StringUtils.isBlank(v)) {
                continue;
            }
            TeachingPlanSection s = new TeachingPlanSection();
            s.setSectionTitle(h.trim());
            s.setSectionCode(mapSectionCode(h, result.sections.size() + 1));
            s.setContent(v.trim());
            s.setSort(result.sections.size() + 1);
            result.sections.add(s);
        }
    }

    private void parseLabelContentTable(List<List<String>> grid, ParseResult result) {
        for (List<String> row : grid) {
            String label = cell(row, 0);
            String value = cell(row, 1);
            if (StringUtils.isBlank(label)) {
                continue;
            }
            if (label.contains("支撑的课程目标") || label.contains("配套支撑课程")
                    || label.contains("涉及的知识体系") || label.contains("团队规模")) {
                continue;
            }
            if (StringUtils.isBlank(value)) {
                continue;
            }
            TeachingPlanSection s = new TeachingPlanSection();
            s.setSectionTitle(label.trim());
            s.setSectionCode(mapSectionCode(label, result.sections.size() + 1));
            s.setContent(value.trim());
            s.setSort(result.sections.size() + 1);
            result.sections.add(s);
        }
    }

    private void parseOrganizationSteps(List<List<String>> grid, ParseResult result) {
        // 找「实施步骤」表头行
        int start = 0;
        for (int r = 0; r < grid.size(); r++) {
            String c0 = cell(grid.get(r), 0);
            if ("实施步骤".equals(c0) || "项目步骤".equals(c0)) {
                start = r + 1;
                break;
            }
            if ("组织方式".equals(c0) || "团队组织与管理".equals(c0)) {
                continue;
            }
        }
        for (int r = start; r < grid.size(); r++) {
            List<String> row = grid.get(r);
            String title = cell(row, 0);
            String req = row.size() > 2 ? cell(row, 2) : cell(row, 1);
            if (StringUtils.isBlank(title) && StringUtils.isBlank(req)) {
                continue;
            }
            if ("实施步骤".equals(title) || "项目步骤".equals(title) || "组织方式".equals(title)) {
                continue;
            }
            TeachingPlanSection s = new TeachingPlanSection();
            s.setSectionTitle(StringUtils.defaultIfBlank(title, "步骤" + (result.sections.size() + 1)));
            s.setSectionCode("step_" + (result.sections.size() + 1));
            s.setContent(req);
            s.setSort(result.sections.size() + 1);
            result.sections.add(s);
        }
    }

    // ============================ detect type ============================

    private int detectDocType(XWPFDocument doc, List<TeachingPlanImportIssueVo> issues, Integer expected) {
        String title = firstNonBlankParagraph(doc);
        int detected = DOC_TYPE_COURSE;
        if (title != null) {
            if (title.contains("实验课程教学计划")) {
                detected = DOC_TYPE_EXPERIMENT_COURSE;
            } else if (title.contains("实践训练课目")) {
                detected = DOC_TYPE_PRACTICE_SUBJECT;
            } else if (title.contains("实践项目教学计划")) {
                detected = DOC_TYPE_PRACTICE_PROJECT;
            } else if (title.contains("课程教学计划")) {
                detected = DOC_TYPE_COURSE;
            }
        }
        if (expected != null && !expected.equals(detected)) {
            issues.add(TeachingPlanImportIssueVo.warn("文档识别", title, "docType",
                    "Word 识别类型=" + detected + " 与课程 type 映射=" + expected + " 不一致，以 Word 为准"));
        }
        return detected;
    }

    private String firstNonBlankParagraph(XWPFDocument doc) {
        for (XWPFParagraph p : doc.getParagraphs()) {
            String t = norm(p.getText());
            if (StringUtils.isNotBlank(t)) {
                return t;
            }
        }
        return null;
    }

    // ============================ table helpers ============================

    private List<List<String>> tableToGrid(XWPFTable table) {
        List<List<String>> grid = new ArrayList<>();
        List<String> lastRow = null;
        for (XWPFTableRow row : table.getRows()) {
            List<String> cells = new ArrayList<>();
            List<XWPFTableCell> tcs = row.getTableCells();
            for (int c = 0; c < tcs.size(); c++) {
                XWPFTableCell cell = tcs.get(c);
                String text = norm(cell.getText());
                // 纵向合并：空且 vMerge continue → 取上行
                if (StringUtils.isBlank(text) && lastRow != null && c < lastRow.size() && isVMergeContinue(cell)) {
                    text = lastRow.get(c);
                } else if (StringUtils.isBlank(text) && lastRow != null && c < lastRow.size()) {
                    // POI 有时不暴露 vMerge，空单元格也继承（目标类型列常见）
                    // 仅当前列在表头不是「内容类」时由调用方逻辑处理；此处先原样空
                }
                cells.add(text);
            }
            grid.add(cells);
            lastRow = cells;
        }
        return grid;
    }

    private boolean isVMergeContinue(XWPFTableCell cell) {
        try {
            CTTcPr pr = cell.getCTTc().getTcPr();
            if (pr != null && pr.isSetVMerge()) {
                return pr.getVMerge().getVal() == null || pr.getVMerge().getVal() == STMerge.CONTINUE;
            }
        } catch (Exception ignored) {
            // ignore
        }
        return false;
    }

    private String joinHeader(List<String> row) {
        StringBuilder sb = new StringBuilder();
        for (String c : row) {
            sb.append(c == null ? "" : c);
        }
        return sb.toString();
    }

    private boolean headerContains(List<List<String>> grid, String key) {
        if (grid.isEmpty()) {
            return false;
        }
        return joinHeader(grid.get(0)).contains(key) || cellEquals(grid, 0, 0, key);
    }

    private boolean cellEquals(List<List<String>> grid, int r, int c, String expect) {
        if (r >= grid.size()) {
            return false;
        }
        return expect.equals(cell(grid.get(r), c));
    }

    private String cell(List<String> row, int idx) {
        if (row == null || idx < 0 || idx >= row.size()) {
            return "";
        }
        String v = row.get(idx);
        return v == null ? "" : v.trim();
    }

    private boolean isAllBlank(List<String> row) {
        if (row == null) {
            return true;
        }
        for (String c : row) {
            if (StringUtils.isNotBlank(c)) {
                return false;
            }
        }
        return true;
    }

    // ============================ text / heading ============================

    private boolean isH1(String text, XWPFParagraph p) {
        if (H1.matcher(text).matches()) {
            return true;
        }
        String style = p.getStyle();
        return style != null && (style.contains("Heading1") || style.contains("1"))
                && text.length() < 40 && text.contains("、");
    }

    private boolean isH2(String text, XWPFParagraph p) {
        if (H2_PAREN.matcher(text).matches()) {
            return true;
        }
        String style = p.getStyle();
        return style != null && (style.contains("Heading2") || "2".equals(style));
    }

    /**
     * 第四节下、非 H1 的短段落视作培养方案小标题（导出用 h2 写 schemeName（version））。
     */
    private boolean isSchemeSubtitle(String section, String text, XWPFParagraph p) {
        if (section == null || !section.contains("目标与支撑毕业要求")) {
            return false;
        }
        if (isH1(text, p) || H2_PAREN.matcher(text).matches()) {
            return false;
        }
        return text.length() > 0 && text.length() <= 80 && !text.startsWith("说明");
    }

    private boolean isH3(String text, XWPFParagraph p) {
        String style = p.getStyle();
        return style != null && (style.contains("Heading3") || "3".equals(style));
    }

    private boolean looksLikeSectionTitle(String text) {
        return text.length() <= 30 && !text.endsWith("。") && !text.endsWith("；");
    }

    private String norm(String s) {
        if (s == null) {
            return "";
        }
        return s.replace(' ', ' ').replaceAll("[\\r\\n]+", "\n").trim();
    }

    private String mapSectionCode(String title, int fallbackIdx) {
        if (StringUtils.isBlank(title)) {
            return "section_" + fallbackIdx;
        }
        String t = title.trim();
        for (Map.Entry<String, String> e : SECTION_TITLE_TO_CODE.entrySet()) {
            if (t.contains(e.getKey())) {
                return e.getValue();
            }
        }
        return "section_" + fallbackIdx;
    }

    private int indexOfColon(String s) {
        int i = s.indexOf('：');
        if (i < 0) {
            i = s.indexOf(':');
        }
        return i;
    }

    private BigDecimal parseDecimal(String s) {
        if (StringUtils.isBlank(s)) {
            return null;
        }
        try {
            String n = s.replaceAll("[^0-9.\\-]", "");
            if (StringUtils.isBlank(n)) {
                return null;
            }
            return new BigDecimal(n);
        } catch (Exception e) {
            return null;
        }
    }

    private Integer parseInt(String s) {
        if (StringUtils.isBlank(s)) {
            return null;
        }
        try {
            Matcher m = Pattern.compile("(\\d+)").matcher(s);
            if (m.find()) {
                return Integer.parseInt(m.group(1));
            }
        } catch (Exception ignored) {
            // ignore
        }
        return null;
    }

    private String reverseDict(String label, String dictType, ParseContext ctx) {
        if (StringUtils.isBlank(label) || ctx == null || ctx.dictLabelToValue == null) {
            return label;
        }
        String key = dictType + "#" + label.trim();
        String v = ctx.dictLabelToValue.get(key);
        return StringUtils.isNotBlank(v) ? v : label;
    }

    private String reverseDictMulti(String labels, String dictType, ParseContext ctx,
                                    ParseResult result, String section) {
        if (StringUtils.isBlank(labels)) {
            return labels;
        }
        String[] parts = labels.split("[、,，]");
        List<String> out = new ArrayList<>();
        for (String p : parts) {
            if (StringUtils.isBlank(p)) {
                continue;
            }
            String code = reverseDict(p.trim(), dictType, ctx);
            if (code.equals(p.trim()) && ctx.dictLabelToValue != null
                    && !ctx.dictLabelToValue.containsKey(dictType + "#" + p.trim())) {
                result.issues.add(TeachingPlanImportIssueVo.warn(section, p.trim(), dictType,
                        "字典未匹配，保留原文: " + p.trim()));
            }
            out.add(code);
        }
        return String.join("、", out);
    }

    /**
     * 供 Service 预填：把字典 list 转成 label→value 键 {@code type#label}。
     */
    public static void putDict(Map<String, String> map, String type, String label, String value) {
        if (map == null || StringUtils.isBlank(type) || StringUtils.isBlank(label)) {
            return;
        }
        map.putIfAbsent(type + "#" + label.trim(), value);
    }
}
