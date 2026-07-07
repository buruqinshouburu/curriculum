/*
 Navicat Premium Data Transfer

 Source Server         : 27.132.187.88
 Source Server Type    : MySQL
 Source Server Version : 80028
 Source Host           : 27.132.187.88:3306
 Source Schema         : doinner-curriculum-test-3.2

 Target Server Type    : MySQL
 Target Server Version : 80028
 File Encoding         : 65001

 Date: 25/06/2026 11:01:51
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for t_csys_course
-- ----------------------------
DROP TABLE IF EXISTS `t_csys_course`;
CREATE TABLE `t_csys_course`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键(id)',
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '课程名(name)',
  `code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '课程编号(code)',
  `type` tinyint NULL DEFAULT 1 COMMENT '课程类型(type)',
  `authors` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '执笔人名称(authors)',
  `author_ids` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '执笔人ID(author_ids)，多值拼接，对应sys_user.user_id',
  `college_id` bigint NULL DEFAULT NULL COMMENT '所属学院(college_id)',
  `teach_college_id` bigint NULL DEFAULT NULL COMMENT '施教学院(teach_college_id)',
  `en_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '英文名称(en_name)',
  `before_course_id` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '预修课程(before_curr_id)',
  `after_course_id` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '后续课程(after_curr_id)',
  `hours` double NULL DEFAULT NULL COMMENT '学时安排(hours)',
  `theory_hours` double NULL DEFAULT NULL COMMENT '理论学时(theory_hours)',
  `practice_hours` double NULL DEFAULT NULL COMMENT '实践学时(practice_hours)',
  `week_hours` double NULL DEFAULT NULL COMMENT '周学时(week_hours)',
  `teach_hours` double NULL DEFAULT NULL COMMENT '讲授学时(teach_hours)',
  `examine_hours` double NULL DEFAULT NULL COMMENT '考核学时(examine_hours)',
  `other_hours` double NULL DEFAULT NULL COMMENT '其他学时(other_hours)',
  `hours_unit` varchar(60) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '学时单位(hours_unit)',
  `credit` double NULL DEFAULT NULL COMMENT '学分(credit)',
  `course_prop` tinyint NULL DEFAULT NULL COMMENT '课程性质(curr_prop)',
  `course_type` tinyint NULL DEFAULT NULL COMMENT '课程大类(curr_type)',
  `course_attr` tinyint NULL DEFAULT NULL COMMENT '课程属性(curr_attr)',
  `location` varchar(60) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '实施地点(location)',
  `open_term` varchar(60) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '开课学期(open_term)',
  `summary` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '内容简介(summary)',
  `file_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '文件id(file_id)',
  `file_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '文件名称(file_name)',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间(create_time)',
  `creator` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '创建者(creat_by)',
  `last_modified_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间(update_time)',
  `last_modifier` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '更新者(update_by)',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '备注(remark)',
  `sysflag` tinyint UNSIGNED NOT NULL DEFAULT 0 COMMENT '删除标志(del_flag)',
  `dept_by` bigint NULL DEFAULT NULL COMMENT '创建部门',
  `status` tinyint NOT NULL DEFAULT 0 COMMENT '审核状态：0未审核1审核中2审核通过3审核失败',
  `analysis_status` tinyint NULL DEFAULT 0 COMMENT '分析状态：0，未分析；1，已分析',
  `course_Module` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '课程模块',
  `course_Module_Children` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '课程模块联级子字典',
  `semester_Schedule` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '学年',
  `spring_Autumn` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '春秋',
  `open_Year` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '开课年份',
  `major_Id` bigint NULL DEFAULT NULL COMMENT '专业id',
  `category_Id` bigint NULL DEFAULT NULL COMMENT '门类id',
  `exa_Method` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '考核方式',
  `program_Level` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '项目层级字段名',
  `time_Week` double NULL DEFAULT NULL COMMENT '时间安排(周),一位小数',
  `sub_Major_Id` bigint NULL DEFAULT NULL COMMENT '专业方向',
  `source_id` bigint NULL DEFAULT NULL COMMENT '源id',
  `template_type` tinyint NULL DEFAULT 2 COMMENT '模板类型',
  `version` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '版本',
  `bind_status` tinyint NULL DEFAULT 2 COMMENT '绑定状态1已绑定，2未绑定',
  `max_quote_count` bigint(20) UNSIGNED ZEROFILL NULL DEFAULT 00000000000000000001 COMMENT '最大引用次数',
  `enable_flag` tinyint(10) UNSIGNED ZEROFILL NULL DEFAULT 0000000001 COMMENT '是否启用 1：是 0：否',
  `education_level` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '培养层次 字典表sys_education_level',
  `build_status` tinyint NULL DEFAULT 0 COMMENT '知识体系绑定状态',
  `has_work` tinyint NULL DEFAULT 0 COMMENT '是否提交大作业 0：否 1：是',
  `academic_terms_number` int NULL DEFAULT NULL COMMENT '排课学期数量',
  `unit` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '时间安排单位 sys_course_unit',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `source_index`(`source_id` ASC) USING BTREE,
  INDEX `course_name`(`name` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 11111112287 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '课程表' ROW_FORMAT = DYNAMIC;

-- 课程执笔人由"前端录入名称"改为"选择系统用户(sys_user)"：
-- 原 authors 列语义改为"执笔人名称"，新增 author_ids 列存"执笔人ID(多值拼接)"。
-- 已有库升级执行：
-- ALTER TABLE `t_csys_course` ADD COLUMN `author_ids` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '执笔人ID(author_ids)，多值拼接，对应sys_user.user_id' AFTER `authors`;
-- ALTER TABLE `t_csys_course` MODIFY COLUMN `authors` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '执笔人名称(authors)';


-- ----------------------------
-- Table structure for t_csys_course_knowledge_point
-- ----------------------------
DROP TABLE IF EXISTS `t_csys_course_knowledge_point`;
CREATE TABLE `t_csys_course_knowledge_point`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `sort` int NULL DEFAULT NULL,
  `unit_id` bigint NULL DEFAULT NULL,
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间(create_time)',
  `creator` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '创建者(creat_by)',
  `last_modified_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间(update_time)',
  `last_modifier` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '更新者(update_by)',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `unit_id_index`(`unit_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 19557 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_csys_course_knowledge_unit
-- ----------------------------
DROP TABLE IF EXISTS `t_csys_course_knowledge_unit`;
CREATE TABLE `t_csys_course_knowledge_unit`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `sort` int NULL DEFAULT NULL,
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间(create_time)',
  `creator` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '创建者(creat_by)',
  `last_modified_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间(update_time)',
  `last_modifier` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '更新者(update_by)',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3525 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = DYNAMIC;




-- ----------------------------
-- Table structure for t_csys_course_ref_graduation
-- ----------------------------
DROP TABLE IF EXISTS `t_csys_course_ref_graduation`;
CREATE TABLE `t_csys_course_ref_graduation`  (
  `course_id` bigint NULL DEFAULT NULL COMMENT '课程id',
  `graduation_id` bigint NULL DEFAULT NULL COMMENT '毕业标准id',
  `course_target_id` bigint NULL DEFAULT NULL COMMENT '课程目标id',
  `college_Id` bigint NULL DEFAULT NULL COMMENT '学院id',
  `category_Id` bigint NULL DEFAULT NULL COMMENT '门类id',
  `major_Id` bigint NULL DEFAULT NULL COMMENT '专业id',
  INDEX `course_graduation_index`(`course_id` ASC, `major_Id` ASC, `graduation_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_german2_ci COMMENT = '课程和毕业标准' ROW_FORMAT = DYNAMIC;


-- ----------------------------
-- Table structure for t_csys_course_ref_knowledge_unit
-- ----------------------------
DROP TABLE IF EXISTS `t_csys_course_ref_knowledge_unit`;
CREATE TABLE `t_csys_course_ref_knowledge_unit`  (
  `course_id` bigint NOT NULL,
  `course_unit_id` bigint NULL DEFAULT NULL,
  INDEX `course_index`(`course_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = DYNAMIC;




-- ----------------------------
-- Table structure for t_csys_knowledge_check_log
-- ----------------------------
DROP TABLE IF EXISTS `t_csys_knowledge_check_log`;
CREATE TABLE `t_csys_knowledge_check_log`  (
  `id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '主键ID',
  `source_course_id` bigint NULL DEFAULT NULL COMMENT '一个课程的ID',
  `source_course_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '一个知识领域的名称',
  `source_unit_id` bigint NULL DEFAULT NULL COMMENT '一个知识领域的知识单元ID',
  `source_unit_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '一个知识领域的知识单元ID',
  `source_point_id` bigint NULL DEFAULT NULL COMMENT '一个知识领域的知识点ID',
  `source_point_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '一个知识领域的知识点名称',
  `target_course_id` bigint NULL DEFAULT NULL COMMENT '另外一个课程的ID',
  `target_course_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '另外一个知识领域的名称',
  `target_unit_id` bigint NULL DEFAULT NULL COMMENT '另外一个知识领域的知识单元ID',
  `target_unit_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '另外一个知识领域的知识单元名称',
  `target_point_id` bigint NULL DEFAULT NULL COMMENT '另外一个知识领域的知识点ID',
  `target_point_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '另外一个知识领域的知识点名称',
  `start` tinyint(1) NULL DEFAULT NULL COMMENT '是否相似',
  `remark` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '审核意见',
  `training_scheme_id` bigint NULL DEFAULT NULL COMMENT '培养方案ID',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `source_course_ID`(`source_course_id` ASC) USING BTREE,
  INDEX `target_course_id`(`target_course_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '知识点查重意见表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_csys_knowledge_chek_total
-- ----------------------------
DROP TABLE IF EXISTS `t_csys_knowledge_chek_total`;
CREATE TABLE `t_csys_knowledge_chek_total`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `source_course_id` bigint NULL DEFAULT NULL COMMENT '课程D',
  `total_unit_num` bigint NULL DEFAULT NULL COMMENT '知识单元数量',
  `total_point_num` bigint NULL DEFAULT NULL COMMENT '知识点数量',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `course_id`(`source_course_id` ASC) USING BTREE COMMENT '课程id唯一'
) ENGINE = InnoDB AUTO_INCREMENT = 10356 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '课程知识单元，知识点，统计表' ROW_FORMAT = DYNAMIC;


-- ----------------------------
-- Table structure for t_csys_knowledge_no_check_log
-- ----------------------------
DROP TABLE IF EXISTS `t_csys_knowledge_no_check_log`;
CREATE TABLE `t_csys_knowledge_no_check_log`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `source_course_id` bigint NULL DEFAULT NULL COMMENT '一个课程的ID',
  `target_course_id` bigint NULL DEFAULT NULL COMMENT '另外一个课程的ID',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `课程联合索引`(`source_course_id` ASC, `target_course_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 898344 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '没有重复知识点的ID组合表' ROW_FORMAT = DYNAMIC;




-- ----------------------------
-- Table structure for t_csys_scheme_ref_major
-- ----------------------------
DROP TABLE IF EXISTS `t_csys_scheme_ref_major`;
CREATE TABLE `t_csys_scheme_ref_major`  (
  `scheme_id` bigint NOT NULL COMMENT '培养方案ID',
  `major_id` bigint NOT NULL COMMENT '专业ID',
  INDEX `scheme_id_index`(`scheme_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '培养方案包含专业(注：不是专业类，一个培养方案对应一个专业类，包含多个专业)' ROW_FORMAT = DYNAMIC;




-- ----------------------------
-- Table structure for t_csys_std_cultivation_target
-- ----------------------------
DROP TABLE IF EXISTS `t_csys_std_cultivation_target`;
CREATE TABLE `t_csys_std_cultivation_target`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `name` varchar(4000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '名称',
  `code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '编码',
  `parent_id` bigint NOT NULL COMMENT '父级id',
  `leaf` tinyint NULL DEFAULT NULL COMMENT '是否叶子(leaf)',
  `level` tinyint NULL DEFAULT NULL COMMENT '层级(level)',
  `url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '路径(url)',
  `college_id` bigint NULL DEFAULT NULL COMMENT '学院(college_id)',
  `major_id` bigint NULL DEFAULT NULL COMMENT '专业(major)',
  `sub_major_id` bigint NULL DEFAULT NULL COMMENT '细分专业(sub_major)',
  `class_id` tinyint NULL DEFAULT 0 COMMENT '技术指挥分类(class)    0：未分类、1：技术类、2：指挥类',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间(create_time)',
  `creator` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '创建者(creator)',
  `last_modified_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间(last_modified_time)',
  `last_modifier` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '更新者(last_modifier)',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注(remark)',
  `sysflag` tinyint UNSIGNED NOT NULL DEFAULT 0 COMMENT '删除标志(sysflag)',
  `version` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '版本',
  `category_id` bigint NULL DEFAULT NULL COMMENT '大类Id',
  `training_scheme_id` bigint NULL DEFAULT NULL COMMENT '培养方案ID',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `TRAINING_SCHEME_INDEX`(`training_scheme_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 22549 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '培养目标表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_csys_std_graduation
-- ----------------------------
DROP TABLE IF EXISTS `t_csys_std_graduation`;
CREATE TABLE `t_csys_std_graduation`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `name` varchar(4000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '名称',
  `code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '编码',
  `cultivation_target_id` bigint NULL DEFAULT NULL COMMENT '培养目标id',
  `parent_id` bigint NOT NULL COMMENT '父级',
  `leaf` tinyint NULL DEFAULT NULL COMMENT '是否叶子(leaf)',
  `level` tinyint NULL DEFAULT NULL COMMENT '层级(level)',
  `url` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '路径(url)',
  `college_id` bigint NULL DEFAULT NULL COMMENT '学院(college_id)',
  `major_id` bigint NULL DEFAULT NULL COMMENT '专业(major)',
  `sub_major_id` bigint NULL DEFAULT NULL COMMENT '细分专业(sub_major)',
  `class_id` tinyint NULL DEFAULT 0 COMMENT '技术指挥分类(class)    0：未分类、1：技术类、2：指挥类',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间(create_time)',
  `creator` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '创建者(creator)',
  `last_modified_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间(last_modified_time)',
  `last_modifier` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '更新者(last_modifier)',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注(remark)',
  `sysflag` tinyint UNSIGNED NOT NULL DEFAULT 0 COMMENT '删除标志(sysflag)',
  `version` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '版本',
  `category_id` bigint NULL DEFAULT NULL COMMENT '大类ID',
  `type` tinyint NULL DEFAULT 2 COMMENT '1毕业要求模板，2毕业要求',
  `graduation_type` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '1:知识，2：能力，3：素质',
  `source_id` bigint NULL DEFAULT NULL COMMENT '源id',
  `scheme_Id` bigint NULL DEFAULT NULL COMMENT '培养方案id',
  `education_level` bigint NULL DEFAULT NULL COMMENT '培养层次 字典表sys_education_level',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 46074 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '毕业标准表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_csys_std_graduation_ref_cultivation_target
-- ----------------------------
DROP TABLE IF EXISTS `t_csys_std_graduation_ref_cultivation_target`;
CREATE TABLE `t_csys_std_graduation_ref_cultivation_target`  (
  `graduation_id` bigint NOT NULL COMMENT '毕业标准id',
  `cultivation_target_id` bigint NOT NULL COMMENT '培养目标id',
  PRIMARY KEY (`graduation_id`, `cultivation_target_id`) USING BTREE,
  INDEX `graduation_culTraget_index`(`graduation_id` ASC, `cultivation_target_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '毕业标准与培养目标关联' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_csys_std_major
-- ----------------------------
DROP TABLE IF EXISTS `t_csys_std_major`;
CREATE TABLE `t_csys_std_major`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `name` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '名称',
  `code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '编码',
  `parent_id` bigint NOT NULL COMMENT '父级id',
  `level` tinyint NULL DEFAULT NULL COMMENT '层级(level)',
  `college_id` bigint NULL DEFAULT NULL COMMENT '学院(college_id)',
  `category_id` bigint NULL DEFAULT NULL COMMENT '细门类id',
  `class_id` varchar(2) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '0' COMMENT '细分专业分类(0:未分类,1:技术类,2:指挥类,3:技术/技术类)',
  `applicable_object` varchar(225) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '适用对象 字典表sys_education_level',
  `leaf` tinyint UNSIGNED NULL DEFAULT 1 COMMENT '是否叶子(leaf)',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间(create_time)',
  `creator` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '创建者(creator)',
  `last_modified_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间(last_modified_time)',
  `last_modifier` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '更新者(last_modifier)',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注(remark)',
  `sysflag` tinyint UNSIGNED NOT NULL DEFAULT 0 COMMENT '删除标志(sysflag)',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 272 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '学院专业表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_csys_teaching_program
-- ----------------------------
DROP TABLE IF EXISTS `t_csys_teaching_program`;
CREATE TABLE `t_csys_teaching_program`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `course_id` bigint NULL DEFAULT NULL COMMENT '课程id',
  `version` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_german2_ci NULL DEFAULT NULL COMMENT '教学计划版本',
  `term` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_german2_ci NULL DEFAULT NULL COMMENT '学期',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `creator` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_german2_ci NULL DEFAULT NULL COMMENT '创建人',
  `last_modified_time` datetime NULL DEFAULT NULL COMMENT '最后修改时间',
  `last_modifier` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_german2_ci NULL DEFAULT NULL COMMENT '最后修改人',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_german2_ci NULL DEFAULT NULL COMMENT '备注',
  `sysflag` tinyint(1) NULL DEFAULT 0 COMMENT '标志位',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_german2_ci COMMENT = '教学计划表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_csys_teaching_programme_attribute
-- ----------------------------
DROP TABLE IF EXISTS `t_csys_teaching_programme_attribute`;
CREATE TABLE `t_csys_teaching_programme_attribute`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'id',
  `parent_Id` bigint NULL DEFAULT NULL COMMENT '父id',
  `instance_id` bigint NULL DEFAULT NULL COMMENT '教学大纲实例id',
  `attribute_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_german2_ci NULL DEFAULT NULL COMMENT '属性名称',
  `attribute_value` varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_german2_ci NULL DEFAULT NULL COMMENT '属性值',
  `template_id` bigint NULL DEFAULT NULL COMMENT '对应t_csys_teaching_programme_template id',
  `template_parent_Id` bigint NULL DEFAULT NULL COMMENT '对应t_csys_teaching_programme_template父id',
  `sort` int NULL DEFAULT 1 COMMENT '排序',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 59832 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_german2_ci COMMENT = '教学大纲属性' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_csys_teaching_programme_instance
-- ----------------------------
DROP TABLE IF EXISTS `t_csys_teaching_programme_instance`;
CREATE TABLE `t_csys_teaching_programme_instance`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'id',
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_german2_ci NULL DEFAULT NULL COMMENT '名称',
  `version` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_german2_ci NULL DEFAULT NULL COMMENT '版本',
  `outline_id` bigint NULL DEFAULT NULL COMMENT '模板id,顶层的',
  `college_id` bigint NULL DEFAULT NULL COMMENT '学院id',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `creator` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_german2_ci NULL DEFAULT NULL COMMENT '创建人',
  `last_modified_time` datetime NULL DEFAULT NULL COMMENT '修改时间',
  `last_modifier` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_german2_ci NULL DEFAULT NULL COMMENT '最后修改人',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_german2_ci NULL DEFAULT NULL COMMENT '备注',
  `sysflag` tinyint NULL DEFAULT 0 COMMENT '标志位',
  `status` tinyint NULL DEFAULT 0 COMMENT '审核状态：0未审核1审核中2审核通过3审核失败',
  `file_id` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_german2_ci NULL DEFAULT NULL COMMENT '文件ID',
  `download_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_german2_ci NULL DEFAULT NULL COMMENT '下载地址',
  `preview_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_german2_ci NULL DEFAULT NULL COMMENT '预览地址',
  `category_id` bigint NULL DEFAULT NULL COMMENT '学科门类',
  `major_id` bigint NULL DEFAULT NULL COMMENT '专业类',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1815 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_german2_ci COMMENT = '教学大纲实例' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_csys_teaching_programme_instance_ex_ref_major
-- ----------------------------
DROP TABLE IF EXISTS `t_csys_teaching_programme_instance_ex_ref_major`;
CREATE TABLE `t_csys_teaching_programme_instance_ex_ref_major`  (
  `extract_id` bigint NOT NULL COMMENT 't_csys_teaching_programme_instance_extract表id',
  `major_id` bigint NOT NULL COMMENT '专业表id'
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_german2_ci COMMENT = '文档抽取和专业关联表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_csys_teaching_programme_instance_extract
-- ----------------------------
DROP TABLE IF EXISTS `t_csys_teaching_programme_instance_extract`;
CREATE TABLE `t_csys_teaching_programme_instance_extract`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'id',
  `instance_Id` bigint NULL DEFAULT NULL COMMENT 't_csys_teaching_programme_instance表id',
  `learn_Institution` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_german2_ci NULL DEFAULT NULL COMMENT '学制',
  `category_Id` bigint NULL DEFAULT NULL COMMENT '门类id',
  `degree` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_german2_ci NULL DEFAULT NULL COMMENT '授予学位',
  `total_Hour` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_german2_ci NULL DEFAULT NULL COMMENT '总学时',
  `total_Credit` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_german2_ci NULL DEFAULT NULL COMMENT '总学分',
  `float_Rate` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_german2_ci NULL DEFAULT NULL COMMENT '上下浮动比例',
  `political_Hour` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_german2_ci NULL DEFAULT NULL COMMENT '政治理论模块学时',
  `military_Hour` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_german2_ci NULL DEFAULT NULL COMMENT '军事基础模块学时',
  `science_Hour` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_german2_ci NULL DEFAULT NULL COMMENT '科学文化模块学时',
  `basics_Hour` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_german2_ci NULL DEFAULT NULL COMMENT '学科基础模块学时',
  `status` tinyint NULL DEFAULT 0 COMMENT '抽取状态0 默认 1 ai抽取中 2 抽取成功 9 抽取失败',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `creator` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_german2_ci NULL DEFAULT NULL COMMENT '创建人',
  `last_modified_time` datetime NULL DEFAULT NULL COMMENT '最后修改时间',
  `last_modifier` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_german2_ci NULL DEFAULT NULL COMMENT '最后修改人',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_german2_ci NULL DEFAULT NULL COMMENT '备注',
  `sysflag` tinyint NULL DEFAULT 0 COMMENT '标志位',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 24 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_german2_ci COMMENT = '教育大纲从文档抽取内容' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_csys_teaching_programme_outline
-- ----------------------------
DROP TABLE IF EXISTS `t_csys_teaching_programme_outline`;
CREATE TABLE `t_csys_teaching_programme_outline`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_german2_ci NULL DEFAULT NULL COMMENT '大纲名称模板',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `creator` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_german2_ci NULL DEFAULT NULL COMMENT '创建人',
  `last_modified_time` datetime NULL DEFAULT NULL COMMENT '最后修改时间',
  `last_modifier` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_german2_ci NULL DEFAULT NULL COMMENT '最后修改人',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_german2_ci NULL DEFAULT NULL COMMENT '备注',
  `sysflag` tinyint NULL DEFAULT 0 COMMENT '标志位',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 70 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_german2_ci COMMENT = '教学大纲模板实例' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_csys_teaching_programme_template
-- ----------------------------
DROP TABLE IF EXISTS `t_csys_teaching_programme_template`;
CREATE TABLE `t_csys_teaching_programme_template`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'id',
  `outline_id` bigint NULL DEFAULT NULL COMMENT '大纲id',
  `attribute_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_german2_ci NULL DEFAULT NULL COMMENT '模板名称',
  `parent_id` bigint NULL DEFAULT NULL COMMENT '父id',
  `level` tinyint NULL DEFAULT 1 COMMENT '层级',
  `leaf` tinyint NULL DEFAULT 1 COMMENT '是否叶子节点',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `creator` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_german2_ci NULL DEFAULT NULL COMMENT '创建人',
  `last_modified_time` datetime NULL DEFAULT NULL COMMENT '修改时间',
  `last_modifier` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_german2_ci NULL DEFAULT NULL COMMENT '修改人',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_german2_ci NULL DEFAULT NULL COMMENT '备注',
  `sysflag` tinyint NULL DEFAULT 0 COMMENT '标志位',
  `sort` int NULL DEFAULT 1 COMMENT '排序',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1297 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_german2_ci COMMENT = '教学大纲模板' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_csys_training_scheme
-- ----------------------------
DROP TABLE IF EXISTS `t_csys_training_scheme`;
CREATE TABLE `t_csys_training_scheme`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `standard_id` bigint NULL DEFAULT NULL COMMENT '培养标准id',
  `name` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '培养方案名称',
  `category_id` bigint NULL DEFAULT NULL COMMENT '门类id',
  `college_id` bigint NULL DEFAULT NULL COMMENT '学院',
  `file_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '上传文件id',
  `file_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '上传文件名称',
  `major_id` bigint NULL DEFAULT NULL COMMENT '专业(major)',
  `sub_major_id` bigint NULL DEFAULT NULL COMMENT '技术指挥分类(class)    0：未分类、1：技术类、2：指挥类',
  `class_id` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '年度',
  `year` tinyint NULL DEFAULT NULL COMMENT '年度',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间(create_time)',
  `creator` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '创建者(creator)',
  `last_modified_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间(last_modified_time)',
  `last_modifier` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '更新者(last_modifier)',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '备注(remark)',
  `sysflag` tinyint UNSIGNED NOT NULL DEFAULT 0 COMMENT '删除标志(sysflag)',
  `program_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '培养规划名称',
  `plan_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '教学计划名称',
  `history_version_id` bigint NULL DEFAULT NULL COMMENT '培养方案来源版本id',
  `history_version` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '培养方案来源版本',
  `version` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '培养规划版本',
  `object_type` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '培养对象',
  `education` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '培养对象学历',
  `academic_type` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '培养对象学制类型',
  `duration_type` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '培养对象学制年限',
  `degree` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '毕业授予学士学位类型',
  `instance_id` bigint NULL DEFAULT NULL COMMENT '教学大纲id,t_csys_teaching_programme_instance',
  `status` int NULL DEFAULT 0 COMMENT '审核状态：0未审核1审核中2审核通过3审核失败',
  `download_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '培养方案下载地址',
  `preview_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '培养方案预览地址',
  `education_level` varchar(225) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '适用对象sys_education_level',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 314 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '培养方案表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_csys_training_scheme_category
-- ----------------------------
DROP TABLE IF EXISTS `t_csys_training_scheme_category`;
CREATE TABLE `t_csys_training_scheme_category`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `parent_id` bigint NOT NULL COMMENT '父级id',
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '名称',
  `leaf` tinyint(1) NULL DEFAULT 0 COMMENT '是否叶子节点(0:否，1:是)',
  `level` tinyint(1) NULL DEFAULT NULL COMMENT '层级',
  `url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '路径',
  `system_type` tinyint UNSIGNED NULL DEFAULT NULL COMMENT '四大类(1:优势工科专业,2:理科基础专业,3:文学法学专业,4:军事管理专业)',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间(create_time)',
  `creator` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '创建者(creator)',
  `last_modified_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间(last_modified_time)',
  `last_modifier` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '更新者(last_modifier)',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '备注(remark)',
  `sysflag` tinyint UNSIGNED NOT NULL DEFAULT 0 COMMENT '删除标志(sysflag)',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 56 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '培养方案门类' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_csys_training_scheme_course_schedule
-- ----------------------------
DROP TABLE IF EXISTS `t_csys_training_scheme_course_schedule`;
CREATE TABLE `t_csys_training_scheme_course_schedule`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `scheme_id` bigint NOT NULL COMMENT '培养方案id',
  `course_id` bigint NOT NULL COMMENT '课程id',
  `type` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '课程子模块',
  `term` tinyint NULL DEFAULT NULL COMMENT '学期:1:大一上\r\n            2:大一下\r\n            3:大二上\r\n            4:大二下\r\n            5:大三上\r\n            6:大三下\r\n            7:大四上\r\n            8:大四下',
  `hours` double NULL DEFAULT NULL COMMENT '总课时',
  `theory_hours` double NULL DEFAULT NULL COMMENT '理论课时',
  `practice_hours` double NULL DEFAULT NULL COMMENT '实践课时',
  `checked` tinyint UNSIGNED NOT NULL DEFAULT 1 COMMENT '课程是否已选',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间(create_time)',
  `creator` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '创建者(creator)',
  `last_modified_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间(last_modified_time)',
  `last_modifier` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '更新者(last_modifier)',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '备注(remark)',
  `sysflag` tinyint UNSIGNED NOT NULL DEFAULT 0 COMMENT '删除标志(sysflag)',
  `teach_hours` double NULL DEFAULT NULL COMMENT '讲授学时',
  `course_attr` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '课程类型:1 必修 2：选修 3：任选',
  `credits` double NULL DEFAULT NULL COMMENT '学分',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `scheme_index`(`scheme_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 23027 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '培养方案排课表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_csys_training_scheme_ref_course
-- ----------------------------
DROP TABLE IF EXISTS `t_csys_training_scheme_ref_course`;
CREATE TABLE `t_csys_training_scheme_ref_course`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `scheme_id` bigint NOT NULL COMMENT '培养方案id',
  `course_id` bigint NOT NULL COMMENT '课程id',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `scheme_course_index`(`scheme_id` ASC, `course_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 11635 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '培养方案课程关联' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_csys_training_scheme_week
-- ----------------------------
DROP TABLE IF EXISTS `t_csys_training_scheme_week`;
CREATE TABLE `t_csys_training_scheme_week`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `scheme_id` bigint NULL DEFAULT NULL COMMENT '培养方案id',
  `course_teaching` int NULL DEFAULT NULL COMMENT '课程教学',
  `practice_teaching` int NULL DEFAULT NULL COMMENT '集中实践教学',
  `vacation` int NULL DEFAULT NULL COMMENT '假期休整',
  `motor_driven` int NULL DEFAULT NULL COMMENT '机动',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 29 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '修业时间分配情况' ROW_FORMAT = DYNAMIC;


-- ----------------------------
-- Table structure for t_csys_course_invoke_delete_log
-- ----------------------------
DROP TABLE IF EXISTS `t_csys_course_invoke_delete_log`;
CREATE TABLE `t_csys_course_invoke_delete_log`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `delete_batch_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '删除批次ID，同一次删除操作的多条记录相同',
  `course_id` bigint NULL DEFAULT NULL COMMENT '被删除的调用课程ID',
  `course_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '被删除的课程名称',
  `course_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '被删除的课程编号',
  `source_id` bigint NULL DEFAULT NULL COMMENT '源课程ID',
  `scheme_id` bigint NULL DEFAULT NULL COMMENT '所属培养方案ID',
  `template_type` tinyint NULL DEFAULT NULL COMMENT '模板类型 1-总库课程 2-调用课程',
  `major_id` bigint NULL DEFAULT NULL COMMENT '专业ID',
  `category_id` bigint NULL DEFAULT NULL COMMENT '门类ID',
  `version` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '版本',
  `operator` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '操作人',
  `operation_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `course_id_index`(`course_id` ASC) USING BTREE,
  INDEX `delete_batch_id_index`(`delete_batch_id` ASC) USING BTREE,
  INDEX `source_id_index`(`source_id` ASC) USING BTREE,
  INDEX `scheme_id_index`(`scheme_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '调用课程删除日志表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- View structure for cc
-- ----------------------------
DROP VIEW IF EXISTS `cc`;
CREATE ALGORITHM = UNDEFINED SQL SECURITY DEFINER VIEW `cc` AS select `c`.`id` AS `course_id`,`c`.`name` AS `course_name`,`cc`.`id` AS `chapter_id`,`cc`.`name` AS `chapter_name` from (`t_csys_course` `c` join `t_csys_course_chapter` `cc`) where (`c`.`id` = `cc`.`course_id`);

-- ----------------------------
-- View structure for view_quote_course
-- ----------------------------
DROP VIEW IF EXISTS `view_quote_course`;
CREATE ALGORITHM = UNDEFINED SQL SECURITY DEFINER VIEW `view_quote_course` AS select `t_csys_course`.`source_id` AS `source_id`,count(distinct `t_csys_course`.`id`) AS `qoute_count`,`t_csys_course`.`version` AS `version` from `t_csys_course` where ((`t_csys_course`.`sysflag` = 0) and (`t_csys_course`.`template_type` = 2) and (`t_csys_course`.`course_Module` = '69a7f32e2dc370362ef3ee6e') and (`t_csys_course`.`source_id` is not null)) group by `t_csys_course`.`source_id`,`t_csys_course`.`version`;

SET FOREIGN_KEY_CHECKS = 1;
