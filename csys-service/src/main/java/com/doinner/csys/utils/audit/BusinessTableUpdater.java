package com.doinner.csys.utils.audit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class BusinessTableUpdater {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private final Map<String, String> tableIdFieldCache = new HashMap<>();

    public void updateBusinessStatus(String tableName, String idField, String statusField, Long businessId, Integer status) {
        String cacheKey = tableName + "_" + idField;

        String sql = String.format(
                "UPDATE %s SET %s = ? WHERE %s = ?",
                tableName, statusField, idField
        );

        jdbcTemplate.update(sql, status, businessId);
    }
}