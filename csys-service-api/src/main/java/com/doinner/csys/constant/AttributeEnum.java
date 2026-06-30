package com.doinner.csys.constant;

import com.doinner.common.core.utils.StringUtils;

public enum AttributeEnum {
    // 定义枚举常量，code和name
    BX_COLLEGE("1", "必修"),
    XX_COLLEGE("2", "限选"),
    RX_COLLEGE("3", "任选"),
    ;

    private final String code;
    private final String name;

    // 构造函数
    AttributeEnum(String code, String name) {
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
        if(StringUtils.isNotBlank(name)) {
            for (AttributeEnum college : AttributeEnum.values()) {
                if (college.getName().equals(name)) {
                    return college.getCode();
                }
            }
        }
        return "1"; // 如果没找到返回null
    }

    @Override
    public String toString() {
        return "CollegeEnum{" +
                "code='" + code + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}