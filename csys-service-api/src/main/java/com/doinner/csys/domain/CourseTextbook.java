package com.doinner.csys.domain;

import com.doinner.common.core.domain.db.AbstractDoinnerLogicalDelBaseEntity;
import com.doinner.common.core.annotation.Excel;

/**
 * 课程教材对象 t_csys_course_textbook
 *
 * @author doinner
 * @date 2023-03-15
 */
public class CourseTextbook extends AbstractDoinnerLogicalDelBaseEntity {

    private static final long serialVersionUID = -1613454583945129270L;

    /**
     * 课程id(course_id)
     */
    @Excel(name = "课程id(course_id)")
    private Long courseId;

    /**
     * 名称(name)
     */
    @Excel(name = "名称(name)")
    private String name;

    /**
     * 类型(type)
     */
    @Excel(name = "类型(type)")
    private Integer type;

    /**
     * 作者(author)
     */
    @Excel(name = "作者(author)")
    private String author;

    /**
     * 出版社名称(press_name)
     */
    @Excel(name = "出版社名称(press_name)")
    private String pressName;

    /**
     * 出版时间版次(press_version)
     */
    @Excel(name = "出版时间版次(press_version)")
    private String pressVersion;

    /**
     * 印刷时间(press_time)
     */
    @Excel(name = "印刷时间(press_time)")
    private String pressTime;


    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

    public Long getCourseId() {
        return courseId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getType() {
        return type;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getAuthor() {
        return author;
    }

    public void setPressName(String pressName) {
        this.pressName = pressName;
    }

    public String getPressName() {
        return pressName;
    }

    public void setPressVersion(String pressVersion) {
        this.pressVersion = pressVersion;
    }

    public String getPressVersion() {
        return pressVersion;
    }

    public void setPressTime(String pressTime) {
        this.pressTime = pressTime;
    }

    public String getPressTime() {
        return pressTime;
    }


}
