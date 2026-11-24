-- Run in a maintenance window after a full backup. Keep this connection open.
-- Explicit detail marker only: do not infer four-year courses from expanded years.
DROP TEMPORARY TABLE IF EXISTS tmp_four_year_rows;
CREATE TEMPORARY TABLE tmp_four_year_rows AS
SELECT s.*
FROM t_csys_training_scheme_course_schedule s
JOIN t_csys_course c ON c.id = s.course_id AND c.sysflag = 0
JOIN t_csys_training_scheme p ON p.id = s.scheme_id AND p.sysflag = 0
WHERE s.sysflag = 0 AND c.type IN ('1', '2', '3', '4')
  AND EXISTS (SELECT 1 FROM t_csys_course_ref_schedule d
              WHERE d.course_id = c.id AND d.semester_schedule = '6');

DROP TEMPORARY TABLE IF EXISTS tmp_four_year_plan;
CREATE TEMPORARY TABLE tmp_four_year_plan AS
SELECT scheme_id, course_id, MIN(id) AS keep_id, COUNT(*) AS old_rows,
       SUM(teach_hours) AS teach_hours, SUM(practice_hours) AS practice_hours,
       SUM(theory_hours) AS theory_hours, MAX(checked) AS checked
FROM tmp_four_year_rows
GROUP BY scheme_id, course_id
HAVING COUNT(*) > 1 OR MIN(term) <> 8 OR COUNT(term) <> COUNT(*);

-- Preview every affected record before changing it.
SELECT p.scheme_id, c.name AS course_name, c.type AS course_type,
       r.id AS old_id, r.term AS old_term, p.keep_id, 8 AS new_term,
       p.old_rows, r.teach_hours AS old_teach_hours, p.teach_hours AS new_teach_hours,
       r.practice_hours AS old_practice_hours, p.practice_hours AS new_practice_hours,
       r.hours, r.credits
FROM tmp_four_year_rows r
JOIN tmp_four_year_plan p ON p.scheme_id = r.scheme_id AND p.course_id = r.course_id
JOIN t_csys_course c ON c.id = r.course_id
ORDER BY p.scheme_id, p.course_id, r.id;

SELECT COUNT(*) AS course_scheme_groups, COALESCE(SUM(old_rows - 1), 0) AS rows_to_remove
FROM tmp_four_year_plan;

-- Header-only choices are not reliable after historical detail expansion: manual review only.
SELECT c.id, c.name, c.type, c.semester_schedule
FROM t_csys_course c
WHERE c.sysflag = 0 AND c.type IN ('1', '2', '3', '4') AND c.semester_schedule = '6'
  AND NOT EXISTS (SELECT 1 FROM t_csys_course_ref_schedule d
                  WHERE d.course_id = c.id AND d.semester_schedule = '6');
