package com.doinner.csys.controller;

import com.doinner.common.core.domain.DataSet;
import com.doinner.common.core.domain.DataTable;
import com.doinner.common.core.domain.Message;
import com.doinner.common.core.utils.PageUtils;
import com.doinner.csys.domain.SourceDomain;
import com.doinner.csys.domain.SourcePoint;
import com.doinner.csys.domain.SourceUnit;
import com.doinner.csys.domain.vo.KnowledgeDomainReferenceVo;
import com.doinner.csys.domain.vo.SourceDomainTreeVo;
import com.doinner.csys.service.KnowledgeSourceService;
import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@RestController
@RequestMapping("/knowledgeSource")
@Api(value = "/knowledgeSource", tags = "knowledgeSource-controller")

/**
 * 来源理论知识体系
 */
public class KnowledgeSourceController {

    @Resource
    private KnowledgeSourceService knowledgeSourceService;

    /**
     * 查询源知识领域列表
     */
    @GetMapping("/list")
    public DataTable list(SourceDomain sourceDomain) {
        PageUtils.startPage();
        List<SourceDomain> list = knowledgeSourceService.selectSourceDomainList(sourceDomain);
        return DataTable.success(list);
    }

    @GetMapping("/tree")
    public DataSet treeKnowledge(SourceDomain sourceDomain) {
        return DataSet.success(knowledgeSourceService.treeKnowledge(sourceDomain));
    }

    @GetMapping("/children/byDomainId")
    public DataSet childrenKnowledgeByDomainId(@RequestParam Long domainId) {
        return DataSet.success(knowledgeSourceService.childrenKnowledgeByDomainId(domainId));
    }

    @GetMapping("/children/byCourseId")
    public DataSet childrenKnowledgeByCourseId(@RequestParam Long courseId) {
        return DataSet.success(knowledgeSourceService.childrenKnowledgeByCourseId(courseId));
    }

    /**
     * 获取源知识领域详细信息
     */
    @GetMapping(value = "/{id}")
    public DataSet getInfo(@PathVariable("id") Long id) {
        return DataSet.success(knowledgeSourceService.selectSourceDomainById(id));
    }

    /**
     * 新增源知识领域
     */
    @PostMapping
    public DataSet add(@RequestBody SourceDomain sourceDomain) {
        return DataSet.success(knowledgeSourceService.insertSourceDomain(sourceDomain));
    }

    /**
     * 修改源知识领域
     */
    @PutMapping
    public DataSet edit(@RequestBody SourceDomain sourceDomain) {
        return DataSet.success(knowledgeSourceService.updateSourceDomain(sourceDomain));
    }

    /**
     * 删除源知识领域
     */
    @DeleteMapping
    public Message remove(@RequestBody Long[] ids) {
        knowledgeSourceService.deleteSourceDomainByIds(ids);
        return Message.success();
    }


    /**
     * 新增，修改知识领域下面的知识单元和知识点
     */
    @PostMapping("source/unitAndPoint")
    public DataSet addOrUpdateSourceUnitAndPoint(@RequestBody SourceDomain sourceDomain) {
        return DataSet.success(knowledgeSourceService.addOrUpdateSourceUnitAndPoint(sourceDomain));
    }


    /**
     * 导出知识体系
     * @param response
     * @param ids
     */
    @PostMapping("/export")
    public void exportOutTemplate(HttpServletResponse response, @RequestBody(required = false) List<Long> ids) {
        knowledgeSourceService.exportOutTemplate(response,ids);
    }

    /**
     * 导入知识体系
     * @param file
     * @return
     */
    @PostMapping("/import")
    public Message exportInTemplate(MultipartFile file,Long collegeId,Integer type,Long categoryId,Long majorId,String version) {
        return  knowledgeSourceService.exportInTemplate(file,collegeId,type,categoryId,majorId,version);
    }

    /**
     * @param
     * @return
     */
    @RequestMapping("/template")
    public void exportTemplate(HttpServletResponse response) {
        knowledgeSourceService.exportTemplate(response);
    }



    /**
     * 新增源知识单元
     */
    @PostMapping("/unit")
    public DataSet addUnit(@RequestBody SourceUnit unit) {
        return DataSet.success(knowledgeSourceService.addUnit(unit));
    }

    /**
     * 修改源知识单元
     */
    @PutMapping("/unit")
    public DataSet editUnit(@RequestBody SourceUnit unit) {
        return DataSet.success(knowledgeSourceService.editUnit(unit));
    }

    /**
     * 删除源知识单元
     */
    @DeleteMapping("/unit")
    public Message removeUnit(@RequestBody Long[] ids) {
        knowledgeSourceService.removeUnit(ids);
        return Message.success();
    }

    /**
     * 新增源知识点
     */
    @PostMapping("/point")
    public DataSet addPoint(@RequestBody SourcePoint point) {
        return DataSet.success(knowledgeSourceService.addPoint(point));
    }

    /**
     * 修改源知识点
     */
    @PutMapping("/point")
    public DataSet editPoint(@RequestBody SourcePoint point) {
        return DataSet.success(knowledgeSourceService.editPoint(point));
    }

    /**
     * 删除源知识点
     */
    @DeleteMapping("/point")
    public Message removePoint(@RequestBody Long[] ids) {
        knowledgeSourceService.removePoint(ids);
        return Message.success();
    }

    /**
     * 公共知识调用
     * @param referenceVo
     * @return
     */
    @PostMapping("/reference")
    public Message referenceDomain(@RequestBody KnowledgeDomainReferenceVo referenceVo) {
        knowledgeSourceService.referenceDomain(referenceVo);
        return Message.success();
    }
}
