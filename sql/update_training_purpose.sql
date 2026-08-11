-- 实践训练课目第二节「训练目的与支撑毕业要求」改造：多条训练目的 + 每条绑定多条毕业要求（按培养方案分组）
-- 对标 t_csys_teaching_plan_task_background / _task_background_ref

-- 训练目的主表
CREATE TABLE IF NOT EXISTS `t_csys_teaching_plan_training_purpose` (
  `id`              BIGINT       NOT NULL AUTO_INCREMENT,
  `plan_id`         BIGINT       NOT NULL COMMENT '教学计划ID',
  `scheme_id`       BIGINT       NULL     COMMENT '培养方案ID(通识通用为NULL)',
  `purpose`         TEXT         NULL     COMMENT '训练目的',
  `sort`            INT          NULL     COMMENT '排序',
  `creator`         VARCHAR(64)  NULL,
  `create_time`     DATETIME     NULL,
  `last_modifier`   VARCHAR(64)  NULL,
  `last_modified_time` DATETIME  NULL,
  `remark`          VARCHAR(500) NULL,
  `sysflag`         TINYINT       NOT NULL DEFAULT 0 COMMENT '0有效 2删除',
  PRIMARY KEY (`id`),
  KEY `idx_plan_scheme` (`plan_id`, `scheme_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='教学计划训练目的(实践训练课目第二节)';

-- 训练目的 -> 毕业要求绑定（对标 t_csys_teaching_plan_task_background_ref）
CREATE TABLE IF NOT EXISTS `t_csys_teaching_plan_training_purpose_ref` (
  `id`              BIGINT       NOT NULL AUTO_INCREMENT,
  `plan_id`         BIGINT       NOT NULL COMMENT '教学计划ID',
  `purpose_id`      BIGINT       NOT NULL COMMENT '训练目的ID',
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
  KEY `idx_purpose` (`purpose_id`),
  KEY `idx_plan` (`plan_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='教学计划训练目的支撑毕业要求';
