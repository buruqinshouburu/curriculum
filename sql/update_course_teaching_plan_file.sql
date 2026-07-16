-- ============================================================
-- 课程教学计划 Word 文档生成功能 - 增量 DDL
-- 在 t_csys_course 上新增 4 个字段，用于保存生成文档的文件信息
-- （plan_file_id / plan_file_name / plan_download_url / plan_preview_url）
-- 与已有的 file_id/file_name 区分：后者为课程源文件上传，本组为生成文档
-- ============================================================

ALTER TABLE `t_csys_course`
  ADD COLUMN `plan_file_id`       varchar(255) DEFAULT NULL COMMENT '课程教学计划生成文档文件ID'  AFTER `file_name`,
  ADD COLUMN `plan_file_name`     varchar(255) DEFAULT NULL COMMENT '课程教学计划生成文档文件名'  AFTER `plan_file_id`,
  ADD COLUMN `plan_download_url`  varchar(500) DEFAULT NULL COMMENT '课程教学计划生成文档下载地址' AFTER `plan_file_name`,
  ADD COLUMN `plan_preview_url`   varchar(500) DEFAULT NULL COMMENT '课程教学计划生成文档预览地址' AFTER `plan_download_url`;
