package com.example.cscy.generator;

import com.example.cscy.entity.scheme.model.*;
import org.apache.poi.xwpf.usermodel.*;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 培养方案文档生成器测试类 - 基于TrainingPlanModel
 *
 * 测试生成培养方案文档，包含四个一级标题：
 * 1. 培养目标
 * 2. 毕业要求
 * 3. 修业时间与学时学分
 * 4. 教学训练体系与安排
 */
public class TrainingPlanGeneratorTest {

    @Test
    public void testGenerateTrainingPlan() throws Exception {
        // 创建测试数据模型
        TrainingPlanModel model = createTestTrainingPlanModel();

        // 创建生成器
        TrainingPlanGenerator generator = new TrainingPlanGenerator();

        // 输出路径
        String outputPath = "target/output/training_plan_generated.docx";
        new File("target/output").mkdirs();

        // 生成文档
        generator.generate(model, outputPath);

        System.out.println("培养方案文档生成成功: " + outputPath);

        // 验证文件存在且不为空
        File file = new File(outputPath);
        if (!file.exists()) {
            throw new AssertionError("生成的文件不存在");
        }
        if (file.length() <= 0) {
            throw new AssertionError("生成的文件为空");
        }

        System.out.println("文件大小: " + file.length() + " 字节");

        // 验证DOCX文件结构
        verifyDocxFile(outputPath);

        System.out.println("测试通过!");
    }

    /**
     * 创建测试用的培养方案模型
     */
    private TrainingPlanModel createTestTrainingPlanModel() {
        TrainingPlanModel model = new TrainingPlanModel();
        model.setTrainingPlanName("计算机科学与技术专业培养方案");

        // 1. 培养目标
        TrainingTargetModel target = new TrainingTargetModel();
        target.setFirstLevelTitle("一、培养目标");
        target.setFirstLevelContent("本培养方案适用于生长军官本科学员xxxxxxxx、xxxxxxxx、xxxxxxxx、xxxxxxxx专业本科学历教育。");
        target.setSecondLevelTitle1("1.总体目标");
        target.setSecondLevelContent1("培养\"对党绝对忠诚、科技基础厚实、创新思维敏锐、军事素质过硬、作风纪律优良\"的高素质专业化新型军事人才，为造就通晓战争的科技专家和掌握科技的军事专家奠定坚实基础。");
        target.setSecondLevelTitle2("2.具体目标");
        target.setSecondLevelContent2("立足军事信息技术行业发展需求，面向作战、保障、管理、技术领域，培养德、智、体、美、劳全面发展，具有扎实的计算机科学与技术、人工智能、数据科学知识，具备系统思维、工程思维、创新思维和领导管理能力，具备坚定的政治素质、过硬的军事素质、优秀的科技素质、深厚的人文素质和健康的心理素质，能够在军事信息技术行业胜任系统开发、数据分析、网络安全、智能决策等岗位，从事系统设计、数据分析、安全防护、智能应用等工作的复合型/研究型/应用型高素质指挥管理、作战保障、科技创新、工程技术人才。");
        model.setTrainingTarget(target);

        // 2. 毕业要求
        model.setStandardGraduationContent("具有学籍的本科学员，在修业年限内完成本培养方案规定的教学训练，通过各项考核、达成以下毕业要求，依据国防科技大学《高等教育生长军官学员、军士职业技术教育学员学籍管理规定实施细则（暂行）》，颁发毕业证书；依据《国防科技大学学位工作细则（暂行）》，对符合学位授予条件的毕业学员，授予工学学士学位。");

        List<StandardGraduationModel> graduations = new ArrayList<>();
        // level=1: 毕业要求大类 - 1
        StandardGraduationModel grad1 = new StandardGraduationModel();
        grad1.setName("（一）知识要求");
        grad1.setLevel("1");
        grad1.setParentId(-1L);
        grad1.setOrder(1);
        grad1.setLeaf(0);
        grad1.setId(1L);
        graduations.add(grad1);

        // level=2: 二级标题 - 1.1
        StandardGraduationModel grad1_1 = new StandardGraduationModel();
        grad1_1.setName("1.政治理论知识");
        grad1_1.setLevel("2");
        grad1_1.setParentId(1L); // 父项是grad1，其id为1
        grad1_1.setOrder(1);
        grad1_1.setLeaf(0);
        grad1_1.setId(2L);
        graduations.add(grad1_1);

        // level=3: 具体内容
        StandardGraduationModel grad1_1_1 = new StandardGraduationModel();
        grad1_1_1.setName("掌握思想道德与法治、马克思列宁主义、毛泽东思想、邓小平理论、“三个代表”重要思想、科学发展观、习近平新时代中国特色社会主义思想的基本内容；");
        grad1_1_1.setLevel("3");
        grad1_1_1.setParentId(2L); // 父项是grad1_1，其id为2
        grad1_1_1.setOrder(1);
        grad1_1_1.setLeaf(1);
        grad1_1_1.setId(3L);
        graduations.add(grad1_1_1);

        StandardGraduationModel grad1_2 = new StandardGraduationModel();
        grad1_2.setName("2.军事基础知识");
        grad1_2.setLevel("2");
        grad1_2.setParentId(1L); // 父项是grad1，其id为1
        grad1_2.setOrder(2);
        grad1_2.setLeaf(0);
        grad1_2.setId(4L);
        graduations.add(grad1_2);

        // level=3: 具体内容
        StandardGraduationModel grad1_2_1 = new StandardGraduationModel();
        grad1_2_1.setName("掌握联合作战、军事高科技、军事历史、军事思想等事理论知识；");
        grad1_2_1.setLevel("3");
        grad1_2_1.setParentId(4L); // 父项是grad1_2，其id为4
        grad1_2_1.setOrder(2);
        grad1_2_1.setLeaf(1);
        grad1_2_1.setId(5L);
        graduations.add(grad1_2_1);

        // level=2: 二级标题 - 1.3
        StandardGraduationModel grad1_3 = new StandardGraduationModel();
        grad1_3.setName("3.基础科学知识");
        grad1_3.setLevel("2");
        grad1_3.setParentId(1L); // 父项是grad1，其id为1
        grad1_3.setOrder(3);
        grad1_3.setLeaf(0);
        grad1_3.setId(6L);
        graduations.add(grad1_3);

        StandardGraduationModel grad1_3_1 = new StandardGraduationModel();
        grad1_3_1.setName("掌握高等数学、线性代数、概率论与数理统计的基本理论、基本知识和基本方法；");
        grad1_3_1.setLevel("3");
        grad1_3_1.setParentId(6L); // 父项是grad1_3，其id为6
        grad1_3_1.setOrder(3);
        grad1_3_1.setLeaf(1);
        grad1_3_1.setId(7L);
        graduations.add(grad1_3_1);

        StandardGraduationModel grad1_4 = new StandardGraduationModel();
        grad1_4.setName("4.人文与社会科学知识");
        grad1_4.setLevel("2");
        grad1_4.setParentId(1L); // 父项是grad1，其id为1
        grad1_4.setOrder(4);
        grad1_4.setLeaf(0);
        grad1_4.setId(8L);
        graduations.add(grad1_4);

        StandardGraduationModel grad1_4_1 = new StandardGraduationModel();
        grad1_4_1.setName("掌握沟通与写作、艺术与鉴赏、文学鉴赏、心理学、社会学等基本知识和基本方法。");
        grad1_4_1.setLevel("3");
        grad1_4_1.setParentId(8L); // 父项是grad1_4，其id为8
        grad1_4_1.setOrder(4);
        grad1_4_1.setLeaf(1);
        grad1_4_1.setId(9L);
        graduations.add(grad1_4_1);


        model.setStandardGraduations(graduations);

        // 3. 修业时间与学时学分
        DurationAndCreditsModel dac = new DurationAndCreditsModel();
        dac.setFirstLevelTitle1("（一）修业时间安排");
        dac.setFirstLevelContent1("在校总时间约203周，其中入学入伍教育训练约6周、4个寒假约16周、2个暑假约8周、教学训练安排不少于164周，安排约130周课程教学（不含法定节假日）和约34周实践训练。具体教学训练及寒暑假安排根据学校教学周历确定。");
        dac.setFirstLevelTitle2("（二）学时学分要求");
        dac.setFirstLevelContent2("学员教学训练体系由课程体系和实践训练体系组成，课程体系包含通识课程和专业课程2个部分，实践训练体系包含实践项目和训练科目2个部分。按照修读要求，所有课程、实践项目、训练课目分为必修和选修2种类别，其中，选修又区分为限定选修（以下简称\"限选\"）、任意选修（以下简称\"任选\"）2种类别，限选即所有学员均须修读、任选即学员自主选择修读。\n" +
                "学员在校期间课程学习须修满160学分，其中必修课程120学分、选修课程40学时（含政治类20学时、军事类30学时、科技类50学时、人文与社会科学类20学时、专业选修20学时）；实践训练须修满30周，其中必修实践训练20周、选修实践训练10周。课程教学按16学时折合1学分计算、训练课目按每周折合1学分计算、实践项目不再单独计算学分。");
        dac.setFirstLevelTitle3("（三）学分冲抵机制");
        dac.setFirstLevelContent3("1.学员修读新生研讨课所获学分可冲抵通识任选课程学分，新生研讨课教学安排见年度选课通知。\n" +
                "2.学员修读军事职业教育平台、学校智课平台在线课程，按照每20学时在线课时冲抵1学分任选课程。\n" +
                "3.学员在校期间参加学科竞赛、科技创新、文化活动、军事比武、运动会、俱乐部等实践活动并获奖、发表学术论文或取得专利，申报并完成创新实践项目或自主设计并完成创新实验，参加国际或国家组织的各类正规专业性资格认证或水平考试达到一定成绩等，可根据学校有关规定凭获奖证书冲抵选修实践训练学分。学科竞赛列表详见学校年度学科竞赛计划。");
        model.setDurationAndCredits(dac);

        // 4. 课程安排
        List<TrainingSchemeCourseModel> generalCourses = createGeneralEducationCourses();
        model.setGeneralEducationCourses(generalCourses);

        List<TrainingSchemeCourseModel> majorCategoryCourses = createMajorCategoryCourses();
        model.setMajorCategoryCourseArrangements(majorCategoryCourses);

        List<TrainingSchemeCourseModel> majorDirectionCourses = createMajorDirectionCourses();
        model.setMajorDirectionCourseArrangements(majorDirectionCourses);

        return model;
    }

    /**
     * 创建通识类课程
     */
    private List<TrainingSchemeCourseModel> createGeneralEducationCourses() {
        List<TrainingSchemeCourseModel> courses = new ArrayList<>();

        // 政治理论模块 - modeChildrenNameSort=1
        // modeFourLevelName 是 courseModeChildrenName 的子模式
        courses.add(createCourseWithFourLevel("马克思主义基本原理", 48.0, 48.0, 0.0, "必修", "考试", "政治理论", "通识课", "第一学年", "秋", "计算机科学", 1, null, null));
        courses.add(createCourseWithFourLevel("毛泽东思想和中国特色社会主义理论体系概论", 64.0, 64.0, 0.0, "必修", "考试", "政治理论", "通识课", "第一学年", "春", "计算机科学", 1, null, null));
        courses.add(createCourseWithFourLevel("中国共产党历史", 32.0, 32.0, 0.0, "必修", "考查", "政治理论", "通识课", "第二学年", "春", "计算机科学", 1, null, null));
        // 选修课程 - modeFourLevelName 为 "政治理论-选修"
        courses.add(createCourseWithFourLevel("形势与政策", 32.0, 32.0, 0.0, "任选", "考查", "政治理论", "通识课", "第二学年", "春", "计算机科学", 1, null, null));

        // 军事基础模块 - modeChildrenNameSort=2
        courses.add(createCourseWithFourLevel("军事理论", 32.0, 32.0, 0.0, "必修", "考查", "军事基础", "通识课", "第一学年", "秋", "计算机科学", 2, null, null));
        courses.add(createCourseWithFourLevel("军事技能训练", 2.0, 0.0, 2.0, "必修", "考查", "军事基础", "通识课", "第一学年", "秋", "计算机科学", 2, null, null));
        courses.add(createCourseWithFourLevel("单兵战术", 16.0, 4.0, 12.0, "必修", "考查", "军事基础", "通识课", "第二学年", "春", "计算机科学", 2, null, null));
        courses.add(createCourseWithFourLevel("射击实训", 24.0, 6.0, 18.0, "必修", "考查", "军事基础", "通识课", "第三学年", "春", "计算机科学", 2, null, null));

        // 基础科学模块 - modeChildrenNameSort=3
        courses.add(createCourseWithFourLevel("高等数学A", 80.0, 80.0, 0.0, "必修", "考试", "科学文化", "通识课", "第四学年", "秋", "计算机科学", 3, 1, "基础科学"));
        courses.add(createCourseWithFourLevel("线性代数", 48.0, 48.0, 0.0, "必修", "考试", "科学文化", "通识课", "第四学年", "春", "计算机科学", 3, 1, "基础科学"));
        courses.add(createCourseWithFourLevel("概率论与数理统计", 48.0, 48.0, 0.0, "必修", "考试", "科学文化", "通识课", "第四学年", "春", "计算机科学", 3, 1, "基础科学"));
        // 物理课程 - modeFourLevelName 为 "基础科学-物理"
        courses.add(createCourseWithFourLevel("大学物理", 64.0, 64.0, 0.0, "必修", "考试", "科学文化", "通识课", "第三学年", "秋", "计算机科学", 3, 1, "基础科学"));

        // 人文与社会科学模块 - modeChildrenNameSort=4
        courses.add(createCourseWithFourLevel("大学英语A", 64.0, 64.0, 0.0, "必修", "考试", "科学文化", "通识课", "第一学年", "秋", "计算机科学", 4, 2, "外语"));
        courses.add(createCourseWithFourLevel("大学英语听说", 32.0, 16.0, 16.0, "必修", "考查", "科学文化", "通识课", "第二学年", "春", "计算机科学", 4, 2, "外语"));
        courses.add(createCourseWithFourLevel("应用文写作", 32.0, 32.0, 0.0, "任选", "考查", "科学文化", "通识课", "第二学年", "春", "计算机科学", 4, 3, "人文与社会科学"));
        courses.add(createCourseWithFourLevel("艺术鉴赏", 32.0, 32.0, 0.0, "任选", "考查", "科学文化", "通识课", "第三学年", "秋", "计算机科学", 4, 3, "人文与社会科学"));

        // 人工智能与信息技术模块 - modeChildrenNameSort=5
        courses.add(createCourseWithFourLevel("计算机导论", 16.0, 16.0, 0.0, "必修", "考查", "科学文化", "通识课", "第三学年", "秋", "计算机科学", 5, 4, "人工智能与信息技术"));
        courses.add(createCourseWithFourLevel("程序设计基础", 64.0, 32.0, 32.0, "必修", "考试", "科学文化", "通识课", "第一学年", "秋", "计算机科学", 5, 4, "人工智能与信息技术"));
        courses.add(createCourseWithFourLevel("数据结构", 48.0, 24.0, 24.0, "必修", "考试", "科学文化", "通识课", "第三学年", "春", "计算机科学", 5, 4, "人工智能与信息技术"));
        courses.add(createCourseWithFourLevel("算法设计", 48.0, 24.0, 24.0, "限选", "考试", "科学文化", "通识课", "第四学年", "秋", "计算机科学", 5, 4, "人工智能与信息技术"));

        return courses;
    }

    /**
     * 创建专业大类课程
     */
    private List<TrainingSchemeCourseModel> createMajorCategoryCourses() {
        List<TrainingSchemeCourseModel> courses = new ArrayList<>();

        // 专业基础课 - modeChildrenNameSort=1, modeFourLevelSort为空表示无四级模块
        courses.add(createCourseWithFourLevel("计算机组成原理", 48.0, 32.0, 16.0, "必修", "考试", "专业大类", "专业课", "第一学年", "秋", "计算机科学", 1, null, null));
        courses.add(createCourseWithFourLevel("操作系统", 48.0, 32.0, 16.0, "必修", "考试", "专业大类", "专业课", "第二学年", "春", "计算机科学", 1, null, null));
        courses.add(createCourseWithFourLevel("计算机网络", 48.0, 32.0, 16.0, "必修", "考试", "专业大类", "专业课", "第三学年", "春", "计算机科学", 1, null, null));
        courses.add(createCourseWithFourLevel("数据库系统", 48.0, 32.0, 16.0, "必修", "考试", "专业大类", "专业课", "第四学年", "春", "计算机科学", 1, null, null));

        return courses;
    }

    /**
     * 创建专业方向课程
     */
    private List<TrainingSchemeCourseModel> createMajorDirectionCourses() {
        List<TrainingSchemeCourseModel> courses = new ArrayList<>();

        // 软件工程方向 - modeChildrenNameSort=1
        courses.add(createCourseWithFourLevel("软件工程", 48.0, 32.0, 16.0, "必修", "考试", "软件工程", "专业课", "第二学年", "秋", "软件工程方向", 1, 1, "软件工程-核心"));
        courses.add(createCourseWithFourLevel("软件测试", 32.0, 16.0, 16.0, "限选", "考查", "软件工程", "专业课", "第一学年", "春", "软件工程方向", 1, 2, "软件工程-测试"));
        courses.add(createCourseWithFourLevel("敏捷开发", 32.0, 16.0, 16.0, "任选", "考查", "软件工程", "专业课", "第四学年", "春", "软件工程方向", 1, 3, "软件工程-开发"));

        // 数据科学方向 - modeChildrenNameSort=2
        courses.add(createCourseWithFourLevel("机器学习", 48.0, 24.0, 24.0, "限选", "考试", "数据科学", "专业课", "第一学年", "秋", "数据科学方向", 2, 1, "数据科学-核心"));
        courses.add(createCourseWithFourLevel("数据挖掘", 32.0, 16.0, 16.0, "限选", "考查", "数据科学", "专业课", "第三学年", "春", "数据科学方向", 2, 2, "数据科学-挖掘"));
        courses.add(createCourseWithFourLevel("大数据技术", 32.0, 16.0, 16.0, "任选", "考查", "数据科学", "专业课", "第二学年", "春", "数据科学方向", 2, 3, "数据科学-大数据"));

        return courses;
    }

    /**
     * 创建课程对象
     */
    private TrainingSchemeCourseModel createCourse(String name, Double hours, Double theoryHours,
                                                   Double practiceHours, String attrName, String openTerm,
                                                   String courseModeChildrenName, String courseModelName,
                                                   String semesterSchedule, String springAutumn, String majorName) {
        return createCourseWithFourLevel(name, hours, theoryHours, practiceHours, attrName, openTerm,
                courseModeChildrenName, courseModelName, semesterSchedule, springAutumn, majorName,
                null, null, null);
    }

    /**
     * 创建带modeFourLevelName的课程对象
     */
    private TrainingSchemeCourseModel createCourseWithFourLevel(String name, Double hours, Double theoryHours,
                                                                   Double practiceHours, String attrName, String openTerm,
                                                                   String courseModeChildrenName, String courseModelName,
                                                                   String semesterSchedule, String springAutumn,
                                                                   String majorName, Integer modeChildrenNameSort,
                                                                Integer modeFourLevelSort, String modeFourLevelName) {
        TrainingSchemeCourseModel course = new TrainingSchemeCourseModel();
        course.setName(name);
        course.setHours(hours);
        course.setTheoryHours(theoryHours);
        course.setPracticeHours(practiceHours);
        course.setCourseAttrName(attrName);
        course.setOpenTerm(openTerm);
        course.setCourseModeChildrenName(courseModeChildrenName);
        course.setCourseModelName(courseModelName);
        course.setSemesterSchedule(semesterSchedule);
        course.setSpringAutumn(springAutumn);
        course.setMajorName(majorName);
        course.setModeChildrenNameSort(modeChildrenNameSort);
        course.setModeFourLevelSort(modeFourLevelSort);
        course.setModeFourLevelName(modeFourLevelName);
        return course;
    }

    /**
     * 验证DOCX文件的有效性
     */
    private void verifyDocxFile(String docxPath) throws Exception {
        System.out.println("\n=== 验证 DOCX 文件 ===");

        // 这里可以添加更多验证逻辑
        // 例如：解压DOCX文件，检查内部结构
        File file = new File(docxPath);
        System.out.println("文件路径: " + file.getAbsolutePath());
        System.out.println("文件大小: " + file.length() + " 字节");
        System.out.println("文件存在: " + file.exists());
        System.out.println("文件可读: " + file.canRead());
    }
}
