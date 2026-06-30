package com.doinner.csys.service.impl;

import com.doinner.csys.service.KnowledgeCheckLogService;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

@Component
@EnableScheduling
public class SchedulingTask {


    @Resource
    private KnowledgeCheckLogService knowledgeCheckLogService;

    @Scheduled(cron = "0 10 00 * * ? ")//每天0点10分触发
    public void updateKnowledgeCheckLog() {
        List<Long> courseIds = knowledgeCheckLogService.getCourseIdsByCollegeId(null);
        knowledgeCheckLogService.knowledgePointCheck(courseIds);
    }
}
