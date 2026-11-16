package com.doinner.csys.handler;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/** Keeps the API's numeric arrays separate from the legacy single-ID columns. */
public class CourseMajorIdsTypeHandler extends BaseTypeHandler<List<Long>> {
    @Override
    public void setNonNullParameter(PreparedStatement ps, int index, List<Long> ids, JdbcType type) throws SQLException {
        if (ids.stream().anyMatch(id -> id == null || id <= 0)) {
            throw new SQLException("Professional IDs must be positive integers");
        }
        ps.setString(index, ids.stream().distinct().map(String::valueOf).collect(Collectors.joining(",")));
    }

    private List<Long> parse(String value) throws SQLException {
        if (value == null) return null;
        List<Long> ids = new ArrayList<>();
        if (value.isEmpty()) return ids;
        try {
            for (String part : value.split(",")) {
                long id = Long.parseLong(part);
                if (id <= 0) throw new NumberFormatException("Nonpositive ID");
                if (!ids.contains(id)) ids.add(id);
            }
        } catch (NumberFormatException error) {
            throw new SQLException("Invalid stored professional IDs", error);
        }
        return ids;
    }

    @Override
    public List<Long> getNullableResult(ResultSet rs, String column) throws SQLException { return parse(rs.getString(column)); }
    @Override
    public List<Long> getNullableResult(ResultSet rs, int column) throws SQLException { return parse(rs.getString(column)); }
    @Override
    public List<Long> getNullableResult(CallableStatement cs, int column) throws SQLException { return parse(cs.getString(column)); }
}
