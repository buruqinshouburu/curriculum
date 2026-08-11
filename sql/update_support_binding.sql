-- =====================================================================
-- 实践项目教学计划(type=4)第二节「任务背景与目标」支撑绑定增量 DDL
-- 需求：将第二节两行改为多选绑定
--   「支撑的课程目标或训练目的」：绑支撑课程(源课 before_course_id)课程教学计划
--     第四部分课程目标 + 支撑训练课目(after_course_id)实践训练课目第二部分训练目的
--   「涉及的知识体系或训练内容」：绑支撑课程教学计划 content(知识单元知识点) +
--     支撑训练课目第四部分训练内容
-- 计划级绑定，不按培养方案(scheme)区分。
-- 只写增量：新增 2 张表，不修改既有表。
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
