package com.doinner.csys.constant.audit;

/**
 * 审核实例状态枚举
 */
public enum BusinessStatusEnum {

    PENDING(0, "待审核"),
    UNDER_REVIEW(1, "审核中"),
    APPROVED(2, "审核通过"),
    REJECTED(3, "审核未通过");

    private final Integer code;
    private final String desc;

    BusinessStatusEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public Integer getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    public static BusinessStatusEnum getByCode(Integer code) {
        for (BusinessStatusEnum status : values()) {
            if (status.code.equals(code)) {
                return status;
            }
        }
        return null;
    }
}