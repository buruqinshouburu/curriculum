package com.doinner.csys.controller;


import com.doinner.common.core.domain.DataSet;
import com.doinner.common.core.domain.Message;
import com.doinner.csys.domain.vo.CourseKnowledgeUnitVo;
import com.doinner.csys.domain.vo.CourseKnowledgeVo;
import com.doinner.csys.service.IKnowledgeUnitService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 知识单元控制器
 * 功能说明：
 * 1. 新增知识体系：在课程列表中点击"建设知识体系"按钮，进入结构树添加
 *    新增时，前端传全部数据（id都为null），后端统一保存
 * 2. 修改知识体系：前端传全部数据（包含已有数据的id），后端根据id判断是新增还是更新
 * 3. 查询：根据课程Id进行查询
 */
@RestController
@RequestMapping("/courseKnowledge")
@Api(value = "知识单元控制器", tags = "知识单元管理")
public class KnowledgeUnitController {

    @Autowired
    private IKnowledgeUnitService knowledgeUnitService;

    /**
     * 根据课程ID查询知识单元列表（包含知识点）
     * 查询是根据课程Id进行的查询
     *
     * @param courseId 课程ID
     * @return 知识单元列表
     */
    @GetMapping("/list")
    @ApiOperation("查询知识单元列表")
    public DataSet<List<CourseKnowledgeUnitVo>> listByCourseId(@RequestParam Long courseId) {
        List<CourseKnowledgeUnitVo> list = knowledgeUnitService.listByCourseId(courseId);
        return DataSet.success(list);
    }

    /**
     * 批量保存知识单元（包含知识点）
     * 新增时：前端传全部数据，id都为null
     * 修改时：前端传全部数据，包含已有数据的id
     *
     * @return 保存响应
     */
    @PostMapping("/saveBatch")
    @ApiOperation("批量保存知识单元（支持新增和修改）")
    public Message saveBatch(@RequestBody CourseKnowledgeVo courseKnowledgeVo) {
        knowledgeUnitService.saveBatch(courseKnowledgeVo);
        return Message.success();
    }

    /**课程知识单元知识点模板下载
     * @param
     * @return
     */
    @RequestMapping("/template")
    @ApiOperation("课程知识单元知识点模板下载")
    public void exportTemplate(HttpServletResponse response) {
        knowledgeUnitService.exportTemplate(response);
    }

    /**
     * 导入知识体系
     * @param file
     * @return
     */
    @PostMapping("/import")
    @ApiOperation("导入知识体系 type:1=追加，2=覆盖")
    public Message exportInTemplate(MultipartFile file, Long courseId, Integer type) {
        return  knowledgeUnitService.exportInTemplate(file,courseId,type);
    }


}
