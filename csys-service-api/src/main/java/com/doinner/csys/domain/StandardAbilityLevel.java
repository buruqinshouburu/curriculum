package com.doinner.csys.domain;

import com.doinner.common.core.domain.db.AbstractBaseEntity;
import com.doinner.common.core.domain.db.AbstractLogicalDelTreeBaseEntity;
import com.doinner.common.core.annotation.Excel;

/**
 * 能力等级对象 t_csys_std_ability_level
 *
 * @author doinner
 * @date 2023-03-28
 */
public class StandardAbilityLevel extends AbstractBaseEntity {

    private static final long serialVersionUID = -1;

    /**
     * 名称
     */
    private String name;

    /**
     * 描述
     */
    private String describe;

    /**
     * 能力id
     */
    private Long abilityId;

    /**
     * 排序
     */
    private Integer order;

    /**
     * 删除标志
     */
    private Long sysflag;

    /**
     * 是否选中 1选中，0未选中
     */
    private Integer checkflag;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescribe() {
        return describe;
    }

    public void setDescribe(String describe) {
        this.describe = describe;
    }

    public Long getAbilityId() {
        return abilityId;
    }

    public void setAbilityId(Long abilityId) {
        this.abilityId = abilityId;
    }

    public Integer getOrder() {
        return order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }

    public Long getSysflag() {
        return sysflag;
    }

    public void setSysflag(Long sysflag) {
        this.sysflag = sysflag;
    }

    public Integer getCheckflag() {
        return checkflag;
    }

    public void setCheckflag(Integer checkflag) {
        this.checkflag = checkflag;
    }
}
