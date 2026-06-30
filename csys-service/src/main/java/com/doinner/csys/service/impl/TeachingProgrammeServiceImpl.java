package com.doinner.csys.service.impl;

import com.alibaba.fastjson2.JSONObject;
import com.doinner.common.core.domain.Message;
import com.doinner.csys.constant.CourseConstant;
import com.doinner.csys.dao.*;
import com.doinner.csys.domain.*;
import com.doinner.csys.entity.csys.TeachProgrammeDocumentGenerator;
import com.doinner.csys.service.CommonService;
import com.doinner.csys.service.TeachingProgrammeService;
import com.doinner.csys.utils.PaginationUtils;
import com.doinner.csys.utils.RestTemplateUtils;
import com.doinner.csys.utils.TreeBuilderUtils;
import com.doinner.csys.utils.UserUtils;
import com.doinner.file.api.domain.FileInfo;
import com.doinner.file.api.domain.vo.FileInfoVO;
import com.doinner.file.api.service.RemoteFileInfoService;
import com.doinner.system.domain.custom.CustomDept;
import com.doinner.system.domain.entity.SysDept;
import com.doinner.system.service.DoinnerDeptService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class TeachingProgrammeServiceImpl implements TeachingProgrammeService {

    private final static Logger logger = LoggerFactory.getLogger(TeachingProgrammeServiceImpl.class);

    @Resource
    private TeachingProgrammeOutlineMapper outlineMapper;

    @Resource
    private TeachingProgrammeTemplateMapper templateMapper;

    @Resource
    private TeachingProgrammeInstanceMapper instanceMapper;

    @Resource
    private TeachingProgrammeAttributeMapper attributeMapper;

    @Resource
    private TeachingProgrammeInstanceExtractMapper instanceExtractMapper;

    @Resource
    private TeachingProgrammeInstanceExRefMajorMapper instanceExRefMajorMapper;

    @Resource
    private CommonService commonService;
    @Resource
    private RemoteFileInfoService remoteFileInfoService;

    @Resource
    private DoinnerDeptService doinnerDeptService;


    @Value("${category.TeachingProgramme}")
    private String teachingProgrammeCategoryId;

    @Value("${ai.extract.url:http://27.132.187.88:9998/api/tp/trainingStandardExtract}")
    private String aiExtractUrl;

    @Override
    @Transactional(rollbackFor = {Exception.class})
    public TeachingProgrammeOutline addOutline(TeachingProgrammeOutline outline) {
        UserUtils.reflash(outline);
        outlineMapper.insertTeachingProgrammeOutline(outline);
        return outline;
    }

    @Override
    @Transactional(rollbackFor = {Exception.class})
    public TeachingProgrammeOutline updateOutline(TeachingProgrammeOutline outline) {
        UserUtils.reflash(outline);
        TeachingProgrammeOutline db = outlineMapper.selectTeachingProgrammeOutlineById(outline.getId());
        UserUtils.checkDataPermission(db);
        outlineMapper.updateTeachingProgrammeOutline(outline);
        return outline;
    }

    @Override
    public List<TeachingProgrammeOutline> outlineList(TeachingProgrammeOutline outline) {
        return outlineMapper.selectTeachingProgrammeOutlineList(outline);
    }

    @Override
    @Transactional(rollbackFor = {Exception.class})
    public void deleteOutlines(Long[] ids) {
        for (Long id : ids) {
            checkOutlineUpdate(id);
        }
        templateMapper.deleteTeachingProgrammeAttributeByOutlineIds(ids);
        outlineMapper.deleteTeachingProgrammeOutlineByIds(ids);
    }

    @Override
    public TeachingProgrammeOutline selectOutlineTree(Long id) {
        TeachingProgrammeOutline teachingProgrammeOutline = outlineMapper.selectTeachingProgrammeOutlineById(id);
        TeachingProgrammeTemplate teachingProgrammeTemplate = new TeachingProgrammeTemplate();
        teachingProgrammeTemplate.setOutlineId(id);
        List<TeachingProgrammeTemplate> teachingProgrammeTemplates = templateMapper.selectTeachingProgrammeTemplateList(teachingProgrammeTemplate);
        if (CollectionUtils.isNotEmpty(teachingProgrammeTemplates)){
            teachingProgrammeOutline.setAttributeInstances(TreeBuilderUtils.buildRootTree(teachingProgrammeTemplates));
        }
        return teachingProgrammeOutline;
    }

    @Override
    @Transactional(rollbackFor = {Exception.class})
    public TeachingProgrammeOutline addTemplate(TeachingProgrammeOutline outline) {
        UserUtils.reflash(outline);
        outlineMapper.insertTeachingProgrammeOutline(outline);
        // 递归处理每个根节点
        for (TeachingProgrammeTemplate template : outline.getAttributeInstances()) {
            processTemplate(template, null, 1L,outline.getId());
        }
        return outline;
    }

    @Override
    @Transactional(rollbackFor = {Exception.class})
    public TeachingProgrammeOutline updateTemplate(TeachingProgrammeOutline outline) {
        UserUtils.reflash(outline);
        // checkOutlineUpdate(outline.getId());
        outlineMapper.updateTeachingProgrammeOutline(outline);
        // 先删
        templateMapper.deleteTeachingProgrammeAttributeByOutlineId(outline.getId());
        // 递归处理每个根节点
        for (TeachingProgrammeTemplate template : outline.getAttributeInstances()) {
            processTemplate(template, null, 1L,outline.getId());
        }
        return outline;
    }

    /**
     * @param outlineId
     */
    private void checkOutlineUpdate(Long outlineId){
        TeachingProgrammeOutline db = outlineMapper.selectTeachingProgrammeOutlineById(outlineId);
        UserUtils.checkDataPermission(db);
        TeachingProgrammeInstance teachingProgrammeInstance = new TeachingProgrammeInstance();
        teachingProgrammeInstance.setOutlineId(outlineId);
        List<TeachingProgrammeInstance> teachingProgrammeInstances = instanceMapper.selectTeachingProgrammeInstanceList(teachingProgrammeInstance);
        if (CollectionUtils.isNotEmpty(teachingProgrammeInstances)){
            throw new RuntimeException("模板被引用后不允许删除！");
        }
    }


    /**
     * 递归处理模板节点
     */
    private void processTemplate(TeachingProgrammeTemplate template, Long parentId, Long level,Long outlineId) {
        // 设置层级和父节点
        template.setParentId(parentId == null ? -1 : parentId );
        template.setLevel(level);
        template.setOutlineId(outlineId);
        template.setLeaf(CollectionUtils.isEmpty(template.getChildren()) ? 1L : 0L);
        // 保存当前节点
        templateMapper.insertTeachingProgrammeTemplate(template);
        // 递归处理子节点
        if (CollectionUtils.isNotEmpty(template.getChildren())) {
            for (TeachingProgrammeTemplate child : template.getChildren()) {
                processTemplate(child, template.getId(), level + 1,outlineId);
            }
        }
    }

    @Override
    @Transactional(rollbackFor = {Exception.class})
    public TeachingProgrammeInstance addInstance(TeachingProgrammeInstance instance) {
        UserUtils.reflash(instance);
        instanceMapper.insertTeachingProgrammeInstance(instance);
        return instance;
    }

    @Override
    @Transactional(rollbackFor = {Exception.class})
    public TeachingProgrammeInstance updateInstance(TeachingProgrammeInstance instance) {
        UserUtils.reflash(instance);
        instanceMapper.updateTeachingProgrammeInstance(instance);
        return instance;
    }

    @Override
    public TeachingProgrammeInstance selectInstance(Long id) {
        TeachingProgrammeInstance teachingProgrammeInstance = instanceMapper.selectTeachingProgrammeInstanceById(id);
        if (teachingProgrammeInstance == null){
            return null;
        }
        TeachingProgrammeAttribute attribute = new TeachingProgrammeAttribute();
        attribute.setInstanceId(id);
        List<TeachingProgrammeAttribute> teachingProgrammeAttributes = attributeMapper.selectTeachingProgrammeAttributeList(attribute);
        if (CollectionUtils.isNotEmpty(teachingProgrammeAttributes)){
            teachingProgrammeInstance.setAttributeInstances(TreeBuilderUtils.buildRootTree(teachingProgrammeAttributes));
        }else {
            TeachingProgrammeTemplate teachingProgrammeTemplate = new TeachingProgrammeTemplate();
            teachingProgrammeTemplate.setOutlineId(teachingProgrammeInstance.getOutlineId());
            List<TeachingProgrammeTemplate> teachingProgrammeTemplates = templateMapper.selectTeachingProgrammeTemplateList(teachingProgrammeTemplate);
            if (CollectionUtils.isNotEmpty(teachingProgrammeTemplates)){
                List<TeachingProgrammeTemplate> tree = TreeBuilderUtils.buildRootTree(teachingProgrammeTemplates);
                List<TeachingProgrammeAttribute> attributes = new ArrayList<>();
                for (TeachingProgrammeTemplate programmeTemplate : tree) {
                    TeachingProgrammeAttribute a = convertTemplateToAttribute(programmeTemplate,id);
                    attributes.add(a);
                }
                teachingProgrammeInstance.setAttributeInstances(attributes);
            }
        }
        return teachingProgrammeInstance;
    }


    /**
     * 将单个 TeachingProgrammeTemplate 转换为 TeachingProgrammeAttribute
     */
    private TeachingProgrammeAttribute convertTemplateToAttribute(
            TeachingProgrammeTemplate template,Long instanceId) {
        if (template == null) {
            return null;
        }
        TeachingProgrammeAttribute attribute = new TeachingProgrammeAttribute();
        attribute.setAttributeName(template.getAttributeName());
        attribute.setTemplateParentId(template.getParentId());
        attribute.setTemplateId(template.getId());
        attribute.setInstanceId(instanceId);
        // 给前端构建树
        attribute.setId(template.getId());
        // 转换子节点集合
        if (CollectionUtils.isNotEmpty(template.getChildren())) {
            List<TeachingProgrammeAttribute> childrenList = new ArrayList<>();
            for (TeachingProgrammeTemplate child : template.getChildren()) {
                childrenList.add(convertTemplateToAttribute(child,instanceId));
            }
            attribute.setChildren(childrenList);
        }
        return attribute;
    }


    @Override
    public List<TeachingProgrammeInstance> instanceList(TeachingProgrammeInstance instance) {
        // 排序方式归一化: ascending/descending -> asc/desc
        if (StringUtils.isNotBlank(instance.getOrder())) {
            if (CourseConstant.CUR_SORT_ASC.equals(instance.getOrder())) {
                instance.setOrder("asc");
            } else if (CourseConstant.CUR_SORT_DESC.equals(instance.getOrder())) {
                instance.setOrder("desc");
            }
        }
        List<TeachingProgrammeInstance> teachingProgrammeInstances = instanceMapper.selectTeachingProgrammeInstanceList(instance);
        Map<Long, String> deptIdNameMap = doinnerDeptService.list(new CustomDept()).getData().parallelStream().collect(Collectors.toMap(SysDept::getDeptId,SysDept::getDeptName,(a, b)->a));
        for (TeachingProgrammeInstance teachingProgrammeInstance : teachingProgrammeInstances) {
            if (StringUtils.isNotBlank(teachingProgrammeInstance.getFileId())){
                teachingProgrammeInstance.setFileName(teachingProgrammeInstance.getName()+".docx");
            }
            if (teachingProgrammeInstance.getCollegeId() != null){
                teachingProgrammeInstance.setCollegeName(deptIdNameMap.get(teachingProgrammeInstance.getCollegeId()));
            }
        }
        return teachingProgrammeInstances;
    }

    @Override
    @Transactional(rollbackFor = {Exception.class})
    public void deleteInstance(Long[] ids) {
        for (Long id : ids) {
            TeachingProgrammeInstance db = instanceMapper.selectTeachingProgrammeInstanceById(id);
            UserUtils.checkDataPermission(db);
            if(db.getStatus() == 1){
                throw new RuntimeException(db.getName()+"审核状态为：审核中，不能删除！");
            }else if(db.getStatus() == 2){
                throw new RuntimeException(db.getName()+"审核状态为：审核通过，不能删除！");
            }
        }
        instanceMapper.deleteTeachingProgrammeInstanceByIds(ids);
        for (Long id : ids) {
            attributeMapper.deleteTeachingProgrammeAttributeByInstanceId(id);
        }
    }

    @Override
    @Transactional
    public TeachingProgrammeInstance createWord(Long instanceId) {
        //教学大纲查询
        TeachingProgrammeInstance instance = selectInstance(instanceId);
        try {
            if(ObjectUtils.isNotEmpty(instance.getFileId())){
                //查找文件
                FileInfo fileInfo = remoteFileInfoService.getFileInfo(instance.getFileId()).getData();
                if(ObjectUtils.isNotEmpty(fileInfo)){
                    //已有文件先删除
                    Message data = remoteFileInfoService.delete(fileInfo.getId().toString());
                    System.out.println(data);
                }
            }
            InputStream inputStream = TeachProgrammeDocumentGenerator.generateWordDocument(instance);
            String fileId = commonService.uploadFile(inputStream, instance.getName()+".docx", teachingProgrammeCategoryId);
            //更新instance
            instance.setFileId(fileId);
            setUrl(instance);
            instanceMapper.updateInstanceFileId(instance.getDownloadUrl(),instance.getPreviewUrl(),instance.getFileId(),instanceId);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return instance;
    }

    private void setUrl(TeachingProgrammeInstance instance) {
        FileInfoVO fileInfo = new FileInfoVO();
        fileInfo.setFileId(instance.getFileId());
        List<FileInfo> fileInfos = remoteFileInfoService.list(fileInfo).getData();
        if(ObjectUtils.isNotEmpty(fileInfos)){
            FileInfo file = fileInfos.get(0);
            instance.setDownloadUrl(file.getDownloadUrl());
            instance.setPreviewUrl(file.getPreviewUrl());
            //将fileId替换为id
            instance.setFileId(file.getId().toString());
        }
    }

    @Override
    public FileInfo downloadAndPreView(String fileId) {
        FileInfoVO fileInfo = new FileInfoVO();
        fileInfo.setFileId(fileId);
        List<FileInfo> fileInfos = remoteFileInfoService.list(fileInfo).getData();
        if(ObjectUtils.isNotEmpty(fileInfos)){
            return fileInfos.get(0);
        }
        return new FileInfo();
    }


    @Override
    @Transactional(rollbackFor = {Exception.class})
    public TeachingProgrammeInstance instanceAddDetails(TeachingProgrammeInstance instance) {
        UserUtils.reflash(instance);
        instanceMapper.insertTeachingProgrammeInstance(instance);
        if (CollectionUtils.isNotEmpty(instance.getAttributeInstances())){
            for (TeachingProgrammeAttribute attributeInstance : instance.getAttributeInstances()) {
                processInstance(attributeInstance,null,instance.getId());
            }
        }
        return instance;
    }

    @Override
    @Transactional(rollbackFor = {Exception.class})
    public TeachingProgrammeInstance instanceUpdateDetails(TeachingProgrammeInstance instance) {
        UserUtils.reflash(instance);
        instanceMapper.updateTeachingProgrammeInstance(instance);
        attributeMapper.deleteTeachingProgrammeAttributeByInstanceId(instance.getId());
        if (CollectionUtils.isNotEmpty(instance.getAttributeInstances())){
            for (TeachingProgrammeAttribute attributeInstance : instance.getAttributeInstances()) {
                processInstance(attributeInstance,null,instance.getId());
            }
        }
        return instance;
    }



    private void processInstance(@NotNull TeachingProgrammeAttribute attributeInstance, Long parentId, Long instanceId){
        attributeInstance.setParentId(parentId == null ? -1 : parentId );
        attributeInstance.setInstanceId(instanceId);
        attributeMapper.insertTeachingProgrammeAttribute(attributeInstance);
        if (CollectionUtils.isNotEmpty(attributeInstance.getChildren())){
            for (TeachingProgrammeAttribute child : attributeInstance.getChildren()) {
                processInstance(child,attributeInstance.getId(),instanceId);
            }
        }
    }


    @Override
    @Transactional(rollbackFor = {Exception.class})
    public TeachingProgrammeInstanceExtract extractTeachingProgramme(TeachingProgrammeInstanceExtract teachingProgrammeInstanceExtract) {
        TeachingProgrammeInstance teachingProgrammeInstance = instanceMapper.selectTeachingProgrammeInstanceById(teachingProgrammeInstanceExtract.getInstanceId());
        if (teachingProgrammeInstance == null || teachingProgrammeInstance.getFileId() == null){
            throw new RuntimeException("请先将教学大纲生成文件后再抽取！");
        }
        List<TeachingProgrammeInstanceExtract> teachingProgrammeInstanceExtracts = instanceExtractMapper.selectTeachingProgrammeInstanceExtractByInstanceId(teachingProgrammeInstanceExtract.getInstanceId());
        if (CollectionUtils.isNotEmpty(teachingProgrammeInstanceExtracts)){
            teachingProgrammeInstanceExtract = teachingProgrammeInstanceExtracts.get(0);
        }
        // 先更新状态为1（准备开始抽取）
        teachingProgrammeInstanceExtract.setStatus(1);
        instanceExtractMapper.updateTeachingProgrammeInstanceExtract(teachingProgrammeInstanceExtract);
        // 异步执行AI抽取任务
        // 使用 thenAccept 处理异步结果
       asyncDataTask(teachingProgrammeInstance.getFileId(), teachingProgrammeInstanceExtract)
                .thenAccept(result -> {
                    // 处理结果
                    logger.info("异步任务完成");
                })
                .exceptionally(throwable -> {
                    logger.error("异步任务异常", throwable);
                    return null;
                });
       return teachingProgrammeInstanceExtract;
    }

    /**
     * 调用ai抽取内容
     * @param fileId
     * @return
     */
    public CompletableFuture<TeachingProgrammeInstanceExtract> asyncDataTask(String fileId, TeachingProgrammeInstanceExtract teachingProgrammeInstanceExtract) {
        return CompletableFuture.supplyAsync(() -> {
            long start = System.currentTimeMillis();
            try {
                Map<String, String> body = new HashMap<>();
                body.put("fileId", fileId);
                ResponseEntity<JSONObject> restTemplateUtils = RestTemplateUtils.post(aiExtractUrl, body, JSONObject.class);
                logger.info("AI抽取任务完成返回内容: {}", restTemplateUtils);
                // 正确解析返回结果并赋值给对象
                if (restTemplateUtils != null && restTemplateUtils.getBody() != null) {
                    JSONObject j = restTemplateUtils.getBody();
                    Map result = (Map) j.get("data");
                    if (result != null) {
                        teachingProgrammeInstanceExtract.setTotalCredit(getValueOrDefault(result, "totalCredit"));
                        teachingProgrammeInstanceExtract.setTotalHour(getValueOrDefault(result, "totalHour"));
                        teachingProgrammeInstanceExtract.setFloatRate(getValueOrDefault(result, "floatRate"));
                        teachingProgrammeInstanceExtract.setPoliticalHour(getValueOrDefault(result, "politicalHour"));
                        teachingProgrammeInstanceExtract.setMilitaryHour(getValueOrDefault(result, "militaryHour"));
                        teachingProgrammeInstanceExtract.setScienceHour(getValueOrDefault(result, "scienceCultureHour"));
                        teachingProgrammeInstanceExtract.setBasicsHour(getValueOrDefault(result, "majorBasicHour"));
                        teachingProgrammeInstanceExtract.setDegree(getValueOrDefault(result, "conferDegree"));
                    }
                }
                // 只在成功时更新状态为2
                teachingProgrammeInstanceExtract.setStatus(2);
                instanceExtractMapper.updateTeachingProgrammeInstanceExtract(teachingProgrammeInstanceExtract);
                long end = System.currentTimeMillis();
                logger.info("AI抽取耗时: {}ms", (end - start));
                return teachingProgrammeInstanceExtract;
            } catch (Exception e) {
                // 更新状态为失败
                teachingProgrammeInstanceExtract.setStatus(9);
                instanceExtractMapper.updateTeachingProgrammeInstanceExtract(teachingProgrammeInstanceExtract);
                logger.error("AI抽取任务失败: {}", fileId, e);
                throw new RuntimeException("AI抽取失败", e);
            }
        });
    }

    // 安全获取值
    private String getValueOrDefault(Map result, String key) {
        Object value = result.get(key);
        return value == null ? null : value.toString();
    }


    private void checkExtract(Long id){
        TeachingProgrammeInstanceExtract teachingProgrammeInstanceExtract = instanceExtractMapper.selectTeachingProgrammeInstanceExtractById(id);
        if (teachingProgrammeInstanceExtract.getStatus() == 1 ){
            throw new RuntimeException("抽取中的大纲不能删除和修改！");
        }
        UserUtils.checkDataPermission(teachingProgrammeInstanceExtract);
    }

    @Override
    @Transactional(rollbackFor = {Exception.class})
    public TeachingProgrammeInstanceExtract updateTeachingProgramme(TeachingProgrammeInstanceExtract teachingProgrammeInstanceExtract) {
        checkExtract(teachingProgrammeInstanceExtract.getId());
        teachingProgrammeInstanceExtract.setStatus(2);
        UserUtils.reflash(teachingProgrammeInstanceExtract);
        instanceExtractMapper.updateTeachingProgrammeInstanceExtract(teachingProgrammeInstanceExtract);
        instanceExRefMajorMapper.deleteTeachingProgrammeInstanceExRefMajorByExtractId(teachingProgrammeInstanceExtract.getId());
        if (CollectionUtils.isNotEmpty(teachingProgrammeInstanceExtract.getMajorIds())){
            for (Long majorId : teachingProgrammeInstanceExtract.getMajorIds()) {
                TeachingProgrammeInstanceExRefMajor t = new TeachingProgrammeInstanceExRefMajor();
                t.setExtractId(teachingProgrammeInstanceExtract.getId());
                t.setMajorId(majorId);
                instanceExRefMajorMapper.insertTeachingProgrammeInstanceExRefMajor(t);
            }
        }
        return teachingProgrammeInstanceExtract;
    }

    @Override
    @Transactional(rollbackFor = {Exception.class})
    public TeachingProgrammeInstanceExtract addTeachingProgramme(TeachingProgrammeInstanceExtract teachingProgrammeInstanceExtract) {
        UserUtils.reflash(teachingProgrammeInstanceExtract);
        instanceExtractMapper.insertTeachingProgrammeInstanceExtract(teachingProgrammeInstanceExtract);
        if (CollectionUtils.isNotEmpty(teachingProgrammeInstanceExtract.getMajorIds())){
            for (Long majorId : teachingProgrammeInstanceExtract.getMajorIds()) {
                TeachingProgrammeInstanceExRefMajor t = new TeachingProgrammeInstanceExRefMajor();
                t.setExtractId(teachingProgrammeInstanceExtract.getId());
                t.setMajorId(majorId);
                instanceExRefMajorMapper.insertTeachingProgrammeInstanceExRefMajor(t);
            }
        }
        return teachingProgrammeInstanceExtract;
    }

    @Override
    @Transactional(rollbackFor = {Exception.class})
    public void deleteTeachingProgramme(List<Long> ids) {
        for (Long id : ids) {
            checkExtract(id);
        }
        instanceExRefMajorMapper.deleteTeachingProgrammeInstanceExRefMajorByExtractIds(ids.toArray(Long[]::new));
        instanceExtractMapper.deleteTeachingProgrammeInstanceExtractByIds(ids.toArray(Long[]::new));
    }

    @Override
    public Page<TeachingProgrammeInstanceExtract> selectExtractTeachingProgramme(TeachingProgrammeInstanceExtract teachingProgrammeInstanceExtract) {
        List<TeachingProgrammeInstanceExtract> teachingProgrammeInstanceExtracts = instanceExtractMapper.selectTeachingProgrammeInstanceExtractList(teachingProgrammeInstanceExtract);
        if (CollectionUtils.isNotEmpty(teachingProgrammeInstanceExtracts)) {
            for (TeachingProgrammeInstanceExtract programmeInstanceExtract : teachingProgrammeInstanceExtracts) {
                if (CollectionUtils.isNotEmpty(programmeInstanceExtract.getMajorVos())){
                    programmeInstanceExtract.setMajorIds(programmeInstanceExtract.getMajorVos().stream().map(a->a.getMajorId()).collect(Collectors.toList()));
                }
            }
            return PaginationUtils.getPage(teachingProgrammeInstanceExtracts,teachingProgrammeInstanceExtract.getPageNum()== null ? 1 : teachingProgrammeInstanceExtract.getPageNum(),teachingProgrammeInstanceExtract.getPageSize() == null ? 20 : teachingProgrammeInstanceExtract.getPageSize());
        }
        return new PageImpl<>(new ArrayList<>(), Pageable.unpaged(), 0);
    }

    @Override
    @Transactional(rollbackFor = {Exception.class})
    public Message copyInstance(TeachingProgrammeInstanceDto instance) {
        //查询当前年份的专业类是否已经存在大纲
        TeachingProgrammeInstance selectInstance = new TeachingProgrammeInstance();
        selectInstance.setMajorId(instance.getMajorId());
        selectInstance.setVersion(instance.getVersion());
        List<TeachingProgrammeInstance> teachingProgrammeInstances = instanceMapper.selectTeachingProgrammeInstanceList(selectInstance);
        if(ObjectUtils.isNotEmpty(teachingProgrammeInstances)){
            return Message.error("该专业类在该年度下已存在教学大纲，无法复制");
        }
        //新增教学大纲
        //查询原始大纲
        TeachingProgrammeInstance sourceInstance = instanceMapper.selectTeachingProgrammeInstanceById(instance.getSourceInstanceId());
        if(ObjectUtils.isEmpty(sourceInstance)){
            return Message.error("源教学大纲不存在");
        }
        TeachingProgrammeInstance newInstance = copyInstance(instance, sourceInstance);
        TeachingProgrammeInstance copyInstance = addInstance(newInstance);
        //添加教学大纲内容
        TeachingProgrammeInstance sourceInstanceIncludeAttr = selectInstance(sourceInstance.getId());
        if(ObjectUtils.isNotEmpty(sourceInstanceIncludeAttr.getAttributeInstances())){
            for (TeachingProgrammeAttribute attributeInstance : sourceInstanceIncludeAttr.getAttributeInstances()) {
                copyAttr(attributeInstance,null,copyInstance.getId());
            }
        }
        return Message.success();
    }

    private void copyAttr(@NotNull TeachingProgrammeAttribute attributeInstance, Long parentId, Long instanceId){
        TeachingProgrammeAttribute copyAttr = new TeachingProgrammeAttribute();
        BeanUtils.copyProperties(attributeInstance, copyAttr);
        copyAttr.setParentId(parentId == null ? -1 : parentId );
        copyAttr.setInstanceId(instanceId);
        copyAttr.setId(null);
        attributeMapper.insertTeachingProgrammeAttribute(copyAttr);
        if (CollectionUtils.isNotEmpty(attributeInstance.getChildren())){
            for (TeachingProgrammeAttribute child : attributeInstance.getChildren()) {
                copyAttr(child,copyAttr.getId(),instanceId);
            }
        }
    }

    @NotNull
    private static TeachingProgrammeInstance copyInstance(TeachingProgrammeInstanceDto instance, TeachingProgrammeInstance sourceInstance) {
        TeachingProgrammeInstance copyInstance = new TeachingProgrammeInstance();
        BeanUtils.copyProperties(sourceInstance, copyInstance);
        copyInstance.setMajorId(instance.getMajorId());
        copyInstance.setVersion(instance.getVersion());
        copyInstance.setCategoryId(instance.getCategoryId());
        copyInstance.setId(null);
        copyInstance.setCreator(null);
        copyInstance.setCreateTime(null);
        copyInstance.setLastModifier(null);
        copyInstance.setLastModifiedTime(null);
        copyInstance.setStatus(0);
        copyInstance.setFileId(null);
        copyInstance.setFileName(null);
        copyInstance.setDownloadUrl(null);
        copyInstance.setPreviewUrl(null);
        UserUtils.reflash(copyInstance);
        return copyInstance;
    }
}
