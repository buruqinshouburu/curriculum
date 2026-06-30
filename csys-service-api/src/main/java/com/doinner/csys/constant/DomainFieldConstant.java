package com.doinner.csys.constant;

import com.doinner.csys.domain.Course;

import java.util.List;
import java.util.Map;

public class DomainFieldConstant {

    public static final String ROOT_NODE_STRING_ID = "-1";

    public static final Long ROOT_NODE_LONG_ID = -1l;
    //正常
    public static final Integer DEL_FLAG_NORMAL_VALUE = 0;
    //已删除
    public static final Integer DEL_FLAG_DELETE_VALUE = 2;
    //叶子节点
    public static final Integer TREE_LEAF_VALUE = 1;
    //目录节点
    public static final Integer TREE_FOLDER_VALUE = 0;

    //课程审核状态0未审核 1 已审核
    public static final Integer COURSE_STATUS_EXAMINE = 0;
    public static final Integer COURSE_STATUS_NO_EXAMINE = 1;


    /**
     * -------------------------------------------
     * 课程---结束
     * -------------------------------------------
     */
    public static final String COURSE_DICT_TYPE = "kg_attr_course_type";
    public static final String COURSE_DICT_PROP = "kg_attr_course_pro";
    public static final String COURSE_DICT_BRO = "kg_attr_course_bro";
    public static final String COURSE_DICT_ATTR = "kg_attr_course_attr";
    public static final String COURSE_DICT_ATTR_EXAM = "kg_attr_exam";
    public static final String UNIT_DICT_ATTR_CSCD = "kg_attr_cscd";
    public static final String UNIT_DICT_ATTR_YQCD = "kg_attr_yqcd";
    public static final String COURSE = "课程";
    public static final String COURSE_TYPE = "课程类型";
    public static final String COURSE_CODE = "课程编号";
    public static final String COURSE_COLLEGE = "所属学院";
    public static final String COURSE_NAME = "课程";

    public static final String EXCEL_TITLE_SERIAL_NUMBER = "序号";
    public static final String EXCEL_TITLE_COURSE_NAME = "课程";
    public static final String EXCEL_TITLE_CHARGE = "课程负责人";
    public static final String EXCEL_TITLE_AUTHORS = "课程团队人员";
    public static final String EXCEL_TITLE_HOURS = "学时";
    public static final String EXCEL_TITLE_THEORY_HOURS = "理论学时";
    public static final String EXCEL_TITLE_TEACH_HOURS = "讲授学时";
    public static final String EXCEL_TITLE_PRACTICE_HOURS = "实践学时";
    public static final String EXCEL_TITLE_EXA_METHOD = "考核方式";
    public static final String EXCEL_TITLE_TEXT_BOOK = "教材";
    public static final String EXCEL_TITLE_REFERENCE_BOOK = "其他教学资源";
    public static final String EXCEL_TITLE_BEFORE_COURSE = "前续课程";
    public static final String EXCEL_TITLE_AFTER_COURSE = "后续课程";
    public static final String EXCEL_TITLE_SUMMARY = "课程简介";
    public static final String EXCEL_TITLE_UNIT = "知识单元";
    public static final String EXCEL_TITLE_UNIT_REMARK = "知识单元简介";
    public static final String EXCEL_TITLE_POINT = "知识点";
    public static final String EXCEL_TITLE_POINT_POWER = "能力水平要求";
    public static final String EXCEL_TITLE_POINT_BEFORE_KNOWLEDGE = "先导知识";

    public static final String EXCEL_FILE_NAME = "课程数据.xlsx";
    public static final String EXCEL_SHEET_NAME = "课程数据";

    //教材参考书
    public static final Integer TEXT_BOOK_TYPE = 1;
    public static final Integer REFERENCE_BOOK_TYPE = 2;

    /**
     * -------------------------------------------
     * 课程---结束
     * -------------------------------------------
     */

    public static final String STANDARD_MAJOR_NAME = "专业";
    public static final String STANDARD_SUB_MAJOR_NAME = "细分专业";

    public static final String STANDARD_CLASS_NAME = "技术指挥分类";
    public static final String STANDARD_CLASS_NO_TYPE_NAME = "未分类";
    public static final String STANDARD_CLASS_TECHNOLOGY_NAME = "技术类";
    public static final String STANDARD_CLASS_COMMAND_NAME = "指挥类";

    public static final Long CLASS_NO_TYPE = 0l;
    public static final Long CLASS_TECHNOLOGY = 1l;
    public static final Long CLASS_COMMAND = 2l;

    public static final Map<String, Long> STANDARD_CLASS_MAP = Map.of(
            STANDARD_CLASS_NO_TYPE_NAME, CLASS_NO_TYPE,
            STANDARD_CLASS_TECHNOLOGY_NAME, CLASS_TECHNOLOGY,
            STANDARD_CLASS_COMMAND_NAME, CLASS_COMMAND
    );

    public static final String EXCEL_CHOOSE = "√";


    /**
     * -------------------------------------------
     * 能力---开始
     * -------------------------------------------
     */
    public static final String STANDARD_ABILITY = "能力";
    public static final String STANDARD_ABILITY_NAME = "能力名称";
    public static final String STANDARD_ABILITY_FIRST = "一级指标";
    public static final String STANDARD_ABILITY_SECOND = "二级指标";
    public static final String STANDARD_ABILITY_THIRD = "三级指标";
    public static final String ABILITY_STANDARD = "能力标准";
    /**
     * -------------------------------------------
     *          能力---结束
     * -------------------------------------------
     */
    /**
     * -------------------------------------------
     * 培养目标---开始
     * -------------------------------------------
     */
    public static final String STANDARD_CULTIVATION_TARGET = "培养目标";
    public static final String STANDARD_CULTIVATION_TARGET_NAME = "培养目标名称";
    public static final String STANDARD_CULTIVATION_PARTIAL_TARGET_NAME = "分目标";
    public static final String STANDARD_CULTIVATION_TARGET_REMARK = "描述";
    public static final String STANDARD_CULTIVATION_MAIN_TARGET = "总体目标";
    public static final String STANDARD_CULTIVATION_TARGET_CODE = "编码";
    public static final String REMARK = "remark";
    /**
     * -------------------------------------------
     *          培养目标---结束
     * -------------------------------------------
     */
    /**
     * -------------------------------------------
     * 毕业标准---开始
     * -------------------------------------------
     */
    public static final String STANDARD_GRADUATION = "毕业标准";
    public static final String STANDARD_GRADUATION_NAME = "毕业标准名称";
    public static final String STANDARD_GRADUATION_CLASSI = "分类";
    public static final String STANDARD_GRADUATION_REMARK = "描述";
    public static final String STANDARD_GRADUATION_CODE = "编码";
    /**
     * -------------------------------------------
     *          毕业标准---结束
     * -------------------------------------------
     */
    /**
     * -------------------------------------------
     * 培养标准---开始
     * -------------------------------------------
     */
    public static final String STANDARD_CULTIVATION = "培养标准";
    public static final String STANDARD_CULTIVATION_NAME = "培养标准名称";
    public static final String STANDARD_CULTIVATION_FIRST = "一级标准";
    public static final String STANDARD_CULTIVATION_SECOND = "二级标准";
    public static final String STANDARD_CULTIVATION_THIRD = "三级标准";
    public static final String STANDARD_CULTIVATION_REMARK = "四级描述";
    public static final String STANDARD_CULTIVATION_CODE = "编号";
    /**
     * -------------------------------------------
     *          培养标准---结束
     * -------------------------------------------
     */

    /**
     * -------------------------------------------
     * 培养方案---结束
     * -------------------------------------------
     */
    //5类课程类型
    public final static String PUBLIC_REQUIRED_COURSE_NAME = "公共基础必修课程";

    public final static String SUBJECT_REQUIRED_COURSE_NAME = "学科基础必修课程";

    public final static String SPECIALITY_REQUIRED_COURSE_NAME = "专业必修课程";

    public final static String PUBLIC_ELECTIVE_COURSE_NAME = "公共基础选修课程";

    public final static String SUBJECT_ELECTIVE_COURSE_NAME = "学科基础、专业选修课程";

    public final static Integer PUBLIC_REQUIRED_COURSE = 1;

    public final static Integer SUBJECT_REQUIRED_COURSE = 2;

    public final static Integer SPECIALITY_REQUIRED_COURSE = 3;

    public final static Integer PUBLIC_ELECTIVE_COURSE = 4;

    public final static Integer SUBJECT_ELECTIVE_COURSE = 5;

    public static Map<String, Integer> COURSE_TYPE_MAP = Map.of(
            PUBLIC_REQUIRED_COURSE_NAME, PUBLIC_REQUIRED_COURSE,
            SUBJECT_REQUIRED_COURSE_NAME, SUBJECT_REQUIRED_COURSE,
            SPECIALITY_REQUIRED_COURSE_NAME, SPECIALITY_REQUIRED_COURSE,
            PUBLIC_ELECTIVE_COURSE_NAME, PUBLIC_ELECTIVE_COURSE,
            SUBJECT_ELECTIVE_COURSE_NAME, SUBJECT_ELECTIVE_COURSE);


    //8个学期
    public final static String FERSHMAN_YEAR_FIRST_SEMESTER = "大一上";

    public final static String FERSHMAN_YEAR_SECOND_SEMESTER = "大一下";

    public final static String SOPHOMORE_YEAR_FIRST_SEMESTER = "大二上";

    public final static String SOPHOMORE_YEAR_SECOND_SEMESTER = "大二下";

    public final static String JUNIOR_YEAR_FIRST_SEMESTER = "大三上";

    public final static String JUNIOR_YEAR_SECOND_SEMESTER = "大三下";

    public final static String SENIOR_YEAR_FIRST_SEMESTER = "大四上";

    public final static String SENIOR_YEAR_SECOND_SEMESTER = "大四下";

    public final static Integer FERSHMAN_YEAR_FIRST_SEMESTER_TYPE = 1;

    public final static Integer FERSHMAN_YEAR_SECOND_SEMESTER_TYPE = 2;

    public final static Integer SOPHOMORE_YEAR_FIRST_SEMESTER_TYPE = 3;

    public final static Integer SOPHOMORE_YEAR_SECOND_SEMESTER_TYPE = 4;

    public final static Integer JUNIOR_YEAR_FIRST_SEMESTER_TYPE = 5;

    public final static Integer JUNIOR_YEAR_SECOND_SEMESTER_TYPE = 6;

    public final static Integer SENIOR_YEAR_FIRST_SEMESTER_TYPE = 7;

    public final static Integer SENIOR_YEAR_SECOND_SEMESTER_TYPE = 8;

    public static Map<String, Integer> TERM_NAME_NUMBER_MAP = Map.of(
            FERSHMAN_YEAR_FIRST_SEMESTER, FERSHMAN_YEAR_FIRST_SEMESTER_TYPE,
            FERSHMAN_YEAR_SECOND_SEMESTER, FERSHMAN_YEAR_SECOND_SEMESTER_TYPE,
            SOPHOMORE_YEAR_FIRST_SEMESTER, SOPHOMORE_YEAR_FIRST_SEMESTER_TYPE,
            SOPHOMORE_YEAR_SECOND_SEMESTER, SOPHOMORE_YEAR_SECOND_SEMESTER_TYPE,
            JUNIOR_YEAR_FIRST_SEMESTER, JUNIOR_YEAR_FIRST_SEMESTER_TYPE,
            JUNIOR_YEAR_SECOND_SEMESTER, JUNIOR_YEAR_SECOND_SEMESTER_TYPE,
            SENIOR_YEAR_FIRST_SEMESTER, SENIOR_YEAR_FIRST_SEMESTER_TYPE,
            SENIOR_YEAR_SECOND_SEMESTER, SENIOR_YEAR_SECOND_SEMESTER_TYPE);

    public static Map<Integer, String> TERM_NUMBER_NAME_MAP = Map.of(
            FERSHMAN_YEAR_FIRST_SEMESTER_TYPE, FERSHMAN_YEAR_FIRST_SEMESTER,
            FERSHMAN_YEAR_SECOND_SEMESTER_TYPE, FERSHMAN_YEAR_SECOND_SEMESTER,
            SOPHOMORE_YEAR_FIRST_SEMESTER_TYPE, SOPHOMORE_YEAR_FIRST_SEMESTER,
            SOPHOMORE_YEAR_SECOND_SEMESTER_TYPE, SOPHOMORE_YEAR_SECOND_SEMESTER,
            JUNIOR_YEAR_FIRST_SEMESTER_TYPE, JUNIOR_YEAR_FIRST_SEMESTER,
            JUNIOR_YEAR_SECOND_SEMESTER_TYPE, JUNIOR_YEAR_SECOND_SEMESTER,
            SENIOR_YEAR_FIRST_SEMESTER_TYPE, SENIOR_YEAR_FIRST_SEMESTER,
            SENIOR_YEAR_SECOND_SEMESTER_TYPE, SENIOR_YEAR_SECOND_SEMESTER);

    public static final String EXCEL_COURSE_NAME = "课程名称";

    public static final String EXCEL_TRAINING_SCHEME_NAME = "培养规划名称";

    public static final String EXCEL_COURSE_TYPE = "课程类别";

    public static final String TRAINING_SCHEME_TYPE_NAME = "培养方案类别";

    public static final String TRAINING_SCHEME_END = "培养规划";

    public static final String TRAINING_PLAN_END = "教学计划";

    public static final String TRAINING_PROGRAM_END = "培养方案";

    /*
     * -------------------------------------------
     *          培养方案---结束
     * -------------------------------------------
     */


    /*
     * -------------------------------------------
     *          矩阵---开始
     * -------------------------------------------
     */
    public static final String SHEET_NAME_STANDARD_GRADUATION_TO_STANDARD_CULTIVATION_TARGET = "毕业标准对培养目标";
    public static final String SHEET_NAME_STANDARD_CULTIVATION_TO_STANDARD_GRADUATION = "培养标准对毕业标准";
    public static final String SHEET_NAME_TRAINING_SCHEME_TO_STANDARD_CULTIVATION = "培养规划对培养标准";

    public static final String EXCEL_NAME_STANDARD_GRADUATION_TO_STANDARD_CULTIVATION_TARGET = "毕业标准对培养目标矩阵图.xlsx";
    public static final String EXCEL_NAME_STANDARD_CULTIVATION_TO_STANDARD_GRADUATION = "培养标准对毕业标准矩阵图.xlsx";
    public static final String EXCEL_NAME_TRAINING_SCHEME_TO_STANDARD_CULTIVATION = "培养规划对培养标准矩阵图.xlsx";
    public static final String EXCEL_NAME_COURSE_TO_STANDARD_GRADUATION  = "课程对毕业要求矩阵图.xlsx";

    public static final String PARTIAL_TITLE_STANDARD_GRADUATION_TO_STANDARD_CULTIVATION_TARGET = "毕业标准对培养目标支撑关系";
    public static final String PARTIAL_TITLE_STANDARD_CULTIVATION_TO_STANDARD_GRADUATION = "培养标准对毕业标准支撑关系";
    public static final String PARTIAL_TITLE_TRAINING_SCHEME_TO_STANDARD_CULTIVATION = "培养规划对培养标准支撑关系";
    public static final String PARTIAL_TITLE_COURSE_TO_STANDARD_GRADUATION = "课程对毕业要求支撑关系";

    public static final String TOTAL_TITLE = "%s 对 %s支撑关系矩阵";

    /*
     * -------------------------------------------
     *          矩阵---结束
     * -------------------------------------------
     */

    /*
     * -------------------------------------------
     *          培养目标支持度---开始
     * -------------------------------------------
     */
    public static final List<String> filterTargetList = List.of("思想政治", "科学文化", "专业业务", "身体心理", "军事基础");

    public static final List<String> courseType = List.of("", "公共基础必修", "学科基础必修", "专业必修", "公共基础选修", "学科基础、专业选修");
    public static final List<String> subCourseName = List.of("政治理论", "自然科学", "人文科学", "军事基础");
    public static final List<Long> subCourseValue = List.of(6L, 6L, 2L, 7L);
    public static final List<String> termName = List.of("", "大一上", "大一下", "大二上", "大二下", "大三上", "大三下", "大四上", "大四下");

    /*
     * -------------------------------------------
     *          培养目标支持度---结束
     * -------------------------------------------
     */

    /*
     * -------------------------------------------
     *          首页统计---开始
     * -------------------------------------------
     */
    public static final String STATISTICS_SHEET_NAME = "导出结果";
    public static final String CREDIT_EXCEL_NAME = "学分配比图.xlsx";
    public static final String COURSE_TYPE_EXCEL_NAME = "必修与选修课比例.xlsx";
    public static final String COURSE_SELECT_EXCEL_NAME = "课程选用排行.xlsx";
    public static final String SCHEME_HOURS_EXCEL_NAME = "讲授、实践课比例.xlsx";
    public static final String SCHEME_TYPE_EXCEL_NAME = "课程分配比例.xlsx";
    public static final String KNOWLEDGE_CHECK_EXCEL_NAME = "知识点查重结果.xlsx";
    public static final String MAJOR_COUNT_EXCEL_NAME = "专业统计.xlsx";
    public static final String SCHEME_COUNT_EXCEL_NAME = "培养方案统计.xlsx";
    public static final String WORD_CLOUD_STANDARD_TARGET_EXCEL_NAME = "培养目标词云.xlsx";
    public static final String WORD_CLOUD_STANDARD_GRADUATION_EXCEL_NAME = "毕业标准词云.xlsx";

    /*
     * -------------------------------------------
     *          首页统计---结束
     * -------------------------------------------
     */

    /**
     * 导出章节表格
     * */
    public static final String[] CHAPTER_NAMES = {"章节","实验名称","项目名称"};
    public static final String CHAPTER_CONTENT = "内容";
    public static final String CHAPTER_HOUR = "课程学时数";

}
