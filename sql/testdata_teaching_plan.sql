-- =====================================================================
-- 教学计划四类接口测试数据（本地库 doinner-curriculum-test-3.2）
-- 数据链：共性毕业要求 -> 三个课程库 -> 知识单元知识点 -> 培养方案
--        -> 调用课程与毕业要求 -> 课程绑定毕业要求
-- 覆盖 plan_type(前后端统一)：1课程(7003) 2实验课程(8003) 3实践训练课目(8002) 4实践项目(8004)
--   * 课程库 type 与 plan_type 编号 2/3 对调：8002 实践训练课目(type=2 课程) plan_type=3，
--     8003 实验课程(type=3 课程) plan_type=2；课程 type=1/3 同为「课程」类别，教学计划区分。
-- 今天改动重点：
--   * 实践训练课目(plan_type=3)第四部分「模块」列已字典化：t_csys_teaching_plan_content.title
--     存 sys_plan_training_module 的 dict_value(1/2/3)，生成 Word 译为 label。
--   * type4 实践项目支撑绑定：before_course_id(支撑课程=7003) / after_course_id
--     (支撑训练课目=8002)，走 t_csys_teaching_plan_support_objective/content。
-- 全部用显式 id，可重复执行前先 DELETE 本文件涉及主键。
-- =====================================================================

-- =====================================================================
-- 0. 清理（幂等：删除本文件涉及的显式主键，便于反复执行）
-- =====================================================================
DELETE FROM t_csys_teaching_plan_support_content  WHERE plan_id = 6004;
DELETE FROM t_csys_teaching_plan_support_objective WHERE plan_id = 6004;
DELETE FROM t_csys_teaching_plan_objective_assessment WHERE plan_id IN (6001,6002,6003,6004);
DELETE FROM t_csys_teaching_plan_target_design     WHERE plan_id IN (6001,6002,6003,6004);
DELETE FROM t_csys_teaching_plan_practice_item_detail WHERE item_id IN (66011,66012,66511,66512);
DELETE FROM t_csys_teaching_plan_practice_item     WHERE plan_id IN (6001,6002,6003,6004);
DELETE FROM t_csys_teaching_plan_assessment        WHERE plan_id IN (6001,6002,6003,6004);
DELETE FROM t_csys_teaching_plan_textbook          WHERE plan_id IN (6001,6002,6003,6004);
DELETE FROM t_csys_teaching_plan_condition         WHERE plan_id IN (6001,6002,6003,6004);
DELETE FROM t_csys_teaching_plan_teacher           WHERE plan_id IN (6001,6002,6003,6004);
DELETE FROM t_csys_teaching_plan_process_step      WHERE plan_id IN (6001,6002,6003,6004);
DELETE FROM t_csys_teaching_plan_section           WHERE plan_id IN (6001,6002,6003,6004);
DELETE FROM t_csys_teaching_plan_content_purpose   WHERE plan_id IN (6001,6002,6003,6004);
DELETE FROM t_csys_teaching_plan_content           WHERE plan_id IN (6001,6002,6003,6004);
DELETE FROM t_csys_teaching_plan_training_purpose_ref WHERE plan_id IN (6001,6002,6003,6004);
DELETE FROM t_csys_teaching_plan_training_purpose  WHERE plan_id IN (6001,6002,6003,6004);
DELETE FROM t_csys_teaching_plan_task_background   WHERE plan_id IN (6001,6002,6003,6004);
DELETE FROM t_csys_teaching_plan_objective_ref     WHERE plan_id IN (6001,6002,6003,6004);
DELETE FROM t_csys_teaching_plan_objective         WHERE plan_id IN (6001,6002,6003,6004);
DELETE FROM t_csys_teaching_plan_context           WHERE plan_id IN (6001,6002,6003,6004);
DELETE FROM t_csys_teaching_plan                   WHERE id IN (6001,6002,6003,6004);
DELETE FROM t_csys_scheme_course_ref_graduation    WHERE id IN (1001,1002,1003,1004,1005);
DELETE FROM t_csys_course_ref_graduation WHERE course_id IN (7001,7002,7003,70031,8002,80021,8003,80031,8004,80041);
DELETE FROM t_csys_course_ref_knowledge_unit       WHERE course_id = 7003;
DELETE FROM t_csys_course_knowledge_point          WHERE unit_id IN (7501,7502);
DELETE FROM t_csys_course_knowledge_unit           WHERE id IN (7501,7502);
DELETE FROM t_csys_training_scheme_ref_course      WHERE scheme_id = 7601;
DELETE FROM t_csys_training_scheme_course_schedule WHERE scheme_id = 7601;
DELETE FROM t_csys_course WHERE id IN (7001,7002,7003,70031,8002,80021,8003,80031,8004,80041);
DELETE FROM t_csys_scheme_ref_major                WHERE scheme_id = 7601;
DELETE FROM t_csys_training_scheme                 WHERE id = 7601;
DELETE FROM t_csys_std_graduation                  WHERE id IN (90101,90102,90103);
DELETE FROM t_csys_std_major                       WHERE id = 9001;

-- =====================================================================
-- 1. 专业 t_csys_std_major
-- =====================================================================
INSERT INTO t_csys_std_major
(id, name, code, parent_id, level, college_id, category_id, leaf, applicable_object, sysflag)
VALUES
(9001, '计算机科学与技术', 'CS001', -1, 1, 1, 1, 1, '本科四年', 0);

-- =====================================================================
-- 2. 共性毕业要求 t_csys_std_graduation
--    type=2(毕业要求)  graduation_type=1知识/2能力/3素质
-- =====================================================================
INSERT INTO t_csys_std_graduation
(id, name, code, parent_id, leaf, level, type, graduation_type, major_id, category_id, education_level, sysflag)
VALUES
(90101, '掌握计算机科学与技术的基础理论与专业知识', 'GR1', -1, 1, 1, 2, '1', 9001, 1, 1, 0),
(90102, '具备计算机工程实践与系统开发能力',          'GR2', -1, 1, 1, 2, '2', 9001, 1, 1, 0),
(90103, '具有良好的职业道德、团队合作与终身学习意识','GR3', -1, 1, 1, 2, '3', 9001, 1, 1, 0);

-- =====================================================================
-- 3. 培养方案 t_csys_training_scheme + t_csys_scheme_ref_major
-- =====================================================================
INSERT INTO t_csys_training_scheme
(id, name, plan_name, program_name, category_id, college_id, major_id, sub_major_id,
 education_level, version, status, sysflag)
VALUES
(7601, '计算机科学与技术2026级培养方案', '计算机科学与技术', 'CS',
 1, 1, 9001, NULL, '1', '2026', 1, 0);

INSERT INTO t_csys_scheme_ref_major (scheme_id, major_id) VALUES (7601, 9001);

-- =====================================================================
-- 4. 三个课程库（type=1 理论课程，按 course_Module 分库）
--    7001 公共基础库 / 7002 学科基础库 / 7003 专业课程库（type1 教学计划主题）
--    8002 实践训练课目(type2) 8003 实验课程(type3) 8004 实践项目(type4)
-- =====================================================================
INSERT INTO t_csys_course
(id, name, code, type, en_name, before_course_id, after_course_id,
 hours, teach_hours, practice_hours, credit, course_attr, location, open_term,
 course_Module, major_Id, source_id, template_type, version, education_level,
 unit, time_week, sysflag, status)
VALUES
-- 三个课程库（type1）
(7001, '大学物理',        'DXWL',  1, 'College Physics',        NULL, NULL,
 64, 56, 8,  4.0, '1', NULL, '1', '69a7f32e2dc370362ef3ee6e', 9001, NULL, NULL, '2026', '1', '1', 16, 0, 1),
(7002, '高等数学',        'GDSX',  1, 'Advanced Mathematics',   NULL, NULL,
 80, 80, 0,  5.0, '1', NULL, '1', '2',                         9001, NULL, NULL, '2026', '1', '1', 16, 0, 1),
(7003, '程序设计基础',    'CXSJJ', 1, 'Programming Fundamentals',NULL, NULL,
 56, 40, 16, 3.5, '1', NULL, '1', '3',                         9001, NULL, NULL, '2026', '1', '1', 16, 0, 1),
-- 调用课程副本（source_id 指向源课，template_type=2 表示被调用）
(70031,'程序设计基础',    'CXSJJ-Q',1,'Programming Fundamentals',NULL, NULL,
 56, 40, 16, 3.5, '1', NULL, '1', '3',                         9001, 7003, 2,    '2026', '1', '1', 16, 0, 1),
-- 四类教学计划课程
(8002, '军事基础训练',    'JSJCXL', 2, 'Basic Military Training', NULL, NULL,
 32, 0,  32, 2.0, '2', '1', '1', NULL,                          9001, NULL, NULL, '2026', '1', '1', 2,  0, 1),
(80021,'军事基础训练',    'JSJCXL-Q',2,'Basic Military Training', NULL, NULL,
 32, 0,  32, 2.0, '2', '1', '1', NULL,                          9001, 8002, 2,    '2026', '1', '1', 2,  0, 1),
(8003, '大学物理实验',    'DXWLSY', 3, 'College Physics Experiment', NULL, NULL,
 24, 0,  24, 1.5, '1', NULL, '3', '3',                          9001, NULL, NULL, '2026', '1', '1', 2,  0, 1),
(80031,'大学物理实验',    'DXWLSY-Q',3,'College Physics Experiment', NULL, NULL,
 24, 0,  24, 1.5, '1', NULL, '3', '3',                          9001, 8003, 2,    '2026', '1', '1', 2,  0, 1),
(8004, '综合课程设计实践','ZHKCSJ', 4, 'Comprehensive Course Design Project',
 '7003', '8002',
 48, 0,  48, 3.0, '1', NULL, '3', '3',                          9001, NULL, NULL, '2026', '1', '1', 6,  0, 1),
(80041,'综合课程设计实践','ZHKCSJ-Q',4,'Comprehensive Course Design Project',
 '7003', '8002',
 48, 0,  48, 3.0, '1', NULL, '3', '3',                          9001, 8004, 2,    '2026', '1', '1', 6,  0, 1);

-- =====================================================================
-- 5. 知识单元/知识点（7003 程序设计基础）
-- =====================================================================
INSERT INTO t_csys_course_knowledge_unit (id, name, sort, create_time)
VALUES (7501, '基础编程概念', 1, NOW()), (7502, '数据结构与算法', 2, NOW());

INSERT INTO t_csys_course_knowledge_point (id, name, sort, unit_id, create_time)
VALUES
(75011, '变量与数据类型', 1, 7501, NOW()),
(75012, '流程控制',      2, 7501, NOW()),
(75021, '线性表',        1, 7502, NOW()),
(75022, '排序算法',      2, 7502, NOW());

INSERT INTO t_csys_course_ref_knowledge_unit (course_id, course_unit_id)
VALUES (7003, 7501), (7003, 7502);

-- =====================================================================
-- 6. 培养方案课程安排（调用课进方案）+ 方案引用课程
-- =====================================================================
INSERT INTO t_csys_training_scheme_course_schedule
(scheme_id, course_id, type, term, hours, teach_hours, practice_hours, course_attr, credits, sysflag)
VALUES
(7601, 70031, '1', 4, 56, 40, 16, '1', 3.5, 0),
(7601, 80021, '2', 2, 32,  0, 32, '2', 2.0, 0),
(7601, 80031, '3', 3, 24,  0, 24, '1', 1.5, 0),
(7601, 80041, '4', 6, 48,  0, 48, '1', 3.0, 0);

INSERT INTO t_csys_training_scheme_ref_course (scheme_id, course_id)
VALUES (7601, 70031), (7601, 80021), (7601, 80031), (7601, 80041);

-- =====================================================================
-- 7. 课程绑定毕业要求 t_csys_course_ref_graduation
--    （源课 + 调用课均绑定；listCourseGraduationByScheme 优先取调用课绑定）
-- =====================================================================
INSERT INTO t_csys_course_ref_graduation (course_id, graduation_id, course_target_id, major_Id)
VALUES
(7003,  90101, 1, 9001),
(70031, 90101, 1, 9001),
(70031, 90102, 2, 9001),
(8002,  90101, 1, 9001),
(8002,  90103, 3, 9001),
(80021, 90101, 1, 9001),
(80021, 90103, 3, 9001),
(8003,  90102, 2, 9001),
(80031, 90102, 2, 9001),
(8004,  90101, 1, 9001),
(8004,  90103, 3, 9001);

-- =====================================================================
-- 8. 方案课程-毕业要求绑定 t_csys_scheme_course_ref_graduation
--    （调用课程与毕业要求）
-- =====================================================================
INSERT INTO t_csys_scheme_course_ref_graduation
(id, scheme_id, quote_course_id, source_course_id, graduation_id, source_graduation_id,
 major_id, support_level, graduation_code, graduation_name, sort, sysflag)
VALUES
(1001, 7601, 70031, 7003, 90101, 90101, 9001, 'H', 'GR1', '掌握计算机科学与技术的基础理论与专业知识', 1, 0),
(1002, 7601, 70031, 7003, 90102, 90102, 9001, 'M', 'GR2', '具备计算机工程实践与系统开发能力',          2, 0),
(1005, 7601, 70031, 7003, 90103, 90103, 9001, 'M', 'GR3', '具有良好的职业道德、团队合作与终身学习意识', 3, 0),
(1003, 7601, 80021, 8002, 90101, 90101, 9001, 'H', 'GR1', '掌握计算机科学与技术的基础理论与专业知识', 1, 0),
(1004, 7601, 80021, 8002, 90103, 90103, 9001, 'M', 'GR3', '具有良好的职业道德、团队合作与终身学习意识', 2, 0);

-- =====================================================================
-- 9. 教学计划主表（四类）
-- plan_type 编号(前后端统一)：1课程 2实验课程 3实践训练课目 4实践项目；
-- 与课程库 type(1课程 2实践训练课目 3实验课程 4实践项目) 2/3 对调。
-- =====================================================================
INSERT INTO t_csys_teaching_plan
(id, root_plan_id, source_course_id, plan_type, version, current_flag, enabled_term, status,
 source_course_name, source_course_code, source_hours, source_teach_hours,
 source_practice_hours, source_credit, score_rule, sysflag)
VALUES
-- status=0 草稿（可覆盖导入）；若改为 1 审核中/2 已通过，importWord 会拒绝覆盖导入
-- 6001 课程(type=1 课程) / 6002 实践训练课目(type=2 课程) / 6003 实验课程(type=3 课程) / 6004 实践项目(type=4 课程)
(6001, 6001, 7003, 1, '1', 1, '2026', 0, '程序设计基础', 'CXSJJ', 56, 40, 16, 3.5,
 '平时30%+期末70%', 0),
(6002, 6002, 8002, 3, '1', 1, '2026', 0, '军事基础训练', 'JSJCXL', 32, 0, 32, 2.0,
 '训练考核60%+平时表现40%', 0),
(6003, 6003, 8003, 2, '1', 1, '2026', 0, '大学物理实验', 'DXWLSY', 24, 0, 24, 1.5,
 '实验报告40%+操作考核60%', 0),
(6004, 6004, 8004, 4, '1', 1, '2026', 0, '综合课程设计实践', 'ZHKCSJ', 48, 0, 48, 3.0,
 '成果评审70%+过程表现30%', 0);

-- =====================================================================
-- 10. 教学计划上下文（每个方案一条）
-- =====================================================================
INSERT INTO t_csys_teaching_plan_context
(plan_id, source_course_id, quote_course_id, scheme_id, scheme_name, scheme_version,
 education_level, major_id, course_module, term, course_attr,
 hours, teach_hours, practice_hours, credits, sysflag)
VALUES
(6001, 7003, 70031, 7601, '计算机科学与技术2026级培养方案', '2026', '1', 9001, '3', 4, '1',
 56, 40, 16, 3.5, 0),
(6002, 8002, 80021, 7601, '计算机科学与技术2026级培养方案', '2026', '1', 9001, NULL, 2, '2',
 32, 0, 32, 2.0, 0),
(6003, 8003, 80031, 7601, '计算机科学与技术2026级培养方案', '2026', '1', 9001, '3', 3, '1',
 24, 0, 24, 1.5, 0),
(6004, 8004, 80041, 7601, '计算机科学与技术2026级培养方案', '2026', '1', 9001, '3', 6, '1',
 48, 0, 48, 3.0, 0);

-- =====================================================================
-- 11. type1 计划 6001（程序设计基础）子模块
-- =====================================================================
-- 教学团队
INSERT INTO t_csys_teaching_plan_teacher
(plan_id, teacher_id, teacher_name, professional_title, duty, lecture_content, sort)
VALUES
(6001, 't001', '张老师', '教授', '主讲', '程序设计基础全部章节', 1);

-- 课程概述
INSERT INTO t_csys_teaching_plan_section
(plan_id, section_code, section_title, content, sort)
VALUES
(6001, 'course_overview', '课程概述', '本课程讲授程序设计的基本方法与常用数据结构，为后续专业课奠定基础。', 1),
(6001, 'course_task',     '课程任务', '使学生掌握结构化编程、线性表与排序等基本算法，具备初步的工程实现能力。', 2);

-- 课程目标（非公共基础 -> 按方案 scheme_id 分组）
-- id 显式给定 60011/60012/60013，供 objective_ref / target_design / support_objective 引用
INSERT INTO t_csys_teaching_plan_objective
(id, plan_id, scheme_id, major_id, objective_type_code, objective_type_name, content, source_mode, sort)
VALUES
(60011, 6001, 7601, 9001, '1', '知识目标', '掌握结构化程序设计的基本方法与三种基本结构', 1, 1),
(60012, 6001, 7601, 9001, '2', '能力目标', '具备运用线性表、树等数据结构解决实际问题的能力', 1, 2),
(60013, 6001, 7601, 9001, '3', '素质目标', '培养规范编码习惯、调试能力与团队协作意识', 1, 3);

-- 目标-毕业要求绑定
INSERT INTO t_csys_teaching_plan_objective_ref
(plan_id, objective_id, scheme_course_graduation_id, quote_course_id, scheme_id,
 graduation_id, source_graduation_id, graduation_code, graduation_name, graduation_bind_source, sort)
VALUES
(6001, 60011, 1001, 70031, 7601, 90101, 90101, 'GR1', '掌握计算机科学与技术的基础理论与专业知识', 'scheme_course', 1),
(6001, 60012, 1002, 70031, 7601, 90102, 90102, 'GR2', '具备计算机工程实践与系统开发能力',           'scheme_course', 2),
(6001, 60013, 1005, 70031, 7601, 90103, 90103, 'GR3', '具有良好的职业道德、团队合作与终身学习意识', 'scheme_course', 3);

-- 教学内容（第五部分）
-- id 显式给定 65011/65012，供 support_content 引用
INSERT INTO t_csys_teaching_plan_content
(id, plan_id, content_type, title, content, purpose, time_arrange, sort)
VALUES
(65011, 6001, 1, '第一章 程序设计基础', '变量、数据类型、流程控制语句', NULL, '第1-4周', 1),
(65012, 6001, 1, '第二章 数据结构',     '线性表、排序算法',           NULL, '第5-8周', 2);

-- 目标达成设计（第六部分：知识/能力/素质 三表）
INSERT INTO t_csys_teaching_plan_target_design
(plan_id, scheme_id, design_type_code, objective_id, objective_text,
 knowledge_unit_id, knowledge_unit_name, knowledge_point_id, knowledge_point_name,
 content_text, teaching_link, teaching_method, learning_method, hours, teaching_design, sort)
VALUES
(6001, 7601, '1', 60011, '掌握结构化程序设计的基本方法与三种基本结构',
 7501, '基础编程概念', 75011, '变量与数据类型',
 '第一章', '课堂讲授', '讲授法', '练习法', 24, '讲授+上机练习', 1),
(6001, 7601, '2', 60012, '具备运用线性表、树等数据结构解决实际问题的能力',
 7502, '数据结构与算法', 75021, '线性表',
 '第二章', '课堂讲授', '案例教学', '项目实践', 16, '案例驱动+项目实践', 2),
(6001, 7601, '3', 60013, '培养规范编码习惯、调试能力与团队协作意识',
 NULL, NULL, NULL, NULL,
 '全过程', '实践环节', '项目教学', '小组协作', 4, '小组项目+代码评审', 3);

-- 实验/实践环节（第七部分）
-- id 显式给定 66011/66012，供 practice_item_detail 引用
INSERT INTO t_csys_teaching_plan_practice_item
(id, plan_id, item_type, name, hours, group_info, experiment_nature, study_nature, sort)
VALUES
(66011, 6001, 1, '基础编程实验', 8, '2人/组', '验证性', '必做', 1),
(66012, 6001, 2, '综合程序设计', 8, '3人/组', '综合性', '必做', 2);

INSERT INTO t_csys_teaching_plan_practice_item_detail (item_id, detail_type, content, sort)
VALUES
(66011, 'purpose_task',        '掌握程序调试与运行验证方法', 1),
(66011, 'content_requirement', '完成三个基础编程题并提交',  2),
(66012, 'purpose_task',        '完成一个综合小系统设计与实现', 1),
(66012, 'result_requirement',  '提交项目源码与说明文档',    2);

-- 考核评价（第八部分）
-- id 显式给定 68011/68012，供 objective_assessment 引用
INSERT INTO t_csys_teaching_plan_assessment
(id, plan_id, assessment_category, assessment_item, method, weight, outcome_type, sort)
VALUES
(68011, 6001, 1, '平时作业',   '随堂作业与上机检查', 30, 1, 1),
(68012, 6001, 2, '期末考试',   '闭卷笔试',           70, 2, 2);

-- 目标达成考核设计（第八部分（二））
INSERT INTO t_csys_teaching_plan_objective_assessment
(plan_id, scheme_id, objective_id, assessment_id, assessment_item, weight, assessment_item_content)
VALUES
(6001, 7601, 60011, 68011, '平时作业', 0.3000, '检验结构化编程掌握情况'),
(6001, 7601, 60012, 68012, '期末考试', 0.3500, '检验数据结构应用能力'),
(6001, 7601, 60013, 68012, '期末考试', 0.1500, '检验规范编码与协作素养');

-- 教材与条件（第九部分）
INSERT INTO t_csys_teaching_plan_textbook
(plan_id, material_nature, name, first_author, edition, publisher, publish_time, isbn, sort)
VALUES
(6001, '教材', 'C语言程序设计（第3版）', '谭浩强', '第3版', '清华大学出版社', '2010-01', '9787302254373', 1),
(6001, '参考书', '数据结构（C语言版）',   '严蔚敏', '第2版', '清华大学出版社', '2011-02', '9787302147514', 2);

INSERT INTO t_csys_teaching_plan_condition (plan_id, condition_type, requirement, sort)
VALUES
(6001, '3', '配备多媒体机房，安装C语言开发环境', 1),
(6001, '2', '课程在线教学平台及编程实训资源',   2);

-- =====================================================================
-- 12. 实践训练课目(plan_type=3)计划 6002（军事基础训练）子模块
--     重点：第四部分「模块」列 title 存 sys_plan_training_module 字典 value
-- =====================================================================
-- 训练目的（通识通用 location='1' -> scheme_id 恒为 NULL 单组）
-- id 显式给定 62111/62112，供 training_purpose_ref / content_purpose / support_objective 引用
INSERT INTO t_csys_teaching_plan_training_purpose
(id, plan_id, scheme_id, purpose, sort)
VALUES
(62111, 6002, NULL, '掌握单个军人队列动作、班队列组织等基本军事素养', 1),
(62112, 6002, NULL, '培养令行禁止、雷厉风行的战斗作风', 2);

INSERT INTO t_csys_teaching_plan_training_purpose_ref
(plan_id, purpose_id, scheme_course_graduation_id, quote_course_id, scheme_id,
 graduation_id, source_graduation_id, graduation_code, graduation_name, graduation_bind_source, sort)
VALUES
(6002, 62111, 1003, 80021, 7601, 90101, 90101, 'GR1', '掌握计算机科学与技术的基础理论与专业知识', 'scheme_course', 1),
(6002, 62112, 1004, 80021, 7601, 90103, 90103, 'GR3', '具有良好的职业道德、团队合作与终身学习意识', 'scheme_course', 2);

-- 第四部分 训练内容与时间安排：title=字典value(1/2/3)
-- id 显式给定 62211/62212/62213，供 content_purpose / support_content 引用
INSERT INTO t_csys_teaching_plan_content
(id, plan_id, content_type, title, content, purpose, time_arrange, sort)
VALUES
(62211, 6002, 1, '1', '单个军人队列动作与班队列组织', NULL, '第1-2周', 1),
(62212, 6002, 1, '2', '指挥口令运用与队列指挥',       NULL, '第3-4周', 2),
(62213, 6002, 1, '3', '新质新域装备认知与操作基础',   NULL, '第5-6周', 3);

-- 训练内容 -> 训练目的（第四部分「目的」列）
INSERT INTO t_csys_teaching_plan_content_purpose (plan_id, content_id, purpose_id, sort)
VALUES
(6002, 62211, 62111, 1),
(6002, 62212, 62111, 2),
(6002, 62213, 62112, 3);

-- 组织实施（第五部分）
INSERT INTO t_csys_teaching_plan_section
(plan_id, section_code, section_title, content, sort)
VALUES
(6002, 'organize_way', '组织方式', '按班建制组织实施，小班化教学；采用分队与班组相结合的形式进行连贯作业。', 1);

INSERT INTO t_csys_teaching_plan_process_step
(plan_id, stage_name, step_name, requirement, sort)
VALUES
(6002, '1', '战备等级转进', '完成由平时状态向一级战备状态转进，组织筹划工作，开展战前动员。', 1),
(6002, '1', '远程跨域机动', '完成远程投送筹划，采用摩托化机动方式实施跨域投送。', 2),
(6002, '1', '组织临战训练', '到达部署地域后，组织开展战场自救与互救、电台通信、简易通信、夜视器材操作等战术基础内容训练。', 3),
(6002, '2', '作战筹划', '根据作战想定，召开“三会”（支委会、作战会、协同会），完成方案拟制、作战推演等内容。', 4),
(6002, '2', '隐蔽渗透', '完成隐蔽机动，期间开展防敌空中侦察、克服自然障碍、通过染毒地段、通过隘口、传单处置等内容训练。', 5),
(6002, '2', '引导打击', '完成战斗勤务（观察报知）、电台通信、简易通信等内容考核，完成战场自救与互救、战场宣传鼓动等内容训练。', 6),
(6002, '2', '夜间侦察', '完成夜间基础（夜视器材操作）考核，完成战场管理训练。', 7),
(6002, '2', '进攻战斗', '完成班组战斗行动（战斗准备、接敌运动、冲击行动、攻击目标）考核，完成无人、反无人作战力量运用、火线入党等内容。', 8),
(6002, '2', '追歼残敌', '完成战斗勤务（战斗准备、搜索前进、建立阵地）考核，完成战斗体能等训练内容。', 9),
(6002, '3', '复盘总结', '学员队按照战斗班对演练全流程进行复盘总结，学员队、战斗班分别形成总结报告。', 10),
(6002, '3', '撤收返回', '撤收返回。根据计划安排，完成宿营物资撤收，组织摩托化机动反营。', 11);

-- 考核评价（第六部分）+ 训练条件（第七部分）
INSERT INTO t_csys_teaching_plan_assessment
(plan_id, assessment_category, assessment_item, method, weight, outcome_type, sort)
VALUES
(6002, 1, '训练考核', '队列会操评分', 60, 1, 1),
(6002, 2, '平时表现', '出勤与作风', 40, 2, 2);

INSERT INTO t_csys_teaching_plan_condition (plan_id, condition_type, requirement, sort)
VALUES
(6002, '6', '单兵队列训练器材', 1),
(6002, '7', '模拟训练条件',    2);

-- =====================================================================
-- 13. 实验课程(plan_type=2)计划 6003（大学物理实验）子模块
-- =====================================================================
INSERT INTO t_csys_teaching_plan_teacher
(plan_id, teacher_id, teacher_name, professional_title, duty, sort)
VALUES
(6003, 't003', '王老师', '副教授', '实验主讲', 1);

-- 任务背景（第三部分）
INSERT INTO t_csys_teaching_plan_task_background
(plan_id, scheme_id, major_id, background_desc, technical_goal, ability_goal, sort)
VALUES
(6003, 7601, 9001,
 '围绕力学、电磁学核心原理开展实验验证，强化理论与实验结合',
 '掌握常用实验仪器的操作与测量方法',
 '培养实验数据分析与科学表达能力',
 1);

-- 课程目标 + 绑定
-- id 显式给定 61511/61512，供 objective_ref 引用
INSERT INTO t_csys_teaching_plan_objective
(id, plan_id, scheme_id, major_id, objective_type_code, objective_type_name, content, source_mode, sort)
VALUES
(61511, 6003, 7601, 9001, '1', '知识目标', '掌握力学、电磁学核心实验原理与仪器原理', 1, 1),
(61512, 6003, 7601, 9001, '2', '能力目标', '具备独立完成实验、处理数据并撰写报告的能力', 1, 2);

INSERT INTO t_csys_teaching_plan_objective_ref
(plan_id, objective_id, scheme_course_graduation_id, quote_course_id, scheme_id,
 graduation_id, source_graduation_id, graduation_code, graduation_name, graduation_bind_source, sort)
VALUES
(6003, 61511, 1001, 80031, 7601, 90101, 90101, 'GR1', '掌握计算机科学与技术的基础理论与专业知识', 'scheme_course', 1),
(6003, 61512, 1002, 80031, 7601, 90102, 90102, 'GR2', '具备计算机工程实践与系统开发能力',           'scheme_course', 2);

-- 实验项目（第四部分）+ 明细
-- id 显式给定 66511/66512，供 practice_item_detail 引用
INSERT INTO t_csys_teaching_plan_practice_item
(id, plan_id, item_type, name, hours, group_info, experiment_nature, study_nature, sort)
VALUES
(66511, 6003, 1, '牛顿第二定律验证实验', 8, '2人/组', '验证性', '必做', 1),
(66512, 6003, 2, '电磁感应综合实验',     8, '2人/组', '综合性', '必做', 2);

INSERT INTO t_csys_teaching_plan_practice_item_detail (item_id, detail_type, content, sort)
VALUES
(66511, 'purpose_task',        '验证牛顿第二定律',           1),
(66511, 'content_requirement', '记录并处理实验数据',         2),
(66512, 'purpose_task',        '测定感应电动势与互感系数',   1),
(66512, 'result_requirement',  '提交实验报告',               2);

-- 考核评价（第六部分）
INSERT INTO t_csys_teaching_plan_assessment
(plan_id, assessment_category, assessment_item, method, weight, outcome_type, sort)
VALUES
(6003, 1, '实验报告', '报告批阅', 40, 1, 1),
(6003, 2, '操作考核', '现场操作', 60, 2, 2);

-- 实验教材（第七部分）
INSERT INTO t_csys_teaching_plan_textbook
(plan_id, material_nature, name, first_author, publisher, sort)
VALUES
(6003, '教材', '大学物理实验指导书', '物理教研室', '校内讲义', 1);

-- =====================================================================
-- 14. type4 计划 6004（综合课程设计实践）子模块
--     重点：第二节支撑绑定走 t_csys_teaching_plan_support_objective/content
-- =====================================================================
INSERT INTO t_csys_teaching_plan_teacher
(plan_id, teacher_id, teacher_name, professional_title, duty, sort)
VALUES
(6004, 't004', '赵老师', '教授', '项目指导', 1);

-- 任务背景与组织（第二/三部分）
INSERT INTO t_csys_teaching_plan_section
(plan_id, section_code, section_title, content, sort)
VALUES
(6004, 'complex_problem', '拟解决的复杂问题', '设计并实现一个具有实际业务场景的综合管理系统', 1),
(6004, 'main_task',       '主要任务',         '完成需求分析、系统设计、编码实现与测试',       2),
(6004, 'team_scale',      '团队规模',         '3-4人/组',                                      3),
(6004, 'division',        '分工方式',         '按模块分工，组内交叉评审',                       4);

-- 项目实施步骤（第三部分）
INSERT INTO t_csys_teaching_plan_process_step
(plan_id, stage_name, step_name, requirement, sort)
VALUES
(6004, '需求阶段', '需求分析与总体设计', '输出需求规格说明书与总体设计文档', 1),
(6004, '实现阶段', '编码实现与单元测试', '代码走查与单元测试',              2),
(6004, '验收阶段', '系统集成与验收',     '提交项目报告并答辩',              3);

-- 第二节 支撑绑定（预置，供 createWord 直接出文档；测试亦会走 save 接口写/读）
INSERT INTO t_csys_teaching_plan_support_objective
(plan_id, ref_type, ref_plan_id, ref_course_id, objective_id, purpose_id, item_name, item_type_name, major_id, sort)
VALUES
(6004, 1, 6001, 7003, 60011, NULL, '掌握结构化程序设计的基本方法与三种基本结构', '知识目标', 9001, 1),
(6004, 2, 6002, 8002, NULL, 62111, '掌握单个军人队列动作、班队列组织等基本军事素养', NULL, 9001, 2);

-- type2 内容快照存模块字典名称（与 saveSupportContents 的 translateTrainingModuleName 输出一致）
INSERT INTO t_csys_teaching_plan_support_content
(plan_id, ref_type, ref_plan_id, ref_course_id, content_id, item_title, sort)
VALUES
(6004, 1, 6001, 7003, 65011, '第一章 程序设计基础', 1),
(6004, 2, 6002, 8002, 62211, '战斗体技能提升模块', 2);

-- 考核评价（第四部分 成果与评价）
INSERT INTO t_csys_teaching_plan_assessment
(plan_id, assessment_category, assessment_item, method, weight, outcome_type, sort)
VALUES
(6004, 1, '项目成果评审', '成果答辩与文档评审', 70, 1, 1),
(6004, 2, '过程表现',     '进度、协作与出勤',   30, 2, 2);

-- 实践条件（第五部分）
INSERT INTO t_csys_teaching_plan_condition (plan_id, condition_type, requirement, sort)
VALUES
(6004, '8', '软件开发实验室与项目服务器环境', 1),
(6004, '2', '在线项目管理与代码托管平台',     2);
