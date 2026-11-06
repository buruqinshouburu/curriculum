package com.doinner.csys.service.impl;

import com.doinner.common.core.domain.DataSet;
import com.doinner.csys.dao.TeachingPlanTextbookMapper;
import com.doinner.csys.domain.vo.TeachingPlanTextbookExportVo;
import com.doinner.csys.service.TeachingPlanTextbookExportService;
import com.doinner.system.domain.entity.SysDictData;
import com.doinner.system.service.DoinnerDictDataService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class TeachingPlanTextbookExportServiceImpl implements TeachingPlanTextbookExportService {
    @Resource
    private TeachingPlanTextbookMapper textbookMapper;
    @Resource
    private DoinnerDictDataService dictDataService;

    @Override
    public List<TeachingPlanTextbookExportVo> listForExport(Long planId) {
        if (planId == null || planId <= 0) {
            throw new IllegalArgumentException("导出教材必须指定有效的教学计划ID");
        }
        List<TeachingPlanTextbookExportVo> rows = textbookMapper.selectForExport(planId);
        if (rows.isEmpty()) {
            return rows;
        }
        Map<String, String> attrs = dictionary("cur_course_attribute");
        Map<String, String> natures = dictionary("sys_textbook_nature");
        Map<String, String> methods = dictionary("sys_publication_method");
        Map<String, String> sources = dictionary("sys_textbook_source");
        for (TeachingPlanTextbookExportVo row : rows) {
            row.setCourseAttr(label(attrs, row.getCourseAttr(), "cur_course_attribute"));
            row.setMaterialNature(label(natures, row.getMaterialNature(), "sys_textbook_nature"));
            row.setPublishMethod(label(methods, row.getPublishMethod(), "sys_publication_method"));
            row.setTextbookSource(label(sources, row.getTextbookSource(), "sys_textbook_source"));
        }
        return rows;
    }

    private Map<String, String> dictionary(String type) {
        DataSet<List<SysDictData>> response = dictDataService.dictType(type);
        if (response == null || response.getData() == null) {
            return Collections.emptyMap();
        }
        Map<String, String> labels = new HashMap<>();
        for (SysDictData entry : response.getData()) {
            if (entry == null || StringUtils.isBlank(entry.getDictLabel())) {
                continue;
            }
            String label = entry.getDictLabel().trim();
            labels.put(StringUtils.trimToEmpty(entry.getDictValue()), label);
            labels.put(label, label);
        }
        return labels;
    }

    private String label(Map<String, String> dictionary, String value, String type) {
        if (StringUtils.isBlank(value)) {
            return "";
        }
        String label = dictionary.get(value.trim());
        // Historical rows may store the Chinese label itself rather than a dictionary code.
        if (label == null && value.matches("(?s).*\\p{IsHan}.*")) {
            return value.trim();
        }
        if (label == null) {
            // Missing dictionaries must not silently export internal IDs as user-facing labels.
            throw new IllegalStateException("教材导出缺少字典项：" + type + "=" + value);
        }
        return label;
    }
}
