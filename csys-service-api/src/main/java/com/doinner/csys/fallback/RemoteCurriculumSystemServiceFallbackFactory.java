package com.doinner.csys.fallback;

import com.doinner.common.core.domain.DataSet;
import com.doinner.csys.service.RemoteCurriculumSystemService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;


@Component
public class RemoteCurriculumSystemServiceFallbackFactory implements FallbackFactory <RemoteCurriculumSystemService> {

    private static final Logger log = LoggerFactory.getLogger(RemoteCurriculumSystemServiceFallbackFactory.class);


    public RemoteCurriculumSystemService create(Throwable throwable) {
        log.error("培养方案系统服务调用失败:{}", throwable.getMessage());
        return new RemoteCurriculumSystemService() {

            @Override
            public DataSet test() {
                return DataSet.error("培养方案系统服务调用失败"+throwable.getMessage());
            }
        };
    }
}
