package com.doinner.csys.dao;

import java.util.Collection;
import java.util.List;

import com.doinner.csys.domain.StandardCultivation;
import com.doinner.csys.domain.vo.StandardCultivationVo;
import com.doinner.csys.domain.vo.StandardGraduationVo;
import org.apache.ibatis.annotations.Param;

/**
 * 培养标准Mapper接口
 *
 * @author doinner
 * @date 2023-03-21
 */
public interface StandardCultivationMapper {
    /**
     * 查询培养标准
     *
     * @param id 培养标准主键
     * @return 培养标准
     */
    StandardCultivation selectStandardCultivationById(Long id);

    /**
     * 查询培养标准列表
     *
     * @param standardCultivation 培养标准
     * @return 培养标准集合
     */
    List<StandardCultivation> selectStandardCultivationList(StandardCultivation standardCultivation);

    /**
     * 新增培养标准
     *
     * @param standardCultivation 培养标准
     * @return 结果
     */
    int insertStandardCultivation(StandardCultivation standardCultivation);

    /**
     * 修改培养标准
     *
     * @param standardCultivation 培养标准
     * @return 结果
     */
    int updateStandardCultivation(StandardCultivation standardCultivation);

    /**
     * 删除培养标准
     *
     * @param id 培养标准主键
     * @return 结果
     */
    int deleteStandardCultivationById(Long id);

    void setLeafIsNode(@Param("pId") Long pId);


    void insertStandardCultivationList(@Param("standardCultivationList") Collection<StandardCultivation> standardCultivationList);

    void updateStandardCultivationList(@Param("standardCultivationList") Collection<StandardCultivation> standardCultivationList);

    List<StandardCultivation> selectStandardCultivationAll(Long id);
    List<StandardCultivation> selectStandardCultivationAndLeafAll(Long id);

    StandardCultivation selectStandardCultivationByRoot(@Param("id") Long id,@Param("name") String name);

    List<StandardCultivationVo> selectCultivationVoAll(@Param("id") Long id);

    List<StandardCultivationVo> selectCultivationRefGraduation(@Param("id") Long id);

    int updateGraduationId(StandardCultivation standardCultivation);

    List<StandardGraduationVo> selectCByGId(@Param("graduationId") Long graduationId);

    List<String> selectCUrlPName(@Param("ids") List<Long> ids);
}
