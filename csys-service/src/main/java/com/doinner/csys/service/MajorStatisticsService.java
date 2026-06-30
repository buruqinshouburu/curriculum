package com.doinner.csys.service;

import com.doinner.csys.domain.StandardMajor;
import com.doinner.csys.domain.vo.CategoryCountVo;
import com.doinner.csys.domain.vo.MajorCountVo;
import com.doinner.csys.domain.vo.SchemeCountVo;

import java.util.List;

public interface MajorStatisticsService {

    List<MajorCountVo> majorCount();

    List<SchemeCountVo> schemeCount();

    List<CategoryCountVo> systemTypeCount();
}
