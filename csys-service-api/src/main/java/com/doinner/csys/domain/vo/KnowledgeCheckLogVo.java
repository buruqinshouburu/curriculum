package com.doinner.csys.domain.vo;

import com.doinner.csys.domain.KnowledgeCheckLog;
import org.springframework.util.DigestUtils;

import java.nio.charset.StandardCharsets;

public class KnowledgeCheckLogVo extends KnowledgeCheckLog {


    public KnowledgeCheckLogVo(Long sourceCourseId, Long targetCourseId) {
        super.setSourceCourseId(sourceCourseId);
        super.setTargetCourseId(targetCourseId);
    }

    public KnowledgeCheckLogVo(KnowledgePointVo source, KnowledgePointVo target) {
        super.setSourceCourseId(source.getCourseId());
        super.setSourceCourseName(source.getCourseName());
        super.setSourceUnitId(source.getUnitId());
        super.setSourceUnitName(source.getUnitName());
        super.setSourcePointId(source.getId());
        super.setSourcePointName(source.getName());

        super.setTargetCourseId(target.getCourseId());
        super.setTargetCourseName(target.getCourseName());
        super.setTargetPointId(target.getId());
        super.setTargetPointName(target.getName());
        super.setTargetUnitId(target.getUnitId());
        super.setTargetUnitName(target.getUnitName());
        super.setStart(0);
        String cId = source.getCourseId() + "," + source.getUnitId() + "," + source.getId() +
                ":" + target.getCourseId() + "," + target.getUnitId() + "," + target.getId();
        String md5 = DigestUtils.md5DigestAsHex(cId.getBytes(StandardCharsets.UTF_8));
        super.setId(md5);

    }



}
