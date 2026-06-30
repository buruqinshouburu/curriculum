package com.doinner.csys.service;

import com.doinner.common.core.domain.DataSet;
import com.doinner.common.core.domain.Message;
import com.doinner.csys.domain.StandardGraduation;
import com.doinner.csys.domain.vo.CourseBoundGraduationVo;
import com.doinner.csys.domain.vo.CourseChooseStatusVo;
import com.doinner.csys.domain.vo.CourseKnowledgeViewVo;
import com.doinner.csys.domain.vo.CourseTemplateVo;
import com.doinner.csys.entity.csys.model.CourseChooseStatusModel;

import java.util.List;
import java.util.Map;

public interface CourseService {
    void removeStorageCourse(List<Long> ids);

    void removeInvokeCourse(List<Long> ids);

    void disableCourse(Long courseId);

    void startCourse(Long courseId);

    Message courseInvoke(CourseTemplateVo courseTemplateVo);

    Message boundGraduation(CourseBoundGraduationVo courseBoundGraduationVo);

    List<CourseKnowledgeViewVo> viewCourseKnowledgeList(Long trainingSchemeId);

    Map<String,List<Long>> courseGraduation(Long courseId);

    List<StandardGraduation> viewCourseGraduation(Long courseId);

    /**
     * 查询单个源课程的被选用情况(供前端自行渲染表格)
     *
     * @param sourceCourseId 源课程id
     * @return 被选用情况模型(课程名称/编号 + 各选用组合的学期学时)
     */
    CourseChooseStatusModel getCourseChooseStatus(Long sourceCourseId);

}
