-- ============================================================
-- 教学计划取值改造 增量 DDL
-- 1. t_csys_teaching_plan.plan_type 注释更新：与课程类型同一字典值(1课程 2实践训练课目 3实验课程 4实践项目)。
--    plan_type 仍由前端传入(保留前端值), 使单一课程可出现多类型教学计划。
-- 2. 新增唯一约束: 同一课程同一教学计划类型只允许一条有效记录(sysflag=0)。
--    MySQL 无部分唯一索引, 故唯一键含 sysflag:
--    正常记录 sysflag=0 互相约束唯一; 逻辑删除后 sysflag=2, 历史墓碑不阻碍新建。
--    注意: 若线上已有 (source_course_id, plan_type) 重复的 sysflag=0 记录, 加约束前需先清理。
-- ============================================================

ALTER TABLE `t_csys_teaching_plan`
  MODIFY COLUMN `plan_type` tinyint NOT NULL COMMENT '教学计划类型(与课程类型同字典值):1课程 2实践训练课目 3实验课程 4实践项目';

ALTER TABLE `t_csys_teaching_plan`
  ADD UNIQUE KEY `uk_tp_source_course_plan_type` (`source_course_id`, `plan_type`, `sysflag`);
