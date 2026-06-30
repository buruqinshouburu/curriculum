package com.doinner.csys.dao;

import com.doinner.csys.domain.StandardAbility;
import com.doinner.csys.domain.vo.StandardAbilityVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 能力素质Mapper接口
 *
 * @author doinner
 * @date 2023-03-14
 */
public interface StandardAbilityMapper {
    /**
     * 查询能力素质
     *
     * @param id 能力素质主键
     * @return 能力素质
     */
    StandardAbility selectStandardAbilityById(@Param("id") Long id);

    List<StandardAbility> selectStandardAbilityByParentId(@Param("parentId") Long parentId);

    /**
     * 查询能力素质列表
     *
     * @param standardAbility 能力素质
     * @return 能力素质集合
     */
    List<StandardAbility> selectStandardAbilityList(StandardAbility standardAbility);

    List<StandardAbility> selectStandardAbilityAndParentIdList(StandardAbility standardAbility);

    /**
     * 新增能力素质
     *
     * @param standardAbility 能力素质
     * @return 结果
     */
    int insertStandardAbility(StandardAbility standardAbility);

    /**
     * 修改能力素质
     *
     * @param standardAbility 能力素质
     * @return 结果
     */
    int updateStandardAbility(StandardAbility standardAbility);

    /**
     * 删除能力素质
     *
     * @param id 能力素质主键
     * @return 结果
     */
    int deleteStandardAbilityById(@Param("id") Long id);

    int updateSysFlag(@Param("id") Long id);

    /**
     * 批量删除能力素质
     *
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    int deleteStandardAbilityByIds(Long[] ids);

    List<StandardAbility> selectAllAbilityById(@Param("id") Long id);

    List<StandardAbility> selectStandardAbilityByIds(@Param("ids") List<Long> ids,@Param("type") Integer type);


    String selectUrlByPId(@Param("pid") Long pid);



    void updateNotLeaf(@Param("id") Long id);

    int insertAbilityList(@Param("list") List<StandardAbility> standardAbilities);

    int updateAbilityList(@Param("list") List<StandardAbility> standardAbilities);


    void setLeafIsNode(@Param("pId") Long pId);

    List<StandardAbility> checkIssueAbility(StandardAbility standardAbility);

    List<Map> selectStandardAbilityListBySchemeId(@Param("schemeId") Long schemeId, @Param("type")String type);
}
