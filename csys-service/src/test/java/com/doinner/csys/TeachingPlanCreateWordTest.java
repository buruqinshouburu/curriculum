package com.doinner.csys;

import com.doinner.common.core.domain.DataSet;
import com.doinner.common.core.domain.DataTable;
import com.doinner.common.core.domain.Message;
import com.doinner.common.security.utils.DictUtils;
import com.doinner.csys.domain.TeachingPlanContent;
import com.doinner.csys.domain.TeachingPlanObjective;
import com.doinner.csys.domain.TeachingPlanProcessStep;
import com.doinner.csys.domain.TeachingPlanTaskBackground;
import com.doinner.csys.domain.vo.TeachingPlanObjectiveBatchSaveVo;
import com.doinner.csys.domain.vo.TeachingPlanObjectiveSaveVo;
import com.doinner.csys.domain.vo.TeachingPlanSupportCandidateTreeNodeVo;
import com.doinner.csys.entity.csys.TeachingPlanWordImporter;
import com.doinner.csys.service.TeachingPlanModuleService;
import com.doinner.csys.utils.CurDictUtils;
import com.doinner.file.api.domain.FileInfo;
import com.doinner.file.api.domain.vo.FileInfoVO;
import com.doinner.file.api.service.RemoteFileInfoService;
import com.doinner.kg.service.RemoteKgService;
import com.doinner.system.domain.custom.CustomDept;
import com.doinner.system.domain.entity.SysDept;
import com.doinner.system.domain.entity.SysDictData;
import com.doinner.system.service.DoinnerDeptService;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.sql.DataSource;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static com.jayway.jsonpath.JsonPath.read;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 四类教学计划接口 SpringBootTest（对本地测试库 doinner-curriculum-test-3.2 跑真实数据）。
 *
 * <p>plan_type 编号(前后端统一)：1课程(7003) 2实验课程(8003) 3实践训练课目(8002) 4实践项目(8004)；
 * 与课程库 type 编号 2/3 对调（8002 课程 type=2 → plan_type=3，8003 课程 type=3 → plan_type=2）。
 *
 * <p>重点覆盖今天的两处改动：
 * <ol>
 *   <li>实践训练课目(plan_type=3)「四、训练内容与时间安排」模块字段字典化：
 *       DB 存字典编码(1/2/3) → Word 生成译为名称 → 导入反向 label→编码。</li>
 *   <li>type4 实践项目「二、支撑的课程目标/训练目的 / 知识体系/训练内容」支撑绑定。</li>
 * </ol>
 *
 * <p>外部依赖打桩：远程文件服务(RemoteFileInfoService)、知识库字典(RemoteKgService)、
 * 部门(DoinnerDeptService)；字典缓存通过 {@link DictUtils#setDictCache} 预热进本地 Redis
 * （与生产同源：RedisService.setCacheObject/getCacheObject 均为 fastjson2 序列化往返）。
 *
 * <p>测试方法以 {@code tN_} 命名按序执行；类上 @Transactional 保证每次方法 DB 写入回滚，可重复执行。
 */
@SpringBootTest(
        classes = DoinnerCurriculumSystemApplication.class,
        properties = {
                "spring.datasource.dynamic.datasource.master.url=jdbc:mysql://127.0.0.1:3306/doinner-curriculum-test-3.2?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai&useSSL=false&allowPublicKeyRetrieval=true",
                "spring.datasource.dynamic.datasource.master.username=root",
                "spring.datasource.dynamic.datasource.master.password=123456",
                // bootstrap.yml 配了 context-path=/csys；测试里清空，MockMvc 直接用 /teachingPlan/...
                "server.servlet.context-path="
        })
@AutoConfigureMockMvc
@Transactional
@Rollback
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class TeachingPlanCreateWordTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private DataSource dataSource;

    @Autowired
    private TeachingPlanModuleService teachingPlanModuleService;

    /** 远程文件服务（Feign）：upload 时捕获生成的 Word，供 docx 断言与导入复用 */
    @MockBean
    private RemoteFileInfoService remoteFileInfoService;

    /** 知识库字典（课程模块）——空列表即可，未命中按原文展示 */
    @MockBean
    private RemoteKgService remoteKgService;

    /** 部门名称翻译——空列表即可，Word 中部门显示原文 id，不参与断言 */
    @MockBean
    private DoinnerDeptService doinnerDeptService;

    /** 最近一次 upload 捕获的 MultipartFile（生成的 Word） */
    private final AtomicReference<MultipartFile> lastUploaded = new AtomicReference<>();

    @BeforeEach
    void setUp() {
        warmDictCache();
        stubFeign();
        lastUploaded.set(null);
    }

    // ==================== 1. 字典缓存 ====================

    /** 预热 Redis 字典缓存：从测试库 sys_dict_data 全量读入，逐个 setDictCache（同生产同源）。 */
    private void warmDictCache() {
        JdbcTemplate jdbc = new JdbcTemplate(dataSource);
        List<Map<String, Object>> rows = jdbc.queryForList(
                "SELECT dict_type, dict_value, dict_label FROM sys_dict_data WHERE status = '0'");
        Map<String, List<SysDictData>> grouped = new HashMap<>();
        for (Map<String, Object> row : rows) {
            Object type = row.get("dict_type");
            if (type == null) {
                continue;
            }
            SysDictData d = new SysDictData();
            d.setDictType(type.toString());
            d.setDictValue(String.valueOf(row.get("dict_value")));
            d.setDictLabel(row.get("dict_label") == null ? "" : row.get("dict_label").toString());
            grouped.computeIfAbsent(type.toString(), k -> new ArrayList<>()).add(d);
        }
        for (Map.Entry<String, List<SysDictData>> e : grouped.entrySet()) {
            DictUtils.setDictCache(e.getKey(), e.getValue());
        }
    }

    // ==================== 2. Feign 打桩 ====================

    private void stubFeign() {
        // upload：捕获 MultipartFile（生成文档），返回伪 FileInfo
        when(remoteFileInfoService.upload(any(MultipartFile.class), anyString())).thenAnswer(invocation -> {
            lastUploaded.set(invocation.getArgument(0));
            return DataSet.success(makeFileInfo());
        });
        when(remoteFileInfoService.list(any(FileInfoVO.class)))
                .thenReturn(DataTable.success(Collections.singletonList(makeFileInfo())));
        when(remoteFileInfoService.getFileInfo(anyString())).thenReturn(DataSet.success(makeFileInfo()));
        when(remoteFileInfoService.delete(anyString())).thenReturn(Message.success());
        // 课程模块字典 / 部门列表：返回空，生成侧不依赖具体值
        when(remoteKgService.findDictionaryByType(anyString()))
                .thenReturn(DataSet.success(Collections.emptyList()));
        when(doinnerDeptService.list(any(CustomDept.class)))
                .thenReturn(DataSet.success(Collections.emptyList()));
    }

    private FileInfo makeFileInfo() {
        FileInfo info = new FileInfo();
        info.setId(new ObjectId());
        info.setFileId("mock-file-id-001");
        info.setFileName("mock.docx");
        info.setDownloadUrl("http://mock-file/download/001");
        info.setPreviewUrl("http://mock-file/preview/001");
        return info;
    }

    // ==================== 工具方法 ====================

    /** 调 createWord 接口，返回捕获到的 Word 字节。 */
    private byte[] generateDocx(Long courseId) throws Exception {
        lastUploaded.set(null);
        mockMvc.perform(get("/teachingPlan/createWord/" + courseId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
        MultipartFile mf = lastUploaded.get();
        assertNotNull(mf, "remoteFileInfoService.upload 应被调用，捕获到生成的 Word");
        return mf.getBytes();
    }

    /** 抽取 docx 全部段落 + 表格文本。 */
    private String extractText(byte[] bytes) throws Exception {
        try (XWPFDocument doc = new XWPFDocument(new ByteArrayInputStream(bytes))) {
            StringBuilder sb = new StringBuilder();
            for (XWPFParagraph p : doc.getParagraphs()) {
                sb.append(p.getText()).append('\n');
            }
            for (XWPFTable t : doc.getTables()) {
                for (XWPFTableRow r : t.getRows()) {
                    for (XWPFTableCell c : r.getTableCells()) {
                        sb.append(c.getText()).append('\n');
                    }
                }
            }
            return sb.toString();
        }
    }

    // ==================== 用例 ====================

    /** t1 字典缓存预热成功：模块字典 value→label 映射就绪。 */
    @Test
    @Order(1)
    void t1_dictCacheWarmed() {
        List<SysDictData> list = CurDictUtils.getDictData("sys_plan_training_module");
        assertNotNull(list, "模块字典缓存应已预热");
        assertFalse(list.isEmpty(), "sys_plan_training_module 字典应有 3 条");
        Map<String, String> v2l = new HashMap<>();
        for (SysDictData d : list) {
            v2l.put(d.getDictValue(), d.getDictLabel());
        }
        assertEquals("战斗体技能提升模块", v2l.get("1"));
        assertEquals("指挥素养培塑模块", v2l.get("2"));
        assertEquals("新质新域能力拓展模块", v2l.get("3"));
    }

    /** t2 type1 课程(7003 程序设计基础) 教学计划 Word 生成。 */
    @Test
    @Order(2)
    void t2_createWord_type1_course() throws Exception {
        byte[] docx = generateDocx(7003L);
        String text = extractText(docx);
        assertTrue(text.contains("程序设计基础"), "docx 应含课程名");
        assertTrue(text.contains("掌握结构化程序设计的基本方法与三种基本结构"), "docx 应含课程目标内容");
        assertTrue(text.contains("第一章 程序设计基础"), "docx 应含教学内容章节");
        assertTrue(text.contains("五、课程教学内容与时间安排"), "docx 应含第五节标题");
        assertEquals("程序设计基础教学计划.docx", lastUploaded.get().getOriginalFilename());
    }

    /** t3 实践训练课目(plan_type=3, 8002 军事基础训练)：模块字典编码→名称翻译进 Word。 */
    @Test
    @Order(3)
    void t3_createWord_practiceSubject_moduleLabels() throws Exception {
        byte[] docx = generateDocx(8002L);
        String text = extractText(docx);
        assertTrue(text.contains("战斗体技能提升模块"), "模块字典值1应译为名称");
        assertTrue(text.contains("指挥素养培塑模块"), "模块字典值2应译为名称");
        assertTrue(text.contains("新质新域能力拓展模块"), "模块字典值3应译为名称");
        try (XWPFDocument generated = new XWPFDocument(new ByteArrayInputStream(docx))) {
            XWPFTable organization = findTable(generated, "组织方式", "实施步骤", "阶段划分", "有关要求");
            assertEquals("组织方式", organization.getRow(0).getCell(0).getText().trim());
            assertEquals("实施步骤", organization.getRow(1).getCell(0).getText().trim());
            assertEquals("阶段划分", organization.getRow(1).getCell(1).getText().trim());
            assertEquals("有关要求", organization.getRow(1).getCell(2).getText().trim());
            assertEquals("战斗准备", organization.getRow(2).getCell(0).getText().trim());
            assertEquals("战备等级转进", organization.getRow(2).getCell(1).getText().trim());
            assertEquals(1114, Integer.parseInt(String.valueOf(
                    organization.getCTTbl().getTblGrid().getGridColArray(0).getW())));
            assertEquals(2003, Integer.parseInt(String.valueOf(
                    organization.getCTTbl().getTblGrid().getGridColArray(1).getW())));
            assertEquals(5405, Integer.parseInt(String.valueOf(
                    organization.getCTTbl().getTblGrid().getGridColArray(2).getW())));
        }
    }

    /** t4 实验课程(plan_type=2, 8003 大学物理实验) 教学计划 Word 生成。 */
    @Test
    @Order(4)
    void t4_createWord_experimentCourse() throws Exception {
        byte[] docx = generateDocx(8003L);
        String text = extractText(docx);
        assertTrue(text.contains("大学物理实验"), "docx 应含课程名");
        assertTrue(text.contains("围绕力学、电磁学核心原理开展实验验证，强化理论与实验结合"), "docx 应含任务背景");
        assertTrue(text.contains("牛顿第二定律验证实验"), "docx 应含实验项目");
    }

    /** t5 type4 实践项目(8004 综合课程设计实践)：支撑课程目标/训练目的/知识体系/训练内容快照进 Word。 */
    @Test
    @Order(5)
    void t5_createWord_type4_supportBinding() throws Exception {
        byte[] docx = generateDocx(8004L);
        String text = extractText(docx);
        assertTrue(text.contains("掌握结构化程序设计的基本方法与三种基本结构"), "支撑课程目标快照应入 Word");
        assertTrue(text.contains("掌握单个军人队列动作、班队列组织等基本军事素养"), "支撑训练目的快照应入 Word");
        assertTrue(text.contains("第一章 程序设计基础"), "知识体系快照应入 Word");
        assertTrue(text.contains("战斗体技能提升模块"), "训练内容快照应入 Word（实践训练课目内容快照存模块名称）");
    }

    /** t6 实践训练课目内容行 DB 存模块字典编码：content/list 返回 1/2/3（非名称）。 */
    @Test
    @Order(6)
    void t6_practiceSubjectContentStoredAsCodes() throws Exception {
        mockMvc.perform(get("/teachingPlan/content/list").param("planId", "6002"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(3))
                .andExpect(jsonPath("$.data[0].title").value("1"))
                .andExpect(jsonPath("$.data[1].title").value("2"))
                .andExpect(jsonPath("$.data[2].title").value("3"));
    }

    /** t7 type4 支撑绑定候选树：type=1/2 分别返回页面两棵三层树。 */
    @Test
    @Order(7)
    void t7_supportCandidates_type4() throws Exception {
        mockMvc.perform(get("/teachingPlan/support/candidateTree")
                        .param("courseId", "8004")
                        .param("type", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(2))
                .andExpect(jsonPath("$.data[0].nodeType").value("course"))
                .andExpect(jsonPath("$.data[0].children[0].name").value("计算机科学与技术2026级培养方案"))
                .andExpect(jsonPath("$.data[0].children[0].children[0].nodeType").value("objective"))
                .andExpect(jsonPath("$.data[1].nodeType").value("trainingSubject"))
                .andExpect(jsonPath("$.data[1].children[0].name").value("通识通用"))
                .andExpect(jsonPath("$.data[1].children[0].children[0].nodeType").value("purpose"));

        mockMvc.perform(get("/teachingPlan/support/candidateTree")
                        .param("courseId", "8004")
                        .param("type", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(2))
                .andExpect(jsonPath("$.data[0].children[0].children[0].nodeType").value("knowledgeSystem"))
                .andExpect(jsonPath("$.data[1].children[0].children[0].nodeType").value("trainingContent"));

    }

    /** t8 type4 第二部分统一大保存+统一回显，旧拆分 HTTP 接口均已下线。 */
    @Test
    @Order(8)
    void t8_supportSaveList_type4() throws Exception {
        mockMvc.perform(post("/teachingPlan/practiceProject/background/save")
                        .contentType("application/json")
                        .content("{\"planId\":6004,"
                                + "\"complexProblem\":\"<p>统一保存复杂问题</p>\","
                                + "\"mainTask\":\"<p>统一保存主要任务</p>\","
                                + "\"objectiveIds\":[60011],\"purposeIds\":[62111],"
                                + "\"contentIds\":[65011,62211]}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        mockMvc.perform(get("/teachingPlan/practiceProject/background/detail").param("planId", "6004"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.complexProblem").value("<p>统一保存复杂问题</p>"))
                .andExpect(jsonPath("$.data.mainTask").value("<p>统一保存主要任务</p>"))
                .andExpect(jsonPath("$.data.objectiveIds[0]").value(60011))
                .andExpect(jsonPath("$.data.purposeIds[0]").value(62111))
                .andExpect(jsonPath("$.data.contentIds.length()").value(2))
                .andExpect(jsonPath("$.data.supportObjectives.length()").value(2))
                .andExpect(jsonPath("$.data.supportObjectives[0].itemName")
                        .value("掌握结构化程序设计的基本方法与三种基本结构"))
                .andExpect(jsonPath("$.data.supportContents.length()").value(2))
                .andExpect(jsonPath("$.data.supportContents[1].itemTitle").value("战斗体技能提升模块"));

        mockMvc.perform(get("/teachingPlan/support/candidates").param("courseId", "8004"))
                .andExpect(status().isNotFound());
        mockMvc.perform(get("/teachingPlan/supportObjective/list").param("planId", "6004"))
                .andExpect(status().isNotFound());
        mockMvc.perform(post("/teachingPlan/supportObjective/save").contentType("application/json").content("{}"))
                .andExpect(status().isNotFound());
        mockMvc.perform(get("/teachingPlan/supportContent/list").param("planId", "6004"))
                .andExpect(status().isNotFound());
        mockMvc.perform(post("/teachingPlan/supportContent/save").contentType("application/json").content("{}"))
                .andExpect(status().isNotFound());
    }

    /** t9 实践训练课目字典字段导入反向映射，并保证组织实施固定表头不作为数据导入。 */
    @Test
    @Order(9)
    void t9_importReverseMap_practiceSubject() throws Exception {
        byte[] docx = generateDocx(8002L);
        TeachingPlanWordImporter importer = new TeachingPlanWordImporter();
        TeachingPlanWordImporter.ParseContext ctx = new TeachingPlanWordImporter.ParseContext();
        // 与 Service.buildImportParseContext 同源预填：模块、实施步骤字典 label→value
        for (String dictType : Arrays.asList("sys_plan_training_module", "sys_plan_implementation_step")) {
            List<SysDictData> dictData = CurDictUtils.getDictData(dictType);
            for (SysDictData d : dictData) {
                if (d == null || d.getDictLabel() == null || d.getDictValue() == null) {
                    continue;
                }
                TeachingPlanWordImporter.putDict(ctx.dictLabelToValue,
                        dictType, d.getDictLabel(), d.getDictValue());
            }
        }
        TeachingPlanWordImporter.ParseResult result =
                importer.parse(new ByteArrayInputStream(docx), ctx);
        List<String> titles = new ArrayList<>();
        for (TeachingPlanContent c : result.contents) {
            if (c != null) {
                titles.add(c.getTitle());
            }
        }
        assertTrue(titles.containsAll(java.util.Arrays.asList("1", "2", "3")),
                "导出 docx 的模块名称应反查回编码 1/2/3，实际=" + titles);
        assertEquals(11, result.processSteps.size(), "组织实施数据行应全部导入，固定表头不能算数据");
        TeachingPlanProcessStep firstStep = result.processSteps.get(0);
        assertEquals("1", firstStep.getStageName(), "stageName 应为实施步骤类别编码");
        assertEquals("战备等级转进", firstStep.getStepName(), "stepName 应为阶段划分内容");
        assertTrue(result.processSteps.stream().noneMatch(s -> "实施步骤".equals(s.getStageName())
                        || "阶段划分".equals(s.getStepName())),
                "固定表头不得作为业务数据导入");
    }

    /** t10 实践训练课目(plan_type=3)覆盖导入接口（导出的 docx 再导入，模块名称反查编码落库）。 */
    @Test
    @Order(10)
    void t10_importWordEndpoint_practiceSubject() throws Exception {
        byte[] docx = generateDocx(8002L);
        MockMultipartFile file = new MockMultipartFile("file", "军事基础训练教学计划.docx",
                "application/vnd.openxmlformats-officedocument.wordprocessingml.document", docx);
        mockMvc.perform(multipart("/teachingPlan/importWord")
                        .file(file)
                        .param("courseId", "8002")
                        .param("planType", "3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
        // 导入后内容行模块仍存编码（label→code 反向映射成功）
        mockMvc.perform(get("/teachingPlan/content/list").param("planId", "6002"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].title").value("1"))
                .andExpect(jsonPath("$.data[1].title").value("2"))
                .andExpect(jsonPath("$.data[2].title").value("3"));
    }

    // ==================== 补充：16 个写接口全覆盖 ====================
    // 种子关键 id：计划 6001(7003课程)/6002(8002课目)/6003(8003实验课程)/6004(8004项目)
    //   任务背景 6003 种子1行(scheme 7601)；训练目的 6002 两行(62111/62112)；训练目的-毕业要求 62111→90101(GR1)/62112→90103(GR3)
    //   内容 6002 三行(62211/62212/62213)；内容-训练目的 62211→62111
    //   计划 6003 为 8003(实验课程)，非公共基础（createWord t4 能渲染 scheme=7601 的任务背景证明 list 不按 onlyNull 过滤）
    //   计划 6002 为 8002(实践训练课目) 通识通用模块，trainingPurpose 的 schemeId 被强制置 null

    /** POST JSON 断言 code=200，返回响应体（供提取 id 等）。 */
    private String postJson(String path, String body) throws Exception {
        return mockMvc.perform(post(path).contentType("application/json").content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andReturn().getResponse().getContentAsString();
    }

    /** PUT JSON 断言 code=200。 */
    private String putJson(String path, String body) throws Exception {
        return mockMvc.perform(put(path).contentType("application/json").content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andReturn().getResponse().getContentAsString();
    }

    private long idOf(String responseJson) {
        return ((Number) read(responseJson, "$.data")).longValue();
    }

    /** t11 任务背景 CRUD（计划6003 实验课程）：list→add→update→delete 全链路。 */
    @Test
    @Order(11)
    void t11_taskBackgroundCRUD() throws Exception {
        mockMvc.perform(get("/teachingPlan/taskBackground/list").param("planId", "6003").param("schemeId", "7601"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(1))
                .andExpect(jsonPath("$.data[0].backgroundDesc")
                        .value("围绕力学、电磁学核心原理开展实验验证，强化理论与实验结合"))
                .andExpect(jsonPath("$.data[0].technicalGoal").value("掌握常用实验仪器的操作与测量方法"))
                .andExpect(jsonPath("$.data[0].abilityGoal").value("培养实验数据分析与科学表达能力"));
        long newId = idOf(postJson("/teachingPlan/taskBackground",
                "{\"planId\":6003,\"schemeId\":7601,\"backgroundDesc\":\"新增任务背景\","
                        + "\"technicalGoal\":\"技术目标A\",\"abilityGoal\":\"能力目标A\"}"));
        mockMvc.perform(get("/teachingPlan/taskBackground/list").param("planId", "6003").param("schemeId", "7601"))
                .andExpect(jsonPath("$.data.length()").value(2));
        putJson("/teachingPlan/taskBackground",
                "{\"id\":" + newId + ",\"planId\":6003,\"schemeId\":7601,\"backgroundDesc\":\"修改后任务背景\","
                        + "\"technicalGoal\":\"技术目标B\",\"abilityGoal\":\"能力目标B\"}");
        mockMvc.perform(get("/teachingPlan/taskBackground/list").param("planId", "6003").param("schemeId", "7601"))
                .andExpect(jsonPath("$.data[?(@.id==" + newId + ")].backgroundDesc").value("修改后任务背景"));
        mockMvc.perform(delete("/teachingPlan/taskBackground/" + newId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
        mockMvc.perform(get("/teachingPlan/taskBackground/list").param("planId", "6003").param("schemeId", "7601"))
                .andExpect(jsonPath("$.data.length()").value(1));
    }

    /** t12 任务背景-毕业要求 引用：list→save(整表重建)→清空。 */
    @Test
    @Order(12)
    void t12_taskBackgroundRef() throws Exception {
        String listJson = mockMvc.perform(get("/teachingPlan/taskBackground/list").param("planId", "6003").param("schemeId", "7601"))
                .andReturn().getResponse().getContentAsString();
        long tbId = ((Number) read(listJson, "$.data[0].id")).longValue();
        // 种子任务背景已绑 90102(GR2)
        mockMvc.perform(get("/teachingPlan/taskBackgroundRef/list").param("taskBackgroundId", String.valueOf(tbId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(1))
                .andExpect(jsonPath("$.data[0].graduationId").value(90102))
                .andExpect(jsonPath("$.data[0].graduationCode").value("GR2"));
        // 整表重建：绑 90102(GR2)
        postJson("/teachingPlan/taskBackgroundRef/save",
                "{\"taskBackgroundId\":" + tbId + ",\"graduationIds\":[90102]}");
        mockMvc.perform(get("/teachingPlan/taskBackgroundRef/list").param("taskBackgroundId", String.valueOf(tbId)))
                .andExpect(jsonPath("$.data.length()").value(1))
                .andExpect(jsonPath("$.data[0].graduationId").value(90102))
                .andExpect(jsonPath("$.data[0].graduationCode").value("GR2"));
        // 空 refs = 清空
        postJson("/teachingPlan/taskBackgroundRef/save",
                "{\"taskBackgroundId\":" + tbId + ",\"graduationIds\":[]}");
        mockMvc.perform(get("/teachingPlan/taskBackgroundRef/list").param("taskBackgroundId", String.valueOf(tbId)))
                .andExpect(jsonPath("$.data.length()").value(0));
    }

    /** t13 训练目的 CRUD（计划6002 通识通用，schemeId 强制 null）。 */
    @Test
    @Order(13)
    void t13_trainingPurposeCRUD() throws Exception {
        mockMvc.perform(get("/teachingPlan/trainingPurpose/list").param("planId", "6002"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(4))
                .andExpect(jsonPath("$.data[0].purpose")
                        .value("掌握单个军人队列动作、班队列组织等基本军事素养"))
                .andExpect(jsonPath("$.data[0].graduationRequirements")
                        .value("掌握计算机科学与技术的基础理论与专业知识"));
        long newId = idOf(postJson("/teachingPlan/trainingPurpose",
                "{\"planId\":6002,\"purpose\":\"新增训练目的：应急处突训练\"}"));
        mockMvc.perform(get("/teachingPlan/trainingPurpose/list").param("planId", "6002"))
                .andExpect(jsonPath("$.data.length()").value(5));
        putJson("/teachingPlan/trainingPurpose",
                "{\"id\":" + newId + ",\"planId\":6002,\"purpose\":\"修改后的训练目的\"}");
        mockMvc.perform(get("/teachingPlan/trainingPurpose/list").param("planId", "6002"))
                .andExpect(jsonPath("$.data[?(@.id==" + newId + ")].purpose").value("修改后的训练目的"));
        mockMvc.perform(delete("/teachingPlan/trainingPurpose/" + newId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
        mockMvc.perform(get("/teachingPlan/trainingPurpose/list").param("planId", "6002"))
                .andExpect(jsonPath("$.data.length()").value(4));
    }

    /** t14 训练目的-毕业要求 引用：list→save 增删。 */
    @Test
    @Order(14)
    void t14_trainingPurposeRef() throws Exception {
        // 种子：62111 绑 90101(GR1)
        mockMvc.perform(get("/teachingPlan/trainingPurposeRef/list").param("purposeId", "62111"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(1))
                .andExpect(jsonPath("$.data[0].graduationCode").value("GR1"));
        postJson("/teachingPlan/trainingPurposeRef/save",
                "{\"purposeId\":62111,\"planId\":6002,\"refs\":[{\"graduationId\":90101},{\"graduationId\":90103}]}");
        mockMvc.perform(get("/teachingPlan/trainingPurposeRef/list").param("purposeId", "62111"))
                .andExpect(jsonPath("$.data.length()").value(2));
    }

    /** t15 内容-训练目的 关联：list→save 整表重建。 */
    @Test
    @Order(15)
    void t15_contentPurpose() throws Exception {
        mockMvc.perform(get("/teachingPlan/contentPurpose/list").param("contentId", "62211"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(1))
                .andExpect(jsonPath("$.data[0].purposeId").value(62111));
        postJson("/teachingPlan/contentPurpose/save",
                "{\"contentId\":62211,\"planId\":6002,\"purposeIds\":[62111,62112]}");
        mockMvc.perform(get("/teachingPlan/contentPurpose/list").param("contentId", "62211"))
                .andExpect(jsonPath("$.data.length()").value(2));
    }

    /** t16 实践训练课目组织实施数据行 CRUD：stageName=实施步骤编码，stepName=阶段划分。 */
    @Test
    @Order(16)
    void t16_practiceSubjectProcessStepCRUD() throws Exception {
        mockMvc.perform(get("/teachingPlan/processStep/list").param("planId", "6002"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(11))
                .andExpect(jsonPath("$.data[0].stageName").value("1"))
                .andExpect(jsonPath("$.data[0].stepName").value("战备等级转进"));

        long newId = idOf(postJson("/teachingPlan/processStep",
                "{\"planId\":6002,\"stageName\":\"1\",\"stepName\":\"新增阶段划分\"," +
                        "\"requirement\":\"新增有关要求\",\"sort\":99}"));
        putJson("/teachingPlan/processStep",
                "{\"id\":" + newId + ",\"stageName\":\"2\",\"stepName\":\"修改后的阶段划分\"," +
                        "\"requirement\":\"修改后的有关要求\",\"sort\":99}");
        mockMvc.perform(get("/teachingPlan/processStep/list").param("planId", "6002"))
                .andExpect(jsonPath("$.data[?(@.id==" + newId + ")].stageName").value("2"))
                .andExpect(jsonPath("$.data[?(@.id==" + newId + ")].stepName").value("修改后的阶段划分"));
        mockMvc.perform(delete("/teachingPlan/processStep/" + newId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
        mockMvc.perform(get("/teachingPlan/processStep/list").param("planId", "6002"))
                .andExpect(jsonPath("$.data.length()").value(11));
    }

    /** t17 训练目的 batchSave（整表重建：删旧 2 行 → 插新 2 行）。 */
    @Test
    @Order(17)
    void t17_trainingPurposeBatchSave() throws Exception {
        postJson("/teachingPlan/trainingPurpose/batchSave",
                "{\"planId\":6002,\"purposes\":["
                        + "{\"purpose\":{\"purpose\":\"整表目的A\"},\"refs\":[{\"graduationId\":90101}]},"
                        + "{\"purpose\":{\"purpose\":\"整表目的B\"}}]}");
        mockMvc.perform(get("/teachingPlan/trainingPurpose/list").param("planId", "6002"))
                .andExpect(jsonPath("$.data.length()").value(2))
                .andExpect(jsonPath("$.data[0].purpose").value("整表目的A"))
                .andExpect(jsonPath("$.data[1].purpose").value("整表目的B"));
    }

    /** t18 四类文档落盘到 target/gen-docs-full（本轮加厚种子专用，避免覆盖 Word 中打开的 gen-docs 旧档）。 */
    @Test
    @Order(18)
    void t18_dumpFourDocsToDisk() throws Exception {
        File dir = new File("target/gen-docs-full-0812");
        if (!dir.exists() && !dir.mkdirs()) {
            throw new IllegalStateException("无法创建目录: " + dir.getAbsolutePath());
        }
        long[][] cases = {{7003L, 1}, {8003L, 2}, {8002L, 3}, {8004L, 4}};
        for (long[] c : cases) {
            byte[] docx = generateDocx(c[0]);
            String name = "type" + c[1] + "_" + c[0] + ".docx";
            File out = new File(dir, name);
            try (FileOutputStream fos = new FileOutputStream(out)) {
                fos.write(docx);
            }
            assertTrue(out.length() > 0, "应生成文档: " + name);
        }
    }

    /**
     * t19 导入往返：四类教学计划导出的 Word → importWord 原样导入。
     * 正式环境「导出的文档没有办法原样导入」，本用例把四类文档各自导入回其草稿计划，
     * 校验：无 ERROR 问题、各子模块计数与种子一致、评价标准反转为字典编码、
     * type1 学时安排/观测点补齐后往返不丢、type3 考核项目为字典编码。
     * 类上 @Transactional/@Rollback：导入写入随方法回滚，不影响后续用例与本地库。
     */
    @Test
    @Order(19)
    void t19_importRoundTripAllFourTypes() throws Exception {
        long[][] cases = {{7003L, 1}, {8003L, 2}, {8002L, 3}, {8004L, 4}};
        for (long[] c : cases) {
            long courseId = c[0];
            int planType = (int) c[1];
            String tag = "type" + planType;
            byte[] docx = generateDocx(courseId);
            assertTrue(docx.length > 0, tag + " 生成文档非空");
            MockMultipartFile file = new MockMultipartFile("file",
                    "roundtrip_" + tag + ".docx",
                    "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
                    docx);
            String resp = mockMvc.perform(multipart("/teachingPlan/importWord")
                            .file(file)
                            .param("courseId", String.valueOf(courseId))
                            .param("planType", String.valueOf(planType)))
                    .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
            assertEquals(200, ((Number) read(resp, "$.code")).intValue(), tag + " 导入业务码应为200，响应: " + resp);
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> errors = read(resp, "$.data.issues[?(@.severity == 'ERROR')]");
            assertTrue(errors.isEmpty(), tag + " 导入不应有 ERROR 问题: " + resp);
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> warns = read(resp, "$.data.issues[?(@.severity == 'WARN')]");
            List<String> warnMsgs = warns.stream()
                    .map(m -> String.valueOf(m.get("message")))
                    .collect(Collectors.toList());
            JdbcTemplate jdbc = new JdbcTemplate(dataSource);
            switch (planType) {
                case 1:
                    assertEquals(8, countOf(resp, "objective"), tag + " 目标数应原样导入: " + warnMsgs);
                    assertEquals(15, countOf(resp, "objectiveRef"), tag + " 目标-毕业要求应原样导入: " + warnMsgs);
                    assertEquals(4, countOf(resp, "content"), tag + " 教学内容数应原样导入: " + warnMsgs);
                    assertEquals(9, countOf(resp, "targetDesign"), tag + " 目标达成设计应原样导入: " + warnMsgs);
                    assertEquals(6, countOf(resp, "assessment"), tag + " 考核项应原样导入: " + warnMsgs);
                    assertEquals(13, countOf(resp, "objectiveAssessment"), tag + " 目标达成考核关联应原样导入: " + warnMsgs);
                    // 评价标准应反转为字典编码（正式环境导入丢失/存标签的根因）
                    List<String> standards = jdbc.queryForList(
                            "SELECT standard FROM t_csys_teaching_plan_assessment WHERE plan_id=6001 AND standard IS NOT NULL AND sysflag = 0",
                            String.class);
                    assertTrue(!standards.isEmpty()
                                    && standards.stream().noneMatch(s -> s.contains("优秀")),
                            tag + " 评价标准应为字典编码(1,2,3,4,5)而非标签: " + standards);
                    // 学时安排/观测点：修复后导出含值，导入不应丢
                    Integer contentHours = jdbc.queryForObject(
                            "SELECT COUNT(*) FROM t_csys_teaching_plan_content WHERE plan_id=6001 AND hours IS NOT NULL AND sysflag = 0",
                            Integer.class);
                    assertEquals(4, contentHours, tag + " 教学内容学时(学时安排)应全部导入: " + warnMsgs);
                    Integer obsCount = jdbc.queryForObject(
                            "SELECT COUNT(*) FROM t_csys_teaching_plan_target_design WHERE plan_id=6001 AND observation_point IS NOT NULL AND observation_point <> '' AND sysflag = 0",
                            Integer.class);
                    assertEquals(5, obsCount, tag + " 能力/素质目标观测点应全部导入: " + warnMsgs);
                    break;
                case 2:
                    assertEquals(3, countOf(resp, "assessment"), tag + " 考核项应原样导入: " + warnMsgs);
                    assertEquals(2, countOf(resp, "textbook"), tag + " 实验教材应原样导入: " + warnMsgs);
                    break;
                case 3:
                    assertEquals(4, countOf(resp, "trainingPurpose"), tag + " 训练目的应原样导入: " + warnMsgs);
                    assertEquals(4, countOf(resp, "trainingPurposeRef"), tag + " 训练目的-毕业要求应原样导入: " + warnMsgs);
                    assertEquals(3, countOf(resp, "content"), tag + " 训练内容应原样导入: " + warnMsgs);
                    assertEquals(2, countOf(resp, "assessment"), tag + " 考核项应原样导入: " + warnMsgs);
                    assertEquals(11, countOf(resp, "processStep"), tag + " 组织实施数据行应原样导入: " + warnMsgs);
                    List<Map<String, Object>> processSteps = jdbc.queryForList(
                            "SELECT stage_name, step_name, requirement FROM t_csys_teaching_plan_process_step "
                                    + "WHERE plan_id=6002 AND sysflag=0 ORDER BY sort");
                    assertEquals("1", processSteps.get(0).get("stage_name"),
                            tag + " stage_name 应保存实施步骤类别编码");
                    assertEquals("战备等级转进", processSteps.get(0).get("step_name"),
                            tag + " step_name 应保存阶段划分");
                    assertTrue(processSteps.stream().noneMatch(s -> "实施步骤".equals(s.get("stage_name"))
                                    || "阶段划分".equals(s.get("step_name"))),
                            tag + " 固定表头不得入库");
                    List<String> items = jdbc.queryForList(
                            "SELECT assessment_item FROM t_csys_teaching_plan_assessment WHERE plan_id=6002 AND sysflag = 0 ORDER BY sort",
                            String.class);
                    assertEquals(Arrays.asList("5", "7"), items, tag + " 考核项目应为字典编码 5/7: " + items);
                    List<String> purposes = jdbc.queryForList(
                            "SELECT purpose FROM t_csys_teaching_plan_content WHERE plan_id=6002 AND sysflag = 0 ORDER BY sort",
                            String.class);
                    assertTrue(purposes.stream().anyMatch(p -> p != null && p.contains("动作、班队列")),
                            tag + " 训练内容目的应整格导入，正文顿号不能拆分: " + purposes);
                    break;
                case 4:
                    assertEquals(4, countOf(resp, "supportObjective"), tag + " 支撑目标应原样导入: " + warnMsgs);
                    assertEquals(4, countOf(resp, "supportContent"), tag + " 支撑内容应原样导入: " + warnMsgs);
                    assertEquals(3, countOf(resp, "assessment"), tag + " 考核项应原样导入: " + warnMsgs);
                    List<Map<String, Object>> outcomes = jdbc.queryForList(
                            "SELECT outcome_type, assessment_item, method, assessed_content, weight "
                                    + "FROM t_csys_teaching_plan_assessment WHERE plan_id=6004 AND sysflag=0 ORDER BY sort");
                    assertTrue(outcomes.stream().allMatch(o -> o.get("outcome_type") != null
                                    && o.get("assessment_item") != null && o.get("method") == null),
                            tag + " 成果类型/成果形式应按专用字段原样导入: " + outcomes);
                    break;
                default:
                    break;
            }
        }
    }

    /** 现有数据库实验项目生成后导入，五类明细内容与项目分组均不能丢失。 */
    @Test
    @Order(20)
    void t20_experimentItemDetailRoundTrip() throws Exception {
        JdbcTemplate jdbc = new JdbcTemplate(dataSource);
        Map<String, String> before = jdbc.query(
                "SELECT CONCAT(i.name, '#', d.detail_type) k, d.content v "
                        + "FROM t_csys_teaching_plan_practice_item i "
                        + "JOIN t_csys_teaching_plan_practice_item_detail d ON d.item_id=i.id "
                        + "WHERE i.plan_id=6003 AND i.sysflag=0",
                rs -> {
                    Map<String, String> values = new HashMap<>();
                    while (rs.next()) values.put(rs.getString("k"), rs.getString("v"));
                    return values;
                });
        byte[] docx = generateDocx(8003L);
        String text = extractText(docx);
        assertTrue(text.contains("实验目的与任务"));
        assertTrue(text.contains("训练的能力点"));
        assertTrue(text.contains("实验原理"));
        assertTrue(text.contains("实验内容及要求"));
        assertTrue(text.contains("实验结果及要求"));

        MockMultipartFile file = new MockMultipartFile("file", "experiment-roundtrip.docx",
                "application/vnd.openxmlformats-officedocument.wordprocessingml.document", docx);
        mockMvc.perform(multipart("/teachingPlan/importWord").file(file)
                        .param("courseId", "8003").param("planType", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.issues[?(@.severity == 'ERROR')]").isEmpty());

        Map<String, String> after = jdbc.query(
                "SELECT CONCAT(i.name, '#', d.detail_type) k, d.content v "
                        + "FROM t_csys_teaching_plan_practice_item i "
                        + "JOIN t_csys_teaching_plan_practice_item_detail d ON d.item_id=i.id "
                        + "WHERE i.plan_id=6003 AND i.sysflag=0",
                rs -> {
                    Map<String, String> values = new HashMap<>();
                    while (rs.next()) values.put(rs.getString("k"), rs.getString("v"));
                    return values;
                });
        for (Map.Entry<String, String> entry : before.entrySet()) {
            assertEquals(entry.getValue(), after.get(entry.getKey()), "实验明细往返后内容应保持: " + entry.getKey());
        }
    }

    /** 实践项目模板必须包含拟解决的复杂问题，成果评价使用 outcomeType/assessmentItem 专用映射。 */
    @Test
    @Order(21)
    void t21_practiceProjectTemplateAndOutcomeMapping() throws Exception {
        byte[] docx = generateDocx(8004L);
        String text = extractText(docx);
        assertTrue(text.contains("拟解决的复杂问题"));
        assertTrue(text.contains("成果类型"));
        assertTrue(text.contains("成果形式"));
        assertTrue(text.contains("个人成果") || text.contains("团队成果"));
        assertTrue(text.contains("成果答辩与文档评审"));
        assertTrue(text.contains("系统设计、编码实现与文档质量"));
    }

    /** Word 类型错误属于 ERROR，且必须在清空旧计划前返回。 */
    @Test
    @Order(22)
    void t22_importTypeErrorDoesNotClearExistingPlan() throws Exception {
        JdbcTemplate jdbc = new JdbcTemplate(dataSource);
        Integer before = jdbc.queryForObject(
                "SELECT COUNT(*) FROM t_csys_teaching_plan_content WHERE plan_id=6002 AND sysflag=0", Integer.class);
        byte[] courseDocx = generateDocx(7003L);
        MockMultipartFile file = new MockMultipartFile("file", "wrong-type.docx",
                "application/vnd.openxmlformats-officedocument.wordprocessingml.document", courseDocx);
        mockMvc.perform(multipart("/teachingPlan/importWord").file(file)
                        .param("courseId", "8002").param("planType", "3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.issues[?(@.severity == 'ERROR')]").isNotEmpty());
        Integer after = jdbc.queryForObject(
                "SELECT COUNT(*) FROM t_csys_teaching_plan_content WHERE plan_id=6002 AND sysflag=0", Integer.class);
        assertEquals(before, after, "导入 ERROR 不得清空现有计划");
    }

    /** 导入成功计数；键缺失按 0 处理。 */
    /** 四类生成结果必须覆盖 2026-07-30 模板中的关键字段与专用表头。 */
    @Test
    @Order(23)
    void t23_generatedDocumentsMatchUpdatedTemplateKeyFields() throws Exception {
        File template = new File("C:/Users/31019/Desktop/计划模板（20260730更新）.docx");
        assertTrue(template.isFile(), "缺少模板文件: " + template.getAbsolutePath());
        String templateText;
        try (FileInputStream in = new FileInputStream(template);
             XWPFDocument templateDoc = new XWPFDocument(in)) {
            templateText = extractText(templateDoc);
        }

        assertTemplateFields(templateText, extractText(generateDocx(7003L)),
                "课程目标", "观测点", "拟解决的复杂问题", "考核项目", "评价标准");
        assertTemplateFields(templateText, extractText(generateDocx(8003L)),
                "实验项目名称", "实验目的与任务", "训练的能力点", "实验原理",
                "实验内容及要求", "实验结果及要求");
        assertTemplateFields(templateText, extractText(generateDocx(8002L)),
                "训练目的", "训练任务", "训练内容与时间安排", "模块", "时间安排");
        assertTemplateFields(templateText, extractText(generateDocx(8004L)),
                "拟解决的复杂问题", "成果类型", "成果形式", "评价的知识和能力", "评价准则");
    }

    /** 课程目标批量重建和任务背景 CRUD 均按 planId + schemeId 隔离。 */
    @Test
    @Order(24)
    void t24_dataIsolatedByPlanAndScheme() {
        JdbcTemplate jdbc = new JdbcTemplate(dataSource);
        Long otherSchemeId = 7699L;

        TeachingPlanObjective otherObjective = new TeachingPlanObjective();
        otherObjective.setPlanId(6001L);
        otherObjective.setSchemeId(otherSchemeId);
        otherObjective.setObjectiveTypeCode("1");
        otherObjective.setObjectiveTypeName("知识目标");
        otherObjective.setContent("其他培养方案目标");
        otherObjective.setWeight(BigDecimal.ONE);
        otherObjective.setSort(1);
        teachingPlanModuleService.addObjective(otherObjective);

        TeachingPlanObjective currentObjective = new TeachingPlanObjective();
        currentObjective.setObjectiveTypeCode("1");
        currentObjective.setObjectiveTypeName("知识目标");
        currentObjective.setContent("当前培养方案重建目标");
        currentObjective.setWeight(BigDecimal.ONE);
        TeachingPlanObjectiveSaveVo objectiveRow = new TeachingPlanObjectiveSaveVo();
        objectiveRow.setObjective(currentObjective);
        objectiveRow.setRefs(Collections.emptyList());
        TeachingPlanObjectiveBatchSaveVo objectiveBatch = new TeachingPlanObjectiveBatchSaveVo();
        objectiveBatch.setPlanId(6001L);
        objectiveBatch.setSchemeId(7601L);
        objectiveBatch.setObjectives(Collections.singletonList(objectiveRow));
        teachingPlanModuleService.saveObjectivesBatch(objectiveBatch);

        assertEquals(1, jdbc.queryForObject("SELECT COUNT(*) FROM t_csys_teaching_plan_objective "
                + "WHERE plan_id=6001 AND scheme_id=7601 AND sysflag=0", Integer.class));
        assertEquals(1, jdbc.queryForObject("SELECT COUNT(*) FROM t_csys_teaching_plan_objective "
                + "WHERE plan_id=6001 AND scheme_id=7699 AND sysflag=0", Integer.class));

        TeachingPlanTaskBackground otherBackground = new TeachingPlanTaskBackground();
        otherBackground.setPlanId(6003L);
        otherBackground.setSchemeId(otherSchemeId);
        otherBackground.setBackgroundDesc("其他培养方案实验任务背景");
        otherBackground.setTechnicalGoal("其他培养方案技术目标");
        otherBackground.setAbilityGoal("其他培养方案能力目标");
        teachingPlanModuleService.addTaskBackground(otherBackground);

        TeachingPlanTaskBackground currentBackground = new TeachingPlanTaskBackground();
        currentBackground.setPlanId(6003L);
        currentBackground.setSchemeId(7601L);
        currentBackground.setBackgroundDesc("当前培养方案实验任务背景");
        currentBackground.setTechnicalGoal("当前培养方案技术目标");
        currentBackground.setAbilityGoal("当前培养方案能力目标");
        teachingPlanModuleService.addTaskBackground(currentBackground);

        assertEquals(2, jdbc.queryForObject("SELECT COUNT(*) FROM t_csys_teaching_plan_task_background "
                + "WHERE plan_id=6003 AND scheme_id=7601 AND sysflag=0", Integer.class));
        assertEquals(1, jdbc.queryForObject("SELECT COUNT(*) FROM t_csys_teaching_plan_task_background "
                + "WHERE plan_id=6003 AND scheme_id=7699 AND sysflag=0", Integer.class));
    }

    /** 实践项目候选树固定为课程/课目 -> 方案/通识通用 -> 候选条目。 */
    @Test
    @Order(25)
    void t25_supportCandidateTreeCarriesSchemeNames() {
        List<TeachingPlanSupportCandidateTreeNodeVo> roots =
                teachingPlanModuleService.listSupportCandidateTree(8004L, 1);
        assertEquals(2, roots.size(), "支撑课程和支撑训练课目各返回一个根节点");
        assertEquals("course", roots.get(0).getNodeType());
        assertEquals("计算机科学与技术2026级培养方案", roots.get(0).getChildren().get(0).getName());
        assertEquals("objective", roots.get(0).getChildren().get(0).getChildren().get(0).getNodeType());
        assertEquals("trainingSubject", roots.get(1).getNodeType());
        assertEquals("通识通用", roots.get(1).getChildren().get(0).getName());
        assertEquals("purpose", roots.get(1).getChildren().get(0).getChildren().get(0).getNodeType());
    }

    private String extractText(XWPFDocument doc) {
        StringBuilder sb = new StringBuilder();
        for (XWPFParagraph paragraph : doc.getParagraphs()) sb.append(paragraph.getText()).append('\n');
        for (XWPFTable table : doc.getTables()) {
            for (XWPFTableRow row : table.getRows()) {
                for (XWPFTableCell cell : row.getTableCells()) sb.append(cell.getText()).append('\n');
            }
        }
        return sb.toString();
    }

    private XWPFTable findTable(XWPFDocument doc, String... requiredTexts) {
        for (XWPFTable table : doc.getTables()) {
            StringBuilder text = new StringBuilder();
            for (XWPFTableRow row : table.getRows()) {
                for (XWPFTableCell cell : row.getTableCells()) {
                    text.append(cell.getText()).append('\n');
                }
            }
            boolean matched = true;
            for (String requiredText : requiredTexts) {
                if (!text.toString().contains(requiredText)) {
                    matched = false;
                    break;
                }
            }
            if (matched) {
                return table;
            }
        }
        throw new AssertionError("未找到包含字段 " + Arrays.toString(requiredTexts) + " 的表格");
    }

    private void assertTemplateFields(String templateText, String generatedText, String... fields) {
        for (String field : fields) {
            assertTrue(templateText.contains(field), "更新模板缺少关键字段: " + field);
            assertTrue(generatedText.contains(field), "生成文档缺少模板关键字段: " + field);
        }
    }

    private int countOf(String resp, String key) {
        try {
            Object v = com.jayway.jsonpath.JsonPath.read(resp, "$.data.successCounts." + key);
            return v == null ? 0 : ((Number) v).intValue();
        } catch (Exception e) {
            return 0;
        }
    }
}
