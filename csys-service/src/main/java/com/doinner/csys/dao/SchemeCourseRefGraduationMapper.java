package com.doinner.csys.dao;

import com.doinner.csys.domain.SchemeCourseRefGraduation;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 培养方案调用课程毕业要求关联 Mapper
 */
public interface SchemeCourseRefGraduationMapper {

    /**
     * 单条新增，新增后回填自增主键id
     */
    int insert(SchemeCourseRefGraduation record);

    /**
     * 批量新增
     */
    int insertBatch(@Param("list") List<SchemeCourseRefGraduation> list);

    /**
     * 根据主键更新
     */
    int updateById(SchemeCourseRefGraduation record);

    /**
     * 根据主键查询
     */
    SchemeCourseRefGraduation selectById(@Param("id") Long id);

    /**
     * 按培养方案与调用课程查询已绑定毕业要求
     */
    List<SchemeCourseRefGraduation> selectBySchemeAndCourse(@Param("schemeId") Long schemeId,
                                                            @Param("quoteCourseId") Long quoteCourseId);

    /**
     * 根据主键逻辑删除
     */
    int deleteById(@Param("id") Long id);
}