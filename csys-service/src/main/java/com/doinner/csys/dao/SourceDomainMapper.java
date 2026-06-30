package com.doinner.csys.dao;

import java.util.List;

import com.doinner.csys.domain.SourceDomain;
import com.doinner.csys.domain.vo.SourceDomainTreeVo;
import org.apache.ibatis.annotations.Param;

/**
 * 源知识领域Mapper接口
 *
 * @author wzg
 * @date 2026-02-26
 */
public interface SourceDomainMapper {
    /**
     * 查询源知识领域
     *
     * @param id 源知识领域主键
     * @return 源知识领域
     */
     SourceDomain selectSourceDomainById(Long id);
     List<SourceDomain> selectSourceDomainByIds(@Param("ids") List<Long> ids);


     List<SourceDomain> selectSourceDomainByCourseId(SourceDomainTreeVo sourceDomainTreeVo);

    /**
     * 查询源知识领域列表
     *
     * @param sourceDomain 源知识领域
     * @return 源知识领域集合
     */
     List<SourceDomain> selectSourceDomainList(SourceDomain sourceDomain);

    /**
     * 新增源知识领域
     *
     * @param sourceDomain 源知识领域
     * @return 结果
     */
     int insertSourceDomain(SourceDomain sourceDomain);

    /**
     * 修改源知识领域
     *
     * @param sourceDomain 源知识领域
     * @return 结果
     */
     int updateSourceDomain(SourceDomain sourceDomain);

    /**
     * 删除源知识领域
     *
     * @param id 源知识领域主键
     * @return 结果
     */
     int deleteSourceDomainById(Long id);

    /**
     * 批量删除源知识领域
     *
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
     int deleteSourceDomainByIds(Long[] ids);

    List<SourceDomain> selectSourceDomainByCourseId(Long courseId);


    List<SourceDomain> selectSourceDomainByCourseTargetId(Long courseTargetId);

}
