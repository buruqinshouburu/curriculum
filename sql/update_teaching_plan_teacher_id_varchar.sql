-- 教学计划教员团队：teacher_id 由 bigint 改为 varchar（外库字符串主键）
-- 增量脚本，勿整表重建

ALTER TABLE t_csys_teaching_plan_teacher
    MODIFY COLUMN `teacher_id` varchar(100) DEFAULT NULL COMMENT '教员ID(外库字符串主键)';
