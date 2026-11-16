-- Run against the curriculum database before deploying the matching backend.
-- Existing scalar IDs are retained. NULL means no multi-select data was written yet.
SET @schema_name = DATABASE();
SET @ddl = IF(EXISTS(SELECT 1 FROM information_schema.COLUMNS WHERE TABLE_SCHEMA=@schema_name AND TABLE_NAME='t_csys_course' AND COLUMN_NAME='major_ids'), 'SELECT 1', 'ALTER TABLE t_csys_course ADD COLUMN major_ids TEXT NULL COMMENT ''Selected major category IDs, comma separated''');
PREPARE stmt FROM @ddl;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;
SET @ddl = IF(EXISTS(SELECT 1 FROM information_schema.COLUMNS WHERE TABLE_SCHEMA=@schema_name AND TABLE_NAME='t_csys_course' AND COLUMN_NAME='sub_major_ids'), 'SELECT 1', 'ALTER TABLE t_csys_course ADD COLUMN sub_major_ids TEXT NULL COMMENT ''Selected professional direction IDs, comma separated''');
PREPARE stmt FROM @ddl;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;
