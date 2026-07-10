package com.agileai.dataparser.domain;

import org.springframework.data.annotation.AccessType;
import org.springframework.data.annotation.Transient;

public class CurTeachingPlanningTheoryChapter {

    /** 主键 */
    private Long id;

    /** 主表id */
    private Long mainId;

    /** 父id */
    private Long parentId;

    /** 名称 */
    private String name;

    /** 类型：1，课程专题；2，大章节（知识单元）；3，小章节 ；4：课程学习内容与时间节点*/
    private String type;

    /** 知识单元内容 */
    private String content;

    /** 掌握程度-初始 */
    @AccessType(AccessType.Type.PROPERTY)
    private String masterBegin;

    @Transient
    private String masterBeginLv;

    /** 掌握程度-要求 */
    @AccessType(AccessType.Type.PROPERTY)
    private String masterRequire;

    @Transient
    private String masterRequireLv;

    /** 单元学习目标 */
    private String learnTarget;

    /** 单元实现环节 */
    private String realizeLink;

    /** 节内容 */
    private String curContent;

    /** 节课内学时数 */
    private String curNum;

    /** 实验名称 */
    private String praName;

    /** 实验内容 */
    private String praContent;

    /** 实验课内学时数 */
    private String praNum;

    /** 项目名称 */
    private String projectName;

    /** 项目内容 */
    private String projectContent;

    /** 项目课内学时数 */
    private String projectNum;

    /** 合计课内学时数 */
    private String totalNum;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getMainId() {
        return mainId;
    }

    public void setMainId(Long mainId) {
        this.mainId = mainId;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getMasterBegin() {
        return masterBegin;
    }

    public void setMasterBegin(String masterBegin) {
//        if (null != masterBegin){
//            List<SysDictData> sysDictDataList = DictUtils.getDictCache(DictTypeConstants.SYS_POWER);
//            for (SysDictData sysDictData:sysDictDataList){
//                if (masterBegin.equals(sysDictData.getDictCode().toString())){
//                    this.masterBeginLv = sysDictData.getDictValue();
//                }
//            }
//            sysDictDataList.stream().filter(sysDictData -> masterBegin.equals(sysDictData.getDictCode().toString()))
//                    .forEach(sysDictData -> this.masterBeginLv = sysDictData.getDictValue());
//        }
       this.masterBegin = masterBegin;
    }

    public String getMasterBeginLv() {
        return masterBeginLv;
    }

    public void setMasterBeginLv(String masterBeginLv) {
        this.masterBeginLv = masterBeginLv;
    }

    public String getMasterRequire() {
        return masterRequire;
    }

    public void setMasterRequire(String masterRequire) {
//        if (null != masterRequire){
//            List<SysDictData> sysDictDataList = DictUtils.getDictCache(DictTypeConstants.SYS_POWER);
//            for (SysDictData sysDictData:sysDictDataList){
//                if (masterRequire.equals(sysDictData.getDictCode().toString())){
//                    this.masterRequireLv = sysDictData.getDictValue();
//                }
//            }
//            sysDictDataList.stream().filter(sysDictData -> masterRequire.equals(sysDictData.getDictCode().toString()))
//                    .forEach(sysDictData -> this.masterRequireLv = sysDictData.getDictValue());
//        }
        this.masterRequire = masterRequire;
    }

    public String getMasterRequireLv() {
        return masterRequireLv;
    }

    public void setMasterRequireLv(String masterRequireLv) {
        this.masterRequireLv = masterRequireLv;
    }

    public String getLearnTarget() {
        return learnTarget;
    }

    public void setLearnTarget(String learnTarget) {
        this.learnTarget = learnTarget;
    }

    public String getRealizeLink() {
        return realizeLink;
    }

    public void setRealizeLink(String realizeLink) {
        this.realizeLink = realizeLink;
    }

    public String getCurContent() {
        return curContent;
    }

    public void setCurContent(String curContent) {
        this.curContent = curContent;
    }

    public String getCurNum() {
        return curNum;
    }

    public void setCurNum(String curNum) {
        this.curNum = curNum;
    }

    public String getPraName() {
        return praName;
    }

    public void setPraName(String praName) {
        this.praName = praName;
    }

    public String getPraContent() {
        return praContent;
    }

    public void setPraContent(String praContent) {
        this.praContent = praContent;
    }

    public String getPraNum() {
        return praNum;
    }

    public void setPraNum(String praNum) {
        this.praNum = praNum;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getProjectContent() {
        return projectContent;
    }

    public void setProjectContent(String projectContent) {
        this.projectContent = projectContent;
    }

    public String getProjectNum() {
        return projectNum;
    }

    public void setProjectNum(String projectNum) {
        this.projectNum = projectNum;
    }

    public String getTotalNum() {
        return totalNum;
    }

    public void setTotalNum(String totalNum) {
        this.totalNum = totalNum;
    }
}
