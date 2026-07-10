package com.agileai.dataparser.domain;

public class CurTeachingPlanningBook {
    /** 主键 */
    private Long id;

    /** 主表id */
    private Long mainId;

    /** 名称 */
    private String name;

    /** 类型：1，教材；2，参考书 */
    private String type;

    /** 编作者姓名 */
    private String autherName;

    /** 出版社名称 */
    private String pubName;

    /** 出版时间及版次 */
    private String version;

    /** 印刷时间 */
    private String pubTime;

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

    public String getAutherName() {
        return autherName;
    }

    public void setAutherName(String autherName) {
        this.autherName = autherName;
    }

    public String getPubName() {
        return pubName;
    }

    public void setPubName(String pubName) {
        this.pubName = pubName;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getPubTime() {
        return pubTime;
    }

    public void setPubTime(String pubTime) {
        this.pubTime = pubTime;
    }
}
