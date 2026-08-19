-- =====================================================================
-- 教学计划四类接口测试数据（本地库 doinner-curriculum-test-3.2）
-- 数据链：共性毕业要求 -> 三个课程库 -> 知识单元知识点 -> 培养方案
--        -> 调用课程与毕业要求 -> 课程绑定毕业要求
-- 覆盖 plan_type(前后端统一)：1课程(7003) 2实验课程(8003) 3实践训练课目(8002) 4实践项目(8004)
--   * 课程库 type 与 plan_type 编号 2/3 对调：8002 实践训练课目(type=2 课程) plan_type=3，
--     8003 实验课程(type=3 课程) plan_type=2；课程 type=1/3 同为「课程」类别，教学计划区分。
-- 2026-08-12 数据加厚（供生成 Word 对照桌面模板）：
--   * 知识/能力/素质目标各多条；知识单元/知识点扩到 5 单元 13 知识点
--   * 目标→毕业要求绑定完整（一条目标可绑多个毕业要求）
--   * 考核方式多种（期末/期中/作业/随堂/大作业/课堂表现，闭卷/开卷/考查）
--   * 评定机制/评价标准按字典编码存：sys_assessment_mechanism(1百分制/2五级制/3两级制/4四级制)
--     sys_evaluation_standard(1优秀 2良好 3中等 4及格 5不及格 6合格 7不合格)
--     绑定规则：百分制无标准；两级制=6,7(合格/不合格)；四级制=1,2,3,4(优/良/中/及格)；
--     五级制=1,2,3,4,5(优/良/中/及格/不及格)。standard 列存逗号分隔的编码串。
--   * 实验课程(6003)任务背景绑定毕业要求 → 支撑毕业要求列不再为空
--   * 实践训练课目(6002)补训练任务/总体设计 section → 第三节不再空白
-- 全部用显式 id，可重复执行前先 DELETE 本文件涉及主键。
-- =====================================================================

-- =====================================================================
-- 0. 清理（幂等：删除本文件涉及的显式主键，便于反复执行）
-- =====================================================================
DELETE FROM t_csys_teaching_plan_support_content  WHERE plan_id = 6004;
DELETE FROM t_csys_teaching_plan_support_objective WHERE plan_id = 6004;
DELETE FROM t_csys_teaching_plan_objective_assessment WHERE plan_id IN (6001,6002,6003,6004);
DELETE FROM t_csys_teaching_plan_target_design     WHERE plan_id IN (6001,6002,6003,6004);
DELETE FROM t_csys_teaching_plan_task_background_ref WHERE plan_id IN (6001,6002,6003,6004);
DELETE FROM t_csys_teaching_plan_practice_item_detail WHERE item_id IN (66011,66012,66013,66511,66512,66513,66514);
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
DELETE FROM t_csys_course_knowledge_point          WHERE unit_id IN (7501,7502,7503,7504,7505);
DELETE FROM t_csys_course_knowledge_unit           WHERE id IN (7501,7502,7503,7504,7505);
DELETE FROM t_csys_training_scheme_ref_course      WHERE scheme_id = 7601;
DELETE FROM t_csys_training_scheme_course_schedule WHERE scheme_id = 7601;
DELETE FROM t_csys_course WHERE id IN (7001,7002,7003,70031,8002,80021,8003,80031,8004,80041);
DELETE FROM t_csys_scheme_ref_major                WHERE scheme_id = 7601;
DELETE FROM t_csys_training_scheme                 WHERE id = 7601;
DELETE FROM t_csys_std_graduation                  WHERE id IN (90101,90102,90103,90104,90105,90106,90111,90112,90113,90121,90122,90123,90124,90125,90126);
DELETE FROM t_csys_std_major                       WHERE id = 9001;

-- =====================================================================
-- 1. 专业 t_csys_std_major
-- =====================================================================
INSERT INTO t_csys_std_major
(id, name, code, parent_id, level, college_id, category_id, leaf, applicable_object, sysflag)
VALUES
(9001, '计算机科学与技术', 'CS001', -1, 1, 1, 1, 1, '1', 0);

-- =====================================================================
-- 2. 共性毕业要求 t_csys_std_graduation（固定三级，绑定只绑最后一级）
--    一级(type=1 源/模板) -> 二级指标点(type=1) -> 三级(type=2 毕业要求/调用, source_id 指向源)
--    graduation_type=1知识/2能力/3素质；education_level 存 sys_education_level 字典值
-- =====================================================================
INSERT INTO t_csys_std_graduation
(id, name, code, parent_id, leaf, level, type, graduation_type, major_id, category_id, education_level, source_id, sysflag)
VALUES
-- 一级：毕业要求大类（源/模板）
(90111, '毕业要求一：工程知识与应用', 'GR-A', -1, 0, 1, 1, '1', 9001, 1, '1', NULL, 0),
(90112, '毕业要求二：实践与创新',     'GR-B', -1, 0, 1, 1, '2', 9001, 1, '1', NULL, 0),
(90113, '毕业要求三：职业素养与学习', 'GR-C', -1, 0, 1, 1, '3', 9001, 1, '1', NULL, 0),
-- 二级：指标点（源/模板）
(90121, '指标点1.1 掌握计算机学科基础理论与知识体系', 'GR-A1', 90111, 0, 2, 1, '1', 9001, 1, '1', NULL, 0),
(90122, '指标点1.2 运用专业知识解决复杂工程问题',      'GR-A2', 90111, 0, 2, 1, '1', 9001, 1, '1', NULL, 0),
(90123, '指标点2.1 具备工程实践与系统开发能力',        'GR-B1', 90112, 0, 2, 1, '2', 9001, 1, '1', NULL, 0),
(90124, '指标点2.2 具备系统分析、设计与创新能力',      'GR-B2', 90112, 0, 2, 1, '2', 9001, 1, '1', NULL, 0),
(90125, '指标点3.1 具备职业道德与团队合作意识',        'GR-C1', 90113, 0, 2, 1, '3', 9001, 1, '1', NULL, 0),
(90126, '指标点3.2 具有终身学习与自我提升意识',        'GR-C2', 90113, 0, 2, 1, '3', 9001, 1, '1', NULL, 0),
-- 三级：具体毕业要求（调用，绑定只绑这一级）
(90101, '掌握计算机科学与技术的基础理论与专业知识', 'GR1', 90121, 1, 3, 2, '1', 9001, 1, '1', 90121, 0),
(90102, '具备计算机工程实践与系统开发能力',          'GR2', 90123, 1, 3, 2, '2', 9001, 1, '1', 90123, 0),
(90103, '具有良好的职业道德、团队合作与终身学习意识','GR3', 90125, 1, 3, 2, '3', 9001, 1, '1', 90125, 0),
(90104, '能够运用数学与专业原理对复杂工程问题进行建模与分析', 'GR4', 90122, 1, 3, 2, '1', 9001, 1, '1', 90122, 0),
(90105, '具备开展系统设计、编码实现与测试验证的工程实践能力', 'GR5', 90124, 1, 3, 2, '2', 9001, 1, '1', 90124, 0),
(90106, '具备沟通表达、协作共事与组织管理能力',      'GR6', 90126, 1, 3, 2, '3', 9001, 1, '1', 90126, 0);

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
-- 三个课程库（type1）；code 按代码规则：版本(后2位)-层次-学院-流水 = 26-JG-02-XXXX
(7001, '大学物理',        '26-JG-02-0001',  1, 'College Physics',        NULL, NULL,
 64, 56, 8,  4.0, '1', NULL, '1', '69a7f32e2dc370362ef3ee6e', 9001, NULL, NULL, '2026', '1', '1', 16, 0, 1),
(7002, '高等数学',        '26-JG-02-0002',  1, 'Advanced Mathematics',   NULL, NULL,
 80, 80, 0,  5.0, '1', NULL, '1', '69a7f3342dc370362ef3ee6f', 9001, NULL, NULL, '2026', '1', '1', 16, 0, 1),
(7003, '程序设计基础',    '26-JG-02-0003', 1, 'Programming Fundamentals',NULL, NULL,
 56, 40, 16, 3.5, '1', NULL, '1', '69a7f33c2dc370362ef3ee70', 9001, NULL, NULL, '2026', '1', '1', 16, 0, 1),
-- 调用课程副本（source_id 指向源课，template_type=2 表示被调用）
(70031,'程序设计基础',    '26-JG-02-0004',1,'Programming Fundamentals',NULL, NULL,
 56, 40, 16, 3.5, '1', NULL, '1', '69a7f33c2dc370362ef3ee70', 9001, 7003, 2,    '2026', '1', '1', 16, 0, 1),
-- 四类教学计划课程
(8002, '军事基础训练',    '26-JG-02-0005', 2, 'Basic Military Training', NULL, NULL,
 32, 0,  32, 2.0, '2', '1', '1', NULL,                          9001, NULL, NULL, '2026', '1', '1', 2,  0, 1),
(80021,'军事基础训练',    '26-JG-02-0006',2,'Basic Military Training', NULL, NULL,
 32, 0,  32, 2.0, '2', '1', '1', NULL,                          9001, 8002, 2,    '2026', '1', '1', 2,  0, 1),
(8003, '大学物理实验',    '26-JG-02-0007', 3, 'College Physics Experiment', NULL, NULL,
 24, 0,  24, 1.5, '1', NULL, '3', '69a7f33c2dc370362ef3ee70', 9001, NULL, NULL, '2026', '1', '1', 2,  0, 1),
(80031,'大学物理实验',    '26-JG-02-0008',3,'College Physics Experiment', NULL, NULL,
 24, 0,  24, 1.5, '1', NULL, '3', '69a7f33c2dc370362ef3ee70', 9001, 8003, 2,    '2026', '1', '1', 2,  0, 1),
(8004, '综合课程设计实践','26-JG-02-0009', 4, 'Comprehensive Course Design Project',
 '7003', '8002',
 48, 0,  48, 3.0, '1', NULL, '3', '69a7f33c2dc370362ef3ee70', 9001, NULL, NULL, '2026', '1', '1', 6,  0, 1),
(80041,'综合课程设计实践','26-JG-02-0010',4,'Comprehensive Course Design Project',
 '7003', '8002',
 48, 0,  48, 3.0, '1', NULL, '3', '69a7f33c2dc370362ef3ee70', 9001, 8004, 2,    '2026', '1', '1', 6,  0, 1);

-- =====================================================================
-- 5. 知识单元/知识点（7003 程序设计基础，5 单元 13 知识点）
-- =====================================================================
INSERT INTO t_csys_course_knowledge_unit (id, name, sort, create_time)
VALUES (7501, '基础编程概念',     1, NOW()),
       (7502, '数据结构与算法',   2, NOW()),
       (7503, '面向对象程序设计', 3, NOW()),
       (7504, '算法设计与分析',   4, NOW()),
       (7505, '文件与异常处理',   5, NOW());

INSERT INTO t_csys_course_knowledge_point (id, name, sort, unit_id, create_time)
VALUES
(75011, '变量与数据类型',  1, 7501, NOW()),
(75012, '流程控制',        2, 7501, NOW()),
(75013, '函数与递归',      3, 7501, NOW()),
(75021, '线性表',          1, 7502, NOW()),
(75022, '排序算法',        2, 7502, NOW()),
(75023, '树与二叉树',      3, 7502, NOW()),
(75031, '类与对象',        1, 7503, NOW()),
(75032, '继承与多态',      2, 7503, NOW()),
(75033, '接口与抽象类',    3, 7503, NOW()),
(75041, '分治与递归',      1, 7504, NOW()),
(75042, '动态规划',        2, 7504, NOW()),
(75051, '文件读写',        1, 7505, NOW()),
(75052, '异常处理',        2, 7505, NOW());

INSERT INTO t_csys_course_ref_knowledge_unit (course_id, course_unit_id)
VALUES (7003, 7501), (7003, 7502), (7003, 7503), (7003, 7504), (7003, 7505);

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
(7003,  90102, 2, 9001),
(7003,  90103, 3, 9001),
(70031, 90101, 1, 9001),
(70031, 90102, 2, 9001),
(70031, 90103, 3, 9001),
(8002,  90101, 1, 9001),
(8002,  90102, 2, 9001),
(8002,  90103, 3, 9001),
(80021, 90101, 1, 9001),
(80021, 90102, 2, 9001),
(80021, 90103, 3, 9001),
(8003,  90101, 1, 9001),
(8003,  90102, 2, 9001),
(8003,  90103, 3, 9001),
(80031, 90101, 1, 9001),
(80031, 90102, 2, 9001),
(80031, 90103, 3, 9001),
(8004,  90101, 1, 9001),
(8004,  90102, 2, 9001),
(8004,  90103, 3, 9001),
(80041, 90101, 1, 9001),
(80041, 90102, 2, 9001),
(80041, 90103, 3, 9001);

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
(6001, 6001, 7003, 1, '1', 1, '2026', 0, '程序设计基础', '26-JG-02-0003', 56, 40, 16, 3.5,
 '平时30%+期末70%', 0),
(6002, 6002, 8002, 3, '1', 1, '2026', 0, '军事基础训练', '26-JG-02-0005', 32, 0, 32, 2.0,
 '训练考核60%+平时表现40%', 0),
(6003, 6003, 8003, 2, '1', 1, '2026', 0, '大学物理实验', '26-JG-02-0007', 24, 0, 24, 1.5,
 '期末考核30%+实验报告40%+实验操作30%', 0),
(6004, 6004, 8004, 4, '1', 1, '2026', 0, '综合课程设计实践', '26-JG-02-0009', 48, 0, 48, 3.0,
 '个人成果40%+团队成果30%+过程表现30%', 0);

-- =====================================================================
-- 10. 教学计划上下文（每个方案一条）
-- =====================================================================
INSERT INTO t_csys_teaching_plan_context
(plan_id, source_course_id, quote_course_id, scheme_id, scheme_name, scheme_version,
 education_level, major_id, course_module, term, course_attr,
 hours, teach_hours, practice_hours, credits, sysflag)
VALUES
(6001, 7003, 70031, 7601, '计算机科学与技术2026级培养方案', '2026', '1', 9001, '69a7f33c2dc370362ef3ee70', 4, '1',
 56, 40, 16, 3.5, 0),
(6002, 8002, 80021, 7601, '计算机科学与技术2026级培养方案', '2026', '1', 9001, NULL, 2, '2',
 32, 0, 32, 2.0, 0),
(6003, 8003, 80031, 7601, '计算机科学与技术2026级培养方案', '2026', '1', 9001, '69a7f33c2dc370362ef3ee70', 3, '1',
 24, 0, 24, 1.5, 0),
(6004, 8004, 80041, 7601, '计算机科学与技术2026级培养方案', '2026', '1', 9001, '69a7f33c2dc370362ef3ee70', 6, '1',
 48, 0, 48, 3.0, 0);

-- =====================================================================
-- 11. type1 计划 6001（程序设计基础）子模块
-- =====================================================================
-- 教学团队（3 人）
INSERT INTO t_csys_teaching_plan_teacher
(plan_id, teacher_id, teacher_name, professional_title, duty, lecture_content, sort)
VALUES
(6001, 't001', '张老师', '教授', '主讲', '程序设计基础全部章节', 1),
(6001, 't002', '李老师', '副教授', '主讲', '数据结构与算法章节', 2),
(6001, 't005', '王老师', '讲师', '辅导', '上机辅导与答疑', 3);

-- 课程概述
INSERT INTO t_csys_teaching_plan_section
(plan_id, section_code, section_title, content, sort)
VALUES
(6001, 'course_overview', '课程概述', '本课程讲授程序设计的基本方法与常用数据结构，为后续专业课奠定基础。', 1),
(6001, 'course_task',     '课程任务', '使学生掌握结构化编程、线性表与排序等基本算法，具备初步的工程实现能力。', 2);

-- 课程目标（非公共基础 -> 按方案 scheme_id 分组）
-- id 显式给定 60011~60018，供 objective_ref / target_design / support_objective 引用
INSERT INTO t_csys_teaching_plan_objective
(id, plan_id, scheme_id, major_id, objective_type_code, objective_type_name, content, source_mode, sort)
VALUES
(60011, 6001, 7601, 9001, '1', '知识目标', '掌握结构化程序设计的基本方法与三种基本结构', 1, 1),
(60012, 6001, 7601, 9001, '2', '能力目标', '具备运用线性表、树等数据结构解决实际问题的能力', 1, 2),
(60013, 6001, 7601, 9001, '3', '素质目标', '培养规范编码习惯、调试能力与团队协作意识', 1, 3),
(60014, 6001, 7601, 9001, '1', '知识目标', '掌握面向对象程序设计的基本思想与类、继承、多态等核心机制', 1, 4),
(60015, 6001, 7601, 9001, '1', '知识目标', '掌握常用数据结构的存储表示与基本操作的实现方法', 1, 5),
(60016, 6001, 7601, 9001, '2', '能力目标', '具备使用集成开发环境进行程序调试、代码走查与性能分析的能力', 1, 6),
(60017, 6001, 7601, 9001, '2', '能力目标', '能够运用分治、递归、动态规划等算法思想解决中等规模工程问题', 1, 7),
(60018, 6001, 7601, 9001, '3', '素质目标', '树立精益求精、严谨求实的工程实践与科研作风', 1, 8);

-- 目标-毕业要求绑定（一条目标可绑多个毕业要求）
INSERT INTO t_csys_teaching_plan_objective_ref
(plan_id, objective_id, scheme_course_graduation_id, quote_course_id, scheme_id,
 graduation_id, source_graduation_id, graduation_code, graduation_name, graduation_bind_source, sort)
VALUES
(6001, 60011, 1001, 70031, 7601, 90101, 90101, 'GR1', '掌握计算机科学与技术的基础理论与专业知识', 'scheme_course', 1),
(6001, 60012, 1002, 70031, 7601, 90102, 90102, 'GR2', '具备计算机工程实践与系统开发能力',           'scheme_course', 2),
(6001, 60013, 1005, 70031, 7601, 90103, 90103, 'GR3', '具有良好的职业道德、团队合作与终身学习意识', 'scheme_course', 3),
(6001, 60011, 1002, 70031, 7601, 90102, 90102, 'GR2', '具备计算机工程实践与系统开发能力',           'scheme_course', 4),
(6001, 60012, 1001, 70031, 7601, 90101, 90101, 'GR1', '掌握计算机科学与技术的基础理论与专业知识', 'scheme_course', 5),
(6001, 60013, 1001, 70031, 7601, 90101, 90101, 'GR1', '掌握计算机科学与技术的基础理论与专业知识', 'scheme_course', 6),
(6001, 60014, 1001, 70031, 7601, 90101, 90101, 'GR1', '掌握计算机科学与技术的基础理论与专业知识', 'scheme_course', 7),
(6001, 60014, 1002, 70031, 7601, 90102, 90102, 'GR2', '具备计算机工程实践与系统开发能力',           'scheme_course', 8),
(6001, 60015, 1001, 70031, 7601, 90101, 90101, 'GR1', '掌握计算机科学与技术的基础理论与专业知识', 'scheme_course', 9),
(6001, 60016, 1002, 70031, 7601, 90102, 90102, 'GR2', '具备计算机工程实践与系统开发能力',           'scheme_course', 10),
(6001, 60016, 1005, 70031, 7601, 90103, 90103, 'GR3', '具有良好的职业道德、团队合作与终身学习意识', 'scheme_course', 11),
(6001, 60017, 1001, 70031, 7601, 90101, 90101, 'GR1', '掌握计算机科学与技术的基础理论与专业知识', 'scheme_course', 12),
(6001, 60017, 1002, 70031, 7601, 90102, 90102, 'GR2', '具备计算机工程实践与系统开发能力',           'scheme_course', 13),
(6001, 60018, 1005, 70031, 7601, 90103, 90103, 'GR3', '具有良好的职业道德、团队合作与终身学习意识', 'scheme_course', 14),
(6001, 60018, 1001, 70031, 7601, 90101, 90101, 'GR1', '掌握计算机科学与技术的基础理论与专业知识', 'scheme_course', 15);

-- 教学内容（第五部分，4 章）；hours 写入「学时安排」列
-- id 显式给定 65011~65014，供 support_content 引用
INSERT INTO t_csys_teaching_plan_content
(id, plan_id, content_type, title, content, purpose, hours, time_arrange, sort)
VALUES
(65011, 6001, 1, '第一章 程序设计基础',     '变量、数据类型、流程控制语句',       NULL, 16, '第1-4周',  1),
(65012, 6001, 1, '第二章 数据结构',         '线性表、树、排序算法',               NULL, 16, '第5-8周',  2),
(65013, 6001, 1, '第三章 面向对象程序设计', '类与对象、继承与多态、接口设计',     NULL, 14, '第9-12周', 3),
(65014, 6001, 1, '第四章 算法设计与分析',   '分治、递归、动态规划与复杂度分析',   NULL, 10, '第13-16周',4);

-- 目标达成设计（第六部分：知识/能力/素质 三表，design_type_code 1知识/2能力/3素质）
-- observation_point 写「观测点」列：能力/素质行填值，知识行留空
INSERT INTO t_csys_teaching_plan_target_design
(plan_id, scheme_id, design_type_code, objective_id, objective_text,
 knowledge_unit_id, knowledge_unit_name, knowledge_point_id, knowledge_point_name,
 observation_point, content_text, teaching_link, teaching_method, learning_method, hours, teaching_design, sort)
VALUES
(6001, 7601, '1', 60011, '掌握结构化程序设计的基本方法与三种基本结构',
 7501, '基础编程概念', 75011, '变量与数据类型',
 NULL, '第一章', '课堂讲授', '讲授法', '练习法', 24, '讲授+上机练习', 1),
(6001, 7601, '2', 60012, '具备运用线性表、树等数据结构解决实际问题的能力',
 7502, '数据结构与算法', 75021, '线性表',
 '能根据问题规模选择合适的数据结构与算法', '第二章', '课堂讲授', '案例教学', '项目实践', 16, '案例驱动+项目实践', 2),
(6001, 7601, '3', 60013, '培养规范编码习惯、调试能力与团队协作意识',
 NULL, NULL, NULL, NULL,
 '团队分工明确、代码规范、任务按期提交', '全过程', '实践环节', '项目教学', '小组协作', 4, '小组项目+代码评审', 3),
(6001, 7601, '1', 60014, '掌握面向对象程序设计的基本思想与类、继承、多态等核心机制',
 7503, '面向对象程序设计', 75031, '类与对象',
 NULL, '第三章', '课堂讲授', '案例教学', '课堂练习', 12, '案例讲解+上机验证', 4),
(6001, 7601, '1', 60014, '掌握面向对象程序设计的基本思想与类、继承、多态等核心机制',
 7503, '面向对象程序设计', 75032, '继承与多态',
 NULL, '第三章', '课堂讲授', '研讨式', '分组练习', 8, '课堂研讨+小测验', 5),
(6001, 7601, '1', 60015, '掌握常用数据结构的存储表示与基本操作的实现方法',
 7502, '数据结构与算法', 75023, '树与二叉树',
 NULL, '第二章', '课堂讲授', '讲授法', '练习法', 8, '图示教学+习题训练', 6),
(6001, 7601, '2', 60016, '具备使用集成开发环境进行程序调试、代码走查与性能分析的能力',
 7501, '基础编程概念', 75013, '函数与递归',
 '能独立完成程序的编译、运行与错误定位', '第一章', '上机实践', '演示法', '自主练习', 8, '调试实训+代码走查', 7),
(6001, 7601, '2', 60017, '能够运用分治、递归、动态规划等算法思想解决中等规模工程问题',
 7504, '算法设计与分析', 75042, '动态规划',
 '能用算法思想对问题进行建模并实现求解', '第四章', '案例教学', '案例教学', '项目实践', 12, '算法实例+上机实现', 8),
(6001, 7601, '3', 60018, '树立精益求精、严谨求实的工程实践与科研作风',
 NULL, NULL, NULL, NULL,
 '作业提交及时、评审态度严谨、成果完整', '全过程', '实践环节', '项目教学', '小组协作', 4, '团队项目+代码评审', 9);

-- 实验/实践环节（第七部分，3 项）
-- id 显式给定 66011~66013，供 practice_item_detail 引用
INSERT INTO t_csys_teaching_plan_practice_item
(id, plan_id, item_type, name, hours, group_info, experiment_nature, study_nature, sort)
VALUES
(66011, 6001, 1, '基础编程实验',   8, '2人/组', '验证性', '必做', 1),
(66012, 6001, 2, '综合程序设计',   8, '3人/组', '综合性', '必做', 2),
(66013, 6001, 2, '综合课程设计项目',16, '4人/组', '设计性', '必做', 3);

INSERT INTO t_csys_teaching_plan_practice_item_detail (item_id, detail_type, content, sort)
VALUES
(66011, 'purpose_task',        '掌握程序调试与运行验证方法', 1),
(66011, 'content_requirement', '完成三个基础编程题并提交',  2),
(66012, 'purpose_task',        '完成一个综合小系统设计与实现', 1),
(66012, 'result_requirement',  '提交项目源码与说明文档',    2),
(66013, 'purpose_task',        '完成课程综合项目设计与实现', 1),
(66013, 'content_requirement', '按角色分工协作并提交项目报告', 2);

-- 考核评价（第八部分）
-- assessment_item/method/mechanism 存字典编码：
--   sys_assessment_item(1期末 2作业 3期中测试 4随堂测试 5项目 6实验 7课堂表现 8大作业)
--   sys_assessment_method(1闭卷考试 2考查 3开卷考试)
--   sys_assessment_mechanism(1百分制 2五级制 3两级制 4四级制)
--   standard 存 sys_evaluation_standard 编码串(逗号分隔)：五级制=1,2,3,4,5；四级制=1,2,3,4；两级制=6,7；百分制为空
-- id 显式给定 68011~68016，供 objective_assessment 引用
INSERT INTO t_csys_teaching_plan_assessment
(id, plan_id, assessment_category, assessment_item, method, mechanism, standard, weight, outcome_type, sort)
VALUES
(68011, 6001, 1, '1', '1', '1', NULL,       0.50, 1, 1),
(68012, 6001, 1, '3', '1', '1', NULL,       0.10, 1, 2),
(68013, 6001, 2, '2', '2', '2', '1,2,3,4,5',0.15, 2, 3),
(68014, 6001, 2, '4', '3', '1', NULL,       0.05, 2, 4),
(68015, 6001, 2, '8', '2', '4', '1,2,3,4', 0.10, 2, 5),
(68016, 6001, 2, '7', '2', '3', '6,7',     0.10, 2, 6);

-- 目标达成考核设计（第八部分（二））；objective_assessment.assessment_item 存与 assessment 相同的字典编码
INSERT INTO t_csys_teaching_plan_objective_assessment
(plan_id, scheme_id, objective_id, assessment_id, assessment_item, weight, assessment_item_content)
VALUES
(6001, 7601, 60011, 68011, '1', 0.3000, '检验结构化程序设计基础掌握情况'),
(6001, 7601, 60011, 68012, '3', 0.1000, '检验阶段编程能力'),
(6001, 7601, 60014, 68011, '1', 0.1500, '检验面向对象知识掌握情况'),
(6001, 7601, 60014, 68013, '2', 0.1000, '作业反映面向对象综合应用'),
(6001, 7601, 60015, 68012, '3', 0.0500, '检验数据结构知识'),
(6001, 7601, 60012, 68011, '1', 0.1500, '检验算法应用能力'),
(6001, 7601, 60012, 68015, '8', 0.1000, '大作业检验综合工程能力'),
(6001, 7601, 60016, 68013, '2', 0.0500, '作业检验调试与走查能力'),
(6001, 7601, 60017, 68014, '4', 0.0500, '随堂检验算法分析能力'),
(6001, 7601, 60013, 68016, '7', 0.0500, '课堂表现检验协作与素养'),
(6001, 7601, 60013, 68013, '2', 0.0500, '作业检验编码习惯'),
(6001, 7601, 60018, 68016, '7', 0.0500, '课堂表现检验严谨作风'),
(6001, 7601, 60018, 68014, '4', 0.0500, '随堂检验学习诚信');

-- 教材与条件（第九部分）；material_nature 存 sys_textbook_nature(1基本教材/2辅助教材/3参考教材)
INSERT INTO t_csys_teaching_plan_textbook
(plan_id, material_nature, name, first_author, edition, publisher, publish_time, isbn, sort)
VALUES
(6001, 1, 'C语言程序设计（第3版）', '谭浩强', '第3版', '清华大学出版社', '2010-01', '9787302254373', 1),
(6001, 3, '数据结构（C语言版）',   '严蔚敏', '第2版', '清华大学出版社', '2011-02', '9787302147514', 2);

-- condition_type 存 sys_condition_type(1教室/2教学平台/3实验室/4试验设备/5场地/6训练器材/7模拟训练条件/8实践条件/9装备)
INSERT INTO t_csys_teaching_plan_condition (plan_id, condition_type, requirement, sort)
VALUES
(6001, '3', '配备多媒体机房，安装C语言开发环境', 1),
(6001, '2', '课程在线教学平台及编程实训资源',   2),
(6001, '1', '多媒体教室，支持投屏与上机演示',   3);

-- =====================================================================
-- 12. 实践训练课目(plan_type=3)计划 6002（军事基础训练）子模块
--     重点：第四部分「模块」列 title 存 sys_plan_training_module 字典 value
-- =====================================================================
-- 训练目的（通识通用 location='1' -> scheme_id 恒为 NULL 单组，4 条）
-- id 显式给定 62111~62114，供 training_purpose_ref / content_purpose / support_objective 引用
INSERT INTO t_csys_teaching_plan_training_purpose
(id, plan_id, scheme_id, purpose, sort)
VALUES
(62111, 6002, NULL, '掌握单个军人队列动作、班队列组织等基本军事素养', 1),
(62112, 6002, NULL, '培养令行禁止、雷厉风行的战斗作风', 2),
(62113, 6002, NULL, '培养按作战流程组织指挥、协同配合的班组指挥能力', 3),
(62114, 6002, NULL, '锤炼过硬心理素质与临战处置能力', 4);

INSERT INTO t_csys_teaching_plan_training_purpose_ref
(plan_id, purpose_id, scheme_course_graduation_id, quote_course_id, scheme_id,
 graduation_id, source_graduation_id, graduation_code, graduation_name, graduation_bind_source, sort)
VALUES
(6002, 62111, 1003, 80021, 7601, 90101, 90101, 'GR1', '掌握计算机科学与技术的基础理论与专业知识', 'scheme_course', 1),
(6002, 62112, 1004, 80021, 7601, 90103, 90103, 'GR3', '具有良好的职业道德、团队合作与终身学习意识', 'scheme_course', 2),
(6002, 62113, 1002, 80021, 7601, 90102, 90102, 'GR2', '具备计算机工程实践与系统开发能力',           'scheme_course', 3),
(6002, 62114, 1004, 80021, 7601, 90103, 90103, 'GR3', '具有良好的职业道德、团队合作与终身学习意识', 'scheme_course', 4);

-- 第四部分 训练内容与时间安排：title=字典value(1/2/3)，目的为整格字符串，不建立训练目的绑定
-- id 显式给定 62211/62212/62213，供 support_content 引用
INSERT INTO t_csys_teaching_plan_content
(id, plan_id, content_type, title, content, purpose, time_arrange, sort)
VALUES
(62211, 6002, 1, '1', '单个军人队列动作与班队列组织', '掌握单个军人队列动作、班队列组织等基本军事素养', '第1-2周', 1),
(62212, 6002, 1, '2', '指挥口令运用与队列指挥',       '培养按作战流程组织指挥、协同配合的班组指挥能力', '第3-4周', 2),
(62213, 6002, 1, '3', '新质新域装备认知与操作基础',   '了解新质新域装备基本原理并形成规范操作意识',       '第5-6周', 3);

-- 第三部分 训练任务与总体设计（训练任务/总体设计 section；配套支撑课程来自课程库配置）
INSERT INTO t_csys_teaching_plan_section
(plan_id, section_code, section_title, content, sort)
VALUES
(6002, 'organize_way',   '组织方式', '按班建制组织实施，小班化教学；采用分队与班组相结合的形式进行连贯作业。', 1),
(6002, 'task',           '训练任务', '围绕实现从普通学员向准战斗员、班组指挥员角色转变，夯实敢打仗、强技能、懂协同、善指挥军政基础的核心目标，坚持战斗力标准导向，通过高强度、全要素、对抗性的军政融合训练实践，推动教学训练由知识传授向能力生成转型。', 2),
(6002, 'overall_design', '总体设计', '坚持"政治铸魂、军事强基、科技赋能、战教耦合"，构建"课程—训练—考核"一体化融合育人体系，采用任务牵引、对抗推进、承压实施的方式组织实施，推动教学内容与作战任务对接、教学流程与作战进程融合。', 3);

-- 组织实施（第五部分）
INSERT INTO t_csys_teaching_plan_process_step
(plan_id, stage_name, step_name, requirement, sort)
VALUES
(6002, '1', '战备等级转进', '完成由平时状态向一级战备状态转进，组织筹划工作，开展战前动员。', 1),
(6002, '1', '远程跨域机动', '完成远程投送筹划，采用摩托化机动方式实施跨域投送。', 2),
(6002, '1', '组织临战训练', '到达部署地域后，组织开展战场自救与互救、电台通信、简易通信、夜视器材操作等战术基础内容训练。', 3),
(6002, '2', '作战筹划', '根据作战想定，召开"三会"（支委会、作战会、协同会），完成方案拟制、作战推演等内容。', 4),
(6002, '2', '隐蔽渗透', '完成隐蔽机动，期间开展防敌空中侦察、克服自然障碍、通过染毒地段、通过隘口、传单处置等内容训练。', 5),
(6002, '2', '引导打击', '完成战斗勤务（观察报知）、电台通信、简易通信等内容考核，完成战场自救与互救、战场宣传鼓动等内容训练。', 6),
(6002, '2', '夜间侦察', '完成夜间基础（夜视器材操作）考核，完成战场管理训练。', 7),
(6002, '2', '进攻战斗', '完成班组战斗行动（战斗准备、接敌运动、冲击行动、攻击目标）考核，完成无人、反无人作战力量运用、火线入党等内容。', 8),
(6002, '2', '追歼残敌', '完成战斗勤务（战斗准备、搜索前进、建立阵地）考核，完成战斗体能等训练内容。', 9),
(6002, '3', '复盘总结', '学员队按照战斗班对演练全流程进行复盘总结，学员队、战斗班分别形成总结报告。', 10),
(6002, '3', '撤收返回', '撤收返回。根据计划安排，完成宿营物资撤收，组织摩托化机动反营。', 11);

-- 考核评价（第六部分）+ 训练条件（第七部分）
-- 考核项目与其他类型一致用 sys_assessment_item 字典值（5项目/7课堂表现）；考核方式可保留课目自定义
INSERT INTO t_csys_teaching_plan_assessment
(plan_id, assessment_category, assessment_item, method, mechanism, standard, weight, outcome_type, sort)
VALUES
(6002, 1, '5', '队列会操评分', '2', '1,2,3,4,5', 0.60, 1, 1),
(6002, 2, '7', '出勤与作风',   '3', '6,7',      0.40, 2, 2);

INSERT INTO t_csys_teaching_plan_condition (plan_id, condition_type, requirement, sort)
VALUES
(6002, '6', '单兵队列训练器材', 1),
(6002, '7', '模拟训练条件',     2),
(6002, '5', '战术训练场与队列训练场', 3);

-- =====================================================================
-- 13. 实验课程(plan_type=2)计划 6003（大学物理实验）子模块
-- =====================================================================
INSERT INTO t_csys_teaching_plan_teacher
(plan_id, teacher_id, teacher_name, professional_title, duty, sort)
VALUES
(6003, 't003', '王老师', '副教授', '实验主讲', 1);

-- 任务背景（第三部分，每行一个目标类型；显式 id 供 task_background_ref 引用）
INSERT INTO t_csys_teaching_plan_task_background
(id, plan_id, scheme_id, major_id, background_desc, goal_type, goal_content, sort)
VALUES
(63011, 6003, 7601, 9001,
 '围绕力学、电磁学核心原理开展实验验证，强化理论与实验结合',
 1, '掌握常用实验仪器的操作与测量方法', 1),
(63012, 6003, 7601, 9001,
 '围绕力学、电磁学核心原理开展实验验证，强化理论与实验结合',
 2, '培养实验数据分析与科学表达能力', 2);

-- 任务背景 -> 支撑毕业要求（对应第三部分表格「支撑的毕业要求」列）
INSERT INTO t_csys_teaching_plan_task_background_ref
(plan_id, task_background_id, scheme_course_graduation_id, quote_course_id, scheme_id,
 graduation_id, source_graduation_id, graduation_code, graduation_name, graduation_bind_source, sort)
VALUES
(6003, 63011, 1002, 80031, 7601, 90102, 90102, 'GR2',
 '具备计算机工程实践与系统开发能力', 'scheme_course', 1),
(6003, 63012, 1002, 80031, 7601, 90102, 90102, 'GR2',
 '具备计算机工程实践与系统开发能力', 'scheme_course', 1);

-- 课程目标 + 绑定（知识/能力/素质 3 条）
-- id 显式给定 61511~61513，供 objective_ref 引用
INSERT INTO t_csys_teaching_plan_objective
(id, plan_id, scheme_id, major_id, objective_type_code, objective_type_name, content, source_mode, sort)
VALUES
(61511, 6003, 7601, 9001, '1', '知识目标', '掌握力学、电磁学核心实验原理与仪器原理', 1, 1),
(61512, 6003, 7601, 9001, '2', '能力目标', '具备独立完成实验、处理数据并撰写报告的能力', 1, 2),
(61513, 6003, 7601, 9001, '3', '素质目标', '培养严谨求实的科学态度与实验室安全意识', 1, 3);

INSERT INTO t_csys_teaching_plan_objective_ref
(plan_id, objective_id, scheme_course_graduation_id, quote_course_id, scheme_id,
 graduation_id, source_graduation_id, graduation_code, graduation_name, graduation_bind_source, sort)
VALUES
(6003, 61511, 1001, 80031, 7601, 90101, 90101, 'GR1', '掌握计算机科学与技术的基础理论与专业知识', 'scheme_course', 1),
(6003, 61512, 1002, 80031, 7601, 90102, 90102, 'GR2', '具备计算机工程实践与系统开发能力',           'scheme_course', 2),
(6003, 61513, 1005, 80031, 7601, 90103, 90103, 'GR3', '具有良好的职业道德、团队合作与终身学习意识', 'scheme_course', 3);

-- 实验项目（第四部分，4 项）+ 明细
-- id 显式给定 66511~66514，供 practice_item_detail 引用
INSERT INTO t_csys_teaching_plan_practice_item
(id, plan_id, item_type, name, hours, group_info, experiment_nature, study_nature, sort)
VALUES
(66511, 6003, 1, '牛顿第二定律验证实验',        8, '2人/组', '验证性', '必做', 1),
(66512, 6003, 2, '电磁感应综合实验',            8, '2人/组', '综合性', '必做', 2),
(66513, 6003, 3, '电阻应变效应与电桥法测应变',  8, '2人/组', '设计性', '必做', 3),
(66514, 6003, 4, '示波器信号测量与波形分析',    8, '2人/组', '验证性', '必做', 4);

INSERT INTO t_csys_teaching_plan_practice_item_detail (item_id, detail_type, content, sort)
VALUES
(66511, 'purpose_task',        '验证牛顿第二定律',           1),
(66511, 'ability_point',       '训练实验方案执行与数据分析能力', 2),
(66511, 'principle',           '牛顿第二定律及控制变量法',   3),
(66511, 'content_requirement', '记录并处理实验数据',         4),
(66511, 'result_requirement',  '形成规范实验报告',           5),
(66512, 'purpose_task',        '测定感应电动势与互感系数',   1),
(66512, 'ability_point',       '训练电磁学实验操作与误差分析能力', 2),
(66512, 'principle',           '法拉第电磁感应定律与互感原理', 3),
(66512, 'content_requirement', '测量感应电动势并计算互感系数', 4),
(66512, 'result_requirement',  '提交实验报告',               5),
(66513, 'purpose_task',        '掌握电桥法测量原理',         1),
(66513, 'ability_point',       '训练电桥搭建、调试与测量能力', 2),
(66513, 'principle',           '惠斯通电桥平衡原理与应变测量原理', 3),
(66513, 'content_requirement', '完成应变片贴片与电桥电路搭建',4),
(66513, 'result_requirement',  '提交实验报告',               5),
(66514, 'purpose_task',        '掌握示波器使用方法',         1),
(66514, 'ability_point',       '训练示波器操作与信号测量能力', 2),
(66514, 'principle',           '示波器扫描、触发与波形显示原理', 3),
(66514, 'content_requirement', '测量正弦/方波信号参数',      4),
(66514, 'result_requirement',  '准确记录波形参数并完成实验报告', 5);

-- 考核评价（第六部分）：期末百分制 / 实验报告五级制 / 实验操作四级制
INSERT INTO t_csys_teaching_plan_assessment
(plan_id, assessment_category, assessment_item, method, mechanism, standard, weight, outcome_type, sort)
VALUES
(6003, 1, '期末考核', '闭卷考试', '1', NULL,         0.30, 1, 1),
(6003, 2, '实验报告', '考查',   '2', '1,2,3,4,5', 0.40, 2, 2),
(6003, 2, '实验操作', '现场操作', '4', '1,2,3,4',   0.30, 2, 3);

-- 实验教材（第七部分）
INSERT INTO t_csys_teaching_plan_textbook
(plan_id, material_nature, name, first_author, publisher, sort)
VALUES
(6003, 1, '大学物理实验指导书', '物理教研室', '校内讲义', 1),
(6003, 2, '大学物理实验讲义',   '物理教研室', '校内印发', 2);

-- 实验条件（补试验设备/实验室）
INSERT INTO t_csys_teaching_plan_condition (plan_id, condition_type, requirement, sort)
VALUES
(6003, '3', '大学物理实验室（力学、电磁学）', 1),
(6003, '4', '示波器、力传感器、电磁感应装置', 2);

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

-- 第二节 支撑绑定（预置多条，供 createWord 直接出文档；测试亦会走 save 接口写/读）
INSERT INTO t_csys_teaching_plan_support_objective
(plan_id, ref_type, ref_plan_id, ref_course_id, objective_id, purpose_id, item_name, item_type_name, major_id, sort)
VALUES
(6004, 1, 6001, 7003, 60011, NULL, '掌握结构化程序设计的基本方法与三种基本结构', '知识目标', 9001, 1),
(6004, 2, 6002, 8002, NULL, 62111, '掌握单个军人队列动作、班队列组织等基本军事素养', NULL, 9001, 2),
(6004, 1, 6001, 7003, 60014, NULL, '掌握面向对象程序设计的基本思想与类、继承、多态等核心机制', '知识目标', 9001, 3),
(6004, 2, 6002, 8002, NULL, 62113, '培养按作战流程组织指挥、协同配合的班组指挥能力', NULL, 9001, 4);

-- type2 内容快照存模块字典名称（与 saveSupportContents 的 translateTrainingModuleName 输出一致）
INSERT INTO t_csys_teaching_plan_support_content
(plan_id, ref_type, ref_plan_id, ref_course_id, content_id, item_title, sort)
VALUES
(6004, 1, 6001, 7003, 65011, '第一章 程序设计基础',     1),
(6004, 2, 6002, 8002, 62211, '战斗体技能提升模块',       2),
(6004, 1, 6001, 7003, 65013, '第三章 面向对象程序设计', 3),
(6004, 2, 6002, 8002, 62212, '指挥素养培塑模块',         4);

-- 考核评价（第四部分 成果与评价）：成果类型 | 成果形式 | 评价的知识和能力 | 权重 | 评价准则
INSERT INTO t_csys_teaching_plan_assessment
(plan_id, assessment_category, assessment_item, method, assessed_content, mechanism, standard, weight, outcome_type, sort)
VALUES
(6004, 5, '成果答辩与文档评审', NULL, '系统设计、编码实现与文档质量', NULL, '能够完整说明设计方案并提交规范文档', 0.40, 1, 1),
(6004, 5, '团队成果展示与互评', NULL, '团队协作、系统集成与整体效能', NULL, '系统集成完整，团队分工与协作清晰', 0.30, 2, 2),
(6004, 5, '项目过程记录',       NULL, '项目过程管理与责任心',         NULL, '过程记录完整并按计划完成阶段任务', 0.30, 3, 3);

-- 实践条件（第五部分）
INSERT INTO t_csys_teaching_plan_condition (plan_id, condition_type, requirement, sort)
VALUES
(6004, '8', '软件开发实验室与项目服务器环境', 1),
(6004, '2', '在线项目管理与代码托管平台',     2);
