package com.doinner.csys.constant;

public class ConstantTrainingScheme {

    /**
     * 课程类型:1:公共基础必修课程
     * 2:学科基础必修课程
     * 3:专业必修课程
     * 4:公共基础选修课程
     * 5:学科基础、专业选修课程
     */
    public final static Integer PUBLIC_REQUIRED_COURSE = 1;
    public final static Integer SUBJECT_REQUIRED_COURSE = 2;
    public final static Integer SPECIALITY_REQUIRED_COURSE = 3;
    public final static Integer PUBLIC_ELECTIVE_COURSE = 4;
    public final static Integer SUBJECT_ELECTIVE_COURSE = 5;

    /**
     * 课程类型:
     * 1:理论课程
     * 2:实践课程
     * 3:理论+实践课程
     */
    public static final Integer CUR_TYPE_THEORY = 1;
    public static final Integer CUR_TYPE_PRACTICE = 2;
    public static final Integer CUR_TYPE_THEORY_PRACTICE = 3;
     /**
     * 课程类型 必修 选修 任选 COURSE_ATTR
     */
    public final static String COMPULSORY_COURSE = "1";    // 必修课程类型
    public final static String ELECTIVE_COURSE = "2";     // 选修课程类型
    public final static String OPTIONAL_COURSE = "3";     // 任选课程类型
     /**
     * 课程种类 课程 训练 实践 TYPE
     */
    public final static String COURSE_TYPE = "1";        // 课程类型标识
    public final static String TRAINING_TYPE = "2";      // 训练课目类型
    public final static String PRACTICAL_TYPE = "4";     // 实践项目类型
}
