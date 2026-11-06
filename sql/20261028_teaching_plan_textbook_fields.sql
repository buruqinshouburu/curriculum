-- 教材补充字段与字典；仅提供脚本，由发布人员在确认目标库后手动执行。
-- 新增三列均可空，不回填既有教材。兼容本项目 MySQL information_schema + PREPARE 惯例。
SET @textbook_schema = DATABASE();

SET @textbook_ddl = IF(
  EXISTS(SELECT 1 FROM information_schema.COLUMNS
         WHERE TABLE_SCHEMA = @textbook_schema AND TABLE_NAME = 't_csys_teaching_plan_textbook'
           AND COLUMN_NAME = 'security_classification'),
  'SELECT 1',
  'ALTER TABLE t_csys_teaching_plan_textbook ADD COLUMN security_classification VARCHAR(100) NULL DEFAULT NULL COMMENT ''教材密级(sys_textbook_security_classification)'' AFTER publish_method'
);
PREPARE textbook_stmt FROM @textbook_ddl;
EXECUTE textbook_stmt;
DEALLOCATE PREPARE textbook_stmt;

SET @textbook_ddl = IF(
  EXISTS(SELECT 1 FROM information_schema.COLUMNS
         WHERE TABLE_SCHEMA = @textbook_schema AND TABLE_NAME = 't_csys_teaching_plan_textbook'
           AND COLUMN_NAME = 'political_theory_textbook'),
  'SELECT 1',
  'ALTER TABLE t_csys_teaching_plan_textbook ADD COLUMN political_theory_textbook VARCHAR(100) NULL DEFAULT NULL COMMENT ''是否为政治理论教材(sys_textbook_political_theory)'' AFTER security_classification'
);
PREPARE textbook_stmt FROM @textbook_ddl;
EXECUTE textbook_stmt;
DEALLOCATE PREPARE textbook_stmt;

SET @textbook_ddl = IF(
  EXISTS(SELECT 1 FROM information_schema.COLUMNS
         WHERE TABLE_SCHEMA = @textbook_schema AND TABLE_NAME = 't_csys_teaching_plan_textbook'
           AND COLUMN_NAME = 'textbook_source'),
  'SELECT 1',
  'ALTER TABLE t_csys_teaching_plan_textbook ADD COLUMN textbook_source VARCHAR(100) NULL DEFAULT NULL COMMENT ''教材来源(sys_textbook_source)'' AFTER political_theory_textbook'
);
PREPARE textbook_stmt FROM @textbook_ddl;
EXECUTE textbook_stmt;
DEALLOCATE PREPARE textbook_stmt;

-- 使用系统表自增主键；按 dict_type / dict_value 判重，重复执行不新增同值记录。
INSERT INTO sys_dict_type
  (dict_name, dict_type, status, create_by, create_time, remark)
SELECT seed.dict_name, seed.dict_type, '0', 'cur', NOW(), '教学计划教材'
FROM (
  SELECT '教材性质' AS dict_name, 'sys_textbook_nature' AS dict_type
  UNION ALL SELECT '出版方式', 'sys_publication_method'
  UNION ALL SELECT '教材密级', 'sys_textbook_security_classification'
  UNION ALL SELECT '是否为政治理论教材', 'sys_textbook_political_theory'
  UNION ALL SELECT '教材来源', 'sys_textbook_source'
) seed
WHERE NOT EXISTS (SELECT 1 FROM sys_dict_type existing WHERE existing.dict_type = seed.dict_type);

INSERT INTO sys_dict_data
  (dict_sort, dict_label, dict_value, dict_type, list_class, is_default, status, create_by, create_time)
SELECT seed.dict_sort, seed.dict_label, seed.dict_value, seed.dict_type, 'default', 'N', '0', 'cur', NOW()
FROM (
  SELECT 1 AS dict_sort, '基本教材' AS dict_label, '1' AS dict_value, 'sys_textbook_nature' AS dict_type
  UNION ALL SELECT 2, '辅助教材', '2', 'sys_textbook_nature'
  UNION ALL SELECT 3, '参考教材', '3', 'sys_textbook_nature'
  UNION ALL SELECT 1, '公开出版', '1', 'sys_publication_method'
  UNION ALL SELECT 2, '军内发行', '2', 'sys_publication_method'
  UNION ALL SELECT 3, '内部印发', '3', 'sys_publication_method'
  UNION ALL SELECT 1, '公开', '1', 'sys_textbook_security_classification'
  UNION ALL SELECT 2, '内部', '2', 'sys_textbook_security_classification'
  UNION ALL SELECT 3, '秘密', '3', 'sys_textbook_security_classification'
  UNION ALL SELECT 4, '机密', '4', 'sys_textbook_security_classification'
  UNION ALL SELECT 1, '是', '1', 'sys_textbook_political_theory'
  UNION ALL SELECT 2, '否', '2', 'sys_textbook_political_theory'
  UNION ALL SELECT 1, '自编教材', '1', 'sys_textbook_source'
  UNION ALL SELECT 2, '选用国内教材', '2', 'sys_textbook_source'
  UNION ALL SELECT 3, '选用境外教材国内翻译版', '3', 'sys_textbook_source'
  UNION ALL SELECT 4, '选用境外原版教材', '4', 'sys_textbook_source'
) seed
WHERE NOT EXISTS (
  SELECT 1 FROM sys_dict_data existing
  WHERE existing.dict_type = seed.dict_type AND existing.dict_value = seed.dict_value
);

-- 校正已存在的两组旧字典标签，保留字典编码和教材原始数据。
UPDATE sys_dict_data
SET dict_label = CASE dict_value WHEN '1' THEN '基本教材' WHEN '2' THEN '辅助教材' WHEN '3' THEN '参考教材' END,
    dict_sort = CAST(dict_value AS UNSIGNED), status = '0'
WHERE dict_type = 'sys_textbook_nature' AND dict_value IN ('1', '2', '3');

UPDATE sys_dict_data
SET dict_label = CASE dict_value WHEN '1' THEN '公开出版' WHEN '2' THEN '军内发行' WHEN '3' THEN '内部印发' END,
    dict_sort = CAST(dict_value AS UNSIGNED), status = '0'
WHERE dict_type = 'sys_publication_method' AND dict_value IN ('1', '2', '3');
