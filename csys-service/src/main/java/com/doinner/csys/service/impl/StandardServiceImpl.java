package com.doinner.csys.service.impl;

import com.doinner.common.core.constant.SymbolConstants;
import com.doinner.common.core.domain.Message;
import com.doinner.common.core.exception.UpdateDataException;
import com.doinner.common.core.utils.PageUtils;
import com.doinner.common.core.utils.poi.ExcelUtil;
import com.doinner.csys.constant.DomainExceptionConstant;
import com.doinner.csys.constant.DomainFieldConstant;
import com.doinner.csys.dao.*;
import com.doinner.csys.domain.*;
import com.doinner.csys.domain.vo.*;
import com.doinner.csys.io.utils.ExcelUtils;
import com.doinner.csys.io.utils.TreeEntityUtils;
import com.doinner.csys.service.KnowledgeSourceService;
import com.doinner.csys.service.StandardService;
import com.doinner.csys.utils.MapToObjectUtil;
import com.doinner.csys.utils.TreeBuilderUtils;
import com.doinner.csys.utils.UserUtils;
import com.doinner.system.domain.custom.CustomDept;
import com.doinner.system.domain.entity.SysDept;
import com.doinner.system.service.DoinnerDeptService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
public class StandardServiceImpl implements StandardService {

    private static final Logger log = LoggerFactory.getLogger(StandardServiceImpl.class);
    @Resource
    private StandardAbilityMapper standardAbilityMapper;
    @Resource
    private StandardCultivationTargetMapper standardCultivationTargetMapper;
    @Resource
    private StandardGraduationMapper standardGraduationMapper;
    @Resource
    private StandardCultivationMapper standardCultivationMapper;
    @Resource
    private StandardGraduationRefCultivationTargetMapper standardGraduationRefCultivationTargetMapper;
    @Resource
    private StandardCultivationRefGraduationMapper standardCultivationRefGraduationMapper;
    @Resource
    private KnowledgeUnitRefStdCultivationMapper knowledgeUnitRefStdCultivationMapper;
    @Resource
    private StandardAbilityLevelMapper standardAbilityLevelMapper;

    @Resource
    private CourseRefGraduationMapper courseRefGraduationMapper;

    @Resource
    private CourseRefAbilityMapper courseRefAbilityMapper;
    @Resource
    private CourseRefQualityMapper courseRefQualityMapper;

    @Resource
    private DoinnerDeptService doinnerDeptService;
    @Resource
    private StandardMajorMapper standardMajorMapper;

    @Resource
    private TrainingSchemeCategoryMapper trainingSchemeCategoryMapper;

    @Resource
    private AbilityRefGraduationMapper abilityRefGraduationMapper;
    @Autowired
    private CourseMapper courseMapper;

    @Resource
    private SourceDomainMapper sourceDomainMapper;

    @Resource
    private SourceUnitMapper sourceUnitMapper;

    @Resource
    private SourceDomainRefUnitMapper sourceDomainRefUnitMapper;

    @Resource
    private KnowledgeSourceService knowledgeSourceService;




    /*
     * -------------------------------------------
     * 能力素质---代码段开始
     * -------------------------------------------
     */

    /**
     * 查询能力素质列表
     *
     * @param standardAbility 能力素质
     * @return 能力素质集合
     */
    @Override
    public List<StandardAbility> selectStandardAbilityList(StandardAbility standardAbility) {
        return standardAbilityMapper.selectStandardAbilityList(standardAbility);
    }

    @Override
    public List<StandardTreeVo> selectStdAbilityTree(StandardAbility standardAbility) {
        List<StandardAbility> standardAbilities = standardAbilityMapper.selectStandardAbilityList(standardAbility);
        List<StandardTreeVo> vos = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(standardAbilities)) {
            for (StandardAbility ability : standardAbilities) {
                StandardTreeVo vo = new StandardTreeVo();
                vo.setId(ability.getId());
                vo.setName(ability.getName());
                vo.setParentId(ability.getParentId());
                vo.setCollegeId(ability.getCollegeId());
                vo.setCategoryId(ability.getCategoryId());
                vo.setMajorId(ability.getMajorId());
                vos.add(vo);
            }
        }
        return TreeBuilderUtils.buildRootTree(vos);
    }

    /**
     * 查询能力素质
     *
     * @param id 能力素质
     * @return 能力素质
     */
    @Override
    public StandardAbility selectStandardAbilityById(Long id) {
        StandardAbility standardAbility = standardAbilityMapper.selectStandardAbilityById(id);
        if (standardAbility.getParentId().equals(DomainFieldConstant.ROOT_NODE_LONG_ID)) {
            return standardAbility;
        }
        StandardAbility parent = standardAbilityMapper.selectStandardAbilityById(standardAbility.getParentId());
        standardAbility.setParentName(parent.getName());
        if (null != id) {
            List<StandardAbilityLevel> levels = standardAbilityLevelMapper.selectStandardAbilityLevelByAbilityId(id);
            standardAbility.setLevels(levels);
        }
        return standardAbility;
    }


    private void checkAbility(StandardAbility standardAbility){
        StandardAbility param = new StandardAbility();
        param.setMajorId(standardAbility.getMajorId());
        param.setType(standardAbility.getType());
        param.setVersion(standardAbility.getVersion());
        List<StandardAbility> standardAbilities = standardAbilityMapper.selectStandardAbilityList(param);
        if (CollectionUtils.isNotEmpty(standardAbilities)) {
            for (StandardAbility ability : standardAbilities) {
                if (ability.getSourceId() != null){
                    throw new RuntimeException("该专业已存在下发数据不允许新增！");
                }
            }
        }
    }

    /**
     * 通过模板新增能力素质
     */
    @Override
    @Transactional(rollbackFor = {Exception.class})
    public StandardAbility insertStandardAbility(StandardAbility standardAbility) {
        checkAbility(standardAbility);
        List<StandardAbility> abilityList = standardAbilityMapper.selectAllAbilityById(standardAbility.getAbilitySystemId());
        // 判断是否魔板加
        if (CollectionUtils.isNotEmpty(abilityList)) {
            Map<Long, List<StandardAbilityLevel>> levelMap = groupLevel(abilityList);
            //拿到摸版list 让所有的id为空 替换根节点的名称 记录旧id
            for (StandardAbility ability : abilityList) {
                List<StandardAbilityLevel> levels = levelMap.get(ability.getId());
                if (ability.getParentId() == -1L) {
                    ability.setName(standardAbility.getName());
                }
                ability.setOldId(ability.getId());
                ability.setType(standardAbility.getType());
                ability.setId(null);
                ability.setMajorId(standardAbility.getMajorId());
                ability.setSubMajorId(standardAbility.getSubMajorId());
                ability.setClassId(standardAbility.getClassId());
                UserUtils.reflash(ability);
                standardAbilityMapper.insertStandardAbility(ability);
                if (ability.getParentId() == -1L) {
                    standardAbility.setId(ability.getId());
                }

                if (CollectionUtils.isNotEmpty(levels)) {
                    //清空能力等级id，重新添加能力等级
                    for (StandardAbilityLevel l : levels) {
                        l.setAbilityId(standardAbility.getId());
                        l.setCheckflag(0);
                        l.setId(null);
                        standardAbilityLevelMapper.insertStandardAbilityLevel(l);
                    }
                }
            }
            //存储新旧ID映射
            Map<Long, Long> newMap = new HashMap<>();
            newMap.put(-1L, -1L);
            for (StandardAbility ability : abilityList) {
                newMap.put(ability.getOldId(), ability.getId());
            }
            //查询map 替换pId 替换URL
            for (StandardAbility ability : abilityList) {
                Long pId = ability.getParentId();
                Long newPid = newMap.get(pId);
                ability.setParentId(newPid);
                String[] url = ability.getUrl().split(",");
                StringBuilder nUrl = new StringBuilder();
                for (String s : url) {
                    Long nId = newMap.get(Long.valueOf(s));
                    nUrl.append(",").append(nId);
                }
                nUrl = new StringBuilder(nUrl.substring(1));
                ability.setUrl(nUrl.toString());
                standardAbilityMapper.updateStandardAbility(ability);

                //能力id被替换，需同步修改能力等级中的能力id
                List<StandardAbilityLevel> oldLevel = levelMap.get(ability.getOldId());
                if (CollectionUtils.isNotEmpty(oldLevel)) {
                    oldLevel.forEach(l -> l.setAbilityId(ability.getId()));
                    standardAbilityLevelMapper.updateStandardAbilityLevels(oldLevel);
                }
            }
        } else {
            if (standardAbility.getParentId() == null || -1 == standardAbility.getParentId()) {
                standardAbility.setParentId(DomainFieldConstant.ROOT_NODE_LONG_ID);
                standardAbility.setUrl("-1");
                standardAbility.setLevel(1);
                standardAbility.setLeaf(DomainFieldConstant.TREE_LEAF_VALUE);
            } else {
                Long pId = standardAbility.getParentId();
                StandardAbility pAbility = standardAbilityMapper.selectStandardAbilityById(pId);
                Integer level = pAbility.getLevel();
                String url = pAbility.getUrl();
                if (4 == level) {
                    throw new UpdateDataException("叶子节点不允许添加下级");
                }
                standardAbility.setLevel(level + 1);
                standardAbility.setParentId(pId);
                standardAbility.setUrl(url + "," + pId);
                standardAbility.setLeaf(DomainFieldConstant.TREE_LEAF_VALUE);
                pAbility.setLeaf(DomainFieldConstant.TREE_FOLDER_VALUE);
                standardAbilityMapper.updateStandardAbility(pAbility);
            }
            UserUtils.reflash(standardAbility);
            standardAbilityMapper.insertStandardAbility(standardAbility);
            List<StandardAbilityLevel> levels = standardAbility.getLevels();
            if (CollectionUtils.isNotEmpty(levels)) {
                levels.forEach(l -> l.setAbilityId(standardAbility.getId()));
                standardAbilityLevelMapper.insertStandardAbilityLevels(levels);
            }
        }
        if (CollectionUtils.isNotEmpty(standardAbility.getGraduationIds())) {
            for (Long graduationId : standardAbility.getGraduationIds()) {
                AbilityRefGraduation abilityRefGraduation = new AbilityRefGraduation();
                abilityRefGraduation.setAbilityId(standardAbility.getId());
                abilityRefGraduation.setGraduationId(graduationId);
                abilityRefGraduationMapper.insert(abilityRefGraduation);
            }
        }
        return standardAbility;
    }

    /**
     * 分组查询能力等级
     *
     * @param abilityList
     * @return
     */
    public Map<Long, List<StandardAbilityLevel>> groupLevel(List<StandardAbility> abilityList) {
        List<StandardAbility> s = abilityList.stream()
                .filter(a -> a.getLeaf() == 1).collect(Collectors.toList());
        List<Long> ids = s.stream()
                .map(a -> a.getId()).collect(Collectors.toList());
        List<StandardAbilityLevel> standardAbilityLevels = standardAbilityLevelMapper.selectStandardAbilityLevelByAbilityIds(ids);
        Map<Long, List<StandardAbilityLevel>> levelMap = new HashMap<>();
        if (CollectionUtils.isNotEmpty(standardAbilityLevels)) {
            levelMap = standardAbilityLevels.stream().collect(Collectors.groupingBy(StandardAbilityLevel::getAbilityId));
        }
        return levelMap;
    }

    @Override
    @Transactional(rollbackFor = {Exception.class})
    public StandardAbilityLevel saveStdAbilityLevel(StandardAbilityLevel level) {
        if (null != level.getId()) {
            standardAbilityLevelMapper.updateStandardAbilityLevel(level);
        } else {
            standardAbilityLevelMapper.insertStandardAbilityLevel(level);
        }
        return level;
    }

    @Override
    public void exportAbility(HttpServletResponse response, List<Long> ids) {
        List<StandardAbility> standardAbilities = standardAbilityMapper.selectStandardAbilityByIds(ids, 2);
        ExcelUtil<AbilityExcelVo> excelUtil = new ExcelUtil<>(AbilityExcelVo.class);
        List<AbilityExcelVo> volist = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(standardAbilities)) {
            List<SysDept> list = doinnerDeptService.list(new CustomDept()).getData();
            Map<Long, String> deptIdNameMap = list.parallelStream().collect(Collectors.toMap(SysDept::getDeptId, SysDept::getDeptName));
            Map<Long, String> majorIdToNameMap = standardMajorMapper.selectStandardMajorList(null).parallelStream().collect(Collectors.toMap(StandardMajor::getId, StandardMajor::getName));
            Map<Long, String> categoryIdToNameMap = trainingSchemeCategoryMapper.selectTrainingSchemeCategoryList(null).parallelStream().collect(Collectors.toMap(TrainingSchemeCategory::getId, TrainingSchemeCategory::getName));
            for (StandardAbility standardAbility1 : standardAbilities) {
                // 二级
                List<StandardAbility> standardAbilities2 = standardAbilityMapper.selectStandardAbilityByParentId(standardAbility1.getId());
                if (CollectionUtils.isNotEmpty(standardAbilities2)) {
                    for (StandardAbility ability2 : standardAbilities2) {
                        AbilityExcelVo excelVo = new AbilityExcelVo();
                        excelVo.setCollegeName(deptIdNameMap.get(standardAbility1.getCollegeId()));
                        excelVo.setMajorName(majorIdToNameMap.get(standardAbility1.getMajorId()));
                        // excelVo.setSubMajorName(majorIdToNameMap.get(standardAbility1.getSubMajorId()));
                        excelVo.setCategoryName(categoryIdToNameMap.get(standardAbility1.getCategoryId()));
                        excelVo.setVersion(standardAbility1.getVersion());
                        excelVo.setName(standardAbility1.getName());
                        excelVo.setSecondName(ability2.getName());
                        volist.add(excelVo);
                    }
                }
            }
        }
        excelUtil.exportExcel(response, volist, "能力图谱");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Message importAbility(MultipartFile file,Long collegeId, Long categoryId, Long majorId, Long subMajorId, String version) {
        StandardAbility param = new StandardAbility();
        param.setMajorId(majorId);
        param.setType(2);
        param.setVersion(version);
        List<StandardAbility> standardAbilities = standardAbilityMapper.selectStandardAbilityList(param);
        if (CollectionUtils.isNotEmpty(standardAbilities)) {
            for (StandardAbility ability : standardAbilities) {
                if (ability.getSourceId() != null){
                    throw new RuntimeException("该专业已存在下发数据不允许导入！");
                }
            }
        }

        List<TreeTableVo> treeTableVos = ExcelUtils.readTreeTable(file, 0 , 1);
        // List<SysDept> list = doinnerDeptService.list(new CustomDept()).getData();
        // Map<String, Long> deptIdNameMap = list.parallelStream().collect(Collectors.toMap(SysDept::getDeptName, SysDept::getDeptId, (a, b) -> a));
        if (CollectionUtils.isNotEmpty(treeTableVos)) {
            List<KnowledgeTreeVo> treeVos = treeTableVos.parallelStream().map(TreeTableVo::toAbilityTreeVo)
                    .filter(t->ObjectUtils.isNotEmpty(t.getName())).collect(Collectors.toList());
            if(ObjectUtils.isEmpty(treeVos)){
                return Message.error(DomainExceptionConstant.DATA_IMPORT_TEMPLATE_ERROR);
            }
            for (KnowledgeTreeVo treeVo : treeVos) {
                if (CollectionUtils.isNotEmpty(treeVo.getChildren())) {
                    //for (KnowledgeTreeVo child : treeVo.getChildren()) {
                        StandardAbility standardAbility = new StandardAbility();
                        standardAbility.setName(treeVo.getName());
                        // 能力 4
                        standardAbility.setType(2);
                        // standardAbility.setCollegeId(deptIdNameMap.get(treeVo.getCollegeName()));
                        standardAbility.setCollegeId(collegeId);
                        standardAbility.setCategoryId(categoryId);
                        standardAbility.setMajorId(majorId);
                        standardAbility.setSubMajorId(subMajorId);
                        standardAbility.setVersion(version);
                        treeToStandardAbility(standardAbility,treeVo);
                        updateStdAbilityTree(standardAbility);
                    //}
                }
            }
        }
        return Message.success();
    }

    /*public void importAbility(MultipartFile file, Long categoryId, Long majorId, Long subMajorId, String version) {
        ExcelUtil<AbilityExcelVo> excelUtil = new ExcelUtil<>(AbilityExcelVo.class);
        try {
            List<AbilityExcelVo> abilityExcelVos = excelUtil.importExcel(file.getInputStream());
            List<SysDept> list = doinnerDeptService.list(new CustomDept()).getData();
            Map<String, Long> deptIdNameMap = list.parallelStream().collect(Collectors.toMap(SysDept::getDeptName, SysDept::getDeptId, (a, b) -> a));
            Map<String, Long> majorIdToNameMap = standardMajorMapper.selectStandardMajorList(null).parallelStream().collect(Collectors.toMap(StandardMajor::getName, StandardMajor::getId, (a, b) -> a));
            Map<String, Long> categoryIdToNameMap = trainingSchemeCategoryMapper.selectTrainingSchemeCategoryList(null).parallelStream().collect(Collectors.toMap(TrainingSchemeCategory::getName, TrainingSchemeCategory::getId));
            if (CollectionUtils.isNotEmpty(abilityExcelVos)) {
                List<StandardAbility> standardAbilities = convertToTreeSimple(abilityExcelVos,
                        deptIdNameMap, majorIdToNameMap, categoryIdToNameMap);
                if (CollectionUtils.isNotEmpty(standardAbilities)) {
                    for (StandardAbility standardAbility : standardAbilities) {
                        // 能力 2
                        standardAbility.setType(2);
                        standardAbility.setCategoryId(categoryId);
                        standardAbility.setMajorId(majorId);
                        standardAbility.setSubMajorId(subMajorId);
                        standardAbility.setVersion(version);
                        updateStdAbilityTree(standardAbility);
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("能力图谱数据异常：" + e.getMessage());
        }
    }*/

    /**
     * 将excel里面的数据 转实体表数据
     *
     * @param excelList
     * @param deptIdNameMap
     * @param majorIdToNameMap
     * @param categoryIdToNameMap
     * @return
     */
    private List<StandardAbility> convertToTreeSimple(List<AbilityExcelVo> excelList,
                                                      Map<String, Long> deptIdNameMap,
                                                      Map<String, Long> majorIdToNameMap,
                                                      Map<String, Long> categoryIdToNameMap) {
        if (excelList == null || excelList.isEmpty()) {
            return new ArrayList<>();
        }
        // 创建所有节点
        Map<String, StandardAbility> nodeMap = new HashMap<>();
        for (AbilityExcelVo excel : excelList) {
            // 一级能力
            if (excel.getName() != null && !excel.getName().isEmpty()) {
                String key = "level1_" + excel.getName();
                StandardAbility root = new StandardAbility();
                root.setName(excel.getName());
                root.setParentId(-1L);
                root.setChildren(new ArrayList<>());
                //root.setVersion(excel.getVersion());
                root.setCollegeId(deptIdNameMap.get(excel.getCollegeName()));
                //root.setMajorId(majorIdToNameMap.get(excel.getMajorName()));
                //root.setSubMajorId(majorIdToNameMap.get(excel.getSubMajorName()));
                //root.setCategoryId(categoryIdToNameMap.get(excel.getCategoryName()));
                nodeMap.putIfAbsent(key, root);
            }
            // 二级能力
            if (excel.getSecondName() != null && !excel.getSecondName().isEmpty()) {
                String key = "level2_" + excel.getSecondName();
                StandardAbility second = new StandardAbility();
                second.setName(excel.getSecondName());
                second.setChildren(new ArrayList<>());
                //second.setRemark(excel.getSecondDescription());
                nodeMap.putIfAbsent(key, second);
            }
            // 三级能力
            /*if (excel.getThreeName() != null && !excel.getThreeName().isEmpty()) {
                String key = "level3_" + excel.getThreeName();
                StandardAbility three = new StandardAbility();
                three.setName(excel.getThreeName());
                three.setChildren(new ArrayList<>());
                three.setRemark(excel.getThreeDescription());
                nodeMap.putIfAbsent(key, three);
            }*/
        }
        // 建立父子关系
        for (AbilityExcelVo excel : excelList) {
            StandardAbility level1 = null;
            StandardAbility level2 = null;
            StandardAbility level3 = null;
            if (excel.getName() != null && !excel.getName().isEmpty()) {
                level1 = nodeMap.get("level1_" + excel.getName());
            }
            if (excel.getSecondName() != null && !excel.getSecondName().isEmpty()) {
                level2 = nodeMap.get("level2_" + excel.getSecondName());
            }
            /*if (excel.getThreeName() != null && !excel.getThreeName().isEmpty()) {
                level3 = nodeMap.get("level3_" + excel.getThreeName());
            }*/
            // 建立三级到二级的父子关系
            if (level3 != null && level2 != null) {
                level3.setParentId(level2.getId());
                if (level2.getChildren() == null) {
                    level2.setChildren(new ArrayList<>());
                }
                level2.getChildren().add(level3);
            }
            // 建立二级到一级的父子关系
            if (level2 != null && level1 != null) {
                level2.setParentId(level1.getId());
                if (level1.getChildren() == null) {
                    level1.setChildren(new ArrayList<>());
                }
                level1.getChildren().add(level2);
            }
        }
        // 返回根节点
        return nodeMap.values().stream()
                .filter(node -> node.getParentId() != null && node.getParentId() == -1L)
                .collect(Collectors.toList());
    }


    @Override
    public void exportAbilityTemplate(HttpServletResponse response) {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("template/standard/ability.xlsx");
        ExcelUtils.exportTemplate(inputStream, response);
    }


    @Override
    public void exportQuality(HttpServletResponse response, List<Long> ids) {
        List<StandardAbility> standardAbilities = standardAbilityMapper.selectStandardAbilityByIds(ids, 4);
        ExcelUtil<QualityExcelVo> excelUtil = new ExcelUtil<>(QualityExcelVo.class);
        List<QualityExcelVo> volist = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(standardAbilities)) {
            List<SysDept> list = doinnerDeptService.list(new CustomDept()).getData();
            Map<Long, String> deptIdNameMap = list.parallelStream().collect(Collectors.toMap(SysDept::getDeptId, SysDept::getDeptName));
            Map<Long, String> majorIdToNameMap = standardMajorMapper.selectStandardMajorList(null).parallelStream().collect(Collectors.toMap(StandardMajor::getId, StandardMajor::getName));
            Map<Long, String> categoryIdToNameMap = trainingSchemeCategoryMapper.selectTrainingSchemeCategoryList(null).parallelStream().collect(Collectors.toMap(TrainingSchemeCategory::getId, TrainingSchemeCategory::getName));
            for (StandardAbility standardAbility1 : standardAbilities) {
                // 二级
                List<StandardAbility> standardAbilities2 = standardAbilityMapper.selectStandardAbilityByParentId(standardAbility1.getId());
                if (CollectionUtils.isNotEmpty(standardAbilities2)) {
                    for (StandardAbility ability2 : standardAbilities2) {
                        QualityExcelVo excelVo = new QualityExcelVo();
                        excelVo.setCollegeName(deptIdNameMap.get(standardAbility1.getCollegeId()));
                        excelVo.setMajorName(majorIdToNameMap.get(standardAbility1.getMajorId()));
                        //excelVo.setSubMajorName(majorIdToNameMap.get(standardAbility1.getSubMajorId()));
                        excelVo.setCategoryName(categoryIdToNameMap.get(standardAbility1.getCategoryId()));
                        excelVo.setVersion(standardAbility1.getVersion());
                        excelVo.setName(standardAbility1.getName());
                        excelVo.setSecondName(ability2.getName());
                        volist.add(excelVo);
                    }
                }
            }
        }
        excelUtil.exportExcel(response, volist, "素质图谱");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Message importQuality(MultipartFile file,Long collegeId, Long categoryId, Long majorId, Long subMajorId, String version) {
        StandardAbility param = new StandardAbility();
        param.setMajorId(majorId);
        param.setType(4);
        param.setVersion(version);
        List<StandardAbility> standardAbilities = standardAbilityMapper.selectStandardAbilityList(param);
        if (CollectionUtils.isNotEmpty(standardAbilities)) {
            for (StandardAbility ability : standardAbilities) {
                if (ability.getSourceId() != null){
                    throw new RuntimeException("该专业已存在下发数据不允许导入！");
                }
            }
        }

        List<TreeTableVo> treeTableVos = ExcelUtils.readTreeTable(file, 0 , 1, 2);
        //List<SysDept> list = doinnerDeptService.list(new CustomDept()).getData();
        //Map<String, Long> deptIdNameMap = list.parallelStream().collect(Collectors.toMap(SysDept::getDeptName, SysDept::getDeptId, (a, b) -> a));
        if (CollectionUtils.isNotEmpty(treeTableVos)) {
            List<KnowledgeTreeVo> treeVos = treeTableVos.parallelStream().map(TreeTableVo::toQualityTreeVo)
                    .filter(s->ObjectUtils.isNotEmpty(s.getName())).collect(Collectors.toList());
            if(ObjectUtils.isEmpty(treeVos)){
                return Message.error(DomainExceptionConstant.DATA_IMPORT_TEMPLATE_ERROR);
            }
            for (KnowledgeTreeVo treeVo : treeVos) {
                if (CollectionUtils.isNotEmpty(treeVo.getChildren())) {
                    StandardAbility standardAbility = new StandardAbility();
                    standardAbility.setName(treeVo.getName());
                    // 素质 4
                    standardAbility.setType(4);
                    standardAbility.setCollegeId(collegeId);
                    //standardAbility.setCollegeId(deptIdNameMap.get(treeVo.getCollegeName()));
                    standardAbility.setCategoryId(categoryId);
                    standardAbility.setMajorId(majorId);
                    standardAbility.setSubMajorId(subMajorId);
                    standardAbility.setVersion(version);
                    treeToStandardAbility(standardAbility,treeVo);
                    updateStdAbilityTree(standardAbility);
                }
            }
        }
        return Message.success();
    }


    private void treeToStandardAbility(StandardAbility standardAbility,KnowledgeTreeVo treeVo){
        standardAbility.setName(treeVo.getName());
        if (CollectionUtils.isNotEmpty(treeVo.getChildren())) {
            List<StandardAbility> children = new ArrayList<>();
            for (KnowledgeTreeVo child : treeVo.getChildren()) {
                StandardAbility c = new StandardAbility();
                children.add(c);
                c.setName(child.getName());
                treeToStandardAbility(c,child);
            }
            standardAbility.setChildren(children);
        }
    }



    /*public void importQuality(MultipartFile file, Long categoryId, Long majorId, Long subMajorId, String version) {
        ExcelUtil<QualityExcelVo> excelUtil = new ExcelUtil<>(QualityExcelVo.class);
        try {
            List<QualityExcelVo> abilityExcelVos = excelUtil.importExcel(file.getInputStream());
            List<SysDept> list = doinnerDeptService.list(new CustomDept()).getData();
            Map<String, Long> deptIdNameMap = list.parallelStream().collect(Collectors.toMap(SysDept::getDeptName, SysDept::getDeptId, (a, b) -> a));
            Map<String, Long> majorIdToNameMap = standardMajorMapper.selectStandardMajorList(null).parallelStream().collect(Collectors.toMap(StandardMajor::getName, StandardMajor::getId, (a, b) -> a));
            Map<String, Long> categoryIdToNameMap = trainingSchemeCategoryMapper.selectTrainingSchemeCategoryList(null).parallelStream().collect(Collectors.toMap(TrainingSchemeCategory::getName, TrainingSchemeCategory::getId));
            if (CollectionUtils.isNotEmpty(abilityExcelVos)) {
                List<StandardAbility> standardAbilities = convertToTreeSimpleQuality(abilityExcelVos,
                        deptIdNameMap, majorIdToNameMap, categoryIdToNameMap);
                if (CollectionUtils.isNotEmpty(standardAbilities)) {
                    for (StandardAbility standardAbility : standardAbilities) {
                        // 素质 4
                        standardAbility.setType(4);
                        standardAbility.setCategoryId(categoryId);
                        standardAbility.setMajorId(majorId);
                        standardAbility.setSubMajorId(subMajorId);
                        standardAbility.setVersion(version);
                        updateStdAbilityTree(standardAbility);
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("素质图谱数据异常：" + e.getMessage());
        }
    }*/

    private List<StandardAbility> convertToTreeSimpleQuality(List<QualityExcelVo> excelList,
                                                             Map<String, Long> deptIdNameMap,
                                                             Map<String, Long> majorIdToNameMap,
                                                             Map<String, Long> categoryIdToNameMap) {
        if (excelList == null || excelList.isEmpty()) {
            return new ArrayList<>();
        }
        // 创建所有节点
        Map<String, StandardAbility> nodeMap = new HashMap<>();
        for (QualityExcelVo excel : excelList) {
            // 一级能力
            if (excel.getName() != null && !excel.getName().isEmpty()) {
                String key = "level1_" + excel.getName();
                StandardAbility root = new StandardAbility();
                root.setName(excel.getName());
                root.setParentId(-1L);
                root.setChildren(new ArrayList<>());
                //root.setVersion(excel.getVersion());
                root.setCollegeId(deptIdNameMap.get(excel.getCollegeName()));
                //root.setMajorId(majorIdToNameMap.get(excel.getMajorName()));
                //root.setSubMajorId(majorIdToNameMap.get(excel.getSubMajorName()));
                //root.setCategoryId(categoryIdToNameMap.get(excel.getCategoryName()));
                nodeMap.putIfAbsent(key, root);
            }
            // 二级能力
            if (excel.getSecondName() != null && !excel.getSecondName().isEmpty()) {
                String key = "level2_" + excel.getSecondName();
                StandardAbility second = new StandardAbility();
                second.setName(excel.getSecondName());
                second.setChildren(new ArrayList<>());
                //second.setRemark(excel.getSecondDescription());
                nodeMap.putIfAbsent(key, second);
            }
            // 三级能力
            /*if (excel.getThreeName() != null && !excel.getThreeName().isEmpty()) {
                String key = "level3_" + excel.getThreeName();
                StandardAbility three = new StandardAbility();
                three.setName(excel.getThreeName());
                three.setChildren(new ArrayList<>());
                three.setRemark(excel.getThreeDescription());
                nodeMap.putIfAbsent(key, three);
            }*/
        }
        // 建立父子关系
        for (QualityExcelVo excel : excelList) {
            StandardAbility level1 = null;
            StandardAbility level2 = null;
            StandardAbility level3 = null;
            if (excel.getName() != null && !excel.getName().isEmpty()) {
                level1 = nodeMap.get("level1_" + excel.getName());
            }
            if (excel.getSecondName() != null && !excel.getSecondName().isEmpty()) {
                level2 = nodeMap.get("level2_" + excel.getSecondName());
            }
            /*if (excel.getThreeName() != null && !excel.getThreeName().isEmpty()) {
                level3 = nodeMap.get("level3_" + excel.getThreeName());
            }*/
            // 建立三级到二级的父子关系
            if (level3 != null && level2 != null) {
                level3.setParentId(level2.getId());
                if (level2.getChildren() == null) {
                    level2.setChildren(new ArrayList<>());
                }
                level2.getChildren().add(level3);
            }
            // 建立二级到一级的父子关系
            if (level2 != null && level1 != null) {
                level2.setParentId(level1.getId());
                if (level1.getChildren() == null) {
                    level1.setChildren(new ArrayList<>());
                }
                level1.getChildren().add(level2);
            }
        }
        // 返回根节点
        return nodeMap.values().stream()
                .filter(node -> node.getParentId() != null && node.getParentId() == -1L)
                .collect(Collectors.toList());
    }


    @Override
    public void exportQualityTemplate(HttpServletResponse response) {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("template/standard/quality.xlsx");
        ExcelUtils.exportTemplate(inputStream, response);
    }


    /**
     * 新增能力体系
     *
     * @param standardAbility 能力素质
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = {Exception.class})
    public StandardAbility insertAbilitySystem(StandardAbility standardAbility) {
        Long pId = standardAbility.getParentId();
        if (pId == null || pId == -1L) {
            standardAbility.setParentId(-1L);
            standardAbility.setUrl("-1");
            standardAbility.setLevel(1);
            standardAbility.setLeaf(DomainFieldConstant.TREE_LEAF_VALUE);
        } else {
            standardAbility.setUrl(standardAbilityMapper.selectUrlByPId(pId) + "," + pId);
            standardAbility.setLeaf(DomainFieldConstant.TREE_LEAF_VALUE);
            if (standardAbilityMapper.selectUrlByPId(pId).contains(",")) {
                standardAbility.setLevel(standardAbilityMapper.selectUrlByPId(pId).split(",").length + 1);
            } else {
                standardAbility.setLevel(2);
            }

            standardAbilityMapper.updateNotLeaf(standardAbilityMapper.selectStandardAbilityById(pId).getId());
        }
        standardAbility.setSysflag(DomainFieldConstant.DEL_FLAG_NORMAL_VALUE);
        // standardAbility.setType(1);
        UserUtils.reflash(standardAbility);
        standardAbilityMapper.insertStandardAbility(standardAbility);
        //新增能力等级
        insertStandardAbilityLevels(standardAbility);
        return standardAbility;
    }

    /**
     * 新增能力等级
     *
     * @param standardAbility
     */
    private void insertStandardAbilityLevels(StandardAbility standardAbility) {
        List<StandardAbilityLevel> levels = standardAbility.getLevels();
        if (CollectionUtils.isEmpty(levels)) {
            return;
        }
        levels.stream().forEach(l -> {
            l.setAbilityId(standardAbility.getId());
            l.setCheckflag(0);
        });
        standardAbilityLevelMapper.insertStandardAbilityLevels(levels);
    }

    /**
     * 修改能力素质
     *
     * @param standardAbility 能力素质
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = {Exception.class})
    public StandardAbility updateStandardAbility(StandardAbility standardAbility) {
        UserUtils.reflash(standardAbility);
        standardAbilityMapper.updateStandardAbility(standardAbility);
        abilityRefGraduationMapper.deleteByAbilityId(standardAbility.getId());
        if (CollectionUtils.isNotEmpty(standardAbility.getGraduationIds())) {
            for (Long graduationId : standardAbility.getGraduationIds()) {
                AbilityRefGraduation abilityRefGraduation = new AbilityRefGraduation();
                abilityRefGraduation.setAbilityId(standardAbility.getId());
                abilityRefGraduation.setGraduationId(graduationId);
                abilityRefGraduationMapper.insert(abilityRefGraduation);
            }
        }
        updateStandardAbilityLevels(standardAbility);
        return standardAbility;
    }

    @Override
    @Transactional(rollbackFor = {Exception.class})
    public StandardAbility updateStdAbilityTree(StandardAbility standardAbility) {
        standardAbility.setParentId(-1L);
        standardAbility.setUrl("-1");
        standardAbility.setLeaf(0);
        standardAbility.setLevel(1);
        UserUtils.reflash(standardAbility);
        if (standardAbility.getId() != null) {
            checkStandardAbility(standardAbility.getId());
            standardAbilityMapper.updateSysFlag(standardAbility.getId());
            standardAbilityMapper.updateStandardAbility(standardAbility);
        } else {
            standardAbilityMapper.insertStandardAbility(standardAbility);
        }
        // 后增
        if (CollectionUtils.isNotEmpty(standardAbility.getChildren())) {
            for (StandardAbility child : standardAbility.getChildren()) {
                processStandardAbility(child, standardAbility.getId(), 2, standardAbility.getUrl(),
                        standardAbility.getCollegeId(), standardAbility.getMajorId(),
                        standardAbility.getType(), standardAbility.getVersion(),
                        standardAbility.getSubMajorId(), standardAbility.getCategoryId());
            }
        }
        return standardAbility;
    }

    @Override
    @Transactional(rollbackFor = {Exception.class})
    public void insertStandardTemplateAbilityVo(StandardTemplateAbilityVo standardTemplateAbilityVo) {
        if (CollectionUtils.isNotEmpty(standardTemplateAbilityVo.getIds())) {
            for (Long id : standardTemplateAbilityVo.getIds()) {
                List<StandardAbility> rootAndChildrenList = standardAbilityMapper.selectAllAbilityById(id);
                List<StandardAbility> root = TreeBuilderUtils.buildRootTree(rootAndChildrenList);
                if (CollectionUtils.isNotEmpty(root)) {
                    StandardAbility standardAbility = root.get(0);
                    BeanUtils.copyProperties(standardTemplateAbilityVo, standardAbility);
                    standardAbility.setSourceId(standardAbility.getId());
                    standardAbility.setId(null);
                    UserUtils.reflash(standardAbility);
                    standardAbilityMapper.insertStandardAbility(standardAbility);
                    if (CollectionUtils.isNotEmpty(standardAbility.getChildren())) {
                        for (StandardAbility child : standardAbility.getChildren()) {
                            processStandardAbility(child, standardAbility.getId(), standardAbility.getUrl(),
                                    standardTemplateAbilityVo);
                        }
                    }
                }
            }
        }
    }

    private void checkStandardAbility(Long abilityId) {
        StandardAbility db = standardAbilityMapper.selectStandardAbilityById(abilityId);
        UserUtils.checkDataPermission(db);
        List<CourseRefAbility> abilities = courseRefAbilityMapper.selectCourseRefAbilityByAbilityId(abilityId);
        List<CourseRefQuality> qualities = courseRefQualityMapper.selectCourseRefQualityByQualityId(abilityId);
        if (CollectionUtils.isNotEmpty(abilities) || CollectionUtils.isNotEmpty(qualities)) {
            List<Long> courseIds = new ArrayList<>();
            if (CollectionUtils.isNotEmpty(abilities)) {
                for (CourseRefAbility ability : abilities) {
                    courseIds.add(ability.getCourseId());
                }
            }
            if (CollectionUtils.isNotEmpty(qualities)) {
                for (CourseRefQuality quality : qualities) {
                    courseIds.add(quality.getCourseId());
                }
            }
            List<Course> courses = courseMapper.selectCoursesByIds(courseIds);
            throw new RuntimeException("能力素质被课程:" + courses.stream().map(Course::getName).collect(Collectors.joining(",")) + "引用后不能修改和删除！");
        }
    }

    @Override
    @Transactional(rollbackFor = {Exception.class})
    public void deleteStandardAbilityByIds(Long[] ids) {
        for (Long id : ids) {
            StandardAbility db = standardAbilityMapper.selectStandardAbilityById(id);
            UserUtils.checkDataPermission(db);
        }
        standardAbilityMapper.deleteStandardAbilityByIds(ids);
    }

    /**
     * 递归处理
     */
    private void processStandardAbility(StandardAbility standardAbility, Long parentId,
                                        Integer level, String parentUrl, Long collegeId,
                                        Long majorId, Integer type, String version,
                                        Long subMajorId, Long categoryId) {
        standardAbility.setLevel(level);
        standardAbility.setLeaf(CollectionUtils.isEmpty(standardAbility.getChildren()) ? 1 : 0);
        standardAbility.setCollegeId(collegeId);
        standardAbility.setMajorId(majorId);
        standardAbility.setType(type);
        standardAbility.setParentId(parentId);
        standardAbility.setVersion(version);
        standardAbility.setSubMajorId(subMajorId);
        standardAbility.setCategoryId(categoryId);
        // 保存当前节点
        UserUtils.reflash(standardAbility);
        parentUrl = parentUrl + SymbolConstants.COMMA + parentId;
        standardAbility.setUrl(parentUrl);
        standardAbility.setId(null);
        standardAbilityMapper.insertStandardAbility(standardAbility);
        // 能力素质关联 毕业标准
        abilityRefGraduationMapper.deleteByAbilityId(standardAbility.getId());
        if (CollectionUtils.isNotEmpty(standardAbility.getGraduationIds())) {
            for (Long graduationId : standardAbility.getGraduationIds()) {
                AbilityRefGraduation abilityRefGraduation = new AbilityRefGraduation();
                abilityRefGraduation.setAbilityId(standardAbility.getId());
                abilityRefGraduation.setGraduationId(graduationId);
                abilityRefGraduationMapper.insert(abilityRefGraduation);
            }
        }
        updateStandardAbilityLevels(standardAbility);
        // 递归处理子节点
        if (CollectionUtils.isNotEmpty(standardAbility.getChildren())) {
            for (StandardAbility child : standardAbility.getChildren()) {
                processStandardAbility(child, standardAbility.getId(), level + 1,
                        standardAbility.getUrl(), collegeId, majorId, type, version,
                        subMajorId, categoryId);
            }
        }
    }

    private void processStandardAbility(StandardAbility standardAbility, Long parentId,
                                        String parentUrl, StandardTemplateAbilityVo standardTemplateAbilityVo) {
        standardAbility.setParentId(parentId);
        // 保存当前节点
        UserUtils.reflash(standardAbility);
        parentUrl = parentUrl + SymbolConstants.COMMA + parentId;
        standardAbility.setUrl(parentUrl);
        standardAbility.setSourceId(standardAbility.getId());
        standardAbility.setId(null);
        BeanUtils.copyProperties(standardTemplateAbilityVo, standardAbility);
        UserUtils.reflash(standardAbility);
        standardAbilityMapper.insertStandardAbility(standardAbility);
        // 递归处理子节点
        if (CollectionUtils.isNotEmpty(standardAbility.getChildren())) {
            for (StandardAbility child : standardAbility.getChildren()) {
                processStandardAbility(child, standardAbility.getId(),
                        standardAbility.getUrl(), standardTemplateAbilityVo);
            }
        }
    }


    /**
     * 能力体系，能力素质，修改能力等级
     *
     * @param standardAbility
     */
    private void updateStandardAbilityLevels(StandardAbility standardAbility) {
        List<StandardAbilityLevel> levels = standardAbility.getLevels();
        List<StandardAbilityLevel> oldLevel = standardAbilityLevelMapper.selectStandardAbilityLevelByAbilityId(standardAbility.getId());
        //删除老的不存在的能力等级
        if (CollectionUtils.isNotEmpty(oldLevel)) {
            if (CollectionUtils.isEmpty(levels)) {
                List<Long> deleteIds = oldLevel.stream().map(o -> o.getId()).collect(Collectors.toList());
                standardAbilityLevelMapper.deleteStandardAbilityLevel(deleteIds);
                return;
            }
            List<Long> newIds = levels.stream().map(s -> s.getId()).collect(Collectors.toList());
            List<Long> deleteIds = oldLevel.stream().filter(o -> !newIds.contains(o.getId())).map(s -> s.getId()).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(deleteIds)) {
                standardAbilityLevelMapper.deleteStandardAbilityLevel(deleteIds);
            }
        }
        if (CollectionUtils.isNotEmpty(levels)) {
            //修改能力等级
            List<StandardAbilityLevel> update = levels.stream()
                    .filter(standardAbilityLevel -> null != standardAbilityLevel.getId() && standardAbilityLevel.getId() > 0)
                    .collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(update)) {
                standardAbilityLevelMapper.updateStandardAbilityLevels(update);
            }
            //添加新能力等级
            List<StandardAbilityLevel> insert = levels.stream()
                    .filter(standardAbilityLevel -> null == standardAbilityLevel.getId())
                    .collect(Collectors.toList());
            insert.forEach(l -> l.setAbilityId(standardAbility.getId()));
            if (CollectionUtils.isNotEmpty(insert)) {
                for (StandardAbilityLevel standardAbilityLevel : insert) {
                    standardAbilityLevelMapper.insertStandardAbilityLevel(standardAbilityLevel);
                }
            }
        }
    }


    /**
     * 删除能力素质
     *
     * @param id 能力素质主键
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = {Exception.class})
    public void deleteStandardAbilityById(Long id) {
        checkStandardAbility(id);
        StandardAbility sb = standardAbilityMapper.selectStandardAbilityById(id);
        Long pid = sb.getParentId();
        standardAbilityMapper.deleteStandardAbilityById(id);
        if (-1 != pid) {
            StandardAbility nSb = new StandardAbility();
            nSb.setParentId(pid);
            List<StandardAbility> standardAbilityList = standardAbilityMapper.selectStandardAbilityList(nSb);
            if (0 == standardAbilityList.size()) {
                standardAbilityMapper.setLeafIsNode(pid);
            }
        }
        //删除能力等级
        standardAbilityLevelMapper.deleteStandardAbilityLevelByAbilityId(id);


    }


    @Override
    public List<StandardAbility> selectAllStdAbilityById(Long id) {
        List<StandardAbility> list = standardAbilityMapper.selectAllAbilityById(id);
        List<Long> parentIds = list.stream().map(StandardAbility::getParentId).collect(Collectors.toList());
        List<StandardAbility> parentList = standardAbilityMapper.selectStandardAbilityByIds(parentIds, null);
        Map<Long, String> parentMap = parentList.parallelStream().collect(Collectors.toMap(x -> x.getId(), y -> y.getName()));
        for (StandardAbility ability : list) {
            ability.setParentName(parentMap.get(ability.getParentId()));
        }
        List<Long> collect = list.stream().map(a -> a.getId()).collect(Collectors.toList());
        List<StandardAbilityLevel> standardAbilityLevels = standardAbilityLevelMapper.selectStandardAbilityLevelByAbilityIds(collect);
        if (CollectionUtils.isNotEmpty(standardAbilityLevels)) {
            Map<Long, List<StandardAbilityLevel>> abilityIdMap = standardAbilityLevels.stream().collect(Collectors.groupingBy(a -> a.getAbilityId()));
            for (StandardAbility standardAbility : list) {
                standardAbility.setLevels(abilityIdMap.get(standardAbility.getId()));
            }
        }
        return list;
    }

    @Override
    public List<StandardAbility> selectAllStdAbilityAndGraduationById(Long id) {
        List<StandardAbility> list = selectAllStdAbilityById(id);
        // 毕业要求做成树结购展示
        if (CollectionUtils.isNotEmpty(list)) {
            List<StandardAbility> graduationToAbilitys = new ArrayList<>();
            for (StandardAbility standardAbility : list) {
                if (CollectionUtils.isNotEmpty(standardAbility.getGraduationIds())) {
                    List<StandardGraduation> standardGraduations = standardGraduationMapper.selectStandardGraduationByIds(standardAbility.getGraduationIds());
                    if (CollectionUtils.isNotEmpty(standardGraduations)) {
                        for (StandardGraduation standardGraduation : standardGraduations) {
                            StandardAbility g = new StandardAbility();
                            g.setId(standardGraduation.getId());
                            g.setParentId(standardAbility.getId());
                            g.setName(standardGraduation.getName());
                            if (standardGraduation.getParentId() != -1) {
                                graduationToAbilitys.add(g);
                            }
                        }
                    }
                }
            }
            if (CollectionUtils.isNotEmpty(graduationToAbilitys)) {
                list.addAll(graduationToAbilitys);
            }
        }
        return list;
    }

    /*
     * -------------------------------------------
     *          能力素质---代码段结束
     * -------------------------------------------
     */

    /*
     * -------------------------------------------
     *          培养目标---代码段开始
     * -------------------------------------------
     */


    /**
     * 查询培养目标列表
     */
    @Override
    public List<StandardCultivationTarget> selectStandardCultivationTargetList(StandardCultivationTarget standardCultivationTarget) {
        return standardCultivationTargetMapper.selectStandardCultivationTargetList(standardCultivationTarget);
    }

    @Override
    public List<StandardCultivationTarget> selectStdCultivationTargetAll(Long id) {
        return standardCultivationTargetMapper.selectStdCultivationTargetAll(id);
    }

    @Override
    public StandardCultivationTarget selectStdCultivationTargetTree(Long id) {
        List<StandardCultivationTarget> standardCultivationTargetList = selectStdCultivationTargetAll(id);
        if (ObjectUtils.isEmpty(standardCultivationTargetList)) {
            return null;
        }
        Map<Long, StandardCultivationTarget> standardCultivationTargetMap = standardCultivationTargetList.parallelStream()
                .collect(Collectors.toMap(StandardCultivationTarget::getId, _standardCultivationTarget -> _standardCultivationTarget));
        standardCultivationTargetList = standardCultivationTargetList.stream().filter(_standardCultivationTarget -> {
            if (DomainFieldConstant.ROOT_NODE_LONG_ID.equals(_standardCultivationTarget.getParentId())) {
                return true;
            }
            StandardCultivationTarget parentStandardCultivationTarget = standardCultivationTargetMap.get(_standardCultivationTarget.getParentId());
            if (ObjectUtils.isNotEmpty(parentStandardCultivationTarget)) {
                if (ObjectUtils.isEmpty(parentStandardCultivationTarget.getChildren())) {
                    parentStandardCultivationTarget.setChildren(new ArrayList<>());
                }
                ((List<StandardCultivationTarget>) parentStandardCultivationTarget.getChildren()).add(_standardCultivationTarget);
            }
            return false;
        }).collect(Collectors.toList());
        if (ObjectUtils.isEmpty(standardCultivationTargetList)) {
            return null;
        }
        return standardCultivationTargetList.get(0);
    }

    @Override
    public List<StandardCultivationTarget> selectCultivationTargetByMajorId(Long majorId,String version) {
        ArrayList<StandardCultivationTarget> resultList = new ArrayList<>();
        StandardCultivationTarget standardCultivationTarget = new StandardCultivationTarget();
        standardCultivationTarget.setMajorId(majorId);
        standardCultivationTarget.setVersion(version);
        List<StandardCultivationTarget> standardCultivationTargets = standardCultivationTargetMapper.selectStandardCultivationTargetList(standardCultivationTarget);

        standardCultivationTargets.forEach(s -> {
            resultList.addAll(standardCultivationTargetMapper.selectStdCultivationTargetAll(s.getId()));
        });
        return resultList;
    }

    @Override
    public StandardCultivationTarget selectStandardCultivationTargetById(Long id) {
        StandardCultivationTarget std = standardCultivationTargetMapper.selectStandardCultivationTargetById(id);
        StandardCultivationTarget pStd = standardCultivationTargetMapper.selectStandardCultivationTargetById(std.getParentId());
        if (null != pStd) {
            std.setParentName(pStd.getName());
        }
        return std;
    }

    /**
     * 新增培养目标
     *
     * @param standardCultivationTarget 培养目标
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = {Exception.class})
    public StandardCultivationTarget insertStandardCultivationTarget(StandardCultivationTarget standardCultivationTarget) {
        Long pId = standardCultivationTarget.getParentId();
        if (pId == -1) {
            standardCultivationTarget.setUrl(DomainFieldConstant.ROOT_NODE_STRING_ID);
            standardCultivationTarget.setLevel(1);
            standardCultivationTarget.setLeaf(DomainFieldConstant.TREE_LEAF_VALUE);
        } else {
            StandardCultivationTarget pStd = standardCultivationTargetMapper.selectStandardCultivationTargetById(pId);
            Integer level = pStd.getLevel();
            String url = pStd.getUrl();
            if (3 == level) {
                throw new UpdateDataException("叶子节点不允许添加下级");
            }
            standardCultivationTarget.setLevel(level + 1);
            standardCultivationTarget.setParentId(pId);
            standardCultivationTarget.setUrl(url + "," + pId);
            standardCultivationTarget.setLeaf(DomainFieldConstant.TREE_LEAF_VALUE);
            pStd.setLeaf(DomainFieldConstant.TREE_FOLDER_VALUE);
            standardCultivationTargetMapper.updateStandardCultivationTarget(pStd);
        }
        UserUtils.reflash(standardCultivationTarget);
        standardCultivationTargetMapper.insertStandardCultivationTarget(standardCultivationTarget);
        setChildTarget(standardCultivationTarget);
        return standardCultivationTarget;
    }

    private void setChildTarget(StandardCultivationTarget standardCultivationTarget) {
        if (ObjectUtils.isNotEmpty(standardCultivationTarget.getChildren())) {
            List<?> children = standardCultivationTarget.getChildren();
            for (Object child : children) {
                StandardCultivationTarget cultivationTarget = MapToObjectUtil.convertToObject((Map<String, Object>) child, StandardCultivationTarget.class);
                cultivationTarget.setParentId(standardCultivationTarget.getId());
                cultivationTarget.setCollegeId(standardCultivationTarget.getCollegeId());
                cultivationTarget.setCategoryId(standardCultivationTarget.getCategoryId());
                cultivationTarget.setMajorId(standardCultivationTarget.getMajorId());
                cultivationTarget.setSubMajorId(standardCultivationTarget.getSubMajorId());
                cultivationTarget.setVersion(standardCultivationTarget.getVersion());
                cultivationTarget.setTrainingSchemeId(standardCultivationTarget.getTrainingSchemeId());
                insertStandardCultivationTarget(cultivationTarget);
            }
        }
    }

    @Override
    @Transactional(rollbackFor = {Exception.class})
    public int insertStandardAbilityTree(List<StandardAbility> standardAbilityTree) {
//        List<StandardAbility> standardAbilityList = TreeEntityUtils.toList(standardAbilityTree);
//        standardAbilityMapper.insertAbilityList(standardAbilityList);
//        updateStandardAbilityParentId(standardAbilityTree,null);
//        standardAbilityMapper.updateAbilityList(standardAbilityList);
        return 1;
    }

    private List<StandardAbility> updateStandardAbilityParentId(List<StandardAbility> list, StandardAbility parent) {
        if (ObjectUtils.isEmpty(list)) {
            return new ArrayList<>();
        }
        list.parallelStream().forEach(_treeEntity -> {
            if (ObjectUtils.isNotEmpty(parent)) {
                _treeEntity.setParentId(parent.getId());
                _treeEntity.setLevel(parent.getLevel() + 1);
                _treeEntity.setUrl(parent.getUrl() + SymbolConstants.COMMA + parent.getId());
            } else {
                _treeEntity.setParentId(DomainFieldConstant.ROOT_NODE_LONG_ID);
                _treeEntity.setLevel(1);
                _treeEntity.setUrl(String.valueOf(DomainFieldConstant.ROOT_NODE_LONG_ID));
            }
            if (ObjectUtils.isNotEmpty(_treeEntity.getChildren())) {
                List<StandardAbility> children = (List<StandardAbility>) _treeEntity.getChildren();
                children.parallelStream().forEach(_child -> _child.setParentId(_treeEntity.getId()));
                updateStandardAbilityParentId(children, _treeEntity);
                _treeEntity.setLeaf(DomainFieldConstant.TREE_FOLDER_VALUE);
            } else {
                _treeEntity.setLeaf(DomainFieldConstant.TREE_LEAF_VALUE);
            }
            UserUtils.reflash(_treeEntity);
        });
        return list;
    }

    /**
     * execel导入新增培养目标,包含下级
     *
     * @param standardCultivationTargetTree 培养目标
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = {Exception.class})
    public int insertStandardCultivationTargetTree(List<StandardCultivationTarget> standardCultivationTargetTree) {
        //先把所有都插入数据库生成id，然后再更新parentId
        List<StandardCultivationTarget> standardCultivationTargetList = TreeEntityUtils.toList(standardCultivationTargetTree);
        for (StandardCultivationTarget standardCultivationTarget : standardCultivationTargetList) {
            UserUtils.reflash(standardCultivationTarget);
        }
        standardCultivationTargetMapper.insertStandardCultivationTargetList(standardCultivationTargetList);
        updateStandardCultivationTargetParentId(standardCultivationTargetTree, null);
        standardCultivationTargetMapper.updateStandardCultivationTargetList(standardCultivationTargetList);
        return 1;
    }

    private List<StandardCultivationTarget> updateStandardCultivationTargetParentId(List<StandardCultivationTarget> list, StandardCultivationTarget parent) {
        if (ObjectUtils.isEmpty(list)) {
            return new ArrayList<>();
        }
        list.parallelStream().forEach(_treeEntity -> {
            if (ObjectUtils.isNotEmpty(parent)) {
                _treeEntity.setParentId(parent.getId());
                _treeEntity.setLevel(parent.getLevel() + 1);
                _treeEntity.setUrl(parent.getUrl() + SymbolConstants.COMMA + parent.getId());
            } else {
                _treeEntity.setParentId(DomainFieldConstant.ROOT_NODE_LONG_ID);
                _treeEntity.setLevel(1);
                _treeEntity.setUrl(String.valueOf(DomainFieldConstant.ROOT_NODE_LONG_ID));
            }
            if (ObjectUtils.isNotEmpty(_treeEntity.getChildren())) {
                List<StandardCultivationTarget> children = (List<StandardCultivationTarget>) _treeEntity.getChildren();
                children.parallelStream().forEach(_child -> _child.setParentId(_treeEntity.getId()));
                updateStandardCultivationTargetParentId(children, _treeEntity);
                _treeEntity.setLeaf(DomainFieldConstant.TREE_FOLDER_VALUE);
            } else {
                _treeEntity.setLeaf(DomainFieldConstant.TREE_LEAF_VALUE);
            }
            UserUtils.reflash(_treeEntity);
        });
        return list;
    }

    /**
     * 修改培养目标
     *
     * @param standardCultivationTarget 培养目标
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = {Exception.class})
    public StandardCultivationTarget updateStandardCultivationTarget(StandardCultivationTarget standardCultivationTarget) {
        UserUtils.reflash(standardCultivationTarget);
        if(ObjectUtils.isNotEmpty(standardCultivationTarget.getId())) {
            Message message = deleteStandardCultivationTargetForUpdate(standardCultivationTarget.getId());
            if (message.getCode() == 500) {
                return null;
            }
        }
        insertStandardCultivationTarget(standardCultivationTarget);
        //standardCultivationTargetMapper.updateStandardCultivationTarget(standardCultivationTarget);
        return standardCultivationTarget;
    }

    private Message deleteStandardCultivationTargetForUpdate(Long id) {
        StandardCultivationTarget db = standardCultivationTargetMapper.selectStandardCultivationTargetById(id);
        UserUtils.checkDataPermission(db);
        //查询该节点下的所有数据
        List<StandardCultivationTarget> standardCultivationTargetListVo = standardCultivationTargetMapper.selectStdCultivationTargetAll(id);
        // 如果该节点下有数据已配置支撑关系无法删除
        List<Long> tIds = standardCultivationTargetListVo.stream().map(StandardCultivationTarget::getId).collect(Collectors.toList());
        if (ObjectUtils.isNotEmpty(tIds)) {
            List<StandardGraduationRefCultivationTarget> refList = standardGraduationRefCultivationTargetMapper.selectRefByTaIds(tIds);
            if (0 != refList.size()) {
                return Message.error("已配置支撑关系无法删除");
            }
        }
        //物理删除全部数据
        standardCultivationTargetMapper.deleteByIds(tIds);
        return Message.success();
    }

    /**
     * 删除培养目标
     */
    @Override
    @Transactional(rollbackFor = {Exception.class})
    public Message deleteStandardCultivationTargetById(Long id) {
        StandardCultivationTarget db = standardCultivationTargetMapper.selectStandardCultivationTargetById(id);
        UserUtils.checkDataPermission(db);
        StandardCultivationTarget standardCultivationTarget = standardCultivationTargetMapper.selectStandardCultivationTargetById(id);
        //查询该节点下的所有数据
        List<StandardCultivationTarget> standardCultivationTargetListVo = standardCultivationTargetMapper.selectStdCultivationTargetAll(id);
        // 如果该节点下有数据已配置支撑关系无法删除
        List<Long> tIds = standardCultivationTargetListVo.stream().map(StandardCultivationTarget::getId).collect(Collectors.toList());
        List<StandardGraduationRefCultivationTarget> refList = standardGraduationRefCultivationTargetMapper.selectRefByTaIds(tIds);
        if (0 != refList.size()) {
            return Message.error("已配置支撑关系无法删除");
        }
        Long pid = standardCultivationTarget.getParentId();
        standardCultivationTargetMapper.deleteStandardCultivationTarget(id);
        // 培养目标没有指向谁 所以不用删除上层关系
        if (-1 != pid) {
            StandardCultivationTarget st = new StandardCultivationTarget();
            st.setParentId(pid);
            List<StandardCultivationTarget> standardCultivationTargetList = standardCultivationTargetMapper.selectStandardCultivationTargetList(st);
            if (0 == standardCultivationTargetList.size()) {
                standardCultivationTargetMapper.setLeafIsNode(pid);
            }
        }
        return Message.success();
    }


    /*
     * -------------------------------------------
     *          培养目标---代码段结束
     * -------------------------------------------
     */

    /*
     * -------------------------------------------
     * 毕业标准---代码段开始
     * -------------------------------------------
     */
    @Override
    public StandardGraduationRefTargetListVo selectStandardCultivationTargetByGraduationId(Long graduationId) {
        List<StandardGraduationVo> standardGraduationVoList = standardGraduationMapper.selectGraduationRefTarget(graduationId);
        StandardGraduation standardGraduation = standardGraduationMapper.selectStandardGraduationById(graduationId);
        Long targetId = standardGraduation.getCultivationTargetId();
        StandardCultivationTarget st = standardCultivationTargetMapper.selectStandardCultivationTargetById(targetId);
        StandardGraduationRefTargetListVo vo = new StandardGraduationRefTargetListVo();
        if (null != st) {
            vo.setTargetTopId(st.getId());
        }
        vo.setStandardGraduationList(standardGraduationVoList);
        return vo;
    }

    @Override
    public List<Long> selectTargetListByGraduationId(Long graduationId) {
        StandardGraduation sg = standardGraduationMapper.selectStandardGraduationById(graduationId);
        Long tId = sg.getCultivationTargetId();
        return standardCultivationTargetMapper.selectStandardCultivationTargetRefByGraduationId(tId, graduationId);
    }

    @Override
    public List<StandardGraduation> selectStandardGraduationList(StandardGraduation standardGraduation) {
        return standardGraduationMapper.selectStandardGraduationList(standardGraduation);
    }

    @Override
    public List<StandardGraduation> selectStdGraduationSchemeListBy(StandardGraduation standardGraduation) {
        List<StandardGraduation> standardGraduations = standardGraduationMapper.selectStandardGraduationListNoJoin(standardGraduation);
        List<StandardGraduation> tree = TreeBuilderUtils.buildRootTree(standardGraduations);
        if (CollectionUtils.isNotEmpty(tree)){
            for (StandardGraduation root : tree) {
                Integer secondLevelIndicatorsNumber = 0;
                if (CollectionUtils.isNotEmpty(root.getChildren())){
                    for (StandardGraduation first : root.getChildren()) {
                        if (CollectionUtils.isNotEmpty(first.getChildren())){
                            secondLevelIndicatorsNumber +=first.getChildren().size();
                        }
                    }
                }
                if (CollectionUtils.isNotEmpty(root.getChildren())){
                    root.setFirstLevelIndicatorsNumber(root.getChildren().size());
                }
                root.setSecondLevelIndicatorsNumber(secondLevelIndicatorsNumber);
            }
        }
        return tree;
    }

    @Override
    public List<GraduationTreeVo> selectStdGraduationTreeBy(StandardGraduation standardGraduation) {
        List<StandardGraduation> standardGraduations = standardGraduationMapper.selectStandardGraduationListNoJoin(standardGraduation);
        List<GraduationTreeVo> tree = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(standardGraduations)) {
            for (StandardGraduation graduation : standardGraduations) {
                GraduationTreeVo vo = new GraduationTreeVo();
                vo.setId(graduation.getId());
                vo.setName(graduation.getName());
                vo.setParentId(graduation.getParentId());
                vo.setCollegeId(graduation.getCollegeId());
                vo.setCategoryId(graduation.getCategoryId());
                vo.setMajorId(graduation.getMajorId());
                tree.add(vo);
            }
            return TreeBuilderUtils.buildRootTree(tree);
        }
        return tree;
    }

    @Override
    public StandardGraduation selectStandardGraduationById(Long id) {
        StandardGraduation stg = standardGraduationMapper.selectStandardGraduationById(id);
        if (Objects.isNull(stg)) {
            return null;
        }
        StandardGraduation pStg = standardGraduationMapper.selectStandardGraduationById(stg.getParentId());
        if (null != pStg) {
            stg.setParentName(pStg.getName());
        }
        return stg;
    }


    /**
     * 新增毕业标准
     *
     * @param standardGraduation 毕业标准
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = {Exception.class})
    public StandardGraduation insertStandardGraduation(StandardGraduation standardGraduation) {
        Long pId = standardGraduation.getParentId();
        if (pId == -1L) {
            standardGraduation.setUrl("-1");
            standardGraduation.setLevel(1);
            standardGraduation.setLeaf(DomainFieldConstant.TREE_LEAF_VALUE);
        } else {
            StandardGraduation pStg = standardGraduationMapper.selectStandardGraduationById(pId);
            Integer level = pStg.getLevel();
            String url = pStg.getUrl();
            if (3 == level) {
                throw new UpdateDataException("叶子节点不允许添加下级");
            }
            standardGraduation.setLevel(level + 1);
            standardGraduation.setParentId(pId);
            standardGraduation.setUrl(url + "," + pId);
            standardGraduation.setLeaf(DomainFieldConstant.TREE_LEAF_VALUE);
            pStg.setLeaf(DomainFieldConstant.TREE_FOLDER_VALUE);
            standardGraduationMapper.updateStandardGraduation(pStg);
        }
        UserUtils.reflash(standardGraduation);
        standardGraduationMapper.insertStandardGraduation(standardGraduation);
        //绑定培养目标
        boundCultivationTarget(standardGraduation);
        setStandGraduationChild(standardGraduation);
        return standardGraduation;

    }

    private void boundCultivationTarget(StandardGraduation standardGraduation) {
        if (ObjectUtils.isNotEmpty(standardGraduation.getCultivationTargetIds())) {
            ArrayList<StandardGraduationRefCultivationTarget> refList = new ArrayList<>();
            for (Object id : standardGraduation.getCultivationTargetIds()) {
                Long cultivationTargetId = ((Number) id).longValue();  // 兼容Integer/Long
                StandardGraduationRefCultivationTarget srt = new StandardGraduationRefCultivationTarget();
                srt.setGraduationId(standardGraduation.getId());
                srt.setCultivationTargetId(cultivationTargetId);
                refList.add(srt);
            }
            standardGraduationRefCultivationTargetMapper.insetList(refList);
        }
    }

    private void setStandGraduationChild(StandardGraduation standardGraduation) {
        if (ObjectUtils.isNotEmpty(standardGraduation.getChildren())) {
            standardGraduation.getChildren().forEach(s -> {
                StandardGraduation graduation = MapToObjectUtil.convertToObject((Map<String, Object>) s, StandardGraduation.class);
                graduation.setParentId(standardGraduation.getId());
                graduation.setCollegeId(standardGraduation.getCollegeId());
                graduation.setCategoryId(standardGraduation.getCategoryId());
                graduation.setMajorId(standardGraduation.getMajorId());
                graduation.setSubMajorId(standardGraduation.getSubMajorId());
                graduation.setVersion(standardGraduation.getVersion());
                insertStandardGraduation(graduation);
            });
        }
    }

    /**
     * 新增毕业标准,包含下级
     *
     * @param standardGraduationTree 毕业标准树
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = {Exception.class})
    public int insertStandardGraduationTree(List<StandardGraduation> standardGraduationTree) {
        //先把所有都插入数据库生成id，然后再更新parentId
        //List<StandardGraduation> standardGraduationList = TreeEntityUtils.toList(standardGraduationTree);
        List<StandardGraduation> standardGraduationList = TreeBuilderUtils.flattenTree(standardGraduationTree);
        for (StandardGraduation standardGraduation : standardGraduationList) {
            UserUtils.reflash(standardGraduation);
        }
        standardGraduationMapper.insertStandardGraduationList(standardGraduationList);
        updateStandardGraduationParentId(standardGraduationTree, null);
        standardGraduationMapper.updateStandardGraduationList(standardGraduationList);
        return 1;
    }

    private List<StandardGraduation> updateStandardGraduationParentId(List<StandardGraduation> list, StandardGraduation parent) {
        if (ObjectUtils.isEmpty(list)) {
            return new ArrayList<>();
        }
        list.parallelStream().forEach(_treeEntity -> {
            if (ObjectUtils.isNotEmpty(parent)) {
                _treeEntity.setParentId(parent.getId());
                _treeEntity.setLevel(parent.getLevel() + 1);
                _treeEntity.setUrl(parent.getUrl() + SymbolConstants.COMMA + parent.getId());
            } else {
                _treeEntity.setParentId(DomainFieldConstant.ROOT_NODE_LONG_ID);
                _treeEntity.setLevel(1);
                _treeEntity.setUrl(String.valueOf(DomainFieldConstant.ROOT_NODE_LONG_ID));
            }
            if (ObjectUtils.isNotEmpty(_treeEntity.getChildren())) {
                List<StandardGraduation> children = (List<StandardGraduation>) _treeEntity.getChildren();
                children.parallelStream().forEach(_child -> _child.setParentId(_treeEntity.getId()));
                updateStandardGraduationParentId(children, _treeEntity);
                _treeEntity.setLeaf(DomainFieldConstant.TREE_FOLDER_VALUE);
            } else {
                _treeEntity.setLeaf(DomainFieldConstant.TREE_LEAF_VALUE);
            }
            UserUtils.reflash(_treeEntity);
        });
        return list;
    }

    /**
     * 修改毕业标准
     *
     * @param standardGraduation 毕业标准
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = {Exception.class})
    public StandardGraduation updateStandardGraduation(StandardGraduation standardGraduation) {
        UserUtils.reflash(standardGraduation);
        StandardGraduation db = standardGraduationMapper.selectStandardGraduationById(standardGraduation.getId());
        UserUtils.checkDataPermission(db);
        /*Message message = deleteStandardGraduationForUpdate(standardGraduation.getId());
        if (message.getCode() == 500) {
            return null;
        }
        insertStandardGraduation(standardGraduation);*/
        standardGraduationMapper.updateStandardGraduation(standardGraduation);
        List<Long> ids = new ArrayList<>();
        ids.add(standardGraduation.getId());
        standardGraduationRefCultivationTargetMapper.deleteByGraduationIds(ids);
        if (CollectionUtils.isNotEmpty(standardGraduation.getCultivationTargetIds())){
            for (Long cultivationTargetId : standardGraduation.getCultivationTargetIds()) {
                StandardGraduationRefCultivationTarget standardGraduationRefCultivationTarget = new StandardGraduationRefCultivationTarget();
                standardGraduationRefCultivationTarget.setGraduationId(standardGraduation.getId());
                standardGraduationRefCultivationTarget.setCultivationTargetId(cultivationTargetId);
                standardGraduationRefCultivationTargetMapper.insert(standardGraduationRefCultivationTarget);
            }
        }
        return standardGraduation;
    }

    private Message deleteStandardGraduationForUpdate(Long id) {
        //查询该节点下的所有数据
        List<StandardGraduation> standardGraduationListVo = standardGraduationMapper.selectStandardGraduationAll(id);
        List<Long> gIds = standardGraduationListVo.stream().map(StandardGraduation::getId).collect(Collectors.toList());
        //查看毕业标准是否关联了课程
        if (ObjectUtils.isNotEmpty(gIds)) {
            List<CourseRefGraduation> refList = courseRefGraduationMapper.selectRefBygraduationIds(gIds);
            if (0 != refList.size()) {
                return Message.error("已配置支撑关系无法删除");
            }
        }
        //删除毕业标准关联培养目标
        if (ObjectUtils.isNotEmpty(gIds)) {
            standardGraduationRefCultivationTargetMapper.deleteByGraduationIds(gIds);
            //删除全部
            standardGraduationMapper.deleteByIds(gIds);
        }

        return Message.success();
    }

    /**
     * 删除毕业标准
     */
    @Override
    @Transactional(rollbackFor = {Exception.class})
    public Message deleteStandardGraduationById(Long id) {
        StandardGraduation db = standardGraduationMapper.selectStandardGraduationById(id);
        UserUtils.checkDataPermission(db);
        StandardGraduation stg = standardGraduationMapper.selectStandardGraduationById(id);
        //查询该节点下的所有数据
        List<StandardGraduation> standardGraduationListVo = standardGraduationMapper.selectStandardGraduationAll(id);
        // 如果该节点下有数据已配置支撑关系无法删除
        List<Long> gIds = standardGraduationListVo.stream().map(StandardGraduation::getId).collect(Collectors.toList());
        /**
         * TODO判断课程、能力、素质是否关联
         */
        if (ObjectUtils.isNotEmpty(gIds)) {
            List<CourseRefGraduation> refList = courseRefGraduationMapper.selectRefBygraduationIds(gIds);
            if (0 != refList.size()) {
                return Message.error("已配置支撑关系无法删除");
            }
        }
        Long pid = stg.getParentId();
        standardGraduationMapper.deleteStandardGraduation(id);
        //删除毕业标准关联培养目标
        standardGraduationRefCultivationTargetMapper.deleteByGraduationIds(gIds);
        //删除课程关联
        courseRefGraduationMapper.deleteCourseRefGraduationByGraduationIds(gIds);
        if (-1 != pid) {
            StandardGraduation nStg = new StandardGraduation();
            nStg.setParentId(pid);
            List<StandardGraduation> standardGraduationList = standardGraduationMapper.selectStandardGraduationList(nStg);
            if (0 == standardGraduationList.size()) {
                standardGraduationMapper.setLeafIsNode(pid);
            }
        }
        return Message.success();
    }


    @Override
    @Transactional(rollbackFor = {Exception.class})
    public void deleteStandardGraduationBySchemeId(Long schemeId) {
        StandardGraduation param = new StandardGraduation();
        param.setSchemeId(schemeId);
        List<StandardGraduation> standardGraduations = standardGraduationMapper.selectStandardGraduationList(param);
        if (CollectionUtils.isNotEmpty(standardGraduations)) {
            for (StandardGraduation standardGraduation : standardGraduations) {
                deleteStandardGraduationById(standardGraduation.getId());
            }
        }
    }

    @Override
    @Transactional(rollbackFor = {Exception.class})
    public void insetGraduationRefTarget(TowerToTower towerToTower) {
        Long sourceTopId = towerToTower.getSourceTopId();
        Long targetTopId = towerToTower.getTargetTopId();
        StandardGraduation topSg = standardGraduationMapper.selectStandardGraduationById(sourceTopId);
        if (null == targetTopId) {
            clearOldRefGraduation(sourceTopId);
            topSg.setCultivationTargetId(null);
            UserUtils.reflash(topSg);
            standardGraduationMapper.updateCultivationTargetId(topSg);
            return;
        }
        //判断 targetId 是否改变
        Long oldTargetId = topSg.getCultivationTargetId();
        if (null != oldTargetId) {
            if (!oldTargetId.equals(targetTopId)) {
                clearOldRefGraduation(sourceTopId);
            }
        }
        //配置数据
        List<TowerLayer> towerLayers = towerToTower.getRefIdsInfo();
        if (ObjectUtils.isNotEmpty(towerLayers)) {
            List<Long> newGId = new ArrayList<>();
            List<StandardGraduationRefCultivationTarget> insetInfo = new ArrayList<>();
            for (TowerLayer towerLayer : towerLayers) {
                Long gId = towerLayer.getSourceId();
                newGId.add(gId);
                List<Long> targetIds = towerLayer.getTargetIds();
                for (Long targetId : targetIds) {
                    StandardGraduationRefCultivationTarget srt = new StandardGraduationRefCultivationTarget();
                    srt.setGraduationId(gId);
                    srt.setCultivationTargetId(targetId);
                    insetInfo.add(srt);
                }
            }
            standardGraduationRefCultivationTargetMapper.deleteByGraduationIds(newGId);
            if (ObjectUtils.isNotEmpty(insetInfo)) {
                standardGraduationRefCultivationTargetMapper.insetList(insetInfo);
            }
        }
        topSg.setCultivationTargetId(targetTopId);
        UserUtils.reflash(topSg);
        standardGraduationMapper.updateCultivationTargetId(topSg);
    }

    @Override
    @Transactional(rollbackFor = {Exception.class})
    public void insertStandardTemplateGraduationVo(StandardTemplateGraduationVo graduationVo) {
        if (CollectionUtils.isNotEmpty(graduationVo.getIds())) {
            for (Long id : graduationVo.getIds()) {
                List<StandardGraduationVo> standardGraduationVos = standardGraduationMapper.selectStandardGraduationVoAll(id);
                List<StandardGraduationVo> root = TreeBuilderUtils.buildRootTree(standardGraduationVos);
                if (CollectionUtils.isNotEmpty(root)) {
                    StandardGraduation g = root.get(0);
                    BeanUtils.copyProperties(graduationVo, g);
                    g.setSourceId(g.getId());
                    g.setId(null);
                    g.setType(2);
                    UserUtils.reflash(g);
                    standardGraduationMapper.insertStandardGraduation(g);
                    if (CollectionUtils.isNotEmpty(g.getChildren())) {
                        for (StandardGraduation child : g.getChildren()) {
                            processStandardGraduation(child, g.getId(), g.getUrl(),
                                    graduationVo);
                        }
                    }
                }
            }
        }
    }


    private void processStandardGraduation(StandardGraduation vo, Long parentId,
                                           String parentUrl, StandardTemplateGraduationVo standardTemplateGraduationVo) {
        vo.setParentId(parentId);
        // 保存当前节点
        UserUtils.reflash(vo);
        parentUrl = parentUrl + SymbolConstants.COMMA + parentId;
        vo.setUrl(parentUrl);
        vo.setSourceId(vo.getId());
        vo.setId(null);
        BeanUtils.copyProperties(standardTemplateGraduationVo, vo);
        vo.setType(2);
        UserUtils.reflash(vo);
        standardGraduationMapper.insertStandardGraduation(vo);
        // 递归处理子节点
        if (CollectionUtils.isNotEmpty(vo.getChildren())) {
            for (StandardGraduation child : vo.getChildren()) {
                processStandardGraduation(child, vo.getId(),
                        vo.getUrl(), standardTemplateGraduationVo);
            }
        }
    }

    private void clearOldRefGraduation(Long sourceTopId) {
        //根据sourceTopId 查询所有子节点 删除关系数据
        List<StandardGraduation> standardGraduationList = standardGraduationMapper.selectStandardGraduationAll(sourceTopId);
        List<Long> graduationIds = new ArrayList<>();
        for (StandardGraduation standardGraduation : standardGraduationList) {
            graduationIds.add(standardGraduation.getId());
        }
        standardGraduationRefCultivationTargetMapper.deleteByGraduationIds(graduationIds);

    }

    private void clearOldCultivationRef(Long sourceTopId) {
        //根据sourceTopId 查询所有子节点 删除关系数据
        List<StandardCultivation> standardCultivationList = standardCultivationMapper.selectStandardCultivationAll(sourceTopId);
        List<Long> cultivationIds = new ArrayList<>();
        for (StandardCultivation standardCultivation : standardCultivationList) {
            cultivationIds.add(standardCultivation.getId());
        }
        standardCultivationRefGraduationMapper.deleteByCultivationIdsIds(cultivationIds);

    }

    @Override
    public List<StandardGraduation> selectStandardGraduationAll(Long id) {
        return standardGraduationMapper.selectStandardGraduationAll(id);
    }

    @Override
    public List<StandardGraduationVo> selectStandardGraduationRefAll(Long id) {
        List<StandardGraduationVo> list = standardGraduationMapper.selectStandardGraduationVoAll(id);
        List<StandardGraduationVo> tList = new ArrayList<>();
        for (StandardGraduationVo standardGraduationVo : list) {
            Integer leaf = standardGraduationVo.getLeaf();
            if (1 == leaf) {
                List<StandardGraduationVo> standardGraduationVoList = standardCultivationTargetMapper.selectTargetByGraduationId(standardGraduationVo.getId());
                for (StandardGraduationVo graduationVo : standardGraduationVoList) {
                    graduationVo.setParentId(standardGraduationVo.getId());
                    Long gId = graduationVo.getId();
                    graduationVo.setId(gId * -1);
                    graduationVo.setTypeName("target");
                    String[] urlId = graduationVo.getUrl().split(",");
                    List<Long> urlIds = Arrays.stream(urlId).map(Long::valueOf).collect(Collectors.toList());
                    List<String> urlPName = standardCultivationTargetMapper.selectTargetUrlPName(urlIds);
                    graduationVo.setParentNames(urlPName);
                    tList.add(graduationVo);
                }
            }
        }
        list.addAll(tList);
        return list;
    }

    @Override
    public List<StandardCultivationTargetVo> reSelectStandardGraduationRefAll(Long id) {
        ArrayList<StandardCultivationTargetVo> resultList = new ArrayList<>();

        List<StandardGraduationVo> graduationList = standardGraduationMapper.selectStandardGraduationVoAll(id);

        //StandardGraduation sg = standardGraduationMapper.selectStandardGraduationById(id);
        //根据毕业标注 查询关联的培养目标全部结构
        ArrayList<StandardCultivationTarget> sctList = findCultivationTargetByGraduations(graduationList);

        for (StandardCultivationTarget standardCultivationTarget : sctList) {
            List<StandardCultivationTargetVo> tVoList = standardGraduationMapper.selectGByTId(standardCultivationTarget.getId());
            List<StandardCultivationTargetVo> tList = new ArrayList<>();
            for (StandardCultivationTargetVo cultivationTargetVo : tVoList) {
                cultivationTargetVo.setParentId(standardCultivationTarget.getId());
                Long cId = cultivationTargetVo.getId();
                cultivationTargetVo.setId(cId * -1);
                cultivationTargetVo.setTypeName("cultivation");
                String[] urlId = cultivationTargetVo.getUrl().split(",");
                List<Long> urlIds = Arrays.stream(urlId).map(Long::valueOf).collect(Collectors.toList());
                List<String> urlPName = standardGraduationMapper.selectGUrlPName(urlIds);
                cultivationTargetVo.setParentNames(urlPName);
                tList.add(cultivationTargetVo);
            }
            resultList.addAll(tList);
            StandardCultivationTargetVo standardCultivationTargetVo = new StandardCultivationTargetVo();
            BeanUtils.copyProperties(standardCultivationTarget, standardCultivationTargetVo);
            resultList.add(standardCultivationTargetVo);
        }


//        StandardCultivationTarget psct = sctList.stream().filter(sct -> sct.getParentId() == -1).collect(Collectors.toList()).get(0);
//        List<StandardCultivationTargetVo> list = standardCultivationTargetMapper.selectStdCultivationTargetVoAll(psct.getId());
//        List<StandardCultivationTargetVo> tList = new ArrayList<>();
//        for (StandardCultivationTargetVo standardCultivationTargetVo : list) {
//            Integer leaf = standardCultivationTargetVo.getLeaf();
//            if (1 == leaf) {
//                List<StandardCultivationTargetVo> tVoList = standardGraduationMapper.selectGByTId(standardCultivationTargetVo.getId());
//                for (StandardCultivationTargetVo cultivationTargetVo : tVoList) {
//                    cultivationTargetVo.setParentId(standardCultivationTargetVo.getId());
//                    Long cId = cultivationTargetVo.getId();
//                    cultivationTargetVo.setId(cId * -1);
//                    cultivationTargetVo.setTypeName("cultivation");
//                    String[] urlId = cultivationTargetVo.getUrl().split(",");
//                    List<Long> urlIds = Arrays.stream(urlId).map(Long::valueOf).collect(Collectors.toList());
//                    List<String> urlPName = standardGraduationMapper.selectGUrlPName(urlIds);
//                    cultivationTargetVo.setParentNames(urlPName);
//                    tList.add(cultivationTargetVo);
//                }
//            }
//        }
//        list.addAll(tList);


        return resultList;
    }

    @Override
    public ArrayList<StandardCultivationTarget> findCultivationTargetByGraduations(List<? extends StandardGraduation> graduationList) {
        //查询所有关联到的培养目标
        List<StandardCultivationTargetVo> standardCultivationTargetVos = standardCultivationTargetMapper.selectTargetByGraduationIds(graduationList.stream()
                .map(g -> g.getId()).collect(Collectors.toList()));
        ArrayList<StandardCultivationTarget> sctList = new ArrayList<>();
        for (StandardCultivationTarget standardCultivationTarget : standardCultivationTargetVos) {
            if (ObjectUtils.isNotEmpty(sctList) &&
                    sctList.stream().map(c -> c.getId().equals(standardCultivationTarget.getId())).collect(Collectors.toList()).size() != 0) {
                //已经存在不需要继续查询
                continue;
            }
            //查找父节点
            findSct(standardCultivationTarget, sctList);
        }
        return sctList;
    }

    /**
     * 通过任意一级查询全部培养目标
     *
     * @param standardCultivationTarget
     * @param sctList
     */
    private void findSct(StandardCultivationTarget standardCultivationTarget, ArrayList<StandardCultivationTarget> sctList) {
        if (standardCultivationTarget.getParentId() == -1) {
            //查询全部节点
            List<StandardCultivationTarget> standardCultivationTargets = standardCultivationTargetMapper.selectStdCultivationTargetAll(standardCultivationTarget.getId());
            sctList.addAll(standardCultivationTargets);
        } else {
            findSct(standardCultivationTargetMapper.selectCultivationTargetByParentId(standardCultivationTarget.getParentId()), sctList);
        }
    }


    @Override
    public StandardGraduation selectStandardGraduationTree(Long id) {
        List<StandardGraduation> standardGraduationList = selectStandardGraduationAll(id);
        return getGraduationTree(standardGraduationList);
    }

    @Override
    public StandardGraduation getGraduationTree(List<StandardGraduation> standardGraduationList) {
        if (ObjectUtils.isEmpty(standardGraduationList)) {
            return null;
        }
        Map<Long, StandardGraduation> standardCultivationTargetMap = standardGraduationList.parallelStream()
                .collect(Collectors.toMap(StandardGraduation::getId, _standardGraduation -> _standardGraduation));
        standardGraduationList = standardGraduationList.stream().filter(_standardGraduation -> {
            if (DomainFieldConstant.ROOT_NODE_LONG_ID.equals(_standardGraduation.getParentId())) {
                return true;
            }
            StandardGraduation parentStandardGraduation = standardCultivationTargetMap.get(_standardGraduation.getParentId());
            if (ObjectUtils.isNotEmpty(parentStandardGraduation)) {
                if (ObjectUtils.isEmpty(parentStandardGraduation.getChildren())) {
                    parentStandardGraduation.setChildren(new ArrayList<>());
                }
                ((List<StandardGraduation>) parentStandardGraduation.getChildren()).add(_standardGraduation);
            }
            return false;
        }).collect(Collectors.toList());
        if (ObjectUtils.isEmpty(standardGraduationList)) {
            return null;
        }
        return standardGraduationList.get(0);
    }

    @Override
    @Transactional
    public Message deleteBatchStdCultivationTargetById(List<Long> ids) {
        for (int i = 0; i < ids.size(); i++) {
            Long id = ids.get(i);
            List<StandardGraduationVo> db = standardCultivationTargetMapper.selectTargetByGraduationId(id);
            UserUtils.checkDataPermission(db);
            //查询该节点下的所有数据
            List<StandardCultivationTarget> standardCultivationTargetListVo = standardCultivationTargetMapper.selectStdCultivationTargetAll(id);
            // 如果该节点下有数据已配置支撑关系无法删除
            List<Long> tIds = standardCultivationTargetListVo.stream().map(StandardCultivationTarget::getId).collect(Collectors.toList());
            List<StandardGraduationRefCultivationTarget> refList = standardGraduationRefCultivationTargetMapper.selectRefByTaIds(tIds);
            if (0 != refList.size()) {
                return Message.error("已配置支撑关系无法删除");
            }
        }
        //批量删除
        for (Long id : ids) {
            StandardCultivationTarget standardCultivationTarget = standardCultivationTargetMapper.selectStandardCultivationTargetById(id);
            UserUtils.checkDataPermission(standardCultivationTarget);
            Long pid = standardCultivationTarget.getParentId();
            standardCultivationTargetMapper.deleteStandardCultivationTarget(id);
            // 培养目标没有指向谁 所以不用删除上层关系
            if (-1 != pid) {
                StandardCultivationTarget st = new StandardCultivationTarget();
                st.setParentId(pid);
                List<StandardCultivationTarget> standardCultivationTargetList = standardCultivationTargetMapper.selectStandardCultivationTargetList(st);
                if (0 == standardCultivationTargetList.size()) {
                    standardCultivationTargetMapper.setLeafIsNode(pid);
                }
            }
        }
        return Message.success();
    }

    @Override
    @Transactional
    public Message deleteBatchStdGraduationById(List<Long> ids) {
        List<Long> allGIds = new ArrayList<>();
        for (int i = 0; i < ids.size(); i++) {
            Long id = ids.get(i);
            //查询该节点下的所有数据
            List<StandardGraduation> standardGraduationListVo = standardGraduationMapper.selectStandardGraduationAll(id);
            // 如果该节点下有数据已配置支撑关系无法删除
            List<Long> gIds = standardGraduationListVo.stream().map(StandardGraduation::getId).collect(Collectors.toList());
            /**
             * 要判断是否与课程绑定
             */
            if (ObjectUtils.isNotEmpty(gIds)) {
                List<CourseRefGraduation> refList = courseRefGraduationMapper.selectRefBygraduationIds(gIds);
                if (0 != refList.size()) {
                    return Message.error("已配置支撑关系无法删除");
                }
            }
            allGIds.addAll(gIds);
        }
        //批量删除
        for (Long id : ids) {
            StandardGraduation stg = standardGraduationMapper.selectStandardGraduationById(id);
            UserUtils.checkDataPermission(stg);
            Long pid = stg.getParentId();
            standardGraduationMapper.deleteStandardGraduation(id);
            //删除毕业标准关联培养目标
            standardGraduationRefCultivationTargetMapper.deleteByGraduationIds(allGIds);
            //删除课程关联
            courseRefGraduationMapper.deleteCourseRefGraduationByGraduationIds(allGIds);
            if (-1 != pid) {
                StandardGraduation nStg = new StandardGraduation();
                nStg.setParentId(pid);
                List<StandardGraduation> standardGraduationList = standardGraduationMapper.selectStandardGraduationList(nStg);
                if (0 == standardGraduationList.size()) {
                    standardGraduationMapper.setLeafIsNode(pid);
                }
            }
        }

        return Message.success();
    }

    @Override
    public boolean checkIssueAbility(StandardAbility standardAbility) {
        boolean flag = false;
        List<StandardAbility> standardAbilities = standardAbilityMapper.checkIssueAbility(standardAbility);
        if (ObjectUtils.isEmpty(standardAbilities)) {
            //没有下发的能力/素质
            flag = true;
        }
        return flag;
    }

    @Override
    public OverviewTreeVo getOverviewTree(Long majorId, String version, String type) {
        OverviewTreeVo overviewTreeVo = new OverviewTreeVo();
        //查询专业名称
        StandardMajor standardMajor = standardMajorMapper.selectStandardMajorById(majorId);
        overviewTreeVo.setMajorName(standardMajor.getName());
        StandardAbility standardAbility = new StandardAbility();
        standardAbility.setMajorId(majorId);
        standardAbility.setVersion(version);
        switch (type) {
            case "1":
                //能力
                standardAbility.setType(2);
                List<StandardAbility> standardAbilities = standardAbilityMapper.selectStandardAbilityAndParentIdList(standardAbility);
                for (StandardAbility ability : standardAbilities) {
                    List<StandardAbility> abilities = selectAllStdAbilityAndGraduationById(ability.getId());
                    overviewTreeVo.getStandardAbilitieList().addAll(abilities);
                }
                break;
            case "2":
                //素质
                standardAbility.setType(4);
                List<StandardAbility> standardQualities  = standardAbilityMapper.selectStandardAbilityList(standardAbility);
                for (StandardAbility qualities : standardQualities) {
                    List<StandardAbility> qualitieList = selectAllStdAbilityAndGraduationById(qualities.getId());
                    overviewTreeVo.getStandardAbilitieList().addAll(qualitieList);
                }
                break;
            case "3":
                //知识
                SourceDomain sourceDomain = new SourceDomain();
                sourceDomain.setMajorId(majorId);
                sourceDomain.setVersion(version);
                List<SourceDomain> sourceDomains = knowledgeSourceService.selectSourceDomainList(sourceDomain);
                for (SourceDomain domain : sourceDomains) {
                    SourceDomainTreeVo sourceDomainTreeVo = knowledgeSourceService.childrenKnowledgeByDomainId(domain.getId());
                    overviewTreeVo.getSourceDomainTreeVoList().add(sourceDomainTreeVo);
                }
                break;
        }

        return overviewTreeVo;
    }

    @Override
    public List<StandardCultivationTarget> selectStdCultivationTargetAllByTrainingId(Long trainingSchemeId) {
        return standardCultivationTargetMapper.selectStdCultivationTargetAllByTrainingId(trainingSchemeId);
    }

    @Override
    public List checkAbilityList(Long schemeId, String type) {
        PageUtils.startPage();
        return standardAbilityMapper.selectStandardAbilityListBySchemeId(schemeId,type);
    }


    /**
     * -------------------------------------------
     *          毕业标准---代码段结束
     * -------------------------------------------
     */
    /**
     * -------------------------------------------
     *          培养标准---代码段开始
     * -------------------------------------------
     */

    /**
     * 查询培养标准
     */
    @Override
    public StandardCultivation selectStandardCultivationById(Long id) {
        StandardCultivation sc = standardCultivationMapper.selectStandardCultivationById(id);
        StandardCultivation psc = standardCultivationMapper.selectStandardCultivationById(sc.getParentId());
        if (null != psc) {
            sc.setParentName(psc.getName());
        }
        return sc;
    }

    /**
     * 查询培养标准列表
     */
    @Override
    public List<StandardCultivation> selectStandardCultivationList(StandardCultivation standardCultivation) {
        return standardCultivationMapper.selectStandardCultivationList(standardCultivation);
    }

    /**
     * 新增培养标准
     *
     * @param standardCultivation 培养标准
     * @return int
     */
    @Override
    @Transactional(rollbackFor = {Exception.class})
    public StandardCultivation insertStandardCultivation(StandardCultivation standardCultivation) {
        Long pId = standardCultivation.getParentId();
        if (pId == -1L) {
            standardCultivation.setUrl("-1");
            standardCultivation.setLevel(1);
            standardCultivation.setLeaf(DomainFieldConstant.TREE_LEAF_VALUE);
        } else {
            StandardCultivation pStc = standardCultivationMapper.selectStandardCultivationById(pId);
            Integer level = pStc.getLevel();
            String url = pStc.getUrl();
            if (4 == level) {
                throw new UpdateDataException("叶子节点不允许添加下级");
            }
            standardCultivation.setLevel(level + 1);
            standardCultivation.setParentId(pId);
            standardCultivation.setUrl(url + "," + pId);
            standardCultivation.setLeaf(DomainFieldConstant.TREE_LEAF_VALUE);
            pStc.setLeaf(DomainFieldConstant.TREE_FOLDER_VALUE);
            standardCultivationMapper.updateStandardCultivation(pStc);
        }
        UserUtils.reflash(standardCultivation);
        standardCultivationMapper.insertStandardCultivation(standardCultivation);
        return standardCultivation;
    }

    /**
     * 新增培养标准,包含下级
     *
     * @param standardCultivationTree 毕业标准树
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = {Exception.class})
    public int insertStandardCultivationTree(List<StandardCultivation> standardCultivationTree) {
        //先把所有都插入数据库生成id，然后再更新parentId
        List<StandardCultivation> standardCultivationList = TreeEntityUtils.toList(standardCultivationTree);
        standardCultivationMapper.insertStandardCultivationList(standardCultivationList);
        updateStandardCultivationParentId(standardCultivationTree, null);
        standardCultivationMapper.updateStandardCultivationList(standardCultivationList);
        return 1;
    }

    private List<StandardCultivation> updateStandardCultivationParentId(List<StandardCultivation> list, StandardCultivation parent) {
        if (ObjectUtils.isEmpty(list)) {
            return new ArrayList<>();
        }
        list.parallelStream().forEach(_treeEntity -> {
            if (ObjectUtils.isNotEmpty(parent)) {
                _treeEntity.setParentId(parent.getId());
                _treeEntity.setLevel(parent.getLevel() + 1);
                _treeEntity.setUrl(parent.getUrl() + SymbolConstants.COMMA + parent.getId());
            } else {
                _treeEntity.setParentId(DomainFieldConstant.ROOT_NODE_LONG_ID);
                _treeEntity.setLevel(1);
                _treeEntity.setUrl(String.valueOf(DomainFieldConstant.ROOT_NODE_LONG_ID));
            }
            if (ObjectUtils.isNotEmpty(_treeEntity.getChildren())) {
                List<StandardCultivation> children = (List<StandardCultivation>) _treeEntity.getChildren();
                children.parallelStream().forEach(_child -> _child.setParentId(_treeEntity.getId()));
                updateStandardCultivationParentId(children, _treeEntity);
                _treeEntity.setLeaf(DomainFieldConstant.TREE_FOLDER_VALUE);
            } else {
                _treeEntity.setLeaf(DomainFieldConstant.TREE_LEAF_VALUE);
            }
            UserUtils.reflash(_treeEntity);
        });
        return list;
    }

    /**
     * 修改培养标准
     *
     * @param standardCultivation 培养标准
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = {Exception.class})
    public StandardCultivation updateStandardCultivation(StandardCultivation standardCultivation) {
        UserUtils.reflash(standardCultivation);
        standardCultivationMapper.updateStandardCultivation(standardCultivation);
        return standardCultivation;
    }

    /**
     * 删除培养标准
     */

    @Override
    @Transactional(rollbackFor = {Exception.class})
    public Message deleteStandardCultivationById(Long id) {
        StandardCultivation stc = standardCultivationMapper.selectStandardCultivationById(id);
        //查询该节点下的所有数据
        List<StandardCultivationVo> standardCultivationVos = standardCultivationMapper.selectCultivationVoAll(id);
        // 如果该节点下有数据已配置支撑关系无法删除
        List<Long> cuIds = standardCultivationVos.stream().map(StandardCultivationVo::getId).collect(Collectors.toList());
        List<KnowledgeUnitRefStdCultivation> refList = knowledgeUnitRefStdCultivationMapper.selectRefByCuIds(cuIds);
        if (0 != refList.size()) {
            return Message.error("已配置支撑关系无法删除");
        }
        Long pid = stc.getParentId();
        standardCultivationMapper.deleteStandardCultivationById(id);
        //删除培养标准关联毕业标准
        standardCultivationRefGraduationMapper.deleteByCultivationIdsIds(cuIds);
        if (-1 != pid) {
            StandardCultivation nStg = new StandardCultivation();
            nStg.setParentId(pid);
            List<StandardCultivation> standardCultivationList = standardCultivationMapper.selectStandardCultivationList(nStg);
            if (0 == standardCultivationList.size()) {
                standardCultivationMapper.setLeafIsNode(pid);
            }
        }
        return Message.success();
    }

    @Override
    public StandardCultivationRefGraduationListVo selectStandardCultivationByCultivationId(Long cultivationId) {
        List<StandardCultivationVo> standardCultivationVoList = standardCultivationMapper.selectCultivationRefGraduation(cultivationId);
        StandardCultivation standardCultivation = standardCultivationMapper.selectStandardCultivationById(cultivationId);
        Long graduationId = standardCultivation.getGraduationId();
        StandardGraduation sg = standardGraduationMapper.selectStandardGraduationById(graduationId);
        StandardCultivationRefGraduationListVo vo = new StandardCultivationRefGraduationListVo();
        if (null != sg) {
            vo.setTargetTopId(sg.getId());
        }
        vo.setStandardCultivationList(standardCultivationVoList);
        return vo;
    }

    @Override
    public List<Long> selectGraduationListByCultivationId(Long cultivationId) {
        StandardCultivation sc = standardCultivationMapper.selectStandardCultivationById(cultivationId);
        Long gId = sc.getGraduationId();
        return standardGraduationMapper.selectStandardGraduationRefByCultivationId(gId, cultivationId);
    }

    @Override
    @Transactional(rollbackFor = {Exception.class})
    public void insetCultivationRefGraduation(TowerToTower towerToTower) {
        Long sourceTopId = towerToTower.getSourceTopId();
        Long targetTopId = towerToTower.getTargetTopId();
        StandardCultivation topSg = standardCultivationMapper.selectStandardCultivationById(sourceTopId);
        if (null == targetTopId) {
            clearOldCultivationRef(sourceTopId);
            topSg.setGraduationId(null);
            UserUtils.reflash(topSg);
            standardCultivationMapper.updateGraduationId(topSg);
            return;
        }
        //判断 targetId 是否改变
        Long oldGraduationId = topSg.getGraduationId();
        if (null != oldGraduationId) {
            if (!oldGraduationId.equals(targetTopId)) {
                clearOldCultivationRef(sourceTopId);
            }
        }
        //配置数据
        List<TowerLayer> towerLayers = towerToTower.getRefIdsInfo();
        if (ObjectUtils.isNotEmpty(towerLayers)) {
            List<Long> newCId = new ArrayList<>();
            List<StandardCultivationRefGraduation> insetInfo = new ArrayList<>();
            for (TowerLayer towerLayer : towerLayers) {
                Long cId = towerLayer.getSourceId();
                newCId.add(cId);
                List<Long> GraduationIds = towerLayer.getTargetIds();
                for (Long GraduationId : GraduationIds) {
                    StandardCultivationRefGraduation crg = new StandardCultivationRefGraduation();
                    crg.setCultivationId(cId);
                    crg.setGraduationId(GraduationId);
                    insetInfo.add(crg);
                }
            }
            standardCultivationRefGraduationMapper.deleteByCultivationIdsIds(newCId);
            if (ObjectUtils.isNotEmpty(insetInfo)) {
                standardCultivationRefGraduationMapper.insetList(insetInfo);
            }
        }
        topSg.setGraduationId(targetTopId);
        UserUtils.reflash(topSg);
        standardCultivationMapper.updateGraduationId(topSg);
    }


    @Override
    public List<StandardCultivation> selectStandardCultivationAll(Long id) {
        return standardCultivationMapper.selectStandardCultivationAll(id);
    }

    @Override
    public List<StandardCultivationVo> selectStdCultivationRefAll(Long id) {
        List<StandardCultivationVo> list = standardCultivationMapper.selectCultivationVoAll(id);
        List<StandardCultivationVo> cList = new ArrayList<>();
        for (StandardCultivation standardCultivation : list) {
            Integer leaf = standardCultivation.getLeaf();
            if (1 == leaf) {
                List<StandardCultivationVo> cVoList = standardGraduationMapper.selectGByCId(standardCultivation.getId());
                for (StandardCultivationVo cultivationVo : cVoList) {
                    cultivationVo.setParentId(standardCultivation.getId());
                    Long cId = cultivationVo.getId();
                    cultivationVo.setId(cId * -1);
                    cultivationVo.setTypeName("graduation");
                    String[] urlId = cultivationVo.getUrl().split(",");
                    List<Long> urlIds = Arrays.stream(urlId).map(Long::valueOf).collect(Collectors.toList());
                    List<String> urlPName = standardGraduationMapper.selectGUrlPName(urlIds);
                    cultivationVo.setParentNames(urlPName);
                    cList.add(cultivationVo);
                }
            }
        }
        list.addAll(cList);
        return list;
    }

    @Override
    public List<StandardGraduationVo> reSelectStdCultivationRefAll(Long id) {
        StandardCultivation sc = standardCultivationMapper.selectStandardCultivationById(id);
        Long gId = sc.getGraduationId();
        List<StandardGraduationVo> list = standardGraduationMapper.selectStandardGraduationVoAll(gId);
        List<StandardGraduationVo> gList = new ArrayList<>();
        for (StandardGraduationVo standardGraduationVo : list) {
            Integer leaf = standardGraduationVo.getLeaf();
            if (1 == leaf) {
                List<StandardGraduationVo> GVoList = standardCultivationMapper.selectCByGId(standardGraduationVo.getId());
                for (StandardGraduationVo graduationVo : GVoList) {
                    graduationVo.setParentId(standardGraduationVo.getId());
                    Long cId = graduationVo.getId();
                    graduationVo.setId(cId * -1);
                    graduationVo.setTypeName("cultivation");
                    String[] urlId = graduationVo.getUrl().split(",");
                    List<Long> urlIds = Arrays.stream(urlId).map(Long::valueOf).collect(Collectors.toList());
                    List<String> urlPName = standardCultivationMapper.selectCUrlPName(urlIds);
                    graduationVo.setParentNames(urlPName);
                    gList.add(graduationVo);
                }
            }
        }
        list.addAll(gList);
        return list;
    }

    @Override
    public StandardCultivation selectStandardCultivationTree(Long id) {
        List<StandardCultivation> standardCultivationList = selectStandardCultivationAll(id);
        if (ObjectUtils.isEmpty(standardCultivationList)) {
            return null;
        }
        Map<Long, StandardCultivation> standardCultivationMap = standardCultivationList.parallelStream()
                .collect(Collectors.toMap(StandardCultivation::getId, _standardCultivation -> _standardCultivation));
        standardCultivationList = standardCultivationList.stream().filter(_standardCultivation -> {
            if (DomainFieldConstant.ROOT_NODE_LONG_ID.equals(_standardCultivation.getParentId())) {
                return true;
            }
            StandardCultivation parentStandardCultivation = standardCultivationMap.get(_standardCultivation.getParentId());
            if (ObjectUtils.isNotEmpty(parentStandardCultivation)) {
                if (ObjectUtils.isEmpty(parentStandardCultivation.getChildren())) {
                    parentStandardCultivation.setChildren(new ArrayList<>());
                }
                ((List<StandardCultivation>) parentStandardCultivation.getChildren()).add(_standardCultivation);
            }
            return false;
        }).collect(Collectors.toList());
        if (ObjectUtils.isEmpty(standardCultivationList)) {
            return null;
        }
        return standardCultivationList.get(0);
    }

    /*
     * ----------------------------------------------------
     *          培养标准---代码段结束
     * ----------------------------------------------------
     */

    /*
     * ----------------------------------------------------
     *          毕业标准与培养标准关联---代码段开始
     * ----------------------------------------------------
     */


    /**
     * 根据毕业标准id查询毕业标准与培养目标关联
     *
     * @param standardGraduationIds
     * @return
     */
    @Override
    public List<StandardGraduationRefCultivationTarget> selectStandardGraduationRefCultivationTargetByStandardGraduationId(List<Long> standardGraduationIds) {
        List<StandardGraduationRefCultivationTarget> refList = standardGraduationRefCultivationTargetMapper.selectRefByGraduationIds(standardGraduationIds);
        return refList;
    }


    @Override
    public List<StandardCultivationRefGraduation> selectStandardCultivationRefGraduationByStandardCultivationId(List<Long> standardCultivationIds) {
        List<StandardCultivationRefGraduation> standardCultivationRefGraduationList = standardCultivationRefGraduationMapper.selectRefByCultivationIds(standardCultivationIds);
        return standardCultivationRefGraduationList;
    }


    @Override
    @Transactional(rollbackFor = {Exception.class})
    public Message graduationIssueKnowledgeAbilityQuality(GraduationIssueVo graduationVo) {
        List<StandardGraduation> standardGraduations = standardGraduationMapper.selectStandardGraduationAll(graduationVo.getId());
        StandardGraduation root = TreeBuilderUtils.buildRootTree(standardGraduations).get(0);
        if (root.getGraduationType() == null) {
            return Message.success();
        }
        if (graduationVo.getCollegeId() == null) {
            graduationVo.setCollegeId(root.getCollegeId());
        }
        if (graduationVo.getCategoryId() == null) {
            graduationVo.setCategoryId(root.getCategoryId());
        }
        if (graduationVo.getMajorId() == null) {
            graduationVo.setMajorId(root.getMajorId());
        }
        // 1:知识，2：能力，3：素质
        if ("1".equals(root.getGraduationType())) {
            if (CollectionUtils.isNotEmpty(root.getChildren())) {
                List<StandardGraduation> children = root.getChildren();
                for (StandardGraduation child : children) {
                    SourceDomain sourceDomain = new SourceDomain();
                    sourceDomain.setName(child.getName());
                    sourceDomain.setCollegeId(graduationVo.getCollegeId());
                    sourceDomain.setMajorId(graduationVo.getMajorId());
                    sourceDomain.setCategoryId(graduationVo.getCategoryId());
                    sourceDomain.setVersion(graduationVo.getVersion());
                    UserUtils.reflash(sourceDomain);
                    sourceDomainMapper.insertSourceDomain(sourceDomain);
                    if (CollectionUtils.isNotEmpty(child.getChildren())) {
                        for (StandardGraduation childChild : child.getChildren()) {
                            SourceUnit sourceUnit = new SourceUnit();
                            sourceUnit.setName(childChild.getName());
                            sourceUnitMapper.insertSourceUnit(sourceUnit);
                            SourceDomainRefUnit requestSourceDomainRefUnit = new SourceDomainRefUnit();
                            requestSourceDomainRefUnit.setDomainId(sourceDomain.getId());
                            requestSourceDomainRefUnit.setUnitId(sourceUnit.getId());
                            sourceDomainRefUnitMapper.insertSourceDomainRefUnit(requestSourceDomainRefUnit);
                        }
                    }
                }
            }
        } else if ("2".equals(root.getGraduationType())) {
            //查询对应专业下能力素质中是否有数据，有数据的不能下发
            if (checkData(2, graduationVo)) {
                return Message.error("该专业能力中存在数据，无法再次下发");
            }
            if (CollectionUtils.isNotEmpty(root.getChildren())) {
                List<StandardGraduation> children = root.getChildren();
                for (StandardGraduation child : children) {
                    addIssueStandard(child, graduationVo, 2);
                }
            }
        } else if ("3".equals(root.getGraduationType())) {
            //查询对应专业下能力素质中是否有数据，有数据的不能下发
            if (checkData(4, graduationVo)) {
                return Message.error("该专业素质中存在数据，无法再次下发");
            }
            if (CollectionUtils.isNotEmpty(root.getChildren())) {
                List<StandardGraduation> children = root.getChildren();
                for (StandardGraduation child : children) {
                    addIssueStandard(child, graduationVo, 4);
                }
            }
        }
        return Message.success();
    }

    @Override
    public TreeVo graduationOverviewTree(Long majorId, String version,Integer type) {
        StandardMajor standardMajor = standardMajorMapper.selectStandardMajorById(majorId);
        TreeVo treeVo = new TreeVo();
        treeVo.setId(majorId);
        treeVo.setName(standardMajor.getName());
        StandardGraduation query = new StandardGraduation();
        query.setType(type);
        query.setMajorId(majorId);
        query.setVersion(version);
        List<StandardGraduation> standardGraduations = standardGraduationMapper.selectStandardGraduationListNoJoin(query);
        if (CollectionUtils.isNotEmpty(standardGraduations)){
            List<TreeVo> children = new ArrayList<>();
            // 毕业要求树处理
            for (StandardGraduation standardGraduation : standardGraduations) {
                TreeVo t = new TreeVo();
                t.setId(standardGraduation.getId());
                t.setName(standardGraduation.getName());
                t.setParentId(standardGraduation.getParentId());
                children.add(t);
                // 处理培养目标树
                List<StandardGraduationRefCultivationTarget> standardGraduationRefCultivationTargets = standardGraduationRefCultivationTargetMapper.selectRefByGraduationId(standardGraduation.getId());
                if (CollectionUtils.isNotEmpty(standardGraduationRefCultivationTargets)){
                    List<Long> cultivationTargetIds = standardGraduationRefCultivationTargets.stream().map(a -> a.getCultivationTargetId()).collect(Collectors.toList());
                    // Map<Long,String> map = standardCultivationTargetMapper.selectTargetByGraduationIds(cultivationTargetIds).stream().collect(Collectors.toMap(StandardCultivationTargetVo::getId,StandardCultivationTargetVo::getName));
                    List<StandardCultivationTarget> standardCultivationTargetVos = standardCultivationTargetMapper.selectIds(cultivationTargetIds);
                    for (StandardCultivationTarget standardCultivationTargetVo : standardCultivationTargetVos) {
                        TreeVo c = new TreeVo();
                        c.setId(standardCultivationTargetVo.getId());
                        c.setName(standardCultivationTargetVo.getName());
                        c.setParentId(standardGraduation.getId());
                        children.add(c);
                    }
                }
            }
            treeVo.setChildren(TreeBuilderUtils.buildRootTree(children));
        }
        return treeVo;
    }


    private boolean checkData(int type, GraduationIssueVo graduationVo) {
        StandardAbility standardAbility = new StandardAbility();
        standardAbility.setMajorId(graduationVo.getMajorId());
        standardAbility.setType(type);
        standardAbility.setVersion(graduationVo.getVersion());
        standardAbility.setParentId(-1L);
        List<StandardAbility> standardAbilities = standardAbilityMapper.selectStandardAbilityList(standardAbility);
        if (ObjectUtils.isEmpty(standardAbilities)) {
            return false;
        }
        return true;
    }


    private void addIssueStandard(StandardGraduation child, GraduationIssueVo graduationVo, Integer type) {
        StandardAbility standardAbility = new StandardAbility();
        standardAbility.setName(child.getName());
        standardAbility.setCollegeId(graduationVo.getCollegeId());
        standardAbility.setCategoryId(graduationVo.getCategoryId());
        standardAbility.setMajorId(graduationVo.getMajorId());
        standardAbility.setSubMajorId(graduationVo.getSubMajorId());
        standardAbility.setVersion(graduationVo.getVersion());
        standardAbility.setType(type);
        standardAbility.setSourceId(graduationVo.getId());
        if (CollectionUtils.isNotEmpty(child.getChildren())) {
            List<StandardAbility> standardAbilities = new ArrayList<>();
            for (StandardGraduation childChild : child.getChildren()) {
                StandardAbility s = new StandardAbility();
                s.setName(childChild.getName());
                s.setSourceId(childChild.getId());
                standardAbilities.add(s);
            }
            standardAbility.setChildren(standardAbilities);
        }
        updateStdAbilityTree(standardAbility);
    }


    @Override
    @Transactional
    public void graduationBindingScheme(GraduationBindingSchemeVo graduationBindingSchemeVo) {
        // 校验一个培养方案 知识能力素质 不能重复
        if (graduationBindingSchemeVo.getSchemeId() == null || CollectionUtils.isEmpty(graduationBindingSchemeVo.getGraduationIds())){
            throw new RuntimeException("培养方案和毕业要求不能为空！");
        }
        StandardGraduation requestParam = new StandardGraduation();
        requestParam.setSchemeId(graduationBindingSchemeVo.getSchemeId());
        List<StandardGraduation> graduationRefSchemes = standardGraduationMapper.selectStandardGraduationList(requestParam);

        List<StandardGraduation> standardGraduations = standardGraduationMapper.selectStandardGraduationByIds(graduationBindingSchemeVo.getGraduationIds());
        if (CollectionUtils.isNotEmpty(graduationRefSchemes)){
            List<String> existGraduationTypes = standardGraduations.stream().map(StandardGraduation::getGraduationType).collect(Collectors.toList());
            for (StandardGraduation graduationRefScheme : graduationRefSchemes) {
                if (existGraduationTypes.contains(graduationRefScheme.getGraduationType())){
                    String s = "";
                    if ("1".equals(graduationRefScheme.getGraduationType())){
                        s= "知识";
                    }else if ("2".equals(graduationRefScheme.getGraduationType())){
                        s= "能力";
                    }else if ("3".equals(graduationRefScheme.getGraduationType())){
                        s= "素质";
                    }
                    throw new RuntimeException("该培养方案对应的" +s+ "已存在，不能重复！");
                }
            }
        }
        for (StandardGraduation standardGraduation : standardGraduations) {
            List<StandardGraduationVo> standardGraduationVos = standardGraduationMapper.selectStandardGraduationVoAll(standardGraduation.getId());
            StandardGraduationVo root = TreeBuilderUtils.buildRootTree(standardGraduationVos).get(0);
            root.setId(null);
            root.setParentId(-1L);
            root.setUrl("-1");
            root.setSchemeId(graduationBindingSchemeVo.getSchemeId());
            UserUtils.reflash(root);
            root.setType(2);
            standardGraduationMapper.insertStandardGraduation(root);
            if (CollectionUtils.isNotEmpty(root.getChildren())) {
                for (StandardGraduation child : root.getChildren()) {
                    processStandardGraduation(child, root.getId(), root.getUrl(),graduationBindingSchemeVo.getSchemeId());
                }
            }
        }
    }

    private void processStandardGraduation(StandardGraduation vo, Long parentId,
                                           String parentUrl,Long schemeId) {
        vo.setParentId(parentId);
        // 保存当前节点
        UserUtils.reflash(vo);
        parentUrl = parentUrl + SymbolConstants.COMMA + parentId;
        vo.setUrl(parentUrl);
        vo.setSourceId(vo.getId());
        vo.setId(null);
        vo.setSchemeId(schemeId);
        UserUtils.reflash(vo);
        vo.setType(2);
        standardGraduationMapper.insertStandardGraduation(vo);
        // 递归处理子节点
        if (CollectionUtils.isNotEmpty(vo.getChildren())) {
            for (StandardGraduation child : vo.getChildren()) {
                processStandardGraduation(child, vo.getId(),
                        vo.getUrl(),schemeId);
            }
        }
    }
}