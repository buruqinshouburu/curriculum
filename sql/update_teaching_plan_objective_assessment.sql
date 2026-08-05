-- 课程目标权重 + 课程目标考核评价关联
ALTER TABLE t_csys_teaching_plan_objective
  ADD COLUMN weight decimal(8,4) DEFAULT NULL COMMENT '课程目标权重，普通课程目标合计为1';

CREATE TABLE IF NOT EXISTS t_csys_teaching_plan_objective_assessment (
  id bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  plan_id bigint NOT NULL COMMENT '教学计划ID',
  scheme_id bigint DEFAULT NULL COMMENT '培养方案ID',
  objective_id bigint NOT NULL COMMENT '课程目标ID',
  assessment_id bigint DEFAULT NULL COMMENT '考核评价ID',
  assessment_item varchar(255) DEFAULT NULL COMMENT '考核项目名称快照',
  weight decimal(8,4) DEFAULT NULL COMMENT '该目标对应考核项目权重',
  assessment_item_content text COMMENT '考核评价项内容',
  creator varchar(64) DEFAULT NULL,
  create_time datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  last_modifier varchar(64) DEFAULT NULL,
  last_modified_time datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  remark varchar(500) DEFAULT NULL,
  sysflag tinyint unsigned NOT NULL DEFAULT 0,
  PRIMARY KEY (id),
  KEY idx_tpoa_plan_scheme (plan_id, scheme_id),
  KEY idx_tpoa_objective (objective_id),
  KEY idx_tpoa_assessment (assessment_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='课程目标与考核评价关联';
