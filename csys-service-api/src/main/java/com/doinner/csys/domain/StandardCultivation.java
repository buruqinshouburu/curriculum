package com.doinner.csys.domain;

import com.doinner.common.core.domain.db.AbstractLogicalDelTreeBaseEntity;
import com.doinner.common.core.domain.db.AbstractTreeBaseEntity;
import com.doinner.common.core.annotation.Excel;

/**
 * 培养标准对象 t_csys_std_cultivation
 *
 * @author doinner
 * @date 2023-03-21
 */
public class StandardCultivation extends AbstractLogicalDelTreeBaseEntity {

    private static final long serialVersionUID = 6223736132229401040L;

    /**
     * 名称
     */
    @Excel(name = "名称")
    private String name;

    /**
     * 编码
     */
    @Excel(name = "编码")
    private String code;

    /**
     * 毕业标准id
     */
    @Excel(name = "毕业标准id")
    private Long graduationId;


    /**
     * 学院(college_id)
     */
    @Excel(name = "学院(college_id)")
    private Long collegeId;

    /**
     * 专业(major)
     */
    @Excel(name = "专业(major)")
    private Long majorId;

    /**
     * 细分专业(sub_major)
     */
    @Excel(name = "细分专业(sub_major)")
    private Long subMajorId;

    /**
     * 技术指挥分类(class)    0：未分类、1：技术类、2：指挥类
     */
    @Excel(name = "技术指挥分类(class)    0：未分类、1：技术类、2：指挥类")
    private Long classId;

    private String version;


    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public Long getGraduationId() {
        return graduationId;
    }

    public void setGraduationId(Long graduationId) {
        this.graduationId = graduationId;
    }

    public void setCollegeId(Long collegeId) {
        this.collegeId = collegeId;
    }

    public Long getCollegeId() {
        return collegeId;
    }

    public void setMajorId(Long majorId) {
        this.majorId = majorId;
    }

    public Long getMajorId() {
        return majorId;
    }

    public void setSubMajorId(Long subMajorId) {
        this.subMajorId = subMajorId;
    }

    public Long getSubMajorId() {
        return subMajorId;
    }

    public void setClassId(Long classId) {
        this.classId = classId;
    }

    public Long getClassId() {
        return classId;
    }


    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}
