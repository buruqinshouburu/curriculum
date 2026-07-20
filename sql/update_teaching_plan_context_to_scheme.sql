-- ============================================================
-- 教学计划：去掉 context 依赖，tab 改按培养方案 scheme_id
-- 1) objective / target_design / ref：context_id 重命名为 scheme_id（语义=培养方案id）
-- 2) objective_ref：已有 scheme_id，删除 context_id，唯一键改为 (objective_id, scheme_id, graduation_id)
-- 3) t_csys_teaching_plan_context 表保留不删（兼容历史），代码侧不再读写
-- 注意：若表内已有 context_id 业务数据，需先手工迁移后再执行本脚本
-- ============================================================

-- ---------- objective ----------
ALTER TABLE `t_csys_teaching_plan_objective`
  DROP INDEX `idx_tp_obj_context`,
  DROP INDEX `idx_tp_obj_context_type`;

ALTER TABLE `t_csys_teaching_plan_objective`
  CHANGE COLUMN `context_id` `scheme_id` bigint NOT NULL COMMENT '培养方案ID，对应页面当前培养方案tab（t_csys_training_scheme.id）';

ALTER TABLE `t_csys_teaching_plan_objective`
  ADD KEY `idx_tp_obj_scheme` (`scheme_id`),
  ADD KEY `idx_tp_obj_plan_scheme_type` (`plan_id`, `scheme_id`, `objective_type_code`);

-- ---------- objective_ref（已有 scheme_id，去掉 context_id） ----------
ALTER TABLE `t_csys_teaching_plan_objective_ref`
  DROP INDEX `uk_tp_obj_ref`,
  DROP INDEX `idx_tp_obj_ref_context`;

ALTER TABLE `t_csys_teaching_plan_objective_ref`
  DROP COLUMN `context_id`;

ALTER TABLE `t_csys_teaching_plan_objective_ref`
  ADD UNIQUE KEY `uk_tp_obj_ref` (`objective_id`, `scheme_id`, `graduation_id`);

-- ---------- target_design ----------
ALTER TABLE `t_csys_teaching_plan_target_design`
  DROP INDEX `idx_tp_design_context`;

ALTER TABLE `t_csys_teaching_plan_target_design`
  CHANGE COLUMN `context_id` `scheme_id` bigint DEFAULT NULL COMMENT '培养方案ID，按tab维护时填写';

ALTER TABLE `t_csys_teaching_plan_target_design`
  ADD KEY `idx_tp_design_scheme` (`scheme_id`);

-- ---------- ref ----------
ALTER TABLE `t_csys_teaching_plan_ref`
  DROP INDEX `idx_tp_ref_context`;

ALTER TABLE `t_csys_teaching_plan_ref`
  CHANGE COLUMN `context_id` `scheme_id` bigint DEFAULT NULL COMMENT '培养方案ID，可为空';

ALTER TABLE `t_csys_teaching_plan_ref`
  ADD KEY `idx_tp_ref_scheme` (`scheme_id`);
