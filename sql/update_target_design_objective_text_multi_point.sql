-- 教学目标达成设计：支撑目标改存文本；知识目标支持绑定多个知识点（可跨知识单元）
-- 表：t_csys_teaching_plan_target_design

ALTER TABLE `t_csys_teaching_plan_target_design`
    ADD COLUMN `objective_text` text COMMENT '支撑的课程目标文本（知识/能力/素质目标内容字符串，直接保存）' AFTER `objective_id`,
    ADD COLUMN `knowledge_points` json DEFAULT NULL COMMENT '知识目标绑定的多个知识点JSON数组：[{knowledgeUnitId,knowledgeUnitName,knowledgePointId,knowledgePointName},...]' AFTER `knowledge_point_name`;
