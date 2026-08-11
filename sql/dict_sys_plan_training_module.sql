-- =====================================================================
-- 新增字典：训练内容与时间安排模块
-- 用途：实践训练课目(type=2)教学计划「四、训练内容与时间安排」的「模块」列。
--   原为手动输入自由文本，现改为字典选择；DB 存字典 value(编码)，
--   Word 生成时译为 label、导入时 label 反查编码。
-- 字典值：
--   1 战斗体技能提升模块
--   2 指挥素养培塑模块
--   3 新质新域能力拓展模块
-- 只写增量：新增 1 条字典类型 + 3 条字典数据，不修改既有记录。
-- 注意：dict_id=153 / dict_code=330,331,332 取自线上表当前 AUTO_INCREMENT 水位，
--   若本地已存在则改为 INSERT 后最大 id+1。
-- =====================================================================

INSERT INTO `sys_dict_type`
(`dict_id`, `dict_name`, `dict_type`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
VALUES
(153, '训练内容与时间安排模块', 'sys_plan_training_module', '0', 'cur', NOW(), '', NULL, '实践训练课目教学计划第四部分模块字段');

INSERT INTO `sys_dict_data`
(`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
VALUES
(330, 1, '战斗体技能提升模块',   '1', 'sys_plan_training_module', NULL, 'default', 'N', '0', 'cur', NOW(), '', NULL, NULL),
(331, 2, '指挥素养培塑模块',     '2', 'sys_plan_training_module', NULL, 'default', 'N', '0', 'cur', NOW(), '', NULL, NULL),
(332, 3, '新质新域能力拓展模块', '3', 'sys_plan_training_module', NULL, 'default', 'N', '0', 'cur', NOW(), '', NULL, NULL);
