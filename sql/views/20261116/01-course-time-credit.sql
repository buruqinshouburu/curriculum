-- MySQL 8.0+. Shared unit-safe data; no business records are updated.
SET NAMES utf8mb4;
CREATE OR REPLACE ALGORITHM = UNDEFINED
SQL SECURITY INVOKER VIEW v_csys_course_time_credit AS
WITH detail AS (
    SELECT course_id, COUNT(*) AS row_count,
           COUNT(DISTINCT COALESCE(NULLIF(unit, ''), '1')) AS unit_count,
           MIN(COALESCE(NULLIF(unit, ''), '1')) AS detail_unit,
           SUM(CASE WHEN COALESCE(NULLIF(unit, ''), '1') NOT IN ('1', '2') OR time_Week < 0 THEN 1 ELSE 0 END) AS invalid_count,
           SUM(CASE WHEN unit = '2' THEN COALESCE(time_Week, 0) ELSE 0 END) AS hours,
           SUM(CASE WHEN COALESCE(NULLIF(unit, ''), '1') = '1' THEN COALESCE(time_Week, 0) ELSE 0 END) AS weeks,
           SUM(CASE WHEN unit = '2' THEN CAST(COALESCE(time_Week, 0) AS DECIMAL(18,6)) / 16
                    ELSE CAST(COALESCE(time_Week, 0) AS DECIMAL(18,6)) END) AS raw_credit
    FROM t_csys_course_ref_schedule GROUP BY course_id
), amounts AS (
    SELECT c.id AS course_id,
           CASE WHEN c.type IN (2,4) THEN c.time_Week
                ELSE COALESCE(c.teach_hours, 0) + COALESCE(c.practice_hours, 0) END AS duration,
           CASE WHEN c.type NOT IN (2,4) THEN '学时'
                WHEN d.unit_count > 1 THEN '混合（学时/周）'
                WHEN d.row_count > 0 THEN CASE d.detail_unit WHEN '1' THEN '周' WHEN '2' THEN '学时' ELSE d.detail_unit END
                WHEN COALESCE(NULLIF(c.unit, ''), '1') = '1' THEN '周'
                WHEN c.unit = '2' THEN '学时' ELSE c.unit END AS unit_name,
           CASE WHEN c.type NOT IN (2,4) THEN COALESCE(c.hours, COALESCE(c.teach_hours, 0) + COALESCE(c.practice_hours, 0))
                WHEN d.row_count > 0 THEN CASE WHEN d.invalid_count = 0 THEN d.hours END
                WHEN c.unit = '2' AND c.time_Week >= 0 THEN c.time_Week
                WHEN COALESCE(NULLIF(c.unit, ''), '1') = '1' THEN 0 END AS hours,
           CASE WHEN c.type NOT IN (2,4) THEN 0
                WHEN d.row_count > 0 THEN CASE WHEN d.invalid_count = 0 THEN d.weeks END
                WHEN COALESCE(NULLIF(c.unit, ''), '1') = '1' AND c.time_Week >= 0 THEN c.time_Week
                WHEN c.unit = '2' THEN 0 END AS weeks,
           CASE WHEN c.type NOT IN (2,4) THEN c.credit
                WHEN d.row_count > 0 THEN CASE WHEN d.invalid_count = 0 THEN d.raw_credit END
                WHEN c.time_Week >= 0 AND c.unit = '2' THEN CAST(c.time_Week AS DECIMAL(18,6)) / 16
                WHEN c.time_Week >= 0 AND COALESCE(NULLIF(c.unit, ''), '1') = '1' THEN CAST(c.time_Week AS DECIMAL(18,6)) END AS raw_credit,
           c.type
    FROM t_csys_course c LEFT JOIN detail d ON d.course_id = c.id
    WHERE c.sysflag = 0
)
SELECT course_id, duration, unit_name, ROUND(hours, 2) AS hours, ROUND(weeks, 2) AS weeks,
       CASE WHEN type NOT IN (2,4) THEN ROUND(raw_credit, 2)
            WHEN raw_credit - FLOOR(raw_credit) = 0.5 THEN FLOOR(raw_credit) + 0.5
            WHEN raw_credit - FLOOR(raw_credit) > 0.5 THEN CEIL(raw_credit)
            ELSE FLOOR(raw_credit) END AS credits
FROM amounts;
