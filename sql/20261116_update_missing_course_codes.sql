-- MySQL 8.0+. Run AFTER 20261116_preview_missing_course_codes.sql in the SAME connection.
-- Stop all course creation/import/edit jobs first. Back up the database and export the preview.
-- Review skipped records and collision queries before executing. Keep services stopped until COMMIT/ROLLBACK.
START TRANSACTION;
UPDATE t_csys_course c
JOIN tmp_practice_code_plan p ON p.id = c.id
SET c.code = p.new_code
WHERE c.sysflag = 0 AND c.type IN ('1', '2', '3', '4')
  AND (c.code IS NULL OR TRIM(c.code) = '');
SELECT ROW_COUNT() AS updated_count;
SELECT c.id, c.name, c.code, p.new_code
FROM t_csys_course c JOIN tmp_practice_code_plan p ON p.id = c.id
ORDER BY c.id;
-- Verify updated_count equals planned_count; then execute ONE of the following manually.
-- COMMIT;
-- ROLLBACK;
