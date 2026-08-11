-- =====================================================================
-- 新增字典：组织实施实施步骤
-- 用途：实践训练课目(plan_type=3)教学计划「五、组织实施」表的「实施步骤」列。
--   原为手动输入自由文本，现改为字典选择；DB 存字典 value(编码) 于
--   t_csys_teaching_plan_process_step.stage_name，生成 Word 时译为 label、
--   导入时 label 反查编码。同列按相同实施步骤连续竖向合并。
-- 字典值：
--   1 战斗准备
--   2 战斗实施
--   3 撤出战斗
-- 只写增量：新增 1 条字典类型 + 3 条字典数据，不修改既有记录。
-- 注意：dict_id=154 / dict_code=333,334,335 取自线上表当前 AUTO_INCREMENT 水位
--   （紧接 sys_plan_training_module 的 153 / 330-332），若本地已存在则改为
--   INSERT 后最大 id+1。
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
