package com.doinner.csys.io.controller;

import com.doinner.common.core.domain.Message;
import com.doinner.csys.exception.FileException;
import com.doinner.csys.io.service.ImportService;
import com.doinner.csys.service.StandardService;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping
public class ImportController {

    @Resource
    private ImportService importService;

    @ApiOperation("课程导入")
    @PostMapping("/course/import")
    public Message importCourse(@RequestBody MultipartFile file, Long collegeId){
        importService.courseImport(file, collegeId);
        return Message.success();
    }

    @ApiOperation("培养目标导入")
    @PostMapping("/std/standardCultivationTarget/import")
    public Message importStandardCultivationTarget(@RequestBody MultipartFile file, Long schemeId){
        importService.importStandardCultivationTarget(file,schemeId);
        return Message.success();
    }

    @ApiOperation("毕业标准导入")
    @PostMapping("/std/standardGraduation/import")
    public Message importStandardGraduation(@RequestBody MultipartFile file, Long collegeId,Long categoryId,Long majorId,String version,String graduationType,Integer type,Long schemeId,String educationLevel){
        importService.importStandardGraduation(file, collegeId,categoryId,majorId,version,graduationType,type,schemeId,educationLevel);
        return Message.success();
    }

    @ApiOperation("毕业标准培养目标关联导入")
    @PostMapping("/std/standardGraduation/ref/cultivationTarget/import")
    public Message importStandardGraduationRefCultivationTarget(@RequestBody MultipartFile file, Long collegeId,Long graduationId){
        importService.importStandardGraduationRefCultivationTarget(file, collegeId,graduationId);
        return Message.success();
    }

    @ApiOperation("培养标准导入")
    @PostMapping("/std/standardCultivation/import")
    public Message importStandardCultivation(@RequestBody MultipartFile file, Long collegeId){
        importService.importStandardCultivation(file, collegeId);
        return Message.success();
    }

    @ApiOperation("培养标准毕业标准关联导入")
    @PostMapping("/std/cultivation/ref/Graduation/import")
    public Message importCultivationRefGraduation(@RequestBody MultipartFile file, Long collegeId,Long cultivationId){
        importService.importCultivationRefGraduation(file, collegeId,cultivationId);
        return Message.success();
    }

    @ApiOperation("培养方案导入")
    @PostMapping("/std/trainingScheme/import")
    public Message importTrainingScheme(@RequestBody MultipartFile file, Long collegeId){
        importService.importTrainingScheme(file, collegeId);
        return Message.success();
    }

    @ApiOperation("培养方案关联导入")
    @PostMapping("/std/trainingScheme/ref/import")
    public Message importTrainingSchemeRef(@RequestBody MultipartFile file, Long collegeId, Long themeId){
        importService.importTrainingSchemeRef(file, collegeId, themeId);
        return Message.success();
    }

    @ApiOperation("能力素质导入")
    @PostMapping("/std/ability/import")
    public Message importAbility(@RequestBody MultipartFile file, Long collegeId){
        importService.importAbility(file, collegeId);
        return Message.success();
    }

    @ApiOperation("下载模板")
    @GetMapping("/downloadTemp")
    public void downloadTemp(int type, HttpServletResponse response){
        response.setContentType("application/x-download");
        importService.getTemplete(type,response);
    }

}
