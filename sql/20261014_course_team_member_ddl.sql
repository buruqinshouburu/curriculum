-- =====================================================================
-- 课程库及课程教学计划功能优化 V4.0 —— 增量 DDL
-- 日期：2026-10-14
--
-- 内容：
--   1) 新增课程库团队成员表 t_csys_course_team_member（需求一）
--   2) 教学目标达成设计 教法/学法 列宽 255 -> 500（需求三：多选拼值）
--
-- 说明：
--   - 团队成员的鉴权口径与执笔人一致：课程内容写权限 = 执笔人 ∪ 团队成员。
--   - 课程负责人仍复用 t_csys_course.authors / author_ids，本次不改动、不新增字段。
--   - 成员移除采用逻辑删除（sysflag=2），(course_id,user_id统一认证帐号) 唯一键保证同一课程不重复添加；
--     重新添加同一人时复用原行（置回 sysflag=0），因此唯一键不会与逻辑删除冲突。
-- =====================================================================

CREATE TABLE IF NOT EXISTS `t_csys_course_team_member` (
    `id`                 bigint       NOT NULL AUTO_INCREMENT COMMENT '主键',
    `course_id`          bigint       NOT NULL COMMENT '课程ID，始终指向总库课程 t_csys_course.id',
    `user_id`            varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '统一认证帐号，第三方email，对应sys_user.user_name',
    `user_name`          varchar(255) NOT NULL COMMENT '第三方userName教员姓名，与帐号分开',
    `sort`               int                   DEFAULT 1 COMMENT '排序',
    `create_time`        datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `creator`            varchar(64)           DEFAULT NULL COMMENT '创建人',
    `last_modified_time` datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `last_modifier`      varchar(64)           DEFAULT NULL COMMENT '更新人',
    `sysflag`            tinyint unsigned NOT NULL DEFAULT 0 COMMENT '删除标识：0正常 2删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_team_course_user` (`course_id`, `user_id`),
    KEY `idx_team_course` (`course_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT = '课程库团队成员';

-- 教法/学法由单选改多选后，编码/名称顿号拼接写入同列，放宽列宽避免超长截断。
ALTER TABLE `t_csys_teaching_plan_target_design`
    MODIFY COLUMN `teaching_method` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '教法（多选，顿号拼接）',
    MODIFY COLUMN `learning_method` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '学法（多选，顿号拼接）';
