package com.doinner.csys.service;

import com.doinner.common.core.domain.Message;
import com.doinner.csys.domain.SourceDomain;
import com.doinner.csys.domain.SourcePoint;
import com.doinner.csys.domain.SourceUnit;
import com.doinner.csys.domain.vo.KnowledgeDomainReferenceVo;
import com.doinner.csys.domain.vo.SourceDomainTreeVo;
import com.doinner.csys.domain.vo.SourceKnowledgeVo;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

public interface KnowledgeSourceService {

    List<SourceDomain> selectSourceDomainList(SourceDomain sourceDomain);

    List<SourceKnowledgeVo> treeKnowledge(SourceDomain sourceDomain);


    SourceDomainTreeVo childrenKnowledgeByDomainId(Long domainId);

    List<SourceDomainTreeVo> childrenKnowledgeByCourseId(Long courseId);


    SourceDomain selectSourceDomainById(Long id);

    SourceDomain insertSourceDomain(SourceDomain sourceDomain);

    SourceDomain updateSourceDomain(SourceDomain sourceDomain);

    void deleteSourceDomainByIds(Long[] ids);

    SourceDomain addOrUpdateSourceUnitAndPoint(SourceDomain sourceDomain);

    void exportOutTemplate(HttpServletResponse response,List<Long> ids);

    void exportTemplate(HttpServletResponse response);

    Message exportInTemplate(MultipartFile file, Long collegeId, Integer type, Long categoryId, Long majorId, String version);

    SourceUnit addUnit(SourceUnit sourceUnit);

    SourceUnit editUnit(SourceUnit sourceUnit);

    void removeUnit(Long[] ids);


    SourcePoint addPoint(SourcePoint sourcePoint);

    SourcePoint editPoint(SourcePoint sourcePoint);

    void removePoint(Long[] ids);


    void referenceDomain(KnowledgeDomainReferenceVo referenceVo);

    List<Long>  KnowledgePointCheckAllBySchemeId(Long schemeId);
}
