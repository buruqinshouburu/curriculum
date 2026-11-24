-- Read-only checks. Run after 01, 02 and 03 in the business database.
SELECT DATABASE() AS current_database, VERSION() AS mysql_version;
SHOW COLUMNS FROM t_csys_course LIKE 'invocation_sub_major_ids';
SHOW CREATE VIEW v_csys_course_time_credit;
SHOW CREATE VIEW v_csys_scheme_hours_credits;
SHOW CREATE VIEW v_csys_training_scheme_import;

SELECT * FROM v_csys_scheme_hours_credits
ORDER BY `培养方案ID`, `专业方向ID`, `行排序`, `模块ID`;
SELECT * FROM v_csys_training_scheme_import
ORDER BY `培养方案ID`, `专业方向ID`, `课程编号`, `课程ID`;

-- No rows expected: one active scheme/direction/course combination = one import row.
SELECT `培养方案ID`, `专业方向ID`, `课程ID`, COUNT(*) AS duplicate_count
FROM v_csys_training_scheme_import
GROUP BY `培养方案ID`, `专业方向ID`, `课程ID` HAVING COUNT(*) > 1;
SELECT COUNT(*) AS actual_import_rows FROM v_csys_training_scheme_import;
SELECT `培养方案ID`, `专业方向ID`, `专业方向`, COUNT(*) AS course_count,
       SUM(`实际学时`) AS total_hours, SUM(`周数`) AS total_weeks, SUM(`学分`) AS total_credits
FROM v_csys_training_scheme_import
GROUP BY `培养方案ID`, `专业方向ID`, `专业方向`
ORDER BY `培养方案ID`, `专业方向ID`;

-- Existing deleted relations listed here are excluded, not restored by these views.
SELECT DISTINCT r.scheme_id, r.course_id, s.sysflag AS scheme_sysflag, c.sysflag AS course_sysflag
FROM t_csys_training_scheme_ref_course r
JOIN t_csys_training_scheme s ON s.id = r.scheme_id
JOIN t_csys_course c ON c.id = r.course_id
WHERE s.sysflag = 2 OR c.sysflag = 2;
SELECT scheme_id, course_id, term, sysflag
FROM t_csys_training_scheme_course_schedule WHERE sysflag = 2;

-- Unit/negative-duration anomalies require source-data review, no automatic updates.
SELECT id, name, type, time_Week, unit FROM t_csys_course
WHERE sysflag = 0 AND type IN (2,4)
  AND (COALESCE(NULLIF(unit, ''), '1') NOT IN ('1','2') OR time_Week < 0 OR time_Week IS NULL);
SELECT d.* FROM t_csys_course_ref_schedule d
JOIN t_csys_course c ON c.id = d.course_id AND c.sysflag = 0 AND c.type IN (2,4)
WHERE COALESCE(NULLIF(d.unit, ''), '1') NOT IN ('1','2') OR d.time_Week < 0;
SELECT * FROM v_csys_course_time_credit WHERE unit_name = '混合（学时/周）';
