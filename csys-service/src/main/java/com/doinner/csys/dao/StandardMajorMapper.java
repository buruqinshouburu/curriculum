package com.doinner.csys.dao;

import java.util.List;
import java.util.Map;

import com.doinner.csys.domain.StandardMajor;
import com.doinner.csys.domain.vo.MajorCountVo;
import org.apache.ibatis.annotations.Param;

/**
 * 学院专业Mapper接口
 *
 * @author doinner
 * @date 2023-03-21
 */
public interface StandardMajorMapper
{
    /**
     * 查询学院专业
     *
     * @param id 学院专业主键
     * @return 学院专业
     */
    StandardMajor selectStandardMajorById(Long id);

    List<StandardMajor> selectStandardMajorByIds(@Param("ids") List<Long> ids);

    List<StandardMajor> selectStandardMajorByParentId(Long parentId);

    /**
     * 查询学院专业列表
     *
     * @param standardMajor 学院专业
     * @return 学院专业集合
     */
    List<StandardMajor> selectStandardMajorList(StandardMajor standardMajor);

    /**
     * 新增学院专业
     *
     * @param standardMajor 学院专业
     * @return 结果
     */
    int insertStandardMajor(StandardMajor standardMajor);

    /**
     * 修改学院专业
     *
     * @param standardMajor 学院专业
     * @return 结果
     */
    int updateStandardMajor(StandardMajor standardMajor);

    /**
     * 删除学院专业
     *
     * @param id 学院专业主键
     * @return 结果
     */
    int deleteStandardMajorById(Long id);
    int deleteStandardMajorByParentId(Long parentId);

    /**
     * 批量删除学院专业
     *
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    int deleteStandardMajorByIds(Long[] ids);

    List<MajorCountVo> majorCount();

    List<StandardMajor> selectStandardMajorByCategories(@Param("categoryIds") List<Long> categoryIds);

    List<StandardMajor> selectStandardMajorByCategory(Long categoryId);

    List<StandardMajor> selectStandardMajorAccurate(StandardMajor standardMajor);

    List<Map> selectSubMajorList();
}
