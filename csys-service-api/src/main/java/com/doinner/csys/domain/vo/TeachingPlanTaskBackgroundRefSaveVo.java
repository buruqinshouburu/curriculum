package com.doinner.csys.domain.vo;

import java.util.List;

/**
 * 教学计划任务背景绑定毕业要求的保存入参（与任务背景新增解耦）。
 * <p>
 * 流程：先 POST /taskBackground 只填任务背景描述/技术目标/能力目标 ->
 * 再 GET /courseGraduation 取候选、GET /taskBackgroundRef/list 回显已绑 ->
 * 最后 POST /taskBackgroundRef/save 整表重建绑定。
 */
public class TeachingPlanTaskBackgroundRefSaveVo {

    /** 任务背景 id（必填） */
    private Long taskBackgroundId;

    /**
     * 要绑定的毕业要求 id 列表。
     * 保存时先逻辑删除该任务背景下旧绑定，再按本列表重建；
     * 传空列表或 null 表示清空全部绑定。
     */
    private List<Long> graduationIds;

    public Long getTaskBackgroundId() {
        return taskBackgroundId;
    }

    public void setTaskBackgroundId(Long taskBackgroundId) {
        this.taskBackgroundId = taskBackgroundId;
    }

    public List<Long> getGraduationIds() {
        return graduationIds;
    }

    public void setGraduationIds(List<Long> graduationIds) {
        this.graduationIds = graduationIds;
    }
}
