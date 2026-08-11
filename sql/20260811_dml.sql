-- =====================================================================
-- 教学计划接口完善 2026-08-11 新增字典 DML（增量，只新增不修改既有记录）
-- 对应 DDL 见 20260811_ddl.sql。
-- 两处手动输入字段改为字典选择（均实践训练课目）：
--   a. 第四部分「训练内容与时间安排」模块列       -> sys_plan_training_module
--   b. 第五部分「组织实施」实施步骤列             -> sys_plan_implementation_step
-- 注意：dict_id / dict_code 取自线上表当前 AUTO_INCREMENT 水位，
--   若本地已存在则改为 INSERT 后最大 id+1。
-- =====================================================================

-- =====================================================================
-- 新增字典：训练内容与时间安排模块
-- 用途：实践训练课目(type=2)教学计划「四、训练内容与时间安排」的「模块」列。
--   原为手动输入自由文本，现改为字典选择；DB 存字典 value(编码)，
--   Word 生成时译为 label、导入时 label 反查编码。
-- 字典值：1 战斗体技能提升模块 / 2 指挥素养培塑模块 / 3 新质新域能力拓展模块
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

-- =====================================================================
-- 新增字典：组织实施实施步骤
-- 用途：实践训练课目(plan_type=3)教学计划「五、组织实施」表的「实施步骤」列。
--   原为手动输入自由文本，现改为字典选择；DB 存字典 value(编码) 于
--   t_csys_teaching_plan_process_step.stage_name，生成 Word 时译为 label、
--   导入时 label 反查编码。同列按相同实施步骤连续竖向合并。
-- 字典值：1 战斗准备 / 2 战斗实施 / 3 撤出战斗
-- =====================================================================

INSERT INTO `sys_dict_type`
(`dict_id`, `dict_name`, `dict_type`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
VALUES
(154, '实施步骤', 'sys_plan_implementation_step', '0', 'cur', NOW(), '', NULL, '实践训练课目教学计划第五部分组织实施实施步骤字段');

INSERT INTO `sys_dict_data`
(`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
VALUES
(333, 1, '战斗准备', '1', 'sys_plan_implementation_step', NULL, 'default', 'N', '0', 'cur', NOW(), '', NULL, NULL),
(334, 2, '战斗实施', '2', 'sys_plan_implementation_step', NULL, 'default', 'N', '0', 'cur', NOW(), '', NULL, NULL),
(335, 3, '撤出战斗', '3', 'sys_plan_implementation_step', NULL, 'default', 'N', '0', 'cur', NOW(), '', NULL, NULL);
