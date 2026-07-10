package com.agileai.dataparser.controller;

import com.agileai.dataparser.service.TempCourseService;
import com.agileai.dataparser.service.TransferDataService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author wzg
 * @date 2023/3/28 10:15
 */
@RestController
@RequestMapping("/tempCurriculum")
@Api(value = "/tempCurriculum", tags = "tempCurriculum-controller")
public class TempCourseController {
    @Autowired
    private TransferDataService transferDataService;

    @Autowired
    private TempCourseService courseService;
    /**
     * 批量更新课程数据
     */
    @GetMapping("/batch")
    public void batchCourse(){
        transferDataService.transferPlan();
    }

    @GetMapping("/updateBeforeAfter")
    public void updateCourse(){
        courseService.batchUpdateCourse();
    }

    @GetMapping("/updateSchedule")
    public void updateSchedule(){
        transferDataService.updateSchedule();
    }

    @GetMapping("/kg/data")
    public void kgData(){
        transferDataService.transferToKgData();
    }

}
