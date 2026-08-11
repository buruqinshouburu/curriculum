package com.doinner.csys;

import com.doinner.common.core.domain.DataSet;
import com.doinner.common.core.domain.DataTable;
import com.doinner.common.core.domain.Message;
import com.doinner.common.security.utils.DictUtils;
import com.doinner.csys.domain.TeachingPlanContent;
import com.doinner.csys.entity.csys.TeachingPlanWordImporter;
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
                "spring.datasource.dynamic.datasource.master.url=jdbc:mysql://127.0.0.1:3306/doinner-curriculum-test-3.2?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai&useSSL=false",
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

    /** t7 type4 支撑绑定候选：课程目标/训练目的/知识体系/训练内容四组，训练内容显示为模块名称。 */
    @Test
    @Order(7)
    void t7_supportCandidates_type4() throws Exception {
        mockMvc.perform(get("/teachingPlan/support/candidates").param("courseId", "8004"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.objectives[?(@.name=='掌握结构化程序设计的基本方法与三种基本结构')]").exists())
                .andExpect(jsonPath("$.data.objectives[?(@.name=='具备运用线性表、树等数据结构解决实际问题的能力')]").exists())
                .andExpect(jsonPath("$.data.objectives[?(@.name=='培养规范编码习惯、调试能力与团队协作意识')]").exists())
                .andExpect(jsonPath("$.data.purposes[?(@.name=='掌握单个军人队列动作、班队列组织等基本军事素养')]").exists())
                .andExpect(jsonPath("$.data.knowledgePoints[0].name").value("第一章 程序设计基础"))
                .andExpect(jsonPath("$.data.trainingContents.length()").value(3))
                .andExpect(jsonPath("$.data.trainingContents[0].name").value("战斗体技能提升模块"))
                .andExpect(jsonPath("$.data.trainingContents[1].name").value("指挥素养培塑模块"))
                .andExpect(jsonPath("$.data.trainingContents[2].name").value("新质新域能力拓展模块"));
    }

    /** t8 type4 支撑绑定保存+回显（整表重建，与预置种子等值）。 */
    @Test
    @Order(8)
    void t8_supportSaveList_type4() throws Exception {
        // 课程目标/训练目的
        mockMvc.perform(post("/teachingPlan/supportObjective/save")
                        .contentType("application/json")
                        .content("{\"planId\":6004,\"objectiveIds\":[60011],\"purposeIds\":[62111]}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
        mockMvc.perform(get("/teachingPlan/supportObjective/list").param("planId", "6004"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(2))
                .andExpect(jsonPath("$.data[0].itemName").value("掌握结构化程序设计的基本方法与三种基本结构"))
                .andExpect(jsonPath("$.data[0].refType").value(1))
                .andExpect(jsonPath("$.data[1].itemName").value("掌握单个军人队列动作、班队列组织等基本军事素养"))
                .andExpect(jsonPath("$.data[1].refType").value(2));
        // 知识体系/训练内容：实践训练课目内容快照存模块名称
        mockMvc.perform(post("/teachingPlan/supportContent/save")
                        .contentType("application/json")
                        .content("{\"planId\":6004,\"contentIds\":[65011,62211]}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
        mockMvc.perform(get("/teachingPlan/supportContent/list").param("planId", "6004"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(2))
                .andExpect(jsonPath("$.data[0].itemTitle").value("第一章 程序设计基础"))
                .andExpect(jsonPath("$.data[0].refType").value(1))
                .andExpect(jsonPath("$.data[1].itemTitle").value("战斗体技能提升模块"))
                .andExpect(jsonPath("$.data[1].refType").value(2));
    }

    /** t9 实践训练课目模块字段导入反向映射：导出 docx(名称) → 解析器反查 label→编码(1/2/3)。 */
    @Test
    @Order(9)
    void t9_importReverseMap_practiceSubject() throws Exception {
        byte[] docx = generateDocx(8002L);
        TeachingPlanWordImporter importer = new TeachingPlanWordImporter();
        TeachingPlanWordImporter.ParseContext ctx = new TeachingPlanWordImporter.ParseContext();
        // 与 Service.buildImportParseContext 同源预填：模块字典 label→value
        List<SysDictData> modules = CurDictUtils.getDictData("sys_plan_training_module");
        for (SysDictData d : modules) {
            if (d == null || d.getDictLabel() == null || d.getDictValue() == null) {
                continue;
            }
            TeachingPlanWordImporter.putDict(ctx.dictLabelToValue,
                    "sys_plan_training_module", d.getDictLabel(), d.getDictValue());
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
}
