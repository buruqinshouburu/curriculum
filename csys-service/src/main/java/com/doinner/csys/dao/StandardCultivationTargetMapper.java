package com.doinner.csys.dao;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import com.doinner.csys.domain.StandardCultivationTarget;
import com.doinner.csys.domain.statisticsVo.StandardCultivationTargetStatisticsVo;
import com.doinner.csys.domain.vo.StandardCultivationTargetVo;
import com.doinner.csys.domain.vo.StandardGraduationVo;
import com.doinner.csys.domain.vo.TrainingSchemeVo;
import org.apache.ibatis.annotations.Param;

/**
 * 培养目标Mapper接口
 *
 * @author doinner
 * @date 2023-03-21
 */
public interface StandardCultivationTargetMapper {

    StandardCultivationTarget selectStandardCultivationTargetById(Long id);

    List<StandardCultivationTarget> selectStandardCultivationTargetList(StandardCultivationTarget standardCultivationTarget);

    List<StandardCultivationTarget> selectStdCultivationTargetAll(@Param("id") Long id);

    StandardCultivationTarget selectStdCultivationTargetByRoot(@Param("id") Long id,@Param("name") String name);

    List<StandardCultivationTargetVo> selectStdCultivationTargetVoAll(@Param("id") Long id);


    List<Long> selectStandardCultivationTargetRefByGraduationId(@Param("id") Long id,@Param("graduationId") Long graduationId);


    int insertStandardCultivationTarget(StandardCultivationTarget standardCultivationTarget);

    int updateStandardCultivationTarget(StandardCultivationTarget standardCultivationTarget);

    int deleteStandardCultivationTarget(Long id);

    void setLeafIsNode(@Param("pId") Long pId);


    void insertStandardCultivationTargetList(@Param("standardCultivationTargetList") Collection<StandardCultivationTarget> standardCultivationTargetList);

    void updateStandardCultivationTargetList(@Param("standardCultivationTargetList") Collection<StandardCultivationTarget> standardCultivationTargetList);

    List<StandardGraduationVo> selectTargetByGraduationId(@Param("id") Long id);

    List<String>selectTargetUrlPName(@Param("ids")  List<Long>  ids);


    List<StandardCultivationTargetStatisticsVo> selectTargetPropUpBySchemeId(@Param("schemeId") Long schemeId);


    List<String> selectAllWord( );

    void deleteByIds(@Param("ids") List<Long> tIds);

    List<StandardCultivationTarget> selectCultivationTargetByMajorId(@Param("trainingSchemeVo") TrainingSchemeVo trainingSchemeVo);

    List<StandardCultivationTargetVo> selectTargetByGraduationIds(@Param("ids") List<Long> ids);


    StandardCultivationTarget selectCultivationTargetByParentId(Long parentId);


    List<StandardCultivationTarget> selectIds(@Param("ids")  List<Long>  ids);

    List<StandardCultivationTarget> selectStdCultivationTargetAllByTrainingId(@Param("trainingSchemeId") Long trainingSchemeId);

    void deleteStandardCultivationTargetBySchemeId(Long schemeId);
}
