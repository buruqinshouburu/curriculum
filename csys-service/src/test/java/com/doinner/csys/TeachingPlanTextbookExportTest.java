package com.doinner.csys;

import com.doinner.common.core.domain.DataSet;
import com.doinner.csys.controller.TeachingPlanTextbookExportController;
import com.doinner.csys.dao.TeachingPlanTextbookMapper;
import com.doinner.csys.domain.vo.TeachingPlanTextbookExportVo;
import com.doinner.csys.service.impl.TeachingPlanTextbookExportServiceImpl;
import com.doinner.system.domain.entity.SysDictData;
import com.doinner.system.service.DoinnerDictDataService;
import org.apache.ibatis.builder.xml.XMLMapperBuilder;
import org.apache.ibatis.datasource.unpooled.UnpooledDataSource;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/** SQL joins, dictionary conversion and the actual Excel response, using only an isolated H2 database. */
class TeachingPlanTextbookExportTest {
    private SqlSession session;
    private JdbcTemplate jdbc;
    private TeachingPlanTextbookExportServiceImpl service;
    private DoinnerDictDataService dictionaries;

    @BeforeEach
    void setup() throws Exception {
        UnpooledDataSource source = new UnpooledDataSource("org.h2.Driver",
                "jdbc:h2:mem:" + UUID.randomUUID() + ";MODE=MySQL;DB_CLOSE_DELAY=-1", "sa", "");
        jdbc = new JdbcTemplate(source);
        jdbc.execute("CREATE TABLE sys_dept(dept_id BIGINT PRIMARY KEY,dept_name VARCHAR(100))");
        jdbc.update("INSERT INTO sys_dept VALUES(10,'开课学院甲'),(20,'开课学院乙'),(99,'施教学院不可用')");
        jdbc.execute("CREATE TABLE t_csys_course(id BIGINT PRIMARY KEY,name VARCHAR(100),code VARCHAR(100),"
                + "type INT,college_id BIGINT,teach_college_id BIGINT,course_attr VARCHAR(100),source_id BIGINT,sysflag INT)");
        jdbc.update("INSERT INTO t_csys_course VALUES"
                + "(1,'课程一','0001',1,10,99,'必修',NULL,0),"
                + "(2,'训练课目','0002',2,10,99,'2',NULL,0),"
                + "(3,'实验课程','0003',3,20,99,'1',NULL,0),"
                + "(4,'实践项目','0004',4,20,99,'1',NULL,0),"
                + "(7,'学院为空也保留','0007',4,NULL,99,'1',NULL,0),"
                + "(9,'已删除课程','0009',1,10,99,'1',NULL,2),"
                + "(1001,'调用课程一','1001',1,20,99,'1',1,0),"
                + "(1002,'调用课程二','1002',1,20,99,'1',1,0)");
        jdbc.execute("CREATE TABLE t_csys_training_scheme_ref_course(scheme_id BIGINT,course_id BIGINT)");
        jdbc.update("INSERT INTO t_csys_training_scheme_ref_course VALUES(501,1001),(502,1001),(503,1002)");
        jdbc.execute("CREATE TABLE t_csys_teaching_plan(id BIGINT PRIMARY KEY,source_course_id BIGINT,"
                + "plan_type INT,current_flag INT,sysflag INT)");
        jdbc.update("INSERT INTO t_csys_teaching_plan VALUES"
                + "(101,1,1,1,0),(202,2,3,1,0),(303,3,2,1,0),(404,4,4,1,0),"
                + "(105,1,2,1,0),(106,1,1,0,0),(107,1,1,1,2),(707,7,4,1,0),(909,9,1,1,0)");
        jdbc.execute("CREATE TABLE t_csys_teaching_plan_textbook(id BIGINT PRIMARY KEY,plan_id BIGINT,"
                + "name VARCHAR(255),material_nature VARCHAR(100),publish_method VARCHAR(100),"
                + "first_author VARCHAR(100),edition VARCHAR(100),publisher VARCHAR(100),publish_time VARCHAR(100),"
                + "isbn VARCHAR(100),textbook_source VARCHAR(100),sort INT,sysflag INT)");
        book(11, 101, "同名教材", "3", 0);
        book(12, 101, "同名教材", "3", 0);
        book(21, 202, "训练教材", "2", 0);
        book(31, 303, "实验教材", "1", 0);
        book(41, 404, "实践教材", null, 0);
        book(51, 105, "同课另一计划教材", "4", 0);
        book(61, 707, "无学院教材", null, 0);
        book(71, 101, "已删除教材", "1", 2);
        book(81, 106, "历史计划教材", "1", 0);
        book(91, 107, "已删除计划教材", "1", 0);
        book(101, 909, "已删除课程教材", "1", 0);
        book(111, 999, "孤立教材", "1", 0);
        Configuration configuration = new Configuration(new Environment("export-test", new JdbcTransactionFactory(), source));
        try (InputStream xml = getClass().getResourceAsStream("/mapper/csys/TeachingPlanTextbookMapper.xml")) {
            new XMLMapperBuilder(xml, configuration, "TeachingPlanTextbookMapper.xml", configuration.getSqlFragments()).parse();
        }
        session = new SqlSessionFactoryBuilder().build(configuration).openSession(true);
        service = new TeachingPlanTextbookExportServiceImpl();
        dictionaries = mock(DoinnerDictDataService.class);
        ReflectionTestUtils.setField(service, "textbookMapper", session.getMapper(TeachingPlanTextbookMapper.class));
        ReflectionTestUtils.setField(service, "dictDataService", dictionaries);
        dictionary("cur_course_attribute", "必修", "限选");
        dictionary("sys_textbook_nature", "基本教材", "辅助教材", "参考教材");
        dictionary("sys_publication_method", "公开出版", "军内发行", "内部印发");
        dictionary("sys_textbook_source", "自编教材", "选用国内教材", "选用境外教材国内翻译版", "选用境外原版教材");
    }

    @AfterEach
    void cleanup() {
        session.close();
    }

    @Test
    void exportsOnlySelectedPlanWithoutReferenceMultiplicationOrDistinctDataLoss() {
        List<TeachingPlanTextbookExportVo> selected = service.listForExport(101L);
        assertEquals(2, selected.size());
        assertTrue(selected.stream().allMatch(row -> "同名教材".equals(row.getName())));
        assertEquals("同课另一计划教材", service.listForExport(105L).get(0).getName());
        List<TeachingPlanTextbookExportVo> rows = new ArrayList<>();
        for (Long planId : Arrays.asList(101L, 202L, 303L, 404L, 105L, 707L)) {
            rows.addAll(service.listForExport(planId));
        }
        assertEquals(7, rows.size());
        assertEquals(2, rows.stream().filter(row -> "同名教材".equals(row.getName())).count());
        assertEquals(Arrays.asList("同名教材", "同名教材", "同课另一计划教材", "训练教材", "实验教材", "实践教材", "无学院教材")
                        .stream().sorted().collect(Collectors.toList()),
                rows.stream().map(TeachingPlanTextbookExportVo::getName).sorted().collect(Collectors.toList()));
        TeachingPlanTextbookExportVo first = rows.stream().filter(row -> "同名教材".equals(row.getName())).findFirst().orElseThrow();
        assertEquals("开课学院甲", first.getCollegeName());
        assertEquals("0001", first.getCourseCode());
        assertEquals("必修", first.getCourseAttr());
        assertEquals("基本教材", first.getMaterialNature());
        assertEquals("军内发行", first.getPublishMethod());
        assertEquals("选用境外教材国内翻译版", first.getTextbookSource());
        assertTrue(rows.stream().anyMatch(row -> "限选".equals(row.getCourseAttr())));
        assertTrue(rows.stream().anyMatch(row -> row.getCollegeName() == null && "无学院教材".equals(row.getName())));
        assertTrue(rows.stream().anyMatch(row -> "实践教材".equals(row.getName()) && "".equals(row.getTextbookSource())));
    }

    @Test
    void endpointWritesExactlyThirteenOrderedColumnsAndPreservesTextIdentifiers() throws Exception {
        TeachingPlanTextbookExportController controller = new TeachingPlanTextbookExportController();
        ReflectionTestUtils.setField(controller, "textbookExportService", service);
        MockHttpServletResponse response = MockMvcBuilders.standaloneSetup(controller).build()
                .perform(post("/teachingPlan/textbook/export").param("planId", "101"))
                .andExpect(status().isOk()).andReturn().getResponse();
        try (Workbook workbook = WorkbookFactory.create(new ByteArrayInputStream(response.getContentAsByteArray()))) {
            assertEquals(1, workbook.getNumberOfSheets());
            Sheet sheet = workbook.getSheetAt(0);
            Row header = null;
            for (Row row : sheet) {
                if (row.getCell(0) != null && "学院".equals(row.getCell(0).toString())) {
                    header = row;
                    break;
                }
            }
            assertNotNull(header);
            List<String> headers = new ArrayList<>();
            header.forEach(cell -> headers.add(cell.getStringCellValue()));
            assertEquals(Arrays.asList("学院", "课程名称", "课程编号", "课程属性", "教材名称", "教材性质", "出版方式",
                    "第一作者", "版次", "出版（颁发）单位", "出版（颁发）时间", "ISBN号（统一书号）", "教材来源"), headers);
            assertEquals(2, sheet.getLastRowNum() - header.getRowNum());
            boolean found = false;
            for (int index = header.getRowNum() + 1; index <= sheet.getLastRowNum(); index++) {
                Row row = sheet.getRow(index);
                assertEquals(13, row.getLastCellNum());
                if ("同名教材".equals(row.getCell(4).getStringCellValue())) {
                    found = true;
                    assertEquals("0001", row.getCell(2).getStringCellValue());
                    assertEquals("必修", row.getCell(3).getStringCellValue());
                    assertEquals("001-颁发文件号", row.getCell(11).getStringCellValue());
                    assertEquals("选用境外教材国内翻译版", row.getCell(12).getStringCellValue());
                }
            }
            assertTrue(found);
        }
    }

    @Test
    void missingOrInvalidPlanIdCannotFallBackToSchoolWideExport() throws Exception {
        TeachingPlanTextbookExportController controller = new TeachingPlanTextbookExportController();
        ReflectionTestUtils.setField(controller, "textbookExportService", service);
        org.springframework.test.web.servlet.MockMvc mvc = MockMvcBuilders.standaloneSetup(controller).build();
        mvc.perform(post("/teachingPlan/textbook/export")).andExpect(status().isBadRequest());
        mvc.perform(post("/teachingPlan/textbook/export").param("planId", "0")).andExpect(status().isBadRequest());
        mvc.perform(post("/teachingPlan/textbook/export").param("planId", "-1")).andExpect(status().isBadRequest());
    }

    @Test
    void unknownDictionaryCodesFailExplicitlyInsteadOfLeakingIdsIntoExcel() {
        jdbc.update("UPDATE t_csys_teaching_plan_textbook SET textbook_source='999' WHERE id=11");
        IllegalStateException error = assertThrows(IllegalStateException.class, () -> service.listForExport(101L));
        assertTrue(error.getMessage().contains("sys_textbook_source=999"));
    }

    @Test
    void historicalChineseLabelsRemainReadableWithoutBeingReclassified() {
        jdbc.update("UPDATE t_csys_teaching_plan_textbook SET material_nature='第三方',"
                + "publish_method='历史出版方式',textbook_source='历史教材来源' WHERE id=11");
        List<TeachingPlanTextbookExportVo> rows = service.listForExport(101L);
        assertEquals("第三方", rows.get(0).getMaterialNature());
        assertEquals("历史出版方式", rows.get(0).getPublishMethod());
        assertEquals("历史教材来源", rows.get(0).getTextbookSource());
        assertEquals("基本教材", rows.get(1).getMaterialNature());
    }

    @Test
    void missingOptionalSourcesAndEmptyPlanRemainExportable() {
        jdbc.update("UPDATE t_csys_teaching_plan_textbook SET textbook_source=NULL");
        when(dictionaries.dictType("sys_textbook_source")).thenReturn(null);
        assertEquals(2, service.listForExport(101L).size());
        jdbc.update("UPDATE t_csys_teaching_plan_textbook SET sysflag=2");
        session.clearCache();
        clearInvocations(dictionaries);
        assertTrue(service.listForExport(101L).isEmpty());
        verifyNoInteractions(dictionaries);
    }

    @Test
    void deletedHistoricalAndUnknownPlansNeverExportOtherPlanData() {
        for (Long planId : Arrays.asList(106L, 107L, 909L, 999L)) {
            assertTrue(service.listForExport(planId).isEmpty(), "planId=" + planId);
        }
        assertThrows(IllegalArgumentException.class, () -> service.listForExport(null));
        assertThrows(IllegalArgumentException.class, () -> service.listForExport(0L));
        assertThrows(IllegalArgumentException.class, () -> service.listForExport(-1L));
    }

    private void book(int id, int planId, String name, String source, int sysflag) {
        jdbc.update("INSERT INTO t_csys_teaching_plan_textbook VALUES(?,?,?,'1','2','作者','第2版','出版社',"
                + "'2026.10','001-颁发文件号',?,1,?)", id, planId, name, source, sysflag);
    }

    private void dictionary(String type, String... labels) {
        List<SysDictData> entries = new ArrayList<>();
        for (int index = 0; index < labels.length; index++) {
            SysDictData entry = new SysDictData();
            entry.setDictType(type);
            entry.setDictValue(String.valueOf(index + 1));
            entry.setDictLabel(labels[index]);
            entries.add(entry);
        }
        when(dictionaries.dictType(type)).thenReturn(DataSet.success(entries));
    }
}
