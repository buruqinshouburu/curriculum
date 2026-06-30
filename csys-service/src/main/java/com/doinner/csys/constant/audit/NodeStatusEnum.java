package com.doinner.csys.constant.audit;

/**
 * 审核节点状态枚举
 */
public enum NodeStatusEnum {

    PENDING(0, "待审核"),
    APPROVED(1, "已通过"),
    REJECTED(2, "已驳回"),
    SKIPPED(3, "已跳过");

    private final Integer code;
    private final String desc;

    NodeStatusEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public Integer getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    public static NodeStatusEnum getByCode(Integer code) {
        for (NodeStatusEnum status : values()) {
            if (status.code.equals(code)) {
                return status;
            }
        }
        return null;
    }
}