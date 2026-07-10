package com.agileai.dataparser.service.impl;

import com.agileai.dataparser.constant.CurriculumSystemConstants;
import com.agileai.dataparser.constant.DataMapConstant;
import com.agileai.dataparser.service.StdService;
import com.agileai.dataparser.utils.MongodbUtils;
import com.doinner.common.core.constant.SymbolConstants;
import com.doinner.common.core.domain.db.AbstractBaseEntity;
import com.doinner.csys.constant.DomainFieldConstant;
import com.doinner.csys.dao.*;
import com.doinner.csys.domain.*;
import com.doinner.csys.utils.UserUtils;
import com.mongodb.client.*;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class StdServiceImpl implements StdService {

    @Value("${mongodb.url}")
    private String mongoUrl;

    @Value("${mongodb.database}")
    private String mongoDatabase;

    private MongoCollection<Document> ontologyInstance;

    private MongoCollection<Document> relationshipInstance;

    private MongodbUtils mongodbUtils;

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


    @Override
    @Transactional(rollbackFor = {Exception.class})
    public void sync() {
        mongodbUtils = new MongodbUtils(mongoUrl, mongoDatabase);
        ontologyInstance = mongodbUtils.getDBCollection("OntologyInstance");
        relationshipInstance = mongodbUtils.getDBCollection("RelationshipInstance");
        mvStandardCultivationTarget();
        mvStandardGraduation();
        mvStandardCultivation();
        mvGraduationToCultivationTargetRelationship();
        mvStandardCultivationRefGraduationRelationship();
    }


    private void mvStandardCultivationTarget(){
        Document ontologyInstanceQuery = new Document(Map.of("cfgId", CurriculumSystemConstants.TRAINING_OBJECTIVE_ID, "cfg", false));
        ontologyInstance.find(ontologyInstanceQuery).forEach(_document -> {
            StandardCultivationTarget standardCultivationTarget = new StandardCultivationTarget();
            standardCultivationTarget.setName(_document.getString("name"));
            String college = _document.getString("college");
            if(ObjectUtils.isEmpty(college)) {
                return;
            }
            standardCultivationTarget.setCollegeId(Long.valueOf(college));
            standardCultivationTarget.setParentId(-1l);
            standardCultivationTarget.setUrl("-1");
            List<String> alias = _document.getList("alias", String.class);
            if(ObjectUtils.isNotEmpty(alias)) {
                standardCultivationTarget.setCode(alias.get(0));
            }
            standardCultivationTarget.setLeaf(1);
            standardCultivationTarget.setLevel(1);
            Date createTime = _document.getDate("createTime");
            if(ObjectUtils.isNotEmpty(college)) {
                LocalDateTime localDateTime = createTime.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
                standardCultivationTarget.setCreateTime(localDateTime);
            }
            UserUtils.reflash(standardCultivationTarget);
            DataMapConstant.sctMap.put(_document.getObjectId("_id"), standardCultivationTarget);
        });
        List<StandardCultivationTarget> standardCultivationTargets = new ArrayList<>(DataMapConstant.sctMap.values());
        standardCultivationTargets = standardCultivationTargets.parallelStream().sorted(Comparator.comparing(AbstractBaseEntity::getCreateTime)).collect(Collectors.toList());
        standardCultivationTargetMapper.insertStandardCultivationTargetList(standardCultivationTargets);
        Document reLationshipQuery = new Document(Map.of("cfgId", CurriculumSystemConstants.TRAINING_OBJECTIVE_RELATIONSHIP));
        relationshipInstance.find(reLationshipQuery).forEach(_document -> {
            ObjectId sourceInstanceId = _document.getObjectId("sourceInstanceId");
            ObjectId targetInstanceId = _document.getObjectId("targetInstanceId");
            StandardCultivationTarget sourceInstance = DataMapConstant.sctMap.get(sourceInstanceId);
            StandardCultivationTarget targetInstance = DataMapConstant.sctMap.get(targetInstanceId);
            if(ObjectUtils.isEmpty(sourceInstance) || ObjectUtils.isEmpty(targetInstance)){
                return;
            }
            sourceInstance.setLeaf(0);
            targetInstance.setParentId(sourceInstance.getId());
            if(null == sourceInstance.getChildren()){
                sourceInstance.setChildren(new ArrayList<>());
            }
            ((List<StandardCultivationTarget>)(sourceInstance.getChildren())).add(targetInstance);
        });
        List<StandardCultivationTarget> firstLevelList = DataMapConstant.sctMap.values().parallelStream().filter(sct -> sct.getParentId().equals(DomainFieldConstant.ROOT_NODE_LONG_ID)).collect(Collectors.toList());
        setStandardCultivationTargetField(firstLevelList);
        standardCultivationTargetMapper.updateStandardCultivationTargetList(DataMapConstant.sctMap.values());
    }

    private void setStandardCultivationTargetField(List<StandardCultivationTarget> standardCultivationTargetList){

        standardCultivationTargetList.parallelStream().forEach(standardCultivationTarget -> {
            if(ObjectUtils.isEmpty(standardCultivationTarget.getChildren())){
                return;
            }
            ((List<StandardCultivationTarget>)(standardCultivationTarget.getChildren())).forEach(_standardCultivationTarget -> {
                _standardCultivationTarget.setLevel(standardCultivationTarget.getLevel() + 1);
                _standardCultivationTarget.setUrl(standardCultivationTarget.getUrl() + SymbolConstants.COMMA + standardCultivationTarget.getId());
            });
            setStandardCultivationTargetField(((List<StandardCultivationTarget>)(standardCultivationTarget.getChildren())));
        });


    }


    private void mvStandardGraduation(){
        Document ontologyInstanceQuery = new Document(Map.of("cfgId", CurriculumSystemConstants.GRADUATION_STANDARD_ID, "cfg", false));
        ontologyInstance.find(ontologyInstanceQuery).forEach(_document -> {
            StandardGraduation standardGraduation = new StandardGraduation();
            standardGraduation.setName(_document.getString("name"));
            String college = _document.getString("college");
            if(ObjectUtils.isEmpty(college)) {
                return;
            }
            standardGraduation.setCollegeId(Long.valueOf(college));
            standardGraduation.setParentId(-1l);
            standardGraduation.setUrl("-1");
            List<String> alias = _document.getList("alias", String.class);
            if(ObjectUtils.isNotEmpty(alias)) {
                standardGraduation.setCode(alias.get(0));
            }
            standardGraduation.setLeaf(1);
            standardGraduation.setLevel(1);
            String param = _document.getString("param");
            if(StringUtils.isNotBlank(param) && ObjectId.isValid(param)){
                StandardCultivationTarget standardCultivationTarget = DataMapConstant.sctMap.get(new ObjectId(param));
                if(ObjectUtils.isNotEmpty(standardCultivationTarget)) {
                    standardGraduation.setCultivationTargetId(standardCultivationTarget.getId());
                }
            }
            Date createTime = _document.getDate("createTime");
            if(ObjectUtils.isNotEmpty(college)) {
                LocalDateTime localDateTime = createTime.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
                standardGraduation.setCreateTime(localDateTime);
            }
            UserUtils.reflash(standardGraduation);
            DataMapConstant.sgMap.put(_document.getObjectId("_id"), standardGraduation);
        });
        List<StandardGraduation> standardGraduationList = new ArrayList<>(DataMapConstant.sgMap.values());
        standardGraduationList = standardGraduationList.parallelStream().sorted(Comparator.comparing(StandardGraduation::getCreateTime)).collect(Collectors.toList());
        standardGraduationMapper.insertStandardGraduationList(standardGraduationList);
        Document reLationshipQuery = new Document(Map.of("cfgId", CurriculumSystemConstants.GRADUATION_STANDARD_RELATIONSHIP));
        relationshipInstance.find(reLationshipQuery).forEach(_document -> {
            ObjectId sourceInstanceId = _document.getObjectId("sourceInstanceId");
            ObjectId targetInstanceId = _document.getObjectId("targetInstanceId");
            StandardGraduation sourceInstance = DataMapConstant.sgMap.get(sourceInstanceId);
            StandardGraduation targetInstance = DataMapConstant.sgMap.get(targetInstanceId);
            if(ObjectUtils.isEmpty(sourceInstance) || ObjectUtils.isEmpty(targetInstance)){
                return;
            }
            sourceInstance.setLeaf(0);
            targetInstance.setParentId(sourceInstance.getId());
            if(null == sourceInstance.getChildren()){
                sourceInstance.setChildren(new ArrayList<>());
            }
            ((List<StandardGraduation>)(sourceInstance.getChildren())).add(targetInstance);
        });
        List<StandardGraduation> firstLevelList = DataMapConstant.sgMap.values().parallelStream().filter(sct -> sct.getParentId().equals(DomainFieldConstant.ROOT_NODE_LONG_ID)).collect(Collectors.toList());
        setStandardGraduationField(firstLevelList);
        standardGraduationMapper.updateStandardGraduationList(DataMapConstant.sgMap.values());
    }

    private void setStandardGraduationField(List<StandardGraduation> standardGraduationList){

        standardGraduationList.parallelStream().forEach(standardGraduation -> {
            if(ObjectUtils.isEmpty(standardGraduation.getChildren())){
                return;
            }
            ((List<StandardGraduation>)(standardGraduation.getChildren())).forEach(_standardCultivationTarget -> {
                _standardCultivationTarget.setLevel(standardGraduation.getLevel() + 1);
                _standardCultivationTarget.setUrl(standardGraduation.getUrl() + SymbolConstants.COMMA + standardGraduation.getId());
                _standardCultivationTarget.setCultivationTargetId(standardGraduation.getCultivationTargetId());
            });
            setStandardGraduationField(((List<StandardGraduation>)(standardGraduation.getChildren())));
        });


    }


    private void mvStandardCultivation(){
        AtomicInteger maxLength = new AtomicInteger(0);
        ObjectId id = null;
        Document ontologyInstanceQuery = new Document(Map.of("cfgId", CurriculumSystemConstants.DEVELOP_STANDARDS_ID, "cfg", false));
        ontologyInstance.find(ontologyInstanceQuery).forEach(_document -> {
            StandardCultivation standardCultivation = new StandardCultivation();
            standardCultivation.setName(_document.getString("name"));

            String college = _document.getString("college");
            if(ObjectUtils.isEmpty(college)) {
                return;
            }
            standardCultivation.setCollegeId(Long.valueOf(college));
            standardCultivation.setParentId(-1l);
            standardCultivation.setUrl("-1");
            List<String> alias = _document.getList("alias", String.class);
            if(ObjectUtils.isNotEmpty(alias)) {
                standardCultivation.setCode(alias.get(0));
            }
            standardCultivation.setLeaf(1);
            standardCultivation.setLevel(1);
            DataMapConstant.scMap.put(_document.getObjectId("_id"), standardCultivation);
            Date createTime = _document.getDate("createTime");
            if(ObjectUtils.isNotEmpty(college)) {
                LocalDateTime localDateTime = createTime.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
                standardCultivation.setCreateTime(localDateTime);
            }
            UserUtils.reflash(standardCultivation);
            String param = _document.getString("param");
            if(StringUtils.isNotBlank(param) && ObjectId.isValid(param)){
                StandardGraduation standardGraduation = DataMapConstant.sgMap.get(new ObjectId(param));
                if(ObjectUtils.isNotEmpty(standardGraduation)) {
                    standardCultivation.setGraduationId(standardGraduation.getId());
                }
            }
        });
        List<StandardCultivation> standardCultivationList = new ArrayList<>(DataMapConstant.scMap.values());
        standardCultivationList = standardCultivationList.parallelStream().sorted(Comparator.comparing(AbstractBaseEntity::getCreateTime)).collect(Collectors.toList());
        standardCultivationMapper.insertStandardCultivationList(standardCultivationList);
        Document reLationshipQuery = new Document(Map.of("cfgId", CurriculumSystemConstants.DEVELOP_STANDARDS_RELATIONSHIP));
        relationshipInstance.find(reLationshipQuery).forEach(_document -> {
            ObjectId sourceInstanceId = _document.getObjectId("sourceInstanceId");
            ObjectId targetInstanceId = _document.getObjectId("targetInstanceId");
            StandardCultivation sourceInstance = DataMapConstant.scMap.get(sourceInstanceId);
            StandardCultivation targetInstance = DataMapConstant.scMap.get(targetInstanceId);
            if(ObjectUtils.isEmpty(sourceInstance) || ObjectUtils.isEmpty(targetInstance)){
                return;
            }
            sourceInstance.setLeaf(0);
            targetInstance.setParentId(sourceInstance.getId());
            if(null == sourceInstance.getChildren()){
                sourceInstance.setChildren(new ArrayList<>());
            }
            ((List<StandardCultivation>)(sourceInstance.getChildren())).add(targetInstance);
        });
        List<StandardCultivation> firstLevelList = DataMapConstant.scMap.values().parallelStream().filter(sct -> sct.getParentId().equals(DomainFieldConstant.ROOT_NODE_LONG_ID)).collect(Collectors.toList());
        setStandardCultivationField(firstLevelList);
        standardCultivationMapper.updateStandardCultivationList(DataMapConstant.scMap.values());
    }

    private void setStandardCultivationField(List<StandardCultivation> standardCultivationList){

        standardCultivationList.parallelStream().forEach(standardCultivation -> {
            if(ObjectUtils.isEmpty(standardCultivation.getChildren())){
                return;
            }
            ((List<StandardCultivation>)(standardCultivation.getChildren())).forEach(_standardCultivation -> {
                _standardCultivation.setLevel(standardCultivation.getLevel() + 1);
                _standardCultivation.setUrl(standardCultivation.getUrl() + SymbolConstants.COMMA + standardCultivation.getId());
                _standardCultivation.setGraduationId(standardCultivation.getGraduationId());
            });
            setStandardCultivationField(((List<StandardCultivation>)(standardCultivation.getChildren())));
        });


    }

    private void mvGraduationToCultivationTargetRelationship(){
        Document reLationshipQuery = new Document(Map.of("cfgId", CurriculumSystemConstants.TRAINING_GRADUATION_RELATIONSHIP));
        List<StandardGraduationRefCultivationTarget> standardGraduationRefCultivationTargetList = new ArrayList<>();
        relationshipInstance.find(reLationshipQuery).forEach(_document -> {
            ObjectId sourceId = _document.getObjectId("sourceInstanceId");
            ObjectId targetId = _document.getObjectId("targetInstanceId");
            StandardCultivationTarget standardCultivationTarget = DataMapConstant.sctMap.get(targetId);
            StandardGraduation standardGraduation = DataMapConstant.sgMap.get(sourceId);
            if (ObjectUtils.isEmpty(standardCultivationTarget) || ObjectUtils.isEmpty(standardGraduation)) {
                return;
            }
            StandardGraduationRefCultivationTarget standardGraduationRefCultivationTarget = new StandardGraduationRefCultivationTarget();
            standardGraduationRefCultivationTarget.setCultivationTargetId(standardCultivationTarget.getId());
            standardGraduationRefCultivationTarget.setGraduationId(standardGraduation.getId());
            standardGraduationRefCultivationTargetList.add(standardGraduationRefCultivationTarget);
        });
        if(ObjectUtils.isNotEmpty(standardGraduationRefCultivationTargetList)) {
            standardGraduationRefCultivationTargetMapper.insetList(standardGraduationRefCultivationTargetList);
        }
    }

    private void mvStandardCultivationRefGraduationRelationship(){
        Document reLationshipQuery = new Document(Map.of("cfgId", CurriculumSystemConstants.GRADUATION_DEVELOP_RELATIONSHIP));
        List<StandardCultivationRefGraduation> standardGraduationRefCultivationTargetList = new ArrayList<>();
        relationshipInstance.find(reLationshipQuery).forEach(_document -> {
            ObjectId sourceId = _document.getObjectId("sourceInstanceId");
            ObjectId targetId = _document.getObjectId("targetInstanceId");
            StandardGraduation standardGraduation = DataMapConstant.sgMap.get(targetId);
            StandardCultivation standardCultivation = DataMapConstant.scMap.get(sourceId);
            if (ObjectUtils.isEmpty(standardGraduation) || ObjectUtils.isEmpty(standardGraduation)) {
                return;
            }
            StandardCultivationRefGraduation standardCultivationRefGraduation = new StandardCultivationRefGraduation();
            standardCultivationRefGraduation.setCultivationId(standardCultivation.getId());
            standardCultivationRefGraduation.setGraduationId(standardGraduation.getId());
            standardGraduationRefCultivationTargetList.add(standardCultivationRefGraduation);
        });
        if(ObjectUtils.isNotEmpty(standardGraduationRefCultivationTargetList)) {
            standardCultivationRefGraduationMapper.insetList(standardGraduationRefCultivationTargetList);
        }
    }


}
