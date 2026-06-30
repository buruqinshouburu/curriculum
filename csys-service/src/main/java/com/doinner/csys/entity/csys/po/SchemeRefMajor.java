package com.doinner.csys.entity.csys.po;

import com.baomidou.mybatisplus.annotation.TableName;

@TableName("t_csys_scheme_ref_major")
public class SchemeRefMajor {
    private Long schemeId;
    private Long majorId;

    public Long getSchemeId() {
        return schemeId;
    }

    public void setSchemeId(Long schemeId) {
        this.schemeId = schemeId;
    }

    public Long getMajorId() {
        return majorId;
    }

    public void setMajorId(Long majorId) {
        this.majorId = majorId;
    }
}
