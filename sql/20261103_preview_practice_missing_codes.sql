-- 训练课目、实践项目缺编号修复预览（MySQL 8.0+）。
-- 使用前：备份数据库，停止新增/导入/修改课程，直到提交或回滚完成。
-- 整个过程必须在同一个数据库连接中执行；确认 SELECT DATABASE() 是目标业务库。
-- 第 1 部分只创建临时表、查询预览，不修改业务表。
-- 仅处理未删除、template_type=1 且 type 为 2 或 4 的空编号记录。
-- 无法识别的单位/适用对象不会自动生成。

SELECT DATABASE() AS current_database, VERSION() AS mysql_version;

DROP TEMPORARY TABLE IF EXISTS tmp_practice_code_candidates;
CREATE TEMPORARY TABLE tmp_practice_code_candidates AS
SELECT c.id, c.name, c.type, c.version, c.education_level, c.college_id,
       d.dept_name,
       CASE c.education_level
           WHEN '1' THEN 'JG' WHEN '2' THEN 'DB' WHEN '3' THEN 'SG'
           WHEN '4' THEN 'JD' WHEN '5' THEN 'JS' WHEN '6' THEN 'DS'
           WHEN '7' THEN 'YL' WHEN '8' THEN 'JY' ELSE NULL
       END AS education_code,
       CASE d.dept_name
           WHEN '理学院' THEN '01' WHEN '计算机学院' THEN '02'
           WHEN '电子科学学院' THEN '03' WHEN '前沿交叉学科学院' THEN '04'
           WHEN '智能科学学院' THEN '05' WHEN '系统工程学院' THEN '06'
           WHEN '空天科学学院' THEN '07' WHEN '外国语学院' THEN '08'
           WHEN '电子对抗学院' THEN '09' WHEN '气象海洋学院' THEN '10'
           WHEN '军政基础教育学院' THEN '11' WHEN '校本级' THEN '00'
           WHEN '图书馆' THEN '12' ELSE NULL
       END AS college_code
FROM t_csys_course c
LEFT JOIN sys_dept d ON d.dept_id = c.college_id
WHERE c.sysflag = 0 AND c.template_type = 1 AND c.type IN ('2', '4')
  AND (c.code IS NULL OR TRIM(c.code) = '');

-- 无法生成编号的数据：先由维护人员检查版本、适用对象和组织实施单位。
SELECT id, name, type, version, education_level, college_id, dept_name
FROM tmp_practice_code_candidates
WHERE version IS NULL OR CHAR_LENGTH(TRIM(version)) < 2
   OR education_code IS NULL OR college_code IS NULL
ORDER BY id;

DROP TEMPORARY TABLE IF EXISTS tmp_practice_code_prefixes;
CREATE TEMPORARY TABLE tmp_practice_code_prefixes AS
SELECT id, name, type,
       CONCAT(RIGHT(version, 2), '-', education_code, '-', college_code, '-') AS code_prefix
FROM tmp_practice_code_candidates
WHERE version IS NOT NULL AND CHAR_LENGTH(TRIM(version)) >= 2
  AND education_code IS NOT NULL AND college_code IS NOT NULL;

-- 参考现有生成规则，在同前缀已使用的最大流水号之后继续编号。
-- 该前缀下如果存在非数字后缀则跳过并列出，避免与旧代码解析规则不一致。
DROP TEMPORARY TABLE IF EXISTS tmp_practice_code_groups;
CREATE TEMPORARY TABLE tmp_practice_code_groups AS
SELECT p.code_prefix,
       COALESCE(MAX(CASE WHEN SUBSTRING(c.code, CHAR_LENGTH(p.code_prefix) + 1) REGEXP '^[0-9]+$'
                         THEN CAST(SUBSTRING(c.code, CHAR_LENGTH(p.code_prefix) + 1) AS UNSIGNED)
                         ELSE 0 END), 0) AS max_sequence,
       SUM(CASE WHEN c.id IS NOT NULL AND
                     SUBSTRING(c.code, CHAR_LENGTH(p.code_prefix) + 1) NOT REGEXP '^[0-9]+$'
                THEN 1 ELSE 0 END) AS malformed_count
FROM (SELECT DISTINCT code_prefix FROM tmp_practice_code_prefixes) p
LEFT JOIN t_csys_course c
  ON c.sysflag = 0 AND LEFT(c.code, CHAR_LENGTH(p.code_prefix)) = p.code_prefix
GROUP BY p.code_prefix;

SELECT code_prefix, max_sequence, malformed_count
FROM tmp_practice_code_groups WHERE malformed_count > 0 OR max_sequence >= 9999;

DROP TEMPORARY TABLE IF EXISTS tmp_practice_code_plan;
CREATE TEMPORARY TABLE tmp_practice_code_plan AS
SELECT x.id, x.name, x.type, x.code_prefix,
       CONCAT(x.code_prefix, LPAD(x.next_sequence, 4, '0')) AS new_code
FROM (
    SELECT p.id, p.name, p.type, p.code_prefix,
           g.max_sequence + ROW_NUMBER() OVER (PARTITION BY p.code_prefix ORDER BY p.id) AS next_sequence
    FROM tmp_practice_code_prefixes p
    INNER JOIN tmp_practice_code_groups g ON g.code_prefix = p.code_prefix
    WHERE g.malformed_count = 0
) x
WHERE x.next_sequence <= 9999;

-- 拟修改列表；流水号不足 4 位范围的数据不会进入计划，需人工处理。
SELECT * FROM tmp_practice_code_plan ORDER BY code_prefix, id;
SELECT (SELECT COUNT(*) FROM tmp_practice_code_candidates) AS missing_count,
       (SELECT COUNT(*) FROM tmp_practice_code_plan) AS planned_count;

-- 必须都为 0，才可以考虑执行第 2 部分；否则停止并排查。
SELECT new_code, COUNT(*) AS planned_count
FROM tmp_practice_code_plan GROUP BY new_code HAVING COUNT(*) > 1;
SELECT p.id, p.new_code, c.id AS conflicting_id
FROM tmp_practice_code_plan p
JOIN t_csys_course c ON c.sysflag = 0 AND c.code = p.new_code;

-- 第 2 部分：只在维护窗口、完整备份、预览和冲突检查均确认后，
-- 在同一个连接里手动选中下列语句执行。不要整段直接运行。
--
-- START TRANSACTION;
-- UPDATE t_csys_course c
-- JOIN tmp_practice_code_plan p ON p.id = c.id
-- SET c.code = p.new_code
-- WHERE c.sysflag = 0 AND c.template_type = 1 AND c.type IN ('2', '4')
--   AND (c.code IS NULL OR TRIM(c.code) = '');
-- SELECT ROW_COUNT() AS updated_count;
-- SELECT c.id, c.name, c.code FROM t_csys_course c
-- JOIN tmp_practice_code_plan p ON p.id = c.id ORDER BY c.id;
-- 确认记录和数量后单独执行 COMMIT；有疑问执行 ROLLBACK。
