package com.doinner.csys;

import com.doinner.csys.domain.TrainingSchemeCategory;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.ibatis.builder.xml.XMLMapperBuilder;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.session.Configuration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.*;

/** 验证门类专业分类的接口字段与动态 SQL，无需启动应用或连接数据库。 */
class TrainingSchemeCategoryMapperTest {
    private static final String NAMESPACE = "com.doinner.csys.dao.TrainingSchemeCategoryMapper.";
    private Configuration configuration;

    @BeforeEach
    void loadMapper() throws Exception {
        configuration = new Configuration();
        String resource = "mapper/csys/TrainingSchemeCategoryMapper.xml";
        try (InputStream input = getClass().getClassLoader().getResourceAsStream(resource)) {
            assertNotNull(input);
            new XMLMapperBuilder(input, configuration, resource, configuration.getSqlFragments()).parse();
        }
    }

    private BoundSql sql(String statement, Object parameter) {
        return configuration.getMappedStatement(NAMESPACE + statement).getBoundSql(parameter);
    }

    @Test
    void systemTypeCanBeReceivedAndReturned() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        TrainingSchemeCategory category = mapper.readValue("{\"systemType\":2}", TrainingSchemeCategory.class);
        assertEquals(Long.valueOf(2), category.getSystemType());
        assertEquals(2, mapper.readTree(mapper.writeValueAsString(category)).get("systemType").asInt());
    }

    @Test
    void insertAndUpdateBindSystemType() {
        TrainingSchemeCategory category = new TrainingSchemeCategory();
        category.setId(1L);
        category.setName("测试门类");
        category.setSystemType(3L);
        for (String statement : new String[]{"insertTrainingSchemeCategory", "updateTrainingSchemeCategory"}) {
            BoundSql boundSql = sql(statement, category);
            assertTrue(boundSql.getSql().contains("system_type"));
            assertTrue(boundSql.getParameterMappings().stream().anyMatch(p -> "systemType".equals(p.getProperty())));
        }
        category.setSystemType(null);
        assertFalse(sql("updateTrainingSchemeCategory", category).getSql().contains("system_type"));
        assertFalse(sql("insertTrainingSchemeCategory", category).getSql().contains("system_type"));
    }

    @Test
    void listFiltersOnlyWhenProvidedAndQueriesMapSystemType() {
        TrainingSchemeCategory category = new TrainingSchemeCategory();
        assertFalse(sql("selectTrainingSchemeCategoryList", category).getSql().contains("tsc.system_type ="));
        category.setSystemType(4L);
        assertTrue(sql("selectTrainingSchemeCategoryList", category).getSql().contains("tsc.system_type ="));
        assertTrue(sql("selectTrainingSchemeCategoryById", 1L).getSql().contains("system_type"));
        assertTrue(sql("selectAllTrainingSchemeCategoryById", 1L).getSql().contains("system_type"));
        assertTrue(configuration.getResultMap(NAMESPACE + "TrainingSchemeCategoryResult")
                .getResultMappings().stream().anyMatch(m -> "systemType".equals(m.getProperty())
                        && "system_type".equals(m.getColumn())));
    }

    @Test
    void deleteStillUsesLogicalDeletion() {
        String deleteSql = sql("deleteTrainingSchemeCategoryById", 1L).getSql();
        assertTrue(deleteSql.contains("sysflag = 2"));
        assertFalse(deleteSql.contains("system_type"));
    }
}
