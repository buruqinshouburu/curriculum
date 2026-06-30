package com.doinner.csys.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.doinner.csys.entity.csys.po.CourseKnowledgePoint;
import com.doinner.csys.entity.csys.po.SchemeRefMajor;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.ArrayList;
import java.util.List;

/**
 * 课程知识点Mapper
 */
@Mapper
public interface SchemeRefMajorMapper extends BaseMapper<SchemeRefMajor> {

    void insertBatch(@Param("list")List<SchemeRefMajor> schemeRefMajors);

    void deleteBySchemeId(@Param("schemeId") Long id);
}