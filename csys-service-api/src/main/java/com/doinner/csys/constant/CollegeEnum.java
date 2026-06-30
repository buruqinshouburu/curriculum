package com.doinner.csys.constant;

import com.doinner.common.core.utils.StringUtils;

public enum CollegeEnum {
    // 定义枚举常量，code和name
    LI_COLLEGE("01", "理学院"),
    JS_COLLEGE("02", "计算机学院"),
    DZ_COLLEGE("03", "电子科学学院"),
    QY_COLLEGE("04", "前沿交叉学科学院"),
    ZN_COLLEGE("05", "智能科学学院"),
    XT_COLLEGE("06", "系统工程学院"),
    KT_COLLEGE("07", "空天科学学院"),
    WG_COLLEGE("08", "外国语学院"),
    DD_COLLEGE("09", "电子对抗学院"),
    HY_COLLEGE("10", "气象海洋学院"),
    JZ_COLLEGE("11", "军政基础教育学院"),
    X_COLLEGE("00", "校本级"),
    TSG_COLLEGE("12", "图书馆");

    private final String code;
    private final String name;

    // 构造函数
    CollegeEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }

    // 获取code
    public String getCode() {
        return code;
    }

    // 获取name
    public String getName() {
        return name;
    }

    // 根据name获取对应的code
    public static String getCodeByName(String name) {
        if (StringUtils.isNotBlank(name)){
            for (CollegeEnum college : CollegeEnum.values()) {
                if (college.getName().equals(name)) {
                    return college.getCode();
                }
            }
        }
        return "00"; // 如果没找到返回null
    }

    // 根据code获取对应的name
    public static String getNameByCode(String code) {
        for (CollegeEnum college : CollegeEnum.values()) {
            if (college.getCode().equals(code)) {
                return college.getName();
            }
        }
        return null; // 如果没找到返回null
    }

    // 根据name获取对应的枚举对象
    public static CollegeEnum getCollegeByName(String name) {
        for (CollegeEnum college : CollegeEnum.values()) {
            if (college.getName().equals(name)) {
                return college;
            }
        }
        return null; // 如果没找到返回null
    }

    // 根据code获取对应的枚举对象
    public static CollegeEnum getCollegeByCode(String code) {
        for (CollegeEnum college : CollegeEnum.values()) {
            if (college.getCode().equals(code)) {
                return college;
            }
        }
        return null; // 如果没找到返回null
    }

    @Override
    public String toString() {
        return "CollegeEnum{" +
                "code='" + code + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}