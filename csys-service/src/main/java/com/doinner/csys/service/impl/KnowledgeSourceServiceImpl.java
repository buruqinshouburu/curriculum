package com.doinner.csys.service.impl;

import com.doinner.common.core.domain.Message;
import com.doinner.common.core.utils.StringUtils;
import com.doinner.common.core.utils.poi.ExcelUtil;
import com.doinner.csys.constant.DomainExceptionConstant;
import com.doinner.csys.dao.*;
import com.doinner.csys.domain.*;
import com.doinner.csys.domain.vo.*;
import com.doinner.csys.io.utils.ExcelUtils;
import com.doinner.csys.service.KnowledgeSourceService;
import com.doinner.csys.utils.UserUtils;
import com.doinner.system.domain.custom.CustomDept;
import com.doinner.system.domain.entity.SysDept;
import com.doinner.system.service.DoinnerDeptService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class KnowledgeSourceServiceImpl implements KnowledgeSourceService {

    @Resource
    private SourceDomainMapper sourceDomainMapper;

    @Resource
    private SourceDomainRefUnitMapper sourceDomainRefUnitMapper;

    @Resource
    private SourcePointMapper sourcePointMapper;

    @Resource
    private SourceUnitMapper sourceUnitMapper;

    @Resource
    private SourceUnitRefPointMapper sourceUnitRefPointMapper;

    @Resource
    private DoinnerDeptService doinnerDeptService;

    @Resource
    private CourseRefSourceDomainMapper courseRefSourceDomainMapper;

    @Resource
    private StandardMajorMapper standardMajorMapper;

    @Resource
    private TrainingSchemeCategoryMapper trainingSchemeCategoryMapper;
    @Autowired
    private CourseMapper courseMapper;

    @Resource
    private CourseDomainRefSourceUnitMapper courseDomainRefSourceUnitMapper;

    @Resource
    private CourseUnitRefSourcePointMapper courseUnitRefSourcePointMapper;

    @Resource
    private KnowledgeNoCheckLogMapper knowledgeNoCheckLogMapper;
    @Resource
    private KnowledgeChekTotalMapper knowledgeChekTotalMapper;
    @Resource
    private KnowledgeCheckLogMapper knowledgeCheckLogMapper;
    @Autowired
    private TrainingSchemeRefCourseMapper trainingSchemeRefCourseMapper;


    @Override
    public List<SourceDomain> selectSourceDomainList(SourceDomain sourceDomain) {
        List<SourceDomain> sourceDomains = sourceDomainMapper.selectSourceDomainList(sourceDomain);
        CustomDept sysDept = new CustomDept();
        List<SysDept> list = doinnerDeptService.list(sysDept).getData();
        Map<Long, String> deptIdNameMap = list.parallelStream().collect(Collectors.toMap(SysDept::getDeptId, SysDept::getDeptName));
        for (SourceDomain domain : sourceDomains) {
            domain.setCollegeName(deptIdNameMap.get(domain.getCollegeId()));
            // 知识点数量
            Integer pointCount = 0;
            Integer unitCount = 0;
            List<SourceDomainRefUnit> sourceDomainRefUnits = sourceDomainRefUnitMapper.selectSourceDomainRefUnitByDomainId(domain.getId());
            if (CollectionUtils.isNotEmpty(sourceDomainRefUnits)) {
                unitCount = sourceDomainRefUnits.size();
                List<Long> unitIds = sourceDomainRefUnits.stream().map(a -> a.getUnitId()).collect(Collectors.toList());
                List<SourceUnitRefPoint> sourceUnitRefPoints = sourceUnitRefPointMapper.selectSourceUnitRefPointByUnitIds(unitIds);
                if (CollectionUtils.isNotEmpty(sourceUnitRefPoints)) {
                    pointCount += sourceUnitRefPoints.size();
                }
            }
            domain.setPointCount(pointCount);
            domain.setUnitCount(unitCount);
        }
        return sourceDomains;
    }

    @Override
    public List<SourceKnowledgeVo> treeKnowledge(SourceDomain sourceDomain) {
        List<SourceDomain> sourceDomains = sourceDomainMapper.selectSourceDomainList(sourceDomain);
        List<SourceKnowledgeVo> sourceKnowledgeVos = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(sourceDomains)) {
            for (SourceDomain domain : sourceDomains) {
                SourceKnowledgeVo sourceKnowledgeVo = new SourceKnowledgeVo();
                sourceKnowledgeVo.setId(domain.getId());
                // 后续改成一个查询 查询知识单元
                SourceDomain reDomain = sourceDomainMapper.selectSourceDomainById(domain.getId());
                List<SourceKnowledgeVo> unitChildren = new ArrayList<>();
                if (CollectionUtils.isNotEmpty(reDomain.getSourceUnits())) {
                    List<SourceUnit> sourceUnits = reDomain.getSourceUnits();
                    for (SourceUnit sourceUnit : sourceUnits) {
                        SourceKnowledgeVo unitVo = new SourceKnowledgeVo();
                        unitVo.setId(sourceUnit.getId());
                        unitVo.setName(sourceUnit.getName());
                        unitVo.setCollegeId(domain.getCollegeId());
                        unitVo.setCategoryId(domain.getCategoryId());
                        unitVo.setMajorId(domain.getMajorId());
                        List<SourceKnowledgeVo> pointChildren = new ArrayList<>();
                        if (CollectionUtils.isNotEmpty(sourceUnit.getSourcePoints())) {
                            for (SourcePoint sourcePoint : sourceUnit.getSourcePoints()) {
                                SourceKnowledgeVo pointVo = new SourceKnowledgeVo();
                                pointVo.setId(sourcePoint.getId());
                                pointVo.setName(sourcePoint.getName());
                                pointVo.setCollegeId(domain.getCollegeId());
                                pointVo.setCategoryId(domain.getCategoryId());
                                pointVo.setMajorId(domain.getMajorId());
                                pointChildren.add(pointVo);
                            }
                        }
                        unitVo.setChildren(pointChildren);
                        unitChildren.add(unitVo);
                    }
                }
                sourceKnowledgeVo.setChildren(unitChildren);
                sourceKnowledgeVo.setName(domain.getName());
                sourceKnowledgeVo.setCollegeId(domain.getCollegeId());
                sourceKnowledgeVo.setCategoryId(domain.getCategoryId());
                sourceKnowledgeVo.setMajorId(domain.getMajorId());
                sourceKnowledgeVos.add(sourceKnowledgeVo);
            }
        }
        return sourceKnowledgeVos;
    }

    @Override
    public SourceDomain selectSourceDomainById(Long id) {
        SourceDomain sourceDomain = sourceDomainMapper.selectSourceDomainById(id);
        return sourceDomain;
    }

    @Override
    public SourceDomainTreeVo childrenKnowledgeByDomainId(Long domainId) {
        SourceDomain sourceDomain = sourceDomainMapper.selectSourceDomainById(domainId);
        return sourceDomainToTree(sourceDomain);
    }

    private SourceDomainTreeVo sourceDomainToTree(SourceDomain sourceDomain) {
        SourceDomainTreeVo sourceDomainTreeVo = new SourceDomainTreeVo();
        sourceDomainTreeVo.setId(sourceDomain.getId());
        sourceDomainTreeVo.setName(sourceDomain.getName());
        if (CollectionUtils.isNotEmpty(sourceDomain.getSourceUnits())) {
            List<SourceDomainTreeVo> childrenUnits = new ArrayList<>();
            for (SourceUnit sourceUnit : sourceDomain.getSourceUnits()) {
                SourceDomainTreeVo unitVo = new SourceDomainTreeVo();
                unitVo.setId(sourceUnit.getId());
                unitVo.setName(sourceUnit.getName());
                List<SourceDomainTreeVo> childrenPoints = new ArrayList<>();
                if (CollectionUtils.isNotEmpty(sourceUnit.getSourcePoints())) {
                    for (SourcePoint sourcePoint : sourceUnit.getSourcePoints()) {
                        SourceDomainTreeVo pointVo = new SourceDomainTreeVo();
                        pointVo.setId(sourcePoint.getId());
                        pointVo.setName(sourcePoint.getName());
                        childrenPoints.add(pointVo);
                    }
                }
                unitVo.setChildren(childrenPoints);
                childrenUnits.add(unitVo);
            }
            sourceDomainTreeVo.setChildren(childrenUnits);
        }
        return sourceDomainTreeVo;
    }

    @Override
    public List<SourceDomainTreeVo> childrenKnowledgeByCourseId(Long courseId) {
        List<SourceDomain> sourceDomains = sourceDomainMapper.selectSourceDomainByCourseId(courseId);
        List<SourceDomainTreeVo> treeVos = new ArrayList<>();
        for (SourceDomain sourceDomain : sourceDomains) {
            treeVos.add(sourceDomainToTree(sourceDomain));
        }
        return treeVos;
    }


    @Override
    @Transactional(rollbackFor = {Exception.class})
    public SourceDomain insertSourceDomain(SourceDomain sourceDomain) {
        UserUtils.reflash(sourceDomain);
        sourceDomainMapper.insertSourceDomain(sourceDomain);
        return sourceDomain;
    }

    @Override
    @Transactional(rollbackFor = {Exception.class})
    public SourceDomain updateSourceDomain(SourceDomain sourceDomain) {
        UserUtils.reflash(sourceDomain);
        sourceDomainMapper.updateSourceDomain(sourceDomain);
        return sourceDomain;
    }

    @Override
    @Transactional(rollbackFor = {Exception.class})
    public void deleteSourceDomainByIds(Long[] ids) {
        for (Long id : ids) {
            checkDomain(id);
        }
        sourceDomainMapper.deleteSourceDomainByIds(ids);
        for (Long id : ids) {
            deleteAllCheckLog(id);
        }
    }

    private void checkDomain(Long id) {
        SourceDomain db = sourceDomainMapper.selectSourceDomainById(id);
        UserUtils.checkDataPermission(db);
        List<CourseRefSourceDomain> courseRefSourceDomains = courseRefSourceDomainMapper.selectCourseRefSourceDomainByDomainId(id);
        if (CollectionUtils.isNotEmpty(courseRefSourceDomains)) {
            List<Long> courseIds = courseRefSourceDomains.stream().map(CourseRefSourceDomain::getCourseId).collect(Collectors.toList());
            List<Course> courses = courseMapper.selectCoursesByIds(courseIds);
            throw new RuntimeException("知识体系被课程:" + courses.stream().map(Course::getName).collect(Collectors.joining(",")) + "引用后不允许修改和删除!");
        }
    }

    @Override
    @Transactional(rollbackFor = {Exception.class})
    public SourceDomain addOrUpdateSourceUnitAndPoint(SourceDomain sourceDomain) {
        UserUtils.reflash(sourceDomain);
        if (sourceDomain.getId() != null) {
            SourceDomain db = sourceDomainMapper.selectSourceDomainById(sourceDomain.getId());
            UserUtils.checkDataPermission(db);
            sourceDomainMapper.updateSourceDomain(sourceDomain);
        } else {
            sourceDomainMapper.insertSourceDomain(sourceDomain);
        }
        List<SourceDomainRefUnit> dbDomainRefUnits = sourceDomainRefUnitMapper.selectSourceDomainRefUnitByDomainId(sourceDomain.getId());
        List<Long> dbPointIds = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(dbDomainRefUnits)) {
            List<Long> unitIds = dbDomainRefUnits.stream().map(a -> a.getUnitId()).collect(Collectors.toList());
            List<SourceUnitRefPoint> sourceUnitRefPoints = sourceUnitRefPointMapper.selectSourceUnitRefPointByUnitIds(unitIds);
            if (CollectionUtils.isNotEmpty(sourceUnitRefPoints)) {
                List<Long> pointIds = sourceUnitRefPoints.stream().map(a -> a.getPointId()).collect(Collectors.toList());
                dbPointIds.addAll(pointIds);
                // sourcePointMapper.deleteSourcePointByIds(pointIds.toArray(Long[]::new));
                // sourceUnitRefPointMapper.deleteSourceUnitRefPointByUnitIds(unitIds.toArray(Long[]::new));
            }
            // sourceUnitMapper.deleteSourceUnitByIds(unitIds.toArray(Long[]::new));
            // sourceDomainRefUnitMapper.deleteSourceDomainRefUnitByDomainId(sourceDomain.getId());
        }
        // 本来先删后增 后面业务规定得一个一个判断
        if (CollectionUtils.isNotEmpty(sourceDomain.getSourceUnits())) {
            List<Long> existUnitIds = new ArrayList<>();
            List<Long> existPointIds = new ArrayList<>();
            // 处理知识单元和知识领域
            for (SourceUnit sourceUnit : sourceDomain.getSourceUnits()) {
                if (sourceUnit.getId() == null){
                    sourceUnitMapper.insertSourceUnit(sourceUnit);
                    SourceDomainRefUnit requestSourceDomainRefUnit = new SourceDomainRefUnit();
                    requestSourceDomainRefUnit.setDomainId(sourceDomain.getId());
                    requestSourceDomainRefUnit.setUnitId(sourceUnit.getId());
                    sourceDomainRefUnitMapper.insertSourceDomainRefUnit(requestSourceDomainRefUnit);
                    // 处理知识点和知识单元
                    if (CollectionUtils.isNotEmpty(sourceUnit.getSourcePoints())) {
                        for (SourcePoint sourcePoint : sourceUnit.getSourcePoints()) {
                            if (StringUtils.isNotBlank(sourcePoint.getName())) {
                                sourcePointMapper.insertSourcePoint(sourcePoint);
                                SourceUnitRefPoint requestSourceUnitRefPoint = new SourceUnitRefPoint();
                                requestSourceUnitRefPoint.setUnitId(sourceUnit.getId());
                                requestSourceUnitRefPoint.setPointId(sourcePoint.getId());
                                sourceUnitRefPointMapper.insertSourceUnitRefPoint(requestSourceUnitRefPoint);
                            }
                        }
                    }
                }else {
                    sourceUnitMapper.updateSourceUnit(sourceUnit);
                    existUnitIds.add(sourceUnit.getId());
                    List<SourcePoint> sourcePoints = sourceUnit.getSourcePoints();
                    if (CollectionUtils.isNotEmpty(sourcePoints)) {
                        for (SourcePoint sourcePoint : sourcePoints) {
                            if (sourcePoint.getId() == null) {
                                sourcePointMapper.insertSourcePoint(sourcePoint);
                                SourceUnitRefPoint requestSourceUnitRefPoint = new SourceUnitRefPoint();
                                requestSourceUnitRefPoint.setUnitId(sourceUnit.getId());
                                requestSourceUnitRefPoint.setPointId(sourcePoint.getId());
                                sourceUnitRefPointMapper.insertSourceUnitRefPoint(requestSourceUnitRefPoint);
                            }else {
                                sourcePointMapper.updateSourcePoint(sourcePoint);
                                existPointIds.add(sourcePoint.getId());
                            }
                        }
                    }
                }
            }
            List<Long> deleteUnitIds = new ArrayList<>();
            if (CollectionUtils.isNotEmpty(dbDomainRefUnits)) {
                List<Long> dbUnitIds = dbDomainRefUnits.stream().map(a -> a.getUnitId()).collect(Collectors.toList());
                for (Long dbId : dbUnitIds) {
                    if (!existUnitIds.contains(dbId)){
                        deleteUnitIds.add(dbId);
                    }
                }
            }
            List<Long> deletePointIds = new ArrayList<>();
            if (CollectionUtils.isNotEmpty(dbPointIds)){
                for (Long dbPointId : dbPointIds) {
                    if (!existPointIds.contains(dbPointId)){
                        deletePointIds.add(dbPointId);
                    }
                }
            }
            if (CollectionUtils.isNotEmpty(deleteUnitIds)) {
                //  删除 判断是否被课程引用
                List<CourseDomainRefSourceUnit> courseDomainRefSourceUnits = courseDomainRefSourceUnitMapper.selectCourseDomainRefSourceUnitByUnitIds(deleteUnitIds.toArray(Long[]::new));
                if (CollectionUtils.isNotEmpty(courseDomainRefSourceUnits)) {
                    List<Long> courseIds = courseDomainRefSourceUnits.stream().map(CourseDomainRefSourceUnit::getCourseId).collect(Collectors.toList());
                    List<Course> courses = courseMapper.selectCoursesByIds(courseIds);
                    throw new RuntimeException("知识单元已被课程引用:" + courses.stream().map(Course::getName).collect(Collectors.joining(",")) + "不能修改！");
                }
                sourceDomainRefUnitMapper.deleteSourceDomainRefUnitByUnitIds(deleteUnitIds.toArray(Long[]::new));
                sourceUnitRefPointMapper.deleteSourceUnitRefPointByUnitIds(deleteUnitIds.toArray(Long[]::new));
                sourceUnitMapper.deleteSourceUnitByIds(deleteUnitIds.toArray(Long[]::new));
            }
            if (CollectionUtils.isNotEmpty(deletePointIds)){
                sourcePointMapper.deleteSourcePointByIds(deletePointIds.toArray(Long[]::new));
                sourceUnitRefPointMapper.deleteSourceUnitRefPointByPointIds(deletePointIds.toArray(Long[]::new));
            }
        }
        //删除知识点查重
        this.deleteAllCheckLog(sourceDomain.getId());
        return sourceDomain;
    }

    //删除查重记录
    public void deleteAllCheckLog(Long sourceDomainId) {
        if(ObjectUtils.isNotEmpty(sourceDomainId)){
            knowledgeCheckLogMapper.deleteBySourceDomainId(sourceDomainId);
            knowledgeNoCheckLogMapper.deleteBySourceDomainId(sourceDomainId);
            knowledgeChekTotalMapper.deleteBySourceDomainId(sourceDomainId);
        }
    }


    public SourceDomain addOrUpdateSourceUnitAndPoint2(SourceDomain sourceDomain) {
        UserUtils.reflash(sourceDomain);
        if (sourceDomain.getId() != null) {
            sourceDomainMapper.updateSourceDomain(sourceDomain);
        } else {
            sourceDomainMapper.insertSourceDomain(sourceDomain);
        }
        List<SourceDomainRefUnit> sourceDomainRefUnit = sourceDomainRefUnitMapper.selectSourceDomainRefUnitByDomainId(sourceDomain.getId());
        if (CollectionUtils.isNotEmpty(sourceDomainRefUnit)) {
            List<Long> unitIds = sourceDomainRefUnit.stream().map(a -> a.getUnitId()).collect(Collectors.toList());
            List<SourceUnitRefPoint> sourceUnitRefPoints = sourceUnitRefPointMapper.selectSourceUnitRefPointByUnitIds(unitIds);
            if (CollectionUtils.isNotEmpty(sourceUnitRefPoints)) {
                List<Long> pointIds = sourceUnitRefPoints.stream().map(a -> a.getPointId()).collect(Collectors.toList());
                sourcePointMapper.deleteSourcePointByIds(pointIds.toArray(Long[]::new));
                sourceUnitRefPointMapper.deleteSourceUnitRefPointByUnitIds(unitIds.toArray(Long[]::new));
            }
            sourceUnitMapper.deleteSourceUnitByIds(unitIds.toArray(Long[]::new));
            sourceDomainRefUnitMapper.deleteSourceDomainRefUnitByDomainId(sourceDomain.getId());
        }
        // 后增
        if (CollectionUtils.isNotEmpty(sourceDomain.getSourceUnits())) {
            // 处理知识单元和知识领域
            for (SourceUnit sourceUnit : sourceDomain.getSourceUnits()) {
                sourceUnitMapper.insertSourceUnit(sourceUnit);
                SourceDomainRefUnit requestSourceDomainRefUnit = new SourceDomainRefUnit();
                requestSourceDomainRefUnit.setDomainId(sourceDomain.getId());
                requestSourceDomainRefUnit.setUnitId(sourceUnit.getId());
                sourceDomainRefUnitMapper.insertSourceDomainRefUnit(requestSourceDomainRefUnit);
                // 处理知识点和知识单元
                if (CollectionUtils.isNotEmpty(sourceUnit.getSourcePoints())) {
                    for (SourcePoint sourcePoint : sourceUnit.getSourcePoints()) {
                        if (StringUtils.isNotBlank(sourcePoint.getName())) {
                            sourcePointMapper.insertSourcePoint(sourcePoint);
                            SourceUnitRefPoint requestSourceUnitRefPoint = new SourceUnitRefPoint();
                            requestSourceUnitRefPoint.setUnitId(sourceUnit.getId());
                            requestSourceUnitRefPoint.setPointId(sourcePoint.getId());
                            sourceUnitRefPointMapper.insertSourceUnitRefPoint(requestSourceUnitRefPoint);
                        }
                    }
                }
            }
        }
        return sourceDomain;
    }

    @Override
    public void exportOutTemplate(HttpServletResponse response, List<Long> ids) {
        List<SourceDomain> sourceDomains = sourceDomainMapper.selectSourceDomainByIds(ids);
        if (CollectionUtils.isNotEmpty(sourceDomains) && sourceDomains.get(0).getType() != null && 1 == sourceDomains.get(0).getType()) {
            ExcelUtil<SourceDomainUnitPointNoCollegeVo> excelUtil = new ExcelUtil<>(SourceDomainUnitPointNoCollegeVo.class);
            List<SourceDomainUnitPointNoCollegeVo> volist = new ArrayList<>();
            if (CollectionUtils.isNotEmpty(sourceDomains)) {
                for (SourceDomain sourceDomain : sourceDomains) {
                    if (CollectionUtils.isNotEmpty(sourceDomain.getSourceUnits())) {
                        for (SourceUnit sourceUnit : sourceDomain.getSourceUnits()) {
                            if (CollectionUtils.isNotEmpty(sourceUnit.getSourcePoints())) {
                                for (SourcePoint sourcePoint : sourceUnit.getSourcePoints()) {
                                    SourceDomainUnitPointNoCollegeVo vo = new SourceDomainUnitPointNoCollegeVo();
                                    vo.setVersion(sourceDomain.getVersion());
                                    vo.setDomainName(sourceDomain.getName());
                                    vo.setUnitName(sourceUnit.getName());
                                    vo.setPointName(sourcePoint.getName());
                                    volist.add(vo);
                                }
                            }
                        }
                    }
                }
            }
            excelUtil.exportExcel(response, volist, "知识体系");
        }else {
            ExcelUtil<SourceDomainUnitPointVo> excelUtil = new ExcelUtil<>(SourceDomainUnitPointVo.class);
            List<SourceDomainUnitPointVo> volist = new ArrayList<>();
            if (CollectionUtils.isNotEmpty(sourceDomains)) {
                CustomDept sysDept = new CustomDept();
                List<SysDept> list = doinnerDeptService.list(sysDept).getData();
                Map<Long, String> deptIdNameMap = list.parallelStream().collect(Collectors.toMap(SysDept::getDeptId, SysDept::getDeptName));
                Map<Long, String> majorIdToNameMap = standardMajorMapper.selectStandardMajorList(null).parallelStream().collect(Collectors.toMap(StandardMajor::getId, StandardMajor::getName));
                Map<Long, String> categoryIdToNameMap = trainingSchemeCategoryMapper.selectTrainingSchemeCategoryList(null).parallelStream().collect(Collectors.toMap(TrainingSchemeCategory::getId, TrainingSchemeCategory::getName));
                for (SourceDomain sourceDomain : sourceDomains) {
                    if (CollectionUtils.isNotEmpty(sourceDomain.getSourceUnits())) {
                        for (SourceUnit sourceUnit : sourceDomain.getSourceUnits()) {
                            if (CollectionUtils.isNotEmpty(sourceUnit.getSourcePoints())) {
                                for (SourcePoint sourcePoint : sourceUnit.getSourcePoints()) {
                                    SourceDomainUnitPointVo vo = new SourceDomainUnitPointVo();
                                    vo.setCollegeName(sourceDomain.getCollegeId() == null ? "" : deptIdNameMap.get(sourceDomain.getCollegeId()));
                                    vo.setMajorName(sourceDomain.getMajorId() == null ? "" : majorIdToNameMap.get(sourceDomain.getMajorId()));
                                    vo.setCategoryName(sourceDomain.getCategoryId() == null ? "" : categoryIdToNameMap.get(sourceDomain.getCategoryId()));
                                    vo.setVersion(sourceDomain.getVersion());
                                    vo.setDomainName(sourceDomain.getName());
                                    vo.setUnitName(sourceUnit.getName());
                                    vo.setPointName(sourcePoint.getName());
                                    volist.add(vo);
                                }
                            }
                        }
                    }
                }
            }
            excelUtil.exportExcel(response, volist, "知识体系");
        }
    }

    @Override
    public void exportTemplate(HttpServletResponse response) {
        //ExcelUtil<SourceDomainUnitPointVo> excelUtil = new ExcelUtil<>(SourceDomainUnitPointVo.class);
        //excelUtil.exportExcel(response, new ArrayList<>(), "知识体系");
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("template/knowledge/knowledgeSource.xlsx");
        ExcelUtils.exportTemplate(inputStream,response);
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public Message exportInTemplate(MultipartFile file, Long collegeId, Integer type, Long categoryId, Long majorId, String version) {
        List<TreeTableVo> treeTableVos = ExcelUtils.readTreeTable(file,   0 , 1, 2);
        if (CollectionUtils.isNotEmpty(treeTableVos)){
            List<KnowledgeTreeVo> treeVos = treeTableVos.parallelStream().map(TreeTableVo::toKnowledgeTreeVo)
                    .filter(k->ObjectUtils.isNotEmpty(k.getName())).collect(Collectors.toList());
            if(ObjectUtils.isEmpty(treeVos)){
               return Message.error(DomainExceptionConstant.DATA_IMPORT_TEMPLATE_ERROR);
            }
            //CustomDept sysDept = new CustomDept();
            //List<SysDept> list = doinnerDeptService.list(sysDept).getData();
            //Map<String, Long> deptIdNameMap = list.parallelStream().collect(Collectors.toMap(SysDept::getDeptName, SysDept::getDeptId, (a, b) -> a));
            for (KnowledgeTreeVo treeVo : treeVos) {
                SourceDomain sourceDomain = new SourceDomain();
                sourceDomain.setName(treeVo.getName());
                //sourceDomain.setCollegeId(deptIdNameMap.get(collegeName));
                sourceDomain.setCollegeId(collegeId);
                sourceDomain.setCategoryId(categoryId);
                sourceDomain.setMajorId(majorId);
                sourceDomain.setVersion(version);
                setType(sourceDomain, type);
                knowledgeTreeVoToSourceDomain(sourceDomain,treeVo);
                addOrUpdateSourceUnitAndPoint2(sourceDomain);
            }
        }
        return Message.success();
    }


    private void knowledgeTreeVoToSourceDomain(SourceDomain sourceDomain,KnowledgeTreeVo knowledgeTreeVo){
        if (CollectionUtils.isNotEmpty(knowledgeTreeVo.getChildren())){
            List<SourceUnit> sourceUnits = new ArrayList<>();
            for (KnowledgeTreeVo child : knowledgeTreeVo.getChildren()) {
                SourceUnit sourceUnit = new SourceUnit();
                sourceUnit.setName(child.getName());
                if (CollectionUtils.isNotEmpty(child.getChildren())){
                    List<SourcePoint> sourcePoints = new ArrayList<>();
                    for (KnowledgeTreeVo childChild : child.getChildren()) {
                        SourcePoint sourcePoint = new SourcePoint();
                        sourcePoint.setName(childChild.getName());
                        sourcePoints.add(sourcePoint);
                    }
                    sourceUnit.setSourcePoints(sourcePoints);
                }
                sourceUnits.add(sourceUnit);
            }
            sourceDomain.setSourceUnits(sourceUnits);
        }
    }



    /*@Override
    @Transactional(rollbackFor = Exception.class)
    public void exportInTemplate(MultipartFile file, Integer type) {
        ExcelUtil<SourceDomainUnitPointVo> excelUtil = new ExcelUtil<>(SourceDomainUnitPointVo.class);
        try {
            List<SourceDomainUnitPointVo> sourceDomainUnitPointVos = excelUtil.importExcel(file.getInputStream());
            CustomDept sysDept = new CustomDept();
            List<SysDept> list = doinnerDeptService.list(sysDept).getData();
            Map<String, Long> deptIdNameMap = list.parallelStream().collect(Collectors.toMap(SysDept::getDeptName, SysDept::getDeptId, (a, b) -> a));
            Map<String, Long> majorIdToNameMap = standardMajorMapper.selectStandardMajorList(null).parallelStream().collect(Collectors.toMap(StandardMajor::getName, StandardMajor::getId, (a, b) -> a));
            Map<String, Long> categoryIdToNameMap = trainingSchemeCategoryMapper.selectTrainingSchemeCategoryList(null).parallelStream().collect(Collectors.toMap(TrainingSchemeCategory::getName, TrainingSchemeCategory::getId));
            List<SourceDomain> sourceDomains = convertToLevelStructure(sourceDomainUnitPointVos, deptIdNameMap, majorIdToNameMap, categoryIdToNameMap);
            if (CollectionUtils.isNotEmpty(sourceDomains)) {
                for (SourceDomain sourceDomain : sourceDomains) {
                    setType(sourceDomain, type);
                    addOrUpdateSourceUnitAndPoint2(sourceDomain);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("导入知识体系数据异常：" + e.getMessage());
        }
    }*/

    private void setType(SourceDomain sourceDomain, Integer type) {
        sourceDomain.setType(type);
        if (ObjectUtils.isNotEmpty(sourceDomain.getSourceUnits())) {
            for (SourceUnit sourceUnit : sourceDomain.getSourceUnits()) {
                sourceUnit.setType(type);
                if (ObjectUtils.isNotEmpty(sourceUnit.getSourcePoints())) {
                    for (SourcePoint sourcePoint : sourceUnit.getSourcePoints()) {
                        sourcePoint.setType(type);
                    }
                }
            }
        }
    }

    /**
     * 将List<SourceDomainUnitPointVo>转换为层级结构的List<SourceDomain>
     *
     * @param voList 原始数据列表
     * @return 转换后的层级对象列表
     */
    private List<SourceDomain> convertToLevelStructure(List<SourceDomainUnitPointVo> voList, Map<String, Long> deptIdNameMap,
                                                       Map<String, Long> majorIdToNameMap, Map<String, Long> categoryIdToNameMap) {
        if (voList == null || voList.isEmpty()) {
            return new ArrayList<>();
        }
        // 使用Map来存储层级关系，避免重复创建对象
        Map<String, SourceDomain> domainMap = new HashMap<>();
        Map<String, SourceUnit> unitMap = new HashMap<>();
        for (SourceDomainUnitPointVo vo : voList) {
            String domainName = vo.getDomainName();
            String unitName = vo.getUnitName();
            String pointName = vo.getPointName();
            // 创建或获取Domain对象
            SourceDomain domain = domainMap.computeIfAbsent(domainName,
                    k -> new SourceDomain(k));
            // 设置学院和专业
            domain.setCollegeId(deptIdNameMap.get(vo.getCollegeName()));
            domain.setMajorId(majorIdToNameMap.get(vo.getMajorName()));
            domain.setCategoryId(categoryIdToNameMap.get(vo.getCategoryName()));
            domain.setVersion(vo.getVersion());
            // 创建或获取Unit对象
            String unitKey = domainName + "_" + unitName;
            SourceUnit unit = unitMap.computeIfAbsent(unitKey,
                    k -> {
                        SourceUnit sourceUnit = new SourceUnit(unitName);
                        domain.getSourceUnits().add(sourceUnit);
                        return sourceUnit;
                    });
            // 创建Point对象并添加到Unit中
            SourcePoint point = new SourcePoint(pointName);
            unit.getSourcePoints().add(point);
        }
        // 返回结果列表
        return new ArrayList<>(domainMap.values());
    }

    @Override
    @Transactional(rollbackFor = {Exception.class})
    public SourceUnit addUnit(SourceUnit sourceUnit) {
        sourceUnitMapper.insertSourceUnit(sourceUnit);
        SourceDomainRefUnit requestSourceDomainRefUnit = new SourceDomainRefUnit();
        requestSourceDomainRefUnit.setDomainId(sourceUnit.getDomainId());
        requestSourceDomainRefUnit.setUnitId(sourceUnit.getId());
        sourceDomainRefUnitMapper.insertSourceDomainRefUnit(requestSourceDomainRefUnit);
        return sourceUnit;
    }

    @Override
    @Transactional(rollbackFor = {Exception.class})
    public SourceUnit editUnit(SourceUnit sourceUnit) {
        List<CourseDomainRefSourceUnit> courseDomainRefSourceUnits = courseDomainRefSourceUnitMapper.selectCourseDomainRefSourceUnitByUnitIds(new Long[]{sourceUnit.getId()});
        if (CollectionUtils.isNotEmpty(courseDomainRefSourceUnits)) {
            List<Long> courseIds = courseDomainRefSourceUnits.stream().map(CourseDomainRefSourceUnit::getCourseId).collect(Collectors.toList());
            List<Course> courses = courseMapper.selectCoursesByIds(courseIds);
            throw new RuntimeException("知识单元已被课程引用:" + courses.stream().map(Course::getName).collect(Collectors.joining(",")) + "不能修改！");
        }
        sourceUnitMapper.updateSourceUnit(sourceUnit);
        return sourceUnit;
    }

    @Override
    @Transactional(rollbackFor = {Exception.class})
    public void removeUnit(Long[] ids) {
        List<CourseDomainRefSourceUnit> courseDomainRefSourceUnits = courseDomainRefSourceUnitMapper.selectCourseDomainRefSourceUnitByUnitIds(ids);
        if (CollectionUtils.isNotEmpty(courseDomainRefSourceUnits)) {
            List<Long> courseIds = courseDomainRefSourceUnits.stream().map(CourseDomainRefSourceUnit::getCourseId).collect(Collectors.toList());
            List<Course> courses = courseMapper.selectCoursesByIds(courseIds);
            throw new RuntimeException("知识单元已被课程引用:" + courses.stream().map(Course::getName).collect(Collectors.joining(",")) + "不能删除！");
        }
        List<SourceUnitRefPoint> sourceUnitRefPoints = sourceUnitRefPointMapper.selectSourceUnitRefPointByUnitIds(Arrays.stream(ids).collect(Collectors.toList()));
        if (CollectionUtils.isNotEmpty(sourceUnitRefPoints)) {
            List<Long> pointIds = sourceUnitRefPoints.stream().map(a -> a.getPointId()).collect(Collectors.toList());
            sourcePointMapper.deleteSourcePointByIds(pointIds.toArray(Long[]::new));
            sourceUnitRefPointMapper.deleteSourceUnitRefPointByUnitIds(ids);
        }
        sourceUnitMapper.deleteSourceUnitByIds(ids);
        sourceDomainRefUnitMapper.deleteSourceDomainRefUnitByUnitIds(ids);
    }

    @Override
    @Transactional(rollbackFor = {Exception.class})
    public SourcePoint addPoint(SourcePoint sourcePoint) {
        sourcePointMapper.insertSourcePoint(sourcePoint);
        SourceUnitRefPoint sourceUnitRefPoint = new SourceUnitRefPoint();
        sourceUnitRefPoint.setPointId(sourcePoint.getId());
        sourceUnitRefPoint.setUnitId(sourcePoint.getUnitId());
        sourceUnitRefPointMapper.insertSourceUnitRefPoint(sourceUnitRefPoint);
        return sourcePoint;
    }

    @Override
    @Transactional(rollbackFor = {Exception.class})
    public SourcePoint editPoint(SourcePoint sourcePoint) {
        List<CourseUnitRefSourcePoint> courseUnitRefSourcePoints = courseUnitRefSourcePointMapper.selectCourseUnitRefSourcePointByPointIds(new Long[]{sourcePoint.getId()});
        if (CollectionUtils.isNotEmpty(courseUnitRefSourcePoints)) {
            List<Long> courseIds = courseUnitRefSourcePoints.stream().map(CourseUnitRefSourcePoint::getCourseId).collect(Collectors.toList());
            List<Course> courses = courseMapper.selectCoursesByIds(courseIds);
            throw new RuntimeException("知识点已被课程引用:" + courses.stream().map(Course::getName).collect(Collectors.joining(",")) + "不能修改！");
        }
        sourcePointMapper.updateSourcePoint(sourcePoint);
        return sourcePoint;
    }

    @Override
    @Transactional(rollbackFor = {Exception.class})
    public void removePoint(Long[] ids) {
        List<CourseUnitRefSourcePoint> courseUnitRefSourcePoints = courseUnitRefSourcePointMapper.selectCourseUnitRefSourcePointByPointIds(ids);
        if (CollectionUtils.isNotEmpty(courseUnitRefSourcePoints)) {
            List<Long> courseIds = courseUnitRefSourcePoints.stream().map(CourseUnitRefSourcePoint::getCourseId).collect(Collectors.toList());
            List<Course> courses = courseMapper.selectCoursesByIds(courseIds);
            throw new RuntimeException("知识点已被课程引用:" + courses.stream().map(Course::getName).collect(Collectors.joining(",")) + "不能删除！");
        }
        sourceUnitRefPointMapper.deleteSourceUnitRefPointByPointIds(ids);
        sourcePointMapper.deleteSourcePointByIds(ids);
    }

    @Override
    @Transactional(rollbackFor = {Exception.class})
    public void referenceDomain(KnowledgeDomainReferenceVo referenceVo) {
        //查询知识领域、知识单元、知识点
        if (ObjectUtils.isEmpty(referenceVo) || ObjectUtils.isEmpty(referenceVo.getSourceDomainIdList())) {
            return;
        }
        List<SourceDomain> sourceDomains = sourceDomainMapper.selectSourceDomainByIds(referenceVo.getSourceDomainIdList());
        //生成新的知识领域、知识单元知识点
        List<SourceDomain> _newDomainList = createNewDomain(referenceVo, sourceDomains);
        //保存知识领域
        for (SourceDomain _newDomain : _newDomainList) {
            addOrUpdateSourceUnitAndPoint2(_newDomain);
        }
    }

    @Override
    public List<Long>  KnowledgePointCheckAllBySchemeId(Long schemeId) {
        //查询培养方案下的课程
        List<TrainingSchemeRefCourse> trainingSchemeRefCourses = trainingSchemeRefCourseMapper.selectTrainingSchemeRefCourseByTrainingSchemeVoId(schemeId);
        return trainingSchemeRefCourses.stream().map(TrainingSchemeRefCourse::getCourseId).collect(Collectors.toList());
    }

    private List<SourceDomain> createNewDomain(KnowledgeDomainReferenceVo referenceVo, List<SourceDomain> sourceDomains) {
        List<SourceDomain> _newDomainList = new ArrayList<>();
        for (SourceDomain sourceDomain : sourceDomains) {
            SourceDomain _newDomain = new SourceDomain();
            BeanUtils.copyProperties(sourceDomain, _newDomain);
            _newDomain.setId(null);
            _newDomain.setCategoryId(referenceVo.getCategoryId());
            _newDomain.setCollegeId(referenceVo.getCollegeId());
            _newDomain.setMajorId(referenceVo.getMajorId());
            _newDomain.setType(2);
            _newDomain.setSourceId(sourceDomain.getId());
            UserUtils.clearAndRefreshObj(_newDomain);
            createNewUnit(sourceDomain, _newDomain);
            _newDomainList.add(_newDomain);
        }
        return _newDomainList;
    }

    private void createNewUnit(SourceDomain sourceDomain, SourceDomain _newDomain) {
        if (ObjectUtils.isNotEmpty(sourceDomain.getSourceUnits())) {
            ArrayList<SourceUnit> _newUnitList = new ArrayList<>();
            for (SourceUnit sourceUnit : sourceDomain.getSourceUnits()) {
                SourceUnit _newUnit = new SourceUnit();
                BeanUtils.copyProperties(sourceUnit, _newUnit);
                _newUnit.setType(2);
                _newUnit.setSourceId(sourceUnit.getId());
                _newUnit.setId(null);
                UserUtils.clearAndRefreshObj(_newDomain);
                createNewPoint(sourceUnit, _newUnit);
                _newUnitList.add(_newUnit);
            }
            _newDomain.setSourceUnits(_newUnitList);
        }
    }

    private void createNewPoint(SourceUnit sourceUnit, SourceUnit _newUnit) {
        if (ObjectUtils.isNotEmpty(sourceUnit.getSourcePoints())) {
            ArrayList<SourcePoint> _newPointList = new ArrayList<>();
            for (SourcePoint sourcePoint : sourceUnit.getSourcePoints()) {
                SourcePoint _newPoint = new SourcePoint();
                BeanUtils.copyProperties(sourcePoint, _newPoint);
                _newPoint.setType(2);
                _newPoint.setSourceId(sourcePoint.getId());
                _newPoint.setId(null);
                UserUtils.clearAndRefreshObj(_newPoint);
                _newPointList.add(_newPoint);
            }
            _newUnit.setSourcePoints(_newPointList);
        }
    }


}
