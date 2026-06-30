package com.doinner.csys.entity.csys.po;

import com.baomidou.mybatisplus.annotation.TableName;

@TableName("t_csys_course_ref_knowledge_unit")
public class CourseRefKnowledgeUnit {
    private Long courseId;
    private Long courseUnitId;

    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

    public Long getCourseUnitId() {
        return courseUnitId;
    }

    public void setCourseUnitId(Long courseUnitId) {
        this.courseUnitId = courseUnitId;
    }
}
