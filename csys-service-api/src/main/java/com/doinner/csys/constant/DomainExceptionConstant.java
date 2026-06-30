package com.doinner.csys.constant;

import com.doinner.common.core.constant.ExceptionConstants;

public class DomainExceptionConstant extends ExceptionConstants {

    /**
     * -------------------------------------------
     *          导入导出文件---开始
     * -------------------------------------------
     */
    public static final String FILE_NOT_EXISTS = "文件不存在!";

    public static final String FILE_IS_NOT_EXCEL = "不是Excel文件!";

    public static final String EXCEL_FILE_FORMAT_WRONG = "Excel文件格式错误!";

    public static final String EXCEL_HAS_NO_SHEET = "Excel文件中没有工作表";

    public static final String EXCEL_IS_EMPTY = "Excel文件中没有内容";

    public static final String COURSE_NAME_CAN_NOT_NULL = "课程名称不能为空!";

    public static final String COURSE_TYPE_IS_WRONG = "课程类别错误!";

    public static final String COURSE_TYPE_IS_EMPTY = "课程%s的课程类型为空!";

    public static final String COURSE_TYPE_NOT_EXISTS = "课程%s的课程类型:%s不存在!";

    public static final String COURSE_CODE_NOT_EXISTS = "课程%s的课程编号不存在!";

    public static final String COURSE_CODE_HAS_EXISTS = "课程%s、%s的课程编号:%s重复!";

    public static final String COURSE_CODE_IS_EXISTS = "课程编号%s已存在!";

    public static final String TSTANDARD_HAVENT_MAJOR = "%s没有配置专业!";

    public static final String MAJOR_OF_STANDARD_NOT_EXISTS = "专业%s不存在!";

    public static final String SUB_MAJOR_OF_STANDARD_NOT_EXISTS = "细分专业%s不存在!";

    public static final String CLASS_OF_STANDARD_NOT_EXISTS = "技术指挥分类:%s不存在!";

    public static final String TRAINING_SCHEME_NOT_EXISTS = "培养方案不存在!";

    public static final String TRAINING_SCHEME_COURSE_NOT_EXISTS = "培养方案没有设置课程!";

    public static final String CULTIVATION_NOT_EXISTS = "培养标准不存在!";

    public static final String CULTIVATION_TARGET_NOT_EXISTS = "培养目标不存在!";

    public static final String GRADUATION_NOT_EXISTS = "毕业标准不存在!";

    public static final String GRADUATION_NOT_REF_CULTIVATION_TARGET = "毕业标准未关联培养目标!";

    public static final String CULTIVATION_NOT_REF_GRADUATION = "培养标准未关联毕业标准!";

    public static final String TRAINING_SCHEME_NOT_REF_CULTIVATION = "培养规划未关联培养标准!";

    public static final String CATEGORY_OF_TRAINING_SCHEME_NOT_REF_CULTIVATION = "培养规划分类%s不存在!";

    public static final String CATEGORY_OF_TRAINING_SCHEME_IS_EMPTY = "培养规划%s没有培养方案类别!";

    public static final String NAME_OF_STANDARD_CULTIVATION_CAN_NOT_EMPTY = "培养标准名称不能为空!";

    public static final String STANDARD_CULTIVATION_NOT_EXISTS = "培养标准%s不存在!";

    public static final String REF_COUESE_STANDARD_CAN_NOT_REF = "课程%s对应的培养标准未配置一级、二级和三级标准";

    public static final String STANDARD_CULTIVATION_ERROR = "培养标准数据错误";

    public static final String STANDRAD_ID_CAN_NOT_EMPTY = "培养规划id不能为空";

    public final static String DATA_IMPORT_TEMPLATE_ERROR = "模板不正确或模板数据为空";


    /**
     * -------------------------------------------
     *          导入导出文件---结束
     * -------------------------------------------
     */

    public static final String PID_MUST_NOT_NULL = "pId不能为空";
}
