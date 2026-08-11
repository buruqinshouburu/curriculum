-- 实践训练课目第四部分「训练内容与时间安排」目的列改造：
-- 目的由「手动输入」改为「从第二部分训练目的多选」，新增 content ↔ purpose 关联表

-- 训练内容 -> 训练目的绑定（对标 t_csys_teaching_plan_training_purpose_ref）
CREATE TABLE IF NOT EXISTS `t_csys_teaching_plan_content_purpose` (
  `id`              BIGINT       NOT NULL AUTO_INCREMENT,
  `plan_id`         BIGINT       NOT NULL COMMENT '教学计划ID',
  `content_id`      BIGINT       NOT NULL COMMENT '训练内容ID t_csys_teaching_plan_content.id',
  `purpose_id`      BIGINT       NOT NULL COMMENT '训练目的ID t_csys_teaching_plan_training_purpose.id',
  `sort`            INT          NULL     COMMENT '排序',
  `creator`         VARCHAR(64)  NULL,
  `create_time`     DATETIME     NULL,
  `last_modifier`   VARCHAR(64)  NULL,
  `last_modified_time` DATETIME  NULL,
  `remark`          VARCHAR(500) NULL,
  `sysflag`         TINYINT       NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`),
  KEY `idx_content` (`content_id`),
  KEY `idx_purpose` (`purpose_id`),
  KEY `idx_plan` (`plan_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='教学计划训练内容支撑训练目的(第四部分目的多选)';
