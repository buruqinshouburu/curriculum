package com.doinner.csys.constant;

import com.doinner.common.core.utils.StringUtils;

public enum PropertyEnum {
    // 定义枚举常量，code和name
    ZZLL_COLLEGE("11", "政治理论"),
    JSJC_COLLEGE("12", "军事基础"),
    RWKX_COLLEGE("13", "科学文化"),
    JCKX_COLLEGE("13", "基础科学"),
    RWSH_COLLEGE("13", "人文与社会科学"),
    RGZN_COLLEGE("13", "人工智能与信息技术"),
      WY_COLLEGE("13", "外语"),
    ZYDL_COLLEGE("20", "学科基础课程"),
    SJXL_COLLEGE("30", "专业课程"),

    ;

    private final String code;
    private final String name;

    // 构造函数
    PropertyEnum(String code, String name) {
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
            for (PropertyEnum college : PropertyEnum.values()) {
                if (college.getName().equals(name)) {
                    return college.getCode();
                }
            }
        }
        return "30"; // 如果没找到返回null
    }


    @Override
    public String toString() {
        return "CollegeEnum{" +
                "code='" + code + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}