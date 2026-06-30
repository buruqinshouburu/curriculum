package com.doinner.csys.dao;

import com.doinner.csys.domain.StandardAbility;
import com.doinner.csys.domain.StandardAbilityLevel;
import com.doinner.csys.domain.vo.StandardAbilityVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 能力等级Mapper接口
 *
 * @author doinner
 * @date 2023-03-28
 */
public interface StandardAbilityLevelMapper {
    /**
     * 新增能力等级
     * @param standardAbilityLevels 能力等级列表
     * @return
     */
    int insertStandardAbilityLevels(@Param("list") List<StandardAbilityLevel> standardAbilityLevels);

    /**
     * 根据能力id查询能力等级列表
     * @param abilityId
     * @return
     */
    List<StandardAbilityLevel> selectStandardAbilityLevelByAbilityId(@Param("abilityId") Long abilityId);

    /**
     * 根据能力id列表查询能力等级列表
     * @param aIds
     * @return
     */
    List<StandardAbilityLevel> selectStandardAbilityLevelByAbilityIds(@Param("list") List<Long> aIds);

    /**
     * 批量更新能力等级
     * @param levels
     */
    void updateStandardAbilityLevels(@Param("list") List<StandardAbilityLevel> levels);

    /**
     * 更新能力等级
     * @param level
     */
    int updateStandardAbilityLevel(StandardAbilityLevel level);

    /**
     * 新增能力等级
     * @param level
     */
    int insertStandardAbilityLevel(StandardAbilityLevel level);

    /**
     * 修改能力等级中的能力id
     * @param levels
     */
    int updateAbilityId(@Param("list")List<StandardAbilityLevel> levels);

    /**
     * 选中能力等级
     * @param standardAbilityLevel
     * @return
     */
    int updateCheckLevel(StandardAbilityLevel standardAbilityLevel);

    /**
     * 删除能力等级
     * @param ids
     */
    int deleteStandardAbilityLevel(@Param("list") List<Long> ids);

    /**
     * 删除能力等级根据能力id
     * @param abilityId
     * @return
     */
    int deleteStandardAbilityLevelByAbilityId(@Param("abilityId") Long abilityId);
}
