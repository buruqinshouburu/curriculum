package com.doinner.csys.service;

import com.doinner.common.core.domain.Message;
import com.doinner.csys.domain.vo.CourseKnowledgeUnitVo;
import com.doinner.csys.domain.vo.CourseKnowledgeVo;
import org.apache.poi.ss.usermodel.Workbook;
import org.elasticsearch.action.delete.DeleteResponse;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * 知识单元服务接口
 */
public interface IKnowledgeUnitService {

    /**
     * 根据课程ID查询知识单元列表（包含知识点）
     *
     * @param courseId 课程ID
     * @return 知识单元列表
     */
    List<CourseKnowledgeUnitVo> listByCourseId(Long courseId);

    /**
     * 批量保存知识单元（包含知识点）
     *
     * @param courseKnowledgeVo 保存请求列表
     * @return 保存响应
     */
    void saveBatch(CourseKnowledgeVo courseKnowledgeVo);

    void exportTemplate(HttpServletResponse response);

    Message exportInTemplate(MultipartFile file, Long courseId, Integer type);
}
