-- 教学计划 2026-08-13 增量 DDL
-- 课程目标改为按 plan_id + scheme_id 维护；公共基础课程 scheme_id 为 NULL。

SET @schema_name = DATABASE();

SET @ddl = IF(
  EXISTS(SELECT 1 FROM information_schema.COLUMNS
         WHERE TABLE_SCHEMA=@schema_name AND TABLE_NAME='t_csys_teaching_plan_objective'
           AND COLUMN_NAME='scheme_id'),
  'SELECT 1',
  'ALTER TABLE t_csys_teaching_plan_objective ADD COLUMN scheme_id BIGINT NULL COMMENT ''培养方案ID(公共基础为NULL)'' AFTER plan_id'
);
PREPARE stmt FROM @ddl; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @ddl = IF(
  EXISTS(SELECT 1 FROM information_schema.COLUMNS
         WHERE TABLE_SCHEMA=@schema_name AND TABLE_NAME='t_csys_teaching_plan_objective'
           AND COLUMN_NAME='weight'),
  'SELECT 1',
  'ALTER TABLE t_csys_teaching_plan_objective ADD COLUMN weight DECIMAL(8,4) NULL COMMENT ''课程目标权重'' AFTER source_mode'
);
PREPARE stmt FROM @ddl; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @ddl = IF(
  EXISTS(SELECT 1 FROM information_schema.COLUMNS
         WHERE TABLE_SCHEMA=@schema_name AND TABLE_NAME='t_csys_teaching_plan_objective_ref'
           AND COLUMN_NAME='scheme_id'),
  'SELECT 1',
  'ALTER TABLE t_csys_teaching_plan_objective_ref ADD COLUMN scheme_id BIGINT NULL COMMENT ''培养方案ID快照(公共基础为NULL)'' AFTER quote_course_id'
);
PREPARE stmt FROM @ddl; EXECUTE stmt; DEALLOCATE PREPARE stmt;

ALTER TABLE t_csys_teaching_plan_objective
  MODIFY COLUMN scheme_id BIGINT NULL COMMENT '培养方案ID(公共基础为NULL)',
  MODIFY COLUMN weight DECIMAL(8,4) NULL COMMENT '课程目标权重，普通课程目标合计为1';

ALTER TABLE t_csys_teaching_plan_objective_ref
  MODIFY COLUMN scheme_id BIGINT NULL COMMENT '培养方案ID快照(公共基础为NULL)';

SET @ddl = IF(
  EXISTS(SELECT 1 FROM information_schema.STATISTICS
         WHERE TABLE_SCHEMA=@schema_name AND TABLE_NAME='t_csys_teaching_plan_objective_ref'
           AND INDEX_NAME='uk_tp_obj_ref'),
  'ALTER TABLE t_csys_teaching_plan_objective_ref DROP INDEX uk_tp_obj_ref',
  'SELECT 1'
);
PREPARE stmt FROM @ddl; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @ddl = IF(
  EXISTS(SELECT 1 FROM information_schema.STATISTICS
         WHERE TABLE_SCHEMA=@schema_name AND TABLE_NAME='t_csys_teaching_plan_objective_ref'
           AND INDEX_NAME='uk_tp_obj_ref_scheme'),
  'SELECT 1',
  'ALTER TABLE t_csys_teaching_plan_objective_ref ADD UNIQUE INDEX uk_tp_obj_ref_scheme (objective_id, scheme_id, graduation_id)'
);
PREPARE stmt FROM @ddl; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- context_id 为旧版页面上下文列。新代码不再写入，存在时删除，避免 NOT NULL 阻断新增。
SET @ddl = IF(
  EXISTS(SELECT 1 FROM information_schema.COLUMNS
         WHERE TABLE_SCHEMA=@schema_name AND TABLE_NAME='t_csys_teaching_plan_objective'
           AND COLUMN_NAME='context_id'),
  'ALTER TABLE t_csys_teaching_plan_objective DROP COLUMN context_id',
  'SELECT 1'
);
PREPARE stmt FROM @ddl; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @ddl = IF(
  EXISTS(SELECT 1 FROM information_schema.COLUMNS
         WHERE TABLE_SCHEMA=@schema_name AND TABLE_NAME='t_csys_teaching_plan_objective_ref'
           AND COLUMN_NAME='context_id'),
  'ALTER TABLE t_csys_teaching_plan_objective_ref DROP COLUMN context_id',
  'SELECT 1'
);
PREPARE stmt FROM @ddl; EXECUTE stmt; DEALLOCATE PREPARE stmt;
