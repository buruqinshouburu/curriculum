package com.doinner.csys.service;

import com.doinner.csys.domain.vo.TeachingPlanTextbookExportVo;

import java.util.List;

public interface TeachingPlanTextbookExportService {
    List<TeachingPlanTextbookExportVo> listForExport(Long planId);
}
