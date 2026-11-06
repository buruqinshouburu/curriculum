package com.doinner.csys;

import com.doinner.csys.dao.TeachingPlanTextbookMapper;
import com.doinner.csys.domain.TeachingPlanTextbook;
import org.apache.ibatis.builder.xml.XMLMapperBuilder;
import org.apache.ibatis.datasource.unpooled.UnpooledDataSource;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.jdbc.core.JdbcTemplate;

import java.io.InputStream;
import java.util.Arrays;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/** Real MyBatis XML against an isolated in-memory database; no application bootstrap or live writes. */
class TeachingPlanTextbookTest {
    @Test
    void fullSchoolExportQueryIsRegistered() throws Exception {
        Configuration configuration = new Configuration();
        try (InputStream xml = getClass().getResourceAsStream("/mapper/csys/TeachingPlanTextbookMapper.xml")) {
            new XMLMapperBuilder(xml, configuration, "TeachingPlanTextbookMapper.xml", configuration.getSqlFragments()).parse();
        }
        assertTrue(configuration.hasStatement("com.doinner.csys.dao.TeachingPlanTextbookMapper.selectForExport"));
    }

    @Test
    void optionalFieldsRoundTripThroughSingleBatchAndUpdateIncludingClearing() throws Exception {
        UnpooledDataSource source = new UnpooledDataSource("org.h2.Driver",
                "jdbc:h2:mem:" + UUID.randomUUID() + ";MODE=MySQL;DB_CLOSE_DELAY=-1", "sa", "");
        new JdbcTemplate(source).execute("CREATE TABLE t_csys_teaching_plan_textbook ("
                + "id BIGINT AUTO_INCREMENT PRIMARY KEY, plan_id BIGINT, material_nature VARCHAR(100),"
                + "name VARCHAR(255), first_author VARCHAR(255), edition VARCHAR(100), publisher VARCHAR(255),"
                + "publish_time VARCHAR(100), isbn VARCHAR(100), publish_method VARCHAR(100),"
                + "security_classification VARCHAR(100), political_theory_textbook VARCHAR(100), textbook_source VARCHAR(100),"
                + "sort INT, creator VARCHAR(64), create_time TIMESTAMP, last_modifier VARCHAR(64),"
                + "last_modified_time TIMESTAMP, remark VARCHAR(500), sysflag INT DEFAULT 0)");
        Configuration configuration = new Configuration(new Environment("textbook-test", new JdbcTransactionFactory(), source));
        try (InputStream xml = getClass().getResourceAsStream("/mapper/csys/TeachingPlanTextbookMapper.xml")) {
            assertNotNull(xml);
            new XMLMapperBuilder(xml, configuration, "TeachingPlanTextbookMapper.xml", configuration.getSqlFragments()).parse();
        }
        try (SqlSession session = new SqlSessionFactoryBuilder().build(configuration).openSession(true)) {
            TeachingPlanTextbookMapper mapper = session.getMapper(TeachingPlanTextbookMapper.class);
            TeachingPlanTextbook single = book(101L, "单条");
            setExtras(single, "2", "1", "3");
            assertEquals(1, mapper.insert(single));
            assertNotNull(single.getId());
            assertExtras(mapper.selectById(single.getId()), "2", "1", "3");
            setExtras(single, "", "", "");
            assertEquals(1, mapper.updateById(single));
            assertExtras(mapper.selectById(single.getId()), "", "", "");
            TeachingPlanTextbook batch = book(102L, "批量");
            setExtras(batch, "4", "2", "1");
            TeachingPlanTextbook optional = book(102L, "未填可选项");
            mapper.insertBatch(Arrays.asList(batch, optional));
            assertExtras(mapper.selectByPlanId(102L).get(0), "4", "2", "1");
            assertExtras(mapper.selectByPlanId(102L).get(1), null, null, null);
            mapper.deleteById(single.getId());
            assertNull(mapper.selectById(single.getId()));
        }
    }

    private TeachingPlanTextbook book(Long planId, String name) {
        TeachingPlanTextbook book = new TeachingPlanTextbook();
        book.setPlanId(planId);
        book.setName(name);
        book.setSort(1);
        new BeanWrapperImpl(book).setPropertyValue("sysflag", 0);
        return book;
    }

    private void setExtras(TeachingPlanTextbook book, String security, String political, String source) {
        BeanWrapper properties = new BeanWrapperImpl(book);
        properties.setPropertyValue("securityClassification", security);
        properties.setPropertyValue("politicalTheoryTextbook", political);
        properties.setPropertyValue("textbookSource", source);
    }

    private void assertExtras(TeachingPlanTextbook book, String security, String political, String source) {
        assertNotNull(book);
        BeanWrapper properties = new BeanWrapperImpl(book);
        assertEquals(security, properties.getPropertyValue("securityClassification"));
        assertEquals(political, properties.getPropertyValue("politicalTheoryTextbook"));
        assertEquals(source, properties.getPropertyValue("textbookSource"));
    }
}
