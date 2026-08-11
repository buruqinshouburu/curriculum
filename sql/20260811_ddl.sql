-- =====================================================================
-- 教学计划接口完善 2026-08-11 新增表 DDL（增量，只新增不修改既有表）
-- 全部 CREATE TABLE IF NOT EXISTS，可重复执行。
-- 涉及 4 组改造：
--   1. 实验课程第三节任务背景：t_csys_teaching_plan_task_background / _task_background_ref
--   2. 实践训练课目第二节训练目的：t_csys_teaching_plan_training_purpose / _training_purpose_ref
--   3. 实践训练课目第四部分训练内容-训练目的绑定：t_csys_teaching_plan_content_purpose
--   4. 实践项目第二节支撑绑定：t_csys_teaching_plan_support_objective / _support_content
-- 对应 DML（字典）见 20260811_dml.sql。
-- =====================================================================

-- =====================================================================
-- 1. 实验课程第三节「任务背景与目标」改造：多条任务背景 + 每条绑定多条毕业要求（按培养方案分组）
--    对标 t_csys_teaching_plan_objective / _objective_ref
-- =====================================================================

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

-- =====================================================================
-- 2. 实践训练课目第二节「训练目的与支撑毕业要求」改造：多条训练目的 + 每条绑定多条毕业要求（按培养方案分组）
--    对标 t_csys_teaching_plan_task_background / _task_background_ref
-- =====================================================================

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

-- =====================================================================
-- 3. 实践训练课目第四部分「训练内容与时间安排」目的列改造：
--    目的由「手动输入」改为「从第二部分训练目的多选」，新增 content ↔ purpose 关联表
-- =====================================================================

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

-- =====================================================================
-- 4. 实践项目教学计划(type=4)第二节「任务背景与目标」支撑绑定增量 DDL
--    需求：将第二节两行改为多选绑定
--      「支撑的课程目标或训练目的」：绑支撑课程(源课 before_course_id)课程教学计划
--        第四部分课程目标 + 支撑训练课目(after_course_id)实践训练课目第二部分训练目的
--      「涉及的知识体系或训练内容」：绑支撑课程教学计划 content(知识单元知识点) +
--        支撑训练课目第四部分训练内容
--    计划级绑定，不按培养方案(scheme)区分。
-- =====================================================================

-- 表1：实践项目教学计划支撑的课程目标/训练目的绑定
CREATE TABLE IF NOT EXISTS t_csys_teaching_plan_support_objective (
  id             BIGINT        NOT NULL AUTO_INCREMENT,
  plan_id        BIGINT        NOT NULL COMMENT '实践项目教学计划ID(type4)',
  ref_type       TINYINT       NOT NULL COMMENT '绑定类型：1课程目标 2训练目的',
  ref_plan_id    BIGINT        NULL     COMMENT '来源教学计划ID(支撑课程/训练课目的教学计划)',
  ref_course_id  BIGINT        NULL     COMMENT '支撑课程/训练课目ID',
  objective_id   BIGINT        NULL     COMMENT '绑定的课程目标ID(ref_type=1)',
  purpose_id     BIGINT        NULL     COMMENT '绑定的训练目的ID(ref_type=2)',
  item_name      VARCHAR(1000) NULL     COMMENT '内容快照(课程目标内容/训练目的文本)',
  item_type_name VARCHAR(64)   NULL     COMMENT '课程目标类型名称快照(知识/能力/素质目标)；训练目的为空',
  major_id       BIGINT        NULL     COMMENT '来源目标所属专业ID快照(同专业优先排序用)',
  sort           INT           NULL     COMMENT '排序',
  creator        VARCHAR(64)   NULL,
  create_time    DATETIME      NULL,
  last_modifier  VARCHAR(64)   NULL,
  last_modified_time DATETIME  NULL,
  remark         VARCHAR(500)  NULL,
  sysflag        TINYINT       NOT NULL DEFAULT 0 COMMENT '0有效 2删除',
  PRIMARY KEY (id),
  KEY idx_plan (plan_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='实践项目教学计划支撑的课程目标/训练目的绑定';

-- 表2：实践项目教学计划涉及的知识体系/训练内容绑定
CREATE TABLE IF NOT EXISTS t_csys_teaching_plan_support_content (
  id             BIGINT        NOT NULL AUTO_INCREMENT,
  plan_id        BIGINT        NOT NULL COMMENT '实践项目教学计划ID(type4)',
  ref_type       TINYINT       NOT NULL COMMENT '绑定类型：1知识体系(课程教学内容) 2训练内容(课目训练内容)',
  ref_plan_id    BIGINT        NULL     COMMENT '来源教学计划ID(支撑课程/训练课目的教学计划)',
  ref_course_id  BIGINT        NULL     COMMENT '支撑课程/训练课目ID',
  content_id     BIGINT        NULL     COMMENT '绑定的教学内容ID t_csys_teaching_plan_content.id',
  item_title     VARCHAR(500)  NULL     COMMENT '内容名称快照',
  sort           INT           NULL     COMMENT '排序',
  creator        VARCHAR(64)   NULL,
  create_time    DATETIME      NULL,
  last_modifier  VARCHAR(64)   NULL,
  last_modified_time DATETIME  NULL,
  remark         VARCHAR(500)  NULL,
  sysflag        TINYINT       NOT NULL DEFAULT 0 COMMENT '0有效 2删除',
  PRIMARY KEY (id),
  KEY idx_plan (plan_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='实践项目教学计划涉及的知识体系/训练内容绑定';
