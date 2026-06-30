package com.doinner.csys.dao;

import com.doinner.csys.domain.StandardCultivationRefGraduation;
import com.doinner.csys.domain.StandardGraduationRefCultivationTarget;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 毕业标准与培养目标关联Mapper接口
 *
 * @author doinner
 * @date 2023-03-14
 */
public interface StandardGraduationRefCultivationTargetMapper {

    int insetList(@Param("insetInfo") List<StandardGraduationRefCultivationTarget> insetInfo);


    void deleteByGraduationIds(List<Long> ids);

    List<StandardGraduationRefCultivationTarget> selectRefByGraduationIds(@Param("standardGraduationIds") List<Long> standardGraduationIds);

    List<StandardGraduationRefCultivationTarget> selectRefByGraduationId(Long standardGraduationId);


    List<StandardGraduationRefCultivationTarget> selectRefByTaIds(@Param("taIds") List<Long> taIds);

    List<StandardGraduationRefCultivationTarget> selectAll();

    int insert(@Param("srt")StandardGraduationRefCultivationTarget srt);

}
