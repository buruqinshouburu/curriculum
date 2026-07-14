-- MySQL dump 10.13  Distrib 8.0.32, for Win64 (x86_64)
--
-- Host: localhost    Database: my-curr
-- ------------------------------------------------------
-- Server version	8.0.32

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Current Database: `my-curr`
--

CREATE DATABASE /*!32312 IF NOT EXISTS*/ `my-curr` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_german2_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;

USE `my-curr`;

--
-- Table structure for table `t_csys_course`
--

DROP TABLE IF EXISTS `t_csys_course`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `t_csys_course` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键(id)',
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '课程名(name)',
  `code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '课程编号(code)',
  `type` tinyint DEFAULT '1' COMMENT '课程类型(type)',
  `authors` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '执笔人名称(authors)',
  `author_ids` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '执笔人ID(author_ids)，多值拼接，对应sys_user.user_id',
  `college_id` bigint DEFAULT NULL COMMENT '所属学院(college_id)',
  `teach_college_id` bigint DEFAULT NULL COMMENT '施教学院(teach_college_id)',
  `en_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '英文名称(en_name)',
  `before_course_id` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '预修课程(before_curr_id)',
  `after_course_id` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '后续课程(after_curr_id)',
  `hours` double DEFAULT NULL COMMENT '学时安排(hours)',
  `theory_hours` double DEFAULT NULL COMMENT '理论学时(theory_hours)',
  `practice_hours` double DEFAULT NULL COMMENT '实践学时(practice_hours)',
  `week_hours` double DEFAULT NULL COMMENT '周学时(week_hours)',
  `teach_hours` double DEFAULT NULL COMMENT '讲授学时(teach_hours)',
  `examine_hours` double DEFAULT NULL COMMENT '考核学时(examine_hours)',
  `other_hours` double DEFAULT NULL COMMENT '其他学时(other_hours)',
  `hours_unit` varchar(60) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '学时单位(hours_unit)',
  `credit` double DEFAULT NULL COMMENT '学分(credit)',
  `course_prop` tinyint DEFAULT NULL COMMENT '课程性质(curr_prop)',
  `course_type` tinyint DEFAULT NULL COMMENT '课程大类(curr_type)',
  `course_attr` tinyint DEFAULT NULL COMMENT '课程属性(curr_attr)',
  `location` varchar(60) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '实施地点(location)',
  `open_term` varchar(60) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '开课学期(open_term)',
  `summary` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '内容简介(summary)',
  `file_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '文件id(file_id)',
  `file_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '文件名称(file_name)',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间(create_time)',
  `creator` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '创建者(creat_by)',
  `last_modified_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间(update_time)',
  `last_modifier` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '更新者(update_by)',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '备注(remark)',
  `sysflag` tinyint unsigned NOT NULL DEFAULT '0' COMMENT '删除标志(del_flag)',
  `dept_by` bigint DEFAULT NULL COMMENT '创建部门',
  `status` tinyint NOT NULL DEFAULT '0' COMMENT '审核状态：0未审核1审核中2审核通过3审核失败',
  `analysis_status` tinyint DEFAULT '0' COMMENT '分析状态：0，未分析；1，已分析',
  `course_Module` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '课程模块',
  `course_Module_Children` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '课程模块联级子字典',
  `semester_Schedule` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '学年',
  `spring_Autumn` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '春秋',
  `open_Year` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '开课年份',
  `major_Id` bigint DEFAULT NULL COMMENT '专业id',
  `category_Id` bigint DEFAULT NULL COMMENT '门类id',
  `exa_Method` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '考核方式',
  `program_Level` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '项目层级字段名',
  `time_Week` double DEFAULT NULL COMMENT '时间安排(周),一位小数',
  `sub_Major_Id` bigint DEFAULT NULL COMMENT '专业方向',
  `source_id` bigint DEFAULT NULL COMMENT '源id',
  `template_type` tinyint DEFAULT '2' COMMENT '模板类型',
  `version` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '版本',
  `bind_status` tinyint DEFAULT '2' COMMENT '绑定状态1已绑定，2未绑定',
  `max_quote_count` bigint(20) unsigned zerofill DEFAULT '00000000000000000001' COMMENT '最大引用次数',
  `enable_flag` tinyint(10) unsigned zerofill DEFAULT '0000000001' COMMENT '是否启用 1：是 0：否',
  `education_level` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '培养层次 字典表sys_education_level',
  `build_status` tinyint DEFAULT '0' COMMENT '知识体系绑定状态',
  `has_work` tinyint DEFAULT '0' COMMENT '是否提交大作业 0：否 1：是',
  `academic_terms_number` int DEFAULT NULL COMMENT '排课学期数量',
  `unit` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '时间安排单位 sys_course_unit',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `source_index` (`source_id`) USING BTREE,
  KEY `course_name` (`name`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=11111112289 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC COMMENT='课程表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `t_csys_course`
--

LOCK TABLES `t_csys_course` WRITE;
/*!40000 ALTER TABLE `t_csys_course` DISABLE KEYS */;
INSERT INTO `t_csys_course` VALUES (11111112288,'测试课程一下','1',1,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'2026-07-13 15:44:30',NULL,'2026-07-13 15:44:38',NULL,NULL,0,NULL,0,0,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,2,NULL,2,00000000000000000001,0000000001,NULL,0,0,NULL,NULL);
/*!40000 ALTER TABLE `t_csys_course` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `t_csys_course_invoke_delete_log`
--

DROP TABLE IF EXISTS `t_csys_course_invoke_delete_log`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `t_csys_course_invoke_delete_log` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `delete_batch_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '删除批次ID，同一次删除操作的多条记录相同',
  `course_id` bigint DEFAULT NULL COMMENT '被删除的调用课程ID',
  `course_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '被删除的课程名称',
  `course_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '被删除的课程编号',
  `source_id` bigint DEFAULT NULL COMMENT '源课程ID',
  `scheme_id` bigint DEFAULT NULL COMMENT '所属培养方案ID',
  `template_type` tinyint DEFAULT NULL COMMENT '模板类型 1-总库课程 2-调用课程',
  `major_id` bigint DEFAULT NULL COMMENT '专业ID',
  `category_id` bigint DEFAULT NULL COMMENT '门类ID',
  `version` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '版本',
  `operator` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '操作人',
  `operation_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `course_id_index` (`course_id`) USING BTREE,
  KEY `delete_batch_id_index` (`delete_batch_id`) USING BTREE,
  KEY `source_id_index` (`source_id`) USING BTREE,
  KEY `scheme_id_index` (`scheme_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC COMMENT='调用课程删除日志表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `t_csys_course_invoke_delete_log`
--

LOCK TABLES `t_csys_course_invoke_delete_log` WRITE;
/*!40000 ALTER TABLE `t_csys_course_invoke_delete_log` DISABLE KEYS */;
/*!40000 ALTER TABLE `t_csys_course_invoke_delete_log` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `t_csys_course_knowledge_point`
--

DROP TABLE IF EXISTS `t_csys_course_knowledge_point`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `t_csys_course_knowledge_point` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  `sort` int DEFAULT NULL,
  `unit_id` bigint DEFAULT NULL,
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间(create_time)',
  `creator` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '创建者(creat_by)',
  `last_modified_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间(update_time)',
  `last_modifier` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '更新者(update_by)',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `unit_id_index` (`unit_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=19557 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `t_csys_course_knowledge_point`
--

LOCK TABLES `t_csys_course_knowledge_point` WRITE;
/*!40000 ALTER TABLE `t_csys_course_knowledge_point` DISABLE KEYS */;
/*!40000 ALTER TABLE `t_csys_course_knowledge_point` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `t_csys_course_knowledge_unit`
--

DROP TABLE IF EXISTS `t_csys_course_knowledge_unit`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `t_csys_course_knowledge_unit` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  `sort` int DEFAULT NULL,
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间(create_time)',
  `creator` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '创建者(creat_by)',
  `last_modified_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间(update_time)',
  `last_modifier` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '更新者(update_by)',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=3525 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `t_csys_course_knowledge_unit`
--

LOCK TABLES `t_csys_course_knowledge_unit` WRITE;
/*!40000 ALTER TABLE `t_csys_course_knowledge_unit` DISABLE KEYS */;
/*!40000 ALTER TABLE `t_csys_course_knowledge_unit` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `t_csys_course_ref_graduation`
--

DROP TABLE IF EXISTS `t_csys_course_ref_graduation`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `t_csys_course_ref_graduation` (
  `course_id` bigint DEFAULT NULL COMMENT '课程id',
  `graduation_id` bigint DEFAULT NULL COMMENT '毕业标准id',
  `course_target_id` bigint DEFAULT NULL COMMENT '课程目标id',
  `college_Id` bigint DEFAULT NULL COMMENT '学院id',
  `category_Id` bigint DEFAULT NULL COMMENT '门类id',
  `major_Id` bigint DEFAULT NULL COMMENT '专业id',
  KEY `course_graduation_index` (`course_id`,`major_Id`,`graduation_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_german2_ci ROW_FORMAT=DYNAMIC COMMENT='课程和毕业标准';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `t_csys_course_ref_graduation`
--

LOCK TABLES `t_csys_course_ref_graduation` WRITE;
/*!40000 ALTER TABLE `t_csys_course_ref_graduation` DISABLE KEYS */;
/*!40000 ALTER TABLE `t_csys_course_ref_graduation` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `t_csys_course_ref_knowledge_unit`
--

DROP TABLE IF EXISTS `t_csys_course_ref_knowledge_unit`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `t_csys_course_ref_knowledge_unit` (
  `course_id` bigint NOT NULL,
  `course_unit_id` bigint DEFAULT NULL,
  KEY `course_index` (`course_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `t_csys_course_ref_knowledge_unit`
--

LOCK TABLES `t_csys_course_ref_knowledge_unit` WRITE;
/*!40000 ALTER TABLE `t_csys_course_ref_knowledge_unit` DISABLE KEYS */;
/*!40000 ALTER TABLE `t_csys_course_ref_knowledge_unit` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `t_csys_knowledge_check_log`
--

DROP TABLE IF EXISTS `t_csys_knowledge_check_log`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `t_csys_knowledge_check_log` (
  `id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '主键ID',
  `source_course_id` bigint DEFAULT NULL COMMENT '一个课程的ID',
  `source_course_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '一个知识领域的名称',
  `source_unit_id` bigint DEFAULT NULL COMMENT '一个知识领域的知识单元ID',
  `source_unit_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '一个知识领域的知识单元ID',
  `source_point_id` bigint DEFAULT NULL COMMENT '一个知识领域的知识点ID',
  `source_point_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '一个知识领域的知识点名称',
  `target_course_id` bigint DEFAULT NULL COMMENT '另外一个课程的ID',
  `target_course_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '另外一个知识领域的名称',
  `target_unit_id` bigint DEFAULT NULL COMMENT '另外一个知识领域的知识单元ID',
  `target_unit_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '另外一个知识领域的知识单元名称',
  `target_point_id` bigint DEFAULT NULL COMMENT '另外一个知识领域的知识点ID',
  `target_point_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '另外一个知识领域的知识点名称',
  `start` tinyint(1) DEFAULT NULL COMMENT '是否相似',
  `remark` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT '审核意见',
  `training_scheme_id` bigint DEFAULT NULL COMMENT '培养方案ID',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `source_course_ID` (`source_course_id`) USING BTREE,
  KEY `target_course_id` (`target_course_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='知识点查重意见表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `t_csys_knowledge_check_log`
--

LOCK TABLES `t_csys_knowledge_check_log` WRITE;
/*!40000 ALTER TABLE `t_csys_knowledge_check_log` DISABLE KEYS */;
/*!40000 ALTER TABLE `t_csys_knowledge_check_log` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `t_csys_knowledge_chek_total`
--

DROP TABLE IF EXISTS `t_csys_knowledge_chek_total`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `t_csys_knowledge_chek_total` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `source_course_id` bigint DEFAULT NULL COMMENT '课程D',
  `total_unit_num` bigint DEFAULT NULL COMMENT '知识单元数量',
  `total_point_num` bigint DEFAULT NULL COMMENT '知识点数量',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `course_id` (`source_course_id`) USING BTREE COMMENT '课程id唯一'
) ENGINE=InnoDB AUTO_INCREMENT=10356 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='课程知识单元，知识点，统计表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `t_csys_knowledge_chek_total`
--

LOCK TABLES `t_csys_knowledge_chek_total` WRITE;
/*!40000 ALTER TABLE `t_csys_knowledge_chek_total` DISABLE KEYS */;
/*!40000 ALTER TABLE `t_csys_knowledge_chek_total` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `t_csys_knowledge_no_check_log`
--

DROP TABLE IF EXISTS `t_csys_knowledge_no_check_log`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `t_csys_knowledge_no_check_log` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `source_course_id` bigint DEFAULT NULL COMMENT '一个课程的ID',
  `target_course_id` bigint DEFAULT NULL COMMENT '另外一个课程的ID',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `课程联合索引` (`source_course_id`,`target_course_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=898344 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='没有重复知识点的ID组合表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `t_csys_knowledge_no_check_log`
--

LOCK TABLES `t_csys_knowledge_no_check_log` WRITE;
/*!40000 ALTER TABLE `t_csys_knowledge_no_check_log` DISABLE KEYS */;
/*!40000 ALTER TABLE `t_csys_knowledge_no_check_log` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `t_csys_scheme_course_ref_graduation`
--

DROP TABLE IF EXISTS `t_csys_scheme_course_ref_graduation`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `t_csys_scheme_course_ref_graduation` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `scheme_id` bigint NOT NULL COMMENT '培养方案ID',
  `quote_course_id` bigint NOT NULL COMMENT '调用课程ID，t_csys_course.id',
  `source_course_id` bigint DEFAULT NULL COMMENT '总库课程ID快照',
  `graduation_id` bigint NOT NULL COMMENT '方案内毕业标准ID，t_csys_std_graduation.id',
  `source_graduation_id` bigint DEFAULT NULL COMMENT '毕业标准总库ID，通常为t_csys_std_graduation.source_id',
  `college_id` bigint DEFAULT NULL COMMENT '学院ID',
  `category_id` bigint DEFAULT NULL COMMENT '门类ID',
  `major_id` bigint DEFAULT NULL COMMENT '专业ID',
  `sub_major_id` bigint DEFAULT NULL COMMENT '专业方向ID',
  `support_level` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '支撑强度/支撑程度字典编码，可选',
  `graduation_code` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '毕业标准编码快照',
  `graduation_name` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '毕业标准名称快照',
  `sort` int DEFAULT '1' COMMENT '排序',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `creator` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '创建人',
  `last_modified_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `last_modifier` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '更新人',
  `remark` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '备注',
  `sysflag` tinyint unsigned NOT NULL DEFAULT '0' COMMENT '删除标识：0正常 2删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_scheme_course_graduation` (`scheme_id`,`quote_course_id`,`graduation_id`),
  KEY `idx_scrg_quote_course` (`quote_course_id`),
  KEY `idx_scrg_source_course` (`source_course_id`),
  KEY `idx_scrg_graduation` (`graduation_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='培养方案调用课程毕业要求关联表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `t_csys_scheme_course_ref_graduation`
--

LOCK TABLES `t_csys_scheme_course_ref_graduation` WRITE;
/*!40000 ALTER TABLE `t_csys_scheme_course_ref_graduation` DISABLE KEYS */;
/*!40000 ALTER TABLE `t_csys_scheme_course_ref_graduation` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `t_csys_scheme_ref_major`
--

DROP TABLE IF EXISTS `t_csys_scheme_ref_major`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `t_csys_scheme_ref_major` (
  `scheme_id` bigint NOT NULL COMMENT '培养方案ID',
  `major_id` bigint NOT NULL COMMENT '专业ID',
  KEY `scheme_id_index` (`scheme_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='培养方案包含专业(注：不是专业类，一个培养方案对应一个专业类，包含多个专业)';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `t_csys_scheme_ref_major`
--

LOCK TABLES `t_csys_scheme_ref_major` WRITE;
/*!40000 ALTER TABLE `t_csys_scheme_ref_major` DISABLE KEYS */;
/*!40000 ALTER TABLE `t_csys_scheme_ref_major` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `t_csys_std_cultivation_target`
--

DROP TABLE IF EXISTS `t_csys_std_cultivation_target`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `t_csys_std_cultivation_target` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `name` varchar(4000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '名称',
  `code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '编码',
  `parent_id` bigint NOT NULL COMMENT '父级id',
  `leaf` tinyint DEFAULT NULL COMMENT '是否叶子(leaf)',
  `level` tinyint DEFAULT NULL COMMENT '层级(level)',
  `url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '路径(url)',
  `college_id` bigint DEFAULT NULL COMMENT '学院(college_id)',
  `major_id` bigint DEFAULT NULL COMMENT '专业(major)',
  `sub_major_id` bigint DEFAULT NULL COMMENT '细分专业(sub_major)',
  `class_id` tinyint DEFAULT '0' COMMENT '技术指挥分类(class)    0：未分类、1：技术类、2：指挥类',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间(create_time)',
  `creator` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '创建者(creator)',
  `last_modified_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间(last_modified_time)',
  `last_modifier` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '更新者(last_modifier)',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '备注(remark)',
  `sysflag` tinyint unsigned NOT NULL DEFAULT '0' COMMENT '删除标志(sysflag)',
  `version` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '版本',
  `category_id` bigint DEFAULT NULL COMMENT '大类Id',
  `training_scheme_id` bigint DEFAULT NULL COMMENT '培养方案ID',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `TRAINING_SCHEME_INDEX` (`training_scheme_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=22549 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='培养目标表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `t_csys_std_cultivation_target`
--

LOCK TABLES `t_csys_std_cultivation_target` WRITE;
/*!40000 ALTER TABLE `t_csys_std_cultivation_target` DISABLE KEYS */;
/*!40000 ALTER TABLE `t_csys_std_cultivation_target` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `t_csys_std_graduation`
--

DROP TABLE IF EXISTS `t_csys_std_graduation`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `t_csys_std_graduation` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `name` varchar(4000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '名称',
  `code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '编码',
  `cultivation_target_id` bigint DEFAULT NULL COMMENT '培养目标id',
  `parent_id` bigint NOT NULL COMMENT '父级',
  `leaf` tinyint DEFAULT NULL COMMENT '是否叶子(leaf)',
  `level` tinyint DEFAULT NULL COMMENT '层级(level)',
  `url` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '路径(url)',
  `college_id` bigint DEFAULT NULL COMMENT '学院(college_id)',
  `major_id` bigint DEFAULT NULL COMMENT '专业(major)',
  `sub_major_id` bigint DEFAULT NULL COMMENT '细分专业(sub_major)',
  `class_id` tinyint DEFAULT '0' COMMENT '技术指挥分类(class)    0：未分类、1：技术类、2：指挥类',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间(create_time)',
  `creator` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '创建者(creator)',
  `last_modified_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间(last_modified_time)',
  `last_modifier` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '更新者(last_modifier)',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '备注(remark)',
  `sysflag` tinyint unsigned NOT NULL DEFAULT '0' COMMENT '删除标志(sysflag)',
  `version` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '版本',
  `category_id` bigint DEFAULT NULL COMMENT '大类ID',
  `type` tinyint DEFAULT '2' COMMENT '1毕业要求模板，2毕业要求',
  `graduation_type` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '1:知识，2：能力，3：素质',
  `source_id` bigint DEFAULT NULL COMMENT '源id',
  `scheme_Id` bigint DEFAULT NULL COMMENT '培养方案id',
  `education_level` bigint DEFAULT NULL COMMENT '培养层次 字典表sys_education_level',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=46074 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='毕业标准表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `t_csys_std_graduation`
--

LOCK TABLES `t_csys_std_graduation` WRITE;
/*!40000 ALTER TABLE `t_csys_std_graduation` DISABLE KEYS */;
/*!40000 ALTER TABLE `t_csys_std_graduation` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `t_csys_std_graduation_ref_cultivation_target`
--

DROP TABLE IF EXISTS `t_csys_std_graduation_ref_cultivation_target`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `t_csys_std_graduation_ref_cultivation_target` (
  `graduation_id` bigint NOT NULL COMMENT '毕业标准id',
  `cultivation_target_id` bigint NOT NULL COMMENT '培养目标id',
  PRIMARY KEY (`graduation_id`,`cultivation_target_id`) USING BTREE,
  KEY `graduation_culTraget_index` (`graduation_id`,`cultivation_target_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC COMMENT='毕业标准与培养目标关联';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `t_csys_std_graduation_ref_cultivation_target`
--

LOCK TABLES `t_csys_std_graduation_ref_cultivation_target` WRITE;
/*!40000 ALTER TABLE `t_csys_std_graduation_ref_cultivation_target` DISABLE KEYS */;
/*!40000 ALTER TABLE `t_csys_std_graduation_ref_cultivation_target` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `t_csys_std_major`
--

DROP TABLE IF EXISTS `t_csys_std_major`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `t_csys_std_major` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `name` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '名称',
  `code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '编码',
  `parent_id` bigint NOT NULL COMMENT '父级id',
  `level` tinyint DEFAULT NULL COMMENT '层级(level)',
  `college_id` bigint DEFAULT NULL COMMENT '学院(college_id)',
  `category_id` bigint DEFAULT NULL COMMENT '细门类id',
  `class_id` varchar(2) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '0' COMMENT '细分专业分类(0:未分类,1:技术类,2:指挥类,3:技术/技术类)',
  `applicable_object` varchar(225) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '适用对象 字典表sys_education_level',
  `leaf` tinyint unsigned DEFAULT '1' COMMENT '是否叶子(leaf)',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间(create_time)',
  `creator` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '创建者(creator)',
  `last_modified_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间(last_modified_time)',
  `last_modifier` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '更新者(last_modifier)',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '备注(remark)',
  `sysflag` tinyint unsigned NOT NULL DEFAULT '0' COMMENT '删除标志(sysflag)',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=272 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='学院专业表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `t_csys_std_major`
--

LOCK TABLES `t_csys_std_major` WRITE;
/*!40000 ALTER TABLE `t_csys_std_major` DISABLE KEYS */;
/*!40000 ALTER TABLE `t_csys_std_major` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `t_csys_teaching_plan`
--

DROP TABLE IF EXISTS `t_csys_teaching_plan`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `t_csys_teaching_plan` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `root_plan_id` bigint DEFAULT NULL COMMENT '同一教学计划版本根ID',
  `source_course_id` bigint NOT NULL COMMENT '总库课程ID，关联t_csys_course.id',
  `plan_type` tinyint NOT NULL COMMENT '计划类型：1普通课程 2实验课程 3实践训练课目 4实践项目',
  `version` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '教学计划版本，如2026、V1.0',
  `current_flag` tinyint NOT NULL DEFAULT '1' COMMENT '是否当前版本：1是 0否',
  `enabled_term` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '启用时间，如2026年春季学期',
  `status` tinyint NOT NULL DEFAULT '0' COMMENT '状态：0草稿 1审核中 2通过 3退回 9停用',
  `source_course_name` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '总库课程名称快照',
  `source_course_code` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '总库课程编号快照',
  `source_course_en_name` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '总库课程英文名快照',
  `source_hours` decimal(8,2) DEFAULT NULL COMMENT '总库课程总学时快照',
  `source_teach_hours` decimal(8,2) DEFAULT NULL COMMENT '总库课程讲授学时快照',
  `source_practice_hours` decimal(8,2) DEFAULT NULL COMMENT '总库课程实践/实验学时快照',
  `source_credit` decimal(8,2) DEFAULT NULL COMMENT '总库课程学分快照',
  `score_rule` text COLLATE utf8mb4_unicode_ci COMMENT '计分规则',
  `file_id` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '生成或上传文件ID',
  `file_name` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '文件名称',
  `download_url` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '下载地址',
  `preview_url` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '预览地址',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `creator` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '创建人',
  `last_modified_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `last_modifier` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '更新人',
  `remark` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '备注',
  `sysflag` tinyint unsigned NOT NULL DEFAULT '0' COMMENT '删除标识：0正常 2删除',
  PRIMARY KEY (`id`),
  KEY `idx_tp_source_course` (`source_course_id`),
  KEY `idx_tp_root` (`root_plan_id`),
  KEY `idx_tp_type_status` (`plan_type`,`status`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='课程教学计划主表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `t_csys_teaching_plan`
--

LOCK TABLES `t_csys_teaching_plan` WRITE;
/*!40000 ALTER TABLE `t_csys_teaching_plan` DISABLE KEYS */;
/*!40000 ALTER TABLE `t_csys_teaching_plan` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `t_csys_teaching_plan_assessment`
--

DROP TABLE IF EXISTS `t_csys_teaching_plan_assessment`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `t_csys_teaching_plan_assessment` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `plan_id` bigint NOT NULL COMMENT '教学计划ID',
  `item_id` bigint DEFAULT NULL COMMENT '关联实验/实践项目ID',
  `assessment_category` tinyint DEFAULT NULL COMMENT '考核类别：1终结性 2形成性 3实验项目 4训练课目 5成果评价',
  `assessment_item` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '考核项目或成果形式',
  `method` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '考核方式',
  `mechanism` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '评定机制',
  `score_system` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '成绩评定：百分制/五级制/两级制',
  `outcome_type` tinyint DEFAULT '0' COMMENT '成果类型：0无 1个人成果 2团队成果',
  `assessed_content` text COLLATE utf8mb4_unicode_ci COMMENT '评价的知识和能力',
  `weight` decimal(6,2) DEFAULT NULL COMMENT '权重',
  `standard` longtext COLLATE utf8mb4_unicode_ci COMMENT '评价标准/评价准则',
  `sort` int DEFAULT '1' COMMENT '排序',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `creator` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '创建人',
  `last_modified_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `last_modifier` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '更新人',
  `remark` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '备注',
  `sysflag` tinyint unsigned NOT NULL DEFAULT '0' COMMENT '删除标识',
  PRIMARY KEY (`id`),
  KEY `idx_tp_assess_plan` (`plan_id`),
  KEY `idx_tp_assess_item` (`item_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='教学计划考核评价';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `t_csys_teaching_plan_assessment`
--

LOCK TABLES `t_csys_teaching_plan_assessment` WRITE;
/*!40000 ALTER TABLE `t_csys_teaching_plan_assessment` DISABLE KEYS */;
/*!40000 ALTER TABLE `t_csys_teaching_plan_assessment` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `t_csys_teaching_plan_condition`
--

DROP TABLE IF EXISTS `t_csys_teaching_plan_condition`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `t_csys_teaching_plan_condition` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `plan_id` bigint NOT NULL COMMENT '教学计划ID',
  `condition_type` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '条件类型',
  `requirement` longtext COLLATE utf8mb4_unicode_ci COMMENT '有关要求',
  `sort` int DEFAULT '1' COMMENT '排序',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `creator` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '创建人',
  `last_modified_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `last_modifier` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '更新人',
  `remark` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '备注',
  `sysflag` tinyint unsigned NOT NULL DEFAULT '0' COMMENT '删除标识',
  PRIMARY KEY (`id`),
  KEY `idx_tp_condition_plan` (`plan_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='教学计划条件保障';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `t_csys_teaching_plan_condition`
--

LOCK TABLES `t_csys_teaching_plan_condition` WRITE;
/*!40000 ALTER TABLE `t_csys_teaching_plan_condition` DISABLE KEYS */;
/*!40000 ALTER TABLE `t_csys_teaching_plan_condition` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `t_csys_teaching_plan_content`
--

DROP TABLE IF EXISTS `t_csys_teaching_plan_content`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `t_csys_teaching_plan_content` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `plan_id` bigint NOT NULL COMMENT '教学计划ID',
  `parent_id` bigint DEFAULT NULL COMMENT '父级内容ID',
  `content_type` tinyint NOT NULL COMMENT '内容类型：1专题 2课程项目 3实验 4大作业 5训练模块 6模块内容',
  `title` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '专题/模块/内容名称',
  `content` text COLLATE utf8mb4_unicode_ci COMMENT '内容说明',
  `purpose` text COLLATE utf8mb4_unicode_ci COMMENT '目的',
  `hours` decimal(8,2) DEFAULT NULL COMMENT '学时',
  `time_arrange` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '时间安排，如1天',
  `sort` int DEFAULT '1' COMMENT '排序',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `creator` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '创建人',
  `last_modified_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `last_modifier` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '更新人',
  `remark` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '备注',
  `sysflag` tinyint unsigned NOT NULL DEFAULT '0' COMMENT '删除标识',
  PRIMARY KEY (`id`),
  KEY `idx_tp_content_plan` (`plan_id`),
  KEY `idx_tp_content_parent` (`parent_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='教学计划内容与学时安排';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `t_csys_teaching_plan_content`
--

LOCK TABLES `t_csys_teaching_plan_content` WRITE;
/*!40000 ALTER TABLE `t_csys_teaching_plan_content` DISABLE KEYS */;
/*!40000 ALTER TABLE `t_csys_teaching_plan_content` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `t_csys_teaching_plan_context`
--

DROP TABLE IF EXISTS `t_csys_teaching_plan_context`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `t_csys_teaching_plan_context` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `plan_id` bigint NOT NULL COMMENT '教学计划ID',
  `source_course_id` bigint NOT NULL COMMENT '总库课程ID',
  `quote_course_id` bigint NOT NULL COMMENT '调用课程ID，t_csys_course.id',
  `scheme_id` bigint NOT NULL COMMENT '培养方案ID',
  `schedule_id` bigint DEFAULT NULL COMMENT '培养方案排课ID',
  `scheme_name` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '培养方案名称快照',
  `scheme_version` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '培养方案版本快照',
  `education_level` varchar(225) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '适用对象/培养层次',
  `object_type` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '培养对象类型',
  `education` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '学历',
  `academic_type` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '学制类型',
  `duration_type` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '学制年限',
  `degree` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '授予学位类型',
  `college_id` bigint DEFAULT NULL COMMENT '学院ID',
  `category_id` bigint DEFAULT NULL COMMENT '门类ID',
  `major_id` bigint DEFAULT NULL COMMENT '专业ID',
  `sub_major_id` bigint DEFAULT NULL COMMENT '专业方向ID',
  `course_module` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '课程模块编码',
  `course_module_children` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '课程子模块编码',
  `semester_schedule` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '学期安排',
  `term` tinyint DEFAULT NULL COMMENT '开课学期',
  `course_attr` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '修读性质',
  `time_arrange` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '时间安排',
  `hours` decimal(8,2) DEFAULT NULL COMMENT '上下文总学时',
  `teach_hours` decimal(8,2) DEFAULT NULL COMMENT '上下文讲授学时',
  `practice_hours` decimal(8,2) DEFAULT NULL COMMENT '上下文实践/实验学时',
  `credits` decimal(8,2) DEFAULT NULL COMMENT '上下文学分',
  `sync_time` datetime DEFAULT NULL COMMENT '从课程调用关系同步时间',
  `sync_flag` tinyint NOT NULL DEFAULT '1' COMMENT '同步状态：1有效 2调用关系已失效',
  `sort` int DEFAULT '1' COMMENT '排序',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `creator` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '创建人',
  `last_modified_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `last_modifier` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '更新人',
  `remark` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '备注',
  `sysflag` tinyint unsigned NOT NULL DEFAULT '0' COMMENT '删除标识',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_tp_context` (`plan_id`,`quote_course_id`,`scheme_id`),
  KEY `idx_tp_context_source` (`source_course_id`),
  KEY `idx_tp_context_scheme` (`scheme_id`),
  KEY `idx_tp_context_quote_course` (`quote_course_id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='教学计划调用课程上下文';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `t_csys_teaching_plan_context`
--

LOCK TABLES `t_csys_teaching_plan_context` WRITE;
/*!40000 ALTER TABLE `t_csys_teaching_plan_context` DISABLE KEYS */;
/*!40000 ALTER TABLE `t_csys_teaching_plan_context` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `t_csys_teaching_plan_objective`
--

DROP TABLE IF EXISTS `t_csys_teaching_plan_objective`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `t_csys_teaching_plan_objective` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `plan_id` bigint NOT NULL COMMENT '教学计划ID',
  `context_id` bigint NOT NULL COMMENT '教学计划调用上下文ID，对应页面当前培养方案tab',
  `objective_type_code` varchar(64) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '目标类型字典编码：知识目标/能力目标/素质目标',
  `objective_type_name` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '目标类型名称快照',
  `content` text COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '目标内容，手工录入',
  `source_mode` tinyint DEFAULT '2' COMMENT '来源方式：2手工录入',
  `sort` int DEFAULT '1' COMMENT '排序',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `creator` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '创建人',
  `last_modified_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `last_modifier` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '更新人',
  `remark` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '备注',
  `sysflag` tinyint unsigned NOT NULL DEFAULT '0' COMMENT '删除标识',
  PRIMARY KEY (`id`),
  KEY `idx_tp_obj_plan` (`plan_id`),
  KEY `idx_tp_obj_context` (`context_id`),
  KEY `idx_tp_obj_type` (`objective_type_code`),
  KEY `idx_tp_obj_context_type` (`plan_id`,`context_id`,`objective_type_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='教学计划目标';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `t_csys_teaching_plan_objective`
--

LOCK TABLES `t_csys_teaching_plan_objective` WRITE;
/*!40000 ALTER TABLE `t_csys_teaching_plan_objective` DISABLE KEYS */;
/*!40000 ALTER TABLE `t_csys_teaching_plan_objective` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `t_csys_teaching_plan_objective_ref`
--

DROP TABLE IF EXISTS `t_csys_teaching_plan_objective_ref`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `t_csys_teaching_plan_objective_ref` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `plan_id` bigint NOT NULL COMMENT '教学计划ID',
  `context_id` bigint NOT NULL COMMENT '教学计划调用上下文ID，对应页面当前培养方案tab',
  `objective_id` bigint NOT NULL COMMENT '教学计划目标ID',
  `scheme_course_graduation_id` bigint DEFAULT NULL COMMENT '培养方案调用课程毕业要求关联ID，优先关联t_csys_scheme_course_ref_graduation.id',
  `quote_course_id` bigint NOT NULL COMMENT '调用课程ID快照',
  `scheme_id` bigint NOT NULL COMMENT '培养方案ID快照',
  `graduation_id` bigint NOT NULL COMMENT '方案内毕业标准ID，t_csys_std_graduation.id',
  `source_graduation_id` bigint DEFAULT NULL COMMENT '毕业标准总库ID，通常为t_csys_std_graduation.source_id',
  `graduation_code` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '毕业标准编码快照',
  `graduation_name` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '毕业标准名称快照',
  `graduation_bind_source` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '绑定来源：scheme_course_ref或course_ref_graduation',
  `support_desc` varchar(1000) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '支撑说明',
  `sort` int DEFAULT '1' COMMENT '排序',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `creator` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '创建人',
  `last_modified_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `last_modifier` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '更新人',
  `remark` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '备注',
  `sysflag` tinyint unsigned NOT NULL DEFAULT '0' COMMENT '删除标识',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_tp_obj_ref` (`objective_id`,`context_id`,`graduation_id`),
  KEY `idx_tp_obj_ref_plan` (`plan_id`),
  KEY `idx_tp_obj_ref_context` (`context_id`),
  KEY `idx_tp_obj_ref_obj` (`objective_id`),
  KEY `idx_tp_obj_ref_scheme_course_graduation` (`scheme_course_graduation_id`),
  KEY `idx_tp_obj_ref_scheme_course` (`scheme_id`,`quote_course_id`),
  KEY `idx_tp_obj_ref_graduation` (`graduation_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='教学计划目标支撑毕业要求';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `t_csys_teaching_plan_objective_ref`
--

LOCK TABLES `t_csys_teaching_plan_objective_ref` WRITE;
/*!40000 ALTER TABLE `t_csys_teaching_plan_objective_ref` DISABLE KEYS */;
/*!40000 ALTER TABLE `t_csys_teaching_plan_objective_ref` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `t_csys_teaching_plan_practice_item`
--

DROP TABLE IF EXISTS `t_csys_teaching_plan_practice_item`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `t_csys_teaching_plan_practice_item` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `plan_id` bigint NOT NULL COMMENT '教学计划ID',
  `item_type` tinyint NOT NULL COMMENT '项目类型：1实验 2实践项目 3设计实验 4验证实验',
  `name` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '项目名称',
  `hours` decimal(8,2) DEFAULT NULL COMMENT '学时',
  `group_info` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '分组情况',
  `experiment_nature` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '实验性质',
  `study_nature` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '修读性质',
  `sort` int DEFAULT '1' COMMENT '排序',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `creator` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '创建人',
  `last_modified_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `last_modifier` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '更新人',
  `remark` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '备注',
  `sysflag` tinyint unsigned NOT NULL DEFAULT '0' COMMENT '删除标识',
  PRIMARY KEY (`id`),
  KEY `idx_tp_item_plan` (`plan_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='教学计划实验/实践项目';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `t_csys_teaching_plan_practice_item`
--

LOCK TABLES `t_csys_teaching_plan_practice_item` WRITE;
/*!40000 ALTER TABLE `t_csys_teaching_plan_practice_item` DISABLE KEYS */;
/*!40000 ALTER TABLE `t_csys_teaching_plan_practice_item` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `t_csys_teaching_plan_practice_item_detail`
--

DROP TABLE IF EXISTS `t_csys_teaching_plan_practice_item_detail`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `t_csys_teaching_plan_practice_item_detail` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `item_id` bigint NOT NULL COMMENT '实验/实践项目ID',
  `detail_type` varchar(64) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '明细类型：purpose_task、ability_point、principle、content_requirement、result_requirement、teaching_design、complex_problem、main_task、overall_design、outcome_requirement',
  `objective_id` bigint DEFAULT NULL COMMENT '训练能力点或支撑目标ID',
  `content` longtext COLLATE utf8mb4_unicode_ci COMMENT '明细内容',
  `sort` int DEFAULT '1' COMMENT '排序',
  PRIMARY KEY (`id`),
  KEY `idx_tp_item_detail_item` (`item_id`),
  KEY `idx_tp_item_detail_obj` (`objective_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='实验/实践项目明细';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `t_csys_teaching_plan_practice_item_detail`
--

LOCK TABLES `t_csys_teaching_plan_practice_item_detail` WRITE;
/*!40000 ALTER TABLE `t_csys_teaching_plan_practice_item_detail` DISABLE KEYS */;
/*!40000 ALTER TABLE `t_csys_teaching_plan_practice_item_detail` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `t_csys_teaching_plan_process_step`
--

DROP TABLE IF EXISTS `t_csys_teaching_plan_process_step`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `t_csys_teaching_plan_process_step` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `plan_id` bigint NOT NULL COMMENT '教学计划ID',
  `item_id` bigint DEFAULT NULL COMMENT '关联实践项目ID',
  `stage_name` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '阶段划分',
  `step_name` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '实施步骤或项目步骤',
  `requirement` longtext COLLATE utf8mb4_unicode_ci COMMENT '有关要求',
  `sort` int DEFAULT '1' COMMENT '排序',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `creator` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '创建人',
  `last_modified_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `last_modifier` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '更新人',
  `remark` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '备注',
  `sysflag` tinyint unsigned NOT NULL DEFAULT '0' COMMENT '删除标识',
  PRIMARY KEY (`id`),
  KEY `idx_tp_step_plan` (`plan_id`),
  KEY `idx_tp_step_item` (`item_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='教学计划实施步骤';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `t_csys_teaching_plan_process_step`
--

LOCK TABLES `t_csys_teaching_plan_process_step` WRITE;
/*!40000 ALTER TABLE `t_csys_teaching_plan_process_step` DISABLE KEYS */;
/*!40000 ALTER TABLE `t_csys_teaching_plan_process_step` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `t_csys_teaching_plan_ref`
--

DROP TABLE IF EXISTS `t_csys_teaching_plan_ref`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `t_csys_teaching_plan_ref` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `plan_id` bigint NOT NULL COMMENT '教学计划ID',
  `context_id` bigint DEFAULT NULL COMMENT '调用上下文ID，可为空',
  `ref_type` tinyint NOT NULL COMMENT '引用类型：1支撑总库课程 2支撑调用课程/训练课目 3知识单元 4知识点 5教学目标',
  `ref_id` bigint DEFAULT NULL COMMENT '引用对象ID',
  `ref_name` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '引用对象名称快照',
  `ref_hours` decimal(8,2) DEFAULT NULL COMMENT '引用课程/课目学时快照',
  `sort` int DEFAULT '1' COMMENT '排序',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `creator` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '创建人',
  `last_modified_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `last_modifier` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '更新人',
  `remark` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '备注',
  `sysflag` tinyint unsigned NOT NULL DEFAULT '0' COMMENT '删除标识',
  PRIMARY KEY (`id`),
  KEY `idx_tp_ref_plan` (`plan_id`),
  KEY `idx_tp_ref_context` (`context_id`),
  KEY `idx_tp_ref_type` (`ref_type`,`ref_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='教学计划通用引用';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `t_csys_teaching_plan_ref`
--

LOCK TABLES `t_csys_teaching_plan_ref` WRITE;
/*!40000 ALTER TABLE `t_csys_teaching_plan_ref` DISABLE KEYS */;
/*!40000 ALTER TABLE `t_csys_teaching_plan_ref` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `t_csys_teaching_plan_section`
--

DROP TABLE IF EXISTS `t_csys_teaching_plan_section`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `t_csys_teaching_plan_section` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `plan_id` bigint NOT NULL COMMENT '教学计划ID',
  `section_code` varchar(64) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '章节编码：task_background、overall_design等',
  `section_title` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '章节标题',
  `content` longtext COLLATE utf8mb4_unicode_ci COMMENT '章节内容',
  `sort` int DEFAULT '1' COMMENT '排序',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `creator` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '创建人',
  `last_modified_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `last_modifier` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '更新人',
  `remark` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '备注',
  `sysflag` tinyint unsigned NOT NULL DEFAULT '0' COMMENT '删除标识',
  PRIMARY KEY (`id`),
  KEY `idx_tp_section_plan` (`plan_id`),
  KEY `idx_tp_section_code` (`section_code`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='教学计划文本章节';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `t_csys_teaching_plan_section`
--

LOCK TABLES `t_csys_teaching_plan_section` WRITE;
/*!40000 ALTER TABLE `t_csys_teaching_plan_section` DISABLE KEYS */;
/*!40000 ALTER TABLE `t_csys_teaching_plan_section` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `t_csys_teaching_plan_target_design`
--

DROP TABLE IF EXISTS `t_csys_teaching_plan_target_design`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `t_csys_teaching_plan_target_design` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `plan_id` bigint NOT NULL COMMENT '教学计划ID',
  `context_id` bigint DEFAULT NULL COMMENT '教学计划调用上下文ID；按tab维护时填写',
  `design_type_code` varchar(64) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '设计类型字典编码：知识目标/能力目标/素质目标',
  `objective_id` bigint DEFAULT NULL COMMENT '对应教学计划目标ID',
  `knowledge_unit_id` bigint DEFAULT NULL COMMENT '知识单元ID，t_csys_course_knowledge_unit.id',
  `knowledge_unit_name` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '知识单元名称快照',
  `knowledge_point_id` bigint DEFAULT NULL COMMENT '知识点ID，t_csys_course_knowledge_point.id',
  `knowledge_point_name` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '知识点名称快照',
  `observation_point` text COLLATE utf8mb4_unicode_ci COMMENT '观测点',
  `content_ids` json DEFAULT NULL COMMENT '关联教学内容ID数组',
  `content_text` text COLLATE utf8mb4_unicode_ci COMMENT '教学内容文本快照',
  `teaching_link` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '教学环节',
  `teaching_method` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '教法',
  `learning_method` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '学法',
  `hours` decimal(8,2) DEFAULT NULL COMMENT '学时',
  `teaching_design` text COLLATE utf8mb4_unicode_ci COMMENT '教学设计',
  `sort` int DEFAULT '1' COMMENT '排序',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `creator` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '创建人',
  `last_modified_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `last_modifier` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '更新人',
  `remark` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '备注',
  `sysflag` tinyint unsigned NOT NULL DEFAULT '0' COMMENT '删除标识',
  PRIMARY KEY (`id`),
  KEY `idx_tp_design_plan` (`plan_id`),
  KEY `idx_tp_design_context` (`context_id`),
  KEY `idx_tp_design_obj` (`objective_id`),
  KEY `idx_tp_design_knowledge` (`knowledge_unit_id`,`knowledge_point_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='教学目标达成设计';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `t_csys_teaching_plan_target_design`
--

LOCK TABLES `t_csys_teaching_plan_target_design` WRITE;
/*!40000 ALTER TABLE `t_csys_teaching_plan_target_design` DISABLE KEYS */;
/*!40000 ALTER TABLE `t_csys_teaching_plan_target_design` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `t_csys_teaching_plan_teacher`
--

DROP TABLE IF EXISTS `t_csys_teaching_plan_teacher`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `t_csys_teaching_plan_teacher` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `plan_id` bigint NOT NULL COMMENT '教学计划ID',
  `teacher_id` bigint DEFAULT NULL COMMENT '系统用户ID',
  `teacher_name` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '教员姓名',
  `professional_title` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '职称',
  `duty` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '职责',
  `lecture_content` text COLLATE utf8mb4_unicode_ci COMMENT '主讲内容',
  `sort` int DEFAULT '1' COMMENT '排序',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `creator` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '创建人',
  `last_modified_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `last_modifier` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '更新人',
  `remark` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '备注',
  `sysflag` tinyint unsigned NOT NULL DEFAULT '0' COMMENT '删除标识',
  PRIMARY KEY (`id`),
  KEY `idx_tp_teacher_plan` (`plan_id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='教学计划教员团队';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `t_csys_teaching_plan_teacher`
--

LOCK TABLES `t_csys_teaching_plan_teacher` WRITE;
/*!40000 ALTER TABLE `t_csys_teaching_plan_teacher` DISABLE KEYS */;
/*!40000 ALTER TABLE `t_csys_teaching_plan_teacher` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `t_csys_teaching_plan_textbook`
--

DROP TABLE IF EXISTS `t_csys_teaching_plan_textbook`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `t_csys_teaching_plan_textbook` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `plan_id` bigint NOT NULL COMMENT '教学计划ID',
  `material_nature` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '教材性质',
  `name` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '教材名称',
  `first_author` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '第一作者',
  `edition` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '版次',
  `publisher` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '出版或颁发单位',
  `publish_time` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '出版或颁发时间',
  `isbn` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT 'ISBN号或统一书号/文件号',
  `publish_method` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '出版方式',
  `sort` int DEFAULT '1' COMMENT '排序',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `creator` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '创建人',
  `last_modified_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `last_modifier` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '更新人',
  `remark` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '备注',
  `sysflag` tinyint unsigned NOT NULL DEFAULT '0' COMMENT '删除标识',
  PRIMARY KEY (`id`),
  KEY `idx_tp_textbook_plan` (`plan_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='教学计划教材';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `t_csys_teaching_plan_textbook`
--

LOCK TABLES `t_csys_teaching_plan_textbook` WRITE;
/*!40000 ALTER TABLE `t_csys_teaching_plan_textbook` DISABLE KEYS */;
/*!40000 ALTER TABLE `t_csys_teaching_plan_textbook` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `t_csys_teaching_program`
--

DROP TABLE IF EXISTS `t_csys_teaching_program`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `t_csys_teaching_program` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `course_id` bigint DEFAULT NULL COMMENT '课程id',
  `version` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_german2_ci DEFAULT NULL COMMENT '教学计划版本',
  `term` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_german2_ci DEFAULT NULL COMMENT '学期',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `creator` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_german2_ci DEFAULT NULL COMMENT '创建人',
  `last_modified_time` datetime DEFAULT NULL COMMENT '最后修改时间',
  `last_modifier` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_german2_ci DEFAULT NULL COMMENT '最后修改人',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_german2_ci DEFAULT NULL COMMENT '备注',
  `sysflag` tinyint(1) DEFAULT '0' COMMENT '标志位',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_german2_ci ROW_FORMAT=DYNAMIC COMMENT='教学计划表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `t_csys_teaching_program`
--

LOCK TABLES `t_csys_teaching_program` WRITE;
/*!40000 ALTER TABLE `t_csys_teaching_program` DISABLE KEYS */;
/*!40000 ALTER TABLE `t_csys_teaching_program` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `t_csys_teaching_programme_attribute`
--

DROP TABLE IF EXISTS `t_csys_teaching_programme_attribute`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `t_csys_teaching_programme_attribute` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'id',
  `parent_Id` bigint DEFAULT NULL COMMENT '父id',
  `instance_id` bigint DEFAULT NULL COMMENT '教学大纲实例id',
  `attribute_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_german2_ci DEFAULT NULL COMMENT '属性名称',
  `attribute_value` varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_german2_ci DEFAULT NULL COMMENT '属性值',
  `template_id` bigint DEFAULT NULL COMMENT '对应t_csys_teaching_programme_template id',
  `template_parent_Id` bigint DEFAULT NULL COMMENT '对应t_csys_teaching_programme_template父id',
  `sort` int DEFAULT '1' COMMENT '排序',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=59832 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_german2_ci ROW_FORMAT=DYNAMIC COMMENT='教学大纲属性';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `t_csys_teaching_programme_attribute`
--

LOCK TABLES `t_csys_teaching_programme_attribute` WRITE;
/*!40000 ALTER TABLE `t_csys_teaching_programme_attribute` DISABLE KEYS */;
/*!40000 ALTER TABLE `t_csys_teaching_programme_attribute` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `t_csys_teaching_programme_instance`
--

DROP TABLE IF EXISTS `t_csys_teaching_programme_instance`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `t_csys_teaching_programme_instance` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'id',
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_german2_ci DEFAULT NULL COMMENT '名称',
  `version` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_german2_ci DEFAULT NULL COMMENT '版本',
  `outline_id` bigint DEFAULT NULL COMMENT '模板id,顶层的',
  `college_id` bigint DEFAULT NULL COMMENT '学院id',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `creator` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_german2_ci DEFAULT NULL COMMENT '创建人',
  `last_modified_time` datetime DEFAULT NULL COMMENT '修改时间',
  `last_modifier` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_german2_ci DEFAULT NULL COMMENT '最后修改人',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_german2_ci DEFAULT NULL COMMENT '备注',
  `sysflag` tinyint DEFAULT '0' COMMENT '标志位',
  `status` tinyint DEFAULT '0' COMMENT '审核状态：0未审核1审核中2审核通过3审核失败',
  `file_id` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_german2_ci DEFAULT NULL COMMENT '文件ID',
  `download_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_german2_ci DEFAULT NULL COMMENT '下载地址',
  `preview_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_german2_ci DEFAULT NULL COMMENT '预览地址',
  `category_id` bigint DEFAULT NULL COMMENT '学科门类',
  `major_id` bigint DEFAULT NULL COMMENT '专业类',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1815 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_german2_ci ROW_FORMAT=DYNAMIC COMMENT='教学大纲实例';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `t_csys_teaching_programme_instance`
--

LOCK TABLES `t_csys_teaching_programme_instance` WRITE;
/*!40000 ALTER TABLE `t_csys_teaching_programme_instance` DISABLE KEYS */;
/*!40000 ALTER TABLE `t_csys_teaching_programme_instance` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `t_csys_teaching_programme_instance_ex_ref_major`
--

DROP TABLE IF EXISTS `t_csys_teaching_programme_instance_ex_ref_major`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `t_csys_teaching_programme_instance_ex_ref_major` (
  `extract_id` bigint NOT NULL COMMENT 't_csys_teaching_programme_instance_extract表id',
  `major_id` bigint NOT NULL COMMENT '专业表id'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_german2_ci ROW_FORMAT=DYNAMIC COMMENT='文档抽取和专业关联表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `t_csys_teaching_programme_instance_ex_ref_major`
--

LOCK TABLES `t_csys_teaching_programme_instance_ex_ref_major` WRITE;
/*!40000 ALTER TABLE `t_csys_teaching_programme_instance_ex_ref_major` DISABLE KEYS */;
/*!40000 ALTER TABLE `t_csys_teaching_programme_instance_ex_ref_major` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `t_csys_teaching_programme_instance_extract`
--

DROP TABLE IF EXISTS `t_csys_teaching_programme_instance_extract`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `t_csys_teaching_programme_instance_extract` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'id',
  `instance_Id` bigint DEFAULT NULL COMMENT 't_csys_teaching_programme_instance表id',
  `learn_Institution` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_german2_ci DEFAULT NULL COMMENT '学制',
  `category_Id` bigint DEFAULT NULL COMMENT '门类id',
  `degree` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_german2_ci DEFAULT NULL COMMENT '授予学位',
  `total_Hour` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_german2_ci DEFAULT NULL COMMENT '总学时',
  `total_Credit` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_german2_ci DEFAULT NULL COMMENT '总学分',
  `float_Rate` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_german2_ci DEFAULT NULL COMMENT '上下浮动比例',
  `political_Hour` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_german2_ci DEFAULT NULL COMMENT '政治理论模块学时',
  `military_Hour` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_german2_ci DEFAULT NULL COMMENT '军事基础模块学时',
  `science_Hour` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_german2_ci DEFAULT NULL COMMENT '科学文化模块学时',
  `basics_Hour` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_german2_ci DEFAULT NULL COMMENT '学科基础模块学时',
  `status` tinyint DEFAULT '0' COMMENT '抽取状态0 默认 1 ai抽取中 2 抽取成功 9 抽取失败',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `creator` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_german2_ci DEFAULT NULL COMMENT '创建人',
  `last_modified_time` datetime DEFAULT NULL COMMENT '最后修改时间',
  `last_modifier` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_german2_ci DEFAULT NULL COMMENT '最后修改人',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_german2_ci DEFAULT NULL COMMENT '备注',
  `sysflag` tinyint DEFAULT '0' COMMENT '标志位',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=24 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_german2_ci ROW_FORMAT=DYNAMIC COMMENT='教育大纲从文档抽取内容';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `t_csys_teaching_programme_instance_extract`
--

LOCK TABLES `t_csys_teaching_programme_instance_extract` WRITE;
/*!40000 ALTER TABLE `t_csys_teaching_programme_instance_extract` DISABLE KEYS */;
/*!40000 ALTER TABLE `t_csys_teaching_programme_instance_extract` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `t_csys_teaching_programme_outline`
--

DROP TABLE IF EXISTS `t_csys_teaching_programme_outline`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `t_csys_teaching_programme_outline` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_german2_ci DEFAULT NULL COMMENT '大纲名称模板',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `creator` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_german2_ci DEFAULT NULL COMMENT '创建人',
  `last_modified_time` datetime DEFAULT NULL COMMENT '最后修改时间',
  `last_modifier` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_german2_ci DEFAULT NULL COMMENT '最后修改人',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_german2_ci DEFAULT NULL COMMENT '备注',
  `sysflag` tinyint DEFAULT '0' COMMENT '标志位',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=70 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_german2_ci ROW_FORMAT=DYNAMIC COMMENT='教学大纲模板实例';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `t_csys_teaching_programme_outline`
--

LOCK TABLES `t_csys_teaching_programme_outline` WRITE;
/*!40000 ALTER TABLE `t_csys_teaching_programme_outline` DISABLE KEYS */;
/*!40000 ALTER TABLE `t_csys_teaching_programme_outline` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `t_csys_teaching_programme_template`
--

DROP TABLE IF EXISTS `t_csys_teaching_programme_template`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `t_csys_teaching_programme_template` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'id',
  `outline_id` bigint DEFAULT NULL COMMENT '大纲id',
  `attribute_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_german2_ci DEFAULT NULL COMMENT '模板名称',
  `parent_id` bigint DEFAULT NULL COMMENT '父id',
  `level` tinyint DEFAULT '1' COMMENT '层级',
  `leaf` tinyint DEFAULT '1' COMMENT '是否叶子节点',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `creator` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_german2_ci DEFAULT NULL COMMENT '创建人',
  `last_modified_time` datetime DEFAULT NULL COMMENT '修改时间',
  `last_modifier` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_german2_ci DEFAULT NULL COMMENT '修改人',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_german2_ci DEFAULT NULL COMMENT '备注',
  `sysflag` tinyint DEFAULT '0' COMMENT '标志位',
  `sort` int DEFAULT '1' COMMENT '排序',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1297 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_german2_ci ROW_FORMAT=DYNAMIC COMMENT='教学大纲模板';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `t_csys_teaching_programme_template`
--

LOCK TABLES `t_csys_teaching_programme_template` WRITE;
/*!40000 ALTER TABLE `t_csys_teaching_programme_template` DISABLE KEYS */;
/*!40000 ALTER TABLE `t_csys_teaching_programme_template` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `t_csys_training_scheme`
--

DROP TABLE IF EXISTS `t_csys_training_scheme`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `t_csys_training_scheme` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `standard_id` bigint DEFAULT NULL COMMENT '培养标准id',
  `name` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '培养方案名称',
  `category_id` bigint DEFAULT NULL COMMENT '门类id',
  `college_id` bigint DEFAULT NULL COMMENT '学院',
  `file_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '上传文件id',
  `file_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '上传文件名称',
  `major_id` bigint DEFAULT NULL COMMENT '专业(major)',
  `sub_major_id` bigint DEFAULT NULL COMMENT '技术指挥分类(class)    0：未分类、1：技术类、2：指挥类',
  `class_id` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '年度',
  `year` tinyint DEFAULT NULL COMMENT '年度',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间(create_time)',
  `creator` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '创建者(creator)',
  `last_modified_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间(last_modified_time)',
  `last_modifier` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '更新者(last_modifier)',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '备注(remark)',
  `sysflag` tinyint unsigned NOT NULL DEFAULT '0' COMMENT '删除标志(sysflag)',
  `program_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '培养规划名称',
  `plan_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '教学计划名称',
  `history_version_id` bigint DEFAULT NULL COMMENT '培养方案来源版本id',
  `history_version` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '培养方案来源版本',
  `version` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '培养规划版本',
  `object_type` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '培养对象',
  `education` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '培养对象学历',
  `academic_type` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '培养对象学制类型',
  `duration_type` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '培养对象学制年限',
  `degree` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '毕业授予学士学位类型',
  `instance_id` bigint DEFAULT NULL COMMENT '教学大纲id,t_csys_teaching_programme_instance',
  `status` int DEFAULT '0' COMMENT '审核状态：0未审核1审核中2审核通过3审核失败',
  `download_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '培养方案下载地址',
  `preview_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '培养方案预览地址',
  `education_level` varchar(225) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '适用对象sys_education_level',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=314 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC COMMENT='培养方案表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `t_csys_training_scheme`
--

LOCK TABLES `t_csys_training_scheme` WRITE;
/*!40000 ALTER TABLE `t_csys_training_scheme` DISABLE KEYS */;
/*!40000 ALTER TABLE `t_csys_training_scheme` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `t_csys_training_scheme_category`
--

DROP TABLE IF EXISTS `t_csys_training_scheme_category`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `t_csys_training_scheme_category` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `parent_id` bigint NOT NULL COMMENT '父级id',
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '名称',
  `leaf` tinyint(1) DEFAULT '0' COMMENT '是否叶子节点(0:否，1:是)',
  `level` tinyint(1) DEFAULT NULL COMMENT '层级',
  `url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '路径',
  `system_type` tinyint unsigned DEFAULT NULL COMMENT '四大类(1:优势工科专业,2:理科基础专业,3:文学法学专业,4:军事管理专业)',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间(create_time)',
  `creator` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '创建者(creator)',
  `last_modified_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间(last_modified_time)',
  `last_modifier` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '更新者(last_modifier)',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '备注(remark)',
  `sysflag` tinyint unsigned NOT NULL DEFAULT '0' COMMENT '删除标志(sysflag)',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=56 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC COMMENT='培养方案门类';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `t_csys_training_scheme_category`
--

LOCK TABLES `t_csys_training_scheme_category` WRITE;
/*!40000 ALTER TABLE `t_csys_training_scheme_category` DISABLE KEYS */;
/*!40000 ALTER TABLE `t_csys_training_scheme_category` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `t_csys_training_scheme_course_schedule`
--

DROP TABLE IF EXISTS `t_csys_training_scheme_course_schedule`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `t_csys_training_scheme_course_schedule` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `scheme_id` bigint NOT NULL COMMENT '培养方案id',
  `course_id` bigint NOT NULL COMMENT '课程id',
  `type` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '课程子模块',
  `term` tinyint DEFAULT NULL COMMENT '学期:1:大一上\r\n            2:大一下\r\n            3:大二上\r\n            4:大二下\r\n            5:大三上\r\n            6:大三下\r\n            7:大四上\r\n            8:大四下',
  `hours` double DEFAULT NULL COMMENT '总课时',
  `theory_hours` double DEFAULT NULL COMMENT '理论课时',
  `practice_hours` double DEFAULT NULL COMMENT '实践课时',
  `checked` tinyint unsigned NOT NULL DEFAULT '1' COMMENT '课程是否已选',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间(create_time)',
  `creator` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '创建者(creator)',
  `last_modified_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间(last_modified_time)',
  `last_modifier` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '更新者(last_modifier)',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '备注(remark)',
  `sysflag` tinyint unsigned NOT NULL DEFAULT '0' COMMENT '删除标志(sysflag)',
  `teach_hours` double DEFAULT NULL COMMENT '讲授学时',
  `course_attr` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '课程类型:1 必修 2：选修 3：任选',
  `credits` double DEFAULT NULL COMMENT '学分',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `scheme_index` (`scheme_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=23027 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC COMMENT='培养方案排课表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `t_csys_training_scheme_course_schedule`
--

LOCK TABLES `t_csys_training_scheme_course_schedule` WRITE;
/*!40000 ALTER TABLE `t_csys_training_scheme_course_schedule` DISABLE KEYS */;
/*!40000 ALTER TABLE `t_csys_training_scheme_course_schedule` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `t_csys_training_scheme_ref_course`
--

DROP TABLE IF EXISTS `t_csys_training_scheme_ref_course`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `t_csys_training_scheme_ref_course` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `scheme_id` bigint NOT NULL COMMENT '培养方案id',
  `course_id` bigint NOT NULL COMMENT '课程id',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `scheme_course_index` (`scheme_id`,`course_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=11635 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC COMMENT='培养方案课程关联';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `t_csys_training_scheme_ref_course`
--

LOCK TABLES `t_csys_training_scheme_ref_course` WRITE;
/*!40000 ALTER TABLE `t_csys_training_scheme_ref_course` DISABLE KEYS */;
/*!40000 ALTER TABLE `t_csys_training_scheme_ref_course` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `t_csys_training_scheme_week`
--

DROP TABLE IF EXISTS `t_csys_training_scheme_week`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `t_csys_training_scheme_week` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `scheme_id` bigint DEFAULT NULL COMMENT '培养方案id',
  `course_teaching` int DEFAULT NULL COMMENT '课程教学',
  `practice_teaching` int DEFAULT NULL COMMENT '集中实践教学',
  `vacation` int DEFAULT NULL COMMENT '假期休整',
  `motor_driven` int DEFAULT NULL COMMENT '机动',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=29 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='修业时间分配情况';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `t_csys_training_scheme_week`
--

LOCK TABLES `t_csys_training_scheme_week` WRITE;
/*!40000 ALTER TABLE `t_csys_training_scheme_week` DISABLE KEYS */;
/*!40000 ALTER TABLE `t_csys_training_scheme_week` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-07-14 15:43:51
