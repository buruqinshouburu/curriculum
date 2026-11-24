-- MySQL 8.0+. Execute in the curriculum database. No business data is modified.
-- See 00-README.md for the external KG dictionary and template limitations.
SET NAMES utf8mb4;

CREATE OR REPLACE ALGORITHM = UNDEFINED
SQL SECURITY INVOKER VIEW v_csys_scheme_hours_credits AS
WITH
direction_ids AS (
    SELECT DISTINCT scheme_id, major_id FROM t_csys_scheme_ref_major
    UNION
    SELECT s.id, s.sub_major_id FROM t_csys_training_scheme s
    WHERE NOT EXISTS (SELECT 1 FROM t_csys_scheme_ref_major r WHERE r.scheme_id = s.id)
),
schemes AS (
    SELECT CONCAT(s.id, ':', COALESCE(CAST(d.major_id AS CHAR), 'none')) AS id,
           s.id AS original_id, s.name, s.version, s.college_id, s.education_level, s.major_id,
           d.major_id AS direction_id,
           COALESCE(NULLIF(m.code, ''), CAST(d.major_id AS CHAR)) AS direction_code,
           COALESCE(NULLIF(m.name, ''), CAST(d.major_id AS CHAR), '未设置专业方向') AS direction_name
    FROM t_csys_training_scheme s LEFT JOIN direction_ids d ON d.scheme_id = s.id
    LEFT JOIN t_csys_std_major m ON m.id = d.major_id AND m.sysflag = 0
    WHERE s.sysflag = 0
),
education AS (
    SELECT CONVERT(dict_value USING utf8mb4) COLLATE utf8mb4_unicode_ci AS value,
           MIN(NULLIF(dict_label, '')) AS label
    FROM sys_dict_data WHERE dict_type = 'sys_education_level'
    GROUP BY CONVERT(dict_value USING utf8mb4) COLLATE utf8mb4_unicode_ci
),
module_names AS (
    SELECT '69a7f32e2dc370362ef3ee6e' COLLATE utf8mb4_unicode_ci AS id, '公共基础课程' AS name
    UNION ALL SELECT '69a7f3342dc370362ef3ee6f', '学科基础课程'
    UNION ALL SELECT '69a7f33c2dc370362ef3ee70', '专业课程'
    UNION ALL SELECT '69a7f3992dc370362ef3ee73', '科学文化'
    UNION ALL SELECT '6a50c7bdc55a5223d91a753a', '军事职业教育'
    UNION ALL SELECT '6a50c7eec55a5223d91a753b', '政治理论'
    UNION ALL SELECT '6a50c843c55a5223d91a753c', '军事基础'
    UNION ALL SELECT '6a50c85bc55a5223d91a753d', '任职基础'
    UNION ALL SELECT '6a50c864c55a5223d91a753e', '任职岗位'
),
nco_modules AS (
    SELECT '6a50c7eec55a5223d91a753b' COLLATE utf8mb4_unicode_ci AS id, '政治理论课程' AS name, 10 AS seq
    UNION ALL SELECT '6a50c843c55a5223d91a753c', '军事基础课程', 20
    UNION ALL SELECT '6a50c85bc55a5223d91a753d', '任职基础课程', 30
    UNION ALL SELECT '6a50c864c55a5223d91a753e', '任职岗位课程', 40
),
courses AS (
    -- Deduplicate relations, not distinct historical copies of a course.
    SELECT s.id AS scheme_id, c.id AS course_id, c.type, c.course_attr,
           CONVERT(c.course_Module USING utf8mb4) COLLATE utf8mb4_unicode_ci AS parent_id,
           CONVERT(COALESCE(NULLIF(c.course_Module_Children, ''), c.course_Module) USING utf8mb4)
             COLLATE utf8mb4_unicode_ci AS module_id,
           COALESCE(t.hours, 0) AS hours, COALESCE(t.weeks, 0) AS weeks,
           COALESCE(t.credits, 0) AS credits
    FROM (SELECT DISTINCT scheme_id, course_id FROM t_csys_training_scheme_ref_course) r
    JOIN schemes s ON s.original_id = r.scheme_id
    JOIN t_csys_course c ON c.id = r.course_id AND c.sysflag = 0
    JOIN v_csys_course_time_credit t ON t.course_id = c.id
    WHERE s.direction_id IS NULL OR (
      CASE WHEN c.invocation_sub_major_ids IS NOT NULL
           THEN FIND_IN_SET(CAST(s.direction_id AS CHAR), REPLACE(c.invocation_sub_major_ids, ' ', '')) > 0
           WHEN c.sub_major_id IS NOT NULL THEN c.sub_major_id = s.direction_id
           ELSE (
             (NOT COALESCE((c.type IN (2,4) OR c.course_Module = '69a7f33c2dc370362ef3ee70'
               OR (c.course_Module = '6a50c7bdc55a5223d91a753a' AND c.course_Module_Children IN ('6a50c85bc55a5223d91a753d','6a50c864c55a5223d91a753e'))), FALSE)
               OR c.major_id IS NULL OR s.major_id IS NULL OR c.major_id = s.major_id)
             AND NOT EXISTS (
               SELECT 1 FROM t_csys_training_scheme_ref_course xr
               JOIN t_csys_course x ON x.id = xr.course_id AND x.sysflag = 0
               WHERE xr.scheme_id = r.scheme_id AND x.source_id = c.source_id
                 AND x.major_id <=> c.major_id AND x.course_Module <=> c.course_Module
                 AND x.course_Module_Children <=> c.course_Module_Children
                 AND x.location <=> c.location AND x.program_Level <=> c.program_Level
                 AND CASE WHEN x.invocation_sub_major_ids IS NOT NULL
                          THEN FIND_IN_SET(CAST(s.direction_id AS CHAR), REPLACE(x.invocation_sub_major_ids, ' ', '')) > 0
                          ELSE x.sub_major_id = s.direction_id END
             )
           ) END
    )
),
classified AS (
    SELECT c.*,
           CASE WHEN c.type IN (2, 4) THEN 'practice'
                WHEN parent_id = '69a7f32e2dc370362ef3ee6e' THEN 'general'
                WHEN parent_id = '69a7f3342dc370362ef3ee6f' THEN 'discipline'
                WHEN parent_id = '69a7f33c2dc370362ef3ee70' THEN 'major'
                WHEN parent_id = '6a50c7bdc55a5223d91a753a'
                  OR parent_id IN (SELECT id FROM nco_modules) THEN 'nco'
                ELSE 'unknown' END AS category
    FROM courses c
),
regular_values AS (
    SELECT c.scheme_id, c.category,
           CASE WHEN category IN ('practice', 'discipline', 'major') THEN ''
                ELSE COALESCE(module_id, '') END AS module_id,
           SUM(CASE WHEN course_attr = '1' THEN hours ELSE 0 END) AS required_hours,
           SUM(CASE WHEN course_attr = '1' THEN credits ELSE 0 END) AS required_credits,
           SUM(CASE WHEN course_attr = '1' THEN 0 ELSE hours END) AS optional_hours,
           SUM(CASE WHEN course_attr = '1' THEN 0 ELSE credits END) AS optional_credits,
           SUM(CASE WHEN course_attr = '1' THEN weeks ELSE 0 END) AS required_weeks,
           SUM(CASE WHEN course_attr = '1' THEN 0 ELSE weeks END) AS optional_weeks
    FROM classified c JOIN schemes s ON s.id = c.scheme_id
    WHERE COALESCE(s.education_level, '') <> '3'
    GROUP BY c.scheme_id, c.category,
             CASE WHEN category IN ('practice', 'discipline', 'major') THEN '' ELSE COALESCE(module_id, '') END
),
regular_keys AS (
    SELECT scheme_id, category, module_id FROM regular_values
    UNION
    SELECT id, 'discipline', '' FROM schemes WHERE COALESCE(education_level, '') <> '3'
    UNION
    SELECT id, 'major', '' FROM schemes WHERE COALESCE(education_level, '') <> '3'
    UNION
    SELECT id, 'practice', '' FROM schemes WHERE COALESCE(education_level, '') <> '3'
),
regular_details AS (
    SELECT k.scheme_id, k.category, k.module_id,
           COALESCE(v.required_hours, 0) AS rh, COALESCE(v.required_credits, 0) AS rc,
           COALESCE(v.optional_hours, 0) AS oh, COALESCE(v.optional_credits, 0) AS oc,
           COALESCE(v.required_weeks, 0) AS rw, COALESCE(v.optional_weeks, 0) AS ow
    FROM regular_keys k LEFT JOIN regular_values v
      ON v.scheme_id = k.scheme_id AND v.category = k.category AND v.module_id = k.module_id
),
regular_rows AS (
    SELECT d.scheme_id,
           CASE category WHEN 'practice' THEN 900 WHEN 'discipline' THEN 300 WHEN 'major' THEN 400
                         WHEN 'nco' THEN 200 WHEN 'unknown' THEN 600 ELSE 100 END AS seq,
           CASE WHEN category = 'practice' THEN '小计' ELSE '明细' END AS row_kind,
           CASE category WHEN 'practice' THEN '实践训练体系' ELSE '课程体系' END AS system_name,
           CASE category WHEN 'general' THEN '公共基础课程' WHEN 'nco' THEN '军事职业教育'
                         WHEN 'discipline' THEN '学科专业课程' WHEN 'major' THEN '学科专业课程'
                         WHEN 'unknown' THEN '未识别课程模块' ELSE '实践训练体系' END AS group_name,
           CASE category WHEN 'practice' THEN '小计' WHEN 'discipline' THEN '学科基础课程'
                         WHEN 'major' THEN '专业课程' ELSE COALESCE(m.name, NULLIF(d.module_id, ''), '未设置模块') END AS module_name,
           d.module_id, rh, rc, oh, oc, rw, ow
    FROM regular_details d LEFT JOIN module_names m ON m.id = d.module_id
    UNION ALL
    SELECT scheme_id, 800, '小计', '课程体系', '课程体系', '小计', '',
           SUM(rh), SUM(rc), SUM(oh), SUM(oc), SUM(rw), SUM(ow)
    FROM regular_details WHERE category <> 'practice' GROUP BY scheme_id
    UNION ALL
    SELECT scheme_id, 1000, '合计', '合计', '合计', '合计', '',
           SUM(rh), SUM(rc), SUM(oh), SUM(oc), SUM(rw), SUM(ow)
    FROM regular_details GROUP BY scheme_id
),
nco_values AS (
    SELECT c.scheme_id, c.module_id,
           SUM(CASE WHEN course_attr = '1' THEN hours ELSE 0 END) AS rh,
           SUM(CASE WHEN course_attr = '1' THEN 0 ELSE hours END) AS oh
    FROM classified c JOIN schemes s ON s.id = c.scheme_id
    WHERE s.education_level = '3' AND c.category = 'nco'
    GROUP BY c.scheme_id, c.module_id
),
nco_details AS (
    SELECT s.id AS scheme_id, m.seq, m.name AS module_name, m.id AS module_id,
           COALESCE(v.rh, 0) AS rh, COALESCE(v.oh, 0) AS oh
    FROM schemes s CROSS JOIN nco_modules m
    LEFT JOIN nco_values v ON v.scheme_id = s.id AND v.module_id = m.id
    WHERE s.education_level = '3'
),
nco_rows AS (
    SELECT scheme_id, seq, '明细' AS row_kind, module_name, module_id, rh, oh FROM nco_details
    UNION ALL
    SELECT scheme_id, 1000, '合计', '总计', '', SUM(rh), SUM(oh)
    FROM nco_details GROUP BY scheme_id
),
display_rows AS (
    SELECT scheme_id, '通用模板' AS template_name, seq, row_kind, system_name, group_name, module_name, module_id,
           ROUND(rh, 2) AS required_hours, CAST(ROUND(rc, 2) AS CHAR) AS required_credits,
           ROUND(oh, 2) AS optional_hours, CAST(ROUND(oc, 2) AS CHAR) AS optional_credits,
           ROUND(rh + oh, 2) AS total_hours, CAST(ROUND(rc + oc, 2) AS CHAR) AS total_credits,
           CAST(NULL AS DECIMAL(12, 6)) AS ratio, ROUND(rw, 2) AS required_weeks, ROUND(ow, 2) AS optional_weeks
    FROM regular_rows
    UNION ALL
    SELECT n.scheme_id, '军士模板', n.seq, n.row_kind, '课程体系', '军事职业教育', n.module_name, n.module_id,
           ROUND(n.rh, 2), CASE WHEN n.row_kind = '合计' THEN '0' ELSE '/' END,
           NULL, NULL, ROUND(n.rh + n.oh, 2), CASE WHEN n.row_kind = '合计' THEN '0' ELSE '/' END,
           ROUND((n.rh + n.oh) / NULLIF(t.rh + t.oh, 0), 6), 0, 0
    FROM nco_rows n JOIN nco_rows t ON t.scheme_id = n.scheme_id AND t.row_kind = '合计'
)
SELECT s.original_id AS `培养方案ID`, s.name AS `培养方案名称`, s.version AS `年度版本`,
       COALESCE(NULLIF(dept.dept_name, ''), CAST(s.college_id AS CHAR)) AS `学院`,
       s.direction_id AS `专业方向ID`, s.direction_name AS `专业方向`,
       COALESCE(e.label, s.education_level) AS `选用对象`,
       d.template_name AS `模板类型`, d.seq AS `行排序`, d.row_kind AS `行类型`,
       d.system_name AS `体系`, d.group_name AS `课程分类`, d.module_id AS `模块ID`, d.module_name AS `模块或科目`,
       d.required_hours AS `必修学时`, d.required_credits AS `必修学分`,
       d.optional_hours AS `选修学时`, d.optional_credits AS `选修学分`,
       d.total_hours AS `小计学时`, d.total_credits AS `小计学分`, d.ratio AS `学时比例`,
       '学时' AS `学时单位`, d.required_weeks AS `必修周数`, d.optional_weeks AS `选修周数`,
       d.required_weeks + d.optional_weeks AS `小计周数`, '周' AS `周数单位`,
       s.direction_code AS `专业方向编号`
FROM display_rows d JOIN schemes s ON s.id = d.scheme_id
LEFT JOIN sys_dept dept ON dept.dept_id = s.college_id
LEFT JOIN education e ON e.value = CONVERT(s.education_level USING utf8mb4) COLLATE utf8mb4_unicode_ci;
