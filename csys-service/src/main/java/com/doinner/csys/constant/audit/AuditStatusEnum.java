package com.doinner.csys.constant.audit;

/**
 * 审核实例状态枚举
 */
public enum AuditStatusEnum {

    PENDING(0, "审核中"),
    APPROVED(1, "审核通过"),
    REJECTED(2, "审核驳回"),
    CANCELLED(3, "已撤销");

    private final Integer code;
    private final String desc;

    AuditStatusEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public Integer getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    public static AuditStatusEnum getByCode(Integer code) {
        for (AuditStatusEnum status : values()) {
            if (status.code.equals(code)) {
                return status;
            }
        }
        return null;
    }
}