# 2026版课程教学计划表结构设计（按总库课程与培养方案上下文修订）

## 1. 新业务规则修订

本次设计按以下规则调整：

1. 系统存在课程总库。总库课程在 `t_csys_course` 中通常表现为 `source_id is null`、`template_type = 1`。
2. 不同培养方案调用总库课程时，会在 `t_csys_course` 中生成调用课程。调用课程通过 `source_id` 指向总库课程。
3. 课程教学计划绑定在总库课程上，不直接绑定某一个培养方案或某一个调用课程。
4. 教学计划需要展示或导出“适用对象、适用专业、开课学期、课程模块、修读性质”等信息时，应先用教学计划的 `source_course_id` 查询调用课程，再通过调用课程反查引用它的培养方案、排课记录等上下文。
5. 当前版本不使用 `t_csys_course_target` 作为课程目标来源。课程目标改由教学计划目标表保存，目标类型取字典表，例如知识目标、能力目标、素质目标。
6. 培养方案中调用课程绑定毕业要求仍然要通过关联表表达。现有 `t_csys_course_ref_graduation` 可作为兼容来源，按 `course_id = 调用课程ID` 并结合 `college_Id/category_Id/major_Id` 查询；如果允许新建表，建议新增带 `scheme_id` 和主键的 `t_csys_scheme_course_ref_graduation`，避免同一专业不同方案版本混淆。
7. 毕业标准也有总库。培养方案引用毕业标准后，可按方案需要修改，再与引用课程绑定。教学计划中的“支撑毕业要求”应取培养方案上下文下、当前调用课程已经绑定的毕业标准，而不是直接从毕业标准总库任意选择。
8. “课程目标与支撑毕业要求”页面按 tab 切换不同培养方案上下文。同一门总库课程在不同培养方案中被调用时，每个 tab 根据该上下文的调用课程查询已绑定毕业要求。
9. 目标内容为手工录入。新增目标内容与绑定毕业要求在同一个弹框中完成，每次只保存一条目标内容数据；一条目标内容可以绑定一个或多个当前 tab 下允许的毕业要求。
10. 知识体系只使用 `t_csys_course_knowledge_unit` 与 `t_csys_course_knowledge_point`，不再使用 `t_csys_source_domain` 系列表。

## 2. 现有表复用关系

| 现有表 | 使用方式 |
| --- | --- |
| `t_csys_course` | 课程总库与调用课程都在此表。教学计划绑定总库课程 `source_course_id`，调用课程通过 `source_id = source_course_id` 查询。 |
| `t_csys_training_scheme_ref_course` | 培养方案与调用课程的引用关系。通过调用课程 ID 反查培养方案。 |
| `t_csys_training_scheme_course_schedule` | 调用课程在培养方案中的学期、理论/实践学时、学分、修读性质等安排。 |
| `t_csys_training_scheme` | 培养方案上下文，提供培养对象、培养层次、门类、专业、版本等信息。 |
| `t_csys_std_graduation` | 毕业标准。总库毕业标准与方案引用后的毕业标准通过 `source_id`、`scheme_id` 区分。 |
| `t_csys_course_ref_graduation` | 课程与毕业要求关联表。兼容旧结构时用于保存“调用课程 + 毕业标准”的绑定关系，其中 `course_id` 应为调用课程 ID；查询时结合当前 tab 的学院、门类、专业维度过滤。 |
| `t_csys_course_knowledge_unit` | 课程知识单元来源。 |
| `t_csys_course_knowledge_point` | 课程知识点来源。 |

不再作为新设计直接依赖的表：

| 表 | 原因 |
| --- | --- |
| `t_csys_course_target` | 当前版本未使用，课程目标改由教学计划目标表保存。 |
| `t_csys_source_domain` / `t_csys_source_unit` / `t_csys_source_point` | 新逻辑不再使用，知识体系来源改为课程知识单元和知识点。 |

说明：如果后续允许调整课程与毕业要求绑定表，建议用 `t_csys_scheme_course_ref_graduation` 替代 `t_csys_course_ref_graduation` 承接新逻辑；如果短期不改现有结构，则继续读取 `t_csys_course_ref_graduation`，但教学计划目标绑定表中要保留 `scheme_id + quote_course_id + graduation_id` 快照。

## 3. 推荐表结构总览

| 表名 | 说明 |
| --- | --- |
| `t_csys_teaching_plan` | 教学计划主表，绑定总库课程。 |
| `t_csys_teaching_plan_context` | 教学计划适用上下文，记录总库课程被哪些培养方案调用，以及调用课程、排课、专业、对象等快照。也可由视图实时生成。 |
| `t_csys_scheme_course_ref_graduation` | 推荐新增的培养方案课程毕业要求关联表，保存“培养方案 + 调用课程 + 方案内毕业要求”的绑定。若短期复用旧表，则由 `t_csys_course_ref_graduation` 承担此关系。 |
| `t_csys_teaching_plan_teacher` | 教员团队。 |
| `t_csys_teaching_plan_section` | 大段文本章节，如任务背景、总体设计、组织方式、团队管理等。 |
| `t_csys_teaching_plan_objective` | 教学计划目标内容。按 `plan_id + context_id + 目标类型字典编码` 保存，目标内容手工录入，一条记录对应页面中的一行目标内容。 |
| `t_csys_teaching_plan_objective_ref` | 教学目标支撑毕业要求。每条记录表示一条目标内容绑定一个当前 tab 下允许的毕业要求。 |
| `t_csys_teaching_plan_content` | 专题、模块、实验、项目、大作业等内容与学时安排。 |
| `t_csys_teaching_plan_target_design` | 知识/能力/素质目标达成设计，绑定知识单元、知识点、教学环节、教法、学法、观测点。 |
| `t_csys_teaching_plan_practice_item` | 实验项目、实践项目、设计实验、验证实验。 |
| `t_csys_teaching_plan_practice_item_detail` | 实验/实践项目的目的、原理、内容、结果、教学设计等明细。 |
| `t_csys_teaching_plan_assessment` | 考核项目、成果评价、权重、评价标准。 |
| `t_csys_teaching_plan_textbook` | 教材、实验教材、实验指导书。 |
| `t_csys_teaching_plan_condition` | 教室、平台、实验室、场地、装备等条件保障。 |
| `t_csys_teaching_plan_ref` | 支撑课程、实践训练课目、涉及知识单元/知识点等通用引用。 |
| `t_csys_teaching_plan_process_step` | 实施步骤、阶段划分、项目步骤及要求。 |

## 4. 核心取值链路

教学计划绑定总库课程：

```sql
t_csys_teaching_plan.source_course_id = 总库课程 t_csys_course.id
```

查询调用课程：

```sql
select quote_course.*
from t_csys_course quote_course
where quote_course.source_id = #{sourceCourseId}
  and quote_course.sysflag = 0;
```

查询调用课程对应培养方案与排课：

```sql
select
  p.id as plan_id,
  source_course.id as source_course_id,
  quote_course.id as quote_course_id,
  ts.id as scheme_id,
  scs.id as schedule_id,
  ts.education_level,
  ts.object_type,
  ts.category_id,
  ts.major_id,
  ts.sub_major_id,
  quote_course.course_Module,
  quote_course.course_Module_Children,
  quote_course.semester_Schedule,
  scs.term,
  scs.course_attr,
  scs.hours,
  scs.teach_hours,
  scs.practice_hours,
  scs.credits
from t_csys_teaching_plan p
join t_csys_course source_course
  on source_course.id = p.source_course_id
 and source_course.sysflag = 0
join t_csys_course quote_course
  on quote_course.source_id = source_course.id
 and quote_course.sysflag = 0
join t_csys_training_scheme_ref_course trc
  on trc.course_id = quote_course.id
join t_csys_training_scheme ts
  on ts.id = trc.scheme_id
 and ts.sysflag = 0
left join t_csys_training_scheme_course_schedule scs
  on scs.scheme_id = ts.id
 and scs.course_id = quote_course.id
 and scs.sysflag = 0
where p.id = #{planId}
  and p.sysflag = 0;
```

查询当前 tab 下该调用课程可绑定的毕业要求（兼容现有 `t_csys_course_ref_graduation`）：

```sql
select
  c.id as context_id,
  c.quote_course_id,
  c.scheme_id,
  crg.course_id,
  crg.graduation_id,
  sg.code as graduation_code,
  sg.name as graduation_name,
  sg.graduation_type
from t_csys_teaching_plan_context c
join t_csys_course_ref_graduation crg
  on crg.course_id = c.quote_course_id
 and (crg.college_Id is null or crg.college_Id = c.college_id)
 and (crg.category_Id is null or crg.category_Id = c.category_id)
 and (crg.major_Id is null or crg.major_Id = c.major_id)
join t_csys_std_graduation sg
  on sg.id = crg.graduation_id
 and sg.sysflag = 0
 and (sg.scheme_Id = c.scheme_id or sg.scheme_Id is null)
where c.id = #{contextId}
  and c.plan_id = #{planId}
  and c.sysflag = 0;
```

如果新增 `t_csys_scheme_course_ref_graduation`，当前 tab 的毕业要求直接按 `scheme_id + quote_course_id` 查询：

```sql
select
  scrg.id as scheme_course_graduation_id,
  scrg.scheme_id,
  scrg.quote_course_id,
  scrg.graduation_id,
  sg.code as graduation_code,
  sg.name as graduation_name,
  sg.graduation_type
from t_csys_teaching_plan_context c
join t_csys_scheme_course_ref_graduation scrg
  on scrg.scheme_id = c.scheme_id
 and scrg.quote_course_id = c.quote_course_id
 and scrg.sysflag = 0
join t_csys_std_graduation sg
  on sg.id = scrg.graduation_id
 and sg.sysflag = 0
where c.id = #{contextId}
  and c.plan_id = #{planId}
  and c.sysflag = 0;
```

说明：`t_csys_teaching_plan_context` 可以按上面 SQL 定时同步成快照，也可以用视图实时查询。如果导出文件、审核留痕、历史版本必须稳定，建议落快照表；如果只做实时展示，可以先建视图。

## 5. Word 模板映射

| Word 表 | 模板内容 | 新设计落表 |
| --- | --- | --- |
| 表1/12/19/26 | 基本信息 | 主数据来自 `t_csys_teaching_plan` + 总库课程；适用对象、专业、学期、课程模块、修读性质来自 `t_csys_teaching_plan_context`。 |
| 表2/13 | 教员团队 | `t_csys_teaching_plan_teacher`。 |
| 表3 | 知识/能力/素质目标与支撑毕业要求 | 目标内容入 `t_csys_teaching_plan_objective`，目标类型取字典；支撑毕业要求入 `t_csys_teaching_plan_objective_ref`，只能绑定当前 context 中调用课程已关联的方案内毕业标准。 |
| 表4 | 专题/项目/实验/大作业学时安排 | `t_csys_teaching_plan_content`。 |
| 表5 | 知识单元、知识点、教学环节、教法、学法 | `t_csys_teaching_plan_target_design`，知识来源只取 `t_csys_course_knowledge_unit` / `t_csys_course_knowledge_point`。 |
| 表6/7 | 能力/素质目标观测点与教学设计 | `t_csys_teaching_plan_target_design`。 |
| 表8/15/16 | 实验或实践项目设计与学时安排 | `t_csys_teaching_plan_practice_item` + `t_csys_teaching_plan_practice_item_detail`。 |
| 表9/17/24/29 | 考核与成果评价 | `t_csys_teaching_plan_assessment`；计分规则放 `t_csys_teaching_plan.score_rule`。 |
| 表10/18 | 教材、实验教材、实验指导书 | `t_csys_teaching_plan_textbook`。 |
| 表11/25/30 | 条件保障 | `t_csys_teaching_plan_condition`。 |
| 表14/20/21/23/27/28 | 任务背景、训练目的、总体设计、组织方式、复杂问题、团队管理 | `t_csys_teaching_plan_section` + `t_csys_teaching_plan_ref` + `t_csys_teaching_plan_process_step`。 |
| 表22 | 训练模块、内容、目的、时间安排 | `t_csys_teaching_plan_content`。 |

## 6. MySQL DDL 草案

```sql
CREATE TABLE `t_csys_teaching_plan` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `root_plan_id` bigint DEFAULT NULL COMMENT '同一教学计划版本根ID',
  `source_course_id` bigint NOT NULL COMMENT '总库课程ID，关联t_csys_course.id',
  `plan_type` tinyint NOT NULL COMMENT '计划类型：1普通课程 2实验课程 3实践训练课目 4实践项目',
  `version` varchar(20) DEFAULT NULL COMMENT '教学计划版本，如2026、V1.0',
  `current_flag` tinyint NOT NULL DEFAULT 1 COMMENT '是否当前版本：1是 0否',
  `enabled_term` varchar(50) DEFAULT NULL COMMENT '启用时间，如2026年春季学期',
  `status` tinyint NOT NULL DEFAULT 0 COMMENT '状态：0草稿 1审核中 2通过 3退回 9停用',
  `source_course_name` varchar(255) DEFAULT NULL COMMENT '总库课程名称快照',
  `source_course_code` varchar(50) DEFAULT NULL COMMENT '总库课程编号快照',
  `source_course_en_name` varchar(255) DEFAULT NULL COMMENT '总库课程英文名快照',
  `source_hours` decimal(8,2) DEFAULT NULL COMMENT '总库课程总学时快照',
  `source_teach_hours` decimal(8,2) DEFAULT NULL COMMENT '总库课程讲授学时快照',
  `source_practice_hours` decimal(8,2) DEFAULT NULL COMMENT '总库课程实践/实验学时快照',
  `source_credit` decimal(8,2) DEFAULT NULL COMMENT '总库课程学分快照',
  `score_rule` text COMMENT '计分规则',
  `file_id` varchar(255) DEFAULT NULL COMMENT '生成或上传文件ID',
  `file_name` varchar(255) DEFAULT NULL COMMENT '文件名称',
  `download_url` varchar(500) DEFAULT NULL COMMENT '下载地址',
  `preview_url` varchar(500) DEFAULT NULL COMMENT '预览地址',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `creator` varchar(64) DEFAULT NULL COMMENT '创建人',
  `last_modified_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `last_modifier` varchar(64) DEFAULT NULL COMMENT '更新人',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `sysflag` tinyint unsigned NOT NULL DEFAULT 0 COMMENT '删除标识：0正常 2删除',
  PRIMARY KEY (`id`),
  KEY `idx_tp_source_course` (`source_course_id`),
  KEY `idx_tp_root` (`root_plan_id`),
  KEY `idx_tp_type_status` (`plan_type`,`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='课程教学计划主表';

CREATE TABLE `t_csys_teaching_plan_context` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `plan_id` bigint NOT NULL COMMENT '教学计划ID',
  `source_course_id` bigint NOT NULL COMMENT '总库课程ID',
  `quote_course_id` bigint NOT NULL COMMENT '调用课程ID，t_csys_course.id',
  `scheme_id` bigint NOT NULL COMMENT '培养方案ID',
  `schedule_id` bigint DEFAULT NULL COMMENT '培养方案排课ID',
  `scheme_name` varchar(255) DEFAULT NULL COMMENT '培养方案名称快照',
  `scheme_version` varchar(20) DEFAULT NULL COMMENT '培养方案版本快照',
  `education_level` varchar(225) DEFAULT NULL COMMENT '适用对象/培养层次',
  `object_type` varchar(50) DEFAULT NULL COMMENT '培养对象类型',
  `education` varchar(50) DEFAULT NULL COMMENT '学历',
  `academic_type` varchar(50) DEFAULT NULL COMMENT '学制类型',
  `duration_type` varchar(50) DEFAULT NULL COMMENT '学制年限',
  `degree` varchar(50) DEFAULT NULL COMMENT '授予学位类型',
  `college_id` bigint DEFAULT NULL COMMENT '学院ID',
  `category_id` bigint DEFAULT NULL COMMENT '门类ID',
  `major_id` bigint DEFAULT NULL COMMENT '专业ID',
  `sub_major_id` bigint DEFAULT NULL COMMENT '专业方向ID',
  `course_module` varchar(64) DEFAULT NULL COMMENT '课程模块编码',
  `course_module_children` varchar(64) DEFAULT NULL COMMENT '课程子模块编码',
  `semester_schedule` varchar(64) DEFAULT NULL COMMENT '学期安排',
  `term` tinyint DEFAULT NULL COMMENT '开课学期',
  `course_attr` varchar(64) DEFAULT NULL COMMENT '修读性质',
  `time_arrange` varchar(255) DEFAULT NULL COMMENT '时间安排',
  `hours` decimal(8,2) DEFAULT NULL COMMENT '上下文总学时',
  `teach_hours` decimal(8,2) DEFAULT NULL COMMENT '上下文讲授学时',
  `practice_hours` decimal(8,2) DEFAULT NULL COMMENT '上下文实践/实验学时',
  `credits` decimal(8,2) DEFAULT NULL COMMENT '上下文学分',
  `sync_time` datetime DEFAULT NULL COMMENT '从课程调用关系同步时间',
  `sync_flag` tinyint NOT NULL DEFAULT 1 COMMENT '同步状态：1有效 2调用关系已失效',
  `sort` int DEFAULT 1 COMMENT '排序',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `creator` varchar(64) DEFAULT NULL COMMENT '创建人',
  `last_modified_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `last_modifier` varchar(64) DEFAULT NULL COMMENT '更新人',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `sysflag` tinyint unsigned NOT NULL DEFAULT 0 COMMENT '删除标识',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_tp_context` (`plan_id`,`quote_course_id`,`scheme_id`),
  KEY `idx_tp_context_source` (`source_course_id`),
  KEY `idx_tp_context_scheme` (`scheme_id`),
  KEY `idx_tp_context_quote_course` (`quote_course_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='教学计划调用课程上下文';

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
  `support_level` varchar(64) DEFAULT NULL COMMENT '支撑强度/支撑程度字典编码，可选',
  `graduation_code` varchar(100) DEFAULT NULL COMMENT '毕业标准编码快照',
  `graduation_name` varchar(500) DEFAULT NULL COMMENT '毕业标准名称快照',
  `sort` int DEFAULT 1 COMMENT '排序',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `creator` varchar(64) DEFAULT NULL COMMENT '创建人',
  `last_modified_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `last_modifier` varchar(64) DEFAULT NULL COMMENT '更新人',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `sysflag` tinyint unsigned NOT NULL DEFAULT 0 COMMENT '删除标识：0正常 2删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_scheme_course_graduation` (`scheme_id`,`quote_course_id`,`graduation_id`),
  KEY `idx_scrg_quote_course` (`quote_course_id`),
  KEY `idx_scrg_source_course` (`source_course_id`),
  KEY `idx_scrg_graduation` (`graduation_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='培养方案调用课程毕业要求关联表';

CREATE TABLE `t_csys_teaching_plan_teacher` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `plan_id` bigint NOT NULL COMMENT '教学计划ID',
  `teacher_id` varchar(100) DEFAULT NULL COMMENT '教员ID(外库字符串主键)',
  `teacher_name` varchar(100) NOT NULL COMMENT '教员姓名',
  `professional_title` varchar(100) DEFAULT NULL COMMENT '职称',
  `duty` varchar(100) DEFAULT NULL COMMENT '职责',
  `lecture_content` text COMMENT '主讲内容',
  `sort` int DEFAULT 1 COMMENT '排序',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `creator` varchar(64) DEFAULT NULL COMMENT '创建人',
  `last_modified_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `last_modifier` varchar(64) DEFAULT NULL COMMENT '更新人',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `sysflag` tinyint unsigned NOT NULL DEFAULT 0 COMMENT '删除标识',
  PRIMARY KEY (`id`),
  KEY `idx_tp_teacher_plan` (`plan_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='教学计划教员团队';

CREATE TABLE `t_csys_teaching_plan_section` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `plan_id` bigint NOT NULL COMMENT '教学计划ID',
  `section_code` varchar(64) NOT NULL COMMENT '章节编码：task_background、overall_design等',
  `section_title` varchar(255) DEFAULT NULL COMMENT '章节标题',
  `content` longtext COMMENT '章节内容',
  `sort` int DEFAULT 1 COMMENT '排序',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `creator` varchar(64) DEFAULT NULL COMMENT '创建人',
  `last_modified_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `last_modifier` varchar(64) DEFAULT NULL COMMENT '更新人',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `sysflag` tinyint unsigned NOT NULL DEFAULT 0 COMMENT '删除标识',
  PRIMARY KEY (`id`),
  KEY `idx_tp_section_plan` (`plan_id`),
  KEY `idx_tp_section_code` (`section_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='教学计划文本章节';

CREATE TABLE `t_csys_teaching_plan_objective` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `plan_id` bigint NOT NULL COMMENT '教学计划ID',
  `context_id` bigint NOT NULL COMMENT '教学计划调用上下文ID，对应页面当前培养方案tab',
  `objective_type_code` varchar(64) NOT NULL COMMENT '目标类型字典编码：知识目标/能力目标/素质目标',
  `objective_type_name` varchar(100) DEFAULT NULL COMMENT '目标类型名称快照',
  `content` text NOT NULL COMMENT '目标内容，手工录入',
  `source_mode` tinyint DEFAULT 2 COMMENT '来源方式：2手工录入',
  `sort` int DEFAULT 1 COMMENT '排序',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `creator` varchar(64) DEFAULT NULL COMMENT '创建人',
  `last_modified_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `last_modifier` varchar(64) DEFAULT NULL COMMENT '更新人',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `sysflag` tinyint unsigned NOT NULL DEFAULT 0 COMMENT '删除标识',
  PRIMARY KEY (`id`),
  KEY `idx_tp_obj_plan` (`plan_id`),
  KEY `idx_tp_obj_context` (`context_id`),
  KEY `idx_tp_obj_type` (`objective_type_code`),
  KEY `idx_tp_obj_context_type` (`plan_id`,`context_id`,`objective_type_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='教学计划目标';

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
  `graduation_code` varchar(100) DEFAULT NULL COMMENT '毕业标准编码快照',
  `graduation_name` varchar(500) DEFAULT NULL COMMENT '毕业标准名称快照',
  `graduation_bind_source` varchar(64) DEFAULT NULL COMMENT '绑定来源：scheme_course_ref或course_ref_graduation',
  `support_desc` varchar(1000) DEFAULT NULL COMMENT '支撑说明',
  `sort` int DEFAULT 1 COMMENT '排序',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `creator` varchar(64) DEFAULT NULL COMMENT '创建人',
  `last_modified_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `last_modifier` varchar(64) DEFAULT NULL COMMENT '更新人',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `sysflag` tinyint unsigned NOT NULL DEFAULT 0 COMMENT '删除标识',
  PRIMARY KEY (`id`),
  KEY `idx_tp_obj_ref_plan` (`plan_id`),
  KEY `idx_tp_obj_ref_context` (`context_id`),
  KEY `idx_tp_obj_ref_obj` (`objective_id`),
  KEY `idx_tp_obj_ref_scheme_course_graduation` (`scheme_course_graduation_id`),
  KEY `idx_tp_obj_ref_scheme_course` (`scheme_id`,`quote_course_id`),
  KEY `idx_tp_obj_ref_graduation` (`graduation_id`),
  UNIQUE KEY `uk_tp_obj_ref` (`objective_id`,`context_id`,`graduation_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='教学计划目标支撑毕业要求';

CREATE TABLE `t_csys_teaching_plan_content` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `plan_id` bigint NOT NULL COMMENT '教学计划ID',
  `parent_id` bigint DEFAULT NULL COMMENT '父级内容ID',
  `content_type` tinyint NOT NULL COMMENT '内容类型：1专题 2课程项目 3实验 4大作业 5训练模块 6模块内容',
  `title` varchar(255) DEFAULT NULL COMMENT '专题/模块/内容名称',
  `content` text COMMENT '内容说明',
  `purpose` text COMMENT '目的',
  `hours` decimal(8,2) DEFAULT NULL COMMENT '学时',
  `time_arrange` varchar(100) DEFAULT NULL COMMENT '时间安排，如1天',
  `sort` int DEFAULT 1 COMMENT '排序',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `creator` varchar(64) DEFAULT NULL COMMENT '创建人',
  `last_modified_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `last_modifier` varchar(64) DEFAULT NULL COMMENT '更新人',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `sysflag` tinyint unsigned NOT NULL DEFAULT 0 COMMENT '删除标识',
  PRIMARY KEY (`id`),
  KEY `idx_tp_content_plan` (`plan_id`),
  KEY `idx_tp_content_parent` (`parent_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='教学计划内容与学时安排';

CREATE TABLE `t_csys_teaching_plan_target_design` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `plan_id` bigint NOT NULL COMMENT '教学计划ID',
  `context_id` bigint DEFAULT NULL COMMENT '教学计划调用上下文ID；按tab维护时填写',
  `design_type_code` varchar(64) NOT NULL COMMENT '设计类型字典编码：知识目标/能力目标/素质目标',
  `objective_id` bigint DEFAULT NULL COMMENT '对应教学计划目标ID',
  `knowledge_unit_id` bigint DEFAULT NULL COMMENT '知识单元ID，t_csys_course_knowledge_unit.id',
  `knowledge_unit_name` varchar(255) DEFAULT NULL COMMENT '知识单元名称快照',
  `knowledge_point_id` bigint DEFAULT NULL COMMENT '知识点ID，t_csys_course_knowledge_point.id',
  `knowledge_point_name` varchar(500) DEFAULT NULL COMMENT '知识点名称快照',
  `observation_point` text COMMENT '观测点',
  `content_ids` json DEFAULT NULL COMMENT '关联教学内容ID数组',
  `content_text` text COMMENT '教学内容文本快照',
  `teaching_link` varchar(255) DEFAULT NULL COMMENT '教学环节',
  `teaching_method` varchar(255) DEFAULT NULL COMMENT '教法',
  `learning_method` varchar(255) DEFAULT NULL COMMENT '学法',
  `hours` decimal(8,2) DEFAULT NULL COMMENT '学时',
  `teaching_design` text COMMENT '教学设计',
  `sort` int DEFAULT 1 COMMENT '排序',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `creator` varchar(64) DEFAULT NULL COMMENT '创建人',
  `last_modified_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `last_modifier` varchar(64) DEFAULT NULL COMMENT '更新人',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `sysflag` tinyint unsigned NOT NULL DEFAULT 0 COMMENT '删除标识',
  PRIMARY KEY (`id`),
  KEY `idx_tp_design_plan` (`plan_id`),
  KEY `idx_tp_design_context` (`context_id`),
  KEY `idx_tp_design_obj` (`objective_id`),
  KEY `idx_tp_design_knowledge` (`knowledge_unit_id`,`knowledge_point_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='教学目标达成设计';

CREATE TABLE `t_csys_teaching_plan_practice_item` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `plan_id` bigint NOT NULL COMMENT '教学计划ID',
  `item_type` tinyint NOT NULL COMMENT '项目类型：1实验 2实践项目 3设计实验 4验证实验',
  `name` varchar(255) NOT NULL COMMENT '项目名称',
  `hours` decimal(8,2) DEFAULT NULL COMMENT '学时',
  `group_info` varchar(255) DEFAULT NULL COMMENT '分组情况',
  `experiment_nature` varchar(100) DEFAULT NULL COMMENT '实验性质',
  `study_nature` varchar(100) DEFAULT NULL COMMENT '修读性质',
  `sort` int DEFAULT 1 COMMENT '排序',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `creator` varchar(64) DEFAULT NULL COMMENT '创建人',
  `last_modified_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `last_modifier` varchar(64) DEFAULT NULL COMMENT '更新人',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `sysflag` tinyint unsigned NOT NULL DEFAULT 0 COMMENT '删除标识',
  PRIMARY KEY (`id`),
  KEY `idx_tp_item_plan` (`plan_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='教学计划实验/实践项目';

CREATE TABLE `t_csys_teaching_plan_practice_item_detail` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `item_id` bigint NOT NULL COMMENT '实验/实践项目ID',
  `detail_type` varchar(64) NOT NULL COMMENT '明细类型：purpose_task、ability_point、principle、content_requirement、result_requirement、teaching_design、complex_problem、main_task、overall_design、outcome_requirement',
  `objective_id` bigint DEFAULT NULL COMMENT '训练能力点或支撑目标ID',
  `content` longtext COMMENT '明细内容',
  `sort` int DEFAULT 1 COMMENT '排序',
  PRIMARY KEY (`id`),
  KEY `idx_tp_item_detail_item` (`item_id`),
  KEY `idx_tp_item_detail_obj` (`objective_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='实验/实践项目明细';

CREATE TABLE `t_csys_teaching_plan_assessment` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `plan_id` bigint NOT NULL COMMENT '教学计划ID',
  `item_id` bigint DEFAULT NULL COMMENT '关联实验/实践项目ID',
  `assessment_category` tinyint DEFAULT NULL COMMENT '考核类别：1终结性 2形成性 3实验项目 4训练课目 5成果评价',
  `assessment_item` varchar(255) DEFAULT NULL COMMENT '考核项目或成果形式',
  `method` varchar(255) DEFAULT NULL COMMENT '考核方式',
  `mechanism` varchar(255) DEFAULT NULL COMMENT '评定机制',
  `score_system` varchar(100) DEFAULT NULL COMMENT '成绩评定：百分制/五级制/两级制',
  `outcome_type` tinyint DEFAULT 0 COMMENT '成果类型：0无 1个人成果 2团队成果',
  `assessed_content` text COMMENT '评价的知识和能力',
  `weight` decimal(6,2) DEFAULT NULL COMMENT '权重',
  `standard` longtext COMMENT '评价标准/评价准则',
  `sort` int DEFAULT 1 COMMENT '排序',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `creator` varchar(64) DEFAULT NULL COMMENT '创建人',
  `last_modified_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `last_modifier` varchar(64) DEFAULT NULL COMMENT '更新人',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `sysflag` tinyint unsigned NOT NULL DEFAULT 0 COMMENT '删除标识',
  PRIMARY KEY (`id`),
  KEY `idx_tp_assess_plan` (`plan_id`),
  KEY `idx_tp_assess_item` (`item_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='教学计划考核评价';

CREATE TABLE `t_csys_teaching_plan_textbook` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `plan_id` bigint NOT NULL COMMENT '教学计划ID',
  `material_nature` varchar(100) DEFAULT NULL COMMENT '教材性质',
  `name` varchar(255) DEFAULT NULL COMMENT '教材名称',
  `first_author` varchar(100) DEFAULT NULL COMMENT '第一作者',
  `edition` varchar(100) DEFAULT NULL COMMENT '版次',
  `publisher` varchar(255) DEFAULT NULL COMMENT '出版或颁发单位',
  `publish_time` varchar(50) DEFAULT NULL COMMENT '出版或颁发时间',
  `isbn` varchar(100) DEFAULT NULL COMMENT 'ISBN号或统一书号/文件号',
  `publish_method` varchar(100) DEFAULT NULL COMMENT '出版方式',
  `sort` int DEFAULT 1 COMMENT '排序',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `creator` varchar(64) DEFAULT NULL COMMENT '创建人',
  `last_modified_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `last_modifier` varchar(64) DEFAULT NULL COMMENT '更新人',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `sysflag` tinyint unsigned NOT NULL DEFAULT 0 COMMENT '删除标识',
  PRIMARY KEY (`id`),
  KEY `idx_tp_textbook_plan` (`plan_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='教学计划教材';

CREATE TABLE `t_csys_teaching_plan_condition` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `plan_id` bigint NOT NULL COMMENT '教学计划ID',
  `condition_type` varchar(100) NOT NULL COMMENT '条件类型',
  `requirement` longtext COMMENT '有关要求',
  `sort` int DEFAULT 1 COMMENT '排序',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `creator` varchar(64) DEFAULT NULL COMMENT '创建人',
  `last_modified_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `last_modifier` varchar(64) DEFAULT NULL COMMENT '更新人',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `sysflag` tinyint unsigned NOT NULL DEFAULT 0 COMMENT '删除标识',
  PRIMARY KEY (`id`),
  KEY `idx_tp_condition_plan` (`plan_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='教学计划条件保障';

CREATE TABLE `t_csys_teaching_plan_ref` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `plan_id` bigint NOT NULL COMMENT '教学计划ID',
  `context_id` bigint DEFAULT NULL COMMENT '调用上下文ID，可为空',
  `ref_type` tinyint NOT NULL COMMENT '引用类型：1支撑总库课程 2支撑调用课程/训练课目 3知识单元 4知识点 5教学目标',
  `ref_id` bigint DEFAULT NULL COMMENT '引用对象ID',
  `ref_name` varchar(500) DEFAULT NULL COMMENT '引用对象名称快照',
  `ref_hours` decimal(8,2) DEFAULT NULL COMMENT '引用课程/课目学时快照',
  `sort` int DEFAULT 1 COMMENT '排序',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `creator` varchar(64) DEFAULT NULL COMMENT '创建人',
  `last_modified_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `last_modifier` varchar(64) DEFAULT NULL COMMENT '更新人',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `sysflag` tinyint unsigned NOT NULL DEFAULT 0 COMMENT '删除标识',
  PRIMARY KEY (`id`),
  KEY `idx_tp_ref_plan` (`plan_id`),
  KEY `idx_tp_ref_context` (`context_id`),
  KEY `idx_tp_ref_type` (`ref_type`,`ref_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='教学计划通用引用';

CREATE TABLE `t_csys_teaching_plan_process_step` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `plan_id` bigint NOT NULL COMMENT '教学计划ID',
  `item_id` bigint DEFAULT NULL COMMENT '关联实践项目ID',
  `stage_name` varchar(255) DEFAULT NULL COMMENT '阶段划分',
  `step_name` varchar(255) DEFAULT NULL COMMENT '实施步骤或项目步骤',
  `requirement` longtext COMMENT '有关要求',
  `sort` int DEFAULT 1 COMMENT '排序',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `creator` varchar(64) DEFAULT NULL COMMENT '创建人',
  `last_modified_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `last_modifier` varchar(64) DEFAULT NULL COMMENT '更新人',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `sysflag` tinyint unsigned NOT NULL DEFAULT 0 COMMENT '删除标识',
  PRIMARY KEY (`id`),
  KEY `idx_tp_step_plan` (`plan_id`),
  KEY `idx_tp_step_item` (`item_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='教学计划实施步骤';
```

## 7. 关键实现建议

1. 课程教学计划管理列表应以总库课程为主列表，查询 `t_csys_teaching_plan.source_course_id`，展示课程名称、编号、计划类型、版本、状态、引用方案数量。
2. “引用方案数量/适用对象/专业/学期”不要从教学计划主表直接取，应通过 `source_course_id -> 调用课程 -> 培养方案引用 -> 排课记录` 计算或同步到 `t_csys_teaching_plan_context`。
3. “课程目标与支撑毕业要求”页面的 tab 对应 `t_csys_teaching_plan_context`。切换 tab 后，先根据 `context_id` 得到 `scheme_id + quote_course_id`，再读取该调用课程已绑定的毕业要求。
4. 目标类型使用字典表，不在业务代码中写死数字。页面固定展示“知识目标、能力目标、素质目标”时，也应通过字典编码驱动。
5. 目标内容按 `plan_id + context_id + objective_type_code` 查询。每次弹框保存只新增或修改一条 `t_csys_teaching_plan_objective`，并同步维护这条目标内容对应的 `t_csys_teaching_plan_objective_ref`。
6. 弹框中的毕业要求候选项只能来自当前 tab 下调用课程已绑定的毕业要求。不能跨 tab 绑定，也不能直接从毕业标准总库绕过课程关联表选择。
7. “已绑定/未绑定”不建议冗余存字段，可按 `objective_id` 在 `t_csys_teaching_plan_objective_ref` 中是否存在有效记录计算。tab 上“已填写/未填写”可按该 tab 下是否存在目标内容且必要目标内容均已绑定毕业要求计算。
8. 截图里的目标类型列、操作列是前端表格的合并单元格效果。数据库仍保持一条目标内容一条记录、一条目标内容与一个毕业要求绑定一条记录，不要为合并展示设计宽表。
9. 支撑毕业要求必须带 `context_id` 或至少带 `scheme_id + quote_course_id`，否则无法区分不同培养方案引用毕业标准后的修改版本。
10. 知识单元、知识点字段只引用 `t_csys_course_knowledge_unit`、`t_csys_course_knowledge_point`，不要再预留 `source_domain` 系列表字段。
11. 如果审核或导出需要历史稳定，建议在提交审核/导出时固化 `context` 快照；如果只做实时预览，可以用视图代替 `t_csys_teaching_plan_context`。
