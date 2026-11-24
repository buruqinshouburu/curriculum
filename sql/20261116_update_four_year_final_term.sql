-- First run the preview, review it, and keep the SAME database connection.
-- Stop application writes until COMMIT/ROLLBACK. Do not enable auto-commit per statement.
START TRANSACTION;

UPDATE t_csys_training_scheme_course_schedule s
JOIN tmp_four_year_plan p ON p.keep_id = s.id
SET s.term = 8,
    s.teach_hours = p.teach_hours,
    s.practice_hours = p.practice_hours,
    s.theory_hours = p.theory_hours,
    s.checked = p.checked
WHERE s.sysflag = 0;

-- Same physical delete as the application; only redundant IDs captured in preview.
DELETE s FROM t_csys_training_scheme_course_schedule s
JOIN tmp_four_year_rows r ON r.id = s.id
JOIN tmp_four_year_plan p ON p.scheme_id = r.scheme_id AND p.course_id = r.course_id
WHERE s.id <> p.keep_id AND s.sysflag = 0;

-- Must return zero rows. The following SELECT does not commit the transaction.
SELECT p.scheme_id, p.course_id, COUNT(s.id) AS remaining_rows,
       MIN(s.term) AS min_term, MAX(s.term) AS max_term
FROM tmp_four_year_plan p
LEFT JOIN t_csys_training_scheme_course_schedule s
       ON s.scheme_id = p.scheme_id AND s.course_id = p.course_id AND s.sysflag = 0
GROUP BY p.scheme_id, p.course_id
HAVING COUNT(s.id) <> 1 OR MIN(s.term) <> 8 OR COUNT(s.term) <> COUNT(s.id);

-- Inspect retained rows and allocated hours, then execute ONE command manually:
-- COMMIT;
-- ROLLBACK;
