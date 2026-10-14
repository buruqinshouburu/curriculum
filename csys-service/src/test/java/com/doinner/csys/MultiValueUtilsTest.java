package com.doinner.csys;

import com.doinner.csys.utils.MultiValueUtils;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/** 教法/学法多选落库规范化与历史单选兼容。 */
class MultiValueUtilsTest {

    @Test
    void multiSelectIsJoinedDeduplicatedAndTrimmed() {
        assertEquals("1、2、3", MultiValueUtils.normalize("1,2,3"));
        assertEquals("1、2、3", MultiValueUtils.normalize("1、2，3"));
        assertEquals("1、2", MultiValueUtils.normalize(" 1 , 1 、2 "));
        assertEquals("讲授法、案例教学法", MultiValueUtils.normalize("讲授法;案例教学法"));
        assertEquals("1、2", MultiValueUtils.normalize("1|2/"));
    }

    @Test
    void historicalSingleValuesSurviveUnchanged() {
        assertEquals("1", MultiValueUtils.normalize("1"));
        assertNull(MultiValueUtils.normalize(null));
        assertNull(MultiValueUtils.normalize(""));
        assertNull(MultiValueUtils.normalize("  、， "));
    }

    @Test
    void splittingKeepsOriginalOrder() {
        assertEquals(Arrays.asList("3", "1", "2"), MultiValueUtils.split("3,1,2,1"));
        assertEquals(Collections.emptyList(), MultiValueUtils.split(null));
    }

    @Test
    void explicitClearingAndOmittedFieldsHaveDifferentDatabaseEffects() throws Exception {
        org.springframework.jdbc.datasource.DriverManagerDataSource source = new org.springframework.jdbc.datasource.DriverManagerDataSource(
                "jdbc:h2:mem:multiselect_" + java.util.UUID.randomUUID() + ";MODE=MySQL;DB_CLOSE_DELAY=-1", "sa", "");
        org.springframework.jdbc.core.JdbcTemplate jdbc = new org.springframework.jdbc.core.JdbcTemplate(source);
        jdbc.execute("CREATE TABLE t_csys_teaching_plan_target_design(id BIGINT PRIMARY KEY,teaching_method VARCHAR(500),learning_method VARCHAR(500),hours DECIMAL(10,2),teaching_design VARCHAR(500),last_modifier VARCHAR(64),last_modified_time TIMESTAMP,sysflag INT DEFAULT 0)");
        jdbc.update("INSERT INTO t_csys_teaching_plan_target_design(id,teaching_method,learning_method,hours,teaching_design) VALUES(1,'1','3',2,'原设计')");
        org.apache.ibatis.session.Configuration config = new org.apache.ibatis.session.Configuration(
                new org.apache.ibatis.mapping.Environment("test", new org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory(), source));
        String resource = "mapper/csys/TeachingPlanTargetDesignMapper.xml";
        try (java.io.InputStream in = getClass().getClassLoader().getResourceAsStream(resource)) {
            new org.apache.ibatis.builder.xml.XMLMapperBuilder(in, config, resource, config.getSqlFragments()).parse();
        }
        com.fasterxml.jackson.databind.ObjectMapper json = new com.fasterxml.jackson.databind.ObjectMapper();
        try (org.apache.ibatis.session.SqlSession session = new org.apache.ibatis.session.SqlSessionFactoryBuilder().build(config).openSession(true)) {
            com.doinner.csys.service.impl.TeachingPlanModuleServiceImpl service = new com.doinner.csys.service.impl.TeachingPlanModuleServiceImpl();
            org.springframework.test.util.ReflectionTestUtils.setField(service, "teachingPlanTargetDesignMapper", session.getMapper(com.doinner.csys.dao.TeachingPlanTargetDesignMapper.class));
            com.doinner.common.core.context.SecurityContextHolder.setUserName("test.account");
            service.updateTargetDesign(json.readValue("{\"id\":1,\"teachingMethod\":\"1,2,1\",\"learningMethod\":\"3,4\"}", com.doinner.csys.domain.TeachingPlanTargetDesign.class));
            assertEquals("1、2", jdbc.queryForObject("SELECT teaching_method FROM t_csys_teaching_plan_target_design WHERE id=1", String.class));
            service.updateTargetDesign(json.readValue("{\"id\":1,\"teachingMethod\":\"\"}", com.doinner.csys.domain.TeachingPlanTargetDesign.class));
            assertNull(jdbc.queryForObject("SELECT teaching_method FROM t_csys_teaching_plan_target_design WHERE id=1", String.class));
            assertEquals("3、4", jdbc.queryForObject("SELECT learning_method FROM t_csys_teaching_plan_target_design WHERE id=1", String.class));
            service.updateTargetDesign(json.readValue("{\"id\":1,\"learningMethod\":null}", com.doinner.csys.domain.TeachingPlanTargetDesign.class));
            assertNull(jdbc.queryForObject("SELECT learning_method FROM t_csys_teaching_plan_target_design WHERE id=1", String.class));
            assertEquals(new java.math.BigDecimal("2.00"), jdbc.queryForObject("SELECT hours FROM t_csys_teaching_plan_target_design WHERE id=1", java.math.BigDecimal.class));
            assertEquals("原设计", jdbc.queryForObject("SELECT teaching_design FROM t_csys_teaching_plan_target_design WHERE id=1", String.class));
        } finally { com.doinner.common.core.context.SecurityContextHolder.remove(); }
    }
}
