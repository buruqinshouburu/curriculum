package com.doinner.csys.io.service;

import com.doinner.csys.domain.vo.CourseExportVo;
import com.doinner.csys.domain.vo.GraduationExcelVo;
import com.doinner.csys.domain.vo.MatrixVo;
import com.doinner.csys.entity.csys.model.CourseChooseStatusModel;

import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.util.List;

public interface ExportService {

    List<CourseExportVo> courseExportConvert(List<Long> courseIds);

    void courseDataExportConvert(HttpServletResponse response, List<Long> courseIds);

    MatrixVo assembleMatrix(Long id, Integer type);

    void exportMatrix(HttpServletResponse response, Long id, Integer type) throws UnsupportedEncodingException;

    /**
     * 导出课程被选用情况表
     *
     * @param response        响应
     * @param sourceCourseIds 源课程id集合(通过 source_id 查询被选用课程)
     */
    void exportCourseChooseStatus(HttpServletResponse response, List<Long> sourceCourseIds);

    List<CourseChooseStatusModel> buildCourseChooseStatusModels(List<Long> sourceCourseIds);

    /**
     * 导出毕业要求(包含子层级内容)
     *
     * @param response 响应
     * @param ids      毕业要求id集合
     */
    void exportGraduation(HttpServletResponse response, List<Long> ids);

    /**
     * 构建毕业要求导出数据(包含子层级内容)
     *
     * @param ids 毕业要求id集合
     * @return 导出数据集合
     */
    List<GraduationExcelVo> buildGraduationExcelVos(List<Long> ids);
}
