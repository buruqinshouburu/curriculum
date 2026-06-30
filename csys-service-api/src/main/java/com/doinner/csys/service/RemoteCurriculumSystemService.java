package com.doinner.csys.service;

import com.doinner.common.core.domain.DataSet;
import com.doinner.csys.fallback.RemoteCurriculumSystemServiceFallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;


@FeignClient(contextId = "remoteTkService", value = "doinner-curriculum-system", fallbackFactory = RemoteCurriculumSystemServiceFallbackFactory.class)
public interface RemoteCurriculumSystemService {


    @GetMapping("/test/")
    DataSet test();
}
