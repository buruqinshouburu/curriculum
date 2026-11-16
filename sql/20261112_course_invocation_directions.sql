-- Run before deploying the matching backend. No legacy rows are rewritten.
SET @schema_name = DATABASE();
SET @ddl = IF(EXISTS(SELECT 1 FROM information_schema.COLUMNS WHERE TABLE_SCHEMA=@schema_name AND TABLE_NAME='t_csys_course' AND COLUMN_NAME='invocation_sub_major_ids'), 'SELECT 1', 'ALTER TABLE t_csys_course ADD COLUMN invocation_sub_major_ids TEXT NULL COMMENT ''Explicit invocation direction IDs, comma separated; NULL retains legacy scope''');
PREPARE stmt FROM @ddl;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;
