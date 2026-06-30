package com.doinner.csys.controller;

import com.doinner.common.core.domain.DataSet;
import com.doinner.common.core.domain.DataTable;
import com.doinner.common.core.domain.Message;
import com.doinner.common.core.utils.PageUtils;
import com.doinner.common.core.web.controller.BaseController;
import com.doinner.csys.domain.TeachingProgrammeInstance;
import com.doinner.csys.domain.TeachingProgrammeInstanceDto;
import com.doinner.csys.domain.TeachingProgrammeInstanceExtract;
import com.doinner.csys.domain.TeachingProgrammeOutline;
import com.doinner.csys.service.TeachingProgrammeService;
import com.doinner.file.api.domain.FileInfo;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 教学大纲模板Controller
 *
 * @author wzg
 * @date 2026-02-27
 */
@RestController
@RequestMapping("/teachingProgramme")
public class TeachingProgrammeController extends BaseController {

    @Autowired
    private TeachingProgrammeService teachingProgrammeService;


    @ApiOperation("新增教学大纲模板")
    @PostMapping("/outline")
    public DataSet<TeachingProgrammeOutline> addOutline(@RequestBody TeachingProgrammeOutline outline){
        return DataSet.success(teachingProgrammeService.addOutline(outline));
    }

    @ApiOperation("修改教学大纲模板")
    @PutMapping("/outline")
    public DataSet<TeachingProgrammeOutline> updateOutline(@RequestBody TeachingProgrammeOutline outline){
        return DataSet.success(teachingProgrammeService.updateOutline(outline));
    }

    @ApiOperation("查询教学大纲模板")
    @GetMapping("/outline/list")
    public DataTable outlineList(TeachingProgrammeOutline outline){
        PageUtils.startPage();
        return DataTable.success(teachingProgrammeService.outlineList(outline));
    }

    @ApiOperation("删除教学大纲模板")
    @DeleteMapping("/outline")
    public Message deleteOutlines(@RequestBody Long[] ids){
        teachingProgrammeService.deleteOutlines(ids);
        return Message.success();
    }

    @ApiOperation("根据id查询教学大纲的模板和详细信息")
    @GetMapping("/outline/{id}")
    public DataSet<TeachingProgrammeOutline> selectOutlineTree(@PathVariable("id") Long id){
        return DataSet.success(teachingProgrammeService.selectOutlineTree(id));
    }

    @ApiOperation("新增教学大纲模板详细信息")
    @PostMapping("/template")
    public DataSet<TeachingProgrammeOutline> addTemplate(@RequestBody TeachingProgrammeOutline outline){
        return DataSet.success(teachingProgrammeService.addTemplate(outline));
    }

    @ApiOperation("修改教学大纲模板详细信息")
    @PutMapping("/template")
    public DataSet<TeachingProgrammeOutline> updateTemplate(@RequestBody TeachingProgrammeOutline outline){
        return DataSet.success(teachingProgrammeService.updateTemplate(outline));
    }







    @ApiOperation("新增教学大纲实例")
    @PostMapping("/instance")
    public DataSet<TeachingProgrammeInstance> addInstance(@RequestBody TeachingProgrammeInstance instance){
        return DataSet.success(teachingProgrammeService.addInstance(instance));
    }

    @ApiOperation("修改教学大纲实例")
    @PutMapping("/instance")
    public DataSet<TeachingProgrammeInstance> updateInstance(@RequestBody TeachingProgrammeInstance instance){
        return DataSet.success(teachingProgrammeService.updateInstance(instance));
    }

    @ApiOperation("查询教学大纲实例")
    @GetMapping("/instance/list")
    public DataTable instanceList(TeachingProgrammeInstance instance){
        PageUtils.startPage();
        return DataTable.success(teachingProgrammeService.instanceList(instance));
    }

    @ApiOperation("根据id查询教学大纲的模板和详细信息")
    @GetMapping("/instance/{id}")
    public DataSet<TeachingProgrammeInstance> selectInstance(@PathVariable("id") Long id){
        return DataSet.success(teachingProgrammeService.selectInstance(id));
    }

    @ApiOperation("删除教学大纲实例")
    @DeleteMapping("/instance")
    public Message deleteInstance(@RequestBody Long[] ids){
        teachingProgrammeService.deleteInstance(ids);
        return Message.success();
    }

    @ApiOperation("新增教学大纲实例详情数据")
    @PostMapping("/instance/details")
    public DataSet<TeachingProgrammeInstance> instanceAddDetails(@RequestBody TeachingProgrammeInstance instance){
        return DataSet.success(teachingProgrammeService.instanceAddDetails(instance));
    }

    @ApiOperation("修改教学大纲实例详情数据")
    @PutMapping("/instance/details")
    public DataSet<TeachingProgrammeInstance> instanceUpdateDetails(@RequestBody TeachingProgrammeInstance instance){
        return DataSet.success(teachingProgrammeService.instanceUpdateDetails(instance));
    }

    @ApiOperation("教学大纲文件生成")
    @GetMapping("/createWord/{instanceId}")
    public DataSet<TeachingProgrammeInstance> createWord(@PathVariable("instanceId") Long instanceId){
        return DataSet.success(teachingProgrammeService.createWord(instanceId));
    }

    @ApiOperation("教学大纲下载/预览")
    @GetMapping("/downloadAndPreView/{fileId}")
    public DataSet<FileInfo> downloadAndPreView(@PathVariable("fileId") String fileId){
        return DataSet.success(teachingProgrammeService.downloadAndPreView(fileId));
    }


    @ApiOperation("ai教学大纲抽取")
    @PostMapping("/extract/ai")
    public DataSet extractTeachingProgramme(@RequestBody TeachingProgrammeInstanceExtract teachingProgrammeInstanceExtract){
       return DataSet.success(teachingProgrammeService.extractTeachingProgramme(teachingProgrammeInstanceExtract));
    }

    @ApiOperation("教学大纲抽取内容新增")
    @PostMapping("/extract")
    public DataSet<TeachingProgrammeInstanceExtract> addTeachingProgramme(@RequestBody TeachingProgrammeInstanceExtract teachingProgrammeInstanceExtract){
        return DataSet.success(teachingProgrammeService.addTeachingProgramme(teachingProgrammeInstanceExtract));
    }

    @ApiOperation("教学大纲抽取内容修改")
    @PutMapping("/extract")
    public DataSet<TeachingProgrammeInstanceExtract> updateTeachingProgramme(@RequestBody TeachingProgrammeInstanceExtract teachingProgrammeInstanceExtract){
        return DataSet.success(teachingProgrammeService.updateTeachingProgramme(teachingProgrammeInstanceExtract));
    }

    @ApiOperation("教学大纲抽取内容删除")
    @DeleteMapping("/extract")
    public Message deleteTeachingProgramme(@RequestBody List<Long> ids){
        teachingProgrammeService.deleteTeachingProgramme(ids);
        return Message.success();
    }

    @ApiOperation("教学大纲查询")
    @GetMapping("/extract")
    public DataTable<TeachingProgrammeInstanceExtract> selectExtractTeachingProgramme(TeachingProgrammeInstanceExtract teachingProgrammeInstanceExtract){
        return DataTable.success(teachingProgrammeService.selectExtractTeachingProgramme(teachingProgrammeInstanceExtract));
    }


    @ApiOperation("教学大纲复制")
    @PostMapping("/copy")
    public Message copyInstance(@RequestBody TeachingProgrammeInstanceDto instance){
        return teachingProgrammeService.copyInstance(instance);
    }

}
