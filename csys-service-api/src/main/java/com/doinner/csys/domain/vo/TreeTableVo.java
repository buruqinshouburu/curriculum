package com.doinner.csys.domain.vo;

import com.doinner.common.core.constant.SymbolConstants;
import com.doinner.common.core.exception.DataFormatException;
import com.doinner.csys.constant.DomainExceptionConstant;
import com.doinner.csys.constant.DomainFieldConstant;
import com.doinner.csys.domain.*;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TreeTableVo {

    private String id;

    private String parentId;

    private String name;

    private Integer size;

    private List<TreeTableVo> children;

    private Map<String, String> params;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public List<TreeTableVo> getChildren() {
        return children;
    }

    public void setChildren(List<TreeTableVo> children) {
        this.children = children;
    }

    public Map<String, String> getParams() {
        return params;
    }

    public void setParams(Map<String, String> params) {
        this.params = params;
    }

    /**
     * 导入时使用，将excel转换出来的vo转换为培养标准对象；
     * 遍历子级
     */
    public StandardCultivation toStandardCultivation(){
        StandardCultivation standardCultivation = new StandardCultivation();
        if(StringUtils.isNotBlank(this.getParentId())) {
            standardCultivation.setParentId(Long.valueOf(this.getParentId()));
        }
        Map<String, String> params = this.getParams();
        params.keySet().forEach(key -> {
            if (key.equals(DomainFieldConstant.STANDARD_CULTIVATION_NAME) ||
                    key.equals(DomainFieldConstant.STANDARD_CULTIVATION_FIRST) ||
                    key.equals(DomainFieldConstant.STANDARD_CULTIVATION_SECOND) ||
                    key.equals(DomainFieldConstant.STANDARD_CULTIVATION_THIRD)) {
                standardCultivation.setName(params.get(key));
            }
            if (key.equals(DomainFieldConstant.STANDARD_CULTIVATION_REMARK)) {
                standardCultivation.setRemark(params.get(key));
            }
            if (key.equals(DomainFieldConstant.STANDARD_CULTIVATION_CODE)) {
                standardCultivation.setCode(params.get(key));
            }
            if(key.equals(DomainFieldConstant.STANDARD_MAJOR_NAME)) {
                standardCultivation.setMajorId(Long.valueOf(params.get(key)));
            }
            if(key.equals(DomainFieldConstant.STANDARD_SUB_MAJOR_NAME) && StringUtils.isNotBlank(params.get(key))) {
                standardCultivation.setSubMajorId(Long.valueOf(params.get(key)));
            }
            if(key.equals(DomainFieldConstant.STANDARD_CLASS_NAME)) {
                if(StringUtils.isNotBlank(params.get(key))){
                    String className = params.get(key);
                    if(!DomainFieldConstant.STANDARD_CLASS_MAP.containsKey(className)){
                        String errMessage = String.format(DomainExceptionConstant.CLASS_OF_STANDARD_NOT_EXISTS, className);
                        throw new DataFormatException(errMessage);
                    }
                    standardCultivation.setClassId(DomainFieldConstant.STANDARD_CLASS_MAP.get(params.get(key)));
                }else{
                    standardCultivation.setClassId(DomainFieldConstant.CLASS_NO_TYPE);
                }
            }

        });
        if (ObjectUtils.isEmpty(this.getChildren())) {
            return standardCultivation;
        }
        standardCultivation.setChildren(this.getChildren().parallelStream().map(TreeTableVo::toStandardCultivation).collect(Collectors.toList()));
        return standardCultivation;
    }

    /**
     * 导入时使用，将excel转换出来的vo转换为能力对象；
     * 遍历子级
     */
    public StandardAbility toStandardAbility(){
        StandardAbility standardAbility = new StandardAbilityVo();
        if(StringUtils.isNotBlank(this.getParentId())) {
            standardAbility.setParentId(Long.valueOf(this.getParentId()));
        }
        Map<String, String> params = this.getParams();
        params.keySet().forEach(key -> {
            if(key.equals(DomainFieldConstant.STANDARD_ABILITY_NAME) ||
                    key.equals(DomainFieldConstant.STANDARD_ABILITY_FIRST) ||
                    key.equals(DomainFieldConstant.STANDARD_ABILITY_SECOND) ||
                    key.equals(DomainFieldConstant.STANDARD_ABILITY_THIRD)){
                standardAbility.setName(params.get(key));
            }
            if(key.equals(DomainFieldConstant.ABILITY_STANDARD)){
                standardAbility.setRemark(params.get(key));
            }
        });
        if(ObjectUtils.isEmpty(this.getChildren())){
            return standardAbility;
        }
        standardAbility.setChildren(this.getChildren().parallelStream().map(TreeTableVo::toStandardAbility).collect(Collectors.toList()));
        return standardAbility;
    }

    /**
     * 导入时使用，将excel转换出来的vo转换为培养目标对象；
     * 遍历子级
     */
    public StandardCultivationTarget toStandardCultivationTarget() {
        StandardCultivationTarget standardCultivationTarget = new StandardCultivationTarget();
        if(StringUtils.isNotBlank(this.getParentId())) {
            standardCultivationTarget.setParentId(Long.valueOf(this.getParentId()));
        }
        Map<String, String> params = this.getParams();
        params.keySet().forEach(key -> {
            /*if(key.equals(DomainFieldConstant.STANDARD_CULTIVATION_TARGET_NAME) ||
                    key.equals(DomainFieldConstant.STANDARD_CULTIVATION_PARTIAL_TARGET_NAME) ||
                    key.equals(DomainFieldConstant.STANDARD_CULTIVATION_TARGET_REMARK)) {
                standardCultivationTarget.setName(params.get(key));
            }*/
            if(key.equals("总体目标") ||
                    key.equals("具体目标") ) {
                standardCultivationTarget.setName(params.get(key));
            }
            if(key.equals(DomainFieldConstant.STANDARD_CULTIVATION_MAIN_TARGET)) {
                standardCultivationTarget.setRemark(params.get(key));
            }
            if(key.equals(DomainFieldConstant.STANDARD_CULTIVATION_TARGET_CODE)) {
                standardCultivationTarget.setCode(params.get(key));
            }
            if(key.equals(DomainFieldConstant.STANDARD_MAJOR_NAME)) {
                standardCultivationTarget.setMajorId(Long.valueOf(params.get(key)));
            }
            if(key.equals(DomainFieldConstant.STANDARD_SUB_MAJOR_NAME) && StringUtils.isNotBlank(params.get(key))) {
                standardCultivationTarget.setSubMajorId(Long.valueOf(params.get(key)));
            }
            if(key.equals(DomainFieldConstant.STANDARD_CLASS_NAME)) {
                if(StringUtils.isNotBlank(params.get(key))){
                    String className = params.get(key);
                    if(!DomainFieldConstant.STANDARD_CLASS_MAP.containsKey(className)){
                        String errMessage = String.format(DomainExceptionConstant.CLASS_OF_STANDARD_NOT_EXISTS, className);
                        throw new DataFormatException(errMessage);
                    }
                    standardCultivationTarget.setClassId(DomainFieldConstant.STANDARD_CLASS_MAP.get(params.get(key)));
                }else{
                    standardCultivationTarget.setClassId(DomainFieldConstant.CLASS_NO_TYPE);
                }
            }
        });
        if(ObjectUtils.isEmpty(this.getChildren())){
            return standardCultivationTarget;
        }
        standardCultivationTarget.setChildren(this.getChildren().parallelStream().map(TreeTableVo::toStandardCultivationTarget).collect(Collectors.toList()));
        return standardCultivationTarget;
    }

    public StandardGraduation toStandardGraduation() {
        StandardGraduation standardGraduation = new StandardGraduation();
        if(StringUtils.isNotBlank(this.getParentId())) {
            standardGraduation.setParentId(Long.valueOf(this.getParentId()));
        }
        Map<String, String> params = this.getParams();
        params.keySet().forEach(key -> {
            /*if (key.equals(DomainFieldConstant.STANDARD_GRADUATION_NAME) ||
                    key.equals(DomainFieldConstant.STANDARD_GRADUATION_CLASSI) ||
                    key.equals(DomainFieldConstant.STANDARD_GRADUATION_REMARK)) {
                standardGraduation.setName(params.get(key));
            }*/
            if (key.equals("类型") ||
                    key.equals("毕业要求") ||
                    key.equals("具体要求")) {
                standardGraduation.setName(params.get(key));
            }
            if (key.equals(DomainFieldConstant.STANDARD_GRADUATION_CODE)) {
                standardGraduation.setCode(params.get(key));
            }
            if(key.equals(DomainFieldConstant.STANDARD_MAJOR_NAME)) {
                standardGraduation.setMajorId(Long.valueOf(params.get(key)));
            }
            if(key.equals(DomainFieldConstant.STANDARD_SUB_MAJOR_NAME) && StringUtils.isNotBlank(params.get(key))) {
                standardGraduation.setSubMajorId(Long.valueOf(params.get(key)));
            }
            if(key.equals(DomainFieldConstant.STANDARD_CLASS_NAME)) {
                if(StringUtils.isNotBlank(params.get(key))){
                    String className = params.get(key);
                    if(!DomainFieldConstant.STANDARD_CLASS_MAP.containsKey(className)){
                        String errMessage = String.format(DomainExceptionConstant.CLASS_OF_STANDARD_NOT_EXISTS, className);
                        throw new DataFormatException(errMessage);
                    }
                    standardGraduation.setClassId(DomainFieldConstant.STANDARD_CLASS_MAP.get(params.get(key)));
                }else{
                    standardGraduation.setClassId(DomainFieldConstant.CLASS_NO_TYPE);
                }
            }

        });
        if (ObjectUtils.isEmpty(this.getChildren())) {
            return standardGraduation;
        }
        standardGraduation.setChildren(this.getChildren().parallelStream().map(TreeTableVo::toStandardGraduation).collect(Collectors.toList()));
        setGraduationType(standardGraduation,null);
        return standardGraduation;
    }

    private void setGraduationType(StandardGraduation standardGraduation,String graduationType){
        if (StringUtils.isBlank(graduationType)){
            if ("知识".equals(standardGraduation.getName())){
                graduationType = "1";
            }else if ("能力".equals(standardGraduation.getName())){
                graduationType = "2";
            }else if ("素质".equals(standardGraduation.getName())){
                graduationType = "3";
            }
        }
        if (StringUtils.isNotBlank(graduationType)){
            standardGraduation.setGraduationType(graduationType);
            if (!CollectionUtils.isEmpty(standardGraduation.getChildren())){
                for (StandardGraduation child : standardGraduation.getChildren()) {
                    setGraduationType(child,graduationType);
                }
            }
        }
    }

    public TreeTableVo() {
    }

    public TreeTableVo(CourseVo courseVo) {
        this.setId(courseVo.getId().toString());
        this.setName(courseVo.getName());
        this.setParentId(DomainFieldConstant.ROOT_NODE_STRING_ID);
        Map<String, String> params = new HashMap<>();
        params.put(DomainFieldConstant.EXCEL_TITLE_CHARGE, courseVo.getCreator());
        params.put(DomainFieldConstant.EXCEL_TITLE_AUTHORS, courseVo.getAuthors());
        if(ObjectUtils.isNotEmpty(courseVo.getHours())) {
            params.put(DomainFieldConstant.EXCEL_TITLE_HOURS, courseVo.getHours().toString());
        }
        if(ObjectUtils.isNotEmpty(courseVo.getTheoryHours())) {
            params.put(DomainFieldConstant.EXCEL_TITLE_THEORY_HOURS, courseVo.getTheoryHours().toString());
        }
        if(ObjectUtils.isNotEmpty(courseVo.getTeachHours())) {
            params.put(DomainFieldConstant.EXCEL_TITLE_TEACH_HOURS, courseVo.getTeachHours().toString());
        }
        if(ObjectUtils.isNotEmpty(courseVo.getPracticeHours())) {
            params.put(DomainFieldConstant.EXCEL_TITLE_PRACTICE_HOURS, courseVo.getPracticeHours().toString());
        }
        if(ObjectUtils.isNotEmpty(courseVo.getHours()) && ObjectUtils.isNotEmpty(courseVo.getCourseTeachingPracticePlanVo()) && ObjectUtils.isNotEmpty(courseVo.getCourseTeachingPracticePlanVo().getExaMethod())) {
            params.put(DomainFieldConstant.EXCEL_TITLE_EXA_METHOD, courseVo.getCourseTeachingPracticePlanVo().getExaMethod().toString());
        }
        if(ObjectUtils.isNotEmpty(courseVo.getCourseTextbookVoList())) {
            params.put(DomainFieldConstant.EXCEL_TITLE_TEXT_BOOK, courseVo.getCourseTextbookVoList().parallelStream()
                    .filter(book -> DomainFieldConstant.TEXT_BOOK_TYPE == book.getType())
                    .map(CourseTextbookVo::getName).collect(Collectors.joining(SymbolConstants.COMMA)));
            params.put(DomainFieldConstant.EXCEL_TITLE_REFERENCE_BOOK, courseVo.getCourseTextbookVoList().parallelStream()
                    .filter(book -> DomainFieldConstant.REFERENCE_BOOK_TYPE == book.getType())
                    .map(CourseTextbookVo::getName).collect(Collectors.joining(SymbolConstants.COMMA)));
        }
        params.put(DomainFieldConstant.EXCEL_TITLE_BEFORE_COURSE, courseVo.getBeforeCourseId());
        params.put(DomainFieldConstant.EXCEL_TITLE_AFTER_COURSE, courseVo.getAfterCourseId());
        params.put(DomainFieldConstant.EXCEL_TITLE_SUMMARY, courseVo.getSummary());
        this.setParams(params);
        if(ObjectUtils.isNotEmpty(courseVo.getKnowledgeUnitVoList())){
            this.setChildren(courseVo.getKnowledgeUnitVoList().stream().map(_treeTableVo -> new TreeTableVo(_treeTableVo, courseVo.getId())).collect(Collectors.toList()));
            this.setSize(this.getChildren().parallelStream().map(TreeTableVo::getSize).reduce(Integer::sum).get());
        }else{
            this.setSize(1);
        }
    }


    public TreeTableVo(KnowledgeUnitVo knowledgeUnitVo, Long courseId) {
        this.setId(knowledgeUnitVo.getId().toString());
        this.setName(knowledgeUnitVo.getName());
        this.setParentId(courseId.toString());
        Map<String, String> params = new HashMap<>();
        params.put(DomainFieldConstant.EXCEL_TITLE_UNIT_REMARK, knowledgeUnitVo.getRemark());
        this.setParams(params);
        if(ObjectUtils.isNotEmpty(knowledgeUnitVo.getKnowledgePointVoList())){
            this.setChildren(knowledgeUnitVo.getKnowledgePointVoList().stream().map(_treeTableVo -> new TreeTableVo(_treeTableVo, knowledgeUnitVo.getId())).collect(Collectors.toList()));
            this.setSize(this.getChildren().parallelStream().map(TreeTableVo::getSize).reduce(Integer::sum).get());
        }else{
            this.setSize(1);
        }
    }

    public TreeTableVo(KnowledgePointVo knowledgePointVo, Long unitId) {
        this.setId(knowledgePointVo.getId().toString());
        this.setName(knowledgePointVo.getName());
        this.setParentId(unitId.toString());
        Map<String, String> params = new HashMap<>();
        this.setParams(params);
        this.setSize(1);
    }

    public TreeTableVo(StandardCultivationTarget standardCultivationTarget, Long parentId) {
        this.setId(standardCultivationTarget.getId().toString());
        this.setParentId(parentId.toString());
        if(!DomainFieldConstant.ROOT_NODE_LONG_ID.equals(parentId)) {
            this.setName(standardCultivationTarget.getCode());
            TreeTableVo treeTableVo = new TreeTableVo();
            treeTableVo.setId(standardCultivationTarget.getId().toString());
            treeTableVo.setName(standardCultivationTarget.getName());
            Map<String, String> params = new HashMap<>();
            params.put(DomainFieldConstant.REMARK, standardCultivationTarget.getRemark());
            treeTableVo.setParams(params);
            this.setChildren(List.of(treeTableVo));
            if (ObjectUtils.isNotEmpty(standardCultivationTarget.getChildren())) {
                treeTableVo.setChildren(((List<StandardCultivationTarget>) standardCultivationTarget.getChildren()).stream()
                        .map(_standardCultivationTarget -> new TreeTableVo(_standardCultivationTarget, standardCultivationTarget.getId()))
                        .collect(Collectors.toList()));
                treeTableVo.setSize(treeTableVo.getChildren().parallelStream().map(TreeTableVo::getSize).reduce(Integer::sum).get());
                this.setSize(treeTableVo.getSize());
            } else {
                this.setSize(1);
                treeTableVo.setSize(1);
            }
        }else{
            StringBuffer name = new StringBuffer();
            if(StringUtils.isNotBlank(standardCultivationTarget.getName())){
                name.append(standardCultivationTarget.getName());
            }
            if(StringUtils.isNotBlank(standardCultivationTarget.getCode())){
                name.append(standardCultivationTarget.getCode());
            }
            this.setName(name.toString());
            if (ObjectUtils.isNotEmpty(standardCultivationTarget.getChildren())) {
                this.setChildren(((List<StandardCultivationTarget>) standardCultivationTarget.getChildren()).stream()
                        .map(_standardCultivationTarget -> new TreeTableVo(_standardCultivationTarget, standardCultivationTarget.getId()))
                        .collect(Collectors.toList()));
                this.setSize(this.getChildren().parallelStream().map(TreeTableVo::getSize).reduce(Integer::sum).get());
            } else {
                this.setSize(1);
            }
        }
    }

    public TreeTableVo(StandardGraduation standardGraduation, Long parentId) {
        this.setId(standardGraduation.getId().toString());
        this.setParentId(parentId.toString());
        if(!DomainFieldConstant.ROOT_NODE_LONG_ID.equals(parentId)) {
            this.setName(standardGraduation.getName());
//            TreeTableVo treeTableVo = new TreeTableVo();
//            treeTableVo.setId(standardGraduation.getId().toString());
//            treeTableVo.setName(standardGraduation.getName());
            Map<String, String> params = new HashMap<>();
            params.put(DomainFieldConstant.REMARK, standardGraduation.getRemark());
            this.setParams(params);
            //this.setChildren(List.of(this));
            if (ObjectUtils.isNotEmpty(standardGraduation.getChildren())) {
                this.setChildren(((List<StandardGraduation>) standardGraduation.getChildren()).stream()
                        .map(_standardCultivationTarget -> new TreeTableVo(_standardCultivationTarget, standardGraduation.getId()))
                        .collect(Collectors.toList()));
                this.setSize(this.getChildren().parallelStream().map(TreeTableVo::getSize).reduce(Integer::sum).get());
                this.setSize(this.getSize());
            } else {
                this.setSize(1);
                this.setSize(1);
            }
        }else{
            StringBuffer name = new StringBuffer();
            if(StringUtils.isNotBlank(standardGraduation.getName())){
                name.append(standardGraduation.getName());
            }
            if(StringUtils.isNotBlank(standardGraduation.getCode())){
                name.append(standardGraduation.getCode());
            }
            this.setName(name.toString());
            if (ObjectUtils.isNotEmpty(standardGraduation.getChildren())) {
                this.setChildren(((List<StandardGraduation>) standardGraduation.getChildren()).stream()
                        .map(_standardCultivationTarget -> new TreeTableVo(_standardCultivationTarget, standardGraduation.getId()))
                        .collect(Collectors.toList()));
                this.setSize(this.getChildren().parallelStream().map(TreeTableVo::getSize).reduce(Integer::sum).get());
            } else {
                this.setSize(1);
            }
        }
    }

    public TreeTableVo(StandardCultivation standardCultivation, Long ParentId) {
        this.setId(standardCultivation.getId().toString());
        this.setParentId(ParentId.toString());
        this.setName(standardCultivation.getCode());
        TreeTableVo treeTableVo = new TreeTableVo();
        treeTableVo.setId(standardCultivation.getId().toString());
        treeTableVo.setName(standardCultivation.getName());
        Map<String, String> params = new HashMap<>();
        params.put(DomainFieldConstant.REMARK, standardCultivation.getRemark());
        treeTableVo.setParams(params);
        this.setChildren(List.of(treeTableVo));
        if(ObjectUtils.isNotEmpty(standardCultivation.getChildren())){
            treeTableVo.setChildren(((List<StandardCultivation>)standardCultivation.getChildren()).stream()
                    .map(_standardCultivation -> new TreeTableVo(_standardCultivation, standardCultivation.getId()))
                    .collect(Collectors.toList()));
            treeTableVo.setSize(treeTableVo.getChildren().parallelStream().map(TreeTableVo::getSize).reduce(Integer::sum).get());
            this.setSize(treeTableVo.getSize());
        }else{
            this.setSize(1);
            treeTableVo.setSize(1);
        }

    }

   /* public TreeTableVo(TrainingSchemeVo trainingSchemeVo, Map<Long, List<TrainingSchemeCourseScheduleVo>> termMap) {
        this.setId("培养规划" + trainingSchemeVo.getId().toString());
        this.setName(trainingSchemeVo.getName());
        List<TreeTableVo> children = new ArrayList<>();

        createCourseVo(children, "选课1,1", "必修课程", trainingSchemeVo.getCompulsoryCourseList(), termMap);

        createCourseVo(children, "选课1,2", "必修训练项目", trainingSchemeVo.getCompulsoryTrainingSubjectList(), termMap);

        createCourseVo(children, "选课1,3", "必修实践项目", trainingSchemeVo.getCompulsoryPracticalProjectList(), termMap);

        createCourseVo(children, "选课2,1", "选修课程", trainingSchemeVo.getElectiveCourseList(), termMap);

        createCourseVo(children, "选课2,2", "选修训练项目", trainingSchemeVo.getElectiveTrainingSubjectList(), termMap);

        createCourseVo(children, "选课2,3", "选修实践项目", trainingSchemeVo.getElectivePracticalProjectList(), termMap);

        createCourseVo(children, "选课3,1", "任选课程", trainingSchemeVo.getOptionalCourseList(), termMap);

        createCourseVo(children, "选课3,2", "任选训练项目", trainingSchemeVo.getOptionalTrainingSubjectList(), termMap);

        createCourseVo(children, "选课3,3", "任选实践项目", trainingSchemeVo.getOptionalPracticalProjectList(), termMap);

        this.children = children;
        this.size = children.parallelStream().map(TreeTableVo::getSize).reduce(Integer::sum).get();
    }*/

    private void createCourseVo(List<TreeTableVo> children, String id, String TypeClassName, List<TrainingSchemeCourseVo> trainingSchemeVo, Map<Long, List<TrainingSchemeCourseScheduleVo>> termMap) {
        TreeTableVo courseVo = new TreeTableVo();
        children.add(courseVo);
        courseVo.setId(id);
        courseVo.setName(TypeClassName);
        if (ObjectUtils.isNotEmpty(trainingSchemeVo)) {
            courseVo.setChildren(trainingSchemeVo.parallelStream()
                    .map(_course -> new TreeTableVo(_course, termMap.get(_course.getId())))
                    .collect(Collectors.toList()));
            courseVo.setSize(courseVo.getChildren().parallelStream().map(TreeTableVo::getSize).reduce(Integer::sum).get());
        } else {
            courseVo.setSize(1);
        }
    }

    public TreeTableVo(TrainingSchemeCourseVo trainingSchemeCourseVo, List<TrainingSchemeCourseScheduleVo> termList) {
        this.id = "课程" + trainingSchemeCourseVo.getId().toString();
        this.name = trainingSchemeCourseVo.getName();
        TreeTableVo termChild = new TreeTableVo();
        termChild.setId("课程" + trainingSchemeCourseVo.getId().toString());
        termChild.setSize(1);
        if(ObjectUtils.isNotEmpty(termList)) {
            termChild.setName(DomainFieldConstant.TERM_NUMBER_NAME_MAP.get(termList.get(0).getTerm()));
        }
        this.setChildren(List.of(termChild));
        this.setSize(1);
    }


    public TreeTableVo(StandardMajor standardMajor, List<StandardMajor> standardMajorList) {
        this.setId(standardMajor.getId().toString());
        this.setName(standardMajor.getName());
        this.setChildren(standardMajorList.parallelStream().map(TreeTableVo::new).collect(Collectors.toList()));
    }

    public TreeTableVo(StandardMajor standardMajor) {
        this.setId(standardMajor.getId().toString());
        this.setName(standardMajor.getName());
        TreeTableVo technology = new TreeTableVo();
        technology.setId("1");
        technology.setName("技术类");
        technology.setChildren(new ArrayList<>());
        TreeTableVo command = new TreeTableVo();
        command.setId("2");
        command.setName("指挥类");
        command.setChildren(new ArrayList<>());
        ArrayList<TreeTableVo> children = new ArrayList<>();
        children.add(technology);
        children.add(command);
        this.setChildren(children);
    }

    public TreeTableVo(TrainingScheme trainingScheme) {
        this.setId(trainingScheme.getId().toString());
        this.setName(trainingScheme.getName());
    }


    public KnowledgeTreeVo toKnowledgeTreeVo() {
        KnowledgeTreeVo treeVo = new KnowledgeTreeVo();
        if(StringUtils.isNotBlank(this.getParentId())) {
            treeVo.setParentId(Long.valueOf(this.getParentId()));
        }
        Map<String, String> params = this.getParams();
        params.keySet().forEach(key -> {
            if (key.equals("*知识领域") ||
                    key.equals("*知识单元") ||
                    key.equals("知识点")) {
                treeVo.setName(params.get(key));
            }
            /*if (key.equals("*学院名称")){
                treeVo.setCollegeName(params.get(key));
            }*/
        });
        if (ObjectUtils.isEmpty(this.getChildren())) {
            return treeVo;
        }
        treeVo.setChildren(this.getChildren().parallelStream().map(TreeTableVo::toKnowledgeTreeVo).collect(Collectors.toList()));
        return treeVo;
    }

    public KnowledgeTreeVo toAbilityTreeVo() {
        KnowledgeTreeVo treeVo = new KnowledgeTreeVo();
        if(StringUtils.isNotBlank(this.getParentId())) {
            treeVo.setParentId(Long.valueOf(this.getParentId()));
        }
        Map<String, String> params = this.getParams();
        params.keySet().forEach(key -> {
            if (key.equals("能力体系名称") ||
                    key.equals("能力点") ) {
                treeVo.setName(params.get(key));
            }
            /*if (key.equals("*学院名称")){
                treeVo.setCollegeName(params.get(key));
            }*/
        });
        if (ObjectUtils.isEmpty(this.getChildren())) {
            return treeVo;
        }
        treeVo.setChildren(this.getChildren().parallelStream().map(TreeTableVo::toAbilityTreeVo).collect(Collectors.toList()));
        return treeVo;
    }

    public KnowledgeTreeVo toQualityTreeVo() {
        KnowledgeTreeVo treeVo = new KnowledgeTreeVo();
        if(StringUtils.isNotBlank(this.getParentId())) {
            treeVo.setParentId(Long.valueOf(this.getParentId()));
        }
        Map<String, String> params = this.getParams();
        params.keySet().forEach(key -> {
            System.out.println(key);
            if (key.equals("*素质体系名称") ||
                    key.equals("素质点") ) {
                treeVo.setName(params.get(key));
            }
            /*if (key.equals("*学院名称")){
                treeVo.setCollegeName(params.get(key));
            }*/
        });
        if (ObjectUtils.isEmpty(this.getChildren())) {
            return treeVo;
        }
        treeVo.setChildren(this.getChildren().parallelStream().map(TreeTableVo::toQualityTreeVo).collect(Collectors.toList()));
        return treeVo;
    }

    public CourseKnowledgeTreeVo toCourseKnowledgeTreeVo() {
        CourseKnowledgeTreeVo treeVo = new CourseKnowledgeTreeVo();
        if(StringUtils.isNotBlank(this.getParentId())) {
            treeVo.setParentId(Long.valueOf(this.getParentId()));
        }
        Map<String, String> params = this.getParams();
        params.keySet().forEach(key -> {
            if (key.equals("*知识单元") ||
                    key.equals("知识点")) {
                treeVo.setName(params.get(key));
            }
        });
        if (ObjectUtils.isEmpty(this.getChildren())) {
            return treeVo;
        }
        treeVo.setChildren(this.getChildren().parallelStream().map(TreeTableVo::toCourseKnowledgeTreeVo).collect(Collectors.toList()));
        return treeVo;
    }


}
