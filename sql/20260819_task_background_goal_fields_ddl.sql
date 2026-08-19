-- 实验课程「任务背景与目标」字段调整。
-- 本功能尚未产生业务数据，直接用目标类型 + 目标内容替换原技术目标/能力目标两列。

ALTER TABLE `t_csys_teaching_plan_task_background`
  DROP COLUMN `technical_goal`,
  DROP COLUMN `ability_goal`,
  MODIFY COLUMN `background_desc` TEXT NOT NULL COMMENT '任务背景描述',
  ADD COLUMN `goal_type` TINYINT NOT NULL COMMENT '目标类型：1技术目标 2能力目标' AFTER `background_desc`,
  ADD COLUMN `goal_content` TEXT NOT NULL COMMENT '目标内容' AFTER `goal_type`;
