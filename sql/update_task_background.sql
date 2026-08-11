-- 实验课程第三节「任务背景与目标」改造：多条任务背景 + 每条绑定多条毕业要求（按培养方案分组）
-- 对标 t_csys_teaching_plan_objective / _objective_ref

-- 任务背景主表
CREATE TABLE IF NOT EXISTS `t_csys_teaching_plan_task_background` (
  `id`              BIGINT       NOT NULL AUTO_INCREMENT,
  `plan_id`         BIGINT       NOT NULL COMMENT '教学计划ID',
  `scheme_id`       BIGINT       NULL     COMMENT '培养方案ID(公共基础为NULL)',
  `major_id`        BIGINT       NULL     COMMENT '专业ID',
  `background_desc` TEXT         NULL     COMMENT '任务背景描述',
  `technical_goal`  TEXT         NULL     COMMENT '技术目标',
  `ability_goal`    TEXT         NULL     COMMENT '能力目标',
  `sort`            INT          NULL     COMMENT '排序',
  `creator`         VARCHAR(64)  NULL,
  `create_time`     DATETIME     NULL,
  `last_modifier`   VARCHAR(64)  NULL,
  `last_modified_time` DATETIME  NULL,
  `remark`          VARCHAR(500) NULL,
  `sysflag`         TINYINT       NOT NULL DEFAULT 0 COMMENT '0有效 2删除',
  PRIMARY KEY (`id`),
  KEY `idx_plan_scheme` (`plan_id`, `scheme_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='教学计划任务背景(实验课程第三节)';

-- 任务背景 -> 毕业要求绑定（对标 t_csys_teaching_plan_objective_ref）
CREATE TABLE IF NOT EXISTS `t_csys_teaching_plan_task_background_ref` (
  `id`              BIGINT       NOT NULL AUTO_INCREMENT,
  `plan_id`         BIGINT       NOT NULL COMMENT '教学计划ID',
  `task_background_id` BIGINT    NOT NULL COMMENT '任务背景ID',
  `scheme_course_graduation_id` BIGINT NULL COMMENT '培养方案调用课程毕业要求关联ID',
  `quote_course_id` BIGINT       NULL     COMMENT '调用课程ID快照',
  `scheme_id`       BIGINT       NULL     COMMENT '培养方案ID快照',
  `graduation_id`   BIGINT       NOT NULL COMMENT '方案内毕业标准ID t_csys_std_graduation.id',
  `source_graduation_id` BIGINT  NULL     COMMENT '毕业标准总库ID',
  `graduation_code` VARCHAR(64)  NULL     COMMENT '毕业标准编码快照',
  `graduation_name` VARCHAR(500) NULL     COMMENT '毕业标准名称快照',
  `graduation_bind_source` VARCHAR(64) NULL COMMENT '绑定来源',
  `support_desc`    VARCHAR(500) NULL     COMMENT '支撑说明',
  `sort`            INT          NULL,
  `creator`         VARCHAR(64)  NULL,
  `create_time`     DATETIME     NULL,
  `last_modifier`   VARCHAR(64)  NULL,
  `last_modified_time` DATETIME  NULL,
  `remark`          VARCHAR(500) NULL,
  `sysflag`         TINYINT       NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`),
  KEY `idx_task_bg` (`task_background_id`),
  KEY `idx_plan` (`plan_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='教学计划任务背景支撑毕业要求';
