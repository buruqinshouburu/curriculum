package com.doinner.csys.constant;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 课程列表排序字段白名单。
 * <p>
 * 前端传入的排序字段名为 Java 属性名(prop)，此处维护其到数据库列名/表达式的映射，
 * 用于在 mapper 中以 ${} 拼接到 order by 子句。白名单机制同时保证：
 * 1. 仅允许的字段可参与排序，未登记的字段一律忽略（回退默认排序）；
 * 2. 拼接内容为服务端硬编码的列名，杜绝 SQL 注入。
 */
public final class CourseSortField {

    /** 是否需要关联 sys_dept 表（按学院名称排序时为 true） */
    public static final String COLLEGE_NAME = "collegeName";
    public static final String COLLEGE_ID = "collegeId";
    public static final String CODE = "code";

    /**
     * prop(Java属性名) -> 数据库排序表达式。
     * collegeName 需关联 sys_dept，这里给出 join 后的列别名 dept_name。
     */
    private static final Map<String, String> COLUMN_MAP;

    static {
        Map<String, String> m = new HashMap<>();
        m.put(COLLEGE_ID, "college_id");
        m.put(COLLEGE_NAME, "dept_name");
        m.put(CODE, "code");
        COLUMN_MAP = Collections.unmodifiableMap(m);
    }

    /** 需要关联 sys_dept 的排序字段 */
    private static final Set<String> NEED_JOIN_DEPT = Collections.singleton(COLLEGE_NAME);

    private CourseSortField() {
    }

    /**
     * 是否为已登记的合法排序字段。
     */
    public static boolean isSupported(String prop) {
        return prop != null && COLUMN_MAP.containsKey(prop);
    }

    /**
     * 将 prop 翻译为安全的数据库排序表达式。未登记返回 null。
     */
    public static String toColumn(String prop) {
        return prop == null ? null : COLUMN_MAP.get(prop);
    }

    /**
     * 该排序字段是否需要关联 sys_dept 表。
     */
    public static boolean needJoinDept(String prop) {
        return prop != null && NEED_JOIN_DEPT.contains(prop);
    }
}
