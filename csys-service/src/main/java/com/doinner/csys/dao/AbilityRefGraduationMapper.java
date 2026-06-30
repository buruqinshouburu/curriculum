package com.doinner.csys.dao;

import com.doinner.csys.domain.AbilityRefGraduation;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 课程和能力Mapper接口
 *
 * @author wzg
 * @date 2026-03-16
 */
public interface AbilityRefGraduationMapper {

     List<AbilityRefGraduation> selectByAbilityId(Long abilityId);

     int insert(AbilityRefGraduation courseRefAbility);

     int deleteByAbilityId(@Param("abilityId") Long abilityId);

     int deleteByAbilityIds(@Param("abilityIds")List<Long> abilityIds);
}
