-- MySQL 8.0+. Run in the curriculum database after reviewing 00-README.md.
-- Only creates/replaces this view; no business records are changed.
-- If system tables are in another database, qualify sys_dict_data and sys_dept below.
SET NAMES utf8mb4;

CREATE OR REPLACE ALGORITHM = UNDEFINED
SQL SECURITY INVOKER VIEW v_csys_training_scheme_import AS
WITH
course_refs AS (
    SELECT DISTINCT scheme_id, course_id
    FROM t_csys_training_scheme_ref_course
),
direction_ids AS (
    SELECT DISTINCT scheme_id, major_id FROM t_csys_scheme_ref_major
    UNION
    SELECT s.id, s.sub_major_id FROM t_csys_training_scheme s
    WHERE NOT EXISTS (SELECT 1 FROM t_csys_scheme_ref_major r WHERE r.scheme_id = s.id)
),
scheme_directions AS (
    SELECT s.id AS scheme_id, d.major_id AS direction_id,
           COALESCE(NULLIF(m.code, ''), CAST(d.major_id AS CHAR)) AS direction_code,
           COALESCE(NULLIF(m.name, ''), CAST(d.major_id AS CHAR), '未设置专业方向') AS direction_name
    FROM t_csys_training_scheme s LEFT JOIN direction_ids d ON d.scheme_id = s.id
    LEFT JOIN t_csys_std_major m ON m.id = d.major_id AND m.sysflag = 0
    WHERE s.sysflag = 0
),
direction_courses AS (
    -- Same scope as hours_credits: keep historical copies, deduplicate relations only.
    SELECT d.scheme_id, d.direction_id, d.direction_code, d.direction_name, c.id AS course_id
    FROM course_refs r
    JOIN scheme_directions d ON d.scheme_id = r.scheme_id
    JOIN t_csys_training_scheme ts ON ts.id = d.scheme_id
    JOIN t_csys_course c ON c.id = r.course_id AND c.sysflag = 0
    WHERE d.direction_id IS NULL OR (
      CASE WHEN c.invocation_sub_major_ids IS NOT NULL
           THEN FIND_IN_SET(CAST(d.direction_id AS CHAR), REPLACE(c.invocation_sub_major_ids, ' ', '')) > 0
           WHEN c.sub_major_id IS NOT NULL THEN c.sub_major_id = d.direction_id
           ELSE (
             (NOT COALESCE((c.type IN (2,4) OR c.course_Module = '69a7f33c2dc370362ef3ee70'
               OR (c.course_Module = '6a50c7bdc55a5223d91a753a' AND c.course_Module_Children IN ('6a50c85bc55a5223d91a753d','6a50c864c55a5223d91a753e'))), FALSE)
               OR c.major_id IS NULL OR ts.major_id IS NULL OR c.major_id = ts.major_id)
             AND NOT EXISTS (
               SELECT 1 FROM t_csys_training_scheme_ref_course xr
               JOIN t_csys_course x ON x.id = xr.course_id AND x.sysflag = 0
               WHERE xr.scheme_id = r.scheme_id AND x.source_id = c.source_id
                 AND x.major_id <=> c.major_id AND x.course_Module <=> c.course_Module
                 AND x.course_Module_Children <=> c.course_Module_Children
                 AND x.location <=> c.location AND x.program_Level <=> c.program_Level
                 AND CASE WHEN x.invocation_sub_major_ids IS NOT NULL
                          THEN FIND_IN_SET(CAST(d.direction_id AS CHAR), REPLACE(x.invocation_sub_major_ids, ' ', '')) > 0
                          ELSE x.sub_major_id = d.direction_id END
             )
           ) END
    )
),
credit_requirements AS (
    -- Actual syllabus requirements, never the sum of selected course credits.
    SELECT ts.id AS scheme_id, d.direction_id,
           GROUP_CONCAT(DISTINCT NULLIF(TRIM(e.total_Credit), '')
                        ORDER BY NULLIF(TRIM(e.total_Credit), '') SEPARATOR '、') AS required_credits
    FROM t_csys_training_scheme ts
    JOIN scheme_directions d ON d.scheme_id = ts.id
    JOIN t_csys_teaching_programme_instance_extract e
      ON e.instance_Id = ts.instance_id AND e.sysflag = 0
    WHERE ts.sysflag = 0 AND (
        EXISTS (
            SELECT 1 FROM t_csys_teaching_programme_instance_ex_ref_major er
            WHERE er.extract_id = e.id AND (
                er.major_id = ts.major_id OR er.major_id = d.direction_id
            )
        ) OR (
            e.category_Id = ts.category_id AND NOT EXISTS (
                SELECT 1 FROM t_csys_teaching_programme_instance_ex_ref_major er
                WHERE er.extract_id = e.id
            )
        )
    )
    GROUP BY ts.id, d.direction_id
),
dict_values AS (
    -- One row per key prevents duplicate dictionary entries multiplying courses.
    SELECT CONVERT(dict_type USING utf8mb4) COLLATE utf8mb4_unicode_ci AS dict_type,
           CONVERT(dict_value USING utf8mb4) COLLATE utf8mb4_unicode_ci AS dict_value,
           MIN(NULLIF(dict_label, '')) AS dict_label
    FROM sys_dict_data
    WHERE dict_type IN ('cur_exam_format', 'cur_course_attribute', 'sys_subject_module', 'cur_program_level')
    GROUP BY CONVERT(dict_type USING utf8mb4) COLLATE utf8mb4_unicode_ci,
             CONVERT(dict_value USING utf8mb4) COLLATE utf8mb4_unicode_ci
),
module_names AS (
    -- Confirmed backend mappings: entity/csys/model/DictContent.java.
    -- KG cascading dictionaries live outside this SQL database; unknown IDs remain IDs.
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
term_hours AS (
    SELECT s.scheme_id, d.direction_id, s.course_id, s.term,
           CASE WHEN MAX(CASE WHEN c.type IN (2, 4) AND COALESCE(c.unit, '1') <> '2' THEN 1 ELSE 0 END) = 1 THEN NULL
                WHEN COUNT(*) = COUNT(CASE
                    WHEN s.teach_hours IS NOT NULL OR s.practice_hours IS NOT NULL THEN 1 END)
                THEN ROUND(SUM(COALESCE(s.teach_hours, 0) + COALESCE(s.practice_hours, 0)), 2)
                ELSE NULL END AS hours
    FROM t_csys_training_scheme_course_schedule s
    JOIN direction_courses d ON d.scheme_id = s.scheme_id AND d.course_id = s.course_id
    JOIN t_csys_course c ON c.id = s.course_id AND c.sysflag = 0
    WHERE s.sysflag = 0 AND s.checked = 1 AND s.term BETWEEN 1 AND 10
    GROUP BY s.scheme_id, d.direction_id, s.course_id, s.term
),
schedule AS (
    SELECT scheme_id, direction_id, course_id,
           MAX(CASE WHEN term = 1 THEN term END) AS term1, MAX(CASE WHEN term = 1 THEN hours END) AS hours1,
           MAX(CASE WHEN term = 2 THEN term END) AS term2, MAX(CASE WHEN term = 2 THEN hours END) AS hours2,
           MAX(CASE WHEN term = 3 THEN term END) AS term3, MAX(CASE WHEN term = 3 THEN hours END) AS hours3,
           MAX(CASE WHEN term = 4 THEN term END) AS term4, MAX(CASE WHEN term = 4 THEN hours END) AS hours4,
           MAX(CASE WHEN term = 5 THEN term END) AS term5, MAX(CASE WHEN term = 5 THEN hours END) AS hours5,
           MAX(CASE WHEN term = 6 THEN term END) AS term6, MAX(CASE WHEN term = 6 THEN hours END) AS hours6,
           MAX(CASE WHEN term = 7 THEN term END) AS term7, MAX(CASE WHEN term = 7 THEN hours END) AS hours7,
           MAX(CASE WHEN term = 8 THEN term END) AS term8, MAX(CASE WHEN term = 8 THEN hours END) AS hours8,
           MAX(term) AS last_term
    FROM term_hours
    GROUP BY scheme_id, direction_id, course_id
)
SELECT
    COALESCE(NULLIF(sd.dept_name, ''), CAST(ts.college_id AS CHAR)) AS `学院`,
    r.direction_code AS `专业号`,
    r.direction_name AS `专业名称`,
    ts.version AS `年度版本`,
    ts.name AS `培养方案名称`,
    cr.required_credits AS `专业总学分要求`,
    c.code AS `课程编号`,
    c.name AS `课程名称`,
    COALESCE(NULLIF(cd.dept_name, ''), CAST(c.college_id AS CHAR)) AS `开课单位`,
    t.credits AS `学分`,
    CASE WHEN c.type IN (1, 3) AND (c.teach_hours IS NOT NULL OR c.practice_hours IS NOT NULL)
         THEN ROUND(COALESCE(c.teach_hours, 0) + COALESCE(c.practice_hours, 0), 2)
         WHEN c.type IN (2, 4) THEN ROUND(c.time_Week, 2)
         ELSE NULL END AS `总学时`,
    t.unit_name AS `单位`,
    t.hours AS `实际学时`, t.weeks AS `周数`,
    COALESCE(exam.dict_label, NULLIF(c.exa_Method, '')) AS `考核方式`,
    CASE WHEN c.type = 2 THEN '实践训练'
         WHEN c.type = 4 THEN '实践项目'
         ELSE COALESCE(parent_module.name, NULLIF(c.course_Module, '')) END AS `课程性质`,
    COALESCE(attr.dict_label, CAST(c.course_attr AS CHAR)) AS `课程属性`,
    CASE WHEN c.type = 2 THEN '实践环节'
         WHEN c.type = 4 THEN '项目'
         WHEN COALESCE(c.teach_hours, 0) > 0 AND COALESCE(c.practice_hours, 0) > 0 THEN '理论含实践'
         WHEN COALESCE(c.teach_hours, 0) > 0 THEN '理论课'
         WHEN COALESCE(c.practice_hours, 0) > 0 THEN '实践课'
         ELSE NULL END AS `课程类别`,
    CASE WHEN c.type = 2 THEN COALESCE(training_module.dict_label, NULLIF(c.location, ''))
         WHEN c.type = 4 THEN COALESCE(project_level.dict_label, NULLIF(c.program_Level, ''))
         WHEN NULLIF(NULLIF(c.course_Module_Children, ''), '-1') IS NOT NULL
              THEN COALESCE(child_module.name, c.course_Module_Children)
         ELSE COALESCE(parent_module.name, NULLIF(c.course_Module, '')) END AS `课程体系`,
    c.teach_hours AS `讲授学时`,
    c.practice_hours AS `实践学时`,
    c.examine_hours AS `考核`,
    c.other_hours AS `其他`,
    sch.term1 AS `开课学期1`, sch.hours1 AS `学期学时1`,
    sch.term2 AS `开课学期2`, sch.hours2 AS `学期学时2`,
    sch.term3 AS `开课学期3`, sch.hours3 AS `学期学时3`,
    sch.term4 AS `开课学期4`, sch.hours4 AS `学期学时4`,
    sch.term5 AS `开课学期5`, sch.hours5 AS `学期学时5`,
    sch.term6 AS `开课学期6`, sch.hours6 AS `学期学时6`,
    sch.term7 AS `开课学期7`, sch.hours7 AS `学期学时7`,
    sch.term8 AS `开课学期8`, sch.hours8 AS `学期学时8`,
    sch.last_term AS `成绩录入学期（最后一个学期）`,
    ts.id AS `培养方案ID`, r.direction_id AS `专业方向ID`,
    r.direction_name AS `专业方向`, c.id AS `课程ID`
FROM direction_courses r
JOIN t_csys_training_scheme ts ON ts.id = r.scheme_id AND ts.sysflag = 0
JOIN t_csys_course c ON c.id = r.course_id AND c.sysflag = 0
JOIN v_csys_course_time_credit t ON t.course_id = c.id
LEFT JOIN sys_dept sd ON sd.dept_id = ts.college_id
LEFT JOIN sys_dept cd ON cd.dept_id = c.college_id
LEFT JOIN credit_requirements cr ON cr.scheme_id = ts.id AND cr.direction_id <=> r.direction_id
LEFT JOIN schedule sch ON sch.scheme_id = ts.id AND sch.course_id = c.id AND sch.direction_id <=> r.direction_id
LEFT JOIN module_names parent_module
  ON parent_module.id = CONVERT(c.course_Module USING utf8mb4) COLLATE utf8mb4_unicode_ci
LEFT JOIN module_names child_module
  ON child_module.id = CONVERT(c.course_Module_Children USING utf8mb4) COLLATE utf8mb4_unicode_ci
LEFT JOIN dict_values exam ON exam.dict_type = 'cur_exam_format'
  AND exam.dict_value = CONVERT(c.exa_Method USING utf8mb4) COLLATE utf8mb4_unicode_ci
LEFT JOIN dict_values attr ON attr.dict_type = 'cur_course_attribute'
  AND attr.dict_value = CONVERT(c.course_attr USING utf8mb4) COLLATE utf8mb4_unicode_ci
LEFT JOIN dict_values training_module ON training_module.dict_type = 'sys_subject_module'
  AND training_module.dict_value = CONVERT(c.location USING utf8mb4) COLLATE utf8mb4_unicode_ci
LEFT JOIN dict_values project_level ON project_level.dict_type = 'cur_program_level'
  AND project_level.dict_value = CONVERT(c.program_Level USING utf8mb4) COLLATE utf8mb4_unicode_ci;
