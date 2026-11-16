package com.doinner.csys;

import com.doinner.common.core.context.SecurityContextHolder;
import com.doinner.csys.dao.TeachingPlanTargetDesignMapper;
import com.doinner.csys.domain.TeachingPlanTargetDesign;
import com.doinner.csys.domain.vo.TeachingPlanKnowledgeBatchSaveVo;
import com.doinner.csys.controller.TeachingPlanController;
import org.springframework.web.bind.annotation.PostMapping;
import com.doinner.csys.permission.CoursePermissionAspect;
import com.doinner.csys.permission.CoursePermissionService;
import com.doinner.csys.service.impl.TeachingPlanModuleServiceImpl;
import org.apache.ibatis.builder.xml.XMLMapperBuilder;
import org.apache.ibatis.datasource.unpooled.UnpooledDataSource;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.jupiter.api.*;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.transaction.SpringManagedTransactionFactory;
import org.springframework.aop.aspectj.annotation.AspectJProxyFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.annotation.AnnotationTransactionAttributeSource;
import org.springframework.transaction.interceptor.TransactionInterceptor;

import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

import static com.doinner.csys.permission.CourseResource.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class TeachingPlanKnowledgeBatchTest {
    private Object service;
    private JdbcTemplate jdbc;
    private CoursePermissionService permissions;

    @BeforeEach
    void setup() throws Exception {
        SecurityContextHolder.setUserName("knowledge-batch-test");
        UnpooledDataSource db = new UnpooledDataSource("org.h2.Driver",
                "jdbc:h2:mem:" + UUID.randomUUID() + ";MODE=MySQL;DB_CLOSE_DELAY=-1", "sa", "");
        jdbc = new JdbcTemplate(db);
        jdbc.execute("CREATE TABLE t_csys_teaching_plan_target_design (id BIGINT AUTO_INCREMENT PRIMARY KEY,"
                + "plan_id BIGINT, scheme_id BIGINT, design_type_code VARCHAR(10), objective_id BIGINT,"
                + "objective_text VARCHAR(100), knowledge_unit_id BIGINT, knowledge_unit_name VARCHAR(100),"
                + "knowledge_point_id BIGINT, knowledge_point_name VARCHAR(100), knowledge_points CLOB,"
                + "observation_point CLOB, content_ids CLOB, content_text CLOB, teaching_link VARCHAR(100),"
                + "teaching_method VARCHAR(255), learning_method VARCHAR(255), hours DECIMAL(10,1) CHECK(hours>=0),"
                + "teaching_design CLOB, sort INT, creator VARCHAR(100), create_time TIMESTAMP,"
                + "last_modifier VARCHAR(100), last_modified_time TIMESTAMP, remark VARCHAR(100), sysflag INT DEFAULT 0)");
        jdbc.execute("INSERT INTO t_csys_teaching_plan_target_design(id,plan_id,design_type_code,objective_text,hours)"
                + " VALUES (101,60,'1','Original',2),(102,60,'1','Untouched',3),(103,60,'2','Ability',4),(104,61,'1','Foreign',5)");
        jdbc.update("UPDATE t_csys_teaching_plan_target_design SET knowledge_points=? WHERE id=101",
                "[{\"knowledgePointId\":11},{\"knowledgePointId\":12}]");
        Configuration config = new Configuration(new Environment("batch", new SpringManagedTransactionFactory(), db));
        try (InputStream xml = Files.newInputStream(Paths.get("csys-service/src/main/resources/mapper/csys/TeachingPlanTargetDesignMapper.xml"))) {
            new XMLMapperBuilder(xml, config, "TeachingPlanTargetDesignMapper.xml", config.getSqlFragments()).parse();
        }
        TeachingPlanModuleServiceImpl target = new TeachingPlanModuleServiceImpl();
        ReflectionTestUtils.setField(target, "teachingPlanTargetDesignMapper",
                new SqlSessionTemplate(new SqlSessionFactoryBuilder().build(config)).getMapper(TeachingPlanTargetDesignMapper.class));
        permissions = mock(CoursePermissionService.class);
        ReflectionTestUtils.setField(target, "targetDesignPermissions", permissions);
        DataSourceTransactionManager manager = new DataSourceTransactionManager(db);
        AspectJProxyFactory proxy = new AspectJProxyFactory(target);
        proxy.setProxyTargetClass(true);
        proxy.addAspect(new CoursePermissionAspect(permissions, manager));
        proxy.addAdvice(new TransactionInterceptor(manager, new AnnotationTransactionAttributeSource()));
        service = proxy.getProxy();
    }

    @AfterEach
    void cleanup() { SecurityContextHolder.remove(); }

    private TeachingPlanTargetDesign row(Long id, String text) {
        TeachingPlanTargetDesign row = new TeachingPlanTargetDesign();
        row.setId(id);
        row.setPlanId(60L);
        row.setDesignTypeCode("1");
        row.setObjectiveText(text);
        row.setHours(new BigDecimal("6"));
        row.setKnowledgePointId(11L);
        row.setTeachingMethod("A,A");
        return row;
    }

    private List<Long> save(TeachingPlanTargetDesign... rows) {
        return ReflectionTestUtils.invokeMethod(service, "saveKnowledgeDesignsBatch", 60L, Arrays.asList(rows));
    }

    @Test
    void upsertsOnlySuppliedRowsAndReturnsOrderedIds() {
        List<Long> ids = save(row(101L, "Edited"), row(null, "Split"));
        assertEquals(101L, ids.get(0));
        assertNotNull(ids.get(1));
        assertEquals(5, jdbc.queryForObject("SELECT COUNT(*) FROM t_csys_teaching_plan_target_design", Integer.class));
        assertEquals("Untouched", jdbc.queryForObject("SELECT objective_text FROM t_csys_teaching_plan_target_design WHERE id=102", String.class));
        assertEquals("A", jdbc.queryForObject("SELECT teaching_method FROM t_csys_teaching_plan_target_design WHERE id=101", String.class));
        verify(permissions).check(PLAN, 60L, null, true);
        verify(permissions, times(2)).validatePlanScope(eq(PLAN), eq(60L), any(TeachingPlanTargetDesign.class));
        verify(permissions).validateRequest(eq(TARGET_DESIGN), any(TeachingPlanTargetDesign.class));
    }

    @Test
    void databaseFailureAfterAnInsertRollsBackEveryWrite() {
        String originalBindings = jdbc.queryForObject("SELECT knowledge_points FROM t_csys_teaching_plan_target_design WHERE id=101", String.class);
        TeachingPlanTargetDesign invalid = row(101L, "x".repeat(101));
        assertThrows(RuntimeException.class, () -> save(row(null, "Split"), invalid));
        assertEquals(4, jdbc.queryForObject("SELECT COUNT(*) FROM t_csys_teaching_plan_target_design", Integer.class));
        assertEquals("Original", jdbc.queryForObject("SELECT objective_text FROM t_csys_teaching_plan_target_design WHERE id=101", String.class));
        assertEquals(originalBindings, jdbc.queryForObject("SELECT knowledge_points FROM t_csys_teaching_plan_target_design WHERE id=101", String.class));
        TeachingPlanTargetDesign split = row(null, "Split");
        split.setKnowledgePointId(12L);
        assertEquals(2, save(row(101L, "Edited"), split).size());
        assertEquals(5, jdbc.queryForObject("SELECT COUNT(*) FROM t_csys_teaching_plan_target_design", Integer.class));
        assertEquals(1, jdbc.queryForObject("SELECT COUNT(*) FROM t_csys_teaching_plan_target_design WHERE knowledge_point_id=11", Integer.class));
        assertEquals(1, jdbc.queryForObject("SELECT COUNT(*) FROM t_csys_teaching_plan_target_design WHERE knowledge_point_id=12", Integer.class));
    }

    @Test
    void rejectsForeignMissingWrongTypeAndDuplicateIdsBeforeWrites() {
        for (Long id : Arrays.asList(104L, 103L, 999L)) {
            assertThrows(IllegalArgumentException.class, () -> save(row(null, "New"), row(id, "Invalid")));
        }
        TeachingPlanTargetDesign wrongPlan = row(null, "Foreign");
        wrongPlan.setPlanId(61L);
        assertThrows(IllegalArgumentException.class, () -> save(wrongPlan));
        TeachingPlanTargetDesign wrongType = row(null, "Ability");
        wrongType.setDesignTypeCode("2");
        assertThrows(IllegalArgumentException.class, () -> save(wrongType));
        assertThrows(IllegalArgumentException.class, () -> save(row(101L, "First"), row(101L, "Duplicate")));
        assertEquals(4, jdbc.queryForObject("SELECT COUNT(*) FROM t_csys_teaching_plan_target_design", Integer.class));
    }

    @Test
    void permissionAndExistingValidationRejectWholeBatch() {
        doThrow(new IllegalArgumentException("Denied")).when(permissions).check(PLAN, 60L, null, true);
        assertThrows(IllegalArgumentException.class, () -> save(row(null, "Denied")));
        reset(permissions);
        TeachingPlanTargetDesign invalid = row(null, "Invalid method");
        invalid.setTeachingMethod("x".repeat(501));
        assertThrows(RuntimeException.class, () -> save(row(101L, "Edited"), invalid));
        assertEquals("Original", jdbc.queryForObject("SELECT objective_text FROM t_csys_teaching_plan_target_design WHERE id=101", String.class));
    }

    @Test
    void schemeAndObjectiveScopeFailuresPreventAllWrites() {
        doThrow(new IllegalArgumentException("Foreign scheme")).when(permissions)
                .validatePlanScope(eq(PLAN), eq(60L), any(TeachingPlanTargetDesign.class));
        assertThrows(IllegalArgumentException.class, () -> save(row(null, "New")));
        reset(permissions);
        TeachingPlanTargetDesign foreignObjective = row(null, "Foreign objective");
        foreignObjective.setObjectiveId(800L);
        doThrow(new IllegalArgumentException("Foreign objective")).when(permissions).requireSamePlan(OBJECTIVE, 800L, 60L);
        assertThrows(IllegalArgumentException.class, () -> save(row(101L, "Edited"), foreignObjective));
        assertEquals(4, jdbc.queryForObject("SELECT COUNT(*) FROM t_csys_teaching_plan_target_design", Integer.class));
        assertEquals("Original", jdbc.queryForObject("SELECT objective_text FROM t_csys_teaching_plan_target_design WHERE id=101", String.class));
    }

    @Test
    void emptyOrMissingRowsNeverClearTheTabAndControllerExposesDedicatedRoute() throws Exception {
        assertThrows(IllegalArgumentException.class, () -> save());
        assertThrows(IllegalArgumentException.class, () -> ReflectionTestUtils.invokeMethod(service,
                "saveKnowledgeDesignsBatch", 60L, null));
        assertEquals(4, jdbc.queryForObject("SELECT COUNT(*) FROM t_csys_teaching_plan_target_design", Integer.class));
        assertArrayEquals(new String[]{"/targetDesign/batchSave"}, TeachingPlanController.class
                .getMethod("saveKnowledgeDesignsBatch", TeachingPlanKnowledgeBatchSaveVo.class)
                .getAnnotation(PostMapping.class).value());
    }
}
