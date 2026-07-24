package com.doinner.csys.dao;

import com.doinner.csys.domain.TeachingPlanObjective;
import com.doinner.csys.domain.vo.TeachingPlanMajorVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 教学计划目标 Mapper
 */
public interface TeachingPlanObjectiveMapper {

    /**
     * 单条新增，新增后回填自增主键id
     */
    int insert(TeachingPlanObjective record);

    /**
     * 批量新增
     */
    int insertBatch(@Param("list") List<TeachingPlanObjective> list);

    /**
     * 根据主键更新
     */
    int updateById(TeachingPlanObjective record);

    /**
     * 根据主键查询
     */
    TeachingPlanObjective selectById(@Param("id") Long id);

    /**
     * 按计划与培养方案查询目标内容。
     * onlyNullScheme=true 时只取 scheme_id IS NULL（公共基础单组）；
     * onlyNullScheme=false 且 schemeId 非空时按 scheme 过滤；
     * onlyNullScheme=false 且 schemeId 为空时不过滤 scheme（全量）。
     */
    List<TeachingPlanObjective> selectByPlanAndScheme(@Param("planId") Long planId,
                                                      @Param("schemeId") Long schemeId,
                                                      @Param("onlyNullScheme") Boolean onlyNullScheme);

    /**
     * 按计划 + 培养方案查询目标；可选按目标类型编码过滤。
     * onlyNullScheme 语义同 {@link #selectByPlanAndScheme}。
     *
     * @param objectiveTypeCode 可选，对应 t_csys_teaching_plan_objective.objective_type_code
     */
    List<TeachingPlanObjective> selectByPlanAndSchemeAndType(@Param("planId") Long planId,
                                                              @Param("schemeId") Long schemeId,
                                                              @Param("objectiveTypeCode") String objectiveTypeCode,
                                                              @Param("onlyNullScheme") Boolean onlyNullScheme);

    /**
     * 任务6：根据总库课程id查询专业id与名称。
     * 链路：总库课程 -> 调用课程(source_id) -> 培养方案专业关联 -> 专业总库。
     *
     * @param courseId 总库课程id
     * @return 专业id/name(status由业务另行计算)
     */
    List<TeachingPlanMajorVo> selectMajorByCourseId(@Param("courseId") Long courseId);

    /**
     * 任务6状态计算：按教学计划id与专业id统计目标数量。
     */
    int countByPlanAndMajor(@Param("planId") Long planId, @Param("majorId") Long majorId);

    /**
     * 任务6状态计算：按教学计划id与专业id统计「未绑定任何毕业要求」的目标数量。
     * 绑定关系取 t_csys_teaching_plan_objective_ref.objective_id。
     */
    int countUnboundObjectiveByPlanAndMajor(@Param("planId") Long planId, @Param("majorId") Long majorId);

    /**
     * 根据教学计划ID逻辑删除
     */
    int deleteByPlanId(@Param("planId") Long planId);

    /**
     * 根据主键逻辑删除
     */
    int deleteById(@Param("id") Long id);

    /**
     * 将目标 scheme_id 置空（公共基础单组：覆盖历史按 scheme 落库的数据）。
     */
    int clearSchemeIdById(@Param("id") Long id);
}
