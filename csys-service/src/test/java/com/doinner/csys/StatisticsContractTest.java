package com.doinner.csys;

import com.doinner.csys.domain.vo.TrainingSchemeScheduleVo;
import com.doinner.csys.domain.statisticsVo.CourseModuleStatisticsVo;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.ibatis.builder.xml.XMLMapperBuilder;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.type.StringTypeHandler;
import org.junit.jupiter.api.Test;
import java.io.InputStream;
import java.sql.ResultSet;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class StatisticsContractTest {
    @Test
    void scheduleModuleIdUsesStringMappingAndJson() throws Exception {
        Configuration configuration = new Configuration();
        configuration.getTypeAliasRegistry().registerAliases("com.doinner.csys.domain");
        String resource = "mapper/csys/TrainingSchemeMapper.xml";
        try (InputStream input = getClass().getClassLoader().getResourceAsStream(resource)) {
            new XMLMapperBuilder(input, configuration, resource, configuration.getSqlFragments()).parse();
        }
        var mapping = configuration.getResultMap("com.doinner.csys.dao.TrainingSchemeMapper.TrainingSchemeScheduleResult")
                .getResultMappings().stream().filter(m -> "type".equals(m.getProperty())).findFirst().orElseThrow();
        assertTrue(mapping.getTypeHandler() instanceof StringTypeHandler);
        ResultSet rs = mock(ResultSet.class);
        String moduleId = "69a7f3162dc370362ef3ee6d";
        when(rs.getString("type")).thenReturn(moduleId);
        TrainingSchemeScheduleVo vo = new TrainingSchemeScheduleVo();
        vo.setType((String) mapping.getTypeHandler().getResult(rs, "type"));
        var json = new ObjectMapper().readTree(new ObjectMapper().writeValueAsString(vo));
        assertTrue(json.get("type").isTextual());
        assertEquals(moduleId, json.get("type").asText());
    }

    @Test
    void moduleProportionHandlesEmptyAndNormalTotals() {
        CourseModuleStatisticsVo vo = new CourseModuleStatisticsVo();
        assertEquals(0.0, vo.getProportion());
        vo.setCourseCount(2L);
        vo.setTotalCourseCount(8L);
        assertEquals(0.25, vo.getProportion());
    }
}
