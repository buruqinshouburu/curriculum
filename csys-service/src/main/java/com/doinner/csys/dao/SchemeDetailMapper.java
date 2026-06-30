package com.doinner.csys.dao;

import com.doinner.csys.domain.SchemeDetail;

import java.util.List;

/**
 *
 * @author wzg
 * @date 2026-02-25
 */
public interface SchemeDetailMapper {

    SchemeDetail selectSchemeDetailById(Long id);


    List<SchemeDetail> selectSchemeDetailList(SchemeDetail schemeDetail);

    List<SchemeDetail> selectSchemeIdDetailList(Long schemeId);


    int insertSchemeDetail(SchemeDetail schemeDetail);


    int updateSchemeDetail(SchemeDetail schemeDetail);


    int deleteSchemeDetailBySchemeId(Long schemeId);

    int deleteSchemeDetailById(Long id);


    int deleteSchemeDetailByIds(Long[] ids);
}
